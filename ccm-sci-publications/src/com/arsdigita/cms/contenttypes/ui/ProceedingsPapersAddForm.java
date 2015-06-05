/*
 * Copyright (c) 2010 Jens Pelzetter
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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InProceedingsCollection;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.PublicationsConfig;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ProceedingsPapersAddForm
    extends BasicItemForm
    implements FormProcessListener,
               FormInitListener {

    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "papers";
    private ItemSelectionModel m_itemModel;
    private final static PublicationsConfig config = new PublicationsConfig();

    static {
        config.load();
    }

    public ProceedingsPapersAddForm(ItemSelectionModel itemModel) {
        super("PapersAddForm", itemModel);
        m_itemModel = itemModel;
    }

    @Override
    protected void addWidgets() {
        m_itemSearch = new ItemSearchWidget(
            ITEM_SEARCH,
            ContentType.findByAssociatedObjectType(
                InProceedings.class.getName()));
        m_itemSearch.setDefaultCreationFolder(config
            .getDefaultInProceedingsFolder());
        m_itemSearch.setLabel(PublicationGlobalizationUtil.globalize(
            "publications.ui.proceedings.select_paper"));
        add(m_itemSearch);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        Proceedings proceedings = (Proceedings) getItemSelectionModel().
            getSelectedObject(state);

        if (!(this.getSaveCancelSection().getCancelButton().
              isSelected(state))) {
            InProceedings paper = (InProceedings) data.get(ITEM_SEARCH);
            paper = (InProceedings) paper.getContentBundle().getInstance(
                proceedings.
                getLanguage());

            proceedings.addPaper(paper);
            m_itemSearch.publishCreatedItem(data, paper);
        }

        init(fse);
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(
                PublicationGlobalizationUtil.globalize(
                    "publications.ui.proceedings.select_paper.no_paper_selected"));
            return;
        }

        Proceedings proceedings = (Proceedings) getItemSelectionModel().
            getSelectedObject(state);
        InProceedings paper = (InProceedings) data.get(ITEM_SEARCH);
        if (!(paper.getContentBundle().hasInstance(proceedings.getLanguage(),
                                                   Kernel.getConfig().
                                                   languageIndependentItems()))) {
            data.addError(
                PublicationGlobalizationUtil.globalize(
                    "publications.ui.proceedings.select_paper.no_suitable_language_variant"));
            return;
        }

        paper = (InProceedings) paper.getContentBundle().getInstance(proceedings
            .getLanguage());
        InProceedingsCollection papers = proceedings.getPapers();
        papers.addFilter(String.format("id = %s", paper.getContentBundle()
                                       .getID().toString()));
        if (papers.size() > 0) {
            data.addError(
                PublicationGlobalizationUtil.globalize(
                    "publications.ui.proceedings.select_paper.already_added"));
            return;
        }
    }

}
