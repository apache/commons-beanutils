/*
 * $Id$
 *
 * Copyright 2005 The Apache Software Foundation.
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

import java.util.List;
import java.util.ArrayList;

/**
 * Indexed Properties Test bean for JUnit tests for the "beanutils" component.
 *
 * @author Niall Pemberton
 * @version $Revision$ $Date$
 */
public class IndexedTestBean {

    private String[] stringArray;
    private List stringList;
    private ArrayList arrayList;


    // ----------------------------------------------------------- Constructors

    public IndexedTestBean() {
    }

    public String[] getStringArray() {
        return stringArray;
    }

    public void setStringArray(String[] stringArray) {
        this.stringArray = stringArray;
    }

    public String getStringArray(int index) {
        return (String)stringArray[index];
    }

    public void setStringArray(int index, String value) {
        stringArray[index] = value;
    }

    public List getStringList() {
        return stringList;
    }

    public void setStringList(List stringList) {
        this.stringList = stringList;
    }

    public String getStringList(int index) {
        return (String)stringList.get(index);
    }

    public void setStringList(int index, String value) {
        stringList.add(index, value);
    }

    public ArrayList getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList arrayList) {
        this.arrayList = arrayList;
    }

    public Object getArrayList(int index) {
        return arrayList.get(index);
    }

    public void setArrayList(int index, Object value) {
        arrayList.add(index, value);
    }

}
