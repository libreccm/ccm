/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PublicationTypeAsset extends ACSObject {
    
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contentassets.PublicationTypeAsset";
    
    public static final String PUBLICATION_TYPE = "publicationType";
    public static final String ISBN = "isbn";
    public static final String MISC = "misc";
    public static final String PUBLICATION = "publication";
    
    public PublicationTypeAsset() {
        super(BASE_DATA_OBJECT_TYPE);
    }
    
    public PublicationTypeAsset(final BigDecimal publicationTypeId) {
        super(new OID(BASE_DATA_OBJECT_TYPE, publicationTypeId));
    }
    
    public PublicationTypeAsset(final String type) {
        super(type);
    }
    
    public PublicationTypeAsset(final DataObject dataObject) {
        super(dataObject);
    }
    
    public static PublicationTypeAsset create(final Publication publication) {
        final PublicationTypeAsset type = new PublicationTypeAsset();
        type.set(PUBLICATION, publication); 
        
        return type;
    }
    
    public static DataCollection getPublicationTypeAssets(final Publication publication) {
        Assert.exists(publication, Publication.class);
        
        final DataCollection types = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        
        types.addEqualsFilter(PUBLICATION, publication.getID());
        types.addOrder(PUBLICATION_TYPE);
        types.addOrder(ISBN);
        
        return types;
    }
    
    public String getPublicationType() {
        return (String) get(PUBLICATION_TYPE);
    }
    
    public void setPublicationType(final String type) {
        set(PUBLICATION_TYPE, type);
    }
    
    public String getIsbn() {
        return (String) get(ISBN);
    }
    
    public void setIsbn(final String isbn) {
        set(ISBN, isbn);
    }
    
    public String getMisc() {
        return (String) get(MISC);
    }
    
    public void setMisc(final String misc) {
        set(MISC, misc);
    }
    
    public Publication getPublication() {
        final DataObject dataObject = (DataObject) get(PUBLICATION);
        
        return (Publication) DomainObjectFactory.newInstance(dataObject);
    }
    
}
