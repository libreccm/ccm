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
package com.arsdigita.formbuilder.ui;

import com.arsdigita.formbuilder.MetaObject;
import java.math.BigDecimal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.formbuilder.util.FormBuilderUtil;
import com.arsdigita.bebop.PageState;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.util.UncheckedWrapperException;


public class ProcessListenerProperties extends MetaObjectProperties {
    SingleSelectionModel m_form;
    SingleSelectionModel m_type;
    SingleSelectionModel m_action;

    public ProcessListenerProperties(SingleSelectionModel form,
                                     SingleSelectionModel type,
                                     SingleSelectionModel action,
                                     String app) {
        super(app, PersistentProcessListener.class);

        m_form = form;
        m_type = type;
        m_action = action;

        addForms();
    }


    public void setVisible(PageState state,
                           boolean visible) {

        // TODO: This doesn't seem right. It alters the state of the component via
        // the parent method before checking preconditions.
        super.setVisible(state, visible);

        if (visible) {
            BigDecimal action = (BigDecimal)m_action.getSelectedKey(state);
            BigDecimal type = (BigDecimal)m_type.getSelectedKey(state);

            if (action != null) {
                String className = null;
                OID oid = new OID(ACSObject.BASE_DATA_OBJECT_TYPE, action);
                Session s = SessionManager.getSession();
                DataObject o = s.retrieve(oid);

                className = (String)o.get("defaultDomainClass");

                setFormVisible(state, className);
            } else if (type != null) {
                setFormVisible(state, type);
            } else {
                throw new RuntimeException("Neither action or type parameters are set");
            }
        }
    }

    protected PropertiesEditor getPropertiesEditor(MetaObject obj) {
        String formName = obj.getPropertiesFormName();
        Object o = FormBuilderUtil.instantiateObject(formName,
                                                     new Class[] {
                                                         String.class,
                                                         SingleSelectionModel.class,
                                                         SingleSelectionModel.class,
                                                     },
                                                     new Object[] {
                                                         "", //prefix,
                                                         m_form,
                                                         m_action
                                                     });

        try {
            PropertiesEditor e = (PropertiesEditor)o;
            return e;
        } catch (ClassCastException ex) {
            try {
                PropertiesForm f = (PropertiesForm)o;
                PropertiesEditor e = new PropertiesEditor(f);
                return e;
            } catch (ClassCastException ex2) {
                throw new UncheckedWrapperException("Editor must be a PropertiesEditor or PropertiesForm", ex2);
            }
        }
    }
}
