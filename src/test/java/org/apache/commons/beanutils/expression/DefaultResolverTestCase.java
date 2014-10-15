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
package org.apache.commons.beanutils.expression;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Junit Test for BasicResolver.
 *
 * @version $Id$
 */
public class DefaultResolverTestCase extends TestCase {

    private final DefaultResolver resolver = new DefaultResolver();

    // Simple Properties Test Data
    private final String[] validProperties = new String[] {null, "", "a", "bc", "def", "g.h", "ij.k", "lm.no", "pqr.stu"};
    private final String[] validNames      = new String[] {null, "", "a", "bc", "def", "g",   "ij",   "lm",    "pqr"};

    // Indexed Properties Test Data
    private final String[] validIndexProperties = new String[] {"a[1]", "b[12]", "cd[3]", "ef[45]", "ghi[6]", "jkl[789]", };
    private final String[] validIndexNames      = new String[] {"a",    "b",     "cd",    "ef",     "ghi",    "jkl"};
    private final int[]    validIndexValues     = new int[]    {1,      12,      3,       45,       6,        789};

    // Mapped Properties Test Data
    private final String[] validMapProperties = new String[] {"a(b)", "c(de)", "fg(h)", "ij(kl)", "mno(pqr.s)", "tuv(wx).yz[1]"};
    private final String[] validMapNames      = new String[] {"a",    "c",     "fg",    "ij",     "mno",        "tuv"};
    private final String[] validMapKeys       = new String[] {"b",    "de",    "h",     "kl",     "pqr.s",      "wx"};

    private final String[] nextExpressions   = new String[] {"a", "bc", "d.e", "fg.h", "ij.kl", "m(12)", "no(3.4)", "pq(r).s", "t[12]", "uv[34].wx"};
    private final String[] nextProperties    = new String[] {"a", "bc", "d",   "fg",   "ij",    "m(12)", "no(3.4)", "pq(r)",   "t[12]", "uv[34]"};
    private final String[] removeProperties  = new String[] {null, null, "e",  "h",    "kl",    null,    null,      "s",       null,    "wx"};

    /**
     * Construct a DefaultResolver Test Case.
     * @param name The name of the test
     */
    public DefaultResolverTestCase(final String name) {
        super(name);
    }

    // ------------------------------------------------------------------------

    /**
     * Create Test Suite
     * @return test suite
     */
    public static TestSuite suite() {
        return new TestSuite(DefaultResolverTestCase.class);
    }

    /**
     * Set Up
     */
    @Override
    protected void setUp() {
    }

    /**
     * Tear Down
     */
    @Override
    protected void tearDown() {
    }

    // ------------------------------------------------------------------------

    /**
     * Test getIndex() method.
     */
    public void testGetIndex() {
        String label = null;

        // Simple Properties (expect -1)
        for (int i = 0; i < validProperties.length; i++) {
            try {
                label = "Simple " + label(validProperties[i], i);
                assertEquals(label, -1, resolver.getIndex(validProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }

        // Indexed Properties (expect correct index value)
        for (int i = 0; i < validIndexProperties.length; i++) {
            try {
                label = "Indexed " + label(validIndexProperties[i], i);
                assertEquals(label, validIndexValues[i], resolver.getIndex(validIndexProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }

        // Mapped Properties (expect -1)
        for (int i = 0; i < validMapProperties.length; i++) {
            try {
                label = "Mapped " + label(validMapProperties[i], i);
                assertEquals(label, -1, resolver.getIndex(validMapProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }

        // Missing Index Value
        label = "Missing Index";
        try {
            final int index  = resolver.getIndex("foo[]");
            fail(label + " expected IllegalArgumentException: " + index);
        } catch (final IllegalArgumentException e) {
            assertEquals(label + " Error Message", "No Index Value", e.getMessage());
        } catch (final Throwable t) {
            fail(label + " expected IllegalArgumentException: " + t);
        }

        // Malformed
        label = "Malformed";
        try {
            final int index  = resolver.getIndex("foo[12");
            fail(label + " expected IllegalArgumentException: " + index);
        } catch (final IllegalArgumentException e) {
            assertEquals(label + " Error Message", "Missing End Delimiter", e.getMessage());
        } catch (final Throwable t) {
            fail(label + " expected IllegalArgumentException: " + t);
        }

        // Non-numeric
        label = "Malformed";
        try {
            final int index  = resolver.getIndex("foo[BAR]");
            fail(label + " expected IllegalArgumentException: " + index);
        } catch (final IllegalArgumentException e) {
            assertEquals(label + " Error Message", "Invalid index value 'BAR'", e.getMessage());
        } catch (final Throwable t) {
            fail(label + " expected IllegalArgumentException: " + t);
        }
    }

    /**
     * Test getMapKey() method.
     */
    public void testGetMapKey() {
        String label = null;

        // Simple Properties (expect null)
        for (int i = 0; i < validProperties.length; i++) {
            try {
                label = "Simple " + label(validProperties[i], i);
                assertEquals(label, null, resolver.getKey(validProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }

        // Indexed Properties (expect null)
        for (int i = 0; i < validIndexProperties.length; i++) {
            try {
                label = "Indexed " + label(validIndexProperties[i], i);
                assertEquals(label, null, resolver.getKey(validIndexProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }

        // Mapped Properties (expect correct map key)
        for (int i = 0; i < validMapProperties.length; i++) {
            try {
                label = "Mapped " + label(validMapProperties[i], i);
                assertEquals(label, validMapKeys[i], resolver.getKey(validMapProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }

        // Malformed
        label = "Malformed";
        try {
            final String key  = resolver.getKey("foo(bar");
            fail(label + " expected IllegalArgumentException: " + key);
        } catch (final IllegalArgumentException e) {
            assertEquals(label + " Error Message", "Missing End Delimiter", e.getMessage());
        } catch (final Throwable t) {
            fail(label + " expected IllegalArgumentException: " + t);
        }
 }

    /**
     * Test isIndexed() method.
     */
    public void testIsIndexed() {
        String label = null;

        // Simple Properties (expect -1)
        for (int i = 0; i < validProperties.length; i++) {
            try {
                label = "Simple " + label(validProperties[i], i);
                assertFalse(label, resolver.isIndexed(validProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }

        // Indexed Properties (expect correct index value)
        for (int i = 0; i < validIndexProperties.length; i++) {
            try {
                label = "Indexed " + label(validIndexProperties[i], i);
                assertTrue(label, resolver.isIndexed(validIndexProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }

        // Mapped Properties (expect -1)
        for (int i = 0; i < validMapProperties.length; i++) {
            try {
                label = "Mapped " + label(validMapProperties[i], i);
                assertFalse(label, resolver.isIndexed(validMapProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }
    }

    /**
     * Test isMapped() method.
     */
    public void testIsMapped() {
        String label = null;

        // Simple Properties (expect null)
        for (int i = 0; i < validProperties.length; i++) {
            try {
                label = "Simple " + label(validProperties[i], i);
                assertFalse(label, resolver.isMapped(validProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }

        // Indexed Properties (expect null)
        for (int i = 0; i < validIndexProperties.length; i++) {
            try {
                label = "Indexed " + label(validIndexProperties[i], i);
                assertFalse(label, resolver.isMapped(validIndexProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }

        // Mapped Properties (expect correct map key)
        for (int i = 0; i < validMapProperties.length; i++) {
            try {
                label = "Mapped " + label(validMapProperties[i], i);
                assertTrue(label, resolver.isMapped(validMapProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }
    }

    /**
     * Test getName() method.
     */
    public void testGetName() {
        String label = null;

        // Simple Properties
        for (int i = 0; i < validProperties.length; i++) {
            try {
                label = "Simple " + label(validProperties[i], i);
                assertEquals(label, validNames[i], resolver.getProperty(validProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }

        // Indexed Properties
        for (int i = 0; i < validIndexProperties.length; i++) {
            try {
                label = "Indexed " + label(validIndexProperties[i], i);
                assertEquals(label, validIndexNames[i], resolver.getProperty(validIndexProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }

        // Mapped Properties
        for (int i = 0; i < validMapProperties.length; i++) {
            try {
                label = "Mapped " + label(validMapProperties[i], i);
                assertEquals(label, validMapNames[i], resolver.getProperty(validMapProperties[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }
    }

    /**
     * Test next() method.
     */
    public void testNext() {
        String label = null;
        for (int i = 0; i < nextExpressions.length; i++) {
            try {
                label = label(nextExpressions[i], i);
                assertEquals(label, nextProperties[i], resolver.next(nextExpressions[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }
    }

    /**
     * Test remove() method.
     */
    public void testRemove() {
        String label = null;
        for (int i = 0; i < nextExpressions.length; i++) {
            try {
                label = label(nextExpressions[i], i);
                assertEquals(label, removeProperties[i], resolver.remove(nextExpressions[i]));
            } catch (final Throwable t) {
                fail(label + " threw " + t);
            }
        }
    }

    private String label(final String expression, final int i) {
        return "Expression[" + i + "]=\"" + expression + "\"";
    }
}
