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
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link WrapDynaClass}.
 */
class WrapDynaClassTest {

    /**
     * Simple bean used as a cache key.
     */
    public static final class ConcurrentBean {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }
    }

    /**
     * The cache key is one bean class, so {@code createDynaClass} must hand back a single instance no matter how many threads race to populate the
     * per-classloader cache. With a plain {@code WeakHashMap} and an unsynchronized get/create/put sequence, concurrent callers could build and return
     * distinct instances for one key; this drives that race and fails if more than one instance escapes.
     * <p>
     * The cache holds its keys weakly and nothing else references them, so a GC that runs while a round is in flight may evict the freshly cached entry
     * and a late thread then builds a second instance without any race. Each round therefore only asserts when a GC canary shows no collection happened
     * during the round; rounds invalidated by a GC are skipped.
     */
    @Test
    void testConcurrentCreateDynaClassReturnsSameInstance() throws Exception {
        final int threads = 16;
        final int rounds = 200;
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            for (int r = 0; r < rounds; r++) {
                WrapDynaClass.clear();
                // Cleared only if a GC ran after this point, which is the only way a cache entry can disappear mid-round.
                final WeakReference<Object> gcCanary = new WeakReference<>(new Object());
                final CyclicBarrier barrier = new CyclicBarrier(threads);
                final Set<WrapDynaClass> results = Collections.newSetFromMap(new IdentityHashMap<>());
                final Future<?>[] futures = new Future<?>[threads];
                for (int t = 0; t < threads; t++) {
                    futures[t] = pool.submit(() -> {
                        barrier.await();
                        final WrapDynaClass wdc = WrapDynaClass.createDynaClass(ConcurrentBean.class);
                        synchronized (results) {
                            results.add(wdc);
                        }
                        return null;
                    });
                }
                for (final Future<?> f : futures) {
                    f.get();
                }
                if (results.size() > 1 && gcCanary.get() == null) {
                    continue;
                }
                assertEquals(1, results.size(), "createDynaClass returned more than one instance for one key");
            }
        } finally {
            pool.shutdownNow();
            pool.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    /**
     * The single-threaded cache contract: repeated calls for one bean class return the same instance until the cache is cleared. A GC between the two
     * calls may legitimately evict the weakly held cache entry, so such attempts are retried.
     */
    @Test
    void testCreateDynaClassIsCached() {
        WrapDynaClass.clear();
        WeakReference<Object> gcCanary;
        WrapDynaClass first;
        WrapDynaClass second;
        do {
            gcCanary = new WeakReference<>(new Object());
            first = WrapDynaClass.createDynaClass(ConcurrentBean.class);
            second = WrapDynaClass.createDynaClass(ConcurrentBean.class);
        } while (first != second && gcCanary.get() == null);
        assertSame(first, second);
        WrapDynaClass.clear();
    }
}
