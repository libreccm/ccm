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

package com.arsdigita.cms.contentassets;

import java.util.Date;

import com.arsdigita.auditing.AuditingObserver;
import com.arsdigita.auditing.BasicAuditTrail;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;

/**
 * 
 * 
 */
public class Note extends ACSObject {

    private static final Logger s_log = Logger.getLogger( Note.class );
    
    /**  PDL stuff                                                            */
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contentassets.Note";

    static {
        s_log.debug("Static initalizer is starting...");
        DomainObjectFactory.registerInstantiator(
            BASE_DATA_OBJECT_TYPE,
            new DomainObjectInstantiator() {
                @Override
                public DomainObjectInstantiator resolveInstantiator
                    ( DataObject dataObject ) {
                        return this;
                }

                protected DomainObject doNewInstance( DataObject dataObject ) {
                    return new Note( dataObject );
                }
            }
        );

        s_log.debug("Static initalizer finished.");
    }

    public static final String CONTENT = "content";
    public static final String RANK = "rank";
    public static final String OWNER = "owner";
    public static final String NOTES = "ca_notes";
    public static final String AUDIT = "auditing";
    public static final String CREATION_DATE = AUDIT + "."
                                               + BasicAuditTrail.CREATION_DATE;
	

    private BasicAuditTrail auditTrail;

    private boolean m_isNew = false;

    private Note() {
        super( BASE_DATA_OBJECT_TYPE );
    }

    public Note( String type ) {
        super( type );
    }

    public Note( DataObject obj ) {
        super( obj );
    }

    public static Note create( ContentItem item ) {
        DataCollection notes = getNotes( item );
        long nextRank = notes.size();

        Note note = new Note();
        note.set( OWNER, item );
        note.set( RANK, new Long( (int)nextRank ) );

        return note;
    }

    /**
	 * Register auditing observer 
	 *  (non-Javadoc)
	 * @see com.arsdigita.domain.DomainObject#initialize()
	 */
    @Override
	protected void initialize() {
		super.initialize();

		DataObject dataObj = (DataObject) get(AUDIT);
		if (dataObj != null) {
			auditTrail = new BasicAuditTrail(dataObj);
		} else {
			// creates a new one when one doesn't already exist
			auditTrail = BasicAuditTrail.retrieveForACSObject(this);
		}

		addObserver(new AuditingObserver(auditTrail));
	}
	
    public String getContent() {
        return (String) get( CONTENT );
    }

    public void setContent( String content ) {
        set( CONTENT, content );
    }

    public long getRank() {
        Long rank = (Long) get( RANK );
        Assert.exists( rank, Long.class );

        return rank.longValue();
    }

    public void setRank( long newRank ) {
        DataCollection notes = getNotes( getOwner() );

        if( newRank < 0 ) newRank = 0;

        Note last = null;
        long currentRank = 0;
        while( notes.next() ) {
            if( newRank == currentRank ) currentRank++;

            Note current = (Note) DomainObjectFactory.newInstance
                ( notes.getDataObject() );

            if( equals( current ) ) continue;

            if( current.getRank() != currentRank )
                current.set( RANK, new Long( currentRank ) );

            currentRank++;
        }
        notes.close();

        if( newRank > currentRank )
            set( RANK, new Long( currentRank ) );
        else
            set( RANK, new Long( newRank ) );
    }

    public ContentItem getOwner() {
        DataObject obj = (DataObject) get( OWNER );
        Assert.exists( obj, DataObject.class );

        return (ContentItem) DomainObjectFactory.newInstance( obj );
    }

    public User getNoteAuthor () {
    	return auditTrail.getCreationUser();
    }
    
    public Date getCreationDate () {
    	return auditTrail.getCreationDate();
    }
    
    public static DataCollection getNotes( ContentItem item ) {
        Assert.exists( item, ContentItem.class );

        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Retrieving notes for " + item.getOID() );
        }

        DataCollection notes = SessionManager.getSession().retrieve
            ( BASE_DATA_OBJECT_TYPE );

        notes.addEqualsFilter( OWNER, item.getID() );
        notes.addOrder( RANK );

        return notes;
    }

    @Override
    protected void beforeDelete() {
        // Put this note at the end so other notes will be correctly reordered
        setRank( Long.MAX_VALUE );
    }

    @Override
    protected void beforeSave() {
        super.beforeSave();

        if( isNew() ) m_isNew = true;
    }

    @Override
    protected void afterSave() {
        super.afterSave();

        if( m_isNew ) {
            PermissionService.setContext( this, getOwner() );
            m_isNew = false;
        }
    }
}
