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
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.cms.CMSConfig;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contentassets.util.RelatedLinkGlobalizationUtil;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.contenttypes.ui.LinkSelectionModel;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataCollection;

import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

/**
 * Form to edit captions. based on LinkPropertyForm and RelatedLinkPropertyForm
 *
 * @version $Revision: 1# $ $Date: 2015/04/13 $
 * @author konerman (konerman@tzi.de)
 */
public class RelatedLinkCaptionForm extends FormSection
    implements FormInitListener, FormProcessListener,
               FormValidationListener, FormSubmissionListener {

    private static final Logger s_log = Logger.getLogger(
        RelatedLinkCaptionForm.class);

    /**
     * Name of this form
     */
    public static final String ID = "caption";
    public static final String SSL_PROTOCOL = "https://";
    public static final String HTTP_PROTOCOL = "http://";
    protected TextArea m_description;
    //protected TextArea m_title;
    protected TextField m_title;
    protected ItemSelectionModel m_itemModel;
    protected LinkSelectionModel m_linkModel;
    private SaveCancelSection m_saveCancelSection;
    protected final String ITEM_SEARCH = "contentItem";
    private ContentType m_contentType;
    private String m_linkListName;

    /**
     * Constructor creates a new form to edit the Link object specified by the
     * item selection model passed in.
     *
     * @param itemModel    The ItemSelectionModel to use to obtain the
     *                     ContentItem to which this link is (or will be)
     *                     attached
     * @param link         The LinkSelectionModel to use to obtain the Link to
     *                     work on
     * @param linkListName
     */
    public RelatedLinkCaptionForm(ItemSelectionModel itemModel,
                                  LinkSelectionModel link, String linkListName) {
        this(itemModel, link, linkListName, null);
    }

    /**
     * Constructor creates a new form to edit the Link object specified by the
     * item selection model passed in.
     *
     * @param itemModel
     * @param link
     * @param contentType
     */
    public RelatedLinkCaptionForm(ItemSelectionModel itemModel,
                                  LinkSelectionModel link, String linkListName,
                                  ContentType contentType) {
        super(new ColumnPanel(2));
        m_linkListName = linkListName;

        s_log.debug("caption form constructor");
        m_linkModel = link;
        m_itemModel = itemModel;
        m_contentType = contentType;

        addWidgets();
        addSaveCancelSection();

        addInitListener(this);

        addValidationListener(this);

        addProcessListener(this);
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {
        //m_title = new DHTMLEditor("captiontitle");
        m_title = new TextField("captiontitle");
        add(new Label(RelatedLinkGlobalizationUtil.globalize(
            "cms.contentassets.ui.related_link.title")));
        add(m_title);

        /* Add the standard description field                                 */
        m_description = new DHTMLEditor("description");
//        m_description.addValidationListener(new NotNullValidationListener());
        m_description.addValidationListener(new StringLengthValidationListener(
            CMSConfig
            .getInstanceOf().getLinkDescMaxLength()));
        add(new Label(RelatedLinkGlobalizationUtil.globalize(
            "cms.contentassets.ui.related_link.Description")));
        add(m_description);

    }

    /**
     * Adds the saveCancelSection
     */
    public void addSaveCancelSection() {
        m_saveCancelSection = new SaveCancelSection();
        try {
            m_saveCancelSection.getCancelButton().addPrintListener(
                new PrintListener() {

                    @Override
                    public void prepare(PrintEvent e) {
                        Submit target = (Submit) e.getTarget();
                        if (m_linkModel.isSelected(e.getPageState())) {
                            target.setButtonLabel(GlobalizationUtil.globalize(
                                    "cms.contenttyes.link.ui.button_cancel"));
                        } else {
                            target.setButtonLabel(GlobalizationUtil.globalize(
                                    "cms.contenttyes.link.ui.button_reset"));
                        }
                    }

                });
            m_saveCancelSection.getSaveButton().addPrintListener(
                new PrintListener() {

                    @Override
                    public void prepare(PrintEvent e) {
                        Submit target = (Submit) e.getTarget();
                        if (m_linkModel.isSelected(e.getPageState())) {
                            target.setButtonLabel(GlobalizationUtil.globalize(
                                    "cms.contenttyes.link.ui.button_save"));
                        } else {
                            target.setButtonLabel(GlobalizationUtil.globalize(
                                    "cms.contenttyes.link.ui.button_create"));
                        }
                    }

                });
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException("this cannot happen", e);
        }
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH);
    }

    /**
     * Retrieves the saveCancelSection.
     *
     * @return Save/Cencel section
     */
    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancelSection;
    }

    /**
     * return selection model for Link that we are dealing with.
     */
    protected LinkSelectionModel getLinkSelectionModel() {
        return m_linkModel;
    }

    /**
     * Submission listener. Handles cancel events.
     *
     * @param e the FormSectionEvent
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void submitted(FormSectionEvent e)
        throws FormProcessException {
        if (m_saveCancelSection.getCancelButton().isSelected(e.getPageState())) {
            s_log.debug("cancel in submission listener");
            m_linkModel.clearSelection(e.getPageState());
            init(e);
            throw new FormProcessException(
                GlobalizationUtil.globalize("cms.contenttypes.ui.cancelled"));
        }
    }

    /**
     * Validation listener. Ensures consistency of internal vs. external link
     * data
     *
     * @param event the FormSectionEvent
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void validate(FormSectionEvent event)
        throws FormProcessException {

        // test if the user has made an input
        PageState state = event.getPageState();
        String title = (String) m_title.getValue(state);
        String desc = (String) m_description.getValue(state);
        if ((title.length() + desc.length()) <= 0) {
            throw new FormProcessException(RelatedLinkGlobalizationUtil
                .globalize(
                    "cms.contentassets.ui.related_link.input_mandatory"));
        }
    }

    /**
     * Get the current ContentItem
     *
     * @param s the PageState
     *
     * @return the ContentItem
     */
    protected ContentItem getContentItem(PageState s) {
        return (ContentItem) m_itemModel.getSelectedObject(s);
    }

    /**
     * Take care of basic Link creation steps
     *
     * @param s the PageState
     *
     * @return the newly-created Link
     */
    protected Link createLink(PageState s) {
        ContentItem item = getContentItem(s);
        Assert.exists(item, ContentItem.class);
        RelatedLink link = new RelatedLink();

        // remove the following line if we make Link extend ACSObject
        //link.setName(item.getName() + "_link_" + item.getID());
        // set the owner of the link
        link.setLinkOwner(item);

        return link;
    }

    /**
     * Init listener. For edit actions, fills the form with current data
     *
     * @param fse the FormSectionEvent
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        s_log.debug("Init");
        s_log.debug("new link");
        m_description.setValue(state, null);
        m_title.setValue(state, null);
    }

    /**
     * Process listener. Saves/creates the new or modified Link
     *
     * @param fse the FormSectionEvent
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();
        RelatedLink link;

        // save only if save button was pressed
        if (getSaveCancelSection().getCancelButton().isSelected(state)) {
            // cancel button is selected
            m_linkModel.clearSelection(state);
            s_log.debug("link save canceled");

        } else {

            if (m_linkModel.isSelected(state)) {
                // Editing a link
                s_log.debug("processing link edit");
                link = (RelatedLink) m_linkModel.getSelectedLink(state);
            } else {
                s_log.debug("processing new link");
                link = (RelatedLink) createLink(state);
            }
            //call to set various properties of Link.
            setLinkProperties(link, fse);
            s_log.debug("Created Link with ID: " + link.getOID().toString()
                            + "Title " + link.getTitle());
        }
        // XXX Initialize the form
        m_linkModel.clearSelection(state);
        init(fse);
    }

    /**
     * Set various properties of the Link.Child clases can over-ride this method
     * to add additional properties to Link.
     *
     * @param link
     * @param fse
     */
    protected void setLinkProperties(RelatedLink link, FormSectionEvent fse) {
        PageState state = fse.getPageState();
        FormData data = fse.getFormData();

        String title = (String) m_title.getValue(state);
        if (!title.isEmpty()) {
            link.setTitle(title);
        } else {
            //if user did not typed in a title
            link.setTitle(" ");
        }
        link.setDescription((String) m_description.getValue(state));
        link.setTargetType(RelatedLink.EXTERNAL_LINK);
        link.setTargetURI("caption");
        link.setTargetWindow("");
        link.setResourceSize("");
        link.setResourceType(MimeType.loadMimeType("text/html"));
        link.setTargetItem(null);
        link.setLinkListName(m_linkListName);
        DataCollection links = RelatedLink.getRelatedLinks(
            getContentItem(fse.getPageState()),
            m_linkListName);
        //Only change link order if we are creating a new link
        if (!getLinkSelectionModel().isSelected(fse.getPageState())) {
            link.setOrder((int) links.size() + 1);
        }

        link.save();
    }

}
