/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Array;

/**
 * <p>DynaBean which automatically adds properties to the <code>DynaClass</code>
 * if they don't exist.</p>
 *
 * <p><strong>N.B.</strong> Must be used with a <code>DynaClass</code> that
 * implements the <code>MutableDynaClass</code> interface - if not specified
 * the <code>LazyDynaClass</code> is used by default.</p>
 *
 * <p>DynaBeans deal with three types of properties - <i>simple</i>, <i>indexed</i> and <i>mapped</i> and
 *    have the following <code>get()</code> and <code>set()</code> methods for
 *    each of these types:</p>
 *    <ul>
 *        <li><i>Simple</i> property methods - <code>get(name)</code> and <code>set(name, value)</code></li>
 *        <li><i>Indexed</i> property methods - <code>get(name, index)</code> and <code>set(name, index, value)</code></li>
 *        <li><i>Mapped</i> property methods - <code>get(name, key)</code> and <code>set(name, key, value)</code></li>
 *    </ul>
 *
 * <p><b><u>Getting Property Values</u></b></p>
 * <p>Calling any of the <code>get()</code> methods, for a property which
 *    doesn't exist, returns <code>null</code> in this implementation.</p>
 *
 * <p><b><u>Setting Simple Properties</u></b></p>
 *    <p>The <code>LazyDynaBean</code> will automatically add a property to the <code>DynaClass</code>
 *       if it doesn't exist when the <code>set(name, value)</code> method is called.</p>
 *
 *     <code>DynaBean myBean = new LazyDynaBean();</code></br>
 *     <code>myBean.set("myProperty", "myValue");</code></br>
 *
 * <p><b><u>Setting Indexed Properties</u></b></p>
 *    <p>If the property <b>doesn't</b> exist, the <code>LazyDynaBean</code> will automatically add
 *       a property with an <code>ArrayList</code> type to the <code>DynaClass</code> when
 *       the <code>set(name, index, value)</code> method is called.
 *       It will also instantiate a new <code>ArrayList</code> and automatically <i>grow</i>
 *       the <code>List</code> so that it is big enough to accomodate the index being set.
 *       <code>ArrayList</code> is the default indexed property that LazyDynaBean uses but
 *       this can be easily changed by overriding the <code>newIndexedProperty(name)</code>
 *       method.</p>
 *
 *     <code>DynaBean myBean = new LazyDynaBean();</code></br>
 *     <code>myBean.set("myIndexedProperty", 0, "myValue1");</code></br>
 *     <code>myBean.set("myIndexedProperty", 1, "myValue2");</code></br>
 *
 *    <p>If the indexed property <b>does</b> exist in the <code>DynaClass</code> but is set to
 *      <code>null</code> in the <code>LazyDynaBean</code>, then it will instantiate a
 *      new <code>List</code> or <code>Array</code> as specified by the property's type
 *      in the <code>DynaClass</code> and automatically <i>grow</i> the <code>List</code>
 *      or <code>Array</code> so that it is big enough to accomodate the index being set.</p>
 *
 *     <code>DynaBean myBean = new LazyDynaBean();</code></br>
 *     <code>MutableDynaClass myClass = (MutableDynaClass)myBean.getDynaClass();</code></br>
 *     <code>myClass.add("myIndexedProperty", int[].class);</code></br>
 *     <code>myBean.set("myIndexedProperty", 0, new Integer(10));</code></br>
 *     <code>myBean.set("myIndexedProperty", 1, new Integer(20));</code></br>
 *
 * <p><b><u>Setting Mapped Properties</u></b></p>
 *    <p>If the property <b>doesn't</b> exist, the <code>LazyDynaBean</code> will automatically add
 *       a property with a <code>HashMap</code> type to the <code>DynaClass</code> and
 *       instantiate a new <code>HashMap</code> in the DynaBean when the
 *       <code>set(name, key, value)</code> method is called. <code>HashMap</code> is the default
 *       mapped property that LazyDynaBean uses but this can be easily changed by overriding
 *       the <code>newMappedProperty(name)</code> method.</p>
 *
 *     <code>DynaBean myBean = new LazyDynaBean();</code></br>
 *     <code>myBean.set("myMappedProperty", "myKey", "myValue");</code></br>
 *
 *    <p>If the mapped property <b>does</b> exist in the <code>DynaClass</code> but is set to
 *      <code>null</code> in the <code>LazyDynaBean</code>, then it will instantiate a
 *      new <code>Map</code> as specified by the property's type in the <code>DynaClass</code>.</p>
 *
 *     <code>DynaBean myBean = new LazyDynaBean();</code></br>
 *     <code>MutableDynaClass myClass = (MutableDynaClass)myBean.getDynaClass();</code></br>
 *     <code>myClass.add("myMappedProperty", TreeMap.class);</code></br>
 *     <code>myBean.set("myMappedProperty", "myKey", "myValue");</code></br>
 *
 * <p><b><u><i>Restricted</i> DynaClass</u></b></p>
 *    <p><code>MutableDynaClass</code> have a facility to <i>restrict</i> the <code>DynaClass</code>
 *       so that its properties cannot be modified. If the <code>MutableDynaClass</code> is
 *       restricted then calling any of the <code>set()</code> methods for a property which
 *       doesn't exist will result in a <code>IllegalArgumentException</code> being thrown.</p>
 *
 * @see LazyDynaClass
 * @author Niall Pemberton
 */
public class LazyDynaBean extends BasicDynaBean {

    /**
     * The <code>MutableDynaClass</code> "base class" that this DynaBean
     * is associated with.
     */
    protected MutableDynaClass mutableDynaClass;

    /**
     * Construct a new <code>LazyDynaBean</code> with a <code>LazyDynaClass</code> instance.
     */
    public LazyDynaBean() {

        this(new LazyDynaClass());

    }

    /**
     * Construct a new <code>LazyDynaBean</code> with a <code>LazyDynaClass</code> instance.
     *
     * @param name Name of this DynaBean class
     */
    public LazyDynaBean(String name) {

        this(new LazyDynaClass(name));

    }

    /**
     * Construct a new <code>DynaBean</code> associated with the specified
     * <code>DynaClass</code> instance - must be <code>MutableDynaClass</code>.
     *
     * @param dynaClass The DynaClass we are associated with
     */
    public LazyDynaBean(DynaClass dynaClass) {

        super(dynaClass);

        if (!(dynaClass instanceof MutableDynaClass))
            throw new IllegalArgumentException("DynaClass must be a MutableDynaClass type: " +
                                                dynaClass.getClass().getName());

        mutableDynaClass = (MutableDynaClass)dynaClass;

    }

    /**
     * <p>Return the value of a simple property with the specified name.</p>
     *
     * <p><strong>N.B.</strong> Returns <code>null</code> if there is no property
     *  of the specified name.</p>
     *
     * @param name Name of the property whose value is to be retrieved.
     */
    public Object get(String name) {

        if (isDynaProperty(name)) {

            return super.get(name);

        } else {

            return null;

        }

    }

    /**
     * <p>Return the value of an indexed property with the specified name.</p>
     *
     * <p><strong>N.B.</strong> Returns <code>null</code> if there is no 'indexed'
     * property of the specified name.</p>
     *
     * @param name Name of the property whose value is to be retrieved
     * @param index Index of the value to be retrieved
     *
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not indexed
     * @exception IndexOutOfBoundsException if the specified index
     *  is outside the range of the underlying property
     */
    public Object get(String name, int index) {

        if (get(name) == null) {

            return null;

        } else {

            return super.get(name, index);

        }

    }

    /**
     * <p>Return the value of a mapped property with the specified name.</p>
     *
     * <p><strong>N.B.</strong> Returns <code>null</code> if there is no 'mapped'
     * property of the specified name.</p>
     *
     * @param name Name of the property whose value is to be retrieved
     * @param key Key of the value to be retrieved
     *
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not mapped
     */
    public Object get(String name, String key) {

        if (get(name) == null) {

            return null;

        } else {

            return super.get(name, key);

        }

    }

    /**
     * Set the value of a simple property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param value Value to which this property is to be set
     *
     * @exception IllegalArgumentException if this is not an existing property
     *  name for our DynaClass and the MutableDynaClass is restricted
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception NullPointerException if an attempt is made to set a
     *  primitive property to null
     */
    public void set(String name, Object value) {

        // If the property doesn't exist, then add it
        if (!isDynaProperty(name)) {

            if (mutableDynaClass.isRestricted()) {
                throw new IllegalArgumentException
                    ("Invalid property name '" + name + "' (DynaClass is restricted)");
            }

            if (value == null) {
                mutableDynaClass.add(name);
            } else {
                mutableDynaClass.add(name, value.getClass());
            }

        }

        // Set the property's value
        super.set(name, value);

    }

    /**
     * Set the value of an indexed property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param index Index of the property to be set
     * @param value Value to which this property is to be set
     *
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not indexed
     * @exception IndexOutOfBoundsException if the specified index
     *  is outside the range of the underlying property
     */
    public void set(String name, int index, Object value) {

        // If the 'indexed' property doesn't exist, then add it
        if (!isDynaProperty(name)) {
            set(name, newIndexedProperty(name));
        }

        // Check if its an Indexed Property
        DynaProperty dynaProperty = mutableDynaClass.getDynaProperty(name);
        if (!dynaProperty.isIndexed()) {
            throw new IllegalArgumentException
                    ("Non-indexed property for '" + name + "[" + index + "]', type is '" +
                         dynaProperty.getType().getName()+"'");
        }


        // Instantiate the indexed property
        Object prop = get(name);
        if (prop == null) {
            Class type = dynaProperty.getType();
            if (List.class.isAssignableFrom(type)) {

                try {
                    prop = type.newInstance();
                    set(name, prop);
                }
                catch (Exception ex) {
                    throw new IllegalArgumentException
                        ("Error instantiating List of type '" + type.getName() +
                                      "' for '" + name + "[" + index + "]'");
                }

            } else if (type.isArray()) {

                prop = Array.newInstance(type.getComponentType(), index+1);
                set(name, prop);

            } else {

                throw new IllegalArgumentException
                   ("Non-indexed property for '" + name + "[" + index + "]'");
            }
        }

        // Grow the List or Array dynamically
        if (prop instanceof List) {

            List list = (List)prop;
            while (index >= list.size()) {
                list.add(null);
            }

        } else if ((prop.getClass().isArray())) {

            int length = Array.getLength(prop);
            if (index >= length) {
                Object newArray = Array.newInstance(prop.getClass().getComponentType(), (index + 1));
                System.arraycopy(prop, 0, newArray, 0, length);
                set(name, newArray);
            }
        }

        super.set(name, index, value);

    }

    /**
     * <p>Creates a new <code>ArrayList</code> for an 'indexed' property
     *    which doesn't exist.</p>
     *
     * <p>This method shouls be overriden if an alternative <code>List</code>
     *    or <code>Array</code> implementation is required for 'indexed' properties.</p>
     *
     * @param name Name of the 'indexed property.
     */
    protected Object newIndexedProperty(String name) {

        return new ArrayList();

    }

    /**
     * Set the value of a mapped property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param key Key of the property to be set
     * @param value Value to which this property is to be set
     *
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not mapped
     */
    public void set(String name, String key, Object value) {

        // If the 'mapped' property doesn't exist, then add it
        if (!isDynaProperty(name)) {
            set(name, newMappedProperty(name));
        }

        // Check if its an Indexed Property
        DynaProperty dynaProperty = mutableDynaClass.getDynaProperty(name);
        if (!dynaProperty.isMapped()) {
            throw new IllegalArgumentException("Non-mapped property for '" +
                 name + "(" + key + ")', type is '" + dynaProperty.getType().getName()+"'");
        }

        // Instantiate the mapped property
        Object prop = get(name);
        if (prop == null) {
            Class type = dynaProperty.getType();
            try {
                set(name, type.newInstance());
            }
            catch (Exception ex) {
                throw new IllegalArgumentException
                    ("Error instantiating Map of type '" + type.getName() +
                                      "' for '" + name + "(" + key + ")'");
            }
        }


        // Set the 'mapped' property's value
        super.set(name, key, value);


    }

    /**
     * <p>Creates a new <code>HashMap</code> for a 'mapped' property
     *    which doesn't exist.</p>
     *
     * <p>This method can be overriden if an alternative <code>Map</code>
     *    implementation is required for 'mapped' properties.</p>
     *
     * @param name Name of the 'mapped property.
     */
    protected Map newMappedProperty(String name) {

        return new HashMap();

    }

    /**
     * Indicates if there is a property with the specified name.
     */
    protected boolean isDynaProperty(String name) {

        // Handle LazyDynaClasses
        if (mutableDynaClass instanceof LazyDynaClass) {
            return ((LazyDynaClass)mutableDynaClass).isDynaProperty(name);
        }

        // Handle other MutableDynaClass
        return mutableDynaClass.getDynaProperty(name) == null ? false : true;

    }

}
