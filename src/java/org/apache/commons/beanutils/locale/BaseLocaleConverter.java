/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/locale/BaseLocaleConverter.java,v 1.5 2003/10/05 13:34:16 rdonkin Exp $
 * $Revision: 1.5 $
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

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.util.Locale;


/**
 * <p>The base class for all standart type locale-sensitive converters.
 * It has {@link LocaleConverter} and {@link org.apache.commons.beanutils.Converter} implementations,
 * that convert an incoming locale-sensitive Object into an object of correspond type,
 * optionally using a default value or throwing a {@link ConversionException}
 * if a conversion error occurs.</p>
 *
 * @author Yauheny Mikulski
 */

public abstract class BaseLocaleConverter implements LocaleConverter {

    // ----------------------------------------------------- Instance Variables

    /** All logging goes through this logger */
    private static Log log = LogFactory.getLog(BaseLocaleConverter.class);

    /** The default value specified to our Constructor, if any. */
    private Object defaultValue = null;

    /** Should we return the default value on conversion errors? */
    protected boolean useDefault = false;

    /** The locale specified to our Constructor, by default - system locale. */
    protected Locale locale = Locale.getDefault();

    /** The default pattern specified to our Constructor, if any. */
    protected String pattern = null;

    /** The flag indicating whether the given pattern string is localized or not. */
    protected boolean locPattern = false;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a {@link LocaleConverter} that will throw a {@link ConversionException}
     * if a conversion error occurs.
     * An unlocalized pattern is used for the convertion.
     *
     * @param locale        The locale
     * @param pattern       The convertion pattern
     */
    protected BaseLocaleConverter(Locale locale, String pattern) {

        this(null, locale, pattern, false, false);
    }

    /**
     * Create a {@link LocaleConverter} that will throw a {@link ConversionException}
     * if a conversion error occurs.
     *
     * @param locale        The locale
     * @param pattern       The convertion pattern
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    protected BaseLocaleConverter(Locale locale, String pattern, boolean locPattern) {

        this(null, locale, pattern, false, locPattern);
    }

    /**
     * Create a {@link LocaleConverter} that will return the specified default value
     * if a conversion error occurs.
     * An unlocalized pattern is used for the convertion.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     * @param pattern       The convertion pattern
     */
    protected BaseLocaleConverter(Object defaultValue, Locale locale, String pattern) {

        this(defaultValue, locale, pattern, false);
    }

    /**
     * Create a {@link LocaleConverter} that will return the specified default value
     * if a conversion error occurs.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     * @param pattern       The convertion pattern
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    protected BaseLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {

        this(defaultValue, locale, pattern, true, locPattern);
    }

    /**
     * Create a {@link LocaleConverter} that will return the specified default value
     * or throw a {@link ConversionException} if a conversion error occurs.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     * @param pattern       The convertion pattern
     * @param useDefault    Indicate whether the default value is used or not
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    private BaseLocaleConverter(Object defaultValue, Locale locale,
                                String pattern, boolean useDefault, boolean locPattern) {

        if (useDefault) {
            this.defaultValue = defaultValue;
            this.useDefault = true;
        }

        if (locale != null) {
            this.locale = locale;
        }

        this.pattern = pattern;
        this.locPattern = locPattern;
    }

    // --------------------------------------------------------- Methods

    /**
     * Convert the specified locale-sensitive input object into an output object of the
     * specified type.
     *
     * @param value The input object to be converted
     * @param pattern The pattern is used for the convertion
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */

    abstract protected Object parse(Object value, String pattern) throws ParseException;


    /**
     * Convert the specified locale-sensitive input object into an output object.
     * The default pattern is used for the convertion.
     *
     * @param value The input object to be converted
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Object value) {
        return convert(value, null);
    }

    /**
     * Convert the specified locale-sensitive input object into an output object.
     *
     * @param value The input object to be converted
     * @param pattern The pattern is used for the convertion
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Object value, String pattern) {
        return convert(null, value, pattern);
    }

    /**
     * Convert the specified locale-sensitive input object into an output object of the
     * specified type. The default pattern is used for the convertion.
     *
     * @param type Data type to which this value should be converted
     * @param value The input object to be converted
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Class type, Object value) {
        return convert(type, value, null);
    }

    /**
     * Convert the specified locale-sensitive input object into an output object of the
     * specified type.
     *
     * @param type Data type to which this value should be converted
     * @param value The input object to be converted
     * @param pattern The pattern is used for the convertion
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Class type, Object value, String pattern) {
        if (value == null) {
            if (useDefault) {
                return (defaultValue);
            } else {
                // symmetric beanutils function allows null
                // so do not: throw new ConversionException("No value specified");
                log.debug("Null value specified for conversion, returing null");
                return null;
            }
        }

        try {
            if (pattern != null) {
                return parse(value, pattern);
            } else {
                return parse(value, this.pattern);
            }
        } catch (Exception e) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException(e);
            }
        }
    }
}
