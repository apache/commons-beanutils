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

package org.apache.commons.beanutils2;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

/**
 * <p>
 * This comparator compares two beans by the specified bean property. It is also possible to compare beans based on
 * nested, indexed, combined, mapped bean properties. Please see the {@link PropertyUtilsBean} documentation for all
 * property name possibilities.
 *
 * </p>
 * <p>
 * <strong>Note:</strong> The BeanComparator passes the values of the specified bean property to an internal natural
 * order {@link Comparator}, if no comparator is specified in the constructor. If you are comparing two beans based on a
 * property that could contain "null" values, a suitable {@code Comparator} or Apache Commons Collection
 * {@code ComparatorChain} should be supplied in the constructor. Note that the passed in {@code Comparator} must
 * be able to handle the passed in objects. Because the type of the property to be compared is not known at compile time
 * no type checks can be performed by the compiler. Thus {@code ClassCastException} exceptions can be thrown if
 * unexpected property values occur.
 * </p>
 *
 * @param <T> the type of beans to be compared by this {@code Comparator}
 * @param <V> the type of property to compare
 */
public class BeanComparator<T, V> implements Comparator<T>, Serializable {

    /**
     * A {@link Comparator Comparator} that compares {@link Comparable Comparable} objects.
     * <p>
     * This Comparator is useful, for example, for enforcing the natural order in custom implementations of
     * {@link java.util.SortedSet SortedSet} and {@link java.util.SortedMap SortedMap}.
     * </p>
     *
     * @param <E> the type of objects compared by this comparator
     * @see java.util.Collections#reverseOrder()
     */
    private static final class NaturalOrderComparator<E extends Comparable<? super E>>
            implements Comparator<E>, Serializable {

        /** Serialization version. */
        private static final long serialVersionUID = -291439688585137865L;

        /** The singleton instance. */
        @SuppressWarnings("rawtypes")
        public static final NaturalOrderComparator INSTANCE = new NaturalOrderComparator();

        /**
         * Private constructor to prevent instantiation. Only use INSTANCE.
         */
        private NaturalOrderComparator() {
        }

        /**
         * Compare the two {@link Comparable Comparable} arguments. This method is equivalent to:
         *
         * <pre>
         * ((Comparable) obj1).compareTo(obj2)
         * </pre>
         */
        @Override
        public int compare(final E obj1, final E obj2) {
            return obj1.compareTo(obj2);
        }

        @Override
        public boolean equals(final Object object) {
            return this == object || null != object && object.getClass().equals(this.getClass());
        }

        @Override
        public int hashCode() {
            return "NaturalOrderComparator".hashCode();
        }
    }
    private static final long serialVersionUID = 1L;
    private String property;

    private final Comparator<V> comparator;

    /**
     * <p>
     * Constructs a Bean Comparator without a property set.
     * </p>
     * <p>
     * <strong>Note</strong> that this is intended to be used only in bean-centric environments.
     * </p>
     * <p>
     * Until {@link #setProperty} is called with a non-null value. this comparator will compare the Objects only.
     * </p>
     */
    public BeanComparator() {
        this(null);
    }

    /**
     * <p>
     * Constructs a property-based comparator for beans. This compares two beans by the property specified in the
     * property parameter. This constructor creates a {@code BeanComparator} that uses a
     * {@code ComparableComparator} to compare the property values.
     * </p>
     *
     * <p>
     * Passing "null" to this constructor will cause the BeanComparator to compare objects based on natural order, that
     * is {@code java.lang.Comparable}.
     * </p>
     *
     * @param property String Name of a bean property, which may contain the name of a simple, nested, indexed, mapped,
     *        or combined property. See {@link PropertyUtilsBean} for property query language syntax. If the property
     *        passed in is null then the actual objects will be compared
     */
    public BeanComparator(final String property) {
        this(property, NaturalOrderComparator.INSTANCE);
    }

    /**
     * Constructs a property-based comparator for beans. This constructor creates a BeanComparator that uses the
     * supplied Comparator to compare the property values.
     *
     * @param property Name of a bean property, can contain the name of a simple, nested, indexed, mapped, or combined
     *        property. See {@link PropertyUtilsBean} for property query language syntax.
     * @param comparator BeanComparator will pass the values of the specified bean property to this Comparator. If your
     *        bean property is not a comparable or contains null values, a suitable comparator may be supplied in this
     *        constructor.
     */
    public BeanComparator(final String property, final Comparator<V> comparator) {
        setProperty(property);
        this.comparator = comparator != null ? comparator : NaturalOrderComparator.INSTANCE;
    }

    /**
     * Compare two JavaBeans by their shared property. If {@link #getProperty} is null then the actual objects will be
     * compared.
     *
     * @param o1 Object The first bean to get data from to compare against
     * @param o2 Object The second bean to get data from to compare
     * @return int negative or positive based on order
     */
    @Override
    public int compare(final T o1, final T o2) {

        if (property == null) {
            // compare the actual objects
            return internalCompare(o1, o2);
        }

        try {
            final Object value1 = PropertyUtils.getProperty(o1, property);
            final Object value2 = PropertyUtils.getProperty(o2, property);
            return internalCompare(value1, value2);
        } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getClass().getSimpleName()+": " + e.toString());
        }
    }

    /**
     * Two {@code BeanComparator}'s are equals if and only if the wrapped comparators and the property names to be
     * compared are equal.
     *
     * @param o Comparator to compare to
     * @return whether the comparators are equal or not
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeanComparator)) {
            return false;
        }

        final BeanComparator<?, ?> beanComparator = (BeanComparator<?, ?>) o;

        if (!comparator.equals(beanComparator.comparator)) {
            return false;
        }
        if (property == null) {
            return beanComparator.property == null;
        }
        if (!property.equals(beanComparator.property)) {
            return false;
        }

        return true;
    }

    /**
     * Gets the Comparator being used to compare beans.
     *
     * @return the Comparator being used to compare beans
     */
    public Comparator<V> getComparator() {
        return comparator;
    }

    /**
     * Gets the property attribute of the BeanComparator
     *
     * @return String method name to call to compare. A null value indicates that the actual objects will be compared
     */
    public String getProperty() {
        return property;
    }

    /**
     * Hashcode compatible with equals.
     *
     * @return the hash code for this comparator
     */
    @Override
    public int hashCode() {
        return comparator.hashCode();
    }

    /**
     * Compares the given values using the internal {@code Comparator}. <em>Note</em>: This comparison cannot be
     * performed in a type-safe way; so {@code ClassCastException} exceptions may be thrown.
     *
     * @param val1 the first value to be compared
     * @param val2 the second value to be compared
     * @return the result of the comparison
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private int internalCompare(final Object val1, final Object val2) {
        return ((Comparator) comparator).compare(val1, val2);
    }

    /**
     * Sets the method to be called to compare two JavaBeans
     *
     * @param property String method name to call to compare If the property passed in is null then the actual objects
     *        will be compared
     */
    public void setProperty(final String property) {
        this.property = property;
    }
}
