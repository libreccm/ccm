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

package com.arsdigita.london.notes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;

public class Note extends ACSObject {
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.london.notes.Note";

    private static final Logger s_log = Logger.getLogger( Note.class );

    static {
        DomainObjectFactory.registerInstantiator(
            BASE_DATA_OBJECT_TYPE,
            new DomainObjectInstantiator() {
                public DomainObjectInstantiator resolveInstantiator
                    ( DataObject dataObject ) {
                        return this;
                }

                protected DomainObject doNewInstance( DataObject dataObject ) {
                    return new Note( dataObject );
                }
            }
        );
    }

    public static final String CONTENT = "content";
    public static final String RANK = "rank";
    public static final String OWNER = "owner";
    public static final String NOTES = "ca_notes";

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

    protected void beforeDelete() {
        // Put this note at the end so other notes will be correctly reordered
        setRank( Long.MAX_VALUE );
    }

    protected void beforeSave() {
        super.beforeSave();

        if( isNew() ) m_isNew = true;
    }

    protected void afterSave() {
        super.afterSave();

        if( m_isNew ) {
            PermissionService.setContext( this, getOwner() );
            m_isNew = false;
        }
    }
}
