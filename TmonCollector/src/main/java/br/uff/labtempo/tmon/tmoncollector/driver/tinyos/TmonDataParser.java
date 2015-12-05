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
package br.uff.labtempo.tmon.tmoncollector.driver.tinyos;

import br.uff.labtempo.osiris.to.collector.SensorCoTo;
import br.uff.labtempo.osiris.to.common.definitions.LogicalOperator;
import br.uff.labtempo.tmon.tmoncollector.Config;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class TmonDataParser {

    private List<String> exclusion;

    public TmonDataParser() {
        exclusion = new ArrayList<>();
    }

    public void exclude(String line) {
        exclusion.add(line);
    }

    public SensorCoTo parse(String line) throws ParseException {
        //mote, time, sendCount, readingTemperature, readingLight, readingVoltage, parent, metric, moteModel
        int index = 0;
        try {
            String[] data = line.split("\\|");

            String mote = data[0];
            index++;
            //String
            long time = getTimeStampFromString(data[1]);
            index++;
            String sendCount = data[2];
            index++;
            double readingTemperature = Double.parseDouble(data[3]);
            index++;
            int readingLight = Integer.parseInt(data[4]);
            index++;
            double readingVoltage = Double.parseDouble(data[5]);
            index++;
            String parent = data[6];
            index++;
            String metric = data[7];
            index++;
            String moteModel = data[8];

            SensorCoTo sensor = new SensorCoTo(mote, time);
            sensor.addValue(Config.SENSOR_VALUE_TEMPERATURE, readingTemperature, "celsius", "C°");
            sensor.addValue(Config.SENSOR_VALUE_LIGHT, readingLight, "candela", "C");
            sensor.addValue(Config.SENSOR_VALUE_VOLTAGE, readingVoltage, "volts", "V");

            sensor.addInfo(Config.SENSOR_INFO_SEND_COUNT, sendCount);
            sensor.addInfo(Config.SENSOR_INFO_PARENT, parent);
            sensor.addInfo(Config.SENSOR_INFO_METRIC, metric);
            sensor.addInfo(Config.SENSOR_INFO_MOTE_MODEL, moteModel);
            //consumables
//            double minVoltage = 1.6;
//            double maxVoltage = 3.0;
//            double current = (readingVoltage - minVoltage);
//            double max = (maxVoltage - minVoltage);
//            int currentPercent = (int) (current * 100 / max);
//
//            if (currentPercent > 100) {
//                currentPercent = 100;
//            }
//            sensor.addConsumable("battery", currentPercent);
//            sensor.addConsumableRule("Bateria baixa", "battery", LogicalOperator.LESS_THAN, 80, "Bateria baixa!");

            return sensor;
        } catch (Exception ex) {
            for (String item : exclusion) {
                if (item.equalsIgnoreCase(line)) {
                    return null;
                }
            }
            throw new ParseException(ex.getMessage(), index);
        }
    }

    private long getTimeStampFromString(String datetime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = format.parse(datetime);
        return date.getTime();
    }
}
