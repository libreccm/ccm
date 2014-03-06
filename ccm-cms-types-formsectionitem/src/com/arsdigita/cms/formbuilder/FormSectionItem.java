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
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.cms.dispatcher.XMLGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.ui.BaseAddObserver;
import com.arsdigita.formbuilder.ui.PlaceholdersInitListener;
import com.arsdigita.formbuilder.ui.FormBuilderXMLRenderer;
import com.arsdigita.formbuilder.util.FormBuilderUtil;
import com.arsdigita.web.Web;
import com.arsdigita.web.URL;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.formbuilder.ui.ComponentTraverse;

import java.math.BigDecimal;


public class FormSectionItem extends ContentPage
    implements XMLGenerator {

    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.formbuilder.FormSectionItem";

    public static final String FORM_SECTION = "formSection";

    public FormSectionItem() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public FormSectionItem(String typeName) {
        super(typeName);
    }

    // Not with content page we can't :(
    /*
      public FormSectionItem(ObjectType type) {
      super(type);
      }
    */

    public FormSectionItem(DataObject obj) {
        super(obj);
    }

    public FormSectionItem(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public FormSectionItem(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }

    protected void beforeSave() {
        if (isNew() && get(FORM_SECTION) == null) {
            PersistentFormSection formSection = new PersistentFormSection();
            formSection.setAdminName(getName());
            formSection.save();
            setAssociation(FORM_SECTION, formSection);
        }

        super.beforeSave();
    }


    protected void beforeDelete() {
        PersistentFormSection form = getFormSection();
        form.delete();

        super.beforeDelete();

        /*
        Collection children = form.getComponents();
        form.clearComponents();

        Iterator i = children.iterator();
        while (i.hasNext()) {
            PersistentComponent c = (PersistentComponent) i.next();
            c.delete();
        }

        super.delete();

        form.delete();
        */
    }

    public boolean copyProperty(CustomCopy src,
                                Property property,
                                ItemCopier copier) {
        if (property.getName().equals(FORM_SECTION)) {
            setAssociation(
                FORM_SECTION, (new FormCopier())
                .copyFormSection(((FormSectionItem)src).getFormSection()));
            return true;
        }

        return super.copyProperty(src, property, copier);
    }


    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public PersistentFormSection getFormSection() {
        return new PersistentFormSection((DataObject)get(FORM_SECTION));
    }


    public void generateXML(PageState state,
                            Element parent,
                            String useContext) {
        PersistentFormSection form = getFormSection();
        form.setComponentAddObserver( new BaseAddObserver());

        Form c = new Form("formSectionItem");
        c.add((FormSection)form.createComponent());

        c.addInitListener(new PlaceholdersInitListener());

        // Make the form readonly
        Traversal t = new Traversal() {
                public void act(Component c) {
                    try {
                        Widget widget = (Widget)c;
                        widget.setDisabled();
                        widget.setReadOnly();
                    } catch (ClassCastException ex) {
                        // Nada
                    }
                }
            };
        t.preorder(c);

        // Fake the page context for the item, since we
        // have no access to the real page context.
        Page p = new Page("dummy");
        p.add(c);
        p.lock();

        PageState fake;
        try {
            fake = p.process(new NoParametersHttpServletRequest(
                                 state.getRequest()), state.getResponse());
        } catch (Exception e) {
            throw new UncheckedWrapperException(e);
        }

        Traversal t2 = new VisibleTraverse(fake);
        t2.preorder(c);

        // Simply embed the bebop xml as a child of the cms:item tag
        Element element = parent.newChildElement("cms:item", CMS.CMS_XML_NS);

        String action = c.getAction();
        if (action == null) {
            final URL requestURL = Web.getWebContext().getRequestURL();

            if (requestURL == null) {
                action = state.getRequest().getRequestURI();
            } else {
                action = requestURL.getRequestURI();
            }
        }

        element.addAttribute(FormBuilderUtil.FORM_ACTION, action);
        
        FormBuilderXMLRenderer renderer = 
            new FormBuilderXMLRenderer(element);
        
        renderer.setWrapAttributes(true);
        renderer.setWrapRoot(false);
        renderer.setRevisitFullObject(true);
        renderer.setWrapObjects(false);
        
        renderer.walk(this, SimpleXMLGenerator.ADAPTER_CONTEXT);

        // we need to generate the state so that it can be part of the form
        // and correctly included when the form is submitted.  We could
        // do this by iterating through the form data but it does not
        // seem like a good idea to just cut and paste the code out
        // of the PageState class
        state.generateXML(element.newChildElement
                          ("cms:pageState", CMS.CMS_XML_NS));

        // then, if the component is actually a form, we need
        // to print out any possible errors
        // Ideally we could do this as part of the "walk" but for now
        // that does not work because we don't pass in the page state
        // although that can always we updated.
        if (c instanceof Form) {
            Element infoElement = 
                element.newChildElement(FormBuilderUtil.FORMBUILDER_FORM_INFO,
                                        FormBuilderUtil.FORMBUILDER_XML_NS);
            Form f = (Form)c;

            Traversal infoTraversal = 
                new ComponentTraverse(state, ((Form)c).getFormData(state), 
                                      infoElement);
            infoTraversal.preorder(f);
        }

        // we need to generate the state so that it can be part of the form
        // and correctly included when the form is submitted.  We could
        // do this by iterating through the form data but it does not
        // seem like a good idea to just cut and paste the code out
        // of the PageState class
        fake.setControlEvent(c);    
        fake.generateXML(element.newChildElement
                         (FormBuilderUtil.FORMBUILDER_PAGE_STATE, 
                          FormBuilderUtil.FORMBUILDER_XML_NS));
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
