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
package com.arsdigita.kernel;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.HierarchyDenormalization;
import com.arsdigita.web.PathMapCache;

import java.math.BigDecimal;
// import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * A SiteNode represents a part of the URL hierarchy on a server.  Each instance
 * of a SiteNode may be mapped to an application instance for the purpose of
 * providing access to that application.
 *
 * <p>For example, the URL "http://www.example.com/news/" can be broken down
 * into:</p>
 *
 * <ul>
 *   <li>the protocol, which is <code><http://</code> </li>
 *   <li>the server, which is <code>www.example.com</code>. </li>
 *   <li>the hierarchy on the server, which is <code>/news/</code></li>
 * </ul>
 *
 * <p>The hierarchy is delimited by slashes (/).</p>
 *
 * <ul>
 *   <li>The first "/" indicates the root <code>SiteNode</code>, the parent of
 *   all other site nodes on the system.</li>
 *
 *   <li>The next token ("news/") indicates a <code>SiteNode</code> that is a
 *   child of the root <code>SiteNode</code> with a name of "news."</li>
 * </ul>
 *
 * @version 1.0
 * @version $Id: SiteNode.java 287 2005-02-22 00:29:02Z sskracic $
 * @since ACS 5.0
 * @deprecated Refactor to use {@link com.arsdigita.web.Application} instead.
 * The class is due to completly be removed.
 */
public class SiteNode extends ACSObject {

    private static final String s_typeName = "com.arsdigita.kernel.SiteNode";
    private static final Logger s_log =
        Logger.getLogger(SiteNode.class.getName());

    private static final Cache s_cache = new Cache();

    protected String getBaseDataObjectType() {
        return s_typeName;
    }

    private static SiteNode s_rootSiteNode;

    // This holds the class that maintains the hierarchy for the site node
    private HierarchyDenormalization m_hierarchy;

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "SiteNode".
     *
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     * @deprecated see above
     */
    public SiteNode() {
        super(s_typeName);
    }

    /**
     * Creates a new DomainObject instance to encapsulate a given
     * data object.
     *
     * @param dataObject the data object to encapsulate in the new domain
     * object
     * @see com.arsdigita.persistence.Session#retrieve(String)
     * @deprecated see above
     */
    public SiteNode(DataObject dataObject) {
        super(dataObject);
    }

    @Override
    protected void initialize() {
        super.initialize();
        if (isNew()) {
            set("isDirectory", Boolean.TRUE);
            set("isPattern", Boolean.FALSE);
        }
        m_hierarchy = new HierarchyDenormalization
            ("com.arsdigita.kernel.updateSiteNodeDescendants", this, "url") {};
    }

    /**
     * Retrieves the SiteNode with the specified ID.
     *
     * @param id a SiteNode ID
     * @exception DataObjectNotFoundException if the ID does not match
     *     a SiteNode in the system.
     * @deprecated see above
     */
    public SiteNode(BigDecimal id) throws DataObjectNotFoundException {
        super(new OID(s_typeName, id));
    }

    /**
     * Retrieves a SiteNode that corresponds to the specified OID.
     *
     * @param oid the OID for the retrieved instance
     * @see com.arsdigita.domain.DomainObject#DomainObject(OID)
     * @see com.arsdigita.persistence.OID
     * @deprecated see above
     */
    public SiteNode(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Gets the URL of the site note.
     * @return the URL of the site node, <em>not</em> the URL requested by the
     * user!
     *
     * @param req Servlet request.  This is needed to get the context
     * path, in case the servlet is not mounted at /.
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public String getURL(HttpServletRequest req) {
        return getURL(req==null ? (String) null : req.getContextPath());
    }


    /**
     * Gets the URL of the site note.
     *
     * @param req Servlet request.  This is needed to get the context
     * path, in case the servlet is not mounted at /.
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public String getUrl(HttpServletRequest req) {
        return getURL(req);
    }

    /**
     * @return the URL of the site node, <em>not</em> the URL requested by the
     * user!
     *
     * @param ContextPath in case the servlet is not mounted at /
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public String getURL(String contextPath) {
        if (contextPath != null) {
            return contextPath + getURLNoContext();
        } else {
            return getURLNoContext();
        }
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public String getUrl(String contextPath) {
        return getURL(contextPath);
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public String getURL() {
        return getURLNoContext();
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    private void setURL() {
        s_log.debug("Setting url: " + getURLFromParent());
        set("url", getURLFromParent());
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    private String getURLFromParent() {
        SiteNode parent = getParent();
        if (parent != null) {
            return parent.getURL() + getName() + "/";
        } else {
            return getName() + "/";
        }
    }

    /**
     * @return the URL of the site node, <em>not</em> the URL
     * requested by the user!
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public String getURLNoContext() {
        String value = (String)get("url");
        if (value == null) {
            return "/";
        } else {
            return value;
        }
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public BigDecimal getNodeId() {
        return (BigDecimal) get("id");
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public void setName(String name) {
        set("name", name);
        setURL();
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public void setParent(SiteNode siteNode) {
        // Should throw an Exception if parent is not a directory.

        if (siteNode == null) {
            set("parent", null);
        } else {
            if (((Boolean) (siteNode.get("isDirectory"))).booleanValue()) {
                setAssociation("parent", siteNode);
            }
        }

        setURL();
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public String getName() {
        String name = (String) get("name");

        if (name == null) {
            return "";
        } else {
            return name;
        }
    }

    /**
     * Returns a display name for this site node.
     *
     * @see ACSObject#getDisplayName()
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    @Override
    public String getDisplayName() {
        return getURL();
    }

    /**
     * @return <code>true</code> if this SiteNode can have children;
     * <code>false</code> otherwise.
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public boolean isDirectory() {
        return ((Boolean) (get("isDirectory"))).booleanValue();
    }

    /**
     * @return <code>true</code> if the SiteNode supports patterns;
     * <code>false</code> otherwise.
     * @deprecated
     */
    public boolean isPattern() {
        return ((Boolean)(get("isPattern"))).booleanValue();
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public PackageInstance getPackageInstance() {
        DataObject dataObject = (DataObject)get("mountedObject");
        if (dataObject != null) {
            return new PackageInstance(dataObject);
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No package mounted; returning null");
            }
            return null;
        }
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public void mountPackage(PackageInstance pkg) {
        unMountPackage();

        setAssociation("mountedObject", pkg);

        PackageEventListener pels[] = pkg.getType().getListeners();

        for (int i = 0; i < pels.length; i++) {
            PackageEventListener listener = pels[i];
            listener.onMount(this, pkg);
        }
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public void unMountPackage() {
        PackageInstance pkg = getPackageInstance();

        if (pkg != null) {
            PackageEventListener pels[] = pkg.getType().getListeners();

            for (int i = 0; i < pels.length; i++) {
                PackageEventListener listener = pels[i];
                listener.onUnmount(this, pkg);
            }

            set("mountedObject", null);
        }
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public SiteNode getParent() {
        DataObject dataObject = (DataObject)get("parent");

        if (dataObject == null || dataObject.get("id") == null) {
            return null;
        } else {
            return new SiteNode(dataObject);
        }
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public SiteNodeCollection getChildren() {
        DataAssociation childAssociation = (DataAssociation) get("children");
        return new SiteNodeCollection(childAssociation);
    }

    /**
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public static SiteNode getRootSiteNode() {
        // cache the site node statically
        // note lack of synchronization.  worst case:
        // two different threads concurrently query for the root
        // site node and one gets garbage collected.  BFD.
        if (s_rootSiteNode == null || !s_rootSiteNode.isValid()) {
            // Retrieve read-only copy in new transaction context to
            // guarantee validity.

            Session ssn = SessionManager.getSession();

            DataCollection rootSiteNode = SessionManager.getSession().retrieve
                ("com.arsdigita.kernel.SiteNode");
            rootSiteNode.addEqualsFilter("name", null);
            rootSiteNode.addEqualsFilter("parent.id", null);

            if (rootSiteNode.next()) {
                try {
                    DataObject dobj =
                        ssn.retrieve(new OID(s_typeName,
                                             rootSiteNode.get("id")));
                    dobj.disconnect();
                    s_rootSiteNode = new SiteNode(dobj);
                } finally {
                    rootSiteNode.close();
                }
            } else {
                throw new DataObjectNotFoundException
                    ("getRootSiteNode: Root site node not found.");
            }

            rootSiteNode.close();
        }

        return s_rootSiteNode;
    }

    /**
     * for testing, it is necessary to remove all statically
     * cached site nodes. Call this method AFTER creating
     * any new site nodes as part of your unit test's
     * setup method
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public static void repopulateCache() {
        s_cache.refresh();
    }

    /**
     * Finds the site node corresponding to the largest portion
     * of the specified path. The path must begin with '/'.
     * Any trailing slashes are ignored.
     *
     * @param path an absolute path to find the site node of
     * @param readOnly if true, we return a read-only site node
     * from our cache, which may be disconnected from a db session and
     * can't be modified or deleted.  (This is the desired behavior
     * the majority of the time.)
     *
     * @return the site node corresponding to the path, or
     *         null if no matching site node can be found.
     * @throws DataObjectNotFoundException if the path does not start
     *         with a slash (probably the wrong behavior) or if the
     *         RootSiteNode was requested but couldn't be found (also
     *         probably wrong).
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public static SiteNode getSiteNode(final String path, boolean readOnly)
            throws DataObjectNotFoundException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Finding the site node for path '" + path + "'");
        }

        final SiteNode siteNode = s_cache.getNode(path);

        if (siteNode == null) {
            throw new DataObjectNotFoundException
                ("RootSiteNode not available." +
                 "  The data model is not properly loaded.");
        }

        return readOnly ? siteNode : new SiteNode(siteNode.getOID());
    }

    /**
     * Finds the site node corresponding to the largest portion
     * of the specified path. The path must begin with '/'.
     * Any trailing slashes are ignored.
     *
     * @param path an absolute path to find the site node of
     * @return the site node corresponding to the path, or
     *         null if no matching site node can be found.
     * @throws DataObjectNotFoundException if the path does not start
     *         with a slash (probably the wrong behavior) or if the
     *         RootSiteNode was requested but couldn't be found (also
     *         probably wrong).
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public static SiteNode getSiteNode(String path)
        throws DataObjectNotFoundException {
        return getSiteNode(path, false);
    }

    public static SiteNode createSiteNode(String name) {
        return createSiteNode(name, getRootSiteNode());
    }

    /**
     * Overrides the default save method.  If we've changed the
     * URL of this site node (either by changing its name or its parent),
     * we need to also update the URL for all descendants of this site node.
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    protected void beforeSave() {
        if (isPropertyModified("url") || isNew()) {
            s_cache.scheduleRefresh();
        }

        super.beforeSave();
    }

    public void afterDelete() {
        s_cache.scheduleRefresh();
    }

    public static SiteNode createSiteNode(String name, SiteNode parent) {
        SiteNode siteNode = new SiteNode();
        siteNode.setName(name);
        siteNode.setParent(parent);
        siteNode.save();
        return siteNode;
    }

    @Override
    public String toString() {
        return "[url: " + getURL() + "]";
    }

/*    public void addStylesheet(Stylesheet sheet) {
        sheet.addToAssociation((DataAssociation)get("defaultStyle"));
    }
*/

//  /**
//   *
//   * @param locale
//   * @param outputType
//   * @return
//   * @deprecated without direct replacement. It is designed to work with
//   * {@link com.arsdigita.templating.LegacyStylesheetResolver} which is
//   * replaced by {@link com.arsdigita.templating.PatternStylesheetResolver}.
//   * So thes method is just not used anymore. (pboy)
//   */
//    public Stylesheet[] getStylesheets(Locale locale, String outputType) {
//      return StyleAssociation
//          .getStylesheets(get("defaultStyle"), locale, outputType);
//  }
  
    /**
     * 
     * @param locale
     * @param outputType
     * @return
     * @deprecated without direct replacement. It is design wo work with
     * {@link com.arsdigita.templating.LegacyStylesheetResolver} which is
     * replaced by {@link com.arsdigita.templating.PatternStylesheetResolver}.
     * So this method is just not used anymore. (pboy)
     */
/*    public Stylesheet getStylesheet(Locale locale, String outputType) {
        return StyleAssociation
            .getStylesheet(get("defaultStyle"), locale, outputType);
    }
*/
/*    public void removeStylesheet(Stylesheet sheet) {
        sheet.removeFromAssociation((DataAssociation)get("defaultStyle"));
    }
*/
    // the sole purpose of this class is to make site nodes hash the same across
    // multiple server instances.
    private static class SiteNodeWrapper {
        final SiteNode m_node;

        SiteNodeWrapper(SiteNode node) {
            if (node==null) { throw new NullPointerException("node"); }

            m_node = node;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj==null) { return false; }

            SiteNodeWrapper snw = (SiteNodeWrapper) obj;
            return m_node.getID().equals(snw.m_node.getID()) &&
                m_node.getURL().equals(snw.m_node.getURL());
        }

        @Override
        public int hashCode() {
            return m_node.getID().hashCode() + m_node.getURL().hashCode();
        }

        @Override
        public String toString() {
            return m_node.toString();
        }
    }

    // Caching of Site Nodes
    // Stores the cached (url, siteNode) mappings.
    private static class Cache extends PathMapCache {

        public Cache() {
            super("SiteNodeCache");
        }

        // implements the PathMapCache interface
        public String normalize(String path) {
            if ( path==null ) { throw new NullPointerException("path"); }
            if ( !path.startsWith("/") ) {
                throw new DataObjectNotFoundException
                    ("The URL path specified must begin with a '/'.");
            }
            return path.endsWith("/") ? path : (path + "/");
        }

        // implements the PathMapCache interface
        public Object retrieve(String path) {
            DataCollection dc = SessionManager.getSession().retrieve
                ("com.arsdigita.kernel.SiteNode");

            dc.addEqualsFilter("url", path);

            SiteNode siteNode = null;
            if (dc.next()) {
                DataObject dobj = dc.getDataObject();
                dobj.disconnect();
                siteNode = new SiteNode(dobj);
                if (dc.next()) {
                    s_log.error("More than one site node found for url " +
                                path + " ids: " + siteNode.getID() +
                                " and " + dc.get("id"));
                }
                dc.close();
            }
            return siteNode==null ? null : new SiteNodeWrapper(siteNode);
        }

        // implements the PathMapCache interface
        public void refresh() {
            clearAll();
        }

        void scheduleRefresh() {
            super.refreshAfterCommit();
        }

        synchronized SiteNode getNode(String path) {
            SiteNodeWrapper snw = (SiteNodeWrapper) super.get(path);
            return snw.m_node;
        }
    }
}
