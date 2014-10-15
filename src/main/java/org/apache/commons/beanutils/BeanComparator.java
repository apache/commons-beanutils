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
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import org.apache.commons.collections.comparators.ComparableComparator;

/**
 * <p>
 * This comparator compares two beans by the specified bean property.
 * It is also possible to compare beans based on nested, indexed,
 * combined, mapped bean properties. Please see the {@link PropertyUtilsBean}
 * documentation for all property name possibilities.
 *
 * </p><p>
 * <strong>Note:</strong> The BeanComparator passes the values of the specified
 * bean property to a ComparableComparator, if no comparator is
 * specified in the constructor. If you are comparing two beans based
 * on a property that could contain "null" values, a suitable <code>Comparator</code>
 * or <code>ComparatorChain</code> should be supplied in the constructor.
 * Note that the passed in {@code Comparator} must be able to handle the
 * passed in objects. Because the type of the property to be compared is not
 * known at compile time no type checks can be performed by the compiler.
 * Thus {@code ClassCastException} exceptions can be thrown if unexpected
 * property values occur.
 * </p>
 *
 * @param <T> the type of beans to be compared by this {@code Comparator}
 * @version $Id$
 */
public class BeanComparator<T> implements Comparator<T>, Serializable {

    private String property;
    private final Comparator<?> comparator;

    /**
     * <p>Constructs a Bean Comparator without a property set.
     * </p><p>
     * <strong>Note</strong> that this is intended to be used
     * only in bean-centric environments.
     * </p><p>
     * Until {@link #setProperty} is called with a non-null value.
     * this comparator will compare the Objects only.
     * </p>
     */
    public BeanComparator() {
        this( null );
    }

    /**
     * <p>Constructs a property-based comparator for beans.
     * This compares two beans by the property
     * specified in the property parameter. This constructor creates
     * a <code>BeanComparator</code> that uses a <code>ComparableComparator</code>
     * to compare the property values.
     * </p>
     *
     * <p>Passing "null" to this constructor will cause the BeanComparator
     * to compare objects based on natural order, that is
     * <code>java.lang.Comparable</code>.
     * </p>
     *
     * @param property String Name of a bean property, which may contain the
     * name of a simple, nested, indexed, mapped, or combined
     * property. See {@link PropertyUtilsBean} for property query language syntax.
     * If the property passed in is null then the actual objects will be compared
     */
    public BeanComparator( final String property ) {
        this( property, ComparableComparator.getInstance() );
    }

    /**
     * Constructs a property-based comparator for beans.
     * This constructor creates
     * a BeanComparator that uses the supplied Comparator to compare
     * the property values.
     *
     * @param property Name of a bean property, can contain the name
     * of a simple, nested, indexed, mapped, or combined
     * property. See {@link PropertyUtilsBean} for property query language
     * syntax.
     * @param comparator BeanComparator will pass the values of the
     * specified bean property to this Comparator.
     * If your bean property is not a comparable or
     * contains null values, a suitable comparator
     * may be supplied in this constructor.
     */
    public BeanComparator( final String property, final Comparator<?> comparator ) {
        setProperty( property );
        if (comparator != null) {
            this.comparator = comparator;
        } else {
            this.comparator = ComparableComparator.getInstance();
        }
    }

    /**
     * Sets the method to be called to compare two JavaBeans
     *
     * @param property String method name to call to compare
     * If the property passed in is null then the actual objects will be compared
     */
    public void setProperty( final String property ) {
        this.property = property;
    }


    /**
     * Gets the property attribute of the BeanComparator
     *
     * @return String method name to call to compare.
     * A null value indicates that the actual objects will be compared
     */
    public String getProperty() {
        return property;
    }


    /**
     * Gets the Comparator being used to compare beans.
     *
     * @return the Comparator being used to compare beans
     */
    public Comparator<?> getComparator() {
        return comparator;
    }


    /**
     * Compare two JavaBeans by their shared property.
     * If {@link #getProperty} is null then the actual objects will be compared.
     *
     * @param  o1 Object The first bean to get data from to compare against
     * @param  o2 Object The second bean to get data from to compare
     * @return int negative or positive based on order
     */
    public int compare( final T o1, final T o2 ) {

        if ( property == null ) {
            // compare the actual objects
            return internalCompare( o1, o2 );
        }

        try {
            final Object value1 = PropertyUtils.getProperty( o1, property );
            final Object value2 = PropertyUtils.getProperty( o2, property );
            return internalCompare( value1, value2 );
        }
        catch ( final IllegalAccessException iae ) {
            throw new RuntimeException( "IllegalAccessException: " + iae.toString() );
        }
        catch ( final InvocationTargetException ite ) {
            throw new RuntimeException( "InvocationTargetException: " + ite.toString() );
        }
        catch ( final NoSuchMethodException nsme ) {
            throw new RuntimeException( "NoSuchMethodException: " + nsme.toString() );
        }
    }

    /**
     * Two <code>BeanComparator</code>'s are equals if and only if
     * the wrapped comparators and the property names to be compared
     * are equal.
     * @param  o Comparator to compare to
     * @return whether the the comparators are equal or not
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeanComparator)) {
            return false;
        }

        final BeanComparator<?> beanComparator = (BeanComparator<?>) o;

        if (!comparator.equals(beanComparator.comparator)) {
            return false;
        }
        if (property != null)
        {
            if (!property.equals(beanComparator.property)) {
                return false;
            }
        }
        else
        {
            return (beanComparator.property == null);
        }

        return true;
    }

    /**
     * Hashcode compatible with equals.
     * @return the hash code for this comparator
     */
    @Override
    public int hashCode() {
        int result;
        result = comparator.hashCode();
        return result;
    }

    /**
     * Compares the given values using the internal {@code Comparator}.
     * <em>Note</em>: This comparison cannot be performed in a type-safe way; so
     * {@code ClassCastException} exceptions may be thrown.
     *
     * @param val1 the first value to be compared
     * @param val2 the second value to be compared
     * @return the result of the comparison
     */
    private int internalCompare(final Object val1, final Object val2) {
        @SuppressWarnings("rawtypes")
        final
        // to make the compiler happy
        Comparator c = comparator;
        return c.compare(val1, val2);
    }
}
