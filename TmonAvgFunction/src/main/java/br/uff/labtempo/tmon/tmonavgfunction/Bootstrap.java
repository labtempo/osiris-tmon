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
package br.uff.labtempo.tmon.tmonavgfunction;

import br.uff.labtempo.omcp.server.OmcpServer;
import br.uff.labtempo.omcp.server.rabbitmq.RabbitServer;
import br.uff.labtempo.tmon.tmonavgfunction.controller.MainController;
import br.uff.labtempo.tmon.tmonavgfunction.controller.utils.FunctionInterfaceFactory;
import java.util.Properties;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class Bootstrap implements AutoCloseable {

    private OmcpServer omcpServer;

    public Bootstrap(Properties properties) throws Exception {

        String ip = properties.getProperty("rabbitmq.server.ip");
        String user = properties.getProperty("rabbitmq.user.name");
        String pass = properties.getProperty("rabbitmq.user.pass");

        //average.function
        String moduleName = FunctionInterfaceFactory.MODULE_NAME;

        try {            
            omcpServer = new RabbitServer(moduleName, ip, user, pass);
            omcpServer.setHandler(new MainController());
        } catch (Exception ex) {
            close();
            throw ex;
        }
    }

    public void start() {
        try {
            omcpServer.start();
        } catch (Exception ex) {
            close();
            throw ex;
        }
    }

    @Override
    public void close() {
        try {
            omcpServer.close();
        } catch (Exception e) {
        }        
    }
}
