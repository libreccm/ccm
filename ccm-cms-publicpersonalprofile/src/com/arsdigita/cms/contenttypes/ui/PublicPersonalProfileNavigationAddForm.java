package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;

import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileNavigationAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private static final Logger logger =
                                Logger.getLogger(
            PublicPersonalProfileNavigationAddForm.class);
    private PublicPersonalProfileNavigationStep step;
    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "itemSearch";
    private ItemSelectionModel itemModel;
    private SimpleEditStep editStep;

    public PublicPersonalProfileNavigationAddForm(
            ItemSelectionModel itemModel,
            SimpleEditStep editStep) {
        super("PublicPersonalProfileNavAddForm", itemModel);
        this.itemModel = itemModel;
        this.editStep = editStep;
    }

    @Override
    public void addWidgets() {
        add(new Label((String) PublicPersonalProfileGlobalizationUtil.
                globalize("publicpersonalprofile.ui.nav.select_nav_item").
                localize()));
        ParameterModel navItemModel = new StringParameter("navItemName");
        SingleSelect navItemSelect = new SingleSelect(navItemModel);
        navItemSelect.addValidationListener(new NotNullValidationListener());
        final String[] mockNav = new String[]{"Allgemein", "Beruflich",
                                              "Forschung", "Lehre", "Projekte",
                                              "Publikationen"};
        for (String nav : mockNav) {
            navItemSelect.addOption(new Option(nav));
        }

        add(new Label((String) PublicPersonalProfileGlobalizationUtil.
                globalize("publicpersonalprofile.ui.nav.select_target").
                localize()));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH);
        add(this.itemSearch);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
    }
}
