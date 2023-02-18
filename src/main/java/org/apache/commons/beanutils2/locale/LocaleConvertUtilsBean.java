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

package org.apache.commons.beanutils2.locale;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.WeakFastHashMap;
import org.apache.commons.beanutils2.locale.converters.BigDecimalLocaleConverter;
import org.apache.commons.beanutils2.locale.converters.BigIntegerLocaleConverter;
import org.apache.commons.beanutils2.locale.converters.ByteLocaleConverter;
import org.apache.commons.beanutils2.locale.converters.DoubleLocaleConverter;
import org.apache.commons.beanutils2.locale.converters.FloatLocaleConverter;
import org.apache.commons.beanutils2.locale.converters.IntegerLocaleConverter;
import org.apache.commons.beanutils2.locale.converters.LongLocaleConverter;
import org.apache.commons.beanutils2.locale.converters.ShortLocaleConverter;
import org.apache.commons.beanutils2.locale.converters.StringLocaleConverter;
import org.apache.commons.beanutils2.sql.converters.locale.SqlDateLocaleConverter;
import org.apache.commons.beanutils2.sql.converters.locale.SqlTimeLocaleConverter;
import org.apache.commons.beanutils2.sql.converters.locale.SqlTimestampLocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Utility methods for converting locale-sensitive String scalar values to objects of the
 * specified Class, String arrays to arrays of the specified Class and
 * object to locale-sensitive String scalar value.</p>
 *
 * <p>This class provides the implementations used by the static utility methods in
 * {@link LocaleConvertUtils}.</p>
 *
 * <p>The actual {@link LocaleConverter} instance to be used
 * can be registered for each possible destination Class. Unless you override them, standard
 * {@link LocaleConverter} instances are provided for all of the following
 * destination Classes:</p>
 * <ul>
 * <li>java.lang.BigDecimal</li>
 * <li>java.lang.BigInteger</li>
 * <li>byte and java.lang.Byte</li>
 * <li>double and java.lang.Double</li>
 * <li>float and java.lang.Float</li>
 * <li>int and java.lang.Integer</li>
 * <li>long and java.lang.Long</li>
 * <li>short and java.lang.Short</li>
 * <li>java.lang.String</li>
 * <li>java.sql.Date</li>
 * <li>java.sql.Time</li>
 * <li>java.sql.Timestamp</li>
 * </ul>
 *
 * <p>For backwards compatibility, the standard locale converters
 * for primitive types (and the corresponding wrapper classes).
 *
 * If you prefer to have another {@link LocaleConverter}
 * thrown instead, replace the standard {@link LocaleConverter} instances
 * with ones created with the one of the appropriate constructors.
 *
 * It's important that {@link LocaleConverter} should be registered for
 * the specified locale and Class (or primitive type).
 *
 * @since 1.7
 */
public class LocaleConvertUtilsBean {

    /** The {@code Log} instance for this class. */
    private static final Log LOG = LogFactory.getLog(LocaleConvertUtilsBean.class);

    /**
     * Gets singleton instance.
     * This is the same as the instance used by the default {@link LocaleBeanUtilsBean} singleton.
     * @return the singleton instance
     */
    public static LocaleConvertUtilsBean getInstance() {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getLocaleConvertUtils();
    }

    /** The locale - default for conversion. */
    private Locale defaultLocale = Locale.getDefault();

    /** Indicate whether the pattern is localized or not */
    private boolean applyLocalized;

    /**
     * Every entry of the mapConverters is:
     * <ul>
     * <li>key = locale</li>
     * <li>value = map of converters for the certain locale.</li>
     * <ul>
     */
    private final WeakFastHashMap<Locale, Map<Class<?>, LocaleConverter<?>>> mapConverters = new WeakFastHashMap<>();

    /**
     *  Makes the state by default (deregisters all converters for all locales)
     *  and then registers default locale converters.
     */
    public LocaleConvertUtilsBean() {
        mapConverters.setFast(false);
        deregister();
        mapConverters.setFast(true);
    }

    /**
     * Convert the specified locale-sensitive value into a String.
     *
     * @param value The Value to be converted
     * @return the converted value
     *
     * @throws ConversionException if thrown by an
     * underlying Converter
     */
    public String convert(final Object value) {
        return convert(value, defaultLocale, null);
    }

    /**
     * Convert the specified locale-sensitive value into a String
     * using the particular conversion pattern.
     *
     * @param value The Value to be converted
     * @param locale The locale
     * @param pattern The conversion pattern
     * @return the converted value
     *
     * @throws ConversionException if thrown by an
     * underlying Converter
     */
    public String convert(final Object value, final Locale locale, final String pattern) {
        final LocaleConverter<String> converter = lookup(String.class, locale);
        return converter.convert(String.class, value, pattern);
    }

    /**
     * Convert the specified locale-sensitive value into a String
     * using the conversion pattern.
     *
     * @param value The Value to be converted
     * @param pattern       The conversion pattern
     * @return the converted value
     *
     * @throws ConversionException if thrown by an
     * underlying Converter
     */
    public String convert(final Object value, final String pattern) {
        return convert(value, defaultLocale, pattern);
    }

    /**
     * Convert the specified value to an object of the specified class (if
     * possible).  Otherwise, return a String representation of the value.
     *
     * @param value The String scalar value to be converted
     * @param clazz The Data type to which this value should be converted.
     * @return the converted value
     *
     * @throws ConversionException if thrown by an
     * underlying Converter
     */
    public Object convert(final String value, final Class<?> clazz) {
        return convert(value, clazz, defaultLocale, null);
    }

    /**
     * Convert the specified value to an object of the specified class (if
     * possible) using the conversion pattern. Otherwise, return a String
     * representation of the value.
     *
     * @param value The String scalar value to be converted
     * @param clazz The Data type to which this value should be converted.
     * @param locale The locale
     * @param pattern The conversion pattern
     * @return the converted value
     *
     * @throws ConversionException if thrown by an
     * underlying Converter
     */
    public Object convert(final String value, final Class<?> clazz, final Locale locale, final String pattern) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Convert string " + value + " to class " +
                    clazz.getName() + " using " + locale +
                    " locale and " + pattern + " pattern");
        }

        Class<?> targetClass = clazz;
        LocaleConverter converter = lookup(clazz, locale);

        if (converter == null) {
            converter = lookup(String.class, locale);
            targetClass = String.class;
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("  Using converter " + converter);
        }

        return converter.convert(targetClass, value, pattern);
    }

    /**
     * Convert the specified value to an object of the specified class (if
     * possible) using the conversion pattern. Otherwise, return a String
     * representation of the value.
     *
     * @param value The String scalar value to be converted
     * @param clazz The Data type to which this value should be converted.
     * @param pattern The conversion pattern
     * @return the converted value
     *
     * @throws ConversionException if thrown by an
     * underlying Converter
     */
    public Object convert(final String value, final Class<?> clazz, final String pattern) {
        return convert(value, clazz, defaultLocale, pattern);
    }

    /**
        * Convert an array of specified values to an array of objects of the
        * specified class (if possible) .
        *
        * @param values Value to be converted (may be null)
        * @param clazz Java array or element class to be converted to
        * @return the converted value
        *
         * @throws ConversionException if thrown by an
         * underlying Converter
        */
       public Object convert(final String[] values, final Class<?> clazz) {
           return convert(values, clazz, getDefaultLocale(), null);
       }

    /**
     * Convert an array of specified values to an array of objects of the
     * specified class (if possible) using the conversion pattern.
     *
     * @param <T> The result component type
     * @param values Value to be converted (may be null)
     * @param clazz Java array or element class to be converted to
     * @param locale The locale
     * @param pattern The conversion pattern
     * @return the converted value
     *
     * @throws ConversionException if thrown by an
     * underlying Converter
     */
    public <T> T[] convert(final String[] values, final Class<T> clazz, final Locale locale, final String pattern) {
        Class<?> type = clazz;
        if (clazz.isArray()) {
            type = clazz.getComponentType();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Convert String[" + values.length + "] to class " +
                    type.getName() + "[] using " + locale +
                    " locale and " + pattern + " pattern");
        }

        final T[] array = (T[]) Array.newInstance(type, values.length);
        for (int i = 0; i < values.length; i++) {
            Array.set(array, i, convert(values[i], type, locale, pattern));
        }
        return array;
    }

    /**
     * Convert an array of specified values to an array of objects of the
     * specified class (if possible) using the conversion pattern.
     *
     * @param <T> The array component type
     * @param values Value to be converted (may be null)
     * @param clazz Java array or element class to be converted to
     * @param pattern The conversion pattern
     * @return the converted value
     *
     * @throws ConversionException if thrown by an
     * underlying Converter
     */
    public <T> T[] convert(final String[] values, final Class<T> clazz, final String pattern) {
        return convert(values, clazz, getDefaultLocale(), pattern);
    }

    /**
     * Create all {@link LocaleConverter} types for specified locale.
     *
     * @param locale The Locale
     * @return The map instance contains the all {@link LocaleConverter} types
     *  for the specified locale.
     */
    protected Map<Class<?>, LocaleConverter<?>> create(final Locale locale) {
        final WeakFastHashMap<Class<?>, LocaleConverter<?>> converter = new WeakFastHashMap<>();
        converter.setFast(false);

        converter.put(BigDecimal.class, BigDecimalLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());
        converter.put(BigInteger.class, BigIntegerLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());

        converter.put(Byte.class, ByteLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());
        converter.put(Byte.TYPE, ByteLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());

        converter.put(Double.class, DoubleLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());
        converter.put(Double.TYPE, DoubleLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());

        converter.put(Float.class, FloatLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());
        converter.put(Float.TYPE, FloatLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());

        converter.put(Integer.class, IntegerLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());
        converter.put(Integer.TYPE, IntegerLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());

        converter.put(Long.class, LongLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());
        converter.put(Long.TYPE, LongLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());

        converter.put(Short.class, ShortLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());
        converter.put(Short.TYPE, ShortLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());

        converter.put(String.class, StringLocaleConverter.builder().setLocale(locale).setLocalizedPattern(applyLocalized).get());

        // conversion format patterns of java.sql.* types should correspond to default
        // behavior of toString and valueOf methods of these classes
        converter.put(java.sql.Date.class, SqlDateLocaleConverter.builder().setLocale(locale).setPattern("yyyy-MM-dd").get());
        converter.put(java.sql.Time.class, SqlTimeLocaleConverter.builder().setLocale(locale).setPattern("HH:mm:ss").get());
        converter.put(java.sql.Timestamp.class, SqlTimestampLocaleConverter.builder().setLocale(locale).setPattern("yyyy-MM-dd HH:mm:ss.S").get());

        converter.setFast(true);

        return converter;
    }

    /**
     * Remove any registered {@link LocaleConverter}.
     */
    public void deregister() {
        final Map<Class<?>, LocaleConverter<?>> defaultConverter = lookup(defaultLocale);
        mapConverters.setFast(false);

        mapConverters.clear();
        mapConverters.put(defaultLocale, defaultConverter);

        mapConverters.setFast(true);
    }

    /**
     * Remove any registered {@link LocaleConverter} for the specified locale and Class.
     *
     * @param clazz Class for which to remove a registered Converter
     * @param locale The locale
     */
    public void deregister(final Class<?> clazz, final Locale locale) {
        lookup(locale).remove(clazz);
    }

    /**
     * Remove any registered {@link LocaleConverter} for the specified locale
     *
     * @param locale The locale
     */
    public void deregister(final Locale locale) {
        mapConverters.remove(locale);
    }

    /**
     * getter for applyLocalized
     *
     * @return {@code true} if pattern is localized,
     * otherwise {@code false}
     */
    public boolean getApplyLocalized() {
        return applyLocalized;
    }

    /**
     * getter for defaultLocale.
     * @return the default locale
     */
    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * Look up and return any registered {@link LocaleConverter} for the specified
     * destination class and locale; if there is no registered Converter, return
     * {@code null}.
     *
     * @param <T> The converter type.
     * @param clazz Class for which to return a registered Converter
     * @param locale The Locale
     * @return The registered locale Converter, if any
     */
    public <T> LocaleConverter<T> lookup(final Class<T> clazz, final Locale locale) {
        final LocaleConverter<T> converter = (LocaleConverter<T>) lookup(locale).get(clazz);

        if (LOG.isTraceEnabled()) {
            LOG.trace("LocaleConverter:" + converter);
        }

        return converter;
    }

    /**
     * Look up and return any registered map instance for the specified locale;
     * if there is no registered one, return {@code null}.
     *
     * @param locale The Locale
     * @return The map instance contains the all {@link LocaleConverter} types for
     *  the specified locale.
     */
    protected Map<Class<?>, LocaleConverter<?>> lookup(final Locale locale) {
        return mapConverters.computeIfAbsent(locale == null ? defaultLocale : locale, this::create);
    }

    /**
     * Register a custom {@link LocaleConverter} for the specified destination
     * {@code Class}, replacing any previously registered converter.
     *
     * @param <T> The converter type.
     * @param converter The LocaleConverter to be registered
     * @param clazz The Destination class for conversions performed by this
     *  Converter
     * @param locale The locale
     */
    public <T> void register(final LocaleConverter<T> converter, final Class<T> clazz, final Locale locale) {
        lookup(locale).put(clazz, converter);
    }

    /**
     * setter for applyLocalized
     *
     * @param newApplyLocalized {@code true} if pattern is localized,
     * otherwise {@code false}
     */
    public void setApplyLocalized(final boolean newApplyLocalized) {
        applyLocalized = newApplyLocalized;
    }

    /**
     * setter for defaultLocale.
     * @param locale the default locale
     */
    public void setDefaultLocale(final Locale locale) {
        if (locale == null) {
            defaultLocale = Locale.getDefault();
        }
        else {
            defaultLocale = locale;
        }
    }
}
