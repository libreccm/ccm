/*
 * Copyright (c) 2014 Jens Pelzetter
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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciPublicationsDramaticArtsGlobalisationUtil;
import com.arsdigita.cms.contenttypes.SciPublicationsPlay;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPlayPropertyForm
    extends PublicationWithPublisherPropertyForm
    implements FormInitListener,
               FormProcessListener,
               FormSubmissionListener {
    
    private final SciPublicationsPlayPropertiesStep step;
    
    public SciPublicationsPlayPropertyForm(final ItemSelectionModel itemModel) {
        this(itemModel, null);
    }
    
    public SciPublicationsPlayPropertyForm(final ItemSelectionModel itemModel,
                                             final SciPublicationsPlayPropertiesStep step) {
        super(itemModel, step);
        this.step = step;
        addSubmissionListener(this);
    }
    
    @Override
    protected void addWidgets() {
        super.addWidgets();
        
        final SciPublicationsDramaticArtsGlobalisationUtil globalisationUtil = new SciPublicationsDramaticArtsGlobalisationUtil();
        final ParameterModel firstProdYearParam = new IntegerParameter(SciPublicationsPlay.FIRST_PRODUCTION_YEAR);
        final TextField firstProdYear = new TextField(firstProdYearParam);
        firstProdYear.setMaxLength(4);
        firstProdYear.setLabel(globalisationUtil.globalise("publications.dramaticarts.ui.play.first_production_year"));
        add(new Label(globalisationUtil.globalise("publications.dramaticarts.ui.play.first_production_year")));
        add(firstProdYear);
        
    }
    
    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        super.init(event);
        
        final FormData data = event.getFormData();
        final SciPublicationsPlay play = (SciPublicationsPlay) super.initBasicWidgets(event);
        
        data.put(SciPublicationsPlay.FIRST_PRODUCTION_YEAR, play.getFirstProductionYear());
        
    }
    
    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        super.process(event);
        
        final FormData data = event.getFormData();
        final SciPublicationsPlay play = (SciPublicationsPlay) super.processBasicWidgets(event);
        
        if ((play != null) 
            && getSaveCancelSection().getSaveButton().isSelected(event.getPageState())) {
            play.setFirstProductionYear((Integer) data.get(SciPublicationsPlay.FIRST_PRODUCTION_YEAR));
            play.save();
        }
    }
    

}
