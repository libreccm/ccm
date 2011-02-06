/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.scipublications.exporter.ris;

import java.util.EnumMap;
import java.util.Map;

/**
 * Build an reference in RIS format.
 *
 * @author Jens Pelzetter
 */
public class RisBuilder {

    /**
     * Type of the reference
     */
    private RisTypes type;
    /**
     * Fields of the reference.
     */
    private Map<RisFields, String> fields = new EnumMap<RisFields, String>(
            RisFields.class);

    public RisBuilder() {
    }

    /**
     * Sets the type of the reference.
     *
     * @param type Valid RIS type
     */
    public void setType(final RisTypes type) {
        this.type = type;
    }

    /**
     * Adds a field to the reference.
     *
     * @param field The name of the field.
     * @param value The value of the field.
     */
    public void addField(final RisFields field, final String value) {
        fields.put(field, value);
    }

    /**
     * Creates the RIS string.
     *
     * @return The reference in the RIS format.
     */
    public String toRis() {
        StringBuilder builder;

        builder = new StringBuilder();

        appendField("TY", type.name(), builder);
        for (Map.Entry<RisFields, String> field : fields.entrySet()) {
            appendField(field.getKey().name(),
                        field.getValue(),
                        builder);
        }
        appendField("ER", "", builder);

        return builder.toString();
    }

    /**
     * Helper method for adding a field.
     *
     * @param name The name of the field.
     * @param value The value of the field.
     * @param builder The {@link StringBuilder} to use.
     */
    private void appendField(final String name,
                             final String value,
                             final StringBuilder builder) {
        builder.append(name);
        builder.append("  - ");
        builder.append(value);
        builder.append("\r\n");
    }

    @Override
    public String toString() {
        return toRis();
    }
}
