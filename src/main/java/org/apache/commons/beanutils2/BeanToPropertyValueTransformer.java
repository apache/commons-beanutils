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

package org.apache.commons.beanutils2;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>{@code Transformer} that outputs a property value.</p>
 *
 * <p>An implementation of {@code java.util.function.Function} that transforms
 * the object provided by returning the value of a specified property of the object.  The
 * constructor for {@code BeanToPropertyValueTransformer} requires the name of the property
 * that will be used in the transformation.  The property can be a simple, nested, indexed, or
 * mapped property as defined by {@code org.apache.commons.beanutils2.PropertyUtils}. If any
 * object in the property path specified by {@code propertyName</code> is <code>null} then the
 * outcome is based on the value of the {@code ignoreNull} attribute.
 * </p>
 *
 * <p>
 * A typical usage might look like:
 * </p>
 * <pre><code>
 * // create the transformer
 * BeanToPropertyValueTransformer transformer = new BeanToPropertyValueTransformer( "person.address.city" );
 *
 * // transform the Collection
 * Collection peoplesCities = CollectionUtils.collect( peopleCollection, transformer );
 * </code></pre>
 *
 * <p>
 * This would take a {@code Collection</code> of person objects and return a <code>Collection}
 * of objects which represents the cities in which each person lived. Assuming...
 * <ul>
 *    <li>
 *       The top level object in the {@code peopleCollection} is an object which represents a
 *       person.
 *    </li>
 *    <li>
 *       The person object has a {@code getAddress()} method which returns an object which
 *       represents a person's address.
 *    </li>
 *    <li>
 *       The address object has a {@code getCity()} method which returns an object which
 *       represents the city in which a person lives.
 *    </li>
 * </ul>
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 *
 * @see org.apache.commons.beanutils2.PropertyUtils
 * @see java.util.function.Function
 */
public class BeanToPropertyValueTransformer<T, R> implements Function<T, R> {

    /** For logging. */
    private final Log log = LogFactory.getLog(this.getClass());

    /** The name of the property that will be used in the transformation of the object. */
    private String propertyName;

    /**
     * <p>Should null objects on the property path throw an {@code IllegalArgumentException}?</p>
     * <p>
     * Determines whether {@code null} objects in the property path will generate an
     * {@code IllegalArgumentException</code> or not. If set to <code>true} then if any objects
     * in the property path evaluate to {@code null} then the
     * {@code IllegalArgumentException</code> throw by <code>PropertyUtils} will be logged but
     * not re-thrown and {@code null</code> will be returned.  If set to <code>false} then if any
     * objects in the property path evaluate to {@code null} then the
     * {@code IllegalArgumentException</code> throw by <code>PropertyUtils} will be logged and
     * re-thrown.
     * </p>
     */
    private boolean ignoreNull;

    /**
     * Constructs a Transformer which does not ignore nulls.
     * Constructor which takes the name of the property that will be used in the transformation and
     * assumes {@code ignoreNull</code> to be <code>false}.
     *
     * @param propertyName The name of the property that will be used in the transformation.
     * @throws IllegalArgumentException If the {@code propertyName</code> is <code>null} or
     * empty.
     */
    public BeanToPropertyValueTransformer(final String propertyName) {
        this(propertyName, false);
    }

    /**
     * Constructs a Transformer and sets ignoreNull.
     * Constructor which takes the name of the property that will be used in the transformation and
     * a boolean which determines whether {@code null} objects in the property path will
     * generate an {@code IllegalArgumentException} or not.
     *
     * @param propertyName The name of the property that will be used in the transformation.
     * @param ignoreNull Determines whether {@code null} objects in the property path will
     * generate an {@code IllegalArgumentException} or not.
     * @throws IllegalArgumentException If the {@code propertyName</code> is <code>null} or
     * empty.
     */
    public BeanToPropertyValueTransformer(final String propertyName, final boolean ignoreNull) {
        super();

        if (propertyName != null && propertyName.length() > 0) {
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
     * {@code null</code> then the outcome will be based on the value of the <code>ignoreNull}
     * attribute. By default, {@code ignoreNull</code> is <code>false} and would result in an
     * {@code IllegalArgumentException} if an object in the property path leading up to the
     * target property is {@code null}.
     *
     * @param object The object to be transformed.
     * @return The value of the property named in the transformer's constructor for the object
     * provided.
     * @throws IllegalArgumentException If an IllegalAccessException, InvocationTargetException, or
     * NoSuchMethodException is thrown when trying to access the property specified on the object
     * provided. Or if an object in the property path provided is {@code null} and
     * {@code ignoreNull</code> is set to <code>false}.
     */
    @Override
    public R apply(final T object) {

        R propertyValue = null;

        try {
            propertyValue = (R) PropertyUtils.getProperty(object, propertyName);
        } catch (final IllegalArgumentException e) {
            final String errorMsg = "Problem during transformation. Null value encountered in property path...";

            if (ignoreNull) {
                log.warn("WARNING: " + errorMsg + e);
            } else {
                final IllegalArgumentException iae = new IllegalArgumentException(errorMsg);
                if (!BeanUtils.initCause(iae, e)) {
                    log.error(errorMsg, e);
                }
                throw iae;
            }
        } catch (final IllegalAccessException e) {
            final String errorMsg = "Unable to access the property provided.";
            final IllegalArgumentException iae = new IllegalArgumentException(errorMsg);
            if (!BeanUtils.initCause(iae, e)) {
                log.error(errorMsg, e);
            }
            throw iae;
        } catch (final InvocationTargetException e) {
            final String errorMsg = "Exception occurred in property's getter";
            final IllegalArgumentException iae = new IllegalArgumentException(errorMsg);
            if (!BeanUtils.initCause(iae, e)) {
                log.error(errorMsg, e);
            }
            throw iae;
        } catch (final NoSuchMethodException e) {
            final String errorMsg = "No property found for name [" +
                propertyName + "]";
            final IllegalArgumentException iae = new IllegalArgumentException(errorMsg);
            if (!BeanUtils.initCause(iae, e)) {
                log.error(errorMsg, e);
            }
            throw iae;
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
     * Returns the flag which determines whether {@code null} objects in the property path will
     * generate an {@code IllegalArgumentException</code> or not. If set to <code>true} then
     * if any objects in the property path evaluate to {@code null} then the
     * {@code IllegalArgumentException</code> throw by <code>PropertyUtils} will be logged but
     * not re-thrown and {@code null</code> will be returned.  If set to <code>false} then if any
     * objects in the property path evaluate to {@code null} then the
     * {@code IllegalArgumentException</code> throw by <code>PropertyUtils} will be logged and
     * re-thrown.
     *
     * @return The flag which determines whether {@code null} objects in the property path will
     * generate an {@code IllegalArgumentException} or not.
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }
}
