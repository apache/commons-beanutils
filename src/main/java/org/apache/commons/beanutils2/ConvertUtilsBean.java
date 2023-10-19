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

package org.apache.commons.beanutils2;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.beanutils2.converters.ArrayConverter;
import org.apache.commons.beanutils2.converters.BigDecimalConverter;
import org.apache.commons.beanutils2.converters.BigIntegerConverter;
import org.apache.commons.beanutils2.converters.BooleanConverter;
import org.apache.commons.beanutils2.converters.ByteConverter;
import org.apache.commons.beanutils2.converters.CalendarConverter;
import org.apache.commons.beanutils2.converters.CharacterConverter;
import org.apache.commons.beanutils2.converters.ClassConverter;
import org.apache.commons.beanutils2.converters.ConverterFacade;
import org.apache.commons.beanutils2.converters.DateConverter;
import org.apache.commons.beanutils2.converters.DoubleConverter;
import org.apache.commons.beanutils2.converters.DurationConverter;
import org.apache.commons.beanutils2.converters.EnumConverter;
import org.apache.commons.beanutils2.converters.FileConverter;
import org.apache.commons.beanutils2.converters.FloatConverter;
import org.apache.commons.beanutils2.converters.IntegerConverter;
import org.apache.commons.beanutils2.converters.LocalDateConverter;
import org.apache.commons.beanutils2.converters.LocalDateTimeConverter;
import org.apache.commons.beanutils2.converters.LocalTimeConverter;
import org.apache.commons.beanutils2.converters.LongConverter;
import org.apache.commons.beanutils2.converters.MonthDayConverter;
import org.apache.commons.beanutils2.converters.OffsetDateTimeConverter;
import org.apache.commons.beanutils2.converters.OffsetTimeConverter;
import org.apache.commons.beanutils2.converters.PathConverter;
import org.apache.commons.beanutils2.converters.PeriodConverter;
import org.apache.commons.beanutils2.converters.ShortConverter;
import org.apache.commons.beanutils2.converters.StringConverter;
import org.apache.commons.beanutils2.converters.URIConverter;
import org.apache.commons.beanutils2.converters.URLConverter;
import org.apache.commons.beanutils2.converters.UUIDConverter;
import org.apache.commons.beanutils2.converters.YearConverter;
import org.apache.commons.beanutils2.converters.YearMonthConverter;
import org.apache.commons.beanutils2.converters.ZoneIdConverter;
import org.apache.commons.beanutils2.converters.ZoneOffsetConverter;
import org.apache.commons.beanutils2.converters.ZonedDateTimeConverter;
import org.apache.commons.beanutils2.sql.converters.SqlDateConverter;
import org.apache.commons.beanutils2.sql.converters.SqlTimeConverter;
import org.apache.commons.beanutils2.sql.converters.SqlTimestampConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Utility methods for converting String scalar values to objects of the
 * specified Class, String arrays to arrays of the specified Class.  The
 * actual {@link Converter} instance to be used can be registered for each
 * possible destination Class.  Unless you override them, standard
 * {@link Converter} instances are provided for all of the following
 * destination Classes:</p>
 * <ul>
 * <li>java.lang.BigDecimal (no default value)</li>
 * <li>java.lang.BigInteger (no default value)</li>
 * <li>boolean and java.lang.Boolean (default to false)</li>
 * <li>byte and java.lang.Byte (default to zero)</li>
 * <li>char and java.lang.Character (default to a space)</li>
 * <li>java.lang.Class (no default value)</li>
 * <li>double and java.lang.Double (default to zero)</li>
 * <li>float and java.lang.Float (default to zero)</li>
 * <li>int and java.lang.Integer (default to zero)</li>
 * <li>long and java.lang.Long (default to zero)</li>
 * <li>short and java.lang.Short (default to zero)</li>
 * <li>java.lang.String (default to null)</li>
 * <li>java.lang.Enum (default to null)</li>
 * <li>java.io.File (no default value)</li>
 * <li>java.nio.file.Path (no default value)</li>
 * <li>java.net.URL (no default value)</li>
 * <li>java.net.URI (no default value)</li>
 * <li>java.util.UUID (no default value)</li>
 * <li>java.sql.Date (no default value)</li>
 * <li>java.sql.Time (no default value)</li>
 * <li>java.sql.Timestamp (no default value)</li>
 * <li>java.time.LocalDate (no default value)</li>
 * <li>java.time.LocalDateTime (no default value)</li>
 * <li>java.time.LocalTime (no default value)</li>
 * <li>java.time.OffsetDateTime (no default value)</li>
 * <li>java.time.OffsetTime (no default value)</li>
 * <li>java.time.ZonedDateTime (no default value)</li>
 * <li>java.time.Duration (no default value)</li>
 * <li>java.time.MonthDay (no default value)</li>
 * <li>java.time.Period (no default value)</li>
 * <li>java.time.Year (no default value)</li>
 * <li>java.time.YearMonth (no default value)</li>
 * <li>java.time.ZoneId (no default value)</li>
 * <li>java.time.ZoneOffset (no default value)</li>
 * </ul>
 *
 * <p>For backwards compatibility, the standard Converters for primitive
 * types (and the corresponding wrapper classes) return a defined
 * default value when a conversion error occurs.  If you prefer to have a
 * {@link ConversionException} thrown instead, replace the standard Converter
 * instances with instances created with the zero-arguments constructor.  For
 * example, to cause the Converters for integers to throw an exception on
 * conversion errors, you could do this:</p>
 * <pre>
 *   // No-args constructor gets the version that throws exceptions
 *   Converter myConverter =
 *    new org.apache.commons.beanutils2.converter.IntegerConverter();
 *   ConvertUtils.register(myConverter, Integer.TYPE);    // Native type
 *   ConvertUtils.register(myConverter, Integer.class);   // Wrapper class
 * </pre>
 *
 * <p>
 * Converters generally treat null input as if it were invalid
 * input, that is, they return their default value if one was specified when the
 * converter was constructed, and throw an exception otherwise. If you prefer
 * nulls to be preserved for converters that are converting to objects (not
 * primitives) then register a converter as above, passing a default value of
 * null to the converter constructor (and of course registering that converter
 * only for the .class target).
 * </p>
 *
 * <p>
 * When a converter is listed above as having no default value, then that
 * converter will throw an exception when passed null or an invalid value
 * as its input. In particular, by default the BigInteger and BigDecimal
 * converters have no default (and are therefore somewhat inconsistent
 * with the other numerical converters which all have zero as their default).
 * </p>
 *
 * <p>
 * Converters that generate <i>arrays</i> of each of the primitive types are
 * also automatically configured (including String[]). When passed null
 * or invalid input, these return an empty array (not null). See class
 * AbstractArrayConverter for the supported input formats for these converters.
 * </p>
 *
 * @since 1.7
 */
public class ConvertUtilsBean {

    private static final Integer ZERO = Integer.valueOf(0);
    private static final Character SPACE = Character.valueOf(' ');

    /**
     * The {@code Log} instance for this class.
     */
    private static final Log LOG = LogFactory.getLog(ConvertUtilsBean.class);

    /**
     * Get singleton instance
     * @return The singleton instance
     */
    protected static ConvertUtilsBean getInstance() {
        return BeanUtilsBean.getInstance().getConvertUtils();
    }

    /**
     * The set of {@link Converter}s that can be used to convert Strings
     * into objects of a specified Class, keyed by the destination Class.
     */
    private final WeakFastHashMap<Class<?>, Converter<?>> converters = new WeakFastHashMap<>();

    /** Constructs a bean with standard converters registered */
    public ConvertUtilsBean() {
        converters.setFast(false);
        deregister();
        converters.setFast(true);
    }

    /**
     * Convert the specified value into a String.  If the specified value
     * is an array, the first element (converted to a String) will be
     * returned.  The registered {@link Converter} for the
     * {@code java.lang.String} class will be used, which allows
     * applications to customize Object-&gt;String conversions (the default
     * implementation simply uses toString()).
     *
     * @param value Value to be converted (may be null)
     * @return The converted String value or null if value is null
     */
    public String convert(Object value) {

        if (value == null) {
            return null;
        }
        if (!value.getClass().isArray()) {
            final Converter<String> converter = lookup(String.class);
            return converter.convert(String.class, value);
        }
        if (Array.getLength(value) < 1) {
            return null;
        }
        value = Array.get(value, 0);
        if (value == null) {
            return null;
        }
        final Converter<String> converter = lookup(String.class);
        return converter.convert(String.class, value);

    }

    /**
     * Convert the value to an object of the specified class (if
     * possible). If no converter for the desired target type is registered,
     * the passed in object is returned unchanged.
     *
     * @param <T> The Class type.
     * @param value Value to be converted (may be null)
     * @param targetType Class of the value to be converted to (must not be null)
     * @return The converted value
     *
     * @throws ConversionException if thrown by an underlying Converter
     */
    public <T> Object convert(final Object value, final Class<T> targetType) {
        final boolean nullValue = value == null;
        final Class<?> sourceType = nullValue ? null : value.getClass();

        if (LOG.isDebugEnabled()) {
            if (nullValue) {
                LOG.debug("Convert null value to type '" + targetType.getName() + "'");
            } else {
                LOG.debug("Convert type '" + sourceType.getName() + "' value '" + value + "' to type '"
                    + targetType.getName() + "'");
            }
        }

        Object converted = value;
        final Converter<T> converter = lookup(sourceType, targetType);
        if (converter != null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("  Using converter " + converter);
            }
            converted = converter.convert(targetType, value);
        }
        if (String.class.equals(targetType) && converted != null && !(converted instanceof String)) {

            // NOTE: For backwards compatibility, if the Converter
            // doesn't handle conversion-->String then
            // use the registered String Converter
            final Converter<String> strConverter = lookup(String.class);
            if (strConverter != null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("  Using converter " + converter);
                }
                converted = strConverter.convert(String.class, converted);
            }

            // If the object still isn't a String, use toString() method
            if (converted != null && !(converted instanceof String)) {
                converted = converted.toString();
            }

        }
        return converted;
    }

    /**
     * Convert the specified value to an object of the specified class (if
     * possible). Otherwise, return a {@link String} representation of the value.
     *
     * @param <T> The <em>desired</em> return type
     * @param value Value to be converted (may be null)
     * @param clazz Java class to be converted to (must not be null)
     * @return The converted value
     *
     * @throws ConversionException if thrown by an underlying Converter
     */
    public <T> Object convert(final String value, final Class<T> clazz) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Convert string '" + value + "' to class '" + clazz.getName() + "'");
        }
        final Converter<T> converter = lookup(clazz);
        if (converter == null) {
            final Converter<String> sConverter = lookup(String.class);
            if (LOG.isTraceEnabled()) {
                LOG.trace("  Using converter " + converter);
            }
            return sConverter.convert(String.class, value);
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("  Using converter " + converter);
        }
        return converter.convert(clazz, value);
    }

    /**
     * Convert an array of specified values to an array of objects of the
     * specified class (if possible).  If the specified Java class is itself
     * an array class, this class will be the type of the returned value.
     * Otherwise, an array will be constructed whose component type is the
     * specified class.
     *
     * @param <T> The Class type.
     * @param values Array of values to be converted
     * @param clazz Java array or element class to be converted to (must not be null)
     * @return The converted value
     *
     * @throws ConversionException if thrown by an underlying Converter
     */
    public <T> Object convert(final String[] values, final Class<T> clazz) {
        final Class<?> type = clazz.isArray() ? clazz.getComponentType() : clazz;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Convert String[" + values.length + "] to class '" + type.getName() + "[]'");
        }
        Converter converter = lookup(type);
        if (converter == null) {
            converter = lookup(String.class);
        }
        return convert(values, type, converter);
    }

    private <T> Object convert(final String[] values, final Class<T> type, final Converter<T> converter) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("  Using converter " + converter);
        }
        final Object array = Array.newInstance(type, values.length);
        for (int i = 0; i < values.length; i++) {
            Array.set(array, i, converter.convert(type, values[i]));
        }
        return array;
    }

    /**
     * Remove all registered {@link Converter}s, and re-establish the
     * standard Converters.
     */
    public void deregister() {

        converters.clear();

        registerPrimitives(false);
        registerStandard(false, false);
        registerOther(true);
        registerArrays(false, 0);
        register(BigDecimal.class, new BigDecimalConverter());
        register(BigInteger.class, new BigIntegerConverter());
    }

    /**
     * Remove any registered {@link Converter} for the specified destination
     * {@code Class}.
     *
     * @param clazz Class for which to remove a registered Converter
     */
    public void deregister(final Class<?> clazz) {
        converters.remove(clazz);
    }

    /**
     * Look up and return any registered {@link Converter} for the specified
     * source and destination class; if there is no registered Converter,
     * return {@code null}.
     *
     * @param <T> The converter type.
     * @param sourceType Class of the value being converted
     * @param targetType Class of the value to be converted to
     * @return The registered {@link Converter} or {@code null} if not found
     */
    public <T> Converter<T> lookup(final Class<?> sourceType, final Class<T> targetType) {

        if (targetType == null) {
            throw new IllegalArgumentException("Target type is missing");
        }
        if (sourceType == null) {
            return lookup(targetType);
        }

        Converter converter = null;
        // Convert --> String
        if (targetType == String.class) {
            converter = lookup(sourceType);
            if (converter == null && (sourceType.isArray() ||
                        Collection.class.isAssignableFrom(sourceType))) {
                converter = lookup(String[].class);
            }
            if (converter == null) {
                converter = lookup(String.class);
            }
            return converter;
        }

        // Convert --> String array
        if (targetType == String[].class) {
            if (sourceType.isArray() || Collection.class.isAssignableFrom(sourceType)) {
                converter = lookup(sourceType);
            }
            if (converter == null) {
                converter = lookup(String[].class);
            }
            return converter;
        }

        return lookup(targetType);

    }

    /**
     * Look up and return any registered {@link Converter} for the specified
     * destination class; if there is no registered Converter, return
     * {@code null}.
     *
     * @param <T> The converter type.
     * @param clazz Class for which to return a registered Converter
     * @return The registered {@link Converter} or {@code null} if not found
     */
    @SuppressWarnings("unchecked")
    public <T> Converter<T> lookup(final Class<T> clazz) {
        return (Converter<T>) converters.get(clazz);
    }

    /**
     * Register the provided converters with the specified defaults.
     *
     * @param throwException {@code true} if the converters should
     * throw an exception when a conversion error occurs, otherwise
     * {@code false} if a default value should be used.
     * @param defaultNull {@code true}if the <i>standard</i> converters
     * (see {@link ConvertUtilsBean#registerStandard(boolean, boolean)})
     * should use a default value of {@code null</code>, otherwise <code>false}.
     * N.B. This values is ignored if {@code throwException</code> is <code>true}
     * @param defaultArraySize The size of the default array value for array converters
     * (N.B. This values is ignored if {@code throwException</code> is <code>true}).
     * Specifying a value less than zero causes a {@code null} value to be used for
     * the default.
     */
    public void register(final boolean throwException, final boolean defaultNull, final int defaultArraySize) {
        registerPrimitives(throwException);
        registerStandard(throwException, defaultNull);
        registerOther(throwException);
        registerArrays(throwException, defaultArraySize);
    }

    /** strictly for convenience since it has same parameter order as Map.put */
    private <T> void register(final Class<?> clazz, final Converter<T> converter) {
        register(new ConverterFacade<>(converter), clazz);
    }

    /**
     * Register a custom {@link Converter} for the specified destination
     * {@code Class}, replacing any previously registered Converter.
     *
     * @param converter Converter to be registered
     * @param clazz Destination class for conversions performed by this
     *  Converter
     */
    public void register(final Converter converter, final Class<?> clazz) {
        converters.put(clazz, converter);
    }

    /**
     * Register a new ArrayConverter with the specified element delegate converter
     * that returns a default array of the specified size in the event of conversion errors.
     *
     * @param componentType The component type of the array
     * @param componentConverter The converter to delegate to for the array elements
     * @param throwException Whether a conversion exception should be thrown or a default
     * value used in the event of a conversion error
     * @param defaultArraySize The size of the default array
     */
    private <T> void registerArrayConverter(final Class<T> componentType, final Converter<T> componentConverter,
            final boolean throwException, final int defaultArraySize) {
        final Class<T[]> arrayType = (Class<T[]>) Array.newInstance(componentType, 0).getClass();
        final Converter<T[]> arrayConverter;
        if (throwException) {
            arrayConverter = new ArrayConverter<>(arrayType, componentConverter);
        } else {
            arrayConverter = new ArrayConverter<>(arrayType, componentConverter, defaultArraySize);
        }
        register(arrayType, arrayConverter);
    }

    /**
     * Register array converters.
     *
     * @param throwException {@code true} if the converters should
     * throw an exception when a conversion error occurs, otherwise <code>
     * {@code false} if a default value should be used.
     * @param defaultArraySize The size of the default array value for array converters
     * (N.B. This values is ignored if {@code throwException</code> is <code>true}).
     * Specifying a value less than zero causes a <code>null<code> value to be used for
     * the default.
     */
    private void registerArrays(final boolean throwException, final int defaultArraySize) {
        // @formatter:off

        // Primitives
        registerArrayConverter(Boolean.TYPE,   new BooleanConverter(),   throwException, defaultArraySize);
        registerArrayConverter(Byte.TYPE,      new ByteConverter(),      throwException, defaultArraySize);
        registerArrayConverter(Character.TYPE, new CharacterConverter(), throwException, defaultArraySize);
        registerArrayConverter(Double.TYPE,    new DoubleConverter(),    throwException, defaultArraySize);
        registerArrayConverter(Float.TYPE,     new FloatConverter(),     throwException, defaultArraySize);
        registerArrayConverter(Integer.TYPE,   new IntegerConverter(),   throwException, defaultArraySize);
        registerArrayConverter(Long.TYPE,      new LongConverter(),      throwException, defaultArraySize);
        registerArrayConverter(Short.TYPE,     new ShortConverter(),     throwException, defaultArraySize);

        // Standard
        registerArrayConverter(BigDecimal.class, new BigDecimalConverter(), throwException, defaultArraySize);
        registerArrayConverter(BigInteger.class, new BigIntegerConverter(), throwException, defaultArraySize);
        registerArrayConverter(Boolean.class,    new BooleanConverter(),    throwException, defaultArraySize);
        registerArrayConverter(Byte.class,       new ByteConverter(),       throwException, defaultArraySize);
        registerArrayConverter(Character.class,  new CharacterConverter(),  throwException, defaultArraySize);
        registerArrayConverter(Double.class,     new DoubleConverter(),     throwException, defaultArraySize);
        registerArrayConverter(Float.class,      new FloatConverter(),      throwException, defaultArraySize);
        registerArrayConverter(Integer.class,    new IntegerConverter(),    throwException, defaultArraySize);
        registerArrayConverter(Long.class,       new LongConverter(),       throwException, defaultArraySize);
        registerArrayConverter(Short.class,      new ShortConverter(),      throwException, defaultArraySize);
        registerArrayConverter(String.class,     new StringConverter(),     throwException, defaultArraySize);

        // Other
        registerArrayConverter(Class.class,          new ClassConverter(),         throwException, defaultArraySize);
        registerArrayConverter(Enum.class,           new EnumConverter(),          throwException, defaultArraySize);
        registerArrayConverter(java.util.Date.class, new DateConverter(),          throwException, defaultArraySize);
        registerArrayConverter(Calendar.class,       new CalendarConverter(),      throwException, defaultArraySize);
        registerArrayConverter(File.class,           new FileConverter(),          throwException, defaultArraySize);
        registerArrayConverter(Path.class,           new PathConverter(),          throwException, defaultArraySize);
        registerArrayConverter(java.sql.Date.class,  new SqlDateConverter(),       throwException, defaultArraySize);
        registerArrayConverter(java.sql.Time.class,  new SqlTimeConverter(),       throwException, defaultArraySize);
        registerArrayConverter(Timestamp.class,      new SqlTimestampConverter(),  throwException, defaultArraySize);
        registerArrayConverter(URL.class,            new URLConverter(),           throwException, defaultArraySize);
        registerArrayConverter(URI.class,            new URIConverter(),           throwException, defaultArraySize);
        registerArrayConverter(UUID.class,           new UUIDConverter(),          throwException, defaultArraySize);
        registerArrayConverter(LocalDate.class,      new LocalDateConverter(),     throwException, defaultArraySize);
        registerArrayConverter(LocalDateTime.class,  new LocalDateTimeConverter(), throwException, defaultArraySize);
        registerArrayConverter(LocalTime.class,      new LocalTimeConverter(),     throwException, defaultArraySize);
        registerArrayConverter(OffsetDateTime.class, new OffsetDateTimeConverter(),throwException, defaultArraySize);
        registerArrayConverter(OffsetTime.class,     new OffsetTimeConverter(),    throwException, defaultArraySize);
        registerArrayConverter(ZonedDateTime.class,  new ZonedDateTimeConverter(), throwException, defaultArraySize);
        registerArrayConverter(Duration.class,       new DurationConverter(),      throwException, defaultArraySize);
        registerArrayConverter(MonthDay.class,       new MonthDayConverter(),      throwException, defaultArraySize);
        registerArrayConverter(Period.class,         new PeriodConverter(),        throwException, defaultArraySize);
        registerArrayConverter(Year.class,           new YearConverter(),          throwException, defaultArraySize);
        registerArrayConverter(YearMonth.class,      new YearMonthConverter(),     throwException, defaultArraySize);
        registerArrayConverter(ZoneId.class,         new ZoneIdConverter(),        throwException, defaultArraySize);
        registerArrayConverter(ZoneOffset.class,     new ZoneOffsetConverter(),    throwException, defaultArraySize);
        // @formatter:on
    }

    /**
     * Register the converters for other types.
     * </p>
     * This method registers the following converters:
     * <ul>
     *     <li>{@code Class.class} - {@link ClassConverter}</li>
     *     <li>{@code Enum.class} - {@link EnumConverter}</li>
     *     <li>{@code java.util.Date.class} - {@link DateConverter}</li>
     *     <li>{@code java.util.Calendar.class} - {@link CalendarConverter}</li>
     *     <li>{@code File.class} - {@link FileConverter}</li>
     *     <li>{@code Path.class} - {@link PathConverter}</li>
     *     <li>{@code java.sql.Date.class} - {@link SqlDateConverter}</li>
     *     <li>{@code java.sql.Time.class} - {@link SqlTimeConverter}</li>
     *     <li>{@code java.sql.Timestamp.class} - {@link SqlTimestampConverter}</li>
     *     <li>{@code URL.class} - {@link URLConverter}</li>
     *     <li>{@code URI.class} - {@link URIConverter}</li>
     *     <li>{@code UUID.class} - {@link UUIDConverter}</li>
     *     <li>{@code LocalDate.class} - {@link LocalDateConverter}</li>
     *     <li>{@code LocalDateTime.class} - {@link LocalDateTimeConverter}</li>
     *     <li>{@code LocalTime.class} - {@link LocalTimeConverter}</li>
     *     <li>{@code OffsetDateTime.class} - {@link OffsetDateTimeConverter}</li>
     *     <li>{@code OffsetTime.class} - {@link OffsetTimeConverter}</li>
     *     <li>{@code ZonedDateTime.class} - {@link ZonedDateTimeConverter}</li>
     *     <li>{@code Duration.class} - {@link DurationConverter}</li>
     *     <li>{@code MonthDay.class} - {@link MonthDayConverter}</li>
     *     <li>{@code Period.class} - {@link PeriodConverter}</li>
     *     <li>{@code Year.class} - {@link YearConverter}</li>
     *     <li>{@code YearMonth.class} - {@link YearMonthConverter}</li>
     *     <li>{@code ZoneId.class} - {@link ZoneIdConverter}</li>
     *     <li>{@code ZoneOffset.class} - {@link ZoneOffsetConverter}</li>
     * </ul>
     * @param throwException {@code true} if the converters should
     * throw an exception when a conversion error occurs, otherwise <code>
     * {@code false} if a default value should be used.
     */
    private void registerOther(final boolean throwException) {
        // @formatter:off
        register(Class.class,          throwException ? new ClassConverter<>()        : new ClassConverter<>(null));
        register(Enum.class,           throwException ? new EnumConverter()           : new EnumConverter(null));
        register(java.util.Date.class, throwException ? new DateConverter()           : new DateConverter(null));
        register(Calendar.class,       throwException ? new CalendarConverter()       : new CalendarConverter(null));
        register(File.class,           throwException ? new FileConverter()           : new FileConverter(null));
        register(Path.class,           throwException ? new PathConverter()           : new PathConverter(null));
        register(java.sql.Date.class,  throwException ? new SqlDateConverter()        : new SqlDateConverter(null));
        register(java.sql.Time.class,  throwException ? new SqlTimeConverter()        : new SqlTimeConverter(null));
        register(Timestamp.class,      throwException ? new SqlTimestampConverter()   : new SqlTimestampConverter(null));
        register(URL.class,            throwException ? new URLConverter()            : new URLConverter(null));
        register(URI.class,            throwException ? new URIConverter()            : new URIConverter(null));
        register(UUID.class,           throwException ? new UUIDConverter()           : new UUIDConverter(null));
        register(LocalDate.class,      throwException ? new LocalDateConverter()      : new LocalDateConverter(null));
        register(LocalDateTime.class,  throwException ? new LocalDateTimeConverter()  : new LocalDateTimeConverter(null));
        register(LocalTime.class,      throwException ? new LocalTimeConverter()      : new LocalTimeConverter(null));
        register(OffsetDateTime.class, throwException ? new OffsetDateTimeConverter() : new OffsetDateTimeConverter(null));
        register(OffsetTime.class,     throwException ? new OffsetTimeConverter()     : new OffsetTimeConverter(null));
        register(ZonedDateTime.class,  throwException ? new ZonedDateTimeConverter()  : new ZonedDateTimeConverter(null));
        register(Duration.class,       throwException ? new DurationConverter()       : new DurationConverter(null));
        register(MonthDay.class,       throwException ? new MonthDayConverter()       : new MonthDayConverter(null));
        register(Period.class,         throwException ? new PeriodConverter()         : new PeriodConverter(null));
        register(Year.class,           throwException ? new YearConverter()           : new YearConverter(null));
        register(YearMonth.class,      throwException ? new YearMonthConverter()      : new YearMonthConverter(null));
        register(ZoneId.class,         throwException ? new ZoneIdConverter()         : new ZoneIdConverter(null));
        register(ZoneOffset.class,     throwException ? new ZoneOffsetConverter()     : new ZoneOffsetConverter(null));
        // @formatter:on
    }

    /**
     * Register the converters for primitive types.
     * </p>
     * This method registers the following converters:
     * <ul>
     *     <li>{@code Boolean.TYPE} - {@link BooleanConverter}</li>
     *     <li>{@code Byte.TYPE} - {@link ByteConverter}</li>
     *     <li>{@code Character.TYPE} - {@link CharacterConverter}</li>
     *     <li>{@code Double.TYPE} - {@link DoubleConverter}</li>
     *     <li>{@code Float.TYPE} - {@link FloatConverter}</li>
     *     <li>{@code Integer.TYPE} - {@link IntegerConverter}</li>
     *     <li>{@code Long.TYPE} - {@link LongConverter}</li>
     *     <li>{@code Short.TYPE} - {@link ShortConverter}</li>
     * </ul>
     * @param throwException {@code true} if the converters should
     * throw an exception when a conversion error occurs, otherwise <code>
     * {@code false} if a default value should be used.
     */
    private void registerPrimitives(final boolean throwException) {
        register(Boolean.TYPE,   throwException ? new BooleanConverter()    : new BooleanConverter(Boolean.FALSE));
        register(Byte.TYPE,      throwException ? new ByteConverter()       : new ByteConverter(ZERO));
        register(Character.TYPE, throwException ? new CharacterConverter()  : new CharacterConverter(SPACE));
        register(Double.TYPE,    throwException ? new DoubleConverter()     : new DoubleConverter(ZERO));
        register(Float.TYPE,     throwException ? new FloatConverter()      : new FloatConverter(ZERO));
        register(Integer.TYPE,   throwException ? new IntegerConverter()    : new IntegerConverter(ZERO));
        register(Long.TYPE,      throwException ? new LongConverter()       : new LongConverter(ZERO));
        register(Short.TYPE,     throwException ? new ShortConverter()      : new ShortConverter(ZERO));
    }

    /**
     * Register the converters for standard types.
     * </p>
     * This method registers the following converters:
     * <ul>
     *     <li>{@code BigDecimal.class} - {@link BigDecimalConverter}</li>
     *     <li>{@code BigInteger.class} - {@link BigIntegerConverter}</li>
     *     <li>{@code Boolean.class} - {@link BooleanConverter}</li>
     *     <li>{@code Byte.class} - {@link ByteConverter}</li>
     *     <li>{@code Character.class} - {@link CharacterConverter}</li>
     *     <li>{@code Double.class} - {@link DoubleConverter}</li>
     *     <li>{@code Float.class} - {@link FloatConverter}</li>
     *     <li>{@code Integer.class} - {@link IntegerConverter}</li>
     *     <li>{@code Long.class} - {@link LongConverter}</li>
     *     <li>{@code Short.class} - {@link ShortConverter}</li>
     *     <li>{@code String.class} - {@link StringConverter}</li>
     * </ul>
     * @param throwException {@code true} if the converters should
     * throw an exception when a conversion error occurs, otherwise <code>
     * {@code false} if a default value should be used.
     * @param defaultNull {@code true}if the <i>standard</i> converters
     * (see {@link ConvertUtilsBean#registerStandard(boolean, boolean)})
     * should use a default value of {@code null</code>, otherwise <code>false}.
     * N.B. This values is ignored if {@code throwException</code> is <code>true}
     */
    private void registerStandard(final boolean throwException, final boolean defaultNull) {

        final Number     defaultNumber     = defaultNull ? null : ZERO;
        final BigDecimal bigDecDeflt       = defaultNull ? null : new BigDecimal("0.0");
        final BigInteger bigIntDeflt       = defaultNull ? null : new BigInteger("0");
        final Boolean    booleanDefault    = defaultNull ? null : Boolean.FALSE;
        final Character  charDefault       = defaultNull ? null : SPACE;
        final String     stringDefault     = defaultNull ? null : "";

        register(BigDecimal.class, throwException ? new BigDecimalConverter() : new BigDecimalConverter(bigDecDeflt));
        register(BigInteger.class, throwException ? new BigIntegerConverter() : new BigIntegerConverter(bigIntDeflt));
        register(Boolean.class,    throwException ? new BooleanConverter()    : new BooleanConverter(booleanDefault));
        register(Byte.class,       throwException ? new ByteConverter()       : new ByteConverter(defaultNumber));
        register(Character.class,  throwException ? new CharacterConverter()  : new CharacterConverter(charDefault));
        register(Double.class,     throwException ? new DoubleConverter()     : new DoubleConverter(defaultNumber));
        register(Float.class,      throwException ? new FloatConverter()      : new FloatConverter(defaultNumber));
        register(Integer.class,    throwException ? new IntegerConverter()    : new IntegerConverter(defaultNumber));
        register(Long.class,       throwException ? new LongConverter()       : new LongConverter(defaultNumber));
        register(Short.class,      throwException ? new ShortConverter()      : new ShortConverter(defaultNumber));
        register(String.class,     throwException ? new StringConverter()     : new StringConverter(stringDefault));

    }
}
