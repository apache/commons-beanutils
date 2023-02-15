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

/**
 * Plain old java bean (POJO) for microbenchmarks.
 *
 */

public class BenchBean {

    /**
     * A boolean property.
     */
    private boolean booleanProperty = true;

    /**
     * A byte property.
     */
    private byte byteProperty = (byte) 121;

    /**
     * A double property.
     */
    private double doubleProperty = 321.0;

    /**
     * A float property.
     */
    private float floatProperty = (float) 123.0;

    /**
     * An integer property.
     */
    private int intProperty = 123;

    /**
     * A long property.
     */
    private long longProperty = 321;

    /**
     * A short property.
     */
    private short shortProperty = (short) 987;

    /**
     * A String property.
     */
    private String stringProperty = "This is a string";

    public boolean getBooleanProperty() {
        return booleanProperty;
    }

    public byte getByteProperty() {
        return this.byteProperty;
    }

    public double getDoubleProperty() {
        return this.doubleProperty;
    }

    public float getFloatProperty() {
        return this.floatProperty;
    }

    public int getIntProperty() {
        return this.intProperty;
    }

    public long getLongProperty() {
        return this.longProperty;
    }

    public short getShortProperty() {
        return this.shortProperty;
    }

    public String getStringProperty() {
        return this.stringProperty;
    }

    public void setBooleanProperty(final boolean booleanProperty) {
        this.booleanProperty = booleanProperty;
    }

    public void setByteProperty(final byte byteProperty) {
        this.byteProperty = byteProperty;
    }

    public void setDoubleProperty(final double doubleProperty) {
        this.doubleProperty = doubleProperty;
    }

    public void setFloatProperty(final float floatProperty) {
        this.floatProperty = floatProperty;
    }

    public void setIntProperty(final int intProperty) {
        this.intProperty = intProperty;
    }

    public void setLongProperty(final long longProperty) {
        this.longProperty = longProperty;
    }

    public void setShortProperty(final short shortProperty) {
        this.shortProperty = shortProperty;
    }

    public void setStringProperty(final String stringProperty) {
        this.stringProperty = stringProperty;
    }

}
