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

package org.apache.commons.beanutils.locale.converters;

import org.apache.commons.beanutils.locale.BaseLocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * <p>Standard {@link org.apache.commons.beanutils.locale.LocaleConverter}
 * implementation that converts an incoming
 * locale-sensitive object into a <code>java.lang.String</code> object,
 * optionally using a default value or throwing a
 * {@link org.apache.commons.beanutils.ConversionException}
 * if a conversion error occurs.</p>
 *
 * @version $Id$
 */

public class StringLocaleConverter extends BaseLocaleConverter {

    // ----------------------------------------------------- Instance Variables

    /** All logging goes through this logger */
    private final Log log = LogFactory.getLog(StringLocaleConverter.class);     //msz fix


    // ----------------------------------------------------------- Constructors

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs. The locale is the default locale for
     * this instance of the Java Virtual Machine and an unlocalized pattern is used
     * for the convertion.
     *
     */
    public StringLocaleConverter() {

        this(false);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs. The locale is the default locale for
     * this instance of the Java Virtual Machine.
     *
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    public StringLocaleConverter(final boolean locPattern) {

        this(Locale.getDefault(), locPattern);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs. An unlocalized pattern is used for the convertion.
     *
     * @param locale        The locale
     */
    public StringLocaleConverter(final Locale locale) {

        this(locale, false);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs.
     *
     * @param locale        The locale
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    public StringLocaleConverter(final Locale locale, final boolean locPattern) {

        this(locale, (String) null, locPattern);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs. An unlocalized pattern is used for the convertion.
     *
     * @param locale        The locale
     * @param pattern       The convertion pattern
     */
    public StringLocaleConverter(final Locale locale, final String pattern) {

        this(locale, pattern, false);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs.
     *
     * @param locale        The locale
     * @param pattern       The convertion pattern
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    public StringLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {

        super(locale, pattern, locPattern);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will return the specified default value
     * if a conversion error occurs. The locale is the default locale for
     * this instance of the Java Virtual Machine and an unlocalized pattern is used
     * for the convertion.
     *
     * @param defaultValue  The default value to be returned
     */
    public StringLocaleConverter(final Object defaultValue) {

        this(defaultValue, false);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will return the specified default value
     * if a conversion error occurs. The locale is the default locale for
     * this instance of the Java Virtual Machine.
     *
     * @param defaultValue  The default value to be returned
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    public StringLocaleConverter(final Object defaultValue, final boolean locPattern) {

        this(defaultValue, Locale.getDefault(), locPattern);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will return the specified default value
     * if a conversion error occurs. An unlocalized pattern is used for the convertion.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     */
    public StringLocaleConverter(final Object defaultValue, final Locale locale) {

        this(defaultValue, locale, false);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will return the specified default value
     * if a conversion error occurs.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    public StringLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {

        this(defaultValue, locale, null, locPattern);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will return the specified default value
     * if a conversion error occurs. An unlocalized pattern is used for the convertion.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     * @param pattern       The convertion pattern
     */
    public StringLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {

        this(defaultValue, locale, pattern, false);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will return the specified default value
     * if a conversion error occurs.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     * @param pattern       The convertion pattern
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    public StringLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {

        super(defaultValue, locale, pattern, locPattern);
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
     * @throws org.apache.commons.beanutils.ConversionException if conversion
     * cannot be performed successfully
     * @throws ParseException if an error occurs
     */
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {

        String result = null;

        if ((value instanceof Integer) ||
                (value instanceof Long) ||
                (value instanceof BigInteger) ||
                (value instanceof Byte) ||
                (value instanceof Short)) {

            result = getDecimalFormat(locale, pattern).format(((Number) value).longValue());
        }
        else if ((value instanceof Double) ||
                (value instanceof BigDecimal) ||
                (value instanceof Float)) {

            result = getDecimalFormat(locale, pattern).format(((Number) value).doubleValue());
        }
        else if (value instanceof Date) { // java.util.Date, java.sql.Date, java.sql.Time, java.sql.Timestamp

            final SimpleDateFormat dateFormat =
                    new SimpleDateFormat(pattern, locale);

            result = dateFormat.format(value);
        }
        else {
            result = value.toString();
        }

        return result;
    }

    /**
     * Make an instance of DecimalFormat.
     *
     * @param locale The locale
     * @param pattern The pattern is used for the convertion
     * @return The format for the locale and pattern
     *
     * @throws ConversionException if conversion cannot be performed
     *  successfully
     * @throws ParseException if an error occurs parsing a String to a Number
     */
    private DecimalFormat getDecimalFormat(final Locale locale, final String pattern) {

        final DecimalFormat numberFormat = (DecimalFormat) NumberFormat.getInstance(locale);

        // if some constructors default pattern to null, it makes only sense to handle null pattern gracefully
        if (pattern != null) {
            if (locPattern) {
                numberFormat.applyLocalizedPattern(pattern);
            } else {
                numberFormat.applyPattern(pattern);
            }
        } else {
            log.debug("No pattern provided, using default.");
        }

        return numberFormat;
    }
}
