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
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * An implementation of the {@code BeanIntrospector} interface which can
 * detect write methods for properties used in fluent API scenario.
 * </p>
 * <p>
 * A <em>fluent API</em> allows setting multiple properties using a single
 * statement by supporting so-called <em>method chaining</em>: Methods for
 * setting a property value do not return <b>void</b>, but an object which can
 * be called for setting another property. An example of such a fluent API could
 * look as follows:
 * </p>
 * <pre>
 * public class FooBuilder {
 *     public FooBuilder setFooProperty1(String value) {
 *        ...
 *        return this;
 *    }
 *
 *     public FooBuilder setFooProperty2(int value) {
 *        ...
 *        return this;
 *    }
 * }
 * </pre>
 *
 * <p>Per default, {@code PropertyUtils} does not detect methods like this
 * because, having a non-<b>void</b> return type, they violate the Java Beans
 * specification.
 * </p>
 * <p>
 * This class is more tolerant with regards to the return type of a set method.
 * It basically iterates over all methods of a class and filters them for a
 * configurable prefix (the default prefix is {@code set}). It then
 * generates corresponding {@code PropertyDescriptor} objects for the
 * methods found which use these methods as write methods.
 * </p>
 * <p>
 * An instance of this class is intended to collaborate with a
 * {@link DefaultBeanIntrospector} object. So best results are achieved by
 * adding this instance as custom {@code BeanIntrospector} after the
 * {@code DefaultBeanIntrospector} object. Then default introspection finds
 * read-only properties because it does not detect the write methods with a
 * non-<b>void</b> return type. {@code FluentPropertyBeanIntrospector}
 * completes the descriptors for these properties by setting the correct write
 * method.
 * </p>
 *
 * @since 1.9
 */
public class FluentPropertyBeanIntrospector implements BeanIntrospector {
    /** The default prefix for write methods. */
    public static final String DEFAULT_WRITE_METHOD_PREFIX = "set";

    /** The logger. */
    private final Log log = LogFactory.getLog(getClass());

    /** The prefix of write methods to search for. */
    private final String writeMethodPrefix;

    /**
     *
     * Creates a new instance of {@code FluentPropertyBeanIntrospector} and
     * sets the default prefix for write methods.
     */
    public FluentPropertyBeanIntrospector() {
        this(DEFAULT_WRITE_METHOD_PREFIX);
    }

    /**
     *
     * Creates a new instance of {@code FluentPropertyBeanIntrospector} and
     * initializes it with the prefix for write methods used by the classes to
     * be inspected.
     *
     * @param writePrefix the prefix for write methods (must not be <b>null</b>)
     * @throws IllegalArgumentException if the prefix is <b>null</b>
     */
    public FluentPropertyBeanIntrospector(final String writePrefix) {
        if (writePrefix == null) {
            throw new IllegalArgumentException(
                    "Prefix for write methods must not be null!");
        }
        writeMethodPrefix = writePrefix;
    }

    /**
     * Creates a property descriptor for a fluent API property.
     *
     * @param m the set method for the fluent API property
     * @param propertyName the name of the corresponding property
     * @return the descriptor
     * @throws IntrospectionException if an error occurs
     */
    private PropertyDescriptor createFluentPropertyDescritor(final Method m,
            final String propertyName) throws IntrospectionException {
        return new PropertyDescriptor(propertyName(m), null, m);
    }

    /**
     * Returns the prefix for write methods this instance scans for.
     *
     * @return the prefix for write methods
     */
    public String getWriteMethodPrefix() {
        return writeMethodPrefix;
    }

    /**
     * Performs introspection. This method scans the current class's methods for
     * property write methods which have not been discovered by default
     * introspection.
     *
     * @param icontext the introspection context
     * @throws IntrospectionException if an error occurs
     */
    @Override
    public void introspect(final IntrospectionContext icontext)
            throws IntrospectionException {
        for (final Method m : icontext.getTargetClass().getMethods()) {
            if (m.getName().startsWith(getWriteMethodPrefix())) {
                final String propertyName = propertyName(m);
                final PropertyDescriptor pd = icontext
                        .getPropertyDescriptor(propertyName);
                try {
                    if (pd == null) {
                        icontext.addPropertyDescriptor(createFluentPropertyDescritor(
                                m, propertyName));
                    } else if (pd.getWriteMethod() == null) {
                        pd.setWriteMethod(m);
                    }
                } catch (final IntrospectionException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Error when creating PropertyDescriptor for " + m
                            + "! Ignoring this property.", e);
                    }
                }
            }
        }
    }

    /**
     * Derives the name of a property from the given set method.
     *
     * @param m the method
     * @return the corresponding property name
     */
    private String propertyName(final Method m) {
        final String methodName = m.getName().substring(
                getWriteMethodPrefix().length());
        return methodName.length() > 1 ? Introspector.decapitalize(methodName) : methodName
                .toLowerCase(Locale.ENGLISH);
    }
}