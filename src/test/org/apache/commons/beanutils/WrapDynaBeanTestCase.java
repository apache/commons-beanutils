/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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


import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * <p>Test Case for the <code>WrapDynaBean</code> implementation class.
 * These tests were based on the ones in <code>PropertyUtilsTestCase</code>
 * because the two classes provide similar levels of functionality.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
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


    /**
     * Suppress serialization and deserialization tests.  WrapDynaClass
     * is not serializable.
     */
    public void testSerialization() { }
    
    /** Tests getInstance method */
    public void testGetInstance() {
        AlphaBean alphaBean = new AlphaBean("Now On Air... John Peel");
        WrapDynaBean dynaBean = new WrapDynaBean(alphaBean);
        Object wrappedInstance = dynaBean.getInstance();
        assertTrue("Object type is AlphaBean", wrappedInstance instanceof AlphaBean);
        AlphaBean wrappedAlphaBean = (AlphaBean) wrappedInstance;
        assertTrue("Same Object", wrappedAlphaBean == alphaBean);
    }

    /** Tests the newInstance implementation for WrapDynaClass */
    public void testNewInstance() throws Exception {
        WrapDynaClass dynaClass = WrapDynaClass.createDynaClass(AlphaBean.class);
        Object createdInstance = dynaClass.newInstance();
        assertTrue("Object type is WrapDynaBean", createdInstance instanceof WrapDynaBean);
        WrapDynaBean dynaBean = (WrapDynaBean) createdInstance;
        assertTrue("Object type is AlphaBean", dynaBean.getInstance() instanceof AlphaBean);
    }

}
