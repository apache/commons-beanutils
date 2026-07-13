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

package org.apache.commons.beanutils2.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Locale;

import org.apache.commons.beanutils2.DynaProperty;
import org.junit.jupiter.api.Test;

/**
 * Tests that column names are folded to lower case independently of the default {@link Locale}.
 */
class JdbcDynaClassLocaleTest {

    private static ResultSet singleColumnResultSet(final String columnName) {
        final InvocationHandler metaData = (proxy, method, args) -> {
            switch (method.getName()) {
            case "getColumnCount":
                return Integer.valueOf(1);
            case "getColumnLabel":
            case "getColumnName":
                return columnName;
            case "getColumnType":
                return Integer.valueOf(Types.VARCHAR);
            case "getColumnClassName":
                return String.class.getName();
            default:
                throw new UnsupportedOperationException(method.getName());
            }
        };
        final ResultSetMetaData metaDataProxy = (ResultSetMetaData) Proxy.newProxyInstance(ResultSetMetaData.class.getClassLoader(),
                new Class[] { ResultSetMetaData.class }, metaData);
        final InvocationHandler resultSet = (proxy, method, args) -> {
            if ("getMetaData".equals(method.getName())) {
                return metaDataProxy;
            }
            throw new UnsupportedOperationException(method.getName());
        };
        return (ResultSet) Proxy.newProxyInstance(ResultSet.class.getClassLoader(), new Class[] { ResultSet.class }, resultSet);
    }

    @Test
    void testColumnNameLowerCasedIndependentOfLocale() throws Exception {
        final Locale save = Locale.getDefault();
        try {
            // Under the Turkish locale "ID".toLowerCase() is "ıd" (dotless i), not "id".
            Locale.setDefault(new Locale("tr", "TR"));
            final ResultSetDynaClass dynaClass = new ResultSetDynaClass(singleColumnResultSet("ID"));
            final DynaProperty property = dynaClass.getDynaProperty("id");
            assertNotNull(property, "column 'ID' must map to property 'id'");
            assertEquals("id", property.getName());
        } finally {
            Locale.setDefault(save);
        }
    }
}
