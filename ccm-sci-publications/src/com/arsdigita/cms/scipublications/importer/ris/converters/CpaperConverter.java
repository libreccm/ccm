package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InProceedingsBundle;
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
public class CpaperConverter extends AbstractRisConverter<InProceedings, InProceedingsBundle> {

    @Override
    protected InProceedings createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new InProceedings();
        }
    }

    @Override
    protected InProceedingsBundle createBundle(final InProceedings publication,
                                               final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new InProceedingsBundle(publication);
        }
    }

    @Override
    protected void processFields(final RisDataset dataset,
                                 final InProceedings publication,
                                 final ImporterUtil importerUtil,
                                 final PublicationImportReport importReport,
                                 final boolean pretend) {
        final RisFieldUtil fieldUtil = new RisFieldUtil(pretend);
        final RisAuthorUtil authorUtil = new RisAuthorUtil(importerUtil, pretend);
        final RisColVolUtil colVolUtil = new RisColVolUtil(importerUtil, pretend);

        fieldUtil.processTitle(dataset, publication, importReport);

        fieldUtil.processIntField(dataset, RisField.PY, publication, "year", importReport);

        authorUtil.processAuthors(dataset, RisField.AU, publication, importReport);

        colVolUtil.processProceedings(dataset, RisField.T2, RisField.PY, RisField.T2, RisField.A2, RisField.PB,
                                      RisField.C1,
                                      publication, importReport);

        fieldUtil.processField(dataset, RisField.AB, publication, "abstract", importReport);

        fieldUtil.processPages(dataset, RisField.SP, publication, importReport);
    }

    public RisType getRisType() {
        return RisType.CPAPER;
    }

}
