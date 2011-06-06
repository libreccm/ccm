package com.arsdigita.cms.contenttypes;

import com.arsdigita.categorization.CategorizedCollection;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 */
public class ConvertRelatedPublicationLinks {

    public static void main(final String args[]) {
        final com.arsdigita.runtime.Runtime runtime =
                                            new com.arsdigita.runtime.Runtime();

        runtime.startup();

        final Session session = SessionManager.getSession();
        final TransactionContext tctx = session.getTransactionContext();

        tctx.beginTxn();
        try {
            DataCollection data = session.retrieve(
                    SciDepartment.BASE_DATA_OBJECT_TYPE);
            data.addOrder("title");


            SciOrganizationWithPublications organization = null;
            SciDepartmentWithPublications department;
            while (data.next()) {
                department =
                (SciDepartmentWithPublications) DomainObjectFactory.newInstance(
                        data.getDataObject());

                if (department.isDraftVersion()) {
                    System.out.printf("Found department '%s'\n",
                                      department.getTitle());

                    DataCollection relatedLinks = RelatedLink.getRelatedLinks(
                            department, "SciDepartmentPublications");

                    System.out.printf(
                            "Found %d related links from department '%s' with link list name '%s':\n",
                            relatedLinks.size(),
                            department.getTitle(),
                            "SciDepartmentPublications");

                    int i = 0;
                    long num = relatedLinks.size();

                    while (relatedLinks.next()) {
                        RelatedLink relatedLink =
                                    new RelatedLink(relatedLinks.getDataObject());

                        if (relatedLink == null) {
                            System.out.println(
                                    "Failed to convert related link to domain object...\n");
                            continue;
                        }

                        ContentItem target = relatedLink.getTargetItem();
                        Publication publication = (Publication) target;
                        System.out.printf(
                                "\tconverting related link to '%s' (%d of %d)\n",
                                publication.getTitle(),
                                i,
                                num);

                        department.addPublication(publication);

                        relatedLink.delete();

                        i++;

                        organization =
                        (SciOrganizationWithPublications) department.
                                getOrganization().getDraftVersion();
                    }

                }
            }

            if (organization == null) {
                data.rewind();
                while (data.next()) {
                    department =
                    (SciDepartmentWithPublications) DomainObjectFactory.
                            newInstance(data.getDataObject());

                    if (department.isDraftVersion()) {
                        if (department.getOrganization() != null) {
                            organization =
                            (SciOrganizationWithPublications) department.
                                    getOrganization().getDraftVersion();
                        }
                    }
                }
            }

            //Add publications without department to organization.
            if (args.length > 0) {
                System.out.println(
                        "Found arguments. Interpreting as categories.");

                for (int i = 0; i < args.length; i++) {
                    Category category = new Category(new BigDecimal(args[i]));

                    if (category == null) {
                        throw new IllegalArgumentException(String.format(
                                "No category with id '%s'", args[i]));
                    }

                    if (organization == null) {
                        throw new IllegalArgumentException("No organization.");
                    }

                    System.out.printf(
                            "Adding all publications in category '%s' to publications of organization '%s'...\n",
                            category.getName(),
                            organization.getName());

                    CategorizedCollection objects =
                                          category.getObjects(
                            ContentItem.BASE_DATA_OBJECT_TYPE);

                    while (objects.next()) {
                        DomainObject obj =
                                     DomainObjectFactory.newInstance(objects.
                                getDomainObject().getOID());
                        if (obj instanceof ContentBundle) {
                            obj = ((ContentBundle) obj).getPrimaryInstance();
                        }

                        if (obj instanceof Publication) {
                            Publication publication = (Publication) obj;
                            publication = (Publication) publication.
                                    getDraftVersion();

                            if ((publication.get("organizations") == null)
                                || ((DataCollection) publication.get(
                                    "organizations")).size() == 0) {
                                System.out.printf(
                                        "Publication '%s' has no associated organization. Adding...\n",
                                        publication.getTitle());
                                organization.addPublication(publication);
                            } else {
                                System.out.printf(
                                        "Publication '%s' has already an associated organization. No action neccessary.\n",
                                        publication.getTitle());
                            }

                        } else {
                            System.out.printf(
                                    "Object is not a publication. Skiping.\n");
                            continue;
                        }

                    }
                }
            }

            DataCollection persons = session.retrieve(
                    GenericPerson.BASE_DATA_OBJECT_TYPE);
            persons.addOrder("surname");
            persons.addOrder("givenname");

            GenericPerson person;
            while (persons.next()) {
                person =
                (GenericPerson) DomainObjectFactory.newInstance(persons.
                        getDataObject());

                if (person.isDraftVersion()) {
                    System.out.printf("Found person '%s'\n",
                                      person.getGivenName(),
                                      person.getSurname());

                    DataCollection relatedLinks = RelatedLink.getRelatedLinks(
                            person, "MyPublications");

                    System.out.printf(
                            "Found %d related links from person '%s' '%s' with link list name '%s'\n",
                            relatedLinks.size(),
                            person.getGivenName(),
                            person.getSurname(),
                            "MyPublications");
                    int i = 1;
                    while (relatedLinks.next()) {
                        RelatedLink relatedLink =
                                    (RelatedLink) DomainObjectFactory.
                                newInstance(relatedLinks.getDataObject());

                        if (relatedLink == null) {
                            System.out.println(
                                    "Failed to convert related link to domain object...");
                            continue;
                        }

                        ContentItem target = relatedLink.getTargetItem();
                        if (target == null) {
                            System.out.println(
                                    "\t Strange: Target item is null. But thats no problem, since we are only deleting this related link.");

                        } else {
                            Publication publication = (Publication) target;
                            System.out.printf(
                                    "\tDeleting obsolete related link to '%s' (%d of %d)\n",
                                    publication.getTitle(),
                                    i,
                                    relatedLinks.size());
                        }
                        relatedLink.delete();
                        i++;
                    }
                }
            }

            System.out.println("Finished, no errors. Commiting transaction.");
            tctx.commitTxn();
            System.out.println(
                    "Now republish all SciOrganization, SciDepartment, "
                    + "GenericPerson and Publication items, for example using "
                    + "BulkPublish from the london-util module, with the "
                    + "'-r' switch.");
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
