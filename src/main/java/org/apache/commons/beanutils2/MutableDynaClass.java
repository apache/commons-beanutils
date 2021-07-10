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
 * <p>A specialized extension to {@code DynaClass} that allows properties
 * to be added or removed dynamically.</p>
 *
 * <p><strong>WARNING</strong> - No guarantees that this will be in the final
 * APIs ... it's here primarily to preserve some concepts that were in the
 * original proposal for further discussion.</p>
 *
 */

public interface MutableDynaClass extends DynaClass {

    /**
     * Add a new dynamic property with no restrictions on data type,
     * readability, or writeability.
     *
     * @param name Name of the new dynamic property
     *
     * @throws IllegalArgumentException if name is null
     * @throws IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    void add(String name);

    /**
     * Add a new dynamic property with the specified data type, but with
     * no restrictions on readability or writeability.
     *
     * @param name Name of the new dynamic property
     * @param type Data type of the new dynamic property (null for no
     *  restrictions)
     *
     * @throws IllegalArgumentException if name is null
     * @throws IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    void add(String name, Class<?> type);

    /**
     * Add a new dynamic property with the specified data type, readability,
     * and writeability.
     *
     * @param name Name of the new dynamic property
     * @param type Data type of the new dynamic property (null for no
     *  restrictions)
     * @param readable Set to {@code true} if this property value
     *  should be readable
     * @param writable Set to {@code true} if this property value
     *  should be writable
     *
     * @throws IllegalArgumentException if name is null
     * @throws IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    void add(String name, Class<?> type, boolean readable,
             boolean writable);

    /**
     * Is this DynaClass currently restricted, if so, no changes to the
     * existing registration of property names, data types, readability, or
     * writeability are allowed.
     *
     * @return {@code true} if this Mutable {@link DynaClass} is restricted,
     * otherwise {@code false}
     */
    boolean isRestricted();

    /**
     * Remove the specified dynamic property, and any associated data type,
     * readability, and writeability, from this dynamic class.
     * <strong>NOTE</strong> - This does <strong>NOT</strong> cause any
     * corresponding property values to be removed from DynaBean instances
     * associated with this DynaClass.
     *
     * @param name Name of the dynamic property to remove
     *
     * @throws IllegalArgumentException if name is null
     * @throws IllegalStateException if this DynaClass is currently
     *  restricted, so no properties can be removed
     */
    void remove(String name);

    /**
     * Sets the restricted state of this DynaClass to the specified value.
     *
     * @param restricted The new restricted state
     */
    void setRestricted(boolean restricted);

}
