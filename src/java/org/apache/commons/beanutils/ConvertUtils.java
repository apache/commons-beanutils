/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/ConvertUtils.java,v 1.13 2003/03/03 22:33:46 rdonkin Exp $
 * $Revision: 1.13 $
 * $Date: 2003/03/03 22:33:46 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
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
 * @version $Revision: 1.13 $ $Date: 2003/03/03 22:33:46 $
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

        ConvertUtilsBean.getInstance().deregister();

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
