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
 * Standard {@link org.apache.commons.beanutils2.locale.LocaleConverter} implementation that converts an incoming locale-sensitive String into a {@link Short}
 * object, optionally using a default value or throwing a {@link org.apache.commons.beanutils2.ConversionException} if a conversion error occurs.
 */
public class ShortLocaleConverter extends DecimalLocaleConverter<Short> {

    /**
     * Builds instances of {@link ByteLocaleConverter}.
     */
    public static class Builder extends DecimalLocaleConverter.Builder<Builder, Short> {

        /**
         * Gets a new instance.
         *
         * @return a new instance.
         */
        @Override
        public ShortLocaleConverter get() {
            return new ShortLocaleConverter(defaultValue, locale, pattern, useDefault || defaultValue != null, localizedPattern);
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

    private ShortLocaleConverter(final Short defaultValue, final Locale locale, final String pattern, final boolean useDefault, final boolean locPattern) {
        super(defaultValue, locale, pattern, useDefault, locPattern);
    }

    /**
     * Parses the specified locale-sensitive input object into an output object of the specified type. This method will return values of type Short.
     *
     * @param value   The input object to be converted
     * @param pattern The pattern is used for the conversion
     * @return The converted value
     *
     * @throws ConversionException if conversion cannot be performed successfully
     * @throws ParseException                                    if an error occurs parsing a String to a Number
     * @since 1.8.0
     */
    @Override
    protected Short parse(final Object value, final String pattern) throws ParseException {
        final Object result = super.parse(value, pattern);

        if (result == null || result instanceof Short) {
            return (Short) result;
        }

        final Number parsed = (Number) result;
        if (parsed.longValue() != parsed.shortValue()) {
            throw new ConversionException("Supplied number is not of type Short: " + parsed.longValue());
        }

        // now returns property Short
        return Short.valueOf(parsed.shortValue());
    }

}
