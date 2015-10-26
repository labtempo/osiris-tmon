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

import br.uff.labtempo.osiris.to.common.data.FieldTo;
import br.uff.labtempo.osiris.to.common.definitions.State;
import br.uff.labtempo.osiris.to.common.definitions.ValueType;
import br.uff.labtempo.osiris.to.sensornet.SensorSnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.DataTypeVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.FunctionVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.LinkVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.ValueVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.VirtualSensorType;
import br.uff.labtempo.osiris.to.virtualsensornet.VirtualSensorVsnTo;
import br.uff.labtempo.tmon.tmonmanager.controller.util.VsnManager;
import br.uff.labtempo.tmon.tmonmanager.utils.DummyVsnManager;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class MainControllerTest {

    private SensorSnTo sensor;

    public MainControllerTest() {
        this.sensor = generateSensor();
    }

    @Test
    public void testNewSensor_ShouldPass() {
        VsnManager vsnManager = new DummyVsnManager();
        MainController mc = new MainController(vsnManager, 10);
        mc.checkSensor(sensor);
        assertEquals(sensor.getValuesTo().size(), vsnManager.omcpGetDataTypes().length);
        assertEquals(1, vsnManager.omcpGetLinks().length);

        LinkVsnTo link = vsnManager.omcpGetLinks()[0];
        assertEquals("sensor", link.getSensorId());
        assertEquals("collector", link.getCollectorId());
        assertEquals("network", link.getNetworkId());
    }

    @Test
    public void testNewVirtualSensor_ShouldPass() {
        VsnManager vsnManager = new DummyVsnManager();
        MainController mc = new MainController(vsnManager, 10);
        mc.checkSensor(sensor);
        LinkVsnTo link = vsnManager.omcpGetLinks()[0];
        VirtualSensorVsnTo vsensor = convertLinkToVirtualSensor(link, vsnManager.omcpGetDataTypes());
        mc.checkVirtualSensor(vsensor);

        assertEquals(1, vsnManager.omcpGetBlendings().length);
        assertEquals(1, vsnManager.omcpGetFunctions().length);
        assertEquals(2, vsnManager.omcpGetDataTypes().length);

        BlendingVsnTo blending = vsnManager.omcpGetBlendings()[0];
        assertEquals(1, blending.getRequestParams().size());

        FunctionVsnTo function = vsnManager.omcpGetFunctions()[0];
        assertEquals("omcp://average.function.osiris/", function.getAddress());

        assertEquals(function.getId(), blending.getFunctionId());
    }

    @Test
    public void testInactiveVirtualSensor_ShouldPass() {
        VsnManager vsnManager = new DummyVsnManager();
        MainController mc = new MainController(vsnManager, 10);
        mc.checkSensor(sensor);
        LinkVsnTo link = vsnManager.omcpGetLinks()[0];
        VirtualSensorVsnTo vsensor = convertLinkToVirtualSensor(link, vsnManager.omcpGetDataTypes());
        mc.checkVirtualSensor(vsensor);

        assertEquals(1, vsnManager.omcpGetBlendings().length);
        assertEquals(1, vsnManager.omcpGetFunctions().length);
        assertEquals(2, vsnManager.omcpGetDataTypes().length);

        BlendingVsnTo blending = vsnManager.omcpGetBlendings()[0];
        assertEquals(1, blending.getRequestParams().size());

        FunctionVsnTo function = vsnManager.omcpGetFunctions()[0];
        assertEquals("omcp://average.function.osiris/", function.getAddress());

        assertEquals(function.getId(), blending.getFunctionId());

        vsensor = disableVirtualSensor(vsensor);
        mc.checkVirtualSensor(vsensor);
        
        blending = vsnManager.omcpGetBlendings()[0];
        assertEquals(0, blending.getRequestParams().size());

        function = vsnManager.omcpGetFunctions()[0];
        assertEquals("omcp://average.function.osiris/", function.getAddress());

        assertEquals(0, blending.getFunctionId());
    }

    private SensorSnTo generateSensor() {
        String id = "sensor";
        State state = State.NEW;
        long captureTimestampInMillis = 10;
        int capturePrecisionInNano = 20;
        long acquisitionTimestampInMillis = 30;
        long storageTimestampInMillis = 40;
        Calendar lastModifiedDate = Calendar.getInstance();
        String networkId = "network";
        String collectorId = "collector";

        SensorSnTo sensor = new SensorSnTo(id, state, captureTimestampInMillis, capturePrecisionInNano, acquisitionTimestampInMillis, storageTimestampInMillis, lastModifiedDate, networkId, collectorId);

        final String temperature = "temperature";
        final String luminosity = "luminosity";
        sensor.addValue(temperature, ValueType.NUMBER, "30,5", "celsius", "C°");
        sensor.addValue(luminosity, ValueType.NUMBER, "30", "candela", "C");

        return sensor;
    }

    private VirtualSensorVsnTo convertLinkToVirtualSensor(LinkVsnTo link, DataTypeVsnTo[] datatypes) {
        VirtualSensorVsnTo vsensor = new VirtualSensorVsnTo(link.getId(), link.getLabel(), State.NEW, 0, 0, 0, TimeUnit.MINUTES, 0, 0, Calendar.getInstance(), VirtualSensorType.LINK);
        for (FieldTo field : link.getFields()) {
            for (DataTypeVsnTo datatype : datatypes) {
                if (field.getDataTypeId() == datatype.getId()) {
                    vsensor.addValue(field.getId(), field.getName(), datatype.getType(), "666", datatype.getUnit(), datatype.getSymbol());
                }
            }
        }

        return vsensor;
    }

    private VirtualSensorVsnTo disableVirtualSensor(VirtualSensorVsnTo vsensor) {
        VirtualSensorVsnTo vs = new VirtualSensorVsnTo(vsensor.getId(), vsensor.getLabel(), State.INACTIVE, 0, 0, 0, TimeUnit.MINUTES, 0, 0, vsensor.getLastModified(), vsensor.getSensorType());
        for (ValueVsnTo valuesTo : vsensor.getValuesTo()) {
            vs.addValue(valuesTo.getId(), valuesTo.getName(), valuesTo.getType(), valuesTo.getValue(), valuesTo.getUnit(), valuesTo.getSymbol());
        }
        return vs;
    }

}
