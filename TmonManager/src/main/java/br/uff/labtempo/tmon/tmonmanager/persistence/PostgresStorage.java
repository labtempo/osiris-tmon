/*
 * Copyright 2015 Felipe Santos <fralph at ic.uff.br>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.uff.labtempo.tmon.tmonmanager.persistence;

import br.uff.labtempo.osiris.to.common.data.InfoTo;
import br.uff.labtempo.osiris.to.common.data.ValueTo;
import br.uff.labtempo.osiris.to.common.definitions.State;
import br.uff.labtempo.osiris.to.sensornet.SensorSnTo;
import br.uff.labtempo.tmon.tmonmanager.Config;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresStorage implements Storage {

    private Connection connection;

    public PostgresStorage(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void storeSample(SensorSnTo sensor) {
        /*        
         mote_id integer NOT NULL,
         "readingTemperature" double precision NOT NULL,
         "readingLight" integer NOT NULL,
         "readingVoltage" double precision NOT NULL,
         "timeStamp" timestamp with time zone NOT NULL,
         "sendCount" integer NOT NULL,        
         parent integer NOT NULL,
         metric integer NOT NULL        
         */
        int id = Integer.parseInt(sensor.getId());
        long timestamp = sensor.getCaptureTimestampInMillis();
        double temperature = 0;
        double voltage = 0;
        int light = 0;

        for (ValueTo value : sensor.getValuesTo()) {
            switch (value.getName()) {
                case Config.SENSOR_VALUE_LIGHT:
                    light = Integer.parseInt(value.getValue());
                    break;
                case Config.SENSOR_VALUE_TEMPERATURE:
                    temperature = Double.parseDouble(value.getValue());
                    break;
                case Config.SENSOR_VALUE_VOLTAGE:
                    voltage = Double.parseDouble(value.getValue());
                    break;
            }
        }

        //info
        int sendCount = 0;
        int parent = 0;
        int metric = 0;

        for (InfoTo info : sensor.getInfoTo()) {
            switch (info.getName()) {
                case Config.SENSOR_INFO_SEND_COUNT:
                    sendCount = Integer.parseInt(info.getDescription());
                    break;
                case Config.SENSOR_INFO_PARENT:
                    parent = Integer.parseInt(info.getDescription());
                    break;
                case Config.SENSOR_INFO_METRIC:
                    metric = Integer.parseInt(info.getDescription());
                    break;
            }
        }

        String sql = "INSERT INTO \"" + Storage.SAMPLE_TABLE + "\" (mote_id,\"readingTemperature\",\"readingLight\",\"readingVoltage\",\"timeStamp\",\"sendCount\",parent,metric) VALUES(?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement stmt = getStatement(sql);
            stmt.setInt(1, id);
            stmt.setDouble(2, temperature);
            stmt.setInt(3, light);
            stmt.setDouble(4, voltage);
            stmt.setTimestamp(5, getSqlTimestamp(timestamp));
            stmt.setInt(6, sendCount);
            stmt.setInt(7, parent);
            stmt.setInt(8, metric);
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(PostgresStorage.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void storeAverageTemperature(double temperature, long timestamp) {
        /*
         "timeStamp" timestamp with time zone NOT NULL,
         temperature double precision NOT NULL
         */
        String sql = "INSERT INTO \"" + Storage.AVERAGE_TABLE + "\"(temperature,\"timeStamp\") VALUES(?,?)";
        try {
            PreparedStatement stmt = getStatement(sql);
            stmt.setDouble(1, temperature);
            stmt.setTimestamp(2, getSqlTimestamp(timestamp));
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(PostgresStorage.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void storeMote(SensorSnTo sensor) {
        /*
         id integer NOT NULL,
         status boolean NOT NULL,
         "moteModel" character varying(5) NOT NULL,
         "moteLocalization" character varying(6),
         "localizationDescription" character varying(100)
         */

        int id = Integer.parseInt(sensor.getId());
        boolean status = true;
        String model = null;

        for (InfoTo info : sensor.getInfoTo()) {
            if (info.getName().equals(Config.SENSOR_INFO_MOTE_MODEL)) {
                model = info.getDescription();
            }
        }

        String sql = "INSERT INTO \"" + Storage.MOTE_TABLE + "\"( id, status, \"moteModel\") VALUES (?, ?, ?)";
        System.out.println(sql);
        try {
            PreparedStatement stmt = getStatement(sql);
            stmt.setInt(1, id);
            stmt.setBoolean(2, status);
            stmt.setString(3, model);
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(PostgresStorage.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void storeAlert(String alertType, long timestamp, String msg, String exchangeChannel) {
        /*
         type character varying(10) NOT NULL,
         "timeStamp" timestamp with time zone NOT NULL,
         event character varying(100) NOT NULL,
         channel character varying(20) NOT NULL
         */

        String sql = "INSERT INTO \"" + Storage.ALERT_TABLE + "\"(type,\"timeStamp\",event,channel) VALUES(?,?,?,?)";
        try {
            PreparedStatement stmt = getStatement(sql);
            stmt.setString(1, alertType);
            stmt.setTimestamp(2, getSqlTimestamp(timestamp));
            stmt.setString(3, msg);
            stmt.setString(4, exchangeChannel);
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(PostgresStorage.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateMoteStatus(int id, boolean status) {
        /*
         id integer NOT NULL,
         status boolean NOT NULL,
         "moteModel" character varying(5) NOT NULL,
         "moteLocalization" character varying(6),
         "localizationDescription" character varying(100)
         */
        
        String sql = "UPDATE \"" + Storage.MOTE_TABLE + "\" SET status=? WHERE id=?";
        System.out.println(sql);
        try {
            PreparedStatement stmt = getStatement(sql);           
            stmt.setBoolean(1, status);
            stmt.setInt(2, id);
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(PostgresStorage.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    private java.sql.Date getSqlDate(long timestamp) {
        return new java.sql.Date(timestamp);
    }

    private java.sql.Timestamp getSqlTimestamp(long timestamp) {
        return new java.sql.Timestamp(timestamp);
    }

    private PreparedStatement getStatement(String sql) throws SQLException {
        connection.createStatement();
        return connection.prepareStatement(sql);
    }
}
