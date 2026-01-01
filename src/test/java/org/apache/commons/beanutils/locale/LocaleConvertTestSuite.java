/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils.locale;

import org.apache.commons.beanutils.locale.converters.BigDecimalLocaleConverterTest;
import org.apache.commons.beanutils.locale.converters.BigIntegerLocaleConverterTest;
import org.apache.commons.beanutils.locale.converters.ByteLocaleConverterTest;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverterTest;
import org.apache.commons.beanutils.locale.converters.DoubleLocaleConverterTest;
import org.apache.commons.beanutils.locale.converters.FloatLocaleConverterTest;
import org.apache.commons.beanutils.locale.converters.IntegerLocaleConverterTest;
import org.apache.commons.beanutils.locale.converters.LongLocaleConverterTest;
import org.apache.commons.beanutils.locale.converters.ShortLocaleConverterTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * Created a test suite so that new test cases can be added here without having to
 * edit the build.xml.
 * </p>
 *
 */

public class LocaleConvertTestSuite {

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        final TestSuite testSuite = new TestSuite();
        testSuite.addTestSuite(LocaleConvertUtilsTest.class);
        testSuite.addTestSuite(LocaleBeanificationTest.class);
        testSuite.addTestSuite(BigDecimalLocaleConverterTest.class);
        testSuite.addTestSuite(BigIntegerLocaleConverterTest.class);
        testSuite.addTestSuite(ByteLocaleConverterTest.class);
        testSuite.addTestSuite(DateLocaleConverterTest.class);
        testSuite.addTestSuite(DoubleLocaleConverterTest.class);
        testSuite.addTestSuite(FloatLocaleConverterTest.class);
        testSuite.addTestSuite(IntegerLocaleConverterTest.class);
        testSuite.addTestSuite(LongLocaleConverterTest.class);
        testSuite.addTestSuite(ShortLocaleConverterTest.class);
        return testSuite;
    }
}

