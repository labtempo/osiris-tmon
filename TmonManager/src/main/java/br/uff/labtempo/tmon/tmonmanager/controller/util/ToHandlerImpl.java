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
import br.uff.labtempo.osiris.to.common.data.ValueTo;
import br.uff.labtempo.osiris.to.common.definitions.FunctionOperation;
import br.uff.labtempo.osiris.to.common.definitions.ValueType;
import br.uff.labtempo.osiris.to.sensornet.SensorSnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingBondVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.DataTypeVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.FunctionVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.LinkVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.ValueVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.VirtualSensorVsnTo;
import java.util.ArrayList;
import java.util.List;

public class ToHandlerImpl implements ToHandler {

    @Override
    public List<DataTypeVsnTo> generateDataType(SensorSnTo sensor) {
        List<? extends ValueTo> values = sensor.getValuesTo();
        List<DataTypeVsnTo> datatypes = new ArrayList<>();
        for (ValueTo value : values) {
            String name = value.getName();
            ValueType type = value.getType();
            String symbol = value.getSymbol();
            String unit = value.getUnit();
            DataTypeVsnTo to = new DataTypeVsnTo(name, type, unit, symbol);
            datatypes.add(to);
        }

        return datatypes;
    }

    @Override
    public LinkVsnTo generateLink(DataTypeVsnTo dataType, SensorSnTo sensor) {
        List<DataTypeVsnTo> dataTypes = new ArrayList<>();
        dataTypes.add(dataType);
        return generateLink(dataTypes, sensor);
    }

    @Override
    public LinkVsnTo generateLink(List<DataTypeVsnTo> dataTypes, SensorSnTo sensor) {
        LinkVsnTo link = new LinkVsnTo(sensor.getId(), sensor.getCollectorId(), sensor.getNetworkId());
        for (DataTypeVsnTo dataType : dataTypes) {
            link.createField(dataType.getDisplayName(), dataType.getId());
        }
        return link;
    }

    @Override
    public BlendingVsnTo generateBlending(DataTypeVsnTo dataType, String fieldName) {
        BlendingVsnTo blending = new BlendingVsnTo();
        blending.createField(fieldName, dataType.getId());
        return blending;
    }

    @Override
    public BlendingVsnTo addVsensorToBlending(BlendingVsnTo blending, VirtualSensorVsnTo virtualSensor, String fieldName, String requestParamName, String responseParamName) {
        //add vsensor
        List<ValueVsnTo> values = virtualSensor.getValuesTo();
        for (ValueVsnTo value : values) {
            if (value.getName().equals(fieldName)) {
                blending.addRequestParam(value.getId(), requestParamName);
                break;
            }
        }

        //set responseParam
        List<BlendingBondVsnTo> responseParams = blending.getResponseParams();
        if (responseParams == null || responseParams.isEmpty()) {
            FieldTo field = blending.getFields().get(0);
            blending.addResponseParam(field.getId(), responseParamName);
        }
        return blending;
    }

    @Override
    public BlendingVsnTo removeVsensorFromBlending(BlendingVsnTo blending, VirtualSensorVsnTo virtualSensor, String fieldName) {
        long fieldId = -1;

        for (ValueVsnTo valuesTo : virtualSensor.getValuesTo()) {
            if (valuesTo.getName().equals(fieldName)) {
                fieldId = valuesTo.getId();
                break;
            }
        }

        if (fieldId == -1) {
            throw new RuntimeException("Not have field in blending!");
        }

        List<BlendingBondVsnTo> bondsToRemove = new ArrayList<>();
        List<BlendingBondVsnTo> blendingBonds = blending.getRequestParams();

        for (BlendingBondVsnTo blendingBond : blendingBonds) {
            if (blendingBond.getFieldId() == fieldId) {
                bondsToRemove.add(blendingBond);
            }
        }

        for (BlendingBondVsnTo bond : bondsToRemove) {
            blending.removeRequestParam(bond.getFieldId());
        }

        return blending;
    }

    @Override
    public BlendingVsnTo addFunctionToBlending(BlendingVsnTo blending, FunctionVsnTo function, long callInterval) {
        blending.setFunction(function);
        blending.setCallMode(FunctionOperation.SYNCHRONOUS);
        blending.setCallIntervalInMillis(callInterval);
        return blending;
    }

    @Override
    public BlendingVsnTo removeFunctionFromBlending(BlendingVsnTo blending) {
        blending.setFunction(0);
        return blending;
    }

}
