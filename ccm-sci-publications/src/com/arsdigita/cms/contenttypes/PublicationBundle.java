package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.cms.XMLDeliveryCache;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationBundle extends ContentBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.PublicationBundle";
    public static final String AUTHORS = "authors";
    public final static String AUTHOR_ORDER = "authorOrder";
    public final static String EDITOR = "editor";
    public final static String ORGAUNITS = "orgaunits";
    public final static String ORGAUNIT_ORDER = "publicationOrder";
    public final static String ORGAUNIT_PUBLICATIONS = "publications";
    public final static String SERIES = "series";

    public PublicationBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public PublicationBundle(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public PublicationBundle(final BigDecimal id)
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PublicationBundle(final DataObject dobj) {
        super(dobj);
    }

    public PublicationBundle(final String type) {
        super(type);
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final PublicationBundle pubBundle = (PublicationBundle) source;

            if (AUTHORS.equals(attribute)) {
                final DataCollection authors = (DataCollection) pubBundle.get(
                        AUTHORS);

                while (authors.next()) {
                    createAuthorAssoc(authors);
                }

                return true;
            } else if (ORGAUNITS.equals(attribute)) {
                final DataCollection orgaunits = (DataCollection) pubBundle.get(
                        ORGAUNITS);

                while (orgaunits.next()) {
                    createOrgaUnitAssoc(orgaunits);
                }

                return true;
            } else {
                return super.copyProperty(source, property, copier);
            }

        } else {
            return super.copyProperty(source, property, copier);
        }
    }

    private void createAuthorAssoc(final DataCollection authors) {
        final GenericPersonBundle draftAuthor =
                                  (GenericPersonBundle) DomainObjectFactory.
                newInstance(authors.getDataObject());
        final GenericPersonBundle liveAuthor =
                                  (GenericPersonBundle) draftAuthor.
                getLiveVersion();

        if (liveAuthor != null) {
            final DataObject link = add(AUTHORS, liveAuthor);

            link.set(EDITOR, authors.get(AuthorshipCollection.LINKEDITOR));
            link.set(AUTHOR_ORDER, authors.get(AuthorshipCollection.LINKORDER));

            link.save();
        }
    }

    private void createOrgaUnitAssoc(final DataCollection orgaunits) {
        final GenericOrganizationalUnitBundle orgaunitDraft =
                                              (GenericOrganizationalUnitBundle) DomainObjectFactory.
                newInstance(orgaunits.getDataObject());
        final GenericOrganizationalUnitBundle orgaunitLive =
                                              (GenericOrganizationalUnitBundle) orgaunitDraft.
                getLiveVersion();

        if (orgaunitLive != null) {
            final DataObject link = add(ORGAUNITS, orgaunitLive);

            link.set(ORGAUNIT_ORDER, orgaunits.get("link." + ORGAUNIT_ORDER));

            link.save();
        }
    }

    @Override
    public boolean copyReverseProperty(final CustomCopy source,
                                       final ContentItem liveItem,
                                       final Property property,
                                       final ItemCopier copier) {
        final String attribute = property.getName();
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            if (("publication".equals(attribute))
                && (source instanceof GenericPersonBundle)) {
                final GenericPersonBundle authorBundle =
                                          (GenericPersonBundle) source;
                final DataCollection publications =
                                     (DataCollection) authorBundle.get(
                        "publication");

                while (publications.next()) {
                    createAuthorPublicationAssoc(publications,
                                                 (GenericPersonBundle) liveItem);
                }

                return true;
            } else if (("publications".equals(attribute))
                       && (source instanceof GenericOrganizationalUnitBundle)) {
                final GenericOrganizationalUnitBundle orgaunitBundle =
                                                      (GenericOrganizationalUnitBundle) source;
                final DataCollection publications =
                                     (DataCollection) orgaunitBundle.get(
                        "publications");

                while (publications.next()) {
                    createOrgaunitPublicationAssoc(publications,
                                                   (GenericOrganizationalUnitBundle) liveItem);
                }

                return true;
            } else {
                return super.copyReverseProperty(source,
                                                 liveItem,
                                                 property,
                                                 copier);
            }
        } else {
            return super.copyReverseProperty(source, liveItem, property, copier);
        }
    }

    private void createAuthorPublicationAssoc(final DataCollection publications,
                                              final GenericPersonBundle author) {
        final PublicationBundle draftPublication =
                                (PublicationBundle) DomainObjectFactory.
                newInstance(publications.getDataObject());
        final PublicationBundle livePublication =
                                (PublicationBundle) draftPublication.
                getLiveVersion();

        if (livePublication != null) {
            final DataObject link = author.add("publication", livePublication);

            link.set(EDITOR, publications.get(AuthorshipCollection.LINKEDITOR));
            link.set(AUTHOR_ORDER, publications.get(
                    AuthorshipCollection.LINKORDER));                        

            link.save();
            
            XMLDeliveryCache.getInstance().removeFromCache(livePublication.getOID());
        }
    }

    private void createOrgaunitPublicationAssoc(
            final DataCollection publications,
            final GenericOrganizationalUnitBundle orgaunit) {
        final PublicationBundle draftPublication =
                                (PublicationBundle) DomainObjectFactory.
                newInstance(publications.getDataObject());
        final PublicationBundle livePublication =
                                (PublicationBundle) draftPublication.
                getLiveVersion();

        if (livePublication != null) {
            final DataObject link =
                             orgaunit.add("publications", livePublication);

            link.set(ORGAUNIT_ORDER, publications.get("link." + ORGAUNIT_ORDER));

            link.save();
            
            XMLDeliveryCache.getInstance().removeFromCache(livePublication.getOID());
        }
    }

    public AuthorshipCollection getAuthors() {
        return new AuthorshipCollection((DataCollection) get(AUTHORS));
    }

    public void addAuthor(final GenericPerson author, final Boolean editor) {
        Assert.exists(author, GenericPerson.class);

        final DataObject link = add(AUTHORS, author.getGenericPersonBundle());

        link.set(EDITOR, editor);
        link.set(AUTHOR_ORDER, Integer.valueOf((int) getAuthors().size()));

        updateAuthorsStr();
    }

    public void removeAuthor(final GenericPerson author) {
        Assert.exists(author, GenericPerson.class);

        remove(AUTHORS, author.getContentBundle());

        updateAuthorsStr();
    }

    protected void updateAuthorsStr() {
        final AuthorshipCollection authors = getAuthors();
        StringBuilder builder = new StringBuilder();
        while (authors.next()) {
            if (builder.length() > 0) {
                builder.append("; ");
            }
            builder.append(authors.getSurname());
            builder.append(", ");
            builder.append(authors.getGivenName());
        }

        final String authorsStr = builder.toString();

        final ItemCollection instances = getInstances();

        Publication publication;
        while (instances.next()) {
            publication = (Publication) instances.getDomainObject();
            publication.set(Publication.AUTHORS_STR, authorsStr);
        }
    }

    public PublicationGenericOrganizationalsUnitCollection getOrganizationalUnits() {
        return new PublicationGenericOrganizationalsUnitCollection(
                (DataCollection) get(ORGAUNITS));
    }

    public void addOrganizationalUnit(final GenericOrganizationalUnit orgaunit) {
        Assert.exists(orgaunit, GenericOrganizationalUnit.class);

        final DataObject link = add(ORGAUNITS, orgaunit.
                getGenericOrganizationalUnitBundle());

        link.set(ORGAUNIT_ORDER, Integer.valueOf((int) getOrganizationalUnits().
                size()));

        link.save();
    }

    public void removeOrganizationalUnit(
            final GenericOrganizationalUnit orgaunit) {
        Assert.exists(orgaunit, GenericOrganizationalUnit.class);

        remove(ORGAUNITS, orgaunit);
    }

    public static PublicationBundleCollection getPublications(final GenericPerson person) {
        final GenericPersonBundle personBundle = person.getGenericPersonBundle();
        
        final DataCollection collection = (DataCollection) personBundle.get("publication");
        
        return new PublicationBundleCollection(collection);
    }
    
    public static GenericOrganizationalUnitPublicationsCollection getPublications(
            final GenericOrganizationalUnit orgaunit) {
        final GenericOrganizationalUnitBundle orgaunitBundle = orgaunit.
                getGenericOrganizationalUnitBundle();

        final DataCollection collection = (DataCollection) orgaunitBundle.get(
                ORGAUNIT_PUBLICATIONS);

        return new GenericOrganizationalUnitPublicationsCollection(
                collection);
    }
    
    public static void addPublication(final GenericOrganizationalUnit orgaunit,
                                      final Publication publication) {
        Assert.exists(publication);

         final GenericOrganizationalUnitBundle orgaunitBundle = orgaunit.
                getGenericOrganizationalUnitBundle();
        
        orgaunitBundle.add(ORGAUNIT_PUBLICATIONS, 
                           publication.getPublicationBundle());
    }

    public static void removePublication(
            final GenericOrganizationalUnit orgaunit,
            final Publication publication) {
        Assert.exists(publication);
        
        final GenericOrganizationalUnitBundle orgaunitBundle = orgaunit.
                getGenericOrganizationalUnitBundle();

        orgaunitBundle.remove(ORGAUNIT_PUBLICATIONS, 
                              publication.getPublicationBundle());
    }
    
    public SeriesCollection getSeries() {
        return new SeriesCollection((DataCollection) get(SERIES));
    }
    
    public void addSeries(final Series series, final Integer volumeOfSeries) {
        Assert.exists(series, Series.class);
        
        final DataObject link = add(SERIES, series.getSeriesBundle());
        link.set(SeriesBundle.VOLUME_OF_SERIES, volumeOfSeries);
    }
    
    public void removeSeries(final Series series) {
        Assert.exists(series, Series.class);
        
        remove(SERIES, series.getSeriesBundle());
    }
    
    public Publication getPublication() {
        return (Publication) getPrimaryInstance();
    }
    
    public Publication getPublication(final String language) {
        return (Publication) getInstance(language);
    }
}
