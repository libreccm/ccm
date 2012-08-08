package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.util.UncheckedWrapperException;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericPersonAliasSetForm
        extends BasicPageForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private SaveCancelSection saveCancelSection;
    private final String ITEM_SEARCH = "personAlias";
    public static final String ID = "GenericPersonAliasSetForm";

    public GenericPersonAliasSetForm(final ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public GenericPersonAliasSetForm(final ItemSelectionModel itemModel,
                                     final GenericPersonAliasPropertiesStep step) {
        super(ID, itemModel);

        addSaveCancelSection();

        addInitListener(this);
        addProcessListener(this);
    }

    @Override
    public void addWidgets() {
        add(new Label((String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.alias.select").localize()));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(
                "com.arsdigita.cms.contenttypes.GenericPerson"));      
        itemSearch.setEditAfterCreate(false);
        add(itemSearch);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final GenericPerson person = (GenericPerson) getItemSelectionModel().
                getSelectedObject(state);

        setVisible(state, true);

        if (person != null) {
            data.put(ITEM_SEARCH, person.getAlias());
        }
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final GenericPerson person = (GenericPerson) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            GenericPerson alias = (GenericPerson) data.get(ITEM_SEARCH);

            alias = (GenericPerson) alias.getContentBundle().getInstance(person.
                    getLanguage());

            person.setAlias(alias);
            itemSearch.publishCreatedItem(data, alias);
        }

        init(fse);
    }

    @Override
    public void addSaveCancelSection() {
        try {
            getSaveCancelSection().getSaveButton().addPrintListener(new PrintListener() {

                @Override
                public void prepare(PrintEvent event) {
                    GenericPerson person =
                                  (GenericPerson) getItemSelectionModel().
                            getSelectedObject(event.getPageState());
                    Submit target = (Submit) event.getTarget();

                    if (person.getAlias() != null) {
                        target.setButtonLabel((String) ContenttypesGlobalizationUtil.
                                globalize(
                                "cms.contenttypes.ui.contact.alias.select.change").
                                localize());
                    } else {
                        target.setButtonLabel((String) ContenttypesGlobalizationUtil.
                                globalize(
                                "cms.contenttypes.ui.contact.alias.select.add").
                                localize());
                    }
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
    }

    @Override
    public void validate(final FormSectionEvent fse)
            throws FormProcessException {
        super.validate(fse);
        
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        final GenericPerson person = (GenericPerson) getItemSelectionModel().
                getSelectedObject(state);
        final GenericPerson alias = (GenericPerson) data.get(ITEM_SEARCH);

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.person.alias.select.wrong_type"));
        } else if (person.equals(data.get(ITEM_SEARCH))) {
            data.addError(ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.person.alias.select.same_as_person"));
        } else if (!alias.getContentBundle().hasInstance(person.getLanguage(),
                                                         Kernel.getConfig().
                languageIndependentItems())) {
            data.addError(
                    ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.person.alias.select.no_suitable_language_variant"));
        }
    }
}
