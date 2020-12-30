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
package org.apache.commons.beanutils2.expression;

/**
 * Property Name Expression Resolver.
 * <p>
 * Methods such as PropertyUtilsBean's {@code setNestedProperty()} method
 * use a {@code Resolver} to process a <i>property name</i>
 * expression and resolve <i>nested</i>, <i>indexed</i> and <i>mapped</i>
 * property names. The following code provides an example usage
 * demonstrating all the methods:
 *
 * <pre>
 *      // Iterate through a nested property expression
 *      while (resolver.hasNested(name)) {
 *
 *          // isolate a single property from a nested expression
 *          String next = resolver.next(name);
 *
 *          // Process...
 *          String property = resolver.getProperty(next);
 *          if (resolver.isIndexed(next)) {
 *
 *              int index = resolver.getIndex(next);
 *              bean = getIndexedProperty(bean, property, index);
 *
 *          } else if (resolver.isMapped(next)) {
 *
 *              String key = resolver.getKey(next);
 *              bean = getMappedProperty(bean, property, key);
 *
 *          } else {
 *
 *              bean = getSimpleProperty(bean, property);
 *
 *          }
 *
 *          // remove the processed property from the expression
 *          name = resolver.remove(name);
 *      }
 * </pre>
 *
 * In order to create an implementation, it is important to understand how
 * BeanUtils/PropertyUtils uses the {@code resolver}. The following are
 * the main methods that use it:
 * <ul>
 *   <li>{@link org.apache.commons.beanutils2.PropertyUtilsBean}
 *       <ul>
 *          <li>{@link org.apache.commons.beanutils2.PropertyUtilsBean#getIndexedProperty(Object, String)}</li>
 *          <li>{@link org.apache.commons.beanutils2.PropertyUtilsBean#getMappedProperty(Object, String)}</li>
 *          <li>{@link org.apache.commons.beanutils2.PropertyUtilsBean#getNestedProperty(Object, String)}</li>
 *          <li>{@link org.apache.commons.beanutils2.PropertyUtilsBean#getPropertyDescriptor(Object, String)}</li>
 *          <li>{@link org.apache.commons.beanutils2.PropertyUtilsBean#getSimpleProperty(Object, String)}</li>
 *          <li>{@link org.apache.commons.beanutils2.PropertyUtilsBean#setIndexedProperty(Object, String, Object)}</li>
 *          <li>{@link org.apache.commons.beanutils2.PropertyUtilsBean#setMappedProperty(Object, String, Object)}</li>
 *          <li>{@link org.apache.commons.beanutils2.PropertyUtilsBean#setNestedProperty(Object, String, Object)}</li>
 *          <li>{@link org.apache.commons.beanutils2.PropertyUtilsBean#setSimpleProperty(Object, String, Object)}</li>
 *      </ul>
 *   </li>
 *   <li>{@link org.apache.commons.beanutils2.BeanUtilsBean}
 *      <ul>
 *          <li>{@link org.apache.commons.beanutils2.BeanUtilsBean#copyProperty(Object, String, Object)}</li>
 *          <li>{@link org.apache.commons.beanutils2.BeanUtilsBean#setProperty(Object, String, Object)}</li>
 *      </ul>
 *   </li>
 *   <li>{@link org.apache.commons.beanutils2.locale.LocaleBeanUtilsBean}
 *      <ul>
 *          <li>{@link org.apache.commons.beanutils2.locale.LocaleBeanUtilsBean#setProperty(Object,
 *          String, Object, String)}</li>
 *      </ul>
 *   </li>
 * </ul>
 *
 * @see org.apache.commons.beanutils2.PropertyUtilsBean#setResolver(Resolver)
 * @since 1.8.0
 */
public interface Resolver {

    /**
     * Extract the index value from the property expression or -1.
     *
     * @param expression The property expression
     * @return The index value or -1 if the property is not indexed
     * @throws IllegalArgumentException If the indexed property is illegally
     * formed or has an invalid (non-numeric) value
     */
    int getIndex(String expression);

    /**
     * Extract the map key from the property expression or {@code null}.
     *
     * @param expression The property expression
     * @return The index value
     * @throws IllegalArgumentException If the mapped property is illegally formed
     */
    String getKey(String expression);

    /**
     * Gets the property name from the property expression.
     *
     * @param expression The property expression
     * @return The property name
     */
    String getProperty(String expression);

    /**
     * Indicates whether or not the expression
     * contains nested property expressions or not.
     *
     * @param expression The property expression
     * @return The next property expression
     */
    boolean hasNested(String expression);

    /**
     * Indicate whether the expression is for an indexed property or not.
     *
     * @param expression The property expression
     * @return {@code true} if the expression is indexed,
     *  otherwise {@code false}
     */
    boolean isIndexed(String expression);

    /**
     * Indicate whether the expression is for a mapped property or not.
     *
     * @param expression The property expression
     * @return {@code true} if the expression is mapped,
     *  otherwise {@code false}
     */
    boolean isMapped(String expression);

    /**
     * Extract the next property expression from the
     * current expression.
     *
     * @param expression The property expression
     * @return The next property expression
     */
    String next(String expression);

    /**
     * Remove the last property expression from the
     * current expression.
     *
     * @param expression The property expression
     * @return The new expression value, with first property
     * expression removed - null if there are no more expressions
     */
    String remove(String expression);

}
