/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */package org.libreccm.l10n;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.libreccm.l10n.jaxb.LocalizedStringValuesAdapter;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@XmlRootElement(name = "localized-string",
                namespace = L10NConstants.L10N_XML_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class LocalizedString implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The localised values of the string.
     */
    @XmlElement(name = "values", namespace = L10NConstants.L10N_XML_NS)
    @XmlJavaTypeAdapter(LocalizedStringValuesAdapter.class)
    private Map<Locale, String> values;

    /**
     * Constructor. Only creates the initial, empty map for new instances.
     */
    public LocalizedString() {
        values = new HashMap<>();
    }

    /**
     * Get all localised values.
     *
     * @return A unmodifiable {@code Map} containing all localised values of
     *         this localised string.
     */
    public Map<Locale, String> getValues() {
        if (values == null) {
            return null;
        } else {
            return Collections.unmodifiableMap(values);
        }
    }

    /**
     * Setter for replacing the complete {@code Map} of values. Only to be used
     * by JPA and the Repository classes in the package.
     *
     * @param values The new map of values.
     */
    protected void setValues(final Map<Locale, String> values) {
        if (values == null) {
            this.values = new HashMap<>();
        } else {
            this.values = new HashMap<>(values);
        }
    }

    /**
     * Retrieves the values for the default locale.
     *
     * @return The localised value for the default locale of the system the
     *         application is running on. In most cases this is not what you
     *         want. Use {@link #getValue(java.util.Locale)} instead.
     */
    public String getValue() {
        return getValue(Locale.getDefault());
    }

    /**
     * Retrieves the localised value of a locale.
     *
     * @param locale The locale for which the value shall be retrieved.
     *
     * @return The localised for the {@code locale} or {@code null} if there is
     *         no value for the provided locale.
     */
    public String getValue(final Locale locale) {
        return values.get(locale);
    }

    /**
     * Add a new localised value for a locale. If there is already a value for
     * the provided locale the value is replaced with the new value.
     *
     * @param locale The locale of the provided value.
     * @param value  The localised value for the provided locale.
     */
    public void addValue(final Locale locale, final String value) {
        values.put(locale, value);
    }

    /**
     * Removes the value for the provided locale.
     *
     * @param locale The locale for which the value shall be removed.
     */
    public void removeValue(final Locale locale) {
        values.remove(locale);
    }

    /**
     * Checks if a localised string instance has a value for a locale.
     *
     * @param locale The locale.
     *
     * @return {@code true} if this localised string has a value for the
     *         provided locale, {@code false} if not.
     */
    public boolean hasValue(final Locale locale) {
        return values.containsKey(locale);
    }

    /**
     * Retrieves all present locales.
     *
     * @return A {@link Set} containing all locales for which this localised
     *         string has values.
     */
    @JsonIgnore
    public Set<Locale> getAvailableLocales() {
        return values.keySet();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.values);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof LocalizedString)) {
            return false;
        }
        final LocalizedString other = (LocalizedString) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(values, other.getValues());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof LocalizedString;
    }

    @Override
    public String toString() {
        return String.format(
            "%s{ "
                + "%s"
                + " }",
            super.toString(),
            Objects.toString(values));
    }

}
