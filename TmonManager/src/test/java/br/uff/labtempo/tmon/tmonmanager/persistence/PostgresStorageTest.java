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

import br.uff.labtempo.osiris.to.common.definitions.State;
import br.uff.labtempo.osiris.to.common.definitions.ValueType;
import br.uff.labtempo.osiris.to.sensornet.SensorSnTo;
import br.uff.labtempo.tmon.tmonmanager.Config;
import br.uff.labtempo.tmon.tmonmanager.factory.ConnectionFactory;
import java.sql.Connection;
import java.util.Calendar;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class PostgresStorageTest {

    // Channels
    private final String EXCHANGE_READINGS = "readings";
    private final String EXCHANGE_MAINTENANCE = "maintenance";
    private final String EXCHANGE_MOTE_STATUS = "mote_status";
    // Type of alerts
    private final String ALERT_INFO = "info";
    private final String ALERT_WARNING = "warning";
    private final String ALERT_CRITICAL = "critical";

    public PostgresStorageTest() {
    }

    @Ignore
    @Test
    public void testPersistence_temp_ShouldPass() {
        Storage storage = new PostgresStorage(getConnection());

        SensorSnTo sensor = getSensor();

        storage.storeMote(sensor);
        storage.storeSample(sensor);
        storage.updateMoteStatus(1, false);
        storage.storeAverageTemperature(1.5, System.currentTimeMillis());
        storage.storeAlert(ALERT_WARNING, sensor.getCaptureTimestampInMillis(), "Mote ID: " + 1 + " back to the network", EXCHANGE_MOTE_STATUS);

    }

    private SensorSnTo getSensor() {
        SensorSnTo sensor = new SensorSnTo("1", State.NEW, System.currentTimeMillis(), 0, 0, 0, Calendar.getInstance(), null, null);
        sensor.addValue(Config.SENSOR_VALUE_TEMPERATURE, ValueType.NUMBER, "30.5", "celsius", "C°");
        sensor.addValue(Config.SENSOR_VALUE_LIGHT, ValueType.NUMBER, "30", "candela", "C");
        sensor.addValue(Config.SENSOR_VALUE_VOLTAGE, ValueType.NUMBER, "1.5", "volts", "V");

        sensor.addInfo(Config.SENSOR_INFO_SEND_COUNT, "13");
        sensor.addInfo(Config.SENSOR_INFO_PARENT, "14");
        sensor.addInfo(Config.SENSOR_INFO_METRIC, "15");
        sensor.addInfo(Config.SENSOR_INFO_MOTE_MODEL, "iris");

        return sensor;
    }

    private Connection getConnection() {
        Connection connection = new ConnectionFactory("192.168.0.8", 5432, "postgres", "postgres", "tmon").getConnection();
        return connection;
    }

}
