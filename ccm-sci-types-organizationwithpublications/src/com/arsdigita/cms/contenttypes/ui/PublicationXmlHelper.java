package com.arsdigita.cms.contenttypes.ui;

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
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.contenttypes.WorkingPaper;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 */
public class PublicationXmlHelper {

    private Element parent;
    private Publication publication;

    public PublicationXmlHelper(final Element parent,
                                final Publication publication) {
        this.parent = parent;
        this.publication = publication;
    }

    public void generateXml() {
        Element publicationElem = parent.newChildElement(
                "publications");

        generateSystemXml(publicationElem);
        generatePublicationXml(publicationElem);

        if (publication instanceof PublicationWithPublisher) {
            generatePublicationWithPublisherXml(publicationElem);
        }

        if (publication instanceof ArticleInCollectedVolume) {
            generateArticleInCollectedVolumeXml(publicationElem);
        }

        if (publication instanceof ArticleInJournal) {
            generateArticleInJournalXml(publicationElem);
        }

        if (publication instanceof CollectedVolume) {
            generateCollectedVolumeXml(publicationElem);
        }

        if (publication instanceof Expertise) {
            generateExpertiseXml(publicationElem);
        }

        if (publication instanceof GreyLiterature) {
            generateGreyLiteratureXml(publicationElem);
        }

        if (publication instanceof InProceedings) {
            generateInProceedingsXml(publicationElem);
        }

        if (publication instanceof InternetArticle) {
            generateInternetArticleXml(publicationElem);
        }

        if (publication instanceof Journal) {
            generateJournalXml(publicationElem);
        }

        if (publication instanceof Monograph) {
            generateMonographXml(publicationElem);
        }

        if (publication instanceof Proceedings) {
            generateProceedingsXml(publicationElem);
        }

        if (publication instanceof Review) {
            generateReviewXml(publicationElem);
        }

        if (publication instanceof UnPublished) {
            generateUnPublishedXml(publicationElem);
        }

        if (publication instanceof WorkingPaper) {
            generateWorkingPaperXml(publicationElem);
        }
    }

    private void generateXmlElement(final Element parent,
                                    final String name,
                                    final String value) {
        if ((value == null) || value.isEmpty()) {
            return;
        } else {
            Element element = parent.newChildElement(name);
            element.setText(value);
        }
    }

    private void generateXmlElement(final Element parent,
                                    final String name,
                                    final Integer value) {
        if ((value == null)) {
            return;
        } else {
            generateXmlElement(parent, name, value.toString());
        }
    }

    private void generateXmlElement(final Element parent,
                                    final String name,
                                    final Boolean value) {
        if (value == null) {
            return;
        } else if (value.booleanValue()) {
            generateXmlElement(parent, name, "true");
        } else {
            generateXmlElement(parent, name, "false");
        }
    }

    private void generateSystemXml(final Element publicationElem) {
        Element objectTypeElem = publicationElem.newChildElement("objectType");
        objectTypeElem.setText(publication.getObjectType().getQualifiedName());
    }

    private void generatePublicationXml(final Element publicationElem) {
        publicationElem.addAttribute("oid", publication.getOID().toString());
        publicationElem.addAttribute("version", publication.getVersion());
        generateXmlElement(publicationElem, "title", publication.getTitle());
        if (publication.getYearOfPublication() != null) {
            Element yearElem = publicationElem.newChildElement(
                    "yearOfPublication");
            yearElem.setText(publication.getYearOfPublication().toString());
        }      
        generateXmlElement(publicationElem, "misc", publication.getMisc());
        generateXmlElement(parent, "abstract", publication.getAbstract());
        generateAuthorsXml(publicationElem);
    }

    private void generateAuthorsXml(final Element publicationElem) {
        AuthorshipCollection authors = publication.getAuthors();

        if ((authors == null) || authors.isEmpty()) {
            return;
        }

        while (authors.next()) {
            Element authorsElem = publicationElem.newChildElement(
                    "authors");
            Element linkElem = authorsElem.newChildElement("link");
            Element editorElem = linkElem.newChildElement("editor");
            if (authors.isEditor()) {
                editorElem.setText("true");
            } else {
                editorElem.setText("false");
            }
            Element authorOrderElem = linkElem.newChildElement(
                    "authorOrder");
            authorOrderElem.setText(authors.getAuthorshipOrder().
                    toString());

            GenericPerson author = authors.getAuthor();
            Element surnameElem = authorsElem.newChildElement("surname");
            surnameElem.setText(author.getSurname());
            Element givennameElem = authorsElem.newChildElement(
                    "givenname");
            givennameElem.setText(author.getGivenName());
        }
    }

    private void generatePublicationWithPublisherXml(
            final Element publicationElem) {
        PublicationWithPublisher pwp =
                                 (PublicationWithPublisher) publication;
        generatePublisherXml(publicationElem, pwp);

        generateXmlElement(publicationElem, "misc", pwp.getISBN());
        generateXmlElement(publicationElem, "volume", pwp.getVolume());
        generateXmlElement(publicationElem,
                           "numberOfVolumes",
                           pwp.getNumberOfVolumes());
        generateXmlElement(publicationElem,
                           "numberOfPages",
                           pwp.getNumberOfPages());
        generateXmlElement(publicationElem, "edition", pwp.getEdition());
    }

    private void generatePublisherXml(final Element publicationElem,
                                      final PublicationWithPublisher pwp) {
        final Publisher publisher = pwp.getPublisher();

        if (publisher == null) {
            return;
        }

        Element publisherElem = publicationElem.newChildElement(
                "publisher");
        Element publisherNameElem = publisherElem.newChildElement(
                "publisherName");
        publisherNameElem.setText(publisher.getPublisherName());
        Element publisherPlaceElem = publisherElem.newChildElement(
                "place");
        publisherPlaceElem.setText(publisher.getPlace());
    }

    private void generateArticleInCollectedVolumeXml(
            final Element publicationElem) {
        final ArticleInCollectedVolume article =
                                       (ArticleInCollectedVolume) publication;

        generateXmlElement(publicationElem, "pageFrom", article.getPagesFrom());
        generateXmlElement(publicationElem, "pageTo", article.getPagesTo());
        generateXmlElement(publicationElem, "chapter", article.getChapter());
        generateXmlElement(publicationElem, "reviewed", article.getReviewed());
        generateCollectedVolumeLinkXml(publicationElem, article);
    }

    private void generateCollectedVolumeLinkXml(
            final Element publicationElem,
            final ArticleInCollectedVolume article) {
        CollectedVolume collectedVolume = article.getCollectedVolume();

        if (collectedVolume != null) {
            Element collectedVolumeElem = publicationElem.newChildElement(
                    "collectedVolume");
            PublicationXmlHelper xmlHelper =
                                 new PublicationXmlHelper(collectedVolumeElem,
                                                          collectedVolume);
            xmlHelper.generateXml();
        }
    }

    private void generateArticleInJournalXml(final Element publicationElem) {
        final ArticleInJournal article = (ArticleInJournal) publication;

        generateXmlElement(publicationElem, "volume", article.getVolume());
        generateXmlElement(publicationElem, "issue", article.getIssue());
        generateXmlElement(publicationElem, "pageFrom", article.getPagesFrom());
        generateXmlElement(publicationElem, "pageTo", article.getPagesTo());
        generateXmlElement(publicationElem, "reviewed", article.getReviewed());

        generateJournalLinkXml(publicationElem, article);
    }

    private void generateJournalLinkXml(final Element publicationElem,
                                        ArticleInJournal article) {
        Journal journal = article.getJournal();

        if (journal != null) {
            Element journalElem = publicationElem.newChildElement("journal");
            PublicationXmlHelper xmlHelper = new PublicationXmlHelper(
                    journalElem,
                    journal);
            xmlHelper.generateXml();
        }
    }

    private void generateCollectedVolumeXml(final Element publicationElem) {
        CollectedVolume collectedVolume = (CollectedVolume) publication;

        generateXmlElement(publicationElem,
                           "reviewed",
                           collectedVolume.getReviewed());
    }

    private void generateExpertiseXml(final Element publicationElem) {
        Expertise expertise = (Expertise) publication;

        generateXmlElement(publicationElem, "place", expertise.getPlace());
        generateXmlElement(publicationElem,
                           "numberOfPages",
                           expertise.getNumberOfPages());

        generateOrganizationXml(publicationElem,
                                "organization",
                                expertise.getOrganization());

        generateOrganizationXml(publicationElem,
                                "orderer",
                                expertise.getOrderer());
    }

    private void generateGreyLiteratureXml(final Element publicationElem) {
        GreyLiterature grey = (GreyLiterature) publication;

        generateXmlElement(publicationElem, "pageFrom", grey.getPagesFrom());
        generateXmlElement(publicationElem, "pageTo", grey.getPagesTo());
    }

    private void generateInProceedingsXml(final Element publicationElem) {
        InProceedings inProceedings = (InProceedings) publication;

        generateXmlElement(publicationElem,
                           "pageFrom",
                           inProceedings.getPagesFrom());
        generateXmlElement(publicationElem,
                           "pagesTo",
                           inProceedings.getPagesTo());
        Element proceedingsElem = publicationElem.newChildElement("proceedings");
        PublicationXmlHelper xmlHelper = new PublicationXmlHelper(
                proceedingsElem,
                inProceedings.getProceedings());
        xmlHelper.generateXml();
    }

    private void generateInternetArticleXml(final Element publicationElem) {
        InternetArticle article = (InternetArticle) publication;

        generateXmlElement(publicationElem, "place", article.getPlace());
        generateXmlElement(publicationElem, "number", article.getNumber());
        generateXmlElement(publicationElem,
                           "numberOfPages",
                           article.getNumberOfPages());
        generateXmlElement(publicationElem, "edition", article.getEdition());
        generateXmlElement(publicationElem, "issn", article.getISSN());

        generateOrganizationXml(publicationElem,
                                "organization",
                                article.getOrganization());

    }

    private void generateJournalXml(final Element publicationElem) {
        final Journal journal = (Journal) publication;

        generateXmlElement(publicationElem, "lastYear", journal.getLastYear());
        generateXmlElement(publicationElem, "issn", journal.getISSN());
    }

    private void generateMonographXml(final Element publicationElem) {
        final Monograph monograph = (Monograph) publication;

        generateXmlElement(publicationElem, "reviewed", monograph.getReviewed());
    }

    private void generateProceedingsXml(final Element publicationElem) {
        Proceedings proceedings = (Proceedings) publication;

        generateXmlElement(publicationElem,
                           "nameOfConference",
                           proceedings.getNameOfConference());
        generateXmlElement(publicationElem,
                           "placeOfConference",
                           proceedings.getPlaceOfConference());

        generateOrganizationXml(publicationElem,
                                "organizer",
                                proceedings.getOrganizerOfConference());
    }

    private void generateReviewXml(final Element publicationElem) {
        //Nothing for now.
    }

    private void generateUnPublishedXml(final Element publicationElem) {
        UnPublished unPublished = (UnPublished) publication;

        generateXmlElement(publicationElem, "place", unPublished.getPlace());
        generateXmlElement(publicationElem, "number", unPublished.getNumber());
        generateXmlElement(publicationElem,
                           "numberOfPages",
                           unPublished.getNumberOfPages());

        generateOrganizationXml(publicationElem,
                                "organization",
                                unPublished.getOrganization());
    }

    private void generateWorkingPaperXml(final Element publicationElem) {
        WorkingPaper workingPaper = (WorkingPaper) publication;

        generateXmlElement(publicationElem,
                           "reviewed",
                           workingPaper.getReviewed());
    }

    private void generateOrganizationXml(final Element publicationElem,
                                         final String elementName,
                                         final GenericOrganizationalUnit orga) {
        if (orga == null) {
            return;
        }

        Element organizationElem =
                publicationElem.newChildElement(elementName);
        Element orgaTitleElem = organizationElem.newChildElement("title");
        orgaTitleElem.setText(orga.getTitle());

    }
}
