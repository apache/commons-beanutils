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
package org.apache.commons.beanutils.converters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * Base {@link Converter} implementation that provides the structure
 * for handling conversion <b>to</b> and <b>from</b> a specified type.
 * <p>
 * This implementation provides the basic structure for
 * converting to/from a specified type optionally using a default
 * value or throwing a {@link ConversionException} if a
 * conversion error occurs.
 * <p>
 * Implementations should provide conversion to the specified
 * type and from the specified type to a <code>String</code> value
 * by implementing the following methods:
 * <ul>
 *     <li><code>convertToString(value)</code> - convert to a String
 *        (default implementation uses the objects <code>toString()</code>
 *        method).</li>
 *     <li><code>convertToType(Class, value)</code> - convert
 *         to the specified type</li>
 * </ul>
 *
 * @version $Revision$ $Date$
 * @since 1.8.0
 */
public abstract class AbstractConverter implements Converter {

    /** Debug logging message to indicate default value configuration */
    private static final String DEFAULT_CONFIG_MSG =
        "(N.B. Converters can be configured to use default values to avoid throwing exceptions)";

    /** Current package name */
    //    getPackage() below returns null on some platforms/jvm versions during the unit tests.
//    private static final String PACKAGE = AbstractConverter.class.getPackage().getName() + ".";
    private static final String PACKAGE = "org.apache.commons.beanutils.converters.";

    /**
     * Logging for this instance.
     */
    private Log log = LogFactory.getLog(getClass());

    /**
     * The default type this <code>Converter</code> handles.
     */
    private Class defaultType = null;

    /**
     * Should we return the default value on conversion errors?
     */
    private boolean useDefault = false;

    /**
     * The default value specified to our Constructor, if any.
     */
    private Object defaultValue = null;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a <i>Converter</i> that throws a
     * <code>ConversionException</code> if an error occurs.
     *
     * @param defaultType The default type this <code>Converter</code>
     * handles
     */
    public AbstractConverter(Class defaultType) {
        this.defaultType = defaultType;
        if (defaultType == null) {
            throw new IllegalArgumentException("Default type is missing.");
        }
    }

    /**
     * Construct a <i>Converter</i> that returns a default
     * value if an error occurs.
     *
     * @param defaultType The default type this <code>Converter</code>
     * handles
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public AbstractConverter(Class defaultType, Object defaultValue) {
        this(defaultType);
        setDefaultValue(defaultValue);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Set the default value, converting as required.
     * <p>
     * If the default value is different from the type the
     * <code>Converter</code> handles, it will be converted
     * to the handled type.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     * @throws ConversionException if an error occurs converting
     * the default value
     */
    public void setDefaultValue(Object defaultValue) {
        useDefault = false;
        if (log.isDebugEnabled()) {
            log.debug("Setting default value: " + defaultValue);
        }
        if (defaultValue == null) {
           this.defaultValue  = null;
        } else {
           this.defaultValue  = convert(getDefaultType(), defaultValue);
        }
        useDefault = true;
    }

    /**
     * Convert the input object into an output object of the
     * specified type.
     *
     * @param type Data type to which this value should be converted
     * @param value The input value to be converted
     * @return The converted value.
     * @throws ConversionException if conversion cannot be performed
     * successfully and no default is specified.
     */
    public Object convert(Class type, Object value) {

        Class sourceType  = value == null ? null : value.getClass();
        Class targetType  = primitive(type  == null ? getDefaultType() : type);

        if (log.isDebugEnabled()) {
            log.debug("Converting"
                    + (value == null ? "" : " '" + toString(sourceType) + "'")
                    + " value '" + value + "' to type '" + toString(targetType) + "'");
        }

        // Missing Value
        if (value == null) {
            return handleMissing(targetType);

        // Convert --> String
        } else if (targetType.equals(String.class)) {
            return convertToString(value);

        // No conversion necessary
        } else if (targetType.equals(sourceType)) {
            if (log.isDebugEnabled()) {
                log.debug("    No conversion required, value is already a "
                                + toString(targetType));
            }
            return value;

        // Convert --> Type
        } else {
            try {
                Object result = convertToType(targetType, value);
                if (log.isDebugEnabled()) {
                    log.debug("    Converted to " + toString(targetType) +
                                   " value '" + result + "'");
                }
                return result;
            } catch (Exception ex) {
                return handleError(targetType, value, ex);
            }
        }

    }

    /**
     * Convert the input object into a String.
     * <p>
     * <b>N.B.</b>This implementation simply uses the value's
     * <code>toString()</code> method and should be overriden if a
     * more sophisticated mechanism for <i>conversion to a String</i>
     * is required.
     *
     * @param value The input value to be converted.
     * @return the converted String value.
     */
    protected String convertToString(Object value) {
        return value.toString();
    }

    /**
     * Convert the input object into an output object of the
     * specified type.
     * <p>
     * Typical implementations will provide a minimum of
     * <code>String --> type</code> conversion.
     *
     * @param type Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Exception if an error occurs converting to the specified type
     */
    protected abstract Object convertToType(Class type, Object value) throws Exception;

    /**
     * Handle Conversion Errors.
     * <p>
     * If a default value has been specified then it is returned
     * otherwise a ConversionException is thrown.
     *
     * @param type Data type to which this value should be converted.
     * @param value The input value to be converted
     * @param ex The exception thrown by the <code>convert</code> method
     * @return The default value.
     * @throws ConversionException if no default value has been
     * specified for this {@link Converter}.
     */
    protected Object handleError(Class type, Object value, Exception ex) {
        if (log.isDebugEnabled()) {
            if (ex instanceof ConversionException) {
                log.debug("    Conversion threw ConversionException: " + ex.getMessage());
            } else {
                log.debug("    Conversion threw " + ex);
            }
        }

        if (useDefault) {
            return handleMissing(type);
        }

        ConversionException cex = null;
        if (ex instanceof ConversionException) {
            cex = (ConversionException)ex;
            if (log.isDebugEnabled()) {
                log.debug("    Re-throwing ConversionException: " + cex.getMessage());
                log.debug("    " + DEFAULT_CONFIG_MSG);
            }
        } else {
            String msg = "Error converting from '" + toString(value.getClass()) +
                    "' to '" + toString(type) + "' " + ex.getMessage();
            cex = new ConversionException(msg, ex);
            if (log.isDebugEnabled()) {
                log.debug("    Throwing ConversionException: " + msg);
                log.debug("    " + DEFAULT_CONFIG_MSG);
            }
        }

        throw cex;

    }

    /**
     * Handle missing values.
     * <p>
     * If a default value has been specified then it is returned
     * otherwise a ConversionException is thrown.
     *
     * @param type Data type to which this value should be converted.
     * @return The default value.
     * @throws ConversionException if no default value has been
     * specified for this {@link Converter}.
     */
    protected Object handleMissing(Class type) {

        if (useDefault || type.equals(String.class)) {
            Object value = getDefault(type);
            if (useDefault && value != null && !(type.equals(value.getClass()))) {
                try {
                    value = convertToType(type, defaultValue);
                } catch (Exception e) {
                    // default conversion shouldn't fail
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("    Using default "
                        + (value == null ? "" : toString(value.getClass()) + " ")
                        + "value '" + defaultValue + "'");
            }
            return value;
        }

        ConversionException cex =  new ConversionException("No value specified for '" +
                toString(type) + "'");
        if (log.isDebugEnabled()) {
            log.debug("    Throwing ConversionException: " + cex.getMessage());
            log.debug("    " + DEFAULT_CONFIG_MSG);
        }
        throw cex;

    }

    /**
     * Return the default type this <code>Converter</code> handles.
     *
     * @return The default type this <code>Converter</code> handles.
     */
    protected Class getDefaultType() {
        return defaultType;
    }

    /**
     * Return the default value for conversions to the specified
     * type.
     * @param type Data type to which this value should be converted.
     * @return The default value for the specified type.
     */
    protected Object getDefault(Class type) {
        if (type.equals(String.class)) {
            return null;
        } else {
            return defaultValue;
        }
    }

    // ----------------------------------------------------------- Package Methods

    /**
     * Accessor method for Log instance.
     * <p>
     * The Log instance variable is transient and
     * accessing it through this method ensures it
     * is re-initialized when this instance is
     * de-serialized.
     *
     * @return The Log instance.
     */
    Log log() {
        return log;
    }

    /**
     * Change primitve Class types to the associated wrapper class.
     * @param type The class type to check.
     * @return The converted type.
     */
     Class primitive(Class type) {
        if (type == null || !type.isPrimitive()) {
            return type;
        }

        if (type == Integer.TYPE) {
            return Integer.class;
        } else if (type == Double.TYPE) {
            return Double.class;
        } else if (type == Long.TYPE) {
            return Long.class;
        } else if (type == Boolean.TYPE) {
            return Boolean.class;
        } else if (type == Float.TYPE) {
            return Float.class;
        } else if (type == Short.TYPE) {
            return Short.class;
        } else if (type == Byte.TYPE) {
            return Byte.class;
        } else if (type == Character.TYPE) {
            return Character.class;
        } else {
            return type;
        }
    }

    /**
     * Provide a String representation of a <code>java.lang.Class</code>.
     * @param type The <code>java.lang.Class</code>.
     * @return The String representation.
     */
    String toString(Class type) {
        String typeName = null;
        if (type == null) {
            typeName = "null";
        } else if (type.isArray()) {
            Class elementType = type.getComponentType();
            int count = 1;
            while (elementType.isArray()) {
                elementType = elementType .getComponentType();
                count++;
            }
            typeName = elementType.getName();
            for (int i = 0; i < count; i++) {
                typeName += "[]";
            }
        } else {
            typeName = type.getName();
        }
        if (typeName.startsWith("java.lang.")) {
            typeName = typeName.substring("java.lang.".length());
        } else if (typeName.startsWith(PACKAGE)) {
            typeName = typeName.substring(PACKAGE.length());
        }
        return typeName;
    }
}
