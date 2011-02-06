package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

import com.arsdigita.cms.contenttypes.GenericPerson;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public abstract class AbstractBibTeXBuilder implements BibTeXBuilder {

    private static final Logger logger = Logger.getLogger(
            AbstractBibTeXBuilder.class);
    private List<GenericPerson> authors = new ArrayList<GenericPerson>();
    private List<GenericPerson> editors = new ArrayList<GenericPerson>();
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

    protected String generateBibTeXId() {
        StringBuilder builder;

        builder = new StringBuilder();

        if (authors.size() > 0) {
            builder.append(authors.get(0).getSurname().substring(0, 3));
        } else if (fields.containsKey(BibTeXField.TITLE)) {
            builder.append(fields.get(BibTeXField.TITLE));
        }

        if (fields.containsKey(BibTeXField.YEAR)) {
            builder.append(fields.get(BibTeXField.YEAR));
        }

        return builder.toString();
    }

    protected abstract List<BibTeXField> getMandatoryFields();

    protected abstract boolean isFieldSupported(final BibTeXField name);

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

    private String generatePersonFieldValue(final List<GenericPerson> persons) {
        StringBuilder builder;
        GenericPerson person;

        builder = new StringBuilder();

        for (int i = 0; i < authors.size(); i++) {
            person = authors.get(i);
            builder.append(person.getGivenName());
            builder.append(' ');
            builder.append(person.getSurname());

            if (i < (authors.size() - 1)) {
                builder.append(" and ");
            }
        }

        return builder.toString();
    }

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
