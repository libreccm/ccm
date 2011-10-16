package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitSubordinateCollection;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 * Form for adding a subordinate organizational unit to a organizational unit.
 * Can be customized using {@link GenericOrgaUnitSubordinateOrgaUnitAddFormCustomizer}.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrganizationalUnitSubordinateOrgaUnitAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "subordinateOrgaUnits";
    private final GenericOrgaUnitSubordinateOrgaUnitAddFormCustomizer customizer;

    public GenericOrganizationalUnitSubordinateOrgaUnitAddForm(
            final ItemSelectionModel itemModel,
            final GenericOrgaUnitSubordinateOrgaUnitAddFormCustomizer customizer) {
        super("SubordinateOrgaUnitsAddForm", itemModel);
        this.customizer = customizer;
    }

    @Override
    protected void addWidgets() {
        add(new Label(customizer.getSelectSubordinateOrgaUnitLabel()));
        itemSearch = new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(
                customizer.getSubordinateOrgaUnitType()));
        add(itemSearch);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final GenericOrganizationalUnit orgaunit =
                                        (GenericOrganizationalUnit) getItemSelectionModel().
                getSelectedObject(state);

        if (getSaveCancelSection().getSaveButton().isSelected(state)) {
            GenericOrganizationalUnit subOrgaUnit =
                                      (GenericOrganizationalUnit) data.get(
                    ITEM_SEARCH);
            subOrgaUnit = (GenericOrganizationalUnit) subOrgaUnit.
                    getContentBundle().getInstance(orgaunit.getLanguage(), true);

            orgaunit.addSubordinateOrgaUnit(subOrgaUnit, customizer.getAssocType());
        }

        init(fse);
    }

    @Override
    public void validate(final FormSectionEvent fse)
            throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(customizer.getNothingSelectedMessage());
            return;
        }

        final GenericOrganizationalUnit orgaunit =
                                        (GenericOrganizationalUnit) getItemSelectionModel().
                getSelectedObject(state);
        GenericOrganizationalUnit subOrgaUnit =
                                        (GenericOrganizationalUnit) data.get(
                ITEM_SEARCH);
        if (!(subOrgaUnit.getContentBundle().hasInstance(orgaunit.getLanguage(),
                                                         true))) {
            data.addError(customizer.getNoSuitableLanguageVariantMessage());
            return;
        }
        
        subOrgaUnit = (GenericOrganizationalUnit) subOrgaUnit.getContentBundle().getInstance(orgaunit.getLanguage(), true);
        
        if (orgaunit.getID().equals(subOrgaUnit.getID())) {
            data.addError(customizer.getAddingToItselfMessage());
            return;
        }
        
        final GenericOrganizationalUnitSubordinateCollection subOrgaUnits = orgaunit.getSubordinateOrgaUnits();
        subOrgaUnits.addFilter(String.format("id = %s", subOrgaUnit.getID().toString()));
        if (subOrgaUnits.size() > 0) {
            data.addError(customizer.getAlreadyAddedMessage());
        }
        subOrgaUnits.close();        
    } 
}
