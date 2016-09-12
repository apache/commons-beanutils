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

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * getPropertyType return null on second descendant class
 * <p>
 * This test only work in Java 7 or earlier (See BEANUTILS-492) - as
 * a weaker alternative, see {@link Jira422bTestCase}.
 *
 *
 * @version $Id$
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-422">https://issues.apache.org/jira/browse/BEANUTILS-422</a>
 */
public class Jira422TestCase {

    /**
     * Detects BEANUTILS-492 in Java 8 or later
     *
     * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-492">BEANUTILS-492</a>
     */
    @BeforeClass
    public static void assumeSupportsIndexedLists() throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(RootBean.class);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (pd.getName().equals("file")) {
                Assume.assumeTrue("BEANUTILS-492: IndexedPropertyDescriptor no longer supported for java.util.List",
                        pd instanceof IndexedPropertyDescriptor);
                return;
            }
        }
        Assert.fail("Could not find PropertyDescriptor for 'file'");
    }

    @Test
    public void testRootBean() throws Exception {
        final RootBean bean = new FirstChildBean();
        final Class<?> propertyType = PropertyUtils.getPropertyType(bean, "file[0]");
        assertEquals(String.class.getName(), propertyType.getName());
    }

    @Test
    public void testSecondChildBean() throws Exception {
        final RootBean bean = new SecondChildBean();
        final Class<?> propertyType = PropertyUtils.getPropertyType(bean, "file[0]");
        assertEquals(String.class.getName(), propertyType.getName());
    }

}

@SuppressWarnings("rawtypes")
class RootBean {

    private List file;

    public List getFile() {
        return file;
    }

    public void setFile(final List file) {
        this.file = file;
    }

    public String getFile(final int i) {
        return (String) file.get(i);
    }

    @SuppressWarnings("unchecked")
    public void setFile(final int i, final String file) {
        this.file.set(i, file);
    }

}

class FirstChildBean extends RootBean {
}

class SecondChildBean extends RootBean {
}
