/*
 * Copyright (c) 2010 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
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
public class UnPublishedBundle extends PublicationBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.UnPublishedBundle";
    public static final String ORGANIZATION = "organization";

    public UnPublishedBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public UnPublishedBundle(final OID oid) throws
            DataObjectNotFoundException {
        super(oid);
    }

    public UnPublishedBundle(final BigDecimal id)
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public UnPublishedBundle(final DataObject dobj) {
        super(dobj);
    }

    public UnPublishedBundle(final String type) {
        super(type);
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final UnPublishedBundle unPublishedBundle =
                                    (UnPublishedBundle) source;

            if (ORGANIZATION.equals(attribute)) {
                final DataCollection organizations =
                                     (DataCollection) unPublishedBundle.get(
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
            if (("unPublished".equals(attribute)
                 && (source instanceof GenericOrganizationalUnitBundle))) {
                final GenericOrganizationalUnitBundle orgaBundle =
                                                      (GenericOrganizationalUnitBundle) source;
                final DataCollection publications = (DataCollection) orgaBundle.
                        get("unPublished");

                while (publications.next()) {
                    createUnPublishedAssoc(publications,
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

    private void createUnPublishedAssoc(final DataCollection publications,
                                        final GenericOrganizationalUnitBundle orgaBundle) {
        final UnPublishedBundle draftUnPublished =
                                (UnPublishedBundle) DomainObjectFactory.
                newInstance(
                publications.getDataObject());
        final UnPublishedBundle liveUnPublished =
                                (UnPublishedBundle) draftUnPublished.
                getLiveVersion();

        if (liveUnPublished != null) {
            final DataObject link = orgaBundle.add("unPublished",
                                                   liveUnPublished);

            link.set("orgaOrder", 1);

            link.save();
            
            XMLDeliveryCache.getInstance().removeFromCache(liveUnPublished.getOID());
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
