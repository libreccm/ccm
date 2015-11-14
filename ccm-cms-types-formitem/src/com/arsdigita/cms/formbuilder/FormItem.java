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
package com.arsdigita.cms.formbuilder;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.cms.dispatcher.XMLGenerator;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.ui.BaseAddObserver;
import com.arsdigita.formbuilder.ui.PlaceholdersInitListener;
import com.arsdigita.formbuilder.ui.FormBuilderXMLRenderer;
import com.arsdigita.formbuilder.ui.ComponentTraverse;
import com.arsdigita.formbuilder.util.FormBuilderUtil;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;

import java.math.BigDecimal;

public class FormItem extends ContentPage implements XMLGenerator {

    public static final String BASE_DATA_OBJECT_TYPE
                               = "com.arsdigita.cms.formbuilder.FormItem";

    public static final String REMOTE = "remote";
    public static final String REMOTE_URL = "remoteUrl";
    public static final String FORM = "form";
    public static final String CSS = "css";

    public FormItem() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public FormItem(String typeName) {
        super(typeName);
    }

    public FormItem(DataObject obj) {
        super(obj);
    }

    public FormItem(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public FormItem(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }

    protected void beforeSave() {
        if (isNew() && (get(FORM) == null)) {
            PersistentForm form = new PersistentForm();
            form.setHTMLName(getName());
            form.setAdminName(getName());
            setAssociation(FORM, form);
        }

        super.beforeSave();
    }

    public boolean copyProperty(CustomCopy src,
                                Property property,
                                ItemCopier copier) {
        if (property.getName().equals(FORM)) {
            setAssociation(FORM, (new FormCopier())
                           .copyForm(((FormItem) src).getForm()));
            return true;
        }

        return super.copyProperty(src, property, copier);
    }

    public static FormItem getItemByName(Folder folder,
                                         String name)
        throws DataObjectNotFoundException {

        Session ssn = SessionManager.getSession();
        DataCollection types = ssn.retrieve(BASE_DATA_OBJECT_TYPE);

        types.addEqualsFilter(ContentItem.PARENT, folder.getID());
        types.addEqualsFilter(ContentItem.NAME, name);

        if (types.next()) {
            DataObject obj = types.getDataObject();
            FormItem f = new FormItem(obj);
            types.close();
            return f;
        }
        throw new DataObjectNotFoundException(
            (String) GlobalizationUtil
            .globalize("cms.formbuilder.no_such_form").localize());
    }

    public PersistentForm getForm() {
        return new PersistentForm((DataObject) get(FORM));
    }

    /**
     * This sets a string that can be used to locate a Cascading Style Sheet
     * that can be used to style this item.
     */
    public void setCSS(String css) {
        set(CSS, css);
    }

    /**
     * This returns a string that can be used to locate a Cascading Style Sheet
     * that can be used to style this item. This returns null if no style sheet
     * has been set.
     */
    public String getCSS() {
        return (String) get(CSS);
    }

    /**
     * Denotes whether the form is to submit locally or remotely. If the form is
     * to be remotely submitted, no validation can be performed.
     */
    public boolean isRemote() {
        return Boolean.TRUE.equals(get(REMOTE));
    }

    /**
     * @see #isRemote()
     */
    public void setRemote(boolean value) {
        set(REMOTE, new Boolean(value));
    }

    /**
     * Specifies the URL to which this form will be POSTed.
     */
    public String getRemoteURL() {
        return (String) get(REMOTE_URL);
    }

    /**
     * @see #getRemoteURL()
     */
    public void setRemoteURL(String url) {
        set(REMOTE_URL, url);
    }

    protected Form instantiateForm(PersistentForm form,
                                   boolean readOnly) {
        form.setComponentAddObserver(new BaseAddObserver());

        Form c = (Form) form.createComponent();
        c.addInitListener(new PlaceholdersInitListener());
        c.setMethod(Form.GET);
        if (readOnly) {
            Traversal t = new Traversal() {

                public void act(Component c) {
                    try {
                        Widget widget = (Widget) c;
                        widget.setDisabled();
                        widget.setReadOnly();
                    } catch (ClassCastException ex) {
                        // Nada
                    }
                }

            };
            t.preorder(c);
        }

        return c;
    }

    public void generateXML(PageState state,
                            Element parent,
                            String useContext) {
        PersistentForm form = getForm();
        Component c = null;
        try {
            c = instantiateForm(
                form,
                "itemAdminSummary".equals(useContext));
        } catch (FormUnavailableException ex) {
            c = new Label("This form is temporarily unavailable");
        }

        // Fake the page context for the item, since we
        // have no access to the real page context.
        Page p = new Page("dummy");
        p.add(c);
        p.lock();

        PageState fake;
        try {
            if ("itemAdminSummary".equals(useContext)) {
                // Chop off all the parameters to stop bebop stategetting confused
                fake = p.process(new NoParametersHttpServletRequest(
                    state.getRequest()), state.getResponse());
            } else {
                // Really serving the user page, so need the params when
                // processing the form
                fake = p.process(state.getRequest(), state.getResponse());
            }
        } catch (Exception e) {
            throw new UncheckedWrapperException(e);
        }

        Traversal t = new VisibleTraverse(fake);
        t.preorder(c);

        // Simply embed the bebop xml as a child of the cms:item tag
        Element element = parent.newChildElement("cms:item", CMS.CMS_XML_NS);
        generateXMLBody(fake, element, c);
        String action = form.getAction();
        if (action == null) {
            final URL requestURL = Web.getWebContext().getRequestURL();

            if (requestURL == null) {
                action = state.getRequest().getRequestURI();
            } else {
                action = requestURL.getRequestURI();
            }
        }

        element.addAttribute(FormBuilderUtil.FORM_ACTION, action);

        FormBuilderXMLRenderer renderer = new FormBuilderXMLRenderer(element);
        renderer.setWrapAttributes(true);
        renderer.setWrapRoot(false);
        renderer.setRevisitFullObject(true);
        renderer.setWrapObjects(false);

        renderer.walk(this, SimpleXMLGenerator.ADAPTER_CONTEXT);

        // then, if the component is actually a form, we need
        // to print out any possible errors
        // Ideally we could do this as part of the "walk" but for now
        // that does not work because we don't pass in the page state
        // although that can always we updated.
        if (c instanceof Form) {
            Element infoElement = element.newChildElement(
                FormBuilderUtil.FORMBUILDER_FORM_INFO,
                FormBuilderUtil.FORMBUILDER_XML_NS);
            Form f = (Form) c;

            Traversal infoTraversal = new ComponentTraverse(state, ((Form) c)
                                                            .getFormData(state),
                                                            infoElement);
            infoTraversal.preorder(f);
        }

        // we need to generate the state so that it can be part of the form
        // and correctly included when the form is submitted.  We could
        // do this by iterating through the form data but it does not
        // seem like a good idea to just cut and paste the code out
        // of the PageState class
        fake.setControlEvent(c);
        fake.generateXML(element.newChildElement(
            FormBuilderUtil.FORMBUILDER_PAGE_STATE,
            FormBuilderUtil.FORMBUILDER_XML_NS));
    }

    protected void generateXMLBody(PageState state,
                                   Element parent,
                                   Component c) {
        // this is a no-op 
    }

    private class VisibleTraverse extends Traversal {

        PageState m_state;

        VisibleTraverse(PageState state) {
            m_state = state;
        }

        public void act(Component c) {
            try {
                m_state.setVisible(c, true);
            } catch (ClassCastException ex) {
                // Nada
            }
        }

    }

}
