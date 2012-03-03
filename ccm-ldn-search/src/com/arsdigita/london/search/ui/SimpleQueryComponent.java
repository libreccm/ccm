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

package com.arsdigita.london.search.ui;

import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormModel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.london.search.SearchConfig;
import com.arsdigita.cms.ui.search.VersionFilterComponent;
import com.arsdigita.search.Search;
import com.arsdigita.search.ui.BaseQueryComponent;
import com.arsdigita.search.ui.filters.PermissionFilterComponent;
import com.arsdigita.search.ui.filters.ObjectTypeFilterComponent;
import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.filters.ContentSectionFilterSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * 
 * 
 */
public class SimpleQueryComponent extends BaseQueryComponent {

    /** Logger instance for debugging  */
    private static final Logger s_log = Logger.getLogger(SimpleQueryComponent.class);

    private StringParameter m_hiddenAllowedContentSectionsList = new 
                            StringParameter("restrictToContentSections");
    private String m_paramValue;
    private String[] contentSectionTitles;
    private boolean is_restricted;
    private Form m_form;

    /**
     * 
     * @param context 
     */
    public SimpleQueryComponent(String context) {

        if (Search.getConfig().isIntermediaEnabled() ||
            Search.getConfig().isLuceneEnabled()) {
            add(new PermissionFilterComponent(PrivilegeDescriptor.READ));
            add(new VersionFilterComponent(context));
        }

        SearchConfig config = com.arsdigita.london.search.Search.getConfig();
        s_log.debug("Array length : "+config.getSimpleRestrictToArray().length);
        if (config.getSimpleRestrictToArray().length > 0) {
            // Add unionised filter to specified item types
            s_log.info("Restricting to specified types");
            add(new ObjectTypeFilterComponent(config.getSimpleRestrictToArray()));
        }
    }


    /**
     * 
     * @param form
     * @param model 
     */
    @Override
    public void register(Form form, FormModel model) {
      s_log.debug("Adding " + m_hiddenAllowedContentSectionsList.getName() 
                            + " to form model");
      m_hiddenAllowedContentSectionsList.setPassIn(true);
      model.addFormParam(m_hiddenAllowedContentSectionsList);
      super.register(form, model);
      m_form = form;
    }

    /**
     * Gets the hidden restrictToContentSections param.
     * The param is in the form of a comma seperated list of content section 
     * names. If present the search will only return content items from these 
     * content sections.
     */
    protected String getContentSections(PageState state) {
      FormData formData = m_form.getFormData(state);
      if (formData != null) {
          ParameterData contentSectionListParam = 
                  formData.getParameter(m_hiddenAllowedContentSectionsList.getName());
          String paramValue = (String)contentSectionListParam.getValue();
          m_paramValue = paramValue;
          s_log.debug("content sections list is " + paramValue);
          is_restricted = (m_paramValue != null && !"".equals(m_paramValue));
          return (String)contentSectionListParam.getValue();
      }
      return null;
    }

    /**
     * Gets an array of content section titles for creation of an inclusion filter.
     **/
    public String[] getContentSectionsArray(PageState state) {
      String contentSections = getContentSections(state);
      if (contentSections == null) { return null; }
      StringTokenizer st = new StringTokenizer(getContentSections(state), ",");
      contentSectionTitles = new String[st.countTokens()];
      int index = 0;
        while (st.hasMoreTokens()) {
            contentSectionTitles[index] = st.nextToken().trim();
            s_log.info("Restricting to content section : "+contentSectionTitles[index]);
            index++;
        }
      return contentSectionTitles;
    }

    /**
     * Adds the content section filter to any existing filters.
     **/
    @Override
    protected FilterSpecification[] getFilters(PageState state) {
       FilterSpecification[] existingfilters = super.getFilters(state);
      List n = new ArrayList();
      try {
          // this will throw a NullPointerException if there are no existing filters!
          List filters = Arrays.asList(existingfilters); 
          n.addAll(filters);
      } catch (NullPointerException e) {
          // do we need to catch it if we're doing nothing with it?
      }
      String[] contentSections = getContentSectionsArray(state);
      if (contentSections == null) { return existingfilters; }
      ContentSectionFilterSpecification csfs = new 
                          ContentSectionFilterSpecification(contentSections);
      n.add(csfs);
      FilterSpecification[] newFilters = new FilterSpecification[n.size()];
      Iterator i = n.iterator();
      int c = 0;
      while (i.hasNext()) {
          newFilters[c] = (FilterSpecification)i.next();
          c++;
      }

      return newFilters;
    }
}

