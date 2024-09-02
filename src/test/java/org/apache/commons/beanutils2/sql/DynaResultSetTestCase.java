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

package org.apache.commons.beanutils2.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.commons.beanutils2.DynaBean;
import org.apache.commons.beanutils2.DynaProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test accessing ResultSets via DynaBeans.
 */
public class DynaResultSetTestCase {

    /**
     * The mock result set DynaClass to be tested.
     */
    protected ResultSetDynaClass dynaClass;

    /**
     * Names of the columns for this test. Must match the order they are defined in {@link TestResultSetMetaData}, and must be all lower case.
     */
    protected String[] columns = { "bigdecimalproperty", "booleanproperty", "byteproperty", "dateproperty", "doubleproperty", "floatproperty", "intproperty",
            "longproperty", "nullproperty", "shortproperty", "stringproperty", "timeproperty", "timestampproperty" };

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() throws Exception {
        dynaClass = new ResultSetDynaClass(TestResultSet.createProxy());
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        dynaClass = null;
    }

    @Test
    public void testGetDynaProperties() {

        final DynaProperty[] dynaProps = dynaClass.getDynaProperties();
        assertNotNull(dynaProps, "dynaProps exists");
        assertEquals(columns.length, dynaProps.length, "dynaProps length");
        for (int i = 0; i < columns.length; i++) {
            assertEquals(columns[i], dynaProps[i].getName(), "Property " + columns[i]);
        }

    }

    @Test
    public void testGetDynaProperty() {
        // Invalid argument test
        assertThrows(NullPointerException.class, () -> dynaClass.getDynaProperty(null));
        // Negative test
        DynaProperty dynaProp = dynaClass.getDynaProperty("unknownProperty");
        assertNull(dynaProp, "unknown property returns null");
        // Positive test
        dynaProp = dynaClass.getDynaProperty("stringproperty");
        assertNotNull(dynaProp, "string property exists");
        assertEquals("stringproperty", dynaProp.getName(), "string property name");
        assertEquals(String.class, dynaProp.getType(), "string property class");
    }

    @Test
    public void testGetName() {
        assertEquals("org.apache.commons.beanutils2.sql.ResultSetDynaClass", dynaClass.getName(), "DynaClass name");
    }

    @Test
    public void testIteratorCount() {

        final Iterator<?> rows = dynaClass.iterator();
        assertNotNull(rows, "iterator exists");
        int n = 0;
        while (rows.hasNext()) {
            rows.next();
            n++;
            assertFalse(n > 10);
        }
        assertEquals(5, n, "iterator rows");

    }

    @Test
    public void testIteratorResults() {

        // Grab the third row
        final Iterator<DynaBean> rows = dynaClass.iterator();
        rows.next();
        rows.next();
        final DynaBean row = rows.next();

        // Invalid argument test
        assertThrows(IllegalArgumentException.class, () -> row.get("unknownProperty"));

        // Verify property values

        final Object bigDecimalProperty = row.get("bigdecimalproperty");
        assertNotNull(bigDecimalProperty, "bigDecimalProperty exists");
        assertInstanceOf(BigDecimal.class, bigDecimalProperty, "bigDecimalProperty type");
        assertEquals(123.45, ((BigDecimal) bigDecimalProperty).doubleValue(), 0.005, "bigDecimalProperty value");

        final Object intProperty = row.get("intproperty");
        assertNotNull(intProperty, "intProperty exists");
        assertInstanceOf(Integer.class, intProperty, "intProperty type");
        assertEquals(103, ((Integer) intProperty).intValue(), "intProperty value");

        final Object nullProperty = row.get("nullproperty");
        assertNull(nullProperty, "nullProperty null");

        final Object stringProperty = row.get("stringproperty");
        assertNotNull(stringProperty, "stringProperty exists");
        assertInstanceOf(String.class, stringProperty, "stringProperty type");
        assertEquals("This is a string", (String) stringProperty, "stringProperty value");

    }

    /**
     * Test normal case column names (i.e. not converted to lower case)
     */
    @Test
    public void testIteratorResultsNormalCase() throws Exception {
        ResultSetDynaClass dynaClass = new ResultSetDynaClass(TestResultSet.createProxy(), false);

        // Grab the third row
        final Iterator<DynaBean> rows = dynaClass.iterator();
        rows.next();
        rows.next();
        final DynaBean row = rows.next();

        // Invalid argument test
        assertThrows(IllegalArgumentException.class, () -> row.get("unknownProperty"));

        // Verify property values

        final Object bigDecimalProperty = row.get("bigDecimalProperty");
        assertNotNull(bigDecimalProperty, "bigDecimalProperty exists");
        assertInstanceOf(BigDecimal.class, bigDecimalProperty, "bigDecimalProperty type");
        assertEquals(123.45, ((BigDecimal) bigDecimalProperty).doubleValue(), 0.005, "bigDecimalProperty value");

        final Object intProperty = row.get("intProperty");
        assertNotNull(intProperty, "intProperty exists");
        assertInstanceOf(Integer.class, intProperty, "intProperty type");
        assertEquals(103, ((Integer) intProperty).intValue(), "intProperty value");

        final Object nullProperty = row.get("nullProperty");
        assertNull(nullProperty, "nullProperty null");

        final Object stringProperty = row.get("stringProperty");
        assertNotNull(stringProperty, "stringProperty exists");
        assertInstanceOf(String.class, stringProperty, "stringProperty type");
        assertEquals("This is a string", (String) stringProperty, "stringProperty value");

    }

    @Test
    public void testNewInstance() {
        assertThrows(UnsupportedOperationException.class, () -> dynaClass.newInstance());
    }

}
