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


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test accessing RowSets via DynaBeans.
 *
 * @version $Id$
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
    public DynaRowSetTestCase(final String name) {

        super(name);

    }


    // --------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {

        dynaClass = new RowSetDynaClass(TestResultSet.createProxy());

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
    @Override
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
        } catch (final IllegalArgumentException e) {
            // Expected result
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

        final DynaProperty dynaProps[] = dynaClass.getDynaProperties();
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
        } catch (final UnsupportedOperationException e) {
            // Expected result
        } catch (final Exception e) {
            fail("Threw exception " + e);
        }

    }


    public void testListCount() {

        final List<DynaBean> rows = dynaClass.getRows();
        assertNotNull("list exists", rows);
        assertEquals("list row count", 5, rows.size());

    }


    public void testListResults() {

        // Grab the third row
        final List<DynaBean> rows = dynaClass.getRows();
        final DynaBean row = rows.get(2);

        // Invalid argument test
        try {
            row.get("unknownProperty");
            fail("Did not throw IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // Expected result
        }

        // Verify property values

        final Object bigDecimalProperty = row.get("bigdecimalproperty");
        assertNotNull("bigDecimalProperty exists", bigDecimalProperty);
        assertTrue("bigDecimalProperty type",
                   bigDecimalProperty instanceof BigDecimal);
        assertEquals("bigDecimalProperty value",
                     123.45,
                     ((BigDecimal) bigDecimalProperty).doubleValue(),
                     0.005);

        final Object intProperty = row.get("intproperty");
        assertNotNull("intProperty exists", intProperty);
        assertTrue("intProperty type",
                   intProperty instanceof Integer);
        assertEquals("intProperty value",
                     103,
                     ((Integer) intProperty).intValue());

        final Object nullProperty = row.get("nullproperty");
        assertNull("nullProperty null", nullProperty);

        final Object stringProperty = row.get("stringproperty");
        assertNotNull("stringProperty exists", stringProperty);
        assertTrue("stringProperty type",
                   stringProperty instanceof String);
        assertEquals("stringProperty value",
                     "This is a string",
                     (String) stringProperty);


    }

    /**
     * Test normal case column names (i.e. not converted to lower case)
     */
    public void testListResultsNormalCase() {
        RowSetDynaClass dynaClass = null;
        try {
            dynaClass = new RowSetDynaClass(TestResultSet.createProxy(), false);
        } catch (final Exception e) {
            fail("Error creating RowSetDynaClass: " + e);
        }

        // Grab the third row
        final List<DynaBean> rows = dynaClass.getRows();
        final DynaBean row = rows.get(2);

        // Invalid argument test
        try {
            row.get("unknownProperty");
            fail("Did not throw IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // Expected result
        }

        // Verify property values

        final Object bigDecimalProperty = row.get("bigDecimalProperty");
        assertNotNull("bigDecimalProperty exists", bigDecimalProperty);
        assertTrue("bigDecimalProperty type",
                   bigDecimalProperty instanceof BigDecimal);
        assertEquals("bigDecimalProperty value",
                     123.45,
                     ((BigDecimal) bigDecimalProperty).doubleValue(),
                     0.005);

        final Object intProperty = row.get("intProperty");
        assertNotNull("intProperty exists", intProperty);
        assertTrue("intProperty type",
                   intProperty instanceof Integer);
        assertEquals("intProperty value",
                     103,
                     ((Integer) intProperty).intValue());

        final Object nullProperty = row.get("nullProperty");
        assertNull("nullProperty null", nullProperty);

        final Object stringProperty = row.get("stringProperty");
        assertNotNull("stringProperty exists", stringProperty);
        assertTrue("stringProperty type",
                   stringProperty instanceof String);
        assertEquals("stringProperty value",
                     "This is a string",
                     (String) stringProperty);


    }

    public void testLimitedRows() throws Exception {

        // created one with low limit
        final RowSetDynaClass limitedDynaClass = new RowSetDynaClass(TestResultSet.createProxy(), 3);
        final List<DynaBean> rows = limitedDynaClass.getRows();
        assertNotNull("list exists", rows);
        assertEquals("limited row count", 3, rows.size());

    }

    /**
     * Test issues associated with Oracle JDBC driver.
     *
     * See issue# https://issues.apache.org/jira/browse/BEANUTILS-142
     *
     * @throws Exception if an error occurs
     */
    public void testInconsistentOracleDriver() throws Exception {

        final ResultSetMetaData metaData = TestResultSetMetaData.createProxy(new TestResultSetMetaDataInconsistent());
        final ResultSet resultSet = TestResultSet.createProxy(new TestResultSetInconsistent(metaData));

        // Date Column returns "java.sql.Timestamp" for the column class name but ResultSet getObject
        // returns a java.sql.Date value
        final int dateColIdx = 4;
        assertEquals("Date Meta Name",       "dateProperty",       metaData.getColumnName(dateColIdx));
        assertEquals("Date Meta Class",      "java.sql.Timestamp", metaData.getColumnClassName(dateColIdx));
        assertEquals("Date Meta Type",       java.sql.Types.DATE,  metaData.getColumnType(dateColIdx));
        assertEquals("Date ResultSet Value", java.sql.Date.class,  resultSet.getObject("dateProperty").getClass());

        // Timestamp column class returns a custom Timestamp impl for the column class name and ResultSet getObject
        final int timestampColIdx = 13;
        assertEquals("Timestamp Meta Name",       "timestampProperty",             metaData.getColumnName(timestampColIdx));
        assertEquals("Timestamp Meta Class",      CustomTimestamp.class.getName(), metaData.getColumnClassName(timestampColIdx));
        assertEquals("Timestamp Meta Type",       java.sql.Types.TIMESTAMP,        metaData.getColumnType(timestampColIdx));
        assertEquals("Timestamp ResultSet Value", CustomTimestamp.class,           resultSet.getObject("timestampProperty").getClass());

        final RowSetDynaClass inconsistentDynaClass = new RowSetDynaClass(resultSet);
        final DynaBean firstRow = inconsistentDynaClass.getRows().get(0);
        Class<?> expectedType = null;
        DynaProperty property = null;

        // Test Date
        property = firstRow.getDynaClass().getDynaProperty("dateproperty");
        expectedType = java.sql.Date.class;
        assertEquals("Date Class", expectedType, property.getType());
        assertEquals("Date Value", expectedType, firstRow.get(property.getName()).getClass());

        // Test Timestamp
        property = firstRow.getDynaClass().getDynaProperty("timestampproperty");
        expectedType = java.sql.Timestamp.class;
        assertEquals("Timestamp Class", expectedType, property.getType());
        assertEquals("Timestamp Value", expectedType, firstRow.get(property.getName()).getClass());
    }

    /**
     * A proxy ResultSet implementation that returns Timstamp for a date column.
     *
     * See issue# https://issues.apache.org/jira/browse/BEANUTILS-142
     */
    private static class TestResultSetInconsistent extends  TestResultSet {

        public TestResultSetInconsistent(final ResultSetMetaData metaData) {
            super(metaData);
        }
        /**
         * Get an columns's value
         * @param columnName Name of the column
         * @return the column value
         * @throws SQLException if an error occurs
         */
        @Override
        public Object getObject(final String columnName) throws SQLException {
            if ("timestampProperty".equals(columnName)) {
                return new CustomTimestamp();
            } else {
                return super.getObject(columnName);
            }
        }

    }

    /**
     * A proxy ResultSetMetaData implementation that returns a class name that
     * is inconsistent with the type returned by the ResultSet.getObject() method.
     *
     * See issue# https://issues.apache.org/jira/browse/BEANUTILS-142
     */
    private static class TestResultSetMetaDataInconsistent extends  TestResultSetMetaData {

        /**
         * This method substitues class names of "java.sql.Timestamp" with
         * "java.sql.Date" to test inconsistent JDBC drivers.
         *
         * @param columnIndex The column index
         * @return The column class name
         * @throws SQLException if an error occurs
         */
        @Override
        public String getColumnClassName(final int columnIndex) throws SQLException {
            final String columnName = getColumnName(columnIndex);
            if (columnName.equals("dateProperty")) {
                return java.sql.Timestamp.class.getName();
            } else if (columnName.equals("timestampProperty")) {
                return CustomTimestamp.class.getName();
            } else {
                return super.getColumnClassName(columnIndex);
            }
        }
    }
    private static class CustomTimestamp {
        private final long timestamp = new java.util.Date().getTime();
        @Override
        public String toString() {
            return "CustomTimestamp[" + timestamp + "]";
        }
    }
}
