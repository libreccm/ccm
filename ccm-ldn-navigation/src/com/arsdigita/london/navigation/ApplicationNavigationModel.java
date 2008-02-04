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

package com.arsdigita.london.navigation;

import com.arsdigita.london.navigation.cms.CMSNavigationModel;
import com.arsdigita.london.util.TransactionLocal;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

import java.util.HashMap;

import org.apache.log4j.Logger;

public class ApplicationNavigationModel implements NavigationModel {
    private static final Logger s_log =
        Logger.getLogger( ApplicationNavigationModel.class );

    private static final HashMap s_navModels = new HashMap();

    private static final TransactionLocal s_model = new TransactionLocal();
    private static final NavigationModel s_defaultModel =
        new GenericNavigationModel();
        
    static {
        register(ContentSection.BASE_DATA_OBJECT_TYPE, new CMSNavigationModel());
        register(Navigation.BASE_DATA_OBJECT_TYPE, new DefaultNavigationModel());
    }

    public ACSObject getObject() {
        return getNavigationModel().getObject();
    }

    public Category getCategory() {
        return getNavigationModel().getCategory();
    }

    public Category[] getCategoryPath() {
        return getNavigationModel().getCategoryPath();
    }

    public Category getRootCategory() {
        return getNavigationModel().getRootCategory();
    }

    private NavigationModel getNavigationModel() {
        NavigationModel model = (NavigationModel)s_model.get();
        if (model != null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Using cached model " + model.getClass().getName());
            }
            return model;
        }

        Application app = Web.getContext().getApplication();

        if (app != null) {
            model = (NavigationModel)s_navModels.get(
                app.getObjectType().getQualifiedName());
        }
        if (model == null) {
            model = s_defaultModel;
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Using model " + model.getClass().getName() +
                        "for application " + (app == null ? null : app.getOID()));
        }
        
        s_model.set(model);
        return model;
    }

    public static void register( String type, NavigationModel model ) {
        s_navModels.put(type, model);
    }
}
