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
package org.apache.commons.beanutils;

import java.beans.PropertyDescriptor;

/**
 * <p>
 * An internally used helper class for storing introspection information about a bean
 * class.
 * </p>
 * <p>
 * This class is used by {@link PropertyUtilsBean}. When accessing bean properties via
 * reflection information about the properties available and their types and access
 * methods must be present. {@code PropertyUtilsBean} stores this information in a cache
 * so that it can be accessed quickly. The cache stores instances of this class.
 * </p>
 * <p>
 * This class mainly stores information about the properties of a bean class. Per default,
 * this is contained in {@code PropertyDescriptor} objects. Some additional information
 * required by the {@code BeanUtils} library is also stored here.
 * </p>
 *
 * @version $Id: $
 * @since 1.9.1
 */
class BeanIntrospectionData {
    /** An array with property descriptors for the managed bean class. */
    private final PropertyDescriptor[] descriptors;

    /**
     * Creates a new instance of {@code BeanIntrospectionData} and initializes its
     * completely.
     *
     * @param descs the array with the descriptors of the available properties
     */
    public BeanIntrospectionData(PropertyDescriptor[] descs) {
        descriptors = descs;
    }

    /**
     * Returns the array with property descriptors.
     *
     * @return the property descriptors for the associated bean class
     */
    public PropertyDescriptor[] getDescriptors() {
        return descriptors;
    }
}
