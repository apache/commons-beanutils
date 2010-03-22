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
 * @author Craig McClanahan
 * @version $Revision$ $Date$
 */

public class WrapDynaClass implements DynaClass {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new WrapDynaClass for the specified JavaBean class.  This
     * constructor is private; WrapDynaClass instances will be created as
     * needed via calls to the <code>createDynaClass(Class)</code> method.
     *
     * @param beanClass JavaBean class to be introspected around
     */
    private WrapDynaClass(Class beanClass) {

        this.beanClassRef = new SoftReference(beanClass);
        this.beanClassName = beanClass.getName();
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
    private Reference beanClassRef = null;

    /**
     * The JavaBean <code>Class</code> which is represented by this
     * <code>WrapDynaClass</code>.
     *
     * @deprecated No longer initialized, use getBeanClass() method instead
     */
    protected Class beanClass = null;


    /**
     * The set of PropertyDescriptors for this bean class.
     */
    protected PropertyDescriptor[] descriptors = null;


    /**
     * The set of PropertyDescriptors for this bean class, keyed by the
     * property name.  Individual descriptor instances will be the same
     * instances as those in the <code>descriptors</code> list.
     */
    protected HashMap descriptorsMap = new HashMap();


    /**
     * The set of dynamic properties that are part of this DynaClass.
     */
    protected DynaProperty[] properties = null;


    /**
     * The set of dynamic properties that are part of this DynaClass,
     * keyed by the property name.  Individual descriptor instances will
     * be the same instances as those in the <code>properties</code> list.
     */
    protected HashMap propertiesMap = new HashMap();


    // ------------------------------------------------------- Static Variables


    private static final ContextClassLoaderLocal CLASSLOADER_CACHE = 
        new ContextClassLoaderLocal() {
            protected Object initialValue() {
                return new WeakHashMap();
        }
    };

    /**
     * Get the wrap dyna classes cache
     */
    private static Map getDynaClassesMap() {
        return (Map)CLASSLOADER_CACHE.get();
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
    protected static HashMap dynaClasses = new HashMap() {
        public void clear() {
            getDynaClassesMap().clear();
        }
        public boolean containsKey(Object key) {
            return getDynaClassesMap().containsKey(key);
        }
        public boolean containsValue(Object value) {
            return getDynaClassesMap().containsValue(value);
        }
        public Set entrySet() {
            return getDynaClassesMap().entrySet();
        }
        public boolean equals(Object o) {
            return getDynaClassesMap().equals(o);
        }
        public Object get(Object key) {
            return getDynaClassesMap().get(key);
        }
        public int hashCode() {
            return getDynaClassesMap().hashCode();
        }
        public boolean isEmpty() {
            return getDynaClassesMap().isEmpty();
        }
        public Set keySet() {
            return getDynaClassesMap().keySet();
        }
        public Object put(Object key, Object value) {
            return getDynaClassesMap().put(key, value);
        }
        public void putAll(Map m) {
            getDynaClassesMap().putAll(m);
        }
        public Object remove(Object key) {
            return getDynaClassesMap().remove(key);
        }
        public int size() {
            return getDynaClassesMap().size();
        }
        public Collection values() {
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
    protected Class getBeanClass() {
        return (Class)beanClassRef.get();
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
     * @exception IllegalArgumentException if no property name is specified
     */
    public DynaProperty getDynaProperty(String name) {

        if (name == null) {
            throw new IllegalArgumentException
                    ("No property name specified");
        }
        return ((DynaProperty) propertiesMap.get(name));

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
     * @exception IllegalAccessException if the Class or the appropriate
     *  constructor is not accessible
     * @exception InstantiationException if this Class represents an abstract
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
    public PropertyDescriptor getPropertyDescriptor(String name) {

        return ((PropertyDescriptor) descriptorsMap.get(name));

    }


    // --------------------------------------------------------- Static Methods


    /**
     * Clear our cache of WrapDynaClass instances.
     */
    public static void clear() {

        getDynaClassesMap().clear();

    }


    /**
     * Create (if necessary) and return a new <code>WrapDynaClass</code>
     * instance for the specified bean class.
     *
     * @param beanClass Bean class for which a WrapDynaClass is requested
     * @return A new <i>Wrap</i> {@link DynaClass}
     */
    public static WrapDynaClass createDynaClass(Class beanClass) {

            WrapDynaClass dynaClass =
                    (WrapDynaClass) getDynaClassesMap().get(beanClass);
            if (dynaClass == null) {
                dynaClass = new WrapDynaClass(beanClass);
                getDynaClassesMap().put(beanClass, dynaClass);
            }
            return (dynaClass);

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Introspect our bean class to identify the supported properties.
     */
    protected void introspect() {

        // Look up the property descriptors for this bean class
        Class beanClass = getBeanClass();
        PropertyDescriptor[] regulars =
                PropertyUtils.getPropertyDescriptors(beanClass);
        if (regulars == null) {
            regulars = new PropertyDescriptor[0];
        }
        Map mappeds =
                PropertyUtils.getMappedPropertyDescriptors(beanClass);
        if (mappeds == null) {
            mappeds = new HashMap();
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
        Iterator names = mappeds.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            PropertyDescriptor descriptor =
                    (PropertyDescriptor) mappeds.get(name);
            properties[j] =
                    new DynaProperty(descriptor.getName(),
                            Map.class);
            propertiesMap.put(properties[j].getName(),
                    properties[j]);
            j++;
        }

    }


}
