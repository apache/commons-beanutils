/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/test/org/apache/commons/beanutils/NestedTestBean.java,v 1.1 2002/09/28 09:31:55 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2002/09/28 09:31:55 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Specialist test bean for complex nested properties.
 *
 * @author Robert Burrell Donkin
 * @version $Revision: 1.1 $ $Date: 2002/09/28 09:31:55 $
 */

public class NestedTestBean {
    
    
    // ------------------------------------------------------------- Constructors
    public NestedTestBean(String name) {
        setName(name);
    }
    
    
    // ------------------------------------------------------------- Properties
    
    private String name;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    
    private String testString = "NOT SET";
    
    public String getTestString() {
        return testString;
    }
    
    public void setTestString(String testString) {
        this.testString = testString;
    }
    
    
    private boolean testBoolean = false;
    
    public boolean getTestBoolean() {
        return testBoolean;
    }
    
    public void setTestBoolean(boolean testBoolean) {
        this.testBoolean = testBoolean;
    }    
    
    
    private NestedTestBean indexedBeans[];
    
    public void init() {
        indexedBeans = new NestedTestBean[5];
        indexedBeans[0] = new NestedTestBean("Bean@0");
        indexedBeans[1] = new NestedTestBean("Bean@1"); 
        indexedBeans[2] = new NestedTestBean("Bean@2"); 
        indexedBeans[3] = new NestedTestBean("Bean@3"); 
        indexedBeans[4] = new NestedTestBean("Bean@4");
        
        simpleBean = new NestedTestBean("Simple Property Bean");
    };
    
    public NestedTestBean getIndexedProperty(int index) {
        return (this.indexedBeans[index]);
    }

    public void setIndexedProperty(int index, NestedTestBean value) {
        this.indexedBeans[index] = value;
    }
    
    private NestedTestBean simpleBean;
    
    public NestedTestBean getSimpleBeanProperty() {
        return simpleBean;
    }
    
    // ------------------------------------------------------- Static Variables

}
