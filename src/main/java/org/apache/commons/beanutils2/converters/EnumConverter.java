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

/**
 * {@link org.apache.commons.beanutils2.Converter} implementation that handles conversion to and from <strong>java.lang.Enum</strong> objects.
 * <p>
 * Can be configured to either return a <em>default value</em> or throw a {@code ConversionException} if a conversion error occurs.
 * </p>
 *
 * @param <E> The enum type subclass
 * @since 2.0
 * @see Enum
 */
public final class EnumConverter<E extends Enum<E>> extends AbstractConverter<Enum<E>> {

    /**
     * Constructs a <strong>java.lang.Enum</strong> <em>Converter</em> that throws a {@code ConversionException} if an error occurs.
     */
    public EnumConverter() {
    }

    /**
     * Constructs a <strong>java.lang.Enum</strong> <em>Converter</em> that returns a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned if the value to be converted is missing or an error occurs converting the value.
     */
    public EnumConverter(final Enum<E> defaultValue) {
        super(defaultValue);
    }

    /**
     * <p>
     * Converts a java.lang.Enum or object into a String.
     * </p>
     *
     * @param <R>   Target type of the conversion.
     * @param type  Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     * @since 2.0
     */
    @SuppressWarnings({ "rawtypes" })
    @Override
    protected <R> R convertToType(final Class<R> type, final Object value) throws Throwable {
        if (Enum.class.isAssignableFrom(type)) {
            final String stringValue = toString(value);

            try {
                return type.cast((Enum) Enum.valueOf((Class) type, stringValue));
            } catch (IllegalArgumentException ex) {
                // Continue to check fully qualified name.
            }

            final int lastHash = stringValue.lastIndexOf('#');
            final int lastDot = stringValue.lastIndexOf('.');

            if (lastDot == -1 && lastHash == -1) {
                throw new IllegalArgumentException(
                    "Expected fully qualified name for Enum constant like: java.time.DayOfWeek.MONDAY");
            }

            final String enumValue = stringValue.substring(Math.max(lastHash, lastDot) + 1);
            final String className = stringValue.substring(0, stringValue.length() - enumValue.length() - 1);

            try {
                Class classForName = Class.forName(className);

                if (!classForName.isEnum()) {
                    throw new IllegalArgumentException("Value isn't an enumerated type.");
                }

                if (!type.isAssignableFrom(classForName)) {
                    throw new IllegalArgumentException("Class is not the required type.");
                }

                return type.cast((Enum) Enum.valueOf(classForName, enumValue));
            } catch (ClassNotFoundException ex) {
                throw new IllegalArgumentException("Class \"" + className + "\" doesn't exist.", ex);
            }
        }

        throw conversionException(type, value);
    }

    /**
     * Gets the default type this {@code Converter} handles.
     *
     * @return The default type this {@code Converter} handles.
     * @since 2.0
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class<Enum<E>> getDefaultType() {
        return (Class) Enum.class;
    }

}
