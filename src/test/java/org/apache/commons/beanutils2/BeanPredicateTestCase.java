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


import java.util.function.Predicate;

import junit.framework.TestCase;

/**
 * Unit test for {@link BeanPredicate}
 *
 */
public class BeanPredicateTestCase extends TestCase {

    public BeanPredicateTestCase(final String name) {
        super(name);
    }

    public void testEqual() {
    	Predicate<String> p = (s) -> s.equals("foo");
        final BeanPredicate predicate = new BeanPredicate("stringProperty", p);
        assertTrue(predicate.evaluate(new TestBean("foo")));
        assertTrue(!predicate.evaluate(new TestBean("bar")));
    }

    public void testNotEqual() {
    	Predicate<String> p = (s) -> !s.equals("foo");
        final BeanPredicate predicate = new BeanPredicate("stringProperty", p);
        assertTrue(!predicate.evaluate(new TestBean("foo")));
        assertTrue(predicate.evaluate(new TestBean("bar")));
    }

    public void testInstanceOf() {
    	Predicate<String> p = (s) -> (s instanceof String);
        final BeanPredicate predicate = new BeanPredicate("stringProperty",p);
        assertTrue(predicate.evaluate(new TestBean("foo")));
        assertTrue(predicate.evaluate(new TestBean("bar")));
    }

    public void testNull() {
    	Predicate<String> p = (s) -> s == null;
        final BeanPredicate predicate = new BeanPredicate("stringProperty", p);
        final String nullString = null;
        assertTrue(predicate.evaluate(new TestBean(nullString)));
        assertTrue(!predicate.evaluate(new TestBean("bar")));
    }

}
