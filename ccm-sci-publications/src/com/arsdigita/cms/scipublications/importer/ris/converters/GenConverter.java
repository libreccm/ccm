package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 * Converter for the RIS type {@code GEN} to the SciPublications {@link GreyLiterature}
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class GenConverter extends AbstractRisConverter {

    public PublicationImportReport convert(final RisDataset dataset,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(GreyLiterature.BASE_DATA_OBJECT_TYPE);

        final Monograph publication = new Monograph();

        processTitle(dataset, publication, report, pretend);

        processYear(dataset, pretend, publication, report);

        processAuthors(dataset, RisField.AU, importerUtil, publication, report, pretend);
        processAuthors(dataset, RisField.A2, importerUtil, publication, report, pretend);
        processAuthors(dataset, RisField.A3, importerUtil, publication, report, pretend);
        processAuthors(dataset, RisField.A4, importerUtil, publication, report, pretend);

        processPublisher(dataset, pretend, publication, importerUtil, report);

        processField(dataset, RisField.AB, publication, "abstract", report, pretend);

        processField(dataset, RisField.ET, publication, "edition", report, pretend);

        processField(dataset, RisField.SN, publication, "isbn", report, pretend);

        processNumberOfPages(dataset, pretend, publication, report);

        processNumberOfVolumes(dataset, pretend, publication, report);
        
        processVolume(dataset, pretend, publication, report);

        return report;
    }

    public RisType getRisType() {
        return RisType.GEN;
    }

}
