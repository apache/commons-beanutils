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
 * {@link org.apache.commons.beanutils2.Converter} implementation that converts an incoming object into a {@link String} object.
 * <p>
 * Note that ConvertUtils really is designed to do string-&gt;object conversions, and offers very little support for object-&gt;string conversions. The
 * ConvertUtils/ConvertUtilsBean methods only select a converter to apply based upon the target type being converted to, and generally assume that the input is
 * a string (by calling its toString method if needed).
 * <p>
 * This class is therefore just a dummy converter that converts its input into a string by calling the input object's toString method and returning that value.
 * <p>
 * It is possible to replace this converter with something that has a big if/else statement that selects behavior based on the real type of the object being
 * converted (or possibly has a map of converters, and looks them up based on the class of the input object). However this is not part of the existing
 * ConvertUtils framework.
 *
 *
 * @since 1.3
 */
public final class StringConverter extends AbstractConverter<String> {

    /**
     * Constructs a <strong>java.lang.String</strong> <em>Converter</em> that throws a {@code ConversionException} if an error occurs.
     */
    public StringConverter() {
    }

    /**
     * Constructs a <strong>java.lang.String</strong> <em>Converter</em> that returns a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned if the value to be converted is missing or an error occurs converting the value.
     */
    public StringConverter(final String defaultValue) {
        super(defaultValue);
    }

    /**
     * Convert the specified input object into an output object of the specified type.
     *
     * @param <T>   Target type of the conversion.
     * @param type  Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     * @since 1.8.0
     */
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        // We have to support Object, too, because this class is sometimes
        // used for a standard to Object conversion
        if (String.class.equals(type) || Object.class.equals(type)) {
            return type.cast(value.toString());
        }
        throw conversionException(type, value);
    }

    /**
     * Gets the default type this {@code Converter} handles.
     *
     * @return The default type this {@code Converter} handles.
     * @since 1.8.0
     */
    @Override
    protected Class<String> getDefaultType() {
        return String.class;
    }

}
