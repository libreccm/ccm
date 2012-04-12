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

package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.SecurityPropertyEditor;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

public class NotesStep extends SecurityPropertyEditor {
    private ItemSelectionModel m_itemModel;
    private BigDecimalParameter m_noteParam;

    static final String EDIT = "edit";

    public NotesStep( ItemSelectionModel itemModel,
                      AuthoringKitWizard parent ) {
        m_itemModel = itemModel;

        m_noteParam = new BigDecimalParameter( "note" );
        ACSObjectSelectionModel noteModel =
            new ACSObjectSelectionModel( m_noteParam );

        NotesDisplay display = new NotesDisplay( this, noteModel );
        setDisplayComponent( display );

        NotesEdit edit = new NotesEdit( this, noteModel );
        WorkflowLockedComponentAccess editCA =
            new WorkflowLockedComponentAccess( edit, itemModel );
        addComponent( EDIT, "Add Note", editCA );

        addListeners( edit.getForm(), edit.getCancelButton() );
    }

    public ContentItem getItem( PageState ps ) {
        return m_itemModel.getSelectedItem( ps );
    }

    @Override
    public void register( Page p ) {
        super.register( p );

        p.addComponentStateParam( this, m_noteParam );
    }
}
