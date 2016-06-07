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

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Provides common logic for JDBC implementations of {@link DynaClass}.</p>
 *
 * @version $Id$
 */

abstract class JDBCDynaClass implements DynaClass, Serializable {

    // ----------------------------------------------------- Instance Variables

    /**
     * <p>Flag defining whether column names should be lower cased when
     * converted to property names.</p>
     */
    protected boolean lowerCase = true;

    /**
     * <p>Flag defining whether column names or labels should be used.
     */
    private boolean useColumnLabel;

    /**
     * <p>The set of dynamic properties that are part of this
     * {@link DynaClass}.</p>
     */
    protected DynaProperty[] properties = null;

    /**
     * <p>The set of dynamic properties that are part of this
     * {@link DynaClass}, keyed by the property name.  Individual descriptor
     * instances will be the same instances as those in the
     * <code>properties</code> list.</p>
     */
    protected Map<String, DynaProperty> propertiesMap = new HashMap<String, DynaProperty>();

    /**
     * Cross Reference for column name --> dyna property name
     * (needed when lowerCase option is true)
     */
    private Map<String, String> columnNameXref;

    // ------------------------------------------------------ DynaClass Methods

    /**
     * <p>Return the name of this DynaClass (analogous to the
     * <code>getName()</code> method of <code>java.lang.Class</code), which
     * allows the same <code>DynaClass</code> implementation class to support
     * different dynamic classes, with different sets of properties.</p>
     */
    public String getName() {

        return (this.getClass().getName());

    }

    /**
     * <p>Return a property descriptor for the specified property, if it
     * exists; otherwise, return <code>null</code>.</p>
     *
     * @param name Name of the dynamic property for which a descriptor
     *  is requested
     *
     * @throws IllegalArgumentException if no property name is specified
     */
    public DynaProperty getDynaProperty(final String name) {

        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        return (propertiesMap.get(name));

    }

    /**
     * <p>Return an array of <code>ProperyDescriptors</code> for the properties
     * currently defined in this DynaClass.  If no properties are defined, a
     * zero-length array will be returned.</p>
     */
    public DynaProperty[] getDynaProperties() {

        return (properties);

    }

    /**
     * <p>Instantiate and return a new DynaBean instance, associated
     * with this DynaClass.  <strong>NOTE</strong> - This operation is not
     * supported, and throws an exception.</p>
     *
     * @throws IllegalAccessException if the Class or the appropriate
     *  constructor is not accessible
     * @throws InstantiationException if this Class represents an abstract
     *  class, an array class, a primitive type, or void; or if instantiation
     *  fails for some other reason
     */
    public DynaBean newInstance()
            throws IllegalAccessException, InstantiationException {

        throw new UnsupportedOperationException("newInstance() not supported");

    }

    /**
     * Set whether the column label or name should be used for the property name.
     *
     * @param useColumnLabel true if the column label should be used, otherwise false
     */
    public void setUseColumnLabel(final boolean useColumnLabel) {
        this.useColumnLabel = useColumnLabel;
    }

    /**
     * <p>Loads and returns the <code>Class</code> of the given name.
     * By default, a load from the thread context class loader is attempted.
     * If there is no such class loader, the class loader used to load this
     * class will be utilized.</p>
     *
     * @param className The name of the class to load
     * @return The loaded class
     * @throws SQLException if an exception was thrown trying to load
     *  the specified class
     */
    protected Class<?> loadClass(final String className) throws SQLException {

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                    cl = this.getClass().getClassLoader();
            }
            // use Class.forName() - see BEANUTILS-327
            return Class.forName(className, false, cl);
        } catch (final Exception e) {
            throw new SQLException(
                    "Cannot load column class '" + className + "': " + e);
        }

    }

    /**
     * <p>Factory method to create a new DynaProperty for the given index
     * into the result set metadata.</p>
     *
     * @param metadata is the result set metadata
     * @param i is the column index in the metadata
     * @return the newly created DynaProperty instance
     * @throws SQLException If an error occurs accessing the SQL metadata
     */
    protected DynaProperty createDynaProperty(
                                    final ResultSetMetaData metadata,
                                    final int i)
                                    throws SQLException {

        String columnName = null;
        if (useColumnLabel) {
            columnName = metadata.getColumnLabel(i);
        }
        if (columnName == null || columnName.trim().length() == 0) {
            columnName = metadata.getColumnName(i);
        }
        final String name = lowerCase ? columnName.toLowerCase() : columnName;
        if (!name.equals(columnName)) {
            if (columnNameXref == null) {
                columnNameXref = new HashMap<String, String>();
            }
            columnNameXref.put(name, columnName);
        }
        String className = null;
        try {
            final int sqlType = metadata.getColumnType(i);
            switch (sqlType) {
                case java.sql.Types.DATE:
                    return new DynaProperty(name, java.sql.Date.class);
                case java.sql.Types.TIMESTAMP:
                    return new DynaProperty(name, java.sql.Timestamp.class);
                case java.sql.Types.TIME:
                    return new DynaProperty(name, java.sql.Time.class);
                default:
                    className = metadata.getColumnClassName(i);
            }
        } catch (final SQLException e) {
            // this is a patch for HsqlDb to ignore exceptions
            // thrown by its metadata implementation
        }

        // Default to Object type if no class name could be retrieved
        // from the metadata
        Class<?> clazz = Object.class;
        if (className != null) {
            clazz = loadClass(className);
        }
        return new DynaProperty(name, clazz);

    }

    /**
     * <p>Introspect the metadata associated with our result set, and populate
     * the <code>properties</code> and <code>propertiesMap</code> instance
     * variables.</p>
     *
     * @param resultSet The <code>resultSet</code> whose metadata is to
     *  be introspected
     *
     * @throws SQLException if an error is encountered processing the
     *  result set metadata
     */
    protected void introspect(final ResultSet resultSet) throws SQLException {

        // Accumulate an ordered list of DynaProperties
        final ArrayList<DynaProperty> list = new ArrayList<DynaProperty>();
        final ResultSetMetaData metadata = resultSet.getMetaData();
        final int n = metadata.getColumnCount();
        for (int i = 1; i <= n; i++) { // JDBC is one-relative!
            final DynaProperty dynaProperty = createDynaProperty(metadata, i);
            if (dynaProperty != null) {
                    list.add(dynaProperty);
            }
        }

        // Convert this list into the internal data structures we need
        properties =
            list.toArray(new DynaProperty[list.size()]);
        for (DynaProperty propertie : properties) {
            propertiesMap.put(propertie.getName(), propertie);
        }

    }

    /**
     * Get a column value from a {@link ResultSet} for the specified name.
     *
     * @param resultSet The result set
     * @param name The property name
     * @return The value
     * @throws SQLException if an error occurs
     */
    protected Object getObject(final ResultSet resultSet, final String name) throws SQLException {

        final DynaProperty property = getDynaProperty(name);
        if (property == null) {
            throw new IllegalArgumentException("Invalid name '" + name + "'");
        }
        final String columnName = getColumnName(name);
        final Class<?> type = property.getType();

        // java.sql.Date
        if (type.equals(Date.class)) {
            return resultSet.getDate(columnName);
        }

        // java.sql.Timestamp
        if (type.equals(Timestamp.class)) {
            return resultSet.getTimestamp(columnName);
        }

        // java.sql.Time
        if (type.equals(Time.class)) {
            return resultSet.getTime(columnName);
        }

        return resultSet.getObject(columnName);
    }

    /**
     * Get the table column name for the specified property name.
     *
     * @param name The property name
     * @return The column name (which can be different if the <i>lowerCase</i>
     * option is used).
     */
    protected String getColumnName(final String name) {
        if (columnNameXref != null && columnNameXref.containsKey(name)) {
            return columnNameXref.get(name);
        } else {
            return name;
        }
    }

}
