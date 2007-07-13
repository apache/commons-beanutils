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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * A MappedPropertyDescriptor describes one mapped property.
 * Mapped properties are multivalued properties like indexed properties
 * but that are accessed with a String key instead of an index.
 * Such property values are typically stored in a Map collection.
 * For this class to work properly, a mapped value must have
 * getter and setter methods of the form
 * <p><code>get<strong>Property</strong>(String key)<code> and
 * <p><code>set<strong>Property</strong>(String key, Object value)<code>,
 * <p>where <code><strong>Property</strong></code> must be replaced
 * by the name of the property.
 * @see java.beans.PropertyDescriptor
 *
 * @author Rey Francois
 * @author Gregor Rayman
 * @version $Revision$ $Date$
 */


public class MappedPropertyDescriptor extends PropertyDescriptor {
    // ----------------------------------------------------- Instance Variables

    /**
     * The underlying data type of the property we are describing.
     */
    private Class mappedPropertyType;

    /**
     * The reader method for this property (if any).
     */
    private Method mappedReadMethod;

    /**
     * The writer method for this property (if any).
     */
    private Method mappedWriteMethod;

    /**
     * The parameter types array for the reader method signature.
     */
    private static final Class[] STRING_CLASS_PARAMETER = new Class[]{String.class};

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
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public MappedPropertyDescriptor(String propertyName, Class beanClass)
            throws IntrospectionException {

        super(propertyName, null, null);
        
        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name: " +
                    propertyName + " on class: " + beanClass.getClass().getName());
        }

        setName(propertyName);
        String base = capitalizePropertyName(propertyName);
        
        // Look for mapped read method and matching write method
        try {
            try {
                mappedReadMethod = getMethod(beanClass, "get" + base,
                        STRING_CLASS_PARAMETER);
            } catch (IntrospectionException e) {
                mappedReadMethod = getMethod(beanClass, "is" + base,
                        STRING_CLASS_PARAMETER);
            }
            Class params[] = { String.class, mappedReadMethod.getReturnType() };
            mappedWriteMethod = getMethod(beanClass, "set" + base, params);
        } catch (IntrospectionException e) {
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
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public MappedPropertyDescriptor(String propertyName, Class beanClass,
                                    String mappedGetterName, String mappedSetterName)
            throws IntrospectionException {

        super(propertyName, null, null);

        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name: " +
                    propertyName);
        }
        setName(propertyName);

        // search the mapped get and set methods
        mappedReadMethod =
            getMethod(beanClass, mappedGetterName, STRING_CLASS_PARAMETER);

        if (mappedReadMethod != null) {
            Class params[] = { String.class, mappedReadMethod.getReturnType() };
            mappedWriteMethod = 
                getMethod(beanClass, mappedSetterName, params);
        } else {
            mappedWriteMethod =
                getMethod(beanClass, mappedSetterName, 2);
        }

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
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public MappedPropertyDescriptor(String propertyName,
                                    Method mappedGetter, Method mappedSetter)
            throws IntrospectionException {

        super(propertyName, mappedGetter, mappedSetter);

        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name: " +
                    propertyName);
        }

        setName(propertyName);
        mappedReadMethod = mappedGetter;
        mappedWriteMethod = mappedSetter;
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
    public Class getMappedPropertyType() {
        return mappedPropertyType;
    }

    /**
     * Gets the method that should be used to read one of the property value.
     *
     * @return The method that should be used to read the property value.
     * May return null if the property can't be read.
     */
    public Method getMappedReadMethod() {
        return mappedReadMethod;
    }

    /**
     * Sets the method that should be used to read one of the property value.
     *
     * @param mappedGetter The mapped getter method.
     * @throws IntrospectionException If an error occurs finding the
     * mapped property
     */
    public void setMappedReadMethod(Method mappedGetter)
            throws IntrospectionException {
        mappedReadMethod = mappedGetter;
        findMappedPropertyType();
    }

    /**
     * Gets the method that should be used to write one of the property value.
     *
     * @return The method that should be used to write one of the property value.
     * May return null if the property can't be written.
     */
    public Method getMappedWriteMethod() {
        return mappedWriteMethod;
    }

    /**
     * Sets the method that should be used to write the property value.
     *
     * @param mappedSetter The mapped setter method.
     * @throws IntrospectionException If an error occurs finding the
     * mapped property
     */
    public void setMappedWriteMethod(Method mappedSetter)
            throws IntrospectionException {
        mappedWriteMethod = mappedSetter;
        findMappedPropertyType();
    }

    // ------------------------------------------------------- Private Methods

    /**
     * Introspect our bean class to identify the corresponding getter
     * and setter methods.
     */
    private void findMappedPropertyType() throws IntrospectionException {
        try {
            mappedPropertyType = null;
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
                Class params[] = mappedWriteMethod.getParameterTypes();
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
        } catch (IntrospectionException ex) {
            throw ex;
        }
    }


    /**
     * Return a capitalized version of the specified property name.
     *
     * @param s The property name
     */
    private static String capitalizePropertyName(String s) {
        if (s.length() == 0) {
            return s;
        }

        char chars[] = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * Find a method on a class with a specified number of parameters.
     */
    private static Method internalGetMethod(Class initial, String methodName,
                                            int parameterCount) {
        // For overridden methods we need to find the most derived version.
        // So we start with the given class and walk up the superclass chain.
        for (Class clazz = initial; clazz != null; clazz = clazz.getSuperclass()) {
            Method methods[] = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method == null) {
                    continue;
                }
                // skip static methods.
                int mods = method.getModifiers();
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
        Class[] interfaces = initial.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Method method = internalGetMethod(interfaces[i], methodName, parameterCount);
            if (method != null) {
                return method;
            }
        }

        return null;
    }

    /**
     * Find a method on a class with a specified number of parameters.
     */
    private static Method getMethod(Class clazz, String methodName, int parameterCount)
            throws IntrospectionException {
        if (methodName == null) {
            return null;
        }

        Method method = internalGetMethod(clazz, methodName, parameterCount);
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
    private static Method getMethod(Class clazz, String methodName, Class[] parameterTypes) 
                                           throws IntrospectionException {
        if (methodName == null) {
            return null;
        }

        Method method = MethodUtils.getMatchingAccessibleMethod(clazz, methodName, parameterTypes);
        if (method != null) {
            return method;
        }

        int parameterCount = (parameterTypes == null) ? 0 : parameterTypes.length;

        // No Method found
        throw new IntrospectionException("No method \"" + methodName +
                "\" with " + parameterCount + " parameter(s) of matching types.");
    }

}
