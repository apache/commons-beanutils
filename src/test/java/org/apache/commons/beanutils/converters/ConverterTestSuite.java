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

package org.apache.commons.beanutils.converters;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * Created a test suite so that new test cases can be added here without having to
 * edit the build.xml.
 * </p>
 *
 * @version $Id$
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
        testSuite.addTestSuite(CharacterConverterTestCase.class);
        testSuite.addTestSuite(ClassConverterTestCase.class);
        testSuite.addTestSuite(DateConverterTestCase.class);
        testSuite.addTestSuite(DoubleConverterTestCase.class);
        testSuite.addTestSuite(FileConverterTestCase.class);
        testSuite.addTestSuite(FloatConverterTestCase.class);
        testSuite.addTestSuite(IntegerConverterTestCase.class);
        testSuite.addTestSuite(LongConverterTestCase.class);
        testSuite.addTestSuite(ShortConverterTestCase.class);
        testSuite.addTestSuite(SqlDateConverterTestCase.class);
        testSuite.addTestSuite(SqlTimeConverterTestCase.class);
        testSuite.addTestSuite(SqlTimestampConverterTestCase.class);
        testSuite.addTestSuite(StringArrayConverterTestCase.class);
        testSuite.addTestSuite(URLConverterTestCase.class);
        return testSuite;
    }
}

