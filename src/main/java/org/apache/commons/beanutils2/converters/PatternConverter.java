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

import java.util.regex.Pattern;

/**
 * Converts a regular expression into a Java {@link Pattern} object.
 *
 * @since 2.0.0
 */
public class PatternConverter extends AbstractConverter {

    /**
     * Construct a <b>{@link Pattern}</b> <i>Converter</i> that throws a {@code ConversionException} if an error occurs.
     */
    public PatternConverter() {
        super();
    }

    /**
     * Constructs a {@link org.apache.commons.beanutils2.Converter} that will return the specified default value if a
     * conversion error occurs.
     *
     * @param defaultValue The default value to be returned if the value to be converted is missing or an error occurs
     * converting the value.
     */
    public PatternConverter(final Object defaultValue) {
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
        return Pattern.class;
    }

    /**
     * Converts the specified input object into an output object of the specified type.
     *
     * @param value The String property value to convert.
     * @return A {@link Pattern} which represents the compiled configuration property.
     * @throws NullPointerException If the value is null.
     * @throws java.util.regex.PatternSyntaxException If the regular expression {@link String} provided is malformed.
     */
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (Pattern.class.isAssignableFrom(type)) {
            final String stringValue = toString(value);
            return type.cast(Pattern.compile(stringValue));
        }

        throw conversionException(type, value);
    }
}
