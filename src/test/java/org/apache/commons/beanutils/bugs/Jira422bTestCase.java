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

import static org.junit.Assert.assertEquals;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

/**
 * Variant of {@link Jira422TestCase} that is compatible with BEANUTILS-492
 *
 * @version $Id$
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-422">BEANUTILS-422</a>
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-492">BEANUTILS-492</a>
 */
public class Jira422bTestCase {

    @Test
    public void testRootBean() throws Exception {
        final RootBeanB bean = new FirstChildBeanB();
        final Class<?> propertyType = PropertyUtils.getPropertyType(bean, "file[0]");
        assertEquals(String.class.getName(), propertyType.getName());
    }


    @Test
    public void testSecondChildBean() throws Exception {
        final RootBeanB bean = new SecondChildBeanB();
        final Class<?> propertyType = PropertyUtils.getPropertyType(bean, "file[0]");
        assertEquals(String.class.getName(), propertyType.getName());
    }

}


class RootBeanB {

    private String[] file;

    public String[] getFile() {
        return file;
    }

    public void setFile(final String[] file) {
        this.file = file;
    }

    public String getFile(final int i) {
        return file[i];
    }

    public void setFile(final int i, final String file) {
        this.file[i] = file;
    }

}

class FirstChildBeanB extends RootBeanB {
}

class SecondChildBeanB extends RootBeanB {
}
