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
package org.apache.commons.beanutils2.bugs.other;

import org.apache.commons.beanutils2.bugs.Jira273TestCase;

/**
 * Factory which creates beans for {@link Jira273TestCase}.
 *
 */
public class Jira273BeanFactory {

    /**
     * Factory method which creates anonymous
     * {@link PublicBeanWithMethod} with method NOT overridden.
     *
     * @return a new anonymous {@link PublicBeanWithMethod}.
     */
    public static Object createAnonymousOverridden() {
        return new PublicBeanWithMethod() {
            @Override
            public String getBeanValue() {
                return "AnonymousOverridden";
            }
        };
    }

    /**
     * Factory method which creates anonymous
     * {@link PublicBeanWithMethod} with method overridden.
     *
     * @return a new anonymous {@link PublicBeanWithMethod}.
     */
    public static Object createAnonymousNotOverridden() {
        return new PublicBeanWithMethod() {
        };
    }

    /**
     * Factory method which creates a PrivatePublicOverridden bean.
     *
     * @return a new a PrivatePublicOverridden bean.
     */
    public static Object createPrivatePublicOverridden() {
        return new PrivatePublicOverridden();
    }

    /**
     * Factory method which creates a PrivatePrivatePublicOverridden bean.
     *
     * @return a new a PrivatePrivatePublicOverridden bean.
     */
    public static Object createPrivatePrivatePublicOverridden() {
        return new PrivatePrivatePublicOverridden();
    }

    /**
     * Factory method which creates a PrivatePrivatePublicNotOverridden bean.
     *
     * @return a new a PrivatePrivatePublicNotOverridden bean.
     */
    public static Object createPrivatePrivatePublicNotOverridden() {
        return new PrivatePrivatePublicNotOverridden();
    }

    /**
     * Factory method which creates a PrivatePublicNotOverridden bean.
     *
     * @return a new a PrivatePublicNotOverridden bean.
     */
    public static Object createPrivatePublicNotOverridden() {
        return new PrivatePublicNotOverridden();
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
    private static class PrivatePublicOverridden extends PublicBeanWithMethod {
        @Override
        public String getBeanValue() {
            return "PrivatePublicOverridden";
        }
    }
    private static class PrivatePublicNotOverridden extends PublicBeanWithMethod {
    }
    private static class PrivatePrivatePublicOverridden extends PrivatePublicNotOverridden {
        @Override
        public String getBeanValue() {
            return "PrivatePrivatePublicOverridden";
        }
    }
    private static class PrivatePrivatePublicNotOverridden extends PrivatePublicNotOverridden {
    }
}
