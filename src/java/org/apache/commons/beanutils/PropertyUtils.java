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


package org.apache.commons.beanutils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.collections.FastHashMap;


/**
 * <p>Utility methods for using Java Reflection APIs to facilitate generic
 * property getter and setter operations on Java objects.</p>
 *
 * <p>The implementations for these methods are provided by <code>PropertyUtilsBean</code>.
 * For more details see {@link PropertyUtilsBean}.</p>
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @author Rey François
 * @author Gregor Raýman
 * @author Jan Sorensen
 * @author Scott Sanders
 * @version $Revision: 1.42.2.1 $ $Date: 2004/07/27 21:31:00 $
 * @see PropertyUtilsBean
 */

public class PropertyUtils {


    // ----------------------------------------------------- Manifest Constants


    /**
     * The delimiter that preceeds the zero-relative subscript for an
     * indexed reference.
     */
    public static final char INDEXED_DELIM = '[';


    /**
     * The delimiter that follows the zero-relative subscript for an
     * indexed reference.
     */
    public static final char INDEXED_DELIM2 = ']';


    /**
     * The delimiter that preceeds the key of a mapped property.
     */
    public static final char MAPPED_DELIM = '(';


    /**
     * The delimiter that follows the key of a mapped property.
     */
    public static final char MAPPED_DELIM2 = ')';


    /**
     * The delimiter that separates the components of a nested reference.
     */
    public static final char NESTED_DELIM = '.';


    // ------------------------------------------------------- Static Variables


    /**
     * The debugging detail level for this component.
     * @deprecated The <code>debug</code> static property is no longer used
     */
    private static int debug = 0;

    /**
     * @deprecated The <code>debug</code> static property is no longer used
     */
    public static int getDebug() {
        return (debug);
    }

    /**
     * @deprecated The <code>debug</code> static property is no longer used
     */
    public static void setDebug(int newDebug) {
        debug = newDebug;
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Clear any cached property descriptors information for all classes
     * loaded by any class loaders.  This is useful in cases where class
     * loaders are thrown away to implement class reloading.
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#clearDescriptors  
     */
    public static void clearDescriptors() {
	
        PropertyUtilsBean.getInstance().clearDescriptors();

    }


    /**
     * <p>Copy property values from the "origin" bean to the "destination" bean
     * for all cases where the property names are the same (even though the
     * actual getter and setter methods might have been customized via
     * <code>BeanInfo</code> classes).</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#copyProperties  
     */
    public static void copyProperties(Object dest, Object orig)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        PropertyUtilsBean.getInstance().copyProperties(dest, orig);
    }


    /**
     * <p>Return the entire set of properties for which the specified bean
     * provides a read method.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#describe  
     */
    public static Map describe(Object bean)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return (PropertyUtilsBean.getInstance().describe(bean));

    }


    /**
     * <p>Return the value of the specified indexed property of the specified
     * bean, with no type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getIndexedProperty(Object,String)  
     */
    public static Object getIndexedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return (PropertyUtilsBean.getInstance().getIndexedProperty(bean, name));

    }


    /**
     * <p>Return the value of the specified indexed property of the specified
     * bean, with no type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getIndexedProperty(Object,String, int)  
     */
    public static Object getIndexedProperty(Object bean,
                                            String name, int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return (PropertyUtilsBean.getInstance().getIndexedProperty(bean, name, index));
    }


    /**
     * <p>Return the value of the specified mapped property of the
     * specified bean, with no type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getMappedProperty(Object,String)  
     */
    public static Object getMappedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return (PropertyUtilsBean.getInstance().getMappedProperty(bean, name));

    }


    /**
     * <p>Return the value of the specified mapped property of the specified
     * bean, with no type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getMappedProperty(Object,String, String)  
     */
    public static Object getMappedProperty(Object bean,
                                           String name, String key)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return PropertyUtilsBean.getInstance().getMappedProperty(bean, name, key);

    }


    /**
     * <p>Return the mapped property descriptors for this bean class.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getMappedPropertyDescriptors(Class)
     * @deprecated This method should not be exposed
     */
    public static FastHashMap getMappedPropertyDescriptors(Class beanClass) {
	
        return PropertyUtilsBean.getInstance().getMappedPropertyDescriptors(beanClass);

    }


    /**
     * <p>Return the mapped property descriptors for this bean.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getMappedPropertyDescriptors(Object)
     * @deprecated This method should not be exposed
     */
    public static FastHashMap getMappedPropertyDescriptors(Object bean) {

	return PropertyUtilsBean.getInstance().getMappedPropertyDescriptors(bean);

    }


    /**
     * <p>Return the value of the (possibly nested) property of the specified
     * name, for the specified bean, with no type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getNestedProperty
     */
    public static Object getNestedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return PropertyUtilsBean.getInstance().getNestedProperty(bean, name);
        
    }


    /**
     * <p>Return the value of the specified property of the specified bean,
     * no matter which property reference format is used, with no
     * type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getProperty
     */
    public static Object getProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return (PropertyUtilsBean.getInstance().getProperty(bean, name));

    }


    /**
     * <p>Retrieve the property descriptor for the specified property of the
     * specified bean, or return <code>null</code> if there is no such
     * descriptor.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getPropertyDescriptor
     */
    public static PropertyDescriptor getPropertyDescriptor(Object bean,
                                                           String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return PropertyUtilsBean.getInstance().getPropertyDescriptor(bean, name);

    }


    /**
     * <p>Retrieve the property descriptors for the specified class,
     * introspecting and caching them the first time a particular bean class
     * is encountered.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getPropertyDescriptors(Class)
     */
    public static PropertyDescriptor[]
            getPropertyDescriptors(Class beanClass) {

        return PropertyUtilsBean.getInstance().getPropertyDescriptors(beanClass);

    }


    /**
     * <p>Retrieve the property descriptors for the specified bean,
     * introspecting and caching them the first time a particular bean class
     * is encountered.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getPropertyDescriptors(Object)
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Object bean) {

        return PropertyUtilsBean.getInstance().getPropertyDescriptors(bean);

    }


    /**
     * <p>Return the Java Class repesenting the property editor class that has
     * been registered for this property (if any).</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getPropertyEditorClass(Object,String)
     */
    public static Class getPropertyEditorClass(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

	return PropertyUtilsBean.getInstance().getPropertyEditorClass(bean, name);

    }


    /**
     * <p>Return the Java Class representing the property type of the specified
     * property, or <code>null</code> if there is no such property for the
     * specified bean.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getPropertyType
     */
    public static Class getPropertyType(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return PropertyUtilsBean.getInstance().getPropertyType(bean, name);
    }


    /**
     * <p>Return an accessible property getter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getReadMethod
     */
    public static Method getReadMethod(PropertyDescriptor descriptor) {

        return (PropertyUtilsBean.getInstance().getReadMethod(descriptor));

    }


    /**
     * <p>Return the value of the specified simple property of the specified
     * bean, with no type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getSimpleProperty
     */
    public static Object getSimpleProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return PropertyUtilsBean.getInstance().getSimpleProperty(bean, name);
        
    }


    /**
     * <p>Return an accessible property setter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#getWriteMethod
     */
    public static Method getWriteMethod(PropertyDescriptor descriptor) {

        return PropertyUtilsBean.getInstance().getWriteMethod(descriptor);

    }


    /**
     * <p>Return <code>true</code> if the specified property name identifies
     * a readable property on the specified bean; otherwise, return
     * <code>false</code>.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#isReadable
     * @since BeanUtils 1.6
     */
    public static boolean isReadable(Object bean, String name) {

        return PropertyUtilsBean.getInstance().isReadable(bean, name);
    }


    /**
     * <p>Return <code>true</code> if the specified property name identifies
     * a writeable property on the specified bean; otherwise, return
     * <code>false</code>.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#isWriteable
     * @since BeanUtils 1.6
     */
    public static boolean isWriteable(Object bean, String name) {

	return PropertyUtilsBean.getInstance().isWriteable(bean, name);
    }


    /**
     * <p>Sets the value of the specified indexed property of the specified
     * bean, with no type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#setIndexedProperty(Object, String, Object)
     */
    public static void setIndexedProperty(Object bean, String name,
                                          Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        PropertyUtilsBean.getInstance().setIndexedProperty(bean, name, value);

    }


    /**
     * <p>Sets the value of the specified indexed property of the specified
     * bean, with no type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#setIndexedProperty(Object, String, Object)
     */
    public static void setIndexedProperty(Object bean, String name,
                                          int index, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        PropertyUtilsBean.getInstance().setIndexedProperty(bean, name, index, value);
    }


    /**
     * <p>Sets the value of the specified mapped property of the
     * specified bean, with no type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#setMappedProperty(Object, String, Object)
     */
    public static void setMappedProperty(Object bean, String name,
                                         Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        PropertyUtilsBean.getInstance().setMappedProperty(bean, name, value);
    }


    /**
     * <p>Sets the value of the specified mapped property of the specified
     * bean, with no type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#setMappedProperty(Object, String, String, Object)
     */
    public static void setMappedProperty(Object bean, String name,
                                         String key, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        PropertyUtilsBean.getInstance().setMappedProperty(bean, name, key, value);
    }


    /**
     * <p>Sets the value of the (possibly nested) property of the specified
     * name, for the specified bean, with no type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#setNestedProperty
     */
    public static void setNestedProperty(Object bean,
                                         String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        PropertyUtilsBean.getInstance().setNestedProperty(bean, name, value);
    }


    /**
     * <p>Set the value of the specified property of the specified bean,
     * no matter which property reference format is used, with no
     * type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#setProperty
     */
    public static void setProperty(Object bean, String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        PropertyUtilsBean.getInstance().setProperty(bean, name, value);

    }


    /**
     * <p>Set the value of the specified simple property of the specified bean,
     * with no type conversions.</p>
     *
     * <p>For more details see <code>PropertyUtilsBean</code>.</p>
     *
     * @see PropertyUtilsBean#setSimpleProperty
     */
    public static void setSimpleProperty(Object bean,
                                         String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        PropertyUtilsBean.getInstance().setSimpleProperty(bean, name, value);
    }


}
