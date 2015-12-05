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
package br.uff.labtempo.tmon.tmonavgfunction.controller.utils;

import br.uff.labtempo.osiris.to.common.definitions.FunctionOperation;
import br.uff.labtempo.osiris.to.common.definitions.ValueType;
import br.uff.labtempo.osiris.to.function.InterfaceFnTo;
import br.uff.labtempo.osiris.to.function.ParamFnTo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class FunctionInterfaceFactory {

    public static final String MODULE_NAME = "average.function";
    public static final String NAME = "avg";
    public static final String DESRIPTION = "modulo para calcular a media de temperatura";
    public static final String REQUEST_PARAM = "temperatures";
    public static final String RESPONSE_PARAM = "average";
    public static final String ADDRESS = "omcp://average.function.osiris/";

    public static InterfaceFnTo getInterface() {
        List<FunctionOperation> operations = new ArrayList<>();
        operations.add(FunctionOperation.SYNCHRONOUS);

        List<ParamFnTo> requestParamFnTos = new ArrayList<>();
        requestParamFnTos.add(new ParamFnTo(REQUEST_PARAM, ValueType.NUMBER, true));

        List<ParamFnTo> responseParamFnTos = new ArrayList<>();
        responseParamFnTos.add(new ParamFnTo(RESPONSE_PARAM, ValueType.NUMBER));

        InterfaceFnTo interfaceFnTo = new InterfaceFnTo(NAME, DESRIPTION, ADDRESS, operations, requestParamFnTos, responseParamFnTos);
        return interfaceFnTo;
    }

}
