/*
 * Copyright 2001-2004 The Apache Software Foundation.
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


import java.util.List;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;


/**
 * <p>Standard {@link Converter} implementation that converts an incoming
 * String into a primitive array of boolean.  On a conversion failure, returns
 * a specified default value or throws a {@link ConversionException} depending
 * on how this instance is constructed.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 * @since 1.4
 */

public final class BooleanArrayConverter extends AbstractArrayConverter {


    // ----------------------------------------------------------- Constructors


    /**
     * Create a {@link Converter} that will throw a {@link ConversionException}
     * if a conversion error occurs.
     */
    public BooleanArrayConverter() {

        this.defaultValue = null;
        this.useDefault = false;

    }


    /**
     * Create a {@link Converter} that will return the specified default value
     * if a conversion error occurs.
     *
     * @param defaultValue The default value to be returned
     */
    public BooleanArrayConverter(Object defaultValue) {

        this.defaultValue = defaultValue;
        this.useDefault = true;

    }


    // ------------------------------------------------------- Static Variables


    /**
     * <p>Model object for type comparisons.</p>
     */
    private static boolean model[] = new boolean[0];


    // --------------------------------------------------------- Public Methods


    /**
     * Convert the specified input object into an output object of the
     * specified type.
     *
     * @param type Data type to which this value should be converted
     * @param value The input value to be converted
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Class type, Object value) {

        // Deal with a null value
        if (value == null) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException("No value specified");
            }
        }

        // Deal with the no-conversion-needed case
        if (model.getClass() == value.getClass()) {
            return (value);
        }

        // Deal with input value as a String array
        if (strings.getClass() == value.getClass()) {
            try {
                String values[] = (String[]) value;
                boolean results[] = new boolean[values.length];
                for (int i = 0; i < values.length; i++) {
                    String stringValue = values[i];
                    if (stringValue.equalsIgnoreCase("yes") ||
                        stringValue.equalsIgnoreCase("y") ||
                        stringValue.equalsIgnoreCase("true") ||
                        stringValue.equalsIgnoreCase("on") ||
                        stringValue.equalsIgnoreCase("1")) {
                        results[i] = true;
                    } else if (stringValue.equalsIgnoreCase("no") ||
                               stringValue.equalsIgnoreCase("n") ||
                               stringValue.equalsIgnoreCase("false") ||
                               stringValue.equalsIgnoreCase("off") ||
                               stringValue.equalsIgnoreCase("0")) {
                        results[i] = false;
                    } else {
                        if (useDefault) {
                            return (defaultValue);
                        } else {
                            throw new ConversionException(value.toString());
                        }
                    }
                }
                return (results);
            } catch (Exception e) {
                if (useDefault) {
                    return (defaultValue);
                } else {
                    throw new ConversionException(value.toString(), e);
                }
            }
        }

        // Parse the input value as a String into elements
        // and convert to the appropriate type
        try {
            List list = parseElements(value.toString());
            boolean results[] = new boolean[list.size()];
            for (int i = 0; i < results.length; i++) {
                String stringValue = (String) list.get(i);
                if (stringValue.equalsIgnoreCase("yes") ||
                    stringValue.equalsIgnoreCase("y") ||
                    stringValue.equalsIgnoreCase("true") ||
                    stringValue.equalsIgnoreCase("on") ||
                    stringValue.equalsIgnoreCase("1")) {
                    results[i] = true;
                } else if (stringValue.equalsIgnoreCase("no") ||
                           stringValue.equalsIgnoreCase("n") ||
                           stringValue.equalsIgnoreCase("false") ||
                           stringValue.equalsIgnoreCase("off") ||
                           stringValue.equalsIgnoreCase("0")) {
                    results[i] = false;
                } else {
                    if (useDefault) {
                        return (defaultValue);
                    } else {
                        throw new ConversionException(value.toString());
                    }
                }
            }
            return (results);
        } catch (Exception e) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException(value.toString(), e);
            }
        }

    }


}
