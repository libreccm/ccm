/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.formbuilder.ComponentAddObserver;
import com.arsdigita.formbuilder.CompoundComponent;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Iterator;

public class FormSectionWrapper extends PersistentComponent
    implements CompoundComponent {

    public static final Logger s_log = Logger.getLogger(FormSectionWrapper.class);

    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.formbuilder.FormSectionWrapper";

    public static final String FORM_SECTION_ITEM = "formSectionItem";
    public static final String VERSION = "version";

    private ComponentAddObserver m_addObserver = null;

    public FormSectionWrapper() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public FormSectionWrapper(String typeName) {
        super(typeName);
    }

    // Not with content page we can't :(
    /*
      public FormSectionWrapper(ObjectType type) {
      super(type);
      }
    */

    public FormSectionWrapper(DataObject obj) {
        super(obj);
    }

    public FormSectionWrapper(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public FormSectionWrapper(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }

    public static FormSectionWrapper create(FormSectionItem item,
                                            String version) {
        FormSectionWrapper wrapper = new FormSectionWrapper();
        wrapper.setFormSectionItem(item);
        wrapper.setVersion(version);
        return wrapper;
    }

    public void setVersion(String version) {
        set(VERSION, version);
    }

    public String getVersion() {
        return (String)get(VERSION);
    }

    public void setFormSectionItem(FormSectionItem item) {
        Assert.isTrue(ContentItem.DRAFT.equals(item.getVersion()),
                     "item is draft");

        setAssociation(FORM_SECTION_ITEM, item);
    }

    public FormSectionItem getFormSectionItem() {
        return (FormSectionItem)DomainObjectFactory.newInstance
            ((DataObject)get(FORM_SECTION_ITEM));
    }

    public void setComponentAddObserver(ComponentAddObserver observer) {
        m_addObserver = observer;
    }

    public Component createComponent() {
        FormSectionItem item = getFormSectionItem();
        if (item == null) {
            throw new FormUnavailableException("Form section item is missing");
        }

        if (ContentItem.LIVE.equals(getVersion())) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Looking for live version of form");
            }

            item = (FormSectionItem)item.getLiveVersion();


            if (item == null) {
                if (s_log.isDebugEnabled()) {
                    s_log.info("No live version found");
                }
                throw new FormUnavailableException("Form section is not live");
            }
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Using form item " + item.getOID());
        }

        PersistentFormSection section = item.getFormSection();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Using form section " + section.getOID());
        }
        if (m_addObserver != null) {
            section.setComponentAddObserver(m_addObserver);
        }

        return section.createComponent();
    }

    public void addComponent(PersistentComponent component) {
        FormSectionItem item = getFormSectionItem();
        Assert.exists(item, FormSectionItem.class);
        item.getFormSection().addComponent(component);
    }

    public void addComponent(PersistentComponent component,
                             int position) {
        FormSectionItem item = getFormSectionItem();
        Assert.exists(item, FormSectionItem.class);
        item.getFormSection().addComponent(component, position);
    }

    public void removeComponent(PersistentComponent component) {
        FormSectionItem item = getFormSectionItem();
        Assert.exists(item, FormSectionItem.class);
        item.getFormSection().removeComponent(component);
    }

    public void moveComponent(PersistentComponent component,
                              int toPosition) {
        FormSectionItem item = getFormSectionItem();
        Assert.exists(item, FormSectionItem.class);
        item.getFormSection().moveComponent(component, toPosition);
    }

    public void clearComponents() {
        FormSectionItem item = getFormSectionItem();
        Assert.exists(item, FormSectionItem.class);
        item.getFormSection().clearComponents();
    }

    public Iterator getComponentsIter() {
        return getFormSectionItem().getFormSection().getComponentsIter();
    }

    public boolean isEditable() {
        return false;
    }
}
