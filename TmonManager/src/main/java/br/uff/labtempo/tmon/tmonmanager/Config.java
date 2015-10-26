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

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class Config {
    public static final String MODULE_NAME = "TMON_MANAGER";
    public static final String AVERAGE_FUNCTION_MODULE_ADDRESS = "omcp://average.function.osiris/";
    public static final String BLENDING_FIELD_NAME = "temperature";
    public static final String FUNCTION_REQUEST_PARAM = "temperatures";
    public static final String FUNCTION_RESPONSE_PARAM = "average";
}
