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

import java.awt.Color;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts a configuration property into a Java {@link Color} object.
 *
 * <p>
 *     Compatible with web color formats supported by browsers with CSS, such as:
 * </p>
 *
 * <ul>
 *     <li>#RGB</li>
 *     <li>#RGBA</li>
 *     <li>#RRGGBBAA</li>
 * </ul>
 *
 * <p>
 *     This converter will use the web based hexadecimal interpretations if the value is prefixed with
 *     {@link #HEX_COLOR_PREFIX}.
 *
 *     If using a literal number, or {@link Color#decode(String)} is desired, you must prefix your value with
 *     <code>0x</code> instead of {@link #HEX_COLOR_PREFIX}.
 * </p>
 *
 * @since 2.0.0
 */
public class ColorConverter extends AbstractConverter {

    /** Web based hexadecimal color must be prefixed with this. */
    private static final String HEX_COLOR_PREFIX = "#";

    /** A regular expression matching the output of {@link Color#toString()} and similar outputs. */
    private static final Pattern JAVA_COLOR_PATTERN =
        Pattern.compile("^(?:[A-Za-z\\d._]+)??\\[?(?:r=)?(\\d{1,3}),(?:g=)?(\\d{1,3}),(?:b=)?(\\d{1,3})\\]?$");

    /**
     * Construct a <b>{@link Color}</b> <i>Converter</i> that throws a {@code ConversionException} if an error occurs.
     */
    public ColorConverter() {
        super();
    }

    /**
     * Constructs a {@link org.apache.commons.beanutils2.Converter} that will return the specified default value if a
     * conversion error occurs.
     *
     * @param defaultValue The default value to be returned if the value to be converted is missing or an error occurs
     * converting the value.
     */
    public ColorConverter(final Object defaultValue) {
        super(defaultValue);
    }

    /**
     * Gets the default type this {@code Converter} handles.
     *
     * @return The default type this {@code Converter} handles.
     * @since 2.0.0
     */
    @Override
    protected Class<?> getDefaultType() {
        return Color.class;
    }

    /**
     * Converts the configuration value to a Java {@link Color} object, by reading the hexadecimal {@link String} and
     * converting each component.
     *
     * <p>
     *     This can also interpret raw color names based on the standard colors defined in Java, such as the following:
     * </p>
     *
     * <ul>
     *     <li>{@link Color#BLACK}</li>
     *     <li>{@link Color#BLUE}</li>
     *     <li>{@link Color#CYAN}</li>
     *     <li>{@link Color#DARK_GRAY}</li>
     *     <li>{@link Color#GRAY}</li>
     *     <li>{@link Color#GREEN}</li>
     *     <li>{@link Color#LIGHT_GRAY}</li>
     *     <li>{@link Color#MAGENTA}</li>
     *     <li>{@link Color#ORANGE}</li>
     *     <li>{@link Color#PINK}</li>
     *     <li>{@link Color#RED}</li>
     *     <li>{@link Color#WHITE}</li>
     *     <li>{@link Color#YELLOW}</li>
     * </ul>
     *
     * We avoid the use of {@link Color#decode(String)} for hexadecimal {@link String}s starting with
     * {@link #HEX_COLOR_PREFIX} as it doesn't provide the desired result. The {@link Color#decode(String)} method uses
     * {@link Integer#decode(String)} to convert the input to a number, so <code>#FFF</code> gets interpreted
     * unexpectedly, as it's literally converted to the number <code>0xFFF</code>, rather than the color,
     * <code>#FFFFFF</code> which it is short hand for. It also doesn't work for <code>#FFFFFFFF</code> due to it being
     * unable to parse as an {@link Integer}. If this is desired, then this method falls back to using
     * {@link Color#decode(String)}, so for literal hexadecimal values you prefix it with <code>0x</code> instead of
     * {@link #HEX_COLOR_PREFIX}.
     *
     * @param value The String property value to convert.
     * @return A {@link Color} which represents the compiled configuration property.
     * @throws NullPointerException If the value is null.
     * @throws NumberFormatException If an invalid number is provided.
     */
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (Color.class.isAssignableFrom(type)) {
            final String stringValue = toString(value);

            switch (toLowerCase(stringValue)) {
                case "black":
                    return type.cast(Color.BLACK);
                case "blue":
                    return type.cast(Color.BLUE);
                case "cyan":
                    return type.cast(Color.CYAN);
                case "darkgray":
                case "darkgrey":
                case "dark_gray":
                case "dark_grey":
                    return type.cast(Color.DARK_GRAY);
                case "gray":
                case "grey":
                    return type.cast(Color.GRAY);
                case "green":
                    return type.cast(Color.GREEN);
                case "lightgray":
                case "lightgrey":
                case "light_gray":
                case "light_grey":
                    return type.cast(Color.LIGHT_GRAY);
                case "magenta":
                    return type.cast(Color.MAGENTA);
                case "orange":
                    return type.cast(Color.ORANGE);
                case "pink":
                    return type.cast(Color.PINK);
                case "red":
                    return type.cast(Color.RED);
                case "white":
                    return type.cast(Color.WHITE);
                case "yellow":
                    return type.cast(Color.YELLOW);
                default:
                    // Do nothing.
            }

            if (stringValue.startsWith(HEX_COLOR_PREFIX)) {
                return type.cast(parseWebColor(stringValue));
            }

            if (stringValue.contains(",")) {
                return type.cast(parseToStringColor(stringValue));
            }

            return type.cast(Color.decode(stringValue));
        }

        throw conversionException(type, value);
    }

    /**
     * Parses the Color based on how the result of the {@link Color#toString()} method.
     *
     * Accepts the following values:
     * <ul>
     *     <li><code>java.awt.Color[r=255,g=255,b=255]</code></li>
     *     <li><code>[r=255,g=255,b=255]</code></li>
     *     <li><code>r=255,g=255,b=255</code></li>
     *     <li><code>255,255,255</code></li>
     * </ul>
     *
     * @param value A color as represented by {@link Color#toString()}.
     * @return The Java friendly {@link Color} this color represents.
     * @throws IllegalArgumentException If the input can't be matches by the {@link #JAVA_COLOR_PATTERN}
     * or a {@link Color} component specified is over 255.
     */
    private Color parseToStringColor(final String value) {
        Objects.requireNonNull(value);

        Matcher matcher = JAVA_COLOR_PATTERN.matcher(value);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid Color String provided. Could not parse.");
        }

        final int red = Integer.parseInt(matcher.group(1));
        final int green = Integer.parseInt(matcher.group(2));
        final int blue = Integer.parseInt(matcher.group(3));

        if (red > 255 || green > 255 || blue > 255) {
            throw new IllegalArgumentException("Color component integers must be between 0 and 255.");
        }

        return new Color(red, green, blue);
    }

    /**
     * Returns a {@link Color}; not a {@link Number} or {@link String} for
     * {@link Color#decode(String)} as it is not capable of supporting a color alpha channel due to the
     * limited range of an {@link Integer}.
     *
     * @param value The web friendly hexadecimal {@link String}.
     * @return The Java friendly {@link Color} this color represents.
     * @throws NumberFormatException If the hexadecimal input contains non parsable characters.
     */
    private Color parseWebColor(final String value) {
        Objects.requireNonNull(value);

        switch (value.length()) {
            case 4:
                return new Color(
                    Integer.parseInt(value.substring(1, 2), 16) * 17,
                    Integer.parseInt(value.substring(2, 3), 16) * 17,
                    Integer.parseInt(value.substring(3, 4), 16) * 17
                );
            case 5:
                return new Color(
                    Integer.parseInt(value.substring(1, 2), 16) * 17,
                    Integer.parseInt(value.substring(2, 3), 16) * 17,
                    Integer.parseInt(value.substring(3, 4), 16) * 17,
                    Integer.parseInt(value.substring(4, 5), 16) * 17
                );
            case 7:
                return new Color(
                    Integer.parseInt(value.substring(1, 3), 16),
                    Integer.parseInt(value.substring(3, 5), 16),
                    Integer.parseInt(value.substring(5, 7), 16)
                );
            case 9:
                return new Color(
                    Integer.parseInt(value.substring(1, 3), 16),
                    Integer.parseInt(value.substring(3, 5), 16),
                    Integer.parseInt(value.substring(5, 7), 16),
                    Integer.parseInt(value.substring(7, 9), 16)
                );
            default:
                throw new IllegalArgumentException("Invalid hexadecimal color provided, if literal value decoding " +
                    "is required, specify 0x instead of #, otherwise expecting 3, 4, 6, or 8 characters only.");
        }
    }
}
