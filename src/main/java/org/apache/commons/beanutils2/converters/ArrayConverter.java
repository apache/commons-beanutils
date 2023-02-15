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

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.Converter;

/**
 * Generic {@link Converter} implementation that handles conversion
 * to and from <b>array</b> objects.
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * {@code ConversionException} if a conversion error occurs.
 * <p>
 * The main features of this implementation are:
 * <ul>
 *     <li><b>Element Conversion</b> - delegates to a {@link Converter},
 *         appropriate for the type, to convert individual elements
 *         of the array. This leverages the power of existing converters
 *         without having to replicate their functionality for converting
 *         to the element type and removes the need to create a specific
 *         array type converters.</li>
 *     <li><b>Arrays or Collections</b> - can convert from either arrays or
 *         Collections to an array, limited only by the capability
 *         of the delegate {@link Converter}.</li>
 *     <li><b>Delimited Lists</b> - can Convert <b>to</b> and <b>from</b> a
 *         delimited list in String format.</li>
 *     <li><b>Conversion to String</b> - converts an array to a
 *         {@code String} in one of two ways: as a <i>delimited list</i>
 *         or by converting the first element in the array to a String - this
 *         is controlled by the {@link ArrayConverter#setOnlyFirstToString(boolean)}
 *         parameter.</li>
 *     <li><b>Multi Dimensional Arrays</b> - it is possible to convert a {@code String}
 *         to a multi-dimensional arrays, by embedding {@link ArrayConverter}
 *         within each other - see example below.</li>
 *     <li><b>Default Value</b>
 *         <ul>
 *             <li><b><i>No Default</i></b> - use the
 *                 {@link ArrayConverter#ArrayConverter(Class, Converter)}
 *                 constructor to create a converter which throws a
 *                 {@link ConversionException} if the value is missing or
 *                 invalid.</li>
 *             <li><b><i>Default values</i></b> - use the
 *                 {@link ArrayConverter#ArrayConverter(Class, Converter, int)}
 *                 constructor to create a converter which returns a <i>default
 *                 value</i>. The <i>defaultSize</i> parameter controls the
 *                 <i>default value</i> in the following way:
 *                 <ul>
 *                    <li><i>defaultSize &lt; 0</i> - default is {@code null}</li>
 *                    <li><i>defaultSize = 0</i> - default is an array of length zero</li>
 *                    <li><i>defaultSize &gt; 0</i> - default is an array with a
 *                           length specified by {@code defaultSize} (N.B. elements
 *                           in the array will be {@code null})</li>
 *                 </ul>
 *             </li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * <h2>Parsing Delimited Lists</h2>
 * This implementation can convert a delimited list in {@code String} format
 * into an array of the appropriate type. By default, it uses a comma as the delimiter
 * but the following methods can be used to configure parsing:
 * <ul>
 *     <li>{@code setDelimiter(char)} - allows the character used as
 *         the delimiter to be configured [default is a comma].</li>
 *     <li>{@code setAllowedChars(char[])} - adds additional characters
 *         (to the default alphabetic/numeric) to those considered to be
 *         valid token characters.
 * </ul>
 *
 * <h2>Multi Dimensional Arrays</h2>
 * It is possible to convert a {@code String} to multi-dimensional arrays by using
 * {@link ArrayConverter} as the element {@link Converter}
 * within another {@link ArrayConverter}.
 * <p>
 * For example, the following code demonstrates how to construct a {@link Converter}
 * to convert a delimited {@code String} into a two dimensional integer array:
 * </p>
 * <pre>
 *    // Construct an Integer Converter
 *    IntegerConverter integerConverter = new IntegerConverter();
 *
 *    // Construct an array Converter for an integer array (i.e. int[]) using
 *    // an IntegerConverter as the element converter.
 *    // N.B. Uses the default comma (i.e. ",") as the delimiter between individual numbers
 *    ArrayConverter arrayConverter = new ArrayConverter(int[].class, integerConverter);
 *
 *    // Construct a "Matrix" Converter which converts arrays of integer arrays using
 *    // the preceding ArrayConverter as the element Converter.
 *    // N.B. Uses a semicolon (i.e. ";") as the delimiter to separate the different sets of numbers.
 *    //      Also the delimiter used by the first ArrayConverter needs to be added to the
 *    //      "allowed characters" for this one.
 *    ArrayConverter matrixConverter = new ArrayConverter(int[][].class, arrayConverter);
 *    matrixConverter.setDelimiter(';');
 *    matrixConverter.setAllowedChars(new char[] {','});
 *
 *    // Do the Conversion
 *    String matrixString = "11,12,13 ; 21,22,23 ; 31,32,33 ; 41,42,43";
 *    int[][] result = (int[][])matrixConverter.convert(int[][].class, matrixString);
 * </pre>
 *
 * @param <C> The converter type.
 * @since 1.8.0
 */
public class ArrayConverter<C> extends AbstractConverter<C> {

    private final Class<C> defaultType;
    private final Converter elementConverter;
    private int defaultSize;
    private char delimiter = ',';
    private char[] allowedChars = {'.', '-'};
    private boolean onlyFirstToString = true;

    /**
     * Constructs an <b>array</b> {@code Converter} with the specified
     * <b>component</b> {@code Converter} that throws a
     * {@code ConversionException} if an error occurs.
     *
     * @param defaultType The default array type this
     *  {@code Converter} handles
     * @param elementConverter Converter used to convert
     *  individual array elements.
     */
    public ArrayConverter(final Class<C> defaultType, final Converter elementConverter) {
        if (defaultType == null) {
            throw new IllegalArgumentException("Default type is missing");
        }
        if (!defaultType.isArray()) {
            throw new IllegalArgumentException("Default type must be an array.");
        }
        if (elementConverter == null) {
            throw new IllegalArgumentException("Component Converter is missing.");
        }
        this.defaultType = defaultType;
        this.elementConverter = elementConverter;
    }

    /**
     * Constructs an <b>array</b> {@code Converter} with the specified
     * <b>component</b> {@code Converter} that returns a default
     * array of the specified size (or {@code null}) if an error occurs.
     *
     * @param defaultType The default array type this
     *  {@code Converter} handles
     * @param elementConverter Converter used to convert
     *  individual array elements.
     * @param defaultSize Specifies the size of the default array value or if less
     *  than zero indicates that a {@code null} default value should be used.
     */
    public ArrayConverter(final Class<C> defaultType, final Converter elementConverter, final int defaultSize) {
        this(defaultType, elementConverter);
        this.defaultSize = defaultSize;
        C defaultValue = null;
        if (defaultSize >= 0) {
            defaultValue = (C) Array.newInstance(defaultType.getComponentType(), defaultSize);
        }
        setDefaultValue(defaultValue);
    }

    /**
     * Returns the value unchanged.
     *
     * @param value The value to convert
     * @return The value unchanged
     */
    @Override
    protected Object convertArray(final Object value) {
        return value;
    }

    /**
     * <p>
     * Converts non-array values to a Collection prior
     * to being converted either to an array or a String.
     * <ul>
     *   <li>{@link Collection} values are returned unchanged</li>
     *   <li>{@link Number}, {@link Boolean}  and {@link java.util.Date}
     *       values returned as a the only element in a List.</li>
     *   <li>All other types are converted to a String and parsed
     *       as a delimited list.</li>
     * </ul>
     *
     * <strong>N.B.</strong> The method is called by both the
     * {@link ArrayConverter#convertToType(Class, Object)} and
     * {@link ArrayConverter#convertToString(Object)} methods for
     * <i>non-array</i> types.
     * @param value value to be converted
     *
     * @return Collection elements.
     */
    protected Collection<?> convertToCollection(final Object value) {
        if (value instanceof Collection) {
            return (Collection<?>) value;
        }
        if (value instanceof Number ||
            value instanceof Boolean ||
            value instanceof java.util.Date) {
            final List<Object> list = new ArrayList<>(1);
            list.add(value);
            return list;
        }

        return parseElements(value.toString());
    }

    /**
     * Handles conversion to a String.
     *
     * @param value The value to be converted.
     * @return the converted String value.
     * @throws IllegalArgumentException if an error occurs converting to a String
     */
    @Override
    protected String convertToString(final Object value) {
        int size = 0;
        Iterator<?> iterator = null;
        final Class<?> type = value.getClass();
        if (type.isArray()) {
            size = Array.getLength(value);
        } else {
            final Collection<?> collection = convertToCollection(value);
            size = collection.size();
            iterator = collection.iterator();
        }

        if (size == 0) {
            return (String) getDefault(String.class);
        }

        if (onlyFirstToString) {
            size = 1;
        }

        // Create a StringBuffer containing a delimited list of the values
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                buffer.append(delimiter);
            }
            Object element = iterator == null ? Array.get(value, i) : iterator.next();
            element = elementConverter.convert(String.class, element);
            if (element != null) {
                buffer.append(element);
            }
        }

        return buffer.toString();
    }

    /**
     * Handles conversion to an array of the specified type.
     *
     * @param <T> Target type of the conversion.
     * @param type The type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     */
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (!type.isArray()) {
            throw ConversionException.format("%s cannot handle conversion to '%s' (not an array).", toString(getClass()), toString(type));
        }

        // Handle the source
        int size = 0;
        Iterator<?> iterator = null;
        if (value.getClass().isArray()) {
            size = Array.getLength(value);
        } else {
            final Collection<?> collection = convertToCollection(value);
            size = collection.size();
            iterator = collection.iterator();
        }

        // Allocate a new Array
        final Class<?> componentType = type.getComponentType();
        final Object newArray = Array.newInstance(componentType, size);

        // Convert and set each element in the new Array
        for (int i = 0; i < size; i++) {
            Object element = iterator == null ? Array.get(value, i) : iterator.next();
            // TODO - probably should catch conversion errors and throw
            //        new exception providing better info back to the user
            element = elementConverter.convert(componentType, element);
            Array.set(newArray, i, element);
        }

        @SuppressWarnings("unchecked")
        final
        // This is safe because T is an array type and newArray is an array of
        // T's component type
        T result = (T) newArray;
        return result;
    }

    /**
     * Gets the default value for conversions to the specified
     * type.
     * @param type Data type to which this value should be converted.
     * @return The default value for the specified type.
     */
    @Override
    protected Object getDefault(final Class<?> type) {
        if (type.equals(String.class)) {
            return null;
        }

        final Object defaultValue = super.getDefault(type);
        if (defaultValue == null) {
            return null;
        }

        if (defaultValue.getClass().equals(type)) {
            return defaultValue;
        }
        return Array.newInstance(type.getComponentType(), defaultSize);
    }

    /**
     * Gets the default type this {@code Converter} handles.
     *
     * @return The default type this {@code Converter} handles.
     */
    @Override
    protected Class<C> getDefaultType() {
        return defaultType;
    }

    /**
     * <p>Parse an incoming String of the form similar to an array initializer
     * in the Java language into a {@code List} individual Strings
     * for each element, according to the following rules.</p>
     * <ul>
     * <li>The string is expected to be a comma-separated list of values.</li>
     * <li>The string may optionally have matching '{' and '}' delimiters
     *   around the list.</li>
     * <li>Whitespace before and after each element is stripped.</li>
     * <li>Elements in the list may be delimited by single or double quotes.
     *  Within a quoted elements, the normal Java escape sequences are valid.</li>
     * </ul>
     * @param value String value to be parsed
     *
     * @return List of parsed elements.
     *
     * @throws ConversionException if the syntax of {@code value}
     *  is not syntactically valid
     * @throws NullPointerException if {@code value}
     *  is {@code null}
     */
    private List<String> parseElements(String value) {
        if (log().isDebugEnabled()) {
            log().debug("Parsing elements, delimiter=[" + delimiter + "], value=[" + value + "]");
        }

        // Trim any matching '{' and '}' delimiters
        value = toTrim(value);
        if (value.startsWith("{") && value.endsWith("}")) {
            value = value.substring(1, value.length() - 1);
        }

        final String typeName = toString(String.class);
        try {

            // Set up a StreamTokenizer on the characters in this String
            final StreamTokenizer st = new StreamTokenizer(new StringReader(value));
            st.whitespaceChars(delimiter , delimiter); // Set the delimiters
            st.ordinaryChars('0', '9');  // Needed to turn off numeric flag
            st.wordChars('0', '9');      // Needed to make part of tokens
            for (final char allowedChar : allowedChars) {
                st.ordinaryChars(allowedChar, allowedChar);
                st.wordChars(allowedChar, allowedChar);
            }

            // Split comma-delimited tokens into a List
            List<String> list = null;
            while (true) {
                final int ttype = st.nextToken();
                if (ttype == StreamTokenizer.TT_WORD || ttype > 0) {
                    if (st.sval != null) {
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.add(st.sval);
                    }
                } else if (ttype == StreamTokenizer.TT_EOF) {
                    break;
                } else {
                    throw ConversionException.format("Encountered token of type %s parsing elements to '%s'.", ttype, typeName);
                }
            }

            if (list == null) {
                list = Collections.emptyList();
            }
            if (log().isDebugEnabled()) {
                log().debug(list.size() + " elements parsed");
            }

            // Return the completed list
            return list;

        } catch (final IOException e) {
            throw new ConversionException("Error converting from String to '"
                    + typeName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Sets the allowed characters to be used for parsing a delimited String.
     *
     * @param allowedChars Characters which are to be considered as part of
     * the tokens when parsing a delimited String [default is '.' and '-']
     */
    public void setAllowedChars(final char[] allowedChars) {
        this.allowedChars = Objects.requireNonNull(allowedChars, "allowedChars").clone();
    }

    /**
     * Sets the delimiter to be used for parsing a delimited String.
     *
     * @param delimiter The delimiter [default ',']
     */
    public void setDelimiter(final char delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Indicates whether converting to a String should create
     * a delimited list or just convert the first value.
     *
     * @param onlyFirstToString {@code true} converts only
     * the first value in the array to a String, {@code false}
     * converts all values in the array into a delimited list (default
     * is {@code true}
     */
    public void setOnlyFirstToString(final boolean onlyFirstToString) {
        this.onlyFirstToString = onlyFirstToString;
    }

    /**
     * Provide a String representation of this array converter.
     *
     * @return A String representation of this array converter
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(toString(getClass()));
        buffer.append("[UseDefault=");
        buffer.append(isUseDefault());
        buffer.append(", ");
        buffer.append(elementConverter.toString());
        buffer.append(']');
        return buffer.toString();
    }

}
