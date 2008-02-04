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
 */

package com.arsdigita.cms.dispatcher;

import com.arsdigita.cms.Asset;

import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Web;
import com.arsdigita.web.WebConfig;

/**
 * @author mbooth@redhat.com
 *
 * Implementation of URLFinder for Assets
 */
public class AssetURLFinder implements URLFinder {
    /**
      * 
      * find URL for an asset
      * 
      * @param oid the OID of the asset
      * @param context the context of the lookup (live/draft)
      * 
      */
    public String find(OID oid, String context) throws NoValidURLException {
        if( !"live".equals( context ) )
            throw new NoValidURLException("No draft URL for assets");

        WebConfig config = Web.getConfig();

        StringBuffer url = new StringBuffer();
        url.append( config.getDispatcherServletPath() );
        url.append( config.getDispatcherContextPath() );
        url.append( "/cms-service/stream/asset/?asset_id=" );
        url.append( oid.get( Asset.ID ).toString() );

        return url.toString();
    }

    /**
      * 
      * find URL for an asset in the live context
      * 
      * @param oid the OID of the asset
      */
    public String find(OID oid) throws NoValidURLException {
        return find(oid, "live");
    }
}
