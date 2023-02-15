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
package org.apache.commons.beanutils2.converters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.beanutils2.ConversionException;

/**
 * {@link org.apache.commons.beanutils2.Converter} implementation that handles conversion
 * to and from <b>java.lang.Number</b> objects.
 * <p>
 * This implementation handles conversion for the following
 * {@code java.lang.Number} types.
 * <ul>
 *     <li>{@code java.lang.Byte}</li>
 *     <li>{@code java.lang.Short}</li>
 *     <li>{@code java.lang.Integer}</li>
 *     <li>{@code java.lang.Long}</li>
 *     <li>{@code java.lang.Float}</li>
 *     <li>{@code java.lang.Double}</li>
 *     <li>{@code java.math.BigDecimal}</li>
 *     <li>{@code java.math.BigInteger}</li>
 * </ul>
 *
 * <h2>String Conversions (to and from)</h2>
 * This class provides a number of ways in which number
 * conversions to/from Strings can be achieved:
 * <ul>
 *    <li>Using the default format for the default Locale, configure using:
 *        <ul>
 *          <li>{@code setUseLocaleFormat(true)}</li>
 *        </ul>
 *    </li>
 *    <li>Using the default format for a specified Locale, configure using:
 *        <ul>
 *          <li>{@code setLocale(Locale)}</li>
 *        </ul>
 *    </li>
 *    <li>Using a specified pattern for the default Locale, configure using:
 *        <ul>
 *          <li>{@code setPattern(String)}</li>
 *        </ul>
 *    </li>
 *    <li>Using a specified pattern for a specified Locale, configure using:
 *        <ul>
 *          <li>{@code setPattern(String)}</li>
 *          <li>{@code setLocale(Locale)}</li>
 *        </ul>
 *    </li>
 *    <li>If none of the above are configured the
 *        {@code toNumber(String)} method is used to convert
 *        from String to Number and the Number's
 *        {@code toString()} method used to convert from
 *        Number to String.</li>
 * </ul>
 *
 * <p>
 * <strong>N.B.</strong>Patterns can only be specified using the <i>standard</i>
 * pattern characters and NOT in <i>localized</i> form (see {@code java.text.DecimalFormat}).
 * For example to cater for number styles used in Germany such as {@code 0.000,00} the pattern
 * is specified in the normal form {@code 0,000.00</code> and the locale set to <code>Locale.GERMANY}
 *
 * @param <N> The default value type.
 * @since 1.8.0
 */
public abstract class NumberConverter<N extends Number> extends AbstractConverter<N> {

    private static final Integer ZERO = Integer.valueOf(0);
    private static final Integer ONE  = Integer.valueOf(1);

    private String pattern;
    private final boolean allowDecimals;
    private boolean useLocaleFormat;
    private Locale locale;

    /**
     * Constructs a <b>java.lang.Number</b> <i>Converter</i>
     * that throws a {@code ConversionException} if a error occurs.
     *
     * @param allowDecimals Indicates whether decimals are allowed
     */
    public NumberConverter(final boolean allowDecimals) {
        this.allowDecimals = allowDecimals;
    }

    /**
     * Constructs a {@code java.lang.Number} <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param allowDecimals Indicates whether decimals are allowed
     * @param defaultValue The default value to be returned
     */
    public NumberConverter(final boolean allowDecimals, final N defaultValue) {
        this.allowDecimals = allowDecimals;
        setDefaultValue(defaultValue);
    }

    /**
     * Convert an input Number object into a String.
     *
     * @param value The input value to be converted
     * @return the converted String value.
     * @throws IllegalArgumentException if an error occurs converting to a String
     */
    @Override
    protected String convertToString(final Object value) {

        String result = null;
        if (useLocaleFormat && value instanceof Number) {
            final NumberFormat format = getFormat();
            format.setGroupingUsed(false);
            result = format.format(value);
            if (log().isDebugEnabled()) {
                log().debug("    Converted  to String using format '" + result + "'");
            }

        } else {
            result = value.toString();
            if (log().isDebugEnabled()) {
                log().debug("    Converted  to String using toString() '" + result + "'");
            }
        }
        return result;

    }

    /**
     * Convert the input object into a Number object of the
     * specified type.
     *
     * @param <T> Target type of the conversion.
     * @param targetType Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     */
    @Override
    protected <T> T convertToType(final Class<T> targetType, final Object value) throws Throwable {
        final Class<?> sourceType = value.getClass();
        // Handle Number
        if (value instanceof Number) {
            return toNumber(sourceType, targetType, (Number)value);
        }

        // Handle Boolean
        if (value instanceof Boolean) {
            return toNumber(sourceType, targetType, ((Boolean)value).booleanValue() ? ONE : ZERO);
        }

        // Handle Date --> Long
        if (value instanceof Date && Long.class.equals(targetType)) {
            return targetType.cast(Long.valueOf(((Date)value).getTime()));
        }

        // Handle Calendar --> Long
        if (value instanceof Calendar  && Long.class.equals(targetType)) {
            return targetType.cast(Long.valueOf(((Calendar)value).getTime().getTime()));
        }

        // Convert all other types to String & handle
        final String stringValue = toTrim(value);
        if (stringValue.isEmpty()) {
            return handleMissing(targetType);
        }

        // Convert/Parse a String
        Number number = null;
        if (useLocaleFormat) {
            final NumberFormat format = getFormat();
            number = parse(sourceType, targetType, stringValue, format);
        } else {
            if (log().isDebugEnabled()) {
                log().debug("    No NumberFormat, using default conversion");
            }
            number = toNumber(sourceType, targetType, stringValue);
        }

        // Ensure the correct number type is returned
        return toNumber(sourceType, targetType, number);
    }

    /**
     * Gets a NumberFormat to use for Conversion.
     *
     * @return The NumberFormat.
     */
    private NumberFormat getFormat() {
        NumberFormat format = null;
        if (pattern != null) {
            if (locale == null) {
                if (log().isDebugEnabled()) {
                    log().debug("    Using pattern '" + pattern + "'");
                }
                format = new DecimalFormat(pattern);
            } else {
                if (log().isDebugEnabled()) {
                    log().debug("    Using pattern '" + pattern + "'" +
                              " with Locale[" + locale + "]");
                }
                final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
                format = new DecimalFormat(pattern, symbols);
            }
        } else if (locale == null) {
            if (log().isDebugEnabled()) {
                log().debug("    Using default Locale format");
            }
            format = NumberFormat.getInstance();
        } else {
            if (log().isDebugEnabled()) {
                log().debug("    Using Locale[" + locale + "] format");
            }
            format = NumberFormat.getInstance(locale);
        }
        if (!allowDecimals) {
            format.setParseIntegerOnly(true);
        }
        return format;
    }

    /**
     * Gets the Locale for the <i>Converter</i>
     * (or {@code null} if none specified).
     *
     * @return The locale to use for conversion
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Gets the number format pattern used to convert
     * Numbers to/from a {@code java.lang.String}
     * (or {@code null} if none specified).
     * <p>
     * See {@code java.text.DecimalFormat} for details
     * of how to specify the pattern.
     *
     * @return The format pattern.
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Gets whether decimals are allowed in the number.
     *
     * @return Whether decimals are allowed in the number
     */
    public boolean isAllowDecimals() {
        return allowDecimals;
    }

    /**
     * Convert a String into a {@code Number} object.
     * @param sourceType the source type of the conversion
     * @param targetType The type to convert the value to
     * @param value The String date value.
     * @param format The NumberFormat to parse the String value.
     *
     * @return The converted Number object.
     * @throws ConversionException if the String cannot be converted.
     */
    private Number parse(final Class<?> sourceType, final Class<?> targetType, final String value,
            final NumberFormat format) {
        final ParsePosition pos = new ParsePosition(0);
        final Number parsedNumber = format.parse(value, pos);
        if (pos.getErrorIndex() >= 0 || pos.getIndex() != value.length() || parsedNumber == null) {
            String msg = "Error converting from '" + toString(sourceType) + "' to '" + toString(targetType) + "'";
            if (format instanceof DecimalFormat) {
                msg += " using pattern '" + ((DecimalFormat)format).toPattern() + "'";
            }
            if (locale != null) {
                msg += " for locale=[" + locale + "]";
            }
            if (log().isDebugEnabled()) {
                log().debug("    " + msg);
            }
            throw new ConversionException(msg);
        }
        return parsedNumber;
    }

    /**
     * Sets the Locale for the <i>Converter</i>.
     *
     * @param locale The locale to use for conversion
     */
    public void setLocale(final Locale locale) {
        this.locale = locale;
        setUseLocaleFormat(true);
    }

    /**
     * Sets a number format pattern to use to convert
     * Numbers to/from a {@code java.lang.String}.
     * <p>
     * See {@code java.text.DecimalFormat} for details
     * of how to specify the pattern.
     *
     * @param pattern The format pattern.
     */
    public void setPattern(final String pattern) {
        this.pattern = pattern;
        setUseLocaleFormat(true);
    }

    /**
     * Sets whether a format should be used to convert
     * the Number.
     *
     * @param useLocaleFormat {@code true} if a number format
     * should be used.
     */
    public void setUseLocaleFormat(final boolean useLocaleFormat) {
        this.useLocaleFormat = useLocaleFormat;
    }

    /**
     * Default String to Number conversion.
     * <p>
     * This method handles conversion from a String to the following types:
     * <ul>
     *     <li>{@code java.lang.Byte}</li>
     *     <li>{@code java.lang.Short}</li>
     *     <li>{@code java.lang.Integer}</li>
     *     <li>{@code java.lang.Long}</li>
     *     <li>{@code java.lang.Float}</li>
     *     <li>{@code java.lang.Double}</li>
     *     <li>{@code java.math.BigDecimal}</li>
     *     <li>{@code java.math.BigInteger}</li>
     * </ul>
     * @param sourceType The type being converted from
     * @param targetType The Number type to convert to
     * @param value The String value to convert.
     *
     * @return The converted Number value.
     */
    private Number toNumber(final Class<?> sourceType, final Class<?> targetType, final String value) {
        // Byte
        if (targetType.equals(Byte.class)) {
            return Byte.valueOf(value);
        }

        // Short
        if (targetType.equals(Short.class)) {
            return Short.valueOf(value);
        }

        // Integer
        if (targetType.equals(Integer.class)) {
            return Integer.valueOf(value);
        }

        // Long
        if (targetType.equals(Long.class)) {
            return Long.valueOf(value);
        }

        // Float
        if (targetType.equals(Float.class)) {
            return Float.valueOf(value);
        }

        // Double
        if (targetType.equals(Double.class)) {
            return Double.valueOf(value);
        }

        // BigDecimal
        if (targetType.equals(BigDecimal.class)) {
            return new BigDecimal(value);
        }

        // BigInteger
        if (targetType.equals(BigInteger.class)) {
            return new BigInteger(value);
        }

        final String msg = toString(getClass()) + " cannot handle conversion from '" +
                     toString(sourceType) + "' to '" + toString(targetType) + "'";
        if (log().isWarnEnabled()) {
            log().warn("    " + msg);
        }
        throw new ConversionException(msg);
    }

    /**
     * Convert any Number object to the specified type for this
     * <i>Converter</i>.
     * <p>
     * This method handles conversion to the following types:
     * <ul>
     *     <li>{@code java.lang.Byte}</li>
     *     <li>{@code java.lang.Short}</li>
     *     <li>{@code java.lang.Integer}</li>
     *     <li>{@code java.lang.Long}</li>
     *     <li>{@code java.lang.Float}</li>
     *     <li>{@code java.lang.Double}</li>
     *     <li>{@code java.math.BigDecimal}</li>
     *     <li>{@code java.math.BigInteger}</li>
     * </ul>
     * @param sourceType The type being converted from
     * @param targetType The Number type to convert to
     * @param value The Number to convert.
     *
     * @return The converted value.
     */
    private <T> T toNumber(final Class<?> sourceType, final Class<T> targetType, final Number value) {
        // Correct Number type already
        if (targetType.equals(value.getClass())) {
            return targetType.cast(value);
        }

        // Byte
        if (targetType.equals(Byte.class)) {
            final long longValue = value.longValue();
            if (longValue > Byte.MAX_VALUE) {
                throw ConversionException.format("%s value '%s' is too large for %s", toString(sourceType), value, toString(targetType));
            }
            if (longValue < Byte.MIN_VALUE) {
                throw ConversionException.format("%s value '%s' is too small %s", toString(sourceType), value, toString(targetType));
            }
            return targetType.cast(Byte.valueOf(value.byteValue()));
        }

        // Short
        if (targetType.equals(Short.class)) {
            final long longValue = value.longValue();
            if (longValue > Short.MAX_VALUE) {
                throw ConversionException.format("%s value '%s' is too large for %s", toString(sourceType), value, toString(targetType));
            }
            if (longValue < Short.MIN_VALUE) {
                throw ConversionException.format("%s value '%s' is too small %s", toString(sourceType), value, toString(targetType));
            }
            return targetType.cast(Short.valueOf(value.shortValue()));
        }

        // Integer
        if (targetType.equals(Integer.class)) {
            final long longValue = value.longValue();
            if (longValue > Integer.MAX_VALUE) {
                throw ConversionException.format("%s value '%s' is too large for %s", toString(sourceType), value, toString(targetType));
            }
            if (longValue < Integer.MIN_VALUE) {
                throw ConversionException.format("%s value '%s' is too small %s", toString(sourceType), value, toString(targetType));
            }
            return targetType.cast(Integer.valueOf(value.intValue()));
        }

        // Long
        if (targetType.equals(Long.class)) {
            return targetType.cast(Long.valueOf(value.longValue()));
        }

        // Float
        if (targetType.equals(Float.class)) {
            if (value.doubleValue() > Float.MAX_VALUE) {
                throw ConversionException.format("%s value '%s' is too large for %s", toString(sourceType), value, toString(targetType));
            }
            return targetType.cast(Float.valueOf(value.floatValue()));
        }

        // Double
        if (targetType.equals(Double.class)) {
            return targetType.cast(Double.valueOf(value.doubleValue()));
        }

        // BigDecimal
        if (targetType.equals(BigDecimal.class)) {
            if (value instanceof Float || value instanceof Double) {
                return targetType.cast(new BigDecimal(value.toString()));
            }
            if (value instanceof BigInteger) {
                return targetType.cast(new BigDecimal((BigInteger) value));
            }
            if (value instanceof BigDecimal) {
                return targetType.cast(new BigDecimal(value.toString()));
            }
            return targetType.cast(BigDecimal.valueOf(value.longValue()));
        }

        // BigInteger
        if (targetType.equals(BigInteger.class)) {
            if (value instanceof BigDecimal) {
                return targetType.cast(((BigDecimal) value).toBigInteger());
            }
            return targetType.cast(BigInteger.valueOf(value.longValue()));
        }

        final String msg = toString(getClass()) + " cannot handle conversion to '" + toString(targetType) + "'";
        if (log().isWarnEnabled()) {
            log().warn("    " + msg);
        }
        throw new ConversionException(msg);
    }

    /**
     * Provide a String representation of this number converter.
     *
     * @return A String representation of this number converter
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(toString(getClass()));
        buffer.append("[UseDefault=");
        buffer.append(isUseDefault());
        buffer.append(", UseLocaleFormat=");
        buffer.append(useLocaleFormat);
        if (pattern != null) {
            buffer.append(", Pattern=");
            buffer.append(pattern);
        }
        if (locale != null) {
            buffer.append(", Locale=");
            buffer.append(locale);
        }
        buffer.append(']');
        return buffer.toString();
    }

}
