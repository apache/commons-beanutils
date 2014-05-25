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
package org.apache.commons.beanutils.bugs;

import java.util.Locale;

import junit.framework.TestCase;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.locale.converters.IntegerLocaleConverter;

/**
 * BaseLocaleConverter.checkConversionResult() fails with ConversionException when result
 * is null when it should not.
 *
 * @version $Id$
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-458">https://issues.apache.org/jira/browse/BEANUTILS-458</a>
 */
public class Jira458TestCase extends TestCase {
    /**
     * Helper method for testing a conversion with null as default.
     *
     * @param input the input string
     */
    private void checkConversionWithNullDefault(String input) {
        Converter converter = new IntegerLocaleConverter(null, Locale.US);
        assertNull("Wrong result", converter.convert(Integer.class, input));
    }

    /**
     * Tests a conversion passing in null.
     */
    public void testConversionWithNullDefaultNullInput() {
        checkConversionWithNullDefault(null);
    }

    /**
     * Tests a conversion passing in an empty string.
     */
    public void testConversionWithNullDefaultEmptyString() {
        checkConversionWithNullDefault("");
    }
}
