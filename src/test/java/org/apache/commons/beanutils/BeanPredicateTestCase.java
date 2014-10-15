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

import junit.framework.TestCase;

import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.collections.functors.InstanceofPredicate;
import org.apache.commons.collections.functors.NotPredicate;
import org.apache.commons.collections.functors.NullPredicate;

/**
 * @version $Id$
 */
public class BeanPredicateTestCase extends TestCase {

    public BeanPredicateTestCase(final String name) {
        super(name);
    }

    public void testEqual() {
        final BeanPredicate predicate =
            new BeanPredicate("stringProperty",new EqualPredicate("foo"));
        assertTrue(predicate.evaluate(new TestBean("foo")));
        assertTrue(!predicate.evaluate(new TestBean("bar")));
    }

    public void testNotEqual() {
        final BeanPredicate predicate =
            new BeanPredicate("stringProperty",new NotPredicate( new EqualPredicate("foo")));
        assertTrue(!predicate.evaluate(new TestBean("foo")));
        assertTrue(predicate.evaluate(new TestBean("bar")));
    }

    public void testInstanceOf() {
        final BeanPredicate predicate =
            new BeanPredicate("stringProperty",new InstanceofPredicate( String.class ));
        assertTrue(predicate.evaluate(new TestBean("foo")));
        assertTrue(predicate.evaluate(new TestBean("bar")));
    }

    public void testNull() {
        final BeanPredicate predicate =
            new BeanPredicate("stringProperty", NullPredicate.INSTANCE);
        final String nullString = null;
        assertTrue(predicate.evaluate(new TestBean(nullString)));
        assertTrue(!predicate.evaluate(new TestBean("bar")));
    }

}
