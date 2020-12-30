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
package org.apache.commons.beanutils2.memoryleaktests.pojotests;

/**
 * Custom number implementation to test with converters.
 *
 */
public class CustomInteger extends Number {

    private final int i;

    /**
     * Constructs a new instance.
     */
    public CustomInteger() {
        this.i = 12345;
    }

    /**
     * Constructs a new instance.
     * @param i the integer value
     */
    public CustomInteger(final int i) {
        this.i = i;
    }

    /**
     * Gets the double value.
     * @return  the double value
     */
    @Override
    public double doubleValue() {
        return i;
    }

    /**
     * Gets the float value.
     * @return  the float value
     */
    @Override
    public float floatValue() {
        return i;
    }

    /**
     * Gets the integer value.
     * @return  the integer value
     */
    @Override
    public int intValue() {
        return i;
    }

    /**
     * Gets the long value.
     * @return  the long value
     */
    @Override
    public long longValue() {
        return i;
    }

    /**
     * Gets a String representation of this number.
     */
    @Override
    public String toString() {
        return Integer.toString(i);
    }
}
