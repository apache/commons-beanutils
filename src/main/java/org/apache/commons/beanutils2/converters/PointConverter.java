/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.beanutils2.converters;

import java.awt.Point;
import java.util.regex.Pattern;

/**
 * @since 2.0.0
 */
public class PointConverter extends AbstractConverter {

    /** Pattern used to split the {@link Point#x} and {@link Point#y} coordinate. */
    private static final Pattern POINT_SPLIT = Pattern.compile("\\s*,\\s*");

    /**
     * Construct a <b>{@link Point}</b> <i>Converter</i> that throws a {@code ConversionException} if an error occurs.
     */
    public PointConverter() {
        super();
    }

    /**
     * Constructs a {@link org.apache.commons.beanutils2.Converter} that will return the specified default value if a
     * conversion error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public PointConverter(final Object defaultValue) {
        super(defaultValue);
    }

    /**
     * Gets the default type this {@code Converter} handles.
     *
     * @return The default type this {@code Converter} handles.
     * @since 2.0.0
     */
    @Override
    protected Class<?> getDefaultType() {
        return Point.class;
    }

    /**
     * Converts the specified input object into an output object of the specified type.
     *
     * @param value The {@link String} property value to convert.
     * @return A {@link Point} represented by the x and y coordinate of the input.
     * @throws NullPointerException If the value is null.
     * @throws IllegalArgumentException If the configuration value is an invalid representation of a {@link Point}.
     * @throws NumberFormatException If a one of coordinates can not be parsed to an {@link Integer}.
     */
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (Point.class.isAssignableFrom(type)) {
            final String stringValue = toString(value);

            if (stringValue.isEmpty()) {
                throw new IllegalArgumentException("A point can not be empty.");
            }

            final int lastCharIndex = stringValue.length() - 1;

            if (stringValue.charAt(0) != '(' || stringValue.charAt(lastCharIndex) != ')') {
                throw new IllegalArgumentException("Point coordinates must be enclosed in brackets.");
            }

            final String coordinates = stringValue.substring(1, lastCharIndex);
            final String[] xy = POINT_SPLIT.split(coordinates);

            if (xy.length != 2) {
                throw new IllegalArgumentException("Point must have an x coordinate, and y coordinate only, " +
                    "expecting the following format: (40, 200)");
            }

            final int x = Integer.parseInt(xy[0]);
            final int y = Integer.parseInt(xy[1]);
            return type.cast(new Point(x, y));
        }

        throw conversionException(type, value);
    }
}
