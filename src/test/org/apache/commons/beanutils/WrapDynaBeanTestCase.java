/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/WrapDynaBeanTestCase.java,v 1.3 2002/01/23 22:52:26 sanders Exp $
 * $Revision: 1.3 $
 * $Date: 2002/01/23 22:52:26 $
 *
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


import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * <p>Test Case for the <code>WrapDynaBean</code> implementation class.
 * These tests were based on the ones in <code>PropertyUtilsTestCase</code>
 * because the two classes provide similar levels of functionality.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.3 $ $Date: 2002/01/23 22:52:26 $
 */

public class WrapDynaBeanTestCase extends BasicDynaBeanTestCase {


    // ---------------------------------------------------- Instance Variables


    // ---------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public WrapDynaBeanTestCase(String name) {

        super(name);

    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {

        bean = new WrapDynaBean(new TestBean());

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(WrapDynaBeanTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        bean = null;

    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * The <code>contains()</code> method is not supported by the
     * <code>WrapDynaBean</code> implementation class.
     */
    public void testMappedContains() {

        try {
            assertTrue("Can see first key",
                    bean.contains("mappedProperty", "First Key"));
            fail("Should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException t) {
            ; // Expected result
        } catch (Throwable t) {
            fail("Exception: " + t);
        }


        try {
            assertTrue("Can not see unknown key",
                    !bean.contains("mappedProperty", "Unknown Key"));
            fail("Should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException t) {
            ; // Expected result
        } catch (Throwable t) {
            fail("Exception: " + t);
        }

    }


    /**
     * The <code>remove()</code> method is not supported by the
     * <code>WrapDynaBean</code> implementation class.
     */
    public void testMappedRemove() {

        try {
            assertTrue("Can see first key",
                    bean.contains("mappedProperty", "First Key"));
            bean.remove("mappedProperty", "First Key");
            fail("Should have thrown UnsupportedOperationException");
            //            assertTrue("Can not see first key",
            //         !bean.contains("mappedProperty", "First Key"));
        } catch (UnsupportedOperationException t) {
            ; // Expected result
        } catch (Throwable t) {
            fail("Exception: " + t);
        }

        try {
            assertTrue("Can not see unknown key",
                    !bean.contains("mappedProperty", "Unknown Key"));
            bean.remove("mappedProperty", "Unknown Key");
            fail("Should have thrown UnsupportedOperationException");
            //            assertTrue("Can not see unknown key",
            //         !bean.contains("mappedProperty", "Unknown Key"));
        } catch (UnsupportedOperationException t) {
            ; // Expected result
        } catch (Throwable t) {
            fail("Exception: " + t);
        }

    }


}
