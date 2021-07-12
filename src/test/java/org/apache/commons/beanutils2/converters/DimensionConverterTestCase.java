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

import java.awt.Dimension;

/**
 * Tests {@link DimensionConverter}.
 *
 * @since 2.0.0
 */
public class DimensionConverterTestCase {

    private DimensionConverter converter;

    @Before
    public void before() {
        converter = new DimensionConverter();
    }

    @Test
    public void testConvertingDimension() {
        final Dimension expected = new Dimension(1920, 1080);
        final Dimension actual = converter.convert(Dimension.class, "1920x1080");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingSquare() {
        final Dimension expected = new Dimension(512, 512);
        final Dimension actual = converter.convert(Dimension.class, "512");

        Assert.assertEquals(expected, actual);
    }

    @Test(expected = ConversionException.class)
    public void testInvalidDimensions() {
        converter.convert(Dimension.class, "512n512");
    }

    @Test(expected = ConversionException.class)
    public void testInvalidNumberFormatException() {
        converter.convert(Dimension.class, "3000000000x100");
    }

    @Test(expected = ConversionException.class)
    public void testNegativeDimension() {
        converter.convert(Dimension.class, "-512x512");
    }
}
