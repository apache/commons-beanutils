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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An instance of this class represents a value that is provided per (thread)
 * context classloader.
 *
 * <p>Occasionally it is necessary to store data in "global" variables
 * (including uses of the Singleton pattern). In applications which have only
 * a single classloader such data can simply be stored as "static" members on
 * some class. When multiple classloaders are involved, however, this approach
 * can fail; in particular, this doesn't work when the code may be run within a
 * servlet container or a j2ee container, and the class on which the static
 * member is defined is loaded via a "shared" classloader that is visible to all
 * components running within the container. This class provides a mechanism for
 * associating data with a ClassLoader instance, which ensures that when the
 * code runs in such a container each component gets its own copy of the
 * "global" variable rather than unexpectedly sharing a single copy of the
 * variable with other components that happen to be running in the same
 * container at the same time (eg servlets or EJBs.)</p>
 *
 * <p>This class is strongly patterned after the java.lang.ThreadLocal
 * class, which performs a similar task in allowing data to be associated
 * with a particular thread.</p>
 *
 * <p>When code that uses this class is run as a "normal" application, ie
 * not within a container, the effect is identical to just using a static
 * member variable to store the data, because Thread.getContextClassLoader
 * always returns the same classloader (the system classloader).</p>
 *
 * <p>Expected usage is as follows:</p>
 * <pre>
 * <code>
 *  public class SomeClass {
 *    private static final ContextClassLoaderLocal&lt;String&gt; global
 *      = new ContextClassLoaderLocal&lt;String&gt;() {
 *          protected String initialValue() {
 *              return new String("Initial value");
 *          };
 *
 *    public void testGlobal() {
 *      String s = global.get();
 *      System.out.println("global value:" + s);
 *      buf.set("New Value");
 *    }
 * </code>
 * </pre>
 *
 * <p><strong>Note:</strong> This class takes some care to ensure that when
 * a component which uses this class is "undeployed" by a container the
 * component-specific classloader and all its associated classes (and their
 * static variables) are garbage-collected. Unfortunately there is one
 * scenario in which this does <i>not</i> work correctly and there
 * is unfortunately no known workaround other than ensuring that the
 * component (or its container) calls the "unset" method on this class for
 * each instance of this class when the component is undeployed. The problem
 * occurs if:
 * <ul>
 * <li>the class containing a static instance of this class was loaded via
 * a shared classloader, and</li>
 * <li>the value stored in the instance is an object whose class was loaded
 * via the component-specific classloader (or any of the objects it refers
 * to were loaded via that classloader).</li>
 * </ul>
 * <p>The result is that the map managed by this object still contains a strong
 * reference to the stored object, which contains a strong reference to the
 * classloader that loaded it, meaning that although the container has
 * "undeployed" the component the component-specific classloader and all the
 * related classes and static variables cannot be garbage-collected. This is
 * not expected to be an issue with the commons-beanutils library as the only
 * classes which use this class are BeanUtilsBean and ConvertUtilsBean and
 * there is no obvious reason for a user of the beanutils library to subclass
 * either of those classes.</p>
 *
 * <p><strong>Note:</strong> A WeakHashMap bug in several 1.3 JVMs results in
 * a memory leak for those JVMs.</p>
 *
 * <p><strong>Note:</strong> Of course all of this would be unnecessary if
 * containers required each component to load the full set of classes it
 * needs, ie avoided providing classes loaded via a "shared" classloader.</p>
 *
 * @param <T> the type of data stored in an instance
 * @version $Id$
 * @see java.lang.Thread#getContextClassLoader
 */
public class ContextClassLoaderLocal<T> {
    private final Map<WeakKey<ClassLoader>, Object> valueByClassLoader = new ConcurrentHashMap<WeakKey<ClassLoader>, Object>();
    private final ReferenceQueue<ClassLoader> weakQueue = new ReferenceQueue<ClassLoader>();

    private AtomicReference<Object> localValue = new AtomicReference<Object>();
    private AtomicReference<Object> globalValue = new AtomicReference<Object>();

    private WeakReference<ClassLoader> localClassLoader;

    /**
     * Value representing null keys inside tables.
     */
    private static final Object NULL_VALUE = new Object();

    /**
     * Use NULL_KEY for key if it is null.
     */
    private static Object maskNull(Object value) {
        return (value == null ? NULL_VALUE : value);
    }

    /**
     * Returns internal representation of null key back to caller as null.
     */
    @SuppressWarnings("unchecked")
    private static <T> T unmaskNull(Object value) {
        return (T)(value == NULL_VALUE ? null : value);
    }

    private static class WeakKey<T> extends WeakReference<T> {
        private final int hashCode;

        WeakKey(T key) {
            this(key,null);
        }

        WeakKey(T key, ReferenceQueue<? super T> queue) {
            super(key,queue);
            hashCode = key.hashCode();
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof WeakKey)) {
                return false;
            }
            T curObj = get();
            return this == obj || curObj != null && curObj.equals(obj);
        }
    }

    /**
     * Construct a context classloader instance
     */
    public ContextClassLoaderLocal() {
        super();
        localClassLoader = new WeakReference<ClassLoader>(this.getClass().getClassLoader());
    }

    /**
     * Returns the initial value for this ContextClassLoaderLocal
     * variable. This method will be called once per Context ClassLoader for
     * each ContextClassLoaderLocal, the first time it is accessed
     * with get or set.  If the programmer desires ContextClassLoaderLocal variables
     * to be initialized to some value other than null, ContextClassLoaderLocal must
     * be subclassed, and this method overridden.  Typically, an anonymous
     * inner class will be used.  Typical implementations of initialValue
     * will call an appropriate constructor and return the newly constructed
     * object.
     *
     * @return a new Object to be used as an initial value for this ContextClassLoaderLocal
     */
    protected T initialValue() {
        return null;
    }

    private void expungeStaleValues() {
      for (Reference<? extends ClassLoader> x; (x = weakQueue.poll()) != null; ) {
          //noinspection SuspiciousMethodCalls
          valueByClassLoader.remove(x);
      }
    }

    @SuppressWarnings("unchecked")
    private T getRefValue(AtomicReference<Object> ref) {
  		Object value = ref.get();
  		if(value == null) {
  			value = maskNull(initialValue());
  			if(!ref.compareAndSet(null, value)) {
  				value = ref.get();
  			}
  		}
  		return (T)unmaskNull(value);
    }

    private void setRefValue(AtomicReference<Object> ref, T value) {
  		ref.set(maskNull(value));
    }

    private ClassLoader getLocalClassLoader() {
    	ClassLoader result = localClassLoader.get();
    	if(result == null) {
    		synchronized (this) {
    			if((result = localClassLoader.get()) == null) {
      			localValue.set(null);
      			result = this.getClass().getClassLoader();
      			localClassLoader = new WeakReference<ClassLoader>(result);
    			}
    		}
    	}
    	return result;
    }

    /**
     * Gets the instance which provides the functionality for {@link BeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     * @return the object currently associated with the context-classloader of the current thread.
     */
    public T get() {
        expungeStaleValues();
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                if(contextClassLoader.equals(getLocalClassLoader())) {
                    return getRefValue(localValue);
                }
                WeakKey<ClassLoader> key = new WeakKey<ClassLoader>(contextClassLoader);
                Object value = valueByClassLoader.get(key);
                if(value == null) {
                    value = initialValue();
                    valueByClassLoader.put(new WeakKey<ClassLoader>(contextClassLoader,weakQueue), maskNull(value));
                }
                return unmaskNull(value);
            }
  		  } catch (final SecurityException e) { /* SWALLOW - should we log this? */ }

  		return getRefValue(globalValue);
    }

    /**
     * Sets the value - a value is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     *
     * @param value the object to be associated with the entrant thread's context classloader
     */
    public void set(T value) {
        expungeStaleValues();
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                if(contextClassLoader.equals(getLocalClassLoader())) {
                    setRefValue(localValue, value);
                } else {
                    valueByClassLoader.put(new WeakKey<ClassLoader>(contextClassLoader), maskNull(value));
                }
                return;
            }
        } catch (SecurityException e) { /* SWALLOW - should we log this? */ }

        setRefValue(globalValue, value);
    }

    /**
     * Unsets the value associated with the current thread's context classloader
     */
    public void unset() {
        try {
            unset(Thread.currentThread().getContextClassLoader());
        } catch (final SecurityException e) { /* SWALLOW - should we log this? */ }
    }

    /**
     * Unsets the value associated with the given classloader
     * @param classLoader The classloader to <i>unset</i> for
     */
    public void unset(ClassLoader classLoader) {
        if(classLoader != null) {
            if(classLoader.equals(getLocalClassLoader())) {
                localValue.set(null);
            } else {
                valueByClassLoader.remove(new WeakKey<ClassLoader>(classLoader));
            }
        }
    }
}