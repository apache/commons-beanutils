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


import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;


/**
 * <p>Implementation of <code>DynaClass</code> for DynaBeans that wrap
 * standard JavaBean instances.</p>
 *
 * <p>
 * It is suggested that this class should not usually need to be used directly
 * to create new <code>WrapDynaBean</code> instances.
 * It's usually better to call the <code>WrapDynaBean</code> constructor directly.
 * For example:</p>
 * <code><pre>
 *   Object javaBean = ...;
 *   DynaBean wrapper = new WrapDynaBean(javaBean);
 * </pre></code>
 * <p>
 *
 * @version $Id$
 */

public class WrapDynaClass implements DynaClass {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new WrapDynaClass for the specified JavaBean class.  This
     * constructor is private; WrapDynaClass instances will be created as
     * needed via calls to the <code>createDynaClass(Class)</code> method.
     *
     * @param beanClass JavaBean class to be introspected around
     * @param propUtils the {@code PropertyUtilsBean} associated with this class
     */
    private WrapDynaClass(final Class<?> beanClass, final PropertyUtilsBean propUtils) {

        this.beanClassRef = new SoftReference<Class<?>>(beanClass);
        this.beanClassName = beanClass.getName();
        propertyUtilsBean = propUtils;
        introspect();

    }


    // ----------------------------------------------------- Instance Variables

    /**
     * Name of the JavaBean class represented by this WrapDynaClass.
     */
    private String beanClassName = null;

    /**
     * Reference to the JavaBean class represented by this WrapDynaClass.
     */
    private Reference<Class<?>> beanClassRef = null;

    /** Stores the associated {@code PropertyUtilsBean} instance. */
    private final PropertyUtilsBean propertyUtilsBean;

    /**
     * The JavaBean <code>Class</code> which is represented by this
     * <code>WrapDynaClass</code>.
     *
     * @deprecated No longer initialized, use getBeanClass() method instead
     */
    @Deprecated
    protected Class<?> beanClass = null;


    /**
     * The set of PropertyDescriptors for this bean class.
     */
    protected PropertyDescriptor[] descriptors = null;


    /**
     * The set of PropertyDescriptors for this bean class, keyed by the
     * property name.  Individual descriptor instances will be the same
     * instances as those in the <code>descriptors</code> list.
     */
    protected HashMap<String, PropertyDescriptor> descriptorsMap = new HashMap<String, PropertyDescriptor>();


    /**
     * The set of dynamic properties that are part of this DynaClass.
     */
    protected DynaProperty[] properties = null;


    /**
     * The set of dynamic properties that are part of this DynaClass,
     * keyed by the property name.  Individual descriptor instances will
     * be the same instances as those in the <code>properties</code> list.
     */
    protected HashMap<String, DynaProperty> propertiesMap = new HashMap<String, DynaProperty>();


    // ------------------------------------------------------- Static Variables


    private static final ContextClassLoaderLocal<Map<CacheKey, WrapDynaClass>> CLASSLOADER_CACHE =
        new ContextClassLoaderLocal<Map<CacheKey, WrapDynaClass>>() {
            @Override
            protected Map<CacheKey, WrapDynaClass> initialValue() {
                return new WeakHashMap<CacheKey, WrapDynaClass>();
        }
    };

    /**
     * Get the wrap dyna classes cache. Note: This method only exists to
     * satisfy the deprecated {@code dynaClasses} hash map.
     */
    @SuppressWarnings("unchecked")
    private static Map<Object, Object> getDynaClassesMap() {
        @SuppressWarnings("rawtypes")
        final
        Map cache = CLASSLOADER_CACHE.get();
        return cache;
    }

    /**
     * Returns the cache for the already created class instances. For each
     * combination of bean class and {@code PropertyUtilsBean} instance an
     * entry is created in the cache.
     * @return the cache for already created {@code WrapDynaClass} instances
     */
    private static Map<CacheKey, WrapDynaClass> getClassesCache() {
        return CLASSLOADER_CACHE.get();
    }

    /**
     * The set of <code>WrapDynaClass</code> instances that have ever been
     * created, keyed by the underlying bean Class. The keys to this map
     * are Class objects, and the values are corresponding WrapDynaClass
     * objects.
     * <p>
     * This static variable is safe even when this code is deployed via a
     * shared classloader because it is keyed via a Class object. The same
     * class loaded via two different classloaders will result in different
     * entries in this map.
     * <p>
     * Note, however, that this HashMap can result in a memory leak. When
     * this class is in a shared classloader it will retain references to
     * classes loaded via a webapp classloader even after the webapp has been
     * undeployed. That will prevent the entire classloader and all the classes
     * it refers to and all their static members from being freed.
     *
     ************* !!!!!!!!!!!! PLEASE NOTE !!!!!!!!!!!! *************
     *
     * THE FOLLOWING IS A NASTY HACK TO SO THAT BEANUTILS REMAINS BINARY
     *              COMPATIBLE WITH PREVIOUS RELEASES.
     *
     * There are two issues here:
     *
     * 1) Memory Issues: The static HashMap caused memory problems (See BEANUTILS-59)
     *    to resolve this it has been moved into a ContextClassLoaderLocal instance
     *    (named CLASSLOADER_CACHE above) which holds one copy per
     *    ClassLoader in a WeakHashMap.
     *
     * 2) Binary Compatibility: As the "dynaClasses" static HashMap is "protected"
     *    removing it breaks BeanUtils binary compatibility with previous versions.
     *    To resolve this all the methods have been overriden to delegate to the
     *    Map for the ClassLoader in the ContextClassLoaderLocal.
     *
     * @deprecated The dynaClasses Map will be removed in a subsequent release
     */
    @Deprecated
    protected static HashMap<Object, Object> dynaClasses = new HashMap<Object, Object>() {
        @Override
        public void clear() {
            getDynaClassesMap().clear();
        }
        @Override
        public boolean containsKey(final Object key) {
            return getDynaClassesMap().containsKey(key);
        }
        @Override
        public boolean containsValue(final Object value) {
            return getDynaClassesMap().containsValue(value);
        }
        @Override
        public Set<Map.Entry<Object, Object>> entrySet() {
            return getDynaClassesMap().entrySet();
        }
        @Override
        public boolean equals(final Object o) {
            return getDynaClassesMap().equals(o);
        }
        @Override
        public Object get(final Object key) {
            return getDynaClassesMap().get(key);
        }
        @Override
        public int hashCode() {
            return getDynaClassesMap().hashCode();
        }
        @Override
        public boolean isEmpty() {
            return getDynaClassesMap().isEmpty();
        }
        @Override
        public Set<Object> keySet() {
            // extract the classes from the key to stay backwards compatible
            final Set<Object> result = new HashSet<Object>();
            for (final CacheKey k : getClassesCache().keySet()) {
                result.add(k.beanClass);
            }
            return result;
        }
        @Override
        public Object put(final Object key, final Object value) {
            return getClassesCache().put(
                    new CacheKey((Class<?>) key, PropertyUtilsBean.getInstance()),
                    (WrapDynaClass) value);
        }
        @Override
        public void putAll(final Map<? extends Object, ? extends Object> m) {
            for (final Map.Entry<? extends Object, ? extends Object> e : m.entrySet()) {
                put(e.getKey(), e.getValue());
            }
        }
        @Override
        public Object remove(final Object key) {
            return getDynaClassesMap().remove(key);
        }
        @Override
        public int size() {
            return getDynaClassesMap().size();
        }
        @Override
        public Collection<Object> values() {
            return getDynaClassesMap().values();
        }
    };


    // ------------------------------------------------------ DynaClass Methods

    /**
     * Return the class of the underlying wrapped bean.
     *
     * @return the class of the underlying wrapped bean
     * @since 1.8.0
     */
    protected Class<?> getBeanClass() {
        return beanClassRef.get();
    }

    /**
     * Return the name of this DynaClass (analogous to the
     * <code>getName()</code> method of <code>java.lang.Class</code), which
     * allows the same <code>DynaClass</code> implementation class to support
     * different dynamic classes, with different sets of properties.
     *
     * @return the name of the DynaClass
     */
    public String getName() {

        return beanClassName;

    }


    /**
     * Return a property descriptor for the specified property, if it exists;
     * otherwise, return <code>null</code>.
     *
     * @param name Name of the dynamic property for which a descriptor
     *  is requested
     * @return The descriptor for the specified property
     *
     * @throws IllegalArgumentException if no property name is specified
     */
    public DynaProperty getDynaProperty(final String name) {

        if (name == null) {
            throw new IllegalArgumentException
                    ("No property name specified");
        }
        return (propertiesMap.get(name));

    }


    /**
     * <p>Return an array of <code>ProperyDescriptors</code> for the properties
     * currently defined in this DynaClass.  If no properties are defined, a
     * zero-length array will be returned.</p>
     *
     * <p><strong>FIXME</strong> - Should we really be implementing
     * <code>getBeanInfo()</code> instead, which returns property descriptors
     * and a bunch of other stuff?</p>
     *
     * @return the set of properties for this DynaClass
     */
    public DynaProperty[] getDynaProperties() {

        return (properties);

    }


    /**
     * <p>Instantiates a new standard JavaBean instance associated with
     * this DynaClass and return it wrapped in a new WrapDynaBean
     * instance. <strong>NOTE</strong> the JavaBean should have a
     * no argument constructor.</p>
     *
     * <strong>NOTE</strong> - Most common use cases should not need to use
     * this method. It is usually better to create new
     * <code>WrapDynaBean</code> instances by calling its constructor.
     * For example:</p>
     * <code><pre>
     *   Object javaBean = ...;
     *   DynaBean wrapper = new WrapDynaBean(javaBean);
     * </pre></code>
     * <p>
     * (This method is needed for some kinds of <code>DynaBean</code> framework.)
     * </p>
     *
     * @return A new <code>DynaBean</code> instance
     * @throws IllegalAccessException if the Class or the appropriate
     *  constructor is not accessible
     * @throws InstantiationException if this Class represents an abstract
     *  class, an array class, a primitive type, or void; or if instantiation
     *  fails for some other reason
     */
    public DynaBean newInstance()
            throws IllegalAccessException, InstantiationException {

        return new WrapDynaBean(getBeanClass().newInstance());

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Return the PropertyDescriptor for the specified property name, if any;
     * otherwise return <code>null</code>.
     *
     * @param name Name of the property to be retrieved
     * @return The descriptor for the specified property
     */
    public PropertyDescriptor getPropertyDescriptor(final String name) {

        return (descriptorsMap.get(name));

    }


    // --------------------------------------------------------- Static Methods


    /**
     * Clear our cache of WrapDynaClass instances.
     */
    public static void clear() {

        getClassesCache().clear();

    }


    /**
     * Create (if necessary) and return a new <code>WrapDynaClass</code>
     * instance for the specified bean class.
     *
     * @param beanClass Bean class for which a WrapDynaClass is requested
     * @return A new <i>Wrap</i> {@link DynaClass}
     */
    public static WrapDynaClass createDynaClass(final Class<?> beanClass) {

        return createDynaClass(beanClass, null);

    }


    /**
     * Create (if necessary) and return a new {@code WrapDynaClass} instance
     * for the specified bean class using the given {@code PropertyUtilsBean}
     * instance for introspection. Using this method a specially configured
     * {@code PropertyUtilsBean} instance can be hooked into the introspection
     * mechanism of the managed bean. The argument is optional; if no
     * {@code PropertyUtilsBean} object is provided, the default instance is used.
     * @param beanClass Bean class for which a WrapDynaClass is requested
     * @param pu the optional {@code PropertyUtilsBean} to be used for introspection
     * @return A new <i>Wrap</i> {@link DynaClass}
     * @since 1.9
     */
    public static WrapDynaClass createDynaClass(final Class<?> beanClass, final PropertyUtilsBean pu) {

        final PropertyUtilsBean propUtils = (pu != null) ? pu : PropertyUtilsBean.getInstance();
        final CacheKey key = new CacheKey(beanClass, propUtils);
        WrapDynaClass dynaClass = getClassesCache().get(key);
        if (dynaClass == null) {
            dynaClass = new WrapDynaClass(beanClass, propUtils);
            getClassesCache().put(key, dynaClass);
        }
        return (dynaClass);

    }


    // ------------------------------------------------------ Protected Methods

    /**
     * Returns the {@code PropertyUtilsBean} instance associated with this class. This
     * bean is used for introspection.
     *
     * @return the associated {@code PropertyUtilsBean} instance
     * @since 1.9
     */
    protected PropertyUtilsBean getPropertyUtilsBean() {
        return propertyUtilsBean;
    }

    /**
     * Introspect our bean class to identify the supported properties.
     */
    protected void introspect() {

        // Look up the property descriptors for this bean class
        final Class<?> beanClass = getBeanClass();
        PropertyDescriptor[] regulars =
                getPropertyUtilsBean().getPropertyDescriptors(beanClass);
        if (regulars == null) {
            regulars = new PropertyDescriptor[0];
        }
        @SuppressWarnings("deprecation")
        Map<?, ?> mappeds =
                PropertyUtils.getMappedPropertyDescriptors(beanClass);
        if (mappeds == null) {
            mappeds = new HashMap<Object, Object>();
        }

        // Construct corresponding DynaProperty information
        properties = new DynaProperty[regulars.length + mappeds.size()];
        for (int i = 0; i < regulars.length; i++) {
            descriptorsMap.put(regulars[i].getName(),
                    regulars[i]);
            properties[i] =
                    new DynaProperty(regulars[i].getName(),
                            regulars[i].getPropertyType());
            propertiesMap.put(properties[i].getName(),
                    properties[i]);
        }
        int j = regulars.length;
        final Iterator<?> names = mappeds.keySet().iterator();
        while (names.hasNext()) {
            final String name = (String) names.next();
            final PropertyDescriptor descriptor =
                    (PropertyDescriptor) mappeds.get(name);
            properties[j] =
                    new DynaProperty(descriptor.getName(),
                            Map.class);
            propertiesMap.put(properties[j].getName(),
                    properties[j]);
            j++;
        }

    }

    /**
     * A class representing the combined key for the cache of {@code WrapDynaClass}
     * instances. A single key consists of a bean class and an instance of
     * {@code PropertyUtilsBean}. Instances are immutable.
     */
    private static class CacheKey {
        /** The bean class. */
        private final Class<?> beanClass;

        /** The instance of PropertyUtilsBean. */
        private final PropertyUtilsBean propUtils;

        /**
         * Creates a new instance of {@code CacheKey}.
         *
         * @param beanCls the bean class
         * @param pu the instance of {@code PropertyUtilsBean}
         */
        public CacheKey(final Class<?> beanCls, final PropertyUtilsBean pu) {
            beanClass = beanCls;
            propUtils = pu;
        }

        @Override
        public int hashCode() {
            final int factor = 31;
            int result = 17;
            result = factor * beanClass.hashCode() + result;
            result = factor * propUtils.hashCode() + result;
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CacheKey)) {
                return false;
            }

            final CacheKey c = (CacheKey) obj;
            return beanClass.equals(c.beanClass) && propUtils.equals(c.propUtils);
        }
    }
}
