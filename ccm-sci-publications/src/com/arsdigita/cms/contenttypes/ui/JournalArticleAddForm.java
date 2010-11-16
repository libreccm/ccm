package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.Journal;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class JournalArticleAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private static final Logger s_log = Logger.getLogger(
            JournalArticleAddForm.class);
    private JournalPropertiesStep m_step;
    private ItemSearchWidget m_itemSearchWidget;
    private final String ITEM_SEARCH = "articles";
    private ItemSelectionModel m_itemModel;

    public JournalArticleAddForm(ItemSelectionModel itemModel) {
        super("ArticlesAddForm", itemModel);
        m_itemModel = itemModel;
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
        Journal journal = (Journal) getItemSelectionModel().getSelectedObject(
                state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            journal.addArticle((ArticleInJournal) data.get(ITEM_SEARCH));
        }

        init(fse);
    }
}
