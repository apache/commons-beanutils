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

import java.util.HashMap;
import java.util.Map;

/**
 * Just a java bean (JAJB) to try to replicate a reported bug 
 *
 * @author Robert Burrell Donkin
 * @version $Revision$ $Date$
 */

public class MappedPropertyTestBean {

    private Map map = new HashMap();
    private Map myMap = new HashMap();


    // -------------------------------------------------------------- Properties

    public String getMapproperty(String key) {
        return (String) map.get(key);
    }

    public void setMapproperty(String key, String value) {
        map.put(key, value);
    }

    public boolean isMappedBoolean(String key) {
        return ((Boolean)map.get(key)).booleanValue();
    }

    public void setMappedBoolean(String key, boolean value) {
        map.put(key, (value ? Boolean.TRUE : Boolean.FALSE));
    }

    protected String getProtectedMapped(String key) {
        return (String) map.get(key);
    }

    protected void setProtectedMapped(String key, String value) {
        map.put(key, value);
    }

    public void setMappedPrimitive(int key, int value) {
        map.put(new Integer(key), new Integer(value));
    }

    public void setAnyMapped(MappedPropertyTestBean key, MappedPropertyTestBean value) {
        map.put(key, value);
    }

    public void setMappedSetterOnly(String key, String value) {
        map.put(key, value);
    }

    public String getMappedGetterOnly(String key) {
        return (String) map.get(key);
    }

    public String getInvalidGetter(String key, String other) {
        return (String) map.get(key);
    }
    public Map getMyMap() {
        return myMap;
    }

    public void setInvalidGetter(String key, String value) {
        map.put(key, value);
    }
    public String getInvalidSetter(String key) {
        return (String) map.get(key);
    }
    public void setInvalidSetter(String key, String value, String other) {
    }

    public Long getDifferentTypes(String key) {
        return new Long(((Number)map.get(key)).longValue());
    }
    public void setDifferentTypes(String key, Integer value) {
        map.put(key, value);
    }

}
