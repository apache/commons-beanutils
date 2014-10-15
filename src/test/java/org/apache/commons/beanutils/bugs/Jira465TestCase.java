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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Indexed List Setters no longer work.
 *
 * @version $Id$
 * @see <a
 * href="https://issues.apache.org/jira/browse/BEANUTILS-465">https://issues.apache.org/jira/browse/BEANUTILS-465</a>
 */
public class Jira465TestCase extends TestCase {
    /** Constant for the property path. */
    private static final String PATH = "foo[0]";

    /** Constant for the updated value. */
    private static final String NEW_VALUE = "2";

    /** Constant for the original value. */
    private static final String OLD_VALUE = "1";

    /**
     * Changes the value of the test property.
     *
     * @param bean the bean to be updated
     */
    private static void changeValue(final Object bean) {
        try {
            BeanUtils.setProperty(bean, PATH, NEW_VALUE);
        } catch (final Exception e) {
            fail("Could not set property: " + e);
        }
    }

    public void testArrayProperty() throws InvocationTargetException,
            IllegalAccessException {
        final ArrayProp bean = new ArrayProp();
        changeValue(bean);
        assertEquals("Wrong value", NEW_VALUE, bean.getFoo()[0]);
    }

    public void testArrayIndexedProperty() {
        final ArrayIndexedProp bean = new ArrayIndexedProp();
        changeValue(bean);
        assertEquals("Wrong value", NEW_VALUE, bean.getFoo(0));
    }

    public void testListProperty() {
        final ListProp bean = new ListProp();
        changeValue(bean);
        assertEquals("Wrong value", NEW_VALUE, bean.getFoo().get(0));
    }

    public void testListIndexedProperty() {
        final ListIndexedProp bean = new ListIndexedProp();
        changeValue(bean);
        assertEquals("Wrong value", NEW_VALUE, bean.getFoo(0));
    }

    public static class ArrayProp {
        private Object[] foo = new Object[] { OLD_VALUE };

        public Object[] getFoo() {
            return foo;
        }

        public void setFoo(final Object[] foo) {
            this.foo = foo;
        }
    }

    public static class ArrayIndexedProp {
        private final Object[] foo = new Object[] { OLD_VALUE };

        public Object getFoo(final int i) {
            return foo[i];
        }

        public void setFoo(final int i, final Object value) {
            this.foo[i] = value;
        }
    }

    public static class ListProp {
        private List<String> foo = new ArrayList<String>(Arrays.asList(OLD_VALUE));

        public List<String> getFoo() {
            return foo;
        }

        public void setFoo(final List<String> foo) {
            this.foo = foo;
        }
    }

    public static class ListIndexedProp {
        private final List<String> foo = new ArrayList<String>(Arrays.asList(OLD_VALUE));

        public String getFoo(final int i) {
            return foo.get(i);
        }

        public void setFoo(final int i, final String value) {
            this.foo.set(i, value);
        }
    }
}
