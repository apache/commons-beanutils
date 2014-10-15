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
 * Converter implementation that throws a <code>PassTestException</code>
 * when convert is called.
 * The idea is that catching this exception is a clear signal that this method
 * has been called.
 *
 * @version $Id$
 */

public class ThrowExceptionConverter implements Converter {

    public <T> T convert(final Class<T> type, final Object value) {
        throw new PassTestException();
    }
}
