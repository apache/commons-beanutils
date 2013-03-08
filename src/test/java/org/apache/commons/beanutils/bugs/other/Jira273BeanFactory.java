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
package org.apache.commons.beanutils.bugs.other;

import org.apache.commons.beanutils.bugs.Jira273TestCase;

/**
 * Factory which creates beans for {@link Jira273TestCase}.
 *
 * @version $Id$
 */
public class Jira273BeanFactory {

    /**
     * Factory method which creates annonymous
     * {@link PublicBeanWithMethod} with method NOT overriden.
     *
     * @return a new annonymous {@link PublicBeanWithMethod}.
     */
    public static Object createAnnonymousOverriden() {
        return new PublicBeanWithMethod() {
            @Override
            public String getBeanValue() {
                return "AnnonymousOverriden";
            }
        };
    }

    /**
     * Factory method which creates annonymous
     * {@link PublicBeanWithMethod} with method overriden.
     *
     * @return a new annonymous {@link PublicBeanWithMethod}.
     */
    public static Object createAnnonymousNotOverriden() {
        return new PublicBeanWithMethod() {
        };
    }

    /**
     * Factory method which creates a PrivatePublicOverriden bean.
     *
     * @return a new a PrivatePublicOverriden bean.
     */
    public static Object createPrivatePublicOverriden() {
        return new PrivatePublicOverriden();
    }

    /**
     * Factory method which creates a PrivatePrivatePublicOverriden bean.
     *
     * @return a new a PrivatePrivatePublicOverriden bean.
     */
    public static Object createPrivatePrivatePublicOverriden() {
        return new PrivatePrivatePublicOverriden();
    }

    /**
     * Factory method which creates a PrivatePrivatePublicNotOverriden bean.
     *
     * @return a new a PrivatePrivatePublicNotOverriden bean.
     */
    public static Object createPrivatePrivatePublicNotOverriden() {
        return new PrivatePrivatePublicNotOverriden();
    }

    /**
     * Factory method which creates a PrivatePublicNotOverriden bean.
     *
     * @return a new a PrivatePublicNotOverriden bean.
     */
    public static Object createPrivatePublicNotOverriden() {
        return new PrivatePublicNotOverriden();
    }

    private static class PrivateBeanWithMethod {
        public String getBeanValue() {
            return "PrivateBeanWithMethod";
        }
    }

    public static class PublicBeanWithMethod {
        public String getBeanValue() {
            return "PublicBeanWithMethod";
        }
    }
    private static class PrivatePublicOverriden extends PublicBeanWithMethod {
        @Override
        public String getBeanValue() {
            return "PrivatePublicOverriden";
        }
    }
    private static class PrivatePublicNotOverriden extends PublicBeanWithMethod {
    }
    private static class PrivatePrivatePublicOverriden extends PrivatePublicNotOverriden {
        @Override
        public String getBeanValue() {
            return "PrivatePrivatePublicOverriden";
        }
    }
    private static class PrivatePrivatePublicNotOverriden extends PrivatePublicNotOverriden {
    }
}
