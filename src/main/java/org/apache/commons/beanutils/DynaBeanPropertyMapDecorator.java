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
package org.apache.commons.beanutils;

/**
 * <p>Decorates a {@link DynaBean} to provide <code>Map</code> behavior.</p>
 *
 * <p>The motivation for this implementation is to provide access to {@link DynaBean}
 *    properties in technologies that are unaware of BeanUtils and {@link DynaBean}s -
 *    such as the expression languages of JSTL and JSF.</p>
 *
 * <p>This can be achieved either by wrapping the {@link DynaBean} prior to
 *    providing it to the technology to process or by providing a <code>Map</code>
 *    accessor method on the DynaBean implementation:
 *    <pre><code>
 *         public Map&lt;String, Object&gt; getMap() {
 *             return new DynaBeanPropertyMapDecorator(this);
 *         }</code></pre>
 *   </ul>
 * </p>
 *
 * <p>This, for example, could be used in JSTL in the following way to access
 *    a DynaBean's <code>fooProperty</code>:
 *    <ul><li><code>${myDynaBean.<b>map</b>.fooProperty}</code></li></ul>
 * </p>
 *
 * <h3>Usage</h3>
 *
 * <p>To decorate a {@link DynaBean} simply instantiate this class with the
 *    target {@link DynaBean}:</p>
 *
 * <ul><li><code>Map&lt;String, Object&gt; fooMap = new DynaBeanPropertyMapDecorator(fooDynaBean);</code></li></ul>
 *
 * <p>The above example creates a <b><i>read only</i></b> <code>Map</code>.
 *    To create  a <code>Map</code> which can be modified, construct a
 *    <code>DynaBeanPropertyMapDecorator</code> with the <b><i>read only</i></b>
 *    attribute set to <code>false</code>:</p>
 *
 * <ul><li><code>Map&lt;String, Object&gt; fooMap =
 *   new DynaBeanPropertyMapDecorator(fooDynaBean, false);</code></li></ul>
 *
 * <h3>Limitations</h3>
 * <p>In this implementation the <code>entrySet()</code>, <code>keySet()</code>
 *    and <code>values()</code> methods create an <b><i>unmodifiable</i></b>
 *    <code>Set</code> and it does not support the Map's <code>clear()</code>
 *    and <code>remove()</code> operations.</p>
 *
 * @since BeanUtils 1.9.0
 * @version $Id$
 */
public class DynaBeanPropertyMapDecorator extends BaseDynaBeanMapDecorator<String> {
    /**
     * Construct a Map for the specified {@link DynaBean}.
     *
     * @param dynaBean The dyna bean being decorated
     * @param readOnly <code>true</code> if the Map is read only
     * otherwise <code>false</code>
     * @throws IllegalArgumentException if the {@link DynaBean} is null.
     */
    public DynaBeanPropertyMapDecorator(final DynaBean dynaBean, final boolean readOnly) {
        super(dynaBean, readOnly);
    }

    /**
     * Constructs a read only Map for the specified
     * {@link DynaBean}.
     *
     * @param dynaBean The dyna bean being decorated
     * @throws IllegalArgumentException if the {@link DynaBean} is null.
     */
    public DynaBeanPropertyMapDecorator(final DynaBean dynaBean) {
        super(dynaBean);
    }

    @Override
    protected String convertKey(final String propertyName) {
        return propertyName;
    }
}
