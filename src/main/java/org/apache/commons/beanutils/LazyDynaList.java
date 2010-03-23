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

import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.lang.reflect.Array;

/**
 * <h2><i>Lazy</i> DynaBean List.</h2>
 * 
 * <p>There are two main purposes for this class:</p>
 *    <ul>
 *        <li>To provide <i>Lazy List</i> behaviour - automatically
 *            <i>growing</i> and <i>populating</i> the <code>List</code>
 *            with either <code>DynaBean</code>, <code>java.util.Map</code>
 *            or POJO Beans.</li>
 *        <li>To provide a straight forward way of putting a Collection
 *            or Array into the lazy list <i>and</i> a straight forward 
 *            way to get it out again at the end.</li>
 *    </ul>
 * 
 * <p>All elements added to the List are stored as <code>DynaBean</code>'s:</p>
 * <ul>
 *    <li><code>java.util.Map</code> elements are "wrapped" in a <code>LazyDynaMap</code>.</i> 
 *    <li>POJO Bean elements are "wrapped" in a <code>WrapDynaBean.</code></i> 
 *    <li><code>DynaBean</code>'s are stored un-changed.</i>
 * </ul>
 *  
 * <h4><code>toArray()</code></h4>
 * <p>The <code>toArray()</code> method returns an array of the
 *    elements of the appropriate type. If the <code>LazyDynaList</code>
 *    is populated with <code>java.util.Map</code> objects a 
 *    <code>Map[]</code> array is returned.
 *    If the list is populated with POJO Beans an appropriate
 *    array of the POJO Beans is returned. Otherwise a <code>DynaBean[]</code>
 *    array is returned.
 * </p>
 *  
 * <h4><code>toDynaBeanArray()</code></h4>
 * <p>The <code>toDynaBeanArray()</code> method returns a 
 *    <code>DynaBean[]</code> array of the elements in the List.
 * </p>
 *  
 * <p><strong>N.B.</strong>All the elements in the List must be the
 *    same type. If the <code>DynaClass</code> or <code>Class</code>
 *    of the <code>LazyDynaList</code>'s elements is
 *    not specified, then it will be automatically set to the type
 *    of the first element populated.
 * </p>
 * 
 * <h3>Example 1</h3>
 * <p>If you have an array of <code>java.util.Map[]</code> - you can put that into
 *    a <code>LazyDynaList</code>.</p>
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
 * <h3>Example 2</h3>
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
 * <h3>Example 3</h3>
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
 *    using a <code>DynaClass</code> rather than the <code>Class</code>
 *    as in the previous example - the reason is that some <code>DynaBean</code>
 *    implementations don't have a <i>default</i> empty constructor and
 *    therefore need to be instantiated using the <code>DynaClass.newInstance()</code>
 *    method.</p>
 * 
 * <h3>Example 4</h3>
 * <p>A slight variation - set the element type using either
 *    the <code>setElementType(Class)</code> method or the
 *    <code>setElementDynaClass(DynaClass)</code> method - then populate
 *    with the normal <code>java.util.List</code> methods(i.e.
 *    <code>add()</code>, <code>addAll()</code> or <code>set()</code>).</p>
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
 * @author Niall Pemberton
 * @version $Revision$ $Date$
 * @since 1.8.0
 */
public class LazyDynaList extends ArrayList {
    
    /**
     * The DynaClass of the List's elements.
     */
    private DynaClass elementDynaClass;
    
    /**
     * The WrapDynaClass if the List's contains
     * POJO Bean elements.
     *
     * N.B. WrapDynaClass isn't serlializable, which
     *      is why its stored separately in a 
     *      transient instance variable.
     */
    private transient WrapDynaClass wrapDynaClass;
    
    /**
     * The type of the List's elements.
     */
    private Class elementType;
    
    /**
     * The DynaBean type of the List's elements.
     */
    private Class elementDynaBeanType;


    // ------------------- Constructors ------------------------------

    /**
     * Default Constructor.
     */
    public LazyDynaList() {
        super();
    }

    /**
     * Construct a LazyDynaList with the 
     * specified capacity.
     *
     * @param capacity The initial capacity of the list.
     */
    public LazyDynaList(int capacity) {
        super(capacity);
        
    }

    /**
     * Construct a  LazyDynaList with a
     * specified DynaClass for its elements.
     * 
     * @param elementDynaClass The DynaClass of the List's elements.
     */
    public LazyDynaList(DynaClass elementDynaClass) {
        super();
        setElementDynaClass(elementDynaClass);
    }

    /**
     * Construct a  LazyDynaList with a
     * specified type for its elements.
     * 
     * @param elementType The Type of the List's elements.
     */
    public LazyDynaList(Class elementType) {
        super();
        setElementType(elementType);
    }
    
    /**
     * Construct a  LazyDynaList populated with the
     * elements of a Collection.
     *
     * @param collection The Collection to poulate the List from.
     */
    public LazyDynaList(Collection collection) {
        super(collection.size());
        addAll(collection);
    }
    
    /**
     * Construct a  LazyDynaList populated with the
     * elements of an Array.
     *
     * @param array The Array to poulate the List from.
     */
    public LazyDynaList(Object[] array) {
        super(array.length);
        for (int i = 0; i < array.length; i++) {
            add(array[i]);
        }
    }


    // ------------------- java.util.List Methods --------------------

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
    public void add(int index, Object element) {

        DynaBean dynaBean = transform(element);

        growList(index);
        
        super.add(index, dynaBean);

    }

    /**
     * <p>Add an element to the List.</p>
     *
     * @param element The new element to add.
     * @return true.
     */
    public boolean add(Object element) {

        DynaBean dynaBean = transform(element);

        return super.add(dynaBean);

    }

    /**
     * <p>Add all the elements from a Collection to the list.
     *
     * @param collection The Collection of new elements.
     * @return true if elements were added.
     */
    public boolean addAll(Collection collection) {

        if (collection == null || collection.size() == 0) {
            return false;
        }
        
        ensureCapacity(size() + collection.size());

        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            add(iterator.next());
        }

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
    public boolean addAll(int index, Collection collection) {

        if (collection == null || collection.size() == 0) {
            return false;
        }
        
        ensureCapacity((index > size() ? index : size()) + collection.size());
        
        // Call "tranform" with first element, before
        // List is "grown" to ensure the correct DynaClass
        // is set.
        if (size() == 0) {
            transform(collection.iterator().next());
        }

        growList(index);

        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            add(index++, iterator.next());
        }

        return true;
        
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
    public Object get(int index) {

        growList(index + 1);

        return super.get(index);

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
    public Object set(int index, Object element) {

        DynaBean dynaBean = transform(element);

        growList(index + 1);

        return super.set(index, dynaBean);
        
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
     * 
     * @return An Array of the elements in this List.
     */
    public Object[] toArray() {

        if (size() == 0 && elementType == null) {
            return new LazyDynaBean[0];
        }

        Object[] array = (Object[])Array.newInstance(elementType, size());
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
     * @param model The model for the type of array to return
     * @return An Array of the elements in this List.
     */
    public Object[] toArray(Object[] model) {
        
        // Allocate the Array
        Class arrayType = model.getClass().getComponentType();
        Object[] array = (Object[])Array.newInstance(arrayType, size());

        if (size() == 0 && elementType == null) {
            return new LazyDynaBean[0];
        }

        if ((DynaBean.class.isAssignableFrom(arrayType))) {
            for (int i = 0; i < size(); i++) {
                array[i] = get(i);
            }
            return array;
        }

        if ((arrayType.isAssignableFrom(elementType))) {
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

        throw new IllegalArgumentException("Invalid array type: " 
                  + arrayType.getName() + " - not compatible with '"
                  + elementType.getName());
        
    }


    // ------------------- Public Methods ----------------------------

    /**
     * <p>Converts the List to an DynaBean Array.</p>
     *
     * @return A DynaBean[] of the elements in this List.
     */
    public DynaBean[] toDynaBeanArray() {

        if (size() == 0 && elementDynaBeanType == null) {
            return new LazyDynaBean[0];
        }
        
        DynaBean[] array = (DynaBean[])Array.newInstance(elementDynaBeanType, size());
        for (int i = 0; i < size(); i++) {
            array[i] = (DynaBean)get(i);
        }
        return array;
        
    }

    /**
     * <p>Set the element Type and DynaClass.</p>
     *
     * @param elementType The type of the elements.
     * @exception IllegalArgumentException if the List already
     *            contains elements or the DynaClass is null.
     */
    public void setElementType(Class elementType) {

        if (elementType == null) {
            throw new IllegalArgumentException("Element Type is missing");
        }

        boolean changeType = (this.elementType != null && !this.elementType.equals(elementType));
        if (changeType && size() > 0) {
            throw new IllegalStateException("Element Type cannot be reset");
        }

        this.elementType = elementType;

        // Create a new object of the specified type
        Object object = null;
        try {
            object = elementType.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating type: " 
                           + elementType.getName() + " - " + e);
        }

        // Create a DynaBean
        DynaBean dynaBean = null;
        if (Map.class.isAssignableFrom(elementType)) {
            dynaBean = new LazyDynaMap((Map)object);
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
     * <p>Set the element Type and DynaClass.</p>
     *
     * @param elementDynaClass The DynaClass of the elements.
     * @exception IllegalArgumentException if the List already
     *            contains elements or the DynaClass is null.
     */
    public void setElementDynaClass(DynaClass elementDynaClass) {

        if (elementDynaClass == null) {
            throw new IllegalArgumentException("Element DynaClass is missing");
        }

        if (size() > 0) {
            throw new IllegalStateException("Element DynaClass cannot be reset");
        }

        // Try to create a new instance of the DynaBean
        try {
            DynaBean dynaBean  = elementDynaClass.newInstance();
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
        } catch (Exception e) {
            throw new IllegalArgumentException(
                        "Error creating DynaBean from " +
                        elementDynaClass.getClass().getName() + " - " + e);
        }

    }


    // ------------------- Private Methods ---------------------------

    /**
     * <p>Automatically <i>grown</i> the List
     *    to the appropriate size, populating with
     *    DynaBeans.</p>
     *
     * @param requiredSize the required size of the List.
     */
    private void growList(int requiredSize) {
        
        if (requiredSize < size()) {
            return;
        }
        
        ensureCapacity(requiredSize + 1);
        
        for (int i = size(); i < requiredSize; i++) {
            DynaBean dynaBean = transform(null);
            super.add(dynaBean);
        }
        
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
     * @param element The element to transformt.
     * @param The DynaBean to store in the List.
     */
    private DynaBean transform(Object element) {

        DynaBean dynaBean     = null;
        Class newDynaBeanType = null;
        Class newElementType  = null;

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
            } catch (Exception e) {
                throw new IllegalArgumentException("Error creating DynaBean: " 
                              + getDynaClass().getClass().getName() 
                              + " - " + e);
            }

        } else {

            // Transform Object to a DynaBean
            newElementType = element.getClass();
            if (Map.class.isAssignableFrom(element.getClass())) {
                dynaBean = new LazyDynaMap((Map)element);
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

    /**
     * Return the DynaClass.
     */
    private DynaClass getDynaClass() {
        return (elementDynaClass == null ? wrapDynaClass : elementDynaClass);
    }
}
