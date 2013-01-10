package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.CollectedVolumeBundle;
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
public class EdbookConverter extends AbstractRisConverter {

    public PublicationImportReport convert(final RisDataset dataset,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(CollectedVolume.BASE_DATA_OBJECT_TYPE);

        final CollectedVolume collectedVolume = new CollectedVolume();
        collectedVolume.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
        final CollectedVolumeBundle bundle = new CollectedVolumeBundle(collectedVolume);

        processTitle(dataset, collectedVolume, report, pretend);

        processEditors(dataset, RisField.AU, importerUtil, collectedVolume, report, pretend);

        processYear(dataset, pretend, collectedVolume, report);

        processPublisher(dataset, pretend, collectedVolume, importerUtil, report);

        processField(dataset, RisField.AB, collectedVolume, "abstract", report, pretend);
        processField(dataset, RisField.ET, collectedVolume, "edition", report, pretend);
        processNumberOfVolumes(dataset, pretend, collectedVolume, report);
        processField(dataset, RisField.SN, collectedVolume, "isbn", report, pretend);
        processNumberOfPages(dataset, pretend, collectedVolume, report);
        processVolume(dataset, pretend, collectedVolume, report);


        return report;
    }

    public RisType getRisType() {
        return RisType.EDBOOK;
    }

}
