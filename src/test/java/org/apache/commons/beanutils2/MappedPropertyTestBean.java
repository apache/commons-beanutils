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

import java.util.HashMap;
import java.util.Map;

/**
 * Just a Java bean (JAJB) to try to replicate a reported bug
 */

public class MappedPropertyTestBean {

    private final Map<Object, Object> map = new HashMap<>();
    private final Map<Object, Object> myMap = new HashMap<>();

    public Long getDifferentTypes(final String key) {
        return Long.valueOf(((Number) map.get(key)).longValue());
    }

    public String getInvalidGetter(final String key, final String other) {
        return (String) map.get(key);
    }

    public String getInvalidSetter(final String key) {
        return (String) map.get(key);
    }

    public String getMappedGetterOnly(final String key) {
        return (String) map.get(key);
    }

    public String getMapproperty(final String key) {
        return (String) map.get(key);
    }

    public Map<Object, Object> getMyMap() {
        return myMap;
    }

    protected String getProtectedMapped(final String key) {
        return (String) map.get(key);
    }

    public boolean isMappedBoolean(final String key) {
        return ((Boolean) map.get(key)).booleanValue();
    }

    public void setAnyMapped(final MappedPropertyTestBean key, final MappedPropertyTestBean value) {
        map.put(key, value);
    }

    public void setDifferentTypes(final String key, final Integer value) {
        map.put(key, value);
    }

    public void setInvalidGetter(final String key, final String value) {
        map.put(key, value);
    }

    public void setInvalidSetter(final String key, final String value, final String other) {
    }

    public void setMappedBoolean(final String key, final boolean value) {
        map.put(key, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setMappedPrimitive(final int key, final int value) {
        map.put(Integer.valueOf(key), Integer.valueOf(value));
    }

    public void setMappedSetterOnly(final String key, final String value) {
        map.put(key, value);
    }

    public void setMapproperty(final String key, final String value) {
        map.put(key, value);
    }

    protected void setProtectedMapped(final String key, final String value) {
        map.put(key, value);
    }

}
