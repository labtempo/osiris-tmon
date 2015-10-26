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
package br.uff.labtempo.tmon.tmonavgfunction.controller;

import br.uff.labtempo.omcp.client.rabbitmq.RabbitClient;
import br.uff.labtempo.omcp.common.Request;
import br.uff.labtempo.omcp.common.Response;
import br.uff.labtempo.omcp.common.exceptions.BadRequestException;
import br.uff.labtempo.omcp.common.exceptions.InternalServerErrorException;
import br.uff.labtempo.omcp.common.exceptions.MethodNotAllowedException;
import br.uff.labtempo.omcp.common.exceptions.NotFoundException;
import br.uff.labtempo.omcp.common.exceptions.NotImplementedException;
import br.uff.labtempo.omcp.common.utils.ResponseBuilder;
import br.uff.labtempo.osiris.omcp.Controller;
import br.uff.labtempo.osiris.to.common.definitions.Path;
import br.uff.labtempo.osiris.to.function.InterfaceFnTo;
import br.uff.labtempo.osiris.to.function.ParameterizedRequestFn;
import br.uff.labtempo.osiris.to.function.RequestFnTo;
import br.uff.labtempo.osiris.to.function.ResponseFnTo;
import br.uff.labtempo.osiris.to.function.SingleValueFnTo;
import br.uff.labtempo.osiris.to.function.ValueFnTo;
import br.uff.labtempo.tmon.tmonavgfunction.controller.utils.FunctionInterfaceFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class MainController extends Controller {

    private final String REQUEST_PARAM = FunctionInterfaceFactory.REQUEST_PARAM;
    private final String RESPONSE_PARAM = FunctionInterfaceFactory.RESPONSE_PARAM;

    @Override
    public Response process(Request request) throws MethodNotAllowedException, NotFoundException, InternalServerErrorException, NotImplementedException, BadRequestException {
        try {
            return routing(request);
        } finally {
        }
    }

    public Response routing(Request request) throws MethodNotAllowedException, NotFoundException, InternalServerErrorException, NotImplementedException, BadRequestException {
        String contentType = request.getContentType();
        if (match(request.getResource(), Path.RESOURCE_FUNCTION_REQUEST.toString())) {
            Map<String, String> urlParams = super.extractParams(request.getResource(), Path.RESOURCE_FUNCTION_REQUEST.toString());
            //omcp://average.function/
            switch (request.getMethod()) {
                case GET:
                    if(!urlParams.containsKey(REQUEST_PARAM)){
                       throw new BadRequestException("Request param not found!");
                    }
                    
                    ParameterizedRequestFn prf = new ParameterizedRequestFn(urlParams);
                    RequestFnTo to = prf.getRequestFnTo();
                    ResponseFnTo responseFnTo = execute(to, request.getDate());
                    Response response = new ResponseBuilder().ok(responseFnTo, contentType).build();
                    return response;
                case POST:
                    //no spool resource
                default:
                    throw new MethodNotAllowedException("Action not allowed for this resource!");
            }
        } else if (match(request.getResource(), Path.RESOURCE_FUNCTION_SPOOL.toString())) {
            //omcp://average.function/spool/
            switch (request.getMethod()) {
                default:
                    throw new MethodNotAllowedException("Action not allowed for this resource!");
            }
        } else if (match(request.getResource(), Path.RESOURCE_FUNCTION_SPOOL_ITEM.toString())) {
            Map<String, String> map = extractParams(request.getResource(), Path.RESOURCE_FUNCTION_SPOOL_ITEM.toString());
            String urlId = map.get(Path.ID1.toString());
            //omcp://average.function/spool/{id}
            switch (request.getMethod()) {
                case DELETE:
                     //no spool resource                    
                default:
                    throw new MethodNotAllowedException("Action not allowed for this resource!");
            }
        } else if (match(request.getResource(), Path.RESOURCE_FUNCTION_INTERFACE.toString())) {
            //omcp://average.function/interface/
            switch (request.getMethod()) {
                case GET:
                    InterfaceFnTo to = getInterface();
                    Response response = new ResponseBuilder().ok(to, contentType).build();
                    return response;
                default:
                    throw new MethodNotAllowedException("Action not allowed for this resource!");
            }
        }
        return null;
    }

    public synchronized ResponseFnTo execute(RequestFnTo requestFnTo, Calendar calendar) throws NotFoundException, InternalServerErrorException, BadRequestException {
        List<Double> values = getValuesFromRequest(requestFnTo);
        Double total = calculateAverage(values);
        return createResponseFnTo(total, calendar);
    }

    private double calculateAverage(List<Double> values) {
        Double total = 0.0;
        for (Double value : values) {
            total += value;
        }
        return total / (double) values.size();
    }

    public InterfaceFnTo getInterface() {
        return FunctionInterfaceFactory.getInterface();
    }


    private ResponseFnTo createResponseFnTo(Double value, Calendar calendar) {
        List<ValueFnTo> list = new ArrayList<>();
        list.add(new SingleValueFnTo(RESPONSE_PARAM, String.valueOf(value)));
        ResponseFnTo responseFnTo = new ResponseFnTo(calendar.getTimeInMillis(), System.currentTimeMillis(), list);
        return responseFnTo;
    }

    private List<Double> getValuesFromRequest(RequestFnTo requestFnTo) throws BadRequestException {
        try {
            List<ValueFnTo> valueFnTos = requestFnTo.getValues();
            ValueFnTo valueFnTo = valueFnTos.get(0);
            List<String> stringValues = valueFnTo.getValues();
            List<Double> values = getDoubleValues(stringValues);
            return values;
        } catch (RuntimeException ex) {
            throw new BadRequestException("Params cannot be null!");
        }
    }

    private List<Double> getDoubleValues(List<String> values) {
        List<Double> list = new ArrayList<>();
        for (String value : values) {
            list.add(Double.valueOf(value.trim()));
        }
        return list;
    }
}
