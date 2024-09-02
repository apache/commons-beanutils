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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the {@code LazyDynaList}class.
 * </p>
 */
public class LazyDynaListTestCase {

    private static final String BASIC_PROP1 = "BasicDynaClass_Property1";
    private static final String BASIC_PROP2 = "BasicDynaClass_Property2";

    protected DynaProperty[] properties = { new DynaProperty(BASIC_PROP1, String.class), new DynaProperty(BASIC_PROP2, HashMap.class) };
    protected DynaClass treeMapDynaClass = new LazyDynaMap(new TreeMap<>());
    protected DynaClass hashMapDynaClass = new LazyDynaMap(new HashMap<>());

    protected DynaClass pojoDynaClass = new WrapDynaBean(new TestBean()).getDynaClass();

    protected DynaClass basicDynaClass = new BasicDynaClass("test", BasicDynaBean.class, properties);

    /**
     * Test DynaBean Create
     */
    private void dynaBeanTest(final LazyDynaList list, final Class<?> testClass, final DynaClass testDynaClass, final Object wrongBean)
            throws IllegalAccessException, InstantiationException {

        // Test get(index) created correct DynaBean - Second
        Object dynaBean = list.get(1);
        assertNotNull(dynaBean, "1. DynaBean Not Created");
        assertEquals(testClass, dynaBean.getClass(), "2. Wrong Type");

        // Test toArray() creates correct Array - Second
        Object array = list.toArray();
        assertNotNull(array, "3. Array Not Created");
        assertEquals(testClass, array.getClass().getComponentType(), "4. Not DynaBean[]");
        DynaBean[] dynaArray = (DynaBean[]) array;
        assertEquals(2, dynaArray.length, "5. Array Size Wrong");

        // Test get(index) created correct DynaBean - Fourth
        dynaBean = list.get(3);
        assertNotNull(dynaBean, "6. DynaBean Not Created");
        assertEquals(testClass, dynaBean.getClass(), "7. Wrong type");

        // Test toArray() creates correct Array - Fourth
        array = list.toArray();
        assertNotNull(array, "8. Array Not Created");
        assertEquals(testClass, array.getClass().getComponentType(), "9. Not DynaBean[]");
        dynaArray = (DynaBean[]) array;
        assertEquals(4, dynaArray.length, "10. Array Size Wrong");

        // Test fail if different type added
        assertThrows(IllegalArgumentException.class, () -> list.add(2, wrongBean));

        // find a String property to set
        final String testProperty = findStringProperty(testDynaClass);
        assertNotNull(testProperty, "Test Property Not Found");
        dynaArray = list.toDynaBeanArray();
        for (int i = 0; i < dynaArray.length; i++) {
            dynaArray[i].set(testProperty, "orig_pos" + i);
        }

        // Create Collection
        final List<Object> collection = new ArrayList<>();
        collection.add(testDynaClass.newInstance());
        collection.add(testDynaClass.newInstance());
        collection.add(testDynaClass.newInstance());
        final int expectedSize = dynaArray.length + collection.size();
        final String origValue = (String) ((DynaBean) collection.get(0)).get(testProperty);
        ((DynaBean) collection.get(0)).set(testProperty, origValue + "_updated_" + 0);
        ((DynaBean) collection.get(1)).set(testProperty, origValue + "_updated_" + 1);
        ((DynaBean) collection.get(2)).set(testProperty, origValue + "_updated_" + 2);

        // Test Insert - addAll(index, Collection)
        list.addAll(1, collection);
        dynaArray = list.toDynaBeanArray();

        // Check array after insert
        dynaArray = list.toDynaBeanArray();
        assertEquals(expectedSize, dynaArray.length, "11. Array Size Wrong");

        // Check Beans have inserted correctly - by checking the property values
        assertEquals("orig_pos0", dynaArray[0].get(testProperty), "12. Wrong Value");
        assertEquals(origValue + "_updated_" + 0, dynaArray[1].get(testProperty), "13. Wrong Value");
        assertEquals(origValue + "_updated_" + 1, dynaArray[2].get(testProperty), "14. Wrong Value");
        assertEquals(origValue + "_updated_" + 2, dynaArray[3].get(testProperty), "15. Wrong Value");
        assertEquals("orig_pos1", dynaArray[4].get(testProperty), "16. Wrong Value");

        // Test Insert - add(index, Object)
        final DynaBean extraElement = testDynaClass.newInstance();
        extraElement.set(testProperty, "extraOne");
        list.add(2, extraElement);
        dynaArray = list.toDynaBeanArray();
        assertEquals(origValue + "_updated_" + 0, dynaArray[1].get(testProperty), "17. Wrong Value");
        assertEquals("extraOne", dynaArray[2].get(testProperty), "18. Wrong Value");
        assertEquals(origValue + "_updated_" + 1, dynaArray[3].get(testProperty), "19. Wrong Value");
    }

    /**
     * Test Map Create
     */
    private String findStringProperty(final DynaClass dynaClass) {
        final DynaProperty[] properties = dynaClass.getDynaProperties();
        for (final DynaProperty property : properties) {
            if (property.getType() == String.class) {
                return property.getName();
            }
        }
        return null;
    }

    /**
     * Test Map Create
     */
    private void mapTest(final LazyDynaList list, final Class<?> testClass, final Object wrongBean) {

        // Test get(index) created correct DynaBean - First
        Object dynaBean = list.get(0);
        assertNotNull(dynaBean, "1. DynaBean Not Created");
        assertEquals(LazyDynaMap.class, dynaBean.getClass(), "2. Not LazyDynaMap");

        // Test get(index) created correct Map - First
        Object map = ((LazyDynaMap) dynaBean).getMap();
        assertNotNull(map, "3. Map Not Created");
        assertEquals(testClass, map.getClass(), "4. Wrong Map");

        // Test toArray() creates correct Array - First
        Object array = list.toArray();
        assertNotNull(array, "5. Array Not Created");
        assertEquals(testClass, array.getClass().getComponentType(), "6. Not Map[]");
        Map<?, ?>[] mapArray = (Map[]) array;
        assertEquals(1, mapArray.length, "7. Array Size Wrong");

        // Test get(index) created correct DynaBean - Third
        dynaBean = list.get(2);
        assertNotNull(dynaBean, "8. DynaBean Not Created");
        assertEquals(LazyDynaMap.class, dynaBean.getClass(), "9. Not LazyDynaMap");

        // Test get(index) created correct Map - Third
        map = ((LazyDynaMap) dynaBean).getMap();
        assertNotNull(map, "10. Map Not Created");
        assertEquals(testClass, map.getClass(), "11. Wrong Map");

        // Test toArray() creates correct Array - Third
        array = list.toArray();
        assertNotNull(array, "12. Array Not Created");
        assertEquals(testClass, array.getClass().getComponentType(), "13. Not Map[]");
        mapArray = (Map[]) array;
        assertEquals(3, mapArray.length, "14. Array Size Wrong");

        // Test fail if different type added
        assertThrows(IllegalArgumentException.class, () -> list.add(2, wrongBean));

    }

    /**
     * Test Pojo Create
     */
    private void pojoTest(final LazyDynaList list, final Class<?> testClass, final Object wrongBean) {

        // Test get(index) created correct DynaBean - First
        Object dynaBean = list.get(0);
        assertNotNull(dynaBean, "1. DynaBean Not Created");
        assertEquals(WrapDynaBean.class, dynaBean.getClass(), "2. Not WrapDynaBean");

        // Test get(index) created correct POJO - First
        Object pojoBean = ((WrapDynaBean) dynaBean).getInstance();
        assertNotNull(pojoBean, "3. POJO Not Created");
        assertEquals(testClass, pojoBean.getClass(), "4. Not WrapDynaBean");

        // Test toArray() creates correct Array - First
        Object array = list.toArray();
        assertNotNull(array, "5. Array Not Created");
        assertEquals(testClass, array.getClass().getComponentType(), "6. Wrong array");
        Object[] pojoArray = (Object[]) array;
        assertEquals(1, pojoArray.length, "7. Array Size Wrong");

        // Test get(index) created correct DynaBean - Second
        dynaBean = list.get(1);
        assertNotNull(dynaBean, "8. DynaBean Not Created");
        assertEquals(WrapDynaBean.class, dynaBean.getClass(), "9. Not WrapDynaBean");

        // Test get(index) created correct POJO - Second
        pojoBean = ((WrapDynaBean) dynaBean).getInstance();
        assertNotNull(pojoBean, "10. POJO Not Created");
        assertEquals(testClass, pojoBean.getClass(), "11. Not WrapDynaBean");

        // Test toArray() creates correct Array - Second
        array = list.toArray();
        assertNotNull(array, "12. Array Not Created");
        assertEquals(testClass, array.getClass().getComponentType(), "13. Wrong array");
        pojoArray = (Object[]) array;
        assertEquals(2, pojoArray.length, "14. Array Size Wrong");

        // Test fail if different type added
        assertThrows(IllegalArgumentException.class, () -> list.add(2, wrongBean));

    }

    /**
     * Do serialization and deserialization.
     */
    private Object serializeDeserialize(final Object target, final String text) throws IOException, ClassNotFoundException {
        // Serialize the test object
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(target);
            oos.flush();
        }
        // Deserialize the test object
        Object result = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bais)) {
            result = ois.readObject();
        }
        return result;
    }

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() throws Exception {
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test Collection
     */
    public void testCollection(final LazyDynaList list, final Class<?> testClass, final DynaClass testDynaClass, final Object wrongBean) {

        // Create Collection & Array of Maps
        final int size = 5;
        final List<Object> testList = new ArrayList<>(size);
        final TreeMap<?, ?>[] testArray = new TreeMap[size];
        for (int i = 0; i < size; i++) {
            final TreeMap<String, Object> map = new TreeMap<>();
            map.put("prop" + i, "val" + i);
            testArray[i] = map;
            testList.add(testArray[i]);
        }

        // Create LazyArrayList from Collection
        LazyDynaList lazyList = new LazyDynaList(testList);
        assertEquals(size, lazyList.size(), "1. check size");

        DynaBean[] dynaArray = lazyList.toDynaBeanArray();
        TreeMap<?, ?>[] mapArray = (TreeMap[]) lazyList.toArray();

        // Check values
        assertEquals(size, dynaArray.length, "2. check size");
        assertEquals(size, mapArray.length, "3. check size");
        for (int i = 0; i < size; i++) {
            assertEquals("val" + i, dynaArray[i].get("prop" + i), "4." + i + " DynaBean error ");
            assertEquals("val" + i, mapArray[i].get("prop" + i), "5." + i + " Map error ");
        }

        // Create LazyArrayList from Array
        lazyList = new LazyDynaList(testArray);
        assertEquals(size, lazyList.size(), "6. check size");

        dynaArray = lazyList.toDynaBeanArray();
        mapArray = (TreeMap[]) lazyList.toArray();

        // Check values
        assertEquals(size, dynaArray.length, "7. check size");
        assertEquals(size, mapArray.length, "8. check size");
        for (int i = 0; i < size; i++) {
            assertEquals("val" + i, dynaArray[i].get("prop" + i), "9." + i + " DynaBean error ");
            assertEquals("val" + i, mapArray[i].get("prop" + i), "10." + i + " Map error ");
        }

    }

    /**
     * Test DynaBean Create
     */
    @Test
    public void testDynaBeanDynaClass() throws Exception {
        // Create LazyArrayList for DynaBeans
        final LazyDynaList list = new LazyDynaList(basicDynaClass);
        // test
        dynaBeanTest(list, BasicDynaBean.class, basicDynaClass, new BenchBean());
    }

    /**
     * Test DynaBean Create
     */
    @Test
    public void testDynaBeanType() throws Exception {
        // Create LazyArrayList for DynaBeans
        final LazyDynaList list = new LazyDynaList(LazyDynaBean.class);
        final LazyDynaBean bean = new LazyDynaBean();
        bean.set("prop1", "val");
        // test
        dynaBeanTest(list, LazyDynaBean.class, bean.getDynaClass(), new BenchBean());
    }

    /**
     * Test Map Create
     */
    @Test
    public void testMapDynaClass() {

        // Create LazyArrayList for TreeMap's
        final LazyDynaList list = new LazyDynaList(treeMapDynaClass);

        // test
        mapTest(list, TreeMap.class, new BenchBean());

    }

    /**
     * Test Map Create
     */
    @Test
    public void testMapType() {

        // Create LazyArrayList for HashMap's
        final LazyDynaList list = new LazyDynaList(HashMap.class);

        // test
        mapTest(list, HashMap.class, new BenchBean());

    }

    /**
     * Test adding a map to List with no type set.
     */
    @Test
    public void testNullType() {
        final LazyDynaList lazyList = new LazyDynaList();
        lazyList.add(new HashMap<>());
    }

    /**
     * Test Pojo Create
     */
    @Test
    public void testPojoDynaClass() {

        // Create LazyArrayList for POJO's
        final LazyDynaList list = new LazyDynaList(pojoDynaClass);

        // test
        pojoTest(list, TestBean.class, new BenchBean());

    }

    /**
     * Test Pojo Create
     */
    @Test
    public void testPojoType() {

        // Create LazyArrayList for POJO's
        final LazyDynaList list = new LazyDynaList(TestBean.class);

        // test
        pojoTest(list, TestBean.class, new BenchBean());

    }

    /**
     * Test DynaBean serialization.
     */
    @Test
    public void testSerializationDynaBean() throws Exception {

        // Create LazyArrayList for DynaBeans
        LazyDynaList target = new LazyDynaList(basicDynaClass);
        BasicDynaBean bean = (BasicDynaBean) target.get(0);

        // Set a Property
        assertNull(bean.get(BASIC_PROP1), "pre-set check");
        bean.set(BASIC_PROP1, "value1");
        assertEquals("value1", bean.get(BASIC_PROP1), "post-set check");

        // Serialize/Deserialize
        final LazyDynaList result = (LazyDynaList) serializeDeserialize(target, "DynaBean");
        target = null;
        bean = null;

        // Confirm property value
        bean = (BasicDynaBean) result.get(0);
        assertEquals("value1", bean.get(BASIC_PROP1), "post-serialize check");

    }

    /**
     * Test DynaBean serialization.
     */
    @Test
    public void testSerializationLazyDynaBean() throws Exception {

        // Create LazyArrayList for DynaBeans
        LazyDynaList target = new LazyDynaList();
        LazyDynaBean bean = (LazyDynaBean) target.get(0);

        // Set a Property
        assertNull(bean.get(BASIC_PROP1), "pre-set check");
        bean.set(BASIC_PROP1, "value1");
        assertEquals("value1", bean.get(BASIC_PROP1), "post-set check");

        // Serialize/Deserialize
        final LazyDynaList result = (LazyDynaList) serializeDeserialize(target, "DynaBean");
        target = null;
        bean = null;

        // Confirm property value
        bean = (LazyDynaBean) result.get(0);
        assertEquals("value1", bean.get(BASIC_PROP1), "post-serialize check");

    }

    /**
     * Test Map serialization.
     */
    @Test
    public void testSerializationMap() throws Exception {

        // Create LazyArrayList for DynaBeans
        LazyDynaList target = new LazyDynaList(treeMapDynaClass);
        LazyDynaMap bean = (LazyDynaMap) target.get(0);

        // Set a Property
        assertNull(bean.get(BASIC_PROP1), "pre-set check");
        bean.set(BASIC_PROP1, "value1");
        assertEquals("value1", bean.get(BASIC_PROP1), "post-set check");

        // Serialize/Deserialize
        final LazyDynaList result = (LazyDynaList) serializeDeserialize(target, "Map");
        target = null;
        bean = null;

        // Confirm property value
        bean = (LazyDynaMap) result.get(0);
        assertEquals("value1", bean.get(BASIC_PROP1), "post-serialize check");

    }

    /**
     * Test POJO (WrapDynaBean) serialization.
     */
    @Test
    public void testSerializationPojo() throws Exception {

        // Create LazyArrayList for DynaBeans
        LazyDynaList target = new LazyDynaList(pojoDynaClass);
        WrapDynaBean bean = (WrapDynaBean) target.get(0);

        // Set a Property
        assertEquals("This is a string", bean.get("stringProperty"), "pre-set check");
        bean.set("stringProperty", "value1");
        assertEquals("value1", bean.get("stringProperty"), "post-set check");

        // Serialize/Deserialize
        final LazyDynaList result = (LazyDynaList) serializeDeserialize(target, "POJO");
        target = null;
        bean = null;

        // Test BEANUTILS-300
        result.add(null);

        // Confirm property value
        bean = (WrapDynaBean) result.get(0);
        assertEquals("value1", bean.get("stringProperty"), "post-serialize check");

    }

    /**
     * Tests toArray() if the list contains DynaBean objects.
     */
    @Test
    public void testToArrayDynaBeans() {
        final LazyDynaList list = new LazyDynaList(LazyDynaBean.class);
        final LazyDynaBean elem = new LazyDynaBean();
        list.add(elem);
        final LazyDynaBean[] beans = new LazyDynaBean[1];
        assertSame(beans, list.toArray(beans), "Wrong array");
        assertSame(elem, beans[0], "Wrong element");
    }

    /**
     * Tests toArray() if the list contains maps.
     */
    @Test
    public void testToArrayMapType() {
        final LazyDynaList list = new LazyDynaList(HashMap.class);
        final HashMap<String, Object> elem = new HashMap<>();
        list.add(elem);
        final Map<?, ?>[] array = new Map[1];
        assertSame(array, list.toArray(array), "Wrong array");
        assertEquals(elem, array[0], "Wrong element");
    }

    /**
     * Tests toArray() for other bean elements.
     */
    @Test
    public void testToArrayOtherType() {
        final LazyDynaList list = new LazyDynaList(TestBean.class);
        final TestBean elem = new TestBean();
        list.add(elem);
        final TestBean[] array = new TestBean[1];
        assertSame(array, list.toArray(array), "Wrong array");
        assertEquals(elem, array[0], "Wrong element");
    }

    /**
     * Tests toArray() if the array's size does not fit the collection size.
     */
    @Test
    public void testToArrayUnsufficientSize() {
        final LazyDynaList list = new LazyDynaList(LazyDynaBean.class);
        final LazyDynaBean elem = new LazyDynaBean();
        list.add(elem);
        final LazyDynaBean[] array = list.toArray(LazyDynaBean.EMPTY_ARRAY);
        assertEquals(1, array.length, "Wrong array size");
        assertEquals(elem, array[0], "Wrong element");
    }
}
