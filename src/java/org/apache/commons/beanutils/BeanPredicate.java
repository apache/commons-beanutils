/*
 * Copyright 2004 The Apache Software Foundation.
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

package org.apache.commons.beanutils;

import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;

public class BeanPredicate implements Predicate {
   
    private final Log log = LogFactory.getLog(this.getClass());

    private String propertyName;
    private Predicate predicate;

    public BeanPredicate(String propertyName, Predicate predicate) {
        this.propertyName = propertyName;
        this.predicate = predicate;
    }

    public boolean evaluate(Object object) {
       
        boolean evaluation = false;

        try {
            Object propValue = PropertyUtils.getProperty( object, propertyName );
            evaluation = predicate.evaluate(propValue);
        } catch (IllegalArgumentException e) {
            final String errorMsg = "Problem during evaluation.";
            log.error("ERROR: " + errorMsg, e);
            throw e;
        } catch (IllegalAccessException e) {
            final String errorMsg = "Unable to access the property provided.";
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg);
        } catch (InvocationTargetException e) {
            final String errorMsg = "Exception occurred in property's getter";
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg);
        } catch (NoSuchMethodException e) {
            final String errorMsg = "Property not found.";
            log.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg);
        }

        return evaluation;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

}
