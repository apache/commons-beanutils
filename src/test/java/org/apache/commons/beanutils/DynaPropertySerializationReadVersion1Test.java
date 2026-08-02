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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests that {@link DynaProperty} instances serialized by a prior version can still be deserialized correctly.
 * <h2>Workflow</h2>
 * <ol>
 * <li>Run {@link #main(String[])} once to produce serialized files under {@code ./target/DynaPropertySerialization}. Each file is named after the property it
 * represents (for example {@code nameOnly.ser}).</li>
 * <li>Copy those files to {@code src/test/resources/org/apache/commons/beanutils/}.</li>
 * <li>Run the test suite normally. {@link #testDeserializeFromResources()} reads every {@code *.ser} file from the resource directory and asserts that
 * deserialization succeeds and produces a non-null {@link DynaProperty} with the expected name.</li>
 * </ol>
 * <p>
 * {@link #testDeserializeFromTargetDirectory()} provides immediate feedback: it reads the files written by {@link #main(String[])} from
 * {@code ./target/DynaPropertySerialization} and performs the same assertions, so a developer can verify the round-trip without copying files first.
 * </p>
 */
class DynaPropertySerializationReadVersion1Test {

    /** Resource path (relative to the classpath root) where committed serialized files live. */
    private static final String RESOURCE_DIR = "org/apache/commons/beanutils";

    /** Directory where {@link #main(String[])} writes serialized files. */
    private static final String TARGET_DIR = "target/DynaPropertySerialization";

    /**
     * Deserializes a {@link DynaProperty} from the given stream and asserts that the result is structurally equal to {@code expected}.
     *
     * @param ois      source stream (caller is responsible for closing).
     * @param expected the property the deserialized instance must equal.
     * @throws IOException            if an I/O error occurs.
     * @throws ClassNotFoundException if the class of the serialized object cannot be found.
     */
    private static void assertDeserializedProperty(final ObjectInputStream ois, final DynaProperty expected) throws IOException, ClassNotFoundException {
        final Object obj = ois.readObject();
        assertNotNull(obj, "Deserialized object must not be null");
        assertTrue(obj instanceof DynaProperty, "Deserialized object must be a DynaProperty, was: " + obj.getClass());
        final DynaProperty actual = (DynaProperty) obj;
        assertEquals(expected.getName(), actual.getName(), () -> "name must be preserved for property " + expected.getName());
        assertEquals(expected.getType(), actual.getType(), () -> "type must be preserved for property " + expected.getName());
        assertEquals(expected.getContentType(), actual.getContentType(), () -> "contentType must be preserved for property " + expected.getName());
        assertEquals(expected, actual, () -> "equals() must hold for property " + expected.getName());
    }

    /**
     * Deserializes all {@code *.ser} files found in {@code dir} and asserts that each one matches the canonical property of the same name.
     *
     * @param dir         directory containing {@code *.ser} files.
     * @param description human-readable label used in assertion messages.
     * @throws Exception Thrown if deserialization fails or an assertion is violated.
     */
    private static void assertDeserializeFromDirectory(final File dir, final String description) throws Exception {
        assertTrue(dir.isDirectory(), () -> description + ": directory must exist: " + dir.getAbsolutePath());
        final File[] files = dir.listFiles((d, name) -> name.endsWith(".ser"));
        assertNotNull(files, () -> description + ": listFiles() must not return null");
        assertTrue(files.length > 0, () -> description + ": directory must contain at least one *.ser file: " + dir.getAbsolutePath());
        final Map<String, DynaProperty> expected = buildCanonicalMap();
        for (final File file : files) {
            final String propName = file.getName().replace(".ser", "");
            final DynaProperty expectedProp = expected.get(propName);
            assertNotNull(expectedProp,
                    () -> description + ": no canonical property found for file: " + file.getName() + ". Known names: " + expected.keySet());
            try (FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis)) {
                assertDeserializedProperty(ois, expectedProp);
            }
        }
    }

    /**
     * Builds a lookup map from property name to canonical {@link DynaProperty} so that test methods can locate the expected value for any file they encounter.
     */
    private static Map<String, DynaProperty> buildCanonicalMap() {
        final Map<String, DynaProperty> map = new HashMap<>();
        for (final DynaProperty prop : buildCanonicalProperties()) {
            map.put(prop.getName(), prop);
        }
        return map;
    }

    /**
     * Returns the canonical list of {@link DynaProperty} instances whose serialized form is committed to source control as "version 1" compatibility fixtures.
     *
     * @return ordered list of property instances.
     */
    private static List<DynaProperty> buildCanonicalProperties() {
        final List<DynaProperty> props = new ArrayList<>();
        // Simple types
        props.add(new DynaProperty("nameOnly")); // Object.class
        props.add(new DynaProperty("strProp", String.class));
        props.add(new DynaProperty("integerProp", Integer.class));
        props.add(new DynaProperty("objProp", Object.class));
        // Primitive types
        props.add(new DynaProperty("boolProp", Boolean.TYPE));
        props.add(new DynaProperty("byteProp", Byte.TYPE));
        props.add(new DynaProperty("charProp", Character.TYPE));
        props.add(new DynaProperty("doubleProp", Double.TYPE));
        props.add(new DynaProperty("floatProp", Float.TYPE));
        props.add(new DynaProperty("intProp", Integer.TYPE));
        props.add(new DynaProperty("longProp", Long.TYPE));
        props.add(new DynaProperty("shortProp", Short.TYPE));
        // Array types
        props.add(new DynaProperty("strArrayProp", String[].class));
        props.add(new DynaProperty("intArrayProp", int[].class));
        // Indexed (List) types
        props.add(new DynaProperty("listProp", List.class));
        props.add(new DynaProperty("typedListProp", List.class, String.class));
        props.add(new DynaProperty("intListProp", List.class, Integer.TYPE));
        props.add(new DynaProperty("arrayListProp", ArrayList.class, String.class));
        // Mapped (Map) types
        props.add(new DynaProperty("mapProp", Map.class));
        props.add(new DynaProperty("typedMapProp", Map.class, String.class));
        props.add(new DynaProperty("intMapProp", Map.class, Integer.TYPE));
        props.add(new DynaProperty("hashMapProp", HashMap.class, Double.TYPE));
        return props;
    }

    private static File checkDir(final File dir) throws IOException {
        if (!dir.mkdirs() && !dir.isDirectory()) {
            throw new IOException("Cannot create directory: " + dir.getAbsolutePath());
        }
        return dir;
    }

    /**
     * Serializes each canonical {@link DynaProperty} to a file under {@value #TARGET_DIR}.
     * <p>
     * Run this once to produce the fixture files, then copy them to {@code src/test/resources/org/apache/commons/beanutils/} before committing.
     * </p>
     *
     * @param args ignored.
     * @throws IOException Thrown if any I/O error occurs.
     */
    public static void main(final String[] args) throws IOException {
        final File dir = checkDir(new File(TARGET_DIR));
        for (final DynaProperty prop : buildCanonicalProperties()) {
            final File file = new File(dir, prop.getName() + ".ser");
            try (FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(prop);
            }
            System.out.println("Wrote: " + file.getAbsolutePath());
        }
        System.out.println("Done. Copy " + TARGET_DIR + "/*.ser to src/test/resources/" + RESOURCE_DIR + "/");
    }

    /**
     * Reads every {@code *.ser} file from the classpath resource directory {@code src/test/resources/org/apache/commons/beanutils/} and asserts that
     * deserialization produces a {@link DynaProperty} equal to the canonical instance with the same name.
     * <p>
     * This test verifies backwards compatibility: files serialized by an older version of the code can still be read by the current version.
     * </p>
     * <p>
     * <strong>Prerequisites:</strong> run {@link #main(String[])} once and copy the resulting {@code *.ser} files from {@value #TARGET_DIR} to
     * {@code src/test/resources/org/apache/commons/beanutils/} before running this test.
     * </p>
     */
    @Test
    void testDeserializeFromResources() throws Exception {
        // Resolve the resource directory relative to the project root so the test
        // works whether launched from Maven or from an IDE.
        final File resourceDir = new File("src/test/resources/" + RESOURCE_DIR);
        assertDeserializeFromDirectory(resourceDir, "Resources directory");
    }

    /**
     * Reads every {@code *.ser} file written by {@link #main(String[])} from {@value #TARGET_DIR} and asserts that deserialization produces a
     * {@link DynaProperty} equal to the canonical instance with the same name.
     * <p>
     * This test allows a developer to verify the round-trip immediately after running {@link #main(String[])}, without having to copy the files first.
     * </p>
     */
    @Test
    @Disabled("Requires manual execution of main() to produce files first")
    void testDeserializeFromTargetDirectory() throws Exception {
        final File targetDir = new File(TARGET_DIR);
        assertDeserializeFromDirectory(targetDir, "Target directory");
    }
}
