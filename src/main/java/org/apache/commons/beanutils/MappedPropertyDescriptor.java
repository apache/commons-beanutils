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


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * A MappedPropertyDescriptor describes one mapped property.
 * Mapped properties are multivalued properties like indexed properties
 * but that are accessed with a String key instead of an index.
 * Such property values are typically stored in a Map collection.
 * For this class to work properly, a mapped value must have
 * getter and setter methods of the form
 * <p><code>get<strong>Property</strong>(String key)</code> and
 * <p><code>set<strong>Property</strong>(String key, Object value)</code>,
 * <p>where <code><strong>Property</strong></code> must be replaced
 * by the name of the property.
 * @see java.beans.PropertyDescriptor
 *
 * @version $Id$
 */
public class MappedPropertyDescriptor extends PropertyDescriptor {
    // ----------------------------------------------------- Instance Variables

    /**
     * The underlying data type of the property we are describing.
     */
    private Reference<Class<?>> mappedPropertyTypeRef;

    /**
     * The reader method for this property (if any).
     */
    private MappedMethodReference mappedReadMethodRef;

    /**
     * The writer method for this property (if any).
     */
    private MappedMethodReference mappedWriteMethodRef;

    /**
     * The parameter types array for the reader method signature.
     */
    private static final Class<?>[] STRING_CLASS_PARAMETER = new Class[]{String.class};

    // ----------------------------------------------------------- Constructors

    /**
     * Constructs a MappedPropertyDescriptor for a property that follows
     * the standard Java convention by having getFoo and setFoo
     * accessor methods, with the addition of a String parameter (the key).
     * Thus if the argument name is "fred", it will
     * assume that the writer method is "setFred" and the reader method
     * is "getFred".  Note that the property name should start with a lower
     * case character, which will be capitalized in the method names.
     *
     * @param propertyName The programmatic name of the property.
     * @param beanClass The Class object for the target bean.  For
     *        example sun.beans.OurButton.class.
     *
     * @throws IntrospectionException if an exception occurs during
     *              introspection.
     */
    public MappedPropertyDescriptor(final String propertyName, final Class<?> beanClass)
            throws IntrospectionException {

        super(propertyName, null, null);

        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name: " +
                    propertyName + " on class: " + beanClass.getClass().getName());
        }

        setName(propertyName);
        final String base = capitalizePropertyName(propertyName);

        // Look for mapped read method and matching write method
        Method mappedReadMethod = null;
        Method mappedWriteMethod = null;
        try {
            try {
                mappedReadMethod = getMethod(beanClass, "get" + base,
                        STRING_CLASS_PARAMETER);
            } catch (final IntrospectionException e) {
                mappedReadMethod = getMethod(beanClass, "is" + base,
                        STRING_CLASS_PARAMETER);
            }
            final Class<?>[] params = { String.class, mappedReadMethod.getReturnType() };
            mappedWriteMethod = getMethod(beanClass, "set" + base, params);
        } catch (final IntrospectionException e) {
            /* Swallow IntrospectionException
             * TODO: Why?
             */
        }

        // If there's no read method, then look for just a write method
        if (mappedReadMethod == null) {
            mappedWriteMethod = getMethod(beanClass, "set" + base, 2);
        }

        if ((mappedReadMethod == null) && (mappedWriteMethod == null)) {
            throw new IntrospectionException("Property '" + propertyName +
                    "' not found on " +
                    beanClass.getName());
        }
        mappedReadMethodRef  = new MappedMethodReference(mappedReadMethod);
        mappedWriteMethodRef = new MappedMethodReference(mappedWriteMethod);

        findMappedPropertyType();
    }


    /**
     * This constructor takes the name of a mapped property, and method
     * names for reading and writing the property.
     *
     * @param propertyName The programmatic name of the property.
     * @param beanClass The Class object for the target bean.  For
     *        example sun.beans.OurButton.class.
     * @param mappedGetterName The name of the method used for
     *          reading one of the property values.  May be null if the
     *          property is write-only.
     * @param mappedSetterName The name of the method used for writing
     *          one of the property values.  May be null if the property is
     *          read-only.
     *
     * @throws IntrospectionException if an exception occurs during
     *              introspection.
     */
    public MappedPropertyDescriptor(final String propertyName, final Class<?> beanClass,
                                    final String mappedGetterName, final String mappedSetterName)
            throws IntrospectionException {

        super(propertyName, null, null);

        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name: " +
                    propertyName);
        }
        setName(propertyName);

        // search the mapped get and set methods
        Method mappedReadMethod = null;
        Method mappedWriteMethod = null;
        mappedReadMethod =
            getMethod(beanClass, mappedGetterName, STRING_CLASS_PARAMETER);

        if (mappedReadMethod != null) {
            final Class<?>[] params = { String.class, mappedReadMethod.getReturnType() };
            mappedWriteMethod =
                getMethod(beanClass, mappedSetterName, params);
        } else {
            mappedWriteMethod =
                getMethod(beanClass, mappedSetterName, 2);
        }
        mappedReadMethodRef  = new MappedMethodReference(mappedReadMethod);
        mappedWriteMethodRef = new MappedMethodReference(mappedWriteMethod);

        findMappedPropertyType();
    }

    /**
     * This constructor takes the name of a mapped property, and Method
     * objects for reading and writing the property.
     *
     * @param propertyName The programmatic name of the property.
     * @param mappedGetter The method used for reading one of
     *          the property values.  May be be null if the property
     *          is write-only.
     * @param mappedSetter The method used for writing one the
     *          property values.  May be null if the property is read-only.
     *
     * @throws IntrospectionException if an exception occurs during
     *              introspection.
     */
    public MappedPropertyDescriptor(final String propertyName,
                                    final Method mappedGetter, final Method mappedSetter)
            throws IntrospectionException {

        super(propertyName, mappedGetter, mappedSetter);

        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name: " +
                    propertyName);
        }

        setName(propertyName);
        mappedReadMethodRef  = new MappedMethodReference(mappedGetter);
        mappedWriteMethodRef = new MappedMethodReference(mappedSetter);
        findMappedPropertyType();
    }

    // -------------------------------------------------------- Public Methods

    /**
     * Gets the Class object for the property values.
     *
     * @return The Java type info for the property values.  Note that
     * the "Class" object may describe a built-in Java type such as "int".
     * The result may be "null" if this is a mapped property that
     * does not support non-keyed access.
     * <p>
     * This is the type that will be returned by the mappedReadMethod.
     */
    public Class<?> getMappedPropertyType() {
        return mappedPropertyTypeRef.get();
    }

    /**
     * Gets the method that should be used to read one of the property value.
     *
     * @return The method that should be used to read the property value.
     * May return null if the property can't be read.
     */
    public Method getMappedReadMethod() {
        return mappedReadMethodRef.get();
    }

    /**
     * Sets the method that should be used to read one of the property value.
     *
     * @param mappedGetter The mapped getter method.
     * @throws IntrospectionException If an error occurs finding the
     * mapped property
     */
    public void setMappedReadMethod(final Method mappedGetter)
            throws IntrospectionException {
        mappedReadMethodRef = new MappedMethodReference(mappedGetter);
        findMappedPropertyType();
    }

    /**
     * Gets the method that should be used to write one of the property value.
     *
     * @return The method that should be used to write one of the property value.
     * May return null if the property can't be written.
     */
    public Method getMappedWriteMethod() {
        return mappedWriteMethodRef.get();
    }

    /**
     * Sets the method that should be used to write the property value.
     *
     * @param mappedSetter The mapped setter method.
     * @throws IntrospectionException If an error occurs finding the
     * mapped property
     */
    public void setMappedWriteMethod(final Method mappedSetter)
            throws IntrospectionException {
        mappedWriteMethodRef = new MappedMethodReference(mappedSetter);
        findMappedPropertyType();
    }

    // ------------------------------------------------------- Private Methods

    /**
     * Introspect our bean class to identify the corresponding getter
     * and setter methods.
     */
    private void findMappedPropertyType() throws IntrospectionException {
        try {
            final Method mappedReadMethod  = getMappedReadMethod();
            final Method mappedWriteMethod = getMappedWriteMethod();
            Class<?> mappedPropertyType = null;
            if (mappedReadMethod != null) {
                if (mappedReadMethod.getParameterTypes().length != 1) {
                    throw new IntrospectionException
                            ("bad mapped read method arg count");
                }
                mappedPropertyType = mappedReadMethod.getReturnType();
                if (mappedPropertyType == Void.TYPE) {
                    throw new IntrospectionException
                            ("mapped read method " +
                            mappedReadMethod.getName() + " returns void");
                }
            }

            if (mappedWriteMethod != null) {
                final Class<?>[] params = mappedWriteMethod.getParameterTypes();
                if (params.length != 2) {
                    throw new IntrospectionException
                            ("bad mapped write method arg count");
                }
                if (mappedPropertyType != null &&
                        mappedPropertyType != params[1]) {
                    throw new IntrospectionException
                            ("type mismatch between mapped read and write methods");
                }
                mappedPropertyType = params[1];
            }
            mappedPropertyTypeRef = new SoftReference<Class<?>>(mappedPropertyType);
        } catch (final IntrospectionException ex) {
            throw ex;
        }
    }


    /**
     * Return a capitalized version of the specified property name.
     *
     * @param s The property name
     */
    private static String capitalizePropertyName(final String s) {
        if (s.length() == 0) {
            return s;
        }

        final char[] chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * Find a method on a class with a specified number of parameters.
     */
    private static Method internalGetMethod(final Class<?> initial, final String methodName,
                                            final int parameterCount) {
        // For overridden methods we need to find the most derived version.
        // So we start with the given class and walk up the superclass chain.
        for (Class<?> clazz = initial; clazz != null; clazz = clazz.getSuperclass()) {
            final Method[] methods = clazz.getDeclaredMethods();
            for (final Method method : methods) {
                if (method == null) {
                    continue;
                }
                // skip static methods.
                final int mods = method.getModifiers();
                if (!Modifier.isPublic(mods) ||
                    Modifier.isStatic(mods)) {
                    continue;
                }
                if (method.getName().equals(methodName) &&
                        method.getParameterTypes().length == parameterCount) {
                    return method;
                }
            }
        }

        // Now check any inherited interfaces.  This is necessary both when
        // the argument class is itself an interface, and when the argument
        // class is an abstract class.
        final Class<?>[] interfaces = initial.getInterfaces();
        for (Class<?> interface1 : interfaces) {
            final Method method = internalGetMethod(interface1, methodName, parameterCount);
            if (method != null) {
                return method;
            }
        }

        return null;
    }

    /**
     * Find a method on a class with a specified number of parameters.
     */
    private static Method getMethod(final Class<?> clazz, final String methodName, final int parameterCount)
            throws IntrospectionException {
        if (methodName == null) {
            return null;
        }

        final Method method = internalGetMethod(clazz, methodName, parameterCount);
        if (method != null) {
            return method;
        }

        // No Method found
        throw new IntrospectionException("No method \"" + methodName +
                "\" with " + parameterCount + " parameter(s)");
    }

    /**
     * Find a method on a class with a specified parameter list.
     */
    private static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>[] parameterTypes)
                                           throws IntrospectionException {
        if (methodName == null) {
            return null;
        }

        final Method method = MethodUtils.getMatchingAccessibleMethod(clazz, methodName, parameterTypes);
        if (method != null) {
            return method;
        }

        final int parameterCount = (parameterTypes == null) ? 0 : parameterTypes.length;

        // No Method found
        throw new IntrospectionException("No method \"" + methodName +
                "\" with " + parameterCount + " parameter(s) of matching types.");
    }

    /**
     * Holds a {@link Method} in a {@link SoftReference} so that it
     * it doesn't prevent any ClassLoader being garbage collected, but
     * tries to re-create the method if the method reference has been
     * released.
     *
     * See http://issues.apache.org/jira/browse/BEANUTILS-291
     */
    private static class MappedMethodReference {
        private String className;
        private String methodName;
        private Reference<Method> methodRef;
        private Reference<Class<?>> classRef;
        private Reference<Class<?>> writeParamTypeRef0;
        private Reference<Class<?>> writeParamTypeRef1;
        private String[] writeParamClassNames;
        MappedMethodReference(final Method m) {
            if (m != null) {
                className = m.getDeclaringClass().getName();
                methodName = m.getName();
                methodRef = new SoftReference<Method>(m);
                classRef = new WeakReference<Class<?>>(m.getDeclaringClass());
                final Class<?>[] types = m.getParameterTypes();
                if (types.length == 2) {
                    writeParamTypeRef0 = new WeakReference<Class<?>>(types[0]);
                    writeParamTypeRef1 = new WeakReference<Class<?>>(types[1]);
                    writeParamClassNames = new String[2];
                    writeParamClassNames[0] = types[0].getName();
                    writeParamClassNames[1] = types[1].getName();
                }
            }
        }
        private Method get() {
            if (methodRef == null) {
                return null;
            }
            Method m = methodRef.get();
            if (m == null) {
                Class<?> clazz = classRef.get();
                if (clazz == null) {
                    clazz = reLoadClass();
                    if (clazz != null) {
                        classRef = new WeakReference<Class<?>>(clazz);
                    }
                }
                if (clazz == null) {
                    throw new RuntimeException("Method " + methodName + " for " +
                            className + " could not be reconstructed - class reference has gone");
                }
                Class<?>[] paramTypes = null;
                if (writeParamClassNames != null) {
                    paramTypes = new Class[2];
                    paramTypes[0] = writeParamTypeRef0.get();
                    if (paramTypes[0] == null) {
                        paramTypes[0] = reLoadClass(writeParamClassNames[0]);
                        if (paramTypes[0] != null) {
                            writeParamTypeRef0 = new WeakReference<Class<?>>(paramTypes[0]);
                        }
                    }
                    paramTypes[1] = writeParamTypeRef1.get();
                    if (paramTypes[1] == null) {
                        paramTypes[1] = reLoadClass(writeParamClassNames[1]);
                        if (paramTypes[1] != null) {
                            writeParamTypeRef1 = new WeakReference<Class<?>>(paramTypes[1]);
                        }
                    }
                } else {
                    paramTypes = STRING_CLASS_PARAMETER;
                }
                try {
                    m = clazz.getMethod(methodName, paramTypes);
                    // Un-comment following line for testing
                    // System.out.println("Recreated Method " + methodName + " for " + className);
                } catch (final NoSuchMethodException e) {
                    throw new RuntimeException("Method " + methodName + " for " +
                            className + " could not be reconstructed - method not found");
                }
                methodRef = new SoftReference<Method>(m);
            }
            return m;
        }

        /**
         * Try to re-load the class
         */
        private Class<?> reLoadClass() {
            return reLoadClass(className);
        }

        /**
         * Try to re-load the class
         */
        private Class<?> reLoadClass(final String name) {

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            // Try the context class loader
            if (classLoader != null) {
                try {
                    return classLoader.loadClass(name);
                } catch (final ClassNotFoundException e) {
                    // ignore
                }
            }

            // Try this class's class loader
            classLoader = MappedPropertyDescriptor.class.getClassLoader();
            try {
                return classLoader.loadClass(name);
            } catch (final ClassNotFoundException e) {
                return null;
            }
        }
    }
}
