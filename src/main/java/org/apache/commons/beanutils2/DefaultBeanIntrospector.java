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

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * The default {@link BeanIntrospector} implementation.
 * </p>
 * <p>
 * This class implements a default bean introspection algorithm based on the JDK
 * classes in the {@code java.beans} package. It discovers properties
 * conforming to the Java Beans specification.
 * </p>
 * <p>
 * This class is a singleton. The single instance can be obtained using the
 * {@code INSTANCE} field. It does not define any state and thus can be
 * shared by arbitrary clients. {@link PropertyUtils} per default uses this
 * instance as its only {@code BeanIntrospector} object.
 * </p>
 *
 * @since 1.9
 */
public class DefaultBeanIntrospector implements BeanIntrospector {

    /** The singleton instance of this class. */
    public static final BeanIntrospector INSTANCE = new DefaultBeanIntrospector();

    /** Constant for arguments types of a method that expects a list argument. */
    private static final Class<?>[] LIST_CLASS_PARAMETER = new Class[] { java.util.List.class };

    /** For logging. Each subclass gets its own log instance. */
    private final Log log = LogFactory.getLog(getClass());

    /**
     * Private constructor so that no instances can be created.
     */
    private DefaultBeanIntrospector() {
    }

    /**
     * This method fixes an issue where IndexedPropertyDescriptor behaves
     * differently in different versions of the JDK for 'indexed' properties
     * which use java.util.List (rather than an array). It implements a
     * workaround for Bug 28358. If you have a Bean with the following
     * getters/setters for an indexed property:
     *
     * <pre>
     * public List getFoo()
     * public Object getFoo(int index)
     * public void setFoo(List foo)
     * public void setFoo(int index, Object foo)
     * </pre>
     *
     * then the IndexedPropertyDescriptor's getReadMethod() and getWriteMethod()
     * behave as follows:
     * <ul>
     * <li>JDK 1.3.1_04: returns valid Method objects from these methods.</li>
     * <li>JDK 1.4.2_05: returns null from these methods.</li>
     * </ul>
     *
     * @param beanClass the current class to be inspected
     * @param descriptors the array with property descriptors
     */
    private void handleIndexedPropertyDescriptors(final Class<?> beanClass,
            final PropertyDescriptor[] descriptors) {
        for (final PropertyDescriptor pd : descriptors) {
            if (pd instanceof IndexedPropertyDescriptor) {
                final IndexedPropertyDescriptor descriptor = (IndexedPropertyDescriptor) pd;
                final String propName = descriptor.getName().substring(0, 1)
                        .toUpperCase()
                        + descriptor.getName().substring(1);

                if (descriptor.getReadMethod() == null) {
                    final String methodName = descriptor.getIndexedReadMethod() != null ? descriptor
                            .getIndexedReadMethod().getName() : "get"
                            + propName;
                    final Method readMethod = MethodUtils
                            .getMatchingAccessibleMethod(beanClass, methodName,
                                    BeanUtils.EMPTY_CLASS_ARRAY);
                    if (readMethod != null) {
                        try {
                            descriptor.setReadMethod(readMethod);
                        } catch (final Exception e) {
                            log.error(
                                    "Error setting indexed property read method",
                                    e);
                        }
                    }
                }
                if (descriptor.getWriteMethod() == null) {
                    final String methodName = descriptor.getIndexedWriteMethod() != null ? descriptor
                            .getIndexedWriteMethod().getName() : "set"
                            + propName;
                    Method writeMethod = MethodUtils
                            .getMatchingAccessibleMethod(beanClass, methodName,
                                    LIST_CLASS_PARAMETER);
                    if (writeMethod == null) {
                        for (final Method m : beanClass.getMethods()) {
                            if (m.getName().equals(methodName)) {
                                final Class<?>[] parameterTypes = m.getParameterTypes();
                                if (parameterTypes.length == 1
                                        && List.class
                                                .isAssignableFrom(parameterTypes[0])) {
                                    writeMethod = m;
                                    break;
                                }
                            }
                        }
                    }
                    if (writeMethod != null) {
                        try {
                            descriptor.setWriteMethod(writeMethod);
                        } catch (final Exception e) {
                            log.error(
                                    "Error setting indexed property write method",
                                    e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Performs introspection of a specific Java class. This implementation uses
     * the {@code java.beans.Introspector.getBeanInfo()} method to obtain
     * all property descriptors for the current class and adds them to the
     * passed in introspection context.
     *
     * @param icontext the introspection context
     */
    @Override
    public void introspect(final IntrospectionContext icontext) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(icontext.getTargetClass());
        } catch (final IntrospectionException e) {
            // no descriptors are added to the context
            log.error(
                    "Error when inspecting class " + icontext.getTargetClass(),
                    e);
            return;
        }

        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        if (descriptors == null) {
            descriptors = PropertyDescriptors.EMPTY_ARRAY;
        }

        handleIndexedPropertyDescriptors(icontext.getTargetClass(),
                descriptors);
        icontext.addPropertyDescriptors(descriptors);
    }
}
