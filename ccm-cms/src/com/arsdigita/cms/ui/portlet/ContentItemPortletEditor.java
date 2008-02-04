/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.portlet;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.portal.PortletConfigFormSection;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.portal.Portlet;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.dispatcher.CMSDispatcher;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.MultilingualItemResolver;
import com.arsdigita.cms.portlet.ContentItemPortlet;

import java.net.MalformedURLException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class ContentItemPortletEditor extends PortletConfigFormSection {

    private static final Logger s_log =
        Logger.getLogger( ContentItemPortletEditor.class );

    private TextField m_url;

    private RequestLocal m_contentItem = new RequestLocal() {
        protected Object initialValue( PageState ps ) {
            String userURL = (String) m_url.getValue(ps);
            java.net.URL contextURL;
            try {
                contextURL = new java.net.URL( Web.getRequest().getRequestURL().toString() );
            } catch ( MalformedURLException ex ) {
                throw new UncheckedWrapperException( ex );
            }

            java.net.URL url;
            try {
                url = new java.net.URL( contextURL, userURL );
            } catch( MalformedURLException ex ) {
                s_log.info( "Malformed URL " + userURL );
                return null;
            }

            String dp = URL.getDispatcherPath();
            String path = url.getPath();
            if( path.startsWith( dp ) ) {
                path = path.substring(dp.length());
            }

            StringTokenizer tok = new StringTokenizer( path, "/" );
            if( !tok.hasMoreTokens() ) {
                s_log.info( "Couldn't find a content section for " + path +
                            " in " + userURL );
                return null;
            }

            String sectionPath = '/' + tok.nextToken() + '/';

            String context = ContentItem.LIVE;
            if( tok.hasMoreTokens() &&
                CMSDispatcher.PREVIEW.equals( tok.nextToken() ) ) {

                context = CMSDispatcher.PREVIEW;
            }

            ContentSectionCollection sections = ContentSection.getAllSections();
            sections.addEqualsFilter( Application.PRIMARY_URL, sectionPath );

            ContentSection section;
            if( sections.next() ) {
                section = sections.getContentSection();
                sections.close();
            } else {
                s_log.info( "Content section " + sectionPath + " in " +
                            userURL + " doesn't exist." );
                return null;
            }

            ItemResolver resolver = section.getItemResolver();

            path = path.substring( sectionPath.length() );

            if (path.endsWith(".jsp")) {
                path = path.substring(0, path.length()-4);
            }

            ContentItem item = resolver.getItem(section, path, context);
            if (item == null) {
                s_log.debug( "Couldn't resolve item " + path );
                return null;
            }

            return item.getDraftVersion();
        }
    };

    public ContentItemPortletEditor(ResourceType resType,
                                    RequestLocal parentAppRL) {
        super(resType, parentAppRL);
    }

    public ContentItemPortletEditor(RequestLocal application) {
        super(application);
    }

    public void addWidgets() {
        super.addWidgets();
        m_url = new TextField(new StringParameter("url"));
        m_url.setSize(50);
        m_url.addValidationListener(new NotNullValidationListener());
        m_url.addValidationListener(new StringInRangeValidationListener(1, 250));

        add(new Label("Item URL:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_url);
    }

    public void initWidgets(PageState state,
                            Portlet portlet)
        throws FormProcessException {
        super.initWidgets(state, portlet);

        if (portlet != null) {
            ContentItemPortlet myportlet = (ContentItemPortlet)portlet;
            ItemResolver resolver = new MultilingualItemResolver();
            ContentItem item = myportlet.getContentItem();
            if (item != null) {
                String context;
                if( item.isLive() ) {
                    item = item.getPublicVersion();
                    context = ContentItem.LIVE;
                }
                else context = CMSDispatcher.PREVIEW;

                m_url.setValue(state, resolver.generateItemURL(state, item,
                                                               item.getContentSection(),
                                                               context));
            } else {
                m_url.setValue(state, "");
            }
        }
    }

    public void validateWidgets(PageState state,
                                Portlet portlet)
        throws FormProcessException {
        super.validateWidgets(state, portlet);

        Object item = m_contentItem.get( state );
        if (item == null)
            throw new FormProcessException("cannot find content item");
    }

    public void processWidgets(PageState state,
                               Portlet portlet)
        throws FormProcessException {
        super.processWidgets(state, portlet);

        ContentItem item = (ContentItem) m_contentItem.get( state );

        ContentItemPortlet myportlet = (ContentItemPortlet)portlet;
        myportlet.setContentItem(item);
    }
}
