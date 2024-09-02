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
package org.apache.commons.beanutils2.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Junit Test for BasicResolver.
 */
public class DefaultResolverTestCase {

    private final DefaultResolver resolver = new DefaultResolver();
    // Simple Properties Test Data
    private final String[] validProperties = { null, "", "a", "bc", "def", "g.h", "ij.k", "lm.no", "pqr.stu" };

    private final String[] validNames = { null, "", "a", "bc", "def", "g", "ij", "lm", "pqr" };
    // Indexed Properties Test Data
    private final String[] validIndexProperties = { "a[1]", "b[12]", "cd[3]", "ef[45]", "ghi[6]", "jkl[789]", };
    private final String[] validIndexNames = { "a", "b", "cd", "ef", "ghi", "jkl" };

    private final int[] validIndexValues = { 1, 12, 3, 45, 6, 789 };
    // Mapped Properties Test Data
    private final String[] validMapProperties = { "a(b)", "c(de)", "fg(h)", "ij(kl)", "mno(pqr.s)", "tuv(wx).yz[1]" };
    private final String[] validMapNames = { "a", "c", "fg", "ij", "mno", "tuv" };

    private final String[] validMapKeys = { "b", "de", "h", "kl", "pqr.s", "wx" };
    private final String[] nextExpressions = { "a", "bc", "d.e", "fg.h", "ij.kl", "m(12)", "no(3.4)", "pq(r).s", "t[12]", "uv[34].wx" };
    private final String[] nextProperties = { "a", "bc", "d", "fg", "ij", "m(12)", "no(3.4)", "pq(r)", "t[12]", "uv[34]" };

    private final String[] removeProperties = { null, null, "e", "h", "kl", null, null, "s", null, "wx" };

    private String label(final String expression, final int i) {
        return "Expression[" + i + "]=\"" + expression + "\"";
    }

    /**
     * Sets Up
     */
    @BeforeEach
    protected void setUp() {
    }

    /**
     * Tear Down
     */
    @AfterEach
    protected void tearDown() {
    }

    /**
     * Test getIndex() method.
     */
    @Test
    public void testGetIndex() throws Exception {
        String label = null;

        // Simple Properties (expect -1)
        for (int i = 0; i < validProperties.length; i++) {
            label = "Simple " + label(validProperties[i], i);
            assertEquals(-1, resolver.getIndex(validProperties[i]), label);
        }

        // Indexed Properties (expect correct index value)
        for (int i = 0; i < validIndexProperties.length; i++) {
            label = "Indexed " + label(validIndexProperties[i], i);
            assertEquals(validIndexValues[i], resolver.getIndex(validIndexProperties[i]), label);
        }

        // Mapped Properties (expect -1)
        for (int i = 0; i < validMapProperties.length; i++) {
            label = "Mapped " + label(validMapProperties[i], i);
            assertEquals(-1, resolver.getIndex(validMapProperties[i]), label);
        }

        // Missing Index Value
        label = "Missing Index";
        assertThrows(IllegalArgumentException.class, () -> resolver.getIndex("foo[]"));

        // Malformed
        label = "Malformed";
        assertThrows(IllegalArgumentException.class, () -> resolver.getIndex("foo[12"));

        // Non-numeric
        label = "Malformed";
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> resolver.getIndex("foo[BAR]"));
        assertEquals("Invalid index value 'BAR'", e.getMessage(), label + " Error Message");
    }

    /**
     * Test getMapKey() method.
     */
    @Test
    public void testGetMapKey() {
        String label = null;

        // Simple Properties (expect null)
        for (int i = 0; i < validProperties.length; i++) {
            label = "Simple " + label(validProperties[i], i);
            assertEquals(null, resolver.getKey(validProperties[i]), label);
        }

        // Indexed Properties (expect null)
        for (int i = 0; i < validIndexProperties.length; i++) {
            label = "Indexed " + label(validIndexProperties[i], i);
            assertEquals(null, resolver.getKey(validIndexProperties[i]), label);
        }

        // Mapped Properties (expect correct map key)
        for (int i = 0; i < validMapProperties.length; i++) {
            label = "Mapped " + label(validMapProperties[i], i);
            assertEquals(validMapKeys[i], resolver.getKey(validMapProperties[i]), label);
        }

        // Malformed
        label = "Malformed";
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> resolver.getKey("foo(bar"));
        assertEquals("Missing End Delimiter", e.getMessage(), label + " Error Message");
    }

    /**
     * Test getName() method.
     */
    @Test
    public void testGetName() {
        String label = null;

        // Simple Properties
        for (int i = 0; i < validProperties.length; i++) {
            label = "Simple " + label(validProperties[i], i);
            assertEquals(validNames[i], resolver.getProperty(validProperties[i]), label);
        }

        // Indexed Properties
        for (int i = 0; i < validIndexProperties.length; i++) {
            label = "Indexed " + label(validIndexProperties[i], i);
            assertEquals(validIndexNames[i], resolver.getProperty(validIndexProperties[i]), label);
        }

        // Mapped Properties
        for (int i = 0; i < validMapProperties.length; i++) {
            label = "Mapped " + label(validMapProperties[i], i);
            assertEquals(validMapNames[i], resolver.getProperty(validMapProperties[i]), label);
        }
    }

    /**
     * Test isIndexed() method.
     */
    @Test
    public void testIsIndexed() {
        String label = null;

        // Simple Properties (expect -1)
        for (int i = 0; i < validProperties.length; i++) {
            label = "Simple " + label(validProperties[i], i);
            assertFalse(resolver.isIndexed(validProperties[i]), label);
        }

        // Indexed Properties (expect correct index value)
        for (int i = 0; i < validIndexProperties.length; i++) {
            label = "Indexed " + label(validIndexProperties[i], i);
            assertTrue(resolver.isIndexed(validIndexProperties[i]), label);
        }

        // Mapped Properties (expect -1)
        for (int i = 0; i < validMapProperties.length; i++) {
            label = "Mapped " + label(validMapProperties[i], i);
            assertFalse(resolver.isIndexed(validMapProperties[i]), label);
        }
    }

    /**
     * Test isMapped() method.
     */
    @Test
    public void testIsMapped() {
        String label = null;

        // Simple Properties (expect null)
        for (int i = 0; i < validProperties.length; i++) {
            label = "Simple " + label(validProperties[i], i);
            assertFalse(resolver.isMapped(validProperties[i]), label);
        }

        // Indexed Properties (expect null)
        for (int i = 0; i < validIndexProperties.length; i++) {
            label = "Indexed " + label(validIndexProperties[i], i);
            assertFalse(resolver.isMapped(validIndexProperties[i]), label);
        }

        // Mapped Properties (expect correct map key)
        for (int i = 0; i < validMapProperties.length; i++) {
            label = "Mapped " + label(validMapProperties[i], i);
            assertTrue(resolver.isMapped(validMapProperties[i]), label);
        }
    }

    /**
     * Test next() method.
     */
    @Test
    public void testNext() {
        String label = null;
        for (int i = 0; i < nextExpressions.length; i++) {
            label = label(nextExpressions[i], i);
            assertEquals(nextProperties[i], resolver.next(nextExpressions[i]), label);
        }
    }

    /**
     * Test remove() method.
     */
    @Test
    public void testRemove() {
        String label = null;
        for (int i = 0; i < nextExpressions.length; i++) {
            label = label(nextExpressions[i], i);
            assertEquals(removeProperties[i], resolver.remove(nextExpressions[i]), label);
        }
    }
}
