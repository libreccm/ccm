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
public class ArticleInJournalBundle extends PublicationBundle {
    
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.ArticleInJournalBundle";
    public static final String JOURNAL = "journal";
    
    public ArticleInJournalBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);
        
        Assert.exists(primary, ContentItem.class);
        
        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);
        
        setName(primary.getName());        
    }
    
    public ArticleInJournalBundle(final OID oid) 
            throws DataObjectNotFoundException {
        super(oid);
    }
    
    public ArticleInJournalBundle(final BigDecimal id) 
    throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
    public ArticleInJournalBundle(final DataObject dobj) {
        super(dobj);
    }
    
    public ArticleInJournalBundle(final String type) {
        super(type);
    }
    
    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property, 
                                final ItemCopier copier) {
        final String attribute = property.getName();
        
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final ArticleInJournalBundle articleBundle = (ArticleInJournalBundle) source;
            
            if (JOURNAL.equals(attribute)) {
                final DataCollection journals = (DataCollection) articleBundle.get(JOURNAL);
                
                while(journals.next()) {
                    createJournalAssoc(journals);
                }
                
                return true;
            } else {
                return super.copyProperty(source, property, copier);
            }
        } else {
            return super.copyProperty(source, property, copier);
        }
    }
    
    private void createJournalAssoc(final DataCollection journals) {
        final JournalBundle draftJournal = (JournalBundle) DomainObjectFactory.newInstance(journals.getDataObject());
        final JournalBundle liveJournal = (JournalBundle) draftJournal.getLiveVersion();
        
        if (liveJournal != null) {
            final DataObject link = add(JOURNAL, liveJournal);
            
            link.set(JournalBundle.ARTICLE_ORDER, 
                     journals.get(ArticleInJournalCollection.LINKORDER));
            
            link.save();
        }
    }
    
    public JournalBundle getJournal() {
        final DataCollection collection = (DataCollection) get(JOURNAL); 
        
        if (collection.size() == 0) {
            return null;
        } else {
            final DataObject dobj;
            
            collection.next();
            dobj = collection.getDataObject();
            collection.close();
            
            return (JournalBundle) DomainObjectFactory.newInstance(dobj);
        }        
    }
    
    public void setJournal(final Journal journal) {
        final JournalBundle oldJournal = getJournal();
        
        if (oldJournal != null) {
            remove(JOURNAL, oldJournal);
        }
        
        if (journal != null) {
            Assert.exists(journal, JournalBundle.class);
            
            final DataObject link = add(JOURNAL, journal.getJournalBundle());
            link.set(JournalBundle.ARTICLE_ORDER, 
                     Integer.valueOf((int) journal.getArticles().size()));
            link.save();
        }
    }
}
