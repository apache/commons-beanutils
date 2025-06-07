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

package org.apache.commons.beanutils2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test cases for {@code BeanPropertyValueChangeClosure}.
 */
public class BeanPropertyValueChangeConsumerTest {

    private static final Integer expectedIntegerValue = Integer.valueOf(123);
    private static final Float expectedFloatValue = Float.valueOf(123.123f);
    private static final Double expectedDoubleValue = Double.valueOf(567879.12344d);
    private static final Boolean expectedBooleanValue = Boolean.TRUE;
    private static final Byte expectedByteValue = Byte.valueOf("12");

    /**
     * Test execute with indexed property.
     */
    @Test
    void testExecuteWithIndexedProperty() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("intIndexed[0]", expectedIntegerValue).accept(testBean);
        assertSame(expectedIntegerValue.intValue(), testBean.getIntIndexed(0));
    }

    /**
     * Test execute with an invalid property name.
     */
    @Test
    void testExecuteWithInvalidPropertyName() {
        assertThrows(IllegalArgumentException.class, () -> new BeanPropertyValueChangeConsumer<>("bogusProperty", "foo").accept(new TestBean()));
    }

    /**
     * Test execute with mapped property.
     */
    @Test
    void testExecuteWithMappedProperty() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("mappedProperty(fred)", "barney").accept(testBean);
        assertEquals("barney", testBean.getMappedProperty("fred"));
    }

    /**
     * Test execute with a nested property.
     */
    @Test
    void testExecuteWithNestedProperty() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("nested.stringProperty", "bar").accept(testBean);
        assertEquals("bar", testBean.getNested().getStringProperty());
    }

    /**
     * Test execute with a nested property and null in the property path.
     */
    @Test
    void testExecuteWithNullInPropertyPath() {
        assertThrows(IllegalArgumentException.class, () -> new BeanPropertyValueChangeConsumer<>("anotherNested.stringProperty", "foo").accept(new TestBean()));
    }

    /**
     * Test execute with a nested property and null in the property path and ignoreNull = true.
     */
    @Test
    void testExecuteWithNullInPropertyPathAngIgnoreTrue() {
        final TestBean testBean = new TestBean();

        // create a consumer that will attempt to set a property on the null bean in the path
        final BeanPropertyValueChangeConsumer<TestBean, Object> consumer = new BeanPropertyValueChangeConsumer<>("anotherNested.stringProperty",
                "Should ignore exception", true);
        consumer.accept(testBean);
    }

    /**
     * Test execute with read only property.
     */
    @Test
    void testExecuteWithReadOnlyProperty() {
        assertThrows(IllegalArgumentException.class, () -> new BeanPropertyValueChangeConsumer<>("readOnlyProperty", "foo").accept(new TestBean()));
    }

    /**
     * Test execute with simple boolean property and Boolean value.
     */
    @Test
    void testExecuteWithSimpleBooleanPropertyAndBooleanValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("booleanProperty", expectedBooleanValue).accept(testBean);
        assertEquals(expectedBooleanValue.booleanValue(), testBean.getBooleanProperty());
    }

    /**
     * Test execute with simple boolean property and String value.
     */
    @Test
    void testExecuteWithSimpleBooleanPropertyAndStringValue() {
        assertThrows(IllegalArgumentException.class, () -> new BeanPropertyValueChangeConsumer<>("booleanProperty", "true").accept(new TestBean()));
    }

    /**
     * Test execute with simple byte property and Byte value.
     */
    @Test
    void testExecuteWithSimpleBytePropertyAndByteValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("byteProperty", expectedByteValue).accept(testBean);
        assertEquals(expectedByteValue.byteValue(), testBean.getByteProperty());
    }

    /**
     * Test execute with simple boolean property and String value.
     */
    @Test
    void testExecuteWithSimpleBytePropertyAndStringValue() {
        assertThrows(IllegalArgumentException.class, () -> new BeanPropertyValueChangeConsumer<>("byteProperty", "foo").accept(new TestBean()));
    }

    /**
     * Test execute with simple double property and Double value.
     */
    @Test
    void testExecuteWithSimpleDoublePropertyAndDoubleValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("doubleProperty", expectedDoubleValue).accept(testBean);
        assertEquals(expectedDoubleValue.doubleValue(), testBean.getDoubleProperty());
    }

    /**
     * Test execute with simple double property and Float value.
     */
    @Test
    void testExecuteWithSimpleDoublePropertyAndFloatValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("doubleProperty", expectedFloatValue).accept(testBean);
        assertEquals(expectedFloatValue.doubleValue(), testBean.getDoubleProperty());
    }

    /**
     * Test execute with simple double property and Integer value.
     */
    @Test
    void testExecuteWithSimpleDoublePropertyAndIntegerValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("doubleProperty", expectedIntegerValue).accept(testBean);
        assertEquals(expectedIntegerValue.doubleValue(), testBean.getDoubleProperty());
    }

    /**
     * Test execute with simple double property and String value.
     */
    @Test
    void testExecuteWithSimpleDoublePropertyAndStringValue() {
        assertThrows(IllegalArgumentException.class, () -> new BeanPropertyValueChangeConsumer<>("doubleProperty", "123").accept(new TestBean()));
    }

    /**
     * Test execute with simple float property and Double value.
     */
    @Test
    void testExecuteWithSimpleFloatPropertyAndDoubleValue() {
        assertThrows(IllegalArgumentException.class, () -> new BeanPropertyValueChangeConsumer<>("floatProperty", expectedDoubleValue).accept(new TestBean()));
    }

    /**
     * Test execute with simple float property and Float value.
     */
    @Test
    void testExecuteWithSimpleFloatPropertyAndFloatValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("floatProperty", expectedFloatValue).accept(testBean);
        assertEquals(expectedFloatValue.floatValue(), testBean.getFloatProperty());
    }

    /**
     * Test execute with simple float property and Integer value.
     */
    @Test
    void testExecuteWithSimpleFloatPropertyAndIntegerValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("floatProperty", expectedIntegerValue).accept(testBean);
        assertEquals(expectedIntegerValue.floatValue(), testBean.getFloatProperty());
    }

    /**
     * Test execute with simple float property and String value.
     */
    @Test
    void testExecuteWithSimpleFloatPropertyAndStringValue() {
        assertThrows(IllegalArgumentException.class, () -> new BeanPropertyValueChangeConsumer<>("floatProperty", "123").accept(new TestBean()));
    }

    /**
     * Test execute with simple int property and Double value.
     */
    @Test
    void testExecuteWithSimpleIntPropertyAndDoubleValue() {
        assertThrows(IllegalArgumentException.class, () -> new BeanPropertyValueChangeConsumer<>("intProperty", expectedDoubleValue).accept(new TestBean()));
    }

    /**
     * Test execute with simple int property and Float value.
     */
    @Test
    void testExecuteWithSimpleIntPropertyAndFloatValue() {
        assertThrows(IllegalArgumentException.class, () -> new BeanPropertyValueChangeConsumer<>("intProperty", expectedFloatValue).accept(new TestBean()));
    }

    /**
     * Test execute with simple int property and Integer value.
     */
    @Test
    void testExecuteWithSimpleIntPropertyAndIntegerValue() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("intProperty", expectedIntegerValue).accept(testBean);
        assertEquals(expectedIntegerValue.intValue(), testBean.getIntProperty());
    }

    /**
     * Test execute with simple int property and String value.
     */
    @Test
    void testExecuteWithSimpleIntPropertyAndStringValue() {
        assertThrows(IllegalArgumentException.class, () -> new BeanPropertyValueChangeConsumer<>("intProperty", "123").accept(new TestBean()));
    }

    /**
     * Test execute with simple primitive property and null value.
     */
    @Test
    void testExecuteWithSimplePrimitivePropertyAndNullValue() {
        assertThrows(IllegalArgumentException.class, () -> new BeanPropertyValueChangeConsumer<>("intProperty", null).accept(new TestBean()));
    }

    /**
     * Test execute with a simple String property.
     */
    @Test
    void testExecuteWithSimpleStringProperty() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("stringProperty", "barney").accept(testBean);
        assertEquals("barney", testBean.getStringProperty());
    }

    /**
     * Test execute with write only property.
     */
    @Test
    void testExecuteWithWriteOnlyProperty() {
        final TestBean testBean = new TestBean();
        new BeanPropertyValueChangeConsumer<>("writeOnlyProperty", "foo").accept(testBean);
        assertEquals("foo", testBean.getWriteOnlyPropertyValue());
    }
}
