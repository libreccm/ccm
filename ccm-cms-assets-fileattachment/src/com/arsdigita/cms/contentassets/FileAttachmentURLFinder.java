/*
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
 * Created on 11-May-04
 */

package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contentassets.FileAttachment;
import com.arsdigita.cms.dispatcher.AssetURLFinder;

import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.kernel.URLService;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

/**
 * A URLFinder for FileAttachments. In the draft context (ie, in CMS
 * backend search) it will return the draft url for the containing item.
 * Otherwise it will delegate to an AssetURLFinder.
 *
 * @author mbooth@redhat.com
 * @author cgyg9330
 */
public class FileAttachmentURLFinder implements URLFinder {

    private static final AssetURLFinder s_assetFinder = new AssetURLFinder();
    
    /**
      * 
      * find URL for a file attachment by finding its article
      * 
      * @param oid the OID of the file attachment
      * @param content the context of the search (ie draft/live)
      */
    public String find(OID oid, String context) throws NoValidURLException {
        if (!"draft".equals(context))
            return s_assetFinder.find(oid, context);

        DataObject dobj = SessionManager.getSession().retrieve(oid);
        if (dobj == null) {
            throw new NoValidURLException("No such data object " + oid);
        }

        if (!dobj
            .getObjectType()
            .getQualifiedName()
            .equals(FileAttachment.BASE_DATA_OBJECT_TYPE)) {
            throw new NoValidURLException(
                "Data Object is not a file attachment "
                    + dobj.getObjectType().getQualifiedName()
                    + " "
                    + oid);
        }

        FileAttachment file = new FileAttachment(dobj);
        ContentItem owner = file.getFileOwner();
        return URLService.locate(owner.getOID(), context);
    }

    /**
      * 
      * find URL for the live context of a file attachment. Delegates to
      * AssetURLFinder.
      * 
      * @param oid the OID of the file attachment
      * 
      */
    public String find(OID oid) throws NoValidURLException {
        return s_assetFinder.find(oid);
    }
}
