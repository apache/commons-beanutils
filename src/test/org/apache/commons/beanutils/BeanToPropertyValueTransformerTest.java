/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/Attic/BeanToPropertyValueTransformerTest.java,v 1.3 2003/10/09 20:40:07 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/09 20:40:07 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 
 
package org.apache.commons.beanutils;

import junit.framework.TestCase;


/**
 * Test cases for <code>BeanToPropertyValueTransformer</code>.
 *
 * @author Norm Deane
 */
public class BeanToPropertyValueTransformerTest extends TestCase {
   
    private static final Integer expectedIntegerValue = new Integer(123);
    private static final Long expectedLongValue = new Long(123);
    private static final Float expectedFloatValue = new Float(123.123f);
    private static final Double expectedDoubleValue = new Double(567879.12344d);
    private static final Boolean expectedBooleanValue = Boolean.TRUE;
    private static final Byte expectedByteValue = new Byte("12");

    /**
     * Constructor for BeanToPropertyValueTransformerTest.
     *
     * @param name Name of this test case.
     */
    public BeanToPropertyValueTransformerTest(String name) {
        super(name);
    }

    /**
     * Test transform with simple String property.
     */
    public void testTransformWithSimpleStringProperty() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("stringProperty");
        TestBean testBean = new TestBean("foo");
        assertEquals("foo", transformer.transform(testBean));
    }

    /**
     * Test transform with simple String property and null value.
     *
     */
    public void testTransformWithSimpleStringPropertyAndNullValue() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("stringProperty");
        TestBean testBean = new TestBean((String) null);
        assertNull(transformer.transform(testBean));
    }

    /**
     * Test transform with simple int property.
     */
    public void testTransformWithSimpleIntProperty() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("intProperty");
        TestBean testBean = new TestBean(expectedIntegerValue.intValue());
        assertEquals(expectedIntegerValue, transformer.transform(testBean));
    }

    /**
     * Test transform with simple long property.
     */
    public void testTransformWithSimpleLongProperty() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("longProperty");
        TestBean testBean = new TestBean();
        testBean.setLongProperty(expectedLongValue.longValue());
        assertEquals(expectedLongValue, transformer.transform(testBean));
    }

    /**
     * Test transform with simple float property.
     */
    public void testTransformWithSimpleFloatProperty() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("floatProperty");
        TestBean testBean = new TestBean(expectedFloatValue.floatValue());
        assertEquals(expectedFloatValue, transformer.transform(testBean));
    }

    /**
     * Test transform with simple double property.
     */
    public void testTransformWithSimpleDoubleProperty() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("doubleProperty");
        TestBean testBean = new TestBean(expectedDoubleValue.doubleValue());
        assertEquals(expectedDoubleValue, transformer.transform(testBean));
    }

    /**
     * Test transform with simple byte property.
     */
    public void testTransformWithSimpleByteProperty() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("byteProperty");
        TestBean testBean = new TestBean();
        testBean.setByteProperty(expectedByteValue.byteValue());
        assertEquals(expectedByteValue, transformer.transform(testBean));
    }

    /**
     * Test transform with simple boolean property.
     */
    public void testTransformWithSimpleBooleanProperty() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("booleanProperty");
        TestBean testBean = new TestBean(expectedBooleanValue.booleanValue());
        assertEquals(expectedBooleanValue, transformer.transform(testBean));
    }

    /**
     * Test transform with write only property.
     */
    public void testTransformWithWriteOnlyProperty() {
        try {
            new BeanToPropertyValueTransformer("writeOnlyProperty").transform(new TestBean());
        } catch (IllegalArgumentException e) { 
            /* This is what should happen */
        }
    }

    /**
     * Test transform with read only property.
     */
    public void testTransformWithReadOnlyProperty() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("readOnlyProperty");
        TestBean testBean = new TestBean();
        assertEquals(testBean.getReadOnlyProperty(), transformer.transform(testBean));
    }

    /**
     * Test transform with invalid property.
     */
    public void testTransformWithInvalidProperty() {
        try {
            new BeanToPropertyValueTransformer("bogusProperty").transform(new TestBean());
        } catch (IllegalArgumentException e) { 
            /* This is what should happen */
        }
    }

    /**
     * Test transform with nested property.
     */
    public void testTransformWithNestedProperty() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("anotherNested.stringProperty");
        TestBean testBean = new TestBean();
        TestBean nestedBean = new TestBean("foo");
        testBean.setAnotherNested(nestedBean);
        assertEquals("foo", transformer.transform(testBean));
    }

    /**
     * Test transform with mapped property.
     */
    public void testTransformWithMappedProperty() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("mappedProperty(test-key)");
        TestBean testBean = new TestBean();

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
        TestBean testBean = new TestBean();
        testBean.setIntIndexed(0, expectedIntegerValue.intValue());
        assertEquals(expectedIntegerValue, transformer.transform(testBean));

        // test index out of range
        transformer = new BeanToPropertyValueTransformer("intIndexed[9999]");

        try {
            transformer.transform(testBean);
            fail("Should have thrown an ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) { 
            /* this is what should happen */
        }
    }

    /**
     * Test transform with nested indexed property.
     */
    public void testTransformWithNestedIndexedProperty() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("anotherNested.intIndexed[0]");
        TestBean testBean = new TestBean();
        TestBean nestedBean = new TestBean();
        nestedBean.setIntIndexed(0, expectedIntegerValue.intValue());
        testBean.setAnotherNested(nestedBean);
        assertEquals(expectedIntegerValue, transformer.transform(testBean));
    }

    /**
     * Test transform with null in property path.
     */
    public void testTransformWithNullInPath() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("anotherNested.stringProperty");

        try {
            transformer.transform(new TestBean());
            fail("Should have throw IllegalArgumentException");
        } catch (IllegalArgumentException e) { 
            /* ignore this is what should happen */
        }
    }

    /**
     * Test transform with null in property path and ignore = true.
     */
    public void testTransformWithNullInPathAndIgnoreTrue() {
        BeanToPropertyValueTransformer transformer = 
            new BeanToPropertyValueTransformer("anotherNested.stringProperty",true);
        assertEquals(null, transformer.transform(new TestBean()));
    }
}
