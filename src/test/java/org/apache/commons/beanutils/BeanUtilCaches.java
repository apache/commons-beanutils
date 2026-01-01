/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils;

import org.apache.commons.beanutils.locale.LocaleBeanUtilsBean;

public class BeanUtilCaches {

    /**
     * Clears all the BeanUtils Caches manually.
     *
     * This is probably overkill, but since we're dealing with static caches
     * it seems sensible to ensure that all test cases start with a clean sheet.
     */
    public static void clear() {

        // Clear BeanUtilsBean's PropertyUtilsBean descriptor caches
        BeanUtilsBean.getInstance().getPropertyUtils().clearDescriptors();

        // Clear LocaleBeanUtilsBean's PropertyUtilsBean descriptor caches
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getPropertyUtils().clearDescriptors();

        // Clear MethodUtils's method cache
        MethodUtils.clearCache();

        // Clear WrapDynaClass cache
        WrapDynaClass.clear();

        // Replace the existing BeanUtilsBean instance for the current class loader with a new, clean instance
        BeanUtilsBean.setInstance(new BeanUtilsBean());

        // Replace the existing LocaleBeanUtilsBean instance for the current class loader with a new, clean instance
        LocaleBeanUtilsBean.setInstance(new LocaleBeanUtilsBean());
    }
}
