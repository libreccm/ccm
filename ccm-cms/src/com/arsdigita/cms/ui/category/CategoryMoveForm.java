/*
 * Copyright (C) 2013 Jens Pelzetter All Rights Reserved.
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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ui.BaseTree;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class CategoryMoveForm extends CMSForm {

    public static final String CONTEXT_SELECTED = "sel_context";
    private static final String DEFAULT_USE_CONTEXT =
                                CategoryUseContextModelBuilder.DEFAULT_USE_CONTEXT;
    private final CategoryRequestLocal selectedCategory;
    private final SaveCancelSection saveCancelSection;
    private final ChangeListener changeListener;
    //private final SingleSelectionModel selectionModel;
    private final Tree categoryTree;

    public CategoryMoveForm(final CategoryRequestLocal selectedCategory,
                            final SingleSelectionModel contextModel) {

        super("MoveCategory");
        setMethod(Form.POST);
        this.selectedCategory = selectedCategory;

        //final Label header = new Label(GlobalizationUtil.globalize("cms.ui.category.move"));
        final Label header = new Label();
        header.addPrintListener(new PrintListener() {
            @Override
            public void prepare(final PrintEvent event) {
                final String[] args = new String[1];
                args[0] = selectedCategory.getCategory(event.getPageState()).getName();

                final Label target = (Label) event.getTarget();
                target.setLabel(GlobalizationUtil.globalize("cms.ui.move.category", args));
            }

        });

        header.setFontWeight(Label.BOLD);
        add(header, ColumnPanel.FULL_WIDTH);

        changeListener = new TreeChangeListener();
        //selectionModel = new ParameterSingleSelectionModel(new StringParameter("selectedCategory"));
        categoryTree = new BaseTree(new CategoryTreeModelBuilder(contextModel));
        categoryTree.addChangeListener(changeListener);

        add(categoryTree);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
        addSubmissionListener(new FormSecurityListener(
                com.arsdigita.cms.SecurityManager.CATEGORY_ADMIN));

    }

    protected Submit getCancelButton() {
        return saveCancelSection.getCancelButton();
    }

    protected Category getCategory(final PageState state) {
        final Category category = selectedCategory.getCategory(state);
        Assert.exists(category);
        return category;
    }

    private class TreeChangeListener implements ChangeListener {

        public TreeChangeListener() {
            //Nothing
        }

        @Override
        public void stateChanged(final ChangeEvent event) {
            //Nothing for now
        }

    }

    private class InitListener implements FormInitListener {

        public InitListener() {
            //Nothing
        }

        @Override
        public void init(final FormSectionEvent event) throws FormProcessException {
            //Nothing
        }

    }

    private class ProcessListener implements FormProcessListener {

        public ProcessListener() {
            //Nothing
        }

        @Override
        public void process(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();
            if (saveCancelSection.getSaveButton().isSelected(state)
                && !(categoryTree.getSelectedKey(state).equals(selectedCategory.getCategory(state).getID().toString()))) {

                final Category categoryToMove = selectedCategory.getCategory(state);
                final String targetKey = (String) categoryTree.getSelectedKey(state);
                final Category target = new Category(new BigDecimal(targetKey));

                final CategoryCollection parents = categoryToMove.getParents();
                while (parents.next()) {
                    final Category parent = parents.getCategory();
                    parent.removeChild(categoryToMove);
                    parent.save();
                }

                target.addChild(categoryToMove);
                categoryToMove.setDefaultParentCategory(target);

                target.save();
                categoryToMove.save();
            }

            categoryTree.clearSelection(state);
            categoryTree.clearExpansionState(state);
        }

    }
}
