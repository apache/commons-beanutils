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

package org.apache.commons.beanutils2.locale.converters;

import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.beanutils2.ConversionException;

/**
 * Standard {@link org.apache.commons.beanutils2.locale.LocaleConverter} implementation that converts an incoming locale-sensitive String into a {@link Long}
 * object, optionally using a default value or throwing a {@link org.apache.commons.beanutils2.ConversionException} if a conversion error occurs.
 */
public class LongLocaleConverter extends DecimalLocaleConverter<Long> {

    /**
     * Builds instances of {@link ByteLocaleConverter}.
     */
    public static class Builder extends DecimalLocaleConverter.Builder<Builder, Long> {

        /**
         * Constructs a new instance.
         */
        public Builder() {
            // empty
        }

        @Override
        public LongLocaleConverter get() {
            return new LongLocaleConverter(defaultValue, locale, pattern, useDefault || defaultValue != null, localizedPattern);
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

    private LongLocaleConverter(final Long defaultValue, final Locale locale, final String pattern, final boolean useDefault, final boolean locPattern) {
        super(defaultValue, locale, pattern, useDefault, locPattern);
    }

    /**
     * Parses the specified locale-sensitive input object into an output object of the specified type. This method will return a Long type.
     *
     * @param value   The input object to be converted
     * @param pattern The pattern is used for the conversion
     * @return The converted value
     * @throws ConversionException if conversion cannot be performed successfully
     * @throws ParseException      if an error occurs parsing a String to a Number
     * @since 1.8.0
     */
    @Override
    protected Long parse(final Object value, final String pattern) throws ParseException {
        final Number result = super.parse(value, pattern);

        if (result == null || result instanceof Long) {
            return (Long) result;
        }

        return Long.valueOf(result.longValue());
    }
}
