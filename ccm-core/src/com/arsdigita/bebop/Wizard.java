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
import java.util.Set;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.util.Traversal;

/**
 * The Wizard class can be used in conjunction with FormStep to build a series
 * of forms that gather information from the user in several stages. A simple
 * two stage confirmation step would be built like this:
 *
 * <blockquote><pre>
 * // The first step asks the user for a subject and body.
 * FormStep one = new FormStep("one");
 *
 * one.add(new Label("Subject"));
 * final TextField subject = new TextField("subject");
 * one.add(subject);
 *
 * one.add(new Label("Body"));
 * final TextArea body = new TextArea("body");
 * one.add(body);
 *
 *
 * // The second step displays the subject and body to the user as it will
 * // appear when posted.
 * GridPanel two = new GridPanel(1);
 * two.add(new Label() {
 *     public String getLabel(PageState ps) {
 *         return "Subject: " + subject.getValue(ps);
 *     }
 * });
 * two.add(new Label() {
 *     public String getLabel(PageState ps) {
 *         return (String) body.getValue(ps);
 *     }
 * });
 * two.add(new Label("Are you sure?"));
 *
 *
 * // Create the wizard and add the steps in the appropriate order.
 * Wizard form = new Wizard("post");
 * form.add(one);
 * form.add(two);
 *
 * // Add a process listener to actually save the message.
 * form.addProcessListener(new FormProcessListener() {
 *     public void process(FormSectionEvent evt) {
 *         PageState ps = evt.getPageState();
 *         System.out.println("SAVING MESSAGE");
 *         System.out.println("Subject: " + subject.getValue(ps));
 *         System.out.println("Body: " + body.getValue(ps));
 *     }
 * });
 * </pre></blockquote>
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 *
 *updated chris.gilbert@westsussex.gov.uk
 * Allow wizard to store intermediate state in session.
 * This means that the state is not lost if a link is included
 * in one of the steps (commonly used if a step involves
 * adding several items to a list, with remove links required
 * for items in the list)
 *
 * Also, allow steps to be skipped - see comments above hideStep method
 *
 * Also, allow to jump to a specific step (useful for providing quick
 * links on a confirmation step in a wizard with several steps)
 **/

public class Wizard extends MultiStepForm {

    public final static String versionId = "$Id: Wizard.java 1414 2006-12-07 14:24:10Z chrisgilbert23 $ by $Author: chrisgilbert23 $, $DateTime: 2004/08/16 18:10:38 $";

    private static Logger s_log = Logger.getLogger(Wizard.class);
    private ModalContainer m_steps = new ModalContainer();

    // if true, display finish button on every page, so user can jump out of the
    // wizard at any stage
    private boolean m_quickFinish;

    private Submit m_cancel = new Submit("Cancel");
    private Submit m_back = new Submit("<< Back");
    private Submit m_next = new Submit("Next >>");
    private Submit m_finish = new Submit("Finish");

    private Component m_first = null;
    private Component m_last = null;

    private RequestLocal m_hiddenSteps = new RequestLocal() {
	protected Object initialValue(PageState state) {
            return new HashSet();
        }
    };

    // constructors for backward compatibility cg
    public Wizard(String name) {
        this(name, new GridPanel(1));
    }

    public Wizard(String name, Container panel) {
         this(name, panel, false, false);
    }

    // new constructors
    public Wizard(String name, boolean quickFinish, boolean useSession) {
	this(name, new GridPanel(1), quickFinish, useSession);
    }

    public Wizard(
               String name,
               Container panel,
               boolean quickFinish,
               boolean useSession) {

	super(name, panel, useSession);
        super.add(m_steps);
        m_quickFinish = quickFinish;
        BoxPanel buttons = new BoxPanel(BoxPanel.HORIZONTAL);
        buttons.add(m_cancel);
        buttons.add(m_next);
        buttons.add(m_back);
        buttons.add(m_finish);

        super.add(buttons);
	m_steps.addModeChangeListener(new SkipStepListener());
        forwardSubmission();
    }
        
    /**
     *
     * hide a specific step - this should be invoked in a submission listener
     * in order to affect the current request.
     *
     * The problem this addresses is that the set of components that makes up
     * this wizard is added when the wizard is constructed and this set is
     * shared by all instances of the application. In some
     * circumstances we want different instances to have different steps.
     * For example in a forum, we may have an optional file attachment step
     * or image step. We need to add these components to the wizard, as they
     * may be used for some forums, but we should be able to disable them
     * if the administrator of a specific forum decides that file uploads
     * are not allowed on that forum.
     *
     * Every hidden step should be specified in a submission listener
     * that is fired regardless of which button is pressed.
     *
     * A limitation of this method is that it cannot be used to hide all but
     * one component. The reason is that when the wizard is first displayed
     * there has been no submission and so inclusion of previous, finish etc
     * is based on the full set of steps.
     *
     * @param step
     * @param state
     */
    public void hideStep(int step, PageState state) {
	Set hiddenSteps = (Set) m_hiddenSteps.get(state);
    	hiddenSteps.add(m_steps.get(step));
        m_hiddenSteps.set(state, hiddenSteps);
    }




   
    public void register(Page p) {
        super.register(p);
        
	p.setVisibleDefault(m_back, false);
        if (!m_quickFinish) {
	    p.setVisibleDefault(m_finish, false);
        }
         Traversal trav = new Traversal () {
                 protected void act(Component c) {
                    if (c instanceof Widget) {
                        ((Widget) c).setValidationGuard(
                                                        new Widget.ValidationGuard() {
                                                            public boolean shouldValidate(PageState ps) {
                                                                return m_back.isSelected(ps);
                                                            }
                                                        });
                    }
                }
            };

        trav.preorder(this);
    }

    public void add(Container step) {
        if (m_first == null) { 
	    m_first = step; 
	}
        m_steps.add(step);
        m_last = step;
    }

    public void add(Container step, int constraints) {
        add(step);
    }

    public Submit getCancel() {
        return m_cancel;
    }

    public Submit getFinish() {
        return m_finish;
    }

    public boolean isFirst(PageState ps) {

	Set hiddenSteps = (Set) m_hiddenSteps.get(ps);
        for (int i = 0; i < m_steps.size(); i++) {
            Component comp = m_steps.get(i);
            if (!hiddenSteps.contains(comp)) {
        	return comp == m_steps.getVisibleComponent(ps);
            }
        }
        // will reach this if all steps are hidden
        return false;
    }

    public boolean isLast(PageState ps) {
	Set hiddenSteps = (Set) m_hiddenSteps.get(ps);
        for (int i = m_steps.size() - 1; i >= 0; i--) {
            Component comp = m_steps.get(i);
            if (!hiddenSteps.contains(comp)) {
                return comp == m_steps.getVisibleComponent(ps);
            }
        }
        return false;
    }

    public void next(PageState ps) {
        if (isFirst(ps)) {
            m_back.setVisible(ps, true);
        }
        m_steps.next(ps);
        if (isLast(ps)) {
            m_next.setVisible(ps, false);
            m_finish.setVisible(ps, true);
        }
    }

    public void previous(PageState ps) {
	if (isLast(ps)) {
            m_next.setVisible(ps, true);
            if (!m_quickFinish) {
 		m_finish.setVisible(ps, false);
            }
        }
        m_steps.previous(ps);
        if (isFirst(ps)) {
	     m_back.setVisible(ps, false);
	}
    }

    public void jumpTo(PageState ps, int index) {
	m_steps.jumpTo(ps, index);
        if (isLast(ps)) {
            m_next.setVisible(ps, false);
            m_finish.setVisible(ps, true);
        } else {
            m_next.setVisible(ps, true);
            if (!m_quickFinish) {
                 m_finish.setVisible(ps, false);
            } else {
                 m_finish.setVisible(ps, true);

            }
	}
        if (isFirst(ps)) {
            m_back.setVisible(ps, false);
        } else {
            m_back.setVisible(ps, true);
        }

    }

    protected void fireSubmitted(FormSectionEvent evt)
        throws FormProcessException {
        super.fireSubmitted(evt);

        PageState ps = evt.getPageState();
        if (m_cancel.isSelected(ps)) {
            super.fireCancel(evt);
            ps.reset(this);
            throw new FormProcessException("cancel hit");
        }
    }
    protected void fireProcess(FormSectionEvent evt)
        throws FormProcessException {
        PageState ps = evt.getPageState();
	s_log.debug("process form");
        if (m_next.isSelected(ps)) {
            next(ps);
        } else if (m_back.isSelected(ps)) {
            previous(ps);
        } else if (m_cancel.isSelected(ps)) {
            super.fireCancel(evt);
            ps.reset(this);
        } else if (m_finish.isSelected(evt.getPageState())) {
            super.fireProcess(evt);
            ps.reset(this);

        }
    }

    private class SkipStepListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
            PageState state = e.getPageState();
            s_log.debug("state of underlying modal container changed - new visible component is "
                                       + m_steps.indexOf(m_steps.getVisibleComponent(state)));
            Component newComponent = m_steps.getVisibleComponent(state);
            if (((Set) m_hiddenSteps.get(state)).contains(newComponent)) {
                s_log.debug("I'm going to skip this step");
                if (m_next.isSelected(state)) {
                    m_steps.next(state);
                } else if (m_back.isSelected(state)) {
                    m_steps.previous(state);
                }
            }

	}
    }


}
