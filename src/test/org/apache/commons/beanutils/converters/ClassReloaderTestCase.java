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

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for the ClassReloader utility class.
 */

public class ClassReloaderTestCase extends TestCase {

    // ------------------------------------------------------------------------

    public ClassReloaderTestCase(String name) {
        super(name);
    }


    public static TestSuite suite() {
        return new TestSuite(ClassReloaderTestCase.class);
    }

    // ------------------------------------------------------------------------

    public static class DummyClass {
    }
        
    /**
     * Test basic operation of the ClassReloader.
     */
    public void testBasicOperation() throws Exception {
        ClassLoader sharedLoader = this.getClass().getClassLoader();
        ClassReloader componentLoader = new ClassReloader(sharedLoader);

        Class sharedClass = DummyClass.class;
        Class componentClass = componentLoader.reload(sharedClass);

        // the two Class objects contain the same bytecode, but are not equal
        assertTrue(sharedClass != componentClass);
        
        // the two class objects have different classloaders
        assertSame(sharedLoader, sharedClass.getClassLoader());
        assertSame(componentLoader, componentClass.getClassLoader());
        assertTrue(sharedLoader != componentLoader);

        // verify that objects of these two types are not assignment-compatible
        Object obj1 = sharedClass.newInstance();
        Object obj2 = componentClass.newInstance();

        assertTrue("Obj1 class incorrect", sharedClass.isInstance(obj1));
        assertFalse("Obj1 class incorrect", componentClass.isInstance(obj1));
        assertFalse("Obj2 class incorrect", sharedClass.isInstance(obj2));
        assertTrue("Obj2 class incorrect", componentClass.isInstance(obj2));
    }

}

