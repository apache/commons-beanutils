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

package org.apache.commons.beanutils.converters;

import junit.framework.TestCase;
import junit.framework.TestCase;

/**
 * Test Case for StringArrayConverter
 *
 * @author Robert Burrell Donkin
 * @version $Revision$ $Date$
 */

public class StringArrayConverterTestCase extends TestCase {
    
    public StringArrayConverterTestCase(String name) {
        super(name);
    }
    
    public void testIntToString() {
        
        int[] testArray = {1, 2, 3, 4, 5};
        String[] results = (String []) new StringArrayConverter().convert(String.class, testArray);
        
        assertEquals("Incorrect results size", 5, results.length);
        assertEquals("Entry one is wrong", "1", results[0]); 
        assertEquals("Entry two is wrong", "2", results[1]); 
        assertEquals("Entry three is wrong", "3", results[2]); 
        assertEquals("Entry four is wrong", "4", results[3]); 
        assertEquals("Entry five is wrong", "5", results[4]); 
    }
}

