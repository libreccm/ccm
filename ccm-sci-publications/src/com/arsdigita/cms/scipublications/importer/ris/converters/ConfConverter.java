package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.ProceedingsBundle;
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
public class ConfConverter extends AbstractRisConverter {

    public PublicationImportReport convert(final RisDataset dataset,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(Proceedings.BASE_DATA_OBJECT_TYPE);

        final Proceedings proceedings = new Proceedings();
        proceedings.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
        final ProceedingsBundle bundle = new ProceedingsBundle(proceedings);        

        processTitle(dataset, proceedings, report, pretend);

        processYear(dataset, RisField.C2, pretend, proceedings, report);

        processAuthors(dataset, RisField.AU, importerUtil, proceedings, report, pretend);

        processEditors(dataset, RisField.A2, importerUtil, proceedings, report, pretend);

        processSeries(dataset, RisField.T3, proceedings, importerUtil, pretend, report);

        processPublisher(dataset, RisField.PB, RisField.C1, pretend, proceedings, importerUtil, report);

        processField(dataset, RisField.AB, proceedings, "abstract", report, pretend);
        processField(dataset, RisField.CY, proceedings, "placeOfConference", report, pretend);
        processDateField(dataset, RisField.DA, proceedings, "dateFromOfConference", report, pretend);
        processField(dataset, RisField.ET, proceedings, "edition", report, pretend);
        processNumberOfVolumes(dataset, pretend, proceedings, report);
        processField(dataset, RisField.SN, proceedings, "isbn", report, pretend);
        processNumberOfPages(dataset, pretend, proceedings, report);
        processField(dataset, RisField.T2, proceedings, "nameOfConference", report, pretend);
        processVolume(dataset, pretend, proceedings, report);

        return report;
    }

    public RisType getRisType() {
        return RisType.CONF;
    }

}
