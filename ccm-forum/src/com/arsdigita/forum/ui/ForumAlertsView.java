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
package com.arsdigita.forum.ui;


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.forum.DailySubscription;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.ForumSubscription;
import com.arsdigita.forum.ThreadSubscription;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * 
 * 
 */
class ForumAlertsView extends SimpleContainer implements Constants {
    private static final Logger s_log = Logger.getLogger
        (ForumAlertsView.class);

    /**
     * Standard Constructor
     */
    ForumAlertsView() {
        add(forumAlertsSegment());
        add(threadAlertsSegment());
    }

    private Component forumAlertsSegment() {
        SimpleContainer seg = new SimpleContainer(FORUM_XML_PREFIX + ":forumAlerts", 
                                                  FORUM_XML_NS);
        seg.add(forumAlertsForm());
        return seg;
    }

    private Component threadAlertsSegment() {
        SimpleContainer seg = new SimpleContainer(FORUM_XML_PREFIX + ":threadAlerts", 
                                                  FORUM_XML_NS);
        seg.add(threadAlertsForm());
        return seg;
    }

    private Component forumAlertsForm() {
        Form alertsForm = new Form("instantAlerts", new ColumnPanel(2));

        final RadioGroup instant = new RadioGroup("instant");
        instant.addOption(new Option(Text.gzAsStr("forum.ui.yes")));
        instant.addOption(new Option(Text.gzAsStr("forum.ui.no")));
        alertsForm.add(new Label(Text.gz("forum.ui.receive_instant_alerts")));
        alertsForm.add(instant);

        final RadioGroup daily = new RadioGroup("daily");
        daily.addOption(new Option(Text.gzAsStr("forum.ui.yes")));
        daily.addOption(new Option(Text.gzAsStr("forum.ui.no")));
        alertsForm.add(new Label(Text.gz("forum.ui.receive_daily_summary")));
        alertsForm.add(daily);

        alertsForm.add(new Label(""));
        alertsForm.add(new Submit(Text.gz("forum.ui.save")));

        alertsForm.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent e) {
                    FormData data = e.getFormData();
                    PageState s = e.getPageState();
                    Party party = Kernel.getContext().getParty();
                    Forum forum = ForumContext.getContext(s).getForum();

                    ForumSubscription fSub =
                        ForumSubscription.getFromForum(forum);
                    if (fSub.isSubscribed(party)) {
                        instant.setValue(s,"Yes");
                    } else {
                        instant.setValue(s, "No");
                    }

                    DailySubscription dSub = (DailySubscription)
                        DailySubscription.getFromForum(forum);
                    if (dSub.isSubscribed(party)) {
                        daily.setValue(s,"Yes");
                    } else {
                        daily.setValue(s, "No");
                    }
                }
            });

        alertsForm.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e)
                    throws FormProcessException {

                    FormData data = e.getFormData();
                    PageState s = e.getPageState();
                    Party party = Kernel.getContext().getParty();
                    Forum forum = ForumContext.getContext(s).getForum();

                    ForumSubscription fSub =
                        ForumSubscription.getFromForum(forum);
                    DailySubscription dSub = (DailySubscription)
                        DailySubscription.getFromForum(forum);

                    if (data.get("instant").equals("Yes")) {
                        fSub.subscribe(party);
                    } else if (data.get("instant").equals("No")) {
                        fSub.unsubscribe(party);
                    } else {
                        throw new FormProcessException(
                            "Received bad option for instant: "
                            + data.get("instant"));
                    }
                    fSub.save();

                    if (data.get("daily").equals("Yes")) {
                        dSub.subscribe(party);
                    } else if (data.get("daily").equals("No")) {
                        dSub.unsubscribe(party);
                    } else {
                        throw new FormProcessException(
                            "Received bad option for daily: "
                            + data.get("daily"));
                    }
                    dSub.save();
                }
            });
        return alertsForm;
    }

    private Component threadAlertsForm() {
        Form form = new Form("dailyAlerts");
        form.setRedirecting(true);
        form.add(new ThreadAlertsList() {
                public Element generateAlertXML(ThreadSubscription sub) {
                    Element subEl = super.generateAlertXML(sub);
                    
                    subEl.addAttribute("param", "delete");
                    return subEl;
                }
            });
        
        CheckboxGroup boxes = new CheckboxGroup(
            new ArrayParameter(new BigDecimalParameter("delete")));
        form.add(boxes);
        
        form.add(new Submit(Text.gz("forum.ui.delete")),
                 FULL_WIDTH | RIGHT);
        
        form.addProcessListener(new DeleteProcesser());
        
        return form;
    }

    class DeleteProcesser implements FormProcessListener {
        public void process(FormSectionEvent e) {
            FormData data = e.getFormData();
            PageState s = e.getPageState();
            Party party = Kernel.getContext().getParty();
            
            BigDecimal[] deletes = (BigDecimal[])
                data.get("delete");
            
            if (deletes != null) {
                for (int i = 0; i < deletes.length ; i++) {
                    try {
                        ThreadSubscription tSub =
                            new ThreadSubscription(deletes[i]);
                        tSub.unsubscribe(party);
                        tSub.save();
                    } catch (DataObjectNotFoundException x) {
                        throw new UncheckedWrapperException(x);
                    }
                }
            }
        }
    }
}
