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


import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Test accessing ResultSets via DynaBeans.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class DynaResultSetTestCase extends TestCase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The mock result set DynaClass to be tested.
     */
    protected ResultSetDynaClass dynaClass = null;


    /**
     * Names of the columns for this test.  Must match the order they are
     * defined in {@link TestResultSetMetaData}, and must be all lower case.
     */
    protected String columns[] =
    { "bigdecimalproperty", "booleanproperty",
      "byteproperty", "dateproperty",
      "doubleproperty", "floatproperty",
      "intproperty", "longproperty",
      "nullproperty", "shortproperty",
      "stringproperty", "timeproperty",
      "timestampproperty" };


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public DynaResultSetTestCase(String name) {

        super(name);

    }


    // --------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {

        dynaClass = new ResultSetDynaClass(new TestResultSet());

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(DynaResultSetTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        dynaClass = null;

    }



    // ------------------------------------------------ Individual Test Methods


    public void testGetName() {

        assertEquals("DynaClass name",
                     "org.apache.commons.beanutils.ResultSetDynaClass",
                     dynaClass.getName());


    }


    public void testGetDynaProperty() {

        // Invalid argument test
        try {
            dynaClass.getDynaProperty(null);
            fail("Did not throw IllegaArgumentException");
        } catch (IllegalArgumentException e) {
            ; // Expected result
        }

        // Negative test
        DynaProperty dynaProp = dynaClass.getDynaProperty("unknownProperty");
        assertTrue("unknown property returns null",
                   (dynaProp == null));

        // Positive test
        dynaProp = dynaClass.getDynaProperty("stringproperty");
        assertNotNull("string property exists", dynaProp);
        assertEquals("string property name", "stringproperty",
                     dynaProp.getName());
        assertEquals("string property class", String.class,
                     dynaProp.getType());

    }


    public void testGetDynaProperties() {

        DynaProperty dynaProps[] = dynaClass.getDynaProperties();
        assertNotNull("dynaProps exists", dynaProps);
        assertEquals("dynaProps length", columns.length, dynaProps.length);
        for (int i = 0; i < columns.length; i++) {
            assertEquals("Property " + columns[i],
                         columns[i], dynaProps[i].getName());
        }

    }


    public void testNewInstance() {

        try {
            dynaClass.newInstance();
            fail("Did not throw UnsupportedOperationException()");
        } catch (UnsupportedOperationException e) {
            ; // Expected result
        } catch (Exception e) {
            fail("Threw exception " + e);
        }

    }


    public void testIteratorCount() {

        Iterator rows = dynaClass.iterator();
        assertNotNull("iterator exists", rows);
        int n = 0;
        while (rows.hasNext()) {
            rows.next();
            n++;
            if (n > 10) {
                fail("Returned too many rows");
            }
        }
        assertEquals("iterator rows", 5, n);

    }


    public void testIteratorResults() {

        // Grab the third row
        Iterator rows = dynaClass.iterator();
        rows.next();
        rows.next();
        DynaBean row = (DynaBean) rows.next();

        // Invalid argument test
        try {
            row.get("unknownProperty");
            fail("Did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            ; // Expected result
        }

        // Verify property values

        Object bigDecimalProperty = row.get("bigdecimalproperty");
        assertNotNull("bigDecimalProperty exists", bigDecimalProperty);
        assertTrue("bigDecimalProperty type",
                   bigDecimalProperty instanceof BigDecimal);
        assertEquals("bigDecimalProperty value",
                     123.45,
                     ((BigDecimal) bigDecimalProperty).doubleValue(),
                     0.005);

        Object intProperty = row.get("intproperty");
        assertNotNull("intProperty exists", intProperty);
        assertTrue("intProperty type",
                   intProperty instanceof Integer);
        assertEquals("intProperty value",
                     103,
                     ((Integer) intProperty).intValue());

        Object nullProperty = row.get("nullproperty");
        assertNull("nullProperty null", nullProperty);

        Object stringProperty = row.get("stringproperty");
        assertNotNull("stringProperty exists", stringProperty);
        assertTrue("stringProperty type",
                   stringProperty instanceof String);
        assertEquals("stringProperty value",
                     "This is a string",
                     (String) stringProperty);


    }


}
