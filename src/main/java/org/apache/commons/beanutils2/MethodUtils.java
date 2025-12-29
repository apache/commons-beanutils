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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.collections4.map.ConcurrentReferenceHashMap;
import org.apache.commons.collections4.map.ConcurrentReferenceHashMap.ReferenceType;
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
 *
 * @see <a href="https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/reflect/MethodUtils.html">Apache Commons Lang MethodUtils</a>
 */
public final class MethodUtils {

    /**
     * Represents the key to looking up a Method by reflection.
     */
    private static final class MethodKey {

        private final Class<?> cls;
        private final String methodName;
        private final Class<?>[] paramTypes;
        private final boolean exact;
        private final int hashCode;

        /**
         * The sole constructor.
         *
         * @param cls        the class to reflect, must not be null.
         * @param methodName the method name to obtain.
         * @param paramTypes the array of classes representing the parameter types.
         * @param exact      whether the match has to be exact.
         */
        public MethodKey(final Class<?> cls, final String methodName, final Class<?>[] paramTypes, final boolean exact) {
            this.cls = Objects.requireNonNull(cls, "cls");
            this.methodName = Objects.requireNonNull(methodName, "methodName");
            this.paramTypes = paramTypes != null ? paramTypes : BeanUtils.EMPTY_CLASS_ARRAY;
            this.exact = exact;
            this.hashCode = methodName.length();
        }

        /**
         * Checks for equality.
         *
         * @param obj object to be tested for equality.
         * @return true, if the object describes the same Method.
         */
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof MethodKey)) {
                return false;
            }
            final MethodKey md = (MethodKey) obj;
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

        @Override
        public String toString() {
            return "MethodKey [cls=" + cls + ", methodName=" + methodName + ", paramTypes=" + Arrays.toString(paramTypes) + ", exact=" + exact + "]";
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
    private static boolean CACHE_ENABLED = true; //NOPMD @GuardedBy("this")

    /**
     * Stores a cache of MethodKey -> Method.
     * <p>
     * The keys into this map only ever exist as temporary variables within methods of this class, and are never exposed to users of this class. This means that
     * this map is used only as a mechanism for limiting the size of the cache, that is, a way to tell the garbage collector that the contents of the cache can
     * be completely garbage-collected whenever it needs the memory. Whether this is a good approach to this problem is doubtful; something like the Commons
     * Collections LRUMap may be more appropriate (though of course selecting an appropriate size is an issue).
     * </p>
     * <p>
     * This static variable is safe even when this code is deployed via a shared class loader because it is keyed via a MethodKey object which has a Class as
     * one of its members and that member is used in the MethodKey.equals method. So two components that load the same class via different class loaders will
     * generate non-equal MethodKey objects and hence end up with different entries in the map.
     * </p>
     */
    // @formatter:off
    private static final Map<MethodKey, Method> CACHE = new ConcurrentReferenceHashMap.Builder<MethodKey, Method>()
            .setKeyReferenceType(ReferenceType.WEAK)
            .setValueReferenceType(ReferenceType.WEAK)
            .get();
    // @formatter:on

    /**
     * Clear the method cache.
     *
     * @return the number of cached methods cleared.
     * @since 1.8.0
     */
    public static synchronized int clearCache() {
        final int size = CACHE.size();
        CACHE.clear();
        return size;
    }

    private static Method computeIfAbsent(final MethodKey key, final Function<MethodKey, ? extends Method> mappingFunction) {
        final Method method = CACHE_ENABLED ? CACHE.computeIfAbsent(key, k -> mappingFunction.apply(k)) : mappingFunction.apply(key);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Matched " + key + " with method: " + method + ", CACHE_ENABLED: " + CACHE_ENABLED);
        }
        return method;
    }

    /**
     * Gets an accessible method (that is, one that can be invoked via reflection) that implements the specified Method. If no such method can be found, return
     * {@code null}.
     *
     * @param clazz  The class of the object.
     * @param method The method that we wish to call.
     * @return The accessible method.
     * @since 1.8.0
     * @deprecated Use {@link org.apache.commons.lang3.reflect.MethodUtils#getAccessibleMethod(Class, Method)}.
     */
    @Deprecated
    public static Method getAccessibleMethod(Class<?> clazz, Method method) {
        return org.apache.commons.lang3.reflect.MethodUtils.getAccessibleMethod(clazz, method);
    }

    /**
     * Gets an accessible method (that is, one that can be invoked via reflection) with given name and parameters. If no such method can be found, return
     * {@code null}. This is just a convenient wrapper for {@link #getAccessibleMethod(Method method)}.
     *
     * @param clazz          get method from this class.
     * @param methodName     get method with this name.
     * @param parameterTypes with these parameters types.
     * @return The accessible method.
     */
    public static Method getAccessibleMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
        return computeIfAbsent(new MethodKey(clazz, methodName, parameterTypes, true), k -> {
            try {
                return org.apache.commons.lang3.reflect.MethodUtils.getAccessibleMethod(clazz, clazz.getMethod(methodName, parameterTypes));
            } catch (final NoSuchMethodException e) {
                return null;
            }
        });
    }

    /**
     * Gets an accessible method (that is, one that can be invoked via reflection) that implements the specified Method. If no such method can be found, return
     * {@code null}.
     *
     * @param method The method that we wish to call.
     * @return The accessible method.
     * @deprecated Use {@link org.apache.commons.lang3.reflect.MethodUtils#getAccessibleMethod(Method)}.
     */
    @Deprecated
    public static Method getAccessibleMethod(final Method method) {
        return org.apache.commons.lang3.reflect.MethodUtils.getAccessibleMethod(method);
    }

    /**
     * Gets an accessible method that matches the given name and has compatible parameters. Compatible parameters mean that every method parameter is assignable
     * from the given parameters. In other words, it finds a method with the given name that will take the parameters given.
     *
     * <p>
     * This method is slightly indeterministic since it loops through methods names and return the first matching method.
     * </p>
     *
     * <p>
     * This method can match primitive parameter by passing in wrapper classes. For example, a {@code Boolean</code> will match a primitive <code>boolean}
     * parameter.
     * </p>
     *
     * @param clazz          find method in this class.
     * @param methodName     find method with this name.
     * @param parameterTypes find method with compatible parameters.
     * @return The accessible method.
     */
    public static Method getMatchingAccessibleMethod(final Class<?> clazz, final String methodName, final Class<?>[] parameterTypes) {
        return computeIfAbsent(new MethodKey(clazz, methodName, parameterTypes, false), k -> {
            final Method method = org.apache.commons.lang3.reflect.MethodUtils.getMatchingAccessibleMethod(clazz, methodName, parameterTypes);
            if (method != null) {
                setMethodAccessible(method); // Default access superclass workaround
            }
            return method;
        });
    }

    /**
     * Sets whether methods should be cached for greater performance or not, default is {@code true}.
     *
     * @param cacheMethods {@code true} if methods should be cached for greater performance, otherwise {@code false}
     * @since 1.8.0
     */
    public static synchronized void setCacheMethods(final boolean cacheMethods) {
        CACHE_ENABLED = cacheMethods;
        if (!CACHE_ENABLED) {
            CACHE.clear();
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
