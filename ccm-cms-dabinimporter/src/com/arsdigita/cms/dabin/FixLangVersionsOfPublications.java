package com.arsdigita.cms.dabin;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.AuthorshipCollection;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.Journal;
import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.Publisher;
import com.arsdigita.cms.contenttypes.Review;
import com.arsdigita.cms.contenttypes.WorkingPaper;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.packaging.Program;
import com.arsdigita.persistence.OID;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author jensp
 */
public class FixLangVersionsOfPublications extends Program {

    public FixLangVersionsOfPublications() {
        this(true);
    }

    public FixLangVersionsOfPublications(boolean startup) {
        super("FixLangVersionsOfPublications",
              "0.1.0",
              "configFile",
              startup);
    }

    @Override
    protected void doRun(CommandLine cmdLine) {

        final String[] args = cmdLine.getArgs();

        if (args.length != 1) {
            System.out.println(
                    "\n\nUsage: FixLangVersionsOfPublication folderId");
            help(System.err);
            System.exit(-1);
        }

        Transaction transaction = new Transaction() {

            @Override
            protected void doRun() {

                int folderId = Integer.parseInt(args[0]);
                Folder folder = new Folder(new OID(Folder.BASE_DATA_OBJECT_TYPE,
                                                   folderId));

                System.out.println("Fixing publications in folder: " + folder.
                        getDisplayName());

                ItemCollection items = folder.getItems();

                while (items.next()) {
                    ContentItem item = items.getContentItem();

                    ContentBundle bundle = null;
                    Publication publicationDe;
                    Publication publicationEn = null;

                    if (item instanceof ContentBundle) {
                        bundle = (ContentBundle) item;
                    } else {
                        if (item instanceof Publication) {
                            Publication publication = (Publication) item;
                            bundle = publication.getContentBundle();
                        } else {
                            System.out.printf(
                                    "Item %s is not a publication. Skiping.\n",
                                    item.getID().toString());
                        }
                    }

                    publicationDe = (Publication) bundle.getPrimaryInstance();
                    System.out.printf(
                            "Procesing publication '%s' (Bundle id: %s; type: %s)...\n",
                            publicationDe.getName(),
                            bundle.getID(),
                            publicationDe.getClass().getName());

                    if (bundle.hasInstance("en")) {
                        System.out.println(
                                "\tPublication has already an english version. Skiping.");
                        continue;
                    }

                    //Copy special properties
                    if ((publicationDe instanceof ArticleInJournal)
                        || (publicationDe instanceof Review)) {
                        System.out.println(
                                "\tPublication is an ArticleInJournal (or a Review)");

                        ArticleInJournal articleDe =
                                         (ArticleInJournal) publicationDe;
                        ArticleInJournal articleEn = new ArticleInJournal();

                        articleEn.setVolume(articleDe.getVolume());
                        articleEn.setIssue(articleDe.getIssue());
                        articleEn.setPagesFrom(articleDe.getPagesFrom());
                        articleEn.setPagesTo(articleDe.getPagesTo());
                        //articleEn.setJournal(articleDe.getJournal());
                        articleEn.setPublicationDate(articleDe.
                                getPublicationDate());
                        articleEn.setReviewed(articleDe.getReviewed());

                        publicationEn = articleEn;
                    } else if (publicationDe instanceof ArticleInCollectedVolume) {
                        System.out.println(
                                "\tpublicationDe is an ArticleInCollectedVolume");

                        ArticleInCollectedVolume articleDe =
                                                 (ArticleInCollectedVolume) publicationDe;
                        ArticleInCollectedVolume articleEn =
                                                 new ArticleInCollectedVolume();

                        articleEn.setPagesFrom(articleDe.getPagesFrom());
                        articleEn.setPagesTo(articleDe.getPagesTo());
                        articleEn.setChapter(articleDe.getChapter());
                        articleEn.setReviewed(articleDe.getReviewed());

                        publicationEn = articleEn;
                    } else if (publicationDe instanceof CollectedVolume) {
                        System.out.println(
                                "\tpublicationDe is an CollectedVolume");

                        //CollectedVolumes imported from DaBIn to not have associated
                        //articles

                        CollectedVolume collectedVolumeDe =
                                        (CollectedVolume) publicationDe;
                        CollectedVolume collectedVolumeEn =
                                        new CollectedVolume();

                        collectedVolumeEn.setReviewed(collectedVolumeDe.
                                getReviewed());

                        publicationEn = collectedVolumeEn;
                    } else if (publicationDe instanceof Expertise) {
                        System.out.println("\tpublicationDe is an Experise");

                        Expertise expertiseDe = (Expertise) publicationDe;
                        Expertise expertiseEn = new Expertise();

                        expertiseEn.setPlace(expertiseDe.getPlace());
                        if (expertiseDe.getOrganization().getContentBundle().
                                hasInstance(
                                "en")) {
                            expertiseEn.setOrganization((GenericOrganizationalUnit) expertiseDe.
                                    getOrganization().
                                    getContentBundle().getInstance("en"));
                        } else {
                            expertiseEn.setOrganization(expertiseDe.
                                    getOrganization());
                        }
                        expertiseEn.setNumberOfPages(expertiseDe.
                                getNumberOfPages());
                        if (expertiseDe.getOrderer().getContentBundle().
                                hasInstance("en")) {
                            expertiseEn.setOrderer((GenericOrganizationalUnit) expertiseDe.
                                    getOrderer().getContentBundle().getInstance(
                                    "en"));
                        } else {
                            expertiseEn.setOrderer(expertiseDe.getOrderer());
                        }

                        publicationEn = expertiseEn;
                    } else if (publicationDe instanceof GreyLiterature) {
                        System.out.println("\tpublicationDe is GreyLiterature");

                        GreyLiterature greyLiteratureDe =
                                       (GreyLiterature) publicationDe;
                        GreyLiterature greyLiteratureEn = new GreyLiterature();

                        greyLiteratureEn.setPlace(greyLiteratureDe.getPlace());
                        greyLiteratureEn.setNumber(greyLiteratureDe.getNumber());
                        greyLiteratureEn.setNumberOfPages(greyLiteratureDe.
                                getNumberOfPages());
                        greyLiteratureEn.setPagesFrom(greyLiteratureDe.
                                getPagesFrom());
                        greyLiteratureEn.setPagesTo(
                                greyLiteratureDe.getPagesTo());

                        publicationEn = greyLiteratureEn;
                    } else if (publicationDe instanceof InProceedings) {
                        System.out.println("\tpublicationDe is an InProceedings");
                        //Not used by the DaBInImporter

                    } else if (publicationDe instanceof InternetArticle) {
                        System.out.println(
                                "\tpublicationDe is an InternetArticle");

                        //Not used by the DabInImporter
                    } else if (publicationDe instanceof Journal) {
                        System.out.println("\tpublicationDe is an Journal");

                        //Not used by the DaBInImporter
                    } else if (publicationDe instanceof Monograph) {
                        System.out.println("\tpublicationDe is an Monograph");

                        Monograph monographDe = (Monograph) publicationDe;
                        Monograph monographEn = new Monograph();

                        monographEn.setReviewed(monographDe.getReviewed());

                        publicationEn = monographEn;
                    } else if (publicationDe instanceof Proceedings) {
                        System.out.println("\tpublicationDe is a Proceedings");

                        //Not used by the DaBInImporter
                    } else if (publicationDe instanceof WorkingPaper) {
                        System.out.println(
                                "\tpublicationDe is UnPublished or a WorkingPaper");

                        WorkingPaper workingPaperDe =
                                     (WorkingPaper) publicationDe;
                        WorkingPaper workingPaperEn = new WorkingPaper();

                        workingPaperEn.setPlace(workingPaperDe.getPlace());
                        workingPaperEn.setNumber(workingPaperDe.getNumber());
                        workingPaperEn.setNumberOfPages(
                                workingPaperDe.getNumberOfPages());
                        workingPaperEn.setReviewed(workingPaperDe.getReviewed());

                        publicationEn = workingPaperEn;
                    } else {
                        System.out.println(
                                "\tUnknown publicationDe type. Skiping.");
                        continue;
                    }

                    System.out.println("\tSetting common properties...");
                    //Copy common properties
                    System.out.println("\tSetting name...");
                    publicationEn.setName(publicationDe.getName());
                    System.out.println("\tSetting title...");
                    publicationEn.setTitle(publicationDe.getTitle());
                    System.out.println("\tSetting year of publication...");
                    publicationEn.setYearOfPublication(publicationDe.
                            getYearOfPublication());
                    System.out.println("\tSetting abstract...");
                    publicationEn.setAbstract(publicationDe.getAbstract());
                    System.out.println("\tSetting misc...");
                    publicationEn.setMisc(publicationDe.getMisc());

                    System.out.println("\tGetting authors...");
                    AuthorshipCollection authors = publicationDe.getAuthors();
                    authors.addOrder(AuthorshipCollection.LINKORDER);
                    System.out.println("\tAdding authors...");
                    while (authors.next()) {
                        GenericPerson author = authors.getAuthor();
                        boolean isEditor = authors.isEditor();

                        if (author.getContentBundle().hasInstance("en")) {
                            publicationEn.addAuthor((GenericPerson) author.
                                    getContentBundle().getInstance("en"),
                                                    isEditor);
                        } else {
                            publicationEn.addAuthor(author, isEditor);
                        }
                    }

                    //Copy properties for publication with publisher                    
                    if (publicationDe instanceof PublicationWithPublisher) {
                        System.out.println(
                                "\tpublicationDe is a publicationWithPublisher");

                        PublicationWithPublisher publicationWithPublisherDe =
                                                 (PublicationWithPublisher) publicationDe;
                        PublicationWithPublisher publicationWithPublisherEn =
                                                 (PublicationWithPublisher) publicationEn;

                        System.out.printf("\tSetting isbn %s...",
                                          publicationWithPublisherDe.getISBN());
                        if (publicationWithPublisherDe.getISBN() != null) {
                            publicationWithPublisherEn.setISBN(publicationWithPublisherDe.
                                    getISBN());
                        }
                        System.out.printf("\tSetting volume...\n");
                        publicationWithPublisherEn.setVolume(publicationWithPublisherDe.
                                getVolume());
                        System.out.println("\tSetting number...");
                        publicationWithPublisherEn.setNumberOfVolumes(publicationWithPublisherDe.
                                getNumberOfVolumes());
                        System.out.println("\tSetting number of pages...");
                        publicationWithPublisherEn.setNumberOfPages(publicationWithPublisherDe.
                                getNumberOfPages());
                        System.out.println("\tSetting edition...");
                        publicationWithPublisherEn.setEdition(publicationWithPublisherDe.
                                getEdition());
                        if (publicationWithPublisherDe.getPublisher() != null) {
                            System.out.println("\tSetting publisher...");
                            if (publicationWithPublisherDe.getPublisher().
                                    getContentBundle().
                                    hasInstance("en")) {
                                publicationWithPublisherEn.setPublisher((Publisher) publicationWithPublisherDe.
                                        getPublisher().getContentBundle().
                                        getInstance("en"));
                            } else {
                                publicationWithPublisherEn.setPublisher(publicationWithPublisherDe.
                                        getPublisher());
                            }
                        }

                    } else {
                        System.out.println("\tpublicationDe is a publication");
                    }

                    publicationEn.save();
                    publicationEn.setLanguage("en");
                    bundle.addInstance(publicationEn);
                    
                    bundle.save();
                    
                    
                    
                    System.out.println("\tEnglish version created.");
                }
            }
        };

        transaction.run();

    }

    public static void main(String[] args) {
        new FixLangVersionsOfPublications().run(args);
    }
}
