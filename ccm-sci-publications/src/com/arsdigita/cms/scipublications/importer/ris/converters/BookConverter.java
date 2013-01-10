package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.contenttypes.PublicationWithPublisherBundle;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import com.arsdigita.kernel.Kernel;

/**
 * Converter for the RIS type {@code BOOK} to the SciPublications type {@link Monograph}.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class BookConverter extends AbstractRisConverter {

    public PublicationImportReport convert(final RisDataset dataset,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(Monograph.BASE_DATA_OBJECT_TYPE);

        final Monograph monograph = new Monograph();
        monograph.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());        
        final PublicationWithPublisherBundle bundle = new PublicationWithPublisherBundle(monograph);

        processTitle(dataset, monograph, report, pretend);

        processYear(dataset, pretend, monograph, report);

        processAuthors(dataset, RisField.AU, importerUtil, monograph, report, pretend);

        processEditors(dataset, RisField.A3, importerUtil, monograph, report, pretend);

        processPublisher(dataset, pretend, monograph, importerUtil, report);

        processField(dataset, RisField.AB, monograph, "abstract", report, pretend);

        processField(dataset, RisField.ET, monograph, "edition", report, pretend);

        processNumberOfVolumes(dataset, pretend, monograph, report);

        processField(dataset, RisField.SN, monograph, "isbn", report, pretend);

        processNumberOfPages(dataset, pretend, monograph, report);

        processVolume(dataset, pretend, monograph, report);

        processSeries(dataset, RisField.T2, monograph, importerUtil, pretend, report);

        return report;
    }

    public RisType getRisType() {
        return RisType.BOOK;
    }

}
