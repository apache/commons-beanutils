/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/Attic/BeanToPropertyValueTransformer.java,v 1.2 2003/10/05 13:35:37 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/05 13:35:37 $
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
 
package org.apache.commons.beanutils;

import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;


/**
 * <p><code>Transformer</code> that outputs a property value.</p>
 *
 * <p>An implementation of <code>org.apache.commons.collections.Transformer</code> that transforms
 * the object provided by returning the value of a specified property of the object.  The
 * constructor for <code>BeanToPropertyValueTransformer</code> requires the name of the property
 * that will be used in the transformation.  The property can be a simple, nested, indexed, or
 * mapped property as defined by <code>org.apache.commons.beanutils.PropertyUtils</code>. If any
 * object in the property path specified by <code>propertyName</code> is <code>null</code> then the
 * outcome is based on the value of the <code>ignoreNull</code> attribute.
 * </p>
 *
 * <p>
 * A typical usage might look like:
 * <code><pre>
 * // create the transformer
 * BeanToPropertyValueTransformer transformer = new BeanToPropertyValueTransformer( "person.address.city" );
 *
 * // transform the Collection
 * Collection peoplesCities = CollectionUtils.collect( peopleCollection, transformer );
 * </pre></code>
 * </p>
 *
 * <p>
 * This would take a <code>Collection</code> of person objects and return a <code>Collection</code>
 * of objects which represents the cities in which each person lived. Assuming...
 * <ul>
 *    <li>
 *       The top level object in the <code>peeopleCollection</code> is an object which represents a
 *       person.
 *    </li>
 *    <li>
 *       The person object has a <code>getAddress()</code> method which returns an object which
 *       represents a person's address.
 *    </li>
 *    <li>
 *       The address object has a <code>getCity()</code> method which returns an object which
 *       represents the city in which a person lives.
 *    </li>
 * </ul>
 *
 * @author Norm Deane
 * @see org.apache.commons.beanutils.PropertyUtils
 * @see org.apache.commons.collections.Transformer
 */
public class BeanToPropertyValueTransformer implements Transformer {
   
    /** For logging. */
    private final Log log = LogFactory.getLog(this.getClass());

    /** The name of the property that will be used in the transformation of the object. */
    private String propertyName;

    /**
     * <p>Should null objects on the property path throw an <code>IllegalArgumentException</code>?</p>
     * <p>
     * Determines whether <code>null</code> objects in the property path will genenerate an
     * <code>IllegalArgumentException</code> or not. If set to <code>true</code> then if any objects
     * in the property path evaluate to <code>null</code> then the
     * <code>IllegalArgumentException</code> throw by <code>PropertyUtils</code> will be logged but
     * not rethrown and <code>null</code> will be returned.  If set to <code>false</code> then if any
     * objects in the property path evaluate to <code>null</code> then the
     * <code>IllegalArgumentException</code> throw by <code>PropertyUtils</code> will be logged and
     * rethrown.
     * </p>
     */
    private boolean ignoreNull;

    /**
     * Constructs a Transformer which does not ignore nulls.
     * Constructor which takes the name of the property that will be used in the transformation and
     * assumes <code>ignoreNull</code> to be <code>false</code>.
     *
     * @param propertyName The name of the property that will be used in the transformation.
     * @throws IllegalArgumentException If the <code>propertyName</code> is <code>null</code> or
     * empty.
     */
    public BeanToPropertyValueTransformer(String propertyName) {
        this(propertyName, false);
    }

    /**
     * Constructs a Transformer and sets ignoreNull.
     * Constructor which takes the name of the property that will be used in the transformation and
     * a boolean which determines whether <code>null</code> objects in the property path will
     * genenerate an <code>IllegalArgumentException</code> or not.
     *
     * @param propertyName The name of the property that will be used in the transformation.
     * @param ignoreNull Determines whether <code>null</code> objects in the property path will
     * genenerate an <code>IllegalArgumentException</code> or not.
     * @throws IllegalArgumentException If the <code>propertyName</code> is <code>null</code> or
     * empty.
     */
    public BeanToPropertyValueTransformer(String propertyName, boolean ignoreNull) {
        super();

        if ((propertyName != null) && (propertyName.length() > 0)) {
            this.propertyName = propertyName;
            this.ignoreNull = ignoreNull;
        } else {
            throw new IllegalArgumentException(
                "propertyName cannot be null or empty");
        }
    }

    /**
     * Returns the value of the property named in the transformer's constructor for
     * the object provided. If any object in the property path leading up to the target property is
     * <code>null</code> then the outcome will be based on the value of the <code>ignoreNull</code>
     * attribute. By default, <code>ignoreNull</code> is <code>false</code> and would result in an
     * <code>IllegalArgumentException</code> if an object in the property path leading up to the
     * target property is <code>null</code>.
     *
     * @param object The object to be transformed.
     * @return The value of the property named in the transformer's constructor for the object
     * provided.
     * @throws IllegalArgumentException If an IllegalAccessException, InvocationTargetException, or
     * NoSuchMethodException is thrown when trying to access the property specified on the object
     * provided. Or if an object in the property path provided is <code>null</code> and
     * <code>ignoreNull</code> is set to <code>false</code>.
     */
    public Object transform(Object object) {
       
        Object propertyValue = null;

        try {
            propertyValue = PropertyUtils.getProperty(object, propertyName);
        } catch (IllegalArgumentException e) {
            final String errorMsg = "Problem during transformation. Null value encountered in property path...";

            if (ignoreNull) {
                log.warn("WARNING: " + errorMsg, e);
            } else {
                log.error("ERROR: " + errorMsg, e);
                throw e;
            }
        } catch (IllegalAccessException e) {
            final String errorMsg = "Unable to access the property provided.";
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg);
        } catch (InvocationTargetException e) {
            final String errorMsg = "Exception occurred in property's getter";
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg);
        } catch (NoSuchMethodException e) {
            final String errorMsg = "No property found for name [" +
                propertyName + "]";
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg);
        }

        return propertyValue;
    }

    /**
     * Returns the name of the property that will be used in the transformation of the bean.
     *
     * @return The name of the property that will be used in the transformation of the bean.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Returns the flag which determines whether <code>null</code> objects in the property path will
     * genenerate an <code>IllegalArgumentException</code> or not. If set to <code>true</code> then
     * if any objects in the property path evaluate to <code>null</code> then the
     * <code>IllegalArgumentException</code> throw by <code>PropertyUtils</code> will be logged but
     * not rethrown and <code>null</code> will be returned.  If set to <code>false</code> then if any
     * objects in the property path evaluate to <code>null</code> then the
     * <code>IllegalArgumentException</code> throw by <code>PropertyUtils</code> will be logged and
     * rethrown.
     *
     * @return The flag which determines whether <code>null</code> objects in the property path will
     * genenerate an <code>IllegalArgumentException</code> or not.
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }
}
