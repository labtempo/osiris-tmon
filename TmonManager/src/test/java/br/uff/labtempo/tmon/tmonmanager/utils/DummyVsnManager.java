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
package br.uff.labtempo.tmon.tmonmanager.utils;

import br.uff.labtempo.omcp.common.utils.Serializer;
import br.uff.labtempo.osiris.to.common.data.FieldTo;
import br.uff.labtempo.osiris.to.common.definitions.FunctionOperation;
import br.uff.labtempo.osiris.to.common.definitions.ValueType;
import br.uff.labtempo.osiris.to.function.ParamFnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingBondVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.DataTypeVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.FunctionVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.LinkVsnTo;
import br.uff.labtempo.tmon.tmonmanager.controller.util.VsnManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class DummyVsnManager implements VsnManager {

    private long idCount = 7000;
    private Map<Class<?>, List<? extends Object>> storage;

    public DummyVsnManager() {
        this.storage = new HashMap<>();
    }

    @Override
    public BlendingVsnTo omcpCreateBlending(BlendingVsnTo blending) {
        BlendingVsnTo b = new BlendingVsnTo(id(), blending.getLabel());
        for (FieldTo field : blending.getFields()) {
            b.createField(id(), field.getName(), field.getDataTypeId());
        }
        save(b.getClass(), b);
        return b;
    }

    @Override
    public DataTypeVsnTo omcpCreateDataType(DataTypeVsnTo dataType) {
        DataTypeVsnTo dt = new DataTypeVsnTo(id(), dataType.getDisplayName(), dataType.getType(), dataType.getUnit(), dataType.getSymbol());
        save(dt.getClass(), dt);
        return dt;
    }

    @Override
    public FunctionVsnTo omcpCreateFunction(FunctionVsnTo function) {
        FunctionVsnTo fn = new FunctionVsnTo(id(), function.getName(), function.getDescription(), function.getDescription(), function.getOperations(), function.getRequestParams(), function.getResponseParams());
        save(fn.getClass(), fn);
        return fn;
    }

    @Override
    public FunctionVsnTo omcpCreateFunctionFrom(String address) {
        String NAME = "avg";
        String DESRIPTION = "average function";
        String REQUEST_PARAM = "temperatures";
        String RESPONSE_PARAM = "average";
        String ADDRESS = "omcp://average.function.osiris/";

        List<FunctionOperation> operations = new ArrayList<>();
        operations.add(FunctionOperation.SYNCHRONOUS);

        List<ParamFnTo> requestParamFnTos = new ArrayList<>();
        requestParamFnTos.add(new ParamFnTo(REQUEST_PARAM, ValueType.NUMBER, true));

        List<ParamFnTo> responseParamFnTos = new ArrayList<>();
        responseParamFnTos.add(new ParamFnTo(RESPONSE_PARAM, ValueType.NUMBER));

        FunctionVsnTo fn = new FunctionVsnTo(id(), NAME, DESRIPTION, ADDRESS, operations, requestParamFnTos, responseParamFnTos);
        save(fn.getClass(), fn);
        return fn;
    }

    @Override
    public LinkVsnTo omcpCreateLink(LinkVsnTo link) {
        LinkVsnTo l = new LinkVsnTo(id(), link.getLabel(), link.getSensorId(), link.getCollectorId(), link.getNetworkId());
        for (FieldTo field : link.getFields()) {
            l.createField(id(), field.getName(), field.getDataTypeId());
        }
        save(l.getClass(), l);
        return l;
    }

    @Override
    public BlendingVsnTo[] omcpGetBlendings() {
        List<BlendingVsnTo> list = getAll(BlendingVsnTo.class);
        BlendingVsnTo[] array = list.toArray(new BlendingVsnTo[list.size()]);
        return array;
    }

    @Override
    public DataTypeVsnTo[] omcpGetDataTypes() {
        List<DataTypeVsnTo> list = getAll(DataTypeVsnTo.class);
        DataTypeVsnTo[] array = list.toArray(new DataTypeVsnTo[list.size()]);
        return array;
    }

    @Override
    public FunctionVsnTo[] omcpGetFunctions() {
        List<FunctionVsnTo> list = getAll(FunctionVsnTo.class);
        FunctionVsnTo[] array = list.toArray(new FunctionVsnTo[list.size()]);
        return array;
    }

    @Override
    public LinkVsnTo[] omcpGetLinks() {
        List<LinkVsnTo> list = getAll(LinkVsnTo.class);
        LinkVsnTo[] array = list.toArray(new LinkVsnTo[list.size()]);
        return array;
    }

    @Override
    public boolean omcpUpdateBlending(BlendingVsnTo blending) {
        BlendingVsnTo b = getById(BlendingVsnTo.class, blending.getId());
        if (b != null) {
            b.setFunction(blending.getFunctionId());
            b.setCallIntervalInMillis(blending.getCallIntervalInMillis());
            b.setCallMode(blending.getCallMode());
            b.setLabel(blending.getLabel());
            updateBlendingRequestParams(blending, b);
            return true;
        }
        return false;
    }

    private long id() {
        return idCount++;
    }

    private <T> void save(Class<?> klass, T object) {
        List<T> list;
        if (storage.containsKey(klass)) {
            list = (List<T>) storage.get(klass);
        } else {
            list = new ArrayList<>();
            storage.put(klass, list);
        }
        Serializer s = new Serializer();
        String json = s.toJson(object);
        object = s.fromJson(json, klass);
        list.add(object);
    }

    private <T> List<T> getAll(Class<?> klass) {
        if (storage.containsKey(klass)) {
            return (List<T>) storage.get(klass);
        }
        return new ArrayList<>();
    }

    private <T> T getById(Class<?> klass, long id) {
        if (storage.containsKey(klass)) {
            List<T> list = (List<T>) storage.get(klass);
            for (T item : list) {
                if (klass.equals(BlendingVsnTo.class)) {
                    BlendingVsnTo obj = (BlendingVsnTo) item;
                    if (obj.getId() == id) {
                        return item;
                    }
                }
                if (klass.equals(DataTypeVsnTo.class)) {
                    DataTypeVsnTo obj = (DataTypeVsnTo) item;
                    if (obj.getId() == id) {
                        return item;
                    }
                }
                if (klass.equals(LinkVsnTo.class)) {
                    LinkVsnTo obj = (LinkVsnTo) item;
                    if (obj.getId() == id) {
                        return item;
                    }
                }
                if (klass.equals(FunctionVsnTo.class)) {
                    FunctionVsnTo obj = (FunctionVsnTo) item;
                    if (obj.getId() == id) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    private void updateBlendingRequestParams(BlendingVsnTo blending, BlendingVsnTo b) {
        List<BlendingBondVsnTo> current = b.getRequestParams();
        List<BlendingBondVsnTo> newest = blending.getRequestParams();

        List<BlendingBondVsnTo> currIntersection = new ArrayList<>();
        List<BlendingBondVsnTo> neweIntersection = new ArrayList<>();
        /**
         * EQUALS
         */
        for (BlendingBondVsnTo curr : current) {
            for (BlendingBondVsnTo newe : newest) {
                if (curr.getFieldId() == newe.getFieldId()) {
                    currIntersection.add(curr);
                    neweIntersection.add(newe);
                    break;
                }
            }
        }

        /**
         * TO REMOVE
         */
        List<BlendingBondVsnTo> toRemove = new ArrayList<>(current);
        toRemove.removeAll(currIntersection);

        /**
         * TO ADD
         */
        List<BlendingBondVsnTo> toInsert = new ArrayList<>(newest);
        toInsert.removeAll(neweIntersection);

        for (BlendingBondVsnTo bond : toRemove) {
            b.removeRequestParam(bond.getFieldId());
        }

        for (BlendingBondVsnTo bond : toInsert) {
            b.addRequestParam(bond.getFieldId(), bond.getParamName());
        }
    }
}
