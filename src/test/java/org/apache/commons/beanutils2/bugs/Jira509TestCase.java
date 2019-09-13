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
package org.apache.commons.beanutils2.bugs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils2.WrapDynaClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests Jira issue# BEANUTILS-509.
 *
 * The bug causes an infinite loop in WeakHashMap.get which was not accessed in a thread safe manner. This originates
 * with WrapDynaClass.createDynaClass().
 */
@Ignore("https://issues.apache.org/jira/browse/BEANUTILS-509.")
public class Jira509TestCase {

    protected int random(final int max) {
        return (int) (Math.random() * 100_000) % max;
    }

    /**
     * The bug makes the {@link WrapDynaClass#createDynaClass} method run in an infinite loop and acquire locks. The
     * test case adds a timeout. The test case may pass even without this fix because this is a rare scenario.
     */
    @Test(timeout = 60_000)
    public void test_concurrent() throws InterruptedException {
        final List<Class<?>> classList = Arrays.asList(Map.class, HashMap.class, Collections.class, Arrays.class,
                Collection.class, Set.class, ArrayList.class, List.class, HashSet.class);

        // All daemon threads.
        final ExecutorService executor = Executors.newFixedThreadPool(100, r -> {
            final Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });

        try {
            // Loop _may_ hang without fix.
            for (int i = 1; i < 10_000_000; i++) {
                executor.submit(new Runnable() {
                    Class<?> clazz = classList.get(random(classList.size()));

                    @Override
                    public void run() {
                        final WrapDynaClass w = WrapDynaClass.createDynaClass(clazz);
                        assert w != null;
                    }

                });
            }
        } finally {
            executor.shutdown();
            executor.awaitTermination(3500, TimeUnit.MILLISECONDS);

        }
    }

}