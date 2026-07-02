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

import java.util.UUID;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;

/**
 * {@link Converter} implementation that handles conversion to {@link UUID} objects.
 * <p>
 * Can be configured to either return a <em>default value</em> or throw a {@link ConversionException} if a conversion error occurs.
 * </p>
 *
 * @since 2.0
 */
public final class UUIDConverter extends AbstractConverter<UUID> {

    /**
     * Constructs a {@link UUID} an instance that throws a {@link ConversionException} if an error occurs.
     */
    public UUIDConverter() {
    }

    /**
     * Constructs a {@link UUID} an instance that returns a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned if the value to be converted is missing or an error occurs converting the value.
     */
    public UUIDConverter(final UUID defaultValue) {
        super(defaultValue);
    }

    /**
     * Converts an object to a {@link UUID}.
     * <p>
     * This implementation converts the input value to a {@link String} and then calls {@link UUID#fromString(String)} to perform the conversion.
     * </p>
     *
     * @param <T>   Target type of the conversion.
     * @param type  Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws IllegalArgumentException Thrown if an error occurs {@link UUID#fromString(String) converting} to the {@link UUID} type.
     * @throws ConversionException      Thrown if the given type is not {@link UUID}.
     * @since 2.0
     */
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (UUID.class.equals(type)) {
            return type.cast(UUID.fromString(String.valueOf(value)));
        }
        throw conversionException(type, value);
    }

    /**
     * Gets the default type this {@code Converter} handles.
     *
     * @return The default type this {@code Converter} handles.
     * @since 2.0
     */
    @Override
    protected Class<UUID> getDefaultType() {
        return UUID.class;
    }
}
