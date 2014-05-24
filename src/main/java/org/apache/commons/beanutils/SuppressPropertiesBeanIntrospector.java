package org.apache.commons.beanutils;

import java.beans.IntrospectionException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * A specialized {@code BeanIntrospector} implementation which suppresses some properties.
 * </p>
 * <p>
 * An instance of this class is passed a set with the names of the properties it should
 * process. During introspection of a bean class it removes all these properties from the
 * {@link IntrospectionContext}. So effectively, properties added by a different
 * {@code BeanIntrospector} are removed again.
 * </p>
 *
 * @version $Id$
 * @since 1.9.2
 */
public class SuppressPropertiesBeanIntrospector implements BeanIntrospector {
    /** A set with the names of the properties to be suppressed. */
    private final Set<String> propertyNames;

    /**
     * Creates a new instance of {@code SuppressPropertiesBeanIntrospector} and sets the
     * names of the properties to be suppressed.
     *
     * @param propertiesToSuppress the names of the properties to be suppressed (must not
     * be <b>null</b>)
     * @throws IllegalArgumentException if the collection with property names is
     * <b>null</b>
     */
    public SuppressPropertiesBeanIntrospector(Collection<String> propertiesToSuppress) {
        if (propertiesToSuppress == null) {
            throw new IllegalArgumentException("Property names must not be null!");
        }

        propertyNames = Collections.unmodifiableSet(new HashSet<String>(
                propertiesToSuppress));
    }

    /**
     * Returns a (unmodifiable) set with the names of the properties which are suppressed
     * by this {@code BeanIntrospector}.
     *
     * @return a set with the names of the suppressed properties
     */
    public Set<String> getSuppressedProperties() {
        return propertyNames;
    }

    /**
     * {@inheritDoc} This implementation removes all properties from the given context it
     * is configured for.
     */
    public void introspect(IntrospectionContext icontext) throws IntrospectionException {
        for (String property : getSuppressedProperties()) {
            icontext.removePropertyDescriptor(property);
        }
    }
}
