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

import junit.framework.TestCase;
import java.util.Locale;
import org.apache.commons.beanutils.locale.BaseLocaleConverter
;

/**
 * Base Test Case for the DecimalLocaleConverter classes.
 *
 * @author Niall Pemberton
 * @version $Revision: 1.1 $ $Date: 2004/07/08 13:12:06 $
 */

public class BaseLocaleConverterTestCase extends TestCase {


    // Original Default Locale
    protected Locale origLocale;

    // Converter
    protected BaseLocaleConverter converter;
    protected Object result;
    protected Object defaultValue;
    protected Object expectedValue;


    // Localized values
    protected Locale localizedLocale;
    protected String localizedDecimalPattern; 
    protected String localizedIntegerPattern; 
    protected String localizedDatePattern; 
    protected String localizedDecimalValue;
    protected String localizedIntegerValue;
    protected String localizedDateValue;

    // Locale values
    protected Locale defaultLocale;
    protected String defaultDecimalPattern; 
    protected String defaultIntegerPattern; 
    protected String defaultDatePattern; 
    protected String defaultDecimalValue;
    protected String defaultIntegerValue;
    protected String defaultDateValue;


    // Expected values
    protected String expectedDecimalValue;
    protected String expectedIntegerValue;

    // ---------------------------------------------------------- Constructors

    public BaseLocaleConverterTestCase(String name) {
        super(name);
    }
    
    // -------------------------------------------------- Overall Test Methods

    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {

        // Default Locale (Use US)
        defaultLocale           = Locale.US;
        defaultDecimalPattern   = "#,###.00";
        defaultIntegerPattern   = "#,###";
        defaultDatePattern      = "d MMMM yyyy";
        defaultDecimalValue     = "1,234.56";
        defaultIntegerValue     = "1,234";
        defaultDateValue        = "1 October 2004";

        // Use German Locale (uses different separators to US)
        localizedLocale         = Locale.GERMAN;
        localizedDecimalPattern = "#.###,00";
        localizedIntegerPattern = "#.###";
        localizedDatePattern    = "t MMMM uuuu";
        localizedDecimalValue   = "1.234,56";
        localizedIntegerValue   = "1.234";
        localizedDateValue      = "1 Oktober 2004";

        // Expected Values
        expectedDecimalValue    = "1234.56";
        expectedIntegerValue    = "1234";

        // Reset default to the one specified
        origLocale = Locale.getDefault();

        // Initialize
        converter = null;
        result = null;
        defaultValue = null;
        expectedValue= null;

        if (defaultLocale.equals(origLocale)) {
            origLocale = null;
        } else {
            // System.out.println("Changing default locale from " + origLocale + " to " + defaultLocale);
            Locale.setDefault(defaultLocale);
        }


    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        converter = null;
        result = null;
        defaultValue = null;
        expectedValue= null;

        // Set the Default Locale back to the original value 
        if (origLocale != null) {
            // System.out.println("Restoring default locale to " + origLocale);
            Locale.setDefault(origLocale);
        }

    }


    // -------------------------------------------------- Generic Test Methods

    /**
     * Test Converting Value WITH a pattern
     */
    protected void convertValueWithPattern(BaseLocaleConverter converter, Object value, String pattern, Object expectedValue) {
        convertValueWithPattern(converter, "", value, pattern, expectedValue);
    }

    /**
     * Test Converting Value WITH a pattern
     */
    protected void convertValueWithPattern(BaseLocaleConverter converter, String msgId, Object value, String pattern, Object expectedValue) {

        // Convert value with no pattern
        try {
            result = converter.convert(value, pattern);
        } catch (Exception e) {
            fail("Pattern conversion threw " + msgId + " threw " + e);
        }
        assertEquals("Check conversion value with pattern " + msgId, expectedValue, result);

    }

    /**
     * Test Converting Value WITHOUT a pattern
     */
    protected void convertValueNoPattern(BaseLocaleConverter converter, Object value, Object expectedValue) {
        convertValueNoPattern(converter, "", value, expectedValue);
    }

    /**
     * Test Converting Value WITHOUT a pattern
     */
    protected void convertValueNoPattern(BaseLocaleConverter converter, String msgId, Object value, Object expectedValue) {

        // Convert value with no pattern
        try {
            result = converter.convert(value);
        } catch (Exception e) {
            fail("No Pattern conversion threw " + msgId + " threw " + e);
        }
        assertEquals("Check conversion value without pattern " + msgId, expectedValue, result);


    }

    /**
     * Test Converting Value To a spcified Type
     */
    protected void convertValueToType(BaseLocaleConverter converter, Class clazz, Object value, String pattern, Object expectedValue) {
        convertValueToType(converter, "", clazz, value, pattern, expectedValue);
    }

    /**
     * Test Converting Value To a spcified Type
     */
    protected void convertValueToType(BaseLocaleConverter converter, String msgId, Class clazz, Object value, String pattern, Object expectedValue) {

        // Convert value with no pattern
        try {
            result = converter.convert(clazz, value, pattern);
        } catch (Exception e) {
            fail("Type  conversion threw " + msgId + " threw " + e);
        }
        assertEquals("Check conversion value to type " + msgId, expectedValue, result);

    }

    /**
     * Test Converting Null value.
     */
    protected void convertNull(BaseLocaleConverter converter, Object expectedValue) {
        convertNull(converter, "", expectedValue);
    }

    /**
     * Test Converting Null value.
     */
    protected void convertNull(BaseLocaleConverter converter, String msgId, Object expectedValue) {

        // Convert value with no pattern
        try {
            result = converter.convert(null);
        } catch (Exception e) {
            fail("Null conversion " + msgId + " threw " + e);
        }

        if (expectedValue == null) {
            assertNull("Check null conversion is null " + msgId + " result="+result, result);
        } else {
            assertEquals("Check null conversion is default " + msgId, expectedValue, result);
        }

    }

    /**
     * Test Converting an invalid value.
     */
    protected void convertInvalid(BaseLocaleConverter converter, Object expectedValue) {
        convertInvalid(converter, "", expectedValue);
    }

    /**
     * Test Converting an invalid value.
     */
    protected void convertInvalid(BaseLocaleConverter converter, String msgId, Object expectedValue) {

        // Convert value with no pattern
        try {
            result = converter.convert("xyz");
        } catch (Exception e) {
            fail("Invalid conversion " + msgId + " threw " + e);
        }

        if (expectedValue == null) {
            assertNull("Check invalid conversion is null " + msgId + " result="+result, result);
        } else {
            assertEquals("Check invalid conversion is default " + msgId, expectedValue, result);
        }

    }

}

