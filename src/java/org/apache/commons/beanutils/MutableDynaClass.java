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
 * <p>A specialized extension to <code>DynaClass</code> that allows properties
 * to be added or removed dynamically.</p>
 *
 * <p><strong>WARNING</strong> - No guarantees that this will be in the final
 * APIs ... it's here primarily to preserve some concepts that were in the
 * original proposal for further discussion.</p>
 *
 * @author Craig McClanahan
 * @author Michael Smith
 * @author Paulo Gaspar
 * @version $Revision: 1.8 $ $Date: 2004/02/28 13:18:33 $
 */

public interface MutableDynaClass extends DynaClass {


    /**
     * Add a new dynamic property with no restrictions on data type,
     * readability, or writeability.
     *
     * @param name Name of the new dynamic property
     *
     * @exception IllegalArgumentException if name is null
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    public void add(String name);


    /**
     * Add a new dynamic property with the specified data type, but with
     * no restrictions on readability or writeability.
     *
     * @param name Name of the new dynamic property
     * @param type Data type of the new dynamic property (null for no
     *  restrictions)
     *
     * @exception IllegalArgumentException if name is null
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    public void add(String name, Class type);


    /**
     * Add a new dynamic property with the specified data type, readability,
     * and writeability.
     *
     * @param name Name of the new dynamic property
     * @param type Data type of the new dynamic property (null for no
     *  restrictions)
     * @param readable Set to <code>true</code> if this property value
     *  should be readable
     * @param writeable Set to <code>true</code> if this property value
     *  should be writeable
     *
     * @exception IllegalArgumentException if name is null
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    public void add(String name, Class type, boolean readable,
                    boolean writeable);


    /**
     * Is this DynaClass currently restricted, if so, no changes to the
     * existing registration of property names, data types, readability, or
     * writeability are allowed.
     */
    public boolean isRestricted();


    /**
     * Remove the specified dynamic property, and any associated data type,
     * readability, and writeability, from this dynamic class.
     * <strong>NOTE</strong> - This does <strong>NOT</strong> cause any
     * corresponding property values to be removed from DynaBean instances
     * associated with this DynaClass.
     *
     * @param name Name of the dynamic property to remove
     *
     * @exception IllegalArgumentException if name is null
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no properties can be removed
     */
    public void remove(String name);


    /**
     * Set the restricted state of this DynaClass to the specified value.
     *
     * @param restricted The new restricted state
     */
    public void setRestricted(boolean restricted);


}
