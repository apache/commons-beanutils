/*
 * Copyright 2001-2004 The Apache Software Foundation.
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


package org.apache.commons.beanutils.priv;


/**
 * Bean that has a private constructor that exposes properties via
 * various mechanisms (based on property name):
 * <ul>
 * <li><strong>foo</strong> - Via direct public method
 * <li><strong>bar</strong> - Via directly implemented interface
 * <li><strong>baz</strong> - Via indirectly implemented interface
 * </ul>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

class PrivateBean implements PrivateDirect {


    // ----------------------------------------------------------- Constructors


    /**
     * Package private constructor - can only use factory method to create
     * beans.
     */
    PrivateBean() {

        super();

    }


    // ------------------------------------------------------------- Properties


    /**
     * A directly implemented property.
     */
    private String foo = "This is foo";

    public String getFoo() {

        return (this.foo);

    }


    /**
     * A property accessible via a directly implemented interface.
     */
    private String bar = "This is bar";

    public String getBar() {

        return (this.bar);

    }


    /**
     * A method accessible via a directly implemented interface.
     */
    public String methodBar(String in) {

        return (in);

    }


    /**
     * A property accessible via an indirectly implemented interface.
     */
    private String baz = "This is baz";

    public String getBaz() {

        return (this.baz);

    }


    /**
     * A method accessible via an indirectly implemented interface.
     */
    public String methodBaz(String in) {

        return (in);

    }


}
