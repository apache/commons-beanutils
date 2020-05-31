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
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>{@code Closure} that sets a property.</p>
 * <p>
 * An implementation of {@code java.util.function.Consumer} that updates
 * a specified property on the object provided with a specified value.
 * The {@code BeanPropertyValueChangeClosure} constructor takes two parameters which determine
 * what property will be updated and with what value.
 * <dl>
 *    <dt>
 *       {@code public BeanPropertyValueChangeClosure( String propertyName, Object propertyValue )}
 *    </dt>
 *    <dd>
 *       Will create a {@code Closure} that will update an object by setting the property
 *       specified by {@code propertyName</code> to the value specified by <code>propertyValue}.
 *    </dd>
 * </dl>
 *
 * <p>
 * <strong>Note:</strong> Property names can be a simple, nested, indexed, or mapped property as defined by
 * {@code org.apache.commons.beanutils2.PropertyUtils}.  If any object in the property path
 * specified by {@code propertyName</code> is <code>null} then the outcome is based on the
 * value of the {@code ignoreNull} attribute.
 * </p>
 * <p>
 * A typical usage might look like:
 * </p>
 * <pre><code>
 * // create the closure
 * BeanPropertyValueChangeClosure closure =
 *    new BeanPropertyValueChangeClosure( "activeEmployee", Boolean.TRUE );
 *
 * // update the Collection
 * CollectionUtils.forAllDo( peopleCollection, closure );
 * </code></pre>
 *
 * This would take a {@code Collection} of person objects and update the
 * {@code activeEmployee</code> property of each object in the <code>Collection} to
 * {@code true}. Assuming...
 * <ul>
 *    <li>
 *       The top level object in the {@code peopleCollection} is an object which represents a
 *       person.
 *    </li>
 *    <li>
 *       The person object has a {@code setActiveEmployee( boolean )} method which updates
 *       the value for the object's {@code activeEmployee} property.
 *    </li>
 * </ul>
 *
 * @param <T> The type of the input to the operation
 * @param <V> The property value type.
 *
 * @see org.apache.commons.beanutils2.PropertyUtils
 * @see java.util.function.Consumer
 */
public class BeanPropertyValueChangeConsumer<T, V> implements Consumer<T> {

    /** For logging. */
    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * The name of the property which will be updated when this {@code Closure} executes.
     */
    private String propertyName;

    /**
     * The value that the property specified by {@code propertyName}
     * will be updated to when this {@code Closure} executes.
     */
    private V propertyValue;

    /**
     * Determines whether {@code null} objects in the property path will generate an
     * {@code IllegalArgumentException</code> or not. If set to <code>true} then if any objects
     * in the property path leading up to the target property evaluate to {@code null} then the
     * {@code IllegalArgumentException</code> throw by <code>PropertyUtils} will be logged but
     * not re-thrown.  If set to {@code false} then if any objects in the property path leading
     * up to the target property evaluate to {@code null} then the
     * {@code IllegalArgumentException</code> throw by <code>PropertyUtils} will be logged and
     * re-thrown.
     */
    private boolean ignoreNull;

    /**
     * Constructor which takes the name of the property to be changed, the new value to set
     * the property to, and assumes {@code ignoreNull</code> to be <code>false}.
     *
     * @param propertyName The name of the property that will be updated with the value specified by
     * {@code propertyValue}.
     * @param propertyValue The value that {@code propertyName} will be set to on the target
     * object.
     * @throws IllegalArgumentException If the propertyName provided is null or empty.
     */
    public BeanPropertyValueChangeConsumer(final String propertyName, final V propertyValue) {
        this(propertyName, propertyValue, false);
    }

    /**
     * Constructor which takes the name of the property to be changed, the new value to set
     * the property to and a boolean which determines whether {@code null} objects in the
     * property path will generate an {@code IllegalArgumentException} or not.
     *
     * @param propertyName The name of the property that will be updated with the value specified by
     * {@code propertyValue}.
     * @param propertyValue The value that {@code propertyName} will be set to on the target
     * object.
     * @param ignoreNull Determines whether {@code null} objects in the property path will
     * generate an {@code IllegalArgumentException} or not.
     * @throws IllegalArgumentException If the propertyName provided is null or empty.
     */
    public BeanPropertyValueChangeConsumer(final String propertyName, final V propertyValue, final boolean ignoreNull) {
        super();

        if (propertyName != null && propertyName.length() > 0) {
            this.propertyName = propertyName;
            this.propertyValue = propertyValue;
            this.ignoreNull = ignoreNull;
        } else {
            throw new IllegalArgumentException("propertyName cannot be null or empty");
        }
    }

    /**
     * Updates the target object provided using the property update criteria provided when this
     * {@code BeanPropertyValueChangeClosure} was constructed.  If any object in the property
     * path leading up to the target property is {@code null} then the outcome will be based on
     * the value of the {@code ignoreNull</code> attribute. By default, <code>ignoreNull} is
     * {@code false</code> and would result in an <code>IllegalArgumentException} if an object
     * in the property path leading up to the target property is {@code null}.
     *
     * @param object The object to be updated.
     * @throws IllegalArgumentException If an IllegalAccessException, InvocationTargetException, or
     * NoSuchMethodException is thrown when trying to access the property specified on the object
     * provided. Or if an object in the property path provided is {@code null} and
     * {@code ignoreNull</code> is set to <code>false}.
     */
    @Override
    public void accept(final Object object) {

        try {
            PropertyUtils.setProperty(object, propertyName, propertyValue);
        } catch (final IllegalArgumentException e) {
            final String errorMsg = "Unable to execute Closure. Null value encountered in property path...";

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
            final String errorMsg = "Property not found";
            final IllegalArgumentException iae = new IllegalArgumentException(errorMsg);
            if (!BeanUtils.initCause(iae, e)) {
                log.error(errorMsg, e);
            }
            throw iae;
        }
    }

    /**
     * Returns the name of the property which will be updated when this {@code Closure} executes.
     *
     * @return The name of the property which will be updated when this {@code Closure} executes.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Returns the value that the property specified by {@code propertyName}
     * will be updated to when this {@code Closure} executes.
     *
     * @return The value that the property specified by {@code propertyName}
     * will be updated to when this {@code Closure} executes.
     */
    public V getPropertyValue() {
        return propertyValue;
    }

    /**
     * Returns the flag that determines whether {@code null} objects in the property path will
     * generate an {@code IllegalArgumentException</code> or not. If set to <code>true} then
     * if any objects in the property path leading up to the target property evaluate to
     * {@code null</code> then the <code>IllegalArgumentException} throw by
     * {@code PropertyUtils</code> will be logged but not re-thrown.  If set to <code>false} then
     * if any objects in the property path leading up to the target property evaluate to
     * {@code null</code> then the <code>IllegalArgumentException} throw by
     * {@code PropertyUtils} will be logged and re-thrown.
     *
     * @return The flag that determines whether {@code null} objects in the property path will
     * generate an {@code IllegalArgumentException} or not.
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }
}
