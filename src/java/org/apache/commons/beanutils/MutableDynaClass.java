/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/MutableDynaClass.java,v 1.7 2003/10/09 20:43:15 rdonkin Exp $
 * $Revision: 1.7 $
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





/**
 * <p>A specialized extension to <code>DynaClass</code> that allows properties
 * to be added or removed dynamically.</p>
 *
 * <p><strong>WARNING</strong> - No guarantees that this will be in the final
 * APIs ... it's here primarily to preserve some concepts that were in the
 * original proposal for further discussion.</p>
 *
 * @author Craig McClanahan
 * @author Michael Smith
 * @author Paulo Gaspar
 * @version $Revision: 1.7 $ $Date: 2003/10/09 20:43:15 $
 */

public interface MutableDynaClass extends DynaClass {


    /**
     * Add a new dynamic property with no restrictions on data type,
     * readability, or writeability.
     *
     * @param name Name of the new dynamic property
     *
     * @exception IllegalArgumentException if name is null
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    public void add(String name);


    /**
     * Add a new dynamic property with the specified data type, but with
     * no restrictions on readability or writeability.
     *
     * @param name Name of the new dynamic property
     * @param type Data type of the new dynamic property (null for no
     *  restrictions)
     *
     * @exception IllegalArgumentException if name is null
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    public void add(String name, Class type);


    /**
     * Add a new dynamic property with the specified data type, readability,
     * and writeability.
     *
     * @param name Name of the new dynamic property
     * @param type Data type of the new dynamic property (null for no
     *  restrictions)
     * @param readable Set to <code>true</code> if this property value
     *  should be readable
     * @param writeable Set to <code>true</code> if this property value
     *  should be writeable
     *
     * @exception IllegalArgumentException if name is null
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    public void add(String name, Class type, boolean readable,
                    boolean writeable);


    /**
     * Is this DynaClass currently restricted, if so, no changes to the
     * existing registration of property names, data types, readability, or
     * writeability are allowed.
     */
    public boolean isRestricted();


    /**
     * Remove the specified dynamic property, and any associated data type,
     * readability, and writeability, from this dynamic class.
     * <strong>NOTE</strong> - This does <strong>NOT</strong> cause any
     * corresponding property values to be removed from DynaBean instances
     * associated with this DynaClass.
     *
     * @param name Name of the dynamic property to remove
     *
     * @exception IllegalArgumentException if name is null
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no properties can be removed
     */
    public void remove(String name);


    /**
     * Set the restricted state of this DynaClass to the specified value.
     *
     * @param restricted The new restricted state
     */
    public void setRestricted(boolean restricted);


}
