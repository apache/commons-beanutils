/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.beanutils2.converters;

import org.apache.commons.beanutils2.ConversionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Tests {@link InetAddressConverter}.
 *
 * @since 2.0.0
 */
public class InetAddressConverterTestCase {

    private InetAddressConverter converter;

    @Before
    public void before() {
        converter = new InetAddressConverter();
    }

    @Test
    public void testConvertingIpv4() throws UnknownHostException {
        final InetAddress expected = InetAddress.getByName("192.168.0.1");
        final InetAddress actual = converter.convert(InetAddress.class, "192.168.0.1");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingIpv6() throws UnknownHostException {
        final InetAddress expected = InetAddress.getByName("2001:db8:0:1234:0:567:8:1");
        final InetAddress actual = converter.convert(InetAddress.class, "2001:db8:0:1234:0:567:8:1");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingLocalhost() throws UnknownHostException {
        final InetAddress expected = InetAddress.getByName("127.0.0.1");
        final InetAddress actual = converter.convert(InetAddress.class, "localhost");

        Assert.assertEquals(expected, actual);
    }

    @Test(expected = ConversionException.class)
    public void testText() {
        converter.convert(InetAddress.class, "Hello, world!");
    }

    @Test(expected = ConversionException.class)
    public void testInvalidIp() {
        converter.convert(InetAddress.class, "512.512.512.512");
    }
}
