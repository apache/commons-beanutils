/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/JDBCDynaClass.java,v 1.3 2003/10/09 20:43:15 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/09 20:43:15 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
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
import java.util.Map;

/**
 * <p>Provides common logic for JDBC implementations of {@link DynaClass}.</p>
 *
 * @author   Craig R. McClanahan
 * @author   George Franciscus
 * @version $Revision: 1.3 $ $Date: 2003/10/09 20:43:15 $
 */

abstract class JDBCDynaClass implements DynaClass, Serializable {

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
            throw new IllegalArgumentException("No property name specified");
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
     */
    protected DynaProperty createDynaProperty(
                                    ResultSetMetaData metadata,
                                    int i)
                                    throws SQLException {

        String name = null;
        if (lowerCase) {
            name = metadata.getColumnName(i).toLowerCase();
        } else {
            name = metadata.getColumnName(i);
        }
        String className = null;
        try {
            className = metadata.getColumnClassName(i);
        } catch (SQLException e) {
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

}
