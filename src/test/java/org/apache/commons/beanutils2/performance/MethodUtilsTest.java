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
package org.apache.commons.beanutils2.performance;

import org.apache.commons.beanutils2.MethodUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class MethodUtilsTest {

    public static Class<?> getPrimitiveType1Old(final Class<?> wrapperType) {
        // does anyone know a better strategy than comparing names?
        if (Boolean.class.equals(wrapperType)) {
            return boolean.class;
        } else if (Float.class.equals(wrapperType)) {
            return float.class;
        } else if (Long.class.equals(wrapperType)) {
            return long.class;
        } else if (Integer.class.equals(wrapperType)) {
            return int.class;
        } else if (Short.class.equals(wrapperType)) {
            return short.class;
        } else if (Byte.class.equals(wrapperType)) {
            return byte.class;
        } else if (Double.class.equals(wrapperType)) {
            return double.class;
        } else if (Character.class.equals(wrapperType)) {
            return char.class;
        } else {
            final Log log = LogFactory.getLog(MethodUtils.class);
            if (log.isDebugEnabled()) {
                log.debug("Not a known primitive wrapper class: " + wrapperType);
            }
            return null;
        }
    }

    public static Class<?> getPrimitiveType2(final Class<?> wrapperType) {
        Class<?> res = null;
        try {
            Field field = wrapperType.getField("TYPE");
            Object fieldContent = field.get(null);
            if (fieldContent instanceof Class) {
                res = (Class<?>) fieldContent;
                if (!res.isPrimitive()) {
                    res = null;
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
        }
        if (res == null) {
            final Log log = LogFactory.getLog(MethodUtils.class);
            if (log.isDebugEnabled()) {
                log.debug("Not a known primitive wrapper class: " + wrapperType);
            }
        }
        return res;
    }

    public static Class<?> getPrimitiveType3(final Class<?> wrapperType) {
        if (Number.class.isAssignableFrom(wrapperType)) {
            if (Integer.class.equals(wrapperType)) {
                return int.class;
            } else if (Double.class.equals(wrapperType)) {
                return double.class;
            } else if (Long.class.equals(wrapperType)) {
                return long.class;
            } else if (Float.class.equals(wrapperType)) {
                return float.class;
            } else if (Short.class.equals(wrapperType)) {
                return short.class;
            } else if (Byte.class.equals(wrapperType)) {
                return byte.class;
            }
        } else {
            if (Boolean.class.equals(wrapperType)) {
                return boolean.class;
            } else if (Character.class.equals(wrapperType)) {
                return char.class;
            }
        }
        final Log log = LogFactory.getLog(MethodUtils.class);
        if (log.isDebugEnabled()) {
            log.debug("Not a known primitive wrapper class: " + wrapperType);
        }
        return null;
    }

    public static Class<?> getPrimitiveType4(final Class<?> wrapperType) {
        if (Comparable.class.isAssignableFrom(wrapperType)) {
            if (Integer.class.equals(wrapperType)) {
                return int.class;
            } else if (Double.class.equals(wrapperType)) {
                return double.class;
            } else if (Boolean.class.equals(wrapperType)) {
                return boolean.class;
            } else if (Long.class.equals(wrapperType)) {
                return long.class;
            } else if (Float.class.equals(wrapperType)) {
                return float.class;
            } else if (Short.class.equals(wrapperType)) {
                return short.class;
            } else if (Byte.class.equals(wrapperType)) {
                return byte.class;
            } else if (Character.class.equals(wrapperType)) {
                return char.class;
            }
        }
        final Log log = LogFactory.getLog(MethodUtils.class);
        if (log.isDebugEnabled()) {
            log.debug("Not a known primitive wrapper class: " + wrapperType);
        }
        return null;
    }

    public static Class<?> getPrimitiveType5(final Class<?> wrapperType) {
        switch (wrapperType.getName()) {
            case "Boolean":
                return boolean.class;
            case "Float":
                return float.class;
            case "Long":
                return long.class;
            case "Integer":
                return int.class;
            case "Short":
                return short.class;
            case "Byte":
                return byte.class;
            case "Double":
                return double.class;
            case "Character":
                return char.class;
            default:
                final Log log = LogFactory.getLog(MethodUtils.class);
                if (log.isDebugEnabled()) {
                    log.debug("Not a known primitive wrapper class: " + wrapperType);
                }
                return null;
        }
    }


    /** The number of primitive types. */
    private static final int PRIMITIVE_SIZE = 8;
    /** The boxing types to primitive conversion map. */
    private static final Map<Class<?>, Class<?>> BOXING_CLASSES;

    static {
        BOXING_CLASSES = new IdentityHashMap<Class<?>, Class<?>>(PRIMITIVE_SIZE);
        BOXING_CLASSES.put(Boolean.class, Boolean.TYPE);
        BOXING_CLASSES.put(Byte.class, Byte.TYPE);
        BOXING_CLASSES.put(Character.class, Character.TYPE);
        BOXING_CLASSES.put(Double.class, Double.TYPE);
        BOXING_CLASSES.put(Float.class, Float.TYPE);
        BOXING_CLASSES.put(Integer.class, Integer.TYPE);
        BOXING_CLASSES.put(Long.class, Long.TYPE);
        BOXING_CLASSES.put(Short.class, Short.TYPE);
    }

    /**
     * this function is learned from commons-jexl3, class ArrayBuilder
     * @param wrapperType
     * @return
     */
    public static Class<?> getPrimitiveType6(final Class<?> wrapperType) {
        Class<?> prim = BOXING_CLASSES.get(wrapperType);
        if (prim != null)
            return prim;
        final Log log = LogFactory.getLog(MethodUtils.class);
        if (log.isDebugEnabled()) {
            log.debug("Not a known primitive wrapper class: " + wrapperType);
        }
        return null;
    }

    public static Class<?> getPrimitiveType7(final Class<?> wrapperType) {
        // does anyone know a better strategy than comparing names?
        if (Boolean.class == wrapperType) {
            return boolean.class;
        } else if (Float.class == wrapperType) {
            return float.class;
        } else if (Long.class == wrapperType) {
            return long.class;
        } else if (Integer.class == wrapperType) {
            return int.class;
        } else if (Short.class == wrapperType) {
            return short.class;
        } else if (Byte.class == wrapperType) {
            return byte.class;
        } else if (Double.class == wrapperType) {
            return double.class;
        } else if (Character.class == wrapperType) {
            return char.class;
        } else {
            final Log log = LogFactory.getLog(MethodUtils.class);
            if (log.isDebugEnabled()) {
                log.debug("Not a known primitive wrapper class: " + wrapperType);
            }
            return null;
        }
    }

    private static final List<Class> ARRAY0 = new ArrayList<Class>(
            Arrays.asList(
                    new Class[]{
                            Boolean.class,
                            Float.class,
                            Long.class,
                            Integer.class,
                            Short.class,
                            Byte.class,
                            Double.class,
                            Character.class,
                            String.class,
                            Object.class,
                            List.class,
                            BigInteger.class,
                            BigDecimal.class,
                            Map.class,
                            Set.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                            Object.class,
                    }
            )
    );


    @Benchmark
    public void test1Old() {
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveType1Old(c);
            }
        }
    }

    @Benchmark
    public void test2() {
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveType2(c);
            }
        }
    }

    @Benchmark
    public void test3() {
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveType3(c);
            }
        }
    }

    @Benchmark
    public void test4() {
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveType4(c);
            }
        }
    }

    @Benchmark
    public void test5() {
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveType5(c);
            }
        }
    }

    @Benchmark
    public void test6() {
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveType6(c);
            }
        }
    }

    @Benchmark
    public void test7() {
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveType7(c);
            }
        }
    }

    @Test
    public void equalTest1() {
        for (Class c : ARRAY0) {
            assertEquals(
                    getPrimitiveType1Old(c),
                    MethodUtils.getPrimitiveType(c)
            );
        }
    }

    @Test
    public void equalTest2() {
        for (Class c : ARRAY0) {
            assertEquals(
                    getPrimitiveWrapper1Old(c),
                    MethodUtils.getPrimitiveWrapper(c)
            );
        }
    }

    public static Class<?> getPrimitiveWrapper1Old(final Class<?> primitiveType) {
        // does anyone know a better strategy than comparing names?
        if (boolean.class.equals(primitiveType)) {
            return Boolean.class;
        } else if (float.class.equals(primitiveType)) {
            return Float.class;
        } else if (long.class.equals(primitiveType)) {
            return Long.class;
        } else if (int.class.equals(primitiveType)) {
            return Integer.class;
        } else if (short.class.equals(primitiveType)) {
            return Short.class;
        } else if (byte.class.equals(primitiveType)) {
            return Byte.class;
        } else if (double.class.equals(primitiveType)) {
            return Double.class;
        } else if (char.class.equals(primitiveType)) {
            return Character.class;
        } else {

            return null;
        }
    }

    public static Class<?> getPrimitiveWrapper2(final Class<?> primitiveType) {
        // does anyone know a better strategy than comparing names?
        if (!primitiveType.isPrimitive()) {
            return null;
        } else if (int.class.equals(primitiveType)) {
            return Integer.class;
        } else if (double.class.equals(primitiveType)) {
            return Double.class;
        } else if (long.class.equals(primitiveType)) {
            return Long.class;
        } else if (float.class.equals(primitiveType)) {
            return Float.class;
        } else if (boolean.class.equals(primitiveType)) {
            return Boolean.class;
        } else if (short.class.equals(primitiveType)) {
            return Short.class;
        } else if (byte.class.equals(primitiveType)) {
            return Byte.class;
        } else if (char.class.equals(primitiveType)) {
            return Character.class;
        }
        return null;
    }

//    @Benchmark
//    public void test3() {
//        long start3 = System.nanoTime();
//        for (int i = 0; i < 100000; i++) {
//            for (Class c : ARRAY0) {
//                getPrimitiveWrapper1(c);
//            }
//        }
//        System.out.println(System.nanoTime() - start3);
//
//        long start4 = System.nanoTime();
//        for (int i = 0; i < 100000; i++) {
//            for (Class c : ARRAY0) {
//                getPrimitiveWrapper2(c);
//            }
//        }
//        System.out.println(System.nanoTime() - start4);
//    }


}
