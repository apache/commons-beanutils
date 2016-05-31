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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * <p> Utility reflection methods focussed on constructors, modelled after {@link MethodUtils}. </p>
 *
 * <h3>Known Limitations</h3>
 * <h4>Accessing Public Constructors In A Default Access Superclass</h4>
 * <p>There is an issue when invoking public constructors contained in a default access superclass.
 * Reflection locates these constructors fine and correctly assigns them as public.
 * However, an <code>IllegalAccessException</code> is thrown if the constructors is invoked.</p>
 *
 * <p><code>ConstructorUtils</code> contains a workaround for this situation.
 * It will attempt to call <code>setAccessible</code> on this constructor.
 * If this call succeeds, then the method can be invoked as normal.
 * This call will only succeed when the application has sufficient security privilages.
 * If this call fails then a warning will be logged and the method may fail.</p>
 *
 * @version $Id$
 */
public class ConstructorUtils {

    // --------------------------------------------------------- Private Members
    /** An empty class array */
    private static final Class<?>[] EMPTY_CLASS_PARAMETERS = new Class<?>[0];
    /** An empty object array */
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    // --------------------------------------------------------- Public Methods

    /**
     * <p>Convenience method returning new instance of <code>klazz</code> using a single argument constructor.
     * The formal parameter type is inferred from the actual values of <code>arg</code>.
     * See {@link #invokeExactConstructor(Class, Object[], Class[])} for more details.</p>
     *
     * <p>The signatures should be assignment compatible.</p>
     *
     * @param <T> the type of the object to be constructed
     * @param klass the class to be constructed.
     * @param arg the actual argument. May be null (this will result in calling the default constructor).
     * @return new instance of <code>klazz</code>
     *
     * @throws NoSuchMethodException If the constructor cannot be found
     * @throws IllegalAccessException If an error occurs accessing the constructor
     * @throws InvocationTargetException If an error occurs invoking the constructor
     * @throws InstantiationException If an error occurs instantiating the class
     *
     * @see #invokeConstructor(java.lang.Class, java.lang.Object[], java.lang.Class[])
     */
    public static <T> T invokeConstructor(final Class<T> klass, final Object arg)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        final Object[] args = toArray(arg);
        return invokeConstructor(klass, args);
    }

    /**
     * <p>Returns new instance of <code>klazz</code> created using the actual arguments <code>args</code>.
     * The formal parameter types are inferred from the actual values of <code>args</code>.
     * See {@link #invokeExactConstructor(Class, Object[], Class[])} for more details.</p>
     *
     * <p>The signatures should be assignment compatible.</p>
     *
     * @param <T> the type of the object to be constructed
     * @param klass the class to be constructed.
     * @param args actual argument array. May be null (this will result in calling the default constructor).
     * @return new instance of <code>klazz</code>
     *
     * @throws NoSuchMethodException If the constructor cannot be found
     * @throws IllegalAccessException If an error occurs accessing the constructor
     * @throws InvocationTargetException If an error occurs invoking the constructor
     * @throws InstantiationException If an error occurs instantiating the class
     *
     * @see #invokeConstructor(java.lang.Class, java.lang.Object[], java.lang.Class[])
     */
    public static <T> T invokeConstructor(final Class<T> klass, Object[] args)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        if (null == args) {
            args = EMPTY_OBJECT_ARRAY;
        }
        final int arguments = args.length;
        final Class<?> parameterTypes[] = new Class<?>[arguments];
        for (int i = 0; i < arguments; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        return invokeConstructor(klass, args, parameterTypes);
    }

    /**
     * <p>Returns new instance of <code>klazz</code> created using constructor
     * with signature <code>parameterTypes</code> and actual arguments <code>args</code>.</p>
     *
     * <p>The signatures should be assignment compatible.</p>
     *
     * @param <T> the type of the object to be constructed
     * @param klass the class to be constructed.
     * @param args actual argument array. May be null (this will result in calling the default constructor).
     * @param parameterTypes parameter types array
     * @return new instance of <code>klazz</code>
     *
     * @throws NoSuchMethodException if matching constructor cannot be found
     * @throws IllegalAccessException thrown on the constructor's invocation
     * @throws InvocationTargetException thrown on the constructor's invocation
     * @throws InstantiationException thrown on the constructor's invocation
     * @see Constructor#newInstance
     */
    public static <T> T invokeConstructor(
        final Class<T> klass,
        Object[] args,
        Class<?>[] parameterTypes)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        if (parameterTypes == null) {
            parameterTypes = EMPTY_CLASS_PARAMETERS;
        }
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }

        final Constructor<T> ctor =
            getMatchingAccessibleConstructor(klass, parameterTypes);
        if (null == ctor) {
            throw new NoSuchMethodException(
                "No such accessible constructor on object: " + klass.getName());
        }
        return ctor.newInstance(args);
    }


    /**
     * <p>Convenience method returning new instance of <code>klazz</code> using a single argument constructor.
     * The formal parameter type is inferred from the actual values of <code>arg</code>.
     * See {@link #invokeExactConstructor(Class, Object[], Class[])} for more details.</p>
     *
     * <p>The signatures should match exactly.</p>
     *
     * @param <T> the type of the object to be constructed
     * @param klass the class to be constructed.
     * @param arg the actual argument. May be null (this will result in calling the default constructor).
     * @return new instance of <code>klazz</code>
     *
     * @throws NoSuchMethodException If the constructor cannot be found
     * @throws IllegalAccessException If an error occurs accessing the constructor
     * @throws InvocationTargetException If an error occurs invoking the constructor
     * @throws InstantiationException If an error occurs instantiating the class
     *
     * @see #invokeExactConstructor(java.lang.Class, java.lang.Object[], java.lang.Class[])
     */
    public static <T> T invokeExactConstructor(final Class<T> klass, final Object arg)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        final Object[] args = toArray(arg);
        return invokeExactConstructor(klass, args);
    }

    /**
     * <p>Returns new instance of <code>klazz</code> created using the actual arguments <code>args</code>.
     * The formal parameter types are inferred from the actual values of <code>args</code>.
     * See {@link #invokeExactConstructor(Class, Object[], Class[])} for more details.</p>
     *
     * <p>The signatures should match exactly.</p>
     *
     * @param <T> the type of the object to be constructed
     * @param klass the class to be constructed.
     * @param args actual argument array. May be null (this will result in calling the default constructor).
     * @return new instance of <code>klazz</code>
     *
     * @throws NoSuchMethodException If the constructor cannot be found
     * @throws IllegalAccessException If an error occurs accessing the constructor
     * @throws InvocationTargetException If an error occurs invoking the constructor
     * @throws InstantiationException If an error occurs instantiating the class
     *
     * @see #invokeExactConstructor(java.lang.Class, java.lang.Object[], java.lang.Class[])
     */
    public static <T> T invokeExactConstructor(final Class<T> klass, Object[] args)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        if (null == args) {
            args = EMPTY_OBJECT_ARRAY;
        }
        final int arguments = args.length;
        final Class<?> parameterTypes[] = new Class[arguments];
        for (int i = 0; i < arguments; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        return invokeExactConstructor(klass, args, parameterTypes);
    }

    /**
     * <p>Returns new instance of <code>klazz</code> created using constructor
     * with signature <code>parameterTypes</code> and actual arguments
     * <code>args</code>.</p>
     *
     * <p>The signatures should match exactly.</p>
     *
     * @param <T> the type of the object to be constructed
     * @param klass the class to be constructed.
     * @param args actual argument array. May be null (this will result in calling the default constructor).
     * @param parameterTypes parameter types array
     * @return new instance of <code>klazz</code>
     *
     * @throws NoSuchMethodException if matching constructor cannot be found
     * @throws IllegalAccessException thrown on the constructor's invocation
     * @throws InvocationTargetException thrown on the constructor's invocation
     * @throws InstantiationException thrown on the constructor's invocation
     * @see Constructor#newInstance
     */
    public static <T> T invokeExactConstructor(
        final Class<T> klass,
        Object[] args,
        Class<?>[] parameterTypes)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }

        if (parameterTypes == null) {
            parameterTypes = EMPTY_CLASS_PARAMETERS;
        }

        final Constructor<T> ctor = getAccessibleConstructor(klass, parameterTypes);
        if (null == ctor) {
            throw new NoSuchMethodException(
                "No such accessible constructor on object: " + klass.getName());
        }
        return ctor.newInstance(args);
    }

    /**
     * Returns a constructor with single argument.
     * @param <T> the type of the constructor
     * @param klass the class to be constructed
     * @param parameterType The constructor parameter type
     * @return null if matching accessible constructor can not be found.
     * @see Class#getConstructor
     * @see #getAccessibleConstructor(java.lang.reflect.Constructor)
     */
    public static <T> Constructor<T> getAccessibleConstructor(
        final Class<T> klass,
        final Class<?> parameterType) {

        final Class<?>[] parameterTypes = { parameterType };
        return getAccessibleConstructor(klass, parameterTypes);
    }

    /**
     * Returns a constructor given a class and signature.
     * @param <T> the type to be constructed
     * @param klass the class to be constructed
     * @param parameterTypes the parameter array
     * @return null if matching accessible constructor can not be found
     * @see Class#getConstructor
     * @see #getAccessibleConstructor(java.lang.reflect.Constructor)
     */
    public static <T> Constructor<T> getAccessibleConstructor(
        final Class<T> klass,
        final Class<?>[] parameterTypes) {

        try {
            return getAccessibleConstructor(
                klass.getConstructor(parameterTypes));
        } catch (final NoSuchMethodException e) {
            return (null);
        }
    }

    /**
     * Returns accessible version of the given constructor.
     * @param <T> the type of the constructor
     * @param ctor prototype constructor object.
     * @return <code>null</code> if accessible constructor can not be found.
     * @see java.lang.SecurityManager
     */
    public static <T> Constructor<T> getAccessibleConstructor(final Constructor<T> ctor) {

        // Make sure we have a method to check
        if (ctor == null) {
            return (null);
        }

        // If the requested method is not public we cannot call it
        if (!Modifier.isPublic(ctor.getModifiers())) {
            return (null);
        }

        // If the declaring class is public, we are done
        final Class<T> clazz = ctor.getDeclaringClass();
        if (Modifier.isPublic(clazz.getModifiers())) {
            return (ctor);
        }

        // what else can we do?
        return null;
    }

    private static Object[] toArray(final Object arg) {
        Object[] args = null;
        if (arg != null) {
            args = new Object[] { arg };
        }
        return args;
    }

    // -------------------------------------------------------- Private Methods
    /**
     * <p>Find an accessible constructor with compatible parameters.
     * Compatible parameters mean that every method parameter is assignable from
     * the given parameters. In other words, it finds constructor that will take
     * the parameters given.</p>
     *
     * <p>First it checks if there is constructor matching the exact signature.
     * If no such, all the constructors of the class are tested if their signatures
     * are assignment compatible with the parameter types.
     * The first matching constructor is returned.</p>
     *
     * @param <T> the type of the class to be inspected
     * @param clazz find constructor for this class
     * @param parameterTypes find method with compatible parameters
     * @return a valid Constructor object. If there's no matching constructor, returns <code>null</code>.
     */
    private static <T> Constructor<T> getMatchingAccessibleConstructor(
        final Class<T> clazz,
        final Class<?>[] parameterTypes) {
        // see if we can find the method directly
        // most of the time this works and it's much faster
        try {
            final Constructor<T> ctor = clazz.getConstructor(parameterTypes);
            try {
                //
                // XXX Default access superclass workaround
                //
                // When a public class has a default access superclass
                // with public methods, these methods are accessible.
                // Calling them from compiled code works fine.
                //
                // Unfortunately, using reflection to invoke these methods
                // seems to (wrongly) to prevent access even when the method
                // modifer is public.
                //
                // The following workaround solves the problem but will only
                // work from sufficiently privilages code.
                //
                // Better workarounds would be greatfully accepted.
                //
                ctor.setAccessible(true);
            } catch (final SecurityException se) {
                /* SWALLOW, if workaround fails don't fret. */
            }
            return ctor;

        } catch (final NoSuchMethodException e) { /* SWALLOW */
        }

        // search through all methods
        final int paramSize = parameterTypes.length;
        final Constructor<?>[] ctors = clazz.getConstructors();
        for (Constructor<?> ctor2 : ctors) {
            // compare parameters
            final Class<?>[] ctorParams = ctor2.getParameterTypes();
            final int ctorParamSize = ctorParams.length;
            if (ctorParamSize == paramSize) {
                boolean match = true;
                for (int n = 0; n < ctorParamSize; n++) {
                    if (!MethodUtils
                        .isAssignmentCompatible(
                            ctorParams[n],
                            parameterTypes[n])) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    // get accessible version of method
                    final Constructor<?> ctor = getAccessibleConstructor(ctor2);
                    if (ctor != null) {
                        try {
                            ctor.setAccessible(true);
                        } catch (final SecurityException se) {
                            /* Swallow SecurityException
                             * TODO: Why?
                             */
                        }
                        @SuppressWarnings("unchecked")
                        final
                        // Class.getConstructors() actually returns constructors
                        // of type T, so it is safe to cast.
                        Constructor<T> typedCtor = (Constructor<T>) ctor;
                        return typedCtor;
                    }
                }
            }
        }

        return null;
    }

}
