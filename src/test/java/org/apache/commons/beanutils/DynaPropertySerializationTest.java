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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive serialization tests for {@link DynaProperty#readObject(ObjectInputStream)}.
 * <p>
 * The {@code DynaProperty} class uses a custom serialization protocol to work around JVM issues with serializing primitive {@link Class} types. These tests
 * verify that {@code readObject} correctly reconstructs {@code DynaProperty} instances for every supported primitive type, object types, indexed (array/List)
 * properties, and mapped (Map) properties. A test is also included to verify that a {@link StreamCorruptedException} is thrown when the stream contains an
 * unrecognized primitive-type constant.
 * </p>
 */
class DynaPropertySerializationTest {

    /**
     * Serializes {@code prop} to a byte array.
     */
    private static byte[] serialize(final DynaProperty prop) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(prop);
            return bos.toByteArray();
        }
    }

    /**
     * Deserializes a {@link DynaProperty} from {@code bytes}.
     */
    private static DynaProperty deserialize(final byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (DynaProperty) ois.readObject();
        }
    }

    /**
     * Full round-trip: serialize then deserialize and return the reconstructed instance.
     */
    private static DynaProperty roundTrip(final DynaProperty prop) throws IOException, ClassNotFoundException {
        return deserialize(serialize(prop));
    }

    /**
     * Assert that a round-tripped {@link DynaProperty} is equal to the original and is a distinct object.
     */
    private static void assertRoundTrip(final DynaProperty original) throws IOException, ClassNotFoundException {
        final DynaProperty copy = roundTrip(original);
        assertNotNull(copy);
        assertNotSame(original, copy, "Deserialized instance must be a new object");
        assertEquals(original.getName(), copy.getName(), "name must be preserved");
        assertEquals(original.getType(), copy.getType(), "type must be preserved");
        assertEquals(original.getContentType(), copy.getContentType(), "contentType must be preserved");
        assertEquals(original, copy, "equals() must hold after round-trip");
        assertEquals(original.hashCode(), copy.hashCode(), "hashCode() must hold after round-trip");
    }

    @Test
    void testRoundTripNameOnly() throws Exception {
        // DynaProperty(String) uses Object.class as the type
        assertRoundTrip(new DynaProperty("nameOnly"));
    }

    @Test
    void testRoundTripStringType() throws Exception {
        assertRoundTrip(new DynaProperty("strProp", String.class));
    }

    @Test
    void testRoundTripIntegerWrapperType() throws Exception {
        assertRoundTrip(new DynaProperty("integerProp", Integer.class));
    }

    @Test
    void testRoundTripObjectType() throws Exception {
        assertRoundTrip(new DynaProperty("objProp", Object.class));
    }

    @Test
    void testRoundTripBooleanPrimitiveType() throws Exception {
        assertRoundTrip(new DynaProperty("boolProp", Boolean.TYPE));
    }

    @Test
    void testRoundTripBytePrimitiveType() throws Exception {
        assertRoundTrip(new DynaProperty("byteProp", Byte.TYPE));
    }

    @Test
    void testRoundTripCharPrimitiveType() throws Exception {
        assertRoundTrip(new DynaProperty("charProp", Character.TYPE));
    }

    @Test
    void testRoundTripDoublePrimitiveType() throws Exception {
        assertRoundTrip(new DynaProperty("doubleProp", Double.TYPE));
    }

    @Test
    void testRoundTripFloatPrimitiveType() throws Exception {
        assertRoundTrip(new DynaProperty("floatProp", Float.TYPE));
    }

    @Test
    void testRoundTripIntPrimitiveType() throws Exception {
        assertRoundTrip(new DynaProperty("intProp", Integer.TYPE));
    }

    @Test
    void testRoundTripLongPrimitiveType() throws Exception {
        assertRoundTrip(new DynaProperty("longProp", Long.TYPE));
    }

    @Test
    void testRoundTripShortPrimitiveType() throws Exception {
        assertRoundTrip(new DynaProperty("shortProp", Short.TYPE));
    }

    @Test
    void testRoundTripObjectArrayType() throws Exception {
        // String[] – contentType is String.class (non-primitive)
        assertRoundTrip(new DynaProperty("strArrayProp", String[].class));
    }

    @Test
    void testRoundTripPrimitiveArrayType() throws Exception {
        // int[] – constructor sets contentType = Integer.TYPE (primitive)
        final DynaProperty prop = new DynaProperty("intArrayProp", int[].class);
        assertRoundTrip(prop);
        // Verify contentType is correctly restored as the primitive type
        final DynaProperty copy = roundTrip(prop);
        assertEquals(Integer.TYPE, copy.getContentType());
    }

    @Test
    void testRoundTripListTypeWithNoContentType() throws Exception {
        // Raw List – isIndexed() returns true, contentType may be null
        assertRoundTrip(new DynaProperty("listProp", List.class));
    }

    @Test
    void testRoundTripListTypeWithExplicitContentType() throws Exception {
        // List<String> – explicit contentType is an object class
        assertRoundTrip(new DynaProperty("typedListProp", List.class, String.class));
    }

    @Test
    void testRoundTripListTypeWithPrimitiveContentType() throws Exception {
        // List<int> – explicit contentType is a primitive type
        assertRoundTrip(new DynaProperty("intListProp", List.class, Integer.TYPE));
    }

    @Test
    void testRoundTripConcreteListSubtype() throws Exception {
        // ArrayList also satisfies isIndexed()
        assertRoundTrip(new DynaProperty("arrayListProp", ArrayList.class, String.class));
    }

    @Test
    void testRoundTripMapTypeWithNoContentType() throws Exception {
        // Raw Map – isMapped() returns true, contentType may be null
        assertRoundTrip(new DynaProperty("mapProp", Map.class));
    }

    @Test
    void testRoundTripMapTypeWithObjectContentType() throws Exception {
        // Map with String value type
        assertRoundTrip(new DynaProperty("typedMapProp", Map.class, String.class));
    }

    @Test
    void testRoundTripMapTypeWithPrimitiveContentType() throws Exception {
        // Map with int value type
        assertRoundTrip(new DynaProperty("intMapProp", Map.class, Integer.TYPE));
    }

    @Test
    void testRoundTripConcreteMapSubtype() throws Exception {
        // HashMap also satisfies isMapped()
        assertRoundTrip(new DynaProperty("hashMapProp", HashMap.class, Double.TYPE));
    }

    @Test
    void testNameIsPreservedAfterRoundTrip() throws Exception {
        final String uniqueName = "uniquePropertyName_42";
        final DynaProperty copy = roundTrip(new DynaProperty(uniqueName, String.class));
        assertEquals(uniqueName, copy.getName());
    }

    @Test
    void testTypeIsPreservedAfterRoundTripForEveryPrimitive() throws Exception {
        final Class<?>[] primitives = { Boolean.TYPE, Byte.TYPE, Character.TYPE, Double.TYPE, Float.TYPE, Integer.TYPE, Long.TYPE, Short.TYPE };
        for (final Class<?> primitive : primitives) {
            final DynaProperty copy = roundTrip(new DynaProperty("p", primitive));
            assertEquals(primitive, copy.getType(), () -> "type must be preserved for primitive " + primitive.getName());
        }
    }

    @Test
    void testContentTypeIsNullForSimpleProperty() throws Exception {
        final DynaProperty copy = roundTrip(new DynaProperty("simple", String.class));
        assertNull(copy.getContentType(), "Simple (non-indexed, non-mapped) property must have null contentType");
    }

    @Test
    void testIsIndexedPreservedAfterRoundTrip() throws Exception {
        final DynaProperty indexed = new DynaProperty("idx", List.class, String.class);
        final DynaProperty copy = roundTrip(indexed);
        assertEquals(indexed.isIndexed(), copy.isIndexed());
    }

    @Test
    void testIsMappedPreservedAfterRoundTrip() throws Exception {
        final DynaProperty mapped = new DynaProperty("map", Map.class, Integer.class);
        final DynaProperty copy = roundTrip(mapped);
        assertEquals(mapped.isMapped(), copy.isMapped());
    }

    @Test
    void testToStringAfterRoundTrip() throws Exception {
        final DynaProperty original = new DynaProperty("myProp", List.class, String.class);
        final DynaProperty copy = roundTrip(original);
        assertEquals(original.toString(), copy.toString());
    }

    @Test
    void testMultipleSerializationsProduceSameResult() throws Exception {
        final DynaProperty prop = new DynaProperty("multi", Integer.TYPE);
        final DynaProperty copy1 = roundTrip(prop);
        final DynaProperty copy2 = roundTrip(prop);
        assertEquals(copy1, copy2);
        assertEquals(copy1.hashCode(), copy2.hashCode());
    }

    @Test
    void testSerializationIsIdempotent() throws Exception {
        // serialize the result of the first round-trip and verify it round-trips again
        final DynaProperty original = new DynaProperty("idempotent", Map.class, Long.TYPE);
        final DynaProperty copy1 = roundTrip(original);
        final DynaProperty copy2 = roundTrip(copy1);
        assertEquals(original, copy2);
    }

    /**
     * Verifies that {@link DynaProperty#readObject(ObjectInputStream)} throws a {@link StreamCorruptedException} when the stream contains an unrecognised
     * primitive-type constant (i.e. an integer that is not in the range 1–8).
     * <p>
     * Strategy: serialize two {@code DynaProperty} instances whose types differ only in the primitive-type constant written by {@code writeAnyClass}
     * (BOOLEAN_TYPE=1 vs BYTE_TYPE=2). Find the first differing byte (the last byte of the 4-byte int), then replace those 4 bytes with the invalid constant 99
     * before attempting deserialisation.
     * </p>
     */
    @Test
    void testReadObjectThrowsStreamCorruptedExceptionForInvalidPrimitiveTypeConstant() throws Exception {
        // serialize a DynaProperty whose type is stored as BOOLEAN_TYPE (int 1)
        final byte[] booleanBytes = serialize(new DynaProperty("corrupt", Boolean.TYPE));
        // serialize the same name/class but with BYTE_TYPE (int 2) to locate the int
        final byte[] byteBytes = serialize(new DynaProperty("corrupt", Byte.TYPE));
        // The two streams should be identical except for the last byte of the 4-byte
        // primitive-type int (0x01 vs 0x02). Find the first (and only) differing byte.
        int diffIndex = -1;
        for (int i = 0; i < booleanBytes.length; i++) {
            if (booleanBytes[i] != byteBytes[i]) {
                diffIndex = i;
                break;
            }
        }
        assertNotNull(Integer.valueOf(diffIndex), "Streams must differ at the primitive-type int");
        // The 4-byte int starts 3 bytes before the differing byte (big-endian).
        // Overwrite all 4 bytes with the invalid constant 99 (0x00_00_00_63).
        final byte[] corrupted = booleanBytes.clone();
        corrupted[diffIndex - 3] = 0x00;
        corrupted[diffIndex - 2] = 0x00;
        corrupted[diffIndex - 1] = 0x00;
        corrupted[diffIndex] = (byte) 99;
        // Deserialising the corrupted stream must throw StreamCorruptedException.
        final IOException thrown = assertThrows(IOException.class, () -> deserialize(corrupted),
                "Expected StreamCorruptedException for unrecognised primitive-type constant");
        assertInstanceOf(StreamCorruptedException.class, thrown, "Root cause must be StreamCorruptedException");
    }

    /**
     * Variant of the corruption test using a <em>mapped</em> property so that the second {@code readAnyClass} call (for contentType) is the one that receives
     * the bad constant.
     */
    @Test
    void testReadObjectThrowsStreamCorruptedExceptionForInvalidContentTypePrimitiveConstant() throws Exception {
        // serialize Map<String, Boolean> vs Map<String, Byte> – the type (Map.class)
        // is written as a non-primitive class object; the contentType is written as a
        // primitive-type constant.
        final byte[] boolContentBytes = serialize(new DynaProperty("corruptContent", Map.class, Boolean.TYPE));
        final byte[] byteContentBytes = serialize(new DynaProperty("corruptContent", Map.class, Byte.TYPE));
        int diffIndex = -1;
        for (int i = 0; i < boolContentBytes.length; i++) {
            if (boolContentBytes[i] != byteContentBytes[i]) {
                diffIndex = i;
                break;
            }
        }
        assertNotNull(Integer.valueOf(diffIndex), "Streams must differ at the contentType primitive-type int");
        final byte[] corrupted = boolContentBytes.clone();
        corrupted[diffIndex - 3] = 0x00;
        corrupted[diffIndex - 2] = 0x00;
        corrupted[diffIndex - 1] = 0x00;
        corrupted[diffIndex] = (byte) 99;
        final IOException thrown = assertThrows(IOException.class, () -> deserialize(corrupted),
                "Expected StreamCorruptedException for unrecognised primitive contentType constant");
        assertInstanceOf(StreamCorruptedException.class, thrown, "Root cause must be StreamCorruptedException");
    }
}
