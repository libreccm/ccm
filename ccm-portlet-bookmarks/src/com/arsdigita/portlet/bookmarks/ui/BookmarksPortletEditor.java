/*
 * Copyright (C) 2005 Chris Gilbert  All Rights Reserved.
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
package com.arsdigita.portlet.bookmarks.ui;

import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.TooManyListenersException;


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.HorizontalLine;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.portal.PortletConfigFormSection;
import com.arsdigita.bebop.portal.PortletSelectionModel;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.dispatcher.CMSDispatcher;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portlet.bookmarks.Bookmark;
import com.arsdigita.portlet.bookmarks.BookmarkConstants;
import com.arsdigita.portlet.bookmarks.BookmarksPortlet;
import com.arsdigita.portlet.bookmarks.util.GlobalizationUtil;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;

import org.apache.log4j.Logger;

/**
 * Allows definition of new bookmarks to add to the portlet, and edit, move, 
 * deletion of existing bookmarks.
 *
 * @author cgyg9330
 */
public class BookmarksPortletEditor
             extends PortletConfigFormSection
             implements BookmarkConstants {

    private static final Logger s_log =
                         Logger.getLogger(BookmarksPortletEditor.class);

    private TextField m_title;
    private TextField m_description;
    private TextField m_url;
    private CheckboxGroup m_newWindow;

    private BookmarksTable m_existingBookmarks;
    private BigDecimalParameter m_selectedBookmark;
    private BookmarkSelectionModel m_bookmarkSelectionModel;
    private BigDecimalParameter m_selectedPortlet;
    private PortletSelectionModel m_portletSelectionModel;

	
    /**
     * 
     * @param application 
     */
    public BookmarksPortletEditor(RequestLocal application) {
        super(application);
    }

	
    /**
     * store item in requestlocal for access by various methods
     * 
     * code copied from content item portlet - > may be problems when items 
     * unpublished - check
     */
    private RequestLocal contentItem = new RequestLocal() {
        @Override
        protected Object initialValue(PageState ps) {
            String userURL = (String) m_url.getValue(ps);

            java.net.URL contextURL;
            try {
                contextURL = new java.net.URL(Web.getRequest().getRequestURL()
                                                              .toString());
            } catch (MalformedURLException ex) {
                throw new UncheckedWrapperException(ex);
            }

            java.net.URL url;
            try {
                url = new java.net.URL(contextURL, userURL);
            } catch (MalformedURLException ex) {
                s_log.info("Malformed URL " + userURL);
                return null;
            }

            String dp = URL.getDispatcherPath();
            String path = url.getPath();
            if (path.startsWith(dp)) {
                path = path.substring(dp.length());
            }

            StringTokenizer tok = new StringTokenizer(path, "/");
            if (!tok.hasMoreTokens()) {
                s_log.info("Couldn't find a content section for "
                            + path + " in " + userURL);
                return null;
            }

            String sectionPath = '/' + tok.nextToken() + '/';

            String context = ContentItem.LIVE;
            if (tok.hasMoreTokens()
                && CMSDispatcher.PREVIEW.equals(tok.nextToken())) {

                context = CMSDispatcher.PREVIEW;
            }

            ContentSectionCollection sections = ContentSection.getAllSections();
            sections.addEqualsFilter(Application.PRIMARY_URL, sectionPath);

            ContentSection section;
            if (sections.next()) {
                section = sections.getContentSection();
                sections.close();
            } else {
                s_log.info("Content section " + sectionPath + " in "
                           + userURL + " doesn't exist.");
                return null;
            }

            ItemResolver resolver = section.getItemResolver();

            path = path.substring(sectionPath.length());

            if (path.endsWith(".jsp")) {
                path = path.substring(0, path.length() - 4);
            }

            ContentItem item = resolver.getItem(section, path, context);
            if (item == null) {
                s_log.debug("Couldn't resolve item " + path);
                return null;
            }

            SecurityManager sm = new SecurityManager(item.getContentSection());

            boolean canRead = sm.canAccess(ps.getRequest(),
                                           SecurityManager.PUBLIC_PAGES,
                                           item);
            if (!canRead) {
                s_log.debug("User not allowed access to item");
                return null;
            }

            return item.getDraftVersion();
        }
    };


    /**
     * register the parameter that records the current selected bookmark
     */
    @Override
    public void register (Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_selectedBookmark);
        p.addComponentStateParam(this, m_selectedPortlet);
    }
	
    /**
     * 
     */
    @Override
    protected void addWidgets() {
        // create widgets
        m_url = new TextField(new StringParameter(Link.TARGET_URI));
        m_url.addValidationListener(new NotNullValidationListener());

        m_title = new TextField(new StringParameter(Link.DISPLAY_NAME));
        m_title.addValidationListener(new NotNullValidationListener());

        m_description = new TextField(new StringParameter(Link.DESCRIPTION));

        m_newWindow = new CheckboxGroup(Link.TARGET_WINDOW);
        m_newWindow.addOption(new Option(NEW_WINDOW_YES, new Label(
                GlobalizationUtil.globalize("bookmarks.new-window"))));
        try {
             m_newWindow.addPrintListener(new PrintListener() {

                 public void prepare(PrintEvent e) {
                     PageState state = e.getPageState();
                     CheckboxGroup newWindow = (CheckboxGroup)e.getTarget();
                     if (m_bookmarkSelectionModel.isSelected(state)) {
                         Link link = m_bookmarkSelectionModel.getSelectedLink(state);
                         newWindow.setValue(state, link.getTargetWindow());
                     }

                 }
        });
        } catch (IllegalArgumentException e) {
            s_log.warn("exception when trying to set checkbox value", e);
        } catch (TooManyListenersException e) {
            s_log.warn("exception when trying to set checkbox value", e);
        }


        m_selectedPortlet = new BigDecimalParameter("bookmark_portlet");
        m_portletSelectionModel = new PortletSelectionModel(m_selectedPortlet);
        m_selectedBookmark = new BigDecimalParameter("bookmark");
        m_bookmarkSelectionModel = new BookmarkSelectionModel(
                            "com.arsdigita.portlet.bookmarks.Bookmark", 
                            Bookmark.BASE_DATA_OBJECT_TYPE, m_selectedBookmark);
        m_existingBookmarks = new BookmarksTable(m_bookmarkSelectionModel, 
                                                 m_portletSelectionModel);

        super.addWidgets();

        add(new HorizontalLine(), ColumnPanel.FULL_WIDTH);


        add(m_existingBookmarks, ColumnPanel.FULL_WIDTH);
        add(new HorizontalLine(), ColumnPanel.FULL_WIDTH);

        add(new Label(GlobalizationUtil.globalize("bookmarks.add"), Label.BOLD), ColumnPanel.FULL_WIDTH);
        add(new Label(GlobalizationUtil.globalize("bookmarks.title"), Label.BOLD), ColumnPanel.RIGHT);
        add(m_title);
        add(new Label(GlobalizationUtil.globalize("bookmarks.description"), Label.BOLD), ColumnPanel.RIGHT);
        add(m_description);


        add(new Label(GlobalizationUtil.globalize("bookmarks.url"), Label.BOLD), ColumnPanel.RIGHT);
        add(m_url);
        add(new Label("")); // fill up the left hand column
        add(m_newWindow);

    }

    /**
     * specify current portlet for reference by table 
     * 
     * fill in values if user has selected edit on an existing bookmark
     */
    @Override
    protected void initWidgets(PageState state, Portlet portlet)
                   throws FormProcessException {
        s_log.debug("init widgets - set selected portlet to " + portlet.getOID());
        // set cached version as dirty here rather than during process 
        // in case an action link is pressed (eg to move links up/down)
        portlet.getPortletRenderer().invalidateCachedVersion(state);
        m_portletSelectionModel.setSelectedObject(state, portlet);
        super.initWidgets(state, portlet);

        if (m_bookmarkSelectionModel.isSelected(state)) {
            Bookmark link = m_bookmarkSelectionModel.getSelectedLink(state);
            m_url.setValue(state, BookmarksPortlet.getURIForBookmark(link, state));
            m_description.setValue(state, link.getDescription());
            m_title.setValue(state, link.getTitle());
        }
    }

    /**
     * Validates url if it looks like it is trying to be a content item 
     * but is failing
     */
    @Override
    public void validateWidgets(PageState state, Portlet portlet)
                throws FormProcessException {
        // m_selectedPortlet.set(state, portlet);
        super.validateWidgets(state, portlet);

        String fullUrl = (String) m_url.getValue(state);
        s_log.debug("fullURL = " + fullUrl);

        Object item = contentItem.get(state);
        URL here = URL.here(state.getRequest(), null);
        String thisSite = here.getServerURI();
        s_log.debug("This site is " + thisSite);
        if (item == null && fullUrl.indexOf(thisSite) != -1 
                         && fullUrl.indexOf("/ccm/") != -1 
                         && fullUrl.indexOf("/content/") != -1 ) {
            // not watertight, but is reasonable check that user is trying 
            // to specify a content item on this site
            throw new FormProcessException(GlobalizationUtil.globalize(
                    "bookmarks.error.content-item-not-found"));
        }

    }

    /**
     * add new bookmark to portlet, or amend existing one if we are editing 
     * a selected bookmark
     */
    @Override
    protected void processWidgets(PageState state, Portlet portlet)
                   throws FormProcessException {
        s_log.debug("START processWidgets");
        super.processWidgets(state, portlet);

        BookmarksPortlet myportlet = (BookmarksPortlet) portlet;
		
        String titleText = (String) m_title.getValue(state);
        String urlText = (String) m_url.getValue(state);
        String descriptionText = (String)m_description.getValue(state);

        ContentItem item = (ContentItem) contentItem.get(state);
        String[] newWindowValue = (String[]) m_newWindow.getValue(state);
        String newWindow = newWindowValue == null ? NEW_WINDOW_NO 
                                                  : NEW_WINDOW_YES;

        Bookmark newBookmark;
        if (m_bookmarkSelectionModel.isSelected(state)) {
            newBookmark = m_bookmarkSelectionModel.getSelectedLink(state);
        } else {
            newBookmark = new Bookmark();
            myportlet.addBookmark(newBookmark);
        }
        newBookmark.setTitle(titleText);
        newBookmark.setDescription(descriptionText);	
        newBookmark.setTargetWindow(newWindow);
        if (item == null) {
            newBookmark.setTargetType(Link.EXTERNAL_LINK);
            newBookmark.setTargetURI(urlText);
        } else {
            newBookmark.setTargetType(Link.INTERNAL_LINK);
            newBookmark.setTargetItem(item);
        }

        m_bookmarkSelectionModel.clearSelection(state);
        s_log.debug("END processWidgets");

    }

}
