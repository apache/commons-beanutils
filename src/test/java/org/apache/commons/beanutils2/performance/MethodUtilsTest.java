package org.apache.commons.beanutils2.performance;

import org.apache.commons.beanutils2.MethodUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MethodUtilsTest {

    public static Class<?> getPrimitiveType1(final Class<?> wrapperType) {
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
        // does anyone know a better strategy than comparing names?
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
                            List.class,
                            List.class,
                            List.class,
                            List.class,
                            List.class,
                            List.class,
                            List.class,
                    }
            )
    );

    @Test
    public void test() {
        long start1 = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveType1(c);
            }
        }
        System.out.println(System.nanoTime() - start1);

        long start2 = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveType2(c);
            }
        }
        System.out.println(System.nanoTime() - start2);

        long start3 = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveType3(c);
            }
        }
        System.out.println(System.nanoTime() - start3);

        long start4 = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveType4(c);
            }
        }
        System.out.println(System.nanoTime() - start4);
    }

    @Test
    public void test2() {
        for (Class c : ARRAY0) {
            assertEquals(
                    getPrimitiveType1(c),
                    MethodUtils.getPrimitiveType(c)
            );
        }
    }


    public static Class<?> getPrimitiveWrapper1(final Class<?> primitiveType) {
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

    @Test
    public void test3() {
        long start3 = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveWrapper1(c);
            }
        }
        System.out.println(System.nanoTime() - start3);

        long start4 = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            for (Class c : ARRAY0) {
                getPrimitiveWrapper2(c);
            }
        }
        System.out.println(System.nanoTime() - start4);
    }

    @Test
    public void test4() {
        for (Class c : ARRAY0) {
            assertEquals(
                    getPrimitiveWrapper1(c),
                    MethodUtils.getPrimitiveWrapper(c)
            );
        }
    }
}
