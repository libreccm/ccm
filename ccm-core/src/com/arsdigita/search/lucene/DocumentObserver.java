/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.lucene;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.search.ContentProvider;
import com.arsdigita.search.ContentType;
import com.arsdigita.search.MetadataProvider;
import com.arsdigita.search.MetadataProviderRegistry;
import com.arsdigita.util.Assert;


public class DocumentObserver implements com.arsdigita.search.DocumentObserver {

    private static final Logger s_log = 
        Logger.getLogger(DocumentObserver.class);
    
    /**
     * Invoked after a searchable object has been 
     * created or updated.
     *
     * @param dobj the updated object
     */
    public void onSave(DomainObject dobj) {
        MetadataProvider adapter = MetadataProviderRegistry
            .findAdapter(dobj.getObjectType());

	s_log.debug("using adapter " + adapter);

	// retrieve document & if it doesn't exist, create one if required by this DomainObject
	
        BigDecimal id = (BigDecimal)dobj.getOID().get("id");
        Assert.exists(id, BigDecimal.class);
        Document doc = Document.retrieve(id);
        if (doc == null) {
			if ((adapter == null) || (adapter!= null && adapter.isIndexable(dobj))) {
	
            if (s_log.isDebugEnabled()) {
                s_log.debug("Creating new document");
            }
            doc = Document.create(id);
            doc.setType(dobj.getObjectType().getQualifiedName());
        }
		}
        if (adapter != null) {
			if (adapter.isIndexable(dobj)) {
			
            if (s_log.isDebugEnabled()) {
                s_log.debug("Processing object " + dobj.getOID() + 
                            " using new adapters");
                s_log.debug( "Locale: " + adapter.getLocale( dobj ) );
            }


            doc.setTypeSpecificInfo(adapter.getTypeSpecificInfo(dobj));
            doc.setLocale(adapter.getLocale(dobj));
            doc.setTitle(adapter.getTitle(dobj));
            doc.setSummary(adapter.getSummary(dobj));
            doc.setCreationDate(adapter.getCreationDate(dobj));
            Party party = adapter.getCreationParty(dobj);
            doc.setCreationParty(party == null ? null : party.getID());
            doc.setLastModifiedDate(adapter.getLastModifiedDate(dobj));
            party = adapter.getLastModifiedParty(dobj);
            doc.setLastModifiedParty(party == null ? null : party.getID());
	    doc.setContentSection(adapter.getContentSection(dobj));
            ContentProvider[] content = adapter.getContent(dobj,
                                                           ContentType.TEXT);
            StringBuffer buf = new StringBuffer("");
            for (int i = 0 ; i < content.length ; i++) {
                Assert.isTrue(content[i].getType().equals(ContentType.TEXT),
                             "content is text");
                buf.append(new String(content[i].getBytes()));
            }
            doc.setContent(buf.toString().replace('\0', ' '));
        } else {
				// document already exists, but now shouldn't be indexed
				if (doc != null) {
				
					doc.setDeleted(true);
				}
            
			}
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Processing object " + dobj.getOID() + 
                            " using old adapters");
            } 

            Registry reg = Registry.getInstance();
            Adapter ladapter = reg.getAdapter(dobj.getObjectType());
            Assert.exists(ladapter, Adapter.class);

            doc.setTypeSpecificInfo(ladapter.getTypeSpecificInfo(dobj));
            doc.setLocale(ladapter.getLocale(dobj));
            doc.setTitle(ladapter.getTitle(dobj));
            doc.setSummary(ladapter.getSummary(dobj));
            doc.setCreationDate(ladapter.getCreationDate(dobj));
            Party party = ladapter.getCreationParty(dobj);
            doc.setCreationParty(party == null ? null : party.getID());
            doc.setLastModifiedDate(ladapter.getLastModifiedDate(dobj));
            party = ladapter.getLastModifiedParty(dobj);
            doc.setLastModifiedParty(party == null ? null : party.getID());
            doc.setContent(ladapter.getContent(dobj));
        }
        if (doc != null) {
        doc.setDirty(true);
        doc.save();
    }
    }

    /**
     * Invoked after a searchable object has been 
     * deleted. NB, the only guarenteed valid method
     * that can be invoked on the DomainObject is
     * getOID().
     *
     * @param dobj the deleted object
     */
    public void onDelete(DomainObject dobj) {
        BigDecimal id = (BigDecimal)dobj.getOID().get("id");
        Assert.exists(id, BigDecimal.class);
        Document doc = Document.retrieve(id);
        if (doc != null) {
            doc.setDeleted(true);
            doc.setDirty(true);
            doc.save();
        }
    }
}
