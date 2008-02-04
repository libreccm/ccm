/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.contenttypes.ItemImageAttachment;

import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.SecurityPropertyEditor;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.OIDParameter;

import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Pluggable authoring step to add an ImageAsset to a content item
 * Currently only supports adding one image though the PDL has
 * association for multiple.
 */

public class ImageStep extends SecurityPropertyEditor {
    private static final Logger s_log = Logger.getLogger(ImageStep.class);

    private final ItemSelectionModel m_itemSelection;
    private final AttachmentSelectionModel m_attachmentSelection;
    private final AuthoringKitWizard m_parent;

    private final ImageStepDisplay m_display;
    private final ImageStepEdit m_add;

    private final OIDParameter m_attachmentOID;

    public ImageStep( ItemSelectionModel itemModel,
                      AuthoringKitWizard parent ) {
        super();

        m_itemSelection = itemModel;
        m_parent = parent;

        m_attachmentOID = new OIDParameter( "attachmentID" );
        m_attachmentSelection = new AttachmentSelectionModel();

        m_add = new ImageStepEdit( this );
        WorkflowLockedComponentAccess addCA =
            new WorkflowLockedComponentAccess( m_add, m_itemSelection );
        addComponent( "add", "Add Image", addCA );

        m_display = new ImageStepDisplay( this );
        setDisplayComponent(m_display);

        Iterator imageComponents = m_add.getImageComponents();
        while( imageComponents.hasNext() ) {
            ImageStepEdit.ImageComponent component =
                (ImageStepEdit.ImageComponent) imageComponents.next();

            addListeners( component.getForm(),
                          component.getSaveCancelSection().getCancelButton() );
        }

        m_parent.getList().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                PageState state = event.getPageState();
                showDisplayPane(state);
            }
        });
    }

    public void register( Page p ) {
        super.register( p );

        p.addComponentStateParam( this, m_attachmentOID );
    }

    /**
     * @return the parent wizard
     */
    public AuthoringKitWizard getParentWizard() {
        return m_parent;
    }

    /**
     * @return The item selection model
     */
    public ItemSelectionModel getItemSelectionModel() {
        return m_itemSelection;
    }

    /**
     * @return The currently selected item, null if there isn't one.
     */
    public ContentItem getItem( PageState ps ) {
        return m_itemSelection.getSelectedItem( ps );
    }

    /**
     * @return The currently selected item, null if there isn't one.
     */
    public ItemImageAttachment getAttachment( PageState ps ) {
        return (ItemImageAttachment)
            m_attachmentSelection.getSelectedAttachment( ps );
    }

    private class AttachmentSelectionModel
        extends AbstractSingleSelectionModel
    {
        private final RequestLocal m_attachment = new RequestLocal() {
            protected Object initialValue( PageState ps ) {
                OID oid = (OID) getSelectedKey( ps );
                if( null == oid ) return null;

                return DomainObjectFactory.newInstance( oid );
            }
        };

        public Object getSelectedKey( PageState ps ) {
            OID oid = (OID) ps.getValue( m_attachmentOID );
            if( null == oid ) return null;

            return oid;
        }

        public void setSelectedKey( PageState ps, Object oid ) {
            m_attachment.set( ps, null );
            ps.setValue( m_attachmentOID, oid );
        }

        public ItemImageAttachment getSelectedAttachment( PageState ps ) {
            return (ItemImageAttachment) m_attachment.get( ps );
        }

        public void setSelectedAttachment( PageState ps,
                                           ItemImageAttachment attachment ) {
            setSelectedKey( ps, attachment );
            m_attachment.set( ps, attachment );
        }

        public ParameterModel getStateParameter() {
            return m_attachmentOID;
        }
    }
}
