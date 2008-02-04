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

package com.arsdigita.cms.contenttypes.xmlfeed.ui;


import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.formbuilder.FormProperties;
import com.arsdigita.cms.contenttypes.xmlfeed.XMLFeed;


public class XMLFeedProperties extends FormProperties {

    public XMLFeedProperties(ItemSelectionModel model,
                             AuthoringKitWizard parent) {
        super(model, parent);
    }

    protected BasicPageForm buildEditForm(ItemSelectionModel model) {
        return new XMLFeedPropertyEditForm(model);
    }

    protected Component buildDisplayComponent(ItemSelectionModel model) {
        return new XMLFeedPropertySheet(model);
    }

    protected class XMLFeedPropertyEditForm extends FormPropertyEditForm {

        private TextField m_url;

        public XMLFeedPropertyEditForm(ItemSelectionModel model) {
            super(model);
        }

        protected void addWidgets() {
            super.addWidgets();
            
            m_url = new TextField(new StringParameter("url"));
            m_url.setHint("Enter the URL of the feed, " + 
                          "eg http://upmystreet.co.uk/xml/education.php3");
            m_url.setSize(50);
            add(new Label("Feed URL:"));
            add(m_url);
        }

        public ContentPage initBasicWidgets(FormSectionEvent e) {
            XMLFeed item = (XMLFeed)super.initBasicWidgets(e);

            m_url.setValue(e.getPageState(), item.getURL());
            return item;
        }

        
        public ContentPage processBasicWidgets(FormSectionEvent e) {
            XMLFeed item = (XMLFeed)super.processBasicWidgets(e);

            item.setURL((String)m_url.getValue(e.getPageState()));
            return item;
        }
    }

    protected class XMLFeedPropertySheet extends FormPropertySheet {
        public XMLFeedPropertySheet(ItemSelectionModel model) {
            super(model);

            add("URL:",  XMLFeed.URL);
        }
    }

}
