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
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.contenttypes.GenericAddress;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class GenericContactAttachAddressPropertyForm extends BasicPageForm
        implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private static final Logger logger =
                                Logger.getLogger(
            GenericContactPropertyForm.class);
    private GenericContactAddressPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "contactAddress";
    /**
     * ID of the form
     */
    public static final String ID = "ContactAttachAddress";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public GenericContactAttachAddressPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of ContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public GenericContactAttachAddressPropertyForm(ItemSelectionModel itemModel,
                                                   GenericContactAddressPropertiesStep step) {
        super(ID, itemModel);
        addSubmissionListener(this);

        addSaveCancelSection();

        addInitListener(this);
        addSubmissionListener(this);

    }

    @Override
    public void addWidgets() {
        add(new Label(ContenttypesGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.contact.select_address")));
        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(
                "com.arsdigita.cms.contenttypes.GenericAddress"));
        m_itemSearch.setDisableCreatePane(true);        
        add(this.m_itemSearch);
    }

    @Override
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericContact contact = (GenericContact) getItemSelectionModel().
                getSelectedObject(state);

        setVisible(state, true);

        if (contact != null) {
            data.put(ITEM_SEARCH, contact.getAddress());
        }
    }

    @Override
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericContact contact = (GenericContact) getItemSelectionModel().
                getSelectedObject(state);

        if (!this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            GenericAddress address = (GenericAddress) data.get(ITEM_SEARCH);

            address = (GenericAddress) address.getContentBundle().getInstance(
                    contact.getLanguage());

            contact.setAddress(address);
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

                @Override
                public void prepare(PrintEvent e) {
                    GenericContact contact =
                                   (GenericContact) getItemSelectionModel().
                            getSelectedObject(e.getPageState());
                    Submit target = (Submit) e.getTarget();

                    if (contact.getAddress() != null) {
                        target.setButtonLabel(ContenttypesGlobalizationUtil.
                                globalize(
                                "cms.contenttypes.ui.contact.select_address.change"));
                    } else {
                        target.setButtonLabel(ContenttypesGlobalizationUtil.
                                globalize(
                                "cms.contenttypes.ui.contact.select_address.add"));
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
                    "cms.contenttypes.ui.contact.select_address.wrong_type").
                    localize());
        }
                
        GenericContact contact = (GenericContact) getItemSelectionModel().
                getSelectedObject(state);
        
          GenericAddress address = (GenericAddress) data.get(ITEM_SEARCH);
          
        if (!(address.getContentBundle().hasInstance(contact.getLanguage(),
                                                     Kernel.getConfig().
              languageIndependentItems()))) {
              data.addError( ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.contact.select_address.no_suitable_language_variant"));
          }
    }

    @Override
    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (getSaveCancelSection().getCancelButton().isSelected(e.getPageState())) {
            init(e);
            throw new FormProcessException((String) ContenttypesGlobalizationUtil.
                    globalize(
                    "cms.contenttypes.ui.contact.select_address.cancelled").
                    localize());
        }
    }
}