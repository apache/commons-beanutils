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

package org.apache.commons.beanutils2.sql.converters.locale;

import java.sql.Time;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.locale.converters.DateLocaleConverter;

/**
 * Standard {@link org.apache.commons.beanutils2.locale.LocaleConverter} implementation that converts an incoming locale-sensitive String into a
 * {@link java.sql.Time} object, optionally using a default value or throwing a {@link org.apache.commons.beanutils2.ConversionException} if a conversion error
 * occurs.
 */
public class SqlTimeLocaleConverter extends DateLocaleConverter<Time> {

    /**
     * Builds instances of {@link SqlTimeLocaleConverter}.
     */
    public static class Builder extends DateLocaleConverter.Builder<Builder, Time> {

        @Override
        public SqlTimeLocaleConverter get() {
            return new SqlTimeLocaleConverter(defaultValue, locale, pattern, useDefault || defaultValue != null, localizedPattern, lenient);
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

    private SqlTimeLocaleConverter(final Time defaultValue, final Locale locale, final String pattern, final boolean useDefault, final boolean locPattern,
            final boolean lenient) {
        super(defaultValue, locale, pattern, useDefault, locPattern, lenient);
    }

    /**
     * Converts the specified locale-sensitive input object into an output object of the specified type.
     *
     * @param value   The input object to be converted
     * @param pattern The pattern is used for the conversion
     * @return The converted value
     * @throws ConversionException if conversion cannot be performed successfully
     * @throws ParseException                                    if an error occurs parsing a String to a Number
     */
    @Override
    protected Time parse(final Object value, final String pattern) throws ParseException {
        // MUST cast to java.util.Date to avoid a CCE.
        return new Time(((java.util.Date) super.parse(value, pattern)).getTime());
    }
}
