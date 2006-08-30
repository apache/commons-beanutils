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


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * <p> Test case for <code>ConstructorUtils</code> </p>
 *
 */
public class ConstructorUtilsTestCase extends TestCase {

    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public ConstructorUtilsTestCase(String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {
        super.setUp();
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(ConstructorUtilsTestCase.class));
    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() throws Exception {
        super.tearDown();
    }


    // ------------------------------------------------ Individual Test Methods

    public void testInvokeConstructor() throws Exception {
        {
            Object obj = ConstructorUtils.invokeConstructor(TestBean.class,"TEST");
            assertNotNull(obj);
            assertTrue(obj instanceof TestBean);
            assertEquals("TEST",((TestBean)obj).getStringProperty());
        }
        {
            Object obj = ConstructorUtils.invokeConstructor(TestBean.class,new Float(17.3f));
            assertNotNull(obj);
            assertTrue(obj instanceof TestBean);
            assertEquals(17.3f,((TestBean)obj).getFloatProperty(),0.0f);
        }
    }

    public void testInvokeConstructorWithArgArray() throws Exception {
        Object[] args = { new Float(17.3f), "TEST" };
        Object obj = ConstructorUtils.invokeConstructor(TestBean.class,args);
        assertNotNull(obj);
        assertTrue(obj instanceof TestBean);
        assertEquals(17.3f,((TestBean)obj).getFloatProperty(),0.0f);
        assertEquals("TEST",((TestBean)obj).getStringProperty());
    }

    public void testInvokeConstructorWithTypeArray() throws Exception {
        {
            Object[] args = { Boolean.TRUE, "TEST" };
            Class[] types = { Boolean.TYPE, String.class };
            Object obj = ConstructorUtils.invokeConstructor(TestBean.class,args,types);
            assertNotNull(obj);
            assertTrue(obj instanceof TestBean);
            assertEquals(true,((TestBean)obj).getBooleanProperty());
            assertEquals("TEST",((TestBean)obj).getStringProperty());
        }
        {
            Object[] args = { Boolean.TRUE, "TEST" };
            Class[] types = { Boolean.class, String.class };
            Object obj = ConstructorUtils.invokeConstructor(TestBean.class,args,types);
            assertNotNull(obj);
            assertTrue(obj instanceof TestBean);
            assertEquals(true,((TestBean)obj).isBooleanSecond());
            assertEquals("TEST",((TestBean)obj).getStringProperty());
        }
    }

    public void testInvokeExactConstructor() throws Exception {
        {
            Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class,"TEST");
            assertNotNull(obj);
            assertTrue(obj instanceof TestBean);
            assertEquals("TEST",((TestBean)obj).getStringProperty());
        }
        {
            try {
                Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class,new Float(17.3f));
                fail("Expected NoSuchMethodException");
            } catch(NoSuchMethodException e) {
                // expected
            }
        }
        {
            Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class,Boolean.TRUE);
            assertNotNull(obj);
            assertTrue(obj instanceof TestBean);
            assertEquals(true,((TestBean)obj).isBooleanSecond());
        }
    }

    public void testInvokeExactConstructorWithArgArray() throws Exception {
        {
            Object[] args = { new Float(17.3f), "TEST" };
            try {
                Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class,args);
                fail("Expected NoSuchMethodException");
            } catch(NoSuchMethodException e) {
                // expected
            }
        }
        {
            Object[] args = { Boolean.TRUE, "TEST" };
            Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class,args);
            assertNotNull(obj);
            assertTrue(obj instanceof TestBean);
            assertEquals(true,((TestBean)obj).isBooleanSecond());
            assertEquals("TEST",((TestBean)obj).getStringProperty());
        }
    }
    
    public void testInvokeExactConstructorWithTypeArray() throws Exception {
        {
            Object[] args = { Boolean.TRUE, "TEST" };
            Class[] types = { Boolean.TYPE, String.class };
            Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class,args,types);
            assertNotNull(obj);
            assertTrue(obj instanceof TestBean);
            assertEquals(true,((TestBean)obj).getBooleanProperty());
            assertEquals("TEST",((TestBean)obj).getStringProperty());
        }
        {
            Object[] args = { Boolean.TRUE, "TEST" };
            Class[] types = { Boolean.class, String.class };
            Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class,args,types);
            assertNotNull(obj);
            assertTrue(obj instanceof TestBean);
            assertEquals(true,((TestBean)obj).isBooleanSecond());
            assertEquals("TEST",((TestBean)obj).getStringProperty());
        }
        {
            Object[] args = { new Float(17.3f), "TEST" };
            Class[] types = { Float.TYPE, String.class };
            Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class,args,types);
            assertNotNull(obj);
            assertTrue(obj instanceof TestBean);
            assertEquals(17.3f,((TestBean)obj).getFloatProperty(),0.0f);
            assertEquals("TEST",((TestBean)obj).getStringProperty());
        }
        {
            Object[] args = { new Float(17.3f), "TEST" };
            Class[] types = { Float.class, String.class };
            try {
                Object obj = ConstructorUtils.invokeExactConstructor(TestBean.class,args,types);
                fail("Expected NoSuchMethodException");
            } catch(NoSuchMethodException e) {
                // expected
            }
        }
    }

    public void testGetAccessibleConstructor() throws Exception {
        {
            Constructor ctor = ConstructorUtils.getAccessibleConstructor(TestBean.class,String.class);       
            assertNotNull(ctor);
            assertTrue(Modifier.isPublic(ctor.getModifiers()));
        }
        {
            Constructor ctor = ConstructorUtils.getAccessibleConstructor(TestBean.class,Integer.class);       
            assertNotNull(ctor);
            assertTrue(Modifier.isPublic(ctor.getModifiers()));
        }
        {
            Constructor ctor = ConstructorUtils.getAccessibleConstructor(TestBean.class,Integer.TYPE);       
            assertNull(ctor);
        }
    }

    public void testGetAccessibleConstructorWithTypeArray() throws Exception {
        {
            Class[] types = { Boolean.TYPE, String.class };
            Constructor ctor = ConstructorUtils.getAccessibleConstructor(TestBean.class,types);       
            assertNotNull(ctor);
            assertTrue(Modifier.isPublic(ctor.getModifiers()));
        }
        {
            Class[] types = { Boolean.TYPE, Boolean.TYPE, String.class };
            Constructor ctor = ConstructorUtils.getAccessibleConstructor(TestBean.class,types);       
            assertNull(ctor);
        }
    }

    public void testGetAccessibleConstructorWithConstructorArg() throws Exception {
        {
            Class[] types = { Integer.class };
            Constructor c1 = TestBean.class.getConstructor(types);
            Constructor ctor = ConstructorUtils.getAccessibleConstructor(c1);       
            assertNotNull(ctor);
            assertTrue(Modifier.isPublic(ctor.getModifiers()));
        }
        {
            Class[] types = { Integer.class };
            Constructor c1 = TestBean.class.getDeclaredConstructor(types);
            Constructor ctor = ConstructorUtils.getAccessibleConstructor(c1);       
            assertNotNull(ctor);
            assertTrue(Modifier.isPublic(ctor.getModifiers()));
        }
        {
            Class[] types = { Integer.TYPE };
            Constructor c1 = TestBean.class.getDeclaredConstructor(types);
            Constructor ctor = ConstructorUtils.getAccessibleConstructor(c1);       
            assertNull(ctor);
        }
    }

}
