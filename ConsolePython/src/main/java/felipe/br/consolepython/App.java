/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package felipe.br.consolepython;

import br.uff.labtempo.tmon.tmoncollector.utils.command.Command;
import br.uff.labtempo.tmon.tmoncollector.utils.command.ConsoleCommand;
import br.uff.labtempo.tmon.tmoncollector.utils.command.Printer;
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
        String pythonCommand = "python -u";
        String pythonPrgramFolder = "/home/se/workspace/Thermal_Management/SmartSystemMonitor/";
        String pythonPrgramFilename = "HybridWSNCollector.py";        
        String prgramFile = (pythonPrgramFilename + "/" + pythonPrgramFilename).replace("//", "/");
        String params = "serial@/dev/ttyUSB1:57600 0xee iris";
        
        
        String[] cmd = {"/bin/sh", "-c", pythonPath, pythonCommand, prgramFile, params};

//String command = "ping google.com -w 3";
        Command c = new ConsoleCommand(cmd);
        c.setPrinter(new Printer() {
            
            @Override
            public void setInputStream(InputStream is) throws Exception {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
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
        });
        c.execute();
    }
}
