/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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


import org.apache.commons.beanutils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;


/**
 * <p>Utility methods for populating JavaBeans properties
 * via reflection in a locale-dependent manner.</p>
 *
 * <p>The implementations for these methods are provided by <code>LocaleBeanUtilsBean</code>.
 * For more details see {@link LocaleBeanUtilsBean}.</p>
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @author Rey François
 * @author Gregor Raýman
 * @author Yauheny Mikulski
 */

public class LocaleBeanUtils extends BeanUtils {


    // ----------------------------------------------------- Instance Variables

    /** All logging goes through this logger */
    private static Log log = LogFactory.getLog(LocaleBeanUtils.class);

    /**
     * <p>Gets the locale used when no locale is passed.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
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
     * @see LocaleBeanUtilsBean#setDefaultLocale(Locale)
     */
    public static void setDefaultLocale(Locale locale) {

        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setDefaultLocale(locale);
    }

    /**
     * <p>Gets whether the pattern is localized or not.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
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
     * @see LocaleBeanUtilsBean#setApplyLocalized(boolean)
     */
    public static void setApplyLocalized(boolean newApplyLocalized) {

        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setApplyLocalized(newApplyLocalized);
    }


    // --------------------------------------------------------- Public Methods

    /**
     * <p>Return the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#getIndexedProperty(Object, String, String)
     */
    public static String getIndexedProperty(Object bean, String name, String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, pattern);
    }

    /**
     * Return the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String using the default convertion pattern of
     * the corresponding {@link LocaleConverter}.
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#getIndexedProperty(Object, String)
     */
    public static String getIndexedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name);
    }

    /**
     * <p>Return the value of the specified locale-sensetive indexed property
     * of the specified bean, as a String using the specified convertion pattern.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#getIndexedProperty(Object, String, int, String)
     */
    public static String getIndexedProperty(Object bean,
                                            String name, int index, String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, index, pattern);
    }

    /**
     * <p>Return the value of the specified locale-sensetive indexed property
     * of the specified bean, as a String using the default convertion pattern of
     * the corresponding {@link LocaleConverter}.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#getIndexedProperty(Object, String, int)
     */
    public static String getIndexedProperty(Object bean,
                                            String name, int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, index);
    }

    /**
     * <p>Return the value of the specified simple locale-sensitive property
     * of the specified bean, converted to a String using the specified
     * convertion pattern.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#getSimpleProperty(Object, String, String)
     */
    public static String getSimpleProperty(Object bean, String name, String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getSimpleProperty(bean, name, pattern);
    }

    /**
     * <p>Return the value of the specified simple locale-sensitive property
     * of the specified bean, converted to a String using the default
     * convertion pattern of the corresponding {@link LocaleConverter}.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#getSimpleProperty(Object, String)
     */
    public static String getSimpleProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getSimpleProperty(bean, name);
    }

    /**
     * <p>Return the value of the specified mapped locale-sensitive property
     * of the specified bean, as a String using the specified convertion pattern.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#getMappedProperty(Object, String, String, String)
     */
    public static String getMappedProperty(Object bean,
                                           String name, String key, String pattern)
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
     * @see LocaleBeanUtilsBean#getMappedProperty(Object, String, String)
     */
    public static String getMappedProperty(Object bean,
                                           String name, String key)
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
     * @see LocaleBeanUtilsBean#getMappedPropertyLocale(Object, String, String)
     */
    public static String getMappedPropertyLocale(Object bean, String name, String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedPropertyLocale(bean, name, pattern);
    }


    /**
     * <p>Return the value of the specified locale-sensitive mapped property
     * of the specified bean, as a String using the default
     * convertion pattern of the corresponding {@link LocaleConverter}.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#getMappedProperty(Object, String)
     */
    public static String getMappedProperty(Object bean, String name)
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
     * @see LocaleBeanUtilsBean#getNestedProperty(Object, String, String)
     */
    public static String getNestedProperty(Object bean, String name, String pattern)
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
     * @see LocaleBeanUtilsBean#getNestedProperty(Object, String)
     */
    public static String getNestedProperty(Object bean, String name)
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
     * @see LocaleBeanUtilsBean#getProperty(Object, String, String)
     */
    public static String getProperty(Object bean, String name, String pattern)
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
     * @see LocaleBeanUtilsBean#getProperty(Object, String)
     */
    public static String getProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getProperty(bean, name);
    }

    /**
     * <p>Set the specified locale-sensitive property value, performing type
     * conversions as required to conform to the type of the destination property
     * using the default convertion pattern of the corresponding {@link LocaleConverter}.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#setProperty(Object, String, Object)
     */
    public static void setProperty(Object bean, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {

        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setProperty(bean, name, value);
    }

    /**
     * <p>Set the specified locale-sensitive property value, performing type
     * conversions as required to conform to the type of the destination
     * property using the specified convertion pattern.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#setProperty(Object, String, Object, String)
     */
    public static void setProperty(Object bean, String name, Object value, String pattern)
            throws IllegalAccessException, InvocationTargetException {

        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setProperty(bean, name, value, pattern);
     }

    /**
     * <p>Calculate the property type.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#definePropertyType(Object, String, String)
     */
    protected static Class definePropertyType(Object target, String name, String propName)
            throws IllegalAccessException, InvocationTargetException {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().definePropertyType(target, name, propName);
    }

    /**
     * <p>Convert the specified value to the required type using the
     * specified convertion pattern.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#convert(Class, int, Object, String)
     */
    protected static Object convert(Class type, int index, Object value, String pattern) {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().convert(type, index, value, pattern);
    }

    /**
     * <p>Convert the specified value to the required type.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#convert(Class, int, Object)
     */
    protected static Object convert(Class type, int index, Object value) {

        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().convert(type, index, value);
    }

    /**
     * <p>Invoke the setter method.</p>
     *
     * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
     *
     * @see LocaleBeanUtilsBean#invokeSetter(Object, String, String, int, Object)
     */
    protected static void invokeSetter(Object target, String propName, String key, int index, Object newValue)
            throws IllegalAccessException, InvocationTargetException {

       LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().invokeSetter(target, propName, key, index, newValue);
    }

    /**
     * Resolve any nested expression to get the actual target bean.
     *
     * @deprecated moved into <code>LocaleBeanUtilsBean</code>
     * @param bean The bean
     * @param name The property name
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    protected static Descriptor calculate(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException {

        String propName = null;          // Simple name of target property
        int index = -1;                  // Indexed subscript value (if any)
        String key = null;               // Mapped key value (if any)

        Object target = bean;
        int delim = name.lastIndexOf(PropertyUtils.NESTED_DELIM);
        if (delim >= 0) {
            try {
                target =
                        PropertyUtils.getProperty(bean, name.substring(0, delim));
            }
            catch (NoSuchMethodException e) {
                return null; // Skip this property setter
            }
            name = name.substring(delim + 1);
            if (log.isTraceEnabled()) {
                log.trace("    Target bean = " + target);
                log.trace("    Target name = " + name);
            }
        }

        // Calculate the property name, index, and key values
        propName = name;
        int i = propName.indexOf(PropertyUtils.INDEXED_DELIM);
        if (i >= 0) {
            int k = propName.indexOf(PropertyUtils.INDEXED_DELIM2);
            try {
                index =
                        Integer.parseInt(propName.substring(i + 1, k));
            }
            catch (NumberFormatException e) {
                ;
            }
            propName = propName.substring(0, i);
        }
        int j = propName.indexOf(PropertyUtils.MAPPED_DELIM);
        if (j >= 0) {
            int k = propName.indexOf(PropertyUtils.MAPPED_DELIM2);
            try {
                key = propName.substring(j + 1, k);
            }
            catch (IndexOutOfBoundsException e) {
                ;
            }
            propName = propName.substring(0, j);
        }
        return new Descriptor(target, name, propName, key, index);
    }

    /** @deprecated moved into <code>LocaleBeanUtils</code> */
    protected static class Descriptor {

        private int index = -1;    // Indexed subscript value (if any)
        private String name;
        private String propName;   // Simple name of target property
        private String key;        // Mapped key value (if any)
        private Object target;

        public Descriptor(Object target, String name, String propName, String key, int index) {

            setTarget(target);
            setName(name);
            setPropName(propName);
            setKey(key);
            setIndex(index);
        }

        public Object getTarget() {
            return target;
        }

        public void setTarget(Object target) {
            this.target = target;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPropName() {
            return propName;
        }

        public void setPropName(String propName) {
            this.propName = propName;
        }
    }
}


