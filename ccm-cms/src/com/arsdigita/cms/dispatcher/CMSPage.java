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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.bebop.Bebop;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.page.PageTransformer;
import com.arsdigita.cms.CMSExcursion;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionServlet;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.OID;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * <p>A <tt>CMSPage</tt> is a Bebop {@link com.arsdigita.bebop.Page}
 * implementation of the {@link com.arsdigita.cms.dispatcher.ResourceHandler}
 * interface.</p>
 *
 * <p>It stores the current {@link com.arsdigita.cms.ContentSection} and, if
 * applicable, the {@link com.arsdigita.cms.ContentItem} in the page state as
 * request local objects. Components that are part of the <tt>CMSPage</tt>
 * may access these objects by calling:</p>
 *     <blockquote><code><pre>
 *     getContentSection(PageState state);
 *     </pre></code></blockquote>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Uday Mathur (umathur@arsdigita.com)
 * @version $Id: CMSPage.java 2140 2011-01-16 12:04:20Z pboy $
 */
public class CMSPage extends Page implements ResourceHandler {

    private static final Logger s_log = Logger.getLogger(CMSPage.class);

    /** The global assets URL stub XML parameter name.    */
    public final static String ASSETS = "ASSETS";

    /** The XML page class.     */
    public final static String PAGE_CLASS = "CMS";

    /** Map of XML parameters   */
    private HashMap m_params;

    /**    */
    private PageTransformer m_transformer;

    public CMSPage() {
        super();
        buildPage();
    }

    public CMSPage(String title) {
        super(title);
        buildPage();
    }

    public CMSPage(String title, Container panel) {
        super(title, panel);
        buildPage();
    }

    public CMSPage(Label title) {
        super(title);
        buildPage();
    }

    public CMSPage(Label title, Container panel) {
        super(title, panel);
        buildPage();
    }

    /**
     * Builds the page.
     */
    protected void buildPage() {
        // Set the class attribute value. May be overwritten by child class
        // to hold a more specific value
        setClassAttr(PAGE_CLASS);

        // Global XML params.
        // MP: This only works with older versions of Xalan.
        m_params = new HashMap();
        setXMLParameter(ASSETS, Utilities.getGlobalAssetsURL());

        // MP: This is a hack to so that the XML params work with the newer
        //     version of Xalan.
        // Sets attribute in SimpleComponent, attributes of the same name will
        // be overweritten.
        setAttribute(ASSETS, Utilities.getGlobalAssetsURL());

        // Make sure the error display gets rendered.
        getErrorDisplay().setIdAttr("page-body");

        final PresentationManager pm = Bebop.getConfig().getPresentationManager();

        if (pm instanceof PageTransformer) {
            m_transformer = (PageTransformer) pm;
        }
        else {
            m_transformer = new PageTransformer();
        }
    }

    /**
     * Finishes and locks the page. If the page is already locked, does nothing.
     *
     * This method is called by the {@link com.arsdigita.dispatcher.Dispatcher}
     * that initializes this page.
     */
    @Override
    public synchronized void init() {
        s_log.debug("Initializing the page");

        if (!isLocked()) {
            s_log.debug("The page hasn't been locked; locking it now");

            lock();
        }
    }

    /**
     * Fetches the value of the XML parameter.
     *
     * @param name The parameter name
     * @return The parameter value
     * @pre (name != null)
     */
    public String getXMLParameter(String name) {
        return (String) m_params.get(name);
    }

    /**
     * Set an XML parameter.
     *
     * @param name The parameter name
     * @param value The parameter value
     * @pre (name != null)
     */
    public void setXMLParameter(String name, String value) {
        m_params.put(name, value);
    }

    /**
     * Fetch the request-local content section.
     *
     * @param request The HTTP request
     * @return The current content section
     *
     * @deprecated use com.arsdigita.cms.CMS.getContext().getContentSection() 
     *             instead
     *             Despite of being deprecated it can not be removed because it
     *             is required by the interface Resourcehandler which is
     *             implemented by this class.
     *             On the other hand, if deprecated, implementing ResourceHandler
     *             may not be required
     */
    @Override
    public ContentSection getContentSection(HttpServletRequest request) {
        // Resets all content sections associations.
     // return ContentSectionDispatcher.getContentSection(request);
        return ContentSectionServlet.getContentSection(request);
    }

    /**
     * Fetch the request-local content section.
     *
     * @param state The page state
     * @return The current content section
     *
     * @deprecated use com.arsdigita.cms.CMS.getContext().getContentSection()
     *             instead
     *             Despite of being deprecated it can not be removed because it
     *             is required by ContentItemPage which extends CMSPage and
     *             uses this method.
     */
    public ContentSection getContentSection(PageState state) {
        return getContentSection(state.getRequest());
    }

    /**
     * Fetch the request-local content item.
     *
     * @param request The HTTP request
     * @return The current content item
     *
     * @deprecated use com.arsdigita.cms.CMS.getContext().getContentItem()
     *             instead
     *             Despite of being deprecated it can not be removed because it
     *             is required by the interface Resourcehandler which is
     *             implemented by this class.
     *             On the other hand, if deprecated, implementing ResourceHandler
     *             may not be required
     */
    public ContentItem getContentItem(HttpServletRequest request) {
        // resets all content item associations
        return ContentSectionDispatcher.getContentItem(request);
    }

    /**
     * Fetch the request-local content item.
     *
     * @param state The page state
     * @return The current content item
     * @deprecated use com.arsdigita.cms.CMS.getContext().getContentItem()
     *             instead.
     *             Despite of being deprecated it can not be removed because it
     *             is required by ContentItemPage which extends CMSPage and
     *             uses this method.
     */
    public ContentItem getContentItem(PageState state) {
        return getContentItem(state.getRequest());
    }

    /**
     * Services the Bebop page.
     *
     * @param request The servlet request object
     * @param response the servlet response object
     * @param actx The request context
     *
     * @pre m_transformer != null
     */
    @Override
    public void dispatch(final HttpServletRequest request,
                         final HttpServletResponse response ,
                         RequestContext actx
                        )
        throws IOException, ServletException {

        DeveloperSupport.startStage("CMSPage.dispatch: serve page");

        CMSExcursion excursion = new CMSExcursion() {
                @Override
                public void excurse() throws IOException, ServletException {
                    Application app = Application.getCurrentApplication(request);
                    ContentSection section = null;

                    if (app == null) {
                        // We're at the content center; do nothing.
                    } else if (app instanceof ContentSection) {
                        section = (ContentSection) app;
                    } else {
                        // hack to deal with category browser mounted
                        // under section app.
                        app = app.getParentApplication();
                        if (app instanceof ContentSection) {
                            section = (ContentSection) app;
                        }
                    }

                    if (section != null) {
                        setContentSection(section);
                        setSecurityManager(new SecurityManager(section));
                    }

                    final String itemID = request.getParameter("item_id");

                    if (itemID != null) {
                        try {
                            ContentItem item =
                                (ContentItem) DomainObjectFactory.newInstance
                                 (new OID(ContentItem.BASE_DATA_OBJECT_TYPE,
                                          new BigDecimal(itemID)));
                            setContentItem(item);
                            PermissionDescriptor perm = new PermissionDescriptor(
                                SecurityManager.CMS_PREVIEW_ITEM_DESCRIPTOR,
                                item,
                                Kernel.getContext().getParty() );
                            if (!PermissionService.checkPermission(perm)) {
                                s_log.warn("No perm to CMS_PREVIEW_ITEM " + itemID);
                                throw new AccessDeniedException(
                                  "You do not have privileges to administer item "
                                  + itemID);
                            }
                        } catch (DataObjectNotFoundException donfe) {
                            s_log.warn("Failed to load content item " + itemID);
                        }
                    }

                    final Document doc = buildDocument(request, response);

                    Assert.exists(m_transformer,
                                  "Implementation of PresentationManager");
                    m_transformer.servePage(doc, request, response, m_params);
                }
            };
        try {
            excursion.run();
        } finally {
            DeveloperSupport.endStage("CMSPage.dispatch: serve page");
        }
    }

    /**
     * Overwrites bebop.Page#generateXMLHelper to add the name of the user
     * logged in to the page (displayed as part of the header).
     * @param ps
     * @param parent
     * @return 
     */
    @Override
    protected Element generateXMLHelper(PageState ps, Document parent) {
        Element page = super.generateXMLHelper(ps,parent);
        User user = (User) Kernel.getContext().getParty();
        if ( user != null ) {
            page.addAttribute("name",user.getDisplayName());
        }

        return page;
    }

//  /**
//   * @deprecated Use Kernel.getContext().getParty() if possible and
//   * Web.getWebContext().getUser() if necessary.
//   */
//   public static User getCurrentUser(PageState state) {
//      return (User) Kernel.getContext().getParty();
//  }
}
