/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

    // -------------------------------------------------------------- Properties

    public String getMapproperty(String key) {
        return (String) map.get(key);
    }

    public void setMapproperty(String key, String value) {	
        map.put(key, value);
    }
}
