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

import br.uff.labtempo.tmon.tmoncollector.driver.DataListener;
import br.uff.labtempo.tmon.tmoncollector.driver.Driver;
import br.uff.labtempo.osiris.to.collector.SensorCoTo;
import br.uff.labtempo.tmon.tmoncollector.utils.command.Command;
import br.uff.labtempo.tmon.tmoncollector.utils.command.ConsoleCommand;
import br.uff.labtempo.tmon.tmoncollector.utils.command.Printer;
import br.uff.labtempo.tmon.tmoncollector.utils.command.PythonConsoleCommand;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class TinyOsDriver implements Driver<SensorCoTo>, Printer {

    private DataListener<SensorCoTo> listener;
    private Command command;
    //python HybridWSNCollector.py serial@/dev/ttyUSB1:57600 0xee iris
    private final String FILE = "HybridWSNCollector.py";
    private final String COMMAND_ENTRY = "python" + " " + FILE;
    private TmonDataParser parser;
    private String params;

    public TinyOsDriver(String params) throws Exception {
//        this.params = params;
        this.parser = new TmonDataParser();
//        parser.exclude("Flushing the serial port..");
//
//        String path = GetExecutionPath();
//        File f = new File(path + "/" + FILE);
//        if (!f.exists()) {
//            copyFileFromResourceToJarFolder(FILE, f);
//            if (!f.exists()) {
//                throw new IOException(FILE + " could not be created!");
//            }
//        }
    }

    @Override
    public void setOnDataCaptureListener(DataListener<SensorCoTo> listener) {
        this.listener = listener;
    }

    @Override
    public void start() throws Exception {
//        String cmd = COMMAND_ENTRY+" "+params;
//        System.out.println(cmd);
//        command = new PythonConsoleCommand(cmd);
//        command.setPrinter(this);
//        command.execute();

        List<String> samples = new ArrayList<>();

        samples.add("45678|2015-11-11 15:47:37.136511|0|25.132156|31|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:37.136511|0|25.132156|31|3.177922|0|10|iris");
        samples.add("45678|2015-11-11 15:47:38.136511|1|25.229326|64|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:38.136511|1|25.229326|64|3.177922|0|10|iris");
        samples.add("45678|2015-11-11 15:47:39.136511|2|25.229326|71|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:39.136511|2|25.229326|71|3.177922|0|10|iris");
        samples.add("45678|2015-11-11 15:47:40.136511|3|25.229326|69|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:40.136511|3|25.229326|69|3.177922|0|10|iris");
        samples.add("45678|2015-11-11 15:47:41.136511|4|25.326550|79|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:41.136511|4|25.326550|79|3.177922|0|10|iris");
        samples.add("45678|2015-11-11 15:47:42.136511|5|25.326550|71|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:42.136511|5|25.326550|71|3.177922|0|10|iris");
        samples.add("45678|2015-11-11 15:47:43.136511|6|25.326550|68|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:43.136511|6|25.326550|68|3.177922|0|10|iris");
        samples.add("45678|2015-11-11 15:47:44.136511|7|25.423830|67|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:44.136511|7|25.423830|67|3.177922|0|10|iris");
        samples.add("45678|2015-11-11 15:47:45.136511|8|25.423830|71|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:45.136511|8|25.423830|71|3.177922|0|10|iris");
        samples.add("45678|2015-11-11 15:47:46.136511|9|25.521165|70|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:46.136511|9|25.521165|70|3.177922|0|10|iris");
        samples.add("45678|2015-11-11 15:47:47.136511|10|25.618557|71|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:47.136511|10|25.618557|71|3.177922|0|10|iris");
        samples.add("45678|2015-11-11 15:47:48.136511|11|25.618557|76|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:48.136511|11|25.618557|76|3.177922|0|10|iris");
        samples.add("45678|2015-11-11 15:47:49.136511|12|25.716006|75|3.177922|0|10|iris");
        samples.add("45677|2015-11-11 15:47:49.136511|12|25.716006|75|3.177922|0|10|iris");

        for (String sample : samples) {
            SensorCoTo sensor = parseData(sample);
            listener.onDataCapture(sensor);
            TimeUnit.SECONDS.sleep(30);
        }

    }

    @Override
    public void close() throws IOException {
        command.close();
    }

    @Override
    public void setInputStream(InputStream is) throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            SensorCoTo sensor = parseData(line);
            if (sensor != null) {
                listener.onDataCapture(sensor);
            }
        }
    }

    @Override
    public void setErrorStream(InputStream is) throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    private SensorCoTo parseData(String data) throws Exception {
        SensorCoTo sensor = parser.parse(data);
        return sensor;
    }

    private void copyFileFromResourceToJarFolder(String resource, File destination) throws FileNotFoundException, IOException {
        InputStream ddlStream = getClass().getClassLoader().getResourceAsStream(resource);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(destination);
            byte[] buf = new byte[2048];
            int r = ddlStream.read(buf);
            while (r != -1) {
                fos.write(buf, 0, r);
                r = ddlStream.read(buf);
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    private String GetExecutionPath() {
        String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
        absolutePath = absolutePath.replaceAll("%20", " "); // Surely need to do this here
        return absolutePath;
    }

}
