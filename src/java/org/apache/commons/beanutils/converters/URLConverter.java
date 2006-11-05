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

import java.net.URL;

import org.apache.commons.beanutils.Converter;

/**
 * {@link Converter} implementaion that handles conversion
 * to and from <b>java.net.URL</b> objects.
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * <code>ConversionException</code> if a conversion error occurs.
 *
 * @author Henri Yandell
 * @version $Revision$ $Date$
 * @since 1.3
 */
public final class URLConverter extends AbstractConverter {

    /**
     * Construct a <b>java.net.URL</b> <i>Converter</i> that throws
     * a <code>ConversionException</code> if an error occurs.
     */
    public URLConverter() {
        super(URL.class);
    }

    /**
     * Construct a <b>java.net.URL</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public URLConverter(Object defaultValue) {
        super(URL.class, defaultValue);
    }

    /**
     * <p>Convert a java.net.URL or object into a String.</p>
     *
     * @param type Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Exception if conversion cannot be performed successfully
     */
    protected Object convertToType(Class type, Object value) throws Exception {
        return new URL(value.toString());
    }

}
