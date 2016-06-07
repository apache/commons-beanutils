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
 * Standard {@link org.apache.commons.beanutils.Converter} implementation that converts an incoming
 * String into an array of String objects. On a conversion failure, returns
 * a specified default value or throws a {@link ConversionException} depending
 * on how this instance is constructed.
 * <p>
 * There is also some special handling where the input is of type int[].
 * See method convert for more details.
 *
 * @version $Id$
 * @since 1.4
 * @deprecated Replaced by the new {@link ArrayConverter} implementation
 */

@Deprecated
public final class StringArrayConverter extends AbstractArrayConverter {


    // ----------------------------------------------------------- Constructors


    /**
     * Create a {@link org.apache.commons.beanutils.Converter} that will throw
     * a {@link ConversionException} if a conversion error occurs.
     */
    public StringArrayConverter() {

        this.defaultValue = null;
        this.useDefault = false;

    }


    /**
     * Create a {@link org.apache.commons.beanutils.Converter} that will return
     * the specified default value if a conversion error occurs.
     *
     * @param defaultValue The default value to be returned
     */
    public StringArrayConverter(final Object defaultValue) {

        this.defaultValue = defaultValue;
        this.useDefault = true;

    }


    // ------------------------------------------------------- Static Variables


    /**
     * <p>Model object for type comparisons.</p>
     */
    private static final String[] MODEL = new String[0];

    /**
     * <p> Model object for int arrays.</p>
     */
    private static final int[] INT_MODEL = new int[0];



    // --------------------------------------------------------- Public Methods


    /**
     * Convert the specified input object into an output object of the
     * specified type.
     * <p>
     * If the value is already of type String[] then it is simply returned
     * unaltered.
     * <p>
     * If the value is of type int[], then a String[] is returned where each
     * element in the string array is the result of calling Integer.toString
     * on the corresponding element of the int array. This was added as a
     * result of bugzilla request #18297 though there is not complete
     * agreement that this feature should have been added.
     * <p>
     * In all other cases, this method calls toString on the input object, then
     * assumes the result is a comma-separated list of values. The values are
     * split apart into the individual items and returned as the elements of an
     * array. See class AbstractArrayConverter for the exact input formats
     * supported.
     *
     * @param type is the data type to which this value should be converted.
     * It is expected to be the class for type String[] (though this parameter
     * is actually ignored by this method).
     *
     * @param value is the input value to be converted. If null then the
     * default value is returned or an exception thrown if no default value
     * exists.
     * @return the converted value
     *
     * @throws ConversionException if conversion cannot be performed
     * successfully, or the input is null and there is no default value set
     * for this object.
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
        if (MODEL.getClass() == value.getClass()) {
            return (value);
        }

        // Deal with the input value as an int array
        if (INT_MODEL.getClass() == value.getClass())
        {
            final int[] values = (int[]) value;
            final String[] results = new String[values.length];
            for (int i = 0; i < values.length; i++)
            {
                results[i] = Integer.toString(values[i]);
            }

            return (results);
        }

        // Parse the input value as a String into elements
        // and convert to the appropriate type
        try {
            final List list = parseElements(value.toString());
            final String[] results = new String[list.size()];
            for (int i = 0; i < results.length; i++) {
                results[i] = (String) list.get(i);
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
