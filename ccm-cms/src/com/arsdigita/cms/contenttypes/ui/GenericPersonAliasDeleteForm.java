package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.util.UncheckedWrapperException;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericPersonAliasDeleteForm
        extends BasicPageForm
        implements FormProcessListener {

    public static final String ID = "GenericPersonAliasDeleteForm";

    public GenericPersonAliasDeleteForm(
            final ItemSelectionModel itemModel,
            final GenericPersonAliasPropertiesStep step) {
        super(ID, itemModel);
        addSaveCancelSection();
    }

    @Override
    public void addWidgets() {
        add(new Label((String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.alias.delete.label").localize()));
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final GenericPerson person = (GenericPerson) getItemSelectionModel().getSelectedObject(state);
        
        if ((person != null) && (person.getAlias() != null)) {
            person.unsetAlias();
        }
    }

    @Override
    public void addSaveCancelSection() {
        try {
            getSaveCancelSection().getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(final PrintEvent event) {
                    GenericPerson person =
                                   (GenericPerson) getItemSelectionModel().
                            getSelectedObject(event.getPageState());
                    Submit target = (Submit) event.getTarget();
                    target.setButtonLabel((String) ContenttypesGlobalizationUtil.
                            globalize(
                            "cms.contenttypes.ui.person.alias.delete.label").
                            localize());
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
    }
}
