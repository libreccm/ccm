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
package com.arsdigita.bebop;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.xml.Element;

/**
 * The MultiStepForm class is a simple extension to Form that modifies the
 * behavior of Form with respect to widgets that are not visible. Instead of
 * generating no XML for widgets that are not visible, the MultiStepForm class
 * generates XML that preserves the sate of non visible widgets using hidden
 * form variables  or session state. This allows a single form to preserve data across multiple
 * submits thereby allowing multi step forms such as confirmation forms or
 * wizards to be easily created. When creating complicated wizards it is
 * useful to use MultiStepForm in conjunction with the {@link FormStep} class
 * to provide cascading initialization of the different steps in the form.
 *
 *
 * updated chris.gilbert@westsussex.gov.uk - allow session based form
 * Using a session based multistepform allows steps to contain action links.
 * A hidden field form would lose it's hidden fields when a link was pressed
 *
 *
 * @see FormStep
 * @see Wizard
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: 1.1 $ $Date: 2006/11/13 10:14:35 $
 **/

public class MultiStepForm extends Form implements FormInitListener{

    public final static String versionId =
		"$Id: MultiStepForm.java 1537 2007-03-23 15:33:34Z chrisgilbert23 $ by $Author: chrisgilbert23 $, $DateTime: 2004/08/16 18:10:38 $";

    private boolean m_useSession = false;

    // parameter that allows allows action links on forms -
    // as they do not preserve the magic tag
    // they would normally trigger initialisation, so if we are editing an
    // item, all the attributes will be reset. This parameter suppresses
    // initialisation while the form is active
    private BooleanParameter m_suppressInit = new BooleanParameter("suppressInit");
    private static Logger s_log = Logger.getLogger(MultiStepForm.class);

    private Set m_widgets = new HashSet();
    /**
     * @see Form#Form(String)
     **/

    public MultiStepForm(String name) {
	this(name, false);
    }

    public MultiStepForm(String name, boolean useSession) {
         super(name);
         m_useSession = useSession;
         m_suppressInit.setDefaultValue(Boolean.FALSE);
         addInitListener(this);
    }

    /**
     * @see Form#Form(String, Container)
     **/

    public MultiStepForm(String name, Container panel) {
        this(name, panel, false);
    }

    public MultiStepForm(String name, Container panel, boolean useSession) {
        super(name, panel);
        m_useSession = useSession;
	m_suppressInit.setDefaultValue(Boolean.FALSE);

        addInitListener(this);
		
    }

    public void register(Page p) {
        super.register(p);
    	p.addComponentStateParam(this, m_suppressInit);
        Traversal trav = new Traversal () {
                protected void act(Component c) {
                    if (c instanceof Widget && !(c instanceof Submit)) {
                        m_widgets.add(c);
                    }
                }
            };

        trav.preorder(this);
    }

    protected Element generateXMLSansState(PageState ps, Element parent) {
        if (m_useSession) {
    	    return generateSessionXML(ps, parent);
        } else {
            return generateHiddenFieldXML(ps, parent);
        }
    }

    protected Element generateSessionXML(PageState ps, Element parent) {
  	s_log.debug("******* generating xml *********");
        // get formdata here as it forces init event to fire, and
        // we definitely want that to happen before we do anything else
        // because we want to clear session information BEFORE
        // generating any XML
        FormData data = getFormData(ps);
        HttpSession session = ps.getRequest().getSession(true);
        for (Iterator it = m_widgets.iterator(); it.hasNext();) {
            // store current widget value in session whether visible or not
	    // (this ensures that visible values that are preset are not lost
            // eg the init flag on the FIRST step.
	    Widget w = (Widget) it.next();

            // retrieve value from session if it is stored and there is not a value in the formdata

            Object value = session.getAttribute(w.getName());
            s_log.debug("value of  " + w.getName() + " in session is " + value);

            if (value != null
                     && ps.isVisible(w)
                     && data.getParameter(w.getName()).getValue() == null) {
            	s_log.debug("setting value for "
                             + w.getName()
                             + " as "
                             + value
                             + " from session");
                w.setValue(ps, value);
	    } else {
            	if (data.getParameter(w.getName()).getValue()
                    != null && !w.getName().equals(getModel().getMagicTagName())) {
                    // note don't store the magic tag in session, else it is difficult to
                    // know when we are in a new form
                    session.setAttribute(w.getName(), w.getValue(ps));
                    s_log.debug("storing "
                    	        + w.getName()
                                + " -> "
                                + w.getValue(ps)
                                + " in session");
            	}
            }
	}
        Element form = super.generateXMLSansState(ps, parent);
        return form;
    }



    protected Element generateHiddenFieldXML(PageState ps, Element parent) {
        Element form = super.generateXMLSansState(ps, parent);

        for (Iterator it = m_widgets.iterator(); it.hasNext(); ) {
            Widget w = (Widget) it.next();
            if (!ps.isVisibleOnPage(w)) {
                if (getFormData(ps).getParameter(w.getName()).getValue() != null) {
                    Hidden h = new Hidden(w.getParameterModel()) {
                            public Form getForm() {
                                return MultiStepForm.this;
                            }
                        };
                    h.generateXML(ps, form);
                }
            }
        }

        return form;
    }

    /**
     * should be called either at the end of a process listener, or
     * at the start of an init listener to prevent session information
     * from previous submissions hanging around
     * @param ps
     */
    public void clearSession(PageState ps) {
    	if (m_useSession) {
            HttpSession session = ps.getRequest().getSession(true);

            for (Iterator it = m_widgets.iterator(); it.hasNext();) {
                Widget w = (Widget) it.next();
                s_log.debug("removing " + w.getName() + " from session");
                session.removeAttribute(w.getName());
            }
        }
    }
       
    public void init(FormSectionEvent e) throws FormProcessException {
    	PageState state = e.getPageState();
        s_log.debug("------- init event fired");
		if (!isInitialised(state)) {

            s_log.debug("new form started - clear session information from previous use");
            clearSession(e.getPageState());
            state.setValue(m_suppressInit, Boolean.TRUE);
        }

    }

    public boolean isInitialised (PageState state) {
	return !(state.getValue(m_suppressInit) == null
		|| !((Boolean) state.getValue(m_suppressInit)).booleanValue());
	}

	

}
