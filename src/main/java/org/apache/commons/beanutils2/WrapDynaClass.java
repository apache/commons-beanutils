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


import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Implementation of {@link DynaClass} that wrap
 * standard JavaBean instances.
 * <p>
 * This class should not usually need to be used directly
 * to create new {@link WrapDynaBean} instances -
 * it's usually better to call the {@link WrapDynaBean} constructor.
 * For example:
 * </p>
 * <pre>
 *   Object javaBean = ...;
 *   DynaBean wrapper = new WrapDynaBean(javaBean);
 * </pre>
 *
 */

public class WrapDynaClass implements DynaClass {





    /**
     * Construct a new WrapDynaClass for the specified JavaBean class.  This
     * constructor is private; WrapDynaClass instances will be created as
     * needed via calls to the {@code createDynaClass(Class)} method.
     *
     * @param beanClass JavaBean class to be introspected around
     * @param propUtils the {@code PropertyUtilsBean} associated with this class
     */
    private WrapDynaClass(final Class<?> beanClass, final PropertyUtilsBean propUtils) {

        this.beanClassRef = new SoftReference<>(beanClass);
        this.beanClassName = beanClass.getName();
        propertyUtilsBean = propUtils;
        introspect();

    }




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
     * The set of PropertyDescriptors for this bean class.
     */
    protected PropertyDescriptor[] descriptors = null;


    /**
     * The set of PropertyDescriptors for this bean class, keyed by the
     * property name.  Individual descriptor instances will be the same
     * instances as those in the {@code descriptors} list.
     */
    protected HashMap<String, PropertyDescriptor> descriptorsMap = new HashMap<>();


    /**
     * The set of dynamic properties that are part of this DynaClass.
     */
    protected DynaProperty[] properties = null;


    /**
     * The set of dynamic properties that are part of this DynaClass,
     * keyed by the property name.  Individual descriptor instances will
     * be the same instances as those in the {@code properties} list.
     */
    protected HashMap<String, DynaProperty> propertiesMap = new HashMap<>();





    private static final ContextClassLoaderLocal<Map<CacheKey, WrapDynaClass>> CLASSLOADER_CACHE =
        new ContextClassLoaderLocal<Map<CacheKey, WrapDynaClass>>() {
            @Override
                protected Map<CacheKey, WrapDynaClass> initialValue() {
                    return new WeakHashMap<>();
                }
    };

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
     * {@code getName()} method of {@code java.lang.Class}, which
     * allows the same {@code DynaClass} implementation class to support
     * different dynamic classes, with different sets of properties.
     *
     * @return the name of the DynaClass
     */
    @Override
    public String getName() {

        return beanClassName;

    }


    /**
     * Return a property descriptor for the specified property, if it exists;
     * otherwise, return {@code null}.
     *
     * @param name Name of the dynamic property for which a descriptor
     *  is requested
     * @return The descriptor for the specified property
     *
     * @throws IllegalArgumentException if no property name is specified
     */
    @Override
    public DynaProperty getDynaProperty(final String name) {

        if (name == null) {
            throw new IllegalArgumentException
                    ("No property name specified");
        }
        return propertiesMap.get(name);

    }


    /**
     * <p>Return an array of {@code PropertyDescriptor} for the properties
     * currently defined in this DynaClass.  If no properties are defined, a
     * zero-length array will be returned.</p>
     *
     * <p><strong>FIXME</strong> - Should we really be implementing
     * {@code getBeanInfo()} instead, which returns property descriptors
     * and a bunch of other stuff?</p>
     *
     * @return the set of properties for this DynaClass
     */
    @Override
    public DynaProperty[] getDynaProperties() {

        return properties;

    }


    /**
     * <p>Instantiates a new standard JavaBean instance associated with
     * this DynaClass and return it wrapped in a new WrapDynaBean
     * instance. <strong>NOTE</strong> the JavaBean should have a
     * no argument constructor.</p>
     *
     * <p><strong>NOTE</strong> - Most common use cases should not need to use
     * this method. It is usually better to create new
     * {@code WrapDynaBean} instances by calling its constructor.
     * For example:</p>
     * <pre><code>
     *   Object javaBean = ...;
     *   DynaBean wrapper = new WrapDynaBean(javaBean);
     * </code></pre>
     * <p>
     * (This method is needed for some kinds of {@code DynaBean} framework.)
     * </p>
     *
     * @return A new {@code DynaBean} instance
     * @throws IllegalAccessException if the Class or the appropriate
     *  constructor is not accessible
     * @throws InstantiationException if this Class represents an abstract
     *  class, an array class, a primitive type, or void; or if instantiation
     *  fails for some other reason
     */
    @Override
    public DynaBean newInstance()
            throws IllegalAccessException, InstantiationException {

        return new WrapDynaBean(getBeanClass().newInstance());

    }





    /**
     * Return the PropertyDescriptor for the specified property name, if any;
     * otherwise return {@code null}.
     *
     * @param name Name of the property to be retrieved
     * @return The descriptor for the specified property
     */
    public PropertyDescriptor getPropertyDescriptor(final String name) {

        return descriptorsMap.get(name);

    }





    /**
     * Clear our cache of WrapDynaClass instances.
     */
    public static void clear() {

        getClassesCache().clear();

    }


    /**
     * Create (if necessary) and return a new {@code WrapDynaClass}
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

        final PropertyUtilsBean propUtils = pu != null ? pu : PropertyUtilsBean.getInstance();
        final CacheKey key = new CacheKey(beanClass, propUtils);
        WrapDynaClass dynaClass = getClassesCache().get(key);
        if (dynaClass == null) {
            dynaClass = new WrapDynaClass(beanClass, propUtils);
            getClassesCache().put(key, dynaClass);
        }
        return dynaClass;

    }




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
        Map<?, ?> mappeds =
                PropertyUtils.getMappedPropertyDescriptors(beanClass);
        if (mappeds == null) {
            mappeds = new HashMap<>();
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
        for (final Map.Entry<?, ?> entry : mappeds.entrySet()) {
            final String name = (String) entry.getKey();
            final PropertyDescriptor descriptor =
                    (PropertyDescriptor) entry.getValue();
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
