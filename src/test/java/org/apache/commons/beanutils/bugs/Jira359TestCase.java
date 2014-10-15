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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.converters.ArrayConverter;
import org.apache.commons.beanutils.converters.StringConverter;

/**
 * @version $Id$
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-359">https://issues.apache.org/jira/browse/BEANUTILS-359</a>
 */
public class Jira359TestCase extends TestCase {

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira359TestCase(final String name) {
        super(name);
    }

    /**
     * Run the Test.
     *
     * @param args Arguments
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Create a test suite for this test.
     *
     * @return a test suite
     */
    public static Test suite() {
        return (new TestSuite(Jira359TestCase.class));
    }

    /**
     * Set up.
     *
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tear Down.
     *
     * @throws java.lang.Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test {@link BeanUtils} setProperty() String to array with colon value
     */
    public void testBeanUtilsSetProperty_CustomConvertStringToArray_WithColonValue() throws Exception{
        final ArrayConverter converter = new ArrayConverter(String[].class, new StringConverter());
        converter.setAllowedChars(new char[] {'.', '-', ':'});

        final BeanUtilsBean utils = new BeanUtilsBean();
        utils.getConvertUtils().register(converter, String[].class);

        final SimplePojoData simplePojo = new SimplePojoData();
        utils.setProperty(simplePojo, "jcrMixinTypes", "mix:rereferencible,mix:simple");
        showArray("Custom WithColonValue", simplePojo.getJcrMixinTypes());
        assertEquals("array size", 2, simplePojo.getJcrMixinTypes().length);
        assertEquals("mix:rereferencible", simplePojo.getJcrMixinTypes()[0]);
        assertEquals("mix:simple", simplePojo.getJcrMixinTypes()[1]);
    }

    /**
     * Test {@link BeanUtils} setProperty() String to array with colon value
     */
    public void testBeanUtilsSetProperty_DefaultConvertStringToArray_WithColonValue() throws Exception{
        final SimplePojoData simplePojo = new SimplePojoData();
        BeanUtils.setProperty(simplePojo, "jcrMixinTypes", "mix:rereferencible,mix:simple");
        showArray("Default WithColonValue", simplePojo.getJcrMixinTypes());
        assertEquals("array size", 4, simplePojo.getJcrMixinTypes().length);
        assertEquals("mix", simplePojo.getJcrMixinTypes()[0]);
        assertEquals("rereferencible", simplePojo.getJcrMixinTypes()[1]);
        assertEquals("mix", simplePojo.getJcrMixinTypes()[2]);
        assertEquals("simple", simplePojo.getJcrMixinTypes()[3]);
    }

    /**
     * Test {@link BeanUtils} setProperty() String to array without colon value
     */
    public void testBeanUtilsSetProperty_DefaultConvertStringToArray_WithoutColonValue() throws Exception{
        final SimplePojoData simplePojo = new SimplePojoData();
        BeanUtils.setProperty(simplePojo, "jcrMixinTypes", "mixrereferencible,mixsimple");
        showArray("Default WithoutColonValue", simplePojo.getJcrMixinTypes());
        assertEquals("array size", 2, simplePojo.getJcrMixinTypes().length);
        assertEquals("mixrereferencible", simplePojo.getJcrMixinTypes()[0]);
        assertEquals("mixsimple", simplePojo.getJcrMixinTypes()[1]);
    }

    /**
     * Test {@link BeanUtils} setProperty() String to array without colon value and no comma
     */
    public void testBeanUtilsSetProperty_DefaultConvertStringToArray_WithoutColonValueAndNocoma() throws Exception{
        final SimplePojoData simplePojo = new SimplePojoData();
        BeanUtils.setProperty(simplePojo, "jcrMixinTypes", "mixrereferencible");
        showArray("Default WithoutColonAndNocoma", simplePojo.getJcrMixinTypes());
        assertEquals("array size", 1, simplePojo.getJcrMixinTypes().length);
        assertEquals("mixrereferencible", simplePojo.getJcrMixinTypes()[0]);
    }

    /**
     * Show array contents.
     */
    private void showArray(final String text, final String[] array) {
        if (array == null) {
            System.out.println(text + " array is null");
        } else {
            System.out.println(text + " array length=" + array.length);
            for (int i = 0; i < array.length; i++) {
                System.out.println(text + " array[" + i + "]=" + array[i]);
            }
        }
    }

    public static class SimplePojoData {
        private String[] jcrMixinTypes = new String[1];
        public SimplePojoData() {
        }
        public String[] getJcrMixinTypes() {
            return this.jcrMixinTypes;
        }
        public void setJcrMixinTypes(final String[] mixinTypes) {
            this.jcrMixinTypes = mixinTypes;
        }
    }
}
