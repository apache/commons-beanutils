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
 * Thrown to indicate that the <em>Bean Access Language</em> cannot execute query
 * against given bean since a nested bean referenced is null.
 *
 * @author Robert Burrell Donkin
 * @since 1.7
 */

public class NestedNullException extends BeanAccessLanguageException {
    
    // --------------------------------------------------------- Constuctors
    
    /** 
     * Constructs a <code>NestedNullException</code> without a detail message.
     */
    public NestedNullException() {
        super();
    }
    
    /**
     * Constructs a <code>NestedNullException</code> without a detail message.
     * 
     * @param message the detail message explaining this exception
     */
    public NestedNullException(String message) {
        super(message);
    }
}
