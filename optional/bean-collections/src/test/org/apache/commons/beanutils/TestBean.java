/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * General purpose test bean for JUnit tests for the "beanutils" component.
 *
 * @author Craig R. McClanahan
 * @author Rodney Waldhoff
 * @version $Revision$ $Date$
 */

public class TestBean {

    // ----------------------------------------------------------- Constructors

    public TestBean() {
    }

    public TestBean(String stringProperty) {
        setStringProperty(stringProperty);
    }

    public TestBean(float floatProperty) {
        setFloatProperty(floatProperty);
    }

    public TestBean(boolean booleanProperty) {
        setBooleanProperty(booleanProperty);
    }

    public TestBean(Boolean booleanSecond) {
        setBooleanSecond(booleanSecond.booleanValue());
    }

    public TestBean(float floatProperty, String stringProperty) {
        setFloatProperty(floatProperty);
        setStringProperty(stringProperty);
    }

    public TestBean(boolean booleanProperty, String stringProperty) {
        setBooleanProperty(booleanProperty);
        setStringProperty(stringProperty);
    }

    public TestBean(Boolean booleanSecond, String stringProperty) {
        setBooleanSecond(booleanSecond.booleanValue());
        setStringProperty(stringProperty);
    }

    public TestBean(Integer intProperty) {
        setIntProperty(intProperty.intValue());
    }

   public TestBean(double doubleProperty) {
       setDoubleProperty(doubleProperty);
   }
   
    TestBean(int intProperty) {
        setIntProperty(intProperty);
    }

    protected TestBean(boolean booleanProperty, boolean booleanSecond, String stringProperty) {
        setBooleanProperty(booleanProperty);
        setBooleanSecond(booleanSecond);
        setStringProperty(stringProperty);
    }

    // ------------------------------------------------------------- Properties


    /**
     * A boolean property.
     */
    private boolean booleanProperty = true;

    public boolean getBooleanProperty() {
        return (booleanProperty);
    }

    public void setBooleanProperty(boolean booleanProperty) {
        this.booleanProperty = booleanProperty;
    }


    /**
     * A boolean property that uses an "is" method for the getter.
     */
    private boolean booleanSecond = true;

    public boolean isBooleanSecond() {
        return (booleanSecond);
    }

    public void setBooleanSecond(boolean booleanSecond) {
        this.booleanSecond = booleanSecond;
    }


    /**
     * A byte property.
     */
    private byte byteProperty = (byte) 121;

    public byte getByteProperty() {
        return (this.byteProperty);
    }

    public void setByteProperty(byte byteProperty) {
        this.byteProperty = byteProperty;
    }


    /**
     * A double property.
     */
    private double doubleProperty = 321.0;

    public double getDoubleProperty() {
        return (this.doubleProperty);
    }

    public void setDoubleProperty(double doubleProperty) {
        this.doubleProperty = doubleProperty;
    }


    /**
     * An "indexed property" accessible via both array and subscript
     * based getters and setters.
     */
    private String dupProperty[] =
    { "Dup 0", "Dup 1", "Dup 2", "Dup 3", "Dup 4" };

    public String[] getDupProperty() {
        return (this.dupProperty);
    }

    public String getDupProperty(int index) {
        return (this.dupProperty[index]);
    }

    public void setDupProperty(int index, String value) {
        this.dupProperty[index] = value;
    }

    public void setDupProperty(String dupProperty[]) {
        this.dupProperty = dupProperty;
    }


    /**
     * A float property.
     */
    private float floatProperty = (float) 123.0;

    public float getFloatProperty() {
        return (this.floatProperty);
    }

    public void setFloatProperty(float floatProperty) {
        this.floatProperty = floatProperty;
    }


    /**
     * An integer array property accessed as an array.
     */
    private int intArray[] = { 0, 10, 20, 30, 40 };

    public int[] getIntArray() {
        return (this.intArray);
    }

    public void setIntArray(int intArray[]) {
        this.intArray = intArray;
    }


    /**
     * An integer array property accessed as an indexed property.
     */
    private int intIndexed[] = { 0, 10, 20, 30, 40 };

    public int getIntIndexed(int index) {
        return (intIndexed[index]);
    }

    public void setIntIndexed(int index, int value) {
        intIndexed[index] = value;
    }


    /**
     * An integer property.
     */
    private int intProperty = 123;

    public int getIntProperty() {
        return (this.intProperty);
    }

    public void setIntProperty(int intProperty) {
        this.intProperty = intProperty;
    }


    /**
     * A List property accessed as an indexed property.
     */
    private static List listIndexed = new ArrayList();

    static {
        listIndexed.add("String 0");
        listIndexed.add("String 1");
        listIndexed.add("String 2");
        listIndexed.add("String 3");
        listIndexed.add("String 4");
    }

    public List getListIndexed() {
        return (listIndexed);
    }


    /**
     * A long property.
     */
    private long longProperty = 321;

    public long getLongProperty() {
        return (this.longProperty);
    }

    public void setLongProperty(long longProperty) {
        this.longProperty = longProperty;
    }


    /**
     * A mapped property with only a getter and setter for a Map.
     */
    private Map mapProperty = null;

    public Map getMapProperty() {
        // Create the map the very first time
        if (mapProperty == null) {
            mapProperty = new HashMap();
            mapProperty.put("First Key", "First Value");
            mapProperty.put("Second Key", "Second Value");
        }
        return (mapProperty);
    }

    public void setMapProperty(Map mapProperty) {
        // Create the map the very first time
        if (mapProperty == null) {
            mapProperty = new HashMap();
            mapProperty.put("First Key", "First Value");
            mapProperty.put("Second Key", "Second Value");
        }
        this.mapProperty = mapProperty;
    }


    /**
     * A mapped property that has String keys and Object values.
     */
    private HashMap mappedObjects = null;

    public Object getMappedObjects(String key) {
        // Create the map the very first time
        if (mappedObjects == null) {
            mappedObjects = new HashMap();
            mappedObjects.put("First Key", "First Value");
            mappedObjects.put("Second Key", "Second Value");
        }
        return (mappedObjects.get(key));
    }

    public void setMappedObjects(String key, Object value) {
        // Create the map the very first time
        if (mappedObjects == null) {
            mappedObjects = new HashMap();
            mappedObjects.put("First Key", "First Value");
            mappedObjects.put("Second Key", "Second Value");
        }
        mappedObjects.put(key, value);
    }


    /**
     * A mapped property that has String keys and String values.
     */
    private HashMap mappedProperty = null;

    public String getMappedProperty(String key) {
        // Create the map the very first time
        if (mappedProperty == null) {
            mappedProperty = new HashMap();
            mappedProperty.put("First Key", "First Value");
            mappedProperty.put("Second Key", "Second Value");
        }
        return ((String) mappedProperty.get(key));
    }

    public void setMappedProperty(String key, String value) {
        // Create the map the very first time
        if (mappedProperty == null) {
            mappedProperty = new HashMap();
            mappedProperty.put("First Key", "First Value");
            mappedProperty.put("Second Key", "Second Value");
        }
        mappedProperty.put(key, value);
    }


    /**
     * A mapped property that has String keys and int values.
     */
    private HashMap mappedIntProperty = null;

    public int getMappedIntProperty(String key) {
        // Create the map the very first time
        if (mappedProperty == null) {
            mappedProperty = new HashMap();
            mappedProperty.put("One", new Integer(1));
            mappedProperty.put("Two", new Integer(2));
        }
        Integer x = (Integer) mappedIntProperty.get(key);
        return ((x == null) ? 0 : x.intValue());
    }

    public void setMappedIntProperty(String key, int value) {
        mappedIntProperty.put(key, new Integer(value));
    }


    /**
     * A nested reference to another test bean (populated as needed).
     */
    private TestBean nested = null;

    public TestBean getNested() {
        if (nested == null)
            nested = new TestBean();
        return (nested);
    }

   /**
    * Another nested reference to another test bean,
    */
   private TestBean anotherNested = null;
    
   public TestBean getAnotherNested() {
      return anotherNested;
   }
    
   public void setAnotherNested( TestBean anotherNested ) {
      this.anotherNested = anotherNested;
   }
   
    /*
     * Another nested reference to a bean containing mapp properties
     */
    class MappedTestBean {
        public void setValue(String key,String val) { }
        public String getValue(String key) { return "Mapped Value"; }
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

    public void setNullProperty(String nullProperty) {
        this.nullProperty = nullProperty;
    }


    /**
     * A read-only String property.
     */
    private String readOnlyProperty = "Read Only String Property";

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

    public void setShortProperty(short shortProperty) {
        this.shortProperty = shortProperty;
    }


    /**
     * A String array property accessed as a String.
     */
    private String stringArray[] =
            { "String 0", "String 1", "String 2", "String 3", "String 4" };

    public String[] getStringArray() {
        return (this.stringArray);
    }

    public void setStringArray(String stringArray[]) {
        this.stringArray = stringArray;
    }


    /**
     * A String array property accessed as an indexed property.
     */
    private String stringIndexed[] =
            { "String 0", "String 1", "String 2", "String 3", "String 4" };

    public String getStringIndexed(int index) {
        return (stringIndexed[index]);
    }

    public void setStringIndexed(int index, String value) {
        stringIndexed[index] = value;
    }


    /**
     * A String property.
     */
    private String stringProperty = "This is a string";

    public String getStringProperty() {
        return (this.stringProperty);
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }


    /**
     * A write-only String property.
     */
    private String writeOnlyProperty = "Write Only String Property";

    public String getWriteOnlyPropertyValue() {
        return (this.writeOnlyProperty);
    }

    public void setWriteOnlyProperty(String writeOnlyProperty) {
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

    public void setInvalidBoolean(String invalidBoolean) {
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
    public static void incrementCounter(int amount) {

        counter += amount;

    }


}
