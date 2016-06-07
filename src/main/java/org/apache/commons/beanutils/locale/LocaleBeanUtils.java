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


import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.apache.commons.beanutils.BeanUtils;



/**
 * <p>Utility methods for populating JavaBeans properties
 * via reflection in a locale-dependent manner.</p>
 *
 * <p>The implementations for these methods are provided by <code>LocaleBeanUtilsBean</code>.
 * For more details see {@link LocaleBeanUtilsBean}.</p>
 *
 * @version $Id$
 */

public class LocaleBeanUtils extends BeanUtils {


    // ----------------------------------------------------- Instance Variables

    /**
     * <p>Gets the locale used when no locale is passed.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @return the default locale
     * @see LocaleBeanUtilsBean#getDefaultLocale()
     */
    public static Locale getDefaultLocale() {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getDefaultLocale();
    }


    /**
     * <p>Sets the locale used when no locale is passed.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param locale the default locale
     * @see LocaleBeanUtilsBean#setDefaultLocale(Locale)
     */
    public static void setDefaultLocale(final Locale locale) {

        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setDefaultLocale(locale);
    }

    /**
     * <p>Gets whether the pattern is localized or not.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @return <code>true</code> if pattern is localized,
     * otherwise <code>false</code>
     * @see LocaleBeanUtilsBean#getApplyLocalized()
     */
    public static boolean getApplyLocalized() {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getApplyLocalized();
    }

    /**
     * <p>Sets whether the pattern is localized or not.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param newApplyLocalized <code>true</code> if pattern is localized,
     * otherwise <code>false</code>
     * @see LocaleBeanUtilsBean#setApplyLocalized(boolean)
     */
    public static void setApplyLocalized(final boolean newApplyLocalized) {

        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setApplyLocalized(newApplyLocalized);
    }


    // --------------------------------------------------------- Public Methods

    /**
     * <p>Return the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname[index]</code> of the property value
     *  to be extracted
     * @param pattern The conversion pattern
     * @return The indexed property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getIndexedProperty(Object, String, String)
     */
    public static String getIndexedProperty(final Object bean, final String name, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, pattern);
    }

    /**
     * Return the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String using the default conversion pattern of
     * the corresponding {@link LocaleConverter}.
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname[index]</code> of the property value
     *  to be extracted
     * @return The indexed property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getIndexedProperty(Object, String)
     */
    public static String getIndexedProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name);
    }

    /**
     * <p>Return the value of the specified locale-sensetive indexed property
     * of the specified bean, as a String using the specified conversion pattern.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     * @param pattern The conversion pattern
     * @return The indexed property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getIndexedProperty(Object, String, int, String)
     */
    public static String getIndexedProperty(final Object bean,
                                            final String name, final int index, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, index, pattern);
    }

    /**
     * <p>Return the value of the specified locale-sensetive indexed property
     * of the specified bean, as a String using the default conversion pattern of
     * the corresponding {@link LocaleConverter}.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     * @return The indexed property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getIndexedProperty(Object, String, int)
     */
    public static String getIndexedProperty(final Object bean,
                                            final String name, final int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, index);
    }

    /**
     * <p>Return the value of the specified simple locale-sensitive property
     * of the specified bean, converted to a String using the specified
     * conversion pattern.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @param pattern The conversion pattern
     * @return The property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getSimpleProperty(Object, String, String)
     */
    public static String getSimpleProperty(final Object bean, final String name, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getSimpleProperty(bean, name, pattern);
    }

    /**
     * <p>Return the value of the specified simple locale-sensitive property
     * of the specified bean, converted to a String using the default
     * conversion pattern of the corresponding {@link LocaleConverter}.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @return The property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getSimpleProperty(Object, String)
     */
    public static String getSimpleProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getSimpleProperty(bean, name);
    }

    /**
     * <p>Return the value of the specified mapped locale-sensitive property
     * of the specified bean, as a String using the specified conversion pattern.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param key Lookup key of the property value to be extracted
     * @param pattern The conversion pattern
     * @return The mapped property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getMappedProperty(Object, String, String, String)
     */
    public static String getMappedProperty(final Object bean,
                                           final String name, final String key, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name, key, pattern);
    }

    /**
     * <p>Return the value of the specified mapped locale-sensitive property
     * of the specified bean, as a String
     * The key is specified as a method parameter and must *not* be included
     * in the property name expression.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param key Lookup key of the property value to be extracted
     * @return The mapped property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getMappedProperty(Object, String, String)
     */
    public static String getMappedProperty(final Object bean,
                                           final String name, final String key)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name, key);
    }


    /**
     * <p>Return the value of the specified locale-sensitive mapped property
     * of the specified bean, as a String using the specified pattern.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname(index)</code> of the property value
     *  to be extracted
     * @param pattern The conversion pattern
     * @return The mapped property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getMappedPropertyLocale(Object, String, String)
     */
    public static String getMappedPropertyLocale(final Object bean, final String name, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedPropertyLocale(bean, name, pattern);
    }


    /**
     * <p>Return the value of the specified locale-sensitive mapped property
     * of the specified bean, as a String using the default
     * conversion pattern of the corresponding {@link LocaleConverter}.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname(index)</code> of the property value
     *  to be extracted
     * @return The mapped property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getMappedProperty(Object, String)
     */
    public static String getMappedProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name);
    }

    /**
     * <p>Return the value of the (possibly nested) locale-sensitive property
     * of the specified name, for the specified bean,
     * as a String using the specified pattern.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @param pattern The conversion pattern
     * @return The nested property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getNestedProperty(Object, String, String)
     */
    public static String getNestedProperty(final Object bean, final String name, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getNestedProperty(bean, name, pattern);
    }

    /**
     * <p>Return the value of the (possibly nested) locale-sensitive property
     * of the specified name.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @return The nested property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getNestedProperty(Object, String)
     */
    public static String getNestedProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getNestedProperty(bean, name);
    }

    /**
     * <p>Return the value of the specified locale-sensitive property
     * of the specified bean.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *  to be extracted
     * @param pattern The conversion pattern
     * @return The nested property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getProperty(Object, String, String)
     */
    public static String getProperty(final Object bean, final String name, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getProperty(bean, name, pattern);
    }

    /**
     * <p>Return the value of the specified locale-sensitive property
     * of the specified bean.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *  to be extracted
     * @return The property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     *
     * @see LocaleBeanUtilsBean#getProperty(Object, String)
     */
    public static String getProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getProperty(bean, name);
    }

    /**
     * <p>Set the specified locale-sensitive property value, performing type
     * conversions as required to conform to the type of the destination property
     * using the default conversion pattern of the corresponding {@link LocaleConverter}.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean on which setting is to be performed
     * @param name Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     *
     * @see LocaleBeanUtilsBean#setProperty(Object, String, Object)
     */
    public static void setProperty(final Object bean, final String name, final Object value)
            throws IllegalAccessException, InvocationTargetException {

        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setProperty(bean, name, value);
    }

    /**
     * <p>Set the specified locale-sensitive property value, performing type
     * conversions as required to conform to the type of the destination
     * property using the specified conversion pattern.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param bean Bean on which setting is to be performed
     * @param name Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     * @param pattern The conversion pattern
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     *
     * @see LocaleBeanUtilsBean#setProperty(Object, String, Object, String)
     */
    public static void setProperty(final Object bean, final String name, final Object value, final String pattern)
            throws IllegalAccessException, InvocationTargetException {

        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setProperty(bean, name, value, pattern);
     }

    /**
     * <p>Calculate the property type.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param target The bean
     * @param name The property name
     * @param propName The Simple name of target property
     * @return The property's type
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     *
     * @see LocaleBeanUtilsBean#definePropertyType(Object, String, String)
     */
    protected static Class<?> definePropertyType(final Object target, final String name, final String propName)
            throws IllegalAccessException, InvocationTargetException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().definePropertyType(target, name, propName);
    }

    /**
     * <p>Convert the specified value to the required type using the
     * specified conversion pattern.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param type The Java type of target property
     * @param index The indexed subscript value (if any)
     * @param value The value to be converted
     * @param pattern The conversion pattern
     * @return The converted value
     * @see LocaleBeanUtilsBean#convert(Class, int, Object, String)
     */
    protected static Object convert(final Class<?> type, final int index, final Object value, final String pattern) {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().convert(type, index, value, pattern);
    }

    /**
     * <p>Convert the specified value to the required type.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param type The Java type of target property
     * @param index The indexed subscript value (if any)
     * @param value The value to be converted
     * @return The converted value
     * @see LocaleBeanUtilsBean#convert(Class, int, Object)
     */
    protected static Object convert(final Class<?> type, final int index, final Object value) {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().convert(type, index, value);
    }

    /**
     * <p>Invoke the setter method.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @param target The bean
     * @param propName The Simple name of target property
     * @param key The Mapped key value (if any)
     * @param index The indexed subscript value (if any)
     * @param newValue The value to be set
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     *
     * @see LocaleBeanUtilsBean#invokeSetter(Object, String, String, int, Object)
     */
    protected static void invokeSetter(final Object target, final String propName, final String key, final int index, final Object newValue)
            throws IllegalAccessException, InvocationTargetException {

       LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().invokeSetter(target, propName, key, index, newValue);
    }

    /**
     * Resolve any nested expression to get the actual target bean.
     *
     * @deprecated moved into <code>LocaleBeanUtilsBean</code>
     * @param bean The bean
     * @param name The property name
     * @return The property's descriptor
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     */
    @Deprecated
    protected static Descriptor calculate(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException {

        final org.apache.commons.beanutils.locale.LocaleBeanUtilsBean.Descriptor descriptor
            = LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().calculate(bean, name);
        return new Descriptor(
                descriptor.getTarget(),
                descriptor.getName(),
                descriptor.getPropName(),
                descriptor.getKey(),
                descriptor.getIndex());
    }

    /** @deprecated moved into <code>LocaleBeanUtils</code> */
    @Deprecated
    protected static class Descriptor {

        private int index = -1;    // Indexed subscript value (if any)
        private String name;
        private String propName;   // Simple name of target property
        private String key;        // Mapped key value (if any)
        private Object target;

        /**
         * Construct a descriptor instance for the target bean and property.
         *
         * @param target The target bean
         * @param name The property name (includes indexed/mapped expr)
         * @param propName The property name
         * @param key The mapped property key (if any)
         * @param index The indexed property index (if any)
         */
        public Descriptor(final Object target, final String name, final String propName, final String key, final int index) {

            setTarget(target);
            setName(name);
            setPropName(propName);
            setKey(key);
            setIndex(index);
        }

        /**
         * Return the target bean.
         *
         * @return The descriptors target bean
         */
        public Object getTarget() {
            return target;
        }

        /**
         * Set the target bean.
         *
         * @param target The target bean
         */
        public void setTarget(final Object target) {
            this.target = target;
        }

        /**
         * Return the mapped property key.
         *
         * @return the mapped property key (if any)
         */
        public String getKey() {
            return key;
        }

        /**
         * Set the mapped property key.
         *
         * @param key The mapped property key (if any)
         */
        public void setKey(final String key) {
            this.key = key;
        }

        /**
         * Return indexed property index.
         *
         * @return indexed property index (if any)
         */
        public int getIndex() {
            return index;
        }

        /**
         * Set the indexed property index.
         *
         * @param index The indexed property index (if any)
         */
        public void setIndex(final int index) {
            this.index = index;
        }

        /**
         * Return property name (includes indexed/mapped expr).
         *
         * @return The property name (includes indexed/mapped expr)
         */
        public String getName() {
            return name;
        }

        /**
         * Set the property name (includes indexed/mapped expr).
         *
         * @param name The property name (includes indexed/mapped expr)
         */
        public void setName(final String name) {
            this.name = name;
        }

        /**
         * Return the property name.
         *
         * @return The property name
         */
        public String getPropName() {
            return propName;
        }

        /**
         * Set the property name.
         *
         * @param propName The property name
         */
        public void setPropName(final String propName) {
            this.propName = propName;
        }
    }
}


