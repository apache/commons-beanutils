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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.collections.FastHashMap;


/**
 * <p>Utility methods for populating JavaBeans properties via reflection.</p>
 *
 * <p>The implementations are provided by {@link BeanUtilsBean}.
 * These static utility methods use the default instance.
 * More sophisticated behaviour can be provided by using a <code>BeanUtilsBean</code> instance.</p>
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @author Rey François
 * @author Gregor Raýman
 * @version $Revision: 1.40 $ $Date: 2004/02/28 13:18:33 $
 * @see BeanUtilsBean
 */

public class BeanUtils {


    // ------------------------------------------------------ Private Variables


    /**
     * Dummy collection from the Commons Collections API, to force a
     * ClassNotFoundException if commons-collections.jar is not present in the
     * runtime classpath, and this class is the first one referenced.
     * Otherwise, the ClassNotFoundException thrown by ConvertUtils or
     * PropertyUtils can get masked.
     */
    private static FastHashMap dummy = new FastHashMap();

    /**
     * The debugging detail level for this component.
     * @deprecated BeanUtils now uses commons-logging for all log messages.
     *             Use your favorite logging tool to configure logging for
     *             this class.
     */
    private static int debug = 0;

    /**
     * @deprecated BeanUtils now uses commons-logging for all log messages.
     *             Use your favorite logging tool to configure logging for
     *             this class.
     */
    public static int getDebug() {
        return (debug);
    }

    /**
     * @deprecated BeanUtils now uses commons-logging for all log messages.
     *             Use your favorite logging tool to configure logging for
     *             this class.
     */
    public static void setDebug(int newDebug) {
        debug = newDebug;
    }

    // --------------------------------------------------------- Class Methods


    /**
     * <p>Clone a bean based on the available property getters and setters,
     * even if the bean class itself does not implement Cloneable.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#cloneBean
     */
    public static Object cloneBean(Object bean)
            throws IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException {

        return BeanUtilsBean.getInstance().cloneBean(bean);

    }


    /**
     * <p>Copy property values from the origin bean to the destination bean
     * for all cases where the property names are the same.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#copyProperties
     */
    public static void copyProperties(Object dest, Object orig)
        throws IllegalAccessException, InvocationTargetException {
        
        BeanUtilsBean.getInstance().copyProperties(dest, orig);
    }


    /**
     * <p>Copy the specified property value to the specified destination bean,
     * performing any type conversion that is required.</p>    
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#copyProperty     
     */
    public static void copyProperty(Object bean, String name, Object value)
        throws IllegalAccessException, InvocationTargetException {

        BeanUtilsBean.getInstance().copyProperty(bean, name, value);
    }


    /**
     * <p>Return the entire set of properties for which the specified bean
     * provides a read method.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#describe 
     */
    public static Map describe(Object bean)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return BeanUtilsBean.getInstance().describe(bean);
    }


    /**
     * <p>Return the value of the specified array property of the specified
     * bean, as a String array.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#getArrayProperty 
     */
    public static String[] getArrayProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return BeanUtilsBean.getInstance().getArrayProperty(bean, name);
    }


    /**
     * <p>Return the value of the specified indexed property of the specified
     * bean, as a String.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#getIndexedProperty(Object, String)
     */
    public static String getIndexedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        
        return BeanUtilsBean.getInstance().getIndexedProperty(bean, name);

    }


    /**
     * Return the value of the specified indexed property of the specified
     * bean, as a String.  The index is specified as a method parameter and
     * must *not* be included in the property name expression
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#getIndexedProperty(Object, String, int)
     */
    public static String getIndexedProperty(Object bean,
                                            String name, int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

	return BeanUtilsBean.getInstance().getIndexedProperty(bean, name, index);

    }


    /**
     * </p>Return the value of the specified indexed property of the specified
     * bean, as a String.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#getMappedProperty(Object, String)
     */
    public static String getMappedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return BeanUtilsBean.getInstance().getMappedProperty(bean, name);

    }


    /**
     * </p>Return the value of the specified mapped property of the specified
     * bean, as a String.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#getMappedProperty(Object, String, String)
     */
    public static String getMappedProperty(Object bean,
                                           String name, String key)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return BeanUtilsBean.getInstance().getMappedProperty(bean, name, key);

    }


    /**
     * <p>Return the value of the (possibly nested) property of the specified
     * name, for the specified bean, as a String.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#getNestedProperty
     */
    public static String getNestedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

	return BeanUtilsBean.getInstance().getNestedProperty(bean, name);

    }


    /**
     * <p>Return the value of the specified property of the specified bean,
     * no matter which property reference format is used, as a String.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#getProperty
     */
    public static String getProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return BeanUtilsBean.getInstance().getProperty(bean, name);

    }


    /**
     * <p>Return the value of the specified simple property of the specified
     * bean, converted to a String.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#getSimpleProperty
     */
    public static String getSimpleProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

	return BeanUtilsBean.getInstance().getSimpleProperty(bean, name);

    }


    /**
     * <p>Populate the JavaBeans properties of the specified bean, based on
     * the specified name/value pairs.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#populate
     */
    public static void populate(Object bean, Map properties)
        throws IllegalAccessException, InvocationTargetException {
        
        BeanUtilsBean.getInstance().populate(bean, properties);
    }


    /**
     * <p>Set the specified property value, performing type conversions as
     * required to conform to the type of the destination property.</p>
     *
     * <p>For more details see <code>BeanUtilsBean</code>.</p>
     *
     * @see BeanUtilsBean#setProperty
     */
    public static void setProperty(Object bean, String name, Object value)
        throws IllegalAccessException, InvocationTargetException {

        BeanUtilsBean.getInstance().setProperty(bean, name, value);
    }
}
