/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/ContextClassLoaderLocal.java,v 1.4 2003/12/22 21:26:47 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/12/22 21:26:47 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
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

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A value that is provided per (thread) context classloader.
 * Patterned after ThreadLocal. 
 * There is a separate value used when Thread.getContextClassLoader() is null.  
 * This mechanism provides isolation for web apps deployed in the same container. 
 * <strong>Note:</strong> A WeakHashMap bug in several 1.3 JVMs results in a memory leak
 * for those JVMs.
 * 
 * @see java.lang.Thread#getContextClassLoader  
 * @author Eric Pabst
 */
public class ContextClassLoaderLocal {
    private Map valueByClassLoader = new WeakHashMap();
    private boolean globalValueInitialized = false;
    private Object globalValue;

    public ContextClassLoaderLocal() {
        super();
    }

    /**
     * Returns the initial value for this ContextClassLoaderLocal
     * variable. This method will be called once per Context ClassLoader for
     * each ContextClassLoaderLocal, the first time it is accessed 
     * with get or set.  If the programmer desires ContextClassLoaderLocal variables
     * to be initialized to some value other than null, ContextClassLoaderLocal must
     * be subclassed, and this method overridden.  Typically, an anonymous
     * inner class will be used.  Typical implementations of initialValue
     * will call an appropriate constructor and return the newly constructed
     * object.
     *
     * @return a new Object to be used as an initial value for this ContextClassLoaderLocal
     */
    protected Object initialValue() {
        return null;
    }

    /** 
     * Gets the instance which provides the functionality for {@link BeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container. 
     * @return the object currently associated with the 
     */
    public synchronized Object get() {
        // synchronizing the whole method is a bit slower 
        // but guarentees no subtle threading problems, and there's no 
        // need to synchronize valueByClassLoader
        
        // make sure that the map is given a change to purge itself
        valueByClassLoader.isEmpty();
        try {
            
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                
                Object value = valueByClassLoader.get(contextClassLoader);
                if ((value == null) 
                && !valueByClassLoader.containsKey(contextClassLoader)) {
                    value = initialValue();
                    valueByClassLoader.put(contextClassLoader, value);
                }
                return value;
                
            }
            
        } catch (SecurityException e) { /* SWALLOW - should we log this? */ }
        
        // if none or exception, return the globalValue 
        if (!globalValueInitialized) {
            globalValue = initialValue();
            globalValueInitialized = true;
        }//else already set
        return globalValue;
    }

    /** 
     * Sets the value - a value is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container. 
     * 
     * @param value the object to be associated with the entrant thread's context classloader
     */
    public synchronized void set(Object value) {
        // synchronizing the whole method is a bit slower 
        // but guarentees no subtle threading problems
        
        // make sure that the map is given a change to purge itself
        valueByClassLoader.isEmpty();
        try {
            
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                valueByClassLoader.put(contextClassLoader, value);
                return;
            }
            
        } catch (SecurityException e) { /* SWALLOW - should we log this? */ }
        
        // if in doubt, set the global value
        globalValue = value;
        globalValueInitialized = true;
    }
    
    /** 
     * Unsets the value associated with the current thread's context classloader
     */
    public synchronized void unset() {    
        try {
        
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            unset(contextClassLoader);
            
        } catch (SecurityException e) { /* SWALLOW - should we log this? */ }
    }
    
    /** 
     * Unsets the value associated with the given classloader
     */
    public synchronized void unset(ClassLoader classLoader) {    
        valueByClassLoader.remove(classLoader);
    }    
}