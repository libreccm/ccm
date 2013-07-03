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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.workflow.WorkflowLockedContainer;

/**
 * Authoring step to create a link and change ordering. This class is
 * declared abstract, as this and related Link* base classes do not
 * assign the Links to a specific role/association. 
 * <code>RelatedLinkPropertiesStep</code> extends this functionality to 
 * view/assign RelatedLinks in the specific "links" role on ContentItem.
 */
public abstract class LinkPropertiesStep extends ResettableContainer {

    private AuthoringKitWizard m_parent;
    private ItemSelectionModel m_itemModel;
    private BigDecimalParameter m_linkParam = new BigDecimalParameter("link");
    private LinkSelectionModel m_linkModel = new LinkSelectionModel(m_linkParam);

    /**
     * Constructor. Creates a <code>LinkPropertiesStep</code> given an
     * <code>ItemSelectionModel</code>  and an
     * <code>AuthoringKitWizard</code>.
     *
     * @param itemModel The <code>ItemSelectionModel</code> for the current page.
     * @param parent The <code>AuthoringKitWizard</code> to track the
     * current link
     */
    public LinkPropertiesStep(ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {

        m_itemModel = itemModel;
        m_parent = parent;
        setLinkSelectionModel();
        add(getDisplayComponent());

        Form form = new Form("linkEditForm");
        form.add(getEditSheet());

        WorkflowLockedContainer edit = new WorkflowLockedContainer(itemModel);
        edit.add(form);
        add(edit);
    }

    /**
     * Sets the LinkSelectionModel for this authoring step. Subclasses
     * should override this method if a custom LinkSelectionModel is desired. 
     */
    protected void setLinkSelectionModel() {
        setLinkSelectionModel(new LinkSelectionModel(m_linkParam));
    }

    /**
     * Sets the LinkSelectionModel for this authoring step. 
     *
     * @param linkModel The <code>LinkSelectionModel</code> to use for
     * the authoring step
     */
    protected void setLinkSelectionModel(LinkSelectionModel linkModel) {
        m_linkModel = linkModel;
    }

    /**
     * Gets the LinkSelectionModel for this authoring step. 
     *
     * @return The <code>LinkSelectionModel</code> to use for
     * the authoring step
     */
    protected LinkSelectionModel getLinkSelectionModel() {
        return m_linkModel;
    }

    /**
     * Gets the ItemSelectionModel for this authoring step. 
     *
     * @return The <code>ItemSelectionModel</code> to use for
     * the authoring step
     */
    protected ItemSelectionModel getItemSelectionModel() {
        return m_itemModel;
    }

    /**
     * Gets the link parameter for this authoring step. 
     *
     * @return The link parameter to use for
     * the authoring step
     */
    protected BigDecimalParameter getLinkParam() {
        return m_linkParam;
    }

    /**
     * Gets the display compoent for this authoring step. 
     *
     * @return The display component to use for
     * the authoring step
     */
    public Component getDisplayComponent() {
        SimpleContainer container = new SimpleContainer();
        container.add(new LinkTable(m_itemModel, m_linkModel));
        return container;
    }

    /**
     * Gets the edit form
     *
     * @return The edit form
     */
    protected FormSection getEditSheet() {
        return new LinkPropertyForm(m_itemModel, m_linkModel);
    }

    /**
     * When this component is registered, the link parameter is added
     * as a ComponentStateParameter
     *
     * @param p The Page object
     */
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_linkParam);
    }
}
