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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * <h2><i>Lazy</i> DynaBean List.</h2>
 *
 * <p>There are two main purposes for this class:</p>
 *    <ul>
 *        <li>To provide <i>Lazy List</i> behavior - automatically
 *            <i>growing</i> and <i>populating</i> the {@code List}
 *            with either {@code DynaBean</code>, <code>java.util.Map}
 *            or POJO Beans.</li>
 *        <li>To provide a straight forward way of putting a Collection
 *            or Array into the lazy list <i>and</i> a straight forward
 *            way to get it out again at the end.</li>
 *    </ul>
 *
 * <p>All elements added to the List are stored as {@code DynaBean}'s:</p>
 * <ul>
 *    <li>{@code java.util.Map</code> elements are "wrapped" in a <code>LazyDynaMap}.</li>
 *    <li>POJO Bean elements are "wrapped" in a {@code WrapDynaBean}.</li>
 *    <li>{@code DynaBean}'s are stored un-changed.</li>
 * </ul>
 *
 * <h2>{@code toArray()}</h2>
 * <p>The {@code toArray()} method returns an array of the
 *    elements of the appropriate type. If the {@code LazyDynaList}
 *    is populated with {@code java.util.Map} objects a
 *    {@code Map[]} array is returned.
 *    If the list is populated with POJO Beans an appropriate
 *    array of the POJO Beans is returned. Otherwise a {@code DynaBean[]}
 *    array is returned.
 * </p>
 *
 * <h2>{@code toDynaBeanArray()}</h2>
 * <p>The {@code toDynaBeanArray()} method returns a
 *    {@code DynaBean[]} array of the elements in the List.
 * </p>
 *
 * <p><strong>N.B.</strong>All the elements in the List must be the
 *    same type. If the {@code DynaClass</code> or <code>Class}
 *    of the {@code LazyDynaList}'s elements is
 *    not specified, then it will be automatically set to the type
 *    of the first element populated.
 * </p>
 *
 * <h2>Example 1</h2>
 * <p>If you have an array of {@code java.util.Map[]} - you can put that into
 *    a {@code LazyDynaList}.</p>
 *
 * <pre><code>
 *    TreeMap[] myArray = .... // your Map[]
 *    List lazyList = new LazyDynaList(myArray);
 * </code></pre>
 *
 * <p>New elements of the appropriate Map type are
 *    automatically populated:</p>
 *
 * <pre><code>
 *    // get(index) automatically grows the list
 *    DynaBean newElement = (DynaBean)lazyList.get(lazyList.size());
 *    newElement.put("someProperty", "someValue");
 * </code></pre>
 *
 * <p>Once you've finished you can get back an Array of the
 *    elements of the appropriate type:</p>
 *
 * <pre><code>
 *    // Retrieve the array from the list
 *    TreeMap[] myArray = (TreeMap[])lazyList.toArray());
 * </code></pre>
 *
 *
 * <h2>Example 2</h2>
 * <p>Alternatively you can create an <i>empty</i> List and
 *    specify the Class for List's elements. The LazyDynaList
 *    uses the Class to automatically populate elements:</p>
 *
 * <pre><code>
 *    // e.g. For Maps
 *    List lazyList = new LazyDynaList(TreeMap.class);
 *
 *    // e.g. For POJO Beans
 *    List lazyList = new LazyDynaList(MyPojo.class);
 *
 *    // e.g. For DynaBeans
 *    List lazyList = new LazyDynaList(MyDynaBean.class);
 * </code></pre>
 *
 * <h2>Example 3</h2>
 * <p>Alternatively you can create an <i>empty</i> List and specify the
 *    DynaClass for List's elements. The LazyDynaList uses
 *    the DynaClass to automatically populate elements:</p>
 *
 * <pre><code>
 *    // e.g. For Maps
 *    DynaClass dynaClass = new LazyDynaMap(new HashMap());
 *    List lazyList = new LazyDynaList(dynaClass);
 *
 *    // e.g. For POJO Beans
 *    DynaClass dynaClass = (new WrapDynaBean(myPojo)).getDynaClass();
 *    List lazyList = new LazyDynaList(dynaClass);
 *
 *    // e.g. For DynaBeans
 *    DynaClass dynaClass = new BasicDynaClass(properties);
 *    List lazyList = new LazyDynaList(dynaClass);
 * </code></pre>
 *
 * <p><strong>N.B.</strong> You may wonder why control the type
 *    using a {@code DynaClass</code> rather than the <code>Class}
 *    as in the previous example - the reason is that some {@code DynaBean}
 *    implementations don't have a <i>default</i> empty constructor and
 *    therefore need to be instantiated using the {@code DynaClass.newInstance()}
 *    method.</p>
 *
 * <h2>Example 4</h2>
 * <p>A slight variation - set the element type using either
 *    the {@code setElementType(Class)} method or the
 *    {@code setElementDynaClass(DynaClass)} method - then populate
 *    with the normal {@code java.util.List} methods (i.e.
 *    {@code add()}, {@code addAll()} or {@code set()}).</p>
 *
 * <pre><code>
 *    // Create a new LazyDynaList (100 element capacity)
 *    LazyDynaList lazyList = new LazyDynaList(100);
 *
 *    // Either Set the element type...
 *    lazyList.setElementType(TreeMap.class);
 *
 *    // ...or the element DynaClass...
 *    lazyList.setElementDynaClass(new MyCustomDynaClass());
 *
 *    // Populate from a collection
 *    lazyList.addAll(myCollection);
 *
 * </code></pre>
 *
 * @since 1.8.0
 */
public class LazyDynaList extends ArrayList<Object> {

    private static final long serialVersionUID = 1L;

    /**
     * The DynaClass of the List's elements.
     */
    private DynaClass elementDynaClass;

    /**
     * The WrapDynaClass if the List's contains
     * POJO Bean elements.
     *
     * N.B. WrapDynaClass isn't serializable, which
     *      is why its stored separately in a
     *      transient instance variable.
     */
    private transient WrapDynaClass wrapDynaClass;

    /**
     * The type of the List's elements.
     */
    private Class<?> elementType;

    /**
     * The DynaBean type of the List's elements.
     */
    private Class<?> elementDynaBeanType;

    /**
     * Default Constructor.
     */
    public LazyDynaList() {
    }

    /**
     * Constructs a  LazyDynaList with a
     * specified type for its elements.
     *
     * @param elementType The Type of the List's elements.
     */
    public LazyDynaList(final Class<?> elementType) {
        setElementType(elementType);
    }

    /**
     * Constructs a  LazyDynaList populated with the
     * elements of a Collection.
     *
     * @param collection The Collection to populate the List from.
     */
    public LazyDynaList(final Collection<?> collection) {
        super(collection.size());
        addAll(collection);
    }

    /**
     * Constructs a  LazyDynaList with a
     * specified DynaClass for its elements.
     *
     * @param elementDynaClass The DynaClass of the List's elements.
     */
    public LazyDynaList(final DynaClass elementDynaClass) {
        setElementDynaClass(elementDynaClass);
    }

    /**
     * Constructs a LazyDynaList with the
     * specified capacity.
     *
     * @param capacity The initial capacity of the list.
     */
    public LazyDynaList(final int capacity) {
        super(capacity);

    }

    /**
     * Constructs a  LazyDynaList populated with the
     * elements of an Array.
     *
     * @param array The Array to populate the List from.
     */
    public LazyDynaList(final Object[] array) {
        super(array.length);
        this.addAll(Arrays.asList(array));
    }

    /**
     * <p>Insert an element at the specified index position.</p>
     *
     * <p>If the index position is greater than the current
     *    size of the List, then the List is automatically
     *    <i>grown</i> to the appropriate size.</p>
     *
     * @param index The index position to insert the new element.
     * @param element The new element to add.
     */
    @Override
    public void add(final int index, final Object element) {
        final DynaBean dynaBean = transform(element);

        growList(index);

        super.add(index, dynaBean);
    }

    /**
     * <p>Add an element to the List.</p>
     *
     * @param element The new element to add.
     * @return true.
     */
    @Override
    public boolean add(final Object element) {
        final DynaBean dynaBean = transform(element);

        return super.add(dynaBean);
    }

    /**
     * <p>Add all the elements from a Collection to the list.
     *
     * @param collection The Collection of new elements.
     * @return true if elements were added.
     */
    @Override
    public boolean addAll(final Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }

        ensureCapacity(size() + collection.size());

        collection.forEach(this::add);

        return true;
    }

    /**
     * <p>Insert all the elements from a Collection into the
     *    list at a specified position.
     *
     * <p>If the index position is greater than the current
     *    size of the List, then the List is automatically
     *    <i>grown</i> to the appropriate size.</p>
     *
     * @param collection The Collection of new elements.
     * @param index The index position to insert the new elements at.
     * @return true if elements were added.
     */
    @Override
    public boolean addAll(final int index, final Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }

        ensureCapacity((Math.max(index, size())) + collection.size());

        // Call "transform" with first element, before
        // List is "grown" to ensure the correct DynaClass
        // is set.
        if (isEmpty()) {
            transform(collection.iterator().next());
        }

        growList(index);

        int currentIndex = index;
        for (final Object e : collection) {
            add(currentIndex++, e);
        }

        return true;
    }

    /**
     * Creates a new {@code LazyDynaMap} object for the given property value.
     *
     * @param value the property value
     * @return the newly created {@code LazyDynaMap}
     */
    private LazyDynaMap createDynaBeanForMapProperty(final Object value) {
        @SuppressWarnings("unchecked")
        final
        // map properties are always stored as Map<String, Object>
        Map<String, Object> valueMap = (Map<String, Object>) value;
        return new LazyDynaMap(valueMap);
    }

    /**
     * <p>Return the element at the specified position.</p>
     *
     * <p>If the position requested is greater than the current
     *    size of the List, then the List is automatically
     *    <i>grown</i> (and populated) to the appropriate size.</p>
     *
     * @param index The index position to insert the new elements at.
     * @return The element at the specified position.
     */
    @Override
    public Object get(final int index) {
        growList(index + 1);

        return super.get(index);
    }

    /**
     * Gets the DynaClass.
     */
    private DynaClass getDynaClass() {
        return elementDynaClass == null ? wrapDynaClass : elementDynaClass;
    }

    /**
     * <p>Automatically <i>grown</i> the List
     *    to the appropriate size, populating with
     *    DynaBeans.</p>
     *
     * @param requiredSize the required size of the List.
     */
    private void growList(final int requiredSize) {
        if (requiredSize < size()) {
            return;
        }

        ensureCapacity(requiredSize + 1);

        for (int i = size(); i < requiredSize; i++) {
            final DynaBean dynaBean = transform(null);
            super.add(dynaBean);
        }
    }



    /**
     * <p>Set the element at the specified position.</p>
     *
     * <p>If the position requested is greater than the current
     *    size of the List, then the List is automatically
     *    <i>grown</i> (and populated) to the appropriate size.</p>
     *
     * @param index The index position to insert the new element at.
     * @param element The new element.
     * @return The new element.
     */
    @Override
    public Object set(final int index, final Object element) {
        final DynaBean dynaBean = transform(element);

        growList(index + 1);

        return super.set(index, dynaBean);
    }

    /**
     * <p>Set the element Type and DynaClass.</p>
     *
     * @param elementDynaClass The DynaClass of the elements.
     * @throws IllegalArgumentException if the List already
     *            contains elements or the DynaClass is null.
     */
    public void setElementDynaClass(final DynaClass elementDynaClass) {
        if (elementDynaClass == null) {
            throw new IllegalArgumentException("Element DynaClass is missing");
        }

        if (!isEmpty()) {
            throw new IllegalStateException("Element DynaClass cannot be reset");
        }

        // Try to create a new instance of the DynaBean
        try {
            final DynaBean dynaBean  = elementDynaClass.newInstance();
            this.elementDynaBeanType = dynaBean.getClass();
            if (WrapDynaBean.class.isAssignableFrom(elementDynaBeanType)) {
                this.elementType = ((WrapDynaBean)dynaBean).getInstance().getClass();
                this.wrapDynaClass = (WrapDynaClass)elementDynaClass;
            } else if (LazyDynaMap.class.isAssignableFrom(elementDynaBeanType)) {
                this.elementType = ((LazyDynaMap)dynaBean).getMap().getClass();
                this.elementDynaClass = elementDynaClass;
            } else {
                this.elementType = dynaBean.getClass();
                this.elementDynaClass = elementDynaClass;
            }
        } catch (final Exception e) {
            throw new IllegalArgumentException(
                        "Error creating DynaBean from " +
                        elementDynaClass.getClass().getName() + " - " + e);
        }
    }

    /**
     * <p>Set the element Type and DynaClass.</p>
     *
     * @param elementType The type of the elements.
     * @throws IllegalArgumentException if the List already
     *            contains elements or the DynaClass is null.
     */
    public void setElementType(final Class<?> elementType) {
        if (elementType == null) {
            throw new IllegalArgumentException("Element Type is missing");
        }

        final boolean changeType = this.elementType != null && !this.elementType.equals(elementType);
        if (changeType && !isEmpty()) {
            throw new IllegalStateException("Element Type cannot be reset");
        }

        this.elementType = elementType;

        // Create a new object of the specified type
        Object object = null;
        try {
            object = elementType.newInstance();
        } catch (final Exception e) {
            throw new IllegalArgumentException("Error creating type: "
                           + elementType.getName() + " - " + e);
        }

        // Create a DynaBean
        DynaBean dynaBean = null;
        if (Map.class.isAssignableFrom(elementType)) {
            dynaBean = createDynaBeanForMapProperty(object);
            this.elementDynaClass = dynaBean.getDynaClass();
        } else if (DynaBean.class.isAssignableFrom(elementType)) {
            dynaBean = (DynaBean)object;
            this.elementDynaClass = dynaBean.getDynaClass();
        } else {
            dynaBean = new WrapDynaBean(object);
            this.wrapDynaClass = (WrapDynaClass)dynaBean.getDynaClass();
        }

        this.elementDynaBeanType = dynaBean.getClass();

        // Re-calculate the type
        if (WrapDynaBean.class.isAssignableFrom(elementDynaBeanType )) {
            this.elementType = ((WrapDynaBean)dynaBean).getInstance().getClass();
        } else if (LazyDynaMap.class.isAssignableFrom(elementDynaBeanType )) {
            this.elementType = ((LazyDynaMap)dynaBean).getMap().getClass();
        }
    }



    /**
     * <p>Converts the List to an Array.</p>
     *
     * <p>The type of Array created depends on the contents
     *    of the List:</p>
     * <ul>
     *    <li>If the List contains only LazyDynaMap type elements
     *        then a java.util.Map[] array will be created.</li>
     *    <li>If the List contains only elements which are
     *        "wrapped" DynaBeans then an Object[] of the most
     *        suitable type will be created.</li>
     *    <li>...otherwise a DynaBean[] will be created.</li>
     * </ul>
     *
     * @return An Array of the elements in this List.
     */
    @Override
    public Object[] toArray() {
        if (isEmpty() && elementType == null) {
            return LazyDynaBean.EMPTY_ARRAY;
        }

        final Object[] array = (Object[])Array.newInstance(elementType, size());
        for (int i = 0; i < size(); i++) {
            if (Map.class.isAssignableFrom(elementType)) {
                array[i] = ((LazyDynaMap)get(i)).getMap();
            } else if (DynaBean.class.isAssignableFrom(elementType)) {
                array[i] = get(i);
            } else {
                array[i] = ((WrapDynaBean)get(i)).getInstance();
            }
        }
        return array;
    }

    /**
     * <p>Converts the List to an Array of the specified type.</p>
     *
     * @param <T> The type of the array elements
     * @param model The model for the type of array to return
     * @return An Array of the elements in this List.
     */
    @Override
    public <T> T[] toArray(final T[] model) {
        final Class<?> arrayType = model.getClass().getComponentType();
        if (DynaBean.class.isAssignableFrom(arrayType)
                || isEmpty() && elementType == null) {
            return super.toArray(model);
        }

        if (arrayType.isAssignableFrom(elementType)) {
            T[] array;
            if (model.length >= size()) {
                array = model;
            } else {
                @SuppressWarnings("unchecked")
                final
                // This is safe because we know the element type
                T[] tempArray = (T[]) Array.newInstance(arrayType, size());
                array = tempArray;
            }

            for (int i = 0; i < size(); i++) {
                Object elem;
                if (Map.class.isAssignableFrom(elementType)) {
                    elem = ((LazyDynaMap) get(i)).getMap();
                } else if (DynaBean.class.isAssignableFrom(elementType)) {
                    elem = get(i);
                } else {
                    elem = ((WrapDynaBean) get(i)).getInstance();
                }
                Array.set(array, i, elem);
            }
            return array;
        }

        throw new IllegalArgumentException("Invalid array type: "
                  + arrayType.getName() + " - not compatible with '"
                  + elementType.getName());
    }

    /**
     * <p>Converts the List to an DynaBean Array.</p>
     *
     * @return A DynaBean[] of the elements in this List.
     */
    public DynaBean[] toDynaBeanArray() {
        if (isEmpty() && elementDynaBeanType == null) {
            return LazyDynaBean.EMPTY_ARRAY;
        }

        final DynaBean[] array = (DynaBean[])Array.newInstance(elementDynaBeanType, size());
        for (int i = 0; i < size(); i++) {
            array[i] = (DynaBean)get(i);
        }
        return array;
    }

    /**
     * <p>Transform the element into a DynaBean:</p>
     *
     * <ul>
     *    <li>Map elements are turned into LazyDynaMap's.</li>
     *    <li>POJO Beans are "wrapped" in a WrapDynaBean.</li>
     *    <li>DynaBeans are unchanged.</li>
     * </li>
     *
     * @param element The element to transformed.
     * @return The DynaBean to store in the List.
     */
    private DynaBean transform(final Object element) {
        DynaBean dynaBean     = null;
        Class<?> newDynaBeanType = null;
        Class<?> newElementType;

        // Create a new element
        if (element == null) {

            // Default Types to LazyDynaBean
            // if not specified
            if (elementType == null) {
                setElementDynaClass(new LazyDynaClass());
            }

            // Get DynaClass (restore WrapDynaClass lost in serialization)
            if (getDynaClass() == null) {
                setElementType(elementType);
            }

            // Create a new DynaBean
            try {
                dynaBean = getDynaClass().newInstance();
                newDynaBeanType = dynaBean.getClass();
            } catch (final Exception e) {
                throw new IllegalArgumentException("Error creating DynaBean: "
                              + getDynaClass().getClass().getName()
                              + " - " + e);
            }

        } else {

            // Transform Object to a DynaBean
            newElementType = element.getClass();
            if (Map.class.isAssignableFrom(element.getClass())) {
                dynaBean = createDynaBeanForMapProperty(element);
            } else if (DynaBean.class.isAssignableFrom(element.getClass())) {
                dynaBean = (DynaBean)element;
            } else {
                dynaBean = new WrapDynaBean(element);
            }

            newDynaBeanType = dynaBean.getClass();

        }

        // Re-calculate the element type
        newElementType = dynaBean.getClass();
        if (WrapDynaBean.class.isAssignableFrom(newDynaBeanType)) {
            newElementType = ((WrapDynaBean)dynaBean).getInstance().getClass();
        } else if (LazyDynaMap.class.isAssignableFrom(newDynaBeanType)) {
            newElementType = ((LazyDynaMap)dynaBean).getMap().getClass();
        }

        // Check the new element type, matches all the
        // other elements in the List
        if (elementType != null && !newElementType.equals(elementType)) {
            throw new IllegalArgumentException("Element Type "  + newElementType
                       + " doesn't match other elements " + elementType);
        }

        return dynaBean;
    }
}
