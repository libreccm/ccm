package com.arsdigita.cms.scipublications.importer.util;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.AuthorshipCollection;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.CollectedVolumeBundle;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitBundle;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonBundle;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.Journal;
import com.arsdigita.cms.contenttypes.JournalBundle;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.ProceedingsBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.Publisher;
import com.arsdigita.cms.contenttypes.PublisherBundle;
import com.arsdigita.cms.contenttypes.SciAuthor;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.contenttypes.SeriesBundle;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.cms.scipublications.importer.report.AuthorImportReport;
import com.arsdigita.cms.scipublications.importer.report.CollectedVolumeImportReport;
import com.arsdigita.cms.scipublications.importer.report.JournalImportReport;
import com.arsdigita.cms.scipublications.importer.report.OrganizationalUnitImportReport;
import com.arsdigita.cms.scipublications.importer.report.ProceedingsImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublisherImportReport;
import com.arsdigita.cms.scipublications.importer.report.SeriesImportReport;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ImporterUtil {

    private final transient boolean publish;
    private final transient Set<String> createdAuthors = new HashSet<String>();
    private final transient Set<String> createdColVols = new HashSet<String>();
    private final transient Set<String> createdJournals = new HashSet<String>();
    private final transient Set<String> createdProcs = new HashSet<String>();
    private final transient Set<String> createdPublishers = new HashSet<String>();
    private final transient Set<String> createdOrgas = new HashSet<String>();
    private final transient Set<String> createdSeries = new HashSet<String>();

    public ImporterUtil() {
        publish = false;
    }

    public ImporterUtil(final boolean publish) {
        this.publish = publish;
    }

    public AuthorImportReport processAuthor(final Publication publication,
                                            final AuthorData authorData,
                                            final boolean pretend) {
        final AuthorImportReport report = new AuthorImportReport();
        final Session session = SessionManager.getSession();

        final DataCollection collection = session.retrieve(GenericPerson.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("surname", authorData.getSurname());
        if ((authorData.getGivenName() != null) && !authorData.getGivenName().isEmpty()) {
            collection.addEqualsFilter("givenname", authorData.getGivenName());
        }

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

                if (publish) {
                    publishItem(author);
                }
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
        collection.addEqualsFilter("publisherName", publisherName);
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

                if (publish) {
                    publishItem(publisher);
                }
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
                                                              final String edition,
                                                              final boolean pretend) {
        final CollectedVolumeImportReport report = new CollectedVolumeImportReport();

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(CollectedVolume.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("title", title);
        int yearOfPublication;
        try {
            yearOfPublication = Integer.parseInt(year);
        } catch(NumberFormatException ex) {
            yearOfPublication = 0;
        }
        collection.addEqualsFilter("yearOfPublication", yearOfPublication);
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
                final String name = normalizeString(title);
                if (name.length() < 200) {
                    collectedVolume.setName(name);
                } else {
                    collectedVolume.setName(name.substring(0, 200));
                }
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
                    if ((author.getSurname() != null) && !author.getSurname().isEmpty()) {
                        report.addAuthor(processAuthor(collectedVolume, author, pretend));
                    }
                }

                if ((publisherName != null) && !publisherName.isEmpty()) {
                    report.setPublisher(processPublisher(collectedVolume, place, publisherName, pretend));
                }

                if ((edition != null) && !edition.isEmpty()) {
                    collectedVolume.setEdition(edition);
                }

                collectedVolume.save();
                article.setCollectedVolume(collectedVolume);

                if (publish) {
                    publishItem(collectedVolume);
                }
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
                } catch (NumberFormatException ex) {
                    yearOfPub = 0;
                }
                proceedings.setYearOfPublication(yearOfPub);
                for (AuthorData author : authors) {
                    if ((author.getSurname() != null) && !author.getSurname().isEmpty()) {
                        report.addAuthor(processAuthor(proceedings, author, pretend));
                    }
                }

                if ((publisherName != null) && !publisherName.isEmpty()) {
                    report.setPublisher(processPublisher(proceedings, place, publisherName, pretend));
                }

                proceedings.save();
                inProceedings.setProceedings(proceedings);

                if (publish) {
                    publishItem(proceedings);
                }
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

                final Journal journal = new Journal();
                journal.setName(normalizeString(title));
                journal.setTitle(title);
                journal.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
                journal.setContentSection(folder.getContentSection());
                journal.save();

                final JournalBundle bundle = new JournalBundle(journal);
                bundle.setParent(folder);
                bundle.setContentSection(folder.getContentSection());
                bundle.save();

                article.setJournal(journal);

                if (publish) {
                    publishItem(journal);
                }
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

    public OrganizationalUnitImportReport processOrganization(final UnPublished publication,
                                                              final String name,
                                                              final boolean pretend) {
        final OrganizationalUnitImportReport report = new OrganizationalUnitImportReport();

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(GenericOrganizationalUnit.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("title", name);
        report.setName(name);
        if (collection.isEmpty()) {
            if (!pretend) {
                final Integer folderId = Publication.getConfig().getDefaultOrganizationsFolder();
                final Folder folder = new Folder(new BigDecimal(folderId));
                if (folder == null) {
                    throw new IllegalArgumentException("Error getting folder for organizations.");
                }

                final GenericOrganizationalUnit orga = new GenericOrganizationalUnit(Publication.getConfig().
                        getOrganizationType());
                orga.setTitle(name);
                orga.setName(normalizeString(name));
                orga.setContentSection(folder.getContentSection());
                orga.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
                orga.save();

                final GenericOrganizationalUnitBundle bundle = new GenericOrganizationalUnitBundle(Publication.
                        getConfig().getOrganizationBundleType());
                bundle.setDefaultLanguage(orga.getLanguage());
                bundle.setContentType(orga.getContentType());
                bundle.addInstance(orga);
                bundle.setName(orga.getName());
                bundle.setParent(folder);
                bundle.setContentSection(folder.getContentSection());
                bundle.save();

                publication.setOrganization(orga);

                if (publish) {
                    publishItem(orga);
                }

                report.setType(orga.getClass().getName());
            }

            report.setCreated(true);

            //Special handling for pretend mode
            if (pretend && createdOrgas.contains(name)) {
                report.setCreated(false);
            } else {
                createdOrgas.add(name);
            }
        } else {
            collection.next();
            final GenericOrganizationalUnit orga = new GenericOrganizationalUnit(collection.getDataObject());
            if (!pretend) {
                publication.setOrganization(orga);
            }
            report.setType(orga.getClass().getName());
            report.setCreated(false);
        }

        collection.close();
        return report;
    }

    public OrganizationalUnitImportReport processOrganization(final InternetArticle publication,
                                                              final String name,
                                                              final boolean pretend) {
        final OrganizationalUnitImportReport report = new OrganizationalUnitImportReport();

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(GenericOrganizationalUnit.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("title", name);
        report.setName(name);
        if (collection.isEmpty()) {
            if (!pretend) {
                final Integer folderId = Publication.getConfig().getDefaultOrganizationsFolder();
                final Folder folder = new Folder(new BigDecimal(folderId));
                if (folder == null) {
                    throw new IllegalArgumentException("Error getting folder for organizations.");
                }

                final GenericOrganizationalUnit orga = new GenericOrganizationalUnit(Publication.getConfig().
                        getOrganizationType());
                orga.setTitle(name);
                orga.setName(normalizeString(name));
                orga.setContentSection(folder.getContentSection());
                orga.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
                orga.save();

                final GenericOrganizationalUnitBundle bundle = new GenericOrganizationalUnitBundle(Publication.
                        getConfig().getOrganizationBundleType());
                bundle.setDefaultLanguage(orga.getLanguage());
                bundle.setContentType(orga.getContentType());
                bundle.addInstance(orga);
                bundle.setName(orga.getName());
                bundle.setParent(folder);
                bundle.setContentSection(folder.getContentSection());
                bundle.save();

                publication.setOrganization(orga);

                if (publish) {
                    publishItem(orga);
                }

                report.setType(orga.getClass().getName());
            }

            report.setCreated(true);

            //Special handling for pretend mode
            if (pretend && createdOrgas.contains(name)) {
                report.setCreated(false);
            } else {
                createdOrgas.add(name);
            }
        } else {
            collection.next();
            final GenericOrganizationalUnit orga = new GenericOrganizationalUnit(collection.getDataObject());
            if (!pretend) {
                publication.setOrganization(orga);
            }
            report.setType(orga.getClass().getName());
            report.setCreated(false);
        }

        collection.close();
        return report;
    }

    public SeriesImportReport processSeries(final Publication publication,
                                            final String seriesTitle,
                                            final boolean pretend) {
        final SeriesImportReport report = new SeriesImportReport();

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(Series.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter("title", seriesTitle);

        report.setSeriesTitle(seriesTitle);
        if (collection.isEmpty()) {
            if (!pretend) {
                final Integer folderId = Publication.getConfig().getDefaultSeriesFolder();
                final Folder folder = new Folder(new BigDecimal(folderId));
                if (folder == null) {
                    throw new IllegalArgumentException("Error getting folder for series.");
                }

                final Series series = new Series();
                series.setName(normalizeString(seriesTitle));
                series.setTitle(seriesTitle);
                series.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
                series.setContentSection(folder.getContentSection());
                series.save();

                final SeriesBundle bundle = new SeriesBundle(series);
                bundle.setParent(folder);
                bundle.setContentSection(folder.getContentSection());
                bundle.save();

                publication.addSeries(series);

                if (publish) {
                    publishItem(series);
                }
            }
            report.setCreated(true);

            //Special handling for pretend mode
            if (pretend && createdSeries.contains(seriesTitle)) {
                report.setCreated(false);
            } else {
                createdSeries.add(seriesTitle);
            }
        } else {
            if (!pretend) {
                collection.next();
                final Series series = new Series(collection.getDataObject());
                publication.addSeries(series);
            }
            report.setCreated(false);
        }

        collection.close();

        return report;
    }

    public void publishItem(final ContentItem item) {
        final Calendar now = new GregorianCalendar();
        final LifecycleDefinitionCollection lifecycles = item.getContentSection().getLifecycleDefinitions();
        lifecycles.next();
        final LifecycleDefinition lifecycleDef = lifecycles.getLifecycleDefinition();
        final ContentItem pending = item.publish(lifecycleDef, now.getTime());
        lifecycles.close();
        item.promotePendingVersion(pending);
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
