package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.Review;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class ReviewImporter extends ArticleInJournalImporter {

    public ReviewImporter(final CsvLine data, final PublicationImportReport report) {
        super(data, report);
    }

    @Override
    protected ArticleInJournal createPublication() {
        return new Review();
    }

}
