/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
 * Copyright (C) 2006-2007 Chris Gilbert (Westsussex)
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


import com.arsdigita.forum.util.GlobalizationUtil;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
// import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.categorization.Category;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.security.UserContext;

import org.apache.log4j.Logger;


/**
 * Class to create a form for adding new topics.
 * 
 * A created topic is mapped to the forum parent category.
 *
 * @author <a href=mailto:sarah@arsdigita.com>Sarah Barwig</a>
 * @author rewritten by Chris Gilbert
 * @version $Id: TopicAddForm.java 1628 2007-09-17 08:10:40Z chrisg23 $
 */
public class TopicAddForm extends Form {

    /** Private logger instance for debugging purpose. */
    private static final Logger s_log = Logger.getLogger
        (TopicAddForm.class);

    /** Input field for name of new topic*/
    private TextField m_name;
    /** Input field for (short) description of new topic */
    private TextArea m_description;

    /**
     * Default Constructor builds a form to add a category.
     *
     */
    public TopicAddForm() {
        
        super("categoryAdd");
        setRedirecting(true);   // clear form and redirect back

        add(new Label(GlobalizationUtil.gz("forum.ui.topic.name")));
        m_name = new TextField("name");
        m_name.addValidationListener(new NotNullValidationListener());
        add(m_name);

        add(new Label(GlobalizationUtil.gz("forum.ui.topic.description")));
        m_description = new TextArea("description");
        m_description.setRows(5);
        m_description.setCols(60);
        m_description.setWrap(TextArea.SOFT);
        add(m_description);

	// Cancel button added cg
	// Would have used a saveCancel section but this would make existing
	// stylesheets for legacy forums miss the buttons
        Submit submit = new Submit(GlobalizationUtil.gz("forum.ui.topic.save"));
        final Submit cancel = new Submit(GlobalizationUtil.gz("forum.ui.cancel"));
        add(submit);
        add(cancel);
        addSubmissionListener(new FormSubmissionListener(){
            public void submitted(FormSectionEvent e)
                   throws FormProcessException {
                PageState state = e.getPageState();
                if (cancel.isSelected(state)){
                    fireCompletionEvent(state);
                    throw new FormProcessException("cancelled");
                }
            }
        });


        /*
         * Listener to process form data.  Just adds the topic, then
         * adds mappings.
         */
        addProcessListener
            (new FormProcessListener() {
                public void process( FormSectionEvent e ) {
                    PageState state = e.getPageState();

                    Forum forum = ForumContext.getContext(state).getForum();

                    String name = (String)m_name.getValue(state);
                    String description = (String)m_description.getValue(state);

                    Category topic = new Category();
                    topic.setName(name);
                    topic.setDescription(description);
                    topic.save();

                    Category parent = forum.getRootCategory();
                    parent.addChild(topic);
                    parent.save();
                    topic.setDefaultParentCategory(parent);
                    topic.save();

                    fireCompletionEvent(state);
                }
            });

        /*
         * Generates the object id for the new topic (category)
         */
        addInitListener
            (new FormInitListener() {
                    public void init( FormSectionEvent e ) {
                        PageState state = e.getPageState();
                        FormData data = e.getFormData();

                        if ( Kernel.getContext().getParty() == null ) {
                            UserContext.redirectToLoginPage(state.getRequest());
                        }
                    }
                }
             );
    }
}
