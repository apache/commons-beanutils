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

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * getPropertyType return null on second descendant class
 *
 * @version $Id$
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-422">https://issues.apache.org/jira/browse/BEANUTILS-422</a>
 */
public class Jira422TestCase extends TestCase {

    public void testRootBean() throws Exception {
        RootBean bean = new FirstChildBean();
        Class propertyType = PropertyUtils.getPropertyType(bean, "file[0]");
        assertEquals(String.class.getName(), propertyType.getName());
    }

    public void testSecondChildBean() throws Exception {
        RootBean bean = new SecondChildBean();
        Class propertyType = PropertyUtils.getPropertyType(bean, "file[0]");
        assertEquals(String.class.getName(), propertyType.getName());
    }

}

class RootBean {

    private List file;

    public List getFile() {
        return file;
    }

    public void setFile(List file) {
        this.file = file;
    }

    public String getFile(int i) {
        return (String) file.get(i);
    }

    public void setFile(int i, String file) {
        this.file.set(i, file);
    }

}

class FirstChildBean extends RootBean {
}

class SecondChildBean extends RootBean {
}
