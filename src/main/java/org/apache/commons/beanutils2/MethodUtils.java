/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils2;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * Utility reflection methods focused on methods in general rather than properties in particular.
 * </p>
 *
 * <h2>Known Limitations</h2>
 * <h3>Accessing Public Methods In A Default Access Superclass</h3>
 * <p>
 * There is an issue when invoking public methods contained in a default access superclass. Reflection locates these methods fine and correctly assigns them as
 * public. However, an {@code IllegalAccessException} is thrown if the method is invoked.
 * </p>
 *
 * <p>
 * {@code MethodUtils} contains a workaround for this situation. It will attempt to call {@code setAccessible} on this method. If this call succeeds, then the
 * method can be invoked as normal. This call will only succeed when the application has sufficient security privileges. If this call fails then a warning will
 * be logged and the method may fail.
 * </p>
 */
public final class MethodUtils {

    /**
     * Represents the key to looking up a Method by reflection.
     */
    private static final class MethodDescriptor {
        private final Class<?> cls;
        private final String methodName;
        private final Class<?>[] paramTypes;
        private final boolean exact;
        private final int hashCode;

        /**
         * The sole constructor.
         *
         * @param cls        the class to reflect, must not be null
         * @param methodName the method name to obtain
         * @param paramTypes the array of classes representing the parameter types
         * @param exact      whether the match has to be exact.
         */
        public MethodDescriptor(final Class<?> cls, final String methodName, final Class<?>[] paramTypes, final boolean exact) {
            this.cls = Objects.requireNonNull(cls, "cls");
            this.methodName = Objects.requireNonNull(methodName, "methodName");
            this.paramTypes = paramTypes != null ? paramTypes : BeanUtils.EMPTY_CLASS_ARRAY;
            this.exact = exact;
            this.hashCode = methodName.length();
        }

        /**
         * Checks for equality.
         *
         * @param obj object to be tested for equality
         * @return true, if the object describes the same Method.
         */
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof MethodDescriptor)) {
                return false;
            }
            final MethodDescriptor md = (MethodDescriptor) obj;

            return exact == md.exact && methodName.equals(md.methodName) && cls.equals(md.cls) && Arrays.equals(paramTypes, md.paramTypes);
        }

        /**
         * Returns the string length of method name. I.e. if the hash codes are different, the objects are different. If the hash codes are the same, need to
         * use the equals method to determine equality.
         *
         * @return the string length of method name.
         */
        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    private static final Log LOG = LogFactory.getLog(MethodUtils.class);

    /**
     * Indicates whether methods should be cached for improved performance.
     * <p>
     * Note that when this class is deployed via a shared classloader in a container, this will affect all webapps. However making this configurable per webapp
     * would mean having a map keyed by context classloader which may introduce memory-leak problems.
     * </p>
     */
    private static boolean CACHE_METHODS = true;

    /**
     * Stores a cache of MethodDescriptor -> Method in a WeakHashMap.
     * <p>
     * The keys into this map only ever exist as temporary variables within methods of this class, and are never exposed to users of this class. This means that
     * the WeakHashMap is used only as a mechanism for limiting the size of the cache, that is, a way to tell the garbage collector that the contents of the
     * cache can be completely garbage-collected whenever it needs the memory. Whether this is a good approach to this problem is doubtful; something like the
     * commons-collections LRUMap may be more appropriate (though of course selecting an appropriate size is an issue).
     * </p>
     * <p>
     * This static variable is safe even when this code is deployed via a shared classloader because it is keyed via a MethodDescriptor object which has a Class
     * as one of its members and that member is used in the MethodDescriptor.equals method. So two components that load the same class via different class
     * loaders will generate non-equal MethodDescriptor objects and hence end up with different entries in the map.
     * </p>
     */
    private static final Map<MethodDescriptor, Reference<Method>> CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Add a method to the cache.
     *
     * @param md     The method descriptor
     * @param method The method to cache
     */
    private static void cacheMethod(final MethodDescriptor md, final Method method) {
        if (CACHE_METHODS && method != null) {
            CACHE.put(md, new WeakReference<>(method));
        }
    }

    /**
     * Clear the method cache.
     *
     * @return the number of cached methods cleared
     * @since 1.8.0
     */
    public static synchronized int clearCache() {
        final int size = CACHE.size();
        CACHE.clear();
        return size;
    }

    /**
     * Return an accessible method (that is, one that can be invoked via reflection) that implements the specified Method. If no such method can be found,
     * return {@code null}.
     *
     * @param clazz  The class of the object
     * @param method The method that we wish to call
     * @return The accessible method
     * @since 1.8.0
     */
    public static Method getAccessibleMethod(Class<?> clazz, Method method) {
        // Make sure we have a method to check
        if (method == null) {
            return null;
        }

        // If the requested method is not public we cannot call it
        if (!Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        boolean sameClass = true;
        if (clazz == null) {
            clazz = method.getDeclaringClass();
        } else {
            if (!method.getDeclaringClass().isAssignableFrom(clazz)) {
                throw new IllegalArgumentException(clazz.getName() + " is not assignable from " + method.getDeclaringClass().getName());
            }
            sameClass = clazz.equals(method.getDeclaringClass());
        }

        // If the class is public, we are done
        if (Modifier.isPublic(clazz.getModifiers())) {
            if (!sameClass && !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                setMethodAccessible(method); // Default access superclass workaround
            }
            return method;
        }

        final String methodName = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();

        // Check the implemented interfaces and subinterfaces
        method = getAccessibleMethodFromInterfaceNest(clazz, methodName, parameterTypes);

        // Check the superclass chain
        if (method == null) {
            method = getAccessibleMethodFromSuperclass(clazz, methodName, parameterTypes);
        }

        return method;
    }

    /**
     * Return an accessible method (that is, one that can be invoked via reflection) with given name and parameters. If no such method can be found, return
     * {@code null}. This is just a convenient wrapper for {@link #getAccessibleMethod(Method method)}.
     *
     * @param clazz          get method from this class
     * @param methodName     get method with this name
     * @param parameterTypes with these parameters types
     * @return The accessible method
     */
    public static Method getAccessibleMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
        try {
            final MethodDescriptor md = new MethodDescriptor(clazz, methodName, parameterTypes, true);
            // Check the cache first
            Method method = getCachedMethod(md);
            if (method != null) {
                return method;
            }

            method = getAccessibleMethod(clazz, clazz.getMethod(methodName, parameterTypes));
            cacheMethod(md, method);
            return method;
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Return an accessible method (that is, one that can be invoked via reflection) that implements the specified Method. If no such method can be found,
     * return {@code null}.
     *
     * @param method The method that we wish to call
     * @return The accessible method
     */
    public static Method getAccessibleMethod(final Method method) {
        // Make sure we have a method to check
        if (method == null) {
            return null;
        }

        return getAccessibleMethod(method.getDeclaringClass(), method);
    }

    /**
     * Return an accessible method (that is, one that can be invoked via reflection) that implements the specified method, by scanning through all implemented
     * interfaces and subinterfaces. If no such method can be found, return {@code null}.
     *
     * <p>
     * There isn't any good reason why this method must be private. It is because there doesn't seem any reason why other classes should call this rather than
     * the higher level methods.
     * </p>
     *
     * @param clazz          Parent class for the interfaces to be checked
     * @param methodName     Method name of the method we wish to call
     * @param parameterTypes The parameter type signatures
     */
    private static Method getAccessibleMethodFromInterfaceNest(Class<?> clazz, final String methodName, final Class<?>[] parameterTypes) {
        Method method = null;

        // Search up the superclass chain
        for (; clazz != null; clazz = clazz.getSuperclass()) {

            // Check the implemented interfaces of the parent class
            final Class<?>[] interfaces = clazz.getInterfaces();
            for (final Class<?> anInterface : interfaces) {

                // Is this interface public?
                if (!Modifier.isPublic(anInterface.getModifiers())) {
                    continue;
                }

                // Does the method exist on this interface?
                try {
                    method = anInterface.getDeclaredMethod(methodName, parameterTypes);
                } catch (final NoSuchMethodException e) {
                    /*
                     * Swallow, if no method is found after the loop then this method returns null.
                     */
                }
                if (method != null) {
                    return method;
                }

                // Recursively check our parent interfaces
                method = getAccessibleMethodFromInterfaceNest(anInterface, methodName, parameterTypes);
                if (method != null) {
                    return method;
                }

            }

        }

        // We did not find anything
        return null;
    }

    /**
     * Return an accessible method (that is, one that can be invoked via reflection) by scanning through the superclasses. If no such method can be found,
     * return {@code null}.
     *
     * @param clazz          Class to be checked
     * @param methodName     Method name of the method we wish to call
     * @param parameterTypes The parameter type signatures
     */
    private static Method getAccessibleMethodFromSuperclass(final Class<?> clazz, final String methodName, final Class<?>[] parameterTypes) {
        Class<?> parentClazz = clazz.getSuperclass();
        while (parentClazz != null) {
            if (Modifier.isPublic(parentClazz.getModifiers())) {
                try {
                    return parentClazz.getMethod(methodName, parameterTypes);
                } catch (final NoSuchMethodException e) {
                    return null;
                }
            }
            parentClazz = parentClazz.getSuperclass();
        }
        return null;
    }

    /**
     * Gets the method from the cache, if present.
     *
     * @param md The method descriptor
     * @return The cached method
     */
    private static Method getCachedMethod(final MethodDescriptor md) {
        if (CACHE_METHODS) {
            final Reference<Method> methodRef = CACHE.get(md);
            if (methodRef != null) {
                return methodRef.get();
            }
        }
        return null;
    }

    /**
     * Find an accessible method that matches the given name and has compatible parameters. Compatible parameters mean that every method parameter is assignable
     * from the given parameters. In other words, it finds a method with the given name that will take the parameters given.
     *
     * <p>
     * This method is slightly indeterministic since it loops through methods names and return the first matching method.
     * </p>
     *
     * <p>
     * This method is used by {@link #invokeMethod(Object object, String methodName, Object[] args, Class[] parameterTypes)}.
     *
     * <p>
     * This method can match primitive parameter by passing in wrapper classes. For example, a {@code Boolean</code> will match a primitive <code>boolean}
     * parameter.
     *
     * @param clazz          find method in this class
     * @param methodName     find method with this name
     * @param parameterTypes find method with compatible parameters
     * @return The accessible method
     */
    public static Method getMatchingAccessibleMethod(final Class<?> clazz, final String methodName, final Class<?>[] parameterTypes) {
        // trace logging
        if (LOG.isTraceEnabled()) {
            LOG.trace("Matching name=" + methodName + " on " + clazz);
        }
        final MethodDescriptor md = new MethodDescriptor(clazz, methodName, parameterTypes, false);

        // see if we can find the method directly
        // most of the time this works and it's much faster
        try {
            // Check the cache first
            Method method = getCachedMethod(md);
            if (method != null) {
                return method;
            }

            method = clazz.getMethod(methodName, parameterTypes);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Found straight match: " + method);
                LOG.trace("isPublic:" + Modifier.isPublic(method.getModifiers()));
            }

            setMethodAccessible(method); // Default access superclass workaround

            cacheMethod(md, method);
            return method;

        } catch (final NoSuchMethodException e) {
            /* SWALLOW */ }

        // search through all methods
        final int paramSize = parameterTypes.length;
        Method bestMatch = null;
        final Method[] methods = clazz.getMethods();
        float bestMatchCost = Float.MAX_VALUE;
        float myCost = Float.MAX_VALUE;
        for (final Method method2 : methods) {
            if (method2.getName().equals(methodName)) {
                // log some trace information
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Found matching name:");
                    LOG.trace(method2);
                }

                // compare parameters
                final Class<?>[] methodsParams = method2.getParameterTypes();
                final int methodParamSize = methodsParams.length;
                if (methodParamSize == paramSize) {
                    boolean match = true;
                    for (int n = 0; n < methodParamSize; n++) {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("Param=" + parameterTypes[n].getName());
                            LOG.trace("Method=" + methodsParams[n].getName());
                        }
                        if (!ClassUtils.isAssignable(parameterTypes[n], methodsParams[n])) {
                            if (LOG.isTraceEnabled()) {
                                LOG.trace(methodsParams[n] + " is not assignable from " + parameterTypes[n]);
                            }
                            match = false;
                            break;
                        }
                    }

                    if (match) {
                        // get accessible version of method
                        final Method method = getAccessibleMethod(clazz, method2);
                        if (method != null) {
                            if (LOG.isTraceEnabled()) {
                                LOG.trace(method + " accessible version of " + method2);
                            }
                            setMethodAccessible(method); // Default access superclass workaround
                            myCost = getTotalTransformationCost(parameterTypes, method.getParameterTypes());
                            if (myCost < bestMatchCost) {
                                bestMatch = method;
                                bestMatchCost = myCost;
                            }
                        }

                        LOG.trace("Couldn't find accessible method.");
                    }
                }
            }
        }
        if (bestMatch != null) {
            cacheMethod(md, bestMatch);
        } else {
            // didn't find a match
            LOG.trace("No match found.");
        }

        return bestMatch;
    }

    /**
     * Gets the number of steps required needed to turn the source class into the destination class. This represents the number of steps in the object hierarchy
     * graph.
     *
     * @param srcClass  The source class
     * @param destClass The destination class
     * @return The cost of transforming an object
     */
    private static float getObjectTransformationCost(Class<?> srcClass, final Class<?> destClass) {
        float cost = 0.0f;
        while (srcClass != null && !destClass.equals(srcClass)) {
            if (destClass.isPrimitive()) {
                final Class<?> destClassWrapperClazz = ClassUtils.wrapperToPrimitive(destClass);
                if (destClassWrapperClazz != null && destClassWrapperClazz.equals(srcClass)) {
                    cost += 0.25f;
                    break;
                }
            }
            final Class<?> cls = srcClass;
            if (destClass.isInterface() && ClassUtils.isAssignable(cls, destClass)) {
                // slight penalty for interface match.
                // we still want an exact match to override an interface match, but
                // an interface match should override anything where we have to get a
                // superclass.
                cost += 0.25f;
                break;
            }
            cost++;
            srcClass = srcClass.getSuperclass();
        }

        /*
         * If the destination class is null, we've traveled all the way up to an Object match. We'll penalize this by adding 1.5 to the cost.
         */
        if (srcClass == null) {
            cost += 1.5f;
        }

        return cost;
    }

    /**
     * Returns the sum of the object transformation cost for each class in the source argument list.
     *
     * @param srcArgs  The source arguments
     * @param destArgs The destination arguments
     * @return The total transformation cost
     */
    private static float getTotalTransformationCost(final Class<?>[] srcArgs, final Class<?>[] destArgs) {
        float totalCost = 0.0f;
        for (int i = 0; i < srcArgs.length; i++) {
            Class<?> srcClass, destClass;
            srcClass = srcArgs[i];
            destClass = destArgs[i];
            totalCost += getObjectTransformationCost(srcClass, destClass);
        }

        return totalCost;
    }

    /**
     * Invoke a method whose parameter types match exactly the object types.
     *
     * <p>
     * This uses reflection to invoke the method obtained from a call to {@code getAccessibleMethod()}.
     * </p>
     *
     * @param object     invoke method on this object
     * @param methodName get method with this name
     * @param args       use these arguments - treat null as empty array (passing null will result in calling the parameterless method with name
     *                   {@code methodName}).
     * @return The value returned by the invoked method
     * @throws NoSuchMethodException     if there is no such accessible method
     * @throws InvocationTargetException wraps an exception thrown by the method invoked
     * @throws IllegalAccessException    if the requested method is not accessible via reflection
     */
    public static Object invokeExactMethod(final Object object, final String methodName, Object... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (args == null) {
            args = BeanUtils.EMPTY_OBJECT_ARRAY;
        }
        final int arguments = args.length;
        final Class<?>[] parameterTypes = new Class[arguments];
        for (int i = 0; i < arguments; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        return invokeExactMethod(object, methodName, args, parameterTypes);
    }

    /**
     * Invoke a method whose parameter types match exactly the parameter types given.
     *
     * <p>
     * This uses reflection to invoke the method obtained from a call to {@code getAccessibleMethod()}.
     * </p>
     *
     * @param object         invoke method on this object
     * @param methodName     get method with this name
     * @param args           use these arguments - treat null as empty array (passing null will result in calling the parameterless method with name
     *                       {@code methodName}).
     * @param parameterTypes match these parameters - treat null as empty array
     * @return The value returned by the invoked method
     * @throws NoSuchMethodException     if there is no such accessible method
     * @throws InvocationTargetException wraps an exception thrown by the method invoked
     * @throws IllegalAccessException    if the requested method is not accessible via reflection
     */
    public static Object invokeExactMethod(final Object object, final String methodName, Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (args == null) {
            args = BeanUtils.EMPTY_OBJECT_ARRAY;
        }

        if (parameterTypes == null) {
            parameterTypes = BeanUtils.EMPTY_CLASS_ARRAY;
        }

        final Method method = getAccessibleMethod(object.getClass(), methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + object.getClass().getName());
        }
        return method.invoke(object, args);
    }

    /**
     * Invoke a static method whose parameter types match exactly the parameter types given.
     *
     * <p>
     * This uses reflection to invoke the method obtained from a call to {@link #getAccessibleMethod(Class, String, Class[])}.
     * </p>
     *
     * @param objectClass    invoke static method on this class
     * @param methodName     get method with this name
     * @param args           use these arguments - treat null as empty array (passing null will result in calling the parameterless method with name
     *                       {@code methodName}).
     * @param parameterTypes match these parameters - treat null as empty array
     * @return The value returned by the invoked method
     * @throws NoSuchMethodException     if there is no such accessible method
     * @throws InvocationTargetException wraps an exception thrown by the method invoked
     * @throws IllegalAccessException    if the requested method is not accessible via reflection
     * @since 1.8.0
     */
    public static Object invokeExactStaticMethod(final Class<?> objectClass, final String methodName, Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (args == null) {
            args = BeanUtils.EMPTY_OBJECT_ARRAY;
        }

        if (parameterTypes == null) {
            parameterTypes = BeanUtils.EMPTY_CLASS_ARRAY;
        }

        final Method method = getAccessibleMethod(objectClass, methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: " + objectClass.getName());
        }
        return method.invoke(null, args);
    }

    /**
     * Invoke a named method whose parameter type matches the object type.
     *
     * <p>
     * The behavior of this method is less deterministic than
     * {@link #invokeExactMethod(Object object, String methodName, Object[] args, Class[] parameterTypes)}. It loops through all methods with names that match
     * and then executes the first it finds with compatible parameters.
     * </p>
     *
     * <p>
     * This method supports calls to methods taking primitive parameters via passing in wrapping classes. So, for example, a {@code Boolean} class would match a
     * {@code boolean} primitive.
     * </p>
     *
     *
     * @param object         invoke method on this object
     * @param methodName     get method with this name
     * @param args           use these arguments - treat null as empty array (passing null will result in calling the parameterless method with name
     *                       {@code methodName}).
     * @param parameterTypes match these parameters - treat null as empty array
     * @return The value returned by the invoked method
     * @throws NoSuchMethodException     if there is no such accessible method
     * @throws InvocationTargetException wraps an exception thrown by the method invoked
     * @throws IllegalAccessException    if the requested method is not accessible via reflection
     */
    public static Object invokeMethod(final Object object, final String methodName, Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (parameterTypes == null) {
            parameterTypes = BeanUtils.EMPTY_CLASS_ARRAY;
        }
        if (args == null) {
            args = BeanUtils.EMPTY_OBJECT_ARRAY;
        }

        final Method method = getMatchingAccessibleMethod(object.getClass(), methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + object.getClass().getName());
        }
        return method.invoke(object, args);
    }

    /**
     * Sets whether methods should be cached for greater performance or not, default is {@code true}.
     *
     * @param cacheMethods {@code true} if methods should be cached for greater performance, otherwise {@code false}
     * @since 1.8.0
     */
    public static synchronized void setCacheMethods(final boolean cacheMethods) {
        CACHE_METHODS = cacheMethods;
        if (!CACHE_METHODS) {
            clearCache();
        }
    }

    /**
     * Try to make the method accessible
     *
     * @param method The source arguments
     */
    private static void setMethodAccessible(final Method method) {
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
            // modifier is public.
            //
            // The following workaround solves the problem but will only
            // work from sufficiently privileges code.
            //
            // Better workarounds would be gratefully accepted.
            //
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
        } catch (final SecurityException e) {
            // log but continue just in case the method.invoke works anyway
            LOG.debug("Cannot setAccessible on method. Therefore cannot use jvm access bug workaround.", e);
        }
    }

    private MethodUtils() {
        // empty
    }
}
