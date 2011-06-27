package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.Folder.ItemCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;

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

            System.out.println(
                    "First, the publications of the english variant of the ZeS...");
            Folder folder = sectionContent.getRootFolder();
            folder = (Folder) folder.getItem("das-zentrum", true);
            ContentItem item = folder.getItem("zes", false);

            SciOrganizationWithPublications zesDe;
            if (item instanceof ContentBundle) {
                zesDe =
                (SciOrganizationWithPublications) ((ContentBundle) item).
                        getPrimaryInstance();
            } else {
                zesDe = (SciOrganizationWithPublications) ((ContentPage) item).
                        getContentBundle().getPrimaryInstance();
            }

            System.out.printf("Got the ZeS: %s (%s)\n", zesDe.getName(), zesDe.
                    getLanguage());

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

            System.out.printf("Got '%s' (%s)\n", theorieDe.getName(), theorieDe.
                    getLanguage());

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

            System.out.printf("Got '%s' (%s)\n", theorieDe.getName(), theorieDe.
                    getLanguage());

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

            System.out.printf("Got '%s' (%s)\n", theorieDe.getName(), theorieDe.
                    getLanguage());

            System.out.println("Arbeitsbereich 1...");
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

            System.out.printf("Got '%s' (%s)\n", theorieDe.getName(), theorieDe.
                    getLanguage());

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

            System.out.printf("Got '%s' (%s)\n", theorieDe.getName(), theorieDe.
                    getLanguage());

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

            System.out.printf("Got '%s' (%s)\n", theorieDe.getName(), theorieDe.
                    getLanguage());

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

            System.out.printf("Got '%s' (%s)\n", theorieDe.getName(), theorieDe.
                    getLanguage());
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

            System.out.printf("Got '%s' (%s)\n", theorieDe.getName(), theorieDe.
                    getLanguage());


            System.out.println("FileStorageItems for WorkingPapers...");


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
}
