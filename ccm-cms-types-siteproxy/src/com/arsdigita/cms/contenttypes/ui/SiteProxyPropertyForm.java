/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.contenttypes.SiteProxy;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.SiteProxyGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 * Form to edit the basic properties of an SiteProxy. This form can be
 * extended to create forms for SiteProxy subclasses.
 */
public class SiteProxyPropertyForm extends BasicPageForm
        implements FormProcessListener,
                   FormInitListener {

    private TextField m_url;

    /**
     * Creates a new form to edit the SiteProxy object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the 
     *    SiteProxy to work on
     */
    public SiteProxyPropertyForm(ItemSelectionModel itemModel) {
        super("siteProxyEdit", itemModel);
    }

    /**
     * Adds widgets to the form.
     */
    @Override
    protected void addWidgets() {

        super.addWidgets();

        ParameterModel urlParam = new StringParameter("url");
        m_url.setLabel(SiteProxyGlobalizationUtil
                       .globalize("cms.contenttypes.ui.siteproxy.url"));
        m_url = new TextField(urlParam);
        m_url.setSize(40);
        add(m_url);
    }

    /** 
     * Form initialisation hook. Fills widgets with data. 
     * @param fse
     */
    @Override
    public void init(FormSectionEvent fse) {
        SiteProxy site = (SiteProxy) super.initBasicWidgets(fse);

        m_url.setValue(fse.getPageState(),
                       site.getURL());
    }

    /** 
     * Form processing hook. Saves SiteProxy object. 
     * @param fse
     */
    @Override
    public void process(FormSectionEvent fse) {
        SiteProxy site = (SiteProxy) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (site != null
            && getSaveCancelSection().getSaveButton()
                .isSelected(fse.getPageState())) {
            site.setURL((String) m_url.getValue(fse.getPageState()));
            site.save();
        }
    }

}
