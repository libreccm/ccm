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

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.page.PageTransformer;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.web.Application;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


//  ////////////////////////////////////////////////////////////////////////////
//
//  Currently under development as a replacement for CMSPage without the
//  dispatcher mechanism but a new application style "pure" bebop pageElement
//  served by an application servlet.
//
//  ////////////////////////////////////////////////////////////////////////////


/**
 * A <tt>CMSApplicationPage</tt> is a Bebop {@link com.arsdigita.bebop.Page}
 * implementation serving as a base for any CMS pageElement served by a 
 * servlet. 
 *
 * It stores the current {@link com.arsdigita.cms.ContentSection} and, if
 * applicable, the {@link com.arsdigita.cms.ContentItem} in the pageElement 
 * state as request local objects. Components that are part of the 
 * <tt>CMSPage</tt> may access these objects by calling:
 *     <blockquote><code><pre>
 *     getContentSection(PageState state);
 *     </pre></code></blockquote>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Uday Mathur (umathur@arsdigita.com)
 * @version $Id: CMSApplicationPage.java 2140 2011-01-16 12:04:20Z pboy $
 */
public class CMSApplicationPage extends Page {

    private static final Logger s_log = Logger.getLogger(CMSApplicationPage.class);

    /** The global assets URL stub XML parameter name.    */
    public final static String ASSETS = "ASSETS";

    /** The XML pageElement class.     */
    public final static String PAGE_CLASS = "CMS";

    /** Map of XML parameters   */
    private HashMap m_params;

    /**    */
    private PageTransformer m_transformer;

    public CMSApplicationPage() {
        super();
        buildPage();
    }

    public CMSApplicationPage(String title) {
        super(title);
        buildPage();
    }

    public CMSApplicationPage(String title, Container panel) {
        super(title, panel);
        buildPage();
    }

    public CMSApplicationPage(Label title) {
        super(title);
        buildPage();
    }

    public CMSApplicationPage(Label title, Container panel) {
        super(title, panel);
        buildPage();
    }

    /**
     * Builds the pageElement.
     */
    private void buildPage() {

        // Set the class attribute value (down in SimpleComponent).
        setClassAttr(PAGE_CLASS);

        // Global XML params.
        // MP: This only works with older versions of Xalan.
        m_params = new HashMap();
        setXMLParameter(ASSETS, Utilities.getGlobalAssetsURL());

        // MP: This is a hack to so that the XML params work with the newer
        //     version of Xalan.
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
     * Finishes and locks the pageElement. If the pageElement is already 
     * locked, does nothing.
     * 
     * Client classes may overwrite this method to add context specific bits
     * to the page before it is locked.
     *
     * This method is called by the various servlets serving the various pages
     * of the CMS package, before serving and displaying the page.
     */
    public synchronized void init(HttpServletRequest sreq,
                                  HttpServletResponse sresp,
                                  Application app) {
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
     * @ deprecated use com.arsdigita.cms.CMS.getContext().getContentSection() 
     *             instead
     *             Despite of being deprecated it can not be removed because it
     *             is required by the interface Resourcehandler which is
     *             implemented by this class.
     *             On the other hand, if deprecated, implementing ResourceHandler
     *             may not be required
     */
// public ContentSection getContentSection(HttpServletRequest request) {
//      // Resets all content sections associations.
//   // return ContentSectionDispatcher.getContentSection(request);
//      return ContentSectionServlet.getContentSection(request);
//  }

    /**
     * Fetch the request-local content section.
     *
     * @param state The pageElement state
     * @return The current content section
     *
     * @ deprecated use com.arsdigita.cms.CMS.getContext().getContentSection()
     *             instead
     *             Despite of being deprecated it can not be removed because it
     *             is required by ContentItemPage which extends CMSPage and
     *             uses this method.
     */
//  public ContentSection getContentSection(PageState state) {
//      return getContentSection(state.getRequest());
//  }

    /**
     * Fetch the request-local content item.
     *
     * @param request The HTTP request
     * @return The current content item
     *
     * @ deprecated use com.arsdigita.cms.CMS.getContext().getContentItem()
     *             instead
     *             Despite of being deprecated it can not be removed because it
     *             is required by the interface Resourcehandler which is
     *             implemented by this class.
     *             On the other hand, if deprecated, implementing ResourceHandler
     *             may not be required
     */
//  public ContentItem getContentItem(HttpServletRequest request) {
//      // resets all content item associations
//      return ContentSectionDispatcher.getContentItem(request);
//  }

    /**
     * Fetch the request-local content item.
     *
     * @param state The pageElement state
     * @return The current content item
     * @ deprecated use com.arsdigita.cms.CMS.getContext().getContentItem()
     *             instead.
     *             Despite of being deprecated it can not be removed because it
     *             is required by ContentItemPage which extends CMSPage and
     *             uses this method.
     */
 //  public ContentItem getContentItem(PageState state) {
 //     return getContentItem(state.getRequest());
 // }

    /**
     * Services the Bebop pageElement.
     *
     * @param request The servlet request object
     * @param response the servlet response object
     *
     * @pre m_transformer != null
     */
/*  public void dispatch(final HttpServletRequest request,
                         final HttpServletResponse response // ,
                         // RequestContext actx
                        )
        throws IOException, ServletException {

        DeveloperSupport.startStage("CMSPage.dispatch: serve pageElement");

        CMSExcursion excursion = new CMSExcursion() {
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
            DeveloperSupport.endStage("CMSPage.dispatch: serve pageElement");
        }
    }  */

    /**
     * Overwrites bebop.Page#generateXMLHelper to add the name of the user
     * logged in to the pageElement (displayed as part of the header).
     * @param ps
     * @param parent
     * @return pageElement for use in generateXML
     */
    @Override
    protected Element generateXMLHelper(PageState ps, Document parent) {

        /* Retain elements already included.                                  */
        Element pageElement = super.generateXMLHelper(ps,parent);

        /* Add name of user logged in.                                        */
        // Note: There are at least 2 ways in the API to determin the user
        // TODO: Check for differences, determin the best / recommended way and
        //       document it in the classes. Probably remove one ore the other
        //       way from the API if possible.
        User user = (User) Kernel.getContext().getParty();
        // User user = Web.getContext().getUser();
        if ( user != null ) {
            pageElement.addAttribute("name",user.getDisplayName());
        }

        return pageElement;
    }

}
