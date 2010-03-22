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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.locale.converters.BigDecimalLocaleConverter;
import org.apache.commons.beanutils.locale.converters.BigIntegerLocaleConverter;
import org.apache.commons.beanutils.locale.converters.ByteLocaleConverter;
import org.apache.commons.beanutils.locale.converters.DoubleLocaleConverter;
import org.apache.commons.beanutils.locale.converters.FloatLocaleConverter;
import org.apache.commons.beanutils.locale.converters.IntegerLocaleConverter;
import org.apache.commons.beanutils.locale.converters.LongLocaleConverter;
import org.apache.commons.beanutils.locale.converters.ShortLocaleConverter;
import org.apache.commons.beanutils.locale.converters.StringLocaleConverter;
import org.apache.commons.beanutils.locale.converters.SqlDateLocaleConverter;
import org.apache.commons.beanutils.locale.converters.SqlTimeLocaleConverter;
import org.apache.commons.beanutils.locale.converters.SqlTimestampLocaleConverter;

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
 * @author Yauheny Mikulski
 * @since 1.7
 */
public class LocaleConvertUtilsBean {
    
    /** 
     * Gets singleton instance.
     * This is the same as the instance used by the default {@link LocaleBeanUtilsBean} singleton.
     * @return the singleton instance
     */
    public static LocaleConvertUtilsBean getInstance() {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getLocaleConvertUtils();
    }

    // ----------------------------------------------------- Instance Variables

    /** The locale - default for convertion. */
    private Locale defaultLocale = Locale.getDefault();

    /** Indicate whether the pattern is localized or not */
    private boolean applyLocalized = false;

    /** The <code>Log</code> instance for this class. */
    private Log log = LogFactory.getLog(LocaleConvertUtils.class);

    /** Every entry of the mapConverters is:
     *  key = locale
     *  value = FastHashMap of converters for the certain locale.
     */
    private FastHashMap mapConverters = new DelegateFastHashMap(BeanUtils.createCache());

    // --------------------------------------------------------- Constructors

    /**
     *  Makes the state by default (deregisters all converters for all locales)
     *  and then registers default locale converters.
     */
    public LocaleConvertUtilsBean() {
        mapConverters.setFast(false);
        deregister();
        mapConverters.setFast(true);
    }
    
    // --------------------------------------------------------- Properties
     
    /**
     * getter for defaultLocale.
     * @return the default locale
     */
    public Locale getDefaultLocale() {

        return defaultLocale;
    }

    /**
     * setter for defaultLocale.
     * @param locale the default locale
     */
    public void setDefaultLocale(Locale locale) {

        if (locale == null) {
            defaultLocale = Locale.getDefault();
        }
        else {
            defaultLocale = locale;
        }
    }
    
    /**
     * getter for applyLocalized
     *
     * @return <code>true</code> if pattern is localized,
     * otherwise <code>false</code>
     */
    public boolean getApplyLocalized() {
        return applyLocalized;
    }

    /**
     * setter for applyLocalized
     *
     * @param newApplyLocalized <code>true</code> if pattern is localized,
     * otherwise <code>false</code>
     */
    public void setApplyLocalized(boolean newApplyLocalized) {
        applyLocalized = newApplyLocalized;
    }

    // --------------------------------------------------------- Methods

    /**
     * Convert the specified locale-sensitive value into a String.
     *
     * @param value The Value to be converted
     * @return the converted value
     *
     * @throws org.apache.commons.beanutils.ConversionException if thrown by an
     * underlying Converter
     */
    public String convert(Object value) {
        return convert(value, defaultLocale, null);
    }

    /**
     * Convert the specified locale-sensitive value into a String
     * using the conversion pattern.
     *
     * @param value The Value to be converted
     * @param pattern       The convertion pattern
     * @return the converted value
     *
     * @throws org.apache.commons.beanutils.ConversionException if thrown by an
     * underlying Converter
     */
    public String convert(Object value, String pattern) {
        return convert(value, defaultLocale, pattern);
    }

    /**
     * Convert the specified locale-sensitive value into a String
     * using the paticular convertion pattern.
     *
     * @param value The Value to be converted
     * @param locale The locale
     * @param pattern The convertion pattern
     * @return the converted value
     *
     * @throws org.apache.commons.beanutils.ConversionException if thrown by an
     * underlying Converter
     */
    public String convert(Object value, Locale locale, String pattern) {

        LocaleConverter converter = lookup(String.class, locale);

        return (String) converter.convert(String.class, value, pattern);
    }

    /**
     * Convert the specified value to an object of the specified class (if
     * possible).  Otherwise, return a String representation of the value.
     *
     * @param value The String scalar value to be converted
     * @param clazz The Data type to which this value should be converted.
     * @return the converted value
     *
     * @throws org.apache.commons.beanutils.ConversionException if thrown by an
     * underlying Converter
     */
    public Object convert(String value, Class clazz) {

        return convert(value, clazz, defaultLocale, null);
    }

    /**
     * Convert the specified value to an object of the specified class (if
     * possible) using the convertion pattern. Otherwise, return a String
     * representation of the value.
     *
     * @param value The String scalar value to be converted
     * @param clazz The Data type to which this value should be converted.
     * @param pattern The convertion pattern
     * @return the converted value
     *
     * @throws org.apache.commons.beanutils.ConversionException if thrown by an
     * underlying Converter
     */
    public Object convert(String value, Class clazz, String pattern) {

        return convert(value, clazz, defaultLocale, pattern);
    }

    /**
     * Convert the specified value to an object of the specified class (if
     * possible) using the convertion pattern. Otherwise, return a String
     * representation of the value.
     *
     * @param value The String scalar value to be converted
     * @param clazz The Data type to which this value should be converted.
     * @param locale The locale
     * @param pattern The convertion pattern
     * @return the converted value
     *
     * @throws org.apache.commons.beanutils.ConversionException if thrown by an
     * underlying Converter
     */
    public Object convert(String value, Class clazz, Locale locale, String pattern) {

        if (log.isDebugEnabled()) {
            log.debug("Convert string " + value + " to class " +
                    clazz.getName() + " using " + locale +
                    " locale and " + pattern + " pattern");
        }

        LocaleConverter converter = lookup(clazz, locale);

        if (converter == null) {
            converter = lookup(String.class, locale);
        }
        if (log.isTraceEnabled()) {
            log.trace("  Using converter " + converter);
        }

        return (converter.convert(clazz, value, pattern));
    }

    /**
     * Convert an array of specified values to an array of objects of the
     * specified class (if possible) using the convertion pattern.
     *
     * @param values Value to be converted (may be null)
     * @param clazz Java array or element class to be converted to
     * @param pattern The convertion pattern
     * @return the converted value
     *
     * @throws org.apache.commons.beanutils.ConversionException if thrown by an
     * underlying Converter
     */
    public Object convert(String[] values, Class clazz, String pattern) {

        return convert(values, clazz, getDefaultLocale(), pattern);
    }

   /**
    * Convert an array of specified values to an array of objects of the
    * specified class (if possible) .
    *
    * @param values Value to be converted (may be null)
    * @param clazz Java array or element class to be converted to
    * @return the converted value
    *
     * @throws org.apache.commons.beanutils.ConversionException if thrown by an
     * underlying Converter
    */
   public Object convert(String[] values, Class clazz) {

       return convert(values, clazz, getDefaultLocale(), null);
   }

    /**
     * Convert an array of specified values to an array of objects of the
     * specified class (if possible) using the convertion pattern.
     *
     * @param values Value to be converted (may be null)
     * @param clazz Java array or element class to be converted to
     * @param locale The locale
     * @param pattern The convertion pattern
     * @return the converted value
     *
     * @throws org.apache.commons.beanutils.ConversionException if thrown by an
     * underlying Converter
     */
    public Object convert(String[] values, Class clazz, Locale locale, String pattern) {

        Class type = clazz;
        if (clazz.isArray()) {
            type = clazz.getComponentType();
        }
        if (log.isDebugEnabled()) {
            log.debug("Convert String[" + values.length + "] to class " +
                    type.getName() + "[] using " + locale +
                    " locale and " + pattern + " pattern");
        }

        Object array = Array.newInstance(type, values.length);
        for (int i = 0; i < values.length; i++) {
            Array.set(array, i, convert(values[i], type, locale, pattern));
        }

        return (array);
    }

    /**
     * Register a custom {@link LocaleConverter} for the specified destination
     * <code>Class</code>, replacing any previously registered converter.
     *
     * @param converter The LocaleConverter to be registered
     * @param clazz The Destination class for conversions performed by this
     *  Converter
     * @param locale The locale
     */
    public void register(LocaleConverter converter, Class clazz, Locale locale) {

        lookup(locale).put(clazz, converter);
    }

    /**
     * Remove any registered {@link LocaleConverter}.
     */
    public void deregister() {

        FastHashMap defaultConverter = lookup(defaultLocale);

        mapConverters.setFast(false);

        mapConverters.clear();
        mapConverters.put(defaultLocale, defaultConverter);

        mapConverters.setFast(true);
    }


    /**
     * Remove any registered {@link LocaleConverter} for the specified locale
     *
     * @param locale The locale
     */
    public void deregister(Locale locale) {

        mapConverters.remove(locale);
    }


    /**
     * Remove any registered {@link LocaleConverter} for the specified locale and Class.
     *
     * @param clazz Class for which to remove a registered Converter
     * @param locale The locale
     */
    public void deregister(Class clazz, Locale locale) {

        lookup(locale).remove(clazz);
    }

    /**
     * Look up and return any registered {@link LocaleConverter} for the specified
     * destination class and locale; if there is no registered Converter, return
     * <code>null</code>.
     *
     * @param clazz Class for which to return a registered Converter
     * @param locale The Locale
     * @return The registered locale Converter, if any
     */
    public LocaleConverter lookup(Class clazz, Locale locale) {

        LocaleConverter converter = (LocaleConverter) lookup(locale).get(clazz);
        
        if (log.isTraceEnabled()) {
            log.trace("LocaleConverter:" + converter);
        }
        
        return converter;
    }

    /**
     * Look up and return any registered FastHashMap instance for the specified locale;
     * if there is no registered one, return <code>null</code>.
     *
     * @param locale The Locale
     * @return The FastHashMap instance contains the all {@link LocaleConverter} types for
     *  the specified locale.
     * @deprecated This method will be modified to return a Map in the next release.
     */
    protected FastHashMap lookup(Locale locale) {
        FastHashMap localeConverters;

        if (locale == null) {
            localeConverters = (FastHashMap) mapConverters.get(defaultLocale);
        }
        else {
            localeConverters = (FastHashMap) mapConverters.get(locale);

            if (localeConverters == null) {
                localeConverters = create(locale);
                mapConverters.put(locale, localeConverters);
            }
        }

        return localeConverters;
    }

    /**
     *  Create all {@link LocaleConverter} types for specified locale.
     *
     * @param locale The Locale
     * @return The FastHashMap instance contains the all {@link LocaleConverter} types
     *  for the specified locale.
     * @deprecated This method will be modified to return a Map in the next release.
     */
    protected FastHashMap create(Locale locale) {

        FastHashMap converter = new DelegateFastHashMap(BeanUtils.createCache());
        converter.setFast(false);

        converter.put(BigDecimal.class, new BigDecimalLocaleConverter(locale, applyLocalized));
        converter.put(BigInteger.class, new BigIntegerLocaleConverter(locale, applyLocalized));

        converter.put(Byte.class, new ByteLocaleConverter(locale, applyLocalized));
        converter.put(Byte.TYPE, new ByteLocaleConverter(locale, applyLocalized));

        converter.put(Double.class, new DoubleLocaleConverter(locale, applyLocalized));
        converter.put(Double.TYPE, new DoubleLocaleConverter(locale, applyLocalized));

        converter.put(Float.class, new FloatLocaleConverter(locale, applyLocalized));
        converter.put(Float.TYPE, new FloatLocaleConverter(locale, applyLocalized));

        converter.put(Integer.class, new IntegerLocaleConverter(locale, applyLocalized));
        converter.put(Integer.TYPE, new IntegerLocaleConverter(locale, applyLocalized));

        converter.put(Long.class, new LongLocaleConverter(locale, applyLocalized));
        converter.put(Long.TYPE, new LongLocaleConverter(locale, applyLocalized));

        converter.put(Short.class, new ShortLocaleConverter(locale, applyLocalized));
        converter.put(Short.TYPE, new ShortLocaleConverter(locale, applyLocalized));

        converter.put(String.class, new StringLocaleConverter(locale, applyLocalized));

        // conversion format patterns of java.sql.* types should correspond to default
        // behaviour of toString and valueOf methods of these classes
        converter.put(java.sql.Date.class, new SqlDateLocaleConverter(locale, "yyyy-MM-dd"));
        converter.put(java.sql.Time.class, new SqlTimeLocaleConverter(locale, "HH:mm:ss"));
        converter.put( java.sql.Timestamp.class,
                       new SqlTimestampLocaleConverter(locale, "yyyy-MM-dd HH:mm:ss.S")
                     );

        converter.setFast(true);

        return converter;
    }

    /**
     * FastHashMap implementation that uses WeakReferences to overcome
     * memory leak problems.
     *
     * This is a hack to retain binary compatibility with previous
     * releases (where FastHashMap is exposed in the API), but
     * use WeakHashMap to resolve memory leaks.
     */
    private static class DelegateFastHashMap extends FastHashMap {

        private final Map map;

        private DelegateFastHashMap(Map map) {
            this.map = map;
        }
        public void clear() {
            map.clear();
        }
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }
        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }
        public Set entrySet() {
            return map.entrySet();
        }
        public boolean equals(Object o) {
            return map.equals(o);
        }
        public Object get(Object key) {
            return map.get(key);
        }
        public int hashCode() {
            return map.hashCode();
        }
        public boolean isEmpty() {
            return map.isEmpty();
        }
        public Set keySet() {
            return map.keySet();
        }
        public Object put(Object key, Object value) {
            return map.put(key, value);
        }
        public void putAll(Map m) {
            map.putAll(m);
        }
        public Object remove(Object key) {
            return map.remove(key);
        }
        public int size() {
            return map.size();
        }
        public Collection values() {
            return map.values();
        }
        public boolean getFast() {
            return BeanUtils.getCacheFast(map);
        }
        public void setFast(boolean fast) {
            BeanUtils.setCacheFast(map, fast);
        }
    }
}
