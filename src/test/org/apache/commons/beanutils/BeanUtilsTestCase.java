/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/BeanUtilsTestCase.java,v 1.2 2001/07/14 23:54:50 craigmcc Exp $
 * $Revision: 1.2 $
 * $Date: 2001/07/14 23:54:50 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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

import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * <p>
 *  Test Case for the BeanUtils class.  The majority of these tests use
 *  instances of the TestBean class, so be sure to update the tests if you
 *  change the characteristics of that class.
 * </p>
 *
 * <p>
 *  Template for this stolen from Craigs PropertyUtilsTestCase
 * </p>
 *
 * <p>
 *   Note that the tests are dependant upon the static aspects
 *   (such as array sizes...) of the TestBean.java class, so ensure 
 *   than all changes to TestBean are reflected here.
 * </p>
 *
 * <p>
 *  So far, this test case has tests for the following methods of the
 *  <code>BeanUtils</code> class:
 * </p>
 * <ul>
 *   <li>getArrayProperty(Object bean, String name)</li>
 * </ul>
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Revision: 1.2 $
 */

public class BeanUtilsTestCase extends TestCase 
{

    // ---------------------------------------------------- Instance Variables

    /**
     * The test bean for each test.
     */
    protected TestBean bean = null;


    // ---------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public BeanUtilsTestCase(String name) 
    {
        super(name);
    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() 
    {
        bean = new TestBean();
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() 
    {
        return (new TestSuite(BeanUtilsTestCase.class));
    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() 
    {
        bean = null;
    }


    // ------------------------------------------------ Individual Test Methods


    /**
     *  tests the string and int arrays of TestBean
     */
    public void testGetArrayProperty()
    {
        try
        {
            String arr[] = BeanUtils.getArrayProperty( bean, "stringArray");
            String comp[] = bean.getStringArray();

            assertTrue("String array length = " + comp.length, 
                   ( comp.length == arr.length ));
        
            arr = BeanUtils.getArrayProperty( bean, "intArray");
            int iarr[] = bean.getIntArray();

            assertTrue("String array length = " + iarr.length, 
                   ( iarr.length == arr.length ));
        }
        catch( IllegalAccessException e)
        {
            fail("IllegalAccessException");
        }
        catch( InvocationTargetException e )
        {
            fail("InvocationTargetException");
        }
        catch( NoSuchMethodException e )
        {
            fail("NoSuchMethodException");
        }

    }


    /**
     *  tests getting an indexed property
     */
    public void testGetIndexedProperty1()
    {
        try
        {
            String val = BeanUtils.getIndexedProperty( bean, "intIndexed[3]");
            String comp =  String.valueOf( bean.getIntIndexed(3));
            assertTrue("intIndexed[3] == " + comp, val.equals( comp ));  
                  
            val = BeanUtils.getIndexedProperty( bean, "stringIndexed[3]");
            comp = bean.getStringIndexed(3);
            assertTrue("stringIndexed[3] == " + comp, val.equals( comp ) );
        }
        catch( IllegalAccessException e)
        {
            fail("IllegalAccessException");
        }
        catch( InvocationTargetException e )
        {
            fail("InvocationTargetException");
        }
        catch( NoSuchMethodException e )
        {
            fail("NoSuchMethodException");
        }
    }
    
    /**
     *  tests getting an indexed property
     */
    public void testGetIndexedProperty2()
    {
        try
        {
            String val  = BeanUtils.getIndexedProperty( bean, "intIndexed", 3);
            String comp =  String.valueOf(bean.getIntIndexed(3));

            assertTrue("intIndexed,3 == " + comp,   val.equals( comp ));

            val = BeanUtils.getIndexedProperty( bean, "stringIndexed",3);
            comp = bean.getStringIndexed(3);
           
            assertTrue("stringIndexed,3 == " + comp ,  val.equals(comp));
        
        }
        catch( IllegalAccessException e)
        {
            fail("IllegalAccessException");
        }
        catch( InvocationTargetException e )
        {
            fail("InvocationTargetException");
        }
        catch( NoSuchMethodException e )
        {
            fail("NoSuchMethodException");
        }
    }

    /**
     *  tests getting a nested property
     */
    public void testGetNestedProperty()
    {
        try
        {
            String val  = BeanUtils.getNestedProperty( bean, "nested.stringProperty");
            String comp =  bean.getNested().getStringProperty();
            assertTrue("nested.StringProperty == " + comp,  
                   val.equals( comp ));       
        }
        catch( IllegalAccessException e)
        {
            fail("IllegalAccessException");
        }
        catch( InvocationTargetException e )
        {
            fail("InvocationTargetException");
        }
        catch( NoSuchMethodException e )
        {
            fail("NoSuchMethodException");
        }
    }

    /**
     *  tests getting a 'whatever' property
     */
    public void testGetGeneralProperty()
    {
        try
        {
            String val  = BeanUtils.getProperty( bean, "nested.intIndexed[2]");
            String comp =  String.valueOf( bean.getIntIndexed(2) );

            assertTrue("nested.intIndexed[2] == " + comp,
                val.equals( comp ));       
        }
        catch( IllegalAccessException e)
        {
            fail("IllegalAccessException");
        }
        catch( InvocationTargetException e )
        {
            fail("InvocationTargetException");
        }
        catch( NoSuchMethodException e )
        {
            fail("NoSuchMethodException");
        }
    }

 /**
     *  tests getting a 'whatever' property
     */
    public void testGetSimpleProperty()
    {
        try
        {
            String val  = BeanUtils.getSimpleProperty( bean, "shortProperty");
            String comp = String.valueOf(bean.getShortProperty());

            assertTrue("shortProperty == " + comp,
                   val.equals(comp));     
        }
        catch( IllegalAccessException e)
        {
            fail("IllegalAccessException");
        }
        catch( InvocationTargetException e )
        {
            fail("InvocationTargetException");
        }
        catch( NoSuchMethodException e )
        {
            fail("NoSuchMethodException");
        }
    }
}

