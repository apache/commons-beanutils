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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.beanutils2.expression.Resolver;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link PropertyUtilsBean}.
 */
public class PropertyUtilsBeanTestCase {

    @Test
    public void testGetMappedPropertyDescriptors() throws Exception {
        assertNull(new PropertyUtilsBean().getMappedPropertyDescriptors((Object) null));
        assertNull(new PropertyUtilsBean().getMappedPropertyDescriptors((Class<?>) null));
    }

    @Test
    public void testGetPropertyDescriptor() throws Exception {
        assertThrows(NullPointerException.class, () -> new PropertyUtilsBean().getPropertyDescriptor((Object) null, null));
        assertThrows(NullPointerException.class, () -> new PropertyUtilsBean().getPropertyDescriptor("", null));
    }

    @Test
    public void testGetPropertyEditorClass() throws Exception {
        assertThrows(NullPointerException.class, () -> new PropertyUtilsBean().getPropertyEditorClass(null, ""));
        assertThrows(NullPointerException.class, () -> new PropertyUtilsBean().getPropertyEditorClass("", null));
    }

    @Test
    public void testSetResolver() throws Exception {
        final PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        final Resolver resolver = propertyUtilsBean.getResolver();
        assertNotNull(resolver);
        propertyUtilsBean.setResolver(null);
        assertNotNull(resolver);
        propertyUtilsBean.setResolver(resolver);
        assertEquals(resolver, propertyUtilsBean.getResolver());
    }

}
