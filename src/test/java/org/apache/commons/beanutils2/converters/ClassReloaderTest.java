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

package org.apache.commons.beanutils2.converters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the ClassReloader utility class.
 */
class ClassReloaderTest {

    public static class DummyClass {
    }

    /**
     * Test basic operation of the ClassReloader.
     */
    @Test
    void testBasicOperation() throws Exception {
        final ClassLoader sharedLoader = this.getClass().getClassLoader();
        final ClassReloader componentLoader = new ClassReloader(sharedLoader);

        final Class<?> sharedClass = DummyClass.class;
        final Class<?> componentClass = componentLoader.reload(sharedClass);

        // the two Class objects contain the same bytecode, but are not equal
        assertTrue(sharedClass != componentClass);

        // the two class objects have different class loaders
        assertSame(sharedLoader, sharedClass.getClassLoader());
        assertSame(componentLoader, componentClass.getClassLoader());
        assertTrue(sharedLoader != componentLoader);

        // verify that objects of these two types are not assignment-compatible
        final Object obj1 = sharedClass.newInstance();
        final Object obj2 = componentClass.newInstance();

        assertTrue(sharedClass.isInstance(obj1), "Obj1 class incorrect");
        assertFalse(componentClass.isInstance(obj1), "Obj1 class incorrect");
        assertFalse(sharedClass.isInstance(obj2), "Obj2 class incorrect");
        assertTrue(componentClass.isInstance(obj2), "Obj2 class incorrect");
    }

}
