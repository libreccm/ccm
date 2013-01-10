package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.ArticleInJournalBundle;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import com.arsdigita.kernel.Kernel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class JourConverter extends AbstractRisConverter {

    @Override
    public PublicationImportReport convert(final RisDataset dataset,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(ArticleInJournal.BASE_DATA_OBJECT_TYPE);

        final ArticleInJournal article = new ArticleInJournal();
        article.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
        final ArticleInJournalBundle bundle = new ArticleInJournalBundle(article);

        processTitle(dataset, article, report, pretend);

        processYear(dataset, pretend, article, report);

        processAuthors(dataset, RisField.AU, importerUtil, article, report, pretend);

        processField(dataset, RisField.AB, article, "abstract", report, pretend);

        processJournal(dataset, RisField.T2, article, importerUtil, pretend, report);

        processField(dataset, RisField.M1, article, "issue", report, pretend);

        processPages(dataset, RisField.SP, article, pretend, report);

        processField(dataset, RisField.VL, article, "volume", report, pretend);

        processIntField(dataset, RisField.M2, article, "pagesFrom", report, pretend);
                
        return report;
    }

    @Override
    public RisType getRisType() {
        return RisType.JOUR;
    }

}
