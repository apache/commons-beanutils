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

package org.apache.commons.beanutils2.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.beanutils2.BeanUtilsBean;
import org.apache.commons.beanutils2.PropertyUtilsBean;
import org.apache.commons.beanutils2.SuppressPropertiesBeanIntrospector;
import org.apache.commons.beanutils2.TestEnum;
import org.junit.jupiter.api.Test;

public class EnumDeclaringClassTest {

    public static class Fixture {

        String name = "default";
        TestEnum testEnum = TestEnum.A;

        public String getName() {
            return name;
        }

        public TestEnum getTestEnum() {
            return testEnum;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setTestEnum(final TestEnum day) {
            this.testEnum = day;
        }
    }

    /**
     * Allow opt-out to make your app less secure but allow access to "declaringClass".
     */
    @Test
    public void testAllowAccessToClassPropertyFromBeanUtilsBean() throws ReflectiveOperationException {
        final BeanUtilsBean bub = new BeanUtilsBean();
        final PropertyUtilsBean propertyUtilsBean = bub.getPropertyUtils();
        propertyUtilsBean.removeBeanIntrospector(SuppressPropertiesBeanIntrospector.SUPPRESS_DECLARING_CLASS);
        final Fixture fixture = new Fixture();
        final String string = bub.getProperty(fixture, "testEnum.declaringClass");
        assertEquals(TestEnum.class.getName(), string);
        final Class<TestEnum> teClass = assertInstanceOf(Class.class, propertyUtilsBean.getNestedProperty(fixture, "testEnum.declaringClass"));
        final ClassLoader classLoader = teClass.getClassLoader();
        assertNotNull(classLoader);
        assertNotNull(bub.getProperty(fixture, "testEnum.declaringClass.classLoader"));
        assertInstanceOf(ClassLoader.class, propertyUtilsBean.getNestedProperty(fixture, "testEnum.declaringClass.classLoader"));
    }

    /**
     * Allow opt-out to make your app less secure but allow access to "declaringClass".
     */
    @Test
    public void testAllowAccessToClassPropertyFromPropertyUtilsBean() throws ReflectiveOperationException {
        final PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        propertyUtilsBean.removeBeanIntrospector(SuppressPropertiesBeanIntrospector.SUPPRESS_DECLARING_CLASS);
        final Fixture fixture = new Fixture();
        final Object cls = propertyUtilsBean.getNestedProperty(fixture, "testEnum.declaringClass");
        final Class<TestEnum> teClass = assertInstanceOf(Class.class, cls);
        final ClassLoader classLoader = teClass.getClassLoader();
        assertNotNull(classLoader);
        assertInstanceOf(ClassLoader.class, propertyUtilsBean.getNestedProperty(fixture, "testEnum.declaringClass.classLoader"));
    }

    /**
     * By default opt-in to security that does not allow access to "declaringClass".
     */
    @Test
    public void testSuppressClassPropertyByDefaultFromBeanUtilsBean() throws ReflectiveOperationException {
        final Fixture fixture = new Fixture();
        final BeanUtilsBean bub = new BeanUtilsBean();
        assertThrows(NoSuchMethodException.class, () -> bub.getProperty(fixture, "testEnum.declaringClass.classLoader"));
        assertThrows(NoSuchMethodException.class, () -> bub.getPropertyUtils().getNestedProperty(fixture, "testEnum.declaringClass.classLoader"));
    }

    /**
     * By default opt-in to security that does not allow access to "declaringClass".
     */
    @Test
    public void testSuppressClassPropertyByDefaultFromPropertyUtilsBean() throws ReflectiveOperationException {
        final Fixture fixture = new Fixture();
        final PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        assertThrows(NoSuchMethodException.class, () -> propertyUtilsBean.getNestedProperty(fixture, "testEnum.declaringClass.classLoader"));
    }
}
