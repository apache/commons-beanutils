/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/PropertyUtilsBenchCase.java,v 1.1 2003/08/02 20:37:16 craigmcc Exp $
 * $Revision: 1.1 $
 * $Date: 2003/08/02 20:37:16 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


package org.apache.commons.beanutils;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * JUnit Test Case containing microbenchmarks for PropertyUtils.
 */

public class PropertyUtilsBenchCase extends TestCase {


    // ------------------------------------------------------------ Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public PropertyUtilsBenchCase(String name) {

        super(name);

    }


    // ------------------------------------------------------ Instance Variables


    // Basic loop counter
    private long counter = 100000;

    // DynaClass for inDyna and outDyna
    private DynaClass dynaClass = null;

    // Input objects that have identical sets of properties and values.
    private BenchBean inBean = null;
    private DynaBean inDyna = null;
    private Map inMap = null;

    // Output objects that have identical sets of properties.
    private BenchBean outBean = null;
    private DynaBean outDyna = null;

    // PropertyUtilsBean instance to be used
    private PropertyUtilsBean pu = null;


    // ---------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {

        // Set up loop counter (if property specified)
        String prop = System.getProperty("counter");
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
        inMap = new HashMap();
        inMap.put("booleanProperty", new Boolean(inBean.getBooleanProperty()));
        inMap.put("byteProperty", new Byte(inBean.getByteProperty()));
        inMap.put("doubleProperty", new Double(inBean.getDoubleProperty()));
        inMap.put("floatProperty", new Float(inBean.getFloatProperty()));
        inMap.put("intProperty", new Integer(inBean.getIntProperty()));
        inMap.put("longProperty", new Long(inBean.getLongProperty()));
        inMap.put("shortProperty", new Short(inBean.getShortProperty()));
        inMap.put("stringProperty", inBean.getStringProperty());
        inDyna = dynaClass.newInstance();
        Iterator inKeys = inMap.keySet().iterator();
        while (inKeys.hasNext()) {
            String inKey = (String) inKeys.next();
            inDyna.set(inKey, inMap.get(inKey));
        }

        // Create output instances
        outBean = new BenchBean();
        outDyna = dynaClass.newInstance();
        Iterator outKeys = inMap.keySet().iterator();
        while (outKeys.hasNext()) {
            String outKey = (String) outKeys.next();
            outDyna.set(outKey, inMap.get(outKey));
        }

        // Set up PropertyUtilsBean instance we will use
        pu = PropertyUtilsBean.getInstance();

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(PropertyUtilsBenchCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        dynaClass = null;
        inBean = null;
        inDyna = null;
        inMap = null;
        outBean = null;
        outDyna = null;
        pu = null;

    }



    // ------------------------------------------------- Individual Test Methods


    // Time copyProperties() from a bean
    public void testCopyPropertiesBean() throws Exception {

        long start;
        long stop;

        // Bean->Bean
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outBean, inBean);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outBean, inBean);
        }
        stop = System.currentTimeMillis();
        System.err.println("PU.copyProperties(bean,bean), count=" + counter +
                           ", time=" + (stop - start));

        // Bean->Dyna
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outDyna, inBean);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outDyna, inBean);
        }
        stop = System.currentTimeMillis();
        System.err.println("PU.copyProperties(dyna,bean), count=" + counter +
                           ", time=" + (stop - start));

    }


    // Time copyProperties() from a DynaBean
    public void testCopyPropertiesDyna() throws Exception {

        long start;
        long stop;

        // Dyna->Bean
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outBean, inDyna);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outBean, inDyna);
        }
        stop = System.currentTimeMillis();
        System.err.println("PU.copyProperties(bean,dyna), count=" + counter +
                           ", time=" + (stop - start));

        // Dyna->Dyna
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outDyna, inDyna);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outDyna, inDyna);
        }
        stop = System.currentTimeMillis();
        System.err.println("PU.copyProperties(dyna,dyna), count=" + counter +
                           ", time=" + (stop - start));

    }


    // Time copyProperties() from a Map
    public void testCopyPropertiesMap() throws Exception {

        long start;
        long stop;

        // Dyna->Bean
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outBean, inMap);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outBean, inMap);
        }
        stop = System.currentTimeMillis();
        System.err.println("PU.copyProperties(bean, map), count=" + counter +
                           ", time=" + (stop - start));

        // Dyna->Dyna
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outDyna, inMap);
        }
        start = System.currentTimeMillis();
        for (long i = 0; i < counter; i++) {
            pu.copyProperties(outDyna, inMap);
        }
        stop = System.currentTimeMillis();
        System.err.println("PU.copyProperties(dyna, map), count=" + counter +
                           ", time=" + (stop - start));

    }


    // --------------------------------------------------------- Support Methods


}
