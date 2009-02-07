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
package com.arsdigita.cms.ui.lifecycle;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.TooManyListenersException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.NumberInRangeValidationListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.cms.lifecycle.Phase;
import com.arsdigita.cms.lifecycle.PhaseCollection;
import com.arsdigita.cms.lifecycle.PhaseDefinitionCollection;
import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.cms.ui.item.ItemWorkflowRequestLocal;
import com.arsdigita.cms.ui.workflow.WorkflowRequestLocal;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.workflow.CMSEngine;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.cms.workflow.CMSTaskType;
import com.arsdigita.kernel.User;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.workflow.simple.TaskException;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;

/**
 * <p>A form to select and apply a lifecycle to a content item.</p>
 *
 * @author Michael Pih
 * @author Xixi D'moon &lt;xdmoon@redhat.com&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ItemLifecycleSelectForm.java 1643 2007-09-17 14:19:06Z chrisg23 $
 */
class ItemLifecycleSelectForm extends BaseForm {
    public static final String versionId =
        "$Id: ItemLifecycleSelectForm.java 1643 2007-09-17 14:19:06Z chrisg23 $" +
        "$Author: chrisg23 $" +
        "$DateTime: $";

    private static final Logger s_log = Logger.getLogger
        (ItemLifecycleSelectForm.class);

    private final static String LIFECYCLE  = "lifecycle";
    private final static String START_DATE = "start_date";
    private final static String END_DATE   = "end_date";
    private final static String NOTIFICATION_DAYS  = "notifyDays";
    private final static String NOTIFICATION_HOURS  = "notifyHours";

    private final ContentItemRequestLocal m_item;
    private final WorkflowRequestLocal m_workflow;

    // Form widgets
    private final SingleSelect m_cycleSelect;
    private final Date m_startDate;
    private final TextField m_startHour;
    private final TextField m_startMinute;
    private final SingleSelect m_startAmpm;
    private final Date m_endDate;
    private final TextField m_endHour;
    private final TextField m_endMinute;
    private final SingleSelect m_endAmpm;
    private TextField m_notificationDays;
    private TextField m_notificationHours;

    public ItemLifecycleSelectForm(final ContentItemRequestLocal item) {
        super("PublishItem", gz("cms.ui.item.lifecycle.apply"));

        m_item = item;
        m_workflow = new ItemWorkflowRequestLocal();

        m_cycleSelect = new SingleSelect(new BigDecimalParameter(LIFECYCLE));
        try {
            m_cycleSelect.addPrintListener(new OptionPrinter());
        } catch (TooManyListenersException tmle) {
            throw new UncheckedWrapperException(tmle);
        }
        addField(gz("cms.ui.item.lifecycle"), m_cycleSelect);

        // Start date

        m_startDate = new Date(new DateParameter(START_DATE) {
                protected final Calendar getCalendar
                    (final HttpServletRequest sreq) {
                    final Calendar cal = super.getCalendar(sreq);

                    cal.setLenient(false);

                    return cal;
                }
            });
        addField(gz("cms.ui.item.lifecycle.start_date"), m_startDate);

        // Start time

        final BoxPanel startTime = new BoxPanel(BoxPanel.HORIZONTAL);
        addField(gz("cms.ui.item.lifecycle.start_time"), startTime);

        // Hour

        m_startHour = new TextField(new IntegerParameter("start_hour"));
        startTime.add(m_startHour);

        m_startHour.setSize(3);
        m_startHour.addValidationListener
            (new NumberInRangeValidationListener(1, 12));

        // Minute

        m_startMinute = new TextField(new IntegerParameter("start_minute"));
        startTime.add(m_startMinute);

        m_startMinute.setSize(3);
        m_startMinute.addValidationListener
            (new NumberInRangeValidationListener(0, 59));

        // AM/PM

        m_startAmpm = new SingleSelect(new IntegerParameter("start_ampm"));
        startTime.add(m_startAmpm);

        m_startAmpm.addOption(new Option("0", "am"));
        m_startAmpm.addOption(new Option("1", "pm"));

        // Time zone

        startTime.add(new Label(new TimeZonePrinter()));

        // Expiration date

        m_endDate = new Date(new DateParameter(END_DATE) {
                protected final Calendar getCalendar
                    (final HttpServletRequest sreq) {
                    final Calendar cal = super.getCalendar(sreq);

                    cal.setLenient(false);

                    return cal;
                }
            });
        addField(gz("cms.ui.item.lifecycle.end_date"), m_endDate);

        // End time

        final BoxPanel endTime = new BoxPanel(BoxPanel.HORIZONTAL);
        addField(gz("cms.ui.item.lifecycle.end_time"), endTime);

        // Hour

        m_endHour = new TextField(new IntegerParameter("end_hour"));
        endTime.add(m_endHour);

        m_endHour.setSize(3);
        m_endHour.addValidationListener
            (new NumberInRangeValidationListener(1, 12));

        // Minute

        m_endMinute = new TextField(new IntegerParameter("end_minute"));
        endTime.add(m_endMinute);

        m_endMinute.setSize(3);
        m_endMinute.addValidationListener
            (new NumberInRangeValidationListener(0, 59));

        // AM/PM

        m_endAmpm = new SingleSelect(new IntegerParameter("end_ampm"));
        endTime.add(m_endAmpm);

        m_endAmpm.addOption(new Option("0", "am"));
        m_endAmpm.addOption(new Option("1", "pm"));

        endTime.add(new Label(new TimeZonePrinter()));


        m_notificationDays =
            new TextField(new IntegerParameter(NOTIFICATION_DAYS));
        m_notificationDays.setSize(4);
        m_notificationHours =
            new TextField(new IntegerParameter(NOTIFICATION_HOURS));
        m_notificationHours.setSize(4);
        SimpleContainer cont = new SimpleContainer();
        cont.add(m_notificationDays);
        cont.add(new Label(GlobalizationUtil.globalize("cms.ui.item.days"), false));
        cont.add(m_notificationHours);
        cont.add(new Label(GlobalizationUtil.globalize("cms.ui.item.hours"), false));

        addField(gz("cms.ui.item.notification_period"), cont);

        // A hidden field that checks to see if the user wants publish
        // with a start time earlier than current time.


        addAction(new Submit("finish", gz("cms.ui.item.lifecycle.publish")));

        // Form listeners

        addValidationListener(new ValidationListener());
        addSecurityListener(PUBLISH, m_item);
        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    private class OptionPrinter implements PrintListener {
        public final void prepare(final PrintEvent e) {
            final ContentSection section =
                CMS.getContext().getContentSection();

            final LifecycleDefinitionCollection ldc =
                section.getLifecycleDefinitions();
            ldc.addOrder("label");

            final SingleSelect target = (SingleSelect) e.getTarget();

            while (ldc.next()) {
                final LifecycleDefinition ld = ldc.getLifecycleDefinition();
                final PhaseDefinitionCollection pdc = ld.getPhaseDefinitions();

                // XXX domlay this seems a little weak.  perhaps
                // there's a better way to determine if a lifecycle is
                // ready to be applied to an item.

                if (!pdc.isEmpty()) {
                    target.addOption
                        (new Option(ld.getID().toString(),
                                    ld.getLabel()));
                }

                pdc.close();
            }

            ldc.close();
        }
    }

    private class InitListener implements FormInitListener {
        public final void init(final FormSectionEvent e) {
            final PageState state = e.getPageState();

            final ContentItem item = m_item.getContentItem(state);

            if (item.isPublished()) {
                // If the item is published, select the currently
                // associated lifecycle.

                final LifecycleDefinition ld =
                    item.getLifecycle().getLifecycleDefinition();
                m_cycleSelect.setValue(state, ld.getID());
            } else {
                // Set the default lifecycle (if it exists).

                final ContentSection section =
                    CMS.getContext().getContentSection();
                final LifecycleDefinition ld =
                    ContentTypeLifecycleDefinition.getLifecycleDefinition
                    (section, item.getContentType());

                if (ld != null) {
                    m_cycleSelect.setValue(state, ld.getID());
                }
            }

            // Set the default start date.

            // XXX Isn't just new Date() sufficient?
            final java.util.Date start = new java.util.Date
                (System.currentTimeMillis());
            m_startDate.setValue(state, start);

            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);

            // If the hour is 12, then Calendar.get(Calendar.HOUR)
            // returns 0 (from the 24 hour time - 12).  We want it to
            // return 12.

            if (calendar.get(Calendar.HOUR) == 0) {
                m_startHour.setValue(state, new Integer(12));
            } else {
                m_startHour.setValue
                    (state, new Integer(calendar.get(Calendar.HOUR)));
            }

            final Integer min = new Integer(calendar.get(Calendar.MINUTE));

            if (min.intValue() < 10) {
                m_startMinute.setValue(state, "0" + min.toString());
            } else {
                m_startMinute.setValue(state, min.toString());
            }

            m_startAmpm.setValue
                (state, new Integer(calendar.get(Calendar.AM_PM)));

            BigInteger [] defaultTime =
                BigInteger.valueOf(ContentSection.
                                   getConfig().getDefaultNotificationTime()).
                divideAndRemainder(BigInteger.valueOf(24));


            m_notificationDays.
                setValue(state, new Integer(defaultTime[0].intValue()));
            m_notificationHours.
                setValue(state, new Integer(defaultTime[1].intValue()));
        }
    }

    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
            throws FormProcessException {
            final PageState state = e.getPageState();

            final Integer startHour = (Integer) m_startHour.getValue(state);
            Integer startMinute = (Integer) m_startMinute.getValue(state);

            if (startMinute == null) {
                startMinute = new Integer(0);
            }

            final Integer startAmpm = (Integer) m_startAmpm.getValue(state);

            final Integer endHour = (Integer) m_endHour.getValue(state);
            Integer endMinute = (Integer) m_endMinute.getValue(state);

            if (endMinute == null) {
                endMinute = new Integer(0);
            }

            final Integer endAmpm = (Integer) m_endAmpm.getValue(state);

            // Instantiate the instance of the content type.
            final ContentItem item = m_item.getContentItem(state);

            final BigDecimal defID = (BigDecimal) m_cycleSelect.getValue
                (state);
            Assert.assertNotNull(defID);
            final LifecycleDefinition cycleDef = new LifecycleDefinition
                (defID);

            java.util.Date startDate =
                (java.util.Date) m_startDate.getValue(state);

            final Calendar start = Calendar.getInstance();
            start.setTime(startDate);
            start.set(Calendar.AM_PM, startAmpm.intValue());
            start.set(Calendar.MINUTE, startMinute.intValue());
            start.set(Calendar.AM_PM, startAmpm.intValue());
            if (startHour.intValue() != 12) {
                start.set(Calendar.HOUR_OF_DAY,
                          12 * startAmpm.intValue() + startHour.intValue());
                start.set(Calendar.HOUR, startHour.intValue());
            } else {
                if (startAmpm.intValue() == 0) {
                    start.set(Calendar.HOUR_OF_DAY, 0);
                    start.set(Calendar.HOUR, 0);
                } else {
                    start.set(Calendar.HOUR_OF_DAY, 12);
                    start.set(Calendar.HOUR, 0);
                }
            }
            startDate = start.getTime();

            java.util.Date endDate =
                (java.util.Date) m_endDate.getValue(state);

            if (endDate != null) {
                final Calendar end = Calendar.getInstance();

                end.setTime(endDate);
                end.set(Calendar.AM_PM, endAmpm.intValue());
                end.set(Calendar.MINUTE, endMinute.intValue());
                end.set(Calendar.AM_PM, endAmpm.intValue());

                if (endHour.intValue() != 12) {
                    end.set(Calendar.HOUR_OF_DAY,
                            12 * endAmpm.intValue() + endHour.intValue());
                    end.set(Calendar.HOUR, endHour.intValue());
                } else {
                    if (endAmpm.intValue() == 0) {
                        end.set(Calendar.HOUR_OF_DAY, 0);
                        end.set(Calendar.HOUR, 0);
                    } else {
                        end.set(Calendar.HOUR_OF_DAY, 12);
                        end.set(Calendar.HOUR, 0);
                    }
                }
                endDate = end.getTime();
            }

            // If the item is already published, remove the current lifecycle.
            // Do not touch the live version.
            if (item.isPublished()) {
                item.removeLifecycle(item);
                item.save();
            }

            // Apply the new lifecycle.
            ContentItem pending = item.publish(cycleDef, startDate);
            final Lifecycle lifecycle = pending.getLifecycle();

            // XXX domlay Whoa.  This must be broken for multiphase
            // lifecycles.

            if (endDate != null) {
                
                // update individual phases
                final PhaseCollection phases = lifecycle.getPhases();

                while (phases.next()) {
                    final Phase phase = phases.getPhase();
                    java.util.Date thisEnd = phase.getEndDate();
                    java.util.Date thisStart = phase.getStartDate();
                    if (thisStart.compareTo(endDate) > 0) {
                         phase.setStartDate(endDate);
                         phase.save();
                    }

                    if (thisEnd == null || thisEnd.compareTo(endDate) > 0) {
                        phase.setEndDate(endDate);
                        phase.save();
                    }
                }
            }
            
            // endOfCycle may be the original date according to lifecycle phase definitions, or endDate if that was before
            // natural end of lifecycle
            java.util.Date endOfCycle = lifecycle.getEndDate();
            if (endOfCycle != null) {

                // if advance notification is requested (!= 0)
                // add another phase at the start of which the user is notified
                Integer notificationDays =
                    (Integer)m_notificationDays.getValue(state);
                Integer notificationHours =
                    (Integer)m_notificationHours.getValue(state);
                java.util.Date notificationDate = null;

                int notificationPeriod = 0;
                if (notificationDays != null) {
                    notificationPeriod += notificationDays.intValue()*24;
                }
                if (notificationHours != null) {
                    notificationPeriod += notificationHours.intValue();
                }
                
                if (notificationPeriod > 0) {
                    notificationDate =
                        computeNotificationDate(endOfCycle, notificationPeriod);
                    s_log.debug("adding custom phase");
                    Phase expirationImminentPhase =
                        lifecycle.addCustomPhase("expirationImminent",
                                                 new Long(notificationDate.getTime()),
                                                 new Long(endOfCycle.getTime()));
                    expirationImminentPhase.
                        setListenerClassName("com.arsdigita.cms.lifecycle.NotifyLifecycleListener");
                    expirationImminentPhase.save();
                }
            }

            // Force the lifecycle scheduler to run to avoid any
            // scheduler delay for items that should be published
            // immediately.
            pending.getLifecycle().start();

            item.save();

            final Workflow workflow = m_workflow.getWorkflow(state);
	    try {
                finish(workflow, item, Web.getContext().getUser());
            } catch (TaskException te) {
                throw new FormProcessException(te);
            }
            // redirect to /content-center if streamlined creation mode is active.
            if (ContentSection.getConfig().getUseStreamlinedCreation()) {
                throw new RedirectSignal
                    (URL.there(state.getRequest(),
                               Utilities.getWorkspaceURL()),
                     true);
            }
        }
    }
    
    static void finish(Workflow workflow, ContentItem item, User user) throws TaskException {
        if (workflow != null) {
            final Engine engine = Engine.getInstance(CMSEngine.CMS_ENGINE_TYPE);
            // ;

            final Iterator iter = engine.getEnabledTasks
                (user, workflow.getID()).iterator();

            while (iter.hasNext()) {
                final CMSTask task = (CMSTask) iter.next();
                if( s_log.isDebugEnabled() )
                    s_log.debug( "Task is " + task.getOID().toString() );
                if (task.getTaskType().getID().equals(CMSTaskType.DEPLOY)) {
                    s_log.debug("Found DEPLOY task, ID=" + CMSTaskType.DEPLOY);
                    task.finish(user);
                }
            }
            if (ContentSection.getConfig().getDeleteWorkflowAfterPublication()) {
                workflow.delete();
            } else {
                // restart the workflow by recreating it
                // from the same workflow template
                WorkflowTemplate t = workflow.getWorkflowTemplate();
                workflow.delete();
                workflow = t.instantiateNewWorkflow();
                workflow.setObject(item);
                workflow.start(user);
                workflow.save();
            }
        }
    }

    private class ValidationListener implements FormValidationListener {
        public void validate(FormSectionEvent e) throws FormProcessException {
            final PageState state = e.getPageState();

            final Integer startHour = (Integer) m_startHour.getValue(state);
            if (startHour == null) {
                throw new FormProcessException
                    (lz("cms.ui.item.start_time_incomplete"));
            }

            Integer startMinute = (Integer) m_startMinute.getValue(state);
            if (startMinute == null) {
                startMinute = new Integer(0);
            }

            Integer startAmpm = (Integer) m_startAmpm.getValue(state);

            java.util.Date startDate = (java.util.Date) m_startDate.getValue(state);
            if (startDate == null) {
                throw new FormProcessException
                    (lz("cms.ui.item.lifecycle.start_date_invalid"));
            }

            java.util.Date nowDate = new java.util.Date(System.currentTimeMillis());

            Calendar cStart = Calendar.getInstance();
            Calendar cNow = Calendar.getInstance();
            cStart.setTime(startDate);
            cNow.setTime(nowDate);

            if (startHour.intValue() != 12) {
                cStart.set(Calendar.HOUR_OF_DAY,
                           12 * startAmpm.intValue() + startHour.intValue());
                cStart.set(Calendar.HOUR, startHour.intValue());
            } else {
                if (startAmpm.intValue() == 0) {
                    cStart.set(Calendar.HOUR_OF_DAY, 0);
                    cStart.set(Calendar.HOUR, 0);
                } else {
                    cStart.set(Calendar.HOUR_OF_DAY, 12);
                    cStart.set(Calendar.HOUR, 0);
                }
            }

            // Give the user extra 5 minutes before form complains
            // start time's in the past.

            cStart.set(Calendar.MINUTE, startMinute.intValue() + 5);
            cStart.set(Calendar.AM_PM, startAmpm.intValue());
            cStart.set(Calendar.SECOND, cNow.get(Calendar.SECOND));
            cStart.set(Calendar.MILLISECOND, cNow.get(Calendar.MILLISECOND));

            if (cNow.after(cStart)) {
                throw new FormProcessException
                    (lz("cms.ui.item.lifecycle.start_date_in_past"));
            }

            Integer endHour = (Integer) m_endHour.getValue(state);
            Integer endMinute = (Integer) m_endMinute.getValue(state);
            java.util.Date endDate =
                (java.util.Date) m_endDate.getValue(state);

            if (endHour == null && (endMinute != null || endDate != null)) {
                throw new FormProcessException
                    (lz("cms.ui.item.lifecycle.end_time_incomplete"));
            }

            if (endMinute == null && endHour != null) {
                endMinute = new Integer(0);
            }

            boolean timeBlank = (endHour == null) && (endMinute == null);

            Integer endAmpm = (Integer) m_endAmpm.getValue(state);

            if (endDate == null && !timeBlank) {
                throw new FormProcessException
                    (lz("cms.ui.item.lifecycle.end_date_invalid"));
            }

            if (endDate != null) {
                Calendar cEnd = Calendar.getInstance();
                cEnd.setTime(endDate);

                if (endHour.intValue() != 12) {
                    cEnd.set(Calendar.HOUR_OF_DAY,
                             12 * endAmpm.intValue() + endHour.intValue());
                    cEnd.set(Calendar.HOUR, endHour.intValue());
                } else {
                    if (endAmpm.intValue() == 0) {
                        cEnd.set(Calendar.HOUR_OF_DAY, 0);
                        cEnd.set(Calendar.HOUR, 0);
                    } else {
                        cEnd.set(Calendar.HOUR_OF_DAY, 12);
                        cEnd.set(Calendar.HOUR, 0);
                    }
                }

                // Give the user extra 5 minutes before form complains
                // end time's in the past.

                cEnd.set(Calendar.MINUTE, endMinute.intValue()+5);
                cEnd.set(Calendar.AM_PM, endAmpm.intValue());
                cEnd.set(Calendar.SECOND, cNow.get(Calendar.SECOND));
                cEnd.set(Calendar.MILLISECOND, cNow.get(Calendar.MILLISECOND));

		//check if the end date is prior to the start date
		if(cStart.after(cEnd)) {
		    throw new FormProcessException
			(lz("cms.ui.item.lifecycle.end_date_before_start_date"));
		}

                Integer notificationDays =
                    (Integer)m_notificationDays.getValue(state);
                Integer notificationHours =
                    (Integer)m_notificationHours.getValue(state);

                int notificationPeriod = 0;
                if (notificationDays != null) {
                    notificationPeriod += notificationDays.intValue()*24;
                }
                if (notificationHours != null) {
                    notificationPeriod += notificationHours.intValue();
                }

                if (notificationPeriod > 0) {
                    // point in time for notification == end date - notificationPeriod
                    java.util.Date notificationDate =
                        computeNotificationDate(cEnd.getTime(),
                                                notificationPeriod);
                    s_log.debug("cStart (Date): " + cStart.getTime());
                    s_log.debug("notificationDate: " + notificationDate);
                    // complain if date for notification is before the start date
                    if (notificationDate.before( cStart.getTime() )) {
                        s_log.debug("notification date is before start date!");
                        String errorMessage = (String)
                            GlobalizationUtil
                            .globalize("cms.ui.item.notification_period_before_start")
                            .localize();
                        throw new FormProcessException(errorMessage);
                    }
                    else {
                        s_log.debug("notification date is after start date, OK");
                    }
                }
            }
        }
    }

    public class TimeZonePrinter implements PrintListener {
        public void prepare(PrintEvent e) {
            final Label target = (Label) e.getTarget();
            if (ContentSection.getConfig().getHideTimezone()) {
                target.setLabel("");
            } else {
                final PageState state = e.getPageState();
                final Calendar mStart = Calendar.getInstance();
                java.util.Date st = (java.util.Date) m_startDate.getValue(state);

                if (st != null) {
                    mStart.setTime((java.util.Date) m_startDate.getValue(state));
                }

                final String zone = mStart.getTimeZone().getDisplayName
                    (true, TimeZone.SHORT);

                target.setLabel(zone);
            }
        }
    }

    /**
     * Find out at which date a notification (about an item that is about
     * to expire) should be sent, based on the endDate (== date at which the
     * item is unpublished) and the notification period.
     *@param endDate the endDate of the lifecycle, i.e. the date when the item
     *  is going to be unpublished
     *@param notification how many hours the users shouls be notified in advance
     */
    private java.util.Date computeNotificationDate(java.util.Date endDate,
                                                   int notificationPeriod) {
        if (endDate == null) {
            return null;
        }

        return new
            java.util.Date(endDate.getTime() - (long)notificationPeriod * 3600000L);
    }
}
