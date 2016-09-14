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
package org.apache.commons.beanutils.bugs;

import static org.junit.Assert.assertEquals;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.junit.Test;

/** 
 * Test setting indexed properties on dynabeans
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-493">BEANUTILS-493</a>
 */

public class Jira493TestCase {
	
	@Test
	public void testIndexedProperties() throws Exception {
		LazyDynaBean lazyDynaBean = new LazyDynaBean();
		BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
		beanUtilsBean.setProperty(lazyDynaBean, "x[0]", "x1");
		beanUtilsBean.setProperty(lazyDynaBean, "x[1]", "x2");
		Object x = lazyDynaBean.get("x");
		assertEquals("[x1, x2]", x.toString());
	}
	
}