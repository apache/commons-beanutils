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
 * <p>Utility methods for converting String scalar values to objects of the
 * specified Class, String arrays to arrays of the specified Class.</p>
 *
 * <p>For more details, see <code>ConvertUtilsBean</code> which provides the
 * implementations for these methods.</p>
 *
 * @version $Id$
 * @see ConvertUtilsBean
 */

public class ConvertUtils {


    // ------------------------------------------------------ Static Properties

    /**
     * Gets the default value for Boolean conversions.
     * @return The default Boolean value
     * @deprecated Register replacement converters for Boolean.TYPE and
     *  Boolean.class instead
     */
    @Deprecated
    public static boolean getDefaultBoolean() {
        return (ConvertUtilsBean.getInstance().getDefaultBoolean());
    }

    /**
     * Sets the default value for Boolean conversions.
     * @param newDefaultBoolean The default Boolean value
     * @deprecated Register replacement converters for Boolean.TYPE and
     *  Boolean.class instead
     */
    @Deprecated
    public static void setDefaultBoolean(final boolean newDefaultBoolean) {
        ConvertUtilsBean.getInstance().setDefaultBoolean(newDefaultBoolean);
    }


    /**
     * Gets the default value for Byte conversions.
     * @return The default Byte value
     * @deprecated Register replacement converters for Byte.TYPE and
     *  Byte.class instead
     */
    @Deprecated
    public static byte getDefaultByte() {
        return ConvertUtilsBean.getInstance().getDefaultByte();
    }

    /**
     * Sets the default value for Byte conversions.
     * @param newDefaultByte The default Byte value
     * @deprecated Register replacement converters for Byte.TYPE and
     *  Byte.class instead
     */
    @Deprecated
    public static void setDefaultByte(final byte newDefaultByte) {
        ConvertUtilsBean.getInstance().setDefaultByte(newDefaultByte);
    }


    /**
     * Gets the default value for Character conversions.
     * @return The default Character value
     * @deprecated Register replacement converters for Character.TYPE and
     *  Character.class instead
     */
    @Deprecated
    public static char getDefaultCharacter() {
        return ConvertUtilsBean.getInstance().getDefaultCharacter();
    }

    /**
     * Sets the default value for Character conversions.
     * @param newDefaultCharacter The default Character value
     * @deprecated Register replacement converters for Character.TYPE and
     *  Character.class instead
     */
    @Deprecated
    public static void setDefaultCharacter(final char newDefaultCharacter) {
        ConvertUtilsBean.getInstance().setDefaultCharacter(newDefaultCharacter);
    }


    /**
     * Gets the default value for Double conversions.
     * @return The default Double value
     * @deprecated Register replacement converters for Double.TYPE and
     *  Double.class instead
     */
    @Deprecated
    public static double getDefaultDouble() {
        return ConvertUtilsBean.getInstance().getDefaultDouble();
    }

    /**
     * Sets the default value for Double conversions.
     * @param newDefaultDouble The default Double value
     * @deprecated Register replacement converters for Double.TYPE and
     *  Double.class instead
     */
    @Deprecated
    public static void setDefaultDouble(final double newDefaultDouble) {
        ConvertUtilsBean.getInstance().setDefaultDouble(newDefaultDouble);
    }


    /**
     * Get the default value for Float conversions.
     * @return The default Float value
     * @deprecated Register replacement converters for Float.TYPE and
     *  Float.class instead
     */
    @Deprecated
    public static float getDefaultFloat() {
        return ConvertUtilsBean.getInstance().getDefaultFloat();
    }

    /**
     * Sets the default value for Float conversions.
     * @param newDefaultFloat The default Float value
     * @deprecated Register replacement converters for Float.TYPE and
     *  Float.class instead
     */
    @Deprecated
    public static void setDefaultFloat(final float newDefaultFloat) {
        ConvertUtilsBean.getInstance().setDefaultFloat(newDefaultFloat);
    }


    /**
     * Gets the default value for Integer conversions.
     * @return The default Integer value
     * @deprecated Register replacement converters for Integer.TYPE and
     *  Integer.class instead
     */
    @Deprecated
    public static int getDefaultInteger() {
        return ConvertUtilsBean.getInstance().getDefaultInteger();
    }

    /**
     * Sets the default value for Integer conversions.
     * @param newDefaultInteger The default Integer value
     * @deprecated Register replacement converters for Integer.TYPE and
     *  Integer.class instead
     */
    @Deprecated
    public static void setDefaultInteger(final int newDefaultInteger) {
        ConvertUtilsBean.getInstance().setDefaultInteger(newDefaultInteger);
    }


    /**
     * Gets the default value for Long conversions.
     * @return The default Long value
     * @deprecated Register replacement converters for Long.TYPE and
     *  Long.class instead
     */
    @Deprecated
    public static long getDefaultLong() {
        return (ConvertUtilsBean.getInstance().getDefaultLong());
    }

    /**
     * Sets the default value for Long conversions.
     * @param newDefaultLong The default Long value
     * @deprecated Register replacement converters for Long.TYPE and
     *  Long.class instead
     */
    @Deprecated
    public static void setDefaultLong(final long newDefaultLong) {
        ConvertUtilsBean.getInstance().setDefaultLong(newDefaultLong);
    }


    /**
     * Gets the default value for Short conversions.
     * @return The default Short value
     * @deprecated Register replacement converters for Short.TYPE and
     *  Short.class instead
     */
    @Deprecated
    public static short getDefaultShort() {
        return ConvertUtilsBean.getInstance().getDefaultShort();
    }

    /**
     * Sets the default value for Short conversions.
     * @param newDefaultShort The default Short value
     * @deprecated Register replacement converters for Short.TYPE and
     *  Short.class instead
     */
    @Deprecated
    public static void setDefaultShort(final short newDefaultShort) {
        ConvertUtilsBean.getInstance().setDefaultShort(newDefaultShort);
    }

    // --------------------------------------------------------- Public Classes


    /**
     * <p>Convert the specified value into a String.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @param value Value to be converted (may be null)
     * @return The converted String value or null if value is null
     *
     * @see ConvertUtilsBean#convert(Object)
     */
    public static String convert(final Object value) {
        return ConvertUtilsBean.getInstance().convert(value);
    }


    /**
     * <p>Convert the specified value to an object of the specified class (if
     * possible).  Otherwise, return a String representation of the value.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @param value Value to be converted (may be null)
     * @param clazz Java class to be converted to (must not be null)
     * @return The converted value
     *
     * @see ConvertUtilsBean#convert(String, Class)
     */
    public static Object convert(final String value, final Class<?> clazz) {
        return ConvertUtilsBean.getInstance().convert(value, clazz);
    }


    /**
     * <p>Convert an array of specified values to an array of objects of the
     * specified class (if possible).</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @param values Array of values to be converted
     * @param clazz Java array or element class to be converted to (must not be null)
     * @return The converted value
     *
     * @see ConvertUtilsBean#convert(String[], Class)
     */
    public static Object convert(final String[] values, final Class<?> clazz) {
        return ConvertUtilsBean.getInstance().convert(values, clazz);
    }

    /**
     * <p>Convert the value to an object of the specified class (if
     * possible).</p>
     *
     * @param value Value to be converted (may be null)
     * @param targetType Class of the value to be converted to (must not be null)
     * @return The converted value
     *
     * @throws ConversionException if thrown by an underlying Converter
     */
    public static Object convert(final Object value, final Class<?> targetType) {
        return ConvertUtilsBean.getInstance().convert(value, targetType);
    }

    /**
     * <p>Remove all registered {@link Converter}s, and re-establish the
     * standard Converters.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @see ConvertUtilsBean#deregister()
     */
    public static void deregister() {
        ConvertUtilsBean.getInstance().deregister();
    }


    /**
     * <p>Remove any registered {@link Converter} for the specified destination
     * <code>Class</code>.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @param clazz Class for which to remove a registered Converter
     * @see ConvertUtilsBean#deregister(Class)
     */
    public static void deregister(final Class<?> clazz) {
        ConvertUtilsBean.getInstance().deregister(clazz);
    }


    /**
     * <p>Look up and return any registered {@link Converter} for the specified
     * destination class; if there is no registered Converter, return
     * <code>null</code>.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @param clazz Class for which to return a registered Converter
     * @return The registered {@link Converter} or <code>null</code> if not found
     * @see ConvertUtilsBean#lookup(Class)
     */
    public static Converter lookup(final Class<?> clazz) {
        return ConvertUtilsBean.getInstance().lookup(clazz);
    }

    /**
     * Look up and return any registered {@link Converter} for the specified
     * source and destination class; if there is no registered Converter,
     * return <code>null</code>.
     *
     * @param sourceType Class of the value being converted
     * @param targetType Class of the value to be converted to
     * @return The registered {@link Converter} or <code>null</code> if not found
     */
    public static Converter lookup(final Class<?> sourceType, final Class<?> targetType) {
        return ConvertUtilsBean.getInstance().lookup(sourceType, targetType);
    }

    /**
     * <p>Register a custom {@link Converter} for the specified destination
     * <code>Class</code>, replacing any previously registered Converter.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @param converter Converter to be registered
     * @param clazz Destination class for conversions performed by this
     *  Converter
     * @see ConvertUtilsBean#register(Converter, Class)
     */
    public static void register(final Converter converter, final Class<?> clazz) {
        ConvertUtilsBean.getInstance().register(converter, clazz);
    }


    /**
     * Change primitive Class types to the associated wrapper class. This is
     * useful for concrete converter implementations which typically treat
     * primitive types like their corresponding wrapper types.
     *
     * @param <T> The type to be checked.
     * @param type The class type to check.
     * @return The converted type.
     * @since 1.9
     */
    // All type casts are safe because the TYPE members of the wrapper types
    // return their own class.
    @SuppressWarnings("unchecked")
    public static <T> Class<T> primitiveToWrapper(final Class<T> type) {
        if (type == null || !type.isPrimitive()) {
            return type;
        }

        if (type == Integer.TYPE) {
            return (Class<T>) Integer.class;
        } else if (type == Double.TYPE) {
            return (Class<T>) Double.class;
        } else if (type == Long.TYPE) {
            return (Class<T>) Long.class;
        } else if (type == Boolean.TYPE) {
            return (Class<T>) Boolean.class;
        } else if (type == Float.TYPE) {
            return (Class<T>) Float.class;
        } else if (type == Short.TYPE) {
            return (Class<T>) Short.class;
        } else if (type == Byte.TYPE) {
            return (Class<T>) Byte.class;
        } else if (type == Character.TYPE) {
            return (Class<T>) Character.class;
        } else {
            return type;
        }
    }
}
