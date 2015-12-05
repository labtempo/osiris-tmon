/*
 * Copyright 2015 se.
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

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.python.util.PythonInterpreter;

/**
 *
 * @author se
 */
public class PythonConsoleCommand implements Command {

    private final String command;
    private Printer printer;
    private PythonInterpreter interp;
    private PipedInputStream in;
    private PipedOutputStream inOut;
    private PipedInputStream err;
    private PipedOutputStream errOut;

    public PythonConsoleCommand(String command) {
        this.command = command;
    }

    public PythonConsoleCommand(String command, Printer printer) {
        this.command = command;
        this.printer = printer;
    }

    @Override
    public void close() {
        interp.close();
        try {
            inOut.close();
        } catch (IOException ex) {
        }
        try {
            in.close();
        } catch (IOException ex) {
        }
        try {
            errOut.close();
        } catch (IOException ex) {
        }
        try {
            err.close();
        } catch (IOException ex) {
        }
    }

    @Override
    public void execute() throws Exception {
        in = new PipedInputStream();
        inOut = new PipedOutputStream(in);
        err = new PipedInputStream();
        errOut = new PipedOutputStream(err);
        printer.setInputStream(in);
        printer.setErrorStream(err);
        PythonInterpreter.initialize(System.getProperties(), System.getProperties(), new String[0]);
        interp = new PythonInterpreter();
        interp.setErr(errOut);
        interp.setOut(inOut);
        interp.exec(command);
    }

    @Override
    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

}
