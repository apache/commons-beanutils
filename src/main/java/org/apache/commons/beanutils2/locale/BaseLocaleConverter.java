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

import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.beanutils2.ConversionException;
import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The base class for all standard type locale-sensitive converters. It has {@link LocaleConverter} and {@link org.apache.commons.beanutils2.Converter}
 * implementations, that convert an incoming locale-sensitive Object into an object of correspond type, optionally using a default value or throwing a
 * {@link ConversionException} if a conversion error occurs.
 *
 * @param <T> The converter type.
 */
public abstract class BaseLocaleConverter<T> implements LocaleConverter<T> {

    /**
     * Builds instances of {@link BaseLocaleConverter} subclasses.
     *
     * @param <B> The builder type.
     * @param <T> The converter type.
     */
    public abstract static class Builder<B extends Builder<B, T>, T> {

        /** The default value specified to our Constructor, if any. */
        protected T defaultValue;

        /** The locale specified to our Constructor, by default - system locale. */
        protected Locale locale = Locale.getDefault();

        /** The flag indicating whether the given pattern string is localized or not. */
        protected boolean localizedPattern;

        /** The default pattern specified to our Constructor, if any. */
        protected String pattern;

        /** Should we return the default value on conversion errors? */
        protected boolean useDefault;

        /**
         * Returns this instance cast as the exact subclass type.
         *
         * @return this instance cast as the exact subclass type.
         */
        @SuppressWarnings("unchecked")
        protected B asThis() {
            return (B) this;
        }

        /**
         * Gets a newly built instance.
         *
         * @return a newly built instance.
         */
        public abstract BaseLocaleConverter<?> get();

        /**
         * Sets the default value.
         *
         * @param defaultValue the default value.
         * @return this
         */
        public B setDefault(final T defaultValue) {
            this.defaultValue = defaultValue;
            return asThis();
        }

        /**
         * Sets the locale.
         *
         * @param locale the locale.
         * @return this
         */
        public B setLocale(final Locale locale) {
            this.locale = locale;
            return asThis();
        }

        /**
         * Sets the localized pattern.
         *
         * @param localizedPattern the localized pattern.
         * @return this
         */
        public B setLocalizedPattern(final boolean localizedPattern) {
            this.localizedPattern = localizedPattern;
            return asThis();
        }

        /**
         * Sets the pattern.
         *
         * @param pattern the pattern.
         * @return this
         */
        public B setPattern(final String pattern) {
            this.pattern = pattern;
            return asThis();
        }

        /**
         * Sets the use of default.
         *
         * @param useDefault the use of default.
         * @return this
         */
        public B setUseDefault(final boolean useDefault) {
            this.useDefault = useDefault;
            return asThis();
        }

    }

    /** All logging goes through this logger */
    private static final Log LOG = LogFactory.getLog(BaseLocaleConverter.class);

    /**
     * Checks whether the result of a conversion is conform to the specified target type. If this is the case, the passed in result object is cast to the
     * correct target type. Otherwise, an exception is thrown.
     *
     * @param <T>    the desired result type
     * @param type   the target class of the conversion
     * @param result the conversion result object
     * @return the result cast to the target class
     * @throws ConversionException if the result object is not compatible with the target type
     */
    private static <R> R checkConversionResult(final Class<R> type, final Object result) {
        if (type == null) {
            // In this case we cannot do much: The result object is returned.
            return (R) result;
        }

        if (result == null) {
            return null;
        }
        if (type.isInstance(result)) {
            return type.cast(result);
        }
        throw new ConversionException("Unsupported target type: " + type);
    }

    /** The default value specified to our Constructor, if any. */
    protected final T defaultValue;

    /** The locale specified to our Constructor, by default - system locale. */
    protected final Locale locale;

    /** The flag indicating whether the given pattern string is localized or not. */
    protected final boolean localizedPattern;

    /** The default pattern specified to our Constructor, if any. */
    protected final String pattern;

    /** Should we return the default value on conversion errors? */
    protected final boolean useDefault;

    /**
     * Constructs a {@link LocaleConverter} that will return the specified default value or throw a {@link ConversionException} if a conversion error occurs.
     *
     * @param defaultValue The default value to be returned
     * @param locale       The locale
     * @param pattern      The conversion pattern
     * @param useDefault   Indicate whether the default value is used or not
     * @param locPattern   Indicate whether the pattern is localized or not
     */
    protected BaseLocaleConverter(final T defaultValue, final Locale locale, final String pattern, final boolean useDefault, final boolean locPattern) {
        this.defaultValue = useDefault ? defaultValue : null;
        this.useDefault = useDefault;
        this.locale = locale != null ? locale : Locale.getDefault();
        this.pattern = pattern;
        this.localizedPattern = locPattern;
    }

    /**
     * Converts the specified locale-sensitive input object into an output object of the specified type. The default pattern is used for the conversion.
     *
     * @param type  Data type to which this value should be converted
     * @param value The input object to be converted
     * @return The converted value
     *
     * @throws ConversionException if conversion cannot be performed successfully
     */
    @Override
    public <R> R convert(final Class<R> type, final Object value) {
        return convert(type, value, null);
    }

    /**
     * Converts the specified locale-sensitive input object into an output object of the specified type.
     *
     * @param type    Data is type to which this value should be converted
     * @param value   is the input object to be converted
     * @param pattern is the pattern is used for the conversion; if null is passed then the default pattern associated with the converter object will be used.
     * @return The converted value
     *
     * @throws ConversionException if conversion cannot be performed successfully
     */
    @Override
    public <R> R convert(final Class<R> type, final Object value, final String pattern) {
        final Class<R> targetType = ConvertUtils.primitiveToWrapper(type);
        if (value == null) {
            if (useDefault) {
                return getDefaultAs(targetType);
            }
            // symmetric beanutils function allows null
            // so do not: throw new ConversionException("No value specified");
            LOG.debug("Null value specified for conversion, returning null");
            return null;
        }

        try {
            if (pattern != null) {
                return checkConversionResult(targetType, parse(value, pattern));
            }
            return checkConversionResult(targetType, parse(value, this.pattern));
        } catch (final Exception e) {
            if (useDefault) {
                return getDefaultAs(targetType);
            }
            if (e instanceof ConversionException) {
                throw (ConversionException) e;
            }
            throw new ConversionException(e);
        }
    }

    /**
     * Converts the specified locale-sensitive input object into an output object. The default pattern is used for the conversion.
     *
     * @param value The input object to be converted
     * @return The converted value
     *
     * @throws ConversionException if conversion cannot be performed successfully
     */
    public Object convert(final Object value) {
        return convert(value, null);
    }

    /**
     * Converts the specified locale-sensitive input object into an output object.
     *
     * @param value   The input object to be converted
     * @param pattern The pattern is used for the conversion
     * @return The converted value
     *
     * @throws ConversionException if conversion cannot be performed successfully
     */
    public T convert(final Object value, final String pattern) {
        return convert(null, value, pattern);
    }

    /**
     * Gets the default object specified for this converter cast for the given target type. If the default value is not conform to the given type, an exception
     * is thrown.
     *
     * @param type the target class of the conversion
     * @return the default value in the given target type
     * @throws ConversionException if the default object is not compatible with the target type
     */
    private <R> R getDefaultAs(final Class<R> type) {
        return checkConversionResult(type, defaultValue);
    }

    /**
     * Converts the specified locale-sensitive input object into an output object of the specified type.
     *
     * @param value   The input object to be converted
     * @param pattern The pattern is used for the conversion
     * @return The converted value
     *
     * @throws ParseException if conversion cannot be performed successfully
     */
    abstract protected T parse(Object value, String pattern) throws ParseException;
}
