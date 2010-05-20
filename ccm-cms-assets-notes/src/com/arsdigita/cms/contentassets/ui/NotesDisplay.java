/*
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1 of
the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.xml.Element;

import com.arsdigita.cms.contentassets.Note;

import java.io.IOException;

import org.apache.log4j.Logger;

public class NotesDisplay extends SimpleComponent {
    private static final Logger s_log = Logger.getLogger( NotesDisplay.class );

    private static final String DELETE = "delete";
    private static final String EDIT = "edit";
    private static final String UP = "up";
    private static final String DOWN = "down";

    private NotesStep m_step;
    private ACSObjectSelectionModel m_noteModel;

    public NotesDisplay( NotesStep step,
                         ACSObjectSelectionModel noteModel ) {
        super();

        m_step = step;
        m_noteModel = noteModel;
    }

    public void respond( PageState ps ) {
        String name = ps.getControlEventName();
        String value = ps.getControlEventValue();

        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Action " + name + " on note " + value );
        }

        OID oid = OID.valueOf( value );
        Note note = (Note) DomainObjectFactory.newInstance( oid );

        if( DELETE.equals( name ) ) {
            note.delete();
        }

        else if( EDIT.equals( name ) ) {
            m_noteModel.setSelectedObject( ps, note );
            m_step.showComponent( ps, NotesStep.EDIT );
        }

        else if( UP.equals( name ) ) {
            note.setRank( note.getRank() - 1 );
        }

        else if( DOWN.equals( name ) ) {
            note.setRank( note.getRank() + 1 );
        }

        ps.clearControlEvent();
        try {
            throw new RedirectSignal( ps.stateAsURL(), true );
        } catch( IOException ex ) {
            throw new UncheckedWrapperException( ex );
        }
    }

    public void generateXML( PageState ps, Element parent ) {
        Element root = parent.newChildElement( "cms:notesDisplay",
                                               CMS.CMS_XML_NS );

        ContentItem item = m_step.getItem( ps );
        DataCollection notes = Note.getNotes( item );

        if( s_log.isDebugEnabled() ) {
            s_log.debug( "NotesDisplay.generateXML" );
        }

        DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer( root );
        xr.setWrapRoot( true );
        xr.setWrapAttributes( true );
        xr.setWrapObjects( true );

        while( notes.next() ) {
            Note note = new Note( notes.getDataObject() );
            String oid = note.getOID().toString();

            Element edit = root.newChildElement( "cms:notesAction",
                                                 CMS.CMS_XML_NS );
            edit.addAttribute( "action", EDIT );
            edit.addAttribute( "oid", oid );
            ps.setControlEvent( this, EDIT, oid );
            try {
                edit.addAttribute( "href", ps.stateAsURL() );
            } catch( IOException ex ) {
                throw new UncheckedWrapperException( ex );
            }

            Element delete = root.newChildElement( "cms:notesAction",
                                                   CMS.CMS_XML_NS );
            delete.addAttribute( "action", DELETE );
            delete.addAttribute( "oid", oid );
            ps.setControlEvent( this, DELETE, oid );
            try {
                delete.addAttribute( "href", ps.stateAsURL() );
            } catch( IOException ex ) {
                throw new UncheckedWrapperException( ex );
            }

            Element up = root.newChildElement( "cms:notesAction",
                                               CMS.CMS_XML_NS );
            up.addAttribute( "action", UP );
            up.addAttribute( "oid", oid );
            ps.setControlEvent( this, UP, oid );
            try {
                up.addAttribute( "href", ps.stateAsURL() );
            } catch( IOException ex ) {
                throw new UncheckedWrapperException( ex );
            }

            Element down = root.newChildElement( "cms:notesAction",
                                                 CMS.CMS_XML_NS );
            down.addAttribute( "action", DOWN );
            down.addAttribute( "oid", oid );
            ps.setControlEvent( this, DOWN, oid );
            try {
                down.addAttribute( "href", ps.stateAsURL() );
            } catch( IOException ex ) {
                throw new UncheckedWrapperException( ex );
            }

            ps.clearControlEvent();
            
            xr.walk( note, SimpleXMLGenerator.ADAPTER_CONTEXT );
        }
    }
}
