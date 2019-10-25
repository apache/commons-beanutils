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

package org.apache.commons.beanutils2;

import junit.framework.TestCase;


/**
 * Test cases for {@code BeanPropertyValueChangeClosure}.
 *
 */
public class BeanPropertyValueChangeConsumerTestCase extends TestCase {

    private static final Integer expectedIntegerValue = new Integer(123);
    private static final Float expectedFloatValue = new Float(123.123f);
    private static final Double expectedDoubleValue = new Double(567879.12344d);
    private static final Boolean expectedBooleanValue = Boolean.TRUE;
    private static final Byte expectedByteValue = new Byte("12");

    /**
     * Constructor for BeanPropertyValueChangeClosureTest.
     *
     * @param name Name of this test case.
     */
    public BeanPropertyValueChangeConsumerTestCase(final String name) {
        super(name);
    }

    /**
     * Test execute with simple float property and Float value.
     */
    public void testExecuteWithSimpleFloatPropertyAndFloatValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("floatProperty", expectedFloatValue).accept(testBean);
        assertTrue(expectedFloatValue.floatValue() == testBean.getFloatProperty());
    }

    /**
     * Test execute with simple float property and String value.
     */
    public void testExecuteWithSimpleFloatPropertyAndStringValue() {
        try {
            new BeanPropertyValueChangeConsumer<>("floatProperty", "123").accept(new TestBean());
            fail("Should have thrown an IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* this is what we expect */
        }
    }

    /**
     * Test execute with simple float property and Double value.
     */
    public void testExecuteWithSimpleFloatPropertyAndDoubleValue() {
        try {
            new BeanPropertyValueChangeConsumer<>("floatProperty", expectedDoubleValue).accept(new TestBean());
            fail("Should have thrown an IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* this is what we expect */
        }
    }

    /**
     * Test execute with simple float property and Integer value.
     */
    public void testExecuteWithSimpleFloatPropertyAndIntegerValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("floatProperty", expectedIntegerValue).accept(testBean);
        assertTrue(expectedIntegerValue.floatValue() == testBean.getFloatProperty());
    }

    /**
     * Test execute with simple double property and Double value.
     */
    public void testExecuteWithSimpleDoublePropertyAndDoubleValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("doubleProperty", expectedDoubleValue).accept(testBean);
        assertTrue(expectedDoubleValue.doubleValue() == testBean.getDoubleProperty());
    }

    /**
     * Test execute with simple double property and String value.
     */
    public void testExecuteWithSimpleDoublePropertyAndStringValue() {
        try {
            new BeanPropertyValueChangeConsumer<>("doubleProperty", "123").accept(new TestBean());
            fail("Should have thrown an IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* this is what we expect */
        }
    }

    /**
     * Test execute with simple double property and Float value.
     */
    public void testExecuteWithSimpleDoublePropertyAndFloatValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("doubleProperty", expectedFloatValue).accept(testBean);
        assertTrue(expectedFloatValue.doubleValue() == testBean.getDoubleProperty());
    }

    /**
     * Test execute with simple double property and Integer value.
     */
    public void testExecuteWithSimpleDoublePropertyAndIntegerValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("doubleProperty", expectedIntegerValue).accept(testBean);
        assertTrue(expectedIntegerValue.doubleValue() == testBean.getDoubleProperty());
    }

    /**
     * Test execute with simple int property and Double value.
     */
    public void testExecuteWithSimpleIntPropertyAndDoubleValue() {
        try {
            new BeanPropertyValueChangeConsumer<>("intProperty", expectedDoubleValue).accept(new TestBean());
            fail("Should have thrown an IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* this is what we expect */
        }
    }

    /**
     * Test execute with simple int property and String value.
     */
    public void testExecuteWithSimpleIntPropertyAndStringValue() {
        try {
            new BeanPropertyValueChangeConsumer<>("intProperty", "123").accept(new TestBean());
            fail("Should have thrown an IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* this is what we expect */
        }
    }

    /**
     * Test execute with simple int property and Float value.
     */
    public void testExecuteWithSimpleIntPropertyAndFloatValue() {
        try {
            new BeanPropertyValueChangeConsumer<>("intProperty", expectedFloatValue).accept(new TestBean());
            fail("Should have thrown an IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* this is what we expect */
        }
    }

    /**
     * Test execute with simple int property and Integer value.
     */
    public void testExecuteWithSimpleIntPropertyAndIntegerValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("intProperty", expectedIntegerValue).accept(testBean);
        assertTrue(expectedIntegerValue.intValue() == testBean.getIntProperty());
    }

    /**
     * Test execute with simple boolean property and Boolean value.
     */
    public void testExecuteWithSimpleBooleanPropertyAndBooleanValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("booleanProperty", expectedBooleanValue).accept(testBean);
        assertTrue(expectedBooleanValue.booleanValue() == testBean.getBooleanProperty());
    }

    /**
     * Test execute with simple boolean property and String value.
     */
    public void testExecuteWithSimpleBooleanPropertyAndStringValue() {
        try {
            new BeanPropertyValueChangeConsumer<>("booleanProperty", "true").accept(new TestBean());
            fail("Should have thrown an IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* this is what we expect */
        }
    }

    /**
     * Test execute with simple byte property and Byte value.
     */
    public void testExecuteWithSimpleBytePropertyAndByteValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("byteProperty", expectedByteValue).accept(testBean);
        assertTrue(expectedByteValue.byteValue() == testBean.getByteProperty());
    }

    /**
     * Test execute with simple boolean property and String value.
     */
    public void testExecuteWithSimpleBytePropertyAndStringValue() {
        try {
            new BeanPropertyValueChangeConsumer<>("byteProperty", "foo").accept(new TestBean());
            fail("Should have thrown an IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* this is what we expect */
        }
    }

    /**
     * Test execute with simple primitive property and null value.
     */
    public void testExecuteWithSimplePrimitivePropertyAndNullValue() {
        try {
            new BeanPropertyValueChangeConsumer<>("intProperty", null).accept(new TestBean());
            fail("Should have thrown an IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* this is what we expect */
        }
    }

    /**
     * Test execute with read only property.
     */
    public void testExecuteWithReadOnlyProperty() {
        try {
            new BeanPropertyValueChangeConsumer<>("readOnlyProperty", "foo").accept(new TestBean());
            fail("Should have thrown an IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* this is what we expect */
        }
    }

    /**
     * Test execute with write only property.
     */
    public void testExecuteWithWriteOnlyProperty() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("writeOnlyProperty", "foo").accept(testBean);
        assertEquals("foo", testBean.getWriteOnlyPropertyValue());
    }

    /**
     * Test execute with a nested property.
     */
    public void testExecuteWithNestedProperty() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("nested.stringProperty", "bar").accept(testBean);
        assertEquals("bar", testBean.getNested().getStringProperty());
    }

    /**
     * Test execute with a nested property and null in the property path.
     */
    public void testExecuteWithNullInPropertyPath() {
        try {
            new BeanPropertyValueChangeConsumer<>("anotherNested.stringProperty", "foo").accept(new TestBean());
            fail("Should have thrown an IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* this is what we expect */
        }
    }

    /**
     * Test execute with a nested property and null in the property path and ignoreNull = true.
     */
    public void testExecuteWithNullInPropertyPathAngIgnoreTrue() {
        final TestBean testBean = new TestBean();

        // create a consumer that will attempt to set a property on the null bean in the path
        final BeanPropertyValueChangeConsumer<TestBean, Object> consumer = new BeanPropertyValueChangeConsumer<>(
                "anotherNested.stringProperty", "Should ignore exception", true);

        try {
            consumer.accept(testBean);
        } catch (final IllegalArgumentException e) {
            fail("Should have ignored the exception.");
        }
    }

    /**
     * Test execute with indexed property.
     */
    public void testExecuteWithIndexedProperty() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("intIndexed[0]", expectedIntegerValue).accept(testBean);
        assertTrue(expectedIntegerValue.intValue() == testBean.getIntIndexed(0));
    }

    /**
     * Test execute with mapped property.
     */
    public void testExecuteWithMappedProperty() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("mappedProperty(fred)", "barney").accept(testBean);
        assertEquals("barney", testBean.getMappedProperty("fred"));
    }

    /**
     * Test execute with a simple String property.
     */
    public void testExecuteWithSimpleStringProperty() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("stringProperty", "barney").accept(testBean);
        assertEquals("barney", testBean.getStringProperty());
    }

    /**
     * Test execute with an invalid property name.
     */
    public void testExecuteWithInvalidPropertyName() {
        try {
            new BeanPropertyValueChangeConsumer<>("bogusProperty", "foo").accept(new TestBean());
            fail("Should have thrown an IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* this is what we expect */
        }
    }
}
