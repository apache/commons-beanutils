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


import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * <p>Implementation of <code>java.util.Iterator</code> returned by the
 * <code>iterator()</code> method of {@link ResultSetDynaClass}.  Each
 * object returned by this iterator will be a {@link DynaBean} that
 * represents a single row from the result set being wrapped.</p>
 *
 * @version $Id$
 */

public class ResultSetIterator implements DynaBean, Iterator<DynaBean> {


    // ------------------------------------------------------------ Constructor


    /**
     * <p>Construct an <code>Iterator</code> for the result set being wrapped
     * by the specified {@link ResultSetDynaClass}.</p>
     *
     * @param dynaClass The {@link ResultSetDynaClass} wrapping the
     *  result set we will iterate over
     */
    ResultSetIterator(final ResultSetDynaClass dynaClass) {

        this.dynaClass = dynaClass;

    }


    // ----------------------------------------------------- Instance Variables



    /**
     * <p>Flag indicating whether the result set is currently positioned at a
     * row for which we have not yet returned an element in the iteration.</p>
     */
    protected boolean current = false;


    /**
     * <p>The {@link ResultSetDynaClass} we are associated with.</p>
     */
    protected ResultSetDynaClass dynaClass = null;


    /**
     * <p>Flag indicating whether the result set has indicated that there are
     * no further rows.</p>
     */
    protected boolean eof = false;


    // ------------------------------------------------------- DynaBean Methods


    /**
     * Does the specified mapped property contain a value for the specified
     * key value?
     *
     * @param name Name of the property to check
     * @param key Name of the key to check
     * @return <code>true<code> if the mapped property contains a value for
     * the specified key, otherwise <code>false</code>
     *
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     */
    public boolean contains(final String name, final String key) {

        throw new UnsupportedOperationException
            ("FIXME - mapped properties not currently supported");

    }


    /**
     * Return the value of a simple property with the specified name.
     *
     * @param name Name of the property whose value is to be retrieved
     * @return The property's value
     *
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     */
    public Object get(final String name) {

        if (dynaClass.getDynaProperty(name) == null) {
            throw new IllegalArgumentException(name);
        }
        try {
            return dynaClass.getObjectFromResultSet(name);
        } catch (final SQLException e) {
            throw new RuntimeException
                ("get(" + name + "): SQLException: " + e);
        }

    }


    /**
     * Return the value of an indexed property with the specified name.
     *
     * @param name Name of the property whose value is to be retrieved
     * @param index Index of the value to be retrieved
     * @return The indexed property's value
     *
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     * @throws IllegalArgumentException if the specified property
     *  exists, but is not indexed
     * @throws IndexOutOfBoundsException if the specified index
     *  is outside the range of the underlying property
     * @throws NullPointerException if no array or List has been
     *  initialized for this property
     */
    public Object get(final String name, final int index) {

        throw new UnsupportedOperationException
            ("FIXME - indexed properties not currently supported");

    }


    /**
     * Return the value of a mapped property with the specified name,
     * or <code>null</code> if there is no value for the specified key.
     *
     * @param name Name of the property whose value is to be retrieved
     * @param key Key of the value to be retrieved
     * @return The mapped property's value
     *
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     * @throws IllegalArgumentException if the specified property
     *  exists, but is not mapped
     */
    public Object get(final String name, final String key) {

        throw new UnsupportedOperationException
            ("FIXME - mapped properties not currently supported");

    }


    /**
     * Return the <code>DynaClass</code> instance that describes the set of
     * properties available for this DynaBean.
     *
     * @return The associated DynaClass
     */
    public DynaClass getDynaClass() {

        return (this.dynaClass);

    }


    /**
     * Remove any existing value for the specified key on the
     * specified mapped property.
     *
     * @param name Name of the property for which a value is to
     *  be removed
     * @param key Key of the value to be removed
     *
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     */
    public void remove(final String name, final String key) {

        throw new UnsupportedOperationException
            ("FIXME - mapped operations not currently supported");

    }


    /**
     * Set the value of a simple property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param value Value to which this property is to be set
     *
     * @throws ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     * @throws NullPointerException if an attempt is made to set a
     *  primitive property to null
     */
    public void set(final String name, final Object value) {

        if (dynaClass.getDynaProperty(name) == null) {
            throw new IllegalArgumentException(name);
        }
        try {
            dynaClass.getResultSet().updateObject(name, value);
        } catch (final SQLException e) {
            throw new RuntimeException
                ("set(" + name + "): SQLException: " + e);
        }

    }


    /**
     * Set the value of an indexed property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param index Index of the property to be set
     * @param value Value to which this property is to be set
     *
     * @throws ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     * @throws IllegalArgumentException if the specified property
     *  exists, but is not indexed
     * @throws IndexOutOfBoundsException if the specified index
     *  is outside the range of the underlying property
     */
    public void set(final String name, final int index, final Object value) {

        throw new UnsupportedOperationException
            ("FIXME - indexed properties not currently supported");

    }


    /**
     * Set the value of a mapped property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param key Key of the property to be set
     * @param value Value to which this property is to be set
     *
     * @throws ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     * @throws IllegalArgumentException if the specified property
     *  exists, but is not mapped
     */
    public void set(final String name, final String key, final Object value) {

        throw new UnsupportedOperationException
            ("FIXME - mapped properties not currently supported");

    }


    // ------------------------------------------------------- Iterator Methods


    /**
     * <p>Return <code>true</code> if the iteration has more elements.</p>
     *
     * @return <code>true</code> if the result set has another
     * row, otherwise <code>false</code>
     */
    public boolean hasNext() {

        try {
            advance();
            return (!eof);
        } catch (final SQLException e) {
            throw new RuntimeException("hasNext():  SQLException:  " + e);
        }

    }


    /**
     * <p>Return the next element in the iteration.</p>
     *
     * @return advance to the new row and return this
     */
    public DynaBean next() {

        try {
            advance();
            if (eof) {
                throw new NoSuchElementException();
            }
            current = false;
            return (this);
        } catch (final SQLException e) {
            throw new RuntimeException("next():  SQLException:  " + e);
        }

    }


    /**
     * <p>Remove the current element from the iteration.  This method is
     * not supported.</p>
     */
    public void remove() {

        throw new UnsupportedOperationException("remove()");

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * <p>Advance the result set to the next row, if there is not a current
     * row (and if we are not already at eof).</p>
     *
     * @throws SQLException if the result set throws an exception
     */
    protected void advance() throws SQLException {

        if (!current && !eof) {
            if (dynaClass.getResultSet().next()) {
                current = true;
                eof = false;
            } else {
                current = false;
                eof = true;
            }
        }

    }


}
