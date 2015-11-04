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
package br.uff.labtempo.tmon.tmonmanager.utils;

import br.uff.labtempo.osiris.to.sensornet.SensorSnTo;
import br.uff.labtempo.tmon.tmonmanager.persistence.Storage;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class DummyStorage implements Storage {

    @Override
    public void storeSample(SensorSnTo sensor) {
        System.out.println("storeSample");
    }

    @Override
    public void storeAverageTemperature(double temperature, long timestamp) {
        System.out.println("storeAverageTemperature");
    }

    @Override
    public void storeMote(SensorSnTo sensor) {
        System.out.println("storeMote");
    }

    @Override
    public void storeAlert(String alertType, long timestamp, String msg, String exchangeChannel) {
        System.out.println("storeAlert");
    }

    @Override
    public void updateMoteStatus(int id, boolean status ) {
        System.out.println("updateMoteStatus");
    }

}
