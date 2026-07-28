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

package org.apache.commons.beanutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link DynaProperty}.
 */
public class DynaPropertyTest {

    private DynaProperty testPropertyWithName;

    private DynaProperty testProperty1Duplicate;

    private DynaProperty testPropertyWithNameAndType;

    private DynaProperty testProperty2Duplicate;

    private DynaProperty testPropertyWithNameAndTypeAndContentType;

    private DynaProperty testProperty3Duplicate;

    /**
     * Set up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() {
        testPropertyWithName = new DynaProperty("test1");
        testProperty1Duplicate = new DynaProperty("test1");
        testPropertyWithNameAndType = new DynaProperty("test2", Integer.class);
        testProperty2Duplicate = new DynaProperty("test2", Integer.class);
        testPropertyWithNameAndTypeAndContentType = new DynaProperty("test3", List.class, Short.class);
        testProperty3Duplicate = new DynaProperty("test3", List.class, Short.class);
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        testPropertyWithName = testProperty1Duplicate = null;
        testPropertyWithNameAndType = testProperty2Duplicate = null;
        testPropertyWithNameAndTypeAndContentType = testProperty3Duplicate = null;
    }

    /**
     * Class under test for boolean equals(Object)
     */
    @Test
    public void testEqualsObject() {
        assertEquals(testPropertyWithName, testProperty1Duplicate);
        assertEquals(testPropertyWithNameAndType, testProperty2Duplicate);
        assertEquals(testPropertyWithNameAndTypeAndContentType, testProperty3Duplicate);
        assertNotEquals(testPropertyWithName, testPropertyWithNameAndType);
        assertNotEquals(testPropertyWithNameAndType, testPropertyWithNameAndTypeAndContentType);
        assertNotNull(testPropertyWithName);
    }

    /**
     * Class under test for int hashCode(Object)
     */
    @Test
    public void testHashCode() {
        final int initialHashCode = testPropertyWithNameAndTypeAndContentType.hashCode();
        assertEquals(testPropertyWithName.hashCode(), testProperty1Duplicate.hashCode());
        assertEquals(testPropertyWithNameAndType.hashCode(), testProperty2Duplicate.hashCode());
        assertEquals(testPropertyWithNameAndTypeAndContentType.hashCode(), testProperty3Duplicate.hashCode());
        assertEquals(initialHashCode, testPropertyWithNameAndTypeAndContentType.hashCode());
    }
}
