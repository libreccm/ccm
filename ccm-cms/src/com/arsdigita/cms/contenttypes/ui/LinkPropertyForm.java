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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Embedded;
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
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Fieldset;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.cms.CMSConfig;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.util.GlobalizationUtil;

import java.util.TooManyListenersException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * Form to edit the basic properties of an Link. This form can be extended to create forms for Link
 * subclasses.
 *
 * @version $Revision: #5 $ $Date: 2004/08/17 $
 * @author Nobuko Asakai (nasakai@redhat.com)
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class LinkPropertyForm extends FormSection
    implements FormInitListener, FormProcessListener,
               FormValidationListener, FormSubmissionListener {
    
    private static final Logger s_log = Logger.getLogger(LinkPropertyForm.class);

    /**
     * Name of this form
     */
    public static final String ID = "link_edit";
    public static final String SSL_PROTOCOL = "https://";
    public static final String HTTP_PROTOCOL = "http://";
    private TextArea m_description;
    private TextField m_title;
    private TextField m_targetURI;
    private RadioGroup m_linkType;
    private CheckboxGroup m_URIOption;
    protected ItemSelectionModel m_itemModel;
    private LinkSelectionModel m_linkModel;
    private SaveCancelSection m_saveCancelSection;
    private ItemSearchWidget m_itemSearch;
    private TextField m_itemParams;
    private ContentType m_contentType;
    protected final String ITEM_SEARCH = "contentItem";

    /**
     * Constructor creates a new form to edit the Link object specified by the item selection model
     * passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the ContentItem to which this link
     *                  is (or will be) attached
     * @param link      The LinkSelectionModel to use to obtain the Link to work on
     */
    public LinkPropertyForm(ItemSelectionModel itemModel,
                            LinkSelectionModel link) {
        this(itemModel, link, null);
    }

    /**
     * Constructor creates a new form to edit the Link object specified by the item selection model
     * passed in.
     *
     * @param itemModel
     * @param link
     * @param contentType
     */
    public LinkPropertyForm(ItemSelectionModel itemModel,
                            LinkSelectionModel link,
                            ContentType contentType) {
        
        super(new ColumnPanel(2));
        
        s_log.debug("property form constructor");
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

        /* Add the standard title field                                       */
        m_title = new TextField("title");
        m_title.addValidationListener(new NotNullValidationListener());
        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.title")));
        add(m_title);

        /* Add the standard description field                                 */
        m_description = new TextArea("description");
        m_description.setCols(40);
        m_description.setRows(5);
        m_description.addValidationListener(new StringLengthValidationListener(CMSConfig
            .getInstanceOf().getLinkDescMaxLength()));
        add(new Label(GlobalizationUtil.globalize(
            "cms.contenttypes.ui.description")));
        add(m_description);

        //add(new Label(
        add(new Embedded(
            "<script language=\"javascript\">\n"
                + "<!-- \n"
                + "function toggle_link_fields(status) { \n"
                + "//  document.forms['linkEditForm'].targetURI.disabled = status; \n"
                + "//  document.forms['linkEditForm'].openOption.disabled = status; \n"
                + "//  document.forms['linkEditForm'].contentItem.disabled = !status; \n"
                + "//  document.forms['linkEditForm'].contentItem_search.disabled = !status; \n"
                + "//  document.forms['linkEditForm'].contentItem_clear.disabled = !status; \n"
                + "//  document.forms['linkEditForm'].itemParams.disabled = !status; \n"
                + "}\n"
                + "function enableUrlFields() { \n"
                + "  document.forms['linkEditForm'].targetURI.disabled = false; \n"
                + "  document.forms['linkEditForm'].openOption.disabled = false; \n"
                + "  document.forms['linkEditForm'].contentItem.disabled = true; \n"
                + "  document.forms['linkEditForm'].contentItem_search.disabled = true; \n"
                + "  //document.forms['linkEditForm'].contentItem_clear.disabled = true; \n"
                + "  document.forms['linkEditForm'].itemParams.disabled = true; \n"
                + "} \n"
                + "function enableItemFields() { \n"
                + "  document.forms['linkEditForm'].targetURI.disabled = true; \n"
                + "  document.forms['linkEditForm'].openOption.disabled = true; \n"
                + "  document.forms['linkEditForm'].contentItem.disabled = false; \n"
                + "  document.forms['linkEditForm'].contentItem_search.disabled = false; \n"
                + "  //document.forms['linkEditForm'].contentItem_clear.disabled = false; \n"
                + "  document.forms['linkEditForm'].itemParams.disabled = false; \n"
                + "} \n"
                + "// -->\n"
                + "</script>\n",
            false));

        /* Sub-title external URL / internal URL (content item)               */
        add(new Label(GlobalizationUtil.globalize(
            "cms.contenttyes.link.ui.link_type_subtitle"),
                      Label.BOLD),
            ColumnPanel.FULL_WIDTH);

        /* Option group to choose either external oder internal               */
        m_linkType = new RadioGroup("linkType");
        
        Option m_external = new Option(
            Link.EXTERNAL_LINK,
            new Label(GlobalizationUtil.globalize(
                    "cms.contenttyes.link.ui.option_group.link_type.external")));
        m_external.setOnClick("enableUrlFields()");
        
        Option m_internal = new Option(
            Link.INTERNAL_LINK,
            new Label(GlobalizationUtil.globalize(
                    "cms.contenttyes.link.ui.option_group.link_type.internal")));
        m_internal.setOnClick("enableItemFields()");
        
        m_linkType.addOption(m_external);
        m_linkType.addOption(m_internal);
        m_linkType.setOptionSelected(m_internal);
        m_linkType.addValidationListener(new NotNullValidationListener());
        add(new Label(GlobalizationUtil.globalize(
            "cms.contenttyes.link.ui.option_group.link_type.label")));
        add(m_linkType);

        /* External target  */
        Fieldset externalFieldset = new Fieldset(GlobalizationUtil.globalize(
            "cms.contenttyes.link.ui.target_uri"));
        externalFieldset.setClassAttr("externalLink autoHide");
        m_targetURI = new TextField("targetURI");
        m_targetURI.setOnFocus("toggle_link_fields(false)");
        m_targetURI.setHint(GlobalizationUtil.globalize(
            "cms.contenttyes.link.ui.target_uri_hint"));
        externalFieldset.add(new Label(GlobalizationUtil.globalize(
            "cms.contenttyes.link.ui.target_uri")));
        externalFieldset.add(m_targetURI);
        add(externalFieldset);

        /*  Internal target  */
        Fieldset internalFieldset = new Fieldset(GlobalizationUtil.globalize(
            "cms.contenttyes.link.ui.target_content_item"));
        internalFieldset.setClassAttr("internalLink autoHide");
        internalFieldset.add(new Label(GlobalizationUtil.globalize(
            "cms.contenttyes.link.ui.target_content_item")));
        m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, m_contentType);
        m_itemSearch.getSearchButton().setOnFocus("toggle_link_fields(true)");
        m_itemSearch.getClearButton().setOnFocus("toggle_link_fields(true)");
        internalFieldset.add(m_itemSearch);

        /*  Optional parameters for internal target  */
        internalFieldset.add(new Label(GlobalizationUtil.globalize(
            "cms.contenttyes.link.ui.target_parameters")));
        m_itemParams = new TextField("itemParams");
        m_itemParams.setOnFocus("toggle_link_fields(true)");
        m_itemParams.setHint(GlobalizationUtil.globalize(
            "cms.contenttyes.link.ui.target_parameters_hint"));
        internalFieldset.add(m_itemParams);
        add(internalFieldset);

//      TODO:
//      Move this option to contentasset related link for backwards compatibility
//      because this option is no longer compatible with current HTML
//      Requires database modification (move field target_window from
//      cms_links  to cms_related_links which shoud become ca_related_links
        /* Single option whether to open in new window, strongly discouraged!*/
        Option m_selectWindow = new Option(
            Link.TARGET_WINDOW,
            new Label(GlobalizationUtil.globalize(
                    "cms.contenttyes.link.ui.option.new_window")));
        //   "Open URL in new window");
        m_URIOption = new CheckboxGroup("openOption");
        m_URIOption.addOption(m_selectWindow);
        add(m_URIOption, ColumnPanel.FULL_WIDTH);

        //add(new Label(
        add(new Embedded(
            "<script language=\"javascript\">\n"
                + "<!-- \n"
                + "if (document.forms['linkEditForm'].linkType[0].checked) { \n"
                + "  //toggle_link_fields(false); \n"
                + "  enableUrlFields(); \n"
                + "} else { \n"
                + "  //toggle_link_fields(true); \n"
                + " enableItemFields(); \n"
                + "} \n"
                + "// -->\n"
                + "</script>\n",
            false), ColumnPanel.FULL_WIDTH);
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
            throw new FormProcessException(GlobalizationUtil.globalize("cms.contenttypes.ui.cancelled"));
        }
    }

    /**
     * Validation listener. Ensures consistency of internal vs. external link data
     *
     * @param event the FormSectionEvent
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void validate(FormSectionEvent event)
        throws FormProcessException {
        PageState state = event.getPageState();
        FormData data = event.getFormData();
        
        if (Link.EXTERNAL_LINK.equals((String) m_linkType.getValue(state))) {
            // The link is external, the URL must be valid and not null
            String externalURI = (String) m_targetURI.getValue(state);
            if (externalURI == null || externalURI.length() == 0) {
                throw new FormProcessException(GlobalizationUtil.globalize(
                    "cms.contenttypes.ui.requires_uri_field"));
            }
            
            String url = (String) m_targetURI.getValue(state);
            
            try {
                // See if it's a valid URL
                URL test = new URL(url);
            } catch (MalformedURLException ex) {
                boolean localLink = url.startsWith("/");
                boolean hasProtocol = url.indexOf("://") != -1;
                
                String newURL;
                if (localLink) {
                    // For a local link, see if it would be ok if we stuck
                    // "http://servername" on the front

                    newURL = HTTP_PROTOCOL + Web.getConfig().getHost()
                                 + url;
                } else if (!hasProtocol) {
                    // There's no protocol. See if it would be ok if we
                    // put one on the beginning

                    newURL = HTTP_PROTOCOL + url;
                } else {
                    // No idea, just throw the error
                    
                    // originally: "URL is not valid: " + ex.getMessage()
                    throw new FormProcessException(GlobalizationUtil.globalize("cms.contenttypes.ui.url_not_valid"));
                }
                
                try {
                    URL test = new URL(newURL);
                } catch (MalformedURLException ex2) {
                    StringBuilder msg = new StringBuilder();
                    
                    /* excluded by Tobias Osmers to change the FormProcessException
                    if (localLink) {
                        // For local link, report the error after we put a
                        // protocol and servername on it
                        msg.append("Local URL is not valid: ");
                        msg.append(ex2.getMessage());
                    } else {
                        // For external link, report the error before we tried
                        // to munge it
                        msg.append("External URL is not valid: ");
                        msg.append(ex.getMessage());
                    }
                    */
                    throw new FormProcessException(GlobalizationUtil.globalize("cms.contenttypes.ui.url_not_valid"));
                }

                // If we fixed it by adding a protocol, notify the user to
                // check that's what they intended
                if (!localLink && !hasProtocol) {
                    m_targetURI.setValue(state, newURL);
                    throw new FormProcessException(
                        "A valid URL starts with a protocol, eg http://");
                }
            }
        } else if (Link.INTERNAL_LINK.equals((String) m_linkType.getValue(
            state))) {
            // The link is internal, the item selected must be not null
            if (data.get(ITEM_SEARCH) == null) {
                throw new FormProcessException(
                    "Item selection is required for internal link.");
            }
            // Quasimodo
            // The target of the link must not be the same as the owner
            if (m_itemModel.getSelectedItem(state).getID().equals(
                ((ContentItem) data.get(ITEM_SEARCH)).getID())) {
                throw new FormProcessException("Link target is the same as this object.");
            }
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
        Assert.exists(item);
        Link link = new Link();
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
        setVisible(state, true);
        Link link;
        if (m_linkModel.isSelected(state)) {
            s_log.debug("Edit");
            link = m_linkModel.getSelectedLink(state);
            try {
                m_title.setValue(state, link.getTitle());
                m_description.setValue(state, link.getDescription());
                if ((link.getTargetURI() != null)
                        && link.getTargetURI().startsWith("&")) {
                    m_itemParams.setValue(state,
                                          link.getTargetURI().substring(1));
                } else {
                    m_targetURI.setValue(state, link.getTargetURI());
                }
                if (com.arsdigita.bebop.Link.NEW_FRAME.equals(link.getTargetWindow())) {
                    m_URIOption.setValue(state, Link.TARGET_WINDOW);
                } else {
                    m_URIOption.setValue(state, null);
                }
                m_linkType.setValue(state, link.getTargetType());
                if (Link.INTERNAL_LINK.equals(link.getTargetType())) {
                    data.put(ITEM_SEARCH, link.getTargetItem());
                }
                
            } catch (IllegalStateException e) {
                s_log.error(e.getMessage());
                throw e;
            }
        } else {
            // new link do nothing
            s_log.debug("new link");
            m_title.setValue(state, null);
            m_description.setValue(state, null);
            m_targetURI.setValue(state, null);
            m_URIOption.setValue(state, null);
            m_linkType.setValue(state, Link.EXTERNAL_LINK);
            data.put(ITEM_SEARCH, null);
        }
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
        Link link;

        // save only if save button was pressed
        if (getSaveCancelSection().getCancelButton().isSelected(state)) {
            // cancel button is selected
            m_linkModel.clearSelection(state);
            s_log.debug("link save canceled");
            
        } else {
            
            if (m_linkModel.isSelected(state)) {
                // Editing a link
                s_log.debug("processing link edit");
                link = m_linkModel.getSelectedLink(state);
            } else {
                s_log.debug("processing new link");
                link = createLink(state);
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
     * Set various properties of the Link.Child clases can over-ride this method to add additional
     * properties to Link.
     *
     * @param link
     * @param fse
     */
    protected void setLinkProperties(Link link, FormSectionEvent fse) {
        PageState state = fse.getPageState();
        FormData data = fse.getFormData();
        // * Set required properties *
        link.setTitle((String) m_title.getValue(state));
        link.setDescription((String) m_description.getValue(state));
        link.setTargetType((String) m_linkType.getValue(state));

        // Process internal and external urls
        if (Link.EXTERNAL_LINK.equals(m_linkType.getValue(state))) {
            link.setTargetURI(
                (String) m_targetURI.getValue(state));
            link.setTargetItem(null);
        } else {
            // Internal
            if (((String) m_itemParams.getValue(state)).isEmpty()) {
                link.setTargetURI(null);
            } else {
                link.setTargetURI(String.format("&%s", m_itemParams.getValue(
                                                state)));
            }

            // Quasimodo: BEGIN
            // This is part of the patch to make RelatedLink (and Link) 
            // multilanguage compatible.  Here we have to link to the 
            // content bundle instead of the content item, if there's one
            // else we don't have a proper multilanguage support'
            ContentItem ci = (ContentItem) data.get(ITEM_SEARCH);

            // If the selected target item ci has a parent of type ContentBundle
            if (ci.getParent() instanceof ContentBundle) {
                // Then there a multiple language versions of this content item 
                // and we want to link to the content bundle, so we can later 
                // negotiate the language depending on browser settings
                ci = (ContentItem) ci.getParent();
            }
            
            link.setTargetItem(ci);
        }
        // Process whether link is to be opened in new window
        boolean isNewWindow = false;
        String[] value = (String[]) m_URIOption.getValue(state);
        // Technically this isn't really necessary as there is only one box so any
        // non-null value means it was checked
        if (value != null) {
            isNewWindow = link.TARGET_WINDOW.equals(value[0]);
        }
        if (isNewWindow) {
            link.setTargetWindow(com.arsdigita.bebop.Link.NEW_FRAME);
        } else {
            link.setTargetWindow("");
        }
        
        link.save();
    }
    
}
