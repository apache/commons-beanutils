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
package org.apache.commons.beanutils.memoryleaktests.pojotests;

import java.util.HashMap;

/**
 * Test POJO with mapped property
 *
 * @version $Id$
 */
public class SomeMappedPojo {


    private HashMap<String, String> mappedProperty = null;

    public String getMappedProperty(final String key) {
        // Create the map the very first time
        if (mappedProperty == null) {
            mappedProperty = new HashMap<String, String>();
            mappedProperty.put("First Key", "First Value");
            mappedProperty.put("Second Key", "Second Value");
        }
        return mappedProperty.get(key);
    }

    public void setMappedProperty(final String key, final String value) {
        // Create the map the very first time
        if (mappedProperty == null) {
            mappedProperty = new HashMap<String, String>();
            mappedProperty.put("First Key", "First Value");
            mappedProperty.put("Second Key", "Second Value");
        }
        mappedProperty.put(key, value);
    }
}
