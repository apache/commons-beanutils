/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/Attic/BeanComparator.java,v 1.5 2003/08/25 21:05:02 rdonkin Exp $
 * $Revision: 1.5 $
 * $Date: 2003/08/25 21:05:02 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * Allows you to pass the name of a method in and compare two beans.
 *
 * @author     <a href"mailto:epugh@upstate.com">Eric Pugh</a>
 */
public class BeanComparator implements Comparator, Serializable {

    private String property;
    private Comparator comparator;

    /** 
     * <p>Constructs a Bean Comparator without a property set.
     * <strong>Note</strong> that this is intended to be used 
     * only in bean-centric environments.
     * </p><p>
     * Until {@link setProperty} is called with a non-null value.
     * this comparator will compare the Objects only.
     * </p>
     */
    public BeanComparator() {
        this( null );
    }

    /**
     * Creates a Bean comparator that calls the property specified 
     * on the objects.
     * Defaults to comparing
     * based on the natural order, that is java.lang.Comparable.
     *
     * @param property String name of bean property. 
     * If the property passed in is null then the actual objects will be compared
     */
    public BeanComparator( String property ) {
        this( property, ComparableComparator.getInstance() );
    }

    /**
     * Creates a comparator that compares objects based on the method 
     * name of two JavaBeans.
     *
     * @param property String method name to call to compare 
     * @param comparator      Comparator to call afterwords!
     * If you pass in a null, the BeanComparator defaults to comparing
     * based on the natural order, that is java.lang.Comparable.
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
     * If {@link getProperty} is null then the actual objects will be compared.
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
