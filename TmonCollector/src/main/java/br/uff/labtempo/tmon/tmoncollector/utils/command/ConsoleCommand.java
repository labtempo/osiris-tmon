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
package br.uff.labtempo.tmon.tmoncollector.utils.command;

import java.io.InputStream;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class ConsoleCommand implements Command {

    private final String command;
    private Printer printer;
    private Process process;

    public ConsoleCommand(String command) {
        this.command = command;
    }

    public ConsoleCommand(String command, Printer printer) {
        this.command = command;
        this.printer = printer;
    }

    @Override
    public void execute() throws Exception {
        if (printer != null) {
            process = Runtime.getRuntime().exec(command);
            InputStream is = process.getInputStream();
            printer.setInputStream(is);
            printer.setErrorStream(process.getErrorStream());
            process.waitFor();
            process = null;
        }
    }

    @Override
    public void close() {
        if (isAlive(process)) {
            process.destroy();
        }
    }

    private boolean isAlive(Process process) {
        if (process == null) {
            return false;
        }
        try {
            process.exitValue();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

}
