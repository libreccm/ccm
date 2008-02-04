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

package com.arsdigita.london.navigation.cms;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.TemplateContext;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.london.navigation.GenericNavigationModel;
import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


/**
 * A CMS aware navigation model. The getObject() method will
 * return the current Content Item if any. The getCategory() method
 * will return the content item's default category, if any.
 * The getRootCategory() method will return the content section's
 * root category, if any.  If a non-null TemplateContext is set
 * (by Subsite filter for example), the root category will not
 * be fetched from content section, instead it will be taken from
 * the domain which has a mapping with said context to the
 * default navigation app (/navigation/).
 */
public class CMSNavigationModel extends GenericNavigationModel {

    private static final Logger s_log
        = Logger.getLogger(CMSNavigationModel.class);

    protected ACSObject loadObject() {
        if (CMS.getContext().hasContentItem()) {
            return CMS.getContext().getContentItem();
        }
        return super.loadObject();
    }

    protected Category loadRootCategory() {
        TemplateContext dispatcherContext = Navigation.getContext().getTemplateContext();
        if (dispatcherContext == null) {
            return super.loadRootCategory();
        }
        DataCollection objs = SessionManager.getSession()
            .retrieve(Domain.BASE_DATA_OBJECT_TYPE);
        Navigation navApp = (Navigation) Application.retrieveApplicationForPath(
            Navigation.getConfig().getDefaultCategoryRootPath());
        Assert.exists(navApp, Navigation.class);
        objs.addEqualsFilter("model.ownerUseContext.categoryOwner.id", navApp.getID());
        objs.addEqualsFilter("model.ownerUseContext.useContext",
                             dispatcherContext.getContext());
        Category root = null;
        DomainCollection domains = new DomainCollection(objs);
        if (domains.next()) {
            root = ((Domain) domains.getDomainObject()).getModel();
            domains.close();
        } else {
            // can't find domain, 404
            return null;
        }
        Assert.exists(root, Category.class);
        return root;
    }

    protected Category[] loadCategoryPath() {
        Category cat = null;

        if (CMS.getContext().hasContentItem()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("There is a content item");
            }
            ContentItem item = CMS.getContext().getContentItem();
            ContentBundle bundle = (ContentBundle)item.getParent();
            if (s_log.isDebugEnabled()) {
                s_log.debug("The item is " + item.getName());
            }

            DataAssociation assoc = (DataAssociation)DomainServiceInterfaceExposer
                .get(bundle, "categories");
            DataAssociationCursor categories = assoc.cursor();
            categories.addOrder("assoc.isEnabled desc, link.isDefault desc");
            Filter f = categories.addInSubqueryFilter(
                "id",
                "com.arsdigita.categorization.categoryIDsInSubtree"
            );
            f.set("categoryID", getRootCategory().getID());
            s_log.debug("root cat is " + getRootCategory().getName());
            if (categories.next()) {
                s_log.debug("we have some categories");
                DataObject obj = categories.getDataObject();
                categories.close();
                cat = (Category) DomainObjectFactory.newInstance( obj );
            } else {
                s_log.debug("we don't have any categories in the subtree with that root cat");
                categories.close();
            }
        }

        if (cat == null) {
            s_log.debug("no category, delegating to parent impl");
            return super.loadCategoryPath();
        }

        List path = new ArrayList();
        CategoryCollection cats = cat.getDefaultAscendants();
        cats.addOrder("defaultAncestors");
        while (cats.next()) {
            path.add(cats.getDomainObject());
        }
        return (Category[])path.toArray(new Category[(int)path.size()]);
    }
}
