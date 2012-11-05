package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.Review;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class ReviewImporter extends ArticleInJournalImporter {

    public ReviewImporter(final CsvLine data, 
                          final PublicationImportReport report, 
                          final boolean pretend,
                          final ImporterUtil importerUtil) {
        super(data, report, pretend, importerUtil);
    }

    @Override
    protected ArticleInJournal createPublication() {
        if (isPretend()) {
            return null;
        } else {
            return new Review();
        }
    }

}
