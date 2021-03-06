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
 *
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.*;
import com.arsdigita.cms.contenttypes.CustomizedPreviewLink;
import com.arsdigita.cms.dispatcher.CMSDispatcher;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.ui.authoring.WizardSelector;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.cms.ui.item.ItemLanguages;
import com.arsdigita.cms.ui.item.Summary;
import com.arsdigita.cms.ui.lifecycle.ItemLifecycleAdminPane;
import com.arsdigita.cms.ui.revision.ItemRevisionAdminPane;
import com.arsdigita.cms.ui.templates.ItemTemplates;
import com.arsdigita.cms.ui.workflow.ItemWorkflowAdminPane;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.OID;
import com.arsdigita.ui.DebugPanel;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Page for administering a content item.
 *
 * @author Michael Pih
 * @author Stanislav Freidin &lt;sfreidin@redhat.com&gt;
 * @author Jack Chung
 * @author Sören Bernstein <quasi@quasiweb.de>
 *
 * @version $Id: ContentItemPage.java 2245 2011-11-15 08:03:57Z pboy $
 */
public class ContentItemPage extends CMSPage implements ActionListener {

    /**
     * Private Logger instance for debugging purpose.
     */
    private static final Logger s_log = Logger.getLogger(ContentItemPage.class);
    /**
     * The URL parameter that must be passed in in order to set the current tab.
     * This is a KLUDGE right now because the TabbedDialog's current tab is
     * selected with a local state parameter
     */
    public static final String SET_TAB = "set_tab";
    /**
     * The name of the global state parameter that holds the item id
     */
    public static final String ITEM_ID = "item_id";
    /**
     * The name of the global state parameter which holds the return URL
     */
    public static final String RETURN_URL = "return_url";
    /**
     * The name of the global state parameter that determines whether or not to
     * use the streamlined authoring process (assuming the option is turned on).
     *
     */
    public static final String STREAMLINED_CREATION = "streamlined_creation";
    public static final String STREAMLINED_CREATION_ACTIVE = "active";
    public static final String STREAMLINED_CREATION_INACTIVE = "active";
    private static int s_tabOrder = 0;
    /**
     * Index of the summary tab
     */
    public static final int SUMMARY_TAB = s_tabOrder++;
    /**
     * <p>The name of the state parameter which indicates the content type of
     * the item the user wishes to create. or edit.</p>
     *
     * <p>The parameter must be a BigDecimalParameter which encodes the id of
     * the content type.</p>
     */
    public static final String CONTENT_TYPE = "content_type";
    public static final int AUTHORING_TAB = s_tabOrder++;
    public static final int LANGUAGE_TAB = s_tabOrder++;
    public static final int WORKFLOW_TAB = s_tabOrder++;
    public static final int PUBLISHING_TAB = s_tabOrder++;
    public static final int HISTORY_TAB = s_tabOrder++;
    public static final int TEMPLATES_TAB = s_tabOrder++;
    private final TabbedPane m_tabbedPane;
    private StringParameter m_returnURL;
    private ItemSelectionModel m_itemModel;
    private ACSObjectSelectionModel m_typeModel;
    private ContentItemRequestLocal m_item;
    private Summary m_summaryPane;
    private ItemWorkflowAdminPane m_workflowPane;
    private ItemLifecycleAdminPane m_lifecyclePane;
    private WizardSelector m_wizardPane;
    private ItemLanguages m_languagesPane;
    private ItemRevisionAdminPane m_revisionsPane;
    private ItemTemplates m_templatesPane;
    private Link m_previewLink;
    private GlobalNavigation m_globalNavigation;
    private ContentItemContextBar m_contextBar;

    private class ItemRequestLocal extends ContentItemRequestLocal {

        protected final Object initialValue(final PageState state) {
            return CMS.getContext().getContentItem();
        }
    }

    private class TitlePrinter implements PrintListener {

        public final void prepare(final PrintEvent e) {
            final Label label = (Label) e.getTarget();
            final ContentItem item = m_item.getContentItem(e.getPageState());

            label.setLabel(item.getDisplayName());
        }
    }

    /**
     * Constructs a new ContentItemPage.
     */
    public ContentItemPage() {
        super("", new SimpleContainer());

        m_item = new ItemRequestLocal();

        setClassAttr("cms-admin");
        setTitle(new Label(new TitlePrinter()));

        // Add the item id global state parameter
        BigDecimalParameter itemId = new BigDecimalParameter(ITEM_ID);
        itemId.addParameterListener(new NotNullValidationListener(ITEM_ID));
        addGlobalStateParam(itemId);
        m_itemModel = new ItemSelectionModel(itemId);

        // Add the content type global state parameter
        BigDecimalParameter contentType = new BigDecimalParameter(CONTENT_TYPE);
        addGlobalStateParam(contentType);

        // Add the streamlined creation global state parameter
        StringParameter streamlinedCreation = new StringParameter(
                STREAMLINED_CREATION);
        addGlobalStateParam(streamlinedCreation);

        m_typeModel = new ACSObjectSelectionModel(ContentType.class.getName(),
                ContentType.BASE_DATA_OBJECT_TYPE,
                contentType);

        // Validate the item ID parameter (caches the validation).
        getStateModel().addValidationListener(new FormValidationListener() {
            public void validate(FormSectionEvent event)
                    throws FormProcessException {
                validateItemID(event.getPageState());
            }
        });

        // Add the return url global state parameter
        m_returnURL = new StringParameter(RETURN_URL);
        addGlobalStateParam(m_returnURL);

        m_globalNavigation = new GlobalNavigation();
        add(m_globalNavigation);

        m_contextBar = new ContentItemContextBar(m_itemModel);
        add(m_contextBar);

        // Create panels.
        m_summaryPane = new Summary(m_itemModel);
        m_wizardPane = new WizardSelector(m_itemModel, m_typeModel);
        m_languagesPane = new ItemLanguages(m_itemModel);
        m_workflowPane = new ItemWorkflowAdminPane(itemId); // Make this use m_item XXX
        m_lifecyclePane = new ItemLifecycleAdminPane(m_item);
        m_revisionsPane = new ItemRevisionAdminPane(m_item);
        m_templatesPane = new ItemTemplates(m_itemModel);

        // Create tabbed pane.
        m_tabbedPane = new TabbedPane();
        add(m_tabbedPane);

        m_tabbedPane.setIdAttr("page-body");

        m_tabbedPane.addTab(new Label(gz("cms.ui.item.summary")), m_summaryPane);
        m_tabbedPane.addTab(new Label(gz("cms.ui.item.authoring")), m_wizardPane);
        m_tabbedPane.addTab(new Label(gz("cms.ui.item.languages")),
                m_languagesPane);
        m_tabbedPane.addTab(new Label(gz("cms.ui.item.workflow")),
                m_workflowPane);
        m_tabbedPane.addTab(new Label(gz("cms.ui.item.lifecycles")),
                m_lifecyclePane);
        m_tabbedPane.addTab(new Label(gz("cms.ui.item.history")),
                m_revisionsPane);
        m_tabbedPane.addTab(new Label(gz("cms.ui.item.templates")),
                m_templatesPane);

        m_tabbedPane.addActionListener(new ActionListener() {
            public final void actionPerformed(final ActionEvent e) {
                final PageState state = e.getPageState();
                final Component pane = m_tabbedPane.getCurrentPane(state);

                if (pane instanceof Resettable) {
                    ((Resettable) pane).reset(state);
                }
            }
        });

        // Build the preview link.
        m_previewLink = new Link(new Label(gz("cms.ui.preview")),
                new PrintListener() {
            public final void prepare(final PrintEvent e) {
                final Link link = (Link) e.getTarget();
                link.setTarget(getPreviewURL(e.getPageState()));
                link.setTargetFrame(Link.NEW_FRAME);
            }
        });
        m_previewLink.setIdAttr("preview_link");
        add(m_previewLink);

        addActionListener(this);

        // Add validation to make sure we are not attempting to edit a live item
        getStateModel().addValidationListener(new FormValidationListener() {
            public void validate(FormSectionEvent e) throws FormProcessException {
                PageState s = e.getPageState();
                FormData data = e.getFormData();
                final ContentItem item = m_item.getContentItem(s);
                if (item != null && ContentItem.LIVE.equals(item.getVersion())) {
                    s_log.error(String.format(
                            "The item %d is live and cannot be edited.", item.getID()));
                    //          data.addError(err);
                    throw new FormProcessException(GlobalizationUtil.globalize(
                            "cms.ui.live_item_not_editable"));
                }
            }
        });

        add(new DebugPanel());
    }

    /**
     * Ensures that the item_id parameter references a valid {@link
     * com.arsdigita.cms.ContentItem}.
     *
     * @param state The page state
     * @pre state != null
     * @exception FormProcessException if the item_id is not valid
     */
    protected void validateItemID(PageState state) throws FormProcessException {
        final ContentItem item = m_item.getContentItem(state);

        if (item == null) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                    "cms.ui.invalid_item_id"));
        }
    }

    /**
     * Fetch the request-local content section.
     *
     * @deprecated use com.arsdigita.cms.CMS.getContext().getContentSection()
     * instead
     * @param request The HTTP request
     * @return The current content section
     */
    @Override
    public ContentSection getContentSection(HttpServletRequest request) {
        // Resets all content sections associations.
        ContentSection section = super.getContentSection(request);
        Assert.exists(section);
        return section;
    }

    /**
     * Overrides CMSPage.getContentItem(PageState state) to get the current
     * content item from the page state.
     *
     * @deprecated Use the ItemSelectionModel
     * @param state The page state
     * @return The current content item, null if there is none
     */
    @Override
    public ContentItem getContentItem(PageState state) {
        return (ContentItem) m_itemModel.getSelectedObject(state);
    }

    /**
     * Set the current tab, if necessary
     */
    public void actionPerformed(ActionEvent event) {
        final PageState state = event.getPageState();
        final String setTab = state.getRequest().getParameter(SET_TAB);

        // Hide the templates tab, the workflow tab, and the preview
        // link if the current item is a template.
        final ContentItem item = m_item.getContentItem(state);

        if (item instanceof Template) {
            m_tabbedPane.setTabVisible(state, m_templatesPane, false);
            m_tabbedPane.setTabVisible(state, m_workflowPane, false);
            m_tabbedPane.setTabVisible(state, m_languagesPane, false);
            m_previewLink.setVisible(state, false);
        } else {
            m_tabbedPane.setTabVisible(state, m_templatesPane, !ContentSection.
                    getConfig().getHideTemplatesTab());
        }

        // Added by: Sören Bernstein <quasi@quasiweb.de>
        // If the content item is a language invariant content item, don't show
        // the language pane
        if (item instanceof LanguageInvariantContentItem) {
            LanguageInvariantContentItem li_item = (LanguageInvariantContentItem) item;
            if (li_item.isLanguageInvariant()) {
                m_tabbedPane.setTabVisible(state, m_languagesPane, false);
            }
        }

        // Set the current tab based on parameters
        if (setTab != null) {
            Integer tab = null;

            try {
                tab = Integer.valueOf(setTab);
            } catch (NumberFormatException e) {
                // Stop processing set_tab parameter.
                return;
            }

            if (tab.intValue() < m_tabbedPane.size()) {
                m_tabbedPane.setSelectedIndex(state, tab.intValue());
            }
        }
    }

    /**
     * Construct a URL for displaying a certain item
     *
     * @param nodeURL The URL where this page is mounted
     * @param itemId The id of the item to display
     * @param tab The index of the tab to display
     */
    public static String getItemURL(String nodeURL,
            BigDecimal itemId,
            int tab) {
        return getItemURL(nodeURL, itemId, tab, false);
    }

    /**
     * Construct a URL for displaying a certain item
     *
     * @param nodeURL The URL where this page is mounted
     * @param itemId The id of the item to display
     * @param tab The index of the tab to display
     * @param streamlinedCreation Whether to activate Streamlined item authoring
     */
    public static String getItemURL(String nodeURL,
            BigDecimal itemId,
            int tab,
            boolean streamlinedCreation) {
        StringBuffer url = new StringBuffer();

        url.append(nodeURL).append(PageLocations.ITEM_PAGE).append("?").append(
                ITEM_ID).append("=").append(itemId.toString()).append("&").
                append(SET_TAB).append("=").append(tab);

        if (streamlinedCreation && ContentSection.getConfig().
                getUseStreamlinedCreation()) {
            url.append("&").append(STREAMLINED_CREATION).append("=").append(
                    STREAMLINED_CREATION_ACTIVE);
        }

        return url.toString();
    }

    /**
     * @deprecated Use getItemURL instead
     */
    public static String getRelativeItemURL(BigDecimal itemId, int tab) {
        StringBuffer url = new StringBuffer();
        url.append(PageLocations.ITEM_PAGE).append("?").append(ITEM_ID).append("=").append(itemId.
                toString()).append("&").append(SET_TAB).append("=").append(tab);
        return url.toString();
    }

    /**
     * Constructs a URL for displaying a certain item.
     *
     * @param item the ContentItem object to display
     * @param tab The index of the tab to display
     */
    public static String getItemURL(ContentItem item, int tab) {
        final ContentSection section = item.getContentSection();

        if (section == null) {
            return null;
        } else {
            final String nodeURL = section.getPath() + "/";

            return getItemURL(nodeURL, item.getID(), tab);
        }
    }

    /**
     * Constructs a URL for displaying a certain item.
     *
     * @param itemId the id of the ContentItem object to display
     * @param tab The index of the tab to display
     */
    public static String getItemURL(BigDecimal itemId, int tab) {
        final ContentItem item =
                (ContentItem) DomainObjectFactory.newInstance(new OID(
                ContentItem.BASE_DATA_OBJECT_TYPE, itemId));

        if (item == null) {
            return null;
        } else {
            return getItemURL(item, tab);
        }
    }

    /**
     * Redirect back to wherever the user came from, using the value of the
     * return_url parameter.
     *
     * @param state The current page state
     */
    public void redirectBack(PageState state) {
        try {
            String returnURL = (String) state.getValue(m_returnURL);
            state.getResponse().sendRedirect(returnURL);
        } catch (IOException e) {
            s_log.error("IO Error redirecting back", e);
            // do nothing
        }
    }

    /**
     * Fetch the preview URL.
     */
    private String getPreviewURL(PageState state) {
        final ContentItem item = m_item.getContentItem(state);

        if (item instanceof CustomizedPreviewLink) {
            final String previewLink = ((CustomizedPreviewLink) item).
                    getPreviewUrl(
                    state);
            if ((previewLink == null) || previewLink.isEmpty()) {
                return getDefaultPreviewLink(state, item);
            } else {
                return previewLink;
            }
        } else {
            return getDefaultPreviewLink(state, item);
        }
    }

    /**
     *
     * @param state
     * @param item
     * @return
     */
    private String getDefaultPreviewLink(final PageState state,
            final ContentItem item) {
        final ContentSection section = CMS.getContext().getContentSection();
        //ContentSection section = getContentSection(state);
        final ItemResolver itemResolver = section.getItemResolver();

        // Pass in the "Live" context since we need it for the preview
        return itemResolver.generateItemURL(state, item, section,
                CMSDispatcher.PREVIEW);
    }

    protected final static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    protected final static String lz(final String key) {
        return (String) gz(key).localize();
    }

    public static boolean isStreamlinedCreationActive(PageState state) {
        return ContentSection.getConfig().getUseStreamlinedCreation()
                && STREAMLINED_CREATION_ACTIVE.equals(state.getRequest().
                getParameter(STREAMLINED_CREATION));
    }

    protected TabbedPane getTabbedPane() {
        return m_tabbedPane;
    }

    protected WizardSelector getWizardPane() {
        return m_wizardPane;
    }

    /**
     * Adds the content type to the output.
     * 
     * @param state PageState
     * @param parent Parent document
     * @return page
     */
    protected Element generateXMLHelper(PageState state, Document parent) {
        Element page = super.generateXMLHelper(state, parent);
        Element contenttype = page.newChildElement("bebop:contentType", BEBOP_XML_NS);
        contenttype.setText(m_item.getContentItem(state).getContentType().getName());

        return page;
    }
}
