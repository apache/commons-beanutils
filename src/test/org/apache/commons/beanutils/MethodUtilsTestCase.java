/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.beanutils.priv.PrivateBeanFactory;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * <p> Test case for <code>MethodUtils</code> </p>
 *
 */
public class MethodUtilsTestCase extends TestCase {

    // ---------------------------------------------------- Instance Variables

    protected PrivateBeanFactory privateBeanFactory;

    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public MethodUtilsTestCase(String name) {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {
        privateBeanFactory = new PrivateBeanFactory();
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(MethodUtilsTestCase.class));
    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
        privateBeanFactory = null;
    }


    // ------------------------------------------------ Individual Test Methods

    /**
     * <p> Test <code>getAccessibleMethod</code>.
     */
    public void testGetAccessibleMethod() {
        // test MethodUtils.getAccessibleMethod
        // we'll make things easier by using the convenience methods

        // easy bit first - find a public method
        // METHOD ONE
        Method method = MethodUtils.getAccessibleMethod
                (TestBean.class, "setStringProperty", String.class);

        // check that we've found one that matches
        assertNotNull(method);
        assertEquals("method ONE is named correctly",
                "setStringProperty", method.getName());
        assertTrue("Method ONE is public",
                Modifier.isPublic(method.getModifiers()));

        // trickier this one - find a method in a direct interface
        // METHOD TWO
        method = MethodUtils.getAccessibleMethod
                (privateBeanFactory.create().getClass(),
                        "methodBar",
                        String.class);

        // check that we've found one that matches
        assertNotNull(method);
        assertEquals("Method TWO is named correctly",
                "methodBar", method.getName());
        assertTrue("Method TWO is public",
                Modifier.isPublic(method.getModifiers()));

        // trickier this one - find a method in a indirect interface
        // METHOD THREE
        method = MethodUtils.getAccessibleMethod
                (privateBeanFactory.createSubclass().getClass(),
                        "methodBaz",
                        String.class);

        // check that we've found one that matches
        assertNotNull(method);
        assertEquals("Method THREE is named correctly",
                "methodBaz", method.getName());
        assertTrue("Method THREE is public",
                Modifier.isPublic(method.getModifiers()));

    }


    /**
     * <p> Test <code>invokeExactMethod</code>.
     */
    public void testInvokeExactMethod() {
        // test MethodUtils.invokeExactMethod
        // easy bit first - invoke a public method
        // METHOD ONE
        try {

            TestBean bean = new TestBean();
            Object ret = MethodUtils.invokeExactMethod(bean, "setStringProperty", "TEST");
            // check that the return's right and that the properties been set
            assertNull(ret);
            assertEquals("Method ONE was invoked", "TEST", bean.getStringProperty());

        } catch (Throwable t) {
            // ONE
            fail("Exception in method ONE prevented invokation: " + t.toString());
        }

        // trickier this one - find a method in a direct interface
        // METHOD TWO FAILURE
        try {

            Object ret = MethodUtils.invokeExactMethod(
                    privateBeanFactory.create(),
                    "methodBar",
                    "ANOTHER TEST");

            // check that we've found one that matches
            assertEquals("Method TWO was invoked correctly", "ANOTHER TEST", ret);

        } catch (Throwable t) {
            // METHOD TWO FAILURE
            fail("Exception in method TWO prevented invokation: " + t.toString());
        }


        // trickier this one - find a method in a indirect interface
        // METHOD THREE
        try {

            Object ret = MethodUtils.invokeExactMethod(
                    privateBeanFactory.createSubclass(),
                    "methodBaz",
                    "YET ANOTHER TEST");


            // check that we've found one that matches
            assertEquals("Method TWO was invoked correctly", "YET ANOTHER TEST", ret);


        } catch (Throwable t) {
            // METHOD THREE FAILURE
            fail("Exception in method THREE prevented invokation: " + t.toString());

        }
    }
    
    /**
     * <p> Test <code>invokeMethod</code>.
     */
    public void testInvokeMethod() throws Exception {
        // i'm going to test that the actual calls work first and then try them via reflection
        
        AbstractParent parent = new AlphaBean("parent");
        
        // try testAddChild through abstract superclass
        BetaBean childOne = new BetaBean("ChildOne");
        
        assertEquals("Oh no! Badly coded test case! (1)", "ChildOne", parent.testAddChild(childOne));
        
        // let's try MethodUtils version
        assertEquals(
                        "Cannot invoke through abstract class (1)", 
                        "ChildOne", 
                        MethodUtils.invokeMethod(parent, "testAddChild", childOne));

        
        // try adding through interface
        AlphaBean childTwo = new AlphaBean("ChildTwo");
        
        assertEquals("Oh no! Badly coded test case! (2)", "ChildTwo", parent.testAddChild(childTwo));
        
        // let's try MethodUtils version
        assertEquals(
                        "Cannot invoke through interface (1)", 
                        "ChildTwo", 
                        MethodUtils.invokeMethod(parent, "testAddChild", childTwo));
       
        
        Object[] params = new Object[2];

        assertEquals("Oh no! Badly coded test case! (3)", "ChildOne", parent.testAddChild2("parameter", childOne));
        
        
        // let's try MethodUtils version
        params[0] = "parameter";
        params[1] = childOne;
        
        assertEquals(
                        "Cannot invoke through abstract class (1)", 
                        "ChildOne", 
                        MethodUtils.invokeMethod(parent, "testAddChild2", params));
                        
        assertEquals("Oh no! Badly coded test case! (4)", "ChildTwo", parent.testAddChild2("parameter", childTwo));
        
        // let's try MethodUtils version
        params[0] = "parameter";
        params[1] = childTwo;
       
        assertEquals(
                        "Cannot invoke through abstract class (1)", 
                        "ChildTwo", 
                        MethodUtils.invokeMethod(parent, "testAddChild2", params));
        
    }
}
