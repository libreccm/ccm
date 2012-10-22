package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.ArticleInJournalBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.Review;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

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
        final Integer folderId = Publication.getConfig().getDefaultPublicationsFolder();
        final Folder folder = new Folder(new BigDecimal(folderId));

        final Review review = new Review();
        review.setContentSection(folder.getContentSection());
        review.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());

        final ArticleInJournalBundle bundle = new ArticleInJournalBundle(review);
        bundle.setParent(folder);
        bundle.setContentSection(folder.getContentSection());

        return review;
    }

    
}
