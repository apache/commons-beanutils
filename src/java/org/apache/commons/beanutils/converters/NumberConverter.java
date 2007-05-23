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

import java.util.Date;
import java.util.Locale;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * Generic {@link Converter} implementaion that handles conversion
 * to and from <b>java.lang.Number</b> objects.
 * <p>
 * This implementation handles conversion for the following
 * <code>java.lang.Number</code> types.
 * <ul>
 *     <li><code>java.lang.Byte</code></li>
 *     <li><code>java.lang.Short</code></li>
 *     <li><code>java.lang.Integer</code></li>
 *     <li><code>java.lang.Long</code></li>
 *     <li><code>java.lang.Float</code></li>
 *     <li><code>java.lang.Double</code></li>
 *     <li><code>java.math.BigDecimal</code></li>
 *     <li><code>java.math.BigInteger</code></li>
 * </ul>
 *
 * <h3>String Conversions (to and from)</h3>
 * This class provides a number of ways in which number
 * conversions to/from Strings can be achieved:
 * <ul>
 *    <li>Using the default format for the default Locale, configure using:</li>
 *        <ul>
 *           <li><code>setUseLocaleFormat(true)</code></li>
 *        </ul>
 *    <li>Using the default format for a specified Locale, configure using:</li>
 *        <ul>
 *           <li><code>setLocale(Locale)</code></li>
 *        </ul>
 *    <li>Using a specified pattern for the default Locale, configure using:</li>
 *        <ul>
 *           <li><code>setPattern(String)</code></li>
 *        </ul>
 *    <li>Using a specified pattern for a specified Locale, configure using:</li>
 *        <ul>
 *           <li><code>setPattern(String)</code></li>
 *           <li><code>setLocale(Locale)</code></li>
 *        </ul>
 *    <li>If none of the above are configured the
 *        <code>toNumber(String)</code> method is used to convert
 *        from String to Number and the Number's
 *        <code>toString()</code> method used to convert from
 *        Number to String.</li>
 * </ul>
 *
 * <p>
 * <strong>N.B.</strong>Patterns can only be specified used the <i>standard</i>
 * pattern characters and NOT in <i>localized</i> form (see <code>java.text.SimpleDateFormat</code>).
 * For example to cater for number styles used in Germany such as <code>0.000,00</code> the pattern
 * is specified in the normal form <code>0,000.00</code> and the locale set to <code>Locale.GERMANY</code>.
 *
 * @version $Revision$ $Date$
 * @since 1.8.0
 */
public class NumberConverter extends AbstractConverter {

    private static final Integer ZERO = new Integer(0);
    private static final Integer ONE  = new Integer(1);

    private String pattern;
    private boolean allowDecimals;
    private boolean useLocaleFormat;
    private Locale locale;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a <b>java.lang.Number</b> <i>Converter</i>
     * that throws a <code>ConversionException</code> if a error occurs.
     *
     * @param defaultType The default type this <code>Converter</code>
     * handles
     * @param allowDecimals Indicates whether decimals are allowed
     */
    public NumberConverter(Class defaultType, boolean allowDecimals) {
        super(defaultType);
        this.allowDecimals = allowDecimals;
    }

    /**
     * Construct a <code>java.lang.Number</code> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param defaultType The default type this <code>Converter</code>
     * handles
     * @param allowDecimals Indicates whether decimals are allowed
     * @param defaultValue The default value to be returned
     */
    public NumberConverter(Class defaultType, boolean allowDecimals, Object defaultValue) {
        super(defaultType);
        this.allowDecimals = allowDecimals;
        setDefaultValue(defaultValue);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return whether decimals are allowed in the number.
     *
     * @return Whether decimals are allowed in the number
     */
    public boolean isAllowDecimals() {
        return allowDecimals;
    }

    /**
     * Set whether a format should be used to convert
     * the Number.
     *
     * @param useLocaleFormat <code>true</code> if a number format
     * should be used.
     */
    public void setUseLocaleFormat(boolean useLocaleFormat) {
        this.useLocaleFormat = useLocaleFormat;
    }

    /**
     * Return the number format pattern used to convert
     * Numbers to/from a <code>java.lang.String</code>
     * (or <code>null</code> if none specified).
     * <p>
     * See <code>java.text.SimpleDateFormat</code> for details
     * of how to specify the pattern.
     *
     * @return The format pattern.
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Set a number format pattern to use to convert
     * Numbers to/from a <code>java.lang.String</code>.
     * <p>
     * See <code>java.text.SimpleDateFormat</code> for details
     * of how to specify the pattern.
     *
     * @param pattern The format pattern.
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
        setUseLocaleFormat(true);
    }

    /**
     * Return the Locale for the <i>Converter</i>
     * (or <code>null</code> if none specified).
     *
     * @return The locale to use for conversion
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Set the Locale for the <i>Converter</i>.
     *
     * @param locale The locale to use for conversion
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        setUseLocaleFormat(true);
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Convert an input Number object into a String.
     *
     * @param value The input value to be converted
     * @return the converted String value.
     * @throws Throwable if an error occurs converting to a String
     */
    protected String convertToString(Object value) throws Throwable {

        String result = null;
        if (useLocaleFormat && value instanceof Number) {
            NumberFormat format = getFormat();
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
     * @param type Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     */
    protected Object convertToType(Class type, Object value) throws Throwable {

        // Handle Number
        if (value instanceof Number) {
            return toNumber(type, (Number)value);
        }

        // Handle Boolean
        if (value instanceof Boolean) {
            return toNumber(type, ((Boolean)value).booleanValue() ? ONE : ZERO);
        }

        // Handle Date --> Long
        if (value instanceof Date && Long.class.equals(type)) {
            return new Long(((Date)value).getTime());
        }

        // Convert all other types to String & handle
        String stringValue = value.toString().trim();
        if (stringValue.length() == 0) {
            return handleMissing(type);
        }

        // Convert/Parse a String
        Number number = null;
        if (useLocaleFormat) {
            NumberFormat format = getFormat();
            number = parse(type, stringValue, format);
        } else {
            if (log().isDebugEnabled()) {
                log().debug("    No NumberFormat, using default conversion");
            }
            number = toNumber(type, stringValue);
        }

        // Ensure the correct number type is returned
        return toNumber(type, number);

    }

    /**
     * Convert any Number object to the specified type for this
     * <i>Converter</i>.
     * <p>
     * This method handles conversion to the following types:
     * <ul>
     *     <li><code>java.lang.Byte</code></li>
     *     <li><code>java.lang.Short</code></li>
     *     <li><code>java.lang.Integer</code></li>
     *     <li><code>java.lang.Long</code></li>
     *     <li><code>java.lang.Float</code></li>
     *     <li><code>java.lang.Double</code></li>
     *     <li><code>java.math.BigDecimal</code></li>
     *     <li><code>java.math.BigInteger</code></li>
     * </ul>
     *
     * @param type The Number type to convert to
     * @param value The Number to convert.
     * @return The converted value.
     */
    private Number toNumber(Class type, Number value) {

        // Correct Number type already
        if (type.equals(value.getClass())) {
            return value;
        }

        // Byte
        if (type.equals(Byte.class)) {
            long longValue = value.longValue();
            if (longValue > Byte.MAX_VALUE) {
                throw new ConversionException("Value '" + value + "' is too large for "
                        + toString(type));
            }
            if (longValue < Byte.MIN_VALUE) {
                throw new ConversionException("Value '" + value + "' is too small "
                        + toString(type));
            }
            return new Byte(value.byteValue());
        }

        // Short
        if (type.equals(Short.class)) {
            long longValue = value.longValue();
            if (longValue > Short.MAX_VALUE) {
                throw new ConversionException("Value '" + value + "' is too large for "
                        + toString(type));
            }
            if (longValue < Short.MIN_VALUE) {
                throw new ConversionException("Value '" + value + "' is too small "
                        + toString(type));
            }
            return new Short(value.shortValue());
        }

        // Integer
        if (type.equals(Integer.class)) {
            long longValue = value.longValue();
            if (longValue > Integer.MAX_VALUE) {
                throw new ConversionException("Value '" + value + "' is too large for "
                        + toString(type));
            }
            if (longValue < Integer.MIN_VALUE) {
                throw new ConversionException("Value '" + value + "' is too small "
                        + toString(type));
            }
            return new Integer(value.intValue());
        }

        // Long
        if (type.equals(Long.class)) {
            return new Long(value.longValue());
        }

        // Float
        if (type.equals(Float.class)) {
            if (value.doubleValue() > Float.MAX_VALUE) {
                throw new ConversionException("Value '" + value
                        + "' is too large for " + toString(type));
            }
            return new Float(value.floatValue());
        }

        // Double
        if (type.equals(Double.class)) {
            return new Double(value.doubleValue());
        }

        // BigDecimal
        if (type.equals(BigDecimal.class)) {
            if (value instanceof Float || value instanceof Double) {
                return new BigDecimal(value.toString());
            } else if (value instanceof BigInteger) {
                return new BigDecimal((BigInteger)value);
            } else {
                return BigDecimal.valueOf(value.longValue());
            }
        }

        // BigInteger
        if (type.equals(BigInteger.class)) {
            if (value instanceof BigDecimal) {
                return ((BigDecimal)value).toBigInteger();
            } else {
                return BigInteger.valueOf(value.longValue());
            }
        }

        String msg = toString(getClass()) + " cannot handle conversion to '"
                   + toString(type) + "'";
        if (log().isWarnEnabled()) {
            log().warn("    " + msg);
        }
        throw new ConversionException(msg);

    }

    /**
     * Default String to Number conversion.
     * <p>
     * This method handles conversion from a String to the following types:
     * <ul>
     *     <li><code>java.lang.Byte</code></li>
     *     <li><code>java.lang.Short</code></li>
     *     <li><code>java.lang.Integer</code></li>
     *     <li><code>java.lang.Long</code></li>
     *     <li><code>java.lang.Float</code></li>
     *     <li><code>java.lang.Double</code></li>
     *     <li><code>java.math.BigDecimal</code></li>
     *     <li><code>java.math.BigInteger</code></li>
     * </ul>
     *
     * @param type The Number type to convert to
     * @param value The String value to convert.
     * @return The converted Number value.
     */
    private Number toNumber(Class type, String value) {

        // Byte
        if (type.equals(Byte.class)) {
            return new Byte(value);
        }

        // Short
        if (type.equals(Short.class)) {
            return new Short(value);
        }

        // Integer
        if (type.equals(Integer.class)) {
            return new Integer(value);
        }

        // Long
        if (type.equals(Long.class)) {
            return new Long(value);
        }

        // Float
        if (type.equals(Float.class)) {
            return new Float(value);
        }

        // Double
        if (type.equals(Double.class)) {
            return new Double(value);
        }

        // BigDecimal
        if (type.equals(BigDecimal.class)) {
            return new BigDecimal(value);
        }

        // BigInteger
        if (type.equals(BigInteger.class)) {
            return new BigInteger(value);
        }

        String msg = toString(getClass())
            + " cannot handle conversion from String to '" + toString(type) + "'";
        if (log().isWarnEnabled()) {
            log().warn("    " + msg);
        }
        throw new ConversionException(msg);
    }

    /**
     * Return a NumberFormat to use for Conversion.
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
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
                format = new DecimalFormat(pattern, symbols);
            }
        } else {
            if (locale == null) {
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
        }
        if (!allowDecimals) {
            format.setParseIntegerOnly(true);
        }
        return format;
    }

    /**
     * Convert a String into a <code>Number</code> object.
     *
     * @param type The type to convert the value to
     * @param value The String date value.
     * @param format The NumberFormat to parse the String value.
     * @return The converted Number object.
     * @throws ConversionException if the String cannot be converted.
     */
    private Number parse(Class type, String value, NumberFormat format) {
        ParsePosition pos = new ParsePosition(0);
        Number parsedNumber = (Number)format.parse(value, pos);
        if (pos.getErrorIndex() >= 0 || pos.getIndex() != value.length() || parsedNumber == null) {
            String msg = "Error converting to '" + toString(type) + "'";
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

}
