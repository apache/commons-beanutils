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
package org.apache.commons.beanutils2.bugs;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;

import org.apache.commons.beanutils2.Converter;
import org.apache.commons.beanutils2.locale.converters.IntegerLocaleConverter;
import org.junit.jupiter.api.Test;

/**
 * BaseLocaleConverter.checkConversionResult() fails with ConversionException when result is null when it should not.
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-458">https://issues.apache.org/jira/browse/BEANUTILS-458</a>
 */
public class Jira458TestCase {
    /**
     * Helper method for testing a conversion with null as default.
     *
     * @param input the input string
     */
    private void checkConversionWithNullDefault(final String input) {
        // final Converter<Integer> converter = new IntegerLocaleConverter(null, Locale.US);
        final Converter<Integer> converter = IntegerLocaleConverter.builder().setUseDefault(true).setLocale(Locale.US).get();
        assertNull(converter.convert(Integer.class, input), "Wrong result");
    }

    /**
     * Tests a conversion passing in an empty string.
     */
    @Test
    public void testConversionWithNullDefaultEmptyString() {
        checkConversionWithNullDefault("");
    }

    /**
     * Tests a conversion passing in null.
     */
    @Test
    public void testConversionWithNullDefaultNullInput() {
        checkConversionWithNullDefault(null);
    }
}
