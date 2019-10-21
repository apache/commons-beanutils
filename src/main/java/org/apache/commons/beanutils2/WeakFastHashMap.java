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

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * <p>A customized implementation of <code>java.util.HashMap</code> designed
 * to operate in a multi-threaded environment where the large majority of
 * method calls are read-only, instead of structural changes.  When operating
 * in "fast" mode, read calls are non-synchronized and write calls perform the
 * following steps:</p>
 * <ul>
 * <li>Clone the existing collection
 * <li>Perform the modification on the clone
 * <li>Replace the existing collection with the (modified) clone
 * </ul>
 * <p>When first created, objects of this class default to "slow" mode, where
 * all accesses of any type are synchronized but no cloning takes place.  This
 * is appropriate for initially populating the collection, followed by a switch
 * to "fast" mode (by calling <code>setFast(true)</code>) after initialization
 * is complete.</p>
 *
 * <p><strong>NOTE</strong>: If you are creating and accessing a
 * <code>HashMap</code> only within a single thread, you should use
 * <code>java.util.HashMap</code> directly (with no synchronization), for
 * maximum performance.</p>
 *
 * <p><strong>NOTE</strong>: <i>This class is not cross-platform.
 * Using it may cause unexpected failures on some architectures.</i>
 * It suffers from the same problems as the double-checked locking idiom.
 * In particular, the instruction that clones the internal collection and the
 * instruction that sets the internal reference to the clone can be executed
 * or perceived out-of-order.  This means that any read operation might fail
 * unexpectedly, as it may be reading the state of the internal collection
 * before the internal collection is fully formed.
 * For more information on the double-checked locking idiom, see the
 * <a href="http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html">
 * Double-Checked Locking Idiom Is Broken Declaration</a>.</p>
 *
 * @since Commons Collections 1.0
 */
public class WeakFastHashMap<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = 1L;

    /**
     * The underlying map we are managing.
     */
    private Map<K, V> map = null;

    /**
     * Are we currently operating in "fast" mode?
     */
    private boolean fast = false;

    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct an empty map.
     */
    public WeakFastHashMap() {
        super();
        this.map = createMap();
    }

    /**
     * Construct an empty map with the specified capacity.
     *
     * @param capacity  the initial capacity of the empty map
     */
    public WeakFastHashMap(final int capacity) {
        super();
        this.map = createMap(capacity);
    }

    /**
     * Construct an empty map with the specified capacity and load factor.
     *
     * @param capacity  the initial capacity of the empty map
     * @param factor  the load factor of the new map
     */
    public WeakFastHashMap(final int capacity, final float factor) {
        super();
        this.map = createMap(capacity, factor);
    }

    /**
     * Construct a new map with the same mappings as the specified map.
     *
     * @param map  the map whose mappings are to be copied
     */
    public WeakFastHashMap(final Map<? extends K, ? extends V> map) {
        super();
        this.map = createMap(map);
    }


    // Property access
    // ----------------------------------------------------------------------

    /**
     *  Returns true if this map is operating in fast mode.
     *
     *  @return true if this map is operating in fast mode
     */
    public boolean getFast() {
        return this.fast;
    }

    /**
     *  Sets whether this map is operating in fast mode.
     *
     *  @param fast true if this map should operate in fast mode
     */
    public void setFast(final boolean fast) {
        this.fast = fast;
    }


    // Map access
    // ----------------------------------------------------------------------
    // These methods can forward straight to the wrapped Map in 'fast' mode.
    // (because they are query methods)

    /**
     * Return the value to which this map maps the specified key.  Returns
     * <code>null</code> if the map contains no mapping for this key, or if
     * there is a mapping with a value of <code>null</code>.  Use the
     * <code>containsKey()</code> method to disambiguate these cases.
     *
     * @param key  the key whose value is to be returned
     * @return the value mapped to that key, or null
     */
    @Override
    public V get(final Object key) {
        if (fast) {
            return map.get(key);
        }
        synchronized (map) {
            return map.get(key);
        }
    }

    /**
     * Return the number of key-value mappings in this map.
     *
     * @return the current size of the map
     */
    @Override
    public int size() {
        if (fast) {
            return map.size();
        }
        synchronized (map) {
            return map.size();
        }
    }

    /**
     * Return <code>true</code> if this map contains no mappings.
     *
     * @return is the map currently empty
     */
    @Override
    public boolean isEmpty() {
        if (fast) {
            return map.isEmpty();
        }
        synchronized (map) {
            return map.isEmpty();
        }
    }

    /**
     * Return <code>true</code> if this map contains a mapping for the
     * specified key.
     *
     * @param key  the key to be searched for
     * @return true if the map contains the key
     */
    @Override
    public boolean containsKey(final Object key) {
        if (fast) {
            return map.containsKey(key);
        }
        synchronized (map) {
            return map.containsKey(key);
        }
    }

    /**
     * Return <code>true</code> if this map contains one or more keys mapping
     * to the specified value.
     *
     * @param value  the value to be searched for
     * @return true if the map contains the value
     */
    @Override
    public boolean containsValue(final Object value) {
        if (fast) {
            return map.containsValue(value);
        }
        synchronized (map) {
            return map.containsValue(value);
        }
    }

    // Map modification
    // ----------------------------------------------------------------------
    // These methods perform special behavior in 'fast' mode.
    // The map is cloned, updated and then assigned back.
    // See the comments at the top as to why this won't always work.

    /**
     * Associate the specified value with the specified key in this map.
     * If the map previously contained a mapping for this key, the old
     * value is replaced and returned.
     *
     * @param key  the key with which the value is to be associated
     * @param value  the value to be associated with this key
     * @return the value previously mapped to the key, or null
     */
    @Override
    public V put(final K key, final V value) {
        if (fast) {
            synchronized (this) {
                final Map<K, V> temp = cloneMap(map);
                final V result = temp.put(key, value);
                map = temp;
                return result;
            }
        }
        synchronized (map) {
            return map.put(key, value);
        }
    }

    /**
     * Copy all of the mappings from the specified map to this one, replacing
     * any mappings with the same keys.
     *
     * @param in  the map whose mappings are to be copied
     */
    @Override
    public void putAll(final Map<? extends K, ? extends V> in) {
        if (fast) {
            synchronized (this) {
                final Map<K, V> temp =  cloneMap(map);
                temp.putAll(in);
                map = temp;
            }
        } else {
            synchronized (map) {
                map.putAll(in);
            }
        }
    }

    /**
     * Remove any mapping for this key, and return any previously
     * mapped value.
     *
     * @param key  the key whose mapping is to be removed
     * @return the value removed, or null
     */
    @Override
    public V remove(final Object key) {
        if (fast) {
            synchronized (this) {
                final Map<K, V> temp = cloneMap(map);
                final V result = temp.remove(key);
                map = temp;
                return result;
            }
        }
        synchronized (map) {
            return map.remove(key);
        }
    }

    /**
     * Remove all mappings from this map.
     */
    @Override
    public void clear() {
        if (fast) {
            synchronized (this) {
                map = createMap();
            }
        } else {
            synchronized (map) {
                map.clear();
            }
        }
    }

    // Basic object methods
    // ----------------------------------------------------------------------

    /**
     * Compare the specified object with this list for equality.  This
     * implementation uses exactly the code that is used to define the
     * list equals function in the documentation for the
     * <code>Map.equals</code> method.
     *
     * @param o  the object to be compared to this list
     * @return true if the two maps are equal
     */
    @Override
    public boolean equals(final Object o) {
        // Simple tests that require no synchronization
        if (o == this) {
            return true;
        } else if (!(o instanceof Map)) {
            return false;
        }
        final Map<?, ?> mo = (Map<?, ?>) o;

        // Compare the two maps for equality
        if (fast) {
            if (mo.size() != map.size()) {
                return false;
            }
            for (final Map.Entry<K, V> e : map.entrySet()) {
                final K key = e.getKey();
                final V value = e.getValue();
                if (value == null) {
                    if (!(mo.get(key) == null && mo.containsKey(key))) {
                        return false;
                    }
                } else {
                    if (!value.equals(mo.get(key))) {
                        return false;
                    }
                }
            }
            return true;

        }
        synchronized (map) {
            if (mo.size() != map.size()) {
                return false;
            }
            for (final Map.Entry<K, V> e : map.entrySet()) {
                final K key = e.getKey();
                final V value = e.getValue();
                if (value == null) {
                    if (!(mo.get(key) == null && mo.containsKey(key))) {
                        return false;
                    }
                } else {
                    if (!value.equals(mo.get(key))) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    /**
     * Return the hash code value for this map.  This implementation uses
     * exactly the code that is used to define the list hash function in the
     * documentation for the <code>Map.hashCode</code> method.
     *
     * @return suitable integer hash code
     */
    @Override
    public int hashCode() {
        if (fast) {
            int h = 0;
            for (final Map.Entry<K, V> e : map.entrySet()) {
                h += e.hashCode();
            }
            return h;
        }
        synchronized (map) {
            int h = 0;
            for (final Map.Entry<K, V> e : map.entrySet()) {
                h += e.hashCode();
            }
            return h;
        }
    }

    /**
     * Return a shallow copy of this <code>FastHashMap</code> instance.
     * The keys and values themselves are not copied.
     *
     * @return a clone of this map
     */
    @Override
    public Object clone() {
        WeakFastHashMap<K, V> results = null;
        if (fast) {
            results = new WeakFastHashMap<>(map);
        } else {
            synchronized (map) {
                results = new WeakFastHashMap<>(map);
            }
        }
        results.setFast(getFast());
        return results;
    }

    // Map views
    // ----------------------------------------------------------------------

    /**
     * Return a collection view of the mappings contained in this map.  Each
     * element in the returned collection is a <code>Map.Entry</code>.
     * @return the set of map Map entries
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    /**
     * Return a set view of the keys contained in this map.
     * @return the set of the Map's keys
     */
    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    /**
     * Return a collection view of the values contained in this map.
     * @return the set of the Map's values
     */
    @Override
    public Collection<V> values() {
        return new Values();
    }

    // Abstractions on Map creations (for subclasses such as WeakFastHashMap)
    // ----------------------------------------------------------------------

    protected Map<K, V> createMap() {
        return new WeakHashMap<>();
    }

    protected Map<K, V> createMap(final int capacity) {
        return new WeakHashMap<>(capacity);
    }

    protected Map<K, V> createMap(final int capacity, final float factor) {
        return new WeakHashMap<>(capacity, factor);
    }

    protected Map<K, V> createMap(final Map<? extends K, ? extends V> map) {
        return new WeakHashMap<>(map);
    }

    protected Map<K, V> cloneMap(final Map<? extends K, ? extends V> map) {
        return createMap(map);
    }

    // Map view inner classes
    // ----------------------------------------------------------------------

    /**
     * Abstract collection implementation shared by keySet(), values() and entrySet().
     *
     * @param <E> the element type
     */
    private abstract class CollectionView<E> implements Collection<E> {

        public CollectionView() {
        }

        protected abstract Collection<E> get(Map<K, V> map);
        protected abstract E iteratorNext(Map.Entry<K, V> entry);


        @Override
        public void clear() {
            if (fast) {
                synchronized (WeakFastHashMap.this) {
                    map = createMap();
                }
            } else {
                synchronized (map) {
                    get(map).clear();
                }
            }
        }

        @Override
        public boolean remove(final Object o) {
            if (fast) {
                synchronized (WeakFastHashMap.this) {
                    final Map<K, V> temp = cloneMap(map);
                    final boolean r = get(temp).remove(o);
                    map = temp;
                    return r;
                }
            }
            synchronized (map) {
                return get(map).remove(o);
            }
        }

        @Override
        public boolean removeAll(final Collection<?> o) {
            if (fast) {
                synchronized (WeakFastHashMap.this) {
                    final Map<K, V> temp = cloneMap(map);
                    final boolean r = get(temp).removeAll(o);
                    map = temp;
                    return r;
                }
            }
            synchronized (map) {
                return get(map).removeAll(o);
            }
        }

        @Override
        public boolean retainAll(final Collection<?> o) {
            if (fast) {
                synchronized (WeakFastHashMap.this) {
                    final Map<K, V> temp = cloneMap(map);
                    final boolean r = get(temp).retainAll(o);
                    map = temp;
                    return r;
                }
            }
            synchronized (map) {
                return get(map).retainAll(o);
            }
        }

        @Override
        public int size() {
            if (fast) {
                return get(map).size();
            }
            synchronized (map) {
                return get(map).size();
            }
        }


        @Override
        public boolean isEmpty() {
            if (fast) {
                return get(map).isEmpty();
            }
            synchronized (map) {
                return get(map).isEmpty();
            }
        }

        @Override
        public boolean contains(final Object o) {
            if (fast) {
                return get(map).contains(o);
            }
            synchronized (map) {
                return get(map).contains(o);
            }
        }

        @Override
        public boolean containsAll(final Collection<?> o) {
            if (fast) {
                return get(map).containsAll(o);
            }
            synchronized (map) {
                return get(map).containsAll(o);
            }
        }

        @Override
        public <T> T[] toArray(final T[] o) {
            if (fast) {
                return get(map).toArray(o);
            }
            synchronized (map) {
                return get(map).toArray(o);
            }
        }

        @Override
        public Object[] toArray() {
            if (fast) {
                return get(map).toArray();
            }
            synchronized (map) {
                return get(map).toArray();
            }
        }


        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (fast) {
                return get(map).equals(o);
            }
            synchronized (map) {
                return get(map).equals(o);
            }
        }

        @Override
        public int hashCode() {
            if (fast) {
                return get(map).hashCode();
            }
            synchronized (map) {
                return get(map).hashCode();
            }
        }

        @Override
        public boolean add(final E o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(final Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<E> iterator() {
            return new CollectionViewIterator();
        }

        private class CollectionViewIterator implements Iterator<E> {

            private Map<K, V> expected;
            private Map.Entry<K, V> lastReturned = null;
            private final Iterator<Map.Entry<K, V>> iterator;

            public CollectionViewIterator() {
                this.expected = map;
                this.iterator = expected.entrySet().iterator();
            }

            @Override
            public boolean hasNext() {
                if (expected != map) {
                    throw new ConcurrentModificationException();
                }
                return iterator.hasNext();
            }

            @Override
            public E next() {
                if (expected != map) {
                    throw new ConcurrentModificationException();
                }
                lastReturned = iterator.next();
                return iteratorNext(lastReturned);
            }

            @Override
            public void remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }
                if (fast) {
                    synchronized (WeakFastHashMap.this) {
                        if (expected != map) {
                            throw new ConcurrentModificationException();
                        }
                        WeakFastHashMap.this.remove(lastReturned.getKey());
                        lastReturned = null;
                        expected = map;
                    }
                } else {
                    iterator.remove();
                    lastReturned = null;
                }
            }
        }
    }

    /**
     * Set implementation over the keys of the FastHashMap
     */
    private class KeySet extends CollectionView<K> implements Set<K> {

        @Override
        protected Collection<K> get(final Map<K, V> map) {
            return map.keySet();
        }

        @Override
        protected K iteratorNext(final Map.Entry<K, V> entry) {
            return entry.getKey();
        }

    }

    /**
     * Collection implementation over the values of the FastHashMap
     */
    private class Values extends CollectionView<V> {

        @Override
        protected Collection<V> get(final Map<K, V> map) {
            return map.values();
        }

        @Override
        protected V iteratorNext(final Map.Entry<K, V> entry) {
            return entry.getValue();
        }
    }

    /**
     * Set implementation over the entries of the FastHashMap
     */
    private class EntrySet extends CollectionView<Map.Entry<K, V>> implements Set<Map.Entry<K, V>> {

        @Override
        protected Collection<Map.Entry<K, V>> get(final Map<K, V> map) {
            return map.entrySet();
        }

        @Override
        protected Map.Entry<K, V> iteratorNext(final Map.Entry<K, V> entry) {
            return entry;
        }

    }

}
