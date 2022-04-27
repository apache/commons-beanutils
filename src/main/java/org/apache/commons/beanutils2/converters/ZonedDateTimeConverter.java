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

import java.time.ZonedDateTime;

/**
 * {@link DateTimeConverter} implementation that handles conversion to
 * and from <b>java.time.ZonedDateTime</b> objects.
 * <p>
 * This implementation can be configured to handle conversion either
 * by using a Locale's default format or by specifying a set of format
 * patterns (note, there is no default String conversion for Calendar).
 * See the {@link DateTimeConverter} documentation for further details.
 * </p>
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * {@code ConversionException} if a conversion error occurs.
 * </p>
 *
 * @since 2.0
 * @see java.time.ZonedDateTime
 */
public final class ZonedDateTimeConverter extends DateTimeConverter<ZonedDateTime> {

    /**
     * Constructs a <b>java.time.ZonedDateTime</b> <i>Converter</i> that throws
     * a {@code ConversionException} if an error occurs.
     */
    public ZonedDateTimeConverter() {
    }

    /**
     * Constructs a <b>java.time.ZonedDateTime</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public ZonedDateTimeConverter(final ZonedDateTime defaultValue) {
        super(defaultValue);
    }

    /**
     * Gets the default type this {@code Converter} handles.
     *
     * @return The default type this {@code Converter} handles.
     */
    @Override
    protected Class<ZonedDateTime> getDefaultType() {
        return ZonedDateTime.class;
    }

}
