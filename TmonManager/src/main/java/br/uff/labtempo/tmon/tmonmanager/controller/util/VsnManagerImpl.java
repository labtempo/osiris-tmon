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

import br.uff.labtempo.omcp.client.OmcpClient;
import br.uff.labtempo.omcp.common.Response;
import br.uff.labtempo.omcp.common.StatusCode;
import br.uff.labtempo.osiris.to.function.InterfaceFnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.DataTypeVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.FunctionVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.LinkVsnTo;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class VsnManagerImpl implements VsnManager {

    private OmcpClient client;

    public VsnManagerImpl(OmcpClient client) {
        this.client = client;
    }

    @Override
    public BlendingVsnTo omcpCreateBlending(BlendingVsnTo blending) {
        Response r = client.doPost("omcp://virtualsensornet/blending/", blending);
        if (r.getStatusCode() == StatusCode.CREATED) {
            r = client.doGet(r.getLocation());
            blending = r.getContent(BlendingVsnTo.class);
            return blending;
        } else {
            throw new RuntimeException(r.getStatusCode().toString() + ":" + r.getErrorMessage());
        }
    }

    @Override
    public LinkVsnTo omcpCreateLink(LinkVsnTo link) {
        Response r = client.doPost("omcp://virtualsensornet/link/", link);
        if (r.getStatusCode() == StatusCode.CREATED) {
            r = client.doGet(r.getLocation());
            link = r.getContent(LinkVsnTo.class);
            return link;
        } else {
            throw new RuntimeException(r.getStatusCode().toString() + ":" + r.getErrorMessage());
        }
    }

    @Override
    public DataTypeVsnTo omcpCreateDataType(DataTypeVsnTo dataType) {
        Response r = client.doPost("omcp://virtualsensornet/datatype/", dataType);
        if (r.getStatusCode() == StatusCode.CREATED) {
            r = client.doGet(r.getLocation());
            dataType = r.getContent(DataTypeVsnTo.class);
            return dataType;
        } else {
            throw new RuntimeException(r.getStatusCode().toString() + ":" + r.getErrorMessage());
        }
    }

    @Override
    public FunctionVsnTo omcpCreateFunction(FunctionVsnTo function) {
        Response r = client.doPost("omcp://virtualsensornet/function/", function);
        if (r.getStatusCode() == StatusCode.CREATED) {
            r = client.doGet(r.getLocation());
            function = r.getContent(FunctionVsnTo.class);
            return function;
        } else {
            throw new RuntimeException(r.getStatusCode().toString() + ":" + r.getErrorMessage());
        }
    }

    @Override
    public BlendingVsnTo[] omcpGetBlendings() {
        Response r = client.doGet("omcp://virtualsensornet/blending/");
        if (r.getStatusCode() == StatusCode.OK) {
            BlendingVsnTo[] blendings = r.getContent(BlendingVsnTo[].class);
            return blendings;
        } else {
            throw new RuntimeException(r.getStatusCode().toString());
        }
    }

    @Override
    public DataTypeVsnTo[] omcpGetDataTypes() {
        Response r = client.doGet("omcp://virtualsensornet/datatype/");
        if (r.getStatusCode() == StatusCode.OK) {
            DataTypeVsnTo[] dataTypes = r.getContent(DataTypeVsnTo[].class);
            return dataTypes;
        } else {
            throw new RuntimeException(r.getStatusCode().toString());
        }
    }

    @Override
    public FunctionVsnTo[] omcpGetFunctions() {
        Response r = client.doGet("omcp://virtualsensornet/function/");
        if (r.getStatusCode() == StatusCode.OK) {
            FunctionVsnTo[] functions = r.getContent(FunctionVsnTo[].class);
            return functions;
        } else {
            throw new RuntimeException(r.getStatusCode().toString());
        }
    }

    @Override
    public LinkVsnTo[] omcpGetLinks() {
        Response r = client.doGet("omcp://virtualsensornet/link/");
        if (r.getStatusCode() == StatusCode.OK) {
            LinkVsnTo[] links = r.getContent(LinkVsnTo[].class);
            return links;
        } else {
            throw new RuntimeException(r.getStatusCode().toString());
        }
    }

    @Override
    public boolean omcpUpdateBlending(BlendingVsnTo blending) {
        String address = "omcp://virtualsensornet/blending/" + blending.getId() + "/";
        Response r = client.doPut(address, blending);
        if (r.getStatusCode() == StatusCode.OK) {
            return true;
        } else {
            throw new RuntimeException(r.getStatusCode().toString() + ":" + r.getErrorMessage());
        }
    }

    @Override
    public FunctionVsnTo omcpCreateFunctionFrom(String address) {
        String functionResource = address + "interface/";
        Response r = client.doGet(functionResource);
        if (r.getStatusCode() != StatusCode.OK) {
            throw new RuntimeException(r.getStatusCode().toString() + ":" + r.getErrorMessage());
        }

        InterfaceFnTo interfaceFnTo = r.getContent(InterfaceFnTo.class);
        FunctionVsnTo functionVsnTo = new FunctionVsnTo(interfaceFnTo);

        functionResource = "omcp://virtualsensornet/function/";
        r = client.doPost(functionResource, functionVsnTo);
        if (r.getStatusCode() == StatusCode.CREATED) {
            r = client.doGet(r.getLocation());
            functionVsnTo = r.getContent(FunctionVsnTo.class);
            return functionVsnTo;
        } else {
            throw new RuntimeException(r.getStatusCode().toString() + ":" + r.getErrorMessage());
        }
    }

    @Override
    public boolean omcpHasLink(String network, String collector, String sensor) {
        Response r = client.doGet("omcp://virtualsensornet/link/?sensor="+sensor+"&collector="+collector+"&network="+network+"");
        if (r.getStatusCode() == StatusCode.OK) {
            LinkVsnTo[] links = r.getContent(LinkVsnTo[].class);
            if(links.length > 0){
                return true;
            }
            return false;
        } else {
            throw new RuntimeException(r.getStatusCode().toString());
        }
    }

}
