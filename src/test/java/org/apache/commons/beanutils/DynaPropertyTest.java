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

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.jboss.marshalling.cloner.ClassLoaderClassCloner;
import org.jboss.marshalling.cloner.ClonerConfiguration;
import org.jboss.marshalling.cloner.ObjectCloner;
import org.jboss.marshalling.cloner.ObjectCloners;

import junit.framework.TestCase;

/**
 * Test case for {@link DynaProperty}.
 */
public class DynaPropertyTest extends TestCase {

    private DynaProperty testPropertyWithName;

    private DynaProperty testProperty1Duplicate;

    private DynaProperty testPropertyWithNameAndType;

    private DynaProperty testProperty2Duplicate;

    private DynaProperty testPropertyWithNameAndTypeAndContentType;

    private DynaProperty testProperty3Duplicate;

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public DynaPropertyTest(final String name) {
        super(name);
    }

    /**
     * Set up instance variables required by this test case.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
    @Override
    protected void tearDown() throws Exception {
        testPropertyWithName = testProperty1Duplicate = null;
        testPropertyWithNameAndType = testProperty2Duplicate = null;
        testPropertyWithNameAndTypeAndContentType = testProperty3Duplicate = null;
        super.tearDown();
    }

    /**
     * Class under test for boolean equals(Object)
     */
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
    public void testHashCode() {
        final int initialHashCode = testPropertyWithNameAndTypeAndContentType.hashCode();
        assertEquals(testPropertyWithName.hashCode(), testProperty1Duplicate.hashCode());
        assertEquals(testPropertyWithNameAndType.hashCode(), testProperty2Duplicate.hashCode());
        assertEquals(testPropertyWithNameAndTypeAndContentType.hashCode(), testProperty3Duplicate.hashCode());
        assertEquals(initialHashCode, testPropertyWithNameAndTypeAndContentType.hashCode());
    }

    /**
     * Tests basic serialization and deserialization mechanism.
     */
    public void testSerialization() throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buffer);
        oos.writeObject(testPropertyWithNameAndTypeAndContentType);
        oos.flush();
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        Object obj = ois.readObject();

        assertEquals(testPropertyWithNameAndTypeAndContentType, obj);
    }

    /**
     * Tests cloning mechanism via Wildfly Object Cloner.
     */
    public void testCloneViaWildflyObjectCloner() throws Exception {
        final ClonerConfiguration paramConfig = new ClonerConfiguration();
        paramConfig.setClassCloner(new ClassLoaderClassCloner(DynaPropertyTest.class.getClassLoader()));
        final ObjectCloner objectCloner = ObjectCloners.getSerializingObjectClonerFactory().createCloner(paramConfig);

        final Object cloned = objectCloner.clone(testPropertyWithNameAndTypeAndContentType);

        assertEquals(testPropertyWithNameAndTypeAndContentType, cloned);
    }
}
