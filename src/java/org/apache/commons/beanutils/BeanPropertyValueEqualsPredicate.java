/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/Attic/BeanPropertyValueEqualsPredicate.java,v 1.3 2003/10/09 20:43:15 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/09 20:43:15 $
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
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
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

import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;


/**
 * <p><code>Predicate</code> that evaluates a property value against a specified value.</p>
 * <p>
 * An implementation of <code>org.apache.commons.collections.Predicate</code> that evaluates a
 * property value on the object provided against a specified value and returns <code>true</code>
 * if equal; <code>false</code> otherwise.
 * The <code>BeanPropertyValueEqualsPredicate</code> constructor takes two parameters which
 * determine what property will be evaluated on the target object and what its expected value should
 * be.
 * <dl>
 *    <dt>
 *       <strong><code><pre>public BeanPropertyValueEqualsPredicate( String propertyName, Object propertyValue )</pre></code></strong>
 *    </dt>
 *    <dd>
 *       Will create a <code>Predicate</code> that will evaluate the target object and return
 *       <code>true</code> if the property specified by <code>propertyName</code> has a value which
 *       is equal to the the value specified by <code>propertyValue</code>. Or return
 *       <code>false</code> otherwise.
 *    </dd>
 * </dl>
 * </p>
 * <p>
 * <strong>Note:</strong> Property names can be a simple, nested, indexed, or mapped property as defined by
 * <code>org.apache.commons.beanutils.PropertyUtils</code>.  If any object in the property path
 * specified by <code>propertyName</code> is <code>null</code> then the outcome is based on the
 * value of the <code>ignoreNull</code> attribute.
 * </p>
 * <p>
 * A typical usage might look like:
 * <code><pre>
 * // create the closure
 * BeanPropertyValueEqualsPredicate predicate =
 *    new BeanPropertyValueEqualsPredicate( "activeEmployee", Boolean.FALSE );
 *
 * // filter the Collection
 * CollectionUtils.filter( peopleCollection, predicate );
 * </pre></code>
 * </p>
 * <p>
 * This would take a <code>Collection</code> of person objects and filter out any people whose
 * <code>activeEmployee</code> property is <code>false</code>. Assuming...
 * <ul>
 *    <li>
 *       The top level object in the <code>peeopleCollection</code> is an object which represents a
 *       person.
 *    </li>
 *    <li>
 *       The person object has a <code>getActiveEmployee()</code> method which returns
 *       the boolean value for the object's <code>activeEmployee</code> property.
 *    </li>
 * </ul>
 * </p>
 * <p>
 * Another typical usage might look like:
 * <code><pre>
 * // create the closure
 * BeanPropertyValueEqualsPredicate predicate =
 *    new BeanPropertyValueEqualsPredicate( "personId", "456-12-1234" );
 *
 * // search the Collection
 * CollectionUtils.find( peopleCollection, predicate );
 * </pre><code>
 * </p>
 * <p>
 * This would search a <code>Collection</code> of person objects and return the first object whose
 * <code>personId</code> property value equals <code>456-12-1234</code>. Assuming...
 * <ul>
 *    <li>
 *       The top level object in the <code>peeopleCollection</code> is an object which represents a
 *       person.
 *    </li>
 *    <li>
 *       The person object has a <code>getPersonId()</code> method which returns
 *       the value for the object's <code>personId</code> property.
 *    </li>
 * </ul>
 * </p>
 *
 * @author Norm Deane
 * @see org.apache.commons.beanutils.PropertyUtils
 * @see org.apache.commons.collections.Predicate
 */
public class BeanPropertyValueEqualsPredicate implements Predicate {
   
    /** For logging. */
    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * The name of the property which will be evaluated when this <code>Predicate</code> is executed.
     */
    private String propertyName;

    /**
     * The value that the property specified by <code>propertyName</code>
     * will be compared to when this <code>Predicate</code> executes.
     */
    private Object propertyValue;

    /**
     * <p>Should <code>null</code> objects in the property path be ignored?</p>
     * <p>
     * Determines whether <code>null</code> objects in the property path will genenerate an
     * <code>IllegalArgumentException</code> or not. If set to <code>true</code> then if any objects
     * in the property path evaluate to <code>null</code> then the
     * <code>IllegalArgumentException</code> throw by <code>PropertyUtils</code> will be logged but
     * not rethrown and <code>false</code> will be returned.  If set to <code>false</code> then if
     * any objects in the property path evaluate to <code>null</code> then the
     * <code>IllegalArgumentException</code> throw by <code>PropertyUtils</code> will be logged and
     * rethrown.
     * </p>
     */
    private boolean ignoreNull;

    /**
     * Constructor which takes the name of the property, its expected value to be used in evaluation,
     * and assumes <code>ignoreNull</code> to be <code>false</code>.
     *
     * @param propertyName The name of the property that will be evaluated against the expected value.
     * @param propertyValue The value to use in object evaluation.
     * @throws IllegalArgumentException If the property name provided is null or empty.
     */
    public BeanPropertyValueEqualsPredicate(String propertyName, Object propertyValue) {
        this(propertyName, propertyValue, false);
    }

    /**
     * Constructor which takes the name of the property, its expected value
     * to be used in evaluation, and a boolean which determines whether <code>null</code> objects in
     * the property path will genenerate an <code>IllegalArgumentException</code> or not.
     *
     * @param propertyName The name of the property that will be evaluated against the expected value.
     * @param propertyValue The value to use in object evaluation.
     * @param ignoreNull Determines whether <code>null</code> objects in the property path will
     * genenerate an <code>IllegalArgumentException</code> or not.
     * @throws IllegalArgumentException If the property name provided is null or empty.
     */
    public BeanPropertyValueEqualsPredicate(String propertyName, Object propertyValue, boolean ignoreNull) {
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
     * Evaulates the object provided against the criteria specified when this
     * <code>BeanPropertyValueEqualsPredicate</code> was constructed.  Equality is based on
     * either reference or logical equality as defined by the property object's equals method. If
     * any object in the property path leading up to the target property is <code>null</code> then
     * the outcome will be based on the value of the <code>ignoreNull</code> attribute. By default,
     * <code>ignoreNull</code> is <code>false</code> and would result in an
     * <code>IllegalArgumentException</code> if an object in the property path leading up to the
     * target property is <code>null</code>.
     *
     * @param object The object to be evaluated.
     * @return True if the object provided meets all the criteria for this <code>Predicate</code>;
     * false otherwise.
     * @throws IllegalArgumentException If an IllegalAccessException, InvocationTargetException, or
     * NoSuchMethodException is thrown when trying to access the property specified on the object
     * provided. Or if an object in the property path provided is <code>null</code> and
     * <code>ignoreNull</code> is set to <code>false</code>.
     */
    public boolean evaluate(Object object) {
       
        boolean evaluation = false;

        try {
            evaluation = evaluateValue(propertyValue,
                    PropertyUtils.getProperty(object, propertyName));
        } catch (IllegalArgumentException e) {
            final String errorMsg = "Problem during evaluation. Null value encountered in property path...";

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
            final String errorMsg = "Property not found.";
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg);
        }

        return evaluation;
    }

    /**
     * Utility method which evaluates whether the actual property value equals the expected property
     * value.
     *
     * @param expected The expected value.
     * @param actual The actual value.
     * @return True if they are equal; false otherwise.
     */
    private boolean evaluateValue(Object expected, Object actual) {
        return (expected == actual) || ((expected != null) && expected.equals(actual));
    }

    /**
     * Returns the name of the property which will be evaluated when this <code>Predicate</code> is
     * executed.
     *
     * @return The name of the property which will be evaluated when this <code>Predicate</code> is
     * executed.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Returns the value that the property specified by <code>propertyName</code> will be compared to
     * when this <code>Predicate</code> executes.
     *
     * @return The value that the property specified by <code>propertyName</code> will be compared to
     * when this <code>Predicate</code> executes.
     */
    public Object getPropertyValue() {
        return propertyValue;
    }

    /**
     * Returns the flag which determines whether <code>null</code> objects in the property path will
     * genenerate an <code>IllegalArgumentException</code> or not. If set to <code>true</code> then
     * if any objects in the property path evaluate to <code>null</code> then the
     * <code>IllegalArgumentException</code> throw by <code>PropertyUtils</code> will be logged but
     * not rethrown and <code>false</code> will be returned.  If set to <code>false</code> then if
     * any objects in the property path evaluate to <code>null</code> then the
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
