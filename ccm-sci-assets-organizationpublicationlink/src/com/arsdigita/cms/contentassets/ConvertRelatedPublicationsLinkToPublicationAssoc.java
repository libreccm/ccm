package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitWithPublications;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.dispatcher.ContentItemDispatcher;
import com.arsdigita.cms.installer.xml.ContentItemHelper;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import java.math.BigDecimal;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Jens Pelzetter 
 */
public class ConvertRelatedPublicationsLinkToPublicationAssoc {

    public static void main(String[] args) {
        final com.arsdigita.runtime.Runtime runtime =
                                            new com.arsdigita.runtime.Runtime();

        runtime.startup();

        final Session session = SessionManager.getSession();
        final TransactionContext tc = session.getTransactionContext();

        tc.beginTxn();
        try {
            DataCollection data = session.retrieve(
                    SciDepartment.BASE_DATA_OBJECT_TYPE);
            data.addOrder("title");

            SciDepartment department;
            while (data.next()) {
                department =
                (SciDepartment) DomainObjectFactory.newInstance(data.
                        getDataObject());

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
                        Publication publication = (Publication) target;
                        System.out.printf(
                                "\tconverting related link to '%s' (%d of %d)\n",
                                publication.getTitle(),
                                i,
                                relatedLinks.size());

                        GenericOrganizationalUnitWithPublications orga =
                                                                  new GenericOrganizationalUnitWithPublications(
                                department.getID());
                        orga.addPublication(publication);

                        relatedLink.delete();

                        i++;
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


            tc.commitTxn();
        } catch (Exception ex) {
            System.out.println("Exception ocurred during convert process:");
            ex.printStackTrace(System.err);
        } finally {
            if (tc.inTxn()) {
                tc.abortTxn();
            }
        }
    }
}
