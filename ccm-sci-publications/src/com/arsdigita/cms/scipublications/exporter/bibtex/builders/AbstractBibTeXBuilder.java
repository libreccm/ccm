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
package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

import com.arsdigita.cms.contenttypes.GenericPerson;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Base implementation of the {@link BibTeXBuilder} interface providing common
 * functionality.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public abstract class AbstractBibTeXBuilder implements BibTeXBuilder {

    private static final Logger logger = Logger.getLogger(
            AbstractBibTeXBuilder.class);
    /**
     * The authors of the publication
     */
    private List<GenericPerson> authors = new ArrayList<GenericPerson>();
    /**
     * The editors of the publication
     */
    private List<GenericPerson> editors = new ArrayList<GenericPerson>();
    /**
     * The BibTeX fields of the reference.
     */
    private EnumMap<BibTeXField, String> fields = new EnumMap<BibTeXField, String>(
            BibTeXField.class);

    @Override
    public void addAuthor(final GenericPerson author) {
        authors.add(author);
    }

    @Override
    public void addEditor(final GenericPerson editor) {
        editors.add(editor);
    }

    @Override
    public void setField(final BibTeXField name, final String value)
            throws UnsupportedFieldException {
        if (isFieldSupported(name)) {
            fields.put(name, value);
        } else {
            throw new UnsupportedFieldException(
                    String.format("The field '%s' is not supported for "
                                  + "the BibTeX type '%s'.",
                                  name,
                                  getBibTeXType()));
        }
    }

    @Override
    public String toBibTeX() {
        String type;
        StringBuilder builder;

        if (checkMandatoryFields()) {
            type = getBibTeXType();
        } else {
            logger.warn(String.format(
                    "Missing mandandory field "
                    + "for BibTeX type '%s'. Using type 'misc'.",
                                      getBibTeXType()));
            type = "misc";
        }

        builder = new StringBuilder();

        builder.append('@');
        builder.append(type);
        builder.append('{');
        builder.append(generateBibTeXId());
        //builder.append(",\n");

        if (authors.size() > 0) {
            addField("author", generatePersonFieldValue(authors), builder);
        }

        if (editors.size() > 0) {
            addField("editor", generatePersonFieldValue(editors), builder);
        }

        for (Map.Entry<BibTeXField, String> field : fields.entrySet()) {
            addField(field.getKey().name().toLowerCase(),
                     field.getValue(),
                     builder);
        }

        builder.append("}\n");

        return builder.toString();
    }

    /**
     * Helper method for generating the BibTeX id for the reference.
     *
     * @return The BibTeX id for the reference.
     */
    protected String generateBibTeXId() {
        StringBuilder builder;

        builder = new StringBuilder();

        if (authors.size() > 0) {
            builder.append(authors.get(0));
        } else if (fields.containsKey(BibTeXField.TITLE)) {
            builder.append(fields.get(BibTeXField.TITLE));
        }

        if (fields.containsKey(BibTeXField.YEAR)) {
            builder.append(fields.get(BibTeXField.YEAR));
        }

        return builder.toString();
    }

    /**
     *
     * @return Fields mandatory for the BibTeX type.
     */
    protected abstract List<BibTeXField> getMandatoryFields();

    /**
     * Checks if a field is supported by the BibTeX type.
     *
     * @param name The name of the field.
     * @return <code>true</code> if the field is supported, <code>false</code>
     * otherwise.
     */
    protected abstract boolean isFieldSupported(final BibTeXField name);

    /**
     * Checks if all mandatory fields are present.
     *
     * @return <code>false</code> if not all mandatory fields are present,
     * <code>true</code> if all required fields are present.
     */
    private boolean checkMandatoryFields() {
        if (getMandatoryFields().contains(BibTeXField.AUTHOR)
            && authors.isEmpty()) {
            logger.warn("Field authors is mandatory, but publications has "
                        + "not authors.");
            return false;
        }

        if (getMandatoryFields().contains(BibTeXField.EDITOR)
            && editors.isEmpty()) {
            logger.warn("Field editors is mandatory, but publications has "
                        + "no editors.");
            return false;
        }

        for (BibTeXField field : getMandatoryFields()) {
            if (!BibTeXField.AUTHOR.equals(field)
                && !BibTeXField.EDITOR.equals(field)
                && !fields.containsKey(field)) {
                logger.warn(String.format(
                        "Field '%s' is mandandory for the "
                        + "selected BibTeX type, but is not set.",
                        field.name().toLowerCase()));
                return false;
            }
        }

        return true;
    }

   /**
    * Helper function for generating to <code>authors</code> and the
    * <code>editors</code> field.
    *
    * @param persons List of persons for the field.
    * @return The value for the authors or editors field.
    */
    private String generatePersonFieldValue(final List<GenericPerson> persons) {
        StringBuilder builder;
        GenericPerson person;

        builder = new StringBuilder();

        for (int i = 0; i < persons.size(); i++) {
            person = persons.get(i);
            builder.append(person.getGivenName());
            builder.append(' ');
            builder.append(person.getSurname());

            if (i < (persons.size() - 1)) {
                builder.append(" and ");
            }
        }

        return builder.toString();
    }

    /**
     * Helper function for adding a field to the string.
     *
     * @param name The name of  the field.
     * @param value The value of the field.
     * @param builder The {@link StringBuilder} to use.
     */
    private void addField(final String name,
                          final String value,
                          final StringBuilder builder) {
        builder.append(",\n");
        builder.append(' ');
        builder.append(name);
        builder.append(" = \"");
        builder.append(value);
        builder.append("\"");
    }
}
