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
package com.arsdigita.cms.contenttypes.ui.mparticle;


import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.MultiPartArticle;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.contenttypes.util.MPArticleGlobalizationUtil;
import com.arsdigita.util.Assert;



/**
 * Worker class to create the multipart article's edit form.
 * 
 */
public class MultiPartArticleEditForm extends MultiPartArticleForm 
                                      implements FormSubmissionListener {
        
    private final SimpleEditStep m_step;

    /**
     * Constructor. 
     * 
     * @param itemModel
     * @param step 
     */
    public MultiPartArticleEditForm(ItemSelectionModel itemModel, 
                                    SimpleEditStep step) {
        super("MultiPartArticleEditForm", itemModel);
        addSubmissionListener(this);
        m_step = step;
    }
        
    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        super.initBasicWidgets(e);
    }

    /** 
     * Cancels streamlined editing.
     * @param fse */
    @Override
    public void submitted( FormSectionEvent fse ) {
        if (getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        MultiPartArticle article = processBasicWidgets(e);
        m_step.maybeForwardToNextStep(e.getPageState());
    }

    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        FormData  data  = e.getFormData();

        MultiPartArticle article =
            (MultiPartArticle)m_itemModel.getSelectedObject(state);
        Assert.exists(article, MultiPartArticle.class);

        String newName = (String)data.get(MultiPartArticleForm.NAME);
        String oldName = article.getName();

        boolean valid = true;
        if ( !newName.equalsIgnoreCase(oldName) ) {
            Folder parent = getParentFolder(article);
            valid = validateNameUniqueness(parent, e);
        }

        if ( !valid ) {
            throw new FormProcessException
                ("An item with name already exists",
                 MPArticleGlobalizationUtil.globalize(
                                    "cms.contenttypes.ui.mparticle." + 
                                    "an_item_with_name_already_exists"));
        }
    }

    private Folder getParentFolder(MultiPartArticle article) {
        ContentItem parent = (ContentItem) article.getParent();
        while (parent != null && !(parent instanceof Folder)) {
            parent = (ContentItem) parent.getParent();
        }

        return (Folder) parent;
    }

}
