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

/**
 * <p>General purpose data type converter that can be registered and used
 * within the BeanUtils package to manage the conversion of objects from
 * one type to another.</p>
 *
 * <p>Converter subclasses bundled with the BeanUtils library are required
 * to be thread-safe, as users of the library may call conversion methods
 * from more than one thread simultaneously.</p>
 *
 * <p>Custom converter subclasses created by users of the library can be
 * non-thread-safe if the application using them is single-threaded. However
 * it is recommended that they be written in a thread-safe manner anyway.</p>
 *
 * @since 1.3
 */
public interface Converter {

    /**
     * Convert the specified input object into an output object of the
     * specified type.
     *
     * @param <T> the desired result type
     * @param type Data type to which this value should be converted
     * @param value The input value to be converted
     * @return The converted value
     *
     * @throws ConversionException if conversion cannot be performed
     *  successfully
     */
    <T> T convert(Class<T> type, Object value);
}
