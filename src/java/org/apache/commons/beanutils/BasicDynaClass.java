/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/BasicDynaClass.java,v 1.10 2003/10/09 20:43:15 rdonkin Exp $
 * $Revision: 1.10 $
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
 * @version $Revision: 1.10 $ $Date: 2003/10/09 20:43:15 $
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
