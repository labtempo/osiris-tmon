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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class TinyOsDriver implements Driver<SensorCoTo>, Printer {

    private DataListener<SensorCoTo> listener;
    private Command command;
    //python HybridWSNCollector.py serial@/dev/ttyUSB1:57600 0xee iris
    private final String FILE = "HybridWSNCollector.py";
    private TmonDataParser parser;
    private String params;
    private String pythonPath;
    private String pythonCommand;
    private String pythonPrgramFile;

    public TinyOsDriver(String smartSystemFolder, String pythonPath, String pythonCommand, String params) throws IOException {
        this.pythonPath = pythonPath;
        this.pythonCommand = pythonCommand;
        this.pythonPrgramFile = (smartSystemFolder + "/" + FILE).replace("//", "/");
        this.params = params;
        this.parser = new TmonDataParser();
        parser.exclude("Flushing the serial port..");
        //copy HybridWsnCollector to smartSystemFolder
        File file = new File(pythonPrgramFile);
        if (!file.exists()) {
            copyFileFromResourceToJarFolder(FILE, file);
            if (!file.exists()) {
                throw new IOException(FILE + " could not be created!");
            }
        }
    }

    @Override
    public void setOnDataCaptureListener(DataListener<SensorCoTo> listener) {
        this.listener = listener;
    }

    @Override
    public void start() throws Exception {
        String[] cmd = {"/bin/sh", "-c", pythonPath + " " + pythonCommand + " " + pythonPrgramFile + " " + params};
        command = new ConsoleCommand(cmd);
        command.setPrinter(this);
        command.execute();
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
            System.out.println(line);
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

//    private String GetExecutionPath() {
//        String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
//        absolutePath = absolutePath.replaceAll("%20", " "); // Surely need to do this here
//        return absolutePath;
//    }
}
