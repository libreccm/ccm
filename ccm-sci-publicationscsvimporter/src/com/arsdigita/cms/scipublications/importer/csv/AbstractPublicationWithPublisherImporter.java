package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
abstract class AbstractPublicationWithPublisherImporter<T extends PublicationWithPublisher>
        extends AbstractPublicationImporter<T> {

    public AbstractPublicationWithPublisherImporter(final CsvLine data,
                                                    final PublicationImportReport report,
                                                    final boolean pretend,
                                                    final ImporterUtil importerUtil) {
        super(data, report, pretend, importerUtil);
    }

    @Override
    protected T importPublication() {
        final T publication = super.importPublication();
        final CsvLine data = getData();

        if ((data.getIsbn() != null) && !data.getIsbn().isEmpty()) {
            final String isbn = data.getIsbn().replace("-", "");
            if (!isPretend() && isbn.length() == 13) {
                publication.setISBN(data.getIsbn());
            }
            if (isbn.length() == 13) {
                getReport().addField(new FieldImportReport("isbn", isbn));
            } else {
                getReport().addMessage(String.format("Invalid ISBN at line %d.", data.getLineNumber()));
            }
        }

        processVolume(publication);
        processNumberOfVolumes(publication);
        processNumberOfPages(publication);
        if ((data.getEdition() != null) && !data.getEdition().isEmpty()) {
            if (!isPretend()) {
                publication.setEdition(data.getEdition());
            }
            getReport().addField(new FieldImportReport("edition", data.getEdition()));
        }

        if ((data.getPublisher() != null) && !data.getPublisher().isEmpty()) {
            getImporterUtil().processPublisher(publication, getData().getPlace(), getData().getPublisher(), isPretend());
        }

        return publication;
    }

    private void processVolume(final T publication) {
        if ((getData().getVolume() != null) && !getData().getVolume().isEmpty()) {
            try {
                final int volume = Integer.parseInt(getData().getVolume());
                if (!isPretend()) {
                    publication.setVolume(volume);
                }
                getReport().addField(new FieldImportReport("Volume", getData().getVolume()));
            } catch (NumberFormatException ex) {
                getReport().addMessage(String.format("Failed to parse volume data in line %d.", getData().
                        getLineNumber()));
            }
        }
    }

    private void processNumberOfVolumes(final T publication) {
        if ((getData().getNumberOfVolumes() != null) && !getData().getNumberOfVolumes().isEmpty()) {
            try {
                final int volume = Integer.parseInt(getData().getNumberOfVolumes());
                if (!isPretend()) {
                    publication.setNumberOfVolumes(volume);
                }
                getReport().addField(new FieldImportReport("Number of volumes", getData().getNumberOfVolumes()));
            } catch (NumberFormatException ex) {
                getReport().addMessage(String.format(
                        "Failed to parse number of volumes data in line %d.", getData().getLineNumber()));
            }
        }
    }

    private void processNumberOfPages(final T publication) {
        if ((getData().getNumberOfPages() != null) && !getData().getNumberOfPages().isEmpty()) {
            try {
                final int volume = Integer.parseInt(getData().getNumberOfPages());
                if (!isPretend()) {
                    publication.setNumberOfPages(volume);
                }
                getReport().addField(new FieldImportReport("Number of pages", getData().getNumberOfPages()));
            } catch (NumberFormatException ex) {
                getReport().addMessage(String.format("Failed to parse numberOfPages data in line %d.",
                                                     getData().getLineNumber()));
            }
        }
    }

}
