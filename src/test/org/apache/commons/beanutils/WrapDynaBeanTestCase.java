/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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


    /**
     * Serialization and deserialization tests.
     * (WrapDynaBean is now serializable, although WrapDynaClass still is not)
     */
    public void testSerialization() {

        // Create a bean and set a value
        WrapDynaBean origBean = new WrapDynaBean(new TestBean());
        Integer newValue = new Integer(789);
        assertEquals("origBean default", new Integer(123), (Integer)origBean.get("intProperty"));
        origBean.set("intProperty", newValue); 
        assertEquals("origBean new value", newValue, (Integer)origBean.get("intProperty"));
        
        // Serialize/Deserialize & test value
        WrapDynaBean bean = (WrapDynaBean)serializeDeserialize(origBean, "First Test");
        assertEquals("bean value", newValue, (Integer)bean.get("intProperty"));
        
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
