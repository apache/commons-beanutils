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


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;


/**
 * <p>Mock object that implements enough of
 * <code>java.sql.ResultSetMetaData</code>
 * to exercise the {@link ResultSetDynaClass} functionality.</p>
 *
 * @version $Id$
 */

public class TestResultSetMetaData implements InvocationHandler {


    // ----------------------------------------------------- Instance Variables


    /**
     * <p>Array of column names and class names for metadata.</p>
     */
    protected String metadata[][] = {
        { "bigDecimalProperty", java.math.BigDecimal.class.getName() },
        { "booleanProperty", Boolean.class.getName() },
        { "byteProperty", Byte.class.getName() },
        { "dateProperty", java.sql.Date.class.getName() },
        { "doubleProperty", Double.class.getName() },
        { "floatProperty", Float.class.getName() },
        { "intProperty", Integer.class.getName() },
        { "longProperty", Long.class.getName() },
        { "nullProperty", String.class.getName() },
        { "shortProperty", Short.class.getName() },
        { "stringProperty", String.class.getName() },
        { "timeProperty", java.sql.Time.class.getName() },
        { "timestampProperty", java.sql.Timestamp.class.getName() },
    };


    /**
     * Factory method for creating {@link ResultSetMetaData} proxies.
     *
     * @return A result set meta data proxy
     */
    public static ResultSetMetaData createProxy() {
        return TestResultSetMetaData.createProxy(new TestResultSetMetaData());
    }

    /**
     * Factory method for creating {@link ResultSetMetaData} proxies.
     * @param invocationHandler Invocation Handler
     * @return A result set meta data proxy
     */
    public static ResultSetMetaData createProxy(final InvocationHandler invocationHandler) {
        final ClassLoader classLoader = ResultSetMetaData.class.getClassLoader();
        final Class<?>[] interfaces = new Class[] { ResultSetMetaData.class };
        return (ResultSetMetaData)Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }

    /**
     * Handles method invocation on the {@link ResultSetMetaData} proxy.
     *
     * @param proxy The proxy ResultSet object
     * @param method the method being invoked
     * @param args The method arguments
     * @return The result of invoking the method.
     * @throws Throwable if an error occurs.
     */
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String methodName = method.getName();
        if ("getColumnClassName".equals(methodName)) {
            return getColumnClassName(((Integer)args[0]).intValue());
        } if ("getColumnCount".equals(methodName)) {
            return new Integer(getColumnCount());
        } if ("getColumnName".equals(methodName)) {
            return getColumnName(((Integer)args[0]).intValue());
        } if ("getColumnType".equals(methodName)) {
            return getColumnType(((Integer)args[0]).intValue());
        }

        throw new UnsupportedOperationException(methodName + " not implemented");
    }

    // ---------------------------------------------------- Implemented Methods


    public String getColumnClassName(final int columnIndex) throws SQLException {
        return (metadata[columnIndex - 1][1]);
    }


    public int getColumnCount() throws SQLException {
        return (metadata.length);
    }

    public String getColumnName(final int columnIndex) throws SQLException {
        return (metadata[columnIndex - 1][0]);
    }


    public Integer getColumnType(final int columnIndex) throws SQLException {
        final String columnName = getColumnName(columnIndex);
        int sqlType = Types.OTHER;
        if (columnName.equals("bigDecimalProperty")) {
            sqlType = Types.DECIMAL;
// Types.BOOLEAN only introduced in JDK 1.4
//        } else if (columnName.equals("booleanProperty")) {
//            sqlType = Types.BOOLEAN;
        } else if (columnName.equals("byteProperty")) {
            sqlType = Types.TINYINT;
        } else if (columnName.equals("dateProperty")) {
            sqlType = Types.DATE;
        } else if (columnName.equals("doubleProperty")) {
            sqlType = Types.DOUBLE;
        } else if (columnName.equals("floatProperty")) {
            sqlType = Types.FLOAT;
        } else if (columnName.equals("intProperty")) {
            sqlType = Types.INTEGER;
        } else if (columnName.equals("longProperty")) {
            sqlType = Types.BIGINT;
        } else if (columnName.equals("nullProperty")) {
            sqlType = Types.VARCHAR;
        } else if (columnName.equals("shortProperty")) {
            sqlType = Types.SMALLINT;
        } else if (columnName.equals("stringProperty")) {
            sqlType = Types.VARCHAR;
        } else if (columnName.equals("timeProperty")) {
            sqlType = Types.TIME;
        } else if (columnName.equals("timestampProperty")) {
            sqlType = Types.TIMESTAMP;
        } else {
            sqlType = Types.OTHER;
        }
        return new Integer(sqlType);
    }


    // -------------------------------------------------- Unimplemented Methods


    public String getCatalogName(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getColumnDisplaySize(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public String getColumnLabel(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public String getColumnTypeName(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getPrecision(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getScale(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public String getSchemaName(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public String getTableName(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isAutoIncrement(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isCaseSensitive(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isCurrency(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isDefinitelyWritable(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int isNullable(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isReadOnly(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isSearchable(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isSigned(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isWritable(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


}
