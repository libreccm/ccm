package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
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
public class InternetArticleBundle extends PublicationBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.InternetArticleBundle";
    public static final String ORGANIZATION = "organization";

    public InternetArticleBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public InternetArticleBundle(final OID oid) throws
            DataObjectNotFoundException {
        super(oid);
    }

    public InternetArticleBundle(final BigDecimal id)
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public InternetArticleBundle(final DataObject dobj) {
        super(dobj);
    }

    public InternetArticleBundle(final String type) {
        super(type);
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final InternetArticleBundle InternetArticleBundle =
                                        (InternetArticleBundle) source;

            if (ORGANIZATION.equals(attribute)) {
                final DataCollection organizations =
                                     (DataCollection) InternetArticleBundle.get(
                        ORGANIZATION);

                while (organizations.next()) {
                    createOrganizationAssoc(organizations);
                }

                return true;
            } else {
                return super.copyProperty(source, property, copier);
            }
        } else {
            return super.copyProperty(source, property, copier);
        }
    }

    private void createOrganizationAssoc(final DataCollection organizations) {
        final GenericOrganizationalUnitBundle orgaunitDraft =
                                              (GenericOrganizationalUnitBundle) DomainObjectFactory.
                newInstance(organizations.getDataObject());
        final GenericOrganizationalUnitBundle orgaunitLive =
                                              (GenericOrganizationalUnitBundle) orgaunitDraft.
                getLiveVersion();

        if (orgaunitLive != null) {
            final DataObject link = add(ORGANIZATION, orgaunitLive);

            link.set("orgaOrder", 1);

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
            if (("internetArticle".equals(attribute)
                 && (source instanceof GenericOrganizationalUnitBundle))) {
                final GenericOrganizationalUnitBundle orgaBundle =
                                                      (GenericOrganizationalUnitBundle) source;
                final DataCollection internetArticles = (DataCollection) orgaBundle.
                        get("internetArticle");

                while (internetArticles.next()) {
                    createInternetArticleAssoc(internetArticles,
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

    private void createInternetArticleAssoc(final DataCollection internetArticles,
                                            final GenericOrganizationalUnitBundle orgaBundle) {
        final InternetArticleBundle draftInternetArticle =
                                    (InternetArticleBundle) DomainObjectFactory.
                newInstance(
                internetArticles.getDataObject());
        final InternetArticleBundle liveInternetArticle =
                                    (InternetArticleBundle) draftInternetArticle.
                getLiveVersion();

        if (liveInternetArticle != null) {
            final DataObject link = orgaBundle.add("internetArticle",
                                                   liveInternetArticle);

            link.set("orgaOrder", 1);

            link.save();
            
            XMLDeliveryCache.getInstance().removeFromCache(liveInternetArticle.getOID());
        }
    }

    public GenericOrganizationalUnitBundle getOrganization() {
        final DataCollection collection = (DataCollection) get(ORGANIZATION);

        if (collection.size() == 0) {
            return null;
        } else {
            final DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();
            collection.close();

            return (GenericOrganizationalUnitBundle) DomainObjectFactory.
                    newInstance(dobj);
        }
    }

    public void setOrganization(final GenericOrganizationalUnit organization) {
        final GenericOrganizationalUnitBundle oldOrga = getOrganization();

        if (oldOrga != null) {
            remove(ORGANIZATION, oldOrga);
        }

        if (organization != null) {
            Assert.exists(organization, GenericOrganizationalUnit.class);

            final DataObject link = add(ORGANIZATION,
                                        organization.
                    getGenericOrganizationalUnitBundle());
            link.set("orgaOrder", 1);

            link.save();
        }
    }
}
