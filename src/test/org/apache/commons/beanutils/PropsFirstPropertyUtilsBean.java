/*
 * Created on 29/05/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.commons.beanutils;

import java.util.Map;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * A PropertyUtilsBean which customises the behaviour of the
 * setNestedProperty and getNestedProperty methods to look for
 * simple properties in preference to map entries.
 */
public class PropsFirstPropertyUtilsBean extends PropertyUtilsBean {

    public PropsFirstPropertyUtilsBean() {
        super();
    }
    
    /**
     * Note: this is a *very rough* override of this method. In particular,
     * it does not handle MAPPED_DELIM and INDEXED_DELIM chars in the
     * propertyName, so propertyNames like "a(b)" or "a[3]" will not
     * be correctly handled.
     */
    protected Object getPropertyOfMapBean(Map bean, String propertyName)
    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, propertyName);
        if (descriptor == null) {
            // no simple property exists so return the value from the map
            return bean.get(propertyName);
        } else {
            // a simple property exists so return its value instead.
            return getSimpleProperty(bean, propertyName);
        }
    }

    /**
     * Note: this is a *very rough* override of this method. In particular,
     * it does not handle MAPPED_DELIM and INDEXED_DELIM chars in the
     * propertyName, so propertyNames like "a(b)" or "a[3]" will not
     * be correctly handled.
     */
    protected void setPropertyOfMapBean(Map bean, String propertyName, Object value)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, propertyName);
        if (descriptor == null) {
            // no simple property exists so put the value into the map
            bean.put(propertyName, value);
        } else {
            // a simple property exists so set that instead.
            setSimpleProperty(bean, propertyName, value);
        }
    }
}
