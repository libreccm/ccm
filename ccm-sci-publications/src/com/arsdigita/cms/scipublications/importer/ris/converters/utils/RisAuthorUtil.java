package com.arsdigita.cms.scipublications.importer.ris.converters.utils;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.importer.report.AuthorImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.AuthorData;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RisAuthorUtil {

    private final ImporterUtil importerUtil;
    private final boolean pretend;

    public RisAuthorUtil(final ImporterUtil importerUtil, final boolean pretend) {
        this.importerUtil = importerUtil;
        this.pretend = pretend;
    }

    public void processAuthors(final RisDataset dataset, 
                               final RisField risField, 
                               final Publication publication, 
                               final PublicationImportReport report) {
        processAuthors(dataset, risField, publication, false, report);
    }

    public void processEditors(final RisDataset dataset, 
                               final RisField risField, 
                               final Publication publication, 
                               final PublicationImportReport report) {
        processAuthors(dataset, risField, publication, true, report);
    }

    private void processAuthors(final RisDataset dataset,
                                final RisField risField,
                                final Publication publication,
                                final boolean isEditors,
                                final PublicationImportReport report) {
        final List<String> authors = dataset.getValues().get(risField);
        if ((authors != null) && !authors.isEmpty()) {
            for (String authorStr : authors) {
                processAuthorStr(authorStr,
                                 isEditors,
                                 publication,
                                 report,
                                 dataset.getFirstLine());
            }
        }
    }

    private void processAuthorStr(final String authorStr,
                                  final boolean editor,
                                  final Publication publication,
                                  final PublicationImportReport importReport,
                                  final int firstLine) {
        final AuthorData authorData = new AuthorData();

        final String[] tokens = authorStr.split(",");
        if (tokens.length == 0) {
            importReport.addMessage(String.format("Failed to parse author string '%s' at dataset starting at line %d.",
                                                  authorStr, firstLine));
            return;
        }

        if (tokens.length >= 1) {
            authorData.setSurname(tokens[0]);
        }

        if (tokens.length >= 2) {
            authorData.setGivenName(tokens[1]);
        }

        authorData.setEditor(editor);

        final AuthorImportReport authorReport = importerUtil.processAuthor(publication, authorData, pretend);
        importReport.addAuthor(authorReport);
    }

}
