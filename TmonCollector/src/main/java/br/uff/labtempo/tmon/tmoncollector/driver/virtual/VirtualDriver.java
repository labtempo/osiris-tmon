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
package br.uff.labtempo.tmon.tmoncollector.driver.virtual;

import br.uff.labtempo.tmon.tmoncollector.driver.tinyos.*;
import br.uff.labtempo.tmon.tmoncollector.driver.DataListener;
import br.uff.labtempo.tmon.tmoncollector.driver.Driver;
import br.uff.labtempo.osiris.to.collector.SensorCoTo;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class VirtualDriver implements Driver<SensorCoTo> {

    private long sendCount = 1;
    private DataListener<SensorCoTo> listener;
    private TmonDataParser parser;
    private List<Integer> sensorIds;
    private Map<Integer, Integer> sensorRelationship;
    private boolean running;

    public VirtualDriver() throws Exception {
        parser = new TmonDataParser();
        sensorIds = getSensorIds();
        sensorRelationship = getRelationship();
    }

    @Override
    public void setOnDataCaptureListener(DataListener<SensorCoTo> listener) {
        this.listener = listener;
    }

    @Override
    public void start() throws Exception {
        running = true;
        while (running) {
            sendData();
            TimeUnit.SECONDS.sleep(6);
        }

    }

    @Override
    public void close() throws IOException {
        running = false;
    }

    private void sendData() throws Exception {
        //mote, time, sendCount, readingTemperature, readingLight, readingVoltage, parent, metric, moteModel
        long count = sendCount++;
        for (Integer sensorId : sensorIds) {
            int iparent = 0;
            for (Map.Entry<Integer, Integer> entrySet : sensorRelationship.entrySet()) {
                Integer key = entrySet.getKey();
                Integer value = entrySet.getValue();

                if (key.equals(sensorId)) {
                    iparent = value;
                    break;
                }
            }

            String mote = String.valueOf(sensorId);
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").format(new Date());;
            String parent = String.valueOf(iparent);

            String sample = mote + "|" + time + "|"+ count +"|25.716006|75|3.177922|" + parent + "|10|iris";
            System.out.println(sample);
            SensorCoTo sensor = parser.parse(sample);
            listener.onDataCapture(sensor);
        }
    }

    private List<Integer> getSensorIds() {
        List<Integer> ids = new ArrayList<>();
        int base = 5555121;
        for (int i = 0; i < 9; i++) {
            ids.add(base++);
        }
        return ids;

    }

    private Map<Integer, Integer> getRelationship() {
        Map<Integer, Integer> r = new HashMap<>();
        r.put(5555121, 0);
        r.put(5555122, 5555121);
        r.put(5555123, 5555121);
        r.put(5555124, 5555121);
        r.put(5555125, 5555123);
        r.put(5555126, 5555125);
        r.put(5555127, 5555125);
        r.put(5555128, 5555124);
        r.put(5555129, 5555122);
        return r;
    }
}
