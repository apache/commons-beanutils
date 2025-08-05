/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>{@code Predicate} that evaluates a property value against a specified value.</p>
 * <p>
 * An implementation of {@code org.apache.commons.collections.Predicate} that evaluates a
 * property value on the object provided against a specified value and returns {@code true}
 * if equal; {@code false} otherwise.
 * The {@code BeanPropertyValueEqualsPredicate} constructor takes two parameters which
 * determine what property will be evaluated on the target object and what its expected value should
 * be.
 * </p>
 * <dl>
 *    <dt>
 *       <strong><code>
 *           public BeanPropertyValueEqualsPredicate(String propertyName, Object propertyValue)
 *       </code></strong>
 *    </dt>
 *    <dd>
 *       Will create a {@code Predicate} that will evaluate the target object and return
 *       {@code true} if the property specified by {@code propertyName} has a value which
 *       is equal to the the value specified by {@code propertyValue}. Or return
 *       {@code false} otherwise.
 *    </dd>
 * </dl>
 * <p>
 * <strong>Note:</strong> Property names can be a simple, nested, indexed, or mapped property as defined by
 * {@code org.apache.commons.beanutils.PropertyUtils}.  If any object in the property path
 * specified by {@code propertyName} is {@code null} then the outcome is based on the
 * value of the {@code ignoreNull} attribute.
 * </p>
 * <p>
 * A typical usage might look like:
 * </p>
 * <pre>
 * // create the closure
 * BeanPropertyValueEqualsPredicate predicate =
 *    new BeanPropertyValueEqualsPredicate( "activeEmployee", Boolean.FALSE );
 *
 * // filter the Collection
 * CollectionUtils.filter( peopleCollection, predicate );
 * </pre>
 * <p>
 * This would take a {@code Collection} of person objects and filter out any people whose
 * {@code activeEmployee} property is {@code false}. Assuming...
 * </p>
 * <ul>
 *    <li>
 *       The top level object in the {@code peeopleCollection} is an object which represents a
 *       person.
 *    </li>
 *    <li>
 *       The person object has a {@code getActiveEmployee()} method which returns
 *       the boolean value for the object's {@code activeEmployee} property.
 *    </li>
 * </ul>
 * <p>
 * Another typical usage might look like:
 * </p>
 * <pre>
 * // create the closure
 * BeanPropertyValueEqualsPredicate predicate =
 *    new BeanPropertyValueEqualsPredicate( "personId", "456-12-1234" );
 *
 * // search the Collection
 * CollectionUtils.find( peopleCollection, predicate );
 * </pre>
 * <p>
 * This would search a {@code Collection} of person objects and return the first object whose
 * </p>
 * {@code personId} property value equals {@code 456-12-1234}. Assuming...
 * <ul>
 *    <li>
 *       The top level object in the {@code peeopleCollection} is an object which represents a
 *       person.
 *    </li>
 *    <li>
 *       The person object has a {@code getPersonId()} method which returns
 *       the value for the object's {@code personId} property.
 *    </li>
 * </ul>
 *
 * @see org.apache.commons.beanutils.PropertyUtils
 * @see org.apache.commons.collections.Predicate
 */
public class BeanPropertyValueEqualsPredicate implements Predicate {

    /** For logging. */
    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * The name of the property which will be evaluated when this {@code Predicate} is executed.
     */
    private final String propertyName;

    /**
     * The value that the property specified by {@code propertyName}
     * will be compared to when this {@code Predicate} executes.
     */
    private final Object propertyValue;

    /**
     * <p>Should {@code null} objects in the property path be ignored?</p>
     * <p>
     * Determines whether {@code null} objects in the property path will genenerate an
     * {@code IllegalArgumentException} or not. If set to {@code true} then if any objects
     * in the property path evaluate to {@code null} then the
     * {@code IllegalArgumentException} throw by {@code PropertyUtils} will be logged but
     * not rethrown and {@code false} will be returned.  If set to {@code false} then if
     * any objects in the property path evaluate to {@code null} then the
     * {@code IllegalArgumentException} throw by {@code PropertyUtils} will be logged and
     * rethrown.
     * </p>
     */
    private final boolean ignoreNull;

    /**
     * Constructor which takes the name of the property, its expected value to be used in evaluation,
     * and assumes {@code ignoreNull} to be {@code false}.
     *
     * @param propertyName The name of the property that will be evaluated against the expected value.
     * @param propertyValue The value to use in object evaluation.
     * @throws IllegalArgumentException If the property name provided is null or empty.
     */
    public BeanPropertyValueEqualsPredicate(final String propertyName, final Object propertyValue) {
        this(propertyName, propertyValue, false);
    }

    /**
     * Constructor which takes the name of the property, its expected value
     * to be used in evaluation, and a boolean which determines whether {@code null} objects in
     * the property path will genenerate an {@code IllegalArgumentException} or not.
     *
     * @param propertyName The name of the property that will be evaluated against the expected value.
     * @param propertyValue The value to use in object evaluation.
     * @param ignoreNull Determines whether {@code null} objects in the property path will
     * genenerate an {@code IllegalArgumentException} or not.
     * @throws IllegalArgumentException If the property name provided is null or empty.
     */
    public BeanPropertyValueEqualsPredicate(final String propertyName, final Object propertyValue, final boolean ignoreNull) {
        if (propertyName == null || propertyName.length() <= 0) {
            throw new IllegalArgumentException("propertyName cannot be null or empty");
        }
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.ignoreNull = ignoreNull;
    }

    /**
     * Evaulates the object provided against the criteria specified when this
     * {@code BeanPropertyValueEqualsPredicate} was constructed.  Equality is based on
     * either reference or logical equality as defined by the property object's equals method. If
     * any object in the property path leading up to the target property is {@code null} then
     * the outcome will be based on the value of the {@code ignoreNull} attribute. By default,
     * {@code ignoreNull} is {@code false} and would result in an
     * {@code IllegalArgumentException} if an object in the property path leading up to the
     * target property is {@code null}.
     *
     * @param object The object to be evaluated.
     * @return True if the object provided meets all the criteria for this {@code Predicate};
     * false otherwise.
     * @throws IllegalArgumentException If an IllegalAccessException, InvocationTargetException, or
     * NoSuchMethodException is thrown when trying to access the property specified on the object
     * provided. Or if an object in the property path provided is {@code null} and
     * {@code ignoreNull} is set to {@code false}.
     */
    @Override
    public boolean evaluate(final Object object) {

        boolean evaluation = false;

        try {
            evaluation = evaluateValue(propertyValue,
                    PropertyUtils.getProperty(object, propertyName));
        } catch (final IllegalArgumentException e) {
            final String errorMsg = "Problem during evaluation. Null value encountered in property path...";

            if (!ignoreNull) {
                throw new IllegalArgumentException(errorMsg, e);
            }
            log.warn("WARNING: " + errorMsg + e);
        } catch (final IllegalAccessException e) {
            final String errorMsg = "Unable to access the property provided.";
            throw new IllegalArgumentException(errorMsg, e);
        } catch (final InvocationTargetException e) {
            final String errorMsg = "Exception occurred in property's getter";
            throw new IllegalArgumentException(errorMsg, e);
        } catch (final NoSuchMethodException e) {
            final String errorMsg = "Property not found.";
            throw new IllegalArgumentException(errorMsg, e);
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
    protected boolean evaluateValue(final Object expected, final Object actual) {
        return expected == actual || expected != null && expected.equals(actual);
    }

    /**
     * Returns the name of the property which will be evaluated when this {@code Predicate} is
     * executed.
     *
     * @return The name of the property which will be evaluated when this {@code Predicate} is
     * executed.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Returns the value that the property specified by {@code propertyName} will be compared to
     * when this {@code Predicate} executes.
     *
     * @return The value that the property specified by {@code propertyName} will be compared to
     * when this {@code Predicate} executes.
     */
    public Object getPropertyValue() {
        return propertyValue;
    }

    /**
     * Returns the flag which determines whether {@code null} objects in the property path will
     * genenerate an {@code IllegalArgumentException} or not. If set to {@code true} then
     * if any objects in the property path evaluate to {@code null} then the
     * {@code IllegalArgumentException} throw by {@code PropertyUtils} will be logged but
     * not rethrown and {@code false} will be returned.  If set to {@code false} then if
     * any objects in the property path evaluate to {@code null} then the
     * {@code IllegalArgumentException} throw by {@code PropertyUtils} will be logged and
     * rethrown.
     *
     * @return The flag which determines whether {@code null} objects in the property path will
     * genenerate an {@code IllegalArgumentException} or not.
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }
}
