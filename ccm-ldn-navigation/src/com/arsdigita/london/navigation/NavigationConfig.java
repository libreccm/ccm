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

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.london.navigation.ui.category.Menu;
import com.arsdigita.london.navigation.ui.category.TreeCatProvider;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.ClassParameter;
import com.arsdigita.util.parameter.URLParameter;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import java.lang.reflect.Constructor;

/**
 * Configuration record for the navigation app
 * @author Daniel Berrange
 */
public final class NavigationConfig extends AbstractConfig {
    private static final Logger s_log = Logger.getLogger(NavigationConfig.class);

    private final Parameter m_indexPageCacheLifetime;
    private final Parameter m_generateItemURL;
    private final Parameter m_defaultTemplate;
    private final Parameter m_inheritTemplates;
    private final Parameter m_defaultContentSectionURL;
    private final Parameter m_relatedItemsContext;
    private final Parameter m_defaultModelClass;
    private final Parameter m_defaultCatRootPath;
    private final Parameter m_relatedItemsFactory;
    private final Parameter m_traversalAdapters;
    private final Parameter m_categoryMenuShowNephews;
    private final Parameter m_categoryMenuShowGrandChildren;
    // Quasimodo: Begin
    private final Parameter m_categoryMenuShowGrandChildrenMax;
    private final Parameter m_categoryMenuShowGrandChildrenMin;
    private final Parameter m_categoryMenuShowGrandChildrenLimit;
    // Quasimodo: End
    private final Parameter m_dateOrderCategories;
    private final Parameter m_topLevelDateOrderCategories;
    private final Parameter m_defaultMenuCatProvider;	
	
    private static Set s_fixedDateOrderCats = null;

    private Category m_defaultCategoryRoot = null;

    private NavigationModel m_defaultModel = null;

    private TreeCatProvider m_treeCatProvider = null;

    public NavigationConfig() {
        m_indexPageCacheLifetime = new IntegerParameter
            ("com.arsdigita.london.navigation.index_page_cache_lifetime",
             Parameter.REQUIRED, new Integer(3600));
        m_generateItemURL = new BooleanParameter
            ("com.arsdigita.london.navigation.generate_item_url",
             Parameter.REQUIRED, new Boolean(true));
        m_defaultTemplate = new StringParameter
            ("com.arsdigita.london.navigation.default_template",
             Parameter.REQUIRED, "/packages/navigation/templates/default.jsp");
	// not desirable default value (IMHO) but retains existing behaviour
	m_inheritTemplates = new BooleanParameter
	    ("com.arsdigita.london.navigation.inherit_templates",
	     Parameter.REQUIRED, new Boolean(true));
        m_defaultContentSectionURL = new StringParameter
            ("com.arsdigita.london.navigation.default_content_section_url",
             Parameter.REQUIRED, "/content/");
        m_relatedItemsContext = new StringParameter
            ("com.arsdigita.london.navigation.related_items_context",
             Parameter.REQUIRED, "subject");
        m_defaultModelClass = new StringParameter
            ("com.arsdigita.london.navigation.default_nav_model",
             Parameter.REQUIRED, ApplicationNavigationModel.class.getName());
        m_defaultCatRootPath = new StringParameter
            ("com.arsdigita.london.navigation.default_cat_root_path",
             Parameter.REQUIRED, "/navigation/");
        m_relatedItemsFactory = new ClassParameter
            ("com.arsdigita.london.navigation.related_items_factory",
             Parameter.REQUIRED, RelatedItemsQueryFactoryImpl.class);
        try {
            m_traversalAdapters = new URLParameter
                ("com.arsdigita.london.navigation.traversal_adapters", 
                 Parameter.REQUIRED,
                 new URL(null,
                         "resource:WEB-INF/resources/navigation-adapters.xml"));
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException("Cannot parse URL", ex);
        }
        m_categoryMenuShowNephews = new BooleanParameter
            ("com.arsdigita.london.navigation.category_menu_show_nephews",
             Parameter.OPTIONAL, new Boolean(false));

        // Quasimodo: Begin
        m_categoryMenuShowGrandChildren = new StringParameter
            ("com.arsdigita.london.navigation.category_menu_show_grand_children",
             Parameter.OPTIONAL, "false");
        m_categoryMenuShowGrandChildrenMax = new IntegerParameter
                ("com.arsdigita.london.navigation.category_menu_show_grand_children_max",
                Parameter.OPTIONAL, new Integer(0));
        m_categoryMenuShowGrandChildrenMin = new IntegerParameter
                ("com.arsdigita.london.navigation.category_menu_show_grand_children_min",
                Parameter.OPTIONAL, new Integer(1));
        m_categoryMenuShowGrandChildrenLimit = new IntegerParameter
                ("com.arsdigita.london.navigation.category_menu_show_grand_children_limit",
                Parameter.OPTIONAL, new Integer(1));
	// Quasimodo: End
	m_dateOrderCategories = new StringArrayParameter
	    ("com.arsdigita.london.navigation.date_order_categories",
	     Parameter.OPTIONAL, new String[0]);
	m_topLevelDateOrderCategories = new StringArrayParameter
	    ("com.arsdigita.london.navigation.top_level_date_order_categories",
	     Parameter.OPTIONAL, new String[0]);
	m_defaultMenuCatProvider = new ClassParameter
	    ("com.arsdigita.london.navigation.default_menu_cat_provider",
	     Parameter.OPTIONAL, null);

        register(m_indexPageCacheLifetime);
        register(m_generateItemURL);
        register(m_defaultTemplate);
	register(m_inheritTemplates);
        register(m_defaultContentSectionURL);
        register(m_relatedItemsContext);
        register(m_defaultModelClass);
        register(m_defaultCatRootPath);
        register(m_relatedItemsFactory);
        register(m_traversalAdapters);
        register(m_categoryMenuShowNephews);
        register(m_categoryMenuShowGrandChildren);
        // Quasimodo: Begin
        register(m_categoryMenuShowGrandChildrenMax);
        register(m_categoryMenuShowGrandChildrenMin);
        register(m_categoryMenuShowGrandChildrenLimit);
        // Quasimodo: End
	register(m_dateOrderCategories);
	register(m_topLevelDateOrderCategories);
	register(m_defaultMenuCatProvider);
        loadInfo();

        // Quasimodo: Begin
        // Checking Paramter
        String param = new String((String)get(m_categoryMenuShowGrandChildren));
        if( param.equals("false") || param.equals("adaptive") || param.equals("true")) {
            set(m_categoryMenuShowGrandChildren, param);
        } else {
            s_log.error("com.arsdigita.london.navigation.category_menu_show_grand_children: Invalid setting " + param + ". Falling back to false.");
            set(m_categoryMenuShowGrandChildren, "false");            
        }
        // Quasimodo: End
    }

    public final long getIndexPageCacheLifetime() {
        return ((Integer)get(m_indexPageCacheLifetime)).longValue();
    }

    public final boolean getGenerateItemURL() {
        return ((Boolean)get(m_generateItemURL)).booleanValue();
    }

    public final String getDefaultTemplate() {
        return (String)get(m_defaultTemplate);
    }

    public final boolean inheritTemplates() {
	return ((Boolean) get(m_inheritTemplates)).booleanValue();
    }

    public final String getDefaultContentSectionURL() {
        return (String)get(m_defaultContentSectionURL);
    }

    public final String getRelatedItemsContext() {
        return (String)get(m_relatedItemsContext);
    }

    public final String getDefaultCategoryRootPath() {
        return (String)get(m_defaultCatRootPath);
    }

    public final Class getRelatedItemsFactory() {
        return (Class)get(m_relatedItemsFactory);
    }

    public final synchronized NavigationModel getDefaultModel() {
        if (null != m_defaultModel) {
            return m_defaultModel;
        }

        String defaultModelClassName = (String)get(m_defaultModelClass);
        try {
            Class defaultModelClass = Class.forName(defaultModelClassName);
            Constructor cons = defaultModelClass.getConstructor
                ( new Class[]{} );
            m_defaultModel = (NavigationModel) cons.newInstance
                ( new Object[]{} );
        } catch (Exception ex) {
            throw new UncheckedWrapperException( ex );
        }

        return m_defaultModel;
    }

    public final synchronized Category getDefaultCategoryRoot() {
        if (null != m_defaultCategoryRoot) { 
            return m_defaultCategoryRoot;
        }

        String defaultCatRootPath = (String)get(m_defaultCatRootPath);

        Application app =
            Application.retrieveApplicationForPath(defaultCatRootPath);
        Assert.exists(app, Application.class);

        Category m_defaultCategoryRoot = Category.getRootForObject(app);
        Assert.exists(m_defaultCategoryRoot, Category.class);

        return m_defaultCategoryRoot;
    }

    InputStream getTraversalAdapters() {
        try {
            return ((URL)get(m_traversalAdapters)).openStream();
        } catch (IOException ex) {
            throw new UncheckedWrapperException("Cannot read stream", ex);
        }
    }
    
    public final boolean getCategoryMenuShowNephews() {
        return ((Boolean)get(m_categoryMenuShowNephews)).booleanValue();
	}

    // Quasimodo: Begin
    public final String getCategoryMenuShowGrandChildren() {
        return (String)get(m_categoryMenuShowGrandChildren);
    	}
    public final long getCategoryMenuShowGrandChildrenMax()   {
        return ((Integer)get(m_categoryMenuShowGrandChildrenMax)).longValue();
    	}
    public final long getCategoryMenuShowGrandChildrenMin()   {
        return ((Integer)get(m_categoryMenuShowGrandChildrenMin)).longValue();
    	}
    public final long getCategoryMenuShowGrandChildrenLimit()   {
        return ((Integer)get(m_categoryMenuShowGrandChildrenLimit)).longValue();
    	}
    // Quasimodo: End
    
    /**
     * retrieve a collection of categories to order by date. Collection includes 
     * categories directly specified as date ordered and also all subcategories of
     * categories specified as being top level date ordered categories
     * in config
     * @return
     */
    public final Collection getDateOrderedCategories() {
	if (s_fixedDateOrderCats == null) {
  	    populateFixedDateOrderCats();
	}

	String[] topLevelCats = (String[]) get(m_topLevelDateOrderCategories);
	Set allCats = new HashSet();
	allCats.addAll(s_fixedDateOrderCats);
	for (int i = 0; i < topLevelCats.length; i++) {
	    try {
		String[] categoryArray = StringUtils.split(topLevelCats[i], ':');
		String order = "";
		if (categoryArray.length > 1) {
		    order = ":" + categoryArray[1];
		}
		Category topLevelCat = new Category(new BigDecimal(categoryArray[0]));
		s_log.debug("retrieved top level category " + topLevelCat);
		Set childCats = new HashSet();
		CategoryCollection children = topLevelCat.getDescendants();
		while (children.next()) {
		    BigDecimal id = children.getCategory().getID();
		    childCats.add(id.toString() + order);
		}

		allCats.addAll(childCats);
	    } catch (DataObjectNotFoundException e) {
		// non existent category id specified in configuration. Output warning to 
		// logs and continue
		s_log.warn("Category with id " + topLevelCats[i] + 
		" specified in configuration as a top level date order category, but the category" +
                "does not exist");
	    } catch (NumberFormatException e) {
		// non number specified in configuration. Output warning to 
		// logs and continue
		s_log.warn("Category with id " + topLevelCats[i] + 
		" specified in configuration as a top level date order category, but this is not a valid number");
	    }

	}
	return allCats;

    }

    // synchronised because set isn't - potential to 
    // create and cache very odd set if navigation page is accessed 
    // concurrently first time after server restart
    private synchronized void populateFixedDateOrderCats() {

	String[] catArray = (String[]) get(m_dateOrderCategories);
	s_fixedDateOrderCats = new HashSet();
	for (int i = 0; i < catArray.length; i++) {
	    try {
		// don't need to do this, as including invalid or non existent 
		// ids will not have any adverse effects, but this gives us a chance to 
		// provide some warning to the users when they find that a category
		// they expected to be date ordered isn't because they mistyped etc.
		// This only occurs once, first time navigation page is accessed after 
		// server restart
		String[] category = StringUtils.split(catArray[i], ':');
		Category cat = new Category(new BigDecimal(category[0]));
	    } catch (DataObjectNotFoundException e) {
		// non existent category id specified in configuration. Output warning to 
		// logs and continue
		s_log.warn("Category with id " + catArray[i] + 
		" specified in configuration as a date ordered category, but the category" +
		"does not exist");
	    } catch (NumberFormatException e) {
		// non number specified in configuration. Output warning to 
		// logs and continue
		s_log.warn("Category with id " + catArray[i] + 
		" specified in configuration as a date ordered category, but this is not a valid number");
	    }
	    s_fixedDateOrderCats.add(catArray[i]);
	}
    }

    public final TreeCatProvider getDefaultMenuCatProvider() {
	if (null == m_treeCatProvider) {
	    try {
		Class providerClass = (Class) get(m_defaultMenuCatProvider);
		if (providerClass == null) {
		    m_treeCatProvider = new Menu();
		} else {
		    m_treeCatProvider = (TreeCatProvider) providerClass.newInstance();
		}
	    } catch (Exception ex) {
		throw new UncheckedWrapperException(ex);
	    }
	}
	return m_treeCatProvider;
    }

}
