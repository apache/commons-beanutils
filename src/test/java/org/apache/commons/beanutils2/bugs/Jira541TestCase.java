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
package org.apache.commons.beanutils2.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.beanutils2.FluentPropertyBeanIntrospector;
import org.apache.commons.beanutils2.PropertyUtilsBean;
import org.junit.jupiter.api.Test;

/**
 * Fix BEANUTILS-541
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-541">https://issues.apache.org/jira/browse/BEANUTILS-541</a>
 */
public class Jira541TestCase {

    public static class BaseType {

        private String field;

        public String getField() {
            return field;
        }

        public BaseType setField(final String objectName) {
            this.field = objectName;
            return this;
        }
    }
    public static class SubTypeA extends BaseType {

        @Override
        public SubTypeA setField(final String field) {
            super.setField(field);
            return this;
        }
    }

    public static class SubTypeB extends BaseType {

    }

    private static final String FIELD_NAME = "field";

    private static final String FIELD_VALUE = "name";

    private static void testImpl() throws ReflectiveOperationException {
        final PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        propertyUtilsBean.addBeanIntrospector(new FluentPropertyBeanIntrospector());

        // note: we should setProperty first on SubTypeA (with overridden setter), then on subTypeB
        // but not vice versa
        final SubTypeA subTypeA = new SubTypeA();
        propertyUtilsBean.setProperty(subTypeA, FIELD_NAME, FIELD_VALUE);

        final SubTypeB subTypeB = new SubTypeB();
        propertyUtilsBean.setProperty(subTypeB, FIELD_NAME, FIELD_VALUE);

        assertEquals(FIELD_VALUE, subTypeA.getField());
        assertEquals(FIELD_VALUE, subTypeB.getField());
    }

    @Test
    public void testFluentBeanIntrospectorOnOverriddenSetter() throws Exception {
        testImpl();
    }

    @Test
    public void testFluentBeanIntrospectorOnOverriddenSetterConcurrent() throws Exception {
        final ExecutorService executionService = Executors.newFixedThreadPool(256);
        try {
            final List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                futures.add(executionService.submit(() -> {
                    testImpl();
                    return null;
                }));
            }
            for (final Future<?> future : futures) {
                future.get();
            }
        } finally {
            executionService.shutdown();
        }
    }
}
