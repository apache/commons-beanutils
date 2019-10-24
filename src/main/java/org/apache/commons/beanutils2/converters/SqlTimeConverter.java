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

import java.sql.Time;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * {@link DateTimeConverter} implementation that handles conversion to
 * and from <b>java.sql.Time</b> objects.
 * <p>
 * This implementation can be configured to handle conversion either
 * by using java.sql.Time's default String conversion, or by using a
 * Locale's default format or by specifying a set of format patterns.
 * See the {@link DateTimeConverter} documentation for further details.
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * {@code ConversionException} if a conversion error occurs.
 *
 * @since 1.3
 */
public final class SqlTimeConverter extends DateTimeConverter {

    /**
     * Construct a <b>java.sql.Time</b> <i>Converter</i> that throws
     * a {@code ConversionException} if an error occurs.
     */
    public SqlTimeConverter() {
        super();
    }

    /**
     * Construct a <b>java.sql.Time</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public SqlTimeConverter(final Object defaultValue) {
        super(defaultValue);
    }

    /**
     * Return the default type this {@code Converter} handles.
     *
     * @return The default type this {@code Converter} handles.
     * @since 1.8.0
     */
    @Override
    protected Class<?> getDefaultType() {
        return Time.class;
    }

    /**
     * Return a {@code DateFormat} for the Locale.
     * @param locale TODO
     * @param timeZone TODO
     *
     * @return The DateFormat.
     * @since 1.8.0
     */
    @Override
    protected DateFormat getFormat(final Locale locale, final TimeZone timeZone) {
        DateFormat format = null;
        if (locale == null) {
            format = DateFormat.getTimeInstance(DateFormat.SHORT);
        } else {
            format = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
        }
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        return format;
    }

}
