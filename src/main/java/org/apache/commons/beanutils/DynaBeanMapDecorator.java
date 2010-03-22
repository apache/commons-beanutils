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

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;

/**
 * <p>Decorates a {@link DynaBean} to provide <code>Map</code> behaviour.</p>
 *
 * <p>The motivation for this implementation is to provide access to {@link DynaBean}
 *    properties in technologies that are unaware of BeanUtils and {@link DynaBean}s -
 *    such as the expression languages of JSTL and JSF.</p>
 *
 * <p>This can be achieved either by wrapping the {@link DynaBean} prior to
 *    providing it to the technolody to process or by providing a <code>Map</code>
 *    accessor method on the DynaBean implementation:
 *    <pre><code>
 *         public Map getMap() {
 *             return new DynaBeanMapDecorator(this);
 *         }</code></pre>
 *   </ul>
 * </p>
 *
 * <p>This, for example, could be used in JSTL in the following way to access
 *    a DynaBean's <code>fooProperty</code>:
 *    <ul><li><code>${myDynaBean.<b>map</b>.fooProperty}</code></li></ul>
 * </p>
 *
 * <h3>Usage</h3>
 *
 * <p>To decorate a {@link DynaBean} simply instantiate this class with the
 *    target {@link DynaBean}:</p>
 *
 * <ul><li><code>Map fooMap = new DynaBeanMapDecorator(fooDynaBean);</code></li></ul>
 *
 * <p>The above example creates a <b><i>read only</i></b> <code>Map</code>.
 *    To create  a <code>Map</code> which can be modified, construct a
 *    <code>DynaBeanMapDecorator</code> with the <b><i>read only</i></b>
 *    attribute set to <code>false</code>:</p>
 *
 * <ul><li><code>Map fooMap = new DynaBeanMapDecorator(fooDynaBean, false);</code></li></ul>
 *
 * <h3>Limitations</h3>
 * <p>In this implementation the <code>entrySet()</code>, <code>keySet()</code>
 *    and <code>values()</code> methods create an <b><i>unmodifiable</i></b>
 *    <code>Set</code> and it does not support the Map's <code>clear()</code>
 *    and <code>remove()</code> operations.</p>
 *
 * @since BeanUtils 1.8.0
 * @version $Revision$ $Date$
 */
public class DynaBeanMapDecorator implements Map {

    private DynaBean dynaBean;
    private boolean readOnly;
    private transient Set keySet;

    // ------------------- Constructors ----------------------------------

    /**
     * Constructs a  read only Map for the specified
     * {@link DynaBean}.
     *
     * @param dynaBean The dyna bean being decorated
     * @throws IllegalArgumentException if the {@link DynaBean} is null.
     */
    public DynaBeanMapDecorator(DynaBean dynaBean) {
        this(dynaBean, true);
    }

    /**
     * Construct a Map for the specified {@link DynaBean}.
     *
     * @param dynaBean The dyna bean being decorated
     * @param readOnly <code>true</code> if the Mpa is read only
     * otherwise <code>false</code>
     * @throws IllegalArgumentException if the {@link DynaBean} is null.
     */
    public DynaBeanMapDecorator(DynaBean dynaBean, boolean readOnly) {
        if (dynaBean == null) {
            throw new IllegalArgumentException("DynaBean is null");
        }
        this.dynaBean = dynaBean;
        this.readOnly = readOnly;
    }


    // ------------------- public Methods --------------------------------


    /**
     * Indicate whether the Map is read only.
     *
     * @return <code>true</code> if the Map is read only,
     * otherwise <code>false</code>.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    // ------------------- java.util.Map Methods -------------------------

    /**
     * clear() operation is not supported.
     *
     * @throws UnsupportedOperationException
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Indicate whether the {@link DynaBean} contains a specified
     * value for one (or more) of its properties.
     *
     * @param key The {@link DynaBean}'s property name
     * @return <code>true</code> if one of the {@link DynaBean}'s
     * properties contains a specified value.
     */
    public boolean containsKey(Object key) {
        DynaClass dynaClass = getDynaBean().getDynaClass();
        DynaProperty dynaProperty = dynaClass.getDynaProperty(toString(key));
        return (dynaProperty == null ? false : true);
    }

    /**
     * Indicates whether the decorated {@link DynaBean} contains
     * a specified value.
     *
     * @param value The value to check for.
     * @return <code>true</code> if one of the the {@link DynaBean}'s
     * properties contains the specified value, otherwise
     * <code>false</code>.
     */
    public boolean containsValue(Object value) {
        DynaProperty[] properties = getDynaProperties();
        for (int i = 0; i < properties.length; i++) {
            String key = properties[i].getName();
            Object prop = getDynaBean().get(key);
            if (value == null) {
                if (prop == null) {
                    return true;
                }
            } else {
                if (value.equals(prop)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <p>Returns the Set of the property/value mappings
     * in the decorated {@link DynaBean}.</p>
     *
     * <p>Each element in the Set is a <code>Map.Entry</code>
     * type.</p>
     *
     * @return An unmodifiable set of the DynaBean
     * property name/value pairs
     */
    public Set entrySet() {
        DynaProperty[] properties = getDynaProperties();
        Set set = new HashSet(properties.length);
        for (int i = 0; i < properties.length; i++) {
            String key = properties[i].getName();
            Object value = getDynaBean().get(key);
            set.add(new MapEntry(key, value));
        }
        return Collections.unmodifiableSet(set);
    }

    /**
     * Return the value for the specified key from
     * the decorated {@link DynaBean}.
     *
     * @param key The {@link DynaBean}'s property name
     * @return The value for the specified property.
     */
    public Object get(Object key) {
        return getDynaBean().get(toString(key));
    }

    /**
     * Indicate whether the decorated {@link DynaBean} has
     * any properties.
     *
     * @return <code>true</code> if the {@link DynaBean} has
     * no properties, otherwise <code>false</code>.
     */
    public boolean isEmpty() {
        return (getDynaProperties().length == 0);
    }

    /**
     * <p>Returns the Set of the property
     * names in the decorated {@link DynaBean}.</p>
     *
     * <p><b>N.B.</b>For {@link DynaBean}s whose associated {@link DynaClass}
     * is a {@link MutableDynaClass} a new Set is created every
     * time, otherwise the Set is created only once and cached.</p>
     *
     * @return An unmodifiable set of the {@link DynaBean}s
     * property names.
     */
    public Set keySet() {
        if (keySet != null) {
            return keySet;
        }

        // Create a Set of the keys
        DynaProperty[] properties = getDynaProperties();
        Set set = new HashSet(properties.length);
        for (int i = 0; i < properties.length; i++) {
            set.add(properties[i].getName());
        }
        set = Collections.unmodifiableSet(set);

        // Cache the keySet if Not a MutableDynaClass
        DynaClass dynaClass = getDynaBean().getDynaClass();
        if (!(dynaClass instanceof MutableDynaClass)) {
            keySet = set;
        }

        return set;

    }

    /**
     * Set the value for the specified property in
     * the decorated {@link DynaBean}.
     *
     * @param key The {@link DynaBean}'s property name
     * @param value The value for the specified property.
     * @return The previous property's value.
     * @throws UnsupportedOperationException if
     * <code>isReadOnly()</code> is true.
     */
    public Object put(Object key, Object value) {
        if (isReadOnly()) {
            throw new UnsupportedOperationException("Map is read only");
        }
        String property = toString(key);
        Object previous = getDynaBean().get(property);
        getDynaBean().set(property, value);
        return previous;
    }

    /**
     * Copy the contents of a Map to the decorated {@link DynaBean}.
     *
     * @param map The Map of values to copy.
     * @throws UnsupportedOperationException if
     * <code>isReadOnly()</code> is true.
     */
    public void putAll(Map map) {
        if (isReadOnly()) {
            throw new UnsupportedOperationException("Map is read only");
        }
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            put(key, map.get(key));
        }
    }

    /**
     * remove() operation is not supported.
     *
     * @param key The {@link DynaBean}'s property name
     * @return the value removed
     * @throws UnsupportedOperationException
     */
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the number properties in the decorated
     * {@link DynaBean}.
     * @return The number of properties.
     */
    public int size() {
        return getDynaProperties().length;
    }

    /**
     * Returns the set of property values in the
     * decorated {@link DynaBean}.
     *
     * @return Unmodifiable collection of values.
     */
    public Collection values() {
        DynaProperty[] properties = getDynaProperties();
        List values = new ArrayList(properties.length);
        for (int i = 0; i < properties.length; i++) {
            String key = properties[i].getName();
            Object value = getDynaBean().get(key);
            values.add(value);
        }
        return Collections.unmodifiableList(values);
    }

    // ------------------- protected Methods -----------------------------

    /**
     * Provide access to the underlying {@link DynaBean}
     * this Map decorates.
     *
     * @return the decorated {@link DynaBean}.
     */
    public DynaBean getDynaBean() {
        return dynaBean;
    }

    // ------------------- private Methods -------------------------------

    /**
     * Convenience method to retrieve the {@link DynaProperty}s
     * for this {@link DynaClass}.
     *
     * @return The an array of the {@link DynaProperty}s.
     */
    private DynaProperty[] getDynaProperties() {
        return getDynaBean().getDynaClass().getDynaProperties();
    }

    /**
     * Convenience method to convert an Object
     * to a String.
     *
     * @param obj The Object to convert
     * @return String representation of the object
     */
    private String toString(Object obj) {
        return (obj == null ? null : obj.toString());
    }

    /**
     * Map.Entry implementation.
     */
    private static class MapEntry implements Map.Entry {
        private Object key;
        private Object value;
        MapEntry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return ((key.equals(e.getKey())) &&
                    (value == null ? e.getValue() == null
                                   : value.equals(e.getValue())));
        }
        public int hashCode() {
            return key.hashCode() + (value == null ? 0 : value.hashCode());
        }
        public Object getKey() {
            return key;
        }
        public Object getValue() {
            return value;
        }
        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }
    }

}
