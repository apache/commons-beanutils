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


import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;


/**
 * <p>Mock object that implements enough of <code>java.sql.ResultSet</code>
 * to exercise the {@link ResultSetDynaClass} functionality.</p>
 *
 * @version $Id$
 */

public class TestResultSet implements InvocationHandler {


    // ----------------------------------------------------- Instance Variables


    /**
     * Current row number (0 means "before the first one").
     */
    protected int row = 0;


    /**
     * The constant (per run) value used to initialize date/time/timestamp.
     */
    protected long timestamp = System.currentTimeMillis();

    /**
     * Meta data for the result set.
     */
    protected ResultSetMetaData resultSetMetaData;

    /**
     * Factory method for creating {@link ResultSet} proxies.
     *
     * @return A result set proxy
     */
    public static ResultSet createProxy() {
        return TestResultSet.createProxy(new TestResultSet());
    }

    /**
     * Factory method for creating {@link ResultSet} proxies.
     *
     * @param invocationHandler Invocation Handler
     * @return A result set proxy
     */
    public static ResultSet createProxy(final InvocationHandler invocationHandler) {
        final ClassLoader classLoader = ResultSet.class.getClassLoader();
        final Class<?>[] interfaces = new Class[] { ResultSet.class };
        return (ResultSet)Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }

    /**
     * Create a proxy ResultSet.
     */
    public TestResultSet() {
        this(TestResultSetMetaData.createProxy());
    }

    /**
     * Create a proxy ResultSet with the specified meta data.
     *
     * @param resultSetMetaData The result set meta data
     */
    public TestResultSet(final ResultSetMetaData resultSetMetaData) {
        this.resultSetMetaData = resultSetMetaData;
    }

    /**
     * Handles method invocation on the ResultSet proxy.
     *
     * @param proxy The proxy ResultSet object
     * @param method the method being invoked
     * @param args The method arguments
     * @return The result of invoking the method.
     * @throws Throwable if an error occurs.
     */
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String methodName = method.getName();
        if ("close".equals(methodName)) {
            return null;
        } if ("getMetaData".equals(methodName)) {
            return getMetaData();
        } if ("getObject".equals(methodName)) {
            return getObject(columnName(args[0]));
        } if ("getDate".equals(methodName)) {
            return getDate(columnName(args[0]));
        } if ("getTime".equals(methodName)) {
            return getTime(columnName(args[0]));
        } if ("getTimestamp".equals(methodName)) {
            return getTimestamp(columnName(args[0]));
        } if ("next".equals(methodName)) {
            return (next() ? Boolean.TRUE : Boolean.FALSE);
        } if ("updateObject".equals(methodName)) {
            updateObject((String)args[0], args[1]);
            return null;
        }

        throw new UnsupportedOperationException(methodName + " not implemented");
    }

    private String columnName(final Object arg) throws SQLException {
        if (arg instanceof Integer) {
            return resultSetMetaData.getColumnName(((Integer)arg).intValue());
        } else {
            return (String)arg;
        }
    }

    // ---------------------------------------------------- Implemented Methods


    public void close() throws SQLException {
        // No action required
    }


    public ResultSetMetaData getMetaData() throws SQLException {
        return resultSetMetaData;
    }


    public Object getObject(final String columnName) throws SQLException {
        if (row > 5) {
            throw new SQLException("No current row");
        }
        if ("bigDecimalProperty".equals(columnName)) {
            return (new BigDecimal(123.45));
        } else if ("booleanProperty".equals(columnName)) {
            if ((row % 2) == 0) {
                return (Boolean.TRUE);
            } else {
                return (Boolean.FALSE);
            }
        } else if ("byteProperty".equals(columnName)) {
            return (new Byte((byte) row));
        } else if ("dateProperty".equals(columnName)) {
            return (new Date(timestamp));
        } else if ("doubleProperty".equals(columnName)) {
            return (new Double(321.0));
        } else if ("floatProperty".equals(columnName)) {
            return (new Float((float) 123.0));
        } else if ("intProperty".equals(columnName)) {
            return (new Integer(100 + row));
        } else if ("longProperty".equals(columnName)) {
            return (new Long(200 + row));
        } else if ("nullProperty".equals(columnName)) {
            return (null);
        } else if ("shortProperty".equals(columnName)) {
            return (new Short((short) (300 + row)));
        } else if ("stringProperty".equals(columnName)) {
            return ("This is a string");
        } else if ("timeProperty".equals(columnName)) {
            return (new Time(timestamp));
        } else if ("timestampProperty".equals(columnName)) {
            return (new Timestamp(timestamp));
        } else {
            throw new SQLException("Unknown column name " + columnName);
        }
    }

    public Date getDate(final String columnName) throws SQLException {
        return (new Date(timestamp));
    }

    public Time getTime(final String columnName) throws SQLException {
        return (new Time(timestamp));
    }

    public Timestamp getTimestamp(final String columnName) throws SQLException {
        return (new Timestamp(timestamp));
    }

    public boolean next() throws SQLException {
        if (row++ < 5) {
            return (true);
        } else {
            return (false);
        }
    }


    public void updateObject(final String columnName, final Object x)
        throws SQLException {
        if (row > 5) {
            throw new SQLException("No current row");
        }
        // FIXME - updateObject()
    }


    // -------------------------------------------------- Unimplemented Methods


    public boolean absolute(final int row) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void afterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void beforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int findColumn(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean first() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Array getArray(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Array getArray(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public InputStream getAsciiStream(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public InputStream getAsciiStream(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public BigDecimal getBigDecimal(final int columnIndex, final int scale)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public BigDecimal getBigDecimal(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    /** @deprecated */
    @Deprecated
    public BigDecimal getBigDecimal(final String columnName, final int scale)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public InputStream getBinaryStream(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public InputStream getBinaryStream(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Blob getBlob(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Blob getBlob(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean getBoolean(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean getBoolean(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public byte getByte(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public byte getByte(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public byte[] getBytes(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public byte[] getBytes(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Reader getCharacterStream(final int columnIndex)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Reader getCharacterStream(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Clob getClob(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Clob getClob(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getConcurrency() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Date getDate(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }




    public Date getDate(final String columnName, final Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public double getDouble(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public double getDouble(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public float getFloat(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public float getFloat(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getInt(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getInt(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public long getLong(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public long getLong(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Object getObject(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Object getObject(final int columnIndex, final Map<?, ?> map) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Object getObject(final String columnName, final Map<?, ?> map) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Ref getRef(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Ref getRef(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public short getShort(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public short getShort(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Statement getStatement() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public String getString(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public String getString(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Time getTime(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }



    public Time getTime(final String columnName, final Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Timestamp getTimestamp(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Timestamp getTimestamp(final int columnIndex, final Calendar cal)
        throws SQLException {
        throw new UnsupportedOperationException();
    }



    public Timestamp getTimestamp(final String columnName, final Calendar cal)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getType() throws SQLException {
        throw new UnsupportedOperationException();
    }


    /** @deprecated */
    @Deprecated
    public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    /** @deprecated */
    @Deprecated
    public InputStream getUnicodeStream(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public URL getURL(final int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public URL getURL(final String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isAfterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isBeforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isLast() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean last() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean previous() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean relative(final int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setFetchDirection(final int direction) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setFetchSize(final int size) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateArray(final int columnPosition, final Array x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateArray(final String columnName, final Array x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateAsciiStream(final int columnPosition, final InputStream x, final int len)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateAsciiStream(final String columnName, final InputStream x, final int len)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBigDecimal(final int columnPosition, final BigDecimal x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBigDecimal(final String columnName, final BigDecimal x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBinaryStream(final int columnPosition, final InputStream x, final int len)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBinaryStream(final String columnName, final InputStream x, final int len)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBlob(final int columnPosition, final Blob x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBlob(final String columnName, final Blob x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBoolean(final int columnPosition, final boolean x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBoolean(final String columnName, final boolean x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateByte(final int columnPosition, final byte x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateByte(final String columnName, final byte x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBytes(final int columnPosition, final byte x[])
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBytes(final String columnName, final byte x[])
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateCharacterStream(final int columnPosition, final Reader x, final int len)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateCharacterStream(final String columnName, final Reader x, final int len)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateClob(final int columnPosition, final Clob x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateClob(final String columnName, final Clob x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateDate(final int columnPosition, final Date x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateDate(final String columnName, final Date x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateDouble(final int columnPosition, final double x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateDouble(final String columnName, final double x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateFloat(final int columnPosition, final float x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateFloat(final String columnName, final float x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateInt(final int columnPosition, final int x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateInt(final String columnName, final int x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateLong(final int columnPosition, final long x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateLong(final String columnName, final long x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateNull(final int columnPosition)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateNull(final String columnName)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateObject(final int columnPosition, final Object x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateObject(final int columnPosition, final Object x, final int scale)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateObject(final String columnName, final Object x, final int scale)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateRef(final int columnPosition, final Ref x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateRef(final String columnName, final Ref x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateShort(final int columnPosition, final short x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateShort(final String columnName, final short x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateString(final int columnPosition, final String x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateString(final String columnName, final String x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateTime(final int columnPosition, final Time x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateTime(final String columnName, final Time x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateTimestamp(final int columnPosition, final Timestamp x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateTimestamp(final String columnName, final Timestamp x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean wasNull() throws SQLException {
        throw new UnsupportedOperationException();
    }


}
