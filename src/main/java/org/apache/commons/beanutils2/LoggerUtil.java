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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggerUtil {

    @SuppressWarnings("rawtypes")
    public static Log createLoggerWithContextClassLoader(Class clazz) {
        ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
        Log log = null;
        try {
            Thread.currentThread().setContextClassLoader(clazz.getClassLoader());
            log = LogFactory.getLog(clazz);
        } finally {
            Thread.currentThread().setContextClassLoader(currentContextClassLoader);
        }

        if (log == null) {
            return LogFactory.getLog(clazz); // fallback -- is this needed?
        }

        return log;
    }
}
