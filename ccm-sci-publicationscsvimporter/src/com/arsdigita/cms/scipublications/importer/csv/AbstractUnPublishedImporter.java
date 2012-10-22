package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
abstract class AbstractUnPublishedImporter<T extends UnPublished> extends AbstractPublicationImporter<T> {

    protected AbstractUnPublishedImporter(final CsvLine data,
                                          final PublicationImportReport report) {
        super(data, report);
    }

    @Override
    protected T importPublication() {
        final T publication = super.importPublication();
        final CsvLine data = getData();
        final PublicationImportReport report = getReport();

        if ((data.getPlace() != null) && !data.getPlace().isEmpty()) {
            publication.setPlace(data.getPlace());
            report.addField(new FieldImportReport("place", data.getPlace()));
        }

        processNumberOfPages(publication);
        
        return publication;
    }

    private void processNumberOfPages(final T publication) {
        if ((getData().getNumberOfPages() != null) && !getData().getNumberOfPages().isEmpty()) {
            try {
                final int volume = Integer.parseInt(getData().getNumberOfPages());
                publication.setNumberOfPages(volume);
                getReport().addField(new FieldImportReport("Number of pages", getData().getNumberOfPages()));
            } catch (NumberFormatException ex) {
                getReport().addMessage(String.format("Failed to parse numberOfPages data in line %d.",
                                                     getData().getLineNumber()));
            }
        }
    }

}
