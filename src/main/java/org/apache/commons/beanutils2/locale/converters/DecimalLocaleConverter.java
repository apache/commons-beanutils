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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.locale.BaseLocaleConverter;
import org.apache.commons.beanutils2.locale.LocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Standard {@link LocaleConverter} implementation that converts an incoming locale-sensitive String into a {@code java.lang.Number} object, optionally using a
 * default value or throwing a {@link ConversionException} if a conversion error occurs.
 *
 * @param <T> The converter type.
 * @since 1.7
 */
public class DecimalLocaleConverter<T extends Number> extends BaseLocaleConverter<T> {

    /**
     * Builds instances of {@link DateLocaleConverter}.
     *
     * @param <B> The builder type.
     * @param <T> The Number type.
     */
    public static class Builder<B extends Builder<B, T>, T extends Number> extends BaseLocaleConverter.Builder<B, T> {

        /**
         * Constructs a new instance.
         * <p>
         * By default, construct a {@link LocaleConverter} that will throw a {@link ConversionException} if a conversion error occurs. The locale is the default
         * locale for this instance of the Java Virtual Machine and an unlocalized pattern is used for the conversion.
         * </p>
         *
         * @return a new instance.
         */
        @Override
        public DecimalLocaleConverter<?> get() {
            return new DecimalLocaleConverter<>(defaultValue, locale, pattern, useDefault || defaultValue != null, localizedPattern);
        }

    }

    /** All logging goes through this logger */
    private static final Log LOG = LogFactory.getLog(DecimalLocaleConverter.class);

    /**
     * Constructs a new builder.
     *
     * @param <B> The builder type.
     * @param <T> The Number type.
     * @return a new builder.
     */
    @SuppressWarnings("unchecked")
    public static <B extends Builder<B, T>, T extends Number> B builder() {
        return (B) new Builder<>();
    }

    /**
     * Constructs a new instance.
     *
     * @param defaultValue default value.
     * @param locale locale.
     * @param pattern pattern.
     * @param useDefault use the default.
     * @param locPattern localized pattern.
     */
    protected DecimalLocaleConverter(final T defaultValue, final Locale locale, final String pattern, final boolean useDefault, final boolean locPattern) {
        super(defaultValue, locale, pattern, useDefault, locPattern);
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
    protected T parse(final Object value, final String pattern) throws ParseException {
        if (value instanceof Number) {
            return (T) value;
        }

        // Note that despite the ambiguous "getInstance" name, and despite the
        // fact that objects returned from this method have the same toString
        // representation, each call to getInstance actually returns a new
        // object.
        final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(locale);

        // if some constructors default pattern to null, it makes only sense
        // to handle null pattern gracefully
        if (pattern != null) {
            if (localizedPattern) {
                formatter.applyLocalizedPattern(pattern);
            } else {
                formatter.applyPattern(pattern);
            }
        } else {
            LOG.debug("No pattern provided, using default.");
        }

        return (T) formatter.parse((String) value);
    }
}
