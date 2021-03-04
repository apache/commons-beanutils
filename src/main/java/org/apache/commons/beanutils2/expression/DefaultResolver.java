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

/**
 * Default Property Name Expression {@link Resolver} Implementation.
 * <p>
 * This class assists in resolving property names in the following five formats,
 * with the layout of an identifying String in parentheses:
 * <ul>
 * <li><strong>Simple ({@code name})</strong> - The specified
 *     {@code name} identifies an individual property of a particular
 *     JavaBean.  The name of the actual getter or setter method to be used
 *     is determined using standard JavaBeans introspection, so that (unless
 *     overridden by a {@code BeanInfo} class, a property named "xyz"
 *     will have a getter method named {@code getXyz()} or (for boolean
 *     properties only) {@code isXyz()}, and a setter method named
 *     {@code setXyz()}.</li>
 * <li><strong>Nested ({@code name1.name2.name3})</strong> The first
 *     name element is used to select a property getter, as for simple
 *     references above.  The object returned for this property is then
 *     consulted, using the same approach, for a property getter for a
 *     property named {@code name2}, and so on.  The property value that
 *     is ultimately retrieved or modified is the one identified by the
 *     last name element.</li>
 * <li><strong>Indexed ({@code name[index]})</strong> - The underlying
 *     property value is assumed to be an array, or this JavaBean is assumed
 *     to have indexed property getter and setter methods.  The appropriate
 *     (zero-relative) entry in the array is selected.  {@code List}
 *     objects are now also supported for read/write.  You simply need to define
 *     a getter that returns the {@code List}</li>
 * <li><strong>Mapped ({@code name(key)})</strong> - The JavaBean
 *     is assumed to have an property getter and setter methods with an
 *     additional attribute of type {@code java.lang.String}.</li>
 * <li><strong>Combined ({@code name1.name2[index].name3(key)})</strong> -
 *     Combining mapped, nested, and indexed references is also
 *     supported.</li>
 * </ul>
 *
 * @since 1.8.0
 */
public class DefaultResolver implements Resolver {

    private static final char NESTED        = '.';
    private static final char MAPPED_START  = '(';
    private static final char MAPPED_END    = ')';
    private static final char INDEXED_START = '[';
    private static final char INDEXED_END   = ']';

    /**
     * Default Constructor.
     */
    public DefaultResolver() {
    }

    /**
     * Gets the index value from the property expression or -1.
     *
     * @param expression The property expression
     * @return The index value or -1 if the property is not indexed
     * @throws IllegalArgumentException If the indexed property is illegally
     * formed or has an invalid (non-numeric) value.
     */
    @Override
    public int getIndex(final String expression) {
        if (expression == null || expression.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < expression.length(); i++) {
            final char c = expression.charAt(i);
            if (c == NESTED || c == MAPPED_START) {
                return -1;
            }
            if (c == INDEXED_START) {
                final int end = expression.indexOf(INDEXED_END, i);
                if (end < 0) {
                    throw new IllegalArgumentException("Missing End Delimiter");
                }
                final String value = expression.substring(i + 1, end);
                if (value.isEmpty()) {
                    throw new IllegalArgumentException("No Index Value");
                }
                int index = 0;
                try {
                    index = Integer.parseInt(value, 10);
                } catch (final Exception e) {
                    throw new IllegalArgumentException("Invalid index value '"
                            + value + "'");
                }
                return index;
            }
        }
        return -1;
    }

    /**
     * Gets the map key from the property expression or {@code null}.
     *
     * @param expression The property expression
     * @return The index value
     * @throws IllegalArgumentException If the mapped property is illegally formed.
     */
    @Override
    public String getKey(final String expression) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }
        for (int i = 0; i < expression.length(); i++) {
            final char c = expression.charAt(i);
            if (c == NESTED || c == INDEXED_START) {
                return null;
            }
            if (c == MAPPED_START) {
                final int end = expression.indexOf(MAPPED_END, i);
                if (end < 0) {
                    throw new IllegalArgumentException("Missing End Delimiter");
                }
                return expression.substring(i + 1, end);
            }
        }
        return null;
    }

    /**
     * Gets the property name from the property expression.
     *
     * @param expression The property expression
     * @return The property name
     */
    @Override
    public String getProperty(final String expression) {
        if (expression == null || expression.isEmpty()) {
            return expression;
        }
        for (int i = 0; i < expression.length(); i++) {
            final char c = expression.charAt(i);
            if ((c == NESTED) || (c == MAPPED_START || c == INDEXED_START)) {
                return expression.substring(0, i);
            }
        }
        return expression;
    }

    /**
     * Indicates whether or not the expression
     * contains nested property expressions or not.
     *
     * @param expression The property expression
     * @return The next property expression
     */
    @Override
    public boolean hasNested(final String expression) {
        if (expression == null || expression.isEmpty()) {
            return false;
        }
        return remove(expression) != null;
    }

    /**
     * Indicate whether the expression is for an indexed property or not.
     *
     * @param expression The property expression
     * @return {@code true} if the expression is indexed,
     *  otherwise {@code false}
     */
    @Override
    public boolean isIndexed(final String expression) {
        if (expression == null || expression.isEmpty()) {
            return false;
        }
        for (int i = 0; i < expression.length(); i++) {
            final char c = expression.charAt(i);
            if (c == NESTED || c == MAPPED_START) {
                return false;
            }
            if (c == INDEXED_START) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicate whether the expression is for a mapped property or not.
     *
     * @param expression The property expression
     * @return {@code true} if the expression is mapped,
     *  otherwise {@code false}
     */
    @Override
    public boolean isMapped(final String expression) {
        if (expression == null || expression.isEmpty()) {
            return false;
        }
        for (int i = 0; i < expression.length(); i++) {
            final char c = expression.charAt(i);
            if (c == NESTED || c == INDEXED_START) {
                return false;
            }
            if (c == MAPPED_START) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract the next property expression from the
     * current expression.
     *
     * @param expression The property expression
     * @return The next property expression
     */
    @Override
    public String next(final String expression) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }
        boolean indexed = false;
        boolean mapped  = false;
        for (int i = 0; i < expression.length(); i++) {
            final char c = expression.charAt(i);
            if (indexed) {
                if (c == INDEXED_END) {
                    return expression.substring(0, i + 1);
                }
            } else if (mapped) {
                if (c == MAPPED_END) {
                    return expression.substring(0, i + 1);
                }
            } else {
                switch (c) {
                case NESTED:
                    return expression.substring(0, i);
                case MAPPED_START:
                    mapped = true;
                    break;
                case INDEXED_START:
                    indexed = true;
                    break;
                default:
                    break;
                }
            }
        }
        return expression;
    }

    /**
     * Remove the last property expression from the
     * current expression.
     *
     * @param expression The property expression
     * @return The new expression value, with first property
     * expression removed - null if there are no more expressions
     */
    @Override
    public String remove(final String expression) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }
        final String property = next(expression);
        if (expression.length() == property.length()) {
            return null;
        }
        int start = property.length();
        if (expression.charAt(start) == NESTED) {
            start++;
        }
        return expression.substring(start);
    }
}
