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

package org.apache.commons.beanutils2.converters;

import org.apache.commons.beanutils2.ConversionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Test Case for the EnumConverter class.
 */
public class EnumConverterTestCase {

    private EnumConverter converter;

    @Before
    public void before() {
        converter = new EnumConverter();
    }

    @Test
    public void testSimpleConversion() throws Exception {
        final String[] message= {
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
        };

        final Object[] input = {
            "DELIVERED",
            "ORDERED",
            "READY"
        };

        final PizzaStatus[] expected = {
            PizzaStatus.DELIVERED,
            PizzaStatus.ORDERED,
            PizzaStatus.READY
        };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(message[i] + " to Enum", expected[i], converter.convert(PizzaStatus.class,input[i]));
        }

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i] + " to String", input[i], converter.convert(String.class, expected[i]));
        }
    }

    /**
     * Tests a conversion to an unsupported type.
     */
    @Test(expected = ConversionException.class)
    public void testUnsupportedType() {
        converter.convert(Integer.class, "http://www.apache.org");
    }

    public enum PizzaStatus {
        ORDERED,
        READY,
        DELIVERED;
    }

    @Test
    public void testConvertTimeUnit() {
        final TimeUnit expected = TimeUnit.NANOSECONDS;
        final Enum actual = converter.convert(Enum.class, "java.util.concurrent.TimeUnit.NANOSECONDS");

        Assert.assertEquals(expected, actual);
    }


    @Test
    public void testConvertDayOfWeek() {
        final DayOfWeek expected = DayOfWeek.MONDAY;
        final Enum actual = converter.convert(Enum.class, "java.time.DayOfWeek#MONDAY");

        Assert.assertEquals(expected, actual);
    }

    @Test(expected = ConversionException.class)
    public void testConvertWrongEnumType() {
        converter.convert(TimeUnit.class, "java.time.DayOfWeek#MONDAY");
    }

    @Test(expected = ConversionException.class)
    public void testBrokenNamingConvention() {
        converter.convert(Enum.class, "JAVA-TIME-DAYOFWEEK#MONDAY");
    }

    @Test(expected = ConversionException.class)
    public void testNonEnumClasses() {
        converter.convert(Enum.class, "java.lang.String#MONDAY");
    }

    @Test(expected = ConversionException.class)
    public void testNonExistingClasses() {
        converter.convert(Enum.class, "java.lang.does.not.exist#MONDAY");
    }
}

