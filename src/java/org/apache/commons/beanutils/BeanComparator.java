/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/Attic/BeanComparator.java,v 1.9 2004/01/06 20:53:15 rdonkin Exp $
 * $Revision: 1.9 $
 * $Date: 2004/01/06 20:53:15 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
import java.util.Comparator;
import org.apache.commons.beanutils.PropertyUtils;
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
 * </p>
 *
 * @author     <a href"mailto:epugh@upstate.com">Eric Pugh</a>
 * @author Tim O'Brien 
 */
public class BeanComparator implements Comparator, Serializable {

    private String property;
    private Comparator comparator;

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
    public BeanComparator( String property ) {
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
    public BeanComparator( String property, Comparator comparator ) {
        setProperty( property );
        this.comparator = comparator;
    }

    /**
     * Sets the method to be called to compare two JavaBeans
     *
     * @param property String method name to call to compare 
     * If the property passed in is null then the actual objects will be compared
     */
    public void setProperty( String property ) {
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
     */
    public Comparator getComparator() {
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
    public int compare( Object o1, Object o2 ) {
        
        if ( property == null ) {
            // compare the actual objects
            return comparator.compare( o1, o2 );
        }
        
        try {
            Object value1 = PropertyUtils.getProperty( o1, property );
            Object value2 = PropertyUtils.getProperty( o2, property );
            return comparator.compare( value1, value2 );
        }
        catch ( Exception e ) {
            throw new ClassCastException( e.toString() );
        }
    }

}
