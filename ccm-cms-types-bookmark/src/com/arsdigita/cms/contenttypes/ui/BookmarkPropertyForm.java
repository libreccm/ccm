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

import com.arsdigita.cms.contenttypes.Bookmark;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.BookmarkGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * to edit BasicPageForm the basic properties of an Bookmark. This form can be 
 * extended to create forms for Bookmarksubclasses.
 */
public class BookmarkPropertyForm extends BasicPageForm 
                                  implements FormProcessListener, 
                                             FormInitListener {

    /**
     * parameter names
     */
    public static final String DESCRIPTION = "description";
    public static final String URL = "url";
    /**
     * Name of this form
     */
    public static final String ID = "Bookmark_edit";
    
    /**
     * AuthoringKit step managing this form
     */
    private final BookmarkPropertiesStep step;

    /**
     * Creates a new form to edit the Bookmark object specified by the 
     * item selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the Bookmark 
     *                  to work on
     * @param step
     */
    public BookmarkPropertyForm(final ItemSelectionModel itemModel, 
                                final BookmarkPropertiesStep step) {
        super(ID, itemModel);
        this.step = step;
    }

    /**
     * Adds widgets to the form.
     */
    @Override
    protected void addWidgets() {
        super.addWidgets();

      //add(new Label(GlobalizationUtil.globalize(
      //        "cms.contenttypes.ui.summary")));
        final ParameterModel descriptionParam = new StringParameter(DESCRIPTION);
        final TextArea description = new TextArea(descriptionParam);
        description.setLabel(GlobalizationUtil.globalize(
                             "cms.contenttypes.ui.summary"));
        description.setCols(40);
        description.setRows(5);
        description.setHint(GlobalizationUtil.globalize(
                "cms.contenttypes.ui.summary_hint"));
        add(description);

   //   add(new Label(new GlobalizedMessage("cms.contenttypes.ui.bookmark.url", Bookmark.RESOURCES)));
        final ParameterModel urlParam = new StringParameter(URL);
        final TextField url = new TextField(urlParam);
        url.setLabel(BookmarkGlobalizationUtil.globalize(
                     "cms.contenttypes.ui.bookmark.url"));
        url.setSize(40);
        add(url);

    }

    /**
     * @param fse
     * Form initialisation hook. Fills widgets with data.
     */
    @Override
    public void init(final FormSectionEvent fse) {
        final FormData data = fse.getFormData();
        final Bookmark item = (Bookmark) super.initBasicWidgets(fse);

        data.put(DESCRIPTION, item.getDescription());
        data.put(URL, item.getURL());
    }

    /**
     * Form processing hook. Saves Bookmark object.
     * @param fse
     */
    @Override
    public void process(final FormSectionEvent fse) {
        final FormData data = fse.getFormData();

        final Bookmark item = (Bookmark) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (item != null
            && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
            item.setDescription((String) data.get(DESCRIPTION));
            item.setURL((String) data.get(URL));
            item.save();
            step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
