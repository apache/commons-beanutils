/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/DynaProperty.java,v 1.8 2003/08/21 21:31:56 rdonkin Exp $
 * $Revision: 1.8 $
 * $Date: 2003/08/21 21:31:56 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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


import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.Map;


/**
 * <p>The metadata describing an individual property of a DynaBean.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.8 $ $Date: 2003/08/21 21:31:56 $
 */

public class DynaProperty implements Serializable {

    // ----------------------------------------------------------- Constants
    
    /*
     * There are issues with serializing primitive class types on certain JVM versions
     * (including java 1.3).
     * This class uses a custom serialization implementation that writes an integer
     * for these primitive class.
     * This list of constants are the ones used in serialization.
     * If these values are changed, then older versions will no longer be read correctly
     */
    private static final int BOOLEAN_TYPE = 1;
    private static final int BYTE_TYPE = 2;
    private static final int CHAR_TYPE = 3;
    private static final int DOUBLE_TYPE = 4;
    private static final int FLOAT_TYPE = 5;
    private static final int INT_TYPE = 6;
    private static final int LONG_TYPE = 7;
    private static final int SHORT_TYPE = 8;
    

    // ----------------------------------------------------------- Constructors


    /**
     * Construct a property that accepts any data type.
     *
     * @param name Name of the property being described
     */
    public DynaProperty(String name) {

        this(name, Object.class);

    }


    /**
     * Construct a property of the specified data type.
     *
     * @param name Name of the property being described
     * @param type Java class representing the property data type
     */
    public DynaProperty(String name, Class type) {

        super();
        this.name = name;
        this.type = type;

    }
    
    /**
     * Construct an indexed <code>DynaProperty</code> that supports (pseudo)-introspection
     * of the indexed property type.
     *
     * @param name Name of the property being described
     * @param type Java class representing the property data type
     * @param contentType Class that all indexed elements are instances of
     */
    public DynaProperty(String name, Class type, Class contentType) {

        super();
        this.name = name;
        this.type = type;
        this.contentType = contentType;
        
    }

    /**
     * Construct a mapped <code>DynaProperty</code> that supports (pseudo)-introspection
     * of the mapped content and key type.
     *
     * @param name Name of the property being described
     * @param type Java class representing the property data type
     * @param keyType Class that all keys are instances of
     * @param contentType Class that all mapped elements are instances of
     */
    public DynaProperty(String name, Class type, Class keyType, Class contentType) {

        super();
        this.name = name;
        this.type = type;
        this.keyType = keyType;
        this.contentType = contentType;
        
    }

    // ------------------------------------------------------------- Properties

    /** Property name */
    protected String name = null;
    /**
     * Get the name of this property.
     */
    public String getName() {
        return (this.name);
    }
    
    /** Property type */
    protected transient Class type = null;
    /**
     * <p>Gets the Java class representing the data type of the underlying property
     * values.</p>
     * 
     * <p>There are issues with serializing primitive class types on certain JVM versions
     * (including java 1.3).
     * Therefore, this field <strong>must not be serialized using the standard methods</strong>.</p>
     * 
     * <p><strong>Please leave this field as <code>transient</code></strong></p>
     */
    public Class getType() {
        return (this.type);
    }
    
    
    /** The <em>(optional)</em> type of content elements for indexed <code>DynaProperty</code> */
    protected transient Class contentType;
    /**
     * Gets the <em>(optional)</em> type of the indexed content for <code>DynaProperty</code>'s
     * that support this feature.
     *
     * <p>There are issues with serializing primitive class types on certain JVM versions
     * (including java 1.3).
     * Therefore, this field <strong>must not be serialized using the standard methods</strong>.</p>
     *
     * @return the Class for the content type if this is an indexed <code>DynaProperty</code> 
     * and this feature is supported. Otherwise null.
     */
    public Class getContentType() {
        return contentType;
    }
    
    
    /** The <em>(optional)</em> type of keys for mapped <code>DynaProperty</code>'s */
    protected transient Class keyType;
    /**
     * Gets the <em>(optional)</em> type of the key for mapped <code>DynaProperty</code>'s
     * that support this feature.
     *
     * <p>There are issues with serializing primitive class types on certain JVM versions
     * (including java 1.3).
     * Therefore, this field <strong>must not be serialized using the standard methods</strong>.</p>
     *
     * @return the Class for the key type if this is an mapped <code>DynaProperty</code> 
     * and this feature is supported. Otherwise null.
     */
    public Class getKeyType() {
        return keyType;
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Does this property represent an indexed value (ie an array or List)?
     */
    public boolean isIndexed() {

        if (type == null) {
            return (false);
        } else if (type.isArray()) {
            return (true);
        } else if (List.class.isAssignableFrom(type)) {
            return (true);
        } else {
            return (false);
        }

    }


    /**
     * Does this property represent a mapped value (ie a Map)?
     */
    public boolean isMapped() {

        if (type == null) {
            return (false);
        } else {
            return (Map.class.isAssignableFrom(type));
        }

    }


    /**
     * Return a String representation of this Object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("DynaProperty[name=");
        sb.append(this.name);
        sb.append(",type=");
        sb.append(this.type);
        if (isIndexed()) {
            sb.append(" <").append(this.contentType).append(">");
        } else if (isMapped()) {
            sb.append(" <").append(this.keyType).append(",").append(this.contentType).append(">");
        }
        sb.append("]");
        return (sb.toString());

    }

    // --------------------------------------------------------- Serialization helper methods
    
    /**
     * Writes this object safely.
     * There are issues with serializing primitive class types on certain JVM versions
     * (including java 1.3).
     * This method provides a workaround.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        
        writeAnyClass(this.type,out);
        
        if (isIndexed()) {
            writeAnyClass(this.contentType,out);
        }
        else if (isMapped()) {
            writeAnyClass(this.keyType,out);
            writeAnyClass(this.contentType,out);
        }
        
        // write out other values
        out.defaultWriteObject();
    }

    /**
     * Write a class using safe encoding to workaround java 1.3 serialization bug.
     */
    private void writeAnyClass(Class clazz, ObjectOutputStream out) throws IOException {
        // safely write out any class
        int primitiveType = 0;
        if (Boolean.TYPE.equals(clazz)) {
            primitiveType = BOOLEAN_TYPE;
        } else if (Byte.TYPE.equals(clazz)) {
            primitiveType = BYTE_TYPE;
        } else if (Character.TYPE.equals(clazz)) {
            primitiveType = CHAR_TYPE;
        } else if (Double.TYPE.equals(clazz)) {
            primitiveType = DOUBLE_TYPE;
        } else if (Float.TYPE.equals(clazz)) {
            primitiveType = FLOAT_TYPE;
        } else if (Integer.TYPE.equals(clazz)) {
            primitiveType = INT_TYPE;
        } else if (Long.TYPE.equals(clazz)) {
            primitiveType = LONG_TYPE;
        } else if (Short.TYPE.equals(clazz)) {
            primitiveType = SHORT_TYPE;
        }	
        
        if (primitiveType == 0) {
            // then it's not a primitive type
            out.writeBoolean(false);
            out.writeObject(clazz);
        } else {
            // we'll write out a constant instead
            out.writeBoolean(true);
            out.writeInt(primitiveType);
        }
    }
    
    /**
     * Reads field values for this object safely.
     * There are issues with serializing primitive class types on certain JVM versions
     * (including java 1.3).
     * This method provides a workaround.
     *
     * @throws StreamCorruptedException when the stream data values are outside expected range 
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        
        this.type = readAnyClass(in);
        
        if (isIndexed()) {
            this.contentType = readAnyClass(in);
        }
        else if (isMapped()) {
            this.keyType = readAnyClass(in);
            this.contentType = readAnyClass(in);
        }
        
        // read other values
        in.defaultReadObject();
    }
    

    /**
     * Reads a class using safe encoding to workaround java 1.3 serialization bug.
     */
    private Class readAnyClass(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // read back type class safely 
        if (in.readBoolean()) {
            // it's a type constant
            switch (in.readInt()) {
            
                case BOOLEAN_TYPE: return   Boolean.TYPE;
                case BYTE_TYPE:    return      Byte.TYPE;
                case CHAR_TYPE:    return Character.TYPE;
                case DOUBLE_TYPE:  return    Double.TYPE;
                case FLOAT_TYPE:   return     Float.TYPE;
                case INT_TYPE:     return   Integer.TYPE;
                case LONG_TYPE:    return      Long.TYPE;
                case SHORT_TYPE:   return     Short.TYPE;
                default:
                    // something's gone wrong
                    throw new StreamCorruptedException(
                        "Invalid primitive type. "
                        + "Check version of beanutils used to serialize is compatible.");

            }
              
        } else {
            // it's another class
            return ((Class) in.readObject());
        }
    }
}
