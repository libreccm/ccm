package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericPersonAliasPropertiesStep extends SimpleEditStep {

    public static final String SET_ALIAS_SHEET_NAME = "setAlias";
    public static final String CHANGE_ALIAS_SHEET_NAME = "changeAlias";
    public static final String DELETE_ALIAS_SHEET_NAME = "deleteAlias";
    private final ItemSelectionModel itemModel;

    public GenericPersonAliasPropertiesStep(final ItemSelectionModel itemModel,
                                            final AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }

    public GenericPersonAliasPropertiesStep(final ItemSelectionModel itemModel,
                                            final AuthoringKitWizard parent,
                                            final String prefix) {
        super(itemModel, parent, prefix);

        this.itemModel = itemModel;

        final BasicPageForm setAliasSheet = new GenericPersonAliasSetForm(
                itemModel,
                this);
        /*final BasicPageForm changeAliasSheet =
                            new GenericPersonAliasSetForm(itemModel,
                                                          this);*/
        final BasicPageForm deleteAliasSheet = new GenericPersonAliasDeleteForm(
                itemModel,
                this);

        add(SET_ALIAS_SHEET_NAME,
            (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.set_alias").localize(),
            new GenericPersonAliasSetWorkflowLockedComponentAccess(setAliasSheet,
                                                                   itemModel),
            setAliasSheet.getSaveCancelSection().getCancelButton());
        /*add(CHANGE_ALIAS_SHEET_NAME,
            (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.change_alias").localize(),
            new GenericPersonAliasEditWorkflowLockedComponentAccess(
                changeAliasSheet,
                itemModel),
            changeAliasSheet.getSaveCancelSection().getCancelButton());*/
        add(DELETE_ALIAS_SHEET_NAME,
            (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.delete_alias").localize(),
            new GenericPersonAliasEditWorkflowLockedComponentAccess(
                deleteAliasSheet,
                itemModel),
            deleteAliasSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getAliasPropertySheet(itemModel));
    }

    public static Component getAliasPropertySheet(
            final ItemSelectionModel itemModel) {
        final Label label = new Label(new PrintListener() {

            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();

                final GenericPerson person = (GenericPerson) itemModel.
                        getSelectedObject(state);
                final GenericPerson alias = person.getAlias();

                if (alias == null) {
                    ((Label) event.getTarget()).setLabel(ContenttypesGlobalizationUtil.
                            globalize("cms.contenttypes.ui.person.alias.none"));
                } else {
                    ((Label) event.getTarget()).setLabel(alias.getFullName());
                }
            }
        });
        
        return label;    
    }

    public static Component getEmptyAliasPropertySheet(
            final ItemSelectionModel itemModel) {
        return new Label((String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.alias.none").localize());
    }

    private class GenericPersonAliasSetWorkflowLockedComponentAccess
            extends WorkflowLockedComponentAccess {

        public GenericPersonAliasSetWorkflowLockedComponentAccess(
                final Component component, final ItemSelectionModel itemModel) {
            super(component, itemModel);
        }

        @Override
        public boolean isVisible(final PageState state) {
            final GenericPerson person = (GenericPerson) itemModel.
                    getSelectedObject(state);

            if (person.getAlias() == null) {
                return true;
            } else {
                return false;
            }
        }
    }

    private class GenericPersonAliasEditWorkflowLockedComponentAccess
            extends WorkflowLockedComponentAccess {

        public GenericPersonAliasEditWorkflowLockedComponentAccess(
                final Component component, final ItemSelectionModel itemModel) {
            super(component, itemModel);
        }

        @Override
        public boolean isVisible(final PageState state) {
            final GenericPerson person = (GenericPerson) itemModel.
                    getSelectedObject(state);

            if (person.getAlias() == null) {
                return false;
            } else {
                return true;
            }
        }
    }
}