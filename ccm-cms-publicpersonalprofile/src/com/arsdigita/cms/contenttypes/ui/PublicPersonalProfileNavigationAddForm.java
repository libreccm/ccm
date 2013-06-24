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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;

import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItem;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItemCollection;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;
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
    private List<String> autoNavItemKeys = new ArrayList<String>();

    public PublicPersonalProfileNavigationAddForm(
            final ItemSelectionModel itemModel,
            final SimpleEditStep editStep) {
        this("PublicPersonalProfileNavAddForm", itemModel, editStep);
        this.itemModel = itemModel;
        this.editStep = editStep;
    }

    public PublicPersonalProfileNavigationAddForm(
            final String formName,
            final ItemSelectionModel itemModel,
            final SimpleEditStep editStep) {
        super("PublicPersonalProfileNavAddForm", itemModel);
        this.itemModel = itemModel;
        this.editStep = editStep;
    }

    @Override
    public void addWidgets() {
        add(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.nav.select_nav_item") ));
        ParameterModel navItemModel =
                       new StringParameter(PublicPersonalProfileNavItem.KEY);
        SingleSelect navItemSelect = new SingleSelect(navItemModel);
        navItemSelect.addValidationListener(new NotNullValidationListener());
        navItemSelect.addValidationListener(new NotEmptyValidationListener());
        try {
            navItemSelect.addPrintListener(new PrintListener() {

                public void prepare(final PrintEvent event) {
                    SingleSelect select = (SingleSelect) event.getTarget();

                    select.addOption(new Option("", ""));
                    PublicPersonalProfileNavItemCollection navItems =
                                  new PublicPersonalProfileNavItemCollection();
                    navItems.addLanguageFilter(GlobalizationHelper.
                            getNegotiatedLocale().
                            getLanguage());
                    /*if (showGenerated()) {
                    navItems.addFilter("generatorClass is not null");
                    } else {
                    navItems.addFilter("generatorClass is null");
                    }*/

                    PublicPersonalProfileNavItem navItem;
                    String label;
                    while (navItems.next()) {
                        navItem = navItems.getNavItem();

                        if (navItem.getGeneratorClass() == null) {
                            label = navItem.getLabel();
                        } else {
                            label = String.format("%s (auto)",
                                                  navItem.getLabel());
                            autoNavItemKeys.add(navItem.getKey());
                        }

                        select.addOption(new Option(navItem.getKey(), label));
                    }
                }
            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }

        /*navItemSelect.addOption(new Option("", ""));
        
        PublicPersonalProfileNavItemCollection navItems =
        new PublicPersonalProfileNavItemCollection();
        navItems.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().
        getLanguage());
        if (showGenerated()) {
        navItems.addFilter("generatorClass is not null");
        } else {
        navItems.addFilter("generatorClass is null");
        }
        
        PublicPersonalProfileNavItem navItem;
        while (navItems.next()) {
        navItem = navItems.getNavItem();
        
        navItemSelect.addOption(new Option(navItem.getKey(),
        navItem.getLabel()));
        }*/
        add(navItemSelect);

        //if (!showGenerated()) {
        add(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.nav.select_target") ));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH);
        itemSearch.addValidationListener(this);
        add(this.itemSearch);
        //}
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();
        FormData data = fse.getFormData();

        String navKey = (String) data.get(PublicPersonalProfileNavItem.KEY);

        PublicPersonalProfile profile = (PublicPersonalProfile) itemModel.
                getSelectedObject(state);

        RelatedLink link = new RelatedLink();
        link.setLinkListName(PublicPersonalProfile.LINK_LIST_NAME);
        link.setLinkOwner(profile);

        link.setResourceSize("");
        link.setResourceType(MimeType.loadMimeType("text/html"));

        PublicPersonalProfileNavItemCollection navItems =
                                               new PublicPersonalProfileNavItemCollection();
        navItems.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().
                getLanguage());
        navItems.addKeyFilter(navKey);
        navItems.next();
        PublicPersonalProfileNavItem navItem = navItems.getNavItem();

        link.setTitle(navItem.getKey());
        link.setDescription("");
        link.setOrder(navItem.getOrder());
        link.setTargetType(Link.INTERNAL_LINK);

        ContentItem targetItem;
        //if (showGenerated()) {
        //For generated content the target is the profile itself.
        //targetItem = profile;
        //} else {
        targetItem = (ContentItem) data.get(ITEM_SEARCH);
        //
        //}
        if (targetItem == null) {
            targetItem = profile;
        }

        if (targetItem.getParent() instanceof ContentBundle) {
            targetItem = (ContentItem) targetItem.getParent();
        }

        link.setTargetItem(targetItem);
        link.setTargetWindow("");

        link.save();
    }

    @Override
    public void validate(final FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();
        FormData data = fse.getFormData();

        //if (!showGenerated() && data.get(ITEM_SEARCH) == null) {
        if (!(autoNavItemKeys.contains(
              (String) data.get(PublicPersonalProfileNavItem.KEY)))
            && data.get(ITEM_SEARCH) == null) {
            data.addError(
                    new GlobalizedMessage(
                    "parameter_is_required",
                    "com.arsdigita.bebop.parameters.ParameterResources"));
        }

    }

    /*protected boolean showGenerated() {
    return false;
    }*/
}
