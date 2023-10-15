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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.beanutils2.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base {@link Converter} implementation that provides the structure
 * for handling conversion <b>to</b> and <b>from</b> a specified type.
 * <p>
 * This implementation provides the basic structure for
 * converting to/from a specified type optionally using a default
 * value or throwing a {@link ConversionException} if a
 * conversion error occurs.
 * </p>
 * <p>
 * Implementations should provide conversion to the specified
 * type and from the specified type to a {@code String} value
 * by implementing the following methods:
 * </p>
 * <ul>
 *     <li>{@code convertToString(value)} - convert to a String
 *        (default implementation uses the objects {@code toString()}
 *        method).</li>
 *     <li>{@code convertToType(Class, value)} - convert
 *         to the specified type</li>
 * </ul>
 * <p>
 * The default value has to be compliant to the default type of this
 * converter - which is enforced by the generic type parameter. If a
 * conversion is not possible and a default value is set, the converter
 * tries to transform the default value to the requested target type.
 * If this fails, a {@code ConversionException} if thrown.
 * </p>
 *
 * @param <D> The default value type.
 * @since 1.8.0
 */
public abstract class AbstractConverter<D> implements Converter<D> {

    /** Debug logging message to indicate default value configuration */
    private static final String DEFAULT_CONFIG_MSG =
        "(N.B. Converters can be configured to use default values to avoid throwing exceptions)";

    /** Current package name */
    //    getPackage() below returns null on some platforms/jvm versions during the unit tests.
    // private static final String PACKAGE = AbstractConverter.class.getPackage().getName() + ".";
    private static final String PACKAGE = "org.apache.commons.beanutils2.converters.";

    /**
     * Converts the given object to a lower-case string.
     *
     * @param value the input string.
     * @return the given string trimmed and converter to lower-case.
     */
    protected static String toLowerCase(final Object value) {
        return toString(value).toLowerCase(Locale.ROOT);
    }

    /**
     * Converts the given object to a lower-case string.
     *
     * @param value the input string.
     * @return the given string trimmed and converter to lower-case.
     */
    protected static String toString(final Object value) {
        return Objects.requireNonNull(value, "value").toString();
    }

    /**
     * Converts the given object to a lower-case string.
     *
     * @param value the input string.
     * @return the given string trimmed and converter to lower-case.
     */
    protected static String toTrim(final Object value) {
        return toString(value).trim();
    }

    /**
     * Logging for this instance.
     */
    private transient Log log;

    /**
     * Should we return the default value on conversion errors?
     */
    private boolean useDefault;

    /**
     * The default value specified to our Constructor, if any.
     */
    private D defaultValue;

    /**
     * Constructs a <i>Converter</i> that throws a
     * {@code ConversionException} if an error occurs.
     */
    public AbstractConverter() {
    }

    /**
     * Constructs a <i>Converter</i> that returns a default
     * value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public AbstractConverter(final D defaultValue) {
        setDefaultValue(defaultValue);
    }

    /**
     * Creates a standard conversion exception with a message indicating that
     * the passed in value cannot be converted to the desired target type.
     *
     * @param type the target type
     * @param value the value to be converted
     * @return a {@code ConversionException} with a standard message
     * @since 1.9
     */
    protected ConversionException conversionException(final Class<?> type, final Object value) {
        return ConversionException.format("Can't convert value '%s' to type %s", value, type);
    }

    /**
     * Converts the input object into an output object of the
     * specified type.
     *
     * @param type Data type to which this value should be converted
     * @param value The input value to be converted
     * @return The converted value.
     * @throws ConversionException if conversion cannot be performed
     * successfully and no default is specified.
     */
    @Override
    public <R> R convert(final Class<R> type, Object value) {
        if (type == null) {
            return convertToDefaultType(value);
        }

        Class<?> sourceType  = value == null ? null : value.getClass();
        final Class<R> targetType  = ConvertUtils.primitiveToWrapper(type);

        if (log().isDebugEnabled()) {
            log().debug("Converting"
                    + (value == null ? "" : " '" + toString(sourceType) + "'")
                    + " value '" + value + "' to type '" + toString(targetType) + "'");
        }

        value = convertArray(value);

        // Missing Value
        if (value == null) {
            return handleMissing(targetType);
        }

        sourceType = value.getClass();

        try {
            // Convert --> String
            if (targetType.equals(String.class)) {
                return targetType.cast(convertToString(value));

            // No conversion necessary
            }
            if (targetType.equals(sourceType)) {
                if (log().isDebugEnabled()) {
                    log().debug("    No conversion required, value is already a "
                                    + toString(targetType));
                }
                return targetType.cast(value);

            // Convert --> Type
            }
            final Object result = convertToType(targetType, value);
            if (log().isDebugEnabled()) {
                log().debug("    Converted to " + toString(targetType) +
                               " value '" + result + "'");
            }
            return targetType.cast(result);
        } catch (final Throwable t) {
            return handleError(targetType, value, t);
        }
    }

    /**
     * Returns the first element from an Array (or Collection)
     * or the value unchanged if not an Array (or Collection).
     *
     * N.B. This needs to be overridden for array/Collection converters.
     *
     * @param value The value to convert
     * @return The first element in an Array (or Collection)
     * or the value unchanged if not an Array (or Collection)
     */
    protected Object convertArray(final Object value) {
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            if (Array.getLength(value) > 0) {
                return Array.get(value, 0);
            }
            return null;
        }
        if (value instanceof Collection) {
            final Collection<?> collection = (Collection<?>)value;
            if (!collection.isEmpty()) {
                return collection.iterator().next();
            }
            return null;
        }
        return value;
    }

    /**
     * Converts to the default type. This method is called if we do
     * not have a target class. In this case, the T parameter is not set.
     * Therefore, we can cast to it (which is required to fulfill the contract
     * of the method signature).
     * @param value the value to be converted
     *
     * @param <T> the type of the result object
     * @return the converted value
     */
    @SuppressWarnings("unchecked")
    private <T> T convertToDefaultType(final Object value) {
        return (T) convert(getDefaultType(), value);
    }

    /**
     * Converts the input object into a String.
     * <p>
     * <b>N.B.</b>This implementation simply uses the value's
     * {@code toString()} method and should be overridden if a
     * more sophisticated mechanism for <i>conversion to a String</i>
     * is required.
     * </p>
     *
     * @param value The input value to be converted.
     * @return the converted String value.
     * @throws IllegalArgumentException if an error occurs converting to a String
     */
    protected String convertToString(final Object value) {
        return value.toString();
    }

    /**
     * Converts the input object into an output object of the
     * specified type.
     * <p>
     * Typical implementations will provide a minimum of
     * {@code String --&gt; type} conversion.
     * </p>
     *
     * @param <R> Target type of the conversion.
     * @param type Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     */
    protected abstract <R> R convertToType(Class<R> type, Object value) throws Throwable;

    /**
     * Gets the default value for conversions to the specified
     * type.
     * @param type Data type to which this value should be converted.
     * @return The default value for the specified type.
     */
    protected Object getDefault(final Class<?> type) {
        if (type.equals(String.class)) {
            return null;
        }
        return defaultValue;
    }

    /**
     * Gets the default type this {@code Converter} handles.
     *
     * @return The default type this {@code Converter} handles.
     */
    protected abstract Class<D> getDefaultType();

    /**
     * Handles Conversion Errors.
     * <p>
     * If a default value has been specified then it is returned
     * otherwise a ConversionException is thrown.
     * </p>
     *
     * @param <T> Target type of the conversion.
     * @param type Data type to which this value should be converted.
     * @param value The input value to be converted
     * @param cause The exception thrown by the {@code convert} method
     * @return The default value.
     * @throws ConversionException if no default value has been
     * specified for this {@link Converter}.
     */
    protected <T> T handleError(final Class<T> type, final Object value, final Throwable cause) {
        if (log().isDebugEnabled()) {
            if (cause instanceof ConversionException) {
                log().debug("    Conversion threw ConversionException: " + cause.getMessage());
            } else {
                log().debug("    Conversion threw " + cause);
            }
        }

        if (useDefault) {
            return handleMissing(type);
        }

        ConversionException cex = null;
        if (cause instanceof ConversionException) {
            cex = (ConversionException)cause;
            if (log().isDebugEnabled()) {
                log().debug("    Re-throwing ConversionException: " + cex.getMessage());
                log().debug("    " + DEFAULT_CONFIG_MSG);
            }
        } else {
            final String msg = "Error converting from '" + toString(value.getClass()) +
                    "' to '" + toString(type) + "' " + cause.getMessage();
            cex = new ConversionException(msg, cause);
            if (log().isDebugEnabled()) {
                log().debug("    Throwing ConversionException: " + msg);
                log().debug("    " + DEFAULT_CONFIG_MSG);
            }
            BeanUtils.initCause(cex, cause);
        }

        throw cex;

    }

    /**
     * Handles missing values.
     * <p>
     * If a default value has been specified, then it is returned (after a cast
     * to the desired target class); otherwise a ConversionException is thrown.
     * </p>
     *
     * @param <T> the desired target type
     * @param type Data type to which this value should be converted.
     * @return The default value.
     * @throws ConversionException if no default value has been
     * specified for this {@link Converter}.
     */
    protected <T> T handleMissing(final Class<T> type) {
        if (useDefault || type.equals(String.class)) {
            Object value = getDefault(type);
            if (useDefault && value != null && !type.equals(value.getClass())) {
                try {
                    value = convertToType(type, defaultValue);
                } catch (final Throwable t) {
                    throw new ConversionException("Default conversion to " + toString(type)
                            + " failed.", t);
                }
            }
            if (log().isDebugEnabled()) {
                log().debug("    Using default "
                        + (value == null ? "" : toString(value.getClass()) + " ")
                        + "value '" + defaultValue + "'");
            }
            // value is now either null or of the desired target type
            return type.cast(value);
        }

        final ConversionException cex = ConversionException.format("No value specified for '%s'", toString(type));
        if (log().isDebugEnabled()) {
            log().debug("    Throwing ConversionException: " + cex.getMessage());
            log().debug("    " + DEFAULT_CONFIG_MSG);
        }
        throw cex;
    }

    /**
     * Tests whether a default value will be returned or exception
     * thrown in the event of a conversion error.
     *
     * @return {@code true} if a default value will be returned for
     * conversion errors or {@code false} if a {@link ConversionException}
     * will be thrown.
     */
    public boolean isUseDefault() {
        return useDefault;
    }

    /**
     * Gets the Log instance.
     * <p>
     * The Log instance variable is transient and accessing it through this method ensures it is re-initialized when this instance is de-serialized.
     * </p>
     *
     * @return The Log instance.
     */
    Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }

    /**
     * Sets the default value, converting as required.
     * <p>
     * If the default value is different from the type the
     * {@code Converter} handles, it will be converted
     * to the handled type.
     * </p>
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     * @throws ConversionException if an error occurs converting
     * the default value
     */
    protected void setDefaultValue(final D defaultValue) {
        useDefault = false;
        if (log().isDebugEnabled()) {
            log().debug("Setting default value: " + defaultValue);
        }
        if (defaultValue == null) {
           this.defaultValue = null;
        } else {
           this.defaultValue = convert(getDefaultType(), defaultValue);
        }
        useDefault = true;
    }

    /**
     * Converts this instance to a String.
     *
     * @return A String representation of this converter
     */
    @Override
    public String toString() {
        return toString(getClass()) + "[UseDefault=" + useDefault + "]";
    }

    /**
     * Converts a {@code java.lang.Class} to a String.
     *
     * @param type The {@code java.lang.Class}.
     * @return The String representation.
     */
    String toString(final Class<?> type) {
        String typeName = null;
        if (type == null) {
            typeName = "null";
        } else if (type.isArray()) {
            Class<?> elementType = type.getComponentType();
            int count = 1;
            while (elementType.isArray()) {
                elementType = elementType .getComponentType();
                count++;
            }
            final StringBuilder typeNameBuilder = new StringBuilder(elementType.getName());
            for (int i = 0; i < count; i++) {
                typeNameBuilder.append("[]");
            }
            typeName = typeNameBuilder.toString();
        } else {
            typeName = type.getName();
        }
        if (typeName.startsWith("java.lang.") ||
            typeName.startsWith("java.util.") ||
            typeName.startsWith("java.math.")) {
            typeName = typeName.substring("java.lang.".length());
        } else if (typeName.startsWith(PACKAGE)) {
            typeName = typeName.substring(PACKAGE.length());
        }
        return typeName;
    }

}
