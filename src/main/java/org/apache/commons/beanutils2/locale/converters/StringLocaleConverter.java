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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.locale.BaseLocaleConverter;
import org.apache.commons.beanutils2.locale.LocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Standard {@link org.apache.commons.beanutils2.locale.LocaleConverter} implementation that converts an incoming locale-sensitive object into a {@link String}
 * object, optionally using a default value or throwing a {@link org.apache.commons.beanutils2.ConversionException} if a conversion error occurs.
 */
public class StringLocaleConverter extends BaseLocaleConverter<String> {

    /**
     * Builds instances of {@link StringLocaleConverter}.
     */
    public static class Builder extends BaseLocaleConverter.Builder<Builder, String> {

        /**
         * Gets a new instance.
         * <p>
         * Defaults construct a {@link LocaleConverter} that will throw a {@link ConversionException} if a conversion error occurs. The locale is the default
         * locale for this instance of the Java Virtual Machine and an unlocalized pattern is used for the conversion.
         * </p>
         *
         * @return a new instance.
         */
        @Override
        public StringLocaleConverter get() {
            return new StringLocaleConverter(defaultValue, locale, pattern, useDefault || defaultValue != null, localizedPattern);
        }

    }

    /** All logging goes through this logger */
    private static final Log LOG = LogFactory.getLog(StringLocaleConverter.class);

    /**
     * Constructs a new builder.
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    private StringLocaleConverter(final String defaultValue, final Locale locale, final String pattern, final boolean useDefault, final boolean locPattern) {
        super(defaultValue, locale, pattern, useDefault, locPattern);
    }

    /**
     * Gets an instance of DecimalFormat.
     *
     * @param locale  The locale
     * @param pattern The pattern is used for the conversion
     * @return The format for the locale and pattern
     * @throws ConversionException      if conversion cannot be performed successfully
     * @throws IllegalArgumentException if an error occurs parsing a String to a Number
     */
    private DecimalFormat getDecimalFormat(final Locale locale, final String pattern) {
        final DecimalFormat numberFormat = (DecimalFormat) NumberFormat.getInstance(locale);

        // if some constructors default pattern to null, it makes only sense to handle null pattern gracefully
        if (pattern != null) {
            if (localizedPattern) {
                numberFormat.applyLocalizedPattern(pattern);
            } else {
                numberFormat.applyPattern(pattern);
            }
        } else {
            LOG.debug("No pattern provided, using default.");
        }

        return numberFormat;
    }

    /**
     * Parses the specified locale-sensitive input object into an output object of the specified type.
     *
     * @param value   The input object to be converted
     * @param pattern The pattern is used for the conversion
     * @return The converted value
     * @throws ConversionException if conversion cannot be performed successfully
     * @throws ParseException                                    if an error occurs
     */
    @Override
    protected String parse(final Object value, final String pattern) throws ParseException {
        String result = null;

        if (value instanceof Integer || value instanceof Long || value instanceof BigInteger || value instanceof Byte || value instanceof Short) {
            result = getDecimalFormat(locale, pattern).format(((Number) value).longValue());
        } else if (value instanceof Double || value instanceof BigDecimal || value instanceof Float) {
            result = getDecimalFormat(locale, pattern).format(((Number) value).doubleValue());
        } else if (value instanceof Date) { // java.util.Date, java.sql.Date, java.sql.Time, java.sql.Timestamp
            result = new SimpleDateFormat(pattern, locale).format(value);
        } else {
            result = value.toString();
        }

        return result;
    }
}
