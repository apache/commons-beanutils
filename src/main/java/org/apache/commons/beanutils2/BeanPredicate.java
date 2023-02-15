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
import java.util.function.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Predicate implementation that applies the given {@code Predicate}
 * to the result of calling the given property getter.
 * </p>
 *
 * @param <T> the type of the input to the predicate
 */
public class BeanPredicate<T> implements Predicate<T> {

    private final Log log = LogFactory.getLog(this.getClass());

    /** Name of the property whose value will be predicated */
    private String propertyName;

    /** {@code Predicate} to be applied to the property value */
    private Predicate<T> predicate;

    /**
     * Constructs a {@code BeanPredicate} that applies the given
     * {@code Predicate} to the named property value.
     * @param propertyName the name of the property whose value is to be predicated,
     * not null
     * @param predicate the {@code Predicate} to be applied,
     * not null
     */
    public BeanPredicate(final String propertyName, final Predicate<T> predicate) {
        this.propertyName = propertyName;
        this.predicate = predicate;
    }

    /**
     * Gets the {@code Predicate} to be applied to the value of the named property
     * during {@link #test(Object)}.
     * @return {@code Predicate}, not null
     */
    public Predicate<T> getPredicate() {
        return predicate;
    }

    /**
     * Gets the name of the property whose value is to be predicated.
     * in the evaluation.
     * @return the property name, not null
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the {@code Predicate} to be applied to the value of the named property
     * during {@link #test(Object)}.
     * @param predicate {@code Predicate}, not null
     */
    public void setPredicate(final Predicate<T> predicate) {
        this.predicate = predicate;
    }

    /**
     * Sets the name of the property whose value is to be predicated.
     * @param propertyName the name of the property whose value is to be predicated,
     * not null
     */
    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Evaluates the given object by applying the {@link #getPredicate()}
     * to a property value named by {@link #getPropertyName()}.
     *
     * @param object The object to test
     * @return the result of the predicate evaluation
     * @throws IllegalArgumentException when the property cannot be evaluated
     */
    @Override
    public boolean test(final Object object) {
        boolean evaluation = false;

        try {
            final T propValue = (T) PropertyUtils.getProperty(object, propertyName);
            evaluation = predicate.test(propValue);
        } catch (final IllegalArgumentException e) {
            final String errorMsg = "Problem during evaluation.";
            log.error(errorMsg, e);
            throw e;
        } catch (final IllegalAccessException e) {
            final String errorMsg = "Unable to access the property provided.";
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg);
        } catch (final InvocationTargetException e) {
            final String errorMsg = "Exception occurred in property's getter";
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg);
        } catch (final NoSuchMethodException e) {
            final String errorMsg = "Property not found.";
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg);
        }

        return evaluation;
    }

}
