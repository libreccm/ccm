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
package com.arsdigita.forum.ui;

import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormStep;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Wizard;
import com.arsdigita.bebop.event.FormCancelListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.forum.Post;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.toolbox.ui.TextTypeWidget;
import com.arsdigita.xml.Element;
import com.arsdigita.util.HtmlToText;
import com.arsdigita.util.MessageType;

import org.apache.log4j.Logger;

/**
 * Class PostForm
 *
 * @author Jon Orris (jorris@arsdigita.com)
 *
 * @version $Revision #1 $DateTime: 2004/08/17 23:26:27 $
 */
public abstract class PostForm extends Wizard implements Constants {

    public static final String versionId =
        "$Id: PostForm.java 755 2005-09-02 13:42:47Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static final Logger s_log = Logger.getLogger(PostForm.class);

    private TextField m_subject;
    private TextArea m_body;
    private TextTypeWidget m_bodyType;

    public PostForm(String name) {
        super(name, new SimpleContainer());        
        setMethod(POST);
    }

    protected void setupComponent() {
        add(dataEntryStep());
        add(confirmStep());

        addInitListener(new PostInitListener());
        addProcessListener(new PostProcessListener());
        addCancelListener(new PostCancelListener());
    }

    protected void setSubject(PageState state,
                              String text) {
        m_subject.setValue(state, text);
    }

    protected Container dataEntryStep() {
        FormStep initial = new FormStep(
            "initial",
            new SimpleContainer("forum:postForm", FORUM_XML_NS));

        m_subject = new TextField(new StringParameter("subject"));
        m_subject.addValidationListener(new NotEmptyValidationListener());
        m_subject.addValidationListener(new StringLengthValidationListener(250));
        m_subject.setSize(60);
        initial.add(m_subject);

        m_body = new TextArea(new StringParameter("message"),
                              8, 60, TextArea.SOFT);
        m_body.addValidationListener(new NotEmptyValidationListener());
        m_body.addValidationListener(new StringLengthValidationListener(4000));
        initial.add(m_body);

        m_bodyType = new TextTypeWidget(new StringParameter("bodyType"),
                                        MessageType.TEXT_PLAIN);
        initial.add(m_bodyType);

        return initial;
    }

    protected Container confirmStep() {
        SimpleContainer postContainer = new SimpleContainer
            ("forum:postConfirm", FORUM_XML_NS) {
                
                public void generateXML(PageState state,
                                        Element parent) {
                    Element content = generateParent(parent);
                    
                    Element subject = content.newChildElement("subject");
                    subject.setText((String)m_subject.getValue(state));

                    Element body = content.newChildElement("body");
                    body.setText(HtmlToText.generateHTMLText(
                                     (String)m_body.getValue(state),
                                     (String)m_bodyType.getValue(state)));
                }
            };
        
        return postContainer;
    }

    protected abstract Post getPost(PageState state,
                                    boolean create);

    protected void initWidgets(PageState state,
                               Post post) {
        if (post != null) {
            m_subject.setValue(state, post.getSubject());
            m_body.setValue(state, post.getBody());
            m_bodyType.setValue(state, post.getBodyType());
        }
    }
    
    protected void processWidgets(PageState state,
                                  Post post) {
        post.setSubject((String)m_subject.getValue(state));
        post.setBody((String)m_body.getValue(state),
                     (String)m_bodyType.getValue(state));
    }

    private class PostInitListener implements FormInitListener {        
        public void init(FormSectionEvent e) {
            PageState state = e.getPageState();
            
            if ( Kernel.getContext().getParty() == null ) {
                UserContext.redirectToLoginPage(state.getRequest());
            }
            
            initWidgets(state,
                        getPost(state, false));
        }
    }

    private class PostProcessListener implements FormProcessListener {   
        public void process(FormSectionEvent e)
            throws FormProcessException {

            final PageState state = e.getPageState();
            
            Post post = getPost(state, true);
            processWidgets(state, post);
            
            post.sendNotifications();
            post.sendModeratorAlerts();
        
            post.save();

            fireCompletionEvent(state);
            /* XXX: This is the right thing to do, however it results in 
               the wizard not being reset. I haven't tracked down why.

            state.clearControlEvent();
            try {
                throw new RedirectSignal( state.stateAsURL(), true );
            } catch( IOException ex ) {
                throw new UncheckedWrapperException( ex );
            }
            */
        }
    }

    private class PostCancelListener implements FormCancelListener { 
        public void cancel(FormSectionEvent e) 
            throws FormProcessException {

            PageState state = e.getPageState();
            fireCompletionEvent(state);
        }
    }
}
