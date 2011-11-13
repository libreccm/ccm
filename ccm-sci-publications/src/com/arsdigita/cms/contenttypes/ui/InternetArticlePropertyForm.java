/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.InternetArticle;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Jens Pelzeter
 */
public class InternetArticlePropertyForm
        extends PublicationPropertyForm
        implements FormInitListener,
                   FormProcessListener,
                   FormSubmissionListener {

    private InternetArticlePropertiesStep m_step;
    public static final String ID = "InternetArticleEdit";

    public InternetArticlePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public InternetArticlePropertyForm(ItemSelectionModel itemModel,
                                       InternetArticlePropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.place").localize()));
        ParameterModel placeParam =
                       new StringParameter(InternetArticle.PLACE);
        TextField place = new TextField(placeParam);
        add(place);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.number").localize()));
        ParameterModel numberParam =
                       new StringParameter(InternetArticle.NUMBER);
        TextField number = new TextField(numberParam);
        add(number);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.number_of_pages").localize()));
        ParameterModel numberOfPagesParam =
                       new IntegerParameter(InternetArticle.NUMBER_OF_PAGES);
        TextField numberOfPages = new TextField(numberOfPagesParam);
        add(numberOfPages);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.edition").localize()));
        ParameterModel editionParam =
                       new StringParameter(InternetArticle.EDITION);
        TextField edition = new TextField(editionParam);
        add(edition);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.issn").localize()));
        ParameterModel issnParam =
                       new StringParameter(InternetArticle.ISSN);
        TextField issn = new TextField(issnParam);
        issn.setMaxLength(9);
        issn.addValidationListener(new ParameterListener() {

            public void validate(ParameterEvent event) throws
                    FormProcessException {
                ParameterData data = event.getParameterData();
                String value = (String) data.getValue();

                if (value.isEmpty()) {
                    return;
                }

                value = value.replace("-", "");

                if (value.length() != 8) {
                    data.invalidate();
                    data.addError(PublicationGlobalizationUtil.globalize(
                            "publications.ui.invalid_issn"));
                }

                try {
                    Long num = Long.parseLong(value);
                } catch (NumberFormatException ex) {
                    data.invalidate();
                    data.addError(PublicationGlobalizationUtil.globalize(
                            "publications.ui.invalid_issn"));
                }
            }
        });
        add(issn);

        Calendar today = new GregorianCalendar();
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.lastAccessed").
                localize()));
        ParameterModel pubDateParam =
                       new DateParameter(InternetArticle.LAST_ACCESSED);
        com.arsdigita.bebop.form.Date pubDate =
                                      new com.arsdigita.bebop.form.Date(
                pubDateParam);
        pubDate.setYearRange(1900, today.get(Calendar.YEAR) + 2);
        add(pubDate);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.url").localize()));
        ParameterModel urlModel = new StringParameter(InternetArticle.URL);
        TextField url = new TextField(urlModel);
        add(url);
        
         add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.urn").localize()));
        ParameterModel urnModel = new StringParameter(InternetArticle.URN);
        TextField urn = new TextField(urnModel);
        add(urn);
        
         add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.doi").localize()));
        ParameterModel doiModel = new StringParameter(InternetArticle.DOI);
        TextField doi = new TextField(doiModel);
        add(doi);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        InternetArticle article = (InternetArticle) initBasicWidgets(fse);

        data.put(InternetArticle.PLACE, article.getPlace());
        data.put(InternetArticle.NUMBER, article.getNumber());
        data.put(InternetArticle.NUMBER_OF_PAGES, article.getNumberOfPages());
        data.put(InternetArticle.EDITION, article.getEdition());
        data.put(InternetArticle.ISSN, article.getISSN());
        data.put(InternetArticle.LAST_ACCESSED,
                 article.getLastAccessed());
        data.put(InternetArticle.URL, article.getUrl());
        data.put(InternetArticle.URN, article.getUrn());
        data.put(InternetArticle.DOI, article.getDoi());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        InternetArticle article = (InternetArticle) processBasicWidgets(fse);

        if ((article != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            article.setPlace((String) data.get(InternetArticle.PLACE));
            article.setNumber((String) data.get(InternetArticle.NUMBER));
            article.setNumberOfPages(
                    (Integer) data.get(InternetArticle.NUMBER_OF_PAGES));
            article.setEdition((String) data.get(InternetArticle.EDITION));
            String issn = (String) data.get(InternetArticle.ISSN);
            issn = issn.replace("-", "");
            article.setISSN(issn);
            article.setLastAccessed(
                    (Date) data.get(InternetArticle.LAST_ACCESSED));
            article.setUrl((String) data.get(InternetArticle.URL));
            article.setUrn((String) data.get(InternetArticle.URN));
            article.setDoi((String) data.get(InternetArticle.DOI));

            article.save();
        }
    }
}
