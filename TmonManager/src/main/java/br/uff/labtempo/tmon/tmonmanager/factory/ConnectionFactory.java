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
package br.uff.labtempo.tmon.tmonmanager.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class ConnectionFactory {

    private String ip;
    private String port;
    private String user;
    private String pass;
    private String db;

    public ConnectionFactory(String ip, String port, String user, String pass, String db) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.db = db;
    }
    public ConnectionFactory(String ip, int port, String user, String pass, String db) {
        this(ip, String.valueOf(port), user, pass, db);
    }
    
    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://"+ip+":"+port+"/"+db, user, pass);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
