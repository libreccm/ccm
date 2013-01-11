package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.InternetArticleBundle;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisAuthorUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisFieldUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisOrgaUtil;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class BlogConverter extends AbstractRisConverter<InternetArticle, InternetArticleBundle> {

    @Override
    protected InternetArticle createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new InternetArticle();
        }
    }

    @Override
    protected InternetArticleBundle createBundle(final InternetArticle publication,
                                                 final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new InternetArticleBundle(publication);
        }
    }

    @Override
    protected void processFields(final RisDataset dataset,
                                 final InternetArticle publication, 
                                 final ImporterUtil importerUtil,
                                 final PublicationImportReport importReport, 
                                 final boolean pretend) {
        final RisFieldUtil fieldUtil = new RisFieldUtil(pretend);
        final RisAuthorUtil authorUtil = new RisAuthorUtil(importerUtil, pretend);
        final RisOrgaUtil orgaUtil = new RisOrgaUtil(importerUtil, pretend);

        fieldUtil.processTitle(dataset, publication, importReport);

        fieldUtil.processIntField(dataset, RisField.PY, publication, "year", importReport);

        authorUtil.processAuthors(dataset, RisField.AU, publication, importReport);
        authorUtil.processEditors(dataset, RisField.A2, publication, importReport);

        fieldUtil.processField(dataset, RisField.AB, publication, "abstract", importReport);
        fieldUtil.processField(dataset, RisField.CY, publication, "place", importReport);
        fieldUtil.processField(dataset, RisField.ET, publication, "edition", importReport);

        orgaUtil.processOrganization(dataset, RisField.PB, pretend, publication, importerUtil, importReport);
        orgaUtil.processOrganization(dataset, RisField.T3, pretend, publication, importerUtil, importReport);

        fieldUtil.processField(dataset, RisField.UR, publication, "url", importReport);
        fieldUtil.processDateField(dataset, RisField.Y2, publication, "lastAccessed", importReport);
    }
   
    public RisType getRisType() {
        return RisType.BLOG;
    }

}
