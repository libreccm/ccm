package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactTypeCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class GenericContactAttachPersonPropertyForm extends BasicPageForm
        implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private static final Logger logger =
                                Logger.getLogger(
            GenericContactPropertyForm.class);
    private GenericContactPersonPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "contactPerson";
    private final String CONTACTS_KEY =
                         GenericPersonContactCollection.CONTACTS_KEY;
    /**
     * ID of the form
     */
    public static final String ID = "ContactAttachPerson";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public GenericContactAttachPersonPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of ContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public GenericContactAttachPersonPropertyForm(ItemSelectionModel itemModel,
                                                  GenericContactPersonPropertiesStep step) {
        super(ID, itemModel);
        addSubmissionListener(this);

        addSaveCancelSection();

        addInitListener(this);
        addSubmissionListener(this);

    }

    @Override
    public void addWidgets() {
        add(new Label((String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.contact.select_person").localize()));
        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(
                "com.arsdigita.cms.contenttypes.GenericPerson"));
        m_itemSearch.setDisableCreatePane(true);
        add(this.m_itemSearch);

        // GenericContact type field
        add(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.contact.type")));
        ParameterModel contactTypeParam = new StringParameter(CONTACTS_KEY);
        SingleSelect contactType = new SingleSelect(contactTypeParam);
        contactType.addValidationListener(new NotNullValidationListener());
        contactType.addOption(new Option("",
                                         new Label((String) ContenttypesGlobalizationUtil.
                globalize("cms.ui.select_one").localize())));

        // Add the Options to the SingleSelect widget
        GenericContactTypeCollection contacttypes =
                                     new GenericContactTypeCollection();
        contacttypes.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().
                getLanguage());

        while (contacttypes.next()) {
            RelationAttribute ct = contacttypes.getRelationAttribute();
            contactType.addOption(new Option(ct.getKey(), ct.getName()));
        }

        add(contactType);
    }

    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericContact contact = (GenericContact) getItemSelectionModel().
                getSelectedObject(state);

        setVisible(state, true);

        if (contact != null) {
            data.put(ITEM_SEARCH, contact.getPerson());
            data.put(CONTACTS_KEY, contact.getContactType());
        }
    }

    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericContact contact = (GenericContact) getItemSelectionModel().
                getSelectedObject(state);

        if (!this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            GenericPerson person = (GenericPerson) data.get(ITEM_SEARCH);

            person = (GenericPerson) person.getContentBundle().getInstance(contact.
                    getLanguage());

            contact.setPerson(person, (String) data.get(CONTACTS_KEY));
        }
        init(fse);
    }

    /**
     * Creates the section with the save and the cancel button.
     */
    @Override
    public void addSaveCancelSection() {
        try {
            getSaveCancelSection().getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    GenericContact contact =
                                   (GenericContact) getItemSelectionModel().
                            getSelectedObject(e.getPageState());
                    Submit target = (Submit) e.getTarget();

                    if (contact.getPerson() != null) {
                        target.setButtonLabel((String) ContenttypesGlobalizationUtil.
                                globalize(
                                "cms.contenttypes.ui.contact.select_person.change").
                                localize());
                    } else {
                        target.setButtonLabel((String) ContenttypesGlobalizationUtil.
                                globalize(
                                "cms.contenttypes.ui.contact.select_person.add").
                                localize());
                    }
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
    }

    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        //Calling super.validate(e) here causes an exception because the super method checks things which not available
        //here.
        
        final PageState state = e.getPageState();
        final FormData data = e.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            throw new FormProcessException((String) ContenttypesGlobalizationUtil.
                    globalize(
                    "cms.contenttypes.ui.contact.select_person.wrong_type").
                    localize());
        }

        GenericContact contact = (GenericContact) getItemSelectionModel().
                getSelectedObject(state);
        GenericPerson person = (GenericPerson) data.get(ITEM_SEARCH);

        if (!(person.getContentBundle().hasInstance(contact.getLanguage(),
                                                    Kernel.getConfig().
              languageIndependentItems()))) {
            data.addError(
                    "cms.contenttypes.ui.contact.select_person.no_suitable_language_variant");
        }

    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (getSaveCancelSection().getCancelButton().isSelected(e.getPageState())) {
            init(e);
            throw new FormProcessException((String) ContenttypesGlobalizationUtil.
                    globalize(
                    "cms.contenttypes.ui.contact.select_person.cancelled").
                    localize());
        }
    }
}
