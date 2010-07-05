/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactType;
import com.arsdigita.cms.contenttypes.GenericContactTypeCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.dispatcher.DispatcherHelper;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitContactAddForm extends BasicItemForm {

    private final static Logger s_log = Logger.getLogger(
            GenericOrganizationalUnitContactAddForm.class);
    private GenericOrganizationalUnitPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "personAddress";
    private ItemSelectionModel m_itemModel;

    public GenericOrganizationalUnitContactAddForm(ItemSelectionModel itemModel) {
        super("ContactEntryAddForm", itemModel);
        m_itemModel = itemModel;
    }

    @Override
    protected void addWidgets() {
        add(new Label((String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.select_contact").localize()));
        m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(GenericContact.class.getName()));
        add(m_itemSearch);

        add(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.type")));
        ParameterModel contactTypeParam = new StringParameter(
                GenericOrganizationalUnitContactCollection.CONTACT_TYPE);
        SingleSelect contactType = new SingleSelect(contactTypeParam);
        contactType.addValidationListener(new NotNullValidationListener());
        contactType.addOption(new Option("", new Label((String) ContenttypesGlobalizationUtil.
                globalize("cms.ui.select_one").localize())));

        GenericContactTypeCollection contacttypes =
                                     new GenericContactTypeCollection();
        contacttypes.filterLanguage(DispatcherHelper.getNegotiatedLocale().
                getLanguage());

        while (contacttypes.next()) {
            GenericContactType ct = contacttypes.getContactType();
            contactType.addOption(new Option(ct.getKey(), ct.getName()));
        }

        add(contactType);
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
        GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) getItemSelectionModel().
                getSelectedObject(state);

        if (!(this.getSaveCancelSection().getCancelButton().isSelected(state))) {
            orgaunit.addContact((GenericContact) data.get(ITEM_SEARCH), (String) data.
                    get(GenericOrganizationalUnitContactCollection.CONTACT_TYPE));
        }

        init(fse);
    }
}
