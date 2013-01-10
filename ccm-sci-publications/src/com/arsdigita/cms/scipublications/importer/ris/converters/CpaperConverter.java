package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InProceedingsBundle;
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
public class CpaperConverter extends AbstractRisConverter {

    public PublicationImportReport convert(final RisDataset dataset,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(InProceedings.BASE_DATA_OBJECT_TYPE);

        final InProceedings inProceedings = new InProceedings();
        inProceedings.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
        final InProceedingsBundle bundle = new InProceedingsBundle(inProceedings);

        processTitle(dataset, inProceedings, report, pretend);

        processYear(dataset, pretend, inProceedings, report);

        processAuthors(dataset, RisField.AU, importerUtil, inProceedings, report, pretend);

        processProceedings(dataset, RisField.T2, RisField.PY, RisField.T2, RisField.A2, RisField.PB, RisField.C1,
                           inProceedings, importerUtil, pretend, report);
        
        processField(dataset, RisField.AB, inProceedings, "abstract", report, pretend);
        
        processPages(dataset, RisField.SP, inProceedings, pretend, report);

        return report;
    }

    public RisType getRisType() {
        return RisType.CPAPER;
    }

}
