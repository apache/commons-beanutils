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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.junit.jupiter.api.Test;

class DynaPropertySubclassCompatibilityTest {

    private static final class ExistingProperty extends DynaProperty {
        private static final long serialVersionUID = 31L;
        private final int extraState;

        private ExistingProperty(final int extraState) {
            super("existing", Map.class, String.class);
            this.extraState = extraState;
        }

        public boolean isLegacyStream() {
            return true;
        }
    }

    @Test
    void testSubclassLegacyStreamMethodDoesNotControlDeserialization() throws Exception {
        final ExistingProperty original = new ExistingProperty(29);
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(original);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            final ExistingProperty copy = assertInstanceOf(ExistingProperty.class, input.readObject());
            assertNotSame(original, copy);
            assertEquals(original, copy);
            assertEquals(29, copy.extraState);
            assertTrue(copy.isLegacyStream());
        }
    }
}
