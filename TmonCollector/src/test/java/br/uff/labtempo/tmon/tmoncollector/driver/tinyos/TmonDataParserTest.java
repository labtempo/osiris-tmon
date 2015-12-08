/*
 * Copyright 2015 Felipe Santos <fralph at ic.uff.br>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http|//www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.uff.labtempo.tmon.tmoncollector.driver.tinyos;

import br.uff.labtempo.osiris.to.collector.SensorCoTo;
import br.uff.labtempo.osiris.to.common.data.InfoTo;
import br.uff.labtempo.osiris.to.common.data.ValueTo;
import br.uff.labtempo.tmon.tmoncollector.Config;
import java.text.ParseException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class TmonDataParserTest {

    private TmonDataParser parser;

    public TmonDataParserTest() {
        this.parser = new TmonDataParser();
        parser.exclude("Flushing the serial port..");
    }

    @Test
    public void testSomeMethod() throws ParseException {

        //45678|2015-11-11 15:47:37.136511|1|25.229326|237|3.338598|0|10|iris
        SensorCoTo sensor = parser.parse("Flushing the serial port..");
        assertNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|0|25.132156|31|3.177922|0|10|iris");
        assertNotNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|1|25.229326|64|3.177922|0|10|iris");
        assertNotNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|2|25.229326|71|3.177922|0|10|iris");
        assertNotNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|3|25.229326|69|3.177922|0|10|iris");
        assertNotNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|4|25.326550|79|3.177922|0|10|iris");
        assertNotNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|5|25.326550|71|3.177922|0|10|iris");
        assertNotNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|6|25.326550|68|3.177922|0|10|iris");
        assertNotNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|7|25.423830|67|3.177922|0|10|iris");
        assertNotNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|8|25.423830|71|3.177922|0|10|iris");
        assertNotNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|9|25.521165|70|3.177922|0|10|iris");
        assertNotNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|10|25.618557|71|3.177922|0|10|iris");
        assertNotNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|11|25.618557|76|3.177922|0|10|iris");
        assertNotNull(sensor);
        sensor = parser.parse("45678|2015-11-11 15:47:37.136511|12|25.716006|75|3.177922|0|10|iris");
        assertNotNull(sensor);


        assertEquals("45678", sensor.getId());
        assertEquals(1447264193511L, sensor.getCaptureTimestampInMillis());


        double temperature = 0;
        double voltage = 0;
        int light = 0;
        for (ValueTo value : sensor.getValuesTo()) {
            switch (value.getName()) {
                case Config.SENSOR_VALUE_LIGHT:
                    light = Integer.parseInt(value.getValue());
                    assertEquals(75, light);
                    break;
                case Config.SENSOR_VALUE_TEMPERATURE:
                    temperature = Double.parseDouble(value.getValue());
                    assertEquals(25.716006, temperature, 0);
                    break;
                case Config.SENSOR_VALUE_VOLTAGE:
                    voltage = Double.parseDouble(value.getValue());
                    assertEquals(3.177922, voltage, 0);
                    break;
            }
        }

        //info
        int sendCount = 0;
        int parent = 0;
        int metric = 0;
        String model = "";

        for (InfoTo info : sensor.getInfoTo()) {
            switch (info.getName()) {
                case Config.SENSOR_INFO_SEND_COUNT:
                    sendCount = Integer.parseInt(info.getDescription());
                    assertEquals(12, sendCount);
                    break;
                case Config.SENSOR_INFO_PARENT:
                    parent = Integer.parseInt(info.getDescription());
                    assertEquals(0, parent);
                    break;
                case Config.SENSOR_INFO_METRIC:
                    metric = Integer.parseInt(info.getDescription());
                    assertEquals(10, metric);
                    break;
                case Config.SENSOR_INFO_MOTE_MODEL:
                    model = info.getDescription();
                    assertEquals("iris", model);
                    break;
            }
        }

    }

}
