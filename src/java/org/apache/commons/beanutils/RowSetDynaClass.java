/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/RowSetDynaClass.java,v 1.4 2003/03/13 18:45:38 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/03/13 18:45:38 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Implementation of {@link DynaClass} that creates an in-memory collection
 * of {@link DynaBean}s representing the results of an SQL query.  Once the
 * {@link DynaClass} instance has been created, the JDBC <code>ResultSet</code>
 * and <code>Statement</code> on which it is based can be closed, and the
 * underlying <code>Connection</code> can be returned to its connection pool
 * (if you are using one).</p>
 *
 * <p>The normal usage pattern is something like:</p>
 * <pre>
 *   Connection conn = ...;  // Acquire connection from pool
 *   Statement stmt = conn.createStatement();
 *   ResultSet rs = stmt.executeQuery("SELECT ...");
 *   RowSetDynaClass rsdc = new RowSetDynaClass(rs);
 *   rs.close();
 *   stmt.close();
 *   ...;                    // Return connection to pool
 *   List rows = rsdc.getRows();
 *   ...;                   // Process the rows as desired
 * </pre>
 *
 * <p>Each column in the result set will be represented as a {@link DynaBean}
 * property of the corresponding name (optionally forced to lower case
 * for portability).  There will be one {@link DynaBean} in the
 * <code>List</code> returned by <code>getRows()</code> for each
 * row in the original <code>ResultSet</code>.</p>
 *
 * <p>In general, instances of {@link RowSetDynaClass} can be serialized
 * and deserialized, which will automatically include the list of
 * {@link DynaBean}s representing the data content.  The only exception
 * to this rule would be when the underlying property values that were
 * copied from the <code>ResultSet</code> originally cannot themselves
 * be serialized.  Therefore, a {@link RowSetDynaClass} makes a very
 * convenient mechanism for transporting data sets to remote Java-based
 * application components.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.4 $ $Date: 2003/03/13 18:45:38 $
 */

public class RowSetDynaClass extends JDBCDynaClass implements DynaClass, Serializable {


    // ----------------------------------------------------------- Constructors


    /**
     * <p>Construct a new {@link RowSetDynaClass} for the specified
     * <code>ResultSet</code>.  The property names corresponding
     * to column names in the result set will be lower cased.</p>
     *
     * @param resultSet The result set to be wrapped
     *
     * @exception NullPointerException if <code>resultSet</code>
     *  is <code>null</code>
     * @exception SQLException if the metadata for this result set
     *  cannot be introspected
     */
    public RowSetDynaClass(ResultSet resultSet) throws SQLException {

        this(resultSet, true);

    }


    /**
     * <p>Construct a new {@link RowSetDynaClass} for the specified
     * <code>ResultSet</code>.  The property names corresponding
     * to the column names in the result set will be lower cased or not,
     * depending on the specified <code>lowerCase</code> value.</p>
     *
     * <p><strong>WARNING</strong> - If you specify <code>false</code>
     * for <code>lowerCase</code>, the returned property names will
     * exactly match the column names returned by your JDBC driver.
     * Because different drivers might return column names in different
     * cases, the property names seen by your application will vary
     * depending on which JDBC driver you are using.</p>
     *
     * @param resultSet The result set to be wrapped
     * @param lowerCase Should property names be lower cased?
     *
     * @exception NullPointerException if <code>resultSet</code>
     *  is <code>null</code>
     * @exception SQLException if the metadata for this result set
     *  cannot be introspected
     */
    public RowSetDynaClass(ResultSet resultSet, boolean lowerCase)
        throws SQLException {

        if (resultSet == null) {
            throw new NullPointerException();
        }
        this.lowerCase = lowerCase;
        introspect(resultSet);
        copy(resultSet);

    }


        /**
     * <p>The list of {@link DynaBean}s representing the contents of
     * the original <code>ResultSet</code> on which this
     * {@link RowSetDynaClass} was based.</p>
     */
    protected List rows = new ArrayList();


    /**
     * <p>Return a <code>List</code> containing the {@link DynaBean}s that
     * represent the contents of each <code>Row</code> from the
     * <code>ResultSet</code> that was the basis of this
     * {@link RowSetDynaClass} instance.  These {@link DynaBean}s are
     * disconnected from the database itself, so there is no problem with
     * modifying the contents of the list, or the values of the properties
     * of these {@link DynaBean}s.  However, it is the application's
     * responsibility to persist any such changes back to the database,
     * if it so desires.</p>
     */
    public List getRows() {

        return (this.rows);

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * <p>Copy the column values for each row in the specified
     * <code>ResultSet</code> into a newly created {@link DynaBean}, and add
     * this bean to the list of {@link DynaBean}s that will later by
     * returned by a call to <code>getRows()</code>.</p>
     *
     * @param resultSet The <code>ResultSet</code> whose data is to be
     *  copied
     *
     * @exception SQLException if an error is encountered copying the data
     */
    protected void copy(ResultSet resultSet) throws SQLException {

        while (resultSet.next()) {
            DynaBean bean = new BasicDynaBean(this);
            for (int i = 0; i < properties.length; i++) {
                String name = properties[i].getName();
                bean.set(name, resultSet.getObject(name));
            }
            rows.add(bean);
        }

    }

}
