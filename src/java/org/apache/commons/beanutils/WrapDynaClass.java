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


import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * <p>Implementation of <code>DynaClass</code> for DynaBeans that wrap
 * standard JavaBean instances.</p>
 *
 * <p>
 * It is suggested that this class should not usually need to be used directly
 * to create new <code>WrapDynaBean</code> instances. 
 * It's usually better to call the <code>WrapDynaBean</code> constructor directly.
 * For example:</p>
 * <code><pre>
 *   Object javaBean = ...;
 *   DynaBean wrapper = new WrapDynaBean(javaBean);
 * </pre></code>
 * <p>
 *
 * @author Craig McClanahan
 * @version $Revision: 1.8 $ $Date: 2004/02/28 13:18:34 $
 */

public class WrapDynaClass implements DynaClass {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new WrapDynaClass for the specified JavaBean class.  This
     * constructor is private; WrapDynaClass instances will be created as
     * needed via calls to the <code>createDynaClass(Class)</code> method.
     *
     * @param beanClass JavaBean class to be introspected around
     */
    private WrapDynaClass(Class beanClass) {

        this.beanClass = beanClass;
        introspect();

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The JavaBean <code>Class</code> which is represented by this
     * <code>WrapDynaClass</code>.
     */
    protected Class beanClass = null;


    /**
     * The set of PropertyDescriptors for this bean class.
     */
    protected PropertyDescriptor descriptors[] = null;


    /**
     * The set of PropertyDescriptors for this bean class, keyed by the
     * property name.  Individual descriptor instances will be the same
     * instances as those in the <code>descriptors</code> list.
     */
    protected HashMap descriptorsMap = new HashMap();


    /**
     * The set of dynamic properties that are part of this DynaClass.
     */
    protected DynaProperty properties[] = null;


    /**
     * The set of dynamic properties that are part of this DynaClass,
     * keyed by the property name.  Individual descriptor instances will
     * be the same instances as those in the <code>properties</code> list.
     */
    protected HashMap propertiesMap = new HashMap();


    // ------------------------------------------------------- Static Variables


    /**
     * The set of <code>WrapDynaClass</code> instances that have ever been
     * created, keyed by the underlying bean Class.
     */
    protected static HashMap dynaClasses = new HashMap();


    // ------------------------------------------------------ DynaClass Methods


    /**
     * Return the name of this DynaClass (analogous to the
     * <code>getName()</code> method of <code>java.lang.Class</code), which
     * allows the same <code>DynaClass</code> implementation class to support
     * different dynamic classes, with different sets of properties.
     */
    public String getName() {

        return (this.beanClass.getName());

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
     * <p>Instantiates a new standard JavaBean instance associated with
     * this DynaClass and return it wrapped in a new WrapDynaBean   
     * instance. <strong>NOTE</strong> the JavaBean should have a 
     * no argument constructor.</p>
     *
     * <strong>NOTE</strong> - Most common use cases should not need to use
     * this method. It is usually better to create new
     * <code>WrapDynaBean</code> instances by calling its constructor.
     * For example:</p>
     * <code><pre>
     *   Object javaBean = ...;
     *   DynaBean wrapper = new WrapDynaBean(javaBean);
     * </pre></code>
     * <p>
     * (This method is needed for some kinds of <code>DynaBean</code> framework.)
     * </p>
     *
     * @exception IllegalAccessException if the Class or the appropriate
     *  constructor is not accessible
     * @exception InstantiationException if this Class represents an abstract
     *  class, an array class, a primitive type, or void; or if instantiation
     *  fails for some other reason
     */
    public DynaBean newInstance()
            throws IllegalAccessException, InstantiationException {

        return new WrapDynaBean(beanClass.newInstance());

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Return the PropertyDescriptor for the specified property name, if any;
     * otherwise return <code>null</code>.
     *
     * @param name Name of the property to be retrieved
     */
    public PropertyDescriptor getPropertyDescriptor(String name) {

        return ((PropertyDescriptor) descriptorsMap.get(name));

    }


    // --------------------------------------------------------- Static Methods


    /**
     * Clear our cache of WrapDynaClass instances.
     */
    public static void clear() {

        synchronized (dynaClasses) {
            dynaClasses.clear();
        }

    }


    /**
     * Create (if necessary) and return a new <code>WrapDynaClass</code>
     * instance for the specified bean class.
     *
     * @param beanClass Bean class for which a WrapDynaClass is requested
     */
    public static WrapDynaClass createDynaClass(Class beanClass) {

        synchronized (dynaClasses) {
            WrapDynaClass dynaClass =
                    (WrapDynaClass) dynaClasses.get(beanClass);
            if (dynaClass == null) {
                dynaClass = new WrapDynaClass(beanClass);
                dynaClasses.put(beanClass, dynaClass);
            }
            return (dynaClass);
        }

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Introspect our bean class to identify the supported properties.
     */
    protected void introspect() {

        // Look up the property descriptors for this bean class
        PropertyDescriptor regulars[] =
                PropertyUtils.getPropertyDescriptors(beanClass);
        if (regulars == null) {
            regulars = new PropertyDescriptor[0];
        }
        HashMap mappeds =
                PropertyUtils.getMappedPropertyDescriptors(beanClass);
        if (mappeds == null) {
            mappeds = new HashMap();
        }

        // Construct corresponding DynaProperty information
        properties = new DynaProperty[regulars.length + mappeds.size()];
        for (int i = 0; i < regulars.length; i++) {
            descriptorsMap.put(regulars[i].getName(),
                    regulars[i]);
            properties[i] =
                    new DynaProperty(regulars[i].getName(),
                            regulars[i].getPropertyType());
            propertiesMap.put(properties[i].getName(),
                    properties[i]);
        }
        int j = regulars.length;
        Iterator names = mappeds.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            PropertyDescriptor descriptor =
                    (PropertyDescriptor) mappeds.get(name);
            properties[j] =
                    new DynaProperty(descriptor.getName(),
                            Map.class);
            propertiesMap.put(properties[j].getName(),
                    properties[j]);
            j++;
        }

    }


}
