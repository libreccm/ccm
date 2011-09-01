package com.arsdigita.cms.publicpersonalprofile.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItem;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItemCollection;
import com.arsdigita.cms.contenttypes.ui.PublicPersonalProfileGlobalizationUtil;
import com.arsdigita.cms.util.LanguageUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Pair;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileNavItemsAddForm
        extends FormSection
        implements FormInitListener,
                   FormProcessListener,
                   FormValidationListener,
                   FormSubmissionListener {

    private final FormSection widgetSection;
    private final SaveCancelSection saveCancelSection;
    private PublicPersonalProfileNavItem selected;

    public PublicPersonalProfileNavItemsAddForm() {
        super(new ColumnPanel(2));

        widgetSection = new FormSection(new ColumnPanel(2, true));
        super.add(widgetSection, ColumnPanel.INSERT);

        ColumnPanel panel = (ColumnPanel) getPanel();

        panel.add(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.navitem.key")));
        final ParameterModel keyParam =
                             new StringParameter(
                PublicPersonalProfileNavItem.KEY);
        final TextField keyField = new TextField(keyParam);
        keyField.setMaxLength(32);
        keyField.addValidationListener(new NotNullValidationListener());
        keyField.addValidationListener(new NotEmptyValidationListener());
        panel.add(keyField);

        panel.add(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.navitem.lang")));
        final Collection languages = LanguageUtil.convertToG11N(LanguageUtil.
                getSupportedLanguages2LA());
        final ParameterModel langModel =
                             new StringParameter(
                PublicPersonalProfileNavItem.LANG);
        final SingleSelect langSelect = new SingleSelect(langModel);
        langSelect.addValidationListener(new NotNullValidationListener());
        langSelect.addValidationListener(new NotEmptyValidationListener());

        langSelect.addOption(new Option("", ""));
        Pair pair;
        for (Object obj : languages) {
            pair = (Pair) obj;

            langSelect.addOption(new Option((String) pair.getKey(),
                                            (String) ((GlobalizedMessage) pair.
                                                      getValue()).localize()));
        }
        panel.add(langSelect);

        panel.add(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.navitem.label")));
        final ParameterModel labelParam =
                             new StringParameter(
                PublicPersonalProfileNavItem.LABEL);
        final TextField labelField = new TextField(labelParam);
        labelField.setMaxLength(32);
        labelField.addValidationListener(new NotNullValidationListener());
        labelField.addValidationListener(new NotEmptyValidationListener());
        panel.add(labelField);

        panel.add(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.navitem.generatorclass")));
        final ParameterModel generatorParam =
                             new StringParameter(
                PublicPersonalProfileNavItem.GENERATOR_CLASS);
        final TextField generatorField = new TextField(generatorParam);
        generatorField.setMaxLength(512);
        panel.add(generatorField);

        saveCancelSection = new SaveCancelSection();
        super.add(saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        addInitListener(this);
        addProcessListener(this);
        addValidationListener(this);

    }

    public void init(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();

        if (selected == null) {
            data.put(PublicPersonalProfileNavItem.KEY, "");
            data.put(PublicPersonalProfileNavItem.LANG, "");
            data.put(PublicPersonalProfileNavItem.LABEL, "");
            data.put(PublicPersonalProfileNavItem.GENERATOR_CLASS, "");
        } else {
            data.put(PublicPersonalProfileNavItem.KEY,
                     selected.getKey());
            data.put(PublicPersonalProfileNavItem.LANG,
                     selected.getLang());
            data.put(PublicPersonalProfileNavItem.LABEL,
                     selected.getLabel());
            data.put(PublicPersonalProfileNavItem.GENERATOR_CLASS,
                     selected.getGeneratorClass());
        }
    }

    public void process(final FormSectionEvent fse)
            throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (saveCancelSection.getSaveButton().isSelected(state)) {
            PublicPersonalProfileNavItemCollection navItems =
                                                   new PublicPersonalProfileNavItemCollection();

            final Map<String, PublicPersonalProfileNavItem> navItemMap =
                                                            new HashMap<String, PublicPersonalProfileNavItem>();
            while (navItems.next()) {
                navItemMap.put(navItems.getNavItem().getKey(), navItems.
                        getNavItem());
            }
            final int numberOfKeys = navItemMap.size();

            PublicPersonalProfileNavItem item;
            if (selected == null) {
                item = new PublicPersonalProfileNavItem();
                item.setId(new BigDecimal(navItems.size() + 1));
            } else {
                item = selected;
            }

            item.setKey((String) data.get(PublicPersonalProfileNavItem.KEY));
            item.setLang((String) data.get(PublicPersonalProfileNavItem.LANG));
            item.setLabel((String) data.get(PublicPersonalProfileNavItem.LABEL));
            if (data.get(PublicPersonalProfileNavItem.GENERATOR_CLASS) != null) {
                item.setGeneratorClass((String) data.get(
                        PublicPersonalProfileNavItem.GENERATOR_CLASS));
            }
            final PublicPersonalProfileNavItem navItem =
                                               navItemMap.get((String) data.get(
                    PublicPersonalProfileNavItem.KEY));
            if (navItem == null) {
                item.setOrder(numberOfKeys + 1);
            } else {
                item.setOrder(navItem.getOrder());
            }

            item.save();
        }

        this.selected = null;
        data.put(PublicPersonalProfileNavItem.KEY, "");
        data.put(PublicPersonalProfileNavItem.LANG, "");
        data.put(PublicPersonalProfileNavItem.LABEL, "");
        data.put(PublicPersonalProfileNavItem.GENERATOR_CLASS, "");
    }

    @Override
    public void validate(final FormSectionEvent fse)
            throws FormProcessException {
    }

    @Override
    public void submitted(final FormSectionEvent fse)
            throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (saveCancelSection.getCancelButton().isSelected(state)) {
            this.selected = null;
            data.put(PublicPersonalProfileNavItem.KEY, "");
            data.put(PublicPersonalProfileNavItem.LANG, "");
            data.put(PublicPersonalProfileNavItem.LABEL, "");
            data.put(PublicPersonalProfileNavItem.GENERATOR_CLASS, "");
        }
    }

    protected void setSelectedNavItem(
            final PublicPersonalProfileNavItem selected) {
        this.selected = selected;
    }
}
