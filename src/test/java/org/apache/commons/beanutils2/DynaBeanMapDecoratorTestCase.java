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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the {@code DynaBeanMapDecorator} implementation class.
 * </p>
 */
@SuppressWarnings("deprecation")
public class DynaBeanMapDecoratorTestCase {

    private static final DynaProperty stringProp = new DynaProperty("stringProp", String.class);
    private static final DynaProperty nullProp = new DynaProperty("nullProp", String.class);
    private static final DynaProperty intProp = new DynaProperty("intProp", Integer.class);
    private static final DynaProperty dateProp = new DynaProperty("dateProp", Date.class);
    private static final DynaProperty mapProp = new DynaProperty("mapProp", Map.class);
    private static final DynaProperty[] properties = { stringProp, nullProp, intProp, dateProp, mapProp };
    private static final DynaClass dynaClass = new BasicDynaClass("testDynaClass", BasicDynaBean.class, properties);

    private static final String stringVal = "somevalue";
    private static final Integer intVal = Integer.valueOf(5);
    private static final Date dateVal = new Date();
    private static final Map<String, Object> emptyMap = new DynaBeanPropertyMapDecorator(new BasicDynaBean(new BasicDynaClass()));

    private final Map<Object, Object> mapVal = new HashMap<>();
    private final Object[] values = { stringVal, null, intVal, dateVal, mapVal };
    private BasicDynaBean dynaBean;

    private Map<String, Object> decoratedMap;

    private Map<String, Object> modifiableMap;

    /**
     * Check that a Collection is not modifiable
     */
    private <E> void checkUnmodifiable(final String desc, final Collection<E> collection, final E addElem) {
        // Check can't add()
        assertThrows(UnsupportedOperationException.class, () -> collection.add(addElem));
        // Check can't addAll()
        final List<E> list = new ArrayList<>(1);
        list.add(addElem);
        assertThrows(UnsupportedOperationException.class, () -> collection.addAll(list));
        // Check can't clear()
        assertThrows(UnsupportedOperationException.class, () -> collection.clear());
        // Check can't remove()
        assertThrows(UnsupportedOperationException.class, () -> collection.remove("abc"));
        // Check can't removeAll()
        assertThrows(UnsupportedOperationException.class, () -> collection.removeAll(list));
        // Check can't retainAll()
        assertThrows(UnsupportedOperationException.class, () -> collection.retainAll(list));
    }

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() throws Exception {

        mapVal.clear();
        mapVal.put("key1", "key1Value");
        mapVal.put("key2", "key2Value");

        // Initialize DynaBean and properties
        dynaBean = new BasicDynaBean(dynaClass);
        for (int i = 0; i < properties.length; i++) {
            dynaBean.set(properties[i].getName(), values[i]);
        }

        // Create decorated Maps
        decoratedMap = new DynaBeanPropertyMapDecorator(dynaBean);
        modifiableMap = new DynaBeanPropertyMapDecorator(dynaBean, false);

    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        dynaBean = null;
        decoratedMap = null;
        modifiableMap = null;
    }

    /**
     * Test clear() method
     */
    @Test
    public void testClear() {
        assertThrows(UnsupportedOperationException.class, () -> decoratedMap.clear());
        assertThrows(UnsupportedOperationException.class, () -> modifiableMap.clear());
    }

    /**
     * Test containsKey() method
     */
    @Test
    public void testContainsKey() {
        assertTrue(decoratedMap.containsKey(stringProp.getName()), "decoratedMap true");
        assertFalse(decoratedMap.containsKey("xyz"), "decoratedMap false");
    }

    /**
     * Test containsValue() method
     */
    @Test
    public void testContainsValue() {
        assertTrue(decoratedMap.containsValue(stringVal), "decoratedMap true");
        assertFalse(decoratedMap.containsValue("xyz"), "decoratedMap false");
    }

    /**
     * Test entrySet() method
     */
    @Test
    public void testEntrySet() {
        final Set<Map.Entry<String, Object>> set = modifiableMap.entrySet();

        // Check the Set can't be modified
        final Map<String, Object> m = new HashMap<>();
        m.put("key", "value");
        checkUnmodifiable("entrySet()", set, m.entrySet().iterator().next());

        assertEquals(properties.length, set.size(), "entrySet size");

        final List<String> namesList = new ArrayList<>();
        int i = 0;
        for (final Entry<String, Object> entry : set) {
            final String name = entry.getKey();
            namesList.add(name);
            final Object expectValue = decoratedMap.get(name);
            assertEquals(expectValue, entry.getValue(), "entrySet(" + i + ") val");
            i++;
        }
        for (int j = 0; j < properties.length; j++) {
            final String name = properties[j].getName();
            assertTrue(namesList.contains(name), "Check property[" + j + "]");
        }
    }

    /**
     * Test get() method
     */
    @Test
    public void testGet() {
        // valid property name
        assertEquals(stringVal, decoratedMap.get(stringProp.getName()), "decoratedMap valid");
        // invalid property name
        assertThrows(IllegalArgumentException.class, () -> decoratedMap.get("xyz"));
    }

    /**
     * Test isEmpty() method
     */
    @Test
    public void testIsEmpty() {
        assertTrue(emptyMap.isEmpty(), "Empty");
        assertFalse(decoratedMap.isEmpty(), "Not Empty");
    }

    /**
     * Test isReadOnly() method
     */
    @Test
    public void testIsReadOnly() {
        assertTrue(((DynaBeanPropertyMapDecorator) decoratedMap).isReadOnly(), "decoratedMap true");
        assertFalse(((DynaBeanPropertyMapDecorator) modifiableMap).isReadOnly(), "modifiableMap false");
    }

    /**
     * Test keySet() method
     */
    @Test
    public void testKeySet() {
        final Set<String> set = modifiableMap.keySet();

        // Check the Set can't be modified
        checkUnmodifiable("keySet()", set, "xyz");

        assertEquals(properties.length, set.size(), "keySet size");

        for (int i = 0; i < properties.length; i++) {
            final String name = properties[i].getName();
            assertTrue(set.contains(name), "Check property[" + i + "]");
        }
    }

    /**
     * Test put() method
     */
    @Test
    public void testPut() {
        final String newValue = "ABC";
        // Test read only
        assertThrows(UnsupportedOperationException.class, () -> decoratedMap.put(stringProp.getName(), newValue));
        // Test Writable
        assertEquals(stringVal, modifiableMap.put(stringProp.getName(), newValue), "modifiableMap put");
        assertEquals(newValue, dynaBean.get(stringProp.getName()), "dynaBean get");
        assertEquals(newValue, modifiableMap.get(stringProp.getName()), "modifiableMap get");
    }

    /**
     * Test putAll() method
     */
    @Test
    public void testPutAll() {
        final String newValue = "ABC";
        final Map<String, Object> newMap = new HashMap<>();
        newMap.put(stringProp.getName(), newValue);
        // Test read only
        assertThrows(UnsupportedOperationException.class, () -> decoratedMap.putAll(newMap));
        // Test Writable
        assertEquals(stringVal, dynaBean.get(stringProp.getName()), "before putAll");
        modifiableMap.putAll(newMap);
        assertEquals(newValue, dynaBean.get(stringProp.getName()), "after putAll");
    }

    /**
     * Test remove() method
     */
    @Test
    public void testRemove() {
        assertThrows(UnsupportedOperationException.class, () -> decoratedMap.remove(stringProp.getName()));
        assertThrows(UnsupportedOperationException.class, () -> modifiableMap.remove(stringProp.getName()));
    }

    /**
     * Test size() method
     */
    @Test
    public void testSize() {
        assertEquals(0, emptyMap.size(), "Empty");
        assertEquals(properties.length, decoratedMap.size(), "Not Empty");
    }

    /**
     * Test values() method
     */
    @Test
    public void testValues() {
        final Collection<Object> collection = modifiableMap.values();

        // Check the Collection can't be modified
        checkUnmodifiable("values()", collection, "xyz");

        assertEquals(values.length, collection.size(), "values size");

        // Collection should be ordered in same sequence as properties
        final Iterator<Object> iterator = collection.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            assertEquals(values[i], iterator.next(), "values(" + i + ")");
            i++;
        }
    }
}
