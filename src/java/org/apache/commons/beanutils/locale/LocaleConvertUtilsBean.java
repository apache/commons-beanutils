/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/locale/LocaleConvertUtilsBean.java,v 1.1 2003/03/20 20:12:26 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/20 20:12:26 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.beanutils.locale;

import org.apache.commons.beanutils.locale.converters.*;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Locale;

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
    
    /** Singleton instance */
    private static final LocaleConvertUtilsBean singleton = new LocaleConvertUtilsBean();
    
    /** Gets singleton instance */
    public static LocaleConvertUtilsBean getInstance() {
        return singleton;
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
    private FastHashMap mapConverters = new FastHashMap();

    // --------------------------------------------------------- Constructors

    /**
     *  Makes the state by default (deregisters all converters for all locales)
     *  and then registers default locale converters.
     */
    public LocaleConvertUtilsBean() {
        deregister();
    }
    
    // --------------------------------------------------------- Properties
     
    /**
     * getter for defaultLocale
     */
    public Locale getDefaultLocale() {

        return defaultLocale;
    }

    /**
     * setter for defaultLocale
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
     */
    public boolean getApplyLocalized() {
        return applyLocalized;
    }

    /**
     * setter for applyLocalized
     */
    public void setApplyLocalized(boolean newApplyLocalized) {
        applyLocalized = newApplyLocalized;
    }

    // --------------------------------------------------------- Methods

    /**
     * Convert the specified locale-sensitive value into a String.
     *
     * @param value The Value to be converted
     *
     * @exception org.apache.commons.beanutils.ConversionException if thrown by an underlying Converter
     */
    public String convert(Object value) {
        return convert(value, defaultLocale, null);
    }

    /**
     * Convert the specified locale-sensitive value into a String
     * using the convertion pattern.
     *
     * @param value The Value to be converted
     * @param pattern       The convertion pattern
     *
     * @exception ConversionException if thrown by an underlying Converter
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
     *
     * @exception ConversionException if thrown by an underlying Converter
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
     *
     * @exception ConversionException if thrown by an underlying Converter
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
     *
     * @exception ConversionException if thrown by an underlying Converter
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
     *
     * @exception ConversionException if thrown by an underlying Converter
     */
    public Object convert(String value, Class clazz, Locale locale, String pattern) {

        if (log.isDebugEnabled()) {
            log.debug("Convert string " + value + " to class " +
                    clazz.getName() + " using " + locale.toString() +
                    " locale and " + pattern + " pattern");
        }

        LocaleConverter converter = lookup(clazz, locale);

        if (converter == null) {
            converter = (LocaleConverter) lookup(String.class, locale);
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
     *
     * @exception ConversionException if thrown by an underlying Converter
     */
    public Object convert(String values[], Class clazz, String pattern) {

        return convert(values, clazz, getDefaultLocale(), pattern);
    }

   /**
    * Convert an array of specified values to an array of objects of the
    * specified class (if possible) .
    *
    * @param values Value to be converted (may be null)
    * @param clazz Java array or element class to be converted to
    *
    * @exception ConversionException if thrown by an underlying Converter
    */
   public Object convert(String values[], Class clazz) {

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
     *
     * @exception ConversionException if thrown by an underlying Converter
     */
    public Object convert(String values[], Class clazz, Locale locale, String pattern) {

        Class type = clazz;
        if (clazz.isArray()) {
            type = clazz.getComponentType();
        }
        if (log.isDebugEnabled()) {
            log.debug("Convert String[" + values.length + "] to class " +
                    type.getName() + "[] using " + locale.toString() +
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
     */
    public LocaleConverter lookup(Class clazz, Locale locale) {

        return (LocaleConverter) lookup(locale).get(clazz);
    }

    /**
     * Look up and return any registered FastHashMap instance for the specified locale;
     * if there is no registered one, return <code>null</code>.
     *
     * @param locale The Locale
     * @return The FastHashMap instance contains the all {@link LocaleConverter} types for
     *  the specified locale.
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
     */
    protected FastHashMap create(Locale locale) {

        FastHashMap converter = new FastHashMap();
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
}
