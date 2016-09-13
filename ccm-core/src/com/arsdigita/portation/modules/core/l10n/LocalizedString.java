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
 */
package com.arsdigita.portation.modules.core.l10n;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A helper class for localisable string properties. This class is declared as
 * embeddable, so that it can be used in every other entity. The localised
 * values are stored in a {@link Map}. This class is <em>not</em> designed to be
 * overwritten. But because it is an entity class we can't make the class final.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
public class LocalizedString implements Identifiable {

    private Map<Locale, String> values;

    /**
     * Constructor. Only creates the initial, empty map for new instances.
     */
    public LocalizedString() {
        this.values = new HashMap<>();
    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new LocalizedStringMarshaller();
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
        this.values = values;
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
    public Set<Locale> getAvailableLocales() {
        return values.keySet();
    }
}
