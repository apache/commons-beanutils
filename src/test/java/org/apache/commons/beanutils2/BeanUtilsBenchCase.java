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
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * JUnit Test Case containing microbenchmarks for BeanUtils.
 *
 */

public class BeanUtilsBenchCase extends TestCase {


    


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public BeanUtilsBenchCase(final String name) {

        super(name);

    }


    


    // Basic loop counter
    private long counter = 100000;

    // DynaClass for inDyna and outDyna
    private DynaClass dynaClass = null;

    // Input objects that have identical sets of properties and values.
    private BenchBean inBean = null;
    private DynaBean inDyna = null;
    private Map<String, Object> inMap = null;  // Map of Objects requiring no conversion
    private Map<String, String> inStrs = null; // Map of Strings requiring conversion

    // Output objects that have identical sets of properties.
    private BenchBean outBean = null;
    private DynaBean outDyna = null;

    // BeanUtilsBean instance to be used
    private BeanUtilsBean bu = null;


    


    /**
     * Set up instance variables required by this test case.
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
        Iterator<String> inKeys = inMap.keySet().iterator();
        while (inKeys.hasNext()) {
            final String inKey = inKeys.next();
            inDyna.set(inKey, inMap.get(inKey));
        }
        inStrs = new HashMap<>();
        inKeys = inMap.keySet().iterator();
        while (inKeys.hasNext()) {
            final String inKey = inKeys.next();
            inStrs.put(inKey, inMap.get(inKey).toString());
        }

        // Create output instances
        outBean = new BenchBean();
        outDyna = dynaClass.newInstance();
        final Iterator<String> outKeys = inMap.keySet().iterator();
        while (outKeys.hasNext()) {
            final String outKey = outKeys.next();
            outDyna.set(outKey, inMap.get(outKey));
        }

        // Set up BeanUtilsBean instance we will use
        bu = BeanUtilsBean.getInstance();

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return new TestSuite(BeanUtilsBenchCase.class);

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
        bu = null;

    }



    


    // Time copyProperties() from a bean
    public void testCopyPropertiesBean() throws Exception {

        long start;
        long stop;

        // Bean->Bean
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outBean, inBean);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outBean, inBean);
        }
        stop = System.currentTimeMillis();
        System.err.println("BU.copyProperties(bean,bean), count=" + counter +
                           ", time=" + (stop - start));

        // Bean->Dyna
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outDyna, inBean);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outDyna, inBean);
        }
        stop = System.currentTimeMillis();
        System.err.println("BU.copyProperties(dyna,bean), count=" + counter +
                           ", time=" + (stop - start));

    }


    // Time copyProperties() from a DynaBean
    public void testCopyPropertiesDyna() throws Exception {

        long start;
        long stop;

        // Dyna->Bean
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outBean, inDyna);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outBean, inDyna);
        }
        stop = System.currentTimeMillis();
        System.err.println("BU.copyProperties(bean,dyna), count=" + counter +
                           ", time=" + (stop - start));

        // Dyna->Dyna
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outDyna, inDyna);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outDyna, inDyna);
        }
        stop = System.currentTimeMillis();
        System.err.println("BU.copyProperties(dyna,dyna), count=" + counter +
                           ", time=" + (stop - start));

    }


    // Time copyProperties() from a Map of Objects
    public void testCopyPropertiesMap() throws Exception {

        long start;
        long stop;

        // Map->Bean
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outBean, inMap);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outBean, inMap);
        }
        stop = System.currentTimeMillis();
        System.err.println("BU.copyProperties(bean, map), count=" + counter +
                           ", time=" + (stop - start));

        // Map->Dyna
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outDyna, inMap);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outDyna, inMap);
        }
        stop = System.currentTimeMillis();
        System.err.println("BU.copyProperties(dyna, map), count=" + counter +
                           ", time=" + (stop - start));

    }


    // Time copyProperties() from a Map of Strings
    public void testCopyPropertiesStrs() throws Exception {

        long start;
        long stop;

        // Strs->Bean
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outBean, inStrs);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outBean, inStrs);
        }
        stop = System.currentTimeMillis();
        System.err.println("BU.copyProperties(bean,strs), count=" + counter +
                           ", time=" + (stop - start));

        // Strs->Dyna
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outDyna, inStrs);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            bu.copyProperties(outDyna, inStrs);
        }
        stop = System.currentTimeMillis();
        System.err.println("BU.copyProperties(dyna,strs), count=" + counter +
                           ", time=" + (stop - start));

    }


    // Time populate() from a Map of Objects
    public void testPopulateMap() throws Exception {

        long start;
        long stop;

        // Map->Bean
        for (long i = 0; i < counter; i++) {
            bu.populate(outBean, inMap);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            bu.populate(outBean, inMap);
        }
        stop = System.currentTimeMillis();
        System.err.println("BU.populate(bean, map), count=" + counter +
                           ", time=" + (stop - start));

        // Map->Dyna
        for (long i = 0; i < counter; i++) {
            bu.populate(outDyna, inMap);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            bu.populate(outDyna, inMap);
        }
        stop = System.currentTimeMillis();
        System.err.println("BU.populate(dyna, map), count=" + counter +
                           ", time=" + (stop - start));

    }


    // Time populate() from a Map of Strings
    // NOTE - This simulates what Struts does when processing form beans
    public void testPopulateStrs() throws Exception {

        long start;
        long stop;

        // Strs->Bean
        for (long i = 0; i < counter; i++) {
            bu.populate(outBean, inStrs);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            bu.populate(outBean, inStrs);
        }
        stop = System.currentTimeMillis();
        System.err.println("BU.populate(bean,strs), count=" + counter +
                           ", time=" + (stop - start));

        // Strs->Dyna
        for (long i = 0; i < counter; i++) {
            bu.populate(outDyna, inStrs);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            bu.populate(outDyna, inStrs);
        }
        stop = System.currentTimeMillis();
        System.err.println("BU.populate(dyna,strs), count=" + counter +
                           ", time=" + (stop - start));

    }


    


}
