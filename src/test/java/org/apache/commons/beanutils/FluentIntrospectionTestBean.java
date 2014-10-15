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

/**
 * A bean class used for tests of introspection.
 *
 * @version $Id$
 */
public class FluentIntrospectionTestBean extends AlphaBean {
    private String stringProperty;

    private String fluentGetProperty;

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(final String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public FluentIntrospectionTestBean setFluentProperty(final String value) {
        setStringProperty(value);
        return this;
    }

    public String getFluentGetProperty() {
        return fluentGetProperty;
    }

    public FluentIntrospectionTestBean setFluentGetProperty(
            final String fluentGetProperty) {
        this.fluentGetProperty = fluentGetProperty;
        return this;
    }
}
