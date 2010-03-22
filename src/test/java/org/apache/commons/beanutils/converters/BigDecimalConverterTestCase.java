/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.apache.commons.beanutils.converters;

import java.math.BigDecimal;

import junit.framework.TestSuite;

import org.apache.commons.beanutils.Converter;


/**
 * Test Case for the DoubleConverter class.
 *
 * @author Rodney Waldhoff
 * @version $Revision$ $Date$
 */

public class BigDecimalConverterTestCase extends NumberConverterTestBase {

    private Converter converter = null;

    // ------------------------------------------------------------------------

    public BigDecimalConverterTestCase(String name) {
        super(name);
    }
    
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        converter = makeConverter();
        numbers[0] = new BigDecimal("-12");
        numbers[1] = new BigDecimal("13");
        numbers[2] = new BigDecimal("-22");
        numbers[3] = new BigDecimal("23");
    }

    public static TestSuite suite() {
        return new TestSuite(BigDecimalConverterTestCase.class);        
    }

    public void tearDown() throws Exception {
        converter = null;
    }

    // ------------------------------------------------------------------------
    
    protected NumberConverter makeConverter() {
        return new BigDecimalConverter();
    }
    
    protected NumberConverter makeConverter(Object defaultValue) {
        return new BigDecimalConverter(defaultValue);
    }
    
    protected Class getExpectedType() {
        return BigDecimal.class;
    }

    // ------------------------------------------------------------------------

    public void testSimpleConversion() throws Exception {
        String[] message= { 
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from Byte",
            "from Short",
            "from Integer",
            "from Long",
            "from Float",
            "from Double"
        };
        
        Object[] input = { 
            "-17.2",
            "-1.1",
            "0.0",
            "1.1",
            "17.2",
            new Byte((byte)7),
            new Short((short)8),
            new Integer(9),
            new Long(10),
            new Float("11.1"),
            new Double("12.2")
        };
        
        BigDecimal[] expected = { 
            new BigDecimal("-17.2"),
            new BigDecimal("-1.1"),
            new BigDecimal("0.0"),
            new BigDecimal("1.1"),
            new BigDecimal("17.2"),
            new BigDecimal("7"),
            new BigDecimal("8"),
            new BigDecimal("9"),
            new BigDecimal("10"),
            new BigDecimal("11.1"),
            new BigDecimal("12.2")
        };
        
        for(int i=0;i<expected.length;i++) {
            assertEquals(
                message[i] + " to BigDecimal",
                expected[i],
                converter.convert(BigDecimal.class,input[i]));
            assertEquals(
                message[i] + " to null type",
                expected[i],
                converter.convert(null,input[i]));
        }
    }
    
}

