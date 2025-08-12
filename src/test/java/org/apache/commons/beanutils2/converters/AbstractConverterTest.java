/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils2.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the AbstractConverter class.
 */
class AbstractConverterTest {

    private AbstractConverter<StringWrapper> converter;

    protected AbstractConverter<StringWrapper> makeConverter() {
        return new LowerCaseConverter();
    }

    @BeforeEach
    public void setUp() {
        converter = makeConverter();
    }

    @AfterEach
    public void tearDown() {
        converter = null;
    }

    @Test
    void testToLowerCaseDefaultLocale() {
        assertEquals(new StringWrapper("str"), converter.convert(StringWrapper.class, "str"));
        assertEquals(new StringWrapper("str"), converter.convert(StringWrapper.class, "STR"));
        assertEquals(new StringWrapper("hayir"), converter.convert(StringWrapper.class, "HAYIR"));
    }

    @Test
    void testToLowerCaseRootLocale() {
        converter.setLocale(Locale.ROOT);
        assertEquals(new StringWrapper("hayir"), converter.convert(StringWrapper.class, "HAYIR"));
    }

    @Test
    void testToLowerCaseLanguageLocale() {
        converter.setLocale(Locale.forLanguageTag("tr"));
        assertEquals(new StringWrapper("hayır"), converter.convert(StringWrapper.class, "HAYIR"));
    }

    private static class LowerCaseConverter extends AbstractConverter<StringWrapper> {
        @Override
        protected <T> T convertToType(Class<T> type, Object value) {
            if (StringWrapper.class.equals(type)) {
                return type.cast(new StringWrapper(toLowerCase(value.toString())));
            }

            throw conversionException(type, value);
        }

        @Override
        protected Class<StringWrapper> getDefaultType() {
            return StringWrapper.class;
        }
    }

    private static class StringWrapper {
        private String value;

        StringWrapper(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }

        void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof StringWrapper)) {
                return false;
            }
            StringWrapper stringWrapper = (StringWrapper) o;
            return Objects.equals(value, stringWrapper.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
