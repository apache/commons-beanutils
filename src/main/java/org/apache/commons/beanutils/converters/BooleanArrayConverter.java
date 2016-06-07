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


import java.util.List;
import org.apache.commons.beanutils.ConversionException;


/**
 * <p>Standard {@link org.apache.commons.beanutils.Converter} implementation that converts an incoming
 * String into a primitive array of boolean.  On a conversion failure, returns
 * a specified default value or throws a {@link ConversionException} depending
 * on how this instance is constructed.</p>
 *
 * <p>By default, the values to be converted are expected to be those
 * recognized by a default instance of BooleanConverter. A customised
 * BooleanConverter can be provided in order to recognise alternative values
 * as true/false. </p>
 *
 * @version $Id$
 * @since 1.4
 * @deprecated Replaced by the new {@link ArrayConverter} implementation
 */

@Deprecated
public final class BooleanArrayConverter extends AbstractArrayConverter {


    // ----------------------------------------------------------- Constructors


    /**
     * Create a {@link org.apache.commons.beanutils.Converter} that will throw
     * a {@link ConversionException} if a conversion error occurs.
     *
     * <p>Conversion of strings to boolean values will be done via a default
     * instance of class BooleanConverter.</p>
     */
    public BooleanArrayConverter() {

        super();
        this.booleanConverter = DEFAULT_CONVERTER;

    }


    /**
     * Create a {@link org.apache.commons.beanutils.Converter} that will return
     * the specified default value if a conversion error occurs.
     *
     * <p>Conversion of strings to boolean values will be done via a default
     * instance of class BooleanConverter.</p>
     *
     * @param defaultValue The default value to be returned
     */
    public BooleanArrayConverter(final Object defaultValue) {

        super(defaultValue);
        this.booleanConverter = DEFAULT_CONVERTER;

    }


    /**
     * Create a {@link org.apache.commons.beanutils.Converter} that will return
     * the specified default value if a conversion error occurs.
     *
     * <p>Conversion of strings to boolean values will be done via the
     * specified converter.</p>
     *
     * @param converter is the converter object that will be used to
     *  convert each input string-value into a boolean.
     *
     * @param defaultValue is the default value to be returned by method
     * convert if conversion fails; null is a valid default value. See the
     * documentation for method "convert" for more information.
     * The value BooleanArrayConverter.NO_DEFAULT may be passed here to
     * specify that an exception should be thrown on conversion failure.
     *
     */
    public BooleanArrayConverter(final BooleanConverter converter, final Object defaultValue) {

        super(defaultValue);
        this.booleanConverter = converter;

    }

    // ------------------------------------------------------- Static Variables

    /**
     * Type which this class converts its input to. This value can be
     * used as a parameter to the ConvertUtils.register method.
     * @since 1.8.0
     */
    public static final Class MODEL = new boolean[0].getClass();

    /**
     * The converter that all instances of this class will use to
     * do individual string->boolean conversions, unless overridden
     * in the constructor.
     */
    private static final BooleanConverter DEFAULT_CONVERTER
        = new BooleanConverter();

    // ---------------------------------------------------- Instance Variables

    /**
     * This object is used to perform the conversion of individual strings
     * into Boolean/boolean values.
     */
    protected final BooleanConverter booleanConverter;

    // --------------------------------------------------------- Public Methods


    /**
     * Convert the specified input object into an output object of type
     * array-of-boolean.
     *
     * <p>If the input value is null, then the default value specified in the
     * constructor is returned. If no such value was provided, then a
     * ConversionException is thrown instead.</p>
     *
     * <p>If the input value is of type String[] then the returned array shall
     * be of the same size as this array, with a true or false value in each
     * array element depending on the result of applying method
     * BooleanConverter.convert to each string.</p>
     *
     * <p>For all other types of value, the object's toString method is
     * expected to return a string containing a comma-separated list of
     * values, eg "true, false, true". See the documentation for
     * {@link AbstractArrayConverter#parseElements} for more information on
     * the exact formats supported.</p>
     *
     * <p>If the result of value.toString() cannot be split into separate
     * words, then the default value is also returned (or an exception thrown).
     * </p>
     *
     * <p>If any of the elements in the value array (or the elements resulting
     * from splitting up value.toString) are not recognized by the
     * BooleanConverter associated with this object, then what happens depends
     * on whether that BooleanConverter has a default value or not: if it does,
     * then that unrecognized element is converted into the BooleanConverter's
     * default value. If the BooleanConverter does <i>not</i> have a default
     * value, then the default value for this object is returned as the
     * <i>complete</i> conversion result (not just for the element), or an
     * exception is thrown if this object has no default value defined.</p>
     *
     * @param type is the type to which this value should be converted. In the
     *  case of this BooleanArrayConverter class, this value is ignored.
     *
     * @param value is the input value to be converted.
     *
     * @return an object of type boolean[], or the default value if there was
     *  any sort of error during conversion and the constructor
     *  was provided with a default value.
     *
     * @throws ConversionException if conversion cannot be performed
     *  successfully and the constructor was not provided with a default
     *  value to return on conversion failure.
     *
     * @throws NullPointerException if value is an array, and any of the
     * array elements are null.
     */
    @Override
    public Object convert(final Class type, final Object value) {

        // Deal with a null value
        if (value == null) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException("No value specified");
            }
        }

        // Deal with the no-conversion-needed case
        if (MODEL == value.getClass()) {
            return (value);
        }

        // Deal with input value as a String array
        //
        // TODO: use if (value.getClass().isArray() instead...
        //  this requires casting to Object[], then using values[i].toString()
        if (strings.getClass() == value.getClass()) {
            try {
                final String[] values = (String[]) value;
                final boolean[] results = new boolean[values.length];
                for (int i = 0; i < values.length; i++) {
                    final String stringValue = values[i];
                    final Object result = booleanConverter.convert(Boolean.class, stringValue);
                    results[i] = ((Boolean) result).booleanValue();
                }
                return (results);
            } catch (final Exception e) {
                if (useDefault) {
                    return (defaultValue);
                } else {
                    throw new ConversionException(value.toString(), e);
                }
            }
        }

        // We only get here if the input value is not of type String[].
        // In this case, we assume value.toString() returns a comma-separated
        // sequence of values; see method AbstractArrayConverter.parseElements
        // for more information.
        try {
            final List list = parseElements(value.toString());
            final boolean[] results = new boolean[list.size()];
            for (int i = 0; i < results.length; i++) {
                final String stringValue = (String) list.get(i);
                final Object result = booleanConverter.convert(Boolean.class, stringValue);
                results[i] = ((Boolean) result).booleanValue();
            }
            return (results);
        } catch (final Exception e) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException(value.toString(), e);
            }
        }

    }


}
