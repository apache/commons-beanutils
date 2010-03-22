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

package org.apache.commons.beanutils.locale;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.util.Locale;


/**
 * <p>The base class for all standart type locale-sensitive converters.
 * It has {@link LocaleConverter} and {@link org.apache.commons.beanutils.Converter} implementations,
 * that convert an incoming locale-sensitive Object into an object of correspond type,
 * optionally using a default value or throwing a {@link ConversionException}
 * if a conversion error occurs.</p>
 *
 * @author Yauheny Mikulski
 */

public abstract class BaseLocaleConverter implements LocaleConverter {

    // ----------------------------------------------------- Instance Variables

    /** All logging goes through this logger */
    private Log log = LogFactory.getLog(BaseLocaleConverter.class);

    /** The default value specified to our Constructor, if any. */
    private Object defaultValue = null;

    /** Should we return the default value on conversion errors? */
    protected boolean useDefault = false;

    /** The locale specified to our Constructor, by default - system locale. */
    protected Locale locale = Locale.getDefault();

    /** The default pattern specified to our Constructor, if any. */
    protected String pattern = null;

    /** The flag indicating whether the given pattern string is localized or not. */
    protected boolean locPattern = false;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a {@link LocaleConverter} that will throw a {@link ConversionException}
     * if a conversion error occurs.
     * An unlocalized pattern is used for the convertion.
     *
     * @param locale        The locale
     * @param pattern       The convertion pattern
     */
    protected BaseLocaleConverter(Locale locale, String pattern) {

        this(null, locale, pattern, false, false);
    }

    /**
     * Create a {@link LocaleConverter} that will throw a {@link ConversionException}
     * if a conversion error occurs.
     *
     * @param locale        The locale
     * @param pattern       The convertion pattern
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    protected BaseLocaleConverter(Locale locale, String pattern, boolean locPattern) {

        this(null, locale, pattern, false, locPattern);
    }

    /**
     * Create a {@link LocaleConverter} that will return the specified default value
     * if a conversion error occurs.
     * An unlocalized pattern is used for the convertion.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     * @param pattern       The convertion pattern
     */
    protected BaseLocaleConverter(Object defaultValue, Locale locale, String pattern) {

        this(defaultValue, locale, pattern, false);
    }

    /**
     * Create a {@link LocaleConverter} that will return the specified default value
     * if a conversion error occurs.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     * @param pattern       The convertion pattern
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    protected BaseLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {

        this(defaultValue, locale, pattern, true, locPattern);
    }

    /**
     * Create a {@link LocaleConverter} that will return the specified default value
     * or throw a {@link ConversionException} if a conversion error occurs.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     * @param pattern       The convertion pattern
     * @param useDefault    Indicate whether the default value is used or not
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    private BaseLocaleConverter(Object defaultValue, Locale locale,
                                String pattern, boolean useDefault, boolean locPattern) {

        if (useDefault) {
            this.defaultValue = defaultValue;
            this.useDefault = true;
        }

        if (locale != null) {
            this.locale = locale;
        }

        this.pattern = pattern;
        this.locPattern = locPattern;
    }

    // --------------------------------------------------------- Methods

    /**
     * Convert the specified locale-sensitive input object into an output object of the
     * specified type.
     *
     * @param value The input object to be converted
     * @param pattern The pattern is used for the convertion
     * @return The converted value
     *
     * @exception ParseException if conversion cannot be performed
     *  successfully
     */

    abstract protected Object parse(Object value, String pattern) throws ParseException;


    /**
     * Convert the specified locale-sensitive input object into an output object.
     * The default pattern is used for the convertion.
     *
     * @param value The input object to be converted
     * @return The converted value
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Object value) {
        return convert(value, null);
    }

    /**
     * Convert the specified locale-sensitive input object into an output object.
     *
     * @param value The input object to be converted
     * @param pattern The pattern is used for the convertion
     * @return The converted value
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Object value, String pattern) {
        return convert(null, value, pattern);
    }

    /**
     * Convert the specified locale-sensitive input object into an output object of the
     * specified type. The default pattern is used for the convertion.
     *
     * @param type Data type to which this value should be converted
     * @param value The input object to be converted
     * @return The converted value
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Class type, Object value) {
        return convert(type, value, null);
    }

    /**
     * Convert the specified locale-sensitive input object into an output object of the
     * specified type.
     *
     * @param type Data is type to which this value should be converted
     * @param value is the input object to be converted
     * @param pattern is the pattern is used for the conversion; if null is
     * passed then the default pattern associated with the converter object
     * will be used.
     * @return The converted value
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Class type, Object value, String pattern) {
        if (value == null) {
            if (useDefault) {
                return (defaultValue);
            } else {
                // symmetric beanutils function allows null
                // so do not: throw new ConversionException("No value specified");
                log.debug("Null value specified for conversion, returing null");
                return null;
            }
        }

        try {
            if (pattern != null) {
                return parse(value, pattern);
            } else {
                return parse(value, this.pattern);
            }
        } catch (Exception e) {
            if (useDefault) {
                return (defaultValue);
            } else {
                if (e instanceof ConversionException) {
                    throw (ConversionException)e;
                }
                throw new ConversionException(e);
            }
        }
    }
}
