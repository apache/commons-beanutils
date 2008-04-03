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
 * @author Rey Francois
 * @author Gregor Rayman
 * @author Jan Sorensen
 * @author Scott Sanders
 * @version $Revision$ $Date$
 * @see PropertyUtilsBean
 * @see org.apache.commons.beanutils.expression.Resolver
 */

public class PropertyUtils {


    // ----------------------------------------------------- Manifest Constants


    /**
     * The delimiter that preceeds the zero-relative subscript for an
     * indexed reference.
     *
     * @deprecated The notation used for property name expressions is now
     * dependant on the {@link org.apache.commons.beanutils.expression.Resolver}
     * implementation being used.
     */
    public static final char INDEXED_DELIM = '[';


    /**
     * The delimiter that follows the zero-relative subscript for an
     * indexed reference.
     *
     * @deprecated The notation used for property name expressions is now
     * dependant on the {@link org.apache.commons.beanutils.expression.Resolver}
     * implementation being used.
     */
    public static final char INDEXED_DELIM2 = ']';


    /**
     * The delimiter that preceeds the key of a mapped property.
     *
     * @deprecated The notation used for property name expressions is now
     * dependant on the {@link org.apache.commons.beanutils.expression.Resolver}
     * implementation being used.
     */
    public static final char MAPPED_DELIM = '(';


    /**
     * The delimiter that follows the key of a mapped property.
     *
     * @deprecated The notation used for property name expressions is now
     * dependant on the {@link org.apache.commons.beanutils.expression.Resolver}
     * implementation being used.
     */
    public static final char MAPPED_DELIM2 = ')';


    /**
     * The delimiter that separates the components of a nested reference.
     *
     * @deprecated The notation used for property name expressions is now
     * dependant on the {@link org.apache.commons.beanutils.expression.Resolver}
     * implementation being used.
     */
    public static final char NESTED_DELIM = '.';


    // ------------------------------------------------------- Static Variables


    /**
     * The debugging detail level for this component.
     * 
     * Note that this static variable will have unexpected side-effects if
     * this class is deployed in a shared classloader within a container.
     * However as it is actually completely ignored by this class due to its
     * deprecated status, it doesn't do any actual harm.
     *
     * @deprecated The <code>debug</code> static property is no longer used
     */
    private static int debug = 0;

    /**
     * The <code>debug</code> static property is no longer used
     * @return debug property
     * @deprecated The <code>debug</code> static property is no longer used
     */
    public static int getDebug() {
        return (debug);
    }

    /**
     * The <code>debug</code> static property is no longer used
     * @param newDebug debug property
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
     * @param dest Destination bean whose properties are modified
     * @param orig Origin bean whose properties are retrieved
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if the <code>dest</code> or
     *  <code>orig</code> argument is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean whose properties are to be extracted
     * @return The set of properties for the bean
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname[index]</code> of the property value
     *  to be extracted
     * @return the indexed property value
     *
     * @exception IndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying property
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     * @return the indexed property value
     *
     * @exception IndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying property
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname(key)</code> of the property value
     *  to be extracted
     * @return the mapped property value
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean whose property is to be extracted
     * @param name Mapped property name of the property value to be extracted
     * @param key Key of the property value to be extracted
     * @return the mapped property value
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param beanClass Bean class to be introspected
     * @return the mapped property descriptors
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
     * @param bean Bean to be introspected
     * @return the mapped property descriptors
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
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @return the nested property value
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception NestedNullException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException 
     * if the property accessor method throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *  to be extracted
     * @return the property value
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean for which a property descriptor is requested
     * @param name Possibly indexed and/or nested name of the property for
     *  which a property descriptor is requested
     * @return the property descriptor
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param beanClass Bean class for which property descriptors are requested
     * @return the property descriptors
     * @exception IllegalArgumentException if <code>beanClass</code> is null
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
     * @param bean Bean for which property descriptors are requested
     * @return the property descriptors
     * @exception IllegalArgumentException if <code>bean</code> is null
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
     * @param bean Bean for which a property descriptor is requested
     * @param name Possibly indexed and/or nested name of the property for
     *  which a property descriptor is requested
     * @return the property editor class
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean for which a property descriptor is requested
     * @param name Possibly indexed and/or nested name of the property for
     *  which a property descriptor is requested
     * @return The property type
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     * @see PropertyUtilsBean#getPropertyType(Object, String)
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
     * @param descriptor Property descriptor to return a getter for
     * @return The read method
     * @see PropertyUtilsBean#getReadMethod(PropertyDescriptor)
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
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @return The property value
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if the property name
     *  is nested or indexed
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param descriptor Property descriptor to return a setter for
     * @return The write method
     * @see PropertyUtilsBean#getWriteMethod(PropertyDescriptor)
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
     * @param bean Bean to be examined (may be a {@link DynaBean}
     * @param name Property name to be evaluated
     * @return <code>true</code> if the property is readable,
     * otherwise <code>false</code>
     *
     * @exception IllegalArgumentException if <code>bean</code>
     *  or <code>name</code> is <code>null</code>
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
     * @param bean Bean to be examined (may be a {@link DynaBean}
     * @param name Property name to be evaluated
     * @return <code>true</code> if the property is writeable,
     * otherwise <code>false</code>
     *
     * @exception IllegalArgumentException if <code>bean</code>
     *  or <code>name</code> is <code>null</code>
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
     * @param bean Bean whose property is to be modified
     * @param name <code>propertyname[index]</code> of the property value
     *  to be modified
     * @param value Value to which the specified property element
     *  should be set
     *
     * @exception IndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying property
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean whose property is to be set
     * @param name Simple property name of the property value to be set
     * @param index Index of the property value to be set
     * @param value Value to which the indexed property element is to be set
     *
     * @exception IndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying property
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean whose property is to be set
     * @param name <code>propertyname(key)</code> of the property value
     *  to be set
     * @param value The property value to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean whose property is to be set
     * @param name Mapped property name of the property value to be set
     * @param key Key of the property value to be set
     * @param value The property value to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean whose property is to be modified
     * @param name Possibly nested name of the property to be modified
     * @param value Value to which the property is to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean whose property is to be modified
     * @param name Possibly indexed and/or nested name of the property
     *  to be modified
     * @param value Value to which this property is to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
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
     * @param bean Bean whose property is to be modified
     * @param name Name of the property to be modified
     * @param value Value to which the property should be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if the property name is
     *  nested or indexed
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     * @see PropertyUtilsBean#setSimpleProperty
     */
    public static void setSimpleProperty(Object bean,
                                         String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        PropertyUtilsBean.getInstance().setSimpleProperty(bean, name, value);
    }


}
