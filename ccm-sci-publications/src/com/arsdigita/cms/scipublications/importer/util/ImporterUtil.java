package com.arsdigita.cms.scipublications.importer.util;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.CollectedVolumeBundle;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonBundle;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.Journal;
import com.arsdigita.cms.contenttypes.JournalBundle;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.ProceedingsBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.Publisher;
import com.arsdigita.cms.contenttypes.PublisherBundle;
import com.arsdigita.cms.contenttypes.SciAuthor;
import com.arsdigita.cms.scipublications.importer.report.AuthorImportReport;
import com.arsdigita.cms.scipublications.importer.report.CollectedVolumeImportReport;
import com.arsdigita.cms.scipublications.importer.report.JournalImportReport;
import com.arsdigita.cms.scipublications.importer.report.ProceedingsImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublisherImportReport;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ImporterUtil {

    public AuthorImportReport processAuthor(final Publication publication, final AuthorData authorData) {
        final AuthorImportReport report = new AuthorImportReport();

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(GenericPerson.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("surname", authorData.getSurname());
        collection.addEqualsFilter("givenname", authorData.getGivenName());

        final GenericPerson author;
        report.setSurname(authorData.getSurname());
        report.setGivenName(authorData.getGivenName());
        report.setEditor(authorData.isEditor());

        if (collection.isEmpty()) {
            final Integer folderId = Publication.getConfig().getDefaultAuthorsFolder();
            final Folder folder = new Folder(new BigDecimal(folderId));
            if (folder == null) {
                throw new IllegalArgumentException("Error getting folders for authors.");
            }

            final SciAuthor newAuthor = new SciAuthor();
            newAuthor.setSurname(authorData.getSurname());
            newAuthor.setGivenName(authorData.getGivenName());
            newAuthor.setName(GenericPerson.urlSave(String.format("%s %s",
                                                                  authorData.getSurname(),
                                                                  authorData.getGivenName())));
            newAuthor.setContentSection(folder.getContentSection());
            newAuthor.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
            newAuthor.save();

            final GenericPersonBundle bundle = new GenericPersonBundle(newAuthor);
            bundle.setParent(folder);
            bundle.setContentSection(folder.getContentSection());
            bundle.save();

            report.setCreated(true);
            author = newAuthor;
        } else {
            collection.next();
            author = new GenericPerson(collection.getDataObject());
            report.setCreated(false);
        }

        publication.addAuthor(author, authorData.isEditor());

        return report;
    }

    public PublisherImportReport processPublisher(final PublicationWithPublisher publication,
                                                  final String place,
                                                  final String publisherName) {
        final PublisherImportReport report = new PublisherImportReport();

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(Publisher.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("title", publisherName);
        collection.addEqualsFilter("place", place);
        final Publisher publisher;
        report.setPublisherName(publisherName);
        report.setPlace(place);
        if (collection.isEmpty()) {
            final Integer folderId = Publication.getConfig().getDefaultPublisherFolder();
            final Folder folder = new Folder(new BigDecimal(folderId));
            if (folder == null) {
                throw new IllegalArgumentException("Error getting folders for publishers");
            }

            final Publisher newPublisher = new Publisher();
            newPublisher.setPublisherName(publisherName);
            newPublisher.setPlace(place);
            newPublisher.setTitle(String.format("%s %s", publisherName, place));
            newPublisher.setName(Publisher.urlSave(String.format("%s %s", publisherName, place)));
            newPublisher.setContentSection(folder.getContentSection());
            newPublisher.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
            newPublisher.save();

            final PublisherBundle bundle = new PublisherBundle(newPublisher);
            bundle.setParent(folder);
            bundle.setContentSection(folder.getContentSection());
            bundle.save();

            report.setCreated(true);
            publisher = newPublisher;
        } else {
            collection.next();
            publisher = new Publisher(collection.getDataObject());
            report.setCreated(false);
        }

        publication.setPublisher(publisher);

        return report;
    }
    
    public CollectedVolumeImportReport processCollectedVolume(final ArticleInCollectedVolume article,
                                                              final String title,
                                                              final int year,
                                                              final List<AuthorData> authors) {
        final CollectedVolumeImportReport report = new CollectedVolumeImportReport();
        
        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(CollectedVolume.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("title", title);
        collection.addEqualsFilter("yearOfPublication", year);
        final CollectedVolume collectedVolume;
        report.setCollectedVolumeTitle(title);
        if (collection.isEmpty()) {
            final Integer folderId = Publication.getConfig().getDefaultCollectedVolumesFolder();
            final Folder folder = new Folder(new BigDecimal(folderId));
            if (folder == null) {
                throw new IllegalArgumentException("Error getting foldes for collected volumes.");
            }
            
            final CollectedVolume newCollectedVolume = new CollectedVolume();
            newCollectedVolume.setTitle(title);            
            newCollectedVolume.setName(normalizeString(title));
            newCollectedVolume.setContentSection(folder.getContentSection());
             newCollectedVolume.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
            newCollectedVolume.save();
            
            final CollectedVolumeBundle bundle = new CollectedVolumeBundle(newCollectedVolume);
            bundle.setParent(folder);
            bundle.setContentSection(folder.getContentSection());
            bundle.save();
            
            newCollectedVolume.setYearOfPublication(year);
            for(AuthorData author : authors) {
                report.addAuthor(processAuthor(newCollectedVolume, author));
            }            
           
            newCollectedVolume.save();
            
            report.setCreated(true);
            collectedVolume = newCollectedVolume;            
        } else {
            collection.next();
            collectedVolume = new CollectedVolume(collection.getDataObject());
            report.setCreated(false);
        }
        
        article.setCollectedVolume(collectedVolume);
        
        return report;
    }
    
    public ProceedingsImportReport processProceedings(final InProceedings inProceedings,
                                                      final String title,
                                                      final int year,
                                                      final String conference,                                                     
                                                      final List<AuthorData> authors) {
        final ProceedingsImportReport report = new ProceedingsImportReport();
        
        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(Proceedings.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("title", title);
        collection.addEqualsFilter("yearOfPublication", year);
        final Proceedings proceedings;
        report.setProceedingsTitle(title);
        if (collection.isEmpty()) {
            final Integer folderId = Publication.getConfig().getDefaultProceedingsFolder();
            final Folder folder = new Folder(new BigDecimal(folderId));
            if (folder == null) {
                throw new IllegalArgumentException("Failed to get folder for proceedings.");
            }
            
            final Proceedings newProceedings = new Proceedings();
            newProceedings.setTitle(title);            
            newProceedings.setName(normalizeString(title));
            newProceedings.setNameOfConference(conference);
            report.setConference(conference);
            newProceedings.setContentSection(folder.getContentSection());
            newProceedings.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
            newProceedings.save();
            
            final ProceedingsBundle bundle = new ProceedingsBundle(newProceedings);
            bundle.setContentSection(folder.getContentSection());
            bundle.setParent(folder);
            bundle.save();
            
            newProceedings.setYearOfPublication(year);            
            for(AuthorData author : authors) {
               report.addAuthor(processAuthor(newProceedings, author));
            }
            
            report.setCreated(true);
            proceedings = newProceedings;                    
        } else {
            collection.next();
            proceedings = new Proceedings(collection.getDataObject());
            report.setCreated(false);
        }
        
        inProceedings.setProceedings(proceedings);
                       
        return report;
    }

    public JournalImportReport processJournal(final ArticleInJournal article, final String title) {
        final JournalImportReport report = new JournalImportReport();
        
        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(Journal.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("title", title);
        final Journal journal;
        report.setJournalTitle(title);
        if (collection.isEmpty()) {
            final Integer folderId = Publication.getConfig().getDefaultJournalsFolder();
            final Folder folder = new Folder(new BigDecimal(folderId));
            if (folder == null) {
                throw new IllegalArgumentException("Error getting folder for journals");
            }
            
            final Journal newJournal = new Journal();
            newJournal.setTitle(title);            
            newJournal.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
            newJournal.setContentSection(folder.getContentSection());
            newJournal.save();
            
            final JournalBundle bundle = new JournalBundle(newJournal);
            bundle.setParent(folder);
            bundle.setContentSection(folder.getContentSection());
            bundle.save();
            
            report.setCreated(true);
            journal = newJournal;
        } else {
            collection.next();
            journal = new Journal(collection.getDataObject());            
            report.setCreated(false);            
        }
        
        article.setJournal(journal);                        
        
        return report;
    }
    
    protected final String normalizeString(final String str) {
        if (str == null) {
            return "null";
        }
        return str.replace("ä", "ae").replace("ö", "oe").replace("ü", "ue").
                replace(
                "Ä", "Ae").replace("Ü", "Ue").replace("Ö", "Oe").replace("ß",
                                                                         "ss").
                replace(" ", "-").
                replaceAll("[^a-zA-Z0-9\\-]", "").toLowerCase().trim();
    }

}
