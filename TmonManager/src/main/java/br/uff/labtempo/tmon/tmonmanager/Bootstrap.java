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
package br.uff.labtempo.tmon.tmonmanager;

import br.uff.labtempo.omcp.client.OmcpClient;
import br.uff.labtempo.omcp.client.OmcpClientBuilder;
import br.uff.labtempo.omcp.service.OmcpService;
import br.uff.labtempo.omcp.service.rabbitmq.RabbitService;
import br.uff.labtempo.osiris.omcp.EventController;
import br.uff.labtempo.tmon.tmonmanager.controller.MainController;
import br.uff.labtempo.tmon.tmonmanager.controller.util.CachedVsnManager;
import br.uff.labtempo.tmon.tmonmanager.controller.util.VsnManager;
import br.uff.labtempo.tmon.tmonmanager.controller.util.VsnManagerImpl;
import br.uff.labtempo.tmon.tmonmanager.factory.ConnectionFactory;
import br.uff.labtempo.tmon.tmonmanager.persistence.PostgresStorage;
import br.uff.labtempo.tmon.tmonmanager.persistence.Storage;
import java.sql.Connection;
import java.util.Properties;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class Bootstrap implements AutoCloseable {

    private OmcpService omcpService;
    private final OmcpClient omcpClient;
    private static final long AVERAGE_INTERVAL_IN_MILLIS = 6000;

    public Bootstrap(Properties properties) throws Exception {
        this(properties, false);
    }

    public Bootstrap(Properties properties, boolean silent) throws Exception {
        String ip = properties.getProperty("rabbitmq.server.ip");
        String user = properties.getProperty("rabbitmq.user.name");
        String pass = properties.getProperty("rabbitmq.user.pass");

        Connection connection = getConnection(properties);
        //TMON MANAGER
        String moduleName = Config.MODULE_NAME;

        try {

            omcpClient = new OmcpClientBuilder().host(ip).user(user, pass).source(moduleName).build();

            VsnManager manager = new CachedVsnManager(new VsnManagerImpl(omcpClient), 60);//60 seconds to expire cache
            Storage storage = new PostgresStorage(connection);
            EventController mainController = new MainController(manager, storage, AVERAGE_INTERVAL_IN_MILLIS);

            omcpService = new RabbitService(ip, user, pass, silent);
            omcpService.addReference("omcp://update.messagegroup/#");
            omcpService.setHandler(mainController);
        } catch (Exception ex) {
            close();
            throw ex;
        }
    }

    public void start() {
        try {
            omcpService.start();
        } catch (Exception ex) {
            close();
            throw ex;
        }
    }

    @Override
    public void close() {
        try {
            omcpService.close();
        } catch (Exception e) {
        }

        try {
            omcpClient.close();
        } catch (Exception e) {
        }
    }

    private Connection getConnection(Properties properties) {

        String ip = properties.getProperty("postgres.server.ip");
        String port = properties.getProperty("postgres.server.port");
        String user = properties.getProperty("postgres.user.name");
        String pass = properties.getProperty("postgres.user.pass");
        String db = properties.getProperty("postgres.server.db");

        Connection connection = new ConnectionFactory(ip, port, user, pass, db).getConnection();

        return connection;
    }
}
