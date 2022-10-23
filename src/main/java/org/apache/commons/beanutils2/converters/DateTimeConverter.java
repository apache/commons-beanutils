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

import static java.time.temporal.ChronoField.INSTANT_SECONDS;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.beanutils2.ConversionException;

/**
 * {@link org.apache.commons.beanutils2.Converter} implementation
 * that handles conversion to and from <b>date/time</b> objects.
 * <p>
 * This implementation handles conversion for the following
 * <i>date/time</i> types.
 * <ul>
 *     <li>{@code java.util.Date}</li>
 *     <li>{@code java.util.Calendar}</li>
 *     <li>{@code java.time.Instant}</li>
 *     <li>{@code java.time.LocalDate}</li>
 *     <li>{@code java.time.LocalDateTime}</li>
 *     <li>{@code java.time.OffsetDateTime}</li>
 *     <li>{@code java.time.ZonedDateTime}</li>
 *     <li>{@code java.sql.Date}</li>
 *     <li>{@code java.sql.Time}</li>
 *     <li>{@code java.sql.Timestamp}</li>
 * </ul>
 *
 * <h2>String Conversions (to and from)</h2>
 * This class provides a number of ways in which date/time
 * conversions to/from Strings can be achieved:
 * <ul>
 *    <li>Using the SHORT date format for the default Locale, configure using:
 *        <ul>
 *           <li>{@code setUseLocaleFormat(true)}</li>
 *        </ul>
 *    </li>
 *    <li>Using the SHORT date format for a specified Locale, configure using:
 *        <ul>
 *           <li>{@code setLocale(Locale)}</li>
 *        </ul>
 *    </li>
 *    <li>Using the specified date pattern(s) for the default Locale, configure using:
 *        <ul>
 *           <li>Either {@code setPattern(String)} or
 *                      {@code setPatterns(String[])}</li>
 *        </ul>
 *    </li>
 *    <li>Using the specified date pattern(s) for a specified Locale, configure using:
 *        <ul>
 *           <li>{@code setPattern(String)} or
 *                    {@code setPatterns(String[]) and...}</li>
 *           <li>{@code setLocale(Locale)}</li>
 *        </ul>
 *    </li>
 *    <li>If none of the above are configured the
 *        {@code toDate(String)} method is used to convert
 *        from String to Date and the Dates's
 *        {@code toString()} method used to convert from
 *        Date to String.</li>
 * </ul>
 *
 * <p>
 * The <b>Time Zone</b> to use with the date format can be specified
 * using the {@link #setTimeZone(TimeZone)} method.
 *
 * @param <D> The default value type.
 * @since 1.8.0
 */
public abstract class DateTimeConverter<D> extends AbstractConverter<D> {

    private String[] patterns;
    private String displayPatterns;
    private Locale locale;
    private ZoneId zoneId;
    private boolean useLocaleFormat;

    /**
     * Constructs a Date/Time <i>Converter</i> that throws a
     * {@code ConversionException} if an error occurs.
     */
    public DateTimeConverter() {
    }

    /**
     * Constructs a Date/Time <i>Converter</i> that returns a default
     * value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public DateTimeConverter(final D defaultValue) {
        super(defaultValue);
    }

    /**
     * Indicate whether conversion should use a format/pattern or not.
     *
     * @param useLocaleFormat {@code true} if the format
     * for the locale should be used, otherwise {@code false}
     */
    public void setUseLocaleFormat(final boolean useLocaleFormat) {
        this.useLocaleFormat = useLocaleFormat;
    }

    /**
     * Gets the Time Zone to use when converting dates
     * (or {@code null} if none specified.
     *
     * @return The Time Zone.
     */
    public TimeZone getTimeZone() {
        if(zoneId!=null) {
          return TimeZone.getTimeZone(zoneId);
        } 
        return null;
    }

    /**
     * Sets the Time Zone to use when converting dates.
     *
     * @param timeZone The Time Zone.
     */
    public void setTimeZone(final TimeZone timeZone) {
        this.zoneId = timeZone.toZoneId();
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
     * Sets the Locale for the <i>Converter</i>.
     *
     * @param locale The Locale.
     */
    public void setLocale(final Locale locale) {
        this.locale = locale;
        setUseLocaleFormat(true);
    }

    /**
     * Sets a date format pattern to use to convert
     * dates to/from a {@code java.lang.String}.
     *
     * @see SimpleDateFormat
     * @param pattern The format pattern.
     */
    public void setPattern(final String pattern) {
        setPatterns(new String[] {pattern});
    }

    /**
     * Gets the date format patterns used to convert
     * dates to/from a {@code java.lang.String}
     * (or {@code null} if none specified).
     *
     * @see SimpleDateFormat
     * @return Array of format patterns.
     */
    public String[] getPatterns() {
        return patterns.clone();
    }

    /**
     * Sets the date format patterns to use to convert
     * dates to/from a {@code java.lang.String}.
     *
     * @see SimpleDateFormat
     * @param patterns Array of format patterns.
     */
    public void setPatterns(final String[] patterns) {
        this.patterns = patterns != null ? patterns.clone() : null;
        if (this.patterns != null && this.patterns.length > 1) {
            displayPatterns = String.join(", ", this.patterns);
        }
        setUseLocaleFormat(true);
    }

    /**
     * Convert an input Date/Calendar object into a String.
     * <p>
     * <b>N.B.</b>If the converter has been configured to with
     * one or more patterns (using {@code setPatterns()}), then
     * the first pattern will be used to format the date into a String.
     * Otherwise the default {@code DateFormat} for the default locale
     * (and <i>style</i> if configured) will be used.
     *
     * @param value The input value to be converted
     * @return the converted String value.
     * @throws IllegalArgumentException if an error occurs converting to a String
     */
    @Override
    protected String convertToString(final Object value) {
        TemporalAccessor date = null;
        
        if(value instanceof Instant) {
          date = ((Instant)value).atZone(getZoneId());
        }else if (value instanceof TemporalAccessor) {
          //LocalDateTime、LocalDate、ZonedDateTime、OffsetDateTime
          date = (TemporalAccessor) value;
        } else {
            try {
              date= convertToType(LocalDateTime.class,value);
            }catch(Exception e) {
              log().debug(value+" to LocalDateTime Error : ",e);
            }
        }
        
        String result = null;
        if (useLocaleFormat && date != null) {
            DateTimeFormatter format = null;
            if (patterns != null && patterns.length > 0) {
                format = getFormat(patterns[0]);
            } else {
                format = getFormat(locale, getZoneId(),0);
            }
            logFormat("Formatting", format);
            result = format.format(date);
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
     * Convert the input object into a Date object of the
     * specified type.
     * <p>
     * This method handles conversions between the following
     * types:
     * <ul>
     *     <li>{@code java.util.Date}</li>
     *     <li>{@code java.util.Calendar}</li>
     *     <li>{@code java.time.LocalDate}</li>
     *     <li>{@code java.time.LocalDateTime}</li>
     *     <li>{@code java.time.OffsetDateTime}</li>
     *     <li>{@code java.time.ZonedDateTime}</li>
     *     <li>{@code java.time.Instant}</li>
     *     <li>{@code java.sql.Date}</li>
     *     <li>{@code java.sql.Time}</li>
     *     <li>{@code java.sql.Timestamp}</li>
     * </ul>
     *
     * It also handles conversion from a {@code String} to
     * any of the above types.
     * <p>
     *
     * For {@code String} conversion, if the converter has been configured
     * with one or more patterns (using {@code setPatterns()}), then
     * the conversion is attempted with each of the specified patterns.
     * Otherwise the default {@code DateFormat} for the default locale
     * (and <i>style</i> if configured) will be used.
     *
     * @param <T> The desired target type of the conversion.
     * @param targetType Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Exception if conversion cannot be performed successfully
     */
    @Override
    protected <T> T convertToType(final Class<T> targetType, final Object value) throws Exception {
        final Class<?> sourceType = value.getClass();

        // Handle java.sql.Timestamp
        if (value instanceof java.sql.Timestamp) {


            // N.B. Prior to JDK 1.4 the Timestamp's getTime() method
            //      didn't include the milliseconds. The following code
            //      ensures it works consistently across JDK versions
            final java.sql.Timestamp timestamp = (java.sql.Timestamp)value;
            return toDate(targetType, timestamp.getTime() / 1000,
                timestamp.getNanos());
        }

        // Handle Date (includes java.sql.Date & java.sql.Time)
        if (value instanceof Date) {
            final Date date = (Date)value;
            return toDate(targetType, date.getTime());
        }

        // Handle Calendar
        if (value instanceof Calendar) {
            final Calendar calendar = (Calendar)value;
            return toDate(targetType, calendar.getTime().getTime());
        }

        // Handle Long
        if (value instanceof Long) {
            final Long longObj = (Long)value;
            return toDate(targetType, longObj.longValue());
        }

        // Handle LocalDate
        if (value instanceof LocalDate) {
            final LocalDate date = (LocalDate)value;
            final Instant temp = date.atStartOfDay(getZoneId()).toInstant();
            return toDate(targetType, temp.getEpochSecond(), temp.getNano());
        }
        
        // Handle LocalDate
        if (value instanceof LocalTime) {
            final LocalTime date = (LocalTime)value;
            final Instant temp = date.atDate(LocalDate.of(1970, 1, 1)).atZone(getZoneId()).toInstant();
            return toDate(targetType, temp.getEpochSecond(), temp.getNano());
        }

        // Handle LocalDateTime
        if (value instanceof LocalDateTime) {
            final LocalDateTime date = (LocalDateTime)value;
            final Instant temp = date.atZone(getZoneId()).toInstant();
            return toDate(targetType,  temp.getEpochSecond(), temp.getNano());
        }

        // Handle ZonedDateTime
        if (value instanceof ZonedDateTime) {
            final ZonedDateTime date = (ZonedDateTime)value;
            final Instant temp = date.toInstant();
            return toDate(targetType, temp.getEpochSecond(), temp.getNano());
        }

        // Handle OffsetDateTime
        if (value instanceof OffsetDateTime) {
            final OffsetDateTime date = (OffsetDateTime)value;
            final Instant temp = date.toInstant();
            return toDate(targetType, temp.getEpochSecond(), temp.getNano());
        }
        
        //Handle Instant and other TemporalAccessor implementations.
        if (value instanceof TemporalAccessor) {
            final Instant temp = Instant.from(((TemporalAccessor) value));
            return toDate(targetType, temp.getEpochSecond(), temp.getNano());
        }

        // Convert all other types to String & handle
        final String stringValue = toTrim(value);
        if (stringValue.isEmpty()) {
            return handleMissing(targetType);
        }

        // Parse the Date/Time
        if (useLocaleFormat) {
            TemporalAccessor temporalAccessor = null;
            if (patterns != null && patterns.length > 0) {
                temporalAccessor = parse(sourceType, targetType, stringValue);
            } else {
                final DateTimeFormatter format = getFormat(locale, getZoneId(),stringValue);
                temporalAccessor = parse(sourceType, targetType, stringValue, format);
            }
            return convertToType(targetType, temporalAccessor);
        }

        // Default String conversion
        return toDate(targetType, stringValue);
    }

    /**
     * Convert a milliseconds long value to the specified Date type for this
     * <i>Converter</i>.
     * <p>
     *
     * This method handles conversion to the following types:
     * <ul>
     *     <li>{@code java.util.Date}</li>
     *     <li>{@code java.util.Calendar}</li>
     *     <li>{@code java.time.LocalDate}</li>
     *     <li>{@code java.time.LocalDateTime}</li>
     *     <li>{@code java.time.ZonedDateTime}</li>
     *     <li>{@code java.time.Instant}</li>
     *     <li>{@code java.sql.Date}</li>
     *     <li>{@code java.sql.Time}</li>
     *     <li>{@code java.sql.Timestamp}</li>
     * </ul>
     *
     * @param <T> The target type
     * @param type The Date type to convert to
     * @param milliSeconds The milliseconds long value to convert.
     * @return The converted date value.
     */
    private <T> T toDate(final Class<T> type, final long milliSeconds) {
      return toDate(type, milliSeconds / 1000,
          Long.valueOf(milliSeconds % 1000).intValue() * 1000000);
    }

    /**
     * Convert a seconds value and a nanos value to the specified
     * Date type for this <i>Converter</i>.
     * <p>
     * 
     * This method handles conversion to the following types:
     * <ul>
     *     <li>{@code java.util.Date}</li>
     *     <li>{@code java.util.Calendar}</li>
     *     <li>{@code java.time.LocalDate}</li>
     *     <li>{@code java.time.LocalDateTime}</li>
     *     <li>{@code java.time.ZonedDateTime}</li>
     *     <li>{@code java.time.Instant}</li>
     *     <li>{@code java.sql.Date}</li>
     *     <li>{@code java.sql.Time}</li>
     *     <li>{@code java.sql.Timestamp}</li>
     * </ul>
     *
     * @param <T>     The target type
     * @param type    The Date type to convert to
     * @param seconds Represents seconds of UTC time since Unix epoch
     *                1970-01-01T00:00:00Z. Must be from
     *                0001-01-01T00:00:00Z to 9999-12-31T23:59:59Z
     *                inclusive.
     * @param nanos   Non-negative fractions of a second at
     *                nanosecond resolution. Negative second values
     *                with fractions must still have non-negative
     *                nanos values that count forward in time. Must
     *                be from 0 to 999,999,999 inclusive.
     * @return The converted date value.
     */
    private <T> T toDate(final Class<T> type, final long seconds,
            final int nanos) {
        // milliseconds
        long value = seconds * 1000 + nanos / 1000000;

        // java.util.Date
        if (type.equals(Date.class)) {
            return type.cast(new Date(value));
        }

        // java.sql.Date
        if (type.equals(java.sql.Date.class)) {
            return type.cast(new java.sql.Date(value));
        }

        // java.sql.Time
        if (type.equals(java.sql.Time.class)) {
            return type.cast(new java.sql.Time(value));
        }

        // java.sql.Timestamp
        if (type.equals(java.sql.Timestamp.class)) {
            java.sql.Timestamp stamp = new java.sql.Timestamp(value);
            stamp.setNanos(nanos);
            return type.cast(stamp);
        }

        // java.time.LocalDateTime
        if (type.equals(LocalDate.class)) {
            final LocalDate localDate = Instant.ofEpochSecond(seconds, nanos)
                    .atZone(getZoneId()).toLocalDate();
            return type.cast(localDate);
        }

        // java.time.LocalDateTime
        if (type.equals(LocalDateTime.class)) {
            final LocalDateTime localDateTime = Instant
                    .ofEpochSecond(seconds, nanos).atZone(getZoneId())
                    .toLocalDateTime();
            return type.cast(localDateTime);
        }

        // java.time.ZonedDateTime
        if (type.equals(ZonedDateTime.class)) {
            final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(
                    Instant.ofEpochSecond(seconds, nanos), getZoneId());
            return type.cast(zonedDateTime);
        }

        // java.time.OffsetDateTime
        if (type.equals(OffsetDateTime.class)) {
            final OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(
                    Instant.ofEpochSecond(seconds, nanos), getZoneId());
            return type.cast(offsetDateTime);
        }
        
        // java.time.Instant
        if (type.equals(Instant.class)) {
            final Instant instant = Instant.ofEpochSecond(seconds, nanos);
            return type.cast(instant);
        }

        // java.util.Calendar
        if (type.equals(Calendar.class)) {
            Calendar calendar = null;
            if (locale == null && this.getTimeZone() == null) {
                calendar = Calendar.getInstance();
            } else if (locale == null) {
                calendar = Calendar.getInstance(this.getTimeZone());
            } else if (this.getTimeZone() == null) {
                calendar = Calendar.getInstance(locale);
            } else {
                calendar = Calendar.getInstance(this.getTimeZone(), locale);
            }
            calendar.setTime(new Date(value));
            calendar.setLenient(false);
            return type.cast(calendar);
        }

        final String msg = toString(getClass())
                + " cannot handle conversion to '" + toString(type) + "'";
        if (log().isWarnEnabled()) {
            log().warn("    " + msg);
        }
        throw new ConversionException(msg);
    }
    
    /**
     * Default String to Date conversion.
     * <p>
     * This method handles conversion from a String to the following types:
     * <ul>
     *     <li>{@code java.sql.Date}</li>
     *     <li>{@code java.sql.Time}</li>
     *     <li>{@code java.sql.Timestamp}</li>
     * </ul>
     * <p>
     * <strong>N.B.</strong> No default String conversion
     * mechanism is provided for {@code java.util.Date}
     * and {@code java.util.Calendar} type.
     *
     * @param <T> The target type
     * @param type The date type to convert to
     * @param value The String value to convert.
     * @return The converted Number value.
     */
    private <T> T toDate(final Class<T> type, final String value) {
        // java.sql.Date
        if (type.equals(java.sql.Date.class)) {
            try {
                return type.cast(java.sql.Date.valueOf(value));
            } catch (final IllegalArgumentException e) {
                throw new ConversionException(
                        "String must be in JDBC format [yyyy-MM-dd] to create a java.sql.Date");
            }
        }

        // java.sql.Time
        if (type.equals(java.sql.Time.class)) {
            try {
                return type.cast(java.sql.Time.valueOf(value));
            } catch (final IllegalArgumentException e) {
                throw new ConversionException(
                        "String must be in JDBC format [HH:mm:ss] to create a java.sql.Time");
            }
        }

        // java.sql.Timestamp
        if (type.equals(java.sql.Timestamp.class)) {
            try {
                return type.cast(java.sql.Timestamp.valueOf(value));
            } catch (final IllegalArgumentException e) {
                throw new ConversionException(
                        "String must be in JDBC format [yyyy-MM-dd HH:mm:ss.fffffffff] " +
                        "to create a java.sql.Timestamp");
            }
        }

        final String msg = toString(getClass()) + " does not support default String to '"
                   + toString(type) + "' conversion.";
        if (log().isWarnEnabled()) {
            log().warn("    " + msg);
            log().warn("    (N.B. Re-configure Converter or use alternative implementation)");
        }
        throw new ConversionException(msg);
    }

    /**
     * Gets a {@code DateTimeFormatter} for the Locale.
     * @param locale The Locale to create the Format with (may be null)
     * @param zoneId The Time Zone create the Format with (may be null)
     * @param value format Value
     *
     * @return A Date Format.
     */
    protected DateTimeFormatter getFormat(final Locale locale, final ZoneId zoneId,String value) {
        if (value.length()>10) {
          return getFormat(locale,zoneId,0);
        }else {
          return getFormat(locale,zoneId,1);
        }
    }
    
    /**
     * Gets a {@code DateTimeFormatter} for the Locale.
     * @param locale The Locale to create the Format with (may be null)
     * @param zoneId The Time Zone create the Format with (may be null)
     * @param type 0:LocalizedDateTime、1:LocalizedDate、2:LocalizedTime
     * @return
     */
    protected DateTimeFormatter getFormat(final Locale locale, final ZoneId zoneId,final int type) {
      DateTimeFormatter format = null;
      //DateTime
      if (type==0) {
        format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
      }else if (type==1){
        format = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
      }else if (type==2) {
        format = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
      }
      if (locale != null) {
          format = format.withLocale(locale);
      }
      if (zoneId != null ) {
          format = format.withZone(zoneId);
      }
      return format;
    }

    /**
     * Create a date format for the specified pattern.
     *
     * @param pattern The date pattern
     * @return The DateTimeFormatter
     */
    private DateTimeFormatter getFormat(final String pattern) {
        final DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
        if (zoneId != null ) {
            return format.withZone(getZoneId());
        }
        return format;
    }

    /**
     * Parse a String date value using the set of patterns.
     *
     * @param sourceType The type of the value being converted
     * @param targetType The type to convert the value to.
     * @param value The String date value.
     *
     * @return The converted Date object.
     * @throws Exception if an error occurs parsing the date.
     */
    private TemporalAccessor parse(final Class<?> sourceType, final Class<?> targetType, final String value) throws Exception {
        Exception firstEx = null;
        for (final String pattern : patterns) {
            try {
                final DateTimeFormatter format = getFormat(pattern);
                final TemporalAccessor temporalAccessor = parse(sourceType, targetType, value, format);
                return temporalAccessor;
            } catch (final Exception ex) {
                if (firstEx == null) {
                    firstEx = ex;
                }
            }
        }
        if (patterns.length > 1) {
            throw new ConversionException("Error converting '" + toString(sourceType) + "' to '" + toString(targetType)
                    + "' using  patterns '" + displayPatterns + "'");
        }
        throw firstEx;
    }

    /**
     * Parse a String into a {@code Calendar} object
     * using the specified {@code DateFormat}.
     *
     * @param sourceType The type of the value being converted
     * @param targetType The type to convert the value to
     * @param value The String date value.
     * @param format The DateFormat to parse the String value.
     *
     * @return The converted Calendar object.
     * @throws ConversionException if the String cannot be converted.
     */
    private TemporalAccessor parse(final Class<?> sourceType, final Class<?> targetType, final String value,
            DateTimeFormatter format) {
        logFormat("Parsing", format);
        final ParsePosition pos = new ParsePosition(0);
        final TemporalAccessor parsedDate = format.parse(value, pos); // ignore the result (use the Calendar)
        if (pos.getErrorIndex() >= 0 || pos.getIndex() != value.length() || parsedDate == null) {
            String msg = "Error converting '" + toString(sourceType) + "' to '" + toString(targetType) + "'";
            if (format instanceof DateTimeFormatter) {
                //msg += " using pattern '" + ((SimpleDateFormat)format).toPattern() + "'";
                msg += " using pattern '" + ((DateTimeFormatter)format).toString() + "'";
            }
            if (log().isDebugEnabled()) {
                log().debug("    " + msg);
            }
            throw new ConversionException(msg);
        }

        if (checkZonedDateTime(parsedDate)) {
          return ZonedDateTime.from(parsedDate);
        } else if(checkOffsetDateTime(parsedDate)) {
          return OffsetDateTime.from(parsedDate);
        } else if (checkLocalDateTime(parsedDate)) {
          return LocalDateTime.from(parsedDate);
        } else if (checkLocalDate(parsedDate)) {
          return LocalDate.from(parsedDate);
        } else if (checkLocalTime(parsedDate)) {
          return LocalTime.from(parsedDate);
        } else  {
          return Instant.from(parsedDate);
        } 
    }

    /**
     * Check ZonedDateTime from TemporalAccessor.
     */
    public boolean checkZonedDateTime(TemporalAccessor temporal) {
        if (temporal instanceof ZonedDateTime) {
          return true;
        }
        if (temporal.query(TemporalQueries.offset())!=null) {
          return false;
        }
        if (temporal.query(TemporalQueries.zone())==null) {
          return false;
        }
        if (temporal.isSupported(INSTANT_SECONDS)) {
          return true;
        }
        return checkLocalDateTime(temporal);
    }
    /**
     * Check LocalDateTime from TemporalAccessor.
     */
    public boolean checkLocalDateTime(TemporalAccessor temporal) {
        if (temporal instanceof LocalDateTime) {
          return true;
        }
        if (temporal.query(TemporalQueries.localDate())==null) {
          return false;
        }
        if (temporal.query(TemporalQueries.localTime())==null) {
          return false;
        }
        return true;
    }
    /**
     * Check LocalDate from TemporalAccessor.
     */
    public boolean checkLocalDate(TemporalAccessor temporal) {
        if (temporal instanceof LocalDate) {
          return true;
        }
        if (temporal.query(TemporalQueries.localDate())==null) {
          return false;
        }
        if (temporal.query(TemporalQueries.localTime())!=null) {
          return false;
        }
        return true;
    }
    /**
     * Check LocalTime from TemporalAccessor.
     */
    public boolean checkLocalTime(TemporalAccessor temporal) {
        if (temporal instanceof LocalTime) {
          return true;
        }
        if (temporal.query(TemporalQueries.localDate())!=null) {
          return false;
        }
        if (temporal.query(TemporalQueries.localTime())==null) {
          return false;
        }
        return true;
    }
    /**
     * Check OffsetDateTime from TemporalAccessor.
     */
    public boolean checkOffsetDateTime(TemporalAccessor temporal) {
        if (temporal instanceof OffsetDateTime) {
          return true;
        }
        if (temporal.query(TemporalQueries.offset())==null) {
          return false;
        }
        if (temporal.isSupported(INSTANT_SECONDS)) {
          return true;
        }
        return checkLocalDateTime(temporal);
    }

    /**
     * Provide a String representation of this date/time converter.
     *
     * @return A String representation of this date/time converter
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(toString(getClass()));
        buffer.append("[UseDefault=");
        buffer.append(isUseDefault());
        buffer.append(", UseLocaleFormat=");
        buffer.append(useLocaleFormat);
        if (displayPatterns != null) {
            buffer.append(", Patterns={");
            buffer.append(displayPatterns);
            buffer.append('}');
        }
        if (locale != null) {
            buffer.append(", Locale=");
            buffer.append(locale);
        }
        if (zoneId != null) {
            buffer.append(", ZoneId=");
            buffer.append(zoneId);
        }
        buffer.append(']');
        return buffer.toString();
    }

    /**
     * Log the {@code DateFormat} creation.
     * @param action The action the format is being used for
     * @param format The Date format
     */
    private void logFormat(final String action, final DateTimeFormatter format) {
        if (log().isDebugEnabled()) {
            final StringBuilder buffer = new StringBuilder(45);
            buffer.append("    ");
            buffer.append(action);
            buffer.append(" with Format");
            if (format instanceof DateTimeFormatter) {
                buffer.append("[");
                buffer.append(((DateTimeFormatter)format).toString());
                //buffer.append(((SimpleDateFormat)format).toPattern());
                buffer.append("]");
            }
            buffer.append(" for ");
            if (locale == null) {
                buffer.append("default locale");
            } else {
                buffer.append("locale[");
                buffer.append(locale);
                buffer.append("]");
            }
            if (zoneId != null) {
                buffer.append(", ZoneId[");
                buffer.append(zoneId);
                buffer.append("]");
            }
            log().debug(buffer.toString());
        }
    }
    
    
    /**
     * Sets the {@code java.time.ZoneId</code> from the <code>java.util.Timezone}.
     *
     * @param zoneId The ZoneId.
     */
    public void setZoneId(ZoneId zoneId) {
      this.zoneId = zoneId;
    }

    /**
     * Gets the {@code java.time.ZoneId</code> from the <code>java.util.Timezone}
     * set or use the system default if no time zone is set.
     * @return the {@code ZoneId}
     */
    public ZoneId getZoneId() {
        return zoneId == null ? ZoneId.systemDefault() : zoneId;
    }
}
