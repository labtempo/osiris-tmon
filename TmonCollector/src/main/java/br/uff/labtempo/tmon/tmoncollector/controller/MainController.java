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
package br.uff.labtempo.tmon.tmoncollector.controller;

import br.uff.labtempo.omcp.client.OmcpClient;
import br.uff.labtempo.osiris.to.collector.CollectorCoTo;
import br.uff.labtempo.osiris.to.collector.NetworkCoTo;
import br.uff.labtempo.osiris.to.collector.SampleCoTo;
import br.uff.labtempo.osiris.to.collector.SensorCoTo;
import br.uff.labtempo.tmon.tmoncollector.Config;
import br.uff.labtempo.tmon.tmoncollector.driver.DataListener;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class MainController implements DataListener<SensorCoTo> {

    private OmcpClient client;
    private String collectorName;
    private String networkName;
    private int captureInterval;

    public MainController(OmcpClient client, String collectorName, String networkName, int captureInterval) {
        this.client = client;
        this.collectorName = collectorName;
        this.networkName = networkName;
        this.captureInterval = captureInterval;
    }

    @Override
    public void onDataCapture(SensorCoTo sensor) {
        SampleCoTo sample = new SampleCoTo(getNetwork(), getCollector(), sensor);
        String url = getUrl(sample);
        client.doNofity(url, sample);
    }

    private NetworkCoTo getNetwork() {
        NetworkCoTo networkCoTo = new NetworkCoTo(networkName);
        networkCoTo.addInfo("domain", "br.uff.labtempo");
        networkCoTo.addInfo("type", "wireless");
        networkCoTo.addInfo("OS", "TinyOS");
        return networkCoTo;
    }

    private CollectorCoTo getCollector() {
        CollectorCoTo collectorCoTo = new CollectorCoTo(collectorName, captureInterval, TimeUnit.SECONDS);
//        collectorCoTo.addInfo("descricao", "sala do laboratorio");
//        collectorCoTo.addInfo("numero", "2");
//        collectorCoTo.addInfo("Topologia", "estrela");
        return collectorCoTo;
    }

    private String getUrl(SampleCoTo sample) {
        String url = Config.COLLECTOR_MESSAGEGROUP + sample.getNetwork().getId() + "/" + sample.getCollector().getId() + "/" + sample.getSensor().getId() + "/";
        return url;
    }

}
