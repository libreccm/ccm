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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ImporterUtil {

    private final Set<String> createdAuthors = new HashSet<String>();
    private final Set<String> createdColVols = new HashSet<String>();
    private final Set<String> createdJournals = new HashSet<String>();
    private final Set<String> createdProcs = new HashSet<String>();
    private final Set<String> createdPublishers = new HashSet<String>();

    public AuthorImportReport processAuthor(final Publication publication,
                                            final AuthorData authorData,
                                            final boolean pretend) {
        final AuthorImportReport report = new AuthorImportReport();
        final Session session = SessionManager.getSession();

        final DataCollection collection = session.retrieve(GenericPerson.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("surname", authorData.getSurname());
        collection.addEqualsFilter("givenname", authorData.getGivenName());

        report.setSurname(authorData.getSurname());
        report.setGivenName(authorData.getGivenName());
        report.setEditor(authorData.isEditor());

        if (collection.isEmpty()) {
            if (!pretend) {
                final Integer folderId = Publication.getConfig().getDefaultAuthorsFolder();
                final Folder folder = new Folder(new BigDecimal(folderId));
                if (folder == null) {
                    throw new IllegalArgumentException("Error getting folders for authors.");
                }

                final SciAuthor author = new SciAuthor();
                author.setSurname(authorData.getSurname());
                author.setGivenName(authorData.getGivenName());
                author.setName(GenericPerson.urlSave(String.format("%s %s",
                                                                   authorData.getSurname(),
                                                                   authorData.getGivenName())));
                author.setContentSection(folder.getContentSection());
                author.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
                author.save();

                final GenericPersonBundle bundle = new GenericPersonBundle(author);
                bundle.setParent(folder);
                bundle.setContentSection(folder.getContentSection());
                bundle.save();

                publication.addAuthor(author, authorData.isEditor());
            }

            report.setCreated(true);

            //Special handling for pretend mode
            if (pretend && createdAuthors.contains(String.format("%s %s",
                                                                 authorData.getSurname(),
                                                                 authorData.getGivenName()))) {
                report.setCreated(false);
            } else {
                createdAuthors.add(String.format("%s %s", authorData.getSurname(), authorData.getGivenName()));
            }

        } else {
            if (!pretend) {
                final GenericPerson author;
                collection.next();
                author = new GenericPerson(collection.getDataObject());
                publication.addAuthor(author, authorData.isEditor());
            }
            report.setCreated(false);
        }

        collection.close();
        return report;
    }

    public PublisherImportReport processPublisher(final PublicationWithPublisher publication,
                                                  final String place,
                                                  final String publisherName,
                                                  final boolean pretend) {
        final PublisherImportReport report = new PublisherImportReport();

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(Publisher.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("title", publisherName);
        collection.addEqualsFilter("place", place);
        report.setPublisherName(publisherName);
        report.setPlace(place);
        if (collection.isEmpty()) {
            if (!pretend) {
                final Integer folderId = Publication.getConfig().getDefaultPublisherFolder();
                final Folder folder = new Folder(new BigDecimal(folderId));
                if (folder == null) {
                    throw new IllegalArgumentException("Error getting folders for publishers");
                }

                final Publisher publisher = new Publisher();
                publisher.setPublisherName(publisherName);
                publisher.setPlace(place);
                publisher.setTitle(String.format("%s %s", publisherName, place));
                publisher.setName(Publisher.urlSave(String.format("%s %s", publisherName, place)));
                publisher.setContentSection(folder.getContentSection());
                publisher.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
                publisher.save();

                final PublisherBundle bundle = new PublisherBundle(publisher);
                bundle.setParent(folder);
                bundle.setContentSection(folder.getContentSection());
                bundle.save();

                publication.setPublisher(publisher);
            }

            report.setCreated(true);

            //Special handling for pretend mode
            if (pretend && createdPublishers.contains(String.format("%s %s", publisherName, place))) {
                report.setCreated(false);
            } else {
                createdPublishers.add(String.format("%s %s", publisherName, place));
            }
        } else {
            if (!pretend) {
                collection.next();
                final Publisher publisher = new Publisher(collection.getDataObject());
                publication.setPublisher(publisher);
            }
            report.setCreated(false);
        }

        collection.close();
        return report;
    }

    public CollectedVolumeImportReport processCollectedVolume(final ArticleInCollectedVolume article,
                                                              final String title,
                                                              final String year,
                                                              final List<AuthorData> authors,
                                                              final String publisherName,
                                                              final String place,
                                                              final boolean pretend) {
        final CollectedVolumeImportReport report = new CollectedVolumeImportReport();

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(CollectedVolume.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("title", title);
        collection.addEqualsFilter("yearOfPublication", year);
        report.setCollectedVolumeTitle(title);
        if (collection.isEmpty()) {
            if (!pretend) {
                final Integer folderId = Publication.getConfig().getDefaultCollectedVolumesFolder();
                final Folder folder = new Folder(new BigDecimal(folderId));
                if (folder == null) {
                    throw new IllegalArgumentException("Error getting foldes for collected volumes.");
                }

                final CollectedVolume collectedVolume = new CollectedVolume();
                collectedVolume.setTitle(title);
                collectedVolume.setName(normalizeString(title));
                collectedVolume.setContentSection(folder.getContentSection());
                collectedVolume.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
                collectedVolume.save();

                final CollectedVolumeBundle bundle = new CollectedVolumeBundle(collectedVolume);
                bundle.setParent(folder);
                bundle.setContentSection(folder.getContentSection());
                bundle.save();

                int yearOfPub;
                try {
                    yearOfPub = Integer.parseInt(year);
                } catch (NumberFormatException ex) {
                    yearOfPub = 0;
                }

                collectedVolume.setYearOfPublication(yearOfPub);
                for (AuthorData author : authors) {
                    report.addAuthor(processAuthor(collectedVolume, author, pretend));
                }

                report.setPublisher(processPublisher(collectedVolume, place, publisherName, pretend));

                collectedVolume.save();
                article.setCollectedVolume(collectedVolume);
            }

            report.setCreated(true);

            //Special handling for pretend mode
            if (pretend && createdColVols.contains(String.format("%s %s", title, year))) {
                report.setCreated(false);
            } else {
                if (pretend) {
                    for (AuthorData author : authors) {
                        report.addAuthor(processAuthor(null, author, pretend));
                    }
                    report.setPublisher(processPublisher(null, place, publisherName, pretend));
                }
                createdColVols.add(String.format("%s %s", title, year));
            }
        } else {
            if (!pretend) {
                collection.next();
                final CollectedVolume collectedVolume = new CollectedVolume(collection.getDataObject());
                article.setCollectedVolume(collectedVolume);
            }
            report.setCreated(false);
        }

        collection.close();
        return report;
    }

    public ProceedingsImportReport processProceedings(final InProceedings inProceedings,
                                                      final String title,
                                                      final String year,
                                                      final String conference,
                                                      final List<AuthorData> authors,
                                                      final String publisherName,
                                                      final String place,
                                                      final boolean pretend) {
        final ProceedingsImportReport report = new ProceedingsImportReport();

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(Proceedings.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("title", title);
        collection.addEqualsFilter("yearOfPublication", year);
        report.setProceedingsTitle(title);
        if (collection.isEmpty()) {
            if (!pretend) {
                final Integer folderId = Publication.getConfig().getDefaultProceedingsFolder();
                final Folder folder = new Folder(new BigDecimal(folderId));
                if (folder == null) {
                    throw new IllegalArgumentException("Failed to get folder for proceedings.");
                }

                final Proceedings proceedings = new Proceedings();
                proceedings.setTitle(title);
                proceedings.setName(normalizeString(title));
                proceedings.setNameOfConference(conference);
                report.setConference(conference);
                proceedings.setContentSection(folder.getContentSection());
                proceedings.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
                proceedings.save();

                final ProceedingsBundle bundle = new ProceedingsBundle(proceedings);
                bundle.setContentSection(folder.getContentSection());
                bundle.setParent(folder);
                bundle.save();

                int yearOfPub;
                try {
                    yearOfPub = Integer.parseInt(year);
                } catch(NumberFormatException ex ){
                    yearOfPub = 0;
                }
                proceedings.setYearOfPublication(yearOfPub);
                for (AuthorData author : authors) {
                    report.addAuthor(processAuthor(proceedings, author, pretend));
                }
                               
                report.setPublisher(processPublisher(proceedings, publisherName, place, pretend));

                proceedings.save();
                inProceedings.setProceedings(proceedings);
            }

            report.setCreated(true);

            //Special handling for pretend mode
            if (pretend && createdProcs.contains(String.format("%s %s", title, year))) {
                report.setCreated(false);
            } else {
                if (pretend) {
                    for (AuthorData author : authors) {
                        report.addAuthor(processAuthor(null, author, pretend));
                    }
                    report.setPublisher(processPublisher(null, place, publisherName, pretend));
                    report.setConference(conference);
                }
                createdProcs.add(String.format("%s %s", title, year));
            }

        } else {
            if (!pretend) {
                collection.next();
                final Proceedings proceedings = new Proceedings(collection.getDataObject());
                inProceedings.setProceedings(proceedings);
            }
            report.setCreated(false);
        }

        collection.close();
        return report;
    }

    public JournalImportReport processJournal(final ArticleInJournal article,
                                              final String title,
                                              final boolean pretend) {
        final JournalImportReport report = new JournalImportReport();

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(Journal.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("title", title);

        report.setJournalTitle(title);
        if (collection.isEmpty()) {
            if (!pretend) {
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

                article.setJournal(newJournal);
            }
            report.setCreated(true);

            //Special handling for pretend mode
            if (pretend && createdJournals.contains(title)) {
                report.setCreated(false);
            } else {
                createdJournals.add(title);
            }
        } else {
            if (!pretend) {
                collection.next();
                final Journal journal = new Journal(collection.getDataObject());
                article.setJournal(journal);
            }
            report.setCreated(false);
        }

        collection.close();
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
