package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisConverter;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.util.List;

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

        publication.setTitle(dataset.getValues().get(RisField.TI).get(0));
        report.setTitle(dataset.getValues().get(RisField.TI).get(0));

        final String yearStr = dataset.getValues().get(RisField.PY).get(0);
        try {
            final int year = Integer.parseInt(yearStr);
            publication.setYearOfPublication(year);
            report.addField(new FieldImportReport("year", yearStr));
        } catch (NumberFormatException ex) {
            report.addMessage(String.format("Failed to convert year of publication value '%s' from RIS to"
                                            + "integer value. Setting year of publication to 0"));
            publication.setYearOfPublication(0);
        }

        processAuthors(dataset, RisField.AU, importerUtil, publication, report, pretend);
        processAuthors(dataset, RisField.A2, importerUtil, publication, report, pretend);
        processAuthors(dataset, RisField.A3, importerUtil, publication, report, pretend);
        processAuthors(dataset, RisField.A4, importerUtil, publication, report, pretend);

        processPublisher(dataset, pretend, publication, importerUtil, report);

        final List<String> abstractList = dataset.getValues().get(RisField.AB);
        if ((abstractList != null) && (!abstractList.isEmpty())) {
            publication.setAbstract(abstractList.get(0));
            report.addField(new FieldImportReport("abstract", abstractList.get(0)));
        }

        final List<String> edition = dataset.getValues().get(RisField.ET);
        if ((edition != null) && !edition.isEmpty()) {
            publication.setEdition(edition.get(0));
            report.addField(new FieldImportReport("edition", edition.get(0)));
        }

        final List<String> isbn = dataset.getValues().get(RisField.SN);
        if ((isbn != null) && !isbn.isEmpty()) {
            publication.setISBN(isbn.get(0));
            report.addField(new FieldImportReport("isbn", isbn.get(0)));
        }

        processNumberOfPages(dataset, pretend, publication, report);

        processVolume(dataset, pretend, publication, report);

        return report;
    }

    public RisType getRisType() {
        return RisType.GEN;
    }

}
