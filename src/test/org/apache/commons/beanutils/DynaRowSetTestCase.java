/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/DynaRowSetTestCase.java,v 1.2 2003/05/20 21:01:44 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/05/20 21:01:44 $
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
 * Test accessing RowSets via DynaBeans.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2003/05/20 21:01:44 $
 */

public class DynaRowSetTestCase extends TestCase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The mock result set DynaClass to be tested.
     */
    protected RowSetDynaClass dynaClass = null;


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
    public DynaRowSetTestCase(String name) {

        super(name);

    }


    // --------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {

        dynaClass = new RowSetDynaClass(new TestResultSet());

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(DynaRowSetTestCase.class));

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
                     "org.apache.commons.beanutils.RowSetDynaClass",
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


    public void testListCount() {

        List rows = dynaClass.getRows();
        assertNotNull("list exists", rows);
        assertEquals("list row count", 5, rows.size());

    }


    public void testListResults() {

        // Grab the third row
        List rows = dynaClass.getRows();
        DynaBean row = (DynaBean) rows.get(2);

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

    public void testLimitedRows() throws Exception {
        
        // created one with low limit
        RowSetDynaClass limitedDynaClass = new RowSetDynaClass(new TestResultSet(), 3);
        List rows = limitedDynaClass.getRows();
        assertNotNull("list exists", rows);
        assertEquals("limited row count", 3, rows.size());
        
    }
}
