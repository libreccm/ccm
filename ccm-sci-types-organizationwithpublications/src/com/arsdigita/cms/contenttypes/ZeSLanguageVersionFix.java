package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.ContentTypeWorkflowTemplate;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter 
 */
public class ZeSLanguageVersionFix {

    public static void main(final String[] args) {

        final com.arsdigita.runtime.Runtime runtime =
                                            new com.arsdigita.runtime.Runtime();
        runtime.startup();

        final Session session = SessionManager.getSession();
        final TransactionContext tctx = session.getTransactionContext();

        tctx.beginTxn();
        try {
            System.out.println("Starting to fix associations...");

            ContentSectionCollection contentSections = ContentSection.
                    getAllSections();
            ContentSection sectionContent = null;
            ContentSection sectionResearch = null;
            while (contentSections.next()) {
                ContentSection section = contentSections.getContentSection();
                if ("content".equals(section.getName())) {
                    sectionContent = section;
                } else if ("research".equals(section.getName())) {
                    sectionResearch = section;
                }
            }

            fixZeS(session, sectionContent, sectionResearch);
            fixTheorieUndVerfassung(session, sectionContent, sectionResearch);
            fixInstitutionenUndGeschichte(session,
                                          sectionContent,
                                          sectionResearch);
            fixWirtschaftswissenschaftliche(session,
                                            sectionContent,
                                            sectionResearch);
            fixGesundheit(session, sectionContent, sectionResearch);
            fixGesundheitArbeitsbereich1(session,
                                         sectionContent,
                                         sectionResearch);
            fixGesundheitArbeitsbereich2(session,
                                         sectionContent,
                                         sectionResearch);
            fixGesundheitArbeitsbereich3(session,
                                         sectionContent,
                                         sectionResearch);
            fixGesundheitArbeitsbereich4(session,
                                         sectionContent,
                                         sectionResearch);
            fixGeschlechterpolitik(session, sectionContent, sectionResearch);

            fixFileStorageItems(session, sectionContent, sectionResearch);

            System.out.println("Finished, commiting transaction...");
            tctx.commitTxn();
        } catch (Exception ex) {
            System.err.println(
                    "Exeception ocurred during convert process. "
                    + "Transaction rolled back, all changes will be lost.");
            ex.printStackTrace(System.err);
        } finally {
            if (tctx.inTxn()) {
                tctx.abortTxn();
            }
        }
    }

    public static void fixZeS(final Session session,
                              final ContentSection sectionContent,
                              final ContentSection sectionResearch) {
        System.out.println(
                "First, the publications of the english variant of the ZeS...");
        Folder folder = sectionContent.getRootFolder();
        folder = (Folder) folder.getItem("das-zentrum", true);
        ContentItem item = folder.getItem("zes", false);

        SciOrganizationWithPublications zesDe;
        SciOrganizationWithPublications zesEn;
        if (item instanceof ContentBundle) {
            zesDe =
            (SciOrganizationWithPublications) ((ContentBundle) item).
                    getPrimaryInstance();
        } else {
            zesDe = (SciOrganizationWithPublications) ((ContentPage) item).
                    getContentBundle().getPrimaryInstance();
        }
        zesEn = (SciOrganizationWithPublications) zesDe.getContentBundle().
                getInstance("en");

        System.out.printf("Got the ZeS: %s (%s)\n", zesDe.getName(), zesDe.
                getLanguage());

        SciOrganizationPublicationsCollection publicationsZeSde = zesDe.
                getPublications();
        SciOrganizationPublicationsCollection publicationsZeSen = zesEn.
                getPublications();

        Publication publicationDe;
        Publication publicationEn;

        //Delete all publication links from the english variant to german variants of publications
        System.out.println(
                "Deleting all assciations of the english ZeS item with german variants of publication items...");
        int i = 1;
        long size = publicationsZeSen.size();
        while (publicationsZeSen.next()) {
            publicationEn = publicationsZeSen.getPublication();
            System.out.printf("\tProcessing item %d of %d ('%s')...\n",
                              i,
                              size,
                              publicationEn.getName());
            if (!("de").equals(publicationEn.getLanguage())) {
                zesEn.removePublication(publicationEn);
                LifecycleDefinition lifecycleDef =
                                    ContentTypeLifecycleDefinition.
                        getLifecycleDefinition(
                        publicationEn.getContentSection(),
                        publicationEn.getContentType());

                System.out.println("\t\t\tPublishing new item...");
                publicationEn.publish(lifecycleDef, new Date());
                i++;
            }
        }

        publicationEn = null;

        publicationsZeSen = zesEn.getPublications();

        i = 1;
        size = publicationsZeSde.size();
        System.out.println(
                "Processing publications and linking them to the english variant of the ZeS item...");
        while (publicationsZeSde.next()) {
            publicationDe = publicationsZeSde.getPublication();
            publicationEn = (Publication) publicationDe.getContentBundle().
                    getInstance("en");
            System.out.printf("\tProceesing item %d of %d ('%s')...",
                              i,
                              size,
                              publicationDe.getName());

            System.out.println(
                    "\tCreating english versions of the publication items of associated with the ZeS item (if necessary)...");
            if (publicationEn == null) {
                System.out.println(
                        "\t\tNo english version found, creating one...");
                publicationEn =
                (Publication) createEnglishVersion(publicationDe,
                                                   sectionResearch);
            } else {
                System.out.println(
                        "\t\tEnglish version exists, no action necessary here.");
            }

            System.out.println(
                    "\tChecking if english variant of the publication item is already associated with the english version of the ZeS item. ");
            publicationsZeSen.addFilter(String.format("id = %s", publicationEn.
                    getID()));
            if (publicationsZeSen.isEmpty()) {
                System.out.println(
                        "\t\tAdding english version of publication item to english version of the ZeS item...");
                zesEn.addPublication(publicationEn);
                LifecycleDefinition lifecycleDef =
                                    ContentTypeLifecycleDefinition.
                        getLifecycleDefinition(
                        publicationEn.getContentSection(),
                        publicationEn.getContentType());
                System.out.println("\t\t\t(Re)publishing publication item...");
                if (publicationEn.isPublished()) {
                    publicationEn.republish();
                } else {
                    publicationEn.publish(lifecycleDef, new Date());
                }
            } else {
                System.out.println(
                        "\t\tAlready associated, no actions necessary.");
            }
            publicationsZeSen.reset();
            i++;
        }

        System.out.println("\t\t\t(Re)publishing the english ZeS item...");
        if (zesEn.isPublished()) {
            zesEn.republish();
        } else {
            LifecycleDefinition lifcycleDef =
                                ContentTypeLifecycleDefinition.
                    getLifecycleDefinition(zesEn.getContentSection(),
                                           zesEn.getContentType());
            zesEn.publish(lifcycleDef, new Date());
        }
    }

    public static void fixTheorieUndVerfassung(final Session session,
                                               final ContentSection sectionContent,
                                               final ContentSection researchContent) {
        Folder folder;
        ContentItem item;

        System.out.println(
                "Abteilung Theorie und Verfassung des Wohlfahrtsstaates...");
        folder = sectionContent.getRootFolder();
        folder = (Folder) folder.getItem("theorie-und-verfassung", true);
        item = folder.getItem("theorie-und-verfassung-des-wohlfahrtsstaates",
                              false);

        final SciDepartmentWithPublications theorieDe;
        if (item instanceof ContentBundle) {
            theorieDe =
            (SciDepartmentWithPublications) ((ContentBundle) item).
                    getPrimaryInstance();
        } else {
            theorieDe =
            (SciDepartmentWithPublications) ((ContentPage) item).
                    getContentBundle().getPrimaryInstance();
        }

        System.out.printf("Got '%s' (%s)\n", theorieDe.getName(), theorieDe.
                getLanguage());
    }

    public static void fixInstitutionenUndGeschichte(final Session session,
                                                     final ContentSection sectionContent,
                                                     final ContentSection sectionResearch) {
        Folder folder;
        ContentItem item;

        System.out.println(
                "Abteilung Institutionen und Geschichte des Wohlfahrtsstaates...");
        folder = sectionContent.getRootFolder();
        folder = (Folder) folder.getItem("institutionen-und-geschichte",
                                         true);
        item = folder.getItem(
                "institutionen-und-geschichte-des-wohlfahrtsstaates", false);

        final SciDepartmentWithPublications institutionenDe;
        if (item instanceof ContentBundle) {
            institutionenDe =
            (SciDepartmentWithPublications) ((ContentBundle) item).
                    getPrimaryInstance();
        } else {
            institutionenDe =
            (SciDepartmentWithPublications) ((ContentPage) item).
                    getContentBundle().getPrimaryInstance();
        }

        System.out.printf("Got '%s' (%s)\n",
                          institutionenDe.getName(),
                          institutionenDe.getLanguage());
    }

    public static void fixWirtschaftswissenschaftliche(final Session session,
                                                       final ContentSection sectionContent,
                                                       final ContentSection sectionResearch) {
        Folder folder;
        ContentItem item;

        System.out.println(
                "Abteilung Wirtschaftswissenschaftliche Abteilung...");
        folder = sectionContent.getRootFolder();
        folder = (Folder) folder.getItem(
                "wirtschaftswissenschaftliche-abteilung",
                true);
        item =
        folder.getItem("wirtschaftswissenschaftliche-abteilung", false);
        final SciDepartmentWithPublications wirtschaftDe;
        if (item instanceof ContentBundle) {
            wirtschaftDe =
            (SciDepartmentWithPublications) ((ContentBundle) item).
                    getPrimaryInstance();
        } else {
            wirtschaftDe =
            (SciDepartmentWithPublications) ((ContentPage) item).
                    getContentBundle().getPrimaryInstance();
        }

        System.out.printf("Got '%s' (%s)\n", wirtschaftDe.getName(),
                          wirtschaftDe.getLanguage());
    }

    public static void fixGesundheit(final Session session,
                                     final ContentSection sectionContent,
                                     final ContentSection sectionResearch) {
        Folder folder;
        ContentItem item;

        System.out.println("Abteilung Gesundheit...");
        folder = sectionContent.getRootFolder();
        folder = (Folder) folder.getItem(
                "gesundheit",
                true);
        item =
        folder.getItem(
                "gesundheitsoekonomie-gesundheitspolitik-und-versorgungsforschung",
                false);
        final SciDepartmentWithPublications gesundheitDe;
        if (item instanceof ContentBundle) {
            gesundheitDe =
            (SciDepartmentWithPublications) ((ContentBundle) item).
                    getPrimaryInstance();
        } else {
            gesundheitDe =
            (SciDepartmentWithPublications) ((ContentPage) item).
                    getContentBundle().getPrimaryInstance();
        }

        System.out.printf("Got '%s' (%s)\n", gesundheitDe.getName(),
                          gesundheitDe.getLanguage());
    }

    public static void fixGesundheitArbeitsbereich1(final Session session,
                                                    final ContentSection sectionContent,
                                                    final ContentSection sectionResearch) {
        Folder folder;
        ContentItem item;

        System.out.println("Gesundheit/Arbeitsbereich 1...");
        folder = sectionContent.getRootFolder();
        folder = (Folder) folder.getItem(
                "gesundheit",
                true);
        item =
        folder.getItem("arbeitsbereich-1", false);
        final SciDepartmentWithPublications arbeitsbereich1De;
        if (item instanceof ContentBundle) {
            arbeitsbereich1De =
            (SciDepartmentWithPublications) ((ContentBundle) item).
                    getPrimaryInstance();
        } else {
            arbeitsbereich1De =
            (SciDepartmentWithPublications) ((ContentPage) item).
                    getContentBundle().getPrimaryInstance();
        }

        System.out.printf("Got '%s' (%s)\n", arbeitsbereich1De.getName(),
                          arbeitsbereich1De.getLanguage());
    }

    public static void fixGesundheitArbeitsbereich2(final Session session,
                                                    final ContentSection sectionContent,
                                                    final ContentSection sectionResearch) {
        Folder folder;
        ContentItem item;

        System.out.println("Arbeitsbereich 2...");
        folder = sectionContent.getRootFolder();
        folder = (Folder) folder.getItem(
                "gesundheit",
                true);
        item =
        folder.getItem("arbeitsbereich-2", false);
        final SciDepartmentWithPublications arbeitsbereich2De;
        if (item instanceof ContentBundle) {
            arbeitsbereich2De =
            (SciDepartmentWithPublications) ((ContentBundle) item).
                    getPrimaryInstance();
        } else {
            arbeitsbereich2De =
            (SciDepartmentWithPublications) ((ContentPage) item).
                    getContentBundle().getPrimaryInstance();
        }

        System.out.printf("Got '%s' (%s)\n",
                          arbeitsbereich2De.getName(),
                          arbeitsbereich2De.getLanguage());

    }

    public static void fixGesundheitArbeitsbereich3(final Session session,
                                                    final ContentSection sectionContent,
                                                    final ContentSection sectionResearch) {
        Folder folder;
        ContentItem item;

        System.out.println("Arbeitsbereich 3...");
        folder = sectionContent.getRootFolder();
        folder = (Folder) folder.getItem(
                "gesundheit",
                true);
        item =
        folder.getItem("arbeitsbereich-3", false);
        final SciDepartmentWithPublications arbeitsbereich3De;
        if (item instanceof ContentBundle) {
            arbeitsbereich3De =
            (SciDepartmentWithPublications) ((ContentBundle) item).
                    getPrimaryInstance();
        } else {
            arbeitsbereich3De =
            (SciDepartmentWithPublications) ((ContentPage) item).
                    getContentBundle().getPrimaryInstance();
        }

        System.out.printf("Got '%s' (%s)\n",
                          arbeitsbereich3De.getName(),
                          arbeitsbereich3De.getLanguage());
    }

    public static void fixGesundheitArbeitsbereich4(final Session session,
                                                    final ContentSection sectionContent,
                                                    final ContentSection sectionResearch) {
        Folder folder;
        ContentItem item;

        System.out.println("Arbeitsbereich 4...");
        folder = sectionContent.getRootFolder();
        folder = (Folder) folder.getItem(
                "gesundheit",
                true);
        item =
        folder.getItem("arbeitsbereich-4", false);
        final SciDepartmentWithPublications arbeitsbereich4De;
        if (item instanceof ContentBundle) {
            arbeitsbereich4De =
            (SciDepartmentWithPublications) ((ContentBundle) item).
                    getPrimaryInstance();
        } else {
            arbeitsbereich4De =
            (SciDepartmentWithPublications) ((ContentPage) item).
                    getContentBundle().getPrimaryInstance();
        }

        System.out.printf("Got '%s' (%s)\n",
                          arbeitsbereich4De.getName(),
                          arbeitsbereich4De.getLanguage());
    }

    public static void fixGeschlechterpolitik(final Session session,
                                              final ContentSection sectionContent,
                                              final ContentSection sectionResearch) {
        Folder folder;
        ContentItem item;

        System.out.println("Abteilung Geschlechterpolitik...");
        folder = sectionContent.getRootFolder();
        folder = (Folder) folder.getItem("geschlechterpolitik",
                                         true);
        item =
        folder.getItem("geschlechterpolitik-im-wohlfahrtsstaat", false);
        final SciDepartmentWithPublications geschlechterDe;
        if (item instanceof ContentBundle) {
            geschlechterDe =
            (SciDepartmentWithPublications) ((ContentBundle) item).
                    getPrimaryInstance();
        } else {
            geschlechterDe =
            (SciDepartmentWithPublications) ((ContentPage) item).
                    getContentBundle().getPrimaryInstance();
        }

        System.out.printf("Got '%s' (%s)\n",
                          geschlechterDe.getName(),
                          geschlechterDe.getLanguage());
    }

    public static void fixFileStorageItems(final Session session,
                                           final ContentSection sectionContent,
                                           final ContentSection sectionResearch) {
        Folder folder;
        ContentItem item;

        System.out.println("FileStorageItems for WorkingPapers...");
    }

    public static ContentPage createEnglishVersion(final ContentPage item,
                                                   final ContentSection section) {
        ContentPage itemPrimary;
        ContentPage itemEn;

        itemPrimary = (Publication) item.getContentBundle().
                getPrimaryInstance();

        itemEn = (Publication) itemPrimary.copy("en");
        itemEn.setLanguage("en");
        itemEn.setName(item.getContentBundle().getName());

        ContentType type = item.getContentType();;
        WorkflowTemplate template = ContentTypeWorkflowTemplate.
                getWorkflowTemplate(section, type);
        if (template != null) {
            Workflow workflow = template.instantiateNewWorkflow();
            workflow.setObjectID(itemEn.getID());
            workflow.save();
        }

        return itemEn;
    }
}
