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

/**
 * <p>{@link BeanUtilsBean} implementation that creates a
 * {@link ConvertUtilsBean2} and delegates conversion to
 * {@link ConvertUtilsBean#convert(Object, Class)}.
 * </p>
 *
 * <p>
 * To configure this implementation for the current context ClassLoader invoke
 * {@code BeanUtilsBean.setInstance(new BeanUtilsBean2());}
 * </p>
 *
 * <p>
 * BeanUtils 1.7.0 delegated all conversion to String to the converter
 * registered for the {@code String.class}. One of the improvements in
 * BeanUtils 1.8.0 was to upgrade the {@link Converter} implementations so
 * that they could handle conversion to String for their type (e.g.
 * IntegerConverter now handles conversion from an Integer to a String as
 * well as String to Integer).
 * </p>
 *
 * <p>
 * In order to take advantage of these improvements BeanUtils needs to change
 * how it gets the appropriate {@link Converter}. This functionality has been
 * implemented in the new {@link ConvertUtilsBean#lookup(Class, Class)} and
 * {@link ConvertUtilsBean#convert(Object, Class)} methods. However changing
 * {@link BeanUtilsBean} to use these methods could create compatibility
 * issues for existing users. In order to avoid that, this new
 * {@link BeanUtilsBean} implementation has been created (and the associated
 * {@link ConvertUtilsBean2}).
 * </p>
 *
 * @see ConvertUtilsBean2
 * @since 1.8.0
 */
public class BeanUtilsBean2 extends BeanUtilsBean {

    /**
     * <p>Constructs an instance using new property
     * and conversion instances.</p>
     */
    public BeanUtilsBean2() {
        super(new ConvertUtilsBean2());
    }

    /**
     * <p>Converts the value to an object of the specified class (if
     * possible).</p>
     *
     * @param value Value to be converted (may be null)
     * @param type Class of the value to be converted to
     * @return The converted value
     */
    @Override
    protected <R> Object convert(final Object value, final Class<R> type) {
        return getConvertUtils().convert(value, type);
    }
}
