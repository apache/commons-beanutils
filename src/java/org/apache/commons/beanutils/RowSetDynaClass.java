/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/RowSetDynaClass.java,v 1.2 2003/01/05 05:45:41 craigmcc Exp $
 * $Revision: 1.2 $
 * $Date: 2003/01/05 05:45:41 $
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


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
 * <p><strong>FIXME</strong> - This class shares a lot of behavior with
 * <code>ResultSetDynaClass</code>.  We could not simply subclass it,
 * because the existence of the <code>resultSet</code> instance variable
 * would cause serializability problems.  The common features should be
 * factored out so that they can be shared.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2003/01/05 05:45:41 $
 */

public class RowSetDynaClass implements DynaClass, Serializable {


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


    // ----------------------------------------------------- Instance Variables


    /**
     * <p>Flag defining whether column names should be lower cased when
     * converted to property names.</p>
     */
    protected boolean lowerCase = true;


    /**
     * <p>The set of dynamic properties that are part of this
     * {@link DynaClass}.</p>
     */
    protected DynaProperty properties[] = null;


    /**
     * <p>The set of dynamic properties that are part of this
     * {@link DynaClass}, keyed by the property name.  Individual descriptor
     * instances will be the same instances as those in the
     * <code>properties</code> list.</p>
     */
    protected Map propertiesMap = new HashMap();


    /**
     * <p>The list of {@link DynaBean}s representing the contents of
     * the original <code>ResultSet</code> on which this
     * {@link RowSetDynaClass} was based.</p>
     */
    protected List rows = new ArrayList();


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
     * @exception IllegalArgumentException if no property name is specified
     */
    public DynaProperty getDynaProperty(String name) {

        if (name == null) {
            throw new IllegalArgumentException
                    ("No property name specified");
        }
        return ((DynaProperty) propertiesMap.get(name));

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
     * @exception IllegalAccessException if the Class or the appropriate
     *  constructor is not accessible
     * @exception InstantiationException if this Class represents an abstract
     *  class, an array class, a primitive type, or void; or if instantiation
     *  fails for some other reason
     */
    public DynaBean newInstance()
            throws IllegalAccessException, InstantiationException {

        throw new UnsupportedOperationException("newInstance() not supported");

    }


    // --------------------------------------------------------- Public Methods


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


    /**
     * <p>Introspect the metadata associated with our result set, and populate
     * the <code>properties</code> and <code>propertiesMap</code> instance
     * variables.</p>
     *
     * @param resultSet The <code>resultSet</code> whose metadata is to
     *  be introspected
     *
     * @exception SQLException if an error is encountered processing the
     *  result set metadata
     */
    protected void introspect(ResultSet resultSet) throws SQLException {

        // Accumulate an ordered list of DynaProperties
        ArrayList list = new ArrayList();
        ResultSetMetaData metadata = resultSet.getMetaData();
        int n = metadata.getColumnCount();
        for (int i = 1; i <= n; i++) { // JDBC is one-relative!
            DynaProperty dynaProperty = createDynaProperty(metadata, i);
            if (dynaProperty != null) {
                list.add(dynaProperty);
            }
        }

        // Convert this list into the internal data structures we need
        properties =
            (DynaProperty[]) list.toArray(new DynaProperty[list.size()]);
        for (int i = 0; i < properties.length; i++) {
            propertiesMap.put(properties[i].getName(), properties[i]);
        }

    }


    /**
     * <p>Factory method to create a new DynaProperty for the given index
     * into the result set metadata.</p>
     * 
     * @param metadata is the result set metadata
     * @param i is the column index in the metadata
     * @return the newly created DynaProperty instance
     */
    protected DynaProperty createDynaProperty
        (ResultSetMetaData metadata, int i) throws SQLException {

        String name = null;
        if (lowerCase) {
            name = metadata.getColumnName(i).toLowerCase();
        } else {
            name = metadata.getColumnName(i);
        }
        String className = null;
        try {
            className = metadata.getColumnClassName(i);
        }
        catch (SQLException e) {
            // this is a patch for HsqlDb to ignore exceptions
            // thrown by its metadata implementation
        }

        // Default to Object type if no class name could be retrieved
        // from the metadata
        Class clazz = Object.class;
        if (className != null) {
            clazz = loadClass(className);
        }
        return new DynaProperty(name, clazz);

    }


    /**
     * <p>Loads and returns the <code>Class</code> of the given name.
     * By default, a load from the thread context class loader is attempted.
     * If there is no such class loader, the class loader used to load this
     * class will be utilized.</p>
     *
     * @exception SQLException if an exception was thrown trying to load
     *  the specified class
     */        
    protected Class loadClass(String className) throws SQLException {

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = this.getClass().getClassLoader();
            }
            return (cl.loadClass(className));
        } catch (Exception e) {
            throw new SQLException("Cannot load column class '" +
                                   className + "': " + e);
        }

    }


}
