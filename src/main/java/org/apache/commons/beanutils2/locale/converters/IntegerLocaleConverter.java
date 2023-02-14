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

package org.apache.commons.beanutils2.locale.converters;

import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.beanutils2.ConversionException;

/**
 * Standard {@link org.apache.commons.beanutils2.locale.LocaleConverter} implementation that converts an incoming locale-sensitive String into a {@link Integer}
 * object, optionally using a default value or throwing a {@link org.apache.commons.beanutils2.ConversionException} if a conversion error occurs.
 */
public class IntegerLocaleConverter extends DecimalLocaleConverter<Integer> {

    /**
     * Builds instances of {@link ByteLocaleConverter}.
     */
    public static class Builder extends DecimalLocaleConverter.Builder<Builder, Integer> {

        @Override
        public IntegerLocaleConverter get() {
            return new IntegerLocaleConverter(defaultValue, locale, pattern, useDefault || defaultValue != null, localizedPattern);
        }

    }

    /**
     * Constructs a new builder.
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    private IntegerLocaleConverter(final Integer defaultValue, final Locale locale, final String pattern, final boolean useDefault, final boolean locPattern) {
        super(defaultValue, locale, pattern, useDefault, locPattern);
    }

    /**
     * Parses the specified locale-sensitive input object into an output object of the specified type. This method will return Integer type.
     *
     * @param value   The input object to be converted
     * @param pattern The pattern is used for the conversion
     * @return The converted value
     *
     * @throws ConversionException if conversion cannot be performed successfully
     * @throws ParseException      if an error occurs parsing a String to a Number
     */
    @Override
    protected Integer parse(final Object value, final String pattern) throws ParseException {
        final Number parsed = super.parse(value, pattern);
        if (parsed.longValue() != parsed.intValue()) {
            throw new ConversionException("Supplied number is not of type Integer: " + parsed.longValue());
        }
        return Integer.valueOf(parsed.intValue()); // unlike superclass it will return proper Integer
    }
}
