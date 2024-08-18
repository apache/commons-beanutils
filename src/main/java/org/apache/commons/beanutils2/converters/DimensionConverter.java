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

import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link org.apache.commons.beanutils2.Converter} implementation that handles conversion to and from {@link Dimension}.
 *
 * <p>
 *     Accepts a single {@link Integer} value, or two {@link Integer} values separated by the character {@code x}.
 * </p>
 *
 * <p>The dimensions must consist of non-negative {@link Integer} values only.</p>
 *
 * @since 2.0.0
 */
public class DimensionConverter extends AbstractConverter<Dimension> {

    /** Pattern used to validate and tokenizer the {@link String}.  */
    private static final Pattern DIMENSION_PATTERN = Pattern.compile("(\\d+)(?:x(\\d+))?");

    /**
     * Construct a <b>{@link Dimension}</b> <em>Converter</em> that throws a {@code ConversionException} if an error
     * occurs.
     */
    public DimensionConverter() {
    }

    /**
     * Constructs a {@link org.apache.commons.beanutils2.Converter} that will return the specified default value if a
     * conversion error occurs.
     *
     * @param defaultValue The default value to be returned if the value to be converted is missing or an error occurs
     * converting the value.
     */
    public DimensionConverter(final Dimension defaultValue) {
        super(defaultValue);
    }

    /**
     * Converts the input object into an output object of the specified type.
     *
     * @param type Data type to which this value should be converted.
     * @param value String property value to convert.
     * @return A {@link Dimension} which represents the configuration property value.
     * @throws NullPointerException If the value is null.
     * @throws NumberFormatException If the {@link Dimension} width or height is bigger than {@link Integer#MAX_VALUE}.
     */
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (Dimension.class.isAssignableFrom(type)) {
            final String stringValue = toString(value);

            if (stringValue.isEmpty()) {
                throw new IllegalArgumentException("Dimensions can not be empty.");
            }

            final Matcher matcher = DIMENSION_PATTERN.matcher(stringValue);

            if (!matcher.matches()) {
                throw new IllegalArgumentException(
                    "Dimension doesn't match format: {width/height} or {width}x{height}");
            }

            final String x = matcher.group(1);
            final String y = matcher.group(2);

            final int xValue = Integer.parseInt(x);
            final int yValue = y == null || x.equals(y) ? xValue : Integer.parseInt(y);

            return type.cast(new Dimension(xValue, yValue));
        }

        throw conversionException(type, value);
    }

    /**
     * Gets the default type this {@code Converter} handles.
     *
     * @return The default type this {@code Converter} handles.
     * @since 2.0.0
     */
    @Override
    protected Class<Dimension> getDefaultType() {
        return Dimension.class;
    }
}
