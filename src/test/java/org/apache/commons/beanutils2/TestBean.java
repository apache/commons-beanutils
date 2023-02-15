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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * General purpose test bean for JUnit tests for the "beanutils" component.
 *
 */

public class TestBean implements Serializable {

    /*
     * Another nested reference to a bean containing mapp properties
     */
    public class MappedTestBean {
        public String getValue(final String key) {
            return "Mapped Value";
        }

        public void setValue(final String key, final String val) {
        }
    }

    /**
     * A static variable that is accessed and updated via static methods for MethodUtils testing.
     */
    private static int counter = 0;

    /**
     * Gets the current value of the counter.
     */
    public static int currentCounter() {

        return counter;

    }

    /**
     * Increment the current value of the counter by 1.
     */
    public static void incrementCounter() {

        incrementCounter(1);

    }

    /**
     * Increment the current value of the counter by the specified amount.
     *
     * @param amount Amount to be added to the current counter
     */
    public static void incrementCounter(final int amount) {

        counter += amount;

    }

    /**
     * Increments the current value of the count by the specified amount * 2. It has the same name as the method above so as to test the looseness of getMethod.
     */
    public static void incrementCounter(final Number amount) {
        counter += 2 * amount.intValue();
    }

    /**
     * A boolean property.
     */
    private boolean booleanProperty = true;

    /**
     * A boolean property that uses an "is" method for the getter.
     */
    private boolean booleanSecond = true;

    /**
     * A byte property.
     */
    private byte byteProperty = (byte) 121;

    /**
     * A java.util.Date property.
     */
    private java.util.Date dateProperty;

    /**
     * A java.util.Date property.
     */
    private java.util.Date[] dateArrayProperty;

    /**
     * A double property.
     */
    private double doubleProperty = 321.0;

    /**
     * An "indexed property" accessible via both array and subscript based getters and setters.
     */
    private String[] dupProperty = { "Dup 0", "Dup 1", "Dup 2", "Dup 3", "Dup 4" };

    /**
     * A float property.
     */
    private float floatProperty = (float) 123.0;

    /**
     * An integer array property accessed as an array.
     */
    private int[] intArray = { 0, 10, 20, 30, 40 };

    /**
     * An integer array property accessed as an indexed property.
     */
    private final int[] intIndexed = { 0, 10, 20, 30, 40 };

    /**
     * An integer property.
     */
    private int intProperty = 123;

    /**
     * A List property accessed as an indexed property.
     */
    private List<Object> listIndexed = new ArrayList<>();

    /**
     * A long property.
     */
    private long longProperty = 321;

    /**
     * A mapped property with only a getter and setter for a Map.
     */
    private Map<String, Object> mapProperty = null;

    /**
     * A mapped property that has String keys and Object values.
     */
    private HashMap<String, Object> mappedObjects = null;

    /**
     * A mapped property that has String keys and String values.
     */
    private HashMap<String, String> mappedProperty = null;

    /**
     * A mapped property that has String keys and int values.
     */
    private HashMap<String, Integer> mappedIntProperty = null;

    /**
     * A nested reference to another test bean (populated as needed).
     */
    private TestBean nested = null;

    /**
     * Another nested reference to another test bean,
     */
    private TestBean anotherNested = null;

    /**
     * Another nested reference to another test bean,
     */
    private DynaBean nestedDynaBean = null;

    private MappedTestBean mappedNested = null;

    /**
     * A String property with an initial value of null.
     */
    private String nullProperty = null;

    /**
     * A read-only String property.
     */
    private final String readOnlyProperty = "Read Only String Property";

    /**
     * A short property.
     */
    private short shortProperty = (short) 987;

    /**
     * A String array property accessed as a String.
     */
    private String[] stringArray = { "String 0", "String 1", "String 2", "String 3", "String 4" };

    /**
     * A String array property accessed as an indexed property.
     */
    private final String[] stringIndexed = { "String 0", "String 1", "String 2", "String 3", "String 4" };

    private String[][] string2dArray = { new String[] { "1", "2", "3" }, new String[] { "4", "5", "6" } };

    /**
     * A String property.
     */
    private String stringProperty = "This is a string";

    /**
     * A write-only String property.
     */
    private String writeOnlyProperty = "Write Only String Property";

    /**
     * <p>
     * An invalid property that has two boolean getters (getInvalidBoolean and isInvalidBoolean) plus a String setter (setInvalidBoolean). By the rules
     * described in the JavaBeans Specification, this will be considered a read-only boolean property, using isInvalidBoolean() as the getter.
     * </p>
     */
    private boolean invalidBoolean = false;

    public TestBean() {
        listIndexed.add("String 0");
        listIndexed.add("String 1");
        listIndexed.add("String 2");
        listIndexed.add("String 3");
        listIndexed.add("String 4");
    }

    public TestBean(final boolean booleanProperty) {
        setBooleanProperty(booleanProperty);
    }

    protected TestBean(final boolean booleanProperty, final boolean booleanSecond, final String stringProperty) {
        setBooleanProperty(booleanProperty);
        setBooleanSecond(booleanSecond);
        setStringProperty(stringProperty);
    }

    public TestBean(final boolean booleanProperty, final String stringProperty) {
        setBooleanProperty(booleanProperty);
        setStringProperty(stringProperty);
    }

    public TestBean(final Boolean booleanSecond) {
        setBooleanSecond(booleanSecond.booleanValue());
    }

    public TestBean(final Boolean booleanSecond, final String stringProperty) {
        setBooleanSecond(booleanSecond.booleanValue());
        setStringProperty(stringProperty);
    }

    public TestBean(final double doubleProperty) {
        setDoubleProperty(doubleProperty);
    }

    public TestBean(final float floatProperty) {
        setFloatProperty(floatProperty);
    }

    public TestBean(final float floatProperty, final String stringProperty) {
        setFloatProperty(floatProperty);
        setStringProperty(stringProperty);
    }

    TestBean(final int intProperty) {
        setIntProperty(intProperty);
    }

    public TestBean(final Integer intProperty) {
        setIntProperty(intProperty.intValue());
    }

    public TestBean(final List<Object> listIndexed) {
        this.listIndexed = listIndexed;
    }

    public TestBean(final String stringProperty) {
        setStringProperty(stringProperty);
    }

    public TestBean(final String[][] string2dArray) {
        this.string2dArray = string2dArray;
    }

    public TestBean getAnotherNested() {
        return anotherNested;
    }

    public boolean getBooleanProperty() {
        return booleanProperty;
    }

    public byte getByteProperty() {
        return this.byteProperty;
    }

    public java.util.Date[] getDateArrayProperty() {
        return dateArrayProperty;
    }

    public java.util.Date getDateProperty() {
        return dateProperty;
    }

    public double getDoubleProperty() {
        return this.doubleProperty;
    }

    public String[] getDupProperty() {
        return this.dupProperty;
    }

    public String getDupProperty(final int index) {
        return this.dupProperty[index];
    }

    public float getFloatProperty() {
        return this.floatProperty;
    }

    public int[] getIntArray() {
        return this.intArray;
    }

    public int getIntIndexed(final int index) {
        return intIndexed[index];
    }

    public int getIntProperty() {
        return this.intProperty;
    }

    public boolean getInvalidBoolean() {
        return this.invalidBoolean;
    }

    public List<Object> getListIndexed() {
        return listIndexed;
    }

    public long getLongProperty() {
        return this.longProperty;
    }

    public int getMappedIntProperty(final String key) {
        // Create the map the very first time
        if (mappedIntProperty == null) {
            mappedIntProperty = new HashMap<>();
            mappedIntProperty.put("One", 1);
            mappedIntProperty.put("Two", 2);
        }
        final Integer x = mappedIntProperty.get(key);
        return x == null ? 0 : x.intValue();
    }

    public MappedTestBean getMappedNested() {
        if (mappedNested == null) {
            mappedNested = new MappedTestBean();
        }
        return mappedNested;
    }

    public Object getMappedObjects(final String key) {
        // Create the map the very first time
        if (mappedObjects == null) {
            mappedObjects = new HashMap<>();
            mappedObjects.put("First Key", "First Value");
            mappedObjects.put("Second Key", "Second Value");
        }
        return mappedObjects.get(key);
    }

    public String getMappedProperty(final String key) {
        // Create the map the very first time
        if (mappedProperty == null) {
            mappedProperty = new HashMap<>();
            mappedProperty.put("First Key", "First Value");
            mappedProperty.put("Second Key", "Second Value");
        }
        return mappedProperty.get(key);
    }

    public Map<String, Object> getMapProperty() {
        // Create the map the very first time
        if (mapProperty == null) {
            mapProperty = new HashMap<>();
            mapProperty.put("First Key", "First Value");
            mapProperty.put("Second Key", "Second Value");
        }
        return mapProperty;
    }

    public TestBean getNested() {
        if (nested == null) {
            nested = new TestBean();
        }
        return nested;
    }

    public DynaBean getNestedDynaBean() {
        return nestedDynaBean;
    }

    public String getNullProperty() {
        return this.nullProperty;
    }

    public String getReadOnlyProperty() {
        return this.readOnlyProperty;
    }

    public short getShortProperty() {
        return this.shortProperty;
    }

    public String[] getString2dArray(final int index) {
        return string2dArray[index];
    }

    public String[] getStringArray() {
        return this.stringArray;
    }

    public String getStringIndexed(final int index) {
        return stringIndexed[index];
    }

    public String getStringProperty() {
        return this.stringProperty;
    }

    public String getWriteOnlyPropertyValue() {
        return this.writeOnlyProperty;
    }

    public boolean isBooleanSecond() {
        return booleanSecond;
    }

    public boolean isInvalidBoolean() {
        return this.invalidBoolean;
    }

    public void setAnotherNested(final TestBean anotherNested) {
        this.anotherNested = anotherNested;
    }

    public void setBooleanProperty(final boolean booleanProperty) {
        this.booleanProperty = booleanProperty;
    }

    public void setBooleanSecond(final boolean booleanSecond) {
        this.booleanSecond = booleanSecond;
    }

    public void setByteProperty(final byte byteProperty) {
        this.byteProperty = byteProperty;
    }

    public void setDateArrayProperty(final java.util.Date[] dateArrayProperty) {
        this.dateArrayProperty = dateArrayProperty;
    }

    public void setDateProperty(final java.util.Date dateProperty) {
        this.dateProperty = dateProperty;
    }

    public void setDoubleProperty(final double doubleProperty) {
        this.doubleProperty = doubleProperty;
    }

    public void setDupProperty(final int index, final String value) {
        this.dupProperty[index] = value;
    }

    public void setDupProperty(final String[] dupProperty) {
        this.dupProperty = dupProperty;
    }

    public void setFloatProperty(final float floatProperty) {
        this.floatProperty = floatProperty;
    }

    public void setIntArray(final int[] intArray) {
        this.intArray = intArray;
    }

    public void setIntIndexed(final int index, final int value) {
        intIndexed[index] = value;
    }

    public void setIntProperty(final int intProperty) {
        this.intProperty = intProperty;
    }

    public void setInvalidBoolean(final String invalidBoolean) {
        if ("true".equalsIgnoreCase(invalidBoolean) || "yes".equalsIgnoreCase(invalidBoolean) || "1".equalsIgnoreCase(invalidBoolean)) {
            this.invalidBoolean = true;
        } else {
            this.invalidBoolean = false;
        }
    }

    public void setLongProperty(final long longProperty) {
        this.longProperty = longProperty;
    }

    public void setMappedIntProperty(final String key, final int value) {
        mappedIntProperty.put(key, value);
    }

    public void setMappedObjects(final String key, final Object value) {
        // Create the map the very first time
        if (mappedObjects == null) {
            mappedObjects = new HashMap<>();
            mappedObjects.put("First Key", "First Value");
            mappedObjects.put("Second Key", "Second Value");
        }
        mappedObjects.put(key, value);
    }

    public void setMappedProperty(final String key, final String value) {
        // Create the map the very first time
        if (mappedProperty == null) {
            mappedProperty = new HashMap<>();
            mappedProperty.put("First Key", "First Value");
            mappedProperty.put("Second Key", "Second Value");
        }
        mappedProperty.put(key, value);
    }

    public void setMapProperty(Map<String, Object> mapProperty) {
        // Create the map the very first time
        if (mapProperty == null) {
            mapProperty = new HashMap<>();
            mapProperty.put("First Key", "First Value");
            mapProperty.put("Second Key", "Second Value");
        }
        this.mapProperty = mapProperty;
    }

    public void setNestedDynaBean(final DynaBean nestedDynaBean) {
        this.nestedDynaBean = nestedDynaBean;
    }

    public void setNullProperty(final String nullProperty) {
        this.nullProperty = nullProperty;
    }

    public void setShortProperty(final short shortProperty) {
        this.shortProperty = shortProperty;
    }

    public void setStringArray(final String[] stringArray) {
        this.stringArray = stringArray;
    }

    public void setStringIndexed(final int index, final String value) {
        stringIndexed[index] = value;
    }

    public void setStringProperty(final String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public void setWriteOnlyProperty(final String writeOnlyProperty) {
        this.writeOnlyProperty = writeOnlyProperty;
    }

}
