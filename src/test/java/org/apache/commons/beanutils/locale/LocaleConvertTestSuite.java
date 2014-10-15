/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import junit.framework.TestSuite;

import org.apache.commons.beanutils.locale.converters.BigDecimalLocaleConverterTestCase;
import org.apache.commons.beanutils.locale.converters.BigIntegerLocaleConverterTestCase;
import org.apache.commons.beanutils.locale.converters.ByteLocaleConverterTestCase;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverterTestCase;
import org.apache.commons.beanutils.locale.converters.DoubleLocaleConverterTestCase;
import org.apache.commons.beanutils.locale.converters.FloatLocaleConverterTestCase;
import org.apache.commons.beanutils.locale.converters.IntegerLocaleConverterTestCase;
import org.apache.commons.beanutils.locale.converters.LongLocaleConverterTestCase;
import org.apache.commons.beanutils.locale.converters.ShortLocaleConverterTestCase;

/**
 * <p>
 * Created a test suite so that new test cases can be added here without having to
 * edit the build.xml.
 * </p>
 *
 * @version $Id$
 */

public class LocaleConvertTestSuite {

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        final TestSuite testSuite = new TestSuite();
        testSuite.addTestSuite(LocaleConvertUtilsTestCase.class);
        testSuite.addTestSuite(LocaleBeanificationTestCase.class);
        testSuite.addTestSuite(BigDecimalLocaleConverterTestCase.class);
        testSuite.addTestSuite(BigIntegerLocaleConverterTestCase.class);
        testSuite.addTestSuite(ByteLocaleConverterTestCase.class);
        testSuite.addTestSuite(DateLocaleConverterTestCase.class);
        testSuite.addTestSuite(DoubleLocaleConverterTestCase.class);
        testSuite.addTestSuite(FloatLocaleConverterTestCase.class);
        testSuite.addTestSuite(IntegerLocaleConverterTestCase.class);
        testSuite.addTestSuite(LongLocaleConverterTestCase.class);
        testSuite.addTestSuite(ShortLocaleConverterTestCase.class);
        return testSuite;
    }
}

