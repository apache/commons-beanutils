/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;


/**
 * <p> Utility reflection methods focussed on methods in general rather than properties in particular. </p>
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @author Rey François
 * @author Gregor Raýman
 * @author Jan Sorensen
 * @author Robert Burrell Donkin
 */

public class MethodUtils {


    // --------------------------------------------------------- Public Methods


    /**
     * <p>Invoke a method whose parameter type matches exactly the object
     * type.</p>
     *
     * <p> This is a convenient wrapper for
     * {@link #invokeExactMethod(Object object,String methodName,Object [] args)}.
     * </p>
     *
     * @param object invoke method on this object
     * @param methodName get method with this name
     * @param arg use this argument
     *
     * @throw NoSuchMethodException if there is no such accessible method
     * @throw InvocationTargetException wraps an exception thrown by the
     *  method invoked
     * @throw IllegalAccessException if the requested method is not accessible
     *  via reflection
     */
    public static Object invokeExactMethod(
                Object object,
                String methodName,
                Object arg)
                    throws
                        NoSuchMethodException,
                        IllegalAccessException,
                        InvocationTargetException
    {

        Object [] args = {arg};
        return invokeExactMethod(object, methodName, args);

    }


    /**
     * <p>Invoke a method whose parameter types match exactly the object
     * types.</p>
     *
     * <p> This uses reflection to invoke the method obtained from a call to
     * {@link #getAccessibleMethod}.</p>
     *
     * @param object invoke method on this object
     * @param methodName get method with this name
     * @param args use these arguments
     *
     * @throw NoSuchMethodException if there is no such accessible method
     * @throw InvocationTargetException wraps an exception thrown by the
     *  method invoked
     * @throw IllegalAccessException if the requested method is not accessible
     *  via reflection
     */
    public static Object invokeExactMethod(
                Object object,
                String methodName,
                Object [] args)
                    throws
                        NoSuchMethodException,
                        IllegalAccessException,
                        InvocationTargetException
    {

        int arguments  = args.length;
        Class parameterTypes [] = new Class[arguments];
        for (int i=0; i<arguments ;i++)
        {
            parameterTypes[i] = args[i].getClass();
        }
        return invokeExactMethod(object, methodName, args, parameterTypes);

    }


    /**
     * <p>Invoke a method whose parameter types match exactly the parameter
     * types given.</p>
     *
     * <p>This uses reflection to invoke the method obtained from a call to
     * {@link #getAccessibleMethod}.</p>
     *
     * @param object invoke method on this object
     * @param methodName get method with this name
     * @param args use these arguments
     * @param parameterTypes match these parameters
     *
     * @throw NoSuchMethodException if there is no such accessible method
     * @throw InvocationTargetException wraps an exception thrown by the
     *  method invoked
     * @throw IllegalAccessException if the requested method is not accessible
     *  via reflection
     */
    public static Object invokeExactMethod(
                Object object,
                String methodName,
                Object [] args,
                Class [] parameterTypes)
                    throws
                        NoSuchMethodException,
                        IllegalAccessException,
                        InvocationTargetException
    {

        Method method = getAccessibleMethod(
                                        object.getClass(),
                                        methodName,
                                        parameterTypes);
        if (method == null)
            throw new NoSuchMethodException("No such accessible method: " +
                    methodName + "() on object: " + object.getClass().getName());
        return method.invoke(object, args);

    }


    /**
     * <p>Return an accessible method (that is, one that can be invoked via
     * reflection) with given name and a single parameter.  If no such method
     * can be found, return <code>null</code>.
     * Basically, a convenience wrapper that constructs a <code>Class</code>
     * array for you.</p>
     *
     * @param clazz get method from this class
     * @param methodName get method with this name
     * @param parameterType taking this type of parameter
     */
    public static Method getAccessibleMethod(
                Class clazz,
                String methodName,
                Class parameterType)
    {

        Class [] parameterTypes = {parameterType};
        return getAccessibleMethod(clazz, methodName, parameterTypes);

    }


    /**
     * <p>Return an accessible method (that is, one that can be invoked via
     * reflection) with given name and parameters.  If no such method
     * can be found, return <code>null</code>.
     * This is just a convenient wrapper for
     * {@link #getAccessibleMethod(Method method)}.</p>
     *
     * @param clazz get method from this class
     * @param methodName get method with this name
     * @param parameterTypes with these parameters types
     */
    public static Method getAccessibleMethod(
                Class clazz,
                String methodName,
                Class[] parameterTypes)
    {

        try {
            return getAccessibleMethod
                (clazz.getMethod(methodName, parameterTypes));
        } catch (NoSuchMethodException e) {
            return (null);
        }

    }


    /**
     * <p>Return an accessible method (that is, one that can be invoked via
     * reflection) that implements the specified Method.  If no such method
     * can be found, return <code>null</code>.</p>
     *
     * @param method The method that we wish to call
     */
    public static Method getAccessibleMethod(Method method) {

        // Make sure we have a method to check
        if (method == null) {
            return (null);
        }

        // If the requested method is not public we cannot call it
        if (!Modifier.isPublic(method.getModifiers())) {
            return (null);
        }

        // If the declaring class is public, we are done
        Class clazz = method.getDeclaringClass();
        if (Modifier.isPublic(clazz.getModifiers())) {
            return (method);
        }

        // Check the implemented interfaces and subinterfaces
        String methodName = method.getName();
        Class[] parameterTypes = method.getParameterTypes();
        method =
            getAccessibleMethodFromInterfaceNest(clazz,
                                                 method.getName(),
                                                 method.getParameterTypes());
        return (method);

    }


    // -------------------------------------------------------- Private Methods

    /**
     * <p>Return an accessible method (that is, one that can be invoked via
     * reflection) that implements the specified method, by scanning through
     * all implemented interfaces and subinterfaces.  If no such method
     * can be found, return <code>null</code>.</p>
     *
     * <p> There isn't any good reason why this method must be private.
     * It is because there doesn't seem any reason why other classes should
     * call this rather than the higher level methods.</p>
     *
     * @param clazz Parent class for the interfaces to be checked
     * @param methodName Method name of the method we wish to call
     * @param parameterTypes The parameter type signatures
     */
    private static Method getAccessibleMethodFromInterfaceNest
        (Class clazz, String methodName, Class parameterTypes[]) {

        Method method = null;

        // Search up the superclass chain
        for ( ; clazz != null; clazz = clazz.getSuperclass()) {

            // Check the implemented interfaces of the parent class
            Class interfaces[] = clazz.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {

                // Is this interface public?
                if (!Modifier.isPublic(interfaces[i].getModifiers()))
                    continue;

                // Does the method exist on this interface?
                try {
                    method = interfaces[i].getDeclaredMethod(methodName,
                                                             parameterTypes);
                } catch (NoSuchMethodException e) {
                    ;
                }
                if (method != null)
                    break;

                // Recursively check our parent interfaces
                method =
                    getAccessibleMethodFromInterfaceNest(interfaces[i],
                                                         methodName,
                                                         parameterTypes);
                if (method != null)
                    break;

            }

        }

        // If we found a method return it
        if (method != null)
            return (method);

        // We did not find anything
        return (null);

    }


}
