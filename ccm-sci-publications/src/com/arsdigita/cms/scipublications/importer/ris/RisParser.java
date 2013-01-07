package com.arsdigita.cms.scipublications.importer.ris;

import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisFieldValue;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.SciPublicationsImportException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RisParser {

    public RisParser() {
        //Nothing
    }

    public List<RisDataset> parse(final String[] lines) throws SciPublicationsImportException {
        final List<RisDataset> entries = new ArrayList<RisDataset>();
        boolean openDataset = false;

        RisFieldValue field;
        for (int i = 0; i < lines.length; i++) {
            field = parseRisLine(lines[i], i);

            if (RisField.TY.equals(field.getName())) {
                if (openDataset) {
                    throw new SciPublicationsImportException(
                            String.format("Start of new reference before preceding reference was closed at line %d. "
                                          + "Aborting import.", i + 1));
                } else {
                    try {
                        entries.add(startDataset(field.getValue(), i + 1));
                        openDataset = true;
                    } catch (IllegalArgumentException ex) {
                        throw new SciPublicationsImportException(String.format("Invalid type at line %d.", i + 1), ex);
                    }
                }
            } else if (RisField.ER.equals(field.getName())) {
                openDataset = false;
            } else {
                final RisDataset currentDataset = entries.get(entries.size() - 1);
                if (currentDataset.getValues().get(field.getName()) == null) {
                    final List<String> data = new ArrayList<String>();
                    data.add(field.getValue());
                    currentDataset.getValues().put(field.getName(), data);
                } else {
                    final List<String> data = currentDataset.getValues().get(field.getName());
                    data.add(field.getValue());
                }
            }
        }

        return Collections.unmodifiableList(entries);
    }

    private RisDataset startDataset(final String type, final int firstLine) {
        final RisType risType = RisType.valueOf(type);
        return new RisDataset(risType, firstLine);
    }

    private RisFieldValue parseRisLine(final String line, final int index) throws SciPublicationsImportException {
        final String[] tokens = line.split("  - ");

        if (tokens.length == 2) {
            final RisField fieldName;
            try {
                fieldName = RisField.valueOf(tokens[0]);
            } catch (IllegalArgumentException ex) {
                throw new SciPublicationsImportException(String.format("Unkwown tag '%s' in line %d. Aborting import.",
                                                                       tokens[0], index + 1), ex);
            }

            return new RisFieldValue(fieldName, line);
        } else if ((tokens.length == 1) && RisField.ER.toString().equals(tokens[0])) {
            return new RisFieldValue(RisField.ER, "");
        } else {
            throw new SciPublicationsImportException(String.format("Invalid RIS data in line %d. Aborting import.",
                                                                   index + 1));
        }
    }

}
