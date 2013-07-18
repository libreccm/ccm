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

/*
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class LibrarySignature extends ACSObject {
    
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contentassets.LibrarySignature";
    
    public static final String LIBRARY = "library";
    public static final String SIGNATURE = "signature";
    public static final String LIBRARY_LINK = "librarylink";
    public static final String PUBLICATION = "publication";
    
    public LibrarySignature() {
        super(BASE_DATA_OBJECT_TYPE);
    }
    
    public LibrarySignature(final BigDecimal signatureId) {
        super(new OID(BASE_DATA_OBJECT_TYPE, signatureId));
    }
    
    public LibrarySignature(final String type) {
        super(type);
    }
    
    public LibrarySignature(final DataObject dataObject) {
        super(dataObject);
    }
    
    public static LibrarySignature create(final Publication publication) {                
        final LibrarySignature signature = new LibrarySignature();
        signature.set(PUBLICATION, publication);
        
        return signature;
    }
    
    public static DataCollection getLibrarySignatures(final Publication publication) {
        
        Assert.exists(publication, Publication.class);
        
        final DataCollection signatures = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        
        signatures.addEqualsFilter(PUBLICATION, publication.getID());
        signatures.addOrder(LIBRARY);
        signatures.addOrder(SIGNATURE);
        
        return signatures;
        
    }
    
    public String getLibrary() {
        return (String) get(LIBRARY);
    }
    
    public void setLibrary(final String library) {
        set(LIBRARY, library);
    }
    
    public String getSignature() {
        return (String) get(SIGNATURE);
    }
    
    public void setSignature(final String signature) {
        set(SIGNATURE, signature);
    }
    
    public String getLibraryLink() {
        return (String) get(LIBRARY_LINK);
    }
    
    public void setLibraryLink(final String libraryLink) {
        set(LIBRARY_LINK, libraryLink);
    }
    
    public Publication getPublication() {
        final DataObject dataObject = (DataObject) get(PUBLICATION);
        
        return (Publication) DomainObjectFactory.newInstance(dataObject);
    }
    
    
}
