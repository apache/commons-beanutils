/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/locale/LocaleConvertUtils.java,v 1.7 2003/10/05 13:34:16 rdonkin Exp $
 * $Revision: 1.7 $
 * $Date: 2003/10/05 13:34:16 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
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

import org.apache.commons.collections.FastHashMap;

import java.util.Locale;

/**
 * <p>Utility methods for converting locale-sensitive String scalar values to objects of the
 * specified Class, String arrays to arrays of the specified Class and
 * object to locale-sensitive String scalar value.</p>
 *
 * <p>The implementations for these method are provided by {@link LocaleConvertUtilsBean}.
 * These static utility method use the default instance. More sophisticated can be provided
 * by using a <code>LocaleConvertUtilsBean</code> instance.</p>
 *
 * @author Yauheny Mikulski
 */
public class LocaleConvertUtils {

    // ----------------------------------------------------- Instance Variables

    /**
     * <p>Gets the <code>Locale</code> which will be used when 
     * no <code>Locale</code> is passed to a method.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#getDefaultLocale()
     */
    public static Locale getDefaultLocale() {

        return LocaleConvertUtilsBean.getInstance().getDefaultLocale();
    }

    /**
     * <p>Sets the <code>Locale</code> which will be used when 
     * no <code>Locale</code> is passed to a method.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#setDefaultLocale(Locale)
     */
    public static void setDefaultLocale(Locale locale) {

        LocaleConvertUtilsBean.getInstance().setDefaultLocale(locale);
    }

    /**
     * <p>Gets for applyLocalized</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#getApplyLocalized()
     */
    public static boolean getApplyLocalized() {
        return LocaleConvertUtilsBean.getInstance().getApplyLocalized();
    }

    /**
     * <p>Sets applyLocalized</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#setApplyLocalized(boolean)
     */
    public static void setApplyLocalized(boolean newApplyLocalized) {
        LocaleConvertUtilsBean.getInstance().setApplyLocalized(newApplyLocalized);
    }

    // --------------------------------------------------------- Methods

    /**
     * <p>Convert the specified locale-sensitive value into a String.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(Object)
     */
    public static String convert(Object value) {
        return LocaleConvertUtilsBean.getInstance().convert(value);
    }

    /**
     * <p>Convert the specified locale-sensitive value into a String
     * using the convertion pattern.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(Object, String)
     */
    public static String convert(Object value, String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, pattern);
    }

    /**
     * <p>Convert the specified locale-sensitive value into a String
     * using the paticular convertion pattern.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(Object, Locale, String)
     */
    public static String convert(Object value, Locale locale, String pattern) {

        return LocaleConvertUtilsBean.getInstance().convert(value, locale, pattern);
    }

    /**
     * <p>Convert the specified value to an object of the specified class (if
     * possible).  Otherwise, return a String representation of the value.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(String, Class)
     */
    public static Object convert(String value, Class clazz) {

        return LocaleConvertUtilsBean.getInstance().convert(value, clazz);
    }

    /**
     * <p>Convert the specified value to an object of the specified class (if
     * possible) using the convertion pattern. Otherwise, return a String
     * representation of the value.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(String, Class, String)
     */
    public static Object convert(String value, Class clazz, String pattern) {

        return LocaleConvertUtilsBean.getInstance().convert(value, clazz, pattern);
    }

    /**
     * <p>Convert the specified value to an object of the specified class (if
     * possible) using the convertion pattern. Otherwise, return a String
     * representation of the value.</p>
     *
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(String, Class, Locale, String)
     */
    public static Object convert(String value, Class clazz, Locale locale, String pattern) {

        return LocaleConvertUtilsBean.getInstance().convert(value, clazz, locale, pattern);
    }

    /**
     * <p>Convert an array of specified values to an array of objects of the
     * specified class (if possible) using the convertion pattern.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(String[], Class, String)
     */
    public static Object convert(String values[], Class clazz, String pattern) {

        return LocaleConvertUtilsBean.getInstance().convert(values, clazz, pattern);
    }

   /**
    * <p>Convert an array of specified values to an array of objects of the
    * specified class (if possible).</p>
    * 
    * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
    *
    * @see LocaleConvertUtilsBean#convert(String[], Class)
    */
   public static Object convert(String values[], Class clazz) {

       return LocaleConvertUtilsBean.getInstance().convert(values, clazz);
   }

    /**
     * <p>Convert an array of specified values to an array of objects of the
     * specified class (if possible) using the convertion pattern.</p>
     *
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(String[], Class, Locale, String)
     */
    public static Object convert(String values[], Class clazz, Locale locale, String pattern) {

        return LocaleConvertUtilsBean.getInstance().convert(values, clazz, locale, pattern);
    }

    /**
     * <p>Register a custom {@link LocaleConverter} for the specified destination
     * <code>Class</code>, replacing any previously registered converter.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#register(LocaleConverter, Class, Locale)
     */
    public static void register(LocaleConverter converter, Class clazz, Locale locale) {

        LocaleConvertUtilsBean.getInstance().register(converter, clazz, locale);
    }

    /**
     * <p>Remove any registered {@link LocaleConverter}.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#deregister()
     */
    public static void deregister() {

       LocaleConvertUtilsBean.getInstance().deregister();
    }


    /**
     * <p>Remove any registered {@link LocaleConverter} for the specified locale.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#deregister(Locale)
     */
    public static void deregister(Locale locale) {

        LocaleConvertUtilsBean.getInstance().deregister(locale);
    }


    /**
     * <p>Remove any registered {@link LocaleConverter} for the specified locale and Class.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#deregister(Class, Locale)
     */
    public static void deregister(Class clazz, Locale locale) {

        LocaleConvertUtilsBean.getInstance().deregister(clazz, locale);
    }

    /**
     * <p>Look up and return any registered {@link LocaleConverter} for the specified
     * destination class and locale; if there is no registered Converter, return
     * <code>null</code>.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#lookup(Class, Locale)
     */
    public static LocaleConverter lookup(Class clazz, Locale locale) {

        return LocaleConvertUtilsBean.getInstance().lookup(clazz, locale);
    }

    /**
     * <p>Look up and return any registered FastHashMap instance for the specified locale.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#lookup(Locale)
     */
    protected static FastHashMap lookup(Locale locale) {
        return LocaleConvertUtilsBean.getInstance().lookup(locale);
    }

    /**
     * <p>Create all {@link LocaleConverter} types for specified locale.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#create(Locale)
     */
    protected static FastHashMap create(Locale locale) {

        return LocaleConvertUtilsBean.getInstance().create(locale);
    }
}
