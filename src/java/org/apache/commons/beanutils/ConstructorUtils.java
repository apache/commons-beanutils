/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * @version $Revision: 1.2 $ $Date: 2003/01/15 21:59:38 $
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @author Rey François
 * @author Gregor Raýman
 * @author Jan Sorensen
 * @author Robert Burrell Donkin
 * @author Rodney Waldhoff
 */
public class ConstructorUtils {

    // --------------------------------------------------------- Private Members
    /** An empty class array */
    private static final Class[] emptyClassArray = new Class[0];
    /** An empty object array */
    private static final Object[] emptyObjectArray = new Object[0];

    // --------------------------------------------------------- Public Methods

    public static Object invokeConstructor(Class klass, Object arg)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        Object[] args = { arg };
        return invokeConstructor(klass, args);

    }

    public static Object invokeConstructor(Class klass, Object[] args)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        if (null == args) {
            args = emptyObjectArray;
        }
        int arguments = args.length;
        Class parameterTypes[] = new Class[arguments];
        for (int i = 0; i < arguments; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        return invokeConstructor(klass, args, parameterTypes);

    }

    public static Object invokeConstructor(
        Class klass,
        Object[] args,
        Class[] parameterTypes)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        if (parameterTypes == null) {
            parameterTypes = emptyClassArray;
        }
        if (args == null) {
            args = emptyObjectArray;
        }

        Constructor ctor =
            getMatchingAccessibleConstructor(klass, parameterTypes);
        if (null == ctor) {
            throw new NoSuchMethodException(
                "No such accessible constructor on object: " + klass.getName());
        }
        return ctor.newInstance(args);
    }

    public static Object invokeExactConstructor(Class klass, Object arg)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        Object[] args = { arg };
        return invokeExactConstructor(klass, args);

    }
    public static Object invokeExactConstructor(Class klass, Object[] args)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        if (null == args) {
            args = emptyObjectArray;
        }
        int arguments = args.length;
        Class parameterTypes[] = new Class[arguments];
        for (int i = 0; i < arguments; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        return invokeExactConstructor(klass, args, parameterTypes);

    }

    public static Object invokeExactConstructor(
        Class klass,
        Object[] args,
        Class[] parameterTypes)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        if (args == null) {
            args = emptyObjectArray;
        }

        if (parameterTypes == null) {
            parameterTypes = emptyClassArray;
        }

        Constructor ctor = getAccessibleConstructor(klass, parameterTypes);
        if (null == ctor) {
            throw new NoSuchMethodException(
                "No such accessible constructor on object: " + klass.getName());
        }
        return ctor.newInstance(args);

    }

    public static Constructor getAccessibleConstructor(
        Class klass,
        Class parameterType) {

        Class[] parameterTypes = { parameterType };
        return getAccessibleConstructor(klass, parameterTypes);

    }

    public static Constructor getAccessibleConstructor(
        Class klass,
        Class[] parameterTypes) {

        try {
            return getAccessibleConstructor(
                klass.getConstructor(parameterTypes));
        } catch (NoSuchMethodException e) {
            return (null);
        }

    }

    public static Constructor getAccessibleConstructor(Constructor ctor) {

        // Make sure we have a method to check
        if (ctor == null) {
            return (null);
        }

        // If the requested method is not public we cannot call it
        if (!Modifier.isPublic(ctor.getModifiers())) {
            return (null);
        }

        // If the declaring class is public, we are done
        Class clazz = ctor.getDeclaringClass();
        if (Modifier.isPublic(clazz.getModifiers())) {
            return (ctor);
        }

        // what else can we do?
        return null;

    }

    // -------------------------------------------------------- Private Methods

    private static Constructor getMatchingAccessibleConstructor(
        Class clazz,
        Class[] parameterTypes) {
        // see if we can find the method directly
        // most of the time this works and it's much faster
        try {
            Constructor ctor = clazz.getConstructor(parameterTypes);
            try {
                //
                // XXX Default access superclass workaround
                //
                // When a public class has a default access superclass
                // with public methods, these methods are accessible.
                // Calling them from compiled code works fine.
                //
                // Unfortunately, using reflection to invoke these methods
                // seems to (wrongly) to prevent access even when the method
                // modifer is public.
                //
                // The following workaround solves the problem but will only
                // work from sufficiently privilages code. 
                //
                // Better workarounds would be greatfully accepted.
                //
                ctor.setAccessible(true);
            } catch (SecurityException se) {}
            return ctor;

        } catch (NoSuchMethodException e) { /* SWALLOW */
        }

        // search through all methods 
        int paramSize = parameterTypes.length;
        Constructor[] ctors = clazz.getConstructors();
        for (int i = 0, size = ctors.length; i < size; i++) {
            // compare parameters
            Class[] ctorParams = ctors[i].getParameterTypes();
            int ctorParamSize = ctorParams.length;
            if (ctorParamSize == paramSize) {
                boolean match = true;
                for (int n = 0; n < ctorParamSize; n++) {
                    if (!MethodUtils
                        .isAssignmentCompatible(
                            ctorParams[n],
                            parameterTypes[n])) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    // get accessible version of method
                    Constructor ctor = getAccessibleConstructor(ctors[i]);
                    if (ctor != null) {
                        try {
                            ctor.setAccessible(true);
                        } catch (SecurityException se) {}
                        return ctor;
                    }
                }
            }
        }

        return null;
    }

}
