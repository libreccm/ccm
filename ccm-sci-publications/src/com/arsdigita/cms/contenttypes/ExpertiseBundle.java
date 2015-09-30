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
public class ExpertiseBundle extends PublicationBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.ExpertiseBundle";
    public static final String ORGANIZATION = "organization";
    public static final String ORDERER = "orderer";

    public ExpertiseBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public ExpertiseBundle(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public ExpertiseBundle(final BigDecimal id)
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ExpertiseBundle(final DataObject dobj) {
        super(dobj);
    }

    public ExpertiseBundle(final String type) {
        super(type);
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final ExpertiseBundle expertiseBundle = (ExpertiseBundle) source;

            if (ORGANIZATION.equals(attribute)) {
                final DataCollection organizations =
                                     (DataCollection) expertiseBundle.get(
                        ORGANIZATION);

                while (organizations.next()) {
                    createOrganizationAssoc(organizations);
                }

                return true;
            } else if (ORDERER.equals(attribute)) {
                final DataCollection orderers =
                                     (DataCollection) expertiseBundle.get(
                        ORDERER);

                while (orderers.next()) {
                    createOrdererAssoc(orderers);
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

    private void createOrdererAssoc(final DataCollection orderers) {
        final GenericOrganizationalUnitBundle ordererDraft =
                                              (GenericOrganizationalUnitBundle) DomainObjectFactory.
                newInstance(orderers.getDataObject());
        final GenericOrganizationalUnitBundle ordererLive =
                                              (GenericOrganizationalUnitBundle) ordererDraft.
                getLiveVersion();

        if (ordererLive != null) {
            final DataObject link = add(ORDERER, ordererLive);

            link.set("ordererOrder", 1);

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
            if (("expertise".equals(attribute)
                 && (source instanceof GenericOrganizationalUnitBundle))) {
                final GenericOrganizationalUnitBundle orgaBundle =
                                                      (GenericOrganizationalUnitBundle) source;
                final DataCollection expertises = (DataCollection) orgaBundle.
                        get("expertise");

                while (expertises.next()) {
                    createExpertiseAssoc(expertises,
                                         (GenericOrganizationalUnitBundle) liveItem);
                }

                return true;
            } else if ("ordererExpertise".equals(attribute)
                       && (source instanceof GenericOrganizationalUnitBundle)) {
                final GenericOrganizationalUnitBundle ordererBundle =
                                                      (GenericOrganizationalUnitBundle) source;
                final DataCollection expertises =
                                     (DataCollection) ordererBundle.get(
                        "ordererExpertise");

                while (expertises.next()) {
                    createOrderedExpertiseAssoc(expertises,
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

    private void createExpertiseAssoc(final DataCollection expertises,
                                      final GenericOrganizationalUnitBundle orgaBundle) {
        final ExpertiseBundle draftExpertise =
                              (ExpertiseBundle) DomainObjectFactory.newInstance(
                expertises.getDataObject());
        final ExpertiseBundle liveExpertise = (ExpertiseBundle) draftExpertise.
                getLiveVersion();

        if (liveExpertise != null) {
            final DataObject link = orgaBundle.add("expertise", liveExpertise);

            link.set("orgaOrder", expertises.get("link.orgaOrder"));

            link.save();
            
            XMLDeliveryCache.getInstance().removeFromCache(liveExpertise.getOID());
        }
    }

    private void createOrderedExpertiseAssoc(final DataCollection expertises,
                                             final GenericOrganizationalUnitBundle orderer) {
        final ExpertiseBundle draftExpertise =
                              (ExpertiseBundle) DomainObjectFactory.newInstance(
                expertises.getDataObject());
        final ExpertiseBundle liveExpertise = (ExpertiseBundle) draftExpertise.
                getLiveVersion();

        if (liveExpertise != null) {
            final DataObject link = orderer.add("orderedExpertise",
                                                liveExpertise);

            link.set("ordererOrder", expertises.get("link.ordererOrder"));

            link.save();
            
            XMLDeliveryCache.getInstance().removeFromCache(liveExpertise.getOID());
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
            
            return (GenericOrganizationalUnitBundle) DomainObjectFactory.newInstance(dobj);
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
                                        organization.getGenericOrganizationalUnitBundle());
            link.set("orgaOrder",1);
            
            link.save();
        }
    }
    
     public GenericOrganizationalUnitBundle getOrderer() {
        final DataCollection collection = (DataCollection) get(ORDERER);
        
        if (collection.size() == 0) {
            return null;
        } else {
            final DataObject dobj;
            
            collection.next();
            dobj = collection.getDataObject();
            collection.close();
            
            return (GenericOrganizationalUnitBundle) DomainObjectFactory.newInstance(dobj);
        }
    }
     
      public void setOrderer(final GenericOrganizationalUnit orderer) {
        final GenericOrganizationalUnitBundle oldOrga = getOrganization();
        
        if (oldOrga != null) {
            remove(ORDERER, oldOrga);
        } 
        
        if (orderer != null) {
            Assert.exists(orderer, GenericOrganizationalUnit.class);
            
            final DataObject link = add(ORDERER, 
                                        orderer.getGenericOrganizationalUnitBundle());
            link.set("ordererOrder",1);
            
            link.save();
        }
    }
}
