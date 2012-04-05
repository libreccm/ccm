package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.AssociationCopier;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.ItemCopier;
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
                               "com.arsdigita.cms.contentttypes.PublicationBundle";
    public static final String AUTHORS = "authors";
    public final static String AUTHOR_ORDER = "authorOrder";
    public final static String EDITOR = "editor";

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
            final Publication pubBundle = (Publication) source;

            if (AUTHORS.equals(attribute)) {
                final DataCollection authors = (DataCollection) pubBundle.get(
                        AUTHORS);

                while (authors.next()) {
                    createAuthorAssoc(authors);
                }

                return true;
            } else {
                return super.copyProperty(source, property, copier);
            }

        } else {
            return super.copyProperty(source, property, copier);
        }
    }

    @Override
    public boolean handlesReverseProperties() {
        return true;
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
            } else {
                return super.copyReverseProperty(source, liveItem, property,
                                                 copier);
            }
        } else {
            return super.copyReverseProperty(source, liveItem, property, copier);
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
            link.set(AUTHOR_ORDER, publications.get(AuthorshipCollection.LINKORDER));
            
            link.save();
        }
    }

    public AuthorshipCollection getAuthors() {
        return new AuthorshipCollection((DataCollection) get(AUTHORS));
    }

    public void addAuthor(final GenericPerson author, final Boolean editor) {
        Assert.exists(author, GenericPerson.class);

        final DataObject link = add(AUTHORS, author);

        link.set(EDITOR, editor);
        link.set(AUTHOR_ORDER, Integer.valueOf((int) getAuthors().size()));

        updateAuthorsStr();
    }

    public void removeAuthor(final GenericPerson author) {
        Assert.exists(author, GenericPerson.class);

        remove(AUTHORS, author);

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
}
