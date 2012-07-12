/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui;


import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.util.GlobalizationUtil;


/**
 * Sticks a form at the top of an {@link ImageBrowser} in order to
 * search images by keyword
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ImageChooser.java 1940 2009-05-29 07:15:05Z terry $
 */
public class ImageChooser extends BoxPanel {

    private ImageKeywordForm m_form;
    private ImageBrowser m_browser;
    private Paginator m_paginator;
    private StringParameter m_keyword;
    private SingleSelectionModel m_sel;

    public static final String KEYWORD = "kw";
    public static int LIST_SIZE = 20;


    /**
     * Construct a new ImageChooser
     *
     * @param context the context for the retrieved items. Should be
     *   {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     * @param mode the display mode for the ImageBrowser
     */
    public ImageChooser(String context, int mode) {
        super(BoxPanel.VERTICAL);

        m_keyword = new StringParameter(KEYWORD);
        m_sel = new ParameterSingleSelectionModel(m_keyword);
        m_form = new ImageKeywordForm(m_sel);
        DefaultImageBrowserModelBuilder modelBuilder = 
            new DefaultImageBrowserModelBuilder(m_sel, context);
        m_browser = new ImageBrowser(modelBuilder, mode);
        modelBuilder.setImageBrowser(m_browser);


        m_paginator = new Paginator
            (modelBuilder,
             LIST_SIZE);
        super.add(m_form);
        super.add(m_paginator);
        super.add(m_browser);
    }

    /**
     * Construct a new ImageChooser
     *
     * @param context the context for the retrieved items. Should be
     *   {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     */
    public ImageChooser(String context) {
        this(context, ImageBrowser.SELECT_IMAGE);
    }

    /**
     * Construct a new ImageChooser
     */
    public ImageChooser() {
        this(ContentItem.DRAFT, ImageBrowser.SELECT_IMAGE);
    }

    /**
     * Add the "keyword" parameter to the page state
     */
    public void register(Page p) {
        p.addComponent(this);
        p.addComponentStateParam(this, m_keyword);
    }

    /**
     * Add an action listener to the browser. The inner class
     * {@link ImageBrowser.LinkActionListener} will probably be
     * used here.
     *
     * @param l the action listener.
     */
    public void addImageActionListener(TableActionListener l) {
        m_browser.addTableActionListener(l);
    }

    /**
     * Add a submission listener to the form. The listener will
     * fire whenever a button on the form is clicked.
     *
     * @param l the action listener.
     */
    public void addFormSubmissionListener(FormSubmissionListener l) {
        m_form.addSubmissionListener(l);
    }

    /**
     * @return the "Cancel" button in the form
     */
    public Submit getFormCancelButton() {
        return m_form.getSaveCancelSection().getCancelButton();
    }

    /**
     * @return the keyword selection model
     */
    public SingleSelectionModel getKeywordModel() {
        return m_sel;
    }

    /**
     * Set the specified keyword. All images matching the keyword
     * will be displayed in the browser.
     *
     * @param state The page state
     * @param word The new keyword
     */
    public void setKeyword(PageState state, String word) {
        m_sel.setSelectedKey(state, word);
    }

    /**
     * Clear the selection in the browser
     *
     * @param state The page state
     */
    public void clearSelection(PageState state) {
        m_browser.clearSelection(state);
    }

    /**
     * @param state The page state
     * @return the current keyword
     */
    public String getKeyword(PageState state) {
        return (String)m_sel.getSelectedKey(state);
    }

    /**
     * @return the form
     */
    public ImageKeywordForm getForm() {
        return m_form;
    }

    /**
     * @return the image browser
     */
    public ImageBrowser getImageBrowser() {
        return m_browser;
    }

    /**
     * Clear the keyword used in the keyword form
     *
     * @param s The page state
     */
    public void clearKeyword(PageState s) {
        m_sel.clearSelection(s);
    }

    /**
     * The form which specifies a keyword for the image browser.
     */
    public static class ImageKeywordForm extends Form
        implements FormProcessListener, FormInitListener {

        private SingleSelectionModel m_sel;
        private SaveCancelSection m_saveCancel;

        public static String WORD = "word";

        /**
         * Construct a new ImageKeywordForm
         * @param sel The SingleSelectionModel which the form will use to
         *   set the keyword
         */
        public ImageKeywordForm(SingleSelectionModel sel) {
            super("ImageKeywordForm", new BoxPanel(BoxPanel.HORIZONTAL));
            m_sel = sel;

            add(new Label(GlobalizationUtil.globalize("cms.ui.enter_a_keyword")));
            TextField t = new TextField(WORD);
	    // allow null keyword field for view all
            //t.addValidationListener(new NotNullValidationListener("keyword"));
            add(t);
            m_saveCancel = new SaveCancelSection();
            m_saveCancel.getSaveButton().setButtonLabel("Search");
            add(m_saveCancel);

            addProcessListener(this);
            addInitListener(this);

            setClassAttr("imageKeywordForm");
        }

        /**
         * Set the keyword in the text widget
         */
        public void init(FormSectionEvent e) throws FormProcessException {
            PageState s = e.getPageState();
            FormData data = e.getFormData();
            data.put(WORD, m_sel.getSelectedKey(s));
        }

        /**
         * @return the SaveCancelSection
         */
        public SaveCancelSection getSaveCancelSection() {
            return m_saveCancel;
        }

        /**
         * @return the SingleSelectionModel
         */
        public SingleSelectionModel getKeywordModel() {
            return m_sel;
        }

        public void process(FormSectionEvent e) {
            FormData data = e.getFormData();
            PageState state = e.getPageState();
            m_sel.setSelectedKey(state, (String)data.get(WORD));
        }
    }
}
