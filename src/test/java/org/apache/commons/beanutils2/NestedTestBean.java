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

/**
 * Specialist test bean for complex nested properties.
 *
 */

public class NestedTestBean {

    private String name;

    private String testString = "NOT SET";

    private boolean testBoolean = false;

    private NestedTestBean[] indexedBeans;

    private NestedTestBean simpleBean;

    public NestedTestBean(final String name) {
        setName(name);
    }

    public NestedTestBean getIndexedProperty(final int index) {
        return this.indexedBeans[index];
    }

    public String getName() {
        return name;
    }

    public NestedTestBean getSimpleBeanProperty() {
        return simpleBean;
    }

    public boolean getTestBoolean() {
        return testBoolean;
    }

    public String getTestString() {
        return testString;
    }

    public void init() {
        indexedBeans = new NestedTestBean[5];
        indexedBeans[0] = new NestedTestBean("Bean@0");
        indexedBeans[1] = new NestedTestBean("Bean@1");
        indexedBeans[2] = new NestedTestBean("Bean@2");
        indexedBeans[3] = new NestedTestBean("Bean@3");
        indexedBeans[4] = new NestedTestBean("Bean@4");

        simpleBean = new NestedTestBean("Simple Property Bean");
    }

    public void setIndexedProperty(final int index, final NestedTestBean value) {
        this.indexedBeans[index] = value;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setTestBoolean(final boolean testBoolean) {
        this.testBoolean = testBoolean;
    }

    public void setTestString(final String testString) {
        this.testString = testString;
    }

}
