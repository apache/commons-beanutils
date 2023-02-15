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

import java.util.Locale;

/**
 * <p>Utility methods for converting locale-sensitive String scalar values to objects of the
 * specified Class, String arrays to arrays of the specified Class and
 * object to locale-sensitive String scalar value.</p>
 *
 * <p>The implementations for these method are provided by {@link LocaleConvertUtilsBean}.
 * These static utility method use the default instance. More sophisticated can be provided
 * by using a {@code LocaleConvertUtilsBean} instance.</p>
 *
 */
public class LocaleConvertUtils {

    /**
     * <p>Converts the specified locale-sensitive value into a String.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param value The Value to be converted
     * @return the converted value
     * @see LocaleConvertUtilsBean#convert(Object)
     */
    public static String convert(final Object value) {
        return LocaleConvertUtilsBean.getInstance().convert(value);
    }

    /**
     * <p>Converts the specified locale-sensitive value into a String
     * using the particular conversion pattern.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param value The Value to be converted
     * @param locale The locale
     * @param pattern The conversion pattern
     * @return the converted value
     * @see LocaleConvertUtilsBean#convert(Object, Locale, String)
     */
    public static String convert(final Object value, final Locale locale, final String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, locale, pattern);
    }

    /**
     * <p>Converts the specified locale-sensitive value into a String
     * using the conversion pattern.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param value The Value to be converted
     * @param pattern       The conversion pattern
     * @return the converted value
     * @see LocaleConvertUtilsBean#convert(Object, String)
     */
    public static String convert(final Object value, final String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, pattern);
    }

    /**
     * <p>Converts the specified value to an object of the specified class (if
     * possible).  Otherwise, return a String representation of the value.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param value The String scalar value to be converted
     * @param clazz The Data type to which this value should be converted.
     * @return the converted value
     * @see LocaleConvertUtilsBean#convert(String, Class)
     */
    public static Object convert(final String value, final Class<?> clazz) {
        return LocaleConvertUtilsBean.getInstance().convert(value, clazz);
    }

    /**
     * <p>Converts the specified value to an object of the specified class (if
     * possible) using the conversion pattern. Otherwise, return a String
     * representation of the value.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param value The String scalar value to be converted
     * @param clazz The Data type to which this value should be converted.
     * @param locale The locale
     * @param pattern The conversion pattern
     * @return the converted value
     * @see LocaleConvertUtilsBean#convert(String, Class, Locale, String)
     */
    public static Object convert(final String value, final Class<?> clazz, final Locale locale, final String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, clazz, locale, pattern);
    }

    /**
     * <p>Converts the specified value to an object of the specified class (if
     * possible) using the conversion pattern. Otherwise, return a String
     * representation of the value.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param value The String scalar value to be converted
     * @param clazz The Data type to which this value should be converted.
     * @param pattern The conversion pattern
     * @return the converted value
     * @see LocaleConvertUtilsBean#convert(String, Class, String)
     */
    public static Object convert(final String value, final Class<?> clazz, final String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, clazz, pattern);
    }

    /**
        * <p>Convert an array of specified values to an array of objects of the
        * specified class (if possible).</p>
        *
        * <p>For more details see {@code LocaleConvertUtilsBean}</p>
        *
        * @param values Value to be converted (may be null)
        * @param clazz Java array or element class to be converted to
        * @return the converted value
        * @see LocaleConvertUtilsBean#convert(String[], Class)
        */
       public static Object convert(final String[] values, final Class<?> clazz) {
           return LocaleConvertUtilsBean.getInstance().convert(values, clazz);
       }

    /**
     * <p>Convert an array of specified values to an array of objects of the
     * specified class (if possible) using the conversion pattern.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param values Value to be converted (may be null)
     * @param clazz Java array or element class to be converted to
     * @param locale The locale
     * @param pattern The conversion pattern
     * @return the converted value
     * @see LocaleConvertUtilsBean#convert(String[], Class, Locale, String)
     */
    public static Object convert(final String[] values, final Class<?> clazz, final Locale locale,
            final String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(values, clazz, locale, pattern);
    }

    /**
     * <p>Convert an array of specified values to an array of objects of the
     * specified class (if possible) using the conversion pattern.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param values Value to be converted (may be null)
     * @param clazz Java array or element class to be converted to
     * @param pattern The conversion pattern
     * @return the converted value
     * @see LocaleConvertUtilsBean#convert(String[], Class, String)
     */
    public static Object convert(final String[] values, final Class<?> clazz, final String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(values, clazz, pattern);
    }

    /**
     * <p>Remove any registered {@link LocaleConverter}.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @see LocaleConvertUtilsBean#deregister()
     */
    public static void deregister() {
       LocaleConvertUtilsBean.getInstance().deregister();
    }

    /**
     * <p>Remove any registered {@link LocaleConverter} for the specified locale and Class.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param clazz Class for which to remove a registered Converter
     * @param locale The locale
     * @see LocaleConvertUtilsBean#deregister(Class, Locale)
     */
    public static void deregister(final Class<?> clazz, final Locale locale) {
        LocaleConvertUtilsBean.getInstance().deregister(clazz, locale);
    }

   /**
 * <p>Remove any registered {@link LocaleConverter} for the specified locale.</p>
 *
 * <p>For more details see {@code LocaleConvertUtilsBean}</p>
 *
 * @param locale The locale
 * @see LocaleConvertUtilsBean#deregister(Locale)
 */
public static void deregister(final Locale locale) {
    LocaleConvertUtilsBean.getInstance().deregister(locale);
}

    /**
     * <p>Gets applyLocalized.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @return {@code true} if pattern is localized,
     * otherwise {@code false}
     * @see LocaleConvertUtilsBean#getApplyLocalized()
     */
    public static boolean getApplyLocalized() {
        return LocaleConvertUtilsBean.getInstance().getApplyLocalized();
    }

    /**
     * <p>Gets the {@code Locale} which will be used when
     * no {@code Locale} is passed to a method.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     * @return the default locale
     * @see LocaleConvertUtilsBean#getDefaultLocale()
     */
    public static Locale getDefaultLocale() {
        return LocaleConvertUtilsBean.getInstance().getDefaultLocale();
    }

    /**
     * <p>Look up and return any registered {@link LocaleConverter} for the specified
     * destination class and locale; if there is no registered Converter, return
     * {@code null}.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param <T> The converter type.
     * @param clazz Class for which to return a registered Converter
     * @param locale The Locale
     * @return The registered locale Converter, if any
     * @see LocaleConvertUtilsBean#lookup(Class, Locale)
     */
    public static <T> LocaleConverter<T> lookup(final Class<T> clazz, final Locale locale) {
        return LocaleConvertUtilsBean.getInstance().lookup(clazz, locale);
    }

    /**
     * <p>Register a custom {@link LocaleConverter} for the specified destination
     * {@code Class}, replacing any previously registered converter.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param <T> The converter type.
     * @param converter The LocaleConverter to be registered
     * @param clazz The Destination class for conversions performed by this
     *  Converter
     * @param locale The locale
     * @see LocaleConvertUtilsBean#register(LocaleConverter, Class, Locale)
     */
    public static <T> void register(final LocaleConverter<T> converter, final Class<T> clazz, final Locale locale) {
        LocaleConvertUtilsBean.getInstance().register(converter, clazz, locale);
    }

    /**
     * <p>Sets applyLocalized.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param newApplyLocalized {@code true} if pattern is localized,
     * otherwise {@code false}
     * @see LocaleConvertUtilsBean#setApplyLocalized(boolean)
     */
    public static void setApplyLocalized(final boolean newApplyLocalized) {
        LocaleConvertUtilsBean.getInstance().setApplyLocalized(newApplyLocalized);
    }

    /**
     * <p>Sets the {@code Locale} which will be used when
     * no {@code Locale} is passed to a method.</p>
     *
     * <p>For more details see {@code LocaleConvertUtilsBean}</p>
     *
     * @param locale the default locale
     * @see LocaleConvertUtilsBean#setDefaultLocale(Locale)
     */
    public static void setDefaultLocale(final Locale locale) {
        LocaleConvertUtilsBean.getInstance().setDefaultLocale(locale);
    }
}
