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

    public ReviewImporter(final CsvLine data, final PublicationImportReport report, final boolean pretend) {
        super(data, report, pretend);
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
