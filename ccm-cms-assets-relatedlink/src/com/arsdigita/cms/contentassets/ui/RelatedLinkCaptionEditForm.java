/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.ui.LinkSelectionModel;

/**
 *
 * @author Koalamann
 */
public class RelatedLinkCaptionEditForm extends RelatedLinkCaptionForm {

    public RelatedLinkCaptionEditForm(ItemSelectionModel itemModel,
            LinkSelectionModel link, String linkListName) {
        super(itemModel, link, linkListName);
    }

    /**
     * Init listener. For edit actions, fills the form with current data
     *
     * @param fse the FormSectionEvent
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        setVisible(state, false);
        RelatedLink link;
        if (m_linkModel.isSelected(state)) {
            link = (RelatedLink) m_linkModel.getSelectedLink(state);
            if (link.getTitle().equals("caption")) {
                //make this form visible because we are editing and it is a caption not a link
                setVisible(state, true);
            }
            m_description.setValue(state, link.getDescription());
        }
    }
}
