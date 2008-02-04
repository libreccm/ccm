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
package com.arsdigita.search.intermedia;

import java.math.BigDecimal;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.search.ContentProvider;
import com.arsdigita.search.ContentType;
import com.arsdigita.search.MetadataProvider;
import com.arsdigita.search.MetadataProviderRegistry;


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

	s_log.debug("adapter is " + adapter);
	s_log.debug("dobj is " + dobj);
        if (adapter != null) {
        	
            if (s_log.isDebugEnabled()) {
                s_log.debug("Processing object " + dobj.getOID() + 
                            " using new adapters");
            } 

            ContentProvider[] xmlContent = adapter.getContent(dobj, ContentType.XML);
            ContentProvider[] rawContent = adapter.getContent(dobj, ContentType.RAW);
            
            // We only support one block of XML & RAW content per object currently
            byte[] xml = xmlContent.length > 0 ? xmlContent[0].getBytes() : null;
            byte[] raw = rawContent.length > 0 ? rawContent[0].getBytes() : null;

            Locale locale = adapter.getLocale(dobj);
	    
	    String contentSection = adapter.getContentSection(dobj);

            doOnSave(dobj,
            		 adapter.isIndexable(dobj),
                     adapter.getTitle(dobj),
                     adapter.getSummary(dobj),
                     locale == null ? "en" : locale.getLanguage(),
                     xml == null ? null : new String(xml),
                     raw,
		     contentSection);

        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Processing object " + dobj.getOID() + 
                            " using old adapters");
            } 

            Searchable searchable = (Searchable)dobj;
            String xml = searchable.getSearchXMLContent();
            byte[] raw = searchable.getSearchRawContent();

            if ((xml == null || xml.length() == 0) && 
                (raw == null || raw.length == 0)) {
                doOnDelete(dobj.getOID());
            } else {
                // Check for using SearchIndexHelp class.
                final String helpKeyword = "use_SearchIndexHelp";
                boolean help_xml = xml != null && 
                    xml.equalsIgnoreCase(helpKeyword);
                boolean help_raw = raw != null && 
                    raw.length == helpKeyword.length() &&
                    helpKeyword.equalsIgnoreCase(new String(raw));
                if (help_xml || help_raw) {
                    SearchIndexHelp sh = new SearchIndexHelp();
                    // Get wanted content to index.  Is stored in sh object.
                    sh.retrieveContent(dobj);
                    if (help_xml) {
                        xml = sh.xmlContent();
                    }
                    if (help_raw) {
                        raw = sh.rawContent();
                    }
                }
                // nb - conditional indexing not supported for legacy adapter
                doOnSave(dobj,
                		 true,
                         searchable.getSearchLinkText(),
                         searchable.getSearchSummary(),
                         searchable.getSearchLanguage(),
                         xml,
                         raw);
            }
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
        doOnDelete(dobj.getOID());
    }

    
    private void doOnSave(DomainObject object,
    					  boolean shouldBeIndexed,
                          String title,
                          String summary,
                          String language,
                          String xml,
                          byte[] raw) {
         
        OID oid = object.getOID();
        SearchContent sc = SearchContent.retrieveForObject(oid);
        if (sc == null) {
        	if (shouldBeIndexed) {
        	
            // create new SearchContent object (but don't save in database yet).
            sc = new SearchContent();
            BigDecimal id = (BigDecimal)oid.get("id");
            sc.setObjectId(id);
            sc.setContentObjectType(oid.getObjectType().getQualifiedName());
        } else {
        		// no search content and we don't want any
        		return;
        	}
        } else  {
        	if (!shouldBeIndexed) {
        		// search content exists, but we don't want it now
        		sc.delete();
        		return;
        	} else {
        	
            // Object already indexed.  Don't save again if there were no changes
            if (stringMatch(sc.getSummary(), summary) &&
                stringMatch(sc.getLinkText(), title) &&
                stringMatch(sc.getXMLContent(), xml) &&
                byteMatch(sc.getRawContent(), raw) &&
                stringMatch(sc.getLanguage(), language)) {
                return;  // no changes to object, don't save again.
            }
        }
        }
        
        sc.setSummary(summary);
        sc.setLinkText(title);
        sc.setXMLContent(xml);
        sc.setRawContent(raw);
        sc.setLanguage(language);
        sc.save();
        
        // Flag that content changed so index will be resynced
        ContentChangeTime.flagChange();
    }

    private void doOnSave(DomainObject object,
    					  boolean shouldBeIndexed,    
                          String title,
                          String summary,
                          String language,
                          String xml,
                          byte[] raw,
			  			  String contentSection) 	{
		OID oid = object.getOID();
        SearchContent sc = SearchContent.retrieveForObject(oid);
        if (sc == null) {
			if (shouldBeIndexed) {
	        	
            // create new SearchContent object (but don't save in database yet).
            sc = new SearchContent();
            BigDecimal id = (BigDecimal)oid.get("id");
            sc.setObjectId(id);
            sc.setContentObjectType(oid.getObjectType().getQualifiedName());
        } else {
				// no search content and we don't want any
				return;
			}   
        } else {
			if (!shouldBeIndexed) {
				// search content exists, but we don't want it now
				sc.delete();
				return;
			} else {
            // Object already indexed.  Don't save again if there were no changes
            if (stringMatch(sc.getSummary(), summary) &&
                stringMatch(sc.getLinkText(), title) &&
                stringMatch(sc.getXMLContent(), xml) &&
                byteMatch(sc.getRawContent(), raw) &&
                stringMatch(sc.getLanguage(), language) &&
		stringMatch(sc.getContentSection(), contentSection)) {
                return;  // no changes to object, don't save again.
            }
        }
        }
        
        sc.setSummary(summary);
        sc.setLinkText(title);
        sc.setXMLContent(xml);
        sc.setRawContent(raw);
        sc.setLanguage(language);
	sc.setContentSection(contentSection);
        sc.save();
        
        // Flag that content changed so index will be resynced
        ContentChangeTime.flagChange();
    }


    private void doOnDelete(OID oid) {
        SearchContent sc = SearchContent.retrieveForObject(oid);
        if (sc != null) {
            sc.delete();
            // Flag that content changed so index will be resynced
            ContentChangeTime.flagChange();
        }
    }


    private boolean stringMatch(String a, String b) {
        if (a==null) {
            return (b==null);
        }
        return a.equals(b);
    }
    
    private boolean byteMatch(byte [] a, byte [] b) {
        if (a == null) {
            return (b == null);
        }
        if (b == null) {
            return false;
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }
    

}
