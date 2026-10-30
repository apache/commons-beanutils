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

package org.apache.commons.beanutils2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.map.ConcurrentReferenceHashMap;
import org.apache.commons.collections4.map.ConcurrentReferenceHashMap.ReferenceType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Tests {@link WrapDynaClass}.
 */
@Isolated("Temporarily replaces the shared WrapDynaClass cache")
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

    private ContextClassLoaderLocal<Map<Object, WrapDynaClass>> classLoaderCache;
    private Map<Object, WrapDynaClass> originalCache;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void installStrongKeyCache() throws ReflectiveOperationException {
        final Field field = WrapDynaClass.class.getDeclaredField("CLASSLOADER_CACHE");
        field.setAccessible(true);
        classLoaderCache = (ContextClassLoaderLocal<Map<Object, WrapDynaClass>>) field.get(null);
        originalCache = classLoaderCache.get();
        assertInstanceOf(ConcurrentReferenceHashMap.class, originalCache, "Production cache must use the concurrent map tested here");
        // Keep the production map implementation, but prevent GC from evicting the temporary CacheKey objects while testing instance identity.
        classLoaderCache.set(ConcurrentReferenceHashMap.<Object, WrapDynaClass>builder().setKeyReferenceType(ReferenceType.STRONG).get());
    }

    @AfterEach
    void restoreCache() {
        if (originalCache != null) {
            classLoaderCache.set(originalCache);
        }
    }

    /**
     * Tests that concurrent callers receive the same instance when populating an empty cache.
     *
     * <p>
     * Strong keys isolate atomic initialization from garbage collection of the production cache's weak keys. Each round starts with an empty cache.
     * </p>
     */
    @Test
    void testConcurrentCreateDynaClassReturnsSameInstance() throws Exception {
        final int threads = 16;
        final int rounds = 200;
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            for (int r = 0; r < rounds; r++) {
                WrapDynaClass.clear();
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
                assertEquals(1, results.size(), "createDynaClass returned more than one instance for one key");
            }
        } finally {
            pool.shutdownNow();
            pool.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    /**
     * Tests that repeated calls return the same cached instance when keys cannot be reclaimed.
     */
    @Test
    void testCreateDynaClassIsCached() {
        WrapDynaClass.clear();
        final WrapDynaClass first = WrapDynaClass.createDynaClass(ConcurrentBean.class);
        System.gc();
        assertSame(first, WrapDynaClass.createDynaClass(ConcurrentBean.class));
    }
}
