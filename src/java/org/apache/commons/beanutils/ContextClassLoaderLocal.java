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