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

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.beanutils2.BeanUtilsBean;
import org.apache.commons.beanutils2.converters.ArrayConverter;
import org.apache.commons.beanutils2.converters.StringConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-359">https://issues.apache.org/jira/browse/BEANUTILS-359</a>
 */
public class Jira359TestCase {

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

    /**
     * Sets up.
     *
     * @throws Exception
     */
    @BeforeEach
    protected void setUp() throws Exception {
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

    /**
     * Tear Down.
     *
     * @throws Exception
     */
    @AfterEach
    protected void tearDown() throws Exception {
    }

    /**
     * Test {@link BeanUtils} setProperty() String to array with colon value
     */
    @Test
    public void testBeanUtilsSetProperty_CustomConvertStringToArray_WithColonValue() throws Exception {
        final ArrayConverter converter = new ArrayConverter(String[].class, new StringConverter());
        converter.setAllowedChars(new char[] { '.', '-', ':' });

        final BeanUtilsBean utils = new BeanUtilsBean();
        utils.getConvertUtils().register(converter, String[].class);

        final SimplePojoData simplePojo = new SimplePojoData();
        utils.setProperty(simplePojo, "jcrMixinTypes", "mix:rereferencible,mix:simple");
        showArray("Custom WithColonValue", simplePojo.getJcrMixinTypes());
        assertEquals(2, simplePojo.getJcrMixinTypes().length, "array size");
        assertEquals("mix:rereferencible", simplePojo.getJcrMixinTypes()[0]);
        assertEquals("mix:simple", simplePojo.getJcrMixinTypes()[1]);
    }

    /**
     * Test {@link BeanUtils} setProperty() String to array with colon value
     */
    @Test
    public void testBeanUtilsSetProperty_DefaultConvertStringToArray_WithColonValue() throws Exception {
        final SimplePojoData simplePojo = new SimplePojoData();
        BeanUtils.setProperty(simplePojo, "jcrMixinTypes", "mix:rereferencible,mix:simple");
        showArray("Default WithColonValue", simplePojo.getJcrMixinTypes());
        assertEquals(4, simplePojo.getJcrMixinTypes().length, "array size");
        assertEquals("mix", simplePojo.getJcrMixinTypes()[0]);
        assertEquals("rereferencible", simplePojo.getJcrMixinTypes()[1]);
        assertEquals("mix", simplePojo.getJcrMixinTypes()[2]);
        assertEquals("simple", simplePojo.getJcrMixinTypes()[3]);
    }

    /**
     * Test {@link BeanUtils} setProperty() String to array without colon value
     */
    @Test
    public void testBeanUtilsSetProperty_DefaultConvertStringToArray_WithoutColonValue() throws Exception {
        final SimplePojoData simplePojo = new SimplePojoData();
        BeanUtils.setProperty(simplePojo, "jcrMixinTypes", "mixrereferencible,mixsimple");
        showArray("Default WithoutColonValue", simplePojo.getJcrMixinTypes());
        assertEquals(2, simplePojo.getJcrMixinTypes().length, "array size");
        assertEquals("mixrereferencible", simplePojo.getJcrMixinTypes()[0]);
        assertEquals("mixsimple", simplePojo.getJcrMixinTypes()[1]);
    }

    /**
     * Test {@link BeanUtils} setProperty() String to array without colon value and no comma
     */
    @Test
    public void testBeanUtilsSetProperty_DefaultConvertStringToArray_WithoutColonValueAndNocoma() throws Exception {
        final SimplePojoData simplePojo = new SimplePojoData();
        BeanUtils.setProperty(simplePojo, "jcrMixinTypes", "mixrereferencible");
        showArray("Default WithoutColonAndNocoma", simplePojo.getJcrMixinTypes());
        assertEquals(1, simplePojo.getJcrMixinTypes().length, "array size");
        assertEquals("mixrereferencible", simplePojo.getJcrMixinTypes()[0]);
    }
}
