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
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ContentPage;
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
 * @author Tobias Osmers <tosmers@uni-bremen.de>
 * @version $Revision: #1 $ $Date: 2015/02/22 $
 */
public class ExternalLinkPropertyForm extends BasicPageForm
    implements FormProcessListener, FormInitListener {

    /**
     * Name of this form
     */
    public static final String ID = "externallinkform_edit";
    // formerly "externalLinkEdit"

    private TextArea description;
    private TextField url;
    private TextArea comment;
    private CheckboxGroup showCommentCheckBox, targetWindowCheckBox;

    /**
     * Creates a new form to edit the ExternalLink object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the ExternalLink
     *                  to work on
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

        description = new TextArea(new TrimmedStringParameter(
            ContentPage.DESCRIPTION), 5, 40, TextArea.SOFT);
        description.setLabel(ExternalLinkGlobalizationUtil.globalize(
            "cms.contenttypes.externallink.description"));
        description.setHint(ExternalLinkGlobalizationUtil.globalize(
            "cms.contenttypes.externallink.description_hint"));
        add(description);

        url = new TextField(new TrimmedStringParameter(ExternalLink.URL));
        url.setLabel(ExternalLinkGlobalizationUtil.globalize(
            "cms.contenttypes.externallink.location"));
        url.setHint(ExternalLinkGlobalizationUtil.globalize(
            "cms.contenttypes.externallink.location_hint"));
        url.setSize(40);
        add(url);

        comment = new TextArea(new TrimmedStringParameter(
            ExternalLink.COMMENT), 5, 40, TextArea.SOFT);
        comment.setLabel(ExternalLinkGlobalizationUtil.globalize(
            "cms.contenttypes.externallink.comment"));
        comment.setHint(ExternalLinkGlobalizationUtil.globalize(
            "cms.contenttypes.externallink.comment_hint"));
        add(comment);

        Option showComment = new Option(ExternalLink.SHOW_COMMENT,
                                        new Label(ExternalLinkGlobalizationUtil
                                            .globalize(
                                                "cms.contenttypes.externallink.show_comment")));
        showCommentCheckBox = new CheckboxGroup("showCommentCheckBox");
        showCommentCheckBox.addOption(showComment);
        add(showCommentCheckBox);

        Option targetWindow = new Option(ExternalLink.TARGET_WINDOW,
                                         new Label(ExternalLinkGlobalizationUtil
                                             .globalize(
                                                 "cms.contenttypes.externallink.target_window")));
        targetWindowCheckBox = new CheckboxGroup("targetWindowCheckBox");
        targetWindowCheckBox.addOption(targetWindow);
        add(targetWindowCheckBox);
    }

    /**
     * Form initialisation hook. Fills widgets with data.
     *
     * @param fse FormSectionEvent provided by caller
     */
    @Override
    public void init(final FormSectionEvent fse) {
        final ExternalLink extLink = (ExternalLink) super.initBasicWidgets(fse);

        final PageState state = fse.getPageState();
        description.setValue(state, extLink.getDescription());
        url.setValue(state, extLink.getURL());
        comment.setValue(state, extLink.getComment());
        if (extLink.getShowComment() != null 
                && extLink.getShowComment()) {
            showCommentCheckBox.setValue(
                state, new String[]{ExternalLink.SHOW_COMMENT});
        } else {
            showCommentCheckBox.setValue(state, null);
        }

        if (extLink.getTargetNewWindow() != null 
                && extLink.getTargetNewWindow()) {
            targetWindowCheckBox.setValue(
                state, new String[]{ExternalLink.TARGET_WINDOW});
        } else {
            targetWindowCheckBox.setValue(state, null);
        }
    }

    /**
     * Form processing hook. Saves ExternalLink object.
     *
     * @param fse FormSectionEvent provided by caller
     */
    @Override
    public void process(final FormSectionEvent fse) {
        final ExternalLink extLink = (ExternalLink) super.processBasicWidgets(
            fse);

        final PageState state = fse.getPageState();
        // save only if save button was pressed
        if (extLink != null && getSaveCancelSection().getSaveButton()
            .isSelected(state)) {
            extLink.setDescription((String) description.getValue(state));
            extLink.setURL((String) url.getValue(state));
            extLink.setComment((String) comment.getValue(state));
            // Process whether a comment will be shown
            boolean showComment = false;
            String[] value = (String[]) showCommentCheckBox.getValue(state);
            if (value != null) {
                showComment = ExternalLink.SHOW_COMMENT.equals(value[0]);
            }

            extLink.setShowComment(showComment);

            // Process whether the external link will be opened in a new 
            // window
            boolean newWindow = false;
            value = (String[]) targetWindowCheckBox.getValue(state);
            if (value != null) {
                newWindow = ExternalLink.TARGET_WINDOW.equals(value[0]);
            }

            extLink.setTargetNewWindow(newWindow);

        }
    }

}
