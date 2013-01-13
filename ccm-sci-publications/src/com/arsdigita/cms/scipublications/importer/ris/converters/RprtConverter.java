package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.UnPublishedBundle;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisAuthorUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisFieldUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisOrgaUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisSeriesUtil;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RprtConverter extends AbstractRisConverter<GreyLiterature, UnPublishedBundle> {

    @Override
    protected GreyLiterature createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new GreyLiterature();
        }
    }

    @Override
    protected UnPublishedBundle createBundle(final GreyLiterature publication,
                                             final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new UnPublishedBundle(publication);
        }
    }

    @Override
    protected void processFields(final RisDataset dataset,
                                 final GreyLiterature publication,
                                 final ImporterUtil importerUtil,
                                 final PublicationImportReport importReport,
                                 final boolean pretend) {
        final RisFieldUtil fieldUtil = new RisFieldUtil(pretend);
        final RisAuthorUtil authorUtil = new RisAuthorUtil(importerUtil, pretend);
        final RisOrgaUtil orgaUtil = new RisOrgaUtil(importerUtil, pretend);
        final RisSeriesUtil seriesUtil = new RisSeriesUtil(importerUtil, pretend);

        fieldUtil.processIntField(dataset, RisField.PY, publication, "yearOfPublication", importReport);

        authorUtil.processAuthors(dataset, RisField.AU, publication, importReport);

        seriesUtil.processSeries(dataset, RisField.T2, publication, importReport);

        orgaUtil.processOrganization(dataset, RisField.PB, pretend, publication, importerUtil, importReport);

        fieldUtil.processField(dataset, RisField.AB, publication, "abstract", importReport);
        fieldUtil.processField(dataset, RisField.CY, publication, "place", importReport);
        fieldUtil.processField(dataset, RisField.SN, publication, "number", importReport);
        fieldUtil.processIntField(dataset, RisField.SP, publication, "numberOfPages", importReport);
    }

    public RisType getRisType() {
        return RisType.RPRT;
    }

}
