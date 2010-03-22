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
 
package org.apache.commons.beanutils;

import junit.framework.TestCase;


/**
 * Test cases for <code>BeanPropertyValueEqualsPredicateTest</code>.
 *
 * @author Norm Deane
 */
public class BeanPropertyValueEqualsPredicateTestCase extends TestCase {
   
    private static final Integer expectedIntegerValue = new Integer(123);
    private static final Float expectedFloatValue = new Float(123.123f);
    private static final Double expectedDoubleValue = new Double(567879.12344d);
    private static final Boolean expectedBooleanValue = Boolean.TRUE;
    private static final Byte expectedByteValue = new Byte("12");

    /**
     * Constructor for BeanPropertyValueEqualsPredicateTest.
     *
     * @param name Name of this test case.
     */
    public BeanPropertyValueEqualsPredicateTestCase(String name) {
        super(name);
    }

    /**
     * Test evaluate with simple String property.
     */
    public void testEvaluateWithSimpleStringProperty() {
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("stringProperty","foo");
        assertTrue(predicate.evaluate(new TestBean("foo")));
        assertTrue(!predicate.evaluate(new TestBean("bar")));
    }

    /**
     * Test evaluate with simple String property and null values.
     */
    public void testEvaluateWithSimpleStringPropertyWithNullValues() {
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("stringProperty",null);
        assertTrue(predicate.evaluate(new TestBean((String) null)));
        assertTrue(!predicate.evaluate(new TestBean("bar")));
    }

    /**
     * Test evaluate with nested property.
     */
    public void testEvaluateWithNestedProperty() {
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("anotherNested.stringProperty","match");
        TestBean testBean = new TestBean();
        TestBean nestedBean = new TestBean("match");
        testBean.setAnotherNested(nestedBean);
        assertTrue(predicate.evaluate(testBean));
        testBean.setAnotherNested(new TestBean("no-match"));
        assertTrue(!predicate.evaluate(testBean));
    }

    /**
     * Test evaluate with null in property path and ignore=false.
     */
    public void testEvaluateWithNullInPath() {
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("anotherNested.stringProperty","foo");
        try {
            // try to evaluate the predicate
            predicate.evaluate(new TestBean());
            fail("Should have throw IllegalArgumentException");
        } catch (IllegalArgumentException e) { 
            /* ignore this is what should happen */
        }
    }

    /**
     * Test evaluate with null in property path and ignore=true.
     */
    public void testEvaluateWithNullInPathAndIgnoreTrue() {
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("anotherNested.stringProperty","foo", true);
        try {
            assertTrue(!predicate.evaluate(new TestBean()));
        } catch (IllegalArgumentException e) {
            fail("Should not have throw IllegalArgumentException");
        }
    }

    /**
     * Test evaluate with int property.
     */
    public void testEvaluateWithIntProperty() {
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("intProperty",expectedIntegerValue);
        assertTrue(predicate.evaluate(new TestBean(expectedIntegerValue.intValue())));
        assertTrue(!predicate.evaluate(new TestBean(expectedIntegerValue.intValue() - 1)));
    }

    /**
     * Test evaluate with float property.
     */
    public void testEvaluateWithFloatProperty() {
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("floatProperty",expectedFloatValue);
        assertTrue(predicate.evaluate(new TestBean(expectedFloatValue.floatValue())));
        assertTrue(!predicate.evaluate(new TestBean(expectedFloatValue.floatValue() - 1)));
    }

    /**
     * Test evaluate with double property.
     */
    public void testEvaluateWithDoubleProperty() {
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("doubleProperty",expectedDoubleValue);
        assertTrue(predicate.evaluate(new TestBean(expectedDoubleValue.doubleValue())));
        assertTrue(!predicate.evaluate(new TestBean(expectedDoubleValue.doubleValue() - 1)));
    }

    /**
     * Test evaluate with boolean property.
     */
    public void testEvaluateWithBooleanProperty() {
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("booleanProperty",expectedBooleanValue);
        assertTrue(predicate.evaluate(new TestBean(expectedBooleanValue.booleanValue())));
        assertTrue(!predicate.evaluate(new TestBean(!expectedBooleanValue.booleanValue())));
    }

    /**
     * Test evaluate with byte property.
     */
    public void testEvaluateWithByteProperty() {
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("byteProperty",expectedByteValue);
        TestBean testBean = new TestBean();
        testBean.setByteProperty(expectedByteValue.byteValue());
        assertTrue(predicate.evaluate(testBean));
        testBean.setByteProperty((byte) (expectedByteValue.byteValue() - 1));
        assertTrue(!predicate.evaluate(testBean));
    }

    /**
     * Test evaluate with mapped property.
     */
    public void testEvaluateWithMappedProperty() {
        // try a key that is in the map
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("mappedProperty(test-key)","match");
        TestBean testBean = new TestBean();
        testBean.setMappedProperty("test-key", "match");
        assertTrue(predicate.evaluate(testBean));
        testBean.setMappedProperty("test-key", "no-match");
        assertTrue(!predicate.evaluate(testBean));

        // try a key that isn't in the map
        predicate = new BeanPropertyValueEqualsPredicate("mappedProperty(invalid-key)", "match");
        assertTrue(!predicate.evaluate(testBean));
    }

    /**
     * Test evaluate with indexed property.
     */
    public void testEvaluateWithIndexedProperty() {
        // try a valid index
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("intIndexed[0]",expectedIntegerValue);
        TestBean testBean = new TestBean();
        testBean.setIntIndexed(0, expectedIntegerValue.intValue());
        assertTrue(predicate.evaluate(testBean));
        testBean.setIntIndexed(0, expectedIntegerValue.intValue() - 1);
        assertTrue(!predicate.evaluate(testBean));

        // try an invalid index
        predicate = new BeanPropertyValueEqualsPredicate("intIndexed[999]", "exception-ahead");

        try {
            assertTrue(!predicate.evaluate(testBean));
        } catch (ArrayIndexOutOfBoundsException e) { 
            /* this is what should happen */
        }
    }

    /**
     * Test evaluate with primitive property and null value.
     */
    public void testEvaluateWithPrimitiveAndNull() {
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("intProperty",null);
        assertTrue(!predicate.evaluate(new TestBean(0)));

        predicate = new BeanPropertyValueEqualsPredicate("booleanProperty", null);
        assertTrue(!predicate.evaluate(new TestBean(true)));

        predicate = new BeanPropertyValueEqualsPredicate("floatProperty", null);
        assertTrue(!predicate.evaluate(new TestBean(expectedFloatValue.floatValue())));
    }

    /**
     * Test evaluate with nested mapped property.
     */
    public void testEvaluateWithNestedMappedProperty() {
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("anotherNested.mappedProperty(test-key)","match");
        TestBean testBean = new TestBean();
        TestBean nestedBean = new TestBean();
        nestedBean.setMappedProperty("test-key", "match");
        testBean.setAnotherNested(nestedBean);
        assertTrue(predicate.evaluate(testBean));
        nestedBean.setMappedProperty("test-key", "no-match");
        assertTrue(!predicate.evaluate(testBean));
    }

    /**
     * Test evaluate with write only property.
     */
    public void testEvaluateWithWriteOnlyProperty() {
        try {
            new BeanPropertyValueEqualsPredicate("writeOnlyProperty", null).evaluate(new TestBean());
        } catch (IllegalArgumentException e) { 
            /* This is what should happen */
        }
    }

    /**
     * Test evaluate with read only property.
     */
    public void testEvaluateWithReadOnlyProperty() {
        TestBean testBean = new TestBean();
        BeanPropertyValueEqualsPredicate predicate = 
            new BeanPropertyValueEqualsPredicate("readOnlyProperty",testBean.getReadOnlyProperty());
        assertTrue(predicate.evaluate(new TestBean()));
    }

    /**
     * Test evaluate with an invalid property name.
     */
    public void testEvaluateWithInvalidPropertyName() {
        try {
            new BeanPropertyValueEqualsPredicate("bogusProperty", null).evaluate(new TestBean());
        } catch (IllegalArgumentException e) { 
            /* This is what should happen */
        }
    }
}