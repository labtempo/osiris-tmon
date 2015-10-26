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

import br.uff.labtempo.osiris.to.common.data.FieldTo;
import br.uff.labtempo.osiris.to.common.definitions.FunctionOperation;
import br.uff.labtempo.osiris.to.common.definitions.State;
import br.uff.labtempo.osiris.to.common.definitions.ValueType;
import br.uff.labtempo.osiris.to.function.ParamFnTo;
import br.uff.labtempo.osiris.to.sensornet.SensorSnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingBondVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.DataTypeVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.FunctionVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.LinkVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.VirtualSensorType;
import br.uff.labtempo.osiris.to.virtualsensornet.VirtualSensorVsnTo;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class ToHandlerImplTest {

    private ToHandler toHandler;

    public ToHandlerImplTest() {
        this.toHandler = new ToHandlerImpl();
    }

    @Test
    public void testDataTypeGenerator_ShouldPass() {
        final String temperature = "temperature";
        final String luminosity = "luminosity";

        SensorSnTo sensor = new SensorSnTo("1", State.NEW, 0, 0, 0, 0, Calendar.getInstance(), null, null);
        sensor.addValue(temperature, ValueType.NUMBER, "30,5", "celsius", "C°");
        sensor.addValue(luminosity, ValueType.NUMBER, "30", "candela", "C");

        List<DataTypeVsnTo> dataTypes = toHandler.generateDataType(sensor);

        for (DataTypeVsnTo dataType : dataTypes) {
            switch (dataType.getDisplayName()) {
                case temperature:
                    assertEquals(ValueType.NUMBER, dataType.getType());
                    assertEquals("celsius", dataType.getUnit());
                    assertEquals("C°", dataType.getSymbol());
                    break;
                case luminosity:
                    assertEquals(ValueType.NUMBER, dataType.getType());
                    assertEquals("candela", dataType.getUnit());
                    assertEquals("C", dataType.getSymbol());
                    break;
                default:
                    assertFalse(true);
            }
        }
    }

    @Test
    public void testLinkGenerator_ShouldPass() {
        final String temperature = "temperature";
        final String luminosity = "luminosity";

        SensorSnTo sensor = new SensorSnTo("sensor", State.NEW, 0, 0, 0, 0, Calendar.getInstance(), "network", "collector");
        sensor.addValue(temperature, ValueType.NUMBER, "30,5", "celsius", "C°");
        sensor.addValue(luminosity, ValueType.NUMBER, "30", "candela", "C");

        List<DataTypeVsnTo> dataTypes = toHandler.generateDataType(sensor);

        LinkVsnTo link = toHandler.generateLink(dataTypes, sensor);

        assertEquals("network", link.getNetworkId());
        assertEquals("collector", link.getCollectorId());
        assertEquals("sensor", link.getSensorId());
        assertEquals(2, link.getFields().size());

        for (FieldTo field : link.getFields()) {
            switch (field.getName()) {
                case temperature:
                    assertEquals(field.getDataTypeId(), 0);
                    break;
                case luminosity:
                    assertEquals(field.getDataTypeId(), 0);
                    break;
                default:
                    assertFalse(true);
            }
        }
    }

    @Test
    public void testBlendingGenerator_ShouldPass() {
        final String temperature = "temperature";
        DataTypeVsnTo dataType = new DataTypeVsnTo(temperature, ValueType.TEXT, "celsius", "C°");

        BlendingVsnTo blending = toHandler.generateBlending(dataType, temperature);

        for (FieldTo field : blending.getFields()) {
            switch (field.getName()) {
                case temperature:
                    assertEquals(field.getDataTypeId(), 0);
                    break;
                default:
                    assertFalse(true);
            }
        }
    }

    @Test
    public void testAddVsensorToBlending_ShouldPass() {
        final String temperature = "temperature";
        final String luminosity = "luminosity";
        final String average = "average";

        SensorSnTo sensor = new SensorSnTo("sensor", State.NEW, 0, 0, 0, 0, Calendar.getInstance(), "network", "collector");
        sensor.addValue(temperature, ValueType.NUMBER, "30,5", "celsius", "C°");
        sensor.addValue(luminosity, ValueType.NUMBER, "30", "candela", "C");
        List<DataTypeVsnTo> dataTypes = toHandler.generateDataType(sensor);
        LinkVsnTo link = toHandler.generateLink(dataTypes, sensor);

        VirtualSensorVsnTo virtualSensor = new VirtualSensorVsnTo(1, "", State.NEW, 0, 0, 0, TimeUnit.MINUTES, 0, 0, Calendar.getInstance(), VirtualSensorType.LINK);
        for (FieldTo field : link.getFields()) {
            DataTypeVsnTo dt = null;
            for (DataTypeVsnTo dataType : dataTypes) {
                if (dataType.getDisplayName().equals(field.getName())) {
                    dt = dataType;
                }
            }
            virtualSensor.addValue(field.getId(), field.getName(), dt.getType(), "", dt.getUnit(), dt.getSymbol());
        }

        DataTypeVsnTo dataType = null;
        for (DataTypeVsnTo dt : dataTypes) {
            if (dt.getDisplayName().equals(temperature)) {
                dataType = dt;
                break;
            }
        }

        BlendingVsnTo blending = toHandler.generateBlending(dataType, temperature);

        blending = toHandler.addVsensorToBlending(blending, virtualSensor, temperature, temperature, average);

        assertEquals(1, blending.getRequestParams().size());

        for (BlendingBondVsnTo bond : blending.getRequestParams()) {
            switch (bond.getParamName()) {
                case temperature:
                    assertTrue(true);
                    break;
                default:
                    assertFalse(true);
            }
        }

        for (BlendingBondVsnTo bond : blending.getResponseParams()) {
            switch (bond.getParamName()) {
                case average:
                    assertTrue(true);
                    break;
                default:
                    assertFalse(true);
            }
        }

    }

    @Test
    public void testRemoveVsensorFromBlending_ShouldPass() {
        final String temperature = "temperature";
        final String luminosity = "luminosity";
        final String average = "average";

        SensorSnTo sensor = new SensorSnTo("sensor", State.NEW, 0, 0, 0, 0, Calendar.getInstance(), "network", "collector");
        sensor.addValue(temperature, ValueType.NUMBER, "30,5", "celsius", "C°");
        sensor.addValue(luminosity, ValueType.NUMBER, "30", "candela", "C");
        List<DataTypeVsnTo> dataTypes = toHandler.generateDataType(sensor);
        LinkVsnTo link = toHandler.generateLink(dataTypes, sensor);

        VirtualSensorVsnTo virtualSensor = new VirtualSensorVsnTo(1, "", State.NEW, 0, 0, 0, TimeUnit.MINUTES, 0, 0, Calendar.getInstance(), VirtualSensorType.LINK);
        for (FieldTo field : link.getFields()) {
            DataTypeVsnTo dt = null;
            for (DataTypeVsnTo dataType : dataTypes) {
                if (dataType.getDisplayName().equals(field.getName())) {
                    dt = dataType;
                }
            }
            virtualSensor.addValue(field.getId(), field.getName(), dt.getType(), "", dt.getUnit(), dt.getSymbol());
        }

        DataTypeVsnTo dataType = null;
        for (DataTypeVsnTo dt : dataTypes) {
            if (dt.getDisplayName().equals(temperature)) {
                dataType = dt;
                break;
            }
        }

        BlendingVsnTo blending = toHandler.generateBlending(dataType, temperature);

        blending = toHandler.addVsensorToBlending(blending, virtualSensor, temperature, temperature, average);

        assertEquals(1, blending.getRequestParams().size());

        for (BlendingBondVsnTo bond : blending.getRequestParams()) {
            switch (bond.getParamName()) {
                case temperature:
                    assertTrue(true);
                    break;
                default:
                    assertFalse(true);
            }
        }

        for (BlendingBondVsnTo bond : blending.getResponseParams()) {
            switch (bond.getParamName()) {
                case average:
                    assertTrue(true);
                    break;
                default:
                    assertFalse(true);
            }
        }

        blending = toHandler.removeVsensorFromBlending(blending, virtualSensor, temperature);

        for (BlendingBondVsnTo bond : blending.getRequestParams()) {
            switch (bond.getParamName()) {
                case temperature:
                    assertFalse(true);
                    break;
                default:
                    assertTrue(true);
            }
        }
    }

    @Test
    public void testAddFunctionToBlending_ShouldPass() {
        final String temperature = "temperature";
        final String luminosity = "luminosity";
        final String average = "average";

        SensorSnTo sensor = new SensorSnTo("sensor", State.NEW, 0, 0, 0, 0, Calendar.getInstance(), "network", "collector");
        sensor.addValue(temperature, ValueType.NUMBER, "30,5", "celsius", "C°");
        sensor.addValue(luminosity, ValueType.NUMBER, "30", "candela", "C");
        List<DataTypeVsnTo> dataTypes = toHandler.generateDataType(sensor);
        LinkVsnTo link = toHandler.generateLink(dataTypes, sensor);

        VirtualSensorVsnTo virtualSensor = new VirtualSensorVsnTo(1, "", State.NEW, 0, 0, 0, TimeUnit.MINUTES, 0, 0, Calendar.getInstance(), VirtualSensorType.LINK);
        for (FieldTo field : link.getFields()) {
            DataTypeVsnTo dt = null;
            for (DataTypeVsnTo dataType : dataTypes) {
                if (dataType.getDisplayName().equals(field.getName())) {
                    dt = dataType;
                }
            }
            virtualSensor.addValue(field.getId(), field.getName(), dt.getType(), "", dt.getUnit(), dt.getSymbol());
        }

        DataTypeVsnTo dataType = null;
        for (DataTypeVsnTo dt : dataTypes) {
            if (dt.getDisplayName().equals(temperature)) {
                dataType = dt;
                break;
            }
        }

        BlendingVsnTo blending = toHandler.generateBlending(dataType, temperature);

        List<FunctionOperation> operations = new ArrayList<>();
        List<ParamFnTo> requestParams = new ArrayList<>();
        List<ParamFnTo> responseParams = new ArrayList<>();
        
        operations.add(FunctionOperation.SYNCHRONOUS);
        
        FunctionVsnTo function = new FunctionVsnTo(0, "", "", "", operations, requestParams, responseParams);
        
        
        blending = toHandler.addFunctionToBlending(blending, function, 5);

        assertEquals(0, blending.getFunctionId());
        assertEquals(5, blending.getCallIntervalInMillis());

    }
    
    //removeFunctionFromBlending
    @Test
    public void testRemoveFunctionFromBlending_ShouldPass() {
        final String temperature = "temperature";
        final String luminosity = "luminosity";
        final String average = "average";

        SensorSnTo sensor = new SensorSnTo("sensor", State.NEW, 0, 0, 0, 0, Calendar.getInstance(), "network", "collector");
        sensor.addValue(temperature, ValueType.NUMBER, "30,5", "celsius", "C°");
        sensor.addValue(luminosity, ValueType.NUMBER, "30", "candela", "C");
        List<DataTypeVsnTo> dataTypes = toHandler.generateDataType(sensor);
        LinkVsnTo link = toHandler.generateLink(dataTypes, sensor);

        VirtualSensorVsnTo virtualSensor = new VirtualSensorVsnTo(1, "", State.NEW, 0, 0, 0, TimeUnit.MINUTES, 0, 0, Calendar.getInstance(), VirtualSensorType.LINK);
        for (FieldTo field : link.getFields()) {
            DataTypeVsnTo dt = null;
            for (DataTypeVsnTo dataType : dataTypes) {
                if (dataType.getDisplayName().equals(field.getName())) {
                    dt = dataType;
                }
            }
            virtualSensor.addValue(field.getId(), field.getName(), dt.getType(), "", dt.getUnit(), dt.getSymbol());
        }

        DataTypeVsnTo dataType = null;
        for (DataTypeVsnTo dt : dataTypes) {
            if (dt.getDisplayName().equals(temperature)) {
                dataType = dt;
                break;
            }
        }

        BlendingVsnTo blending = toHandler.generateBlending(dataType, temperature);

        List<FunctionOperation> operations = new ArrayList<>();
        List<ParamFnTo> requestParams = new ArrayList<>();
        List<ParamFnTo> responseParams = new ArrayList<>();
        
        operations.add(FunctionOperation.SYNCHRONOUS);
        
        FunctionVsnTo function = new FunctionVsnTo(10, "", "", "", operations, requestParams, responseParams);
        
        
        blending = toHandler.addFunctionToBlending(blending, function, 5);

        assertEquals(10, blending.getFunctionId());
        assertEquals(5, blending.getCallIntervalInMillis());
        
        blending = toHandler.removeFunctionFromBlending(blending);

        assertEquals(0, blending.getFunctionId());

    }
}
