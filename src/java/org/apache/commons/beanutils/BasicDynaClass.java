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


import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


/**
 * <p>Minimal implementation of the <code>DynaClass</code> interface.  Can be
 * used as a convenience base class for more sophisticated implementations.</p> *
 * <p><strong>IMPLEMENTATION NOTE</strong> - The <code>DynaBean</code>
 * implementation class supplied to our constructor MUST have a one-argument
 * constructor of its own that accepts a <code>DynaClass</code>.  This is
 * used to associate the DynaBean instance with this DynaClass.</p>
 *
 * @author Craig McClanahan
 * @version $Revision: 1.11 $ $Date: 2004/02/28 13:18:33 $
 */

public class BasicDynaClass implements DynaClass, Serializable {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new BasicDynaClass with default parameters.
     */
    public BasicDynaClass() {

        this(null, null, null);

    }


    /**
     * Construct a new BasicDynaClass with the specified parameters.
     *
     * @param name Name of this DynaBean class
     * @param dynaBeanClass The implementation class for new instances
     */
    public BasicDynaClass(String name, Class dynaBeanClass) {

        this(name, dynaBeanClass, null);

    }


    /**
     * Construct a new BasicDynaClass with the specified parameters.
     *
     * @param name Name of this DynaBean class
     * @param dynaBeanClass The implementation class for new intances
     * @param properties Property descriptors for the supported properties
     */
    public BasicDynaClass(String name, Class dynaBeanClass,
                          DynaProperty properties[]) {

        super();
        if (name != null)
            this.name = name;
        if (dynaBeanClass == null)
            dynaBeanClass = BasicDynaBean.class;
        setDynaBeanClass(dynaBeanClass);
        if (properties != null)
            setProperties(properties);

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The constructor of the <code>dynaBeanClass</code> that we will use
     * for creating new instances.
     */
    protected transient Constructor constructor = null;


    /**
     * The method signature of the constructor we will use to create
     * new DynaBean instances.
     */
    protected static Class constructorTypes[] = { DynaClass.class };


    /**
     * The argument values to be passed to the constructore we will use
     * to create new DynaBean instances.
     */
    protected Object constructorValues[] = { this };


    /**
     * The <code>DynaBean</code> implementation class we will use for
     * creating new instances.
     */
    protected Class dynaBeanClass = BasicDynaBean.class;


    /**
     * The "name" of this DynaBean class.
     */
    protected String name = this.getClass().getName();


    /**
     * The set of dynamic properties that are part of this DynaClass.
     */
    protected DynaProperty properties[] = new DynaProperty[0];


    /**
     * The set of dynamic properties that are part of this DynaClass,
     * keyed by the property name.  Individual descriptor instances will
     * be the same instances as those in the <code>properties</code> list.
     */
    protected HashMap propertiesMap = new HashMap();


    // ------------------------------------------------------ DynaClass Methods


    /**
     * Return the name of this DynaClass (analogous to the
     * <code>getName()</code> method of <code>java.lang.Class</code), which
     * allows the same <code>DynaClass</code> implementation class to support
     * different dynamic classes, with different sets of properties.
     */
    public String getName() {

        return (this.name);

    }


    /**
     * Return a property descriptor for the specified property, if it exists;
     * otherwise, return <code>null</code>.
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
     *
     * <p><strong>FIXME</strong> - Should we really be implementing
     * <code>getBeanInfo()</code> instead, which returns property descriptors
     * and a bunch of other stuff?</p>
     */
    public DynaProperty[] getDynaProperties() {

        return (properties);

    }


    /**
     * Instantiate and return a new DynaBean instance, associated
     * with this DynaClass.
     *
     * @exception IllegalAccessException if the Class or the appropriate
     *  constructor is not accessible
     * @exception InstantiationException if this Class represents an abstract
     *  class, an array class, a primitive type, or void; or if instantiation
     *  fails for some other reason
     */
    public DynaBean newInstance()
            throws IllegalAccessException, InstantiationException {

        try {
            // Refind the constructor after a deserialization (if needed)
            if (constructor == null) {
                setDynaBeanClass(this.dynaBeanClass);
            }
            // Invoke the constructor to create a new bean instance
            return ((DynaBean) constructor.newInstance(constructorValues));
        } catch (InvocationTargetException e) {
            throw new InstantiationException
                    (e.getTargetException().getMessage());
        }

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Return the Class object we will use to create new instances in the
     * <code>newInstance()</code> method.  This Class <strong>MUST</strong>
     * implement the <code>DynaBean</code> interface.
     */
    public Class getDynaBeanClass() {

        return (this.dynaBeanClass);

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Set the Class object we will use to create new instances in the
     * <code>newInstance()</code> method.  This Class <strong>MUST</strong>
     * implement the <code>DynaBean</code> interface.
     *
     * @param dynaBeanClass The new Class object
     *
     * @exception IllegalArgumentException if the specified Class does not
     *  implement the <code>DynaBean</code> interface
     */
    protected void setDynaBeanClass(Class dynaBeanClass) {

        // Validate the argument type specified
        if (dynaBeanClass.isInterface())
            throw new IllegalArgumentException
                    ("Class " + dynaBeanClass.getName() +
                    " is an interface, not a class");
        if (!DynaBean.class.isAssignableFrom(dynaBeanClass))
            throw new IllegalArgumentException
                    ("Class " + dynaBeanClass.getName() +
                    " does not implement DynaBean");

        // Identify the Constructor we will use in newInstance()
        try {
            this.constructor = dynaBeanClass.getConstructor(constructorTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException
                    ("Class " + dynaBeanClass.getName() +
                    " does not have an appropriate constructor");
        }
        this.dynaBeanClass = dynaBeanClass;

    }


    /**
     * Set the list of dynamic properties supported by this DynaClass.
     *
     * @param properties List of dynamic properties to be supported
     */
    protected void setProperties(DynaProperty properties[]) {

        this.properties = properties;
        propertiesMap.clear();
        for (int i = 0; i < properties.length; i++) {
            propertiesMap.put(properties[i].getName(), properties[i]);
        }

    }


}
