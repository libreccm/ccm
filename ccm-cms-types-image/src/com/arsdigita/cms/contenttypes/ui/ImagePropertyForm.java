/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Image;
import com.arsdigita.cms.contenttypes.util.ImageGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Form to edit the basic properties of an article. This form can be
 * extended to create forms for Image subclasses.
 */
public class ImagePropertyForm
        extends BasicPageForm
        implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private final static org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(ImagePropertyForm.class);
    private ImagePropertiesStep m_step;

    /**
     * Creates a new form to edit the Image object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Image to work on
     */
    public ImagePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Creates a new form to edit the Image object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Image to work on
     * @param step The ImagePropertiesStep which controls this form.
     */
    public ImagePropertyForm(ItemSelectionModel itemModel, ImagePropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.caption")));
        ParameterModel captionParam = new StringParameter(Image.CAPTION);
        captionParam.addParameterListener(new StringInRangeValidationListener(0, 400));
        TextField caption = new TextField(captionParam);
        add(caption);

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.description")));
        ParameterModel descriptionParam = new StringParameter(Image.DESCRIPTION);
        descriptionParam.addParameterListener(new StringInRangeValidationListener(0, 500));
        TextArea description = new TextArea(descriptionParam);
        description.setRows(10);
        description.setCols(60);
        add(description);

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.artist")));
        ParameterModel artistParam = new StringParameter(Image.ARTIST);
        artistParam.addParameterListener(new StringInRangeValidationListener(0, 200));
        TextField artist = new TextField(artistParam);
        add(artist);

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.publishDate")));
        ParameterModel publishDateParam = new DateParameter(Image.PUBLISHDATE);
        Date publishDate = new Date(publishDateParam);
        publishDate.setYearRange(Image.getConfig().getStartYear(),
                GregorianCalendar.getInstance().get(Calendar.YEAR) + Image.getConfig().getEndYearDelta());
        add(publishDate);

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.source")));
        ParameterModel sourceParam = new StringParameter(Image.SOURCE);
        sourceParam.addParameterListener(new StringInRangeValidationListener(0, 600));
        TextField source = new TextField(sourceParam);
        add(source);

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.media")));
        ParameterModel mediaParam = new StringParameter(Image.MEDIA);
        mediaParam.addParameterListener(new StringInRangeValidationListener(0, 300));
        TextField media = new TextField(mediaParam);
        add(media);

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.copyright")));
        ParameterModel copyrightParam = new StringParameter(Image.COPYRIGHT);
        copyrightParam.addParameterListener(new StringInRangeValidationListener(0, 400));
        TextField copyright = new TextField(copyrightParam);
        add(copyright);

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.site")));
        ParameterModel siteParam = new StringParameter(Image.SITE);
        siteParam.addParameterListener(new StringInRangeValidationListener(0, 500));
        TextField site = new TextField(siteParam);
        add(site);

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.license")));
        ParameterModel licenseParam = new StringParameter(Image.LICENSE);
        licenseParam.addParameterListener(new StringInRangeValidationListener(0, 300));
        TextField license = new TextField(licenseParam);
        add(license);

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.material")));
        ParameterModel materialParam = new StringParameter(Image.MATERIAL);
        materialParam.addParameterListener(new StringInRangeValidationListener(0, 200));
        TextField material = new TextField(materialParam);
        add(material);

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.technique")));
        ParameterModel techniqueParam = new StringParameter(Image.TECHNIQUE);
        techniqueParam.addParameterListener(new StringInRangeValidationListener(0, 200));
        TextField technique = new TextField(techniqueParam);
        add(technique);

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.origin")));
        ParameterModel originParam = new StringParameter(Image.ORIGIN);
        originParam.addParameterListener(new StringInRangeValidationListener(0, 200));
        TextField origin = new TextField(originParam);
        add(origin);

        add(new Label(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.origSize")));
        ParameterModel origSizeParam = new StringParameter(Image.ORIGSIZE);
        origSizeParam.addParameterListener(new StringInRangeValidationListener(0, 100));
        TextField origSize = new TextField(origSizeParam);
        add(origSize);

    }

    /** Form initialisation hook. Fills widgets with data. */
    @Override
    public void init(FormSectionEvent fse) {
        // Do some initialization hook stuff
        FormData data = fse.getFormData();
        PageState ps = fse.getPageState();
        Image image = (Image) super.initBasicWidgets(fse);

        data.put(Image.CAPTION, image.getCaption());
        data.put(Image.DESCRIPTION, image.getDescription());
        data.put(Image.ARTIST, image.getArtist());
        data.put(Image.PUBLISHDATE, image.getPublishDate());
        data.put(Image.SOURCE, image.getSource());
        data.put(Image.MEDIA, image.getMedia());
        data.put(Image.COPYRIGHT, image.getCopyright());
        data.put(Image.SITE, image.getSite());
        data.put(Image.LICENSE, image.getLicense());
        data.put(Image.MATERIAL, image.getMaterial());
        data.put(Image.TECHNIQUE, image.getTechnique());
        data.put(Image.ORIGIN, image.getOrigin());
        data.put(Image.ORIGSIZE, image.getOriginalSize());
    }

    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        FormData d = e.getFormData();
    }

    /** Cancels streamlined editing. */
    @Override
    public void submitted(FormSectionEvent fse) {
        if (m_step != null
                && getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    /** Form processing hook. Saves Event object. */
    @Override
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState ps = fse.getPageState();
        Image image = (Image) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (image != null && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {

            image.setCaption((String) data.get(Image.CAPTION));
            image.setDescription((String) data.get(Image.DESCRIPTION));
            image.setArtist((String) data.get(Image.ARTIST));
            image.setPublishDate((java.util.Date) data.get(Image.PUBLISHDATE));
            image.setSource((String) data.get(Image.SOURCE));
            image.setMedia((String) data.get(Image.MEDIA));
            image.setCopyright((String) data.get(Image.COPYRIGHT));
            image.setSite((String) data.get(Image.SITE));
            image.setLicense((String) data.get(Image.LICENSE));
            image.setMaterial((String) data.get(Image.MATERIAL));
            image.setTechnique((String) data.get(Image.TECHNIQUE));
            image.setOrigin((String) data.get(Image.ORIGIN));
            image.setOriginalSize((String) data.get(Image.ORIGSIZE));

            image.save();
        }
    }
}
