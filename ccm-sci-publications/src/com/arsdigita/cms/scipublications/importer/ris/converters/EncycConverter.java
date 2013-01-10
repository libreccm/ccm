package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolumeBundle;
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
public class EncycConverter extends AbstractRisConverter {

    public PublicationImportReport convert(final RisDataset dataset,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(ArticleInCollectedVolume.BASE_DATA_OBJECT_TYPE);

        final ArticleInCollectedVolume article = new ArticleInCollectedVolume();
        article.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
        ArticleInCollectedVolumeBundle bundle = new ArticleInCollectedVolumeBundle(article);

        processTitle(dataset, article, report, pretend);

        processYear(dataset, pretend, article, report);

        processAuthors(dataset, RisField.AU, importerUtil, article, report, pretend);

        processCollectedVolume(dataset, RisField.T2, RisField.PY, RisField.A2, RisField.CY, RisField.PB, RisField.ET,
                               article, importerUtil, pretend, report);

        processField(dataset, RisField.C1, article, "chapter", report, pretend);
        processField(dataset, RisField.AB, article, "abstract", report, pretend);
        processPages(dataset, RisField.SP, article, pretend, report);


        
        return report;
    }

    public RisType getRisType() {
        return RisType.ENCYC;
    }

}
