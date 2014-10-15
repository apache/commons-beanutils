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
package org.apache.commons.beanutils.bugs;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.commons.beanutils.MethodUtils;

/**
 * MethodUtils's getMatchingAccessibleMethod() does not correctly
 * handle inheritance and method overloading.
 *
 * @version $Id$
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-381">https://issues.apache.org/jira/browse/BEANUTILS-381</a>
 */
public class Jira381TestCase extends TestCase {

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira381TestCase(final String name) {
        super(name);
    }

    /**
     * Test with an private class that overrides a public method
     * of a "grand parent" public class.
     * <p />
     * See Jira issue# BEANUTILS-381.
     */
    public void testIssue_BEANUTILS_381_getMatchingAccessibleMethod() {

        final Class<?> target = TestServiceBean.class;
        final String methodName = "performOp";
        final Class<?>[] runtimeClasses = new Class<?>[]{TestObjectSubclass.class};

        final Method returned = MethodUtils.getMatchingAccessibleMethod(target, methodName, runtimeClasses);

        assertEquals(target, returned.getDeclaringClass());
        assertEquals(methodName, returned.getName());
        assertEquals(TestObject.class, returned.getParameterTypes()[0]);
    }

    /**
     * Test bean.
     */
    public class TestServiceBean{

        /**
         * Generic object method
         */
        public void performOp(final Object o){
        }

        /**
         * Object method
         */
        public void performOp(final TestObject o){
        }
    }

    /**
     * Test object.
     *
     */
    public class TestObject{
    }

    /**
     * Used to match performop with test object
     */
    public class TestObjectSubclass extends TestObject{
    }
}
