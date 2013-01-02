package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.util.List;

/**
 * Converter for the RIS type {@code EBOOK} to the SciPublications type {@link Monograph}.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class EbookConverter extends AbstractRisConverter {

    public PublicationImportReport convert(final RisDataset dataset,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(Monograph.BASE_DATA_OBJECT_TYPE);

        final Monograph monograph = new Monograph();

        monograph.setTitle(dataset.getValues().get(RisField.TI).get(0));
        report.setTitle(dataset.getValues().get(RisField.TI).get(0));

        processYear(dataset, pretend, monograph, report);

        processAuthors(dataset, RisField.AU, importerUtil, monograph, report, pretend);

        processEditors(dataset, RisField.A3, importerUtil, monograph, report, pretend);

        processPublisher(dataset, pretend, monograph, importerUtil, report);

        final List<String> abstractList = dataset.getValues().get(RisField.AB);
        if ((abstractList != null) && (!abstractList.isEmpty())) {
            monograph.setAbstract(abstractList.get(0));
            report.addField(new FieldImportReport("abstract", abstractList.get(0)));
        }

        final List<String> edition = dataset.getValues().get(RisField.ET);
        if ((edition != null) && !edition.isEmpty()) {
            monograph.setEdition(edition.get(0));
            report.addField(new FieldImportReport("edition", edition.get(0)));
        }

        final List<String> isbn = dataset.getValues().get(RisField.SN);
        if ((isbn != null) && !isbn.isEmpty()) {
            monograph.setISBN(isbn.get(0));
            report.addField(new FieldImportReport("isbn", isbn.get(0)));
        }

        processNumberOfPages(dataset, pretend, monograph, report);

        processVolume(dataset, pretend, monograph, report);

        final List<String> series = dataset.getValues().get(RisField.T2);
        if ((series != null) && !series.isEmpty()) {
            report.setSeries(importerUtil.processSeries(monograph, series.get(0), pretend));
        }

        return report;
    }

    public RisType getRisType() {
        return RisType.EBOOK;
    }

}
