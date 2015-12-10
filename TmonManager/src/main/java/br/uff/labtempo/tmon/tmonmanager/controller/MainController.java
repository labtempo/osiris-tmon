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
import br.uff.labtempo.osiris.to.common.data.ValueTo;
import br.uff.labtempo.osiris.to.common.definitions.Path;
import br.uff.labtempo.osiris.to.common.definitions.State;
import br.uff.labtempo.osiris.to.sensornet.NetworkSnTo;
import br.uff.labtempo.osiris.to.sensornet.SensorSnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.DataTypeVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.FunctionVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.LinkVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.ValueVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.VirtualSensorVsnTo;
import br.uff.labtempo.tmon.tmonmanager.Config;
import br.uff.labtempo.tmon.tmonmanager.controller.util.ToHandler;
import br.uff.labtempo.tmon.tmonmanager.controller.util.ToHandlerImpl;
import br.uff.labtempo.tmon.tmonmanager.controller.util.VsnManager;
import br.uff.labtempo.tmon.tmonmanager.model.Mote;
import br.uff.labtempo.tmon.tmonmanager.persistence.Storage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class MainController extends EventController {

    private VsnManager remote;
    private long averageIntervalInMillis;
    private final String AVERAGE_FUNCTION_MODULE_ADDRESS = Config.AVERAGE_FUNCTION_MODULE_ADDRESS;
    private final String SENSOR_FIELD_NAME = Config.SENSOR_VALUE_TEMPERATURE;
    private final String BLENDING_FIELD_NAME = Config.BLENDING_FIELD_NAME;
    private final String FUNCTION_REQUEST_PARAM = Config.FUNCTION_REQUEST_PARAM;
    private final String FUNCTION_RESPONSE_PARAM = Config.FUNCTION_RESPONSE_PARAM;
    private final ToHandler creator;
    private final Storage storage;

    // Channels
    private final String EXCHANGE_READINGS = "readings";
    private final String EXCHANGE_MAINTENANCE = "maintenance";
    private final String EXCHANGE_MOTE_STATUS = "mote_status";
    // Type of alerts
    private final String ALERT_INFO = "info";
    private final String ALERT_WARNING = "warning";
    private final String ALERT_CRITICAL = "critical";

    //TODO: wsn is down!
    public MainController(VsnManager manager, Storage storage, long averageIntervalInMillis) {
        this.creator = new ToHandlerImpl();
        this.remote = manager;
        this.storage = storage;
        this.averageIntervalInMillis = averageIntervalInMillis;
    }

    @Override
    public void process(Request request) throws MethodNotAllowedException, NotFoundException, InternalServerErrorException, NotImplementedException, BadRequestException {
        try {
            if (request.getResource().contains(Path.NAMING_MODULE_VIRTUALSENSORNET.toString())) {
                VirtualSensorVsnTo virtualSensor = request.getContent(VirtualSensorVsnTo.class);
                checkVirtualSensor(virtualSensor);
            } else if (request.getResource().contains(Path.NAMING_MODULE_SENSORNET.toString())) {
                final String UNIQUE_SENSOR = Path.SEPARATOR.toString() + Path.NAMING_MODULE_SENSORNET + Path.RESOURCE_SENSORNET_SENSOR_BY_ID;
                if (match(request.getResource(), UNIQUE_SENSOR)) {
                    SensorSnTo sensor = request.getContent(SensorSnTo.class);
                    checkSensor(sensor);
                }
                //check network is down
                final String UNIQUE_NETWORK = Path.SEPARATOR.toString() + Path.NAMING_MODULE_SENSORNET + Path.RESOURCE_SENSORNET_NETWORK_BY_ID;
                if (match(request.getResource(), UNIQUE_NETWORK)) {
                    NetworkSnTo network = request.getContent(NetworkSnTo.class);
                    checkNetwork(network);
                }
            }
        } catch (AbstractRequestException e) {
            throw e;
        } catch (Exception e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
            System.out.println(e.getMessage());
        }
    }

    //checkVirtualSensor()
    public void checkVirtualSensor(VirtualSensorVsnTo virtualSensor) {
        switch (virtualSensor.getSensorType()) {
            case LINK:
                switch (virtualSensor.getState()) {
                    case NEW:
                    case REACTIVATED:
                    case UPDATED:
                        addLinkToBlending(virtualSensor);
                        break;
                    case INACTIVE:
                        removeLinkFromBlending(virtualSensor);
                        break;
                }
                break;
            case BLENDING:
                switch (virtualSensor.getState()) {
                    case NEW:
                    case REACTIVATED:
                    case UPDATED:
                        persistTmonAverageTable(virtualSensor);
                        break;
                }
                break;
        }
    }

    //checkSensor()
    public void checkSensor(SensorSnTo sensor) {
        //add to link only if it has a temperature value
        if (hasTemperatureValue(sensor)) {
            switch (sensor.getState()) {
                case UPDATED:
                case REACTIVATED:
                    if (existsInVirtualSensorNet(sensor)) {
                        break;
                    }
                case NEW:
                    createLink(sensor);
                    break;
            }
        }
        persistTmonSensorTable(sensor);
    }

    //checkNetwork()
    public void checkNetwork(NetworkSnTo network) {
        switch (network.getState()) {
            case INACTIVE:
                storage.storeAlert(ALERT_CRITICAL, System.currentTimeMillis(), "WSN is down!", EXCHANGE_MOTE_STATUS);
                break;
        }
    }

    private void persistTmonSensorTable(SensorSnTo sensor) {
        State state = sensor.getState();
        int id = Integer.parseInt(sensor.getId());
        switch (state) {
            case NEW:
            case REACTIVATED:
            case UPDATED:
                //insert new mote - begin
                Mote mote = storage.getMote(id);
                if (mote == null) {
                    storage.storeMote(sensor);
                    storage.storeAlert(ALERT_WARNING, sensor.getCaptureTimestampInMillis(), "Mote ID: " + id + "  joined, insert its location", EXCHANGE_MOTE_STATUS);
                    mote = storage.getMote(id);
                }
                //insert new mote - end
                if (!mote.getStatus() || state == State.REACTIVATED) {
                    //sensor voltou a rede
                    storage.updateMoteStatus(id, true);
                    storage.storeAlert(ALERT_WARNING, sensor.getCaptureTimestampInMillis(), "Mote ID: " + id + " back to the network", EXCHANGE_MOTE_STATUS);
                }
                if (calcBatteryLife(sensor) < 3) {
                    //bateria baixa
                    storage.storeAlert(ALERT_WARNING, sensor.getCaptureTimestampInMillis(), "Mote ID: " + id + " battery is low", EXCHANGE_MOTE_STATUS);
                }
                //amostra armazenada
                storage.storeSample(sensor);
                break;
            case INACTIVE:
                //sensor inativo
                storage.updateMoteStatus(id, false);
                storage.storeAlert(ALERT_WARNING, System.currentTimeMillis(), "Mote ID: " + id + " is down", EXCHANGE_MOTE_STATUS);
                break;
        }
        //persistir mote
        //atualizar mote
        //persistir sample
        //persistir alert
    }

    private int calcBatteryLife(SensorSnTo sensor) {
        double voltage = 0;
        for (ValueTo value : sensor.getValuesTo()) {
            switch (value.getName()) {
                case Config.SENSOR_VALUE_VOLTAGE:
                    voltage = Double.parseDouble(value.getValue());
                    break;
            }
        }
        return (int) ((voltage - 1.8) / 0.023);
    }

    private void persistTmonAverageTable(VirtualSensorVsnTo virtualSensor) {
        for (ValueVsnTo value : virtualSensor.getValuesTo()) {
            if (Config.BLENDING_FIELD_NAME.equals(value.getName())) {
                double temperature = Double.parseDouble(value.getValue());
                long timestamp = virtualSensor.getCreationTimestampInMillis();
                storage.storeAverageTemperature(temperature, timestamp);
                break;
            }
        }
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

        blending = creator.addVsensorToBlending(blending, virtualSensor, SENSOR_FIELD_NAME, FUNCTION_REQUEST_PARAM, FUNCTION_RESPONSE_PARAM);
        remote.omcpUpdateBlending(blending);
    }

    //TODO:bug - acertar parametros do blending
    private void removeLinkFromBlending(VirtualSensorVsnTo virtualSensor) {
        BlendingVsnTo[] blendings = remote.omcpGetBlendings();
        BlendingVsnTo blending;

        //check blending exists
        if (blendings.length == 0) {
            throw new RuntimeException("Not have blendings!");
        }
        blending = blendings[0];

        blending = creator.removeVsensorFromBlending(blending, virtualSensor, SENSOR_FIELD_NAME);

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

    private boolean existsInVirtualSensorNet(SensorSnTo sensor) {
        String sensorId = sensor.getId();
        String collectorId = sensor.getCollectorId();
        String networkId = sensor.getNetworkId();
        if (remote.omcpHasLink(networkId, collectorId, sensorId)) {
            return true;
        }
        return false;
    }

    private boolean hasTemperatureValue(SensorSnTo sensor) {
        for (ValueTo valuesTo : sensor.getValuesTo()) {
            if (SENSOR_FIELD_NAME.equals(valuesTo.getName())) {
                return true;
            }
        }
        return false;
    }
}
