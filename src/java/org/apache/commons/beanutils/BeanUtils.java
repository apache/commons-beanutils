/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/BeanUtils.java,v 1.37 2003/03/03 22:33:46 rdonkin Exp $
 * $Revision: 1.37 $
 * $Date: 2003/03/03 22:33:46 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * @version $Revision: 1.37 $ $Date: 2003/03/03 22:33:46 $
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
