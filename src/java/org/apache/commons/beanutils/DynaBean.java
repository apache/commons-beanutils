/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/DynaBean.java,v 1.1 2001/12/24 23:27:04 craigmcc Exp $
 * $Revision: 1.1 $
 * $Date: 2001/12/24 23:27:04 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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


/**
 * <p>A <strong>DynaBean</strong> is a Java object that supports properties
 * whose names and data types, as well as values, may be dynamically modified.
 * To the maximum degree feasible, other components of the BeanUtils package
 * will recognize such beans and treat them as standard JavaBeans for the
 * purpose of retrieving and setting property values.</p>
 *
 * @author Craig McClanahan
 * @version $Revision: 1.1 $ $Date: 2001/12/24 23:27:04 $
 */

public interface DynaBean {


    /**
     * Return the value of a simple property with the specified name.  A
     * <code>null</code> return value means that either the property does
     * not exist, or that the property exists with a null value.  Use the
     * <code>contains()</code> method to distinguish these cases.
     *
     * @param name Name of the property whose value is to be retrieved
     *
     * @exception IllegalStateException if the specified property
     *  exists, but has been defined as write-only
     */
    public Object get(String name);


    /**
     * Return the value of an indexed property with the specified name.  A
     * <code>null</code> return value means that either the property does
     * not exist, or that the property exists with a null value.  Use the
     * <code>contains()</code> method to distinguish these cases.
     *
     * @param name Name of the property whose value is to be retrieved
     * @param index Index of the value to be retrieved
     *
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not indexed
     * @exception IllegalStateException if the specified property
     *  exists, but has been defined as write-only 
     */
    public Object get(String name, int index);


    /**
     * Return the value of a mapped property with the specified name.  A
     * <code>null</code> return value means that either the property does
     * not exist, or that the property exists with a null value.  Use the
     * <code>contains()</code> method to distinguish these cases.
     *
     * @param name Name of the property whose value is to be retrieved
     * @param key Key of the value to be retrieved
     *
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not mapped
     * @exception IllegalStateException if the specified property
     *  exists, but has been defined as write-only
     */
    public Object get(String name, String key);


    /**
     * Return the <code>DynaClass</code> instance that describes the set of
     * properties available for this DynaBean.
     */
    public DynaClass getDynaClass();


    /**
     * Set the value of a simple property with the specified name.  A null
     * value is allowed unless the underlying property type is a primitive.
     * If there is a Converter specified for our associated DynaClass, and
     * if the specified property is restricted to a particular data type,
     * the Converter will be used as necessary to convert the input value to
     * an object of the specified type.
     *
     * @param name Name of the property whose value is to be set
     * @param value Value to which this property is to be set
     *
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception IllegalArgumentException if the specified value cannot
     *  be converted to the required property type
     * @exception IllegalStateException if the specified property exists,
     *  but has been defined as read-only
     */
    public void set(String name, Object value);


    /**
     * Set the value of an indexed property with the specified name.  A null
     * value is allowed unless the underlying property type is a primitive.
     * If there is a Converter specified for our associated DynaClass, and
     * if the specified property is restricted to a particular data type,
     * the Converter will be used as necessary to convert the input value to
     * an object of the specified type.
     *
     * @param name Name of the property whose value is to be set
     * @param index Index of the property to be set
     * @param value Value to which this property is to be set
     *
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception IllegalArgumentException if the specified value cannot
     *  be converted to the required property type
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not indexed
     * @exception IllegalStateException if the specified property exists,
     *  but has been defined as read-only
     */
    public void set(String name, int index, Object value);


    /**
     * Set the value of a mapped property with the specified name.  A null
     * value is allowed unless the underlying property type is a primitive.
     * If there is a Converter specified for our associated DynaClass, and
     * if the specified property is restricted to a particular data type,
     * the Converter will be used as necessary to convert the input value to
     * an object of the specified type.
     *
     * @param name Name of the property whose value is to be set
     * @param key Key of the property to be set
     * @param value Value to which this property is to be set
     *
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception IllegalArgumentException if the specified value cannot
     *  be converted to the required property type
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not mapped
     * @exception IllegalStateException if the specified property exists,
     *  but has been defined as read-only
     */
    public void set(String name, String key, Object value);



}
