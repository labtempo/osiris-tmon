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
package br.uff.labtempo.tmon.tmonmanager.controller;

import br.uff.labtempo.omcp.common.Request;
import br.uff.labtempo.omcp.common.exceptions.AbstractRequestException;
import br.uff.labtempo.omcp.common.exceptions.BadRequestException;
import br.uff.labtempo.omcp.common.exceptions.InternalServerErrorException;
import br.uff.labtempo.omcp.common.exceptions.MethodNotAllowedException;
import br.uff.labtempo.omcp.common.exceptions.NotFoundException;
import br.uff.labtempo.omcp.common.exceptions.NotImplementedException;
import br.uff.labtempo.osiris.omcp.EventController;
import br.uff.labtempo.osiris.to.common.definitions.Path;
import br.uff.labtempo.osiris.to.sensornet.SensorSnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.DataTypeVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.FunctionVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.LinkVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.VirtualSensorVsnTo;
import br.uff.labtempo.tmon.tmonmanager.Config;
import br.uff.labtempo.tmon.tmonmanager.controller.util.ToHandler;
import br.uff.labtempo.tmon.tmonmanager.controller.util.ToHandlerImpl;
import br.uff.labtempo.tmon.tmonmanager.controller.util.VsnManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class MainController extends EventController {

    private VsnManager remote;
    private long averageIntervalInMillis;
    private final String AVERAGE_FUNCTION_MODULE_ADDRESS = Config.AVERAGE_FUNCTION_MODULE_ADDRESS;
    private final String BLENDING_FIELD_NAME = Config.BLENDING_FIELD_NAME;
    private final String FUNCTION_REQUEST_PARAM = Config.FUNCTION_REQUEST_PARAM;
    private final String FUNCTION_RESPONSE_PARAM = Config.FUNCTION_RESPONSE_PARAM;
    private final ToHandler creator;

    public MainController(VsnManager manager, long averageIntervalInMillis) {
        this.creator = new ToHandlerImpl();
        this.remote = manager;
        this.averageIntervalInMillis = averageIntervalInMillis;
    }

    @Override
    public void process(Request request) throws MethodNotAllowedException, NotFoundException, InternalServerErrorException, NotImplementedException, BadRequestException {
        try {
            if (request.getModule().contains(Path.NAMING_MODULE_VIRTUALSENSORNET.toString())) {
                VirtualSensorVsnTo virtualSensor = request.getContent(VirtualSensorVsnTo.class);
                checkVirtualSensor(virtualSensor);
            }

            if (request.getModule().contains(Path.NAMING_MODULE_SENSORNET.toString())) {
                final String UNIQUE_SENSOR = Path.SEPARATOR.toString() + Path.NAMING_MODULE_SENSORNET + Path.RESOURCE_SENSORNET_SENSOR_BY_ID;
                if (match(request.getResource(), UNIQUE_SENSOR)) {
                    SensorSnTo sensor = request.getContent(SensorSnTo.class);
                    checkSensor(sensor);
                }
            }
        } catch (AbstractRequestException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //checkVirtualSensor()
    public void checkVirtualSensor(VirtualSensorVsnTo virtualSensor) {
        switch (virtualSensor.getSensorType()) {
            case LINK:
                switch (virtualSensor.getState()) {
                    case NEW:
                        addLinkToBlending(virtualSensor);
                        break;
                    case INACTIVE:
                        removeLinkFromBlending(virtualSensor);
                        break;
                }
                break;
            case BLENDING:
                persistTmonAverageTable(virtualSensor);
                break;
        }
    }

    //checkSensor()
    public void checkSensor(SensorSnTo sensor) {
        switch (sensor.getState()) {
            case NEW:
                createLink(sensor);
                break;
        }
        persistTmonSensorTable(sensor);
    }

    private void persistTmonSensorTable(SensorSnTo sensor) {
        //TODO: persistir dados
        //TODO: persistir eventos
    }

    private void persistTmonAverageTable(VirtualSensorVsnTo virtualSensor) {
    }

    private LinkVsnTo createLink(SensorSnTo sensor) {
        List<DataTypeVsnTo> collectorDataTypes = creator.generateDataType(sensor);

        DataTypeVsnTo[] dataTypes = remote.omcpGetDataTypes();
        List<DataTypeVsnTo> toCreateDataTypes = new ArrayList<>(collectorDataTypes);
        List<DataTypeVsnTo> toAddDataTypes = new ArrayList<>();
        //compare datatypes        
        for (DataTypeVsnTo dataType : dataTypes) {
            for (DataTypeVsnTo collectorDataType : collectorDataTypes) {
                if (dataType.equals(collectorDataType)) {
                    toCreateDataTypes.remove(collectorDataType);
                    toAddDataTypes.add(dataType);
                    break;
                }
            }
        }

        //create datatypes
        for (DataTypeVsnTo toCreateDataType : toCreateDataTypes) {
            DataTypeVsnTo newDtvt = remote.omcpCreateDataType(toCreateDataType);
            toAddDataTypes.add(newDtvt);
        }

        //generate link
        LinkVsnTo link = creator.generateLink(toAddDataTypes, sensor);
        link = remote.omcpCreateLink(link);

        return link;
    }

    private void addLinkToBlending(VirtualSensorVsnTo virtualSensor) {
        BlendingVsnTo[] blendings = remote.omcpGetBlendings();
        BlendingVsnTo blending;
        
        //check blending exists
        if (blendings.length == 0) {
            blending = createBlending();
        } else {
            blending = blendings[0];
        }
        
        //check function
        if (blending.getFunctionId() == 0) {
            blending = setBlendingFunction(blending);
        }

        blending = creator.addVsensorToBlending(blending, virtualSensor, BLENDING_FIELD_NAME, FUNCTION_REQUEST_PARAM, FUNCTION_RESPONSE_PARAM);
        remote.omcpUpdateBlending(blending);
    }

    private void removeLinkFromBlending(VirtualSensorVsnTo virtualSensor) {
        BlendingVsnTo[] blendings = remote.omcpGetBlendings();
        BlendingVsnTo blending;
        
        //check blending exists
        if (blendings.length == 0) {
            throw new RuntimeException("Not have blendings!");
        }
        blending = blendings[0];        
        
        blending = creator.removeVsensorFromBlending(blending, virtualSensor, BLENDING_FIELD_NAME);
        
        //remove function if not have request params
        if (blending.getRequestParams().isEmpty()) {
            blending = creator.removeFunctionFromBlending(blending);
        }
        
        remote.omcpUpdateBlending(blending);
    }

    private BlendingVsnTo createBlending() {
        DataTypeVsnTo dataType = null;
        DataTypeVsnTo[] dataTypes = remote.omcpGetDataTypes();

        for (DataTypeVsnTo dt : dataTypes) {
            if (dt.getDisplayName().equals(BLENDING_FIELD_NAME)) {
                dataType = dt;
                break;
            }
        }

        if (dataType == null) {
            throw new RuntimeException("Not has compatible DataType!");
        }

        BlendingVsnTo blending = creator.generateBlending(dataType, BLENDING_FIELD_NAME);
        blending = remote.omcpCreateBlending(blending);
        return blending;
    }

    private BlendingVsnTo setBlendingFunction(BlendingVsnTo blending) {
        FunctionVsnTo function;
        FunctionVsnTo[] functions = remote.omcpGetFunctions();

        if (functions.length > 0) {
            function = functions[0];
        } else {
            function = remote.omcpCreateFunctionFrom(AVERAGE_FUNCTION_MODULE_ADDRESS);
        }

       blending = creator.addFunctionToBlending(blending, function, averageIntervalInMillis);
       
       return blending;
    }
}
