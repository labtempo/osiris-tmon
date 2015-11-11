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
import java.text.ParseException;
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
    private final String COMMAND_ENTRY = "python " + FILE;
    private TmonDataParser parser;

    public TinyOsDriver() throws Exception {
        this.parser = new TmonDataParser();
        parser.exclude("Flushing the serial port..");

        String path = GetExecutionPath();
        File f = new File(path + "/" + FILE);
        if (!f.exists()) {
            copyFileFromResourceToJarFolder(FILE, f);
            if (!f.exists()) {
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
        command = new ConsoleCommand(COMMAND_ENTRY);
        command.setPrinter(this);
        command.execute();
    }

    @Override
    public void close() throws IOException {
        command.close();
    }

    @Override
    public void setInputStream(InputStream is) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                SensorCoTo sensor = parseData(line);
                if (sensor != null) {
                    listener.onDataCapture(sensor);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(TinyOsDriver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close();
            reader.close();
        }

    }

    private SensorCoTo parseData(String data) throws Exception {
        try {
            /*
             *
             Flushing the serial port..
             45678:0:25.132156:31:3.177922:0:10
             45678:1:25.229326:64:3.177922:0:10
             45678:2:25.229326:71:3.177922:0:10
             45678:3:25.229326:69:3.177922:0:10
             45678:4:25.326550:79:3.177922:0:10
             45678:5:25.326550:71:3.177922:0:10
             45678:6:25.326550:68:3.177922:0:10
             45678:7:25.423830:67:3.177922:0:10
             45678:8:25.423830:71:3.177922:0:10
             45678:9:25.521165:70:3.177922:0:10
             45678:10:25.618557:71:3.177922:0:10
             45678:11:25.618557:76:3.177922:0:10
             45678:12:25.716006:75:3.177922:0:10
             */
            SensorCoTo sensor = parser.parse(data);
            return sensor;
        } catch (ParseException ex) {
            Logger.getLogger(TinyOsDriver.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
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
