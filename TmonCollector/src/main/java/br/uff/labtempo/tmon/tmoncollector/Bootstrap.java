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


import java.util.Properties;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class Bootstrap implements AutoCloseable {

    public Bootstrap(Properties properties) throws Exception {
        this(properties, false);
    }

    public Bootstrap(Properties properties, boolean silent) throws Exception {
        String ip = properties.getProperty("rabbitmq.server.ip");
        String user = properties.getProperty("rabbitmq.user.name");
        String pass = properties.getProperty("rabbitmq.user.pass");

        //TMON MANAGER
       

        try {

            
        } catch (Exception ex) {
            close();
            throw ex;
        }
    }

    public void start() {
        try {
            
        } catch (Exception ex) {
            close();
            throw ex;
        }
    }

    @Override
    public void close() {
        try {
           
        } catch (Exception e) {
        }

        try {
            
        } catch (Exception e) {
        }
    }

}
