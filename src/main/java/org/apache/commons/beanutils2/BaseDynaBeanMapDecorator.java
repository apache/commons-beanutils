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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>A base class for decorators providing {@code Map} behavior on
 * {@link DynaBean}s.</p>
 *
 * <p>The motivation for this implementation is to provide access to {@link DynaBean}
 *    properties in technologies that are unaware of BeanUtils and {@link DynaBean}s -
 *    such as the expression languages of JSTL and JSF.</p>
 *
 * <p>This rather technical base class implements the methods of the
 *    {@code Map} interface on top of a {@code DynaBean}. It was introduced
 *    to handle generic parameters in a meaningful way without breaking
 *    backwards compatibility of the 1.x {@code DynaBeanMapDecorator} class: A
 *    map wrapping a {@code DynaBean} should be of type {@code Map<String, Object>}.
 *    However, when using these generic parameters in {@code DynaBeanMapDecorator}
 *    this would be an incompatible change (as method signatures would have to
 *    be adapted). To solve this problem, this generic base class is added
 *    which allows specifying the key type as parameter. This makes it easy to
 *    have a new subclass using the correct generic parameters while
 *    {@code DynaBeanMapDecorator} could still remain with compatible
 *    parameters.</p>
 *
 * @param <K> the type of the keys in the decorated map
 * @since BeanUtils 1.9.0
 */
public abstract class BaseDynaBeanMapDecorator<K> implements Map<K, Object> {

    private final DynaBean dynaBean;
    private final boolean readOnly;
    private transient Set<K> keySet;



    /**
     * Constructs a read only Map for the specified
     * {@link DynaBean}.
     *
     * @param dynaBean The dyna bean being decorated
     * @throws IllegalArgumentException if the {@link DynaBean} is null.
     */
    public BaseDynaBeanMapDecorator(final DynaBean dynaBean) {
        this(dynaBean, true);
    }

    /**
     * Construct a Map for the specified {@link DynaBean}.
     *
     * @param dynaBean The dyna bean being decorated
     * @param readOnly {@code true} if the Map is read only
     * otherwise {@code false}
     * @throws IllegalArgumentException if the {@link DynaBean} is null.
     */
    public BaseDynaBeanMapDecorator(final DynaBean dynaBean, final boolean readOnly) {
        if (dynaBean == null) {
            throw new IllegalArgumentException("DynaBean is null");
        }
        this.dynaBean = dynaBean;
        this.readOnly = readOnly;
    }





    /**
     * Indicate whether the Map is read only.
     *
     * @return {@code true} if the Map is read only,
     * otherwise {@code false}.
     */
    public boolean isReadOnly() {
        return readOnly;
    }



    /**
     * clear() operation is not supported.
     *
     * @throws UnsupportedOperationException This operation is not yet supported
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Indicate whether the {@link DynaBean} contains a specified
     * value for one (or more) of its properties.
     *
     * @param key The {@link DynaBean}'s property name
     * @return {@code true} if one of the {@link DynaBean}'s
     * properties contains a specified value.
     */
    @Override
    public boolean containsKey(final Object key) {
        final DynaClass dynaClass = getDynaBean().getDynaClass();
        final DynaProperty dynaProperty = dynaClass.getDynaProperty(toString(key));
        return dynaProperty != null;
    }

    /**
     * Indicates whether the decorated {@link DynaBean} contains
     * a specified value.
     *
     * @param value The value to check for.
     * @return {@code true} if one of the the {@link DynaBean}'s
     * properties contains the specified value, otherwise
     * {@code false}.
     */
    @Override
    public boolean containsValue(final Object value) {
        final DynaProperty[] properties = getDynaProperties();
        for (final DynaProperty property : properties) {
            final String key = property.getName();
            final Object prop = getDynaBean().get(key);
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
     * <p>Each element in the Set is a {@code Map.Entry}
     * type.</p>
     *
     * @return An unmodifiable set of the DynaBean
     * property name/value pairs
     */
    @Override
    public Set<Map.Entry<K, Object>> entrySet() {
        final DynaProperty[] properties = getDynaProperties();
        final Set<Map.Entry<K, Object>> set = new HashSet<>(properties.length);
        for (final DynaProperty property : properties) {
            final K key = convertKey(property.getName());
            final Object value = getDynaBean().get(property.getName());
            set.add(new MapEntry<>(key, value));
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
    @Override
    public Object get(final Object key) {
        return getDynaBean().get(toString(key));
    }

    /**
     * Indicate whether the decorated {@link DynaBean} has
     * any properties.
     *
     * @return {@code true} if the {@link DynaBean} has
     * no properties, otherwise {@code false}.
     */
    @Override
    public boolean isEmpty() {
        return getDynaProperties().length == 0;
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
    @Override
    public Set<K> keySet() {
        if (keySet != null) {
            return keySet;
        }

        // Create a Set of the keys
        final DynaProperty[] properties = getDynaProperties();
        Set<K> set = new HashSet<>(properties.length);
        for (final DynaProperty property : properties) {
            set.add(convertKey(property.getName()));
        }
        set = Collections.unmodifiableSet(set);

        // Cache the keySet if Not a MutableDynaClass
        final DynaClass dynaClass = getDynaBean().getDynaClass();
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
     * {@code isReadOnly()} is true.
     */
    @Override
    public Object put(final K key, final Object value) {
        if (isReadOnly()) {
            throw new UnsupportedOperationException("Map is read only");
        }
        final String property = toString(key);
        final Object previous = getDynaBean().get(property);
        getDynaBean().set(property, value);
        return previous;
    }

    /**
     * Copy the contents of a Map to the decorated {@link DynaBean}.
     *
     * @param map The Map of values to copy.
     * @throws UnsupportedOperationException if
     * {@code isReadOnly()} is true.
     */
    @Override
    public void putAll(final Map<? extends K, ? extends Object> map) {
        if (isReadOnly()) {
            throw new UnsupportedOperationException("Map is read only");
        }
        for (final Map.Entry<? extends K, ?> e : map.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * remove() operation is not supported.
     *
     * @param key The {@link DynaBean}'s property name
     * @return the value removed
     * @throws UnsupportedOperationException This operation is not yet supported
     */
    @Override
    public Object remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the number properties in the decorated
     * {@link DynaBean}.
     * @return The number of properties.
     */
    @Override
    public int size() {
        return getDynaProperties().length;
    }

    /**
     * Returns the set of property values in the
     * decorated {@link DynaBean}.
     *
     * @return Unmodifiable collection of values.
     */
    @Override
    public Collection<Object> values() {
        final DynaProperty[] properties = getDynaProperties();
        final List<Object> values = new ArrayList<>(properties.length);
        for (final DynaProperty property : properties) {
            final String key = property.getName();
            final Object value = getDynaBean().get(key);
            values.add(value);
        }
        return Collections.unmodifiableList(values);
    }



    /**
     * Provide access to the underlying {@link DynaBean}
     * this Map decorates.
     *
     * @return the decorated {@link DynaBean}.
     */
    public DynaBean getDynaBean() {
        return dynaBean;
    }

    /**
     * Converts the name of a property to the key type of this decorator.
     *
     * @param propertyName the name of a property
     * @return the converted key to be used in the decorated map
     */
    protected abstract K convertKey(String propertyName);



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
    private String toString(final Object obj) {
        return obj == null ? null : obj.toString();
    }

    /**
     * Map.Entry implementation.
     */
    private static class MapEntry<K> implements Map.Entry<K, Object> {

        private final K key;
        private final Object value;

        MapEntry(final K key, final Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            return key.equals(e.getKey()) &&
                    (value == null ? e.getValue() == null
                                   : value.equals(e.getValue()));
        }

        @Override
        public int hashCode() {
            return key.hashCode() + (value == null ? 0 : value.hashCode());
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public Object setValue(final Object value) {
            throw new UnsupportedOperationException();
        }
    }

}
