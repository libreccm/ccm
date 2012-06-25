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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.CMSConfig;
import com.arsdigita.cms.CMSExcursion;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Document;
import com.arsdigita.web.Web;
import com.arsdigita.web.Application;
import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>The Item Search page.</p>
 *
 * @author Scott Seago (scott@arsdigita.com)
 * @author Sören Bernstein (sbernstein@quasiweb.de)
 */
public class ItemSearchPage extends CMSPage {

    private final static String XSL_CLASS = "CMS Admin";
    private TabbedPane m_tabbedPane;
    private ItemSearchFlatBrowsePane m_flatBrowse;
    private ItemSearchBrowsePane m_browse;
    private ItemSearchPopup m_search;
    //private ItemSearchCreateItemPane m_create;
    private BigDecimalParameter m_sectionId;
    private static final CMSConfig s_conf = CMSConfig.getInstance();
    private static final boolean LIMIT_TO_CONTENT_SECTION = false;
    public static final String CONTENT_SECTION = "section_id";
    private final boolean showFlatBrowsePane;

    /**
     * Construct a new ItemSearchPage
     */
    public ItemSearchPage() {
        super(GlobalizationUtil.globalize("cms.ui.item_search.page_title").localize().toString(), new SimpleContainer());

        setClassAttr("cms-admin");

        addGlobalStateParam(new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM));
        addGlobalStateParam(new StringParameter(ItemSearchPopup.WIDGET_PARAM));
        addGlobalStateParam(new StringParameter("searchWidget"));

        showFlatBrowsePane = s_conf.getItemSearchFlatBrowsePaneEnable();


        m_sectionId = new BigDecimalParameter(CONTENT_SECTION);
        addGlobalStateParam(m_sectionId);

        m_browse = getBrowsePane();
        if (showFlatBrowsePane) {
            m_flatBrowse = getFlatBrowsePane();
        }
        m_search = getSearchPane();
//        m_create = getCreatePane();

        m_tabbedPane = createTabbedPane();
        m_tabbedPane.setIdAttr("page-body");
        add(m_tabbedPane);
        addRequestListener(new RequestListener() {

            public void pageRequested(final RequestEvent event) {
                final PageState state = event.getPageState();

                //if (showFlatBrowsePane) {
                //    m_tabbedPane.setTabVisible(state, 0, false);
              //     m_tabbedPane.setSelectedIndex(state, 1);
                //}
            }

        });
    }

    /**
     * Creates, and then caches, the Browse pane. Overriding this method to return null will prevent this tab from
     * appearing. Note: not implemented yet.
     */
    protected ItemSearchBrowsePane getBrowsePane() {
        if (m_browse == null) {
            m_browse = new ItemSearchBrowsePane();
        }

        return m_browse;
    }

    protected ItemSearchFlatBrowsePane getFlatBrowsePane() {
        if (m_flatBrowse == null) {
            m_flatBrowse = new ItemSearchFlatBrowsePane("flatBrowse");
        }

        return m_flatBrowse;
    }

    /**
     * Creates, and then caches, the Creation pane. Overriding this method to return null will prevent this tab from
     * appearing.
     */
    protected ItemSearchPopup getSearchPane() {
        if (m_search == null) {
            // Always search in every content section
//            m_search = new ItemSearchPopup(ContentItem.DRAFT, CMS.getConfig().limitToContentSection());
            m_search = new ItemSearchPopup(ContentItem.DRAFT, LIMIT_TO_CONTENT_SECTION);
        }

        return m_search;
    }

//    protected ItemSearchCreateItemPane getCreatePane() {
//        if(m_create == null) {
//            m_create = new ItemSearchCreateItemPane();
//        }
//
//        return m_create;
//    }
    /**
     * Created the TabbedPane to use for this page. Sets the class attribute for this tabbed pane. The default
     * implementation uses a
     * {@link com.arsdigita.bebop.TabbedPane} and sets the class attribute to "CMS Admin." This implementation also adds
     * tasks, content sections, and search panes.
     *
     * Developers can override this method to add only the tabs they want, or to add additional tabs after the default
     * CMS tabs are added.
     */
    protected TabbedPane createTabbedPane() {
        TabbedPane pane = new TabbedPane();
        pane.setClassAttr(XSL_CLASS);

        if (showFlatBrowsePane) {
            addToPane(pane, "flatBrowse", getFlatBrowsePane());
        }
        addToPane(pane, "browse", getBrowsePane());
        addToPane(pane, "search", getSearchPane());
//        addToPane(pane, "create", getCreatePane());

        if ("browse".equals(s_conf.getItemSearchDefaultTab())) {
            pane.setDefaultPane(m_browse);
        }
        if ("search".equals(s_conf.getItemSearchDefaultTab())) {
            pane.setDefaultPane(m_search);
        }
        if ("flatBrowse".equals(s_conf.getItemSearchDefaultTab()) && showFlatBrowsePane) {
            pane.setDefaultPane(m_flatBrowse);
        } else {
            pane.setDefaultPane(m_browse);
        }

        //pane.addActionListener(this);
//        pane.setTabVisible(null, pane, true);
        return pane;
    }

    /**
     * Adds the specified component, with the specified tab name, to the tabbed pane only if it is not null.
     *
     * @param pane    The pane to which to add the tab
     * @param tabName The name of the tab if it's added
     * @param comp    The component to add to the pane
     */
    protected void addToPane(TabbedPane pane, String tabName, Component comp) {
        if (comp != null) {
            pane.addTab(GlobalizationUtil.globalize("cms.ui.item_search." + tabName).localize().toString(), comp);
        }
    }

    /**
     * When a new tab is selected, reset the state of the formerly-selected pane.
     *
     * @param event The event fired by selecting a tab
     */
    //public void actionPerformed(ActionEvent event) {
    //PageState state = event.getPageState();
    //Component pane = m_tabbedPane.getCurrentPane(state);
    //if ( pane == m_browse ) {
    // MP: reset tasks pane
    //} else if ( pane == m_search ) {
    //m_search.reset(state);
    //}
    //}
    /**
     * This strange voodoo from Dan. No idea what it does.
     */
    @Override
    public void dispatch(final HttpServletRequest request,
                         final HttpServletResponse response,
                         RequestContext actx)
            throws IOException, ServletException {
        new CMSExcursion() {

            @Override
            public void excurse()
                    throws IOException, ServletException {
                ContentSection section = null;
                Application app = Web.getContext().getApplication();
                if (app instanceof ContentSection) {
                    section = (ContentSection) app;
                } else {
                    try {
                        section = new ContentSection((BigDecimal) m_sectionId.transformValue(request));
                    } catch (DataObjectNotFoundException ex) {
                        throw new UncheckedWrapperException(ex);
                    }
                }
                setContentSection(section);

                final Document doc = buildDocument(request, response);
                final PresentationManager pm =
                                          Templating.getPresentationManager();

                pm.servePage(doc, request, response);
            }

        }.run();
    }

}
