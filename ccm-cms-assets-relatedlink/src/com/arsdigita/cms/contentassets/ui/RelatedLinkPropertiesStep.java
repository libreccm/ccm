/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.contentassets.RelatedLinkConfig;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contentassets.util.RelatedLinkGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.contenttypes.ui.LinkPropertiesStep;
import com.arsdigita.cms.contenttypes.ui.LinkTable;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.ui.workflow.WorkflowLockedContainer;
import com.arsdigita.persistence.DataCollection;

/**
 * Authoring step to create a RelatedLink and change ordering.
 *
 * It is just a front end to the cms Link asset and makes RelatedLink accessible
 * as installable add related link authoring step
 */
public class RelatedLinkPropertiesStep extends LinkPropertiesStep {

    protected String linkListName;
    protected ContentType contentType;
    private SaveCancelSection m_saveCancelSection;
    private RelatedLinkPropertyForm m_RelatedLinkPropertyForm;
    private RelatedLinkCaptionForm m_RelatedLinkCaptionForm;
    private RelatedLinkTable m_linkList;

    /**
     * Constructor. Creates a <code>RelatedLinkPropertiesStep</code> given an
     * <code>ItemSelectionModel</code> and an <code>AuthoringKitWizard</code>.
     *
     * @param itemModel The <code>ItemSelectionModel</code> for the current
     * page.
     * @param parent The <code>AuthoringKitWizard</code> to track the current
     * link
     */
    public RelatedLinkPropertiesStep(ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        super(itemModel, parent);

        // Reset the editing when this component becomes visible
        parent.getList().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                PageState state = event.getPageState();
                showDisplayPane(state);
            }
        });
    }


    @Override
    protected void addForms() {

        m_Form = new RelatedLinkPropertyForm(m_itemModel, m_linkModel, linkListName);
        add("addlink", RelatedLinkGlobalizationUtil.globalize("cms.contentassets.ui.related_link.add_link"),
                new WorkflowLockedComponentAccess(m_Form, m_itemModel),
                m_Form.getSaveCancelSection().getCancelButton());

        m_RelatedLinkCaptionForm = new RelatedLinkCaptionForm(m_itemModel, m_linkModel, linkListName);
        add("caption", RelatedLinkGlobalizationUtil.globalize(
                "cms.contentassets.ui.related_link.add_caption"),
                new WorkflowLockedComponentAccess(m_RelatedLinkCaptionForm, m_itemModel),
                m_RelatedLinkCaptionForm.getSaveCancelSection().getCancelButton());

        // the link edit form
        Form linkform = new Form("linkEditForm");
        linkform.add(getEditSheet());

        WorkflowLockedContainer edit = new WorkflowLockedContainer(m_itemModel);
        edit.add(linkform);
        add(edit);

        //the caption edit form 
        Form captionform = new Form("captionEditForm");
        captionform.add(getcaptionSheet());

        WorkflowLockedContainer captionEdit = new WorkflowLockedContainer(m_itemModel);
        captionEdit.add(captionform);
        add(captionEdit);

    }

    @Override
    protected void addTable() {
        m_linkList = new RelatedLinkTable(m_itemModel, m_linkModel, linkListName);
        Label mainLabel = new Label("bla");
        mainLabel.setFontWeight(Label.ITALIC);
        mainLabel.addPrintListener(new PrintListener() {
            public void prepare(PrintEvent event) {
                PageState state = event.getPageState();
                ContentItem item = (ContentItem) m_itemModel.getSelectedObject(state);
                if (item != null) {
                    DataCollection rlinks = RelatedLink.getRelatedLinks(item);
                    Label mainTarget = (Label) event.getTarget();
                    if (rlinks.isEmpty()) {
                        mainTarget.setLabel(
                                "no RELATEDLinks");
                    } else {
                        mainTarget.setLabel("");
                    }
                }
            }
        });
        m_display.add(mainLabel);
        m_display.add(m_linkList);
    }

    /**
     * Sets a RelatedLinkSelectionModel as the LinkSelectionModel for this
     * authoring step. Also, set the linkListName and contentType if neccessary
     */
    @Override
    protected void setLinkSelectionModel() {
        linkListName = "NONE";
        setLinkSelectionModel(new RelatedLinkSelectionModel(getLinkParam()));
    }

    /**
     * Returns a RelatedLinkTable as the display component for this authoring
     * step.
     *
     * Uses CMS LinkTable and its display facilities.
     *
     * @return The display component to use for the authoring step
     */
    @Override
    public Component getDisplayComponent() {

        SimpleContainer container = new SimpleContainer();

        LinkTable table;
        if (RelatedLinkConfig.getInstance().isHideAdditionalResourceFields()) {
            // CMS LinkTable it it's standard form
            table = new LinkTable(getItemSelectionModel(),
                    getLinkSelectionModel());
            table.setModelBuilder(new RelatedLinkTableModelBuilder(getItemSelectionModel(),
                    linkListName));
        } else {
            // Add columns to standard CMS LinkTable
            table = new RelatedLinkTable(getItemSelectionModel(),
                    getLinkSelectionModel(),
                    linkListName);
        }

        container.add(table);
        return container;
    }


    /**
     * Retrieves the saveCancelSection.
     *
     * @return Save/Cencel section
     */
    public SaveCancelSection getSaveCancelSection() {
        m_saveCancelSection = new SaveCancelSection();
//        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        return m_saveCancelSection;
    }

//    public String getLinkListName() {
//        return linkListName;
//
//    }
    
    /**
     * Gets the edit form for related links
     *
     * @return The edit form
     */
//    @Override
//    protected FormSection getEditSheet() {
//        return new RelatedLinkPropertyForm(m_itemModel, m_linkModel, linkListName);
//    }

    /**
     * Gets the edit form for captions
     *
     * @return The edit form
     */
    protected FormSection getcaptionSheet() {
        return new RelatedLinkCaptionEditForm(m_itemModel, m_linkModel, linkListName);
    }
    
        /**
     * Gets the edit form (a RelatedLinkPropertyForm)
     *
     * @return The edit form
     */
    @Override
    protected FormSection getEditSheet() {
        return new RelatedLinkEditForm(getItemSelectionModel(),
                getLinkSelectionModel(), linkListName);
    }
    
}
