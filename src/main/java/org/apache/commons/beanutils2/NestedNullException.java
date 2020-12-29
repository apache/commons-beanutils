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
 * Thrown to indicate that the <em>Bean Access Language</em> cannot execute query
 * against given bean since a nested bean referenced is null.
 *
 * @since 1.7
 */

public class NestedNullException extends BeanAccessLanguageException {

    private static final long serialVersionUID = 1L;



    /**
     * Constructs a {@code NestedNullException} without a detail message.
     */
    public NestedNullException() {
    }

    /**
     * Constructs a {@code NestedNullException} without a detail message.
     *
     * @param message the detail message explaining this exception
     */
    public NestedNullException(final String message) {
        super(message);
    }
}
