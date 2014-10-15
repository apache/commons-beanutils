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

import java.util.ArrayList;
import java.util.List;

/**
 * Indexed Properties Test bean for JUnit tests for the "beanutils" component.
 *
 * @version $Id$
 */
public class IndexedTestBean {

    private String[] stringArray;
    private List<String> stringList;
    private ArrayList<Object> arrayList;


    // ----------------------------------------------------------- Constructors

    /**
     * Default Constructor.
     */
    public IndexedTestBean() {
    }

    /**
     * Getter for the String[] property.
     */
    public String[] getStringArray() {
        return stringArray;
    }

    /**
     * Setter for the String[] property.
     */
    public void setStringArray(final String[] stringArray) {
        this.stringArray = stringArray;
    }

    /**
     * Indexed Getter for the String[] property.
     */
    public String getStringArray(final int index) {
        return stringArray[index];
    }

    /**
     * Indexed Setter for the String[] property.
     */
    public void setStringArray(final int index, final String value) {
        stringArray[index] = value;
    }

    /**
     * Getter for the java.util.List property.
     */
    public List<String> getStringList() {
        return stringList;
    }

    /**
     * Setter for the java.util.List property.
     */
    public void setStringList(final List<String> stringList) {
        this.stringList = stringList;
    }

    /**
     * Indexed Getter for the java.util.List property.
     */
    public String getStringList(final int index) {
        return stringList.get(index);
    }

    /**
     * Indexed Setter for the java.util.List property.
     */
    public void setStringList(final int index, final String value) {
        stringList.add(index, value);
    }

    /**
     * Getter for the java.util.ArrayList property.
     */
    public ArrayList<Object> getArrayList() {
        return arrayList;
    }

    /**
     * Setter for the java.util.ArrayList property.
     */
    public void setArrayList(final ArrayList<Object> arrayList) {
        this.arrayList = arrayList;
    }

    /**
     * Indexed Getter for the java.util.ArrayList property.
     */
    public Object getArrayList(final int index) {
        return arrayList.get(index);
    }

    /**
     * Indexed Setter for the java.util.ArrayList property.
     */
    public void setArrayList(final int index, final Object value) {
        arrayList.add(index, value);
    }

}
