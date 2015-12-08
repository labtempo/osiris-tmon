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

import br.uff.labtempo.osiris.to.common.definitions.FunctionOperation;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingVsnTo;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Felipe Santos <live.proto at hotmail.com>
 */
public class BlendingVsnToComparatorTest {
    
    public BlendingVsnToComparatorTest() {
    }

    @Test
    public void testBlendingsEquals_ShouldPass() {
        BlendingVsnTo b1 = new BlendingVsnTo(""); 
        b1.createField(1, "field1", 1);
        b1.createField(2, "field2", 2);
        b1.setFunction(1);       
        b1.setCallMode(FunctionOperation.SYNCHRONOUS);
        b1.setCallIntervalInMillis(2500);
        b1.addRequestParam(1, "param1");
        b1.addRequestParam(2, "param1");
        b1.addRequestParam(3, "param2");
        b1.addResponseParam(1, "functionparam1");
        b1.addResponseParam(2, "functionparam2");
        
        BlendingVsnTo b2 = new BlendingVsnTo(""); 
        b2.createField(1, "field1", 1);
        b2.createField(2, "field2", 2);
        b2.setFunction(1);       
        b2.setCallMode(FunctionOperation.SYNCHRONOUS);
        b2.setCallIntervalInMillis(2500);
        b2.addRequestParam(1, "param1");
        b2.addRequestParam(2, "param1");
        b2.addRequestParam(3, "param2");
        b2.addResponseParam(1, "functionparam1");
        b2.addResponseParam(2, "functionparam2");
        
        assertTrue(BlendingVsnToComparator.equals(b1, b2));
    }
    
    @Test
    public void testBlendingsFieldsNotEquals_ShouldPass() {
        BlendingVsnTo b1 = new BlendingVsnTo(""); 
        b1.createField(1, "field10", 1);
        b1.createField(2, "field20", 2);
        b1.setFunction(1);       
        b1.setCallMode(FunctionOperation.SYNCHRONOUS);
        b1.setCallIntervalInMillis(2500);
        b1.addRequestParam(1, "param1");
        b1.addRequestParam(2, "param1");
        b1.addRequestParam(3, "param2");
        b1.addResponseParam(1, "functionparam1");
        b1.addResponseParam(2, "functionparam2");
        
        BlendingVsnTo b2 = new BlendingVsnTo(""); 
        b2.createField(1, "field1", 1);
        b2.createField(2, "field2", 2);
        b2.setFunction(1);       
        b2.setCallMode(FunctionOperation.SYNCHRONOUS);
        b2.setCallIntervalInMillis(2500);
        b2.addRequestParam(1, "param1");
        b2.addRequestParam(2, "param1");
        b2.addRequestParam(3, "param2");
        b2.addResponseParam(1, "functionparam1");
        b2.addResponseParam(2, "functionparam2");
        
        assertFalse(BlendingVsnToComparator.equals(b1, b2));
    }
    
    @Test
    public void testBlendingsFunctionNotEquals_ShouldPass() {
        BlendingVsnTo b1 = new BlendingVsnTo(""); 
        b1.createField(1, "field1", 1);
        b1.createField(2, "field2", 2);
        b1.setFunction(2);       
        b1.setCallMode(FunctionOperation.SYNCHRONOUS);
        b1.setCallIntervalInMillis(2500);
        b1.addRequestParam(1, "param1");
        b1.addRequestParam(2, "param1");
        b1.addRequestParam(3, "param2");
        b1.addResponseParam(1, "functionparam1");
        b1.addResponseParam(2, "functionparam2");
        
        BlendingVsnTo b2 = new BlendingVsnTo(""); 
        b2.createField(1, "field1", 1);
        b2.createField(2, "field2", 2);
        b2.setFunction(1);       
        b2.setCallMode(FunctionOperation.SYNCHRONOUS);
        b2.setCallIntervalInMillis(2500);
        b2.addRequestParam(1, "param1");
        b2.addRequestParam(2, "param1");
        b2.addRequestParam(3, "param2");
        b2.addResponseParam(1, "functionparam1");
        b2.addResponseParam(2, "functionparam2");
        
        assertFalse(BlendingVsnToComparator.equals(b1, b2));
    }
    @Test
    public void testBlendingsCallModeNotEquals_ShouldPass() {
        BlendingVsnTo b1 = new BlendingVsnTo(""); 
        b1.createField(1, "field1", 1);
        b1.createField(2, "field2", 2);
        b1.setFunction(1);       
        b1.setCallMode(FunctionOperation.ASYNCHRONOUS);
        b1.setCallIntervalInMillis(2500);
        b1.addRequestParam(1, "param1");
        b1.addRequestParam(2, "param1");
        b1.addRequestParam(3, "param2");
        b1.addResponseParam(1, "functionparam1");
        b1.addResponseParam(2, "functionparam2");
        
        BlendingVsnTo b2 = new BlendingVsnTo(""); 
        b2.createField(1, "field1", 1);
        b2.createField(2, "field2", 2);
        b2.setFunction(1);       
        b2.setCallMode(FunctionOperation.SYNCHRONOUS);
        b2.setCallIntervalInMillis(2500);
        b2.addRequestParam(1, "param1");
        b2.addRequestParam(2, "param1");
        b2.addRequestParam(3, "param2");
        b2.addResponseParam(1, "functionparam1");
        b2.addResponseParam(2, "functionparam2");
        
        assertFalse(BlendingVsnToComparator.equals(b1, b2));
    }
    
    @Test
    public void testBlendingsCallIntervalNotEquals_ShouldPass() {
        BlendingVsnTo b1 = new BlendingVsnTo(""); 
        b1.createField(1, "field1", 1);
        b1.createField(2, "field2", 2);
        b1.setFunction(1);       
        b1.setCallMode(FunctionOperation.SYNCHRONOUS);
        b1.setCallIntervalInMillis(2000);
        b1.addRequestParam(1, "param1");
        b1.addRequestParam(2, "param1");
        b1.addRequestParam(3, "param2");
        b1.addResponseParam(1, "functionparam1");
        b1.addResponseParam(2, "functionparam2");
        
        BlendingVsnTo b2 = new BlendingVsnTo(""); 
        b2.createField(1, "field1", 1);
        b2.createField(2, "field2", 2);
        b2.setFunction(1);       
        b2.setCallMode(FunctionOperation.SYNCHRONOUS);
        b2.setCallIntervalInMillis(2500);
        b2.addRequestParam(1, "param1");
        b2.addRequestParam(2, "param1");
        b2.addRequestParam(3, "param2");
        b2.addResponseParam(1, "functionparam1");
        b2.addResponseParam(2, "functionparam2");
        
        assertFalse(BlendingVsnToComparator.equals(b1, b2));
    }
    
    @Test
    public void testBlendingsRequestParamsNotEquals_ShouldPass() {
        BlendingVsnTo b1 = new BlendingVsnTo(""); 
        b1.createField(1, "field1", 1);
        b1.createField(2, "field2", 2);
        b1.setFunction(1);       
        b1.setCallMode(FunctionOperation.SYNCHRONOUS);
        b1.setCallIntervalInMillis(2500);
        b1.addRequestParam(1, "param1");
        b1.addResponseParam(1, "functionparam1");
        b1.addResponseParam(2, "functionparam2");
        
        BlendingVsnTo b2 = new BlendingVsnTo(""); 
        b2.createField(1, "field1", 1);
        b2.createField(2, "field2", 2);
        b2.setFunction(1);       
        b2.setCallMode(FunctionOperation.SYNCHRONOUS);
        b2.setCallIntervalInMillis(2500);
        b2.addRequestParam(1, "param1");
        b2.addRequestParam(2, "param1");
        b2.addRequestParam(3, "param2");
        b2.addResponseParam(1, "functionparam1");
        b2.addResponseParam(2, "functionparam2");
        
        assertFalse(BlendingVsnToComparator.equals(b1, b2));
    }
    
    @Test
    public void testBlendingsResponseParamsNotEquals_ShouldPass() {
        BlendingVsnTo b1 = new BlendingVsnTo(""); 
        b1.createField(1, "field1", 1);
        b1.createField(2, "field2", 2);
        b1.setFunction(1);       
        b1.setCallMode(FunctionOperation.SYNCHRONOUS);
        b1.setCallIntervalInMillis(2500);
        b1.addRequestParam(1, "param1");
        b1.addRequestParam(2, "param1");
        b1.addRequestParam(3, "param2");
        b1.addResponseParam(1, "functionparam10");
        b1.addResponseParam(2, "functionparam20");
        
        BlendingVsnTo b2 = new BlendingVsnTo(""); 
        b2.createField(1, "field1", 1);
        b2.createField(2, "field2", 2);
        b2.setFunction(1);       
        b2.setCallMode(FunctionOperation.SYNCHRONOUS);
        b2.setCallIntervalInMillis(2500);
        b2.addRequestParam(1, "param1");
        b2.addRequestParam(2, "param1");
        b2.addRequestParam(3, "param2");
        b2.addResponseParam(1, "functionparam1");
        b2.addResponseParam(2, "functionparam2");
        
        assertFalse(BlendingVsnToComparator.equals(b1, b2));
    }
}
