/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/locale/converters/BigDecimalLocaleConverter.java,v 1.1 2002/09/03 21:34:20 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2002/09/03 21:34:20 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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

package org.apache.commons.beanutils.locale.converters;

import java.util.Locale;


/**
 * <p>Standard {@link org.apache.commons.beanutils.locale.LocaleConverter} 
 * implementation that converts an incoming
 * locale-sensitive String into a <code>java.math.BigDecimal</code> object,
 * optionally using a default value or throwing a 
 * {@link org.apache.commons.beanutils.ConversionException}
 * if a conversion error occurs.</p>
 *
 * @author Yauheny Mikulski
 */

public class BigDecimalLocaleConverter extends DecimalLocaleConverter {

    // ----------------------------------------------------------- Constructors

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter} 
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs. The locale is the default locale for
     * this instance of the Java Virtual Machine and an unlocalized pattern is used
     * for the convertion.
     *
     */
    public BigDecimalLocaleConverter() {

        this(false);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter} 
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs. The locale is the default locale for
     * this instance of the Java Virtual Machine.
     *
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    public BigDecimalLocaleConverter(boolean locPattern) {

        this(Locale.getDefault(), locPattern);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter} 
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs. An unlocalized pattern is used for the convertion.
     *
     * @param locale        The locale
     */
    public BigDecimalLocaleConverter(Locale locale) {

        this(locale, false);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter} 
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs.
     *
     * @param locale        The locale
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    public BigDecimalLocaleConverter(Locale locale, boolean locPattern) {

        this(locale, (String) null, locPattern);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter} 
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs. An unlocalized pattern is used for the convertion.
     *
     * @param locale        The locale
     * @param pattern       The convertion pattern
     */
    public BigDecimalLocaleConverter(Locale locale, String pattern) {

        this(locale, pattern, false);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter} 
     * that will throw a {@link org.apache.commons.beanutils.ConversionException}
     * if a conversion error occurs.
     *
     * @param locale        The locale
     * @param pattern       The convertion pattern
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    public BigDecimalLocaleConverter(Locale locale, String pattern, boolean locPattern) {

        super(locale, pattern, locPattern);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter} 
     * that will return the specified default value
     * if a conversion error occurs. The locale is the default locale for
     * this instance of the Java Virtual Machine and an unlocalized pattern is used
     * for the convertion.
     *
     * @param defaultValue  The default value to be returned
     */
    public BigDecimalLocaleConverter(Object defaultValue) {

        this(defaultValue, false);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter} 
     * that will return the specified default value
     * if a conversion error occurs. The locale is the default locale for
     * this instance of the Java Virtual Machine.
     *
     * @param defaultValue  The default value to be returned
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    public BigDecimalLocaleConverter(Object defaultValue, boolean locPattern) {

        this(defaultValue, Locale.getDefault(), locPattern);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter} 
     * that will return the specified default value
     * if a conversion error occurs. An unlocalized pattern is used for the convertion.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     */
    public BigDecimalLocaleConverter(Object defaultValue, Locale locale) {

        this(defaultValue, locale, false);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter} 
     * that will return the specified default value
     * if a conversion error occurs.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    public BigDecimalLocaleConverter(Object defaultValue, Locale locale, boolean locPattern) {

        this(defaultValue, locale, null, locPattern);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter} 
     * that will return the specified default value
     * if a conversion error occurs. An unlocalized pattern is used for the convertion.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     * @param pattern       The convertion pattern
     */
    public BigDecimalLocaleConverter(Object defaultValue, Locale locale, String pattern) {

        this(defaultValue, locale, pattern, false);
    }

    /**
     * Create a {@link org.apache.commons.beanutils.locale.LocaleConverter} 
     * that will return the specified default value
     * if a conversion error occurs.
     *
     * @param defaultValue  The default value to be returned
     * @param locale        The locale
     * @param pattern       The convertion pattern
     * @param locPattern    Indicate whether the pattern is localized or not
     */
    public BigDecimalLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern) {

        super(defaultValue, locale, pattern);
    }
}
