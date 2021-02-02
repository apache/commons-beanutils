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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit Test Case containing microbenchmarks for PropertyUtils.
 *
 */

public class PropertyUtilsBenchCase extends TestCase {



    /**
     * Constructs a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public PropertyUtilsBenchCase(final String name) {

        super(name);

    }



    // Basic loop counter
    private long counter = 100000;

    // DynaClass for inDyna and outDyna
    private DynaClass dynaClass = null;

    // Input objects that have identical sets of properties and values.
    private BenchBean inBean = null;
    private DynaBean inDyna = null;
    private Map<String, Object> inMap = null;

    // Output objects that have identical sets of properties.
    private BenchBean outBean = null;
    private DynaBean outDyna = null;

    // PropertyUtilsBean instance to be used
    private PropertyUtilsBean pu = null;



    /**
     * Sets up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {

        // Set up loop counter (if property specified)
        final String prop = System.getProperty("counter");
        if (prop != null) {
            counter = Long.parseLong(prop);
        }

        // Set up DynaClass for our DynaBean instances
        dynaClass = new BasicDynaClass
            ("BenchDynaClass", null,
             new DynaProperty[]{
                 new DynaProperty("booleanProperty", Boolean.TYPE),
                 new DynaProperty("byteProperty", Byte.TYPE),
                 new DynaProperty("doubleProperty", Double.TYPE),
                 new DynaProperty("floatProperty", Float.TYPE),
                 new DynaProperty("intProperty", Integer.TYPE),
                 new DynaProperty("longProperty", Long.TYPE),
                 new DynaProperty("shortProperty", Short.TYPE),
                 new DynaProperty("stringProperty", String.class),
             });

        // Create input instances
        inBean = new BenchBean();
        inMap = new HashMap<>();
        inMap.put("booleanProperty", new Boolean(inBean.getBooleanProperty()));
        inMap.put("byteProperty", new Byte(inBean.getByteProperty()));
        inMap.put("doubleProperty", new Double(inBean.getDoubleProperty()));
        inMap.put("floatProperty", new Float(inBean.getFloatProperty()));
        inMap.put("intProperty", new Integer(inBean.getIntProperty()));
        inMap.put("longProperty", new Long(inBean.getLongProperty()));
        inMap.put("shortProperty", new Short(inBean.getShortProperty()));
        inMap.put("stringProperty", inBean.getStringProperty());
        inDyna = dynaClass.newInstance();
        for (final Map.Entry<String, Object> e : inMap.entrySet()) {
            inDyna.set(e.getKey(), e.getValue());
        }

        // Create output instances
        outBean = new BenchBean();
        outDyna = dynaClass.newInstance();
        inDyna = dynaClass.newInstance();
        for (final Map.Entry<String, Object> e : inMap.entrySet()) {
            outDyna.set(e.getKey(), e.getValue());
        }

        // Set up PropertyUtilsBean instance we will use
        pu = PropertyUtilsBean.getInstance();

    }

    /**
     * Creates the tests included in this test suite.
     */
    public static Test suite() {

        return new TestSuite(PropertyUtilsBenchCase.class);

    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {

        dynaClass = null;
        inBean = null;
        inDyna = null;
        inMap = null;
        outBean = null;
        outDyna = null;
        pu = null;

    }



    // Time copyProperties() from a bean
    public void testCopyPropertiesBean() throws Exception {

        long startMillis;
        long stopMillis;

        // Bean->Bean
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outBean, inBean);
        }
        startMillis = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outBean, inBean);
        }
        stopMillis = System.currentTimeMillis();
        System.err.println("PU.copyProperties(bean,bean), count=" + counter +
                           ", time=" + (stopMillis - startMillis));

        // Bean->Dyna
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outDyna, inBean);
        }
        startMillis = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outDyna, inBean);
        }
        stopMillis = System.currentTimeMillis();
        System.err.println("PU.copyProperties(dyna,bean), count=" + counter +
                           ", time=" + (stopMillis - startMillis));

    }

    // Time copyProperties() from a DynaBean
    public void testCopyPropertiesDyna() throws Exception {

        long startMillis;
        long stopMillis;

        // Dyna->Bean
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outBean, inDyna);
        }
        startMillis = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outBean, inDyna);
        }
        stopMillis = System.currentTimeMillis();
        System.err.println("PU.copyProperties(bean,dyna), count=" + counter +
                           ", time=" + (stopMillis - startMillis));

        // Dyna->Dyna
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outDyna, inDyna);
        }
        startMillis = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outDyna, inDyna);
        }
        stopMillis = System.currentTimeMillis();
        System.err.println("PU.copyProperties(dyna,dyna), count=" + counter +
                           ", time=" + (stopMillis - startMillis));

    }

    // Time copyProperties() from a Map
    public void testCopyPropertiesMap() throws Exception {

        long startMillis;
        long stopMillis;

        // Dyna->Bean
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outBean, inMap);
        }
        startMillis = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outBean, inMap);
        }
        stopMillis = System.currentTimeMillis();
        System.err.println("PU.copyProperties(bean, map), count=" + counter +
                           ", time=" + (stopMillis - startMillis));

        // Dyna->Dyna
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outDyna, inMap);
        }
        startMillis = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outDyna, inMap);
        }
        stopMillis = System.currentTimeMillis();
        System.err.println("PU.copyProperties(dyna, map), count=" + counter +
                           ", time=" + (stopMillis - startMillis));

    }



}
