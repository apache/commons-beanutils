/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/locale/converters/DateLocaleConverterTestCase.java,v 1.3 2003/10/09 20:39:04 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/09 20:39:04 $
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

package org.apache.commons.beanutils.locale.converters;

import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.Locale;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConversionException;

/**
 * Test Case for the DateLocaleConverter class.
 *
 * @author Robert Burrell Donkin
 * @version $Revision: 1.3 $ $Date: 2003/10/09 20:39:04 $
 */

public class DateLocaleConverterTestCase extends TestCase {

    // ------------------------------------------------------------------------

    public DateLocaleConverterTestCase(String name) {
        super(name);
    }
    
    // ------------------------------------------------------------------------

    public static TestSuite suite() {
        return new TestSuite(DateLocaleConverterTestCase.class);        
    }


    // ------------------------------------------------------------------------

    public void testSetLenient() {
        // make sure that date format works as expected
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.UK);
        
        // test with no leniency
        dateFormat.setLenient(false);
        
        try {
            
            dateFormat.parse("Feb 10, 2001");
            
        } catch (ParseException e) {
            fail("Could not parse date (1) - " + e.getMessage());
        }
        
        try {
        
            dateFormat.parse("Feb 31, 2001");
            fail("Parsed illegal date (1)");
        
        } catch (ParseException e) {
            // that's what we expected
        }	
        
        // test with leniency
        dateFormat.setLenient(true);
        
        try {
            
            dateFormat.parse("Feb 10, 2001");
            
        } catch (ParseException e) {
            fail("Could not parse date (2) - " + e.getMessage());
        }
        
        try {
        
            dateFormat.parse("Feb 31, 2001");
        
        } catch (ParseException e) {
            fail("Could not parse date (3) - " + e.getMessage());
        }
        
        // now repeat tests for converter
        DateLocaleConverter converter = new DateLocaleConverter(Locale.UK, "MMM dd, yyyy");
        
        // test with no leniency
        converter.setLenient(false);
        assertEquals("Set lenient failed", converter.isLenient(), false);
        
        try {
            
            converter.convert("Feb 10, 2001");
            
        } catch (ConversionException e) {
            fail("Could not parse date (4) - " + e.getMessage());
        }
        
        try {
        
            converter.convert("Feb 31, 2001");
            assertEquals("Set lenient failed", converter.isLenient(), false);
            fail("Parsed illegal date (2)");
        
        } catch (ConversionException e) {
            // that's what we expected
        }	
        
        // test with leniency
        converter.setLenient(true);
        assertEquals("Set lenient failed", converter.isLenient(), true);
        
        try {
            
            converter.convert("Feb 10, 2001");
            
        } catch (ConversionException e) {
            fail("Could not parse date (5) - " + e.getMessage());
        }
        
        try {
        
            converter.convert("Feb 31, 2001");
        
        } catch (ConversionException e) {
            fail("Could not parse date (6) - " + e.getMessage());
        }
    }
}

