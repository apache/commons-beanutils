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

package org.apache.commons.beanutils;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * <p>Test suite which runs all the test cases application to beans in collections.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Revision$
 */

public class BeanCollectionsTestSuite extends TestCase {

    public BeanCollectionsTestSuite(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(BeanComparatorTestCase.class);
        suite.addTestSuite(BeanToPropertyValueTransformerTest.class);
        suite.addTestSuite(BeanPropertyValueEqualsPredicateTest.class);
        suite.addTestSuite(BeanPropertyValueChangeClosureTest.class);
        suite.addTestSuite(TestBeanMap.class);

        return suite;
    }
}


