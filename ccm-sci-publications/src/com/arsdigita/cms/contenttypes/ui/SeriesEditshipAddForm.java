/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.parameters.IncompleteDateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.EditshipCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.PublicationsConfig;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SeriesEditshipAddForm
        extends BasicItemForm
        implements FormSubmissionListener {

    private static final Logger s_log =
                                Logger.getLogger(SeriesEditshipAddForm.class);
    private SeriesPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "editors";
    private ItemSelectionModel m_itemModel;
    private SeriesEditshipStep editStep;
    private Label selectedEditorLabel;
    private final static PublicationsConfig config = new PublicationsConfig();

    static {
        config.load();
    }

    public SeriesEditshipAddForm(ItemSelectionModel itemModel,
                                 SeriesEditshipStep editStep) {
        super("EditorsEntryForm", itemModel);
        m_itemModel = itemModel;
        this.editStep = editStep;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.editship.selectEditors").localize()));
        m_itemSearch = new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(GenericPerson.class.
                getName()));
        if ((config.getDefaultAuthorsFolder() != null) && (config.getDefaultAuthorsFolder() != 0)) {
            m_itemSearch.setDefaultCreationFolder(new Folder(new BigDecimal(config.getDefaultAuthorsFolder())));
        }
        m_itemSearch.setEditAfterCreate(false);
        add(m_itemSearch);

        selectedEditorLabel = new Label("");
        add(selectedEditorLabel);

        final ParameterModel fromSkipMonthParam =
                             new BooleanParameter(
                EditshipCollection.FROM_SKIP_MONTH);
        Hidden fromSkipMonth = new Hidden(fromSkipMonthParam);
        add(fromSkipMonth);

        final ParameterModel fromSkipDayParam =
                             new BooleanParameter(
                EditshipCollection.FROM_SKIP_DAY);
        Hidden fromSkipDay = new Hidden(fromSkipDayParam);
        add(fromSkipDay);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.editship.from").localize()));
        IncompleteDateParameter fromParam =
                                new IncompleteDateParameter(
                EditshipCollection.FROM);
        fromParam.allowSkipDay(true);
        fromParam.allowSkipMonth(true);
        com.arsdigita.bebop.form.Date from = new com.arsdigita.bebop.form.Date(
                fromParam);
        Calendar today = new GregorianCalendar();
        from.setYearRange(1900, today.get(Calendar.YEAR));
        add(from);

        final ParameterModel toSkipMonthParam =
                             new BooleanParameter(
                EditshipCollection.TO_SKIP_MONTH);
        Hidden toSkipMonth = new Hidden(toSkipMonthParam);
        add(toSkipMonth);

        final ParameterModel toSkipDayParam =
                             new BooleanParameter(EditshipCollection.TO_SKIP_DAY);
        Hidden toSkipDay = new Hidden(toSkipDayParam);
        add(toSkipDay);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.editship.to").localize()));
        IncompleteDateParameter toParam =
                                new IncompleteDateParameter(
                EditshipCollection.TO);
        toParam.allowSkipMonth(true);
        toParam.allowSkipDay(true);
        com.arsdigita.bebop.form.Date to = new com.arsdigita.bebop.form.Date(
                toParam);
        to.setYearRange(1900, today.get(Calendar.YEAR));
        add(to);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();

        GenericPerson editor = editStep.getSelectedEditor();
        Date from = editStep.getSelectedEditorDateFrom();
        Date to = editStep.getSelectedEditorDateTo();

        if (editor == null) {
            m_itemSearch.setVisible(state, true);
            selectedEditorLabel.setVisible(state, false);
        } else {
            //data.put(ITEM_SEARCH, editor);
            data.put(EditshipCollection.FROM, from);
            data.put(EditshipCollection.TO, to);

            m_itemSearch.setVisible(state, false);
            selectedEditorLabel.setLabel(editor.getFullName(), state);
            selectedEditorLabel.setVisible(state, true);
        }

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final Series series =
               (Series) getItemSelectionModel().getSelectedObject(state);

        if (this.getSaveCancelSection().
                getSaveButton().isSelected(state)) {
            GenericPerson editor;
            editor = editStep.getSelectedEditor();

            if (editor == null) {
                final GenericPerson editorToAdd =
                              (GenericPerson) data.get(ITEM_SEARCH);
                editorToAdd.getContentBundle().getInstance(series.getLanguage());

                series.addEditor(editorToAdd,
                                 (Date) data.get(EditshipCollection.FROM),
                                 (Boolean) data.get(
                        EditshipCollection.FROM_SKIP_MONTH),
                                 (Boolean) data.get(
                        EditshipCollection.FROM_SKIP_DAY),
                                 (Date) data.get(EditshipCollection.TO),
                                 (Boolean) data.get(
                        EditshipCollection.TO_SKIP_MONTH),
                                 (Boolean) data.get(
                        EditshipCollection.TO_SKIP_DAY));
                m_itemSearch.publishCreatedItem(data, editorToAdd);
            } else {
                EditshipCollection editors;

                editors = series.getEditors();

                while (editors.next()) {
                    if (editors.getEditor().equals(editor)) {
                        break;
                    }
                }

                editors.setFrom((Date) data.get(EditshipCollection.FROM));
                editors.setFromSkipMonth((Boolean) data.get(
                        EditshipCollection.FROM_SKIP_MONTH));
                editors.setFromSkipDay((Boolean) data.get(
                        EditshipCollection.FROM_SKIP_DAY));
                editors.setTo((Date) data.get(EditshipCollection.TO));
                editors.setToSkipMonth((Boolean) data.get(
                        EditshipCollection.TO_SKIP_MONTH));
                editors.setToSkipDay((Boolean) data.get(
                        EditshipCollection.TO_SKIP_DAY));

                editStep.setSelectedEditor(null);
                editStep.setSelectedEditorDateFrom(null);
                editStep.setSelectedEditorDateTo(null);
                editors.close();
            }
        }

        init(fse);
    }

    @Override
    public void submitted(FormSectionEvent fse) throws FormProcessException {

        if (getSaveCancelSection().getCancelButton().isSelected(
                fse.getPageState())) {
            editStep.setSelectedEditor(null);
            editStep.setSelectedEditorDateFrom(null);
            editStep.setSelectedEditorDateTo(null);

            init(fse);
        }
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        boolean editing = false;

        if ((editStep.getSelectedEditor() == null)
            && (data.get(ITEM_SEARCH) == null)) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.editship.no_editor_selected"));
            return;
        }

        Series series =
               (Series) getItemSelectionModel().getSelectedObject(state);
        GenericPerson editor = (GenericPerson) data.get(ITEM_SEARCH);
        if (editor == null) {
            editor = editStep.getSelectedEditor();
            editing = true;
        }


        if (!editing) {
            EditshipCollection editors = series.getEditors();
            editors.addFilter(
                    String.format("id = %s", editor.getID().toString()));
            if (editors.size() > 0) {
                data.addError(PublicationGlobalizationUtil.globalize(
                        "publications.ui.series.editship.already_added"));
            }

            editors.close();
        }
    }
}
