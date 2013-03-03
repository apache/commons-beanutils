package org.apache.commons.beanutils.bugs;

import org.apache.commons.beanutils.PropertyUtils;

import java.util.ArrayList;

import junit.framework.TestCase;

public class Jira422TestCase extends TestCase {

    public void testRootBean() throws Exception {
        RootBean bean = new FirstChildBean();
        Class propertyType = PropertyUtils.getPropertyType(bean, "file[0]");
        assertEquals(String.class.getName(), propertyType.getName());
    }

    public void testSecondChildBean() throws Exception {
        RootBean bean = new SecondChildBean();
        Class propertyType = PropertyUtils.getPropertyType(bean, "file[0]");
        assertEquals(String.class.getName(), propertyType.getName());
    }

}

class RootBean {

    private ArrayList file;

    public ArrayList getFile() {
        return file;
    }

    public void setFile(ArrayList file) {
        this.file = file;
    }

    public String getFile(int i) {
        return (String) file.get(i);
    }

    public void setFile(int i, String file) {
        this.file.set(i, file);
    }

}

class FirstChildBean extends RootBean {
}

class SecondChildBean extends RootBean {
}
