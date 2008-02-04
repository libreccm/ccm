/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 **/

package com.arsdigita.formbuilder.pdf;

import java.math.BigDecimal;
import java.util.Vector;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.caching.CacheTable;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.Web;

import com.arsdigita.formbuilder.CompoundComponent;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.WidgetLabel;

/**
   * Creation History Start
        @Author Name:	CS Gupta
        @Create Date:	23/11/2004
        @Class Name: 	Converter.java
        @Purpose: 	To retrive Form Data Controls's value  that works as input Parameter for XSL FO.
        @Company: 	Infoaxon Technology
        @Copyright: 	Copyright (c) 2004
        @Version : 	1.0
   * Creation History End
 */


public class PDFListener extends PersistentProcessListener {
    private static final Logger s_log = Logger.getLogger(PDFListener.class);
    public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.formbuilder.pdf.PDFListener";

    private static final CacheTable s_dataCache =
        new CacheTable ("pdf_formdata");

    public PDFListener() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public PDFListener(String typeName) {
        super(typeName);
    }

    public PDFListener(DataObject obj) {
        super(obj);
    }

    public PDFListener(OID oid) {
        super(oid);
    }

    public PDFListener(BigDecimal id) {
        super(id);
    }

    public static PDFListener create(String name, String description,String cName)
    {
        PDFListener objPDFListener = new PDFListener();
        objPDFListener.setup(name, description);
        return objPDFListener;
    }

    protected void setup(String name,
                         String description) {
        super.setup(name, description);
    }
    public boolean isContainerModified() {

        return false;
    }
    public FormProcessListener createProcessListener() {
        return new PDFProcessListener();
    }

    static Vector getFormData(String sessionId) {
        return (Vector) s_dataCache.get(sessionId);
    }

    private class PDFProcessListener implements FormProcessListener {
        public PDFProcessListener() {
        }

        public void process(FormSectionEvent objFormSectionEvent) throws FormProcessException
        {
            s_log.debug("PDFListener-process(FormSectionEvent)-Enter");
            FormData data = objFormSectionEvent.getFormData();

            //This is used to get Request Handle to set Form Data in Session
            HttpServletRequest req = objFormSectionEvent.getPageState().getRequest();
            HttpSession session = req.getSession(true);

            //These is used to stores Form Data
            Vector objOuterFormData = new Vector();

            ///////////////////////////////////
            // Retrieving Form Data here
            /*
            Iterator params = data.getParameters().iterator();
            while (params.hasNext()) {
                ParameterData param = (ParameterData) params.next();
                Object value = param.getValue();

                if (param.getName().toString().startsWith("form.")) {
                    StringTokenizer st = new StringTokenizer(value.toString(),"^^");
                    String strValue="";
                    while(st.hasMoreTokens())
                    {
                       strValue=(String)st.nextElement();
                       objVector.add(strValue);
                    }
                }
            }
            */

            DataAssociationCursor components = getForm().getComponents();
            while (components.next()) {
                PersistentComponent c = (PersistentComponent)
                    DomainObjectFactory.newInstance(components.getDataObject());

                handleComponent(data, c, objOuterFormData);
            }

            // Use cache here as session can't share data between webapps
            s_dataCache.put(session.getId(), objOuterFormData);

            if (s_log.isDebugEnabled()) {
                s_log.debug("Saving with key: " + session.getId());
                Iterator fdIter = objOuterFormData.iterator();
                while (fdIter.hasNext()) {
                    Vector entry = (Vector) fdIter.next();
                    s_log.debug(entry.firstElement().toString() + ": " +
                                entry.lastElement().toString());
                }
            }

            String strURL = req.getScheme() + "://" +
                            Web.getConfig().getServer() +
                            "/ccm-formbuilder-pdf/Converter/";

            s_log.debug("PDFListener-process(FormSectionEvent)-Redirecting");
            throw new RedirectSignal(strURL, true);
        }
        
        private void handleComponent(FormData data, PersistentComponent c,
                                     Vector output) {
            if (c instanceof PersistentWidget) {
                PersistentWidget w = (PersistentWidget) c;
                Vector entry = new Vector();

                WidgetLabel label = WidgetLabel.findByWidget(w);
                if(null == label)
                    entry.add(w.getParameterName());
                else
                    entry.add(label.getLabel());

                Object value = w.getValue(data);
                if(null != value && value.getClass().isArray()) {
                    Object[] values = (Object[]) value;

                    StringBuffer buf = new StringBuffer();
                    for(int i = 0; i < values.length; i++) {
                        buf.append(values[i].toString());
                        if(values.length - 1 != i) buf.append(' ');
                    }

                    value = buf;
                }

                if (null == value)
                    entry.add("");
                else
                    entry.add(value.toString());

                output.add(entry);
            } else if (c instanceof CompoundComponent) {
                Iterator i = ((CompoundComponent) c).getComponentsIter();
                while (i.hasNext()) {
                    handleComponent(data, (PersistentComponent) i.next(),
                                    output);
                }
            } else if (s_log.isDebugEnabled()) {
                s_log.debug("Ignoring component: " + c.getClass().getName());
            }
        }
    }
}
