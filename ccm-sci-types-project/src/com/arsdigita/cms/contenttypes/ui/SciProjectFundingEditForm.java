/*;
 * Copyright (c) 2013 Jens Pelzetter
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
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectConfig;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciProjectFundingEditForm extends BasicItemForm implements FormProcessListener,
                                                                        FormInitListener {

    private final static SciProjectConfig CONFIG = SciProject.getConfig();

    public SciProjectFundingEditForm(final ItemSelectionModel itemModel) {
        super("SciProjectFundingForm", itemModel);
    }

    @Override
    public void addWidgets() {

        if (CONFIG.getEnableFunding()) {
            final ParameterModel fundingParam = new StringParameter(
                    SciProject.FUNDING);
            final TextArea funding;
            if (CONFIG.getEnableFundingDhtml()) {
                funding = new CMSDHTMLEditor(fundingParam);
            } else {
                funding = new TextArea(fundingParam);
            }
            funding.setLabel(SciProjectGlobalizationUtil.globalize(
                             "sciproject.ui.funding"));
            funding.setCols(75);
            funding.setRows(8);
            add(funding);
        }

        if (CONFIG.getEnableFundingVolume()) {
            final ParameterModel fundingVolumeParam = new StringParameter(
                    SciProject.FUNDING_VOLUME);
            final TextField fundingVolume = new TextField(fundingVolumeParam);
            fundingVolume.addValidationListener(new StringInRangeValidationListener(
                    0,
                    CONFIG.getFundingVolumeLength()));
            fundingVolume.setLabel(SciProjectGlobalizationUtil.globalize(
                                   "sciproject.ui.funding.volume"));
            add(fundingVolume);
        }
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final SciProject project = (SciProject) getItemSelectionModel().getSelectedObject(state);

        if (CONFIG.getEnableFunding()) {
            data.put(SciProject.FUNDING, project.getFunding());
        }

        if (CONFIG.getEnableFundingVolume()) {
            data.put(SciProject.FUNDING_VOLUME, project.getFundingVolume());
        }

        setVisible(state, true);


    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final SciProject project = (SciProject) getItemSelectionModel().getSelectedObject(state);

        if ((project != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {

            if (CONFIG.getEnableFunding()) {
                project.setFunding((String) data.get(SciProject.FUNDING));
            }
            if (CONFIG.getEnableFundingVolume()) {
                project.setFundingVolume((String) data.get(SciProject.FUNDING_VOLUME));
            }

            project.save();

        }

        init(event);
    }

}
