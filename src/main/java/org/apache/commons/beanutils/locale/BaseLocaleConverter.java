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

import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>The base class for all standart type locale-sensitive converters.
 * It has {@link LocaleConverter} and {@link org.apache.commons.beanutils.Converter} implementations,
 * that convert an incoming locale-sensitive Object into an object of correspond type,
 * optionally using a default value or throwing a {@link ConversionException}
 * if a conversion error occurs.</p>
 *
 * @version $Id$
 */

public abstract class BaseLocaleConverter implements LocaleConverter {

    // ----------------------------------------------------- Instance Variables

    /** All logging goes through this logger */
    private final Log log = LogFactory.getLog(BaseLocaleConverter.class);

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
    protected BaseLocaleConverter(final Locale locale, final String pattern) {

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
    protected BaseLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {

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
    protected BaseLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {

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
    protected BaseLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {

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
    private BaseLocaleConverter(final Object defaultValue, final Locale locale,
                                final String pattern, final boolean useDefault, final boolean locPattern) {

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
     * @throws ParseException if conversion cannot be performed
     *  successfully
     */

    abstract protected Object parse(Object value, String pattern) throws ParseException;


    /**
     * Convert the specified locale-sensitive input object into an output object.
     * The default pattern is used for the conversion.
     *
     * @param value The input object to be converted
     * @return The converted value
     *
     * @throws ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(final Object value) {
        return convert(value, null);
    }

    /**
     * Convert the specified locale-sensitive input object into an output object.
     *
     * @param value The input object to be converted
     * @param pattern The pattern is used for the conversion
     * @return The converted value
     *
     * @throws ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(final Object value, final String pattern) {
        return convert(null, value, pattern);
    }

    /**
     * Convert the specified locale-sensitive input object into an output object of the
     * specified type. The default pattern is used for the convertion.
     *
     * @param <T> The desired target type of the conversion
     * @param type Data type to which this value should be converted
     * @param value The input object to be converted
     * @return The converted value
     *
     * @throws ConversionException if conversion cannot be performed
     *  successfully
     */
    public <T> T convert(final Class<T> type, final Object value) {
        return convert(type, value, null);
    }

    /**
     * Convert the specified locale-sensitive input object into an output object of the
     * specified type.
     *
     * @param <T> The desired target type of the conversion
     * @param type Data is type to which this value should be converted
     * @param value is the input object to be converted
     * @param pattern is the pattern is used for the conversion; if null is
     * passed then the default pattern associated with the converter object
     * will be used.
     * @return The converted value
     *
     * @throws ConversionException if conversion cannot be performed
     *  successfully
     */
    public <T> T convert(final Class<T> type, final Object value, final String pattern) {
        final Class<T> targetType = ConvertUtils.primitiveToWrapper(type);
        if (value == null) {
            if (useDefault) {
                return getDefaultAs(targetType);
            } else {
                // symmetric beanutils function allows null
                // so do not: throw new ConversionException("No value specified");
                log.debug("Null value specified for conversion, returing null");
                return null;
            }
        }

        try {
            if (pattern != null) {
                return checkConversionResult(targetType, parse(value, pattern));
            } else {
                return checkConversionResult(targetType, parse(value, this.pattern));
            }
        } catch (final Exception e) {
            if (useDefault) {
                return getDefaultAs(targetType);
            } else {
                if (e instanceof ConversionException) {
                    throw (ConversionException)e;
                }
                throw new ConversionException(e);
            }
        }
    }

    /**
     * Returns the default object specified for this converter cast for the
     * given target type. If the default value is not conform to the given type,
     * an exception is thrown.
     *
     * @param <T> the desired target type
     * @param type the target class of the conversion
     * @return the default value in the given target type
     * @throws ConversionException if the default object is not compatible with
     *         the target type
     */
    private <T> T getDefaultAs(final Class<T> type) {
        return checkConversionResult(type, defaultValue);
    }

    /**
     * Checks whether the result of a conversion is conform to the specified
     * target type. If this is the case, the passed in result object is cast to
     * the correct target type. Otherwise, an exception is thrown.
     *
     * @param <T> the desired result type
     * @param type the target class of the conversion
     * @param result the conversion result object
     * @return the result cast to the target class
     * @throws ConversionException if the result object is not compatible with
     *         the target type
     */
    private static <T> T checkConversionResult(final Class<T> type, final Object result) {
        if (type == null) {
            // in this case we cannot do much; the result object is returned
            @SuppressWarnings("unchecked")
            final
            T temp = (T) result;
            return temp;
        }

        if (result == null) {
            return null;
        }
        if (type.isInstance(result)) {
            return type.cast(result);
        }
        throw new ConversionException("Unsupported target type: " + type);
    }
}
