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

import junit.framework.TestSuite;
import junit.framework.TestCase;

import org.apache.commons.beanutils.Converter;

import java.net.URL;


/**
 * Test Case for the URLConverter class.
 *
 * @author Henri Yandell
 * @version $Revision$ $Date$
 */

public class URLConverterTestCase extends TestCase {

    private Converter converter = null;

    // ------------------------------------------------------------------------

    public URLConverterTestCase(String name) {
        super(name);
    }
    
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        converter = makeConverter();
    }

    public static TestSuite suite() {
        return new TestSuite(URLConverterTestCase.class);        
    }

    public void tearDown() throws Exception {
        converter = null;
    }

    // ------------------------------------------------------------------------
    
    protected Converter makeConverter() {
        return new URLConverter();
    }
    
    protected Class getExpectedType() {
        return URL.class;
    }

    // ------------------------------------------------------------------------

    public void testSimpleConversion() throws Exception {
        String[] message= { 
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
            "from String",
        };
        
        Object[] input = { 
            "http://www.apache.org",
            "http://www.apache.org/",
            "ftp://cvs.apache.org",
            "file://project.xml",
            "http://208.185.179.12",
            "http://www.apache.org:9999/test/thing",
            "http://user:admin@www.apache.org:50/one/two.three",
            "http://notreal.apache.org",
        };
        
        URL[] expected = { 
            new URL("http://www.apache.org"),
            new URL("http://www.apache.org/"),
            new URL("ftp://cvs.apache.org"),
            new URL("file://project.xml"),
            new URL("http://208.185.179.12"),
            new URL("http://www.apache.org:9999/test/thing"),
            new URL("http://user:admin@www.apache.org:50/one/two.three"),
            new URL("http://notreal.apache.org")
        };
        
        for(int i=0;i<expected.length;i++) {
            assertEquals(message[i] + " to URL",expected[i],converter.convert(URL.class,input[i]));
            assertEquals(message[i] + " to null type",expected[i],converter.convert(null,input[i]));
        }
    }
    
}

