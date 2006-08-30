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

/**
 * Bean that has primitive properties
 */
public class PrimitiveBean {

    private float _float;
    private double _double;
    private boolean _boolean;
    private long _long;
    private int _int;
    
    public float getFloat() {
        return _float;
    }
    
    public void setFloat(float _float) {
        this._float = _float;
    }
    
    public double getDouble() {
        return _double;
    }
    
    public void setDouble(double _double) {
        this._double = _double;
    }
    
    public boolean getBoolean() {
        return _boolean;
    }
    
    public void setBoolean(boolean _boolean) {
        this._boolean = _boolean;
    }
    
    public long getLong() {
        return _long;
    }
    
    public void setLong(long _long) {
        this._long = _long;
    }
    
    public int getInt() {
        return _int;
    }
    
    public void setInt(int _int) {
        this._int = _int;
    }
}
