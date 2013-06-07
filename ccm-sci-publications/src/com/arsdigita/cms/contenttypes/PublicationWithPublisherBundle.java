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
public class PublicationWithPublisherBundle extends PublicationBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.PublicationWithPublisherBundle";
    public static final String PUBLISHER = "publisher";
    public static final String PUBLISHER_ORDER = "publisherOrder";

    public PublicationWithPublisherBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public PublicationWithPublisherBundle(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public PublicationWithPublisherBundle(final BigDecimal id)
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PublicationWithPublisherBundle(final DataObject dobj) {
        super(dobj);
    }

    public PublicationWithPublisherBundle(final String type) {
        super(type);
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();
        
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final PublicationWithPublisherBundle pubBundle = (PublicationWithPublisherBundle) source;
            
            if (PUBLISHER.equals(attribute)) {
                final DataCollection publishers = (DataCollection) pubBundle.get(PUBLISHER);
                
                while(publishers.next()) {
                    createPublisherAssoc(publishers);
                }
                
              return true;  
            }  else {
                return super.copyProperty(source, property, copier);
            }
        } else {
            return super.copyProperty(source, property, copier);
        }
    }
    
    private void createPublisherAssoc(final DataCollection publishers) {
        final PublisherBundle draftPublisher = (PublisherBundle) DomainObjectFactory.newInstance(
                publishers.getDataObject());
        final PublisherBundle livePublisher = (PublisherBundle) draftPublisher.getLiveVersion();
        
        if (livePublisher != null) {
            final DataObject link = add(PUBLISHER, livePublisher);
            
            link.set(PUBLISHER_ORDER, 
                     publishers.get("link." + PUBLISHER_ORDER));
            
            link.save();
        }                
    }
    
    public PublisherBundle getPublisher() {
        final DataCollection collection = (DataCollection) get(PUBLISHER);
        
        if (collection.size() == 0) {
            return null;
        } else {
            final DataObject dobj;
            
            collection.next();
            dobj = collection.getDataObject();
            collection.close();
            
            return (PublisherBundle) DomainObjectFactory.newInstance(dobj);
        }
    }
    
    public void setPublisher(final Publisher publisher) {
        final PublisherBundle oldPublisher = getPublisher();
        
        if (oldPublisher != null) {
            remove(PUBLISHER, oldPublisher);
        }
        
        if (publisher != null) {
            Assert.exists(publisher, Publisher.class);
            
            final DataObject link = add(PUBLISHER, 
                                        publisher.getPublisherBundle());
            link.set(PUBLISHER_ORDER, Integer.valueOf(1));
            link.save();
        }
    }
}
