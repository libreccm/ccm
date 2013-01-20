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
@SuppressWarnings("PMD.CyclomaticComplexity")
public class RisParser {

    public RisParser() {
        //Nothing
    }

    public List<RisDataset> parse(final String[] lines) throws SciPublicationsImportException {
        final List<RisDataset> entries = new ArrayList<RisDataset>();
        boolean openDataset = false;
        RisField lastField = null;

        RisFieldValue field;
        for (int i = 0; i < lines.length; i++) {

            if ((lines[i] == null) || (skipBom(lines[i]) == null) || skipBom(lines[i]).isEmpty()) {
                continue;
            }

            field = parseRisLine(lines[i], i);
            
            if (field == null) {
                continue;
            }

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
            } else if (field.getName() == null) {
                final RisDataset currentDataset = entries.get(entries.size() - 1);
                final List<String> data = currentDataset.getValues().get(lastField);
                data.set(data.size() - 1, data.get(data.size() - 1) + field.getValue());
            } else {
                final RisDataset currentDataset = entries.get(entries.size() - 1);
                if (currentDataset.getValues().get(field.getName()) == null) {
                    final List<String> data = new ArrayList<String>();
                    data.add(field.getValue());
                    currentDataset.addField(field.getName(), data);
                } else {
                    final List<String> data = currentDataset.getValues().get(field.getName());
                    data.add(field.getValue());
                }
                lastField = field.getName();
            }
        }

        return Collections.unmodifiableList(entries);
    }

    private RisDataset startDataset(final String type, final int firstLine) {
        final RisType risType = RisType.valueOf(type);
        return new RisDataset(risType, firstLine);
    }

    private RisFieldValue parseRisLine(final String line, final int index) throws SciPublicationsImportException {
        final String[] tokens = skipBom(line).split("  - ");

        if (tokens.length == 2) {
            final RisField fieldName;
            try {
                fieldName = RisField.valueOf(tokens[0]);
            } catch (IllegalArgumentException ex) {
//                throw new SciPublicationsImportException(String.format("Unkwown tag '%s' in line %d. Aborting import.",
//                                                                       tokens[0], index + 1), ex);
                //Ignore unknown none standard fields
                return null;
            }

            return new RisFieldValue(fieldName, tokens[1]);
        } else if ((tokens.length == 1) && (tokens[0] != null) && tokens[0].startsWith(RisField.ER.toString())) {
            return new RisFieldValue(RisField.ER, "");
        } else {
            return new RisFieldValue(null, line);
        }
    }

    /**
     * Skip possible UTF-8 BOM 
     * 
     * @param str
     * @return 
     */
    private String skipBom(final String str) {
        if ((str == null) || str.isEmpty()) {
            return null;
        }

        final char firstChar = str.charAt(0);


        // Hex value of BOM = EF BB BF  => int 65279        
        if (firstChar == 65279) {
            return str.substring(1);
        } else {
            return str;
        }
    }

}
