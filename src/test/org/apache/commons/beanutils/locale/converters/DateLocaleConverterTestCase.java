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
 * @version $Revision: 1.4 $ $Date: 2004/02/28 13:18:37 $
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

