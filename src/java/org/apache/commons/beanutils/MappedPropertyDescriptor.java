/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.security.AccessController;
import java.security.PrivilegedAction;


/**
 * A MappedPropertyDescriptor describes one mapped property.
 * Mapped properties are multivalued properties like indexed properties
 * but that are accessed with a String key instead of an index.
 * Such property values are typically stored in a Map collection.
 * For this class to work properly, a mapped value must have
 * getter and setter methods of the form
 * <p><code>get<strong>Property</strong>(String key)<code> and
 * <p><code>set&ltProperty&gt(String key, Object value)<code>,
 * <p>where <code><strong>Property</strong></code> must be replaced
 * by the name of the property.
 * @see java.beans.PropertyDescriptor
 *
 * @author Rey François
 * @author Gregor Raýman
 * @version $Revision: 1.18.2.1 $ $Date: 2004/07/27 21:44:26 $
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
    private static final Class[] stringClassArray = new Class[]{String.class};

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
     *		example sun.beans.OurButton.class.
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
            mappedReadMethod = findMethod(beanClass, "get" + base, 1,
                    stringClassArray);
            Class params[] = { String.class, mappedReadMethod.getReturnType() };
            mappedWriteMethod = findMethod(beanClass, "set" + base, 2,  params);
        } catch (IntrospectionException e) {
            ;
        }
        
        // If there's no read method, then look for just a write method 
        if (mappedReadMethod == null) {
            mappedWriteMethod = findMethod(beanClass, "set" + base, 2);
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
     *		example sun.beans.OurButton.class.
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
            findMethod(beanClass, mappedGetterName, 1, stringClassArray);

        if (mappedReadMethod != null) {
            Class params[] = { String.class, mappedReadMethod.getReturnType() };
            mappedWriteMethod = 
                findMethod(beanClass, mappedSetterName, 2, params);
        } else {
            mappedWriteMethod =
                findMethod(beanClass, mappedSetterName, 2);
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
     * @param mappedGetter The new getter method.
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
     * @param mappedSetter The new setter method.
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

    //======================================================================
    // Package private support methods (copied from java.beans.Introspector).
    //======================================================================

    // Cache of Class.getDeclaredMethods:
    private static java.util.Hashtable 
        declaredMethodCache = new java.util.Hashtable();

    /*
     * Internal method to return *public* methods within a class.
     */
    private static synchronized Method[] getPublicDeclaredMethods(Class clz) {
        // Looking up Class.getDeclaredMethods is relatively expensive,
        // so we cache the results.
        final Class fclz = clz;
        Method[] result = (Method[]) declaredMethodCache.get(fclz);
        if (result != null) {
            return result;
        }

        // We have to raise privilege for getDeclaredMethods
        result = (Method[])
                AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        try{
                        
                            return fclz.getDeclaredMethods();
                            
                        } catch (SecurityException ex) {
                            // this means we're in a limited security environment
                            // so let's try going through the public methods
                            // and null those those that are not from the declaring 
                            // class
                            Method[] methods = fclz.getMethods();
                            for (int i = 0, size = methods.length; i < size; i++) {
                                Method method =  methods[i];
                                if (!(fclz.equals(method.getDeclaringClass()))) {
                                    methods[i] = null;
                                }
                            }
                            return methods;
                        }
                    }
                });

        // Null out any non-public methods.
        for (int i = 0; i < result.length; i++) {
            Method method = result[i];
            if (method != null) {
                int mods = method.getModifiers();
                if (!Modifier.isPublic(mods)) {
                    result[i] = null;
                }
            }
        }

        // Add it to the cache.
        declaredMethodCache.put(clz, result);
        return result;
    }

    /**
     * Internal support for finding a target methodName on a given class.
     */
    private static Method internalFindMethod(Class start, String methodName,
                                             int argCount) {
        // For overridden methods we need to find the most derived version.
        // So we start with the given class and walk up the superclass chain.
        for (Class cl = start; cl != null; cl = cl.getSuperclass()) {
            Method methods[] = getPublicDeclaredMethods(cl);
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method == null) {
                    continue;
                }
                // skip static methods.
                int mods = method.getModifiers();
                if (Modifier.isStatic(mods)) {
                    continue;
                }
                if (method.getName().equals(methodName) &&
                        method.getParameterTypes().length == argCount) {
                    return method;
                }
            }
        }

        // Now check any inherited interfaces.  This is necessary both when
        // the argument class is itself an interface, and when the argument
        // class is an abstract class.
        Class ifcs[] = start.getInterfaces();
        for (int i = 0; i < ifcs.length; i++) {
            Method m = internalFindMethod(ifcs[i], methodName, argCount);
            if (m != null) {
                return m;
            }
        }

        return null;
    }

    /**
     * Internal support for finding a target methodName with a given
     * parameter list on a given class.
     */
    private static Method internalFindMethod(Class start, String methodName,
                                             int argCount, Class args[]) {
        // For overriden methods we need to find the most derived version.
        // So we start with the given class and walk up the superclass chain.
        for (Class cl = start; cl != null; cl = cl.getSuperclass()) {
            Method methods[] = getPublicDeclaredMethods(cl);
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method == null) {
                    continue;
                }
                // skip static methods.
                int mods = method.getModifiers();
                if (Modifier.isStatic(mods)) {
                    continue;
                }
                // make sure method signature matches.
                Class params[] = method.getParameterTypes();
                if (method.getName().equals(methodName) &&
                        params.length == argCount) {
                    boolean different = false;
                    if (argCount > 0) {
                        for (int j = 0; j < argCount; j++) {
                            if (params[j] != args[j]) {
                                different = true;
                                continue;
                            }
                        }
                        if (different) {
                            continue;
                        }
                    }
                    return method;
                }
            }
        }

        // Now check any inherited interfaces.  This is necessary both when
        // the argument class is itself an interface, and when the argument
        // class is an abstract class.
        Class ifcs[] = start.getInterfaces();
        for (int i = 0; i < ifcs.length; i++) {
            Method m = internalFindMethod(ifcs[i], methodName, argCount);
            if (m != null) {
                return m;
            }
        }
        
        return null;
    }

    /**
     * Find a target methodName on a given class.
     */
    static Method findMethod(Class cls, String methodName, int argCount)
            throws IntrospectionException {
        if (methodName == null) {
            return null;
        }

        Method m = internalFindMethod(cls, methodName, argCount);
        if (m != null) {
            return m;
        }

        // We failed to find a suitable method
        throw new IntrospectionException("No method \"" + methodName +
                "\" with " + argCount + " arg(s)");
    }

    /**
     * Find a target methodName with specific parameter list on a given class.
     */
    static Method findMethod(Class cls, String methodName, int argCount,
                             Class args[]) throws IntrospectionException {
        if (methodName == null) {
            return null;
        }

        Method m = internalFindMethod(cls, methodName, argCount, args);
        if (m != null) {
            return m;
        }

        // We failed to find a suitable method
        throw new IntrospectionException("No method \"" + methodName +
                "\" with " + argCount + " arg(s) of matching types.");
    }

    /**
     * Return true if class a is either equivalent to class b, or
     * if class a is a subclass of class b, ie if a either "extends"
     * or "implements" b.
     * Note tht either or both "Class" objects may represent interfaces.
     */
    static boolean isSubclass(Class a, Class b) {
        // We rely on the fact that for any given java class or
        // primtitive type there is a unqiue Class object, so
        // we can use object equivalence in the comparisons.
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        for (Class x = a; x != null; x = x.getSuperclass()) {
            if (x == b) {
                return true;
            }

            if (b.isInterface()) {
                Class interfaces[] = x.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    if (isSubclass(interfaces[i], b)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    /**
     * Return true iff the given method throws the given exception.
     */

    private boolean throwsException(Method method, Class exception) {

        Class exs[] = method.getExceptionTypes();
        for (int i = 0; i < exs.length; i++) {
            if (exs[i] == exception) {
                return true;
            }
        }

        return false;
    }
}
