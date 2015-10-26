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
package br.uff.labtempo.tmon.tmonmanager.controller.util;

import br.uff.labtempo.osiris.to.sensornet.SensorSnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.DataTypeVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.FunctionVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.LinkVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.VirtualSensorVsnTo;
import java.util.List;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public interface ToHandler {

    List<DataTypeVsnTo> generateDataType(SensorSnTo sensor);

    LinkVsnTo generateLink(DataTypeVsnTo dataType, SensorSnTo sensor);

    LinkVsnTo generateLink(List<DataTypeVsnTo> dataTypes, SensorSnTo sensor);

    BlendingVsnTo generateBlending(DataTypeVsnTo dataType, String fieldName);

    BlendingVsnTo addVsensorToBlending(BlendingVsnTo blending, VirtualSensorVsnTo virtualSensor, String fieldName, String requestParamName, String responseParamName);

    BlendingVsnTo removeVsensorFromBlending(BlendingVsnTo blending, VirtualSensorVsnTo virtualSensor, String fieldName);

    BlendingVsnTo addFunctionToBlending(BlendingVsnTo blending, FunctionVsnTo function, long callInterval);

    BlendingVsnTo removeFunctionFromBlending(BlendingVsnTo blending);
}
