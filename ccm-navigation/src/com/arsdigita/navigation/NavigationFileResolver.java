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
 */
package com.arsdigita.navigation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.categorization.CategoryLocalizationCollection;
import com.arsdigita.cms.CMSConfig;
import com.arsdigita.cms.TemplateContext;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.web.Application;
import com.arsdigita.web.DefaultApplicationFileResolver;
import com.arsdigita.web.Web;

import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.KernelConfig;

import java.io.IOException;

/**
 * Manages the processing of URLs in the Navigation application.
 *
 */
public class NavigationFileResolver extends DefaultApplicationFileResolver {

    private static final Logger s_log = Logger.getLogger(
        NavigationFileResolver.class);
    private static final String CATEGORY_PATH_ATTR
                                    = NavigationFileResolver.class
                                          + ".categoryPath";
    // path is set in a cookie, which navigation models may use if they wish
    public static final String PATH_COOKIE_NAME = "ad_path";
    public static final char PATH_COOKIE_SEPARATOR = '|';

    @Override
    public RequestDispatcher resolve(String templatePath,
                                     HttpServletRequest sreq,
                                     HttpServletResponse sresp,
                                     Application app) {

        String path = sreq.getPathInfo();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Resolving " + path);
        }

        if (!path.endsWith(".jsp")) {
            if (CMSConfig.getInstanceOf().getUseLanguageExtension()
                    && path.matches("(.*)/index\\.[a-zA-Z]{2}")) {

                final String lang = path.substring(path.length() - 2);
                path = path.substring(0, path.length() - "index.$$".length());
                GlobalizationHelper.setSelectedLocale(lang);
            } else {

                String lang;
                if (GlobalizationHelper.getSelectedLocale(sreq) == null) {
                    lang = GlobalizationHelper.getNegotiatedLocale()
                        .getLanguage();
                } else {
                    lang = GlobalizationHelper.getSelectedLocale(sreq)
                        .getLanguage();
                }

                final StringBuffer redirectTo = new StringBuffer();

                redirectTo
                    .append(DispatcherHelper.getRequest().getScheme())
                    .append("://")
                    .append(DispatcherHelper.getRequest().getServerName());

                if (DispatcherHelper.getRequest().getServerPort() != 80
                        && DispatcherHelper.getRequest().getServerPort() != 443) {
                    redirectTo
                        .append(":")
                        .append(DispatcherHelper.getRequest().getServerPort());
                }

                if (DispatcherHelper.getWebappContext() != null
                        && !DispatcherHelper.getWebappContext().trim().isEmpty()) {
                    redirectTo.append(DispatcherHelper.getWebappContext());
                }

                //Is category available in lang? If not change lang to default language
                final Category[] cats = resolvePath(getRootCategory(), path);
                if (cats == null) {
                    lang = KernelConfig.getConfig().getDefaultLanguage();
                } else {
                    final CategoryLocalizationCollection langs
                                                         = cats[cats.length
                                                                    - 1]
                            .getCategoryLocalizationCollection();
                    if (!langs.localizationExists(lang)) {
                        lang = KernelConfig.getConfig().getDefaultLanguage();
                    }
                }

                redirectTo
                    .append("/ccm")
                    .append(app.getPath())
                    .append(path);
                if (!path.endsWith("/")) {
                    redirectTo.append('/');
                }
                redirectTo
                    .append("index.")
                    .append(lang);

                sresp.setHeader("Location", redirectTo.toString());
                try {
                    sresp.sendError(HttpServletResponse.SC_MOVED_PERMANENTLY);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                return null;
            }
        }

        if (path.equals("/category.jsp")) {
            Navigation nav = (Navigation) Web.getWebContext().getApplication();

            String id = sreq.getParameter("categoryID");
            if (s_log.isDebugEnabled()) {
                s_log.debug("dispatching to category " + id);
            }

            String useContext = sreq.getParameter("useContext");
            if (s_log.isDebugEnabled()) {
                s_log.debug("Using use context " + useContext);
            }
            if (null == useContext) {
                useContext = Template.DEFAULT_USE_CONTEXT;
            }
            Category cat = null;

            // if the category doesn't exist, send a 404, nicer
            try {
                cat = new Category(new BigDecimal(id));
            } catch (Exception e) {
                s_log.warn("Could not load category for id " + id);
                return null;
            }

            // check that the category is enabled, otherwise 404
            if (!cat.isEnabled()) {
                s_log.warn("Category is disabled.");
                return null;
            }

            // check that the category is in the tree of categories
            Category root = null;
            DataCollection objs = SessionManager.getSession()
                .retrieve(Domain.BASE_DATA_OBJECT_TYPE);
            objs.addEqualsFilter("model.ownerUseContext.categoryOwner.id", nav
                                 .getID());
            String dispatcherContext = null;
            TemplateContext tc = Navigation.getContext().getTemplateContext();
            if (tc != null) {
                dispatcherContext = tc.getContext();
            }
            objs.addEqualsFilter("model.ownerUseContext.useContext",
                                 dispatcherContext);
            DomainCollection domains = new DomainCollection(objs);
            if (domains.next()) {
                root = ((Domain) domains.getDomainObject()).getModel();
            } else {
                // can't find domain, 404
                s_log.warn("Category domain does not exist.");
                return null;
            }
            domains.close();
            if (root == null) {
                // no root category, 404
                s_log.warn("Category domain does not have a root category.");
                return null;
            }

            if (!root.isMemberOfSubtree(cat)) {
                // it is not in the tree, send 404
                s_log.warn("Category doesn't belong to navigation tree.");
                return null;
            }

            CategoryCollection parents = cat.getDefaultAscendants();
            parents.addOrder(Category.DEFAULT_ANCESTORS);
            List cats = new ArrayList();
            while (parents.next()) {
                cats.add(parents.getCategory());
            }

            Category[] catsArray = (Category[]) cats.toArray(new Category[cats
                .size()]);

            sreq.setAttribute(CATEGORY_PATH_ATTR,
                              catsArray);
            setPathCookie(sresp, catsArray);
            return resolveTemplate(catsArray[catsArray.length - 1], useContext);
        } else {
            String useContext = Template.DEFAULT_USE_CONTEXT;
            if (path.endsWith(".jsp")) {
                int lastSlash = path.lastIndexOf('/');

                useContext = path.substring(lastSlash + 1, path.length() - 4);
                path = path.substring(0, lastSlash);

                if (s_log.isDebugEnabled()) {
                    s_log.debug("useContext=" + useContext + ",path=" + path);
                }
            }

            Category root = getRootCategory();
            Category[] cats = resolvePath(root, path);

            if (cats == null || cats.length == 0) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("No category found");
                }
                sreq.setAttribute(CATEGORY_PATH_ATTR,
                                  new Category[]{root});
                setPathCookie(sresp, new Category[]{root});
                return super.resolve(templatePath, sreq, sresp, app);
            } else {
                Category cat = cats[cats.length - 1];
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Got cat " + cat);
                }
                setPathCookie(sresp, cats);
                sreq.setAttribute(CATEGORY_PATH_ATTR,
                                  cats);
                RequestDispatcher rd = resolveTemplate(cat, useContext);
                if (rd == null) {
                    return super.resolve(templatePath, sreq, sresp, app);
                }
                return rd;
            }
        }
    }

    /**
     * sets current path in a cookie
     *
     * @param catsArray
     */
    private void setPathCookie(HttpServletResponse resp, Category[] catsArray) {

        // 1st part of cookie value is website - if cookie domain covers several 
        // Aplaws sites, and the navigation model retains the cookie for more 
        // than one request, we could potentially link to one site with a path 
        // relating to another site. A check on this part of the cookie will 
        // prevent problems
        StringBuffer path = new StringBuffer(Web.getConfig().getSiteName());

        // 2nd part of cookie value is the application that set it. Again may 
        // be used if a navigation model retains the cookie. If we link to 
        // another application, it's navigation model may use this when 
        // deciding whether to trust the given path.
        path.append(PATH_COOKIE_SEPARATOR + Kernel.getContext().getResource()
            .getID().toString());
        for (int i = 0; i < catsArray.length; i++) {
            Category cat = catsArray[i];
            path.append(PATH_COOKIE_SEPARATOR + cat.getID().toString());
        }
        Cookie cookie = new Cookie(PATH_COOKIE_NAME, path.toString());
        s_log.debug("setting cookie with value: " + path);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        String domain = Kernel.getSecurityConfig().getCookieDomain();
        if (domain != null) {
            cookie.setDomain(domain);
        }
        resp.addCookie(cookie);

    }

    public static Category[] getCategoryPath(HttpServletRequest req) {
        return (Category[]) req.getAttribute(CATEGORY_PATH_ATTR);
    }

    private String getTemplateContext() {
        TemplateContext ctx = Navigation.getContext().getTemplateContext();
        if (ctx == null) {
            return Template.DEFAULT_DISPATCHER_CONTEXT;
        } else {
            return ctx.getContext();
        }
    }

    /**
     *
     * @param cat
     * @param useContext
     *
     * @return
     */
    private RequestDispatcher resolveTemplate(Category cat, String useContext) {
        Template template = null;
        if (Navigation.getConfig().inheritTemplates()) {
            template = Template.matchBest(cat,
                                          getTemplateContext(),
                                          useContext);
        } else {
            template = Template.matchExact(cat,
                                           getTemplateContext(),
                                           useContext);
        }
        // If there's an explicit use context which doesn't exist, give a 404
        if (!Template.DEFAULT_USE_CONTEXT.equals(useContext) && null == template) {
            s_log.debug("No template found in context " + getTemplateContext()
                            + " for category " + cat.getID()
                            + " with use context " + useContext);
            return null;
        }

        String path = null;
        if (template == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Using default template");
            }
            path = Navigation.getConfig().getDefaultTemplate();
        } else {
            path = template.getURL();
        }
        // Old style, no longer valid. App may be installed into any arbitrary
        // context and by default all modules are installed into one context.
        // RequestDispatcher rd = Web.findResourceDispatcher(
        //        new String[]{"ROOT"},
        //        path);
        // new style, will lookup path in the current context (formerly used to
        // be ROOT)
        RequestDispatcher rd = Web.findResourceDispatcher(path);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Got dispatcher " + rd);
        }

        return rd;
    }

    private Category getRootCategory() {
        Navigation nav = (Navigation) Web.getWebContext().getApplication();
        TemplateContext ctx = Navigation.getContext().getTemplateContext();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Finding root for " + nav + " in context " + ctx);
        }

        String dispatcherContext = ctx == null ? null : ctx.getContext();

        Category root = Category.getRootForObject(nav,
                                                  dispatcherContext);

        if (root == null && dispatcherContext != null) {
            s_log.debug("No specific root found, trying generic context");
            root = Category.getRootForObject(nav, null);
        }

        Assert.exists(root, Category.class);
        if (s_log.isDebugEnabled()) {
            s_log.debug("Got root category " + root);
        }
        return root;
    }

    /**
     *
     * category resolution retained as an instance method to allow it to be
     * overridden. Default functionality contained in static resolveCategory
     * method.
     *
     * @param root
     * @param path
     *
     * @return
     */
    protected Category[] resolvePath(Category root, String path) {
        return NavigationFileResolver.resolveCategory(root, path);
    }

    /**
     * Match a URL with the category tree and return the requested category if
     * exists.
     *
     * Quasimodo: Originally addEqualsFilter has been used to filter the
     * appropriate category directly inside the SQL query. This isn't possible
     * anymore due to the localised URLs of the new localised categories (or at
     * least: not found it). Therefore we do the filtering in Java now.
     *
     */
    public static Category[] resolveCategory(Category root,
                                             String path) {
        String[] bits = StringUtils.split(path, '/');

        List cats = new ArrayList();
        cats.add(root);

        Category cat = root;
        for (int i = 0; i < bits.length; i++) {
            if ("".equals(bits[i])) {
                continue;
            }
            if (s_log.isDebugEnabled()) {
                s_log.debug("Looking for match to " + bits[i]);
            }

            CategoryCollection children = cat.getChildren();
//            children.addEqualsFilter(Category.URL, bits[i]);
//            children.addEqualsFilter(Category.IS_ENABLED, Boolean.TRUE);
//            if (children.next()) {
            boolean found = false;
            while (children.next()) {
                cat = children.getCategory();
                if (cat.getURL().equals(bits[i]) && cat.isEnabled() == true) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Got category " + cat);
                    }
                    cats.add(cat);
                    children.close();
                    found = true;
                    break;
                }
            }
//            } else {
            if (found == false) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("No category found ");
                }
                return null;
            }
        }

        return (Category[]) cats.toArray(new Category[cats.size()]);
    }

}
