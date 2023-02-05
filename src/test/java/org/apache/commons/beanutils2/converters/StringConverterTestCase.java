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

import junit.framework.TestCase;

/**
 * Test case for {@code StringConverter}.
 *
 */
public class StringConverterTestCase extends TestCase {

    private static final String DEFAULT_VALUE = "default";

    /** The converter to be tested. */
    private StringConverter converter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        converter = new StringConverter();
    }

    /**
     * Tests a conversion to a string type.
     */
    public void testConvertToTypeString() {
        final Object value = new Object();
        final String strVal = converter.convert(String.class, value);
        assertEquals("Wrong conversion result", value.toString(), strVal);
    }

    /**
     * Tests whether the correct default type is returned.
     */
    public void testDefaultType() {
        assertEquals("Wrong default type", String.class, converter.getDefaultType());
    }

    /**
     * Tests whether the correct default value is returned.
     */
    public void testDefaultValue() {
        converter.setDefaultValue(DEFAULT_VALUE);
        assertEquals("Wrong default value", DEFAULT_VALUE, converter.convert(String.class, null));
    }
}
