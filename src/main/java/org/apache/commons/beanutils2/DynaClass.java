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
 * <p>A <strong>DynaClass</strong> is a simulation of the functionality of
 * {@code java.lang.Class} for classes implementing the
 * {@code DynaBean} interface.  DynaBean instances that share the same
 * DynaClass all have the same set of available properties, along with any
 * associated data types, read-only states, and write-only states.</p>
 */
public interface DynaClass {

    /**
     * <p>Returns an array of {@code PropertyDescriptor} for the properties
     * currently defined in this DynaClass.  If no properties are defined, a
     * zero-length array will be returned.</p>
     *
     * <p><strong>FIXME</strong> - Should we really be implementing
     * {@code getBeanInfo()} instead, which returns property descriptors
     * and a bunch of other stuff?</p>
     *
     * @return the set of properties for this DynaClass
     */
    DynaProperty[] getDynaProperties();

    /**
     * Returns a property descriptor for the specified property, if it exists;
     * otherwise, return {@code null}.
     *
     * @param name Name of the dynamic property for which a descriptor
     *  is requested
     * @return The descriptor for the specified property
     *
     * @throws IllegalArgumentException if no property name is specified
     */
    DynaProperty getDynaProperty(String name);

    /**
     * Returns the name of this DynaClass (analogous to the
     * {@code getName()} method of {@code java.lang.Class}, which
     * allows the same {@code DynaClass} implementation class to support
     * different dynamic classes, with different sets of properties.
     *
     * @return the name of the DynaClass
     */
    String getName();

    /**
     * Instantiates and return a new DynaBean instance, associated
     * with this DynaClass.
     *
     * @return A new {@code DynaBean} instance
     *
     * @throws IllegalAccessException if the Class or the appropriate
     *  constructor is not accessible
     * @throws InstantiationException if this Class represents an abstract
     *  class, an array class, a primitive type, or void; or if instantiation
     *  fails for some other reason
     */
    DynaBean newInstance() throws IllegalAccessException, InstantiationException;

}
