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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test cases for {@code BeanPropertyValueEqualsPredicateTest}.
 */
public class BeanPropertyValueEqualsPredicateTestCase {

    private static final Integer expectedIntegerValue = Integer.valueOf(123);
    private static final Float expectedFloatValue = Float.valueOf(123.123f);
    private static final Double expectedDoubleValue = Double.valueOf(567879.12344d);
    private static final Boolean expectedBooleanValue = Boolean.TRUE;
    private static final Byte expectedByteValue = Byte.valueOf("12");

    /**
     * Test evaluate with boolean property.
     */
    @Test
    public void testEvaluateWithBooleanProperty() {
        final BeanPropertyValueEqualsPredicate<TestBean, Boolean> predicate = new BeanPropertyValueEqualsPredicate<>("booleanProperty", expectedBooleanValue);
        assertTrue(predicate.test(new TestBean(expectedBooleanValue.booleanValue())));
        assertFalse(predicate.test(new TestBean(!expectedBooleanValue.booleanValue())));
    }

    /**
     * Test evaluate with byte property.
     */
    @Test
    public void testEvaluateWithByteProperty() {
        final BeanPropertyValueEqualsPredicate<TestBean, Byte> predicate = new BeanPropertyValueEqualsPredicate<>("byteProperty", expectedByteValue);
        final TestBean testBean = new TestBean();
        testBean.setByteProperty(expectedByteValue.byteValue());
        assertTrue(predicate.test(testBean));
        testBean.setByteProperty((byte) (expectedByteValue.byteValue() - 1));
        assertFalse(predicate.test(testBean));
    }

    /**
     * Test evaluate with double property.
     */
    @Test
    public void testEvaluateWithDoubleProperty() {
        final BeanPropertyValueEqualsPredicate<TestBean, Double> predicate = new BeanPropertyValueEqualsPredicate<>("doubleProperty", expectedDoubleValue);
        assertTrue(predicate.test(new TestBean(expectedDoubleValue.doubleValue())));
        assertFalse(predicate.test(new TestBean(expectedDoubleValue.doubleValue() - 1)));
    }

    /**
     * Test evaluate with float property.
     */
    @Test
    public void testEvaluateWithFloatProperty() {
        final BeanPropertyValueEqualsPredicate<TestBean, Float> predicate = new BeanPropertyValueEqualsPredicate<>("floatProperty", expectedFloatValue);
        assertTrue(predicate.test(new TestBean(expectedFloatValue.floatValue())));
        assertFalse(predicate.test(new TestBean(expectedFloatValue.floatValue() - 1)));
    }

    /**
     * Test evaluate with indexed property.
     */
    @Test
    public void testEvaluateWithIndexedProperty() {
        // try a valid index
        BeanPropertyValueEqualsPredicate<TestBean, Object> predicate = new BeanPropertyValueEqualsPredicate<>("intIndexed[0]", expectedIntegerValue);
        final TestBean testBean = new TestBean();
        testBean.setIntIndexed(0, expectedIntegerValue.intValue());
        assertTrue(predicate.test(testBean));
        testBean.setIntIndexed(0, expectedIntegerValue.intValue() - 1);
        assertFalse(predicate.test(testBean));

        // try an invalid index
        predicate = new BeanPropertyValueEqualsPredicate<>("intIndexed[999]", "exception-ahead");

        try {
            assertFalse(predicate.test(testBean));
        } catch (final ArrayIndexOutOfBoundsException e) {
            /* this is what should happen */
        }
    }

    /**
     * Test evaluate with int property.
     */
    @Test
    public void testEvaluateWithIntProperty() {
        final BeanPropertyValueEqualsPredicate<TestBean, Integer> predicate = new BeanPropertyValueEqualsPredicate<>("intProperty", expectedIntegerValue);
        assertTrue(predicate.test(new TestBean(expectedIntegerValue.intValue())));
        assertFalse(predicate.test(new TestBean(expectedIntegerValue.intValue() - 1)));
    }

    /**
     * Test evaluate with an invalid property name.
     */
    @Test
    public void testEvaluateWithInvalidPropertyName() {
        try {
            new BeanPropertyValueEqualsPredicate<TestBean, Object>("bogusProperty", null).test(new TestBean());
        } catch (final IllegalArgumentException e) {
            /* This is what should happen */
        }
    }

    /**
     * Test evaluate with mapped property.
     */
    @Test
    public void testEvaluateWithMappedProperty() {
        // try a key that is in the map
        BeanPropertyValueEqualsPredicate<TestBean, String> predicate = new BeanPropertyValueEqualsPredicate<>("mappedProperty(test-key)", "match");
        final TestBean testBean = new TestBean();
        testBean.setMappedProperty("test-key", "match");
        assertTrue(predicate.test(testBean));
        testBean.setMappedProperty("test-key", "no-match");
        assertFalse(predicate.test(testBean));

        // try a key that isn't in the map
        predicate = new BeanPropertyValueEqualsPredicate<>("mappedProperty(invalid-key)", "match");
        assertFalse(predicate.test(testBean));
    }

    /**
     * Test evaluate with nested mapped property.
     */
    @Test
    public void testEvaluateWithNestedMappedProperty() {
        final BeanPropertyValueEqualsPredicate<TestBean, String> predicate = new BeanPropertyValueEqualsPredicate<>("anotherNested.mappedProperty(test-key)",
                "match");
        final TestBean testBean = new TestBean();
        final TestBean nestedBean = new TestBean();
        nestedBean.setMappedProperty("test-key", "match");
        testBean.setAnotherNested(nestedBean);
        assertTrue(predicate.test(testBean));
        nestedBean.setMappedProperty("test-key", "no-match");
        assertFalse(predicate.test(testBean));
    }

    /**
     * Test evaluate with nested property.
     */
    @Test
    public void testEvaluateWithNestedProperty() {
        final BeanPropertyValueEqualsPredicate<TestBean, String> predicate = new BeanPropertyValueEqualsPredicate<>("anotherNested.stringProperty", "match");
        final TestBean testBean = new TestBean();
        final TestBean nestedBean = new TestBean("match");
        testBean.setAnotherNested(nestedBean);
        assertTrue(predicate.test(testBean));
        testBean.setAnotherNested(new TestBean("no-match"));
        assertFalse(predicate.test(testBean));
    }

    /**
     * Test evaluate with null in property path and ignore=false.
     */
    @Test
    public void testEvaluateWithNullInPath() {
        final BeanPropertyValueEqualsPredicate<TestBean, String> predicate = new BeanPropertyValueEqualsPredicate<>("anotherNested.stringProperty", "foo");
        assertThrows(IllegalArgumentException.class, () -> predicate.test(new TestBean()));
    }

    /**
     * Test evaluate with null in property path and ignore=true.
     */
    @Test
    public void testEvaluateWithNullInPathAndIgnoreTrue() {
        final BeanPropertyValueEqualsPredicate<TestBean, String> predicate = new BeanPropertyValueEqualsPredicate<>("anotherNested.stringProperty", "foo",
                true);
        assertFalse(predicate.test(new TestBean()));
    }

    /**
     * Test evaluate with primitive property and null value.
     */
    @Test
    public void testEvaluateWithPrimitiveAndNull() {
        BeanPropertyValueEqualsPredicate<TestBean, Object> predicate = new BeanPropertyValueEqualsPredicate<>("intProperty", null);
        assertFalse(predicate.test(new TestBean(0)));

        predicate = new BeanPropertyValueEqualsPredicate<>("booleanProperty", null);
        assertFalse(predicate.test(new TestBean(true)));

        predicate = new BeanPropertyValueEqualsPredicate<>("floatProperty", null);
        assertFalse(predicate.test(new TestBean(expectedFloatValue.floatValue())));
    }

    /**
     * Test evaluate with read only property.
     */
    @Test
    public void testEvaluateWithReadOnlyProperty() {
        final TestBean testBean = new TestBean();
        final BeanPropertyValueEqualsPredicate<TestBean, String> predicate = new BeanPropertyValueEqualsPredicate<>("readOnlyProperty",
                testBean.getReadOnlyProperty());
        assertTrue(predicate.test(new TestBean()));
    }

    /**
     * Test evaluate with simple String property.
     */
    @Test
    public void testEvaluateWithSimpleStringProperty() {
        final BeanPropertyValueEqualsPredicate<TestBean, String> predicate = new BeanPropertyValueEqualsPredicate<>("stringProperty", "foo");
        assertTrue(predicate.test(new TestBean("foo")));
        assertFalse(predicate.test(new TestBean("bar")));
    }

    /**
     * Test evaluate with simple String property and null values.
     */
    @Test
    public void testEvaluateWithSimpleStringPropertyWithNullValues() {
        final BeanPropertyValueEqualsPredicate<TestBean, String> predicate = new BeanPropertyValueEqualsPredicate<>("stringProperty", null);
        assertTrue(predicate.test(new TestBean((String) null)));
        assertFalse(predicate.test(new TestBean("bar")));
    }

    /**
     * Test evaluate with write only property.
     */
    @Test
    public void testEvaluateWithWriteOnlyProperty() {
        try {
            new BeanPropertyValueEqualsPredicate<TestBean, String>("writeOnlyProperty", null).test(new TestBean());
        } catch (final IllegalArgumentException e) {
            /* This is what should happen */
        }
    }
}