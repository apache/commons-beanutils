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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
 * @since 1.9.1
 */
class BeanIntrospectionData {
    /**
     * Initializes the map with the names of the write methods for the supported
     * properties. The method names - if defined - need to be stored separately because
     * they may get lost when the GC claims soft references used by the
     * {@code PropertyDescriptor} objects.
     *
     * @param descs the array with the descriptors of the available properties
     * @return the map with the names of write methods for properties
     */
    private static Map<String, String> setUpWriteMethodNames(final PropertyDescriptor[] descs) {
        final Map<String, String> methods = new HashMap<>();
        for (final PropertyDescriptor pd : descs) {
            final Method method = pd.getWriteMethod();
            if (method != null) {
                methods.put(pd.getName(), method.getName());
            }
        }
        return methods;
    }

    /** An array with property descriptors for the managed bean class. */
    private final PropertyDescriptor[] descriptors;

    /** A map for remembering the write method names for properties. */
    private final Map<String, String> writeMethodNames;

    /**
     * Creates a new instance of {@code BeanIntrospectionData} and initializes its
     * completely.
     *
     * @param descs the array with the descriptors of the available properties
     */
    public BeanIntrospectionData(final PropertyDescriptor[] descs) {
        this(descs, setUpWriteMethodNames(descs));
    }

    /**
     * Creates a new instance of {@code BeanIntrospectionData} and allows setting the map
     * with write method names. This constructor is mainly used for testing purposes.
     *
     * @param descs the array with the descriptors of the available properties
     * @param writeMethNames the map with the names of write methods
     */
    BeanIntrospectionData(final PropertyDescriptor[] descs, final Map<String, String> writeMethNames) {
        descriptors = descs;
        writeMethodNames = writeMethNames;
    }

    /**
     * Returns the {@code PropertyDescriptor} for the property with the specified name. If
     * this property is unknown, result is <b>null</b>.
     *
     * @param name the name of the property in question
     * @return the {@code PropertyDescriptor} for this property or <b>null</b>
     */
    public PropertyDescriptor getDescriptor(final String name) {
        for (final PropertyDescriptor pd : getDescriptors()) {
            if (name.equals(pd.getName())) {
                return pd;
            }
        }
        return null;
    }

    /**
     * Returns the array with property descriptors.
     *
     * @return the property descriptors for the associated bean class
     */
    public PropertyDescriptor[] getDescriptors() {
        return descriptors;
    }

    /**
     * Returns the write method for the property determined by the given
     * {@code PropertyDescriptor}. This information is normally available in the
     * descriptor object itself. However, at least by the ORACLE implementation, the
     * method is stored as a {@code SoftReference}. If this reference has been freed by
     * the GC, it may be the case that the method cannot be obtained again. Then,
     * additional information stored in this object is necessary to obtain the method
     * again.
     *
     * @param beanCls the class of the affected bean
     * @param desc the {@code PropertyDescriptor} of the desired property
     * @return the write method for this property or <b>null</b> if there is none
     */
    public Method getWriteMethod(final Class<?> beanCls, final PropertyDescriptor desc) {
        Method method = desc.getWriteMethod();
        if (method == null) {
            final String methodName = writeMethodNames.get(desc.getName());
            if (methodName != null) {
                method = MethodUtils.getAccessibleMethod(beanCls, methodName,
                        desc.getPropertyType());
                if (method != null) {
                    try {
                        desc.setWriteMethod(method);
                    } catch (final IntrospectionException e) {
                        // ignore, in this case the method is not cached
                    }
                }
            }
        }

        return method;
    }
}
