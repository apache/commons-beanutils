/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
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
