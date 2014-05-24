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

/**
 * <p>The <em>Bean Introspection Utilities</em> component of the Apache Commons
 * subproject offers low-level utility classes that assist in getting and setting
 * property values on Java classes that follow the naming design patterns outlined
 * in the JavaBeans Specification, as well as mechanisms for dynamically defining
 * and accessing bean properties.</p>
 *
 * <h1>Table of Contents</h1>
 *
 * <ul>
 * <li>1. <a href="#overview">Overview</a>
 *     <ul>
 *     <li>1.1 <a href="#overview.background">Background</a></li>
 *     <li>1.2 <a href="#overview.dependencies">External Dependencies</a></li>
 *     </ul>
 * </li>
 * <li>2. <a href="#standard">Standard JavaBeans</a>
 *     <ul>
 *     <li>2.1 <a href="#standard.background">Background</a></li>
 *     <li>2.2 <a href="#standard.basic">Basic Property Access</a></li>
 *     <li>2.3 <a href="#standard.nested">Nested Property Access</a></li>
 *     <li>2.4 <a href="#standard.customize">Customizing Introspection</a></li>
 *     <li>2.5 <a href="#standard.suppress">Suppressing Properties</a></li>
 *     </ul>
 * </li>
 * <li>3. <a href="#dynamic">Dynamic Beans (DynaBeans)</a>
 *     <ul>
 *     <li>3.1 <a href="#dynamic.background">Background</a></li>
 *     <li>3.2 <a href="#dynamic.basic">BasicDynaBean and BasicDynaClass</a></li>
 *     <li>3.3 <a href="#dynamic.resultSet">ResultSetDynaClass (Wraps ResultSet in DynaBeans)</a></li>
 *     <li>3.4 <a href="#dynamic.rowSet">RowSetDynaClass (Disconnected ResultSet as DynaBeans)</a></li>
 *     <li>3.5 <a href="#dynamic.wrap">WrapDynaBean and WrapDynaClass</a></li>
 *     <li>3.6 <a href="#dynamic.lazy"><i>Lazy</i> DynaBeans</a></li>
 *     </ul>
 * </li>
 * <li>4. <a href="#conversion">Data Type Conversions</a>
 *     <ul>
 *     <li>4.1 <a href="#conversion.background">Background</a></li>
 *     <li>4.2 <a href="#conversion.beanutils">BeanUtils and ConvertUtils
 *         Conversions</a></li>
 *     <li>4.3 <a href="#conversion.defining">Defining Your Own Converters</a></li>
 *     <li>4.4 <a href="#conversion.i18n">Locale Aware Conversions</a></li>
 *     </ul>
 * </li>
 * <li>5. <a href="#instances">Utility Objects And Static Utility Classes</a></li>
 * <li>6. <a href="#collections">Collections</a>
 *     <ul>
 *     <li>6.1 <a href="#bean-comparator">Comparing Beans</a></li>
 *     <li>6.2 <a href="#bean-property-closure">Operating On Collections Of Beans</a></li>
 *     <li>6.3 <a href="#bean-property-predicate">Querying Or Filtering Collections Of Beans</a></li>
 *     <li>6.4 <a href="#bean-property-transformer">Transforming Collections Of Beans</a></li>
 *     </ul></li>
 * <li>7. <a href="#FAQ">Frequently Asked Questions</a>
 *     <ul>
 *     <li><a href="#FAQ.property">Why Can't BeanUtils Find My Method?</a></li>
 *     <li><a href="#FAQ.bc.order">How Do I Set The BeanComparator Order To Be Ascending/Descending?</a></li>
 *     </ul></li>
 * </ul>
 *
 * <a name="overview"></a>
 * <h1>1. Overview</h1>
 *
 * <a name="overview.background"></a>
 * <h2>1.1 Background</h2>
 *
 * <p>The <em>JavaBeans</em> name comes from a
 * <a href="http://java.sun.com/products/javabeans/">Java API</a>
 * for a component architecture for the Java language.  Writing Java classes that
 * conform to the JavaBeans design patterns makes it easier for Java developers
 * to understand the functionality provided by your class, as well as allowing
 * JavaBeans-aware tools to use Java's <em>introspection</em> capabilities to
 * learn about the properties and operations provided by your class, and present
 * them in a visually appealing manner in development tools.</p>
 *
 * <p>The <a href="http://java.sun.com/products/javabeans/docs/spec.html">JavaBeans
 * Specification</a> describes the complete set of characteristics that makes
 * an arbitrary Java class a JavaBean or not -- and you should consider reading
 * this document to be an important part of developing your Java programming
 * skills.  However, the required characteristics of JavaBeans that are
 * important for most development scenarios are listed here:</p>
 * <ul>
 * <li>The class must be <strong>public</strong>, and provide a
 *     <strong>public</strong> constructor that accepts no arguments.  This allows
 *     tools and applications to dynamically create new instances of your bean,
 *     without necessarily knowing what Java class name will be used ahead of
 *     time, like this:
 * <pre>
 *         String className = ...;
 *         Class beanClass = Class.forName(className);
 *         Object beanInstance = beanClass.newInstance();
 * </pre></li>
 * <li>As a necessary consequence of having a no-arguments constructor,
 *     configuration of your bean's behavior must be accomplished separately
 *     from its instantiation.  This is typically done by defining a set of
 *     <em>properties</em> of your bean, which can be used to modify its behavior
 *     or the data that the bean represents.  The normal convention for
 *     property names is that they start with a lower case letter, and be
 *     comprised only of characters that are legal in a Java identifier.</li>
 * <li>Typically, each bean property will have a public <em>getter</em> and
 *     <em>setter</em> method that are used to retrieve or define the property's
 *     value, respectively.  The JavaBeans Specification defines a design
 *     pattern for these names, using <code>get</code> or <code>set</code> as the
 *     prefix for the property name with it's first character capitalized.  Thus,
 *     you a JavaBean representing an employee might have
 *     (among others) properties named <code>firstName</code>,
 *     <code>lastName</code>, and <code>hireDate</code>, with method signatures
 *     like this:
 * <pre>
 *         public class Employee {
 *             public Employee();   // Zero-arguments constructor
 *             public String getFirstName();
 *             public void setFirstName(String firstName);
 *             public String getLastName();
 *             public void setLastName(String lastName);
 *             public Date getHireDate();
 *             public void setHireDate(Date hireDate);
 *             public boolean isManager();
 *             public void setManager(boolean manager);
 *             public String getFullName();
 *         }
 * </pre></li>
 * <li>As you can see from the above example, there is a special variant allowed
 *     for boolean properties -- you can name the <em>getter</em> method with a
 *     <code>is</code> prefix instead of a <code>get</code> prefix if that makes
 *     for a more understandable method name.</li>
 * <li>If you have both a <em>getter</em> and a <em>setter</em> method for a
 *     property, the data type returned by the <em>getter</em> must match the
 *     data type accepted by the <em>setter</em>.  In addition, it is contrary
 *     to the JavaBeans specification to have more than one <em>setter</em>
 *     with the same name, but different property types.</li>
 * <li>It is not required that you provide a <em>getter</em> and a
 *     <em>setter</em> for every property.  In the example above, the
 *     <code>fullName</code> property is read-only, because there is no
 *     <em>setter</em> method.  It is also possible, but less common, to provide
 *     write-only properties.</li>
 * <li>It is also possible to create a JavaBean where the <em>getter</em> and
 *     <em>setter</em> methods do not match the naming pattern described above.
 *     The standard JavaBeans support classes in the Java language, as well as
 *     all classes in the BeanUtils package, allow you to describe the actual
 *     property method names in a <code>BeanInfo</code> class associated with
 *     your bean class.  See the JavaBeans Specification for full details.</li>
 * <li>The JavaBeans Specification also describes many additional design patterns
 *     for event listeners, wiring JavaBeans together into component hierarchies,
 *     and other useful features that are beyond the scope of the BeanUtils
 *     package.</li>
 * </ul>
 *
 * <p>Using standard Java coding techniques, it is very easy to deal with
 * JavaBeans if you know ahead of time which bean classes you will be using, and
 * which properties you are interested in:</p>
 * <pre>
 *         Employee employee = ...;
 *         System.out.println("Hello " + employee.getFirstName() + "!");
 * </pre>
 *
 * <a name="overview.dependencies"></a>
 * <h2>1.2 External Dependencies</h2>
 *
 * <p>The <em>commons-beanutils</em> package requires that the following
 * additional packages be available in the application's class path at runtime:
 * </p>
 * <ul>
 * <li><a href="http://commons.apache.org/downloads/download_logging.cgi">
 * Logging Package (Apache Commons)</a>, version 1.0 or later</li>
 * <li><a href="http://commons.apache.org/downloads/download_collections.cgi">
 * Collections Package (Apache Commons)</a>, version 1.0 or later</li>
 * </ul>
 *
 * <a name="standard"></a>
 * <h1>2. Standard JavaBeans</h1>
 *
 * <a name="standard.background"></a>
 * <h2>2.1 Background</h2>
 *
 * <p>As described above, the standard facilities of the Java programming language
 * make it easy and natural to access the property values of your beans using
 * calls to the appropriate getter methods.
 * But what happens in more sophisticated environments where you do not
 * necessarily know ahead of time which bean class you are going to be using,
 * or which property you want to retrieve or modify?  The Java language provides
 * classes like <code>java.beans.Introspector</code>, which can examine a Java
 * class at runtime and identify for you the names of the property getter and
 * setter methods, plus the <em>Reflection</em> capabilities to dynamically call
 * such a method.  However, these APIs can be difficult to use, and expose the
 * application developer to many unnecessary details of the underlying structure
 * of Java classes.  The APIs in the BeanUtils package are intended to simplify
 * getting and setting bean properties dynamically, where the objects you are
 * accessing -- and the names of the properties you care about -- are determined
 * at runtime in your application, rather than as you are writing and compiling
 * your application's classes.</p>
 *
 * <p>This is the set of needs that are satisfied by the static methods of the
 * {@link org.apache.commons.beanutils.PropertyUtils}
 * class, which are described further in this section.  First, however, some
 * further definitions will prove to be useful:</p>
 *
 * <p>The general set of possible property types supported by a JavaBean can be
 * broken into three categories -- some of which are supported by the standard
 * JavaBeans specification, and some of which are uniquely supported by the
 * <em>BeanUtils</em> package:</p>
 * <ul>
 * <li><strong>Simple</strong> - Simple, or scalar, properties have a single
 *     value that may be retrieved or modified.  The underlying property type
 *     might be a Java language primitive (such as <code>int</code>, a simple
 *     object (such as a <code>java.lang.String</code>), or a more complex
 *     object whose class is defined either by the Java language, by the
 *     application, or by a class library included with the application.</li>
 * <li><strong>Indexed</strong> - An indexed property stores an ordered collection
 *     of objects (all of the same type) that can be individually accessed by an
 *     integer-valued, non-negative index (or subscript).  Alternatively, the
 *     entire set of values may be set or retrieved using an array.
 *     As an extension to the JavaBeans specification, the
 *     <em>BeanUtils</em> package considers any property whose underlying data
 *     type is <code>java.util.List</code> (or an implementation of List) to be
 *     indexed as well.</li>
 * <li><strong>Mapped</strong> - As an extension to standard JavaBeans APIs,
 *     the <em>BeanUtils</em> package considers any property whose underlying
 *     value is a <code>java.util.Map</code> to be "mapped".  You can set and
 *     retrieve individual values via a String-valued key.</li>
 * </ul>
 *
 * <p>A variety of API methods are provided in the
 * {@link org.apache.commons.beanutils.PropertyUtils} class to get and set
 * property values of all of these types.
 * In the code fragments below, assume that there are two bean classes defined
 * with the following method signatures:</p>
 * <pre>
 *     public class Employee {
 *         public Address getAddress(String type);
 *         public void setAddress(String type, Address address);
 *         public Employee getSubordinate(int index);
 *         public void setSubordinate(int index, Employee subordinate);
 *         public String getFirstName();
 *         public void setFirstName(String firstName);
 *         public String getLastName();
 *         public void setLastName(String lastName);
 *     }
 * </pre>
 *
 * <a name="standard.basic"></a>
 * <h2>2.2 Basic Property Access</h2>
 *
 * <p>Getting and setting <strong>simple</strong> property values is, well,
 * simple :-).  Check out the following API signatures in the Javadocs:</p>
 *
 * <ul>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#getSimpleProperty(Object, String)}</li>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#setSimpleProperty(Object, String, Object)}</li>
 * </ul>
 *
 * <p>Using these methods, you might dynamically manipulate the employee's name
 * in an application:</p>
 * <pre>
 *     Employee employee = ...;
 *     String firstName = (String)
 *       PropertyUtils.getSimpleProperty(employee, "firstName");
 *     String lastName = (String)
 *       PropertyUtils.getSimpleProperty(employee, "lastName");
 *     ... manipulate the values ...
 *     PropertyUtils.setSimpleProperty(employee, "firstName", firstName);
 *     PropertyUtils.setSimpleProperty(employee, "lastName", lastName);
 * </pre>
 *
 * <p>For <strong>indexed</strong> properties, you have two choices - you can
 * either build a subscript into the "property name" string, using square
 * brackets, or you can specify the subscript in a separate argument to the
 * method call:</p>
 *
 * <ul>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#getIndexedProperty(Object, String)}</li>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#getIndexedProperty(Object, String, int)}</li>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#setIndexedProperty(Object, String, Object)}</li>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#setIndexedProperty(Object, String, int, Object)}</li>
 * </ul>
 *
 * <p>Only integer constants are allowed when you add a subscript to the property
 * name.  If you need to calculate the index of the entry you wish to retrieve,
 * you can use String concatenation to assemble the property name expression.
 * For example, you might do either of the following:</p>
 * <pre>
 *     Employee employee = ...;
 *     int index = ...;
 *     String name = "subordinate[" + index + "]";
 *     Employee subordinate = (Employee)
 *       PropertyUtils.getIndexedProperty(employee, name);
 *
 *     Employee employee = ...;
 *     int index = ...;
 *     Employee subordinate = (Employee)
 *       PropertyUtils.getIndexedProperty(employee, "subordinate", index);
 * </pre>
 *
 * <p>In a similar manner, there are two possible method signatures for getting
 * and setting <strong>mapped</strong> properties.  The difference is that the
 * extra argument is surrounded by parentheses ("(" and ")") instead of square
 * brackets, and it is considered to be a String-value key used to get or set
 * the appropriate value from an underlying map.</p>
 *
 * <ul>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#getMappedProperty(Object, String)}</li>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#getMappedProperty(Object, String, String)}</li>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#setMappedProperty(Object, String, Object)}</li>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#setMappedProperty(Object, String, String, Object)}</li>
 * </ul>
 *
 * <p>You can, for example, set the employee's home address in either of these
 * two manners:</p>
 *
 * <pre>
 *     Employee employee = ...;
 *     Address address = ...;
 *     PropertyUtils.setMappedProperty(employee, "address(home)", address);
 *
 *     Employee employee = ...;
 *     Address address = ...;
 *     PropertyUtils.setMappedProperty(employee, "address", "home", address);
 * </pre>
 *
 * <a name="standard.nested"></a>
 * <h2>2.3 Nested Property Access</h2>
 *
 * <p>In all of the examples above, we have assumed that you wished to retrieve
 * the value of a property of the bean being passed as the first argument to a
 * PropertyUtils method.  However, what if the property value you retrieve is
 * really a Java object, and you wish to retrieve a property of <em>that</em>
 * object instead?</p>
 *
 * <p>For example, assume we really wanted the <code>city</code> property of the
 * employee's home address.  Using standard Java programming techniques for direct
 * access to the bean properties, we might write:</p>
 *
 * <pre>
 *     String city = employee.getAddress("home").getCity();
 * </pre>
 *
 * <p>The equivalent mechanism using the PropertyUtils class is called
 * <strong>nested</strong> property access.  To use this approach, you concatenate
 * together the property names of the access path, using "." separators -- very
 * similar to the way you can perform nested property access in JavaScript.</p>
 *
 * <ul>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#getNestedProperty(Object, String)}</li>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#setNestedProperty(Object, String, Object)}</li>
 * </ul>
 *
 * <p>The PropertyUtils equivalent to the above Java expression would be:</p>
 *
 * <pre>
 *     String city = (String)
 *       PropertyUtils.getNestedProperty(employee, "address(home).city");
 * </pre>
 *
 * <p>Finally, for convenience, PropertyUtils provides method signatures that
 * accept any arbitrary combination of simple, indexed, and mapped property
 * access, using any arbitrary level of nesting:</p>
 *
 * <ul>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#getProperty(Object, String)}</li>
 * <li> {@link org.apache.commons.beanutils.PropertyUtils#setProperty(Object, String, Object)}</li>
 * </ul>
 *
 * <p>which you might use like this:</p>
 *
 * <pre>
 *     Employee employee = ...;
 *     String city = (String) PropertyUtils.getProperty(employee,
 *       "subordinate[3].address(home).city");
 * </pre>
 *
 * <a name="standard.customize"></a>
 * <h2>2.4 Customizing Introspection</h2>
 *
 * <p>As was pointed out, BeanUtils relies on conventions defined by the
 * <em>JavaBeans</em> specification to determine the properties available for
 * a specific bean class. Thus all classes conforming to these conventions can
 * be used out of the box.</p>
 *
 * <p>Sometimes an application has to deal with classes using different
 * conventions. For instance, fluent APIs allowing method chaining are not
 * compliant to the <em>JavaBeans</em> specification because here set methods
 * have non-void return values. From version 1.9.0 onwards, BeanUtils supports
 * customization of its introspection mechanism. This allows an application
 * to extend or modify the default discovery of bean properties.</p>
 *
 * <p>The key to this extension mechanism is the {@link org.apache.commons.beanutils.BeanIntrospector}
 * interface. The purpose of an object implementing this interface is to
 * process a specific target class and create corresponding
 * <code>PropertyDescriptor</code> objects for the properties it detects.
 * Per default, BeanUtils uses a {@link org.apache.commons.beanutils.DefaultBeanIntrospector}
 * object which detects properties compatible with the <em>JavaBeans</em>
 * specification.</p>
 *
 * <p>In order to extend the property discovery mechanism, <code>PropertyUtils</code>
 * offers the {@link org.apache.commons.beanutils.PropertyUtils#addBeanIntrospector(BeanIntrospector)}
 * method. Here a custom <code>BeanIntrospector</code> implementation can be
 * passed in. During introspection of a class, this custom introspector is
 * then called, and it can add the properties it has detected to the total
 * result. As an example of such a custom <code>BeanIntrospector</code>
 * implementation, BeanUtils ships with the {@link org.apache.commons.beanutils.FluentPropertyBeanIntrospector}
 * class. This implementation can detect properties whose set methods have a
 * non-void return type - thus enabling support for typical properties in a
 * fluent API.</p>
 *
 * <a name="standard.suppress"></a>
 * <h2>2.5 Suppressing Properties</h2>
 * <p>The mechanism of customizing bean introspection described in the previous
 * section can also be used to suppress specific properties. There is a
 * specialized <code>BeanIntrospector</code> implementation that does exactly
 * this: {@link org.apache.commons.beanutils.SuppressPropertiesBeanIntrospector}.
 * When creating an instance, a collection with the names of properties that
 * should not be accessible on beans has to be provided. These properties will
 * then be removed if they have been detected by other <code>BeanIntrospector</code>
 * instances during processing of a bean class.</p>
 *
 * <p>A good use case for suppressing properties is the special <code>class</code>
 * property which is per default available for all beans; it is generated from the
 * <code>getClass()</code> method inherited from <code>Object</code> which follows the
 * naming conventions for property get methods. Exposing this property in an
 * uncontrolled way can lead to a security vulnerability as it allows access to
 * the class loader. More information can be found at
 * <a href="https://issues.apache.org/jira/browse/BEANUTILS-463">
 * https://issues.apache.org/jira/browse/BEANUTILS-463</a>.</p>
 *
 * <p>Because the <code>class</code> property is undesired in many use cases
 * there is already an instance of <code>SuppressPropertiesBeanIntrospector</code>
 * which is configured to suppress this property. It can be obtained via the
 * <code>SUPPRESS_CLASS</code> constant of
 * <code>SuppressPropertiesBeanIntrospector</code>.</p>
 *
 * <a name="dynamic"></a>
 * <h1>3. Dynamic Beans (DynaBeans)</h1>
 *
 * <a name="dynamic.background"></a>
 * <h2>3.1 Background</h2>
 *
 * <p>The {@link org.apache.commons.beanutils.PropertyUtils} class described in the
 * preceding section is designed to provide dynamic property access on existing
 * JavaBean classes, without modifying them in any way.  A different use case for
 * dynamic property access is when you wish to represent a dynamically calculated
 * set of property values as a JavaBean, but <em>without</em> having to actually
 * write a Java class to represent these properties.  Besides the effort savings
 * in not having to create and maintain a separate Java class, this ability also
 * means you can deal with situations where the set of properties you care about
 * is determined dynamically (think of representing the result set of an SQL
 * select as a set of JavaBeans ...).</p>
 *
 * <p>To support this use case, the <em>BeanUtils</em> package provides the
 * {@link org.apache.commons.beanutils.DynaBean} interface, which must be implemented by a
 * bean class actually implementing the interface's methods, and the associated
 * {@link org.apache.commons.beanutils.DynaClass} interface that defines the set of
 * properties supported by a particular group of DynaBeans, in much the same way
 * that <code>java.lang.Class</code> defines the set of properties supported by
 * all instances of a particular JavaBean class.</p>
 *
 * <p>For example, the <code>Employee</code> class used in the examples above
 * might be implemented as a DynaBean, rather than as a standard JavaBean.  You
 * can access its properties like this:</p>
 *
 * <pre>
 *     DynaBean employee = ...; // Details depend on which
 *                              // DynaBean implementation you use
 *     String firstName = (String) employee.get("firstName");
 *     Address homeAddress = (Address) employee.get("address", "home");
 *     Object subordinate = employee.get("subordinate", 2);
 * </pre>
 *
 * <p>One very important convenience feature should be noted:  <em>the
 * PropertyUtils property getter and setter methods understand how to access
 * properties in DynaBeans</em>.  Therefore, if the bean you pass as the first
 * argument to, say, <code>PropertyUtils.getSimpleProperty()</code> is really a
 * DynaBean implementation, the call will get converted to the appropriate
 * DynaBean getter method transparently.  Thus, you can base your application's
 * dynamic property access totally on the PropertyUtils APIs, if you wish, and
 * use them to access either standard JavaBeans or DynaBeans without having to
 * care ahead of time how a particular bean is implemented.</p>
 *
 * <p>Because DynaBean and DynaClass are interfaces, they may be implemented
 * multiple times, in different ways, to address different usage scenarios.  The
 * following subsections describe the implementations that are provided as a part
 * of the standard <em>BeanUtils</em> package, although you are encouraged to
 * provide your own custom implementations for cases where the standard
 * implementations are not sufficient.</p>
 *
 * <a name="dynamic.basic"></a>
 * <h2>3.2 <code>BasicDynaBean</code> and <code>BasicDynaClass</code></h2>
 *
 * <p>The {@link org.apache.commons.beanutils.BasicDynaBean} and
 * {@link org.apache.commons.beanutils.BasicDynaClass} implementation provides a
 * basic set of
 * dynamic property capabilities where you want to dynamically define the
 * set of properties (described by instances of {@link org.apache.commons.beanutils.DynaProperty}).
 * You start by defining the DynaClass that establishes
 * the set of properties you care about:</p>
 *
 * <pre>
 *     DynaProperty[] props = new DynaProperty[]{
 *         new DynaProperty("address", java.util.Map.class),
 *         new DynaProperty("subordinate", mypackage.Employee[].class),
 *         new DynaProperty("firstName", String.class),
 *         new DynaProperty("lastName",  String.class)
 *       };
 *     BasicDynaClass dynaClass = new BasicDynaClass("employee", null, props);
 * </pre>
 *
 * <p>Note that the 'dynaBeanClass' argument (in the constructor of
 * <code>BasicDynaClass</code>) can have the value of <code>null</code>.  In this
 * case, the value of <code>dynaClass.getDynaBeanClass</code> will just be the
 * <code>Class</code> for BasicDynaBean.</p>
 *
 * <p>Next, you use the <code>newInstance()</code> method of this DynaClass to
 * create new DynaBean instances that conform to this DynaClass, and populate
 * its initial property values (much as you would instantiate a new standard
 * JavaBean and then call its property setters):</p>
 *
 * <pre>
 *     DynaBean employee = dynaClass.newInstance();
 *     employee.set("address", new HashMap());
 *     employee.set("subordinate", new mypackage.Employee[0]);
 *     employee.set("firstName", "Fred");
 *     employee.set("lastName", "Flintstone");
 * </pre>
 *
 * <p>Note that the DynaBean class was declared to be
 * <code>DynaBean</code> instead of <code>BasicDynaBean</code>.  In
 * general, if you are using DynaBeans, you will not want to care about the
 * actual implementation class that is being used -- you only care about
 * declaring that it is a <code>DynaBean</code> so that you can use the
 * DynaBean APIs.</p>
 *
 * <p>As stated above, you can pass a DynaBean instance as the first argument
 * to a <code>PropertyUtils</code> method that gets and sets properties, and it
 * will be interpreted as you expect -- the dynamic properties of the DynaBean
 * will be retrieved or modified, instead of underlying properties on the
 * actual BasicDynaBean implementation class.</p>
 *
 * <a name="dynamic.resultSet"></a>
 * <h2>3.3 <code>ResultSetDynaClass</code> (Wraps ResultSet in DynaBeans)</h2>
 *
 * <p>A very common use case for DynaBean APIs is to wrap other collections of
 * "stuff" that do not normally present themselves as JavaBeans.  One of the most
 * common collections that would be nice to wrap is the
 * <code>java.sql.ResultSet</code> that is returned when you ask a JDBC driver
 * to perform a SQL SELECT statement.  Commons BeanUtils offers a standard
 * mechanism for making each row of the result set visible as a DynaBean,
 * which you can utilize as shown in this example:</p>
 * <pre>
 *   Connection conn = ...;
 *   Statement stmt = conn.createStatement();
 *   ResultSet rs = stmt.executeQuery
 *     ("select account_id, name from customers");
 *   Iterator rows = (new ResultSetDynaClass(rs)).iterator();
 *   while (rows.hasNext()) {
 *     DynaBean row = (DynaBean) rows.next();
 *     System.out.println("Account number is " +
 *                        row.get("account_id") +
 *                        " and name is " + row.get("name"));
 *   }
 *   rs.close();
 *   stmt.close();
 * </pre>
 *
 *
 * <a name="dynamic.rowSet"></a>
 * <h2>3.4 <code>RowSetDynaClass</code> (Disconnected ResultSet as DynaBeans)</h2>
 * <p>Although <a href="#dynamic.resultSet"><code>ResultSetDynaClass</code></a> is
 * a very useful technique for representing the results of an SQL query as a
 * series of DynaBeans, an important problem is that the underlying
 * <code>ResultSet</code> must remain open throughout the period of time that the
 * rows are being processed by your application.  This hinders the ability to use
 * <code>ResultSetDynaClass</code> as a means of communicating information from
 * the model layer to the view layer in a model-view-controller architecture
 * such as that provided by the <a href="http://struts.apache.org/">Struts
 * Framework</a>, because there is no easy mechanism to assure that the result set
 * is finally closed (and the underlying <code>Connection</code> returned to its
 * connection pool, if you are using one).</p>
 *
 * <p>The <code>RowSetDynaClass</code> class represents a different approach to
 * this problem.  When you construct such an instance, the underlying data is
 * <em>copied</em> into a set of in-memory DynaBeans that represent the result.
 * The advantage of this technique, of course, is that you can immediately close
 * the ResultSet (and the corresponding Statement), normally before you even
 * process the actual data that was returned.  The disadvantage, of course, is
 * that you must pay the performance and memory costs of copying the result data,
 * and the result data must fit entirely into available heap memory.  For many
 * environments (particularly in web applications), this tradeoff is usually
 * quite beneficial.</p>
 *
 * <p>As an additional benefit, the <code>RowSetDynaClass</code> class is defined
 * to implement <code>java.io.Serializable</code>, so that it (and the
 * DynaBeans that correspond to each row of the result) can be conveniently
 * serialized and deserialized (as long as the underlying column values are
 * also Serializable).  Thus, <code>RowSetDynaClass</code> represents a very
 * convenient way to transmit the results of an SQL query to a remote Java-based
 * client application (such as an applet).</p>
 *
 * <p>The normal usage pattern for a <code>RowSetDynaClass</code> will look
 * something like this:</p>
 * <pre>
 *     Connection conn = ...;  // Acquire connection from pool
 *     Statement stmt = conn.createStatement();
 *     ResultSet rs = stmt.executeQuery("SELECT ...");
 *     RowSetDynaClass rsdc = new RowSetDynaClass(rs);
 *     rs.close();
 *     stmt.close();
 *     ...;                    // Return connection to pool
 *     List rows = rsdc.getRows();
 *     ...;                   // Process the rows as desired
 * </pre>
 *
 *
 * <a name="dynamic.wrap"></a>
 * <h2>3.5 <code>WrapDynaBean</code> and <code>WrapDynaClass</code></h2>
 *
 * <p>OK, you've tried the DynaBeans APIs and they are cool -- very simple
 * <code>get()</code> and <code>set()</code> methods provide easy access to all
 * of the dynamically defined simple, indexed, and mapped properties of your
 * DynaBeans.  You'd like to use the DynaBean APIs to access <strong>all</strong>
 * of your beans, but you've got a bunch of existing standard JavaBeans classes
 * to deal with as well.  This is where the
 * {@link org.apache.commons.beanutils.WrapDynaBean} (and its associated
 * {@link org.apache.commons.beanutils.WrapDynaClass}) come into play.  As the name
 * implies, a WrapDynaBean is used to "wrap" the DynaBean APIs around an
 * existing standard JavaBean class.  To use it, simply create the wrapper
 * like this:</p>
 *
 * <pre>
 *     MyBean bean = ...;
 *     DynaBean wrapper = new WrapDynaBean(bean);
 *     String firstName = wrapper.get("firstName");
 * </pre>
 *
 * <p>Note that, although appropriate <code>WrapDynaClass</code> instances are
 * created internally, you never need to deal with them.</p>
 *
 * <a name="dynamic.lazy"></a>
 * <h2>3.6 <i>Lazy</i> DynaBeans</h2>
 *
 * <ul>
 *     <li>1. <a href="#LazyDynaBean">LazyDynaBean</a> - A <i>Lazy</i>
 *          {@link org.apache.commons.beanutils.DynaBean}</li>
 *     <li>2. <a href="#LazyDynaMap">LazyDynaMap</a> - A <i>light weight</i>
 *          {@link org.apache.commons.beanutils.DynaBean} facade to a Map
 *          with <i>lazy</i> map/list processing</li>
 *     <li>3. <a href="#LazyDynaList">LazyDynaList</a> - A <i>lazy list</i>
 *          for {@link org.apache.commons.beanutils.DynaBean DynaBean's},
 *          <code>java.util.Map</code>'s or POJO beans.</li>
 *     <li>4. <a href="#LazyDynaClass">LazyDynaClass</a> - A
 *          {@link org.apache.commons.beanutils.MutableDynaClass} implementation.</li>
 * </ul>
 *
 * <p>You bought into the DynaBeans because it saves coding all those POJO JavaBeans but
 *    you're here because <i>lazy</i> caught your eye and wondered whats that about?
 *    What makes these flavors of DynaBean <i>lazy</i> are the following features:</p>
 *     <ul>
 *         <li><strong><i>Lazy</i> property addition</strong> - lazy beans use a
 *              {@link org.apache.commons.beanutils.DynaClass} which implements
 *              the {@link org.apache.commons.beanutils.MutableDynaClass}
 *              interface. This provides the ability to add and remove a DynaClass's
 *              properties. <i>Lazy</i> beans use this feature to automatically add
 *              a property which doesn't exist to the DynaClass when
 *              the <code>set(name, value)</code> method is called.</li>
 *          <li><strong><i>Lazy</i> List/Array growth</strong> - If an <i>indexed</i> property is not large
 *              enough to accomodate the <code>index</code> being set then the <code>List</code> or
 *              <code>Array</code> is automatically <i>grown</i> so that it is.</li>
 *          <li><strong><i>Lazy</i> List/Array instantiation</strong> - if an <i>indexed</i>
 *              property doesn't exist then calling the {@link org.apache.commons.beanutils.DynaBean DynaBean's}
 *              <i>indexed</i> property getter/setter methods (i.e. <code>get(name, index)</code> or
 *              <code>set(name, index, value)</code>) results in either a new <code>List</code>
 *              or <code>Array</code> being instantiated. If the indexed property has not been
 *              defined in the DynaClass then it is automatically added and a default <code>List</code>
 *              implementation instantiated.</li>
 *         <li><strong><i>Lazy</i> Map instantiation</strong> - if a <i>mapped</i>
 *              property doesn't exist then calling the {@link org.apache.commons.beanutils.DynaBean DynaBean's}
 *              <i>mapped</i> property getter/setter methods (i.e. <code>get(name, key)</code> or
 *              <code>set(name, key, value)</code>) results in a new <code>Map</code>
 *              being instantiated. If the mapped property has not been defined in the DynaClass
 *              then it is automatically added and a default <code>Map</code> implementation
 *              instantiated.</li>
 *         <li><strong><i>Lazy</i> Bean instantiation</strong> - if a property is defined in
 *              the <code>DynaClass</code> as a <code>DynaBean</code> or regular bean and
 *              doesn't exist in the <code>DynaBean</code> then <code>LazyDynaBean</code> wiill
 *              try to instantiate the bean using a default empty constructor.</li>
 *     </ul>
 *
 * <p><strong>1. {@link org.apache.commons.beanutils.LazyDynaBean}</strong> is the standard <i>lazy</i> bean
 *    implementation. By default it is associated with a {@link org.apache.commons.beanutils.LazyDynaClass}
 *    which implements the {@link org.apache.commons.beanutils.MutableDynaClass} interface - however
 *    it can be used with any <code>MutableDynaClass</code> implementation. The question is <i>how do
 *    I use it?</i> - well it can be as simple as creating a new bean and then calling the getters/setters...</p>
 *
 * <pre>
 *     DynaBean dynaBean = new LazyDynaBean();
 *
 *     dynaBean.set("foo", "bar");                   // simple
 *
 *     dynaBean.set("customer", "title", "Mr");      // mapped
 *     dynaBean.set("customer", "surname", "Smith"); // mapped
 *
 *     dynaBean.set("address", 0, addressLine1);     // indexed
 *     dynaBean.set("address", 1, addressLine2);     // indexed
 *     dynaBean.set("address", 2, addressLine3);     // indexed
 * </pre>
 *
 * <p><strong>2. {@link org.apache.commons.beanutils.LazyDynaMap}</strong> is a <i>light weight</i>
 *    <code>DynaBean</code> facade to a <code>Map</code> with all the usual <i>lazy</i> features. Its
 *    <i>light weight</i> because it doesn't have an associated <code>DynaClass</code> containing all the properties.
 *    In fact it actually implements the <code>DynaClass</code> interface itself (and <code>MutableDynaClass</code>)
 *    and derives all the <i>DynaClass</i> information from the actual contents of the <code>Map</code>. A
 *    <code>LazyDynaMap</code> can be created around an existing <code>Map</code> or can instantiate its own
 *    <code>Map</code>. After any <code>DynaBean</code> processing has finished the <code>Map</code> can be retrieved
 *    and the DynaBean <i>facade</i> discarded.</p>
 *
 * <p>If you need a new <code>Map</code> then to use....</p>
 *
 * <pre>
 *     DynaBean dynaBean = new LazyDynaMap();        // create DynaBean
 *
 *     dynaBean.set("foo", "bar");                   // simple
 *     dynaBean.set("customer", "title", "Mr");      // mapped
 *     dynaBean.set("address", 0, addressLine1);     // indexed
 *
 *     Map myMap = dynaBean.getMap()                 // retrieve the Map
 * </pre>
 * <p><i>or</i> to use with an existing <code>Map</code> ....</p>
 *
 * <pre>
 *     Map myMap = ....                             // exisitng Map
 *     DynaBean dynaBean = new LazyDynaMap(myMap);  // wrap Map in DynaBean
 *     dynaBean.set("foo", "bar");                  // set properties
 * </pre>
 *
 * <p><strong>3. {@link org.apache.commons.beanutils.LazyDynaList}</strong>
 *      is  <i>lazy list</i> for {@link org.apache.commons.beanutils.DynaBean DynaBeans}
 *      <code>java.util.Map</code>'s or POJO beans. See the <a href="LazyDynaList.html">Javadoc</a>
 *      for more details and example usage.</p>
 *
 * <p><strong>4. {@link org.apache.commons.beanutils.LazyDynaClass}</strong>
 *      extends {@link org.apache.commons.beanutils.BasicDynaClass} and implements
 *      the <a href="MutableDynaClass.html">MutableDynaClass</a> interface.
 *      It can be used with other <code>DynaBean</code> implementations, but it
 *      is the default <code>DynaClass</code> used by <code>LazyDynaBean</code>.
 *      When using the <code>LazyDynaBean</code> there may be no need to have
 *      anything to do with the <code>DynaClass</code>. However sometimes there
 *      is a requirement to set up the <code>DynaClass</code> first - perhaps to
 *      define the type of array for an indexed property, or if using the DynaBean
 *      in <i>restricted</i> mode (see note below) is required. Doing so is
 *      straight forward...</p>
 *
 * <p><i>Either</i> create a <code>LazyDynaClass</code> first...
 *
 * <pre>
 *     MutableDynaClass dynaClass = new LazyDynaClass();    // create DynaClass
 *
 *     dynaClass.add("amount", java.lang.Integer.class);    // add property
 *     dynaClass.add("orders", OrderBean[].class);          // add indexed property
 *     dynaClass.add("orders", java.util.TreeMapp.class);   // add mapped property
 *
 *     DynaBean dynaBean = new LazyDynaBean(dynaClass);     // Create DynaBean with associated DynaClass
 * </pre>
 *
 * <p><i>or</i> create a <code>LazyDynaBean</code> and get the <code>DynaClass</code>...
 *
 * <pre>
 *     DynaBean dynaBean = new LazyDynaBean();              // Create LazyDynaBean
 *     MutableDynaClass dynaClass =
 *              (MutableDynaClass)dynaBean.getDynaClass();  // get DynaClass
 *
 *     dynaClass.add("amount", java.lang.Integer.class);    // add property
 *     dynaClass.add("myBeans", myPackage.MyBean[].class);  // add 'array' indexed property
 *     dynaClass.add("myMap", java.util.TreeMapp.class);    // add mapped property
 * </pre>
 *
 * <p><strong>NOTE:</strong> One feature of {@link org.apache.commons.beanutils.MutableDynaClass} is that it
 *    has a <i>Restricted</i> property. When the DynaClass is <i>restricted</i> no properties can be added
 *    or removed from the <code>DynaClass</code>. Neither the <code>LazyDynaBean</code> or <code>LazyDynaMap</code>
 *    will add properties automatically if the <code>DynaClass</code> is <i>restricted</i>.</p>
 *
 *
 * <a name="conversion"></a>
 * <h1>4. Data Type Conversions</h1>
 *
 * <a name="conversion.background"></a>
 * <h3>4.1 Background</h3>
 *
 * <p>So far, we've only considered the cases where the data types of the
 * dynamically accessed properties are known, and where we can use Java casts
 * to perform type conversions.  What happens if you want to automatically
 * perform type conversions when casting is not possible?  The
 * <em>BeanUtils</em> package provides a variety of APIs and design patterns
 * for performing this task as well.</p>
 *
 * <a name="conversion.beanutils"></a>
 * <h3>4.2 <code>BeanUtils</code> and <code>ConvertUtils</code> Conversions</h3>
 *
 * <p>A very common use case (and the situation that caused the initial creation
 * of the <em>BeanUtils</em> package) was the desire to convert the set of request
 * parameters that were included in a
 * <code>javax.servlet.HttpServletRequest</code> received by a web application
 * into a set of corresponding property setter calls on an arbitrary JavaBean.
 * (This is one of the fundamental services provided by the
 * <a href="http://struts.apache.org/">Struts Framework</a>, which uses
 * <em>BeanUtils</em> internally to implement this functionality.)</p>
 *
 * <p>In an HTTP request, the set of included parameters is made available as a
 * series of String (or String array, if there is more than one value for the
 * same parameter name) instances, which need to be converted to the underlying
 * data type.  The {@link org.apache.commons.beanutils.BeanUtils} class provides
 * property setter methods that accept String values, and automatically convert
 * them to appropriate property types for Java primitives (such as
 * <code>int</code> or <code>boolean</code>), and property getter methods that
 * perform the reverse conversion.  Finally, a <code>populate()</code> method
 * is provided that accepts a <code>java.util.Map</code> containing a set of
 * property values (keyed by property name), and calls all of the appropriate
 * setters whenever the underlying bean has a property with the same name as
 * one of the request parameters.  So, you can perform the all-in-one property
 * setting operation like this:</p>
 *
 * <pre>
 *     HttpServletRequest request = ...;
 *     MyBean bean = ...;
 *     HashMap map = new HashMap();
 *     Enumeration names = request.getParameterNames();
 *     while (names.hasMoreElements()) {
 *       String name = (String) names.nextElement();
 *       map.put(name, request.getParameterValues(name));
 *     }
 *     BeanUtils.populate(bean, map);
 * </pre>
 *
 * <p>The <code>BeanUtils</code> class relies on conversion methods defined in
 * the {@link org.apache.commons.beanutils.ConvertUtils} class to perform the actual
 * conversions, and these methods are availablve for direct use as well.
 * <strong>WARNING</strong> - It is likely that the hard coded use of
 * <code>ConvertUtils</code> methods will be deprecated in the future, and
 * replaced with a mechanism that allows you to plug in your own implementations
 * of the {@link org.apache.commons.beanutils.Converter} interface instead.  Therefore,
 * new code should not be written with reliance on ConvertUtils.</p>
 *
 * <a name="conversion.defining"></a>
 * <h3>4.3 Defining Your Own Converters</h3>
 *
 * <p>The <code>ConvertUtils</code> class supports the ability to define and
 * register your own String --> Object conversions for any given Java class.
 * Once registered, such converters will be used transparently by all of the
 * <code>BeanUtils</code> methods (including <code>populate()</code>).  To
 * create and register your own converter, follow these steps:</p>
 * <ul>
 * <li>Write a class that implements the {@link org.apache.commons.beanutils.Converter}
 *     interface.  The <code>convert()</code> method should accept the
 *     <code>java.lang.Class</code> object of your application class (i.e.
 *     the class that you want to convert to, and a String representing the
 *     incoming value to be converted.</li>
 * <li>At application startup time, register an instance of your converter class
 *     by calling the <code>ConvertUtils.register()</code> method.</li>
 * </ul>
 *
 * <a name="conversion.i18n"></a>
 * <h3>4.4 Locale Aware Conversions</h3>
 * <p>The standard classes in <code>org.apache.commons.beanutils</code> are not
 * locale aware. This gives them a cleaner interface and makes then easier to use
 * in situations where the locale is not important.</p>
 * <p>Extended, locale-aware analogues can be found in
 * <code><a href='locale/package-summary.html'>org.apache.commons.beanutils.locale
 * </a></code>. These are built along the same
 * lines as the basic classes but support localization.</p>
 *
 *
 * <a name="instances"></a>
 * <h1>5. Utility Objects And Static Utility Classes</h1>
 * <a name="instances.background"></a>
 * <h3>Background</h3>
 * <p>
 * So far, the examples have covered the static utility classes (<code>BeanUtils</code>,
 * <code>ConvertUtils</code> and <code>PropertyUtils</code>). These are easy to use but are
 * somewhat inflexible. These all share the same registered converters and the same caches.
 * </p>
 * <p>
 * This functionality can also be accessed through utility objects (in fact, the static utility
 * class use worker instances of these classes). For each static utility class, there is a corresponding
 * class with the same functionality that can be instantiated:
 * </p>
 * <p>
 * <table cols='2' width='60%'>
 * <tr><th>Static Utility Class</th><th>Utility Object</th></tr>
 * <tr><td>BeanUtils</td><td>BeanUtilsBean</td></tr>
 * <tr><td>ConvertUtils</td><td>ConvertUtilsBean</td></tr>
 * <tr><td>PropertyUtils</td><td>PropertyUtilsBean</td></tr>
 * </table>
 * </p>
 * <p>
 * Creating an instances allow gives guarenteed control of the caching and registration
 * to the code that creates it.
 * </p>
 *
 * <a name="collections"></a>
 * <h1>6. Collections</h1>
 * <a name="bean-comparator"></a>
 * <h3>6.1 Comparing Beans</h3>
 * <p>
 * <code>org.apache.commons.beanutils.BeanComparator</code> is a <code>Comparator</code> implementation
 * that compares beans based on a shared property value.
 * </p>
 * <a name="bean-property-closure"></a>
 * <h3>6.2 Operating On Collections Of Beans</h3>
 * <p>
 * The <code>Closure</code> interface in <code>commons-collections</code> encapsulates a block of code that
 * executes on an arbitrary input Object. <code>Commons-collections</code> contains code that allows
 * <code>Closures</code> to be applied to the contents of a Collection. For more details, see the
 * <a href='http://commons.apache.org/collections/'>commons-collections</a>
 * documentation.
 * </p>
 * <p>
 * <code>BeanPropertyValueChangeClosure</code> is a <code>Closure</code> that sets a specified property
 * to a particular value. A typical usage is to combine this with <code>commons-collections</code>
 * so that all the beans in a collection can have a particular property set to a particular value.
 * </p>
 * <p>
 * For example, set the activeEmployee property to TRUE for an entire collection:
 *  <code><pre>
 *     // create the closure
 *     BeanPropertyValueChangeClosure closure =
 *         new BeanPropertyValueChangeClosure( "activeEmployee", Boolean.TRUE );
 *
 *     // update the Collection
 *     CollectionUtils.forAllDo( peopleCollection, closure );
 *   </pre></code>
 * </p>
 *
 * <a name="bean-property-predicate"></a>
 * <h3>6.3 Querying Or Filtering Collections Of Beans</h3>
 * <p>
 * The <code>Predicate</code> interface in <code>commons-collections</code> encapsulates an evaluation
 * of an input Object that returns either true or false. <code>Commons-collections</code> contains code
 * that allows
 * <code>Predicates</code> to be applied to be used to filter collections. For more details, see the
 * <a href='http://commons.apache.org/collections/'>commons-collections</a>
 * documentation.
 * </p>
 * <p>
 * <code>BeanPropertyValueEqualsPredicate</code> is a <code>Predicate</code> that evaluates a
 * set property value against a given value. A typical usage is
 * (in combination with <code>commons-collections</code>)
 * to filter collections on the basis of a property value.
 * </p>
 * <p>
 * For example, to filter a collection to find all beans where active employee is false use:
 * <code><pre>
 *     BeanPropertyValueEqualsPredicate predicate =
 *         new BeanPropertyValueEqualsPredicate( "activeEmployee", Boolean.FALSE );
 *
 *     // filter the Collection
 *     CollectionUtils.filter( peopleCollection, predicate );
 * </pre></code>
 * </p>
 *
 * <a href="bean-property-transformer"></a>
 * <h3>6.4 Transforming Collections Of Beans</h3>
 * <p>
 * The <code>Transformer</code> interface in <code>commons-collections</code> encapsulates the transformation
 * of an input Object into an output object. <code>Commons-collections</code> contains code
 * that allows
 * <code>Transformers</code> to be applied produce a collection of outputs from a collection of inputs.
 * For more details, see the
 * <a href='http://commons.apache.org/collections/'>commons-collections</a>
 * documentation.
 * </p>
 * <p>
 * <code>BeanToPropertyTransformer</code> is a <code>Transformer</code> implementation
 * that transforms a bean into it's property value.
 * </p>
 * <p>
 * For example, to find all cities that are contained in the address of each person property of each bean in
 * a collection:
 *     <code><pre>
 *     // create the transformer
 *     BeanToPropertyValueTransformer transformer = new BeanToPropertyValueTransformer( "person.address.city" );
 *
 *     // transform the Collection
 *     Collection peoplesCities = CollectionUtils.collect( peopleCollection, transformer );
 *     </pre></code>
 * </p>
 *
 * <a name="FAQ"></a>
 * <h1>7. Frequently Asked Questions</h1>
 *
 * <a name="FAQ.property"></a>
 * <h3>Why Can't BeanUtils Find My Method?</h3>
 * <p>The <em>BeanUtils</em> package relies on <em>introspection</em> rather than
 * <em>reflection</em>. This means that it will find only
 * <a href='http://java.sun.com/products/javabeans'><em>JavaBean</em>
 * compliant</a> properties.</p>
 * <p>There are some subtleties  of this specification that can catch out the unwary:
 * <ul>
 * <li>A property can have only one set and one get method. Overloading is not allowed.</li>
 * <li>The <code>java.beans.Introspector</code> searches widely for a custom <em>BeanInfo</em>
 * class. If your class has the same name as another with a custom <em>BeanInfo</em>
 * (typically a java API class) then the <code>Introspector</code> may use that instead of
 * creating via reflection based on your class. If this happens, the only solution is to
 * create your own <em>BeanInfo</em>.</li>
 * </ul>
 * </p>
 * <a name="FAQ.bc.order"></a>
 * <h3>How Do I Set The BeanComparator Order To Be Ascending/Descending?</h3>
 * <p>
 * BeanComparator relies on an internal Comparator to perform the actual
 * comparisions. By default,
 * <code>org.apache.commons.collections.comparators.ComparableComparator</code>
 * is used which imposes a natural order. If you want to change the order,
 * then a custom Comparator should be created and passed into the
 * appropriate constructor.
 * </p>
 * <p>
 * For example:
 * </p>
 * <code><pre>
 *     import org.apache.commons.collections.comparators.ComparableComparator;
 *     import org.apache.commons.collections.comparators.ReverseComparator;
 *     import org.apache.commons.beanutils.BeanComparator;
 *     ...
 *     BeanComparator reversedNaturalOrderBeanComparator
 *         = new BeanComparator("propertyName", new ReverseComparator(new ComparableComparator()));
 *     Collections.sort(myList, reversedNaturalOrderBeanComparator);
 *     ...
 * </pre></code>
 */
package org.apache.commons.beanutils;
