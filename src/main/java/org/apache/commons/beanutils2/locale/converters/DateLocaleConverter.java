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

package org.apache.commons.beanutils2.locale.converters;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.locale.BaseLocaleConverter;
import org.apache.commons.beanutils2.locale.LocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Standard {@link org.apache.commons.beanutils2.locale.LocaleConverter} implementation that converts an incoming locale-sensitive String into a
 * {@code java.util.Date} object, optionally using a default value or throwing a {@link org.apache.commons.beanutils2.ConversionException} if a conversion error
 * occurs.
 *
 * @param <D> The Date type.
 */
public class DateLocaleConverter<D extends Date> extends BaseLocaleConverter<D> {

    /**
     * Builds instances of {@link DateLocaleConverter}.
     *
     * @param <B> The builder type.
     * @param <D> The Date type.
     */
    public static class Builder<B extends Builder<B, D>, D extends Date> extends BaseLocaleConverter.Builder<B, D> {

        /** Should the date conversion be lenient? */
        protected boolean lenient;

        /**
         * Gets a new instance.
         * <p>
         * Defaults construct a {@link LocaleConverter} that will throw a {@link ConversionException} if a conversion error occurs. The locale is the default
         * locale for this instance of the Java Virtual Machine and an unlocalized pattern is used for the conversion.
         * </p>
         *
         * @return a new instance.
         */
        @Override
        public DateLocaleConverter<D> get() {
            return new DateLocaleConverter<>(defaultValue, locale, pattern, useDefault || defaultValue != null, localizedPattern, lenient);
        }

        /**
         * Sets the leniency policy.
         *
         * @param lenient the leniency policy.
         * @return this.
         */
        public B setLenient(final boolean lenient) {
            this.lenient = lenient;
            return asThis();
        }

    }

    /**
     * Default Pattern Characters
     */
    private static final String DEFAULT_PATTERN_CHARS = DateLocaleConverter.initDefaultChars();

    /** All logging goes through this logger */
    private static final Log LOG = LogFactory.getLog(DateLocaleConverter.class);

    /**
     * Constructs a new builder.
     *
     * @param <B> The builder type.
     * @param <D> The Date type.
     * @return a new builder.
     */
    @SuppressWarnings("unchecked")
    public static <B extends Builder<B, D>, D extends Date> B builder() {
        return (B) new Builder<>();
    }

    /**
     * This method is called at class initialization time to define the value for constant member DEFAULT_PATTERN_CHARS. All other methods needing this data
     * should just read that constant.
     */
    private static String initDefaultChars() {
        return new DateFormatSymbols(Locale.US).getLocalPatternChars();
    }

    /** Should the date conversion be lenient? */
    private final boolean isLenient;

    /**
     * Constructs a new instance.
     *
     * @param defaultValue default value.
     * @param locale locale.
     * @param pattern pattern.
     * @param useDefault use the default.
     * @param locPattern localized pattern.
     * @param lenient leniency policy.
     */
    protected DateLocaleConverter(final D defaultValue, final Locale locale, final String pattern, final boolean useDefault, final boolean locPattern,
            final boolean lenient) {
        super(defaultValue, locale, pattern, useDefault, locPattern);
        this.isLenient = lenient;
    }

    /**
     * Converts a pattern from a localized format to the default format.
     *
     * @param locale           The locale
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
            convertedPattern = convertPattern(localizedPattern, localChars, DEFAULT_PATTERN_CHARS);
        } catch (final Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Converting pattern '" + localizedPattern + "' for " + locale, ex);
            }
        }
        return convertedPattern;
    }

    /**
     * Converts a Pattern from one character set to another.
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
            } else if (thisChar == '\'') {
                quoted = true;
            } else if ((thisChar >= 'a' && thisChar <= 'z') || (thisChar >= 'A' && thisChar <= 'Z')) {
                final int index = fromChars.indexOf(thisChar);
                if (index == -1) {
                    throw new IllegalArgumentException("Illegal pattern character '" + thisChar + "'");
                }
                thisChar = toChars.charAt(index);
            }
            converted.append(thisChar);
        }

        if (quoted) {
            throw new IllegalArgumentException("Unfinished quote in pattern");
        }

        return converted.toString();
    }

    /**
     * Tests whether date formatting is lenient.
     *
     * @return true if the {@code DateFormat} used for formatting is lenient
     * @see java.text.DateFormat#isLenient
     */
    public boolean isLenient() {
        return isLenient;
    }

    /**
     * Convert the specified locale-sensitive input object into an output object of the specified type.
     *
     * @param value   The input object to be converted
     * @param pattern The pattern is used for the conversion
     * @return the converted Date value
     * @throws ConversionException if conversion cannot be performed successfully
     * @throws ParseException                                    if an error occurs parsing
     */
    @Override
    protected D parse(final Object value, String pattern) throws ParseException {
        // Handle Date
        if (value instanceof Date) {
            return (D) value;
        }

        // Handle Calendar
        if (value instanceof java.util.Calendar) {
            return (D) ((java.util.Calendar) value).getTime();
        }

        if (localizedPattern) {
            pattern = convertLocalizedPattern(pattern, locale);
        }

        // Create Formatter - use default if pattern is null
        final DateFormat formatter = pattern == null ? DateFormat.getDateInstance(DateFormat.SHORT, locale) : new SimpleDateFormat(pattern, locale);
        formatter.setLenient(isLenient);

        // Parse the Date
        final ParsePosition pos = new ParsePosition(0);
        final String strValue = value.toString();
        final Object parsedValue = formatter.parseObject(strValue, pos);
        if (pos.getErrorIndex() > -1) {
            throw ConversionException.format("Error parsing date '%s' at position = %s", value, pos.getErrorIndex());
        }
        if (pos.getIndex() < strValue.length()) {
            throw ConversionException.format("Date '%s' contains unparsed characters from position = %s", value, pos.getIndex());
        }

        return (D) parsedValue;
    }

}
