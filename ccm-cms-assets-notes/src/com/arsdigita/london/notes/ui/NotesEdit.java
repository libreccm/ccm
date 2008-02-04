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

package com.arsdigita.london.notes.ui;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

import com.arsdigita.london.notes.Note;

public class NotesEdit extends SimpleContainer {
    private Form m_form;
    private SaveCancelSection m_saveCancel;

    private ACSObjectSelectionModel m_noteModel;

    public NotesEdit( final NotesStep step,
                      ACSObjectSelectionModel noteModel ) {
        m_noteModel = noteModel;

        m_form = new Form( "notesEdit",
                           new SimpleContainer( "cms:notesEdit", CMS.CMS_XML_NS ) );
        m_form.setRedirecting( true );
        add( m_form );

        m_saveCancel = new SaveCancelSection();

        StringParameter contentParam = new StringParameter( "content" );
        contentParam.addParameterListener( new NotNullValidationListener() );

        final DHTMLEditor content = new DHTMLEditor( contentParam );
        content.setRows( 20 );

        m_form.add( content );
        m_form.add( m_saveCancel );

        m_form.addInitListener( new FormInitListener() {
            public void init( FormSectionEvent ev ) {
                PageState ps = ev.getPageState();

                Note note = getNote( ps );
                if( null == note ) return;

                content.setValue( ps, note.getContent() );
            }
        } );

        m_form.addSubmissionListener( new FormSubmissionListener() {
            public void submitted( FormSectionEvent ev ) {
                PageState ps = ev.getPageState();

                if( m_saveCancel.getCancelButton().isSelected( ps ) ) {
                    m_noteModel.clearSelection( ps );
                }
            }
        } );

        m_form.addProcessListener( new FormProcessListener() {
            public void process( FormSectionEvent ev ) {
                PageState ps = ev.getPageState();
                Note note = getNote( ps );
                if( null == note ) note = Note.create( step.getItem( ps ) );

                note.setContent( content.getValue( ps ).toString() );

                m_noteModel.clearSelection( ps );
            }
        } );
    }

    public Form getForm() {
        return m_form;
    }

    public Note getNote( PageState ps ) { 
        if( !m_noteModel.isSelected( ps ) ) return null;

        return (Note) m_noteModel.getSelectedObject( ps );
    }

    public Submit getCancelButton() {
        return m_saveCancel.getCancelButton();
    }
}
