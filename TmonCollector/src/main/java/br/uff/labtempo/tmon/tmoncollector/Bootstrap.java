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
package br.uff.labtempo.tmon.tmoncollector;

import br.uff.labtempo.omcp.client.OmcpClient;
import br.uff.labtempo.omcp.client.OmcpClientBuilder;
import br.uff.labtempo.osiris.to.collector.SensorCoTo;
import br.uff.labtempo.tmon.tmoncollector.controller.MainController;
import br.uff.labtempo.tmon.tmoncollector.driver.tinyos.TinyOsDriver;
import br.uff.labtempo.tmon.tmoncollector.driver.DataListener;
import br.uff.labtempo.tmon.tmoncollector.driver.Driver;
import br.uff.labtempo.tmon.tmoncollector.driver.virtual.VirtualDriver;
import java.util.Properties;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class Bootstrap implements AutoCloseable {

    private OmcpClient client;
    private Driver driver;

    public Bootstrap(String params, Properties properties) throws Exception {
        this(params, properties, false);
    }

    public Bootstrap(String params, Properties properties, boolean silent) throws Exception {
        String ip = properties.getProperty("rabbitmq.server.ip");
        String user = properties.getProperty("rabbitmq.user.name");
        String pass = properties.getProperty("rabbitmq.user.pass");
        String collectorName = properties.getProperty("tmon.collector.name");
        int captureInterval = Integer.parseInt(properties.getProperty("tmon.capture.interval.minutes"));
        //TMON Collector

        try {
            driver = new VirtualDriver();
            client = new OmcpClientBuilder().host(ip).user(user, pass).source(collectorName).build();
            DataListener<SensorCoTo> listener = new MainController(client, collectorName, "tmon", captureInterval);
            driver.setOnDataCaptureListener(listener);
        } catch (Exception ex) {
            close();
            throw ex;
        }
    }

    public void start() throws Exception {
        try {
            driver.start();
        } catch (Exception ex) {
            close();
            throw ex;
        }
    }

    @Override
    public void close() {
        try {
            client.close();
        } catch (Exception e) {
        }
        
        try {
            driver.close();
        } catch (Exception e) {
        }
    }

}
