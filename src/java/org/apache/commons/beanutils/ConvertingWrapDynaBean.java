/*
 * $Header: /home/cvs/jakarta-commons/beanutils/src/java/org/apache/commons/beanutils/WrapDynaBean.java,v 1.3 2002/01/23 22:35:58 sanders Exp $
 * $Revision: 1.3 $
 * $Date: 2002/01/23 22:35:58 $
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


/**
 * <p>Implementation of <code>DynaBean</code> that wraps a standard JavaBean
 * instance, so that DynaBean APIs can be used to access its properties,
 * though this implementation allows type conversion to occur when properties are set.
 * This means that (say) Strings can be passed in as values in setter methods and
 * this DynaBean will convert them to the correct primitive data types.</p>
 *
 * <p><strong>IMPLEMENTATION NOTE</strong> - This implementation does not
 * support the <code>contains()</code> and <code>remove()</code> methods.</p>
 *
 * @author James Strachan
 * @version $Revision: 1.3 $ $Date: 2002/01/23 22:35:58 $
 */

public class ConvertingWrapDynaBean extends WrapDynaBean {



    /**
     * Construct a new <code>DynaBean</code> associated with the specified
     * JavaBean instance.
     *
     * @param instance JavaBean instance to be wrapped
     */
    public ConvertingWrapDynaBean(Object instance) {

        super(instance);

    }


    /**
     * Set the value of the property with the specified name
     * performing any type conversions if necessary. So this method
     * can accept String values for primitive numeric data types for example.
     *
     * @param name Name of the property whose value is to be set
     * @param value Value to which this property is to be set
     *
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     * @exception NullPointerException if an attempt is made to set a
     *  primitive property to null
     */
    public void set(String name, Object value) {

        try {
            BeanUtils.copyProperty(instance, name, value);
        } catch (Throwable t) {
            throw new IllegalArgumentException
                    ("Property '" + name + "' has no write method");
        }

    }
}
