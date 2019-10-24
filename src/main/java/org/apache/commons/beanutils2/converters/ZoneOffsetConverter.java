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

import java.time.ZoneOffset;

/**
 * {@link org.apache.commons.beanutils2.Converter} implementation that handles conversion
 * to and from <b>java.time.ZoneOffset</b> objects.
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * {@code ConversionException} if a conversion error occurs.
 * </p>
 *
 * @since 2.0
 * @see java.time.ZoneOffset
 */
public final class ZoneOffsetConverter extends AbstractConverter {

    /**
     * Construct a <b>java.time.ZoneOffset</b> <i>Converter</i> that throws
     * a {@code ConversionException} if an error occurs.
     */
    public ZoneOffsetConverter() {
        super();
    }

    /**
     * Construct a <b>java.time.ZoneOffset</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public ZoneOffsetConverter(final Object defaultValue) {
        super(defaultValue);
    }

    /**
     * Return the default type this {@code Converter} handles.
     *
     * @return The default type this {@code Converter} handles.
     * @since 2.0
     */
    @Override
    protected Class<?> getDefaultType() {
        return ZoneOffset.class;
    }

    /**
     * <p>Convert a java.time.ZoneOffset or object into a String.</p>
     *
     * @param <T> Target type of the conversion.
     * @param type Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     * @since 2.0
     */
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (ZoneOffset.class.equals(type)) {
            return type.cast(ZoneOffset.of((String.valueOf(value))));
        }

        throw conversionException(type, value);
    }

}
