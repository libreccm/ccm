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
package com.arsdigita.cms.ui.templates;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.TemplateContext;
import com.arsdigita.cms.TemplateContextCollection;
import com.arsdigita.cms.TemplateManager;
import com.arsdigita.cms.TemplateManagerFactory;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.db.Sequences;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.TooManyListenersException;

/**
 * Displays a form to create a new template, which will be assigned to a content
 * type within a specific content section. This component displaces the creation
 * step for the templates authoring kit, since it needs to know the content type
 * to which the template would be assigned.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: TemplateCreate.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class TemplateCreate extends BasicItemForm {

    private static Logger s_log = Logger.getLogger(TemplateCreate.class);

    private final ACSObjectSelectionModel m_typeModel;

    private static final String USE_CONTEXT = "use_ctx";
    private static final String ITEM_ID = "item_id";

    /**
     * Construct a new <code>TemplateCreate</code> component
     *
     * @param sectionModel supplies the content section
     * @param typeModel supplies the content type within the section to
     *   which the new template will be assigned
     */
    public TemplateCreate(final ACSObjectSelectionModel typeModel) {
        super("templateCreate", null);

        m_typeModel = typeModel;
    }

    /**
     * Add the "use context" widget
     */
    @Override
    public void addWidgets() {
        super.addWidgets();

        add(new Label(GlobalizationUtil.globalize("cms.ui.templates.use_context"),  false));
        
        SingleSelect ctxWidget = new SingleSelect(new StringParameter(USE_CONTEXT));
        ctxWidget.setDefaultValue(TemplateManager.PUBLIC_CONTEXT);
        try {
            ctxWidget.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        PageState state = e.getPageState();
                        
                        SingleSelect target = (SingleSelect)e.getTarget();
                        target.clearOptions();
                        
                        TemplateContextCollection contexts = TemplateContext.retrieveAll();
                        contexts.addOrder(TemplateContext.LABEL);
                        while (contexts.next()) {
                            TemplateContext type = contexts.getTemplateContext();
                            target.addOption(new Option(type.getContext(),
                                                        type.getLabel()));
                        }
                    }
                });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("This can never happen", ex);
        }
        
        ctxWidget.addValidationListener(new NotNullValidationListener());
        add(ctxWidget);

        Hidden idWidget = new Hidden(new BigDecimalParameter(ITEM_ID));
        add(idWidget);
    }

    /**
     * Initialize the form - populate the id widget for double-click protection
     */
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData d = e.getFormData();

        BigDecimal id;
        try {
            id = Sequences.getNextValue();
            d.put(ITEM_ID, id);
        } catch (SQLException ex) {
            //s_log.error("Error retrieving the sequence value", ex);
            //throw new FormProcessException(ex.getMessage());
            throw new FormProcessException(GlobalizationUtil.globalize(
                    "cms.ui.templates.no_sequence_value_retrieved"));
        }
    }

    /**
     * Validate the form - ensure name uniqueness
     */
    public void validate(FormSectionEvent e) throws FormProcessException {
        final ContentSection section =
            CMS.getContext().getContentSection();

        final Folder folder = section.getTemplatesFolder();

        Assert.exists(folder, Folder.class);

        validateNameUniqueness(folder, e);
    }

    /**
     * Process the form - create the new template, assign it to the
     * section/type
     */
    public void process(FormSectionEvent e) throws FormProcessException {
        PageState s = e.getPageState();
        FormData d = e.getFormData();

        BigDecimal id = (BigDecimal)d.get(ITEM_ID);
        ContentSection sec = CMS.getContext().getContentSection();
        ContentType type = (ContentType)m_typeModel.getSelectedObject(s);
        Assert.exists(type, ContentType.class);

        // Create the template
        Template t = new Template(SessionManager.getSession().create
                                  (new OID(Template.BASE_DATA_OBJECT_TYPE, id)));
        t.setName((String)d.get(BasicItemForm.NAME));
        t.setLabel((String)d.get(BasicItemForm.TITLE));

        t.setText("Template body goes here");

        t.setParent(sec.getTemplatesFolder());
        t.setContentSection(sec);
        t.save();

        // Associate the template
        TemplateManagerFactory.getInstance().addTemplate
            (sec, type, t, (String)d.get(USE_CONTEXT));

        // Redirect to the template editing UI
        String pageURL = ContentItemPage.getItemURL
            (sec.getPath() + "/", t.getID(),
             ContentItemPage.AUTHORING_TAB);

        throw new RedirectSignal(URL.there(s.getRequest(), pageURL), true);
    }
}
