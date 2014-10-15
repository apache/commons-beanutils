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
 * Test cases for <code>BeanToPropertyValueTransformer</code>.
 *
 * @version $Id$
 */
public class BeanToPropertyValueTransformerTestCase extends TestCase {

    private static final Integer expectedIntegerValue = new Integer(123);
    private static final Long expectedLongValue = new Long(123);
    private static final Float expectedFloatValue = new Float(123.123f);
    private static final Double expectedDoubleValue = new Double(567879.12344d);
    private static final Boolean expectedBooleanValue = Boolean.TRUE;
    private static final Byte expectedByteValue = new Byte("12");

    /**
     * Constructor for BeanToPropertyValueTransformerTestCase.
     *
     * @param name Name of this test case.
     */
    public BeanToPropertyValueTransformerTestCase(final String name) {
        super(name);
    }

    /**
     * Test transform with simple String property.
     */
    public void testTransformWithSimpleStringProperty() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("stringProperty");
        final TestBean testBean = new TestBean("foo");
        assertEquals("foo", transformer.transform(testBean));
    }

    /**
     * Test transform with simple String property and null value.
     *
     */
    public void testTransformWithSimpleStringPropertyAndNullValue() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("stringProperty");
        final TestBean testBean = new TestBean((String) null);
        assertNull(transformer.transform(testBean));
    }

    /**
     * Test transform with simple int property.
     */
    public void testTransformWithSimpleIntProperty() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("intProperty");
        final TestBean testBean = new TestBean(expectedIntegerValue.intValue());
        assertEquals(expectedIntegerValue, transformer.transform(testBean));
    }

    /**
     * Test transform with simple long property.
     */
    public void testTransformWithSimpleLongProperty() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("longProperty");
        final TestBean testBean = new TestBean();
        testBean.setLongProperty(expectedLongValue.longValue());
        assertEquals(expectedLongValue, transformer.transform(testBean));
    }

    /**
     * Test transform with simple float property.
     */
    public void testTransformWithSimpleFloatProperty() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("floatProperty");
        final TestBean testBean = new TestBean(expectedFloatValue.floatValue());
        assertEquals(expectedFloatValue, transformer.transform(testBean));
    }

    /**
     * Test transform with simple double property.
     */
    public void testTransformWithSimpleDoubleProperty() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("doubleProperty");
        final TestBean testBean = new TestBean(expectedDoubleValue.doubleValue());
        assertEquals(expectedDoubleValue, transformer.transform(testBean));
    }

    /**
     * Test transform with simple byte property.
     */
    public void testTransformWithSimpleByteProperty() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("byteProperty");
        final TestBean testBean = new TestBean();
        testBean.setByteProperty(expectedByteValue.byteValue());
        assertEquals(expectedByteValue, transformer.transform(testBean));
    }

    /**
     * Test transform with simple boolean property.
     */
    public void testTransformWithSimpleBooleanProperty() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("booleanProperty");
        final TestBean testBean = new TestBean(expectedBooleanValue.booleanValue());
        assertEquals(expectedBooleanValue, transformer.transform(testBean));
    }

    /**
     * Test transform with write only property.
     */
    public void testTransformWithWriteOnlyProperty() {
        try {
            new BeanToPropertyValueTransformer("writeOnlyProperty").transform(new TestBean());
        } catch (final IllegalArgumentException e) {
            /* This is what should happen */
        }
    }

    /**
     * Test transform with read only property.
     */
    public void testTransformWithReadOnlyProperty() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("readOnlyProperty");
        final TestBean testBean = new TestBean();
        assertEquals(testBean.getReadOnlyProperty(), transformer.transform(testBean));
    }

    /**
     * Test transform with invalid property.
     */
    public void testTransformWithInvalidProperty() {
        try {
            new BeanToPropertyValueTransformer("bogusProperty").transform(new TestBean());
        } catch (final IllegalArgumentException e) {
            /* This is what should happen */
        }
    }

    /**
     * Test transform with nested property.
     */
    public void testTransformWithNestedProperty() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("anotherNested.stringProperty");
        final TestBean testBean = new TestBean();
        final TestBean nestedBean = new TestBean("foo");
        testBean.setAnotherNested(nestedBean);
        assertEquals("foo", transformer.transform(testBean));
    }

    /**
     * Test transform with mapped property.
     */
    public void testTransformWithMappedProperty() {
        BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("mappedProperty(test-key)");
        final TestBean testBean = new TestBean();

        // try a valid key
        testBean.setMappedProperty("test-key", "test-value");
        assertEquals("test-value", transformer.transform(testBean));

        // now try an invalid key
        transformer = new BeanToPropertyValueTransformer("mappedProperty(bogus-key)");
        assertEquals(null, transformer.transform(testBean));
    }

    /**
     * Test transform with indexed property.
     */
    public void testTransformWithIndexedProperty() {
        BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("intIndexed[0]");
        final TestBean testBean = new TestBean();
        testBean.setIntIndexed(0, expectedIntegerValue.intValue());
        assertEquals(expectedIntegerValue, transformer.transform(testBean));

        // test index out of range
        transformer = new BeanToPropertyValueTransformer("intIndexed[9999]");

        try {
            transformer.transform(testBean);
            fail("Should have thrown an ArrayIndexOutOfBoundsException");
        } catch (final ArrayIndexOutOfBoundsException e) {
            /* this is what should happen */
        }
    }

    /**
     * Test transform with nested indexed property.
     */
    public void testTransformWithNestedIndexedProperty() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("anotherNested.intIndexed[0]");
        final TestBean testBean = new TestBean();
        final TestBean nestedBean = new TestBean();
        nestedBean.setIntIndexed(0, expectedIntegerValue.intValue());
        testBean.setAnotherNested(nestedBean);
        assertEquals(expectedIntegerValue, transformer.transform(testBean));
    }

    /**
     * Test transform with null in property path.
     */
    public void testTransformWithNullInPath() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("anotherNested.stringProperty");

        try {
            transformer.transform(new TestBean());
            fail("Should have throw IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            /* ignore this is what should happen */
        }
    }

    /**
     * Test transform with null in property path and ignore = true.
     */
    public void testTransformWithNullInPathAndIgnoreTrue() {
        final BeanToPropertyValueTransformer transformer =
            new BeanToPropertyValueTransformer("anotherNested.stringProperty",true);
        assertEquals(null, transformer.transform(new TestBean()));
    }
}
