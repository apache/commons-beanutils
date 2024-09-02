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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.commons.beanutils2.DynaBean;
import org.apache.commons.beanutils2.DynaProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test accessing RowSets via DynaBeans.
 */
public class DynaRowSetTestCase {

    private static class CustomTimestamp {
        private final long timestamp = new java.util.Date().getTime();

        @Override
        public String toString() {
            return "CustomTimestamp[" + timestamp + "]";
        }
    }

    /**
     * A proxy ResultSet implementation that returns Timstamp for a date column.
     *
     * See issue# https://issues.apache.org/jira/browse/BEANUTILS-142
     */
    private static class TestResultSetInconsistent extends TestResultSet {

        public TestResultSetInconsistent(final ResultSetMetaData metaData) {
            super(metaData);
        }

        /**
         * Gets an columns's value
         *
         * @param columnName Name of the column
         * @return the column value
         * @throws SQLException if an error occurs
         */
        @Override
        public Object getObject(final String columnName) throws SQLException {
            if ("timestampProperty".equals(columnName)) {
                return new CustomTimestamp();
            }
            return super.getObject(columnName);
        }

    }

    /**
     * A proxy ResultSetMetaData implementation that returns a class name that is inconsistent with the type returned by the ResultSet.getObject() method.
     *
     * See issue# https://issues.apache.org/jira/browse/BEANUTILS-142
     */
    private static class TestResultSetMetaDataInconsistent extends TestResultSetMetaData {

        /**
         * This method substitues class names of "java.sql.Timestamp" with "java.sql.Date" to test inconsistent JDBC drivers.
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
            }
            if (columnName.equals("timestampProperty")) {
                return CustomTimestamp.class.getName();
            }
            return super.getColumnClassName(columnIndex);
        }
    }

    /**
     * The mock result set DynaClass to be tested.
     */
    protected RowSetDynaClass dynaClass;

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

        dynaClass = new RowSetDynaClass(TestResultSet.createProxy());

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
        assertEquals("org.apache.commons.beanutils2.sql.RowSetDynaClass", dynaClass.getName(), "DynaClass name");
    }

    /**
     * Test issues associated with Oracle JDBC driver.
     *
     * See issue# https://issues.apache.org/jira/browse/BEANUTILS-142
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testInconsistentOracleDriver() throws Exception {

        final ResultSetMetaData metaData = TestResultSetMetaData.createProxy(new TestResultSetMetaDataInconsistent());
        final ResultSet resultSet = TestResultSet.createProxy(new TestResultSetInconsistent(metaData));

        // Date Column returns "java.sql.Timestamp" for the column class name but ResultSet getObject
        // returns a java.sql.Date value
        final int dateColIdx = 4;
        assertEquals("dateProperty", metaData.getColumnName(dateColIdx), "Date Meta Name");
        assertEquals("java.sql.Timestamp", metaData.getColumnClassName(dateColIdx), "Date Meta Class");
        assertEquals(Types.DATE, metaData.getColumnType(dateColIdx), "Date Meta Type");
        assertEquals(Date.class, resultSet.getObject("dateProperty").getClass(), "Date ResultSet Value");

        // Timestamp column class returns a custom Timestamp impl for the column class name and ResultSet getObject
        final int timestampColIdx = 13;
        assertEquals("timestampProperty", metaData.getColumnName(timestampColIdx), "Timestamp Meta Name");
        assertEquals(CustomTimestamp.class.getName(), metaData.getColumnClassName(timestampColIdx), "Timestamp Meta Class");
        assertEquals(Types.TIMESTAMP, metaData.getColumnType(timestampColIdx), "Timestamp Meta Type");
        assertEquals(CustomTimestamp.class, resultSet.getObject("timestampProperty").getClass(), "Timestamp ResultSet Value");

        final RowSetDynaClass inconsistentDynaClass = new RowSetDynaClass(resultSet);
        final DynaBean firstRow = inconsistentDynaClass.getRows().get(0);
        Class<?> expectedType;
        DynaProperty property;

        // Test Date
        property = firstRow.getDynaClass().getDynaProperty("dateproperty");
        expectedType = java.sql.Date.class;
        assertEquals(expectedType, property.getType(), "Date Class");
        assertEquals(expectedType, firstRow.get(property.getName()).getClass(), "Date Value");

        // Test Timestamp
        property = firstRow.getDynaClass().getDynaProperty("timestampproperty");
        expectedType = java.sql.Timestamp.class;
        assertEquals(expectedType, property.getType(), "Timestamp Class");
        assertEquals(expectedType, firstRow.get(property.getName()).getClass(), "Timestamp Value");
    }

    @Test
    public void testLimitedRows() throws Exception {

        // created one with low limit
        final RowSetDynaClass limitedDynaClass = new RowSetDynaClass(TestResultSet.createProxy(), 3);
        final List<DynaBean> rows = limitedDynaClass.getRows();
        assertNotNull(rows, "list exists");
        assertEquals(3, rows.size(), "limited row count");

    }

    @Test
    public void testListCount() {

        final List<DynaBean> rows = dynaClass.getRows();
        assertNotNull(rows, "list exists");
        assertEquals(5, rows.size(), "list row count");

    }

    @Test
    public void testListResults() {

        // Grab the third row
        final List<DynaBean> rows = dynaClass.getRows();
        final DynaBean row = rows.get(2);

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
    public void testListResultsNormalCase() throws Exception {
        final RowSetDynaClass dynaClass = new RowSetDynaClass(TestResultSet.createProxy(), false);

        // Grab the third row
        final List<DynaBean> rows = dynaClass.getRows();
        final DynaBean row = rows.get(2);

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
