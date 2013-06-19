/*
 * Copyright (c) 2011 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.publicpersonalprofile.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
    private final ParameterSingleSelectionModel navItemSelect;

    public PublicPersonalProfileNavItemsAddForm(
            final ParameterSingleSelectionModel navItemSelect) {
        super(new ColumnPanel(2));

        this.navItemSelect = navItemSelect;

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

        if (navItemSelect.getSelectedKey(state) == null) {
            data.put(PublicPersonalProfileNavItem.KEY, "");
            data.put(PublicPersonalProfileNavItem.LANG, "");
            data.put(PublicPersonalProfileNavItem.LABEL, "");
            data.put(PublicPersonalProfileNavItem.GENERATOR_CLASS, "");
        } else {
            PublicPersonalProfileNavItemCollection navItems =
                                                   new PublicPersonalProfileNavItemCollection();
            navItems.addFilter(
                    String.format("navItemId = %s",
                                  navItemSelect.getSelectedKey(state).
                    toString()));

            if (navItems.size() == 0) {
                throw new IllegalStateException(
                        String.format("No nav item with id '%s' found.",
                                      navItemSelect.getSelectedKey(
                        state).toString()));
            }

            navItems.next();
            PublicPersonalProfileNavItem selected = navItems.getNavItem();
            navItems.close();

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
            final List<PublicPersonalProfileNavItem> navItemsList =
                                                     new ArrayList<PublicPersonalProfileNavItem>(navItemMap.
                    values());

            PublicPersonalProfileNavItem item;
            if (navItemSelect.getSelectedKey(state) == null) {
                item = new PublicPersonalProfileNavItem();
                Collections.sort(navItemsList,
                                 new Comparator<PublicPersonalProfileNavItem>() {

                    public int compare(final PublicPersonalProfileNavItem item1,
                                       final PublicPersonalProfileNavItem item2) {
                        return item1.getId().compareTo(item2.getId());
                    }
                });
                if (navItemsList.size() > 0) {
                    item.setId(navItemsList.get(navItemsList.size() - 1).getId().
                            add(
                            BigDecimal.ONE));
                } else {
                    item.setId(BigDecimal.ONE);
                }
            } else {
                navItems.reset();
                navItems.addFilter(
                        String.format("navItemId = %s",
                                      navItemSelect.getSelectedKey(state).
                        toString()));

                if (navItems.size() == 0) {
                    throw new IllegalStateException(
                            String.format("No nav item with id '%s' found.",
                                          navItemSelect.getSelectedKey(
                            state).toString()));
                }

                navItems.next();
                item = navItems.getNavItem();
                navItems.close();
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
                Collections.sort(navItemsList,
                                 new Comparator<PublicPersonalProfileNavItem>() {

                    public int compare(final PublicPersonalProfileNavItem item1,
                                       final PublicPersonalProfileNavItem item2) {
                        return item1.getOrder().compareTo(item2.getOrder());
                    }
                });
                if (navItemsList.size() > 0) {
                    item.setOrder(navItemsList.get(navItemsList.size() - 1).
                            getOrder()
                                  + 1);
                } else {
                    item.setOrder(1);
                }
            } else {
                item.setOrder(navItem.getOrder());
            }

            item.save();
        }

        navItemSelect.clearSelection(state);
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
            navItemSelect.clearSelection(state);
            data.put(PublicPersonalProfileNavItem.KEY, "");
            data.put(PublicPersonalProfileNavItem.LANG, "");
            data.put(PublicPersonalProfileNavItem.LABEL, "");
            data.put(PublicPersonalProfileNavItem.GENERATOR_CLASS, "");
        }
    }
}
