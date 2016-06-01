package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ui.ControlButton;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ContentItem;

/**
  * Panel for managing contact informations
*/
public class GenericContactEntriesEditor extends BoxPanel {

    private final ItemSelectionModel itemModel;
    private final AuthoringKitWizard parent;
    private final StringParameter selectedEntryParam;
    private final ParameterSingleSelectionModel selectedEntry;
    private final Form contactEntryForm;
    private final GenericContactEntriesTable contactEntriesTable;

    public GenericContactEntriesEditor(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent) {
        super(BoxPanel.VERTICAL);

        this.itemModel = itemModel;
        this.parent = parent;
        selectedEntryParam = new StringParameter("selectedContactEntry");
        selectedEntry = new ParameterSingleSelectionModel(selectedEntryParam);

        contactEntryForm = new Form("contactEntryForm");
        contactEntryForm.add(new GenericContactEntryAddForm(itemModel, this, selectedEntry));
        add(contactEntryForm);

        contactEntriesTable = new GenericContactEntriesTable(itemModel,
                                                             this,
                                                             selectedEntry);
        add(contactEntriesTable);

        final ActionLink addButton = new AddButton();
        addButton.addActionListener(
            new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent event) {
                    final PageState state = event.getPageState();
                    showContactEntryForm(state);
                }
            });

        add(addButton);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(selectedEntryParam);

        page.setVisibleDefault(contactEntriesTable, true);
        page.setVisibleDefault(contactEntryForm, false);
    }

    protected void showContactEntryForm(final PageState state) {
        contactEntryForm.setVisible(state, true);
        contactEntriesTable.setVisible(state, false);
    }

    protected void hideContactEntryForm(final PageState state) {
        contactEntryForm.setVisible(state, false);
        contactEntriesTable.setVisible(state, true);

        selectedEntry.clearSelection(state);
    }

    private class AddButton extends ActionLink {

        public AddButton() {
            super(new Label(ContenttypesGlobalizationUtil.globalize(
            "cms.contenttypes.ui.contact.add_contactEntry")));
        }

        @Override
        public boolean isVisible(final PageState state) {
            if (super.isVisible(state)) {
                final SecurityManager securityManager =
                    Utilities.getSecurityManager(state);
                final ContentItem item = (ContentItem) itemModel.getSelectedObject(state);

                return securityManager.canAccess(state.getRequest(),
                                                 SecurityManager.EDIT_ITEM,
                                                 item);
            } else {
                return false;
            }
        }

    }
}
