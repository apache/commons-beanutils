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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * A PropertyUtilsBean which customises the behaviour of the
 * setNestedProperty and getNestedProperty methods to look for
 * simple properties in preference to map entries.
 *
 * @version $Id$
 */
public class PropsFirstPropertyUtilsBean extends PropertyUtilsBean {

    public PropsFirstPropertyUtilsBean() {
        super();
    }

    /**
     * Note: this is a *very rough* override of this method. In particular,
     * it does not handle MAPPED_DELIM and INDEXED_DELIM chars in the
     * propertyName, so propertyNames like "a(b)" or "a[3]" will not
     * be correctly handled.
     */
    @Override
    protected Object getPropertyOfMapBean(final Map<?, ?> bean, final String propertyName)
    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        final PropertyDescriptor descriptor = getPropertyDescriptor(bean, propertyName);
        if (descriptor == null) {
            // no simple property exists so return the value from the map
            return bean.get(propertyName);
        } else {
            // a simple property exists so return its value instead.
            return getSimpleProperty(bean, propertyName);
        }
    }

    /**
     * Note: this is a *very rough* override of this method. In particular,
     * it does not handle MAPPED_DELIM and INDEXED_DELIM chars in the
     * propertyName, so propertyNames like "a(b)" or "a[3]" will not
     * be correctly handled.
     */
    @Override
    protected void setPropertyOfMapBean(final Map<String, Object> bean, final String propertyName, final Object value)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final PropertyDescriptor descriptor = getPropertyDescriptor(bean, propertyName);
        if (descriptor == null) {
            // no simple property exists so put the value into the map
            bean.put(propertyName, value);
        } else {
            // a simple property exists so set that instead.
            setSimpleProperty(bean, propertyName, value);
        }
    }
}
