/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/WrapDynaClass.java,v 1.6 2003/10/09 20:43:15 rdonkin Exp $
 * $Revision: 1.6 $
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


import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * <p>Implementation of <code>DynaClass</code> for DynaBeans that wrap
 * standard JavaBean instances.</p>
 *
 * @author Craig McClanahan
 * @version $Revision: 1.6 $ $Date: 2003/10/09 20:43:15 $
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
     * <p>Instantiate and return a new DynaBean instance, associated
     * with this DynaClass.  <strong>NOTE</strong> - This operation is not
     * supported, and throws an exception.  You should create new
     * <code>WrapDynaBean</code> instances by calling its constructor:</p>
     * <pre>
     *   Object javaBean = ...;
     *   DynaBean wrapper = new WrapDynaBean(javaBean);
     * </pre>
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
