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
import br.uff.labtempo.tmon.tmoncollector.Config;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
            String[] data = line.split("|");

            String mote = data[0];
            index++;
		//String
            long time = Long.parseLong(data[1]);
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
            return sensor;
        } catch (Exception ex) {
            for (String item : exclusion) {
                if (item.equalsIgnoreCase(line)) {
                    return null;
                }
            }
            throw new ParseException(line, index);
        }
    }
}
