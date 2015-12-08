/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package felipe.br.consolepython;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author proto
 */
public class App {

    public static void main(String[] args) throws Exception {
        ///home/proto/Projetos/python/loop.py
        String pythonPath = "PYTHONPATH=/opt/tinyos-2.1.2/support/sdk/python:/home/se/workspace/Thermal_Management/SharedLibs/trunk/src:$PYTHONPATH";
        String command = "python -u /home/se/workspace/Thermal_Management/SmartSystemMonitor/HybridWSNCollector.py serial@/dev/ttyUSB1:57600 0xee iris";
        String[] cmd = {"/bin/sh", "-c", pythonPath + " " + command};

//String command = "ping google.com -w 3";
        Process process = Runtime.getRuntime().exec(cmd);
        InputStream is = process.getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        process.waitFor();
    }
}
