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

package org.apache.commons.beanutils.locale;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.locale.converters.DateLocaleConverterTestCase;

/**
 * <p>
 * Created a test suite so that new test cases can be added here without having to
 * edit the build.xml.
 * </p>
 *
 * @author  Robert Burrell Donkin
 * @version $Revision: 1.6 $ $Date: 2004/02/28 13:18:37 $
 */

public class LocaleConvertTestSuite {

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        TestSuite testSuite = new TestSuite();
        testSuite.addTestSuite(LocaleConvertUtilsTestCase.class);
        testSuite.addTestSuite(DateLocaleConverterTestCase.class);
        testSuite.addTestSuite(LocaleBeanificationTestCase.class);
        return testSuite;
    }
}

