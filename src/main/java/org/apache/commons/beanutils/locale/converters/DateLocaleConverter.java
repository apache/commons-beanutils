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

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.BaseLocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>Standard {@link org.apache.commons.beanutils.locale.LocaleConverter}
 * implementation that converts an incoming
 * locale-sensitive String into a <code>java.util.Date</code> object,
 * optionally using a default value or throwing a
 * {@link org.apache.commons.beanutils.ConversionException}
 * if a conversion error occurs.</p>
 *
 * @version $Id$
 */

public class DateLocaleConverter extends BaseLocaleConverter {

    // ----------------------------------------------------- Instance Variables

    /** All logging goes through this logger */
    private final Log log = LogFactory.getLog(DateLocaleConverter.class);

    /** Should the date conversion be lenient? */
    boolean isLenient = false;

    /**
     * Default Pattern Characters
     *
     */
    private static final String DEFAULT_PATTERN_CHARS = DateLocaleConverter.initDefaultChars();

    // ----------------------------------------------------------- Constructors

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs. The locale is the default locale for
     * this instance of the Java Virtual Machine and an unlocalized pattern is used
     * for the convertion.
     *
     */
    public DateLocaleConverter() {

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
    public DateLocaleConverter(final boolean locPattern) {

        this(Locale.getDefault(), locPattern);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter}
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs. An unlocalized pattern is used for the convertion.
     *
     * @param locale        The locale
     */
    public DateLocaleConverter(final Locale locale) {

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
    public DateLocaleConverter(final Locale locale, final boolean locPattern) {

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
    public DateLocaleConverter(final Locale locale, final String pattern) {

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
    public DateLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {

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
    public DateLocaleConverter(final Object defaultValue) {

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
    public DateLocaleConverter(final Object defaultValue, final boolean locPattern) {

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
    public DateLocaleConverter(final Object defaultValue, final Locale locale) {

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
    public DateLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {

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
    public DateLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {

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
    public DateLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {

        super(defaultValue, locale, pattern, locPattern);
    }

    // --------------------------------------------------------- Methods

    /**
     * Returns whether date formatting is lenient.
     *
     * @return true if the <code>DateFormat</code> used for formatting is lenient
     * @see java.text.DateFormat#isLenient
     */
    public boolean isLenient() {
        return isLenient;
    }

    /**
     * Specify whether or not date-time parsing should be lenient.
     *
     * @param lenient true if the <code>DateFormat</code> used for formatting should be lenient
     * @see java.text.DateFormat#setLenient
     */
    public void setLenient(final boolean lenient) {
        isLenient = lenient;
    }

    // --------------------------------------------------------- Methods

    /**
     * Convert the specified locale-sensitive input object into an output object of the
     * specified type.
     *
     * @param value The input object to be converted
     * @param pattern The pattern is used for the convertion
     * @return the converted Date value
     *
     * @throws org.apache.commons.beanutils.ConversionException
     * if conversion cannot be performed successfully
     * @throws ParseException if an error occurs parsing
     */
    @Override
    protected Object parse(final Object value, String pattern) throws ParseException {

        // Handle Date
        if (value instanceof java.util.Date) {
            return value;
        }

        // Handle Calendar
        if (value instanceof java.util.Calendar) {
            return ((java.util.Calendar)value).getTime();
        }

         if (locPattern) {
             pattern = convertLocalizedPattern(pattern, locale);
         }

         // Create Formatter - use default if pattern is null
         final DateFormat formatter = pattern == null ? DateFormat.getDateInstance(DateFormat.SHORT, locale)
                                                : new SimpleDateFormat(pattern, locale);
         formatter.setLenient(isLenient);


         // Parse the Date
        final ParsePosition pos = new ParsePosition(0);
        final String strValue = value.toString();
        final Object parsedValue = formatter.parseObject(strValue, pos);
        if (pos.getErrorIndex() > -1) {
            throw new ConversionException("Error parsing date '" + value +
                    "' at position="+ pos.getErrorIndex());
        }
        if (pos.getIndex() < strValue.length()) {
            throw new ConversionException("Date '" + value +
                    "' contains unparsed characters from position=" + pos.getIndex());
        }

        return parsedValue;
     }

     /**
      * Convert a pattern from a localized format to the default format.
      *
      * @param locale   The locale
      * @param localizedPattern The pattern in 'local' symbol format
      * @return pattern in 'default' symbol format
      */
     private String convertLocalizedPattern(final String localizedPattern, final Locale locale) {

         if (localizedPattern == null) {
            return null;
         }

         // Note that this is a little obtuse.
         // However, it is the best way that anyone can come up with
         // that works with some 1.4 series JVM.

         // Get the symbols for the localized pattern
         final DateFormatSymbols localizedSymbols = new DateFormatSymbols(locale);
         final String localChars = localizedSymbols.getLocalPatternChars();

         if (DEFAULT_PATTERN_CHARS.equals(localChars)) {
             return localizedPattern;
         }

         // Convert the localized pattern to default
         String convertedPattern = null;
         try {
             convertedPattern = convertPattern(localizedPattern,
                                                localChars,
                                                DEFAULT_PATTERN_CHARS);
         } catch (final Exception ex) {
             log.debug("Converting pattern '" + localizedPattern + "' for " + locale, ex);
         }
         return convertedPattern;
    }

    /**
     * <p>Converts a Pattern from one character set to another.</p>
     */
    private String convertPattern(final String pattern, final String fromChars, final String toChars) {

        final StringBuilder converted = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < pattern.length(); ++i) {
            char thisChar = pattern.charAt(i);
            if (quoted) {
                if (thisChar == '\'') {
                    quoted = false;
                }
            } else {
                if (thisChar == '\'') {
                   quoted = true;
                } else if ((thisChar >= 'a' && thisChar <= 'z') ||
                           (thisChar >= 'A' && thisChar <= 'Z')) {
                    final int index = fromChars.indexOf(thisChar );
                    if (index == -1) {
                        throw new IllegalArgumentException(
                            "Illegal pattern character '" + thisChar + "'");
                    }
                    thisChar = toChars.charAt(index);
                }
            }
            converted.append(thisChar);
        }

        if (quoted) {
            throw new IllegalArgumentException("Unfinished quote in pattern");
        }

        return converted.toString();
    }

    /**
     * This method is called at class initialization time to define the
     * value for constant member DEFAULT_PATTERN_CHARS. All other methods needing
     * this data should just read that constant.
     */
    private static String initDefaultChars() {
        final DateFormatSymbols defaultSymbols = new DateFormatSymbols(Locale.US);
        return defaultSymbols.getLocalPatternChars();
    }

}
