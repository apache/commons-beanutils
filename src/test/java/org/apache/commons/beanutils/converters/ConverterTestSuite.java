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

package org.apache.commons.beanutils.converters;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * Created a test suite so that new test cases can be added here without having to
 * edit the build.xml.
 * </p>
 *
 */

public class ConverterTestSuite {

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        final TestSuite testSuite = new TestSuite();
        testSuite.addTestSuite(ArrayConverterTestCase.class);
        testSuite.addTestSuite(BigDecimalConverterTestCase.class);
        testSuite.addTestSuite(BigIntegerConverterTestCase.class);
        testSuite.addTestSuite(BooleanArrayConverterTestCase.class);
        testSuite.addTestSuite(BooleanConverterTestCase.class);
        testSuite.addTestSuite(ByteConverterTestCase.class);
        testSuite.addTestSuite(CalendarConverterTestCase.class);
        testSuite.addTestSuite(CharacterConverterTest.class);
        testSuite.addTestSuite(ClassConverterTest.class);
        testSuite.addTestSuite(DateConverterTest.class);
        testSuite.addTestSuite(DoubleConverterTest.class);
        testSuite.addTestSuite(FileConverterTest.class);
        testSuite.addTestSuite(FloatConverterTest.class);
        testSuite.addTestSuite(IntegerConverterTest.class);
        testSuite.addTestSuite(LongConverterTest.class);
        testSuite.addTestSuite(ShortConverterTest.class);
        testSuite.addTestSuite(SqlDateConverterTest.class);
        testSuite.addTestSuite(SqlTimeConverterTest.class);
        testSuite.addTestSuite(SqlTimestampConverterTest.class);
        testSuite.addTestSuite(StringArrayConverterTest.class);
        testSuite.addTestSuite(URLConverterTest.class);
        return testSuite;
    }
}

