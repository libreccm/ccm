package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolumeBundle;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisAuthorUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisColVolUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisFieldUtil;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class EncycConverter extends AbstractRisConverter<ArticleInCollectedVolume, ArticleInCollectedVolumeBundle> {    

    @Override
    protected ArticleInCollectedVolume createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new ArticleInCollectedVolume();
        }
    }
    
    @Override
    protected String getTypeName() {
        return ArticleInCollectedVolume.class.getName();
    }

    @Override
    protected ArticleInCollectedVolumeBundle createBundle(final ArticleInCollectedVolume publication,
                                                          final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new ArticleInCollectedVolumeBundle(publication);
        }
    }

    @Override
    protected void processFields(final RisDataset dataset,
                                 final ArticleInCollectedVolume publication,
                                 final ImporterUtil importerUtil,
                                 final PublicationImportReport importReport,
                                 final boolean pretend) {
        final RisFieldUtil fieldUtil = new RisFieldUtil(pretend);
        final RisAuthorUtil authorUtil = new RisAuthorUtil(importerUtil, pretend);
        final RisColVolUtil colVolUtil = new RisColVolUtil(importerUtil, pretend);
    
        fieldUtil.processIntField(dataset, RisField.PY, publication, "yearOfPublication", importReport);

        authorUtil.processAuthors(dataset, RisField.AU, publication, importReport);

        colVolUtil.processCollectedVolume(dataset, RisField.T2, RisField.PY, RisField.A2, RisField.CY, RisField.PB,
                                          RisField.ET,
                                          publication, importReport);

        fieldUtil.processField(dataset, RisField.C1, publication, "chapter", importReport);
        fieldUtil.processField(dataset, RisField.AB, publication, "abstract", importReport);
        fieldUtil.processPages(dataset, RisField.SP, publication, importReport);
    }

    public RisType getRisType() {
        return RisType.ENCYC;
    }

}
