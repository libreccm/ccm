package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
abstract class AbstractPublicationWithPublisherImporter<T extends PublicationWithPublisher>
        extends AbstractPublicationImporter<T> {

    public AbstractPublicationWithPublisherImporter(final CsvLine data,
                                                    final PublicationImportReport report) {
        super(data, report);
    }

    @Override
    protected T importPublication() {
        final T publication = super.importPublication();
        final CsvLine data = getData();

        if ((data.getIsbn() != null) && !data.getIsbn().isEmpty()) {
            publication.setISBN(data.getIsbn());
            getReport().addField(new FieldImportReport("isbn", data.getIsbn()));
        }

        processVolume(publication);
        processNumberOfVolumes(publication);
        processNumberOfPages(publication);
        if ((data.getEdition() != null) && !data.getEdition().isEmpty()) {
            publication.setEdition(data.getEdition());
        }

        getImporterUtil().processPublisher(publication, getData().getPlace(), getData().getPublisher());

        return publication;
    }

    private void processVolume(final T publication) {
        if ((getData().getVolume() != null) && !getData().getVolume().isEmpty()) {
            try {
                final int volume = Integer.parseInt(getData().getVolume());
                publication.setVolume(volume);
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
                publication.setNumberOfVolumes(volume);
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
                publication.setNumberOfPages(volume);
                getReport().addField(new FieldImportReport("Number of pages", getData().getNumberOfPages()));
            } catch (NumberFormatException ex) {
                getReport().addMessage(String.format("Failed to parse numberOfPages data in line %d.", 
                                                     getData().getLineNumber()));
            }
        }
    }

//    private void processPublisher(final T publication) {
//        final String publisherName = getData().getPublisher();
//        final String place = getData().getPlace();
//
//        final Session session = SessionManager.getSession();
//        final DataCollection collection = session.retrieve(Publisher.BASE_DATA_OBJECT_TYPE);
//        collection.addEqualsFilter("title", publisherName);
//        collection.addEqualsFilter("place", place);
//        final Publisher publisher;
//        if (collection.isEmpty()) {
//            getReportWriter().printf("Publisher %s: %s not found in database. Creating...\n",
//                                     getData().getPlace(),
//                                     getData().getPublisher());
//
//            final Integer folderId = Publication.getConfig().getDefaultPublisherFolder();
//            final Folder folder = new Folder(new BigDecimal(folderId));
//            if (folder == null) {
//                throw new IllegalArgumentException("Error getting folders for publishers");
//            }
//
//            final Publisher newPublisher = new Publisher();
//            newPublisher.setPublisherName(publisherName);
//            newPublisher.setPlace(place);
//            newPublisher.setTitle(String.format("%s %s", publisherName, place));
//            newPublisher.setName(Publisher.urlSave(String.format("%s %s", publisherName, place)));
//            newPublisher.setContentSection(folder.getContentSection());
//            newPublisher.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
//            newPublisher.save();
//
//            final PublisherBundle bundle = new PublisherBundle(newPublisher);
//            bundle.setParent(folder);
//            bundle.setContentSection(folder.getContentSection());
//            bundle.save();
//
//            publisher = newPublisher;
//        } else {
//            getReportWriter().printf("Publisher %s: %s found in database. Using existing item.\n",
//                                     getData().getPlace(),
//                                     getData().getPublisher());
//            collection.next();
//            final DataObject dobj = collection.getDataObject();
//            publisher = new Publisher(dobj);
//        }
//
//        publication.setPublisher(publisher);
//    }

}
