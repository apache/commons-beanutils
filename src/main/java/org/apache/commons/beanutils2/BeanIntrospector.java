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
package org.apache.commons.beanutils2;

import java.beans.IntrospectionException;

/**
 * <p>
 * Definition of an interface for components that can perform introspection on
 * bean classes.
 * </p>
 * <p>
 * Before {@link PropertyUtils} can be used for interaction with a specific Java
 * class, the class's properties have to be determined. This is called
 * <em>introspection</em> and is initiated automatically on demand.
 * {@code PropertyUtils} does not perform introspection on its own, but
 * delegates this task to one or more objects implementing this interface. This
 * makes it possible to customize introspection which may be useful for certain
 * code bases using non-standard conventions for accessing properties.
 * </p>
 *
 * @since 1.9
 */
public interface BeanIntrospector {
    /**
     * Performs introspection on a Java class. The current class to be inspected
     * can be queried from the passed in {@code IntrospectionContext}
     * object. A typical implementation has to obtain this class, determine its
     * properties according to the rules it implements, and add them to the
     * passed in context object.
     *
     * @param icontext the context object for interaction with the initiator of
     *        the introspection request
     * @throws IntrospectionException if an error occurs during introspection
     */
    void introspect(IntrospectionContext icontext)
            throws IntrospectionException;
}
