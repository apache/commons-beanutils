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

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>Test Case for the <code>LazyDynaList</code>class.</p>
 *
 */
public class LazyDynaListTestCase extends TestCase {

    private static final String BASIC_PROP1 = "BasicDynaClass_Property1";
    private static final String BASIC_PROP2 = "BasicDynaClass_Property2";

    protected DynaProperty[] properties = new DynaProperty[] {
                                               new DynaProperty(BASIC_PROP1, String.class),
                                               new DynaProperty(BASIC_PROP2, HashMap.class)};

    protected DynaClass treeMapDynaClass = new LazyDynaMap(new TreeMap());
    protected DynaClass hashMapDynaClass = new LazyDynaMap(new HashMap());
    protected DynaClass pojoDynaClass = new WrapDynaBean(new TestBean()).getDynaClass();
    protected DynaClass basicDynaClass = new BasicDynaClass("test", BasicDynaBean.class, properties);

    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public LazyDynaListTestCase(String name) {
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
        return (new TestSuite(LazyDynaListTestCase.class));
    }

    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {
    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
    }

    // ------------------------------------------------ Individual Test Methods

    /**
     * Test DynaBean Create
     */
    public void testDynaBeanDynaClass() {

        // Create LazyArrayList for DynaBeans
        LazyDynaList list = new LazyDynaList(basicDynaClass);

        // test
        dynaBeanTest(list, BasicDynaBean.class, basicDynaClass, new BenchBean());
    }

    /**
     * Test DynaBean Create
     */
    public void testDynaBeanType() {

        // Create LazyArrayList for DynaBeans
        LazyDynaList list = new LazyDynaList(LazyDynaBean.class);

   
        LazyDynaBean bean = new LazyDynaBean();
        bean.set("prop1", "val");

        // test
        dynaBeanTest(list, LazyDynaBean.class, bean.getDynaClass(), new BenchBean());
    }

    /**
     * Test Map Create
     */
    public void testMapDynaClass() {

        // Create LazyArrayList for TreeMap's
        LazyDynaList list = new LazyDynaList(treeMapDynaClass);

        // test
        mapTest(list, TreeMap.class, new BenchBean());

    }

    /**
     * Test Map Create
     */
    public void testMapType() {

        // Create LazyArrayList for HashMap's
        LazyDynaList list = new LazyDynaList(HashMap.class);

        // test
        mapTest(list, HashMap.class, new BenchBean());

    }

    /**
     * Test Pojo Create
     */
    public void testPojoDynaClass() {

        // Create LazyArrayList for POJO's
        LazyDynaList list = new LazyDynaList(pojoDynaClass);

        // test
        pojoTest(list, TestBean.class, new BenchBean());

    }

    /**
     * Test Pojo Create
     */
    public void testPojoType() {

        // Create LazyArrayList for POJO's
        LazyDynaList list = new LazyDynaList(TestBean.class);

        // test
        pojoTest(list, TestBean.class, new BenchBean());

    }

    /**
     * Test Collection
     */
    public void testCollection(LazyDynaList list, Class testClass, DynaClass testDynaClass, Object wrongBean) {

        // ----- Create Collection & Array of Maps -----
        int size = 5;
        List testList = new ArrayList(size);
        TreeMap[] testArray = new TreeMap[size];
        for (int i = 0; i < size; i++) {
            testArray[i] = new TreeMap();
            testArray[i].put("prop"+i, "val"+i);
            testList.add(testArray[i]);
        }


        // ----- Create LazyArrayList from Collection -----
        LazyDynaList lazyList = new LazyDynaList(testList);
        assertEquals("1. check size", size, lazyList.size());

        DynaBean[] dynaArray = lazyList.toDynaBeanArray();
        TreeMap[]  mapArray  = (TreeMap[])lazyList.toArray();

        // Check values
        assertEquals("2. check size", size, dynaArray.length);
        assertEquals("3. check size", size, mapArray.length);
        for (int i = 0; i < size; i++) {
            assertEquals("4."+i+" DynaBean error ", "val"+i, dynaArray[i].get("prop"+i));
            assertEquals("5."+i+" Map error ", "val"+i, mapArray[i].get("prop"+i));
        }



        // ----- Create LazyArrayList from Array -----
        lazyList = new LazyDynaList(testArray);
        assertEquals("6. check size", size, lazyList.size());

        dynaArray = lazyList.toDynaBeanArray();
        mapArray  = (TreeMap[])lazyList.toArray();

        // Check values
        assertEquals("7. check size", size, dynaArray.length);
        assertEquals("8. check size", size, mapArray.length);
        for (int i = 0; i < size; i++) {
            assertEquals("9."+i+" DynaBean error ", "val"+i, dynaArray[i].get("prop"+i));
            assertEquals("10."+i+" Map error ", "val"+i, mapArray[i].get("prop"+i));
        }

    }

    /**
     * Test adding a map to List with no type set.
     */
    public void testNullType() {
        LazyDynaList lazyList = new LazyDynaList();
        lazyList.add(new HashMap());
    }

    /**
     * Test DynaBean Create
     */
    private void dynaBeanTest(LazyDynaList list, Class testClass, DynaClass testDynaClass, Object wrongBean) {

        // Test get(index) created correct DynaBean - Second
        Object dynaBean = list.get(1);
        assertNotNull("1. DynaBean Not Created", dynaBean);
        assertEquals("2. Wrong Type", testClass, dynaBean.getClass());

        // Test toArray() creates correct Array - Second
        Object array = list.toArray();
        assertNotNull("3. Array Not Created", array);
        assertEquals("4. Not DynaBean[]", testClass, array.getClass().getComponentType());
        DynaBean[] dynaArray = (DynaBean[])array;
        assertEquals("5. Array Size Wrong", 2, dynaArray.length);

        // Test get(index) created correct DynaBean - Fourth
        dynaBean = list.get(3);
        assertNotNull("6. DynaBean Not Created", dynaBean);
        assertEquals("7. Wrong type", testClass, dynaBean.getClass());

        // Test toArray() creates correct Array - Fourth
        array = list.toArray();
        assertNotNull("8. Array Not Created", array);
        assertEquals("9. Not DynaBean[]", testClass, array.getClass().getComponentType());
        dynaArray = (DynaBean[])array;
        assertEquals("10. Array Size Wrong", 4, dynaArray.length);

        // Test fail if different type added
        try {
            list.add(2, wrongBean);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException ignore) {
            // expected result
        }


        // find a String property to set
        String testProperty = findStringProperty(testDynaClass);
        assertNotNull("Test Property Not Found", testProperty);
        dynaArray = list.toDynaBeanArray();
        for (int i = 0; i < dynaArray.length; i++) {
            dynaArray[i].set(testProperty, "orig_pos"+i);
        }

        // Create Collection
        List collection = new ArrayList();
        try {
            collection.add(testDynaClass.newInstance());
            collection.add(testDynaClass.newInstance());
            collection.add(testDynaClass.newInstance());
        } catch(Exception ex) {
            fail("1. FAILED: " + ex);
        }
        int expectedSize = dynaArray.length + collection.size();
        String origValue = (String)((DynaBean)collection.get(0)).get(testProperty);
        ((DynaBean)collection.get(0)).set(testProperty, origValue+"_updated_"+0);
        ((DynaBean)collection.get(1)).set(testProperty, origValue+"_updated_"+1);
        ((DynaBean)collection.get(2)).set(testProperty, origValue+"_updated_"+2);

        // Test Insert - addAll(index, Collection)
        list.addAll(1, collection);
        dynaArray = list.toDynaBeanArray();

        // Check array after insert
        dynaArray = list.toDynaBeanArray();
        assertEquals("11. Array Size Wrong", expectedSize, dynaArray.length);

        // Check Beans have inserted correctly - by checking the property values
        assertEquals("12. Wrong Value", "orig_pos0",             dynaArray[0].get(testProperty));
        assertEquals("13. Wrong Value", origValue+"_updated_"+0, dynaArray[1].get(testProperty));
        assertEquals("14. Wrong Value", origValue+"_updated_"+1, dynaArray[2].get(testProperty));
        assertEquals("15. Wrong Value", origValue+"_updated_"+2, dynaArray[3].get(testProperty));
        assertEquals("16. Wrong Value", "orig_pos1",             dynaArray[4].get(testProperty));


        // Test Insert - add(index, Object)
        try {
            DynaBean extraElement = testDynaClass.newInstance();
            extraElement.set(testProperty, "extraOne");
            list.add(2, extraElement);
            dynaArray = list.toDynaBeanArray();
            assertEquals("17. Wrong Value", origValue+"_updated_"+0, dynaArray[1].get(testProperty));
            assertEquals("18. Wrong Value", "extraOne",              dynaArray[2].get(testProperty));
            assertEquals("19. Wrong Value", origValue+"_updated_"+1, dynaArray[3].get(testProperty));
        } catch(Exception ex) {
            fail("2. FAILED: " + ex);
        }

    }

    /**
     * Test Map Create
     */
    private String findStringProperty(DynaClass dynaClass) {
        DynaProperty[] properties = dynaClass.getDynaProperties();
        for (int i = 0; i < properties.length; i++) {
            if (properties[i].getType() == String.class) {
                return properties[i].getName();
            }
        }
        return null;
    }



    /**
     * Test Map Create
     */
    private void mapTest(LazyDynaList list, Class testClass, Object wrongBean) {

        // Test get(index) created correct DynaBean - First
        Object dynaBean = list.get(0);
        assertNotNull("1. DynaBean Not Created", dynaBean);
        assertEquals("2. Not LazyDynaMap", LazyDynaMap.class, dynaBean.getClass());

        // Test get(index) created correct Map - First
        Object map = ((LazyDynaMap)dynaBean).getMap();
        assertNotNull("3. Map Not Created", map);
        assertEquals("4. Wrong Map", testClass, map.getClass());

        // Test toArray() creates correct Array - First
        Object array = list.toArray();
        assertNotNull("5. Array Not Created", array);
        assertEquals("6. Not Map[]", testClass, array.getClass().getComponentType());
        Map[] mapArray = (Map[])array;
        assertEquals("7. Array Size Wrong", 1, mapArray.length);

        // Test get(index) created correct DynaBean - Third
        dynaBean = list.get(2);
        assertNotNull("8. DynaBean Not Created", dynaBean);
        assertEquals("9. Not LazyDynaMap", LazyDynaMap.class, dynaBean.getClass());

        // Test get(index) created correct Map - Third
        map = ((LazyDynaMap)dynaBean).getMap();
        assertNotNull("10. Map Not Created", map);
        assertEquals("11. Wrong Map", testClass, map.getClass());

        // Test toArray() creates correct Array - Third
        array = list.toArray();
        assertNotNull("12. Array Not Created", array);
        assertEquals("13. Not Map[]", testClass, array.getClass().getComponentType());
        mapArray = (Map[])array;
        assertEquals("14. Array Size Wrong", 3, mapArray.length);

        // Test fail if different type added
        try {
            list.add(2, wrongBean);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException ignore) {
            // expected result
        }

    }

    /**
     * Test Pojo Create
     */
    private void pojoTest(LazyDynaList list, Class testClass, Object wrongBean) {

        // Test get(index) created correct DynaBean - First
        Object dynaBean = list.get(0);
        assertNotNull("1. DynaBean Not Created", dynaBean);
        assertEquals("2. Not WrapDynaBean", WrapDynaBean.class, dynaBean.getClass());

        // Test get(index) created correct POJO - First
        Object pojoBean = ((WrapDynaBean)dynaBean).getInstance();
        assertNotNull("3. POJO Not Created", pojoBean);
        assertEquals("4. Not WrapDynaBean", testClass, pojoBean.getClass());

        // Test toArray() creates correct Array - First
        Object array = list.toArray();
        assertNotNull("5. Array Not Created", array);
        assertEquals("6. Wrong array", testClass, array.getClass().getComponentType());
        Object[] pojoArray = (Object[])array;
        assertEquals("7. Array Size Wrong", 1, pojoArray.length);

        // Test get(index) created correct DynaBean - Second
        dynaBean = list.get(1);
        assertNotNull("8. DynaBean Not Created", dynaBean);
        assertEquals("9. Not WrapDynaBean", WrapDynaBean.class, dynaBean.getClass());

        // Test get(index) created correct POJO - Second
        pojoBean = ((WrapDynaBean)dynaBean).getInstance();
        assertNotNull("10. POJO Not Created", pojoBean);
        assertEquals("11. Not WrapDynaBean", testClass, pojoBean.getClass());

        // Test toArray() creates correct Array - Second
        array = list.toArray();
        assertNotNull("12. Array Not Created", array);
        assertEquals("13. Wrong array", testClass, array.getClass().getComponentType());
        pojoArray = (Object[])array;
        assertEquals("14. Array Size Wrong", 2, pojoArray.length);

        // Test fail if different type added
        try {
            list.add(2, wrongBean);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException ignore) {
            // expected result
        }

    }

    /**
     * Test DynaBean serialization.
     */
    public void testSerializationDynaBean() {

        // Create LazyArrayList for DynaBeans
        LazyDynaList target = new LazyDynaList(basicDynaClass);
        BasicDynaBean bean = (BasicDynaBean)target.get(0);

        // Set a Property
        assertNull("pre-set check", bean.get(BASIC_PROP1));
        bean.set(BASIC_PROP1, "value1");
        assertEquals("post-set check", "value1", bean.get(BASIC_PROP1));

        // Serialize/Deserialize
        LazyDynaList result = (LazyDynaList)serializeDeserialize(target, "DynaBean");
        target = null;
        bean = null;

        // Confirm property value
        bean = (BasicDynaBean)result.get(0);
        assertEquals("post-serialize check", "value1", bean.get(BASIC_PROP1));

    }

    /**
     * Test DynaBean serialization.
     */
    public void testSerializationLazyDynaBean() {

        // Create LazyArrayList for DynaBeans
        LazyDynaList target = new LazyDynaList();
        LazyDynaBean bean = (LazyDynaBean)target.get(0);

        // Set a Property
        assertNull("pre-set check", bean.get(BASIC_PROP1));
        bean.set(BASIC_PROP1, "value1");
        assertEquals("post-set check", "value1", bean.get(BASIC_PROP1));

        // Serialize/Deserialize
        LazyDynaList result = (LazyDynaList)serializeDeserialize(target, "DynaBean");
        target = null;
        bean = null;

        // Confirm property value
        bean = (LazyDynaBean)result.get(0);
        assertEquals("post-serialize check", "value1", bean.get(BASIC_PROP1));

    }

    /**
     * Test Map serialization.
     */
    public void testSerializationMap() {

        // Create LazyArrayList for DynaBeans
        LazyDynaList target = new LazyDynaList(treeMapDynaClass);
        LazyDynaMap bean = (LazyDynaMap)target.get(0);

        // Set a Property
        assertNull("pre-set check", bean.get(BASIC_PROP1));
        bean.set(BASIC_PROP1, "value1");
        assertEquals("post-set check", "value1", bean.get(BASIC_PROP1));

        // Serialize/Deserialize
        LazyDynaList result = (LazyDynaList)serializeDeserialize(target, "Map");
        target = null;
        bean = null;

        // Confirm property value
        bean = (LazyDynaMap)result.get(0);
        assertEquals("post-serialize check", "value1", bean.get(BASIC_PROP1));

    }

    /**
     * Test POJO (WrapDynaBean) serialization.
     */
    public void testSerializationPojo() {

        // Create LazyArrayList for DynaBeans
        LazyDynaList target = new LazyDynaList(pojoDynaClass);
        WrapDynaBean bean = (WrapDynaBean)target.get(0);

        // Set a Property
        assertEquals("pre-set check", "This is a string", bean.get("stringProperty"));
        bean.set("stringProperty", "value1");
        assertEquals("post-set check", "value1", bean.get("stringProperty"));

        // Serialize/Deserialize
        LazyDynaList result = (LazyDynaList)serializeDeserialize(target, "POJO");
        target = null;
        bean = null;

        // Test BEANUTILS-300
        result.add(null);

        // Confirm property value
        bean = (WrapDynaBean)result.get(0);
        assertEquals("post-serialize check", "value1", bean.get("stringProperty"));

    }

    /**
     * Do serialization and deserialization.
     */
    private Object serializeDeserialize(Object target, String text) {

        // Serialize the test object
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(target);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            fail(text + ": Exception during serialization: " + e);
        }

        // Deserialize the test object
        Object result = null;
        try {
            ByteArrayInputStream bais =
                new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            result = ois.readObject();
            bais.close();
        } catch (Exception e) {
            fail(text + ": Exception during deserialization: " + e);
        }
        return result;

    }

}