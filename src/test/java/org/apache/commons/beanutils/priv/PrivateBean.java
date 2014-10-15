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
 * @version $Id$
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
    private final String foo = "This is foo";

    public String getFoo() {

        return (this.foo);

    }


    /**
     * A property accessible via a directly implemented interface.
     */
    private final String bar = "This is bar";

    public String getBar() {

        return (this.bar);

    }


    /**
     * A method accessible via a directly implemented interface.
     */
    public String methodBar(final String in) {

        return (in);

    }


    /**
     * A property accessible via an indirectly implemented interface.
     */
    private final String baz = "This is baz";

    public String getBaz() {

        return (this.baz);

    }


    /**
     * A method accessible via an indirectly implemented interface.
     */
    public String methodBaz(final String in) {

        return (in);

    }


}
