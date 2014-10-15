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


package org.apache.commons.beanutils;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * General purpose test bean for JUnit tests for the "beanutils" component.
 *
 * @version $Id$
 */

public class TestBean implements Serializable {

    // ----------------------------------------------------------- Constructors

    public TestBean() {
        listIndexed.add("String 0");
        listIndexed.add("String 1");
        listIndexed.add("String 2");
        listIndexed.add("String 3");
        listIndexed.add("String 4");
    }

    public TestBean(final String stringProperty) {
        setStringProperty(stringProperty);
    }

    public TestBean(final float floatProperty) {
        setFloatProperty(floatProperty);
    }

    public TestBean(final boolean booleanProperty) {
        setBooleanProperty(booleanProperty);
    }

    public TestBean(final Boolean booleanSecond) {
        setBooleanSecond(booleanSecond.booleanValue());
    }

    public TestBean(final float floatProperty, final String stringProperty) {
        setFloatProperty(floatProperty);
        setStringProperty(stringProperty);
    }

    public TestBean(final boolean booleanProperty, final String stringProperty) {
        setBooleanProperty(booleanProperty);
        setStringProperty(stringProperty);
    }

    public TestBean(final Boolean booleanSecond, final String stringProperty) {
        setBooleanSecond(booleanSecond.booleanValue());
        setStringProperty(stringProperty);
    }

    public TestBean(final Integer intProperty) {
        setIntProperty(intProperty.intValue());
    }

   public TestBean(final double doubleProperty) {
       setDoubleProperty(doubleProperty);
   }

    TestBean(final int intProperty) {
        setIntProperty(intProperty);
    }

    protected TestBean(final boolean booleanProperty, final boolean booleanSecond, final String stringProperty) {
        setBooleanProperty(booleanProperty);
        setBooleanSecond(booleanSecond);
        setStringProperty(stringProperty);
    }

    public TestBean(final List<Object> listIndexed) {
        this.listIndexed = listIndexed;
    }

    public TestBean(final String[][] string2dArray) {
        this.string2dArray = string2dArray;
    }

    // ------------------------------------------------------------- Properties


    /**
     * A boolean property.
     */
    private boolean booleanProperty = true;

    public boolean getBooleanProperty() {
        return (booleanProperty);
    }

    public void setBooleanProperty(final boolean booleanProperty) {
        this.booleanProperty = booleanProperty;
    }


    /**
     * A boolean property that uses an "is" method for the getter.
     */
    private boolean booleanSecond = true;

    public boolean isBooleanSecond() {
        return (booleanSecond);
    }

    public void setBooleanSecond(final boolean booleanSecond) {
        this.booleanSecond = booleanSecond;
    }


    /**
     * A byte property.
     */
    private byte byteProperty = (byte) 121;

    public byte getByteProperty() {
        return (this.byteProperty);
    }

    public void setByteProperty(final byte byteProperty) {
        this.byteProperty = byteProperty;
    }


    /**
     * A java.util.Date property.
     */
    private java.util.Date dateProperty;

    public java.util.Date getDateProperty() {
        return dateProperty;
    }

    public void setDateProperty(final java.util.Date dateProperty) {
        this.dateProperty = dateProperty;
    }

    /**
     * A java.util.Date property.
     */
    private java.util.Date[] dateArrayProperty;

    public java.util.Date[] getDateArrayProperty() {
        return dateArrayProperty;
    }

    public void setDateArrayProperty(final java.util.Date[] dateArrayProperty) {
        this.dateArrayProperty = dateArrayProperty;
    }

    /**
     * A double property.
     */
    private double doubleProperty = 321.0;

    public double getDoubleProperty() {
        return (this.doubleProperty);
    }

    public void setDoubleProperty(final double doubleProperty) {
        this.doubleProperty = doubleProperty;
    }


    /**
     * An "indexed property" accessible via both array and subscript
     * based getters and setters.
     */
    private String[] dupProperty =
    { "Dup 0", "Dup 1", "Dup 2", "Dup 3", "Dup 4" };

    public String[] getDupProperty() {
        return (this.dupProperty);
    }

    public String getDupProperty(final int index) {
        return (this.dupProperty[index]);
    }

    public void setDupProperty(final int index, final String value) {
        this.dupProperty[index] = value;
    }

    public void setDupProperty(final String[] dupProperty) {
        this.dupProperty = dupProperty;
    }


    /**
     * A float property.
     */
    private float floatProperty = (float) 123.0;

    public float getFloatProperty() {
        return (this.floatProperty);
    }

    public void setFloatProperty(final float floatProperty) {
        this.floatProperty = floatProperty;
    }


    /**
     * An integer array property accessed as an array.
     */
    private int intArray[] = { 0, 10, 20, 30, 40 };

    public int[] getIntArray() {
        return (this.intArray);
    }

    public void setIntArray(final int[] intArray) {
        this.intArray = intArray;
    }


    /**
     * An integer array property accessed as an indexed property.
     */
    private final int intIndexed[] = { 0, 10, 20, 30, 40 };

    public int getIntIndexed(final int index) {
        return (intIndexed[index]);
    }

    public void setIntIndexed(final int index, final int value) {
        intIndexed[index] = value;
    }


    /**
     * An integer property.
     */
    private int intProperty = 123;

    public int getIntProperty() {
        return (this.intProperty);
    }

    public void setIntProperty(final int intProperty) {
        this.intProperty = intProperty;
    }


    /**
     * A List property accessed as an indexed property.
     */
    private List<Object> listIndexed = new ArrayList<Object>();

    public List<Object> getListIndexed() {
        return (listIndexed);
    }


    /**
     * A long property.
     */
    private long longProperty = 321;

    public long getLongProperty() {
        return (this.longProperty);
    }

    public void setLongProperty(final long longProperty) {
        this.longProperty = longProperty;
    }


    /**
     * A mapped property with only a getter and setter for a Map.
     */
    private Map<String, Object> mapProperty = null;

    public Map<String, Object> getMapProperty() {
        // Create the map the very first time
        if (mapProperty == null) {
            mapProperty = new HashMap<String, Object>();
            mapProperty.put("First Key", "First Value");
            mapProperty.put("Second Key", "Second Value");
        }
        return (mapProperty);
    }

    public void setMapProperty(Map<String, Object> mapProperty) {
        // Create the map the very first time
        if (mapProperty == null) {
            mapProperty = new HashMap<String, Object>();
            mapProperty.put("First Key", "First Value");
            mapProperty.put("Second Key", "Second Value");
        }
        this.mapProperty = mapProperty;
    }


    /**
     * A mapped property that has String keys and Object values.
     */
    private HashMap<String, Object> mappedObjects = null;

    public Object getMappedObjects(final String key) {
        // Create the map the very first time
        if (mappedObjects == null) {
            mappedObjects = new HashMap<String, Object>();
            mappedObjects.put("First Key", "First Value");
            mappedObjects.put("Second Key", "Second Value");
        }
        return (mappedObjects.get(key));
    }

    public void setMappedObjects(final String key, final Object value) {
        // Create the map the very first time
        if (mappedObjects == null) {
            mappedObjects = new HashMap<String, Object>();
            mappedObjects.put("First Key", "First Value");
            mappedObjects.put("Second Key", "Second Value");
        }
        mappedObjects.put(key, value);
    }


    /**
     * A mapped property that has String keys and String values.
     */
    private HashMap<String, String> mappedProperty = null;

    public String getMappedProperty(final String key) {
        // Create the map the very first time
        if (mappedProperty == null) {
            mappedProperty = new HashMap<String, String>();
            mappedProperty.put("First Key", "First Value");
            mappedProperty.put("Second Key", "Second Value");
        }
        return (mappedProperty.get(key));
    }

    public void setMappedProperty(final String key, final String value) {
        // Create the map the very first time
        if (mappedProperty == null) {
            mappedProperty = new HashMap<String, String>();
            mappedProperty.put("First Key", "First Value");
            mappedProperty.put("Second Key", "Second Value");
        }
        mappedProperty.put(key, value);
    }


    /**
     * A mapped property that has String keys and int values.
     */
    private HashMap<String, Integer> mappedIntProperty = null;

    public int getMappedIntProperty(final String key) {
        // Create the map the very first time
        if (mappedIntProperty == null) {
            mappedIntProperty = new HashMap<String, Integer>();
            mappedIntProperty.put("One", 1);
            mappedIntProperty.put("Two", 2);
        }
        final Integer x = mappedIntProperty.get(key);
        return ((x == null) ? 0 : x.intValue());
    }

    public void setMappedIntProperty(final String key, final int value) {
        mappedIntProperty.put(key, value);
    }


    /**
     * A nested reference to another test bean (populated as needed).
     */
    private TestBean nested = null;

    public TestBean getNested() {
        if (nested == null) {
            nested = new TestBean();
        }
        return (nested);
    }

   /**
    * Another nested reference to another test bean,
    */
   private TestBean anotherNested = null;

   public TestBean getAnotherNested() {
      return anotherNested;
   }

   public void setAnotherNested( final TestBean anotherNested ) {
      this.anotherNested = anotherNested;
   }

   /**
    * Another nested reference to another test bean,
    */
   private DynaBean nestedDynaBean = null;

   public DynaBean getNestedDynaBean() {
      return nestedDynaBean;
   }

   public void setNestedDynaBean(final DynaBean nestedDynaBean) {
      this.nestedDynaBean = nestedDynaBean;
   }

    /*
     * Another nested reference to a bean containing mapp properties
     */
    public class MappedTestBean {
        public void setValue(final String key,final String val) { }
        public String getValue(final String key) { return "Mapped Value"; }
    }

    private MappedTestBean mappedNested = null;

    public MappedTestBean getMappedNested() {
        if (mappedNested == null)
        {
            mappedNested = new MappedTestBean();
        }
        return mappedNested;
    }

    /**
     * A String property with an initial value of null.
     */
    private String nullProperty = null;

    public String getNullProperty() {
        return (this.nullProperty);
    }

    public void setNullProperty(final String nullProperty) {
        this.nullProperty = nullProperty;
    }


    /**
     * A read-only String property.
     */
    private final String readOnlyProperty = "Read Only String Property";

    public String getReadOnlyProperty() {
        return (this.readOnlyProperty);
    }


    /**
     * A short property.
     */
    private short shortProperty = (short) 987;

    public short getShortProperty() {
        return (this.shortProperty);
    }

    public void setShortProperty(final short shortProperty) {
        this.shortProperty = shortProperty;
    }


    /**
     * A String array property accessed as a String.
     */
    private String[] stringArray =
            { "String 0", "String 1", "String 2", "String 3", "String 4" };

    public String[] getStringArray() {
        return (this.stringArray);
    }

    public void setStringArray(final String[] stringArray) {
        this.stringArray = stringArray;
    }


    /**
     * A String array property accessed as an indexed property.
     */
    private final String[] stringIndexed =
            { "String 0", "String 1", "String 2", "String 3", "String 4" };

    public String getStringIndexed(final int index) {
        return (stringIndexed[index]);
    }

    public void setStringIndexed(final int index, final String value) {
        stringIndexed[index] = value;
    }

    private String[][] string2dArray = new String[][] {new String[] {"1", "2", "3"}, new String[] {"4","5","6"}};
    public String[] getString2dArray(final int index) {
        return string2dArray[index];
    }

    /**
     * A String property.
     */
    private String stringProperty = "This is a string";

    public String getStringProperty() {
        return (this.stringProperty);
    }

    public void setStringProperty(final String stringProperty) {
        this.stringProperty = stringProperty;
    }


    /**
     * A write-only String property.
     */
    private String writeOnlyProperty = "Write Only String Property";

    public String getWriteOnlyPropertyValue() {
        return (this.writeOnlyProperty);
    }

    public void setWriteOnlyProperty(final String writeOnlyProperty) {
        this.writeOnlyProperty = writeOnlyProperty;
    }


    // ------------------------------------------------------ Invalid Properties


    /**
     * <p>An invalid property that has two boolean getters (getInvalidBoolean
     * and isInvalidBoolean) plus a String setter (setInvalidBoolean).  By the
     * rules described in the JavaBeans Specification, this will be considered
     * a read-only boolean property, using isInvalidBoolean() as the getter.</p>
     */
    private boolean invalidBoolean = false;

    public boolean getInvalidBoolean() {
        return (this.invalidBoolean);
    }

    public boolean isInvalidBoolean() {
        return (this.invalidBoolean);
    }

    public void setInvalidBoolean(final String invalidBoolean) {
        if ("true".equalsIgnoreCase(invalidBoolean) ||
            "yes".equalsIgnoreCase(invalidBoolean) ||
            "1".equalsIgnoreCase(invalidBoolean)) {
            this.invalidBoolean = true;
        } else {
            this.invalidBoolean = false;
        }
    }



    // ------------------------------------------------------- Static Variables


    /**
     * A static variable that is accessed and updated via static methods
     * for MethodUtils testing.
     */
    private static int counter = 0;


    /**
     * Return the current value of the counter.
     */
    public static int currentCounter() {

        return (counter);

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
     * Increments the current value of the count by the
     * specified amount * 2. It has the same name
     * as the method above so as to test the looseness
     * of getMethod.
     */
    public static void incrementCounter(final Number amount) {
        counter += 2 * amount.intValue();
    }

}
