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

import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>{@code Transformer} that outputs a property value.</p>
 *
 * <p>An implementation of {@code org.apache.commons.collections.Transformer} that transforms
 * the object provided by returning the value of a specified property of the object.  The
 * constructor for {@code BeanToPropertyValueTransformer} requires the name of the property
 * that will be used in the transformation.  The property can be a simple, nested, indexed, or
 * mapped property as defined by {@code org.apache.commons.beanutils.PropertyUtils}. If any
 * object in the property path specified by {@code propertyName} is {@code null} then the
 * outcome is based on the value of the {@code ignoreNull} attribute.
 * </p>
 * <p>
 * A typical usage might look like:
 * </p>
 * <pre>
 * // create the transformer
 * BeanToPropertyValueTransformer transformer = new BeanToPropertyValueTransformer( "person.address.city" );
 *
 * // transform the Collection
 * Collection peoplesCities = CollectionUtils.collect( peopleCollection, transformer );
 * </pre>
 * <p>
 * This would take a {@code Collection} of person objects and return a {@code Collection}
 * of objects which represents the cities in which each person lived. Assuming...
 * </p>
 * <ul>
 *    <li>
 *       The top level object in the {@code peeopleCollection} is an object which represents a
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
 * @see org.apache.commons.beanutils.PropertyUtils
 * @see org.apache.commons.collections.Transformer
 */
public class BeanToPropertyValueTransformer implements Transformer {

    /** For logging. */
    private final Log log = LogFactory.getLog(this.getClass());

    /** The name of the property that will be used in the transformation of the object. */
    private final String propertyName;

    /**
     * <p>Should null objects on the property path throw an {@code IllegalArgumentException}?</p>
     * <p>
     * Determines whether {@code null} objects in the property path will genenerate an
     * {@code IllegalArgumentException} or not. If set to {@code true} then if any objects
     * in the property path evaluate to {@code null} then the
     * {@code IllegalArgumentException} throw by {@code PropertyUtils} will be logged but
     * not rethrown and {@code null} will be returned.  If set to {@code false} then if any
     * objects in the property path evaluate to {@code null} then the
     * {@code IllegalArgumentException} throw by {@code PropertyUtils} will be logged and
     * rethrown.
     * </p>
     */
    private final boolean ignoreNull;

    /**
     * Constructs a Transformer which does not ignore nulls.
     * Constructor which takes the name of the property that will be used in the transformation and
     * assumes {@code ignoreNull} to be {@code false}.
     *
     * @param propertyName The name of the property that will be used in the transformation.
     * @throws IllegalArgumentException If the {@code propertyName} is {@code null} or
     * empty.
     */
    public BeanToPropertyValueTransformer(final String propertyName) {
        this(propertyName, false);
    }

    /**
     * Constructs a Transformer and sets ignoreNull.
     * Constructor which takes the name of the property that will be used in the transformation and
     * a boolean which determines whether {@code null} objects in the property path will
     * genenerate an {@code IllegalArgumentException} or not.
     *
     * @param propertyName The name of the property that will be used in the transformation.
     * @param ignoreNull Determines whether {@code null} objects in the property path will
     * genenerate an {@code IllegalArgumentException} or not.
     * @throws IllegalArgumentException If the {@code propertyName} is {@code null} or
     * empty.
     */
    public BeanToPropertyValueTransformer(final String propertyName, final boolean ignoreNull) {
        if (propertyName == null || propertyName.length() <= 0) {
            throw new IllegalArgumentException(
                "propertyName cannot be null or empty");
        }
        this.propertyName = propertyName;
        this.ignoreNull = ignoreNull;
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
     * genenerate an {@code IllegalArgumentException} or not. If set to {@code true} then
     * if any objects in the property path evaluate to {@code null} then the
     * {@code IllegalArgumentException} throw by {@code PropertyUtils} will be logged but
     * not rethrown and {@code null} will be returned.  If set to {@code false} then if any
     * objects in the property path evaluate to {@code null} then the
     * {@code IllegalArgumentException} throw by {@code PropertyUtils} will be logged and
     * rethrown.
     *
     * @return The flag which determines whether {@code null} objects in the property path will
     * genenerate an {@code IllegalArgumentException} or not.
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    /**
     * Returns the value of the property named in the transformer's constructor for
     * the object provided. If any object in the property path leading up to the target property is
     * {@code null} then the outcome will be based on the value of the {@code ignoreNull}
     * attribute. By default, {@code ignoreNull} is {@code false} and would result in an
     * {@code IllegalArgumentException} if an object in the property path leading up to the
     * target property is {@code null}.
     *
     * @param object The object to be transformed.
     * @return The value of the property named in the transformer's constructor for the object
     * provided.
     * @throws IllegalArgumentException If an IllegalAccessException, InvocationTargetException, or
     * NoSuchMethodException is thrown when trying to access the property specified on the object
     * provided. Or if an object in the property path provided is {@code null} and
     * {@code ignoreNull} is set to {@code false}.
     */
    @Override
    public Object transform(final Object object) {

        Object propertyValue = null;

        try {
            propertyValue = PropertyUtils.getProperty(object, propertyName);
        } catch (final IllegalArgumentException e) {
            final String errorMsg = "Problem during transformation. Null value encountered in property path...";

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
            final String errorMsg = "No property found for name [" +
                propertyName + "]";
            throw new IllegalArgumentException(errorMsg, e);
        }

        return propertyValue;
    }
}
