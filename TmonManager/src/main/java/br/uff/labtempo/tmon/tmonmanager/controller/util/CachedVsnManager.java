/*
 * Copyright 2015 Felipe Santos <live.proto at hotmail.com>.
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
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingBondVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.DataTypeVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.FunctionVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.LinkVsnTo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Felipe Santos <live.proto at hotmail.com>
 */
public class CachedVsnManager implements VsnManager {

    private BlendingVsnTo[] blendings;
    private Map<String, Long> linkMap;
    private VsnManager manager;
    private long expirationTimeInSeconds;

    public CachedVsnManager(VsnManager manager, long expirationTimeInSeconds) {
        this.manager = manager;
        this.expirationTimeInSeconds = expirationTimeInSeconds;
        this.linkMap = new HashMap<>();
    }

    @Override
    public BlendingVsnTo omcpCreateBlending(BlendingVsnTo blending) {
        blendings = null;
        return manager.omcpCreateBlending(blending);
    }

    @Override
    public DataTypeVsnTo omcpCreateDataType(DataTypeVsnTo dataType) {
        return manager.omcpCreateDataType(dataType);
    }

    @Override
    public FunctionVsnTo omcpCreateFunction(FunctionVsnTo function) {
        return manager.omcpCreateFunction(function);
    }

    @Override
    public FunctionVsnTo omcpCreateFunctionFrom(String address) {
        return manager.omcpCreateFunctionFrom(address);
    }

    @Override
    public LinkVsnTo omcpCreateLink(LinkVsnTo link) {
        checkRecords();
        return manager.omcpCreateLink(link);
    }

    @Override
    public BlendingVsnTo[] omcpGetBlendings() {
        if (blendings != null) {
            return blendings;
        } else {
            BlendingVsnTo[] bs = manager.omcpGetBlendings();
            if (bs != null && bs.length > 0) {
                blendings = new BlendingVsnTo[bs.length];
                for (int i = 0; i < blendings.length; i++) {
                    blendings[i] = copy(bs[i]);
                }
            }
            return bs;
        }
    }

    @Override
    public DataTypeVsnTo[] omcpGetDataTypes() {
        return manager.omcpGetDataTypes();
    }

    @Override
    public FunctionVsnTo[] omcpGetFunctions() {
        return manager.omcpGetFunctions();
    }

    @Override
    public LinkVsnTo[] omcpGetLinks() {
        LinkVsnTo[] newLinks = manager.omcpGetLinks();
        refreshLinkMap(newLinks);
        return newLinks;
    }

    @Override
    public boolean omcpHasLink(String network, String collector, String sensor) {
        if (checkLinkOnCache(network, collector, sensor)) {
            return true;
        } else {
            if (manager.omcpHasLink(network, collector, sensor)) {
                linkMap.put(network + collector + sensor, System.currentTimeMillis());
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean omcpUpdateBlending(BlendingVsnTo blending) {
        if (blendings != null && blendings.length > 0) {
            BlendingVsnTo b = blendings[0];
            if (BlendingVsnToComparator.equals(blending, b)) {
                return false;
            }
        }
        blendings = null;
        return manager.omcpUpdateBlending(blending);
    }

    private boolean checkLinkOnCache(String network, String collector, String sensor) {
        checkRecords();
        String key = network + collector + sensor;
        return linkMap.containsKey(key);
    }

    private void checkRecords() {
        List<String> keysToRemove = new ArrayList<>();
        for (Map.Entry<String, Long> entrySet : linkMap.entrySet()) {
            String key = entrySet.getKey();
            Long time = entrySet.getValue();
            long now = System.currentTimeMillis();
            long difference = now - time;
            if (difference > (1000 * expirationTimeInSeconds)) {
                keysToRemove.add(key);
            }
        }
        for (String key : keysToRemove) {
            linkMap.remove(key);
        }
    }

    private void refreshLinkMap(LinkVsnTo[] newLinks) {
        linkMap.clear();
        for (LinkVsnTo newLink : newLinks) {
            String key = newLink.getNetworkId() + newLink.getCollectorId() + newLink.getSensorId();
            linkMap.put(key, System.currentTimeMillis());
        }
    }

    private BlendingVsnTo copy(BlendingVsnTo blending) {
        BlendingVsnTo copy = new BlendingVsnTo(blending.getId(), blending.getLabel());
        for (FieldTo field : blending.getFields()) {
            copy.createField(field.getId(), field.getName(), field.getDataTypeId(), field.getConverterId(), field.isInitialized(), field.getSourceId(), field.getAggregates(), field.getDependents());
        }

        if (blending.getFunctionId() != 0) {
            copy.setCallMode(blending.getCallMode());
            copy.setFunction(blending.getFunctionId());
            copy.setCallIntervalInMillis(blending.getCallIntervalInMillis());
            for (BlendingBondVsnTo requestParam : blending.getRequestParams()) {
                copy.addRequestParam(requestParam.getFieldId(), requestParam.getParamName());
            }

            for (BlendingBondVsnTo responseParam : blending.getResponseParams()) {
                copy.addResponseParam(responseParam.getFieldId(), responseParam.getParamName());
            }
        }
        return copy;
    }

}
