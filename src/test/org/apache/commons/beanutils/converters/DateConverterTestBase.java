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

package org.apache.commons.beanutils.converters;

import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConversionException;

/**
 * Abstract base for &lt;Date&gt;Converter classes.
 *
 * @version $Revision$ $Date$
 */

public abstract class DateConverterTestBase extends TestCase {

    // ------------------------------------------------------------------------

    public DateConverterTestBase(String name) {
        super(name);
    }

    // ------------------------------------------------------------------------

    protected abstract Converter makeConverter();
    protected abstract Class getExpectedType();

    // ------------------------------------------------------------------------

    /**
     * Assumes ConversionException in response to covert(getExpectedType(), null).
     */
    public void testConvertNull() throws Exception {
        try {
            makeConverter().convert(getExpectedType(), null);
            fail("Expected ConversionException");
        } catch(ConversionException e) {
            // expected
        }
    }

    /**
     * Assumes convert() returns some non-null
     * instance of getExpectedType().
     */
    public void testConvertDate() throws Exception {
        String[] message= {
            "from Date",
            "from Calendar",
            "from SQL Date",
            "from SQL Time",
            "from SQL Timestamp"
        };

        long now = System.currentTimeMillis();

        Object[] date = {
            new Date(now),
            new java.util.GregorianCalendar(),
            new java.sql.Date(now),
            new java.sql.Time(now),
            new java.sql.Timestamp(now)
        };
        
        // Initialize calendar also with same ms to avoid a failing test in a new time slice
        ((GregorianCalendar)date[1]).setTimeInMillis(now);

        for (int i = 0; i < date.length; i++) {
            Object val = makeConverter().convert(getExpectedType(), date[i]);
            assertNotNull("Convert " + message[i] + " should not be null", val);
            assertTrue("Convert " + message[i] + " should return a " + getExpectedType().getName(),
                       getExpectedType().isInstance(val));
            assertEquals("Convert " + message[i] + " should return a " + date[0],
                         now, ((Date) val).getTime());
        }
    }
}
