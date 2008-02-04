/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.ui;


import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.EmailValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.kernel.User;
import com.arsdigita.mail.Mail;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.web.Web;

/**
 * This component allows user to send an email of the document
 *
 * @author Crag Wolfe
 */
class FileSendColleaguePane extends SimpleContainer
    implements DMConstants
{
    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(SearchPane.class);

    private final FileInfoPropertiesPane m_parent;
    
    private static final String EMAIL_PARAM_NAME = "scEmail";

    private final TrimmedStringParameter m_emailParam 
        = new TrimmedStringParameter(EMAIL_PARAM_NAME);

    private Component m_sendForm;
    private Component m_thankYou;

    FileSendColleaguePane(FileInfoPropertiesPane parent) {

        m_parent = parent;

        GridPanel spacer = new GridPanel(1);
        spacer.add(new Label("   "));
        add(spacer);

        m_sendForm = new SendForm();
        add(m_sendForm);
        
        m_thankYou = buildThankYou();
        add(m_thankYou);
    }

    //public void register(Page p) {
    //    super.register(p);
    //    p.addGlobalStateParam(m_emailParam);
    //    
    //}
    
    public void initState(PageState ps) {
        m_sendForm.setVisible(ps, true);
        m_thankYou.setVisible(ps, false);
    }

    private Component buildThankYou() {
        BoxPanel panel = new BoxPanel();
        
        panel.add(FILE_SEND_COLLEAGUE_THANKS);
        ActionLink backLink = new ActionLink
            (FILE_SEND_COLLEAGUE_THANKS_RETURN_LINK.
             localize().toString());
        backLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    m_parent.displayPropertiesAndActions(state);
                }});
        panel.add(backLink);

        return panel;
    }

    private class SendForm extends Form 
        implements FormProcessListener {
        
        public SendForm() {
            super("sendEmail",new BoxPanel());

            GridPanel panel = new GridPanel(2);

            panel.add(FILE_SEND_COLLEAGUE_FORM_EMAIL);
            TextField emailField = new TextField(m_emailParam);
            emailField.addValidationListener
                (new EmailValidationListener());
            panel.add(emailField, GridPanel.LEFT);
            panel.add(new Submit(FILE_SEND_COLLEAGUE_SUBMIT));
            
            add(panel);

            addProcessListener(this);
        }

        public void process(FormSectionEvent e)
            throws FormProcessException {
            
            PageState state = e.getPageState();
            FormData data = e.getFormData();
            this.setVisible(state, false);
            m_thankYou.setVisible(state, true);

            // send the mail

            User u = Web.getContext().getUser();

            s_log.debug(FILE_SEND_COLLEAGUE_RETURN_ADDRESS
                        .localize().toString());

            
            Mail mail = new Mail
                ((String)data.get(EMAIL_PARAM_NAME),
                 FILE_SEND_COLLEAGUE_RETURN_ADDRESS.localize().toString(),
                 FILE_SEND_COLLEAGUE_SUBJECT.localize().toString(),
                 FILE_SEND_COLLEAGUE_MESSAGE.localize().toString()
                 +" "+u.getName());
            Document doc = new Document
                ((BigDecimal) state.getValue(m_parent.getFileIDParam()));
            
            try {
                FileAsset fa = doc.getFile();
                if (fa == null) { return; }
                
                ByteArrayOutputStream assetStream = new ByteArrayOutputStream();
                long readBytes = fa.writeBytes(assetStream);
                if (readBytes == 0) { return; }

                String mimeTypeString = "application/octet-stream";
                MimeType mimeType = fa.getMimeType();
                if (mimeType != null) {
                    mimeTypeString = mimeType.getMimeType();
                }
                s_log.debug("mimeTypeString: "+mimeTypeString);
                mail.attach(assetStream.toByteArray(),
                            mimeTypeString,
                            doc.getTitle());
                mail.send();
            } catch (java.io.IOException iox) {
                iox.printStackTrace();
                throw new FormProcessException
                    ("An error occurred while trying to send document");
            } catch (javax.mail.MessagingException mex) {
                mex.printStackTrace();
                throw new FormProcessException
                    ("An error occurred while trying to send document");
            } 
        }
    }
}
