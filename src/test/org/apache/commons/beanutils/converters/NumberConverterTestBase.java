/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/converters/NumberConverterTestBase.java,v 1.2 2003/10/05 13:32:38 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/05 13:32:38 $
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
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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

package org.apache.commons.beanutils.converters;

import junit.framework.TestCase;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;


/**
 * Abstract base for &lt;Number&gt;Converter classes.
 *
 * @author Rodney Waldhoff
 * @version $Revision: 1.2 $ $Date: 2003/10/05 13:32:38 $
 */

public abstract class NumberConverterTestBase extends TestCase {

    // ------------------------------------------------------------------------

    public NumberConverterTestBase(String name) {
        super(name);
    }
    
    // ------------------------------------------------------------------------
    
    protected abstract Converter makeConverter();
    protected abstract Class getExpectedType();

    // ------------------------------------------------------------------------

    /**
     * Assumes ConversionException in response to covert(getExpectedType(),null).
     */
    public void testConvertNull() throws Exception {
        try {
            makeConverter().convert(getExpectedType(),null);
            fail("Expected ConversionException");
        } catch(ConversionException e) {
            // expected
        }
    }

    /**
     * Assumes convert(getExpectedType(),Number) returns some non-null
     * instance of getExpectedType().
     */
    public void testConvertNumber() throws Exception {
        String[] message= { 
            "from Byte",
            "from Short",
            "from Integer",
            "from Long",
            "from Float",
            "from Double"
        };

        Object[] number = {
            new Byte((byte)7),
            new Short((short)8),
            new Integer(9),
            new Long(10),
            new Float(11.1),
            new Double(12.2)
        };

        for(int i=0;i<number.length;i++) {
            Object val = makeConverter().convert(getExpectedType(),number[i]);
            assertNotNull("Convert " + message[i] + " should not be null",val);
            assertTrue(
                "Convert " + message[i] + " should return a " + getExpectedType().getName(), 
                getExpectedType().isInstance(val));
        }
    }
}

