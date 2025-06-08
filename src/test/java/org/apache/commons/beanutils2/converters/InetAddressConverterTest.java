/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.beanutils2.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.beanutils2.ConversionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link InetAddressConverter}.
 *
 * @since 2.0.0
 */
class InetAddressConverterTest {

    private InetAddressConverter converter;

    @BeforeEach
    public void before() {
        converter = new InetAddressConverter();
    }

    @Test
    void testConvertingIpv4() throws UnknownHostException {
        final InetAddress expected = InetAddress.getByName("192.168.0.1");
        final InetAddress actual = converter.convert(InetAddress.class, "192.168.0.1");

        assertEquals(expected, actual);
    }

    @Test
    void testConvertingIpv6() throws UnknownHostException {
        final InetAddress expected = InetAddress.getByName("2001:db8:0:1234:0:567:8:1");
        final InetAddress actual = converter.convert(InetAddress.class, "2001:db8:0:1234:0:567:8:1");

        assertEquals(expected, actual);
    }

    @Test
    void testConvertingLocalhost() throws UnknownHostException {
        final InetAddress expected = InetAddress.getByName("127.0.0.1");
        final InetAddress actual = converter.convert(InetAddress.class, "localhost");

        assertEquals(expected, actual);
    }

    @Test
    void testInvalidIp() {
        assertThrows(ConversionException.class, () -> converter.convert(InetAddress.class, "512.512.512.512"));
    }

    @Test
    void testText() {
        assertThrows(ConversionException.class, () -> converter.convert(InetAddress.class, "Hello, world!"));
    }
}
