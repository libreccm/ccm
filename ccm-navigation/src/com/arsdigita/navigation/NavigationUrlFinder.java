/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.navigation;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.TemplateContext;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class NavigationUrlFinder implements URLFinder {

    private final static Logger LOGGER = Logger.getLogger(NavigationUrlFinder.class);

    public String find(final OID oid) throws NoValidURLException {
        LOGGER.info(String.format("Locating %s", oid.toString()));

        final Category category = (Category) DomainObjectFactory.newInstance(oid);
        final CategoryCollection ancestors = category.getDefaultAscendants();
        ancestors.clearOrder();
        ancestors.addOrder(Category.DEFAULT_ANCESTORS);

        final List<BigDecimal> ids = new ArrayList<BigDecimal>();
        List<String> paths = new LinkedList<String>();
        boolean first = true;

        while (ancestors.next()) {
            final Category ancestor = ancestors.getCategory();
            LOGGER.debug(String.format("Process parent %s", ancestor.toString()));

            ids.add(ancestor.getID());
            if (first) {
                first = false;
                paths.add("");
                continue;
            }
            if (paths != null) {
                String url = ancestor.getURL();
                if (url != null && !"".equals(url)) {
                    LOGGER.debug("Appending '" + url + "' for anc");
                    paths.add(url);
                } else {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Cat " + ancestor + " has no url ");
                    }
                    paths = null;
                }
            } else {
                LOGGER.debug("Path is null");
            }
        }

        if (LOGGER.isDebugEnabled() && null != paths) {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < ids.size(); i++) {
                builder.append(ids.get(i).toString()).append(' ');
                builder.append(paths.get(i).toString()).append('/');
            }

            LOGGER.debug(String.format("Full path is %s.", builder.toString()));
        }

        final TemplateContext templateContext = Navigation.getContext().getTemplateContext();
        final String useContext;
        if (templateContext == null) {
            useContext = null;
        } else {
            useContext = templateContext.getContext();
        }
        LOGGER.debug(String.format("Use Context: %s", useContext));

        final DataCollection applications = SessionManager.getSession().retrieve(Application.BASE_DATA_OBJECT_TYPE);
        applications.addEqualsFilter(ACSObject.OBJECT_TYPE, Navigation.BASE_DATA_OBJECT_TYPE);
        applications.addEqualsFilter("rootUseContext.useContext", useContext);
        final Filter filter = applications.addFilter("rootUseContext.rootCategory in :ids");
        filter.set("ids", ids);
        applications.addPath("rootUseContext.rootCategory.id");

        final String applicationUrl;
        final BigDecimal rootCatId;
        if (applications.next()) {
            final Application application = (Application) DomainObjectFactory.newInstance(applications.getDataObject());
            applicationUrl = String.format("%s/", application.getPath());
            rootCatId = (BigDecimal) applications.get("rootUseContext.rootCategory.id");
            applications.close();
        } else {
            applicationUrl = Navigation.getConfig().getDefaultCategoryRootPath();
            rootCatId = null;
            // We can only use named paths if the category is mapped
            // to a navigation app instance in the current
            // use context 
            paths = null;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Using default nav path " + applicationUrl));
            }
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Application path is %s", applicationUrl));
        }

        Assert.isTrue(applicationUrl.charAt(0) == '/', "Assert failed: url starts not with '/'");
        Assert.isTrue(applicationUrl.endsWith("/"), "Assert failed: url ends not with '/'");

        final ParameterMap parameterMap = new ParameterMap();
        final String path;
        if (paths == null) {
            parameterMap.setParameter("categoryID", category.getID());
            path = "category.jsp";
        } else {
            LOGGER.debug(String.format("Generating path from category %s", rootCatId));

            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < paths.size(); i++) {
                if (!ids.get(i).equals(rootCatId)) {
                    builder.append(paths.get(i));
                    builder.append('/');
                }
            }

            path = builder.toString();
        }

        final String url = URL.there(Web.getRequest(), String.format("%s%s", applicationUrl, path), parameterMap).
                toString();
        LOGGER.info(String.format("Final URL is: %s", url));

        return url;

    }

    public String find(final OID oid, final String context) throws NoValidURLException {
        return find(oid);
    }  

}
