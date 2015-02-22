/*
 * Copyright (C) 2015 University of Bremen. All Rights Reserved.
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

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.contenttypes.ExternalLink;
import com.arsdigita.cms.contenttypes.util.ExternalLinkGlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 * Form to edit the basic properties of an ExternalLink.
 *
 * Used by <code>ExternalLinkPropertiesStep</code> authoring kit step.
 * <br />
 * This form can be extended to create forms for ExternalLink subclasses.
 * 
 * @author tosmers
 * @version $Revision: #1 $ $Date: 2015/02/22 $
 */
public class ExternalLinkPropertyForm extends BasicPageForm
        implements FormProcessListener, FormInitListener {

    /**
     * Name of this form
     */
    public static final String ID = "externallinkform_edit";  
    // formerly "externalLinkEdit"

    private TextField url;
    private TextArea description;

    /**
     * Creates a new form to edit the ExternalLink object specified by the 
     * item selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the 
     *                  ExternalLink to work on
     */
    public ExternalLinkPropertyForm(final ItemSelectionModel itemModel) {
        super(ID, itemModel);
    }

    /**
     * Adds widgets to the forms basic properties (name and title).
     */
    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label(ExternalLinkGlobalizationUtil.globalize("cms.contenttypes.ExternalLink.location")));
        url = new TextField(ExternalLink.URL);
        url.setSize(40);
        add(url);

        add(new Label(ExternalLinkGlobalizationUtil.globalize("cms.contenttypes.ExternalLink.description")));
        description = new TextArea(ExternalLink.DESCRIPTION, 5, 40, TextArea.SOFT);
        add(description);
    }

    /**
     * Form initialisation hook. Fills widgets with data.
     *
     * @param fse FormSectionEvent provided by caller
     */
    @Override
    public void init(final FormSectionEvent fse) {
        final ExternalLink site = (ExternalLink) super.initBasicWidgets(fse);

        final PageState state = fse.getPageState();
        url.setValue(state, site.getURL());
        description.setValue(state, site.getDescription());
    }

    /**
     * Form processing hook. Saves ExternalLink object.
     *
     * @param fse FormSectionEvent provided by caller
     */
    @Override
    public void process(final FormSectionEvent fse) {
        final ExternalLink site = (ExternalLink) super.processBasicWidgets(fse);

        final PageState state = fse.getPageState();
        // save only if save button was pressed
        if (site != null
            && getSaveCancelSection().getSaveButton().isSelected(state)) {
            site.setURL((String) url.getValue(state));
            site.setDescription((String) description.getValue(state));
        }
    }

}
