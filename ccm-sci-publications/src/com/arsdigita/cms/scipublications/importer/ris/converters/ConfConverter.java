package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.ProceedingsBundle;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisAuthorUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisFieldUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisOrgaUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisSeriesUtil;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import com.arsdigita.kernel.Kernel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ConfConverter extends AbstractRisConverter<Proceedings, ProceedingsBundle> {

    @Override
    protected Proceedings createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new Proceedings();
        }
    }

    @Override
    protected ProceedingsBundle createBundle(final Proceedings publication, final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new ProceedingsBundle(publication);
        }
    }

    @Override
    protected void processFields(final RisDataset dataset,
                                 final Proceedings publication,
                                 final ImporterUtil importerUtil,
                                 final PublicationImportReport importReport,
                                 final boolean pretend) {
        final RisFieldUtil fieldUtil = new RisFieldUtil(pretend);
        final RisAuthorUtil authorUtil = new RisAuthorUtil(importerUtil, pretend);
        final RisOrgaUtil orgaUtil = new RisOrgaUtil(importerUtil, pretend);
        final RisSeriesUtil seriesUtil = new RisSeriesUtil(importerUtil, pretend);

        fieldUtil.processTitle(dataset, publication, importReport);

        fieldUtil.processIntField(dataset, RisField.C2, publication, "yearPublication", importReport);

        authorUtil.processAuthors(dataset, RisField.AU, publication, importReport);
        authorUtil.processEditors(dataset, RisField.A2, publication, importReport);

        seriesUtil.processSeries(dataset, RisField.T3, publication, importReport);

        orgaUtil.processPublisher(dataset, RisField.PB, RisField.C1, publication, importReport);

        fieldUtil.processField(dataset, RisField.AB, publication, "abstract", importReport);
        fieldUtil.processField(dataset, RisField.CY, publication, "placeOfConference", importReport);
        fieldUtil.processDateField(dataset, RisField.DA, publication, "dateFromOfConference", importReport);
        fieldUtil.processField(dataset, RisField.ET, publication, "edition", importReport);
        fieldUtil.processIntField(dataset, RisField.NV, publication, "numberOfVolumes", importReport);
        fieldUtil.processIsbn(dataset, RisField.SN, publication, importReport);
        fieldUtil.processIntField(dataset, RisField.SP, publication, "numberOfPages", importReport);
        fieldUtil.processField(dataset, RisField.T2, publication, "nameOfConference", importReport);
        fieldUtil.processIntField(dataset, RisField.VL, publication, "volume", importReport);
    }

    public RisType getRisType() {
        return RisType.CONF;
    }

}
