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

package org.apache.commons.beanutils2.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for the URLConverter class.
 */
public class URLConverterTest {

    private URLConverter converter;

    @BeforeEach
    public void before() {
        converter = new URLConverter();
    }

    @Test
    public void testSimpleConversion() {
        final String[] input = { "http://www.apache.org", "http://www.apache.org/", "ftp://cvs.apache.org", "file://project.xml", "http://208.185.179.12",
                "http://www.apache.org:9999/test/thing", "http://user:admin@www.apache.org:50/one/two.three", "http://notreal.apache.org",
                "http://notreal.apache.org/test/file.xml#计算机图形学",
                "http://notreal.apache.org/test/file.xml#%E8%AE%A1%E7%AE%97%E6%9C%BA%E5%9B%BE%E5%BD%A2%E5%AD%A6" };

        for (final String urlString : input) {
            assertEquals(urlString, converter.convert(URL.class, urlString).toString(), "from String to URL");
            assertEquals(urlString, converter.convert(null, urlString).toString(),"from String to null type");
        }
    }

}
