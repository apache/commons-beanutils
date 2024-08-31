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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;

import org.apache.commons.beanutils2.ConversionException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ColorConverter}.
 *
 * @since 2.0.0
 */
public class ColorConverterTestCase {

    private ColorConverter converter;

    @BeforeEach
    public void before() {
        converter = new ColorConverter();
    }

    @Test
    public void testColorBlank() {
        assertThrows(ConversionException.class, () -> converter.convert(Color.class, "#"));
    }

    @Test
    public void testColorInvalidLength() {
        assertThrows(ConversionException.class, () -> converter.convert(Color.class, "#F"));
    }

    @Test
    public void testConvertingColorName() {
        final Color expected = Color.WHITE;
        final Color actual = converter.convert(Color.class, "white");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingColorNameCaps() {
        final Color expected = Color.LIGHT_GRAY;
        final Color actual = converter.convert(Color.class, "LIGHTGRAY");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingJavaColorStringFull() {
        final Color expected = Color.WHITE;
        final Color actual = converter.convert(Color.class, "java.awt.Color[r=255,g=255,b=255]");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingJavaColorStringWithoutBrackets() {
        final Color expected = Color.DARK_GRAY;
        final Color actual = converter.convert(Color.class, "r=64,g=64,b=64");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingJavaColorStringWithoutColorPrefixes() {
        final Color expected = Color.PINK;
        final Color actual = converter.convert(Color.class, "255,175,175");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingJavaColorStringWithoutPackage() {
        final Color expected = Color.GREEN;
        final Color actual = converter.convert(Color.class, "[r=0,g=255,b=0]");

        Assert.assertEquals(expected, actual);
    }

    /**
     * Color can be extended without the {@link Override overriding} the {@link Color#toString()} method.
     * This tests that it can continue to parse the {@link String} from an inherited class.
     */
    @Test
    public void testConvertingJavaExtendsColorString() {
        final Color expected = Color.MAGENTA;
        final Color actual = converter.convert(Color.class, "org.apache.ExtendedColor[r=255,g=0,b=255]");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingLiteralHex() {
        final Color expected = Color.BLUE;
        final Color actual = converter.convert(Color.class, "0x0000FF");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingPattern() {
        final Color expected = Color.BLACK;
        final Color actual = converter.convert(Color.class, "#000000");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingPattern3Digit() {
        final Color expected = Color.WHITE;
        final Color actual = converter.convert(Color.class, "#FFF");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingPattern4Digit() {
        final Color expected = Color.YELLOW;
        final Color actual = converter.convert(Color.class, "#FF0F");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConvertingPatternWithAlpha() {
        final Color expected = Color.LIGHT_GRAY;
        final Color actual = converter.convert(Color.class, "#C0C0C0FF");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testInvalidNumber3() {
        assertThrows(ConversionException.class, () -> converter.convert(Color.class, "#FFZ"));
    }

    @Test
    public void testInvalidNumber4() {
        assertThrows(ConversionException.class, () -> converter.convert(Color.class, "#FFFY"));
    }
}
