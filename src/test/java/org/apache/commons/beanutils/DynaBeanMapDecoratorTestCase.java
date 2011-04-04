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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>Test Case for the <code>DynaBeanMapDecorator</code> implementation class.</p>
 *
 */
public class DynaBeanMapDecoratorTestCase extends TestCase {

    private static final DynaProperty stringProp = new DynaProperty("stringProp", String.class);
    private static final DynaProperty nullProp   = new DynaProperty("nullProp",   String.class);
    private static final DynaProperty intProp    = new DynaProperty("intProp",    Integer.class);
    private static final DynaProperty dateProp   = new DynaProperty("dateProp",   Date.class);
    private static final DynaProperty mapProp    = new DynaProperty("mapProp",    Map.class);
    private static final DynaProperty[] properties = new DynaProperty[] {
                      stringProp, nullProp, intProp, dateProp, mapProp};
    private static final DynaClass dynaClass = new BasicDynaClass("testDynaClass", BasicDynaBean.class, properties);

    private static String  stringVal = "somevalue";
    private static Integer intVal    = new Integer(5);
    private static Date    dateVal   = new Date();
    private Map     mapVal    = new HashMap();

    private Object[] values = new Object[] {stringVal, null, intVal, dateVal, mapVal};

    private BasicDynaBean dynaBean;
    private Map decoratedMap;
    private Map modifiableMap;
    private static final Map emptyMap = new DynaBeanMapDecorator(new BasicDynaBean(new BasicDynaClass()));

    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public DynaBeanMapDecoratorTestCase(String name) {
        super(name);
    }

    // -------------------------------------------------- Overall Test Methods

    /**
     * Run thus Test
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(DynaBeanMapDecoratorTestCase.class));
    }

    /**
     * Set up instance variables required by this test case.
     */
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
        decoratedMap  = new DynaBeanMapDecorator(dynaBean);
        modifiableMap = new DynaBeanMapDecorator(dynaBean, false);

    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
        dynaBean = null;
        decoratedMap = null;
        modifiableMap = null;
    }

    // ------------------------------------------------ Individual Test Methods

    /**
     * Test isReadOnly() method
     */
    public void testIsReadOnly() {
        assertTrue("decoratedMap true",   ((DynaBeanMapDecorator)decoratedMap).isReadOnly());
        assertFalse("modifiableMap false", ((DynaBeanMapDecorator)modifiableMap).isReadOnly());
    }

    /**
     * Test clear() method
     */
    public void testClear() {
        try {
            decoratedMap.clear();
            fail("decoratedMap.clear()");
        } catch(UnsupportedOperationException ignore) {
            // expected result
        }
        try {
            modifiableMap.clear();
            fail("modifiableMap.clear()");
        } catch(UnsupportedOperationException ignore) {
            // expected result
        }
    }

    /**
     * Test containsKey() method
     */
    public void testContainsKey() {
        assertTrue("decoratedMap true",   decoratedMap.containsKey(stringProp.getName()));
        assertFalse("decoratedMap false", decoratedMap.containsKey("xyz"));
    }

    /**
     * Test containsValue() method
     */
    public void testContainsValue() {
        assertTrue("decoratedMap true",   decoratedMap.containsValue(stringVal));
        assertFalse("decoratedMap false", decoratedMap.containsValue("xyz"));
    }

    /**
     * Test entrySet() method
     */
    public void testEntrySet() {
        Set set = modifiableMap.entrySet();

        // Check the Set can't be modified
        checkUnmodifiable("entrySet()", set);

        assertEquals("entrySet size", properties.length, set.size());

        Iterator iterator = set.iterator();
        List namesList = new ArrayList();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String name  = (String)entry.getKey();
            namesList.add(name);
            Object expectValue = decoratedMap.get(name);
            assertEquals("entrySet("+i+") val", expectValue, entry.getValue());
            i++;
        }
        for (int j = 0; j < properties.length; j++) {
            String name = properties[j].getName();
            assertTrue("Check property[" + j + "]", namesList.contains(name));
        }
    }

    /**
     * Test get() method
     */
    public void testGet() {

        // valid property name
        assertEquals("decoratedMap valid", stringVal, decoratedMap.get(stringProp.getName()));

        // invalid property name
        try {
            decoratedMap.get("xyz");
            fail("decoratedMap invalid");
        } catch(IllegalArgumentException ignore) {
            // expected result
        }
    }

    /**
     * Test isEmpty() method
     */
    public void testIsEmpty() {
        assertTrue("Empty",      emptyMap.isEmpty());
        assertFalse("Not Empty", decoratedMap.isEmpty());
    }

    /**
     * Test keySet() method
     */
    public void testKeySet() {
        Set set = modifiableMap.keySet();

        // Check the Set can't be modified
        checkUnmodifiable("keySet()", set);

        assertEquals("keySet size", properties.length, set.size());

        for (int i = 0; i < properties.length; i++) {
            String name = properties[i].getName();
            assertTrue("Check property[" + i + "]", set.contains(name));
        }
    }

    /**
     * Test put() method
     */
    public void testPut() {

        String newValue = "ABC";

        // Test read only
        try {
            decoratedMap.put(stringProp.getName(), newValue);
            fail("Not read only");
        } catch(UnsupportedOperationException ignore) {
            // expected result
        }

        // Test Writable
        assertEquals("modifiableMap put", stringVal, modifiableMap.put(stringProp.getName(), newValue));
        assertEquals("dynaBean get", newValue, dynaBean.get(stringProp.getName()));
        assertEquals("modifiableMap get", newValue, modifiableMap.get(stringProp.getName()));
    }

    /**
     * Test putAll() method
     */
    public void testPutAll() {

        String newValue = "ABC";
        Map newMap = new HashMap();
        newMap.put(stringProp.getName(), newValue);

        // Test read only
        try {
            decoratedMap.putAll(newMap);
            fail("Not read only");
        } catch(UnsupportedOperationException ignore) {
            // expected result
        }

        // Test Writable
        assertEquals("before putAll", stringVal, dynaBean.get(stringProp.getName()));
        modifiableMap.putAll(newMap);
        assertEquals("after putAll",  newValue,  dynaBean.get(stringProp.getName()));
    }

    /**
     * Test remove() method
     */
    public void testRemove() {
        try {
            decoratedMap.remove(stringProp.getName());
            fail("decoratedMap.remove()");
        } catch(UnsupportedOperationException ignore) {
            // expected result
        }
        try {
            modifiableMap.remove(stringProp.getName());
            fail("modifiableMap.remove()");
        } catch(UnsupportedOperationException ignore) {
            // expected result
        }
    }

    /**
     * Test size() method
     */
    public void testSize() {
        assertEquals("Empty", 0, emptyMap.size());
        assertEquals("Not Empty", properties.length, decoratedMap.size());
    }

    /**
     * Test values() method
     */
    public void testValues() {
        Collection collection = modifiableMap.values();

        // Check the Collection can't be modified
        checkUnmodifiable("values()", collection);

        assertEquals("values size", values.length, collection.size());

        // Collection should be ordered in same sequence as properties
        Iterator iterator = collection.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            assertEquals("values("+i+")", values[i], iterator.next());
            i++;
        }
    }

    /**
     * Check that a Collection is not modifiable
     */
    private void checkUnmodifiable(String desc, Collection collection) {
        String testVal = "xyz";

        // Check can't add()
        try {
            collection.add(testVal);
            fail(desc + ".add()");
        } catch(UnsupportedOperationException ignore) {
            // expected result
        }

        // Check can't addAll()
        List list = new ArrayList(1);
        list.add(testVal);
        try {
            collection.addAll(list);
            fail(desc + ".addAll()");
        } catch(UnsupportedOperationException ignore) {
            // expected result
        }

        // Check can't clear()
        try {
            collection.clear();
            fail(desc + ".clear()");
        } catch(UnsupportedOperationException ignore) {
            // expected result
        }

        // Check can't remove()
        try {
            collection.remove("abc");
            fail(desc + ".remove()");
        } catch(UnsupportedOperationException ignore) {
            // expected result
        }

        // Check can't removeAll()
        try {
            collection.removeAll(list);
            fail(desc + ".removeAll()");
        } catch(UnsupportedOperationException ignore) {
            // expected result
        }

        // Check can't retainAll()
        try {
            collection.retainAll(list);
            fail(desc + ".retainAll()");
        } catch(UnsupportedOperationException ignore) {
            // expected result
        }
    }
}