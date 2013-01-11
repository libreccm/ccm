package com.arsdigita.cms.scipublications.importer.ris.converters.utils;

import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.importer.report.JournalImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RisJournalUtil {

    private final ImporterUtil importerUtil;
    private final boolean pretend;

    public RisJournalUtil(final ImporterUtil importerUtil, final boolean pretend) {
        this.importerUtil = importerUtil;
        this.pretend = pretend;
    }

    public void processJournal(final RisDataset dataset, 
                               final RisField field, 
                               final ArticleInJournal article, 
                               final PublicationImportReport report) {
        final List<String> journal = dataset.getValues().get(field);
        if ((journal != null) && !journal.isEmpty()) {
            final JournalImportReport journalReport = importerUtil.processJournal(article, journal.get(0), pretend);
            report.setJournal(journalReport);
        }
    }

}
