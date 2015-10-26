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

import br.uff.labtempo.omcp.common.Request;
import br.uff.labtempo.omcp.common.Response;
import br.uff.labtempo.omcp.common.exceptions.BadRequestException;
import br.uff.labtempo.omcp.common.exceptions.InternalServerErrorException;
import br.uff.labtempo.omcp.common.exceptions.MethodNotAllowedException;
import br.uff.labtempo.omcp.common.exceptions.NotFoundException;
import br.uff.labtempo.omcp.common.exceptions.NotImplementedException;
import br.uff.labtempo.omcp.common.utils.RequestBuilder;
import br.uff.labtempo.osiris.to.function.ParameterizedRequestFn;
import br.uff.labtempo.osiris.to.function.RequestFnTo;
import br.uff.labtempo.osiris.to.function.ResponseFnTo;
import br.uff.labtempo.osiris.to.function.ValueFnTo;
import br.uff.labtempo.tmon.tmonavgfunction.controller.utils.FunctionInterfaceFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Felipe Santos <fralph at ic.uff.br>
 */
public class MainControllerTest {

    private double result = 8.5 / 4;
    private List<String> values;
    private MainController controller; 

    public MainControllerTest() {
        values = new ArrayList<>();
        values.add("1.5");
        values.add("2.5");
        values.add("4");
        values.add("0.5");
        controller = new MainController(); 
    }

    @Test
    public void testFunctionLogic_ShouldPass() throws NotFoundException, InternalServerErrorException, BadRequestException {

        RequestFnTo request = new RequestFnTo();
        request.addValue(FunctionInterfaceFactory.REQUEST_PARAM, values);
        ResponseFnTo response = controller.execute(request, Calendar.getInstance());

        List<ValueFnTo> rvalues = response.getValues();

        for (ValueFnTo value : rvalues) {
            if (FunctionInterfaceFactory.RESPONSE_PARAM.equals(value.getName())) {
                assertEquals(result, Double.parseDouble(value.getValue()));
                return;
            }
        }

        assertFalse(true);
    }
    
    @Test
    public void testFunctionRequest_ShouldPass() throws NotFoundException, InternalServerErrorException, BadRequestException, MethodNotAllowedException, NotImplementedException {
        RequestFnTo request = new RequestFnTo();
        request.addValue(FunctionInterfaceFactory.REQUEST_PARAM, values);
        
        ParameterizedRequestFn parameterized = new ParameterizedRequestFn(request);
        String url = parameterized.getRequestUri(FunctionInterfaceFactory.ADDRESS);
        
        RequestBuilder builder = new RequestBuilder();        
        Request r1 = builder.onGet(url).build();
        Response r2 = controller.routing(r1);

        ResponseFnTo  response = r2.getContent(ResponseFnTo.class);
        List<ValueFnTo> rvalues = response.getValues();

        for (ValueFnTo value : rvalues) {
            if (FunctionInterfaceFactory.RESPONSE_PARAM.equals(value.getName())) {
                assertEquals(result, Double.parseDouble(value.getValue()));
                return;
            }
        }

        assertFalse(true);
    }
    
    @Test(expected = BadRequestException.class)
    public void testFunctionIncorrectRequestParam_ShouldThrowException() throws NotFoundException, InternalServerErrorException, BadRequestException, MethodNotAllowedException, NotImplementedException {
        RequestFnTo request = new RequestFnTo();
        request.addValue("param", values);
        
        ParameterizedRequestFn parameterized = new ParameterizedRequestFn(request);
        String url = parameterized.getRequestUri(FunctionInterfaceFactory.ADDRESS);
        
        RequestBuilder builder = new RequestBuilder();        
        Request r1 = builder.onGet(url).build();
        Response r2 = controller.routing(r1);

        ResponseFnTo  response = r2.getContent(ResponseFnTo.class);
        List<ValueFnTo> rvalues = response.getValues();

        for (ValueFnTo value : rvalues) {
            if (FunctionInterfaceFactory.RESPONSE_PARAM.equals(value.getName())) {
                assertEquals(result, Double.parseDouble(value.getValue()));
                return;
            }
        }

        assertFalse(true);
    }

}
