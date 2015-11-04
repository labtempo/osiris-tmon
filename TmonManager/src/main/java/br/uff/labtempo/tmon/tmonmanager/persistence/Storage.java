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

import br.uff.labtempo.osiris.to.sensornet.SensorSnTo;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public interface Storage {

    final String AVERAGE_TABLE = "SensornetMonitor_averagetemperature";
    final String MOTE_TABLE = "SensornetMonitor_mote";
    final String SAMPLE_TABLE = "SensornetMonitor_sample";
    final String ALERT_TABLE = "AlertSystem_alert";

    void storeSample(SensorSnTo sensor);

    void storeAverageTemperature(double temperature, long timestamp);

    void storeMote(SensorSnTo sensor);

    void storeAlert(String alertType, long timestamp, String msg, String exchangeChannel);
    
    void updateMoteStatus(int id, boolean status );

}
