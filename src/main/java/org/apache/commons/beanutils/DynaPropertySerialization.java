/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils;

import java.io.Serializable;

/**
 * Distinguishes the historical {@link DynaProperty} serialized form before its data is read.
 *
 * <p>Historical streams do not contain this superclass, so deserialization invokes
 * {@link #readObjectNoData()} before restoring {@code DynaProperty}. New streams include
 * this empty serializable superclass and leave the transient flag false. This permits
 * either field ordering to be read without consuming or rewinding the caller's stream.</p>
 *
 * <p>This class's name, serial version UID, empty persistent state and position in the
 * hierarchy are part of the serialized format and must be retained in future releases.</p>
 *
 * @since 1.11.1
 */
class DynaPropertySerialization implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient boolean legacyStream;

    final boolean isLegacyStream() {
        return legacyStream;
    }

    private void readObjectNoData() {
        legacyStream = true;
    }
}
