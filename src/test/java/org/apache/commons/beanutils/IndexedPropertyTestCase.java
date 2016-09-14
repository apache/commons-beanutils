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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.bugs.other.Jira492IndexedListsSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * <p>Test Case for the Indexed Properties.</p>
 *
 * @version $Id$
 */

public class IndexedPropertyTestCase {

    // ---------------------------------------------------- Instance Variables

    /**
     * The test bean for each test.
     */
    private IndexedTestBean bean = null;
    private BeanUtilsBean beanUtilsBean;
    private PropertyUtilsBean propertyUtilsBean;
    private String[] testArray;
    private String[] newArray;
    private List<String> testList;
    private List<Object> newList;
    private ArrayList<Object> arrayList;


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Before
    public void setUp() {

        // BeanUtils
        beanUtilsBean = new BeanUtilsBean();
        propertyUtilsBean = beanUtilsBean.getPropertyUtils();

        // initialize Arrays and Lists
        testArray= new String[] {"array-0", "array-1", "array-2"};
        newArray = new String[]  {"newArray-0", "newArray-1", "newArray-2"};

        testList = new ArrayList<String>();
        testList.add("list-0");
        testList.add("list-1");
        testList.add("list-2");

        newList = new ArrayList<Object>();
        newList.add("newList-0");
        newList.add("newList-1");
        newList.add("newList-2");

        arrayList = new ArrayList<Object>();
        arrayList.add("arrayList-0");
        arrayList.add("arrayList-1");
        arrayList.add("arrayList-2");

        // initialize Test Bean  properties
        bean = new IndexedTestBean();
        bean.setStringArray(testArray);
        bean.setStringList(testList);
        bean.setArrayList(arrayList);
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @After
    public void tearDown() {
        bean = null;
    }


    // ------------------------------------------------ Individual Test Methods

    /**
     * Test IndexedPropertyDescriptor for an Array
     */
    @Test
    public void testArrayIndexedPropertyDescriptor() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
        assertNotNull("No Array Descriptor", descriptor);
        assertEquals("Not IndexedPropertyDescriptor",
                     IndexedPropertyDescriptor.class,
                     descriptor.getClass());
        assertEquals("PropertDescriptor Type invalid",
                     testArray.getClass(),
                     descriptor.getPropertyType());
    }

    /**
     * Test IndexedPropertyDescriptor for a List
     */
    @Test
    public void testListIndexedPropertyDescriptor() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
        assertNotNull("No List Descriptor", descriptor);
        if (Jira492IndexedListsSupport.supportsIndexedLists()) {
            // BEANUTILS-492 - can't assume lists are handled as arrays in Java 8+
            assertEquals("Not IndexedPropertyDescriptor",
                         IndexedPropertyDescriptor.class, descriptor.getClass());
        }
        assertEquals("PropertDescriptor Type invalid",
                     List.class,
                     descriptor.getPropertyType());
    }

    /**
     * Test IndexedPropertyDescriptor for an ArrayList
     */
    @Test
    public void testArrayListIndexedPropertyDescriptor() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "arrayList");
        assertNotNull("No ArrayList Descriptor", descriptor);
        if (Jira492IndexedListsSupport.supportsIndexedLists()) {
            assertEquals("Not IndexedPropertyDescriptor",
                    IndexedPropertyDescriptor.class, descriptor.getClass());
        }
        assertEquals("PropertDescriptor Type invalid",
                     ArrayList.class,
                     descriptor.getPropertyType());
    }

    /**
     * Test Read Method for an Array
     */
    @Test
    public void testArrayReadMethod() throws Exception {
        final PropertyDescriptor descriptor =
             (PropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
        assertNotNull("No Array Read Method", descriptor.getReadMethod());
    }

    /**
     * Test Write Method for an Array
     */
    @Test
    public void testArrayWriteMethod() throws Exception {
        final PropertyDescriptor descriptor =
             (PropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
        assertNotNull("No Array Write Method", descriptor.getWriteMethod());
    }

    /**
     * Test Indexed Read Method for an Array
     */
    @Test
    public void testArrayIndexedReadMethod() throws Exception {
        final IndexedPropertyDescriptor descriptor =
             (IndexedPropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
        assertNotNull("No Array Indexed Read Method", descriptor.getIndexedReadMethod());
    }

    /**
     * Test Indexed Write Method for an Array
     */
    @Test
    public void testArrayIndexedWriteMethod() throws Exception {
        final IndexedPropertyDescriptor descriptor =
             (IndexedPropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
        assertNotNull("No Array Indexed Write Method", descriptor.getIndexedWriteMethod());
    }

    /**
     * Test Read Method for a List
     *
     * JDK 1.3.1_04: Test Passes
     * JDK 1.4.2_05: Test Fails - getter which returns java.util.List not returned
     *                            by IndexedPropertyDescriptor.getReadMethod();
     */
    @Test
    public void testListReadMethod() throws Exception {
        final PropertyDescriptor descriptor =
             (PropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
        assertNotNull("No List Read Method", descriptor.getReadMethod());
    }

    /**
     * Test Write Method for a List
     *
     * JDK 1.3.1_04: Test Passes
     * JDK 1.4.2_05: Test Fails - setter whith java.util.List argument not returned
     *                            by IndexedPropertyDescriptor.getWriteMethod();
     */
    @Test
    public void testListWriteMethod() throws Exception {
        final PropertyDescriptor descriptor =
             (PropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
        assertNotNull("No List Write Method", descriptor.getWriteMethod());
    }

    /**
     * Test Indexed Read Method for a List
     */
    @Test
    public void testListIndexedReadMethod() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
        assertNotNull("stringList descriptor not found", descriptor);
        assumeTrue("JDK does not support index bean properties on java.util.List",
                Jira492IndexedListsSupport.supportsIndexedLists());
        assertNotNull("No List Indexed Read Method",  ((IndexedPropertyDescriptor)descriptor).getIndexedReadMethod());
    }

    /**
     * Test Indexed Write Method for a List
     */
    @Test
    public void testListIndexedWriteMethod() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
        assertNotNull("stringList descriptor not found", descriptor);
        assumeTrue("JDK does not support index bean properties on java.util.List",
                Jira492IndexedListsSupport.supportsIndexedLists());
        assertNotNull("No List Indexed Write Method", ((IndexedPropertyDescriptor)descriptor).getIndexedWriteMethod());
    }

    /**
     * Test Read Method for an ArrayList
     */
    @Test
    public void testArrayListReadMethod() throws Exception {
        final PropertyDescriptor descriptor =
             (PropertyDescriptor)propertyUtilsBean.getPropertyDescriptor(bean, "arrayList");
        assertNotNull("No ArrayList Read Method", descriptor.getReadMethod());
    }

    /**
     * Test Write Method for an ArrayList
     */
    @Test
    public void testArrayListWriteMethod() throws Exception {
        final PropertyDescriptor descriptor =
             propertyUtilsBean.getPropertyDescriptor(bean, "arrayList");
        assertNotNull("No ArrayList Write Method", descriptor.getWriteMethod());
    }

    /**
     * Test getting an array property
     */
    @Test
    public void testGetArray() throws Exception {
        assertEquals(testArray,
                     propertyUtilsBean.getProperty(bean, "stringArray"));
    }

    /**
     * Test getting an array property as a String
     *
     * NOTE: Why does retrieving array just return the first element in the array, whereas
     *       retrieveing a List returns a comma separated list of all the elements?
     */
    @Test
    public void testGetArrayAsString() throws Exception {
        assertEquals("array-0",
                     beanUtilsBean.getProperty(bean, "stringArray"));
    }

    /**
     * Test getting an indexed item of an Array using getProperty("name[x]")
     */
    @Test
    public void testGetArrayItemA() throws Exception {
        assertEquals("array-1",
                     beanUtilsBean.getProperty(bean, "stringArray[1]"));
    }

    /**
     * Test getting an indexed item of an Array using getIndexedProperty("name")
     */
    @Test
    public void testGetArrayItemB() throws Exception {
        assertEquals("array-1",
                     beanUtilsBean.getIndexedProperty(bean, "stringArray", 1));
    }

    /**
     * Test getting a List
     *
     * JDK 1.3.1_04: Test Passes
     * JDK 1.4.2_05: Test Fails - fails NoSuchMethodException, i.e. reason as testListReadMethod()
     *                            failed.
     */
    @Test
    public void testGetList() throws Exception {
        assertEquals(testList,
                     propertyUtilsBean.getProperty(bean, "stringList"));
    }

    /**
     * Test getting a List property as a String
     *
     * JDK 1.3.1_04: Test Passes
     * JDK 1.4.2_05: Test Fails - fails NoSuchMethodException, i.e. reason as testListReadMethod()
     *                            failed.
     */
    @Test
    public void testGetListAsString() throws Exception {
        assertEquals("list-0",
                     beanUtilsBean.getProperty(bean, "stringList"));
    }

    /**
     * Test getting an indexed item of a List using getProperty("name[x]")
     */
    @Test
    public void testGetListItemA() throws Exception {
        assertEquals("list-1",
                     beanUtilsBean.getProperty(bean, "stringList[1]"));
    }

    /**
     * Test getting an indexed item of a List using getIndexedProperty("name")
     */
    @Test
    public void testGetListItemB() throws Exception {
        assertEquals("list-1",
                     beanUtilsBean.getIndexedProperty(bean, "stringList", 1));
    }

    /**
     * Test setting an Array property
     *
     * JDK 1.3.1_04 and 1.4.2_05: Test Fails - IllegalArgumentException can't invoke setter, argument type mismatch
     *
     * Fails because of a bug in BeanUtilsBean.setProperty() method. Value is always converted to the array's component
     * type which in this case is a String. Then it calls the setStringArray(String[]) passing a String rather than
     * String[] causing this exception. If there isn't an "index" value then the PropertyType (rather than
     * IndexedPropertyType) should be used.
     *
     */
    @Test
    public void testSetArray() throws Exception {
        beanUtilsBean.setProperty(bean, "stringArray", newArray);
        final Object value = bean.getStringArray();
        assertEquals("Type is different", newArray.getClass(), value.getClass());
        final String[] array = (String[])value;
        assertEquals("Array Length is different", newArray.length, array.length);
        for (int i = 0; i < array.length; i++) {
            assertEquals("Element " + i + " is different", newArray[i], array[i]);
        }
    }


    /**
     * Test setting an indexed item of an Array using setProperty("name[x]", value)
     */
    @Test
    public void testSetArrayItemA() throws Exception {
        beanUtilsBean.setProperty(bean, "stringArray[1]", "modified-1");
        assertEquals("modified-1", bean.getStringArray(1));
    }

    /**
     * Test setting an indexed item of an Array using setIndexedProperty("name", value)
     */
    @Test
    public void testSetArrayItemB() throws Exception {
        propertyUtilsBean.setIndexedProperty(bean, "stringArray", 1, "modified-1");
        assertEquals("modified-1", bean.getStringArray(1));
    }

    /**
     * Test setting a List property
     *
     * JDK 1.3.1_04: Test Passes
     * JDK 1.4.2_05: Test Fails - setter which returns java.util.List not returned
     *                            by IndexedPropertyDescriptor.getWriteMethod() - therefore
     *                            setProperty does nothing and values remain unchanged.
     */
    @Test
    public void testSetList() throws Exception {
        beanUtilsBean.setProperty(bean, "stringList", newList);
        final Object value = bean.getStringList();
        assertEquals("Type is different", newList.getClass(), value.getClass());
        final List<?> list  = (List<?>)value;
        assertEquals("List size is different", newList.size(), list.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals("Element " + i + " is different", newList.get(i), list.get(i));
        }
    }


    /**
     * Test setting an indexed item of a List using setProperty("name[x]", value)
     */
    @Test
    public void testSetListItemA() throws Exception {
        beanUtilsBean.setProperty(bean, "stringList[1]", "modified-1");
        assertEquals("modified-1", bean.getStringList(1));
    }

    /**
     * Test setting an indexed item of a List using setIndexedProperty("name", value)
     */
    @Test
    public void testSetListItemB() throws Exception {
        propertyUtilsBean.setIndexedProperty(bean, "stringList", 1, "modified-1");
        assertEquals("modified-1", bean.getStringList(1));
    }


    /**
     * Test getting an ArrayList
     */
    @Test
    public void testGetArrayList() throws Exception {
        assertEquals(arrayList,
                     propertyUtilsBean.getProperty(bean, "arrayList"));
    }

    /**
     * Test setting an ArrayList property
     */
    @Test
    public void testSetArrayList() throws Exception {
        beanUtilsBean.setProperty(bean, "arrayList", newList);
        final Object value = bean.getArrayList();
        assertEquals("Type is different", newList.getClass(), value.getClass());
        final List<?> list  = (List<?>)value;
        assertEquals("List size is different", newList.size(), list.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals("Element " + i + " is different", newList.get(i), list.get(i));
        }
    }

}
