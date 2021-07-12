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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link org.apache.commons.beanutils2.Converter} implementation that handles conversion
 * to and from <b>java.lang.Enum</b> objects.
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * {@code ConversionException} if a conversion error occurs.
 * </p>
 *
 * @since 2.0
 * @see java.lang.Enum
 */
public final class EnumConverter extends AbstractConverter {

    /** Validates that the value is an enum, and splits it into it's components. */
    private static final Pattern ENUM_PATTERN = Pattern.compile(
        "(?<package>(?:[a-z\\d.]+)*)\\.(?<class>[A-Za-z\\d]+)[#.](?<name>[A-Z\\d_]+)"
    );

    /**
     * Constructs a <b>java.lang.Enum</b> <i>Converter</i> that throws
     * a {@code ConversionException} if an error occurs.
     */
    public EnumConverter() {
    }

    /**
     * Constructs a <b>java.lang.Enum</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public EnumConverter(final Object defaultValue) {
        super(defaultValue);
    }

    /**
     * Gets the default type this {@code Converter} handles.
     *
     * @return The default type this {@code Converter} handles.
     * @since 2.0
     */
    @Override
    protected Class<?> getDefaultType() {
        return Enum.class;
    }

    /**
     * <p>Converts a java.lang.Enum or object into a String.</p>
     *
     * @param <T> Target type of the conversion.
     * @param type Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     * @since 2.0
     */
    @SuppressWarnings({ "rawtypes" })
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (Enum.class.isAssignableFrom(type)) {
            final String stringValue = toString(value);

            try {
                return type.cast((Enum) Enum.valueOf((Class) type, stringValue));
            } catch (IllegalArgumentException ex) {
                // Continue to check fully qualified name.
            }

            Matcher matcher = ENUM_PATTERN.matcher(stringValue);

            if (!matcher.matches()) {
                throw new IllegalArgumentException(
                    "Value doesn't follow Java naming conventions, expected input like: java.time.DayOfWeek.MONDAY");
            }

            String className = matcher.group(1) + "." + matcher.group(2);

            try {
                Class classForName = Class.forName(className);

                if (!classForName.isEnum()) {
                    throw new IllegalArgumentException("Value provided isn't an enumerated type.");
                }

                if (!type.isAssignableFrom(classForName)) {
                    throw new IllegalArgumentException("Class provided is not the required type.");
                }

                return type.cast((Enum) Enum.valueOf(classForName, matcher.group(3)));
            } catch (ClassNotFoundException ex) {
                throw new IllegalArgumentException("Class \"" + className + "\" doesn't exist.", ex);
            }
        }

        throw conversionException(type, value);
    }
}
