/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;


/**
 * <p>Standard {@link Converter} implementation that converts an incoming
 * String into a <code>java.lang.Boolean</code> object, optionally using a
 * default value or throwing a {@link ConversionException} if a conversion
 * error occurs.</p>
 *
 * <p>By default any object whose string representation is one of the values
 * {"yes", "y", "true", "on", "1"} is converted to Boolean.TRUE, and 
 * string representations {"no", "n", "false", "off", "0"} are converted
 * to Boolean.FALSE. The recognised true/false strings can be changed by:
 * <pre>
 *  String[] trueStrings = {"oui", "o", "1"};
 *  String[] falseStrings = {"non", "n", "0"};
 *  Converter bc = new BooleanConverter(trueStrings, falseStrings);
 *  ConvertUtils.register(bc, Boolean.class);
 *  ConvertUtils.register(bc, Boolean.TYPE);
 * </pre>
 * In addition, it is recommended that the BooleanArrayConverter also be
 * modified to recognise the same set of values:
 * <pre>
 *   Converter bac = new BooleanArrayConverter(bc, BooleanArrayConverter.NO_DEFAULT);
 *   ConvertUtils.register(bac, bac.MODEL);
 * </pre>
 * </p>
 * 
 * <p>Case is ignored when converting values to true or false.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 * @since 1.3
 */

public final class BooleanConverter implements Converter {


    // ----------------------------------------------------------- Constructors


    /**
     * Create a {@link Converter} that will throw a {@link ConversionException}
     * if a conversion error occurs, ie the string value being converted is
     * not one of the known true strings, nor one of the known false strings.
     */
    public BooleanConverter() {

        this.useDefault = false;

    }


    /**
     * Create a {@link Converter} that will return the specified default value
     * if a conversion error occurs, ie the string value being converted is
     * not one of the known true strings, nor one of the known false strings.
     *
     * @param defaultValue The default value to be returned if the value
     *  being converted is not recognised. This value may be null, in which
     *  case null will be returned on conversion failure. When non-null, it is
     *  expected that this value will be either Boolean.TRUE or Boolean.FALSE.
     *  The special value BooleanConverter.NO_DEFAULT can also be passed here,
     *  in which case this constructor acts like the no-argument one.
     */
    public BooleanConverter(Object defaultValue) {

        if (defaultValue == NO_DEFAULT) {
            this.useDefault = false;
        } else {
            this.defaultValue = defaultValue;
            this.useDefault = true;
        }

    }

    /**
     * Create a {@link Converter} that will return the specified default value
     * if a conversion error occurs.
     * <p>
     * The provided string arrays are copied, so that changes to the elements
     * of the array after this call is made do not affect this object.
     *
     * @param trueStrings is the set of strings which should convert to the
     *  value Boolean.TRUE. The value null must not be present. Case is
     *  ignored.
     *
     * @param falseStrings is the set of strings which should convert to the
     *  value Boolean.TRUE. The value null must not be present. Case is
     *  ignored.
     *
     * @param defaultValue The default value to be returned if the value
     *  being converted is not recognised. This value may be null, in which
     *  case null will be returned on conversion failure. When non-null, it is
     *  expected that this value will be either Boolean.TRUE or Boolean.FALSE.
     *  The special value BooleanConverter.NO_DEFAULT can also be passed here,
     *  in which case an exception will be thrown on conversion failure.
     */
    public BooleanConverter(String[] trueStrings, String[] falseStrings, 
                Object defaultValue) {

        this.trueStrings = copyStrings(trueStrings);
        this.falseStrings = copyStrings(falseStrings);
        
        if (defaultValue == NO_DEFAULT) {
            this.useDefault = false;
        } else {
            this.defaultValue = defaultValue;
            this.useDefault = true;
        }

    }


    // ----------------------------------------------------- Static Variables


    /**
     * This is a special reference that can be passed as the "default object"
     * to the constructor to indicate that no default is desired. Note that
     * the value 'null' cannot be used for this purpose, as the caller may
     * want a null to be returned as the default.
     */
    public static final Object NO_DEFAULT = new Object();


    // ----------------------------------------------------- Instance Variables


    /**
     * The default value specified to our Constructor, if any.
     */
    private Object defaultValue;

    /**
     * Should we return the default value on conversion errors?
     */
    private boolean useDefault;

    /**
     * The set of strings that are known to map to Boolean.TRUE.
     */
    private String[] trueStrings = {"yes", "y", "true", "on", "1"};

    /**
     * The set of strings that are known to map to Boolean.FALSE.
     */
    private String[] falseStrings = {"no", "n", "false", "off", "0"};

    // --------------------------------------------------------- Public Methods

    /**
     * Convert the specified input object into an output object of the
     * specified type.
     *
     * @param type is the type to which this value should be converted. In the
     *  case of this BooleanConverter class, this value is ignored.
     *
     * @param value is the input value to be converted. The toString method
     *  shall be invoked on this object, and the result compared (ignoring
     *  case) against the known "true" and "false" string values.
     *
     * @return Boolean.TRUE if the value was a recognised "true" value, 
     *  Boolean.FALSE if the value was a recognised "false" value, or
     *  the default value if the value was not recognised and the constructor
     *  was provided with a default value.
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully and the constructor was not provided with a default
     *  value to return on conversion failure.
     */
    public Object convert(Class type, Object value) {

        if (value == null) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException("No value specified");
            }
        }

        if (value instanceof Boolean) {
            return (value);
        }

        // All the values in the trueStrings and falseStrings arrays are
        // guaranteed to be lower-case. By converting the input value
        // to lowercase too, we can use the efficient String.equals method
        // instead of the less-efficient String.equalsIgnoreCase method.
        String stringValue = value.toString().toLowerCase();

        for(int i=0; i<trueStrings.length; ++i) {
            if (trueStrings[i].equals(stringValue))
                return Boolean.TRUE;
        }

        for(int i=0; i<falseStrings.length; ++i) {
            if (falseStrings[i].equals(stringValue))
                return Boolean.FALSE;
        }
        
        if (useDefault) {
            return defaultValue;
        }

        throw new ConversionException(value.toString());
    }

    /**
     * This method creates a copy of the provided array, and ensures that
     * all the strings in the newly created array contain only lower-case
     * letters.
     * <p>
     * Using this method to copy string arrays means that changes to the
     * src array do not modify the dst array.
     */
    private static String[] copyStrings(String[] src) {
        String[] dst = new String[src.length];
        for(int i=0; i<src.length; ++i) {
            dst[i] = src[i].toLowerCase();
        }
        return dst;
    }
}
