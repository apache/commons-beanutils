/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils2.sql.converters;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.beanutils2.converters.AbstractDateConverterTest;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the {@link SqlTimestampConverter} class.
 */
class SqlTimestampConverterTest extends AbstractDateConverterTest<Timestamp> {

    /**
     * Gets the expected type
     *
     * @return The expected type
     */
    @Override
    protected Class<Timestamp> getExpectedType() {
        return Timestamp.class;
    }

    private boolean isUSFormatWithComma() {
        // BEANUTILS-495 workaround - sometimes Java 9 expects "," in date even if
        // the format is set to lenient
        final DateFormat loc = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
        return loc.format(new Date()).contains(",");
    }

    /**
     * Create the Converter with no default value.
     *
     * @return A new Converter
     */
    @Override
    protected SqlTimestampConverter makeConverter() {
        return new SqlTimestampConverter();
    }

    /**
     * Create the Converter with a default value.
     *
     * @param defaultValue The default value
     * @return A new Converter
     */
    @Override
    protected SqlTimestampConverter makeConverter(final Timestamp defaultValue) {
        return new SqlTimestampConverter(defaultValue);
    }

    /**
     * Test default String to java.sql.Timestamp conversion
     */
    @Override
    @Test
    public void testDefaultStringToTypeConvert() {
        // Create & Configure the Converter
        final SqlTimestampConverter converter = makeConverter();
        converter.setUseLocaleFormat(false);

        // Valid String --> java.sql.Timestamp Conversion
        final String testString = "2006-10-23 15:36:01.0";
        final Object expected = toType(testString, "yyyy-MM-dd HH:mm:ss.S", null);
        validConversion(converter, expected, testString);

        // Invalid String --> java.sql.Timestamp Conversion
        invalidConversion(converter, "2006/09/21 15:36:01.0");
        invalidConversion(converter, "2006-10-22");
        invalidConversion(converter, "15:36:01");

        // Out-of-range fields must be rejected, not silently rolled over (2006-02-31 25:70:90 -> 2006-03-04 02:11:30)
        invalidConversion(converter, "2006-02-31 15:36:01.0");
        invalidConversion(converter, "2006-10-23 25:70:90.0");
    }

    @Test
    void testDefaultStringToTypeConvertInvalidTimestampOutOfRangeDate() {
        final SqlTimestampConverter converter = makeConverter();
        converter.setUseLocaleFormat(false);
        invalidConversion(converter, "2006-02-31 15:36:01");
        invalidConversion(converter, "2006-13-01 00:00:00");
    }

    @Test
    void testDefaultStringToTypeConvertInvalidTimestampOutOfRangeTime() {
        final SqlTimestampConverter converter = makeConverter();
        converter.setUseLocaleFormat(false);
        invalidConversion(converter, "2006-10-23 25:00:00");
        invalidConversion(converter, "2006-10-23 12:60:00");
    }

    /**
     * Test default String to {@code java.sql.Timestamp} conversion with fractional seconds.
     * <p>
     * The strict {@link DateTimeConverter#SQL_TIMESTAMP_FORMAT} accepts an optional fractional second part. The helper
     * {@link AbstractDateConverterTest#toType(String,String,Locale)} builds the expected value via {@link SimpleDateFormat}, which only supports millisecond
     * precision {@code S} = 1-3 digits. Therefore, the test uses a 3-digit fraction {@code .123} for the {@code toType} comparison and additionally verifies
     * that {@code Timestamp.valueOf} correctly handles the full 9-digit nanosecond fraction that the strict formatter accepts. This avoids the
     * {@code ParseException} that occurs with {@code SSSSSSSSS} in {@code SimpleDateFormat}.
     * </p>
     */
    @Test
    void testDefaultStringToTypeConvertValidTimestampWithFraction() {
        final SqlTimestampConverter converter = makeConverter();
        converter.setUseLocaleFormat(false);
        // 3-digit fraction – SimpleDateFormat can create the expected value
        final String testString1 = "2006-10-23 15:36:01.123";
        final Object expected1 = toType(testString1, "yyyy-MM-dd HH:mm:ss.SSS", null);
        validConversion(converter, expected1, testString1);
        // No fraction – also valid
        final String testString2 = "2006-10-23 15:36:01";
        final Object expected2 = toType(testString2, "yyyy-MM-dd HH:mm:ss", null);
        validConversion(converter, expected2, testString2);
        // Full nanosecond fraction – expected built directly with valueOf
        // valueOf handles up to 9 digits, toType cannot, so build expected manually
        final String testString3 = "2006-10-23 15:36:01.123456789";
        final java.sql.Timestamp expected3 = java.sql.Timestamp.valueOf("2006-10-23 15:36:01.123456789");
        validConversion(converter, expected3, testString3);
    }

    /**
     * Test Date Converter with no default value
     */
    @Override
    @Test
    public void testLocale() {
        // Re-set the default Locale to Locale.US
        final Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        isUSFormatWithComma();

        // Create & Configure the Converter
        final SqlTimestampConverter converter = makeConverter();
        converter.setUseLocaleFormat(true);

        String pattern; // SHORT style Date & Time format for US Locale
        String testString;
        if (isUSFormatWithComma()) {
            pattern = "M/d/yy, h:mm" + amPmSeparator() + "a";
            testString = "3/21/06, 3:06" + amPmSeparator() + "PM";
        } else {
            // More regular pattern for Java 8 and earlier
            pattern = "M/d/yy h:mm" + amPmSeparator() + "a";
            testString = "3/21/06 3:06" + amPmSeparator() + "PM";
        }

        // Valid String --> Type Conversion
        final Object expected = toType(testString, pattern, null);
        validConversion(converter, expected, testString);

        // Invalid Conversions
        invalidConversion(converter, null);
        invalidConversion(converter, "");
        invalidConversion(converter, "13:05 pm");
        invalidConversion(converter, "11:05 p");
        invalidConversion(converter, "11.05 pm");
        invalidConversion(converter, Integer.valueOf(2));

        // Restore the default Locale
        Locale.setDefault(defaultLocale);
    }

    /**
     * Convert from a Calendar to the appropriate Date type
     *
     * @param value The Calendar value to convert
     * @return The converted value
     */
    @Override
    protected Timestamp toType(final Calendar value) {
        return new Timestamp(getTimeInMillis(value));
    }
}
