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


package org.apache.commons.beanutils;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.Map;


/**
 * <p>The metadata describing an individual property of a DynaBean.</p>
 *
 * <p>The meta contains an <em>optional</em> content type property ({@link #getContentType})
 * for use by mapped and iterated properties.
 * A mapped or iterated property may choose to indicate the type it expects.
 * The DynaBean implementation may choose to enforce this type on its entries.
 * Alternatively, an implementatin may choose to ignore this property.
 * All keys for maps must be of type String so no meta data is needed for map keys.</p>
 *
 * @version $Id$
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
    public DynaProperty(final String name) {

        this(name, Object.class);

    }


    /**
     * Construct a property of the specified data type.
     *
     * @param name Name of the property being described
     * @param type Java class representing the property data type
     */
    public DynaProperty(final String name, final Class<?> type) {

        super();
        this.name = name;
        this.type = type;
        if (type != null && type.isArray()) {
            this.contentType = type.getComponentType();
        }

    }

    /**
     * Construct an indexed or mapped <code>DynaProperty</code> that supports (pseudo)-introspection
     * of the content type.
     *
     * @param name Name of the property being described
     * @param type Java class representing the property data type
     * @param contentType Class that all indexed or mapped elements are instances of
     */
    public DynaProperty(final String name, final Class<?> type, final Class<?> contentType) {

        super();
        this.name = name;
        this.type = type;
        this.contentType = contentType;

    }

    // ------------------------------------------------------------- Properties

    /** Property name */
    protected String name = null;
    /**
     * Get the name of this property.
     * @return the name of the property
     */
    public String getName() {
        return (this.name);
    }

    /** Property type */
    protected transient Class<?> type = null;
    /**
     * <p>Gets the Java class representing the data type of the underlying property
     * values.</p>
     *
     * <p>There are issues with serializing primitive class types on certain JVM versions
     * (including java 1.3).
     * Therefore, this field <strong>must not be serialized using the standard methods</strong>.</p>
     *
     * <p><strong>Please leave this field as <code>transient</code></strong></p>
     *
     * @return the property type
     */
    public Class<?> getType() {
        return (this.type);
    }


    /** The <em>(optional)</em> type of content elements for indexed <code>DynaProperty</code> */
    protected transient Class<?> contentType;
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
    public Class<?> getContentType() {
        return contentType;
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Does this property represent an indexed value (ie an array or List)?
     *
     * @return <code>true</code> if the property is indexed (i.e. is a List or
     * array), otherwise <code>false</code>
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
     *
     * @return <code>true</code> if the property is a Map
     * otherwise <code>false</code>
     */
    public boolean isMapped() {

        if (type == null) {
            return (false);
        } else {
            return (Map.class.isAssignableFrom(type));
        }

    }

    /**
     * Checks this instance against the specified Object for equality. Overrides the
     * default refererence test for equality provided by {@link java.lang.Object#equals(Object)}
     * @param obj The object to compare to
     * @return <code>true</code> if object is a dyna property with the same name
     * type and content type, otherwise <code>false</code>
     * @since 1.8.0
     */
    @Override
    public boolean equals(final Object obj) {

        boolean result = false;

        result = (obj == this);

        if ((!result) && obj instanceof DynaProperty) {
            final DynaProperty that = (DynaProperty) obj;
            result =
               ((this.name == null) ? (that.name == null) : (this.name.equals(that.name))) &&
               ((this.type == null) ? (that.type == null) : (this.type.equals(that.type))) &&
               ((this.contentType == null) ? (that.contentType == null) : (this.contentType.equals(that.contentType)));
        }

        return result;
    }

    /**
     * @return the hashcode for this dyna property
     * @see java.lang.Object#hashCode
     * @since 1.8.0
     */
    @Override
    public int hashCode() {

       int result = 1;

       result = result * 31 + ((name == null) ? 0 : name.hashCode());
       result = result * 31 + ((type == null) ? 0 : type.hashCode());
       result = result * 31 + ((contentType == null) ? 0 : contentType.hashCode());

       return result;
    }

    /**
     * Return a String representation of this Object.
     * @return a String representation of the dyna property
     */
    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder("DynaProperty[name=");
        sb.append(this.name);
        sb.append(",type=");
        sb.append(this.type);
        if (isMapped() || isIndexed()) {
            sb.append(" <").append(this.contentType).append(">");
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
    private void writeObject(final ObjectOutputStream out) throws IOException {

        writeAnyClass(this.type,out);

        if (isMapped() || isIndexed()) {
            writeAnyClass(this.contentType,out);
        }

        // write out other values
        out.defaultWriteObject();
    }

    /**
     * Write a class using safe encoding to workaround java 1.3 serialization bug.
     */
    private void writeAnyClass(final Class<?> clazz, final ObjectOutputStream out) throws IOException {
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
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {

        this.type = readAnyClass(in);

        if (isMapped() || isIndexed()) {
            this.contentType = readAnyClass(in);
        }

        // read other values
        in.defaultReadObject();
    }


    /**
     * Reads a class using safe encoding to workaround java 1.3 serialization bug.
     */
    private Class<?> readAnyClass(final ObjectInputStream in) throws IOException, ClassNotFoundException {
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
            return ((Class<?>) in.readObject());
        }
    }
}