/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @version $Revision: 1.17 $ $Date: 2004/02/28 13:18:33 $
 * @see ConvertUtilsBean
 */

public class ConvertUtils {


    // ------------------------------------------------------ Static Properties

    /**
     * Gets the default value for Boolean conversions.
     * @deprecated Register replacement converters for Boolean.TYPE and
     *  Boolean.class instead
     */
    public static boolean getDefaultBoolean() {
        return (ConvertUtilsBean.getInstance().getDefaultBoolean());
    }

    /**
     * Sets the default value for Boolean conversions.
     * @deprecated Register replacement converters for Boolean.TYPE and
     *  Boolean.class instead
     */
    public static void setDefaultBoolean(boolean newDefaultBoolean) {
        ConvertUtilsBean.getInstance().setDefaultBoolean(newDefaultBoolean);
    }


    /**
     * Gets the default value for Byte conversions.
     * @deprecated Register replacement converters for Byte.TYPE and
     *  Byte.class instead
     */
    public static byte getDefaultByte() {
        return ConvertUtilsBean.getInstance().getDefaultByte();
    }

    /**
     * Sets the default value for Byte conversions.
     * @deprecated Register replacement converters for Byte.TYPE and
     *  Byte.class instead
     */
    public static void setDefaultByte(byte newDefaultByte) {
        ConvertUtilsBean.getInstance().setDefaultByte(newDefaultByte);
    }


    /**
     * Gets the default value for Character conversions.
     * @deprecated Register replacement converters for Character.TYPE and
     *  Character.class instead
     */
    public static char getDefaultCharacter() {
        return ConvertUtilsBean.getInstance().getDefaultCharacter();
    }

    /**
     * Sets the default value for Character conversions.
     * @deprecated Register replacement converters for Character.TYPE and
     *  Character.class instead
     */
    public static void setDefaultCharacter(char newDefaultCharacter) {
        ConvertUtilsBean.getInstance().setDefaultCharacter(newDefaultCharacter);
    }


    /**
     * Gets the default value for Double conversions.
     * @deprecated Register replacement converters for Double.TYPE and
     *  Double.class instead
     */
    public static double getDefaultDouble() {
        return ConvertUtilsBean.getInstance().getDefaultDouble();
    }

    /**
     * Sets the default value for Double conversions.
     * @deprecated Register replacement converters for Double.TYPE and
     *  Double.class instead
     */
    public static void setDefaultDouble(double newDefaultDouble) {
        ConvertUtilsBean.getInstance().setDefaultDouble(newDefaultDouble);
    }


    /**
     * Get the default value for Float conversions.
     * @deprecated Register replacement converters for Float.TYPE and
     *  Float.class instead
     */
    public static float getDefaultFloat() {
        return ConvertUtilsBean.getInstance().getDefaultFloat();
    }

    /**
     * Sets the default value for Float conversions.
     * @deprecated Register replacement converters for Float.TYPE and
     *  Float.class instead
     */
    public static void setDefaultFloat(float newDefaultFloat) {
        ConvertUtilsBean.getInstance().setDefaultFloat(newDefaultFloat);
    }


    /**
     * Gets the default value for Integer conversions.
     * @deprecated Register replacement converters for Integer.TYPE and
     *  Integer.class instead
     */
    public static int getDefaultInteger() {
        return ConvertUtilsBean.getInstance().getDefaultInteger();
    }

    /**
     * Sets the default value for Integer conversions.
     * @deprecated Register replacement converters for Integer.TYPE and
     *  Integer.class instead
     */
    public static void setDefaultInteger(int newDefaultInteger) {
        ConvertUtilsBean.getInstance().setDefaultInteger(newDefaultInteger);
    }


    /**
     * Gets the default value for Long conversions.
     * @deprecated Register replacement converters for Long.TYPE and
     *  Long.class instead
     */
    public static long getDefaultLong() {
        return (ConvertUtilsBean.getInstance().getDefaultLong());
    }

    /**
     * Sets the default value for Long conversions.
     * @deprecated Register replacement converters for Long.TYPE and
     *  Long.class instead
     */
    public static void setDefaultLong(long newDefaultLong) {
        ConvertUtilsBean.getInstance().setDefaultLong(newDefaultLong);
    }


    /**
     * Gets the default value for Short conversions.
     * @deprecated Register replacement converters for Short.TYPE and
     *  Short.class instead
     */
    public static short getDefaultShort() {
        return ConvertUtilsBean.getInstance().getDefaultShort();
    }

    /**
     * Sets the default value for Short conversions.
     * @deprecated Register replacement converters for Short.TYPE and
     *  Short.class instead
     */
    public static void setDefaultShort(short newDefaultShort) {
        ConvertUtilsBean.getInstance().setDefaultShort(newDefaultShort);
    }

    // --------------------------------------------------------- Public Classes


    /**
     * <p>Convert the specified value into a String.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @see ConvertUtilsBean#convert(Object)
     */
    public static String convert(Object value) {

        return ConvertUtilsBean.getInstance().convert(value);

    }


    /**
     * <p>Convert the specified value to an object of the specified class (if
     * possible).  Otherwise, return a String representation of the value.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @see ConvertUtilsBean#convert(String, Class)
     */
    public static Object convert(String value, Class clazz) {

        return ConvertUtilsBean.getInstance().convert(value, clazz);

    }


    /**
     * <p>Convert an array of specified values to an array of objects of the
     * specified class (if possible).</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @see ConvertUtilsBean#convert(String[], Class)
     */
    public static Object convert(String values[], Class clazz) {

  return ConvertUtilsBean.getInstance().convert(values, clazz);

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
     * @see ConvertUtilsBean#deregister(Class)
     */
    public static void deregister(Class clazz) {

        ConvertUtilsBean.getInstance().deregister(clazz);

    }


    /**
     * <p>Look up and return any registered {@link Converter} for the specified
     * destination class; if there is no registered Converter, return
     * <code>null</code>.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @see ConvertUtilsBean#lookup(Class)
     */
    public static Converter lookup(Class clazz) {

        return ConvertUtilsBean.getInstance().lookup(clazz);

    }


    /**
     * <p>Register a custom {@link Converter} for the specified destination
     * <code>Class</code>, replacing any previously registered Converter.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @see ConvertUtilsBean#register(Converter, Class)
     */
    public static void register(Converter converter, Class clazz) {

        ConvertUtilsBean.getInstance().register(converter, clazz);

    }


}
