/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/Attic/BeanPropertyValueChangeClosure.java,v 1.1 2003/08/28 20:58:04 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/08/28 20:58:04 $
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

import org.apache.commons.collections.Closure;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;


/**
 * <p><code>Closure</code> that sets a property.</p>
 * <p>
 * An implementation of <code>org.apache.commons.collections.Closure</code> that updates
 * a specified property on the object provided with a specified value.
 * The <code>BeanPropertyValueChangeClosure</code> constructor takes two parameters which determine
 * what property will be updated and with what value.
 * <dl>
 *    <dt>
 *       <b><code><pre>public BeanPropertyValueChangeClosure( String propertyName, Object propertyValue )</pre></code></b>
 *    </dt>
 *    <dd>
 *       Will create a <code>Closure</code> that will update an object by setting the property
 *       specified by <code>propertyName</code> to the value specified by <code>propertyValue</code>.
 *    </dd>
 * </dl>
 *
 * <p/>
 * <strong>Note:</strong> Property names can be a simple, nested, indexed, or mapped property as defined by
 * <code>org.apache.commons.beanutils.PropertyUtils</code>.  If any object in the property path
 * specified by <code>propertyName</code> is <code>null</code> then the outcome is based on the
 * value of the <code>ignoreNull</code> attribute.
 *
 * <p/>
 * A typical usage might look like:
 * <code><pre>
 * // create the closure
 * BeanPropertyValueChangeClosure closure =
 *    new BeanPropertyValueChangeClosure( "activeEmployee", Boolean.TRUE );
 *
 * // update the Collection
 * CollectionUtils.forAllDo( peopleCollection, closure );
 * </pre></code>
 * <p/>
 *
 * This would take a <code>Collection</code> of person objects and update the
 * <code>activeEmployee</code> property of each object in the <code>Collection</code> to
 * <code>true</code>. Assuming...
 * <ul>
 *    <li>
 *       The top level object in the <code>peeopleCollection</code> is an object which represents a
 *       person.
 *    </li>
 *    <li>
 *       The person object has a <code>setActiveEmployee( boolean )</code> method which updates
 *       the value for the object's <code>activeEmployee</code> property.
 *    </li>
 * </ul>
 *
 * @author Norm Deane
 * @see org.apache.commons.beanutils.PropertyUtils
 * @see org.apache.commons.collections.Closure
 */
public class BeanPropertyValueChangeClosure implements Closure {
   
    /** For logging. */
    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * The name of the property which will be updated when this <code>Closure</code> executes.
     */
    private String propertyName;

    /**
     * The value that the property specified by <code>propertyName</code>
     * will be updated to when this <code>Closure</code> executes.
     */
    private Object propertyValue;

    /**
     * Determines whether <code>null</code> objects in the property path will genenerate an
     * <code>IllegalArgumentException</code> or not. If set to <code>true</code> then if any objects
     * in the property path leading up to the target property evaluate to <code>null</code> then the
     * <code>IllegalArgumentException</code> throw by <code>PropertyUtils</code> will be logged but
     * not rethrown.  If set to <code>false</code> then if any objects in the property path leading
     * up to the target property evaluate to <code>null</code> then the
     * <code>IllegalArgumentException</code> throw by <code>PropertyUtils</code> will be logged and
     * rethrown.
     */
    private boolean ignoreNull;

    /**
     * Constructor which takes the name of the property to be changed, the new value to set
     * the property to, and assumes <code>ignoreNull</code> to be <code>false</code>.
     *
     * @param propertyName The name of the property that will be updated with the value specified by
     * <code>propertyValue</code>.
     * @param propertyValue The value that <code>propertyName</code> will be set to on the target
     * object.
     * @throws IllegalArgumentException If the propertyName provided is null or empty.
     */
    public BeanPropertyValueChangeClosure(String propertyName, Object propertyValue) {
        this(propertyName, propertyValue, false);
    }

    /**
     * Constructor which takes the name of the property to be changed, the new value to set
     * the property to and a boolean which determines whether <code>null</code> objects in the
     * property path will genenerate an <code>IllegalArgumentException</code> or not.
     *
     * @param propertyName The name of the property that will be updated with the value specified by
     * <code>propertyValue</code>.
     * @param propertyValue The value that <code>propertyName</code> will be set to on the target
     * object.
     * @param ignoreNull Determines whether <code>null</code> objects in the property path will
     * genenerate an <code>IllegalArgumentException</code> or not.
     * @throws IllegalArgumentException If the propertyName provided is null or empty.
     */
    public BeanPropertyValueChangeClosure(String propertyName, Object propertyValue, boolean ignoreNull) {
        super();

        if ((propertyName != null) && (propertyName.length() > 0)) {
            this.propertyName = propertyName;
            this.propertyValue = propertyValue;
            this.ignoreNull = ignoreNull;
        } else {
            throw new IllegalArgumentException("propertyName cannot be null or empty");
        }
    }

    /**
     * Updates the target object provided using the property update criteria provided when this
     * <code>BeanPropertyValueChangeClosure</code> was constructed.  If any object in the property
     * path leading up to the target property is <code>null</code> then the outcome will be based on
     * the value of the <code>ignoreNull</code> attribute. By default, <code>ignoreNull</code> is
     * <code>false</code> and would result in an <code>IllegalArgumentException</code> if an object
     * in the property path leading up to the target property is <code>null</code>.
     *
     * @param object The object to be updated.
     * @throws IllegalArgumentException If an IllegalAccessException, InvocationTargetException, or
     * NoSuchMethodException is thrown when trying to access the property specified on the object
     * provided. Or if an object in the property path provided is <code>null</code> and
     * <code>ignoreNull</code> is set to <code>false</code>.
     */
    public void execute(Object object) {
       
        try {
            PropertyUtils.setProperty(object, propertyName, propertyValue);
        } catch (IllegalArgumentException e) {
            final String errorMsg = "Unable to execute Closure. Null value encountered in property path...";

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
            final String errorMsg = "Property not found";
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg);
        }
    }

    /**
     * Returns the name of the property which will be updated when this <code>Closure</code> executes.
     *
     * @return The name of the property which will be updated when this <code>Closure</code> executes.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Returns the value that the property specified by <code>propertyName</code>
     * will be updated to when this <code>Closure</code> executes.
     *
     * @return The value that the property specified by <code>propertyName</code>
     * will be updated to when this <code>Closure</code> executes.
     */
    public Object getPropertyValue() {
        return propertyValue;
    }

    /**
     * Returns the flag that determines whether <code>null</code> objects in the property path will
     * genenerate an <code>IllegalArgumentException</code> or not. If set to <code>true</code> then
     * if any objects in the property path leading up to the target property evaluate to
     * <code>null</code> then the <code>IllegalArgumentException</code> throw by
     * <code>PropertyUtils</code> will be logged but not rethrown.  If set to <code>false</code> then
     * if any objects in the property path leading up to the target property evaluate to
     * <code>null</code> then the <code>IllegalArgumentException</code> throw by
     * <code>PropertyUtils</code> will be logged and rethrown.
     *
     * @return The flag that determines whether <code>null</code> objects in the property path will
     * genenerate an <code>IllegalArgumentException</code> or not.
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }
}
