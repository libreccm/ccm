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

package com.arsdigita.cms.contentsection;

//import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
//import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
//import com.arsdigita.util.parameter.ParameterError;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;
// import com.arsdigita.util.parameter.ResourceParameter;
// import com.arsdigita.util.parameter.URLParameter;
//import com.arsdigita.util.StringUtils;

// import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
//import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Configuration parameter to configure a content section during startup.
 *
 * Configures parameter which are not persisted in the database and may be
 * changes during each startup of the system.
 * @author pb
 */
public final class ContentSectionConfig extends AbstractConfig {

    /** Private Logger instance.  */
    private static final Logger s_log =
                                Logger.getLogger(ContentSectionConfig.class);


    // Parameters controlling Overdue Task alerts:

    /**
     * A list of workflow tasks, and the associated events for which alerts
     * have to be sent.
     * Parameter name TASK_ALERTS in the old initializer system / enterprise.init
     * Specifies when to generate email alerts: by default, generate email alerts
     * on enable, finish, and rollback (happens on rejection) changes.
     * There are four action types for each task type: enable, disable, finish,
     * and rollback.
     * Example:
     * (Note that the values below are based on the task labels, and as such are
     * not globalized.)
     * <pre>
     * taskAlerts = {
     *      { "Authoring",
     *        { "enable", "finish", "rollback" }
     *      },
     *      { "Approval",
     *        { "enable", "finish", "rollback" }
     *      },
     *      { "Deploy",
     *        { "enable", "finish", "rollback" }
     *      }
     *  };
     * </pre>
     *
     * Default value (site-wide) is handled via the parameter
     * <pre>com.arsdigita.cms.default_task_alerts</pre>.
     * Section-specific override can be added here. Only do so if you are
     * changing for a good reason from the default for a specific content section.
     */
    private final Parameter
            m_taskAlerts = new StringArrayParameter(
                               "com.arsdigita.cms.loader.section_task_alerts",
                               Parameter.REQUIRED,
                               null  );
                               // new String[] {}  );

    /**
     * Should we send alerts about overdue tasks at all?
     * Send alerts when a task is overdue (has remained in the \"enabled\" state
     * for a long time)
     * Parameter SEND_OVERDUE_ALERTS in the old initializer system, default false
     */
    private final Parameter
            m_sendOverdueAlerts = new BooleanParameter(
                        "com.arsdigita.cms.contentsection.send_overdue_alerts",
                        Parameter.REQUIRED,
                        false  );


    /**
     * The time between when a task is enabled (i.e. it is made available for
     * completion) and when it is considered overdue (in HOURS).
     */
    // XXX Once the Duration of a Task can actually be maintained (in the UI,
    // or initialization parameters), we should use the value in the DB, and
    // get rid of this
    // Parameter name TASK_DURATION in the old initializer system.
    // Description: How long a task can remain \"enabled\" before it is
    // considered overdue (in hours)
    private final Parameter
            m_taskDuration = new IntegerParameter(
                        "com.arsdigita.cms.contentsection.task_duration",
                        Parameter.REQUIRED,
                        new Integer(96)  );


    /**
     * The time to wait between sending successive alerts on the same
     * overdue task (in HOURS).
     * Parameter name OVERDUE_ALERT_INTERVAL in old initializer system
     * Description: Time to wait between sending overdue notifications on the
     * same task (in hours)
     */
    private final Parameter
            m_alertInterval = new IntegerParameter(
                        "com.arsdigita.cms.contentsection.alert_interval",
                        Parameter.REQUIRED,
                        new Integer(24)  );


    /**
     * The maximum number of alerts to send about any one overdue task.
     * Parameter name MAX_ALERTS in old initializer system.
     * Description: The maximum number of alerts to send that a single task is
     * overdue
     */
    private final Parameter
            m_maxAlerts = new IntegerParameter(
                        "com.arsdigita.cms.contentsection.max_alerts",
                        Parameter.REQUIRED,
                        new Integer(5)  );



// ///////////////////////////////////////////////////////
//
// Set of parameters which specify a new content section
// to be created during next startup of the system. If
// the section already exists (created during previous
// startups) parameters are ignored and not processed.
//
// ///////////////////////////////////////////////////////

    /**
     * The name of a new content section to be create during next boot of the
     * system. During subsequent startups, when the section to be created
     * already exists, the parameter is ignored and processing skipped.
     *
     * Empty by default so no processing will take place.
     */
    private final Parameter
            m_newContentSectionName = new StringParameter(
                            "com.arsdigita.cms.contentsection.new_section_name",
                            Parameter.REQUIRED,
                            null);


    /**
     * Constructor, do not instantiate this class directly!
     *
     * @see ContentSection#getConfig()
     */
    public ContentSectionConfig() {
        
        // parameters for alerts (notifications)
        register(m_taskAlerts);
        register(m_sendOverdueAlerts);
        register(m_taskDuration);
        register(m_alertInterval);
        register(m_maxAlerts);

        // parameters for creation of a new (additional) content section
        register(m_newContentSectionName);
    }


// //////////////////////////////////////////////////////////
//
// Processing of parameters which handle overdue notification
//
// //////////////////////////////////////////////////////////

    /**
     * Retrieve the list of workflow tasks and events for each tasks which
     * should receive overdue notification alerts
     *
     * XXX wrong implementation !!
     * Should be moved to CMS or section initializer because may be modified
     * each startup. Does not store anything in database so not a loader task!
     */
    public List getTaskAlerts() {
//        String[] m_taskAlerts = (String[]) get(m_contentTypeList);
        return Arrays.asList(m_taskAlerts);
    }


    /**
     * Retrieve whether to send overdue information for unfinished tasks.
     */
    public Boolean getSendOverdueAlerts() {
            return ((Boolean) get(m_sendOverdueAlerts)).booleanValue();
        }

    /**
     * Retrieve time between when a task is enabled and when it is considered
     * overdue.
     */
    public Integer getTaskDuration() {
            return ((Integer) get(m_taskDuration)).intValue();
        }

    /**
     * Retrieve the time to wait between sending successive alerts on the same
     * overdue task (in HOURS).
     */
    public Integer getAlertInterval() {
            return (Integer) get(m_alertInterval);
        }

    /**
     * Retrieve the maximum number of alerts to send that a single task is
     * overdue
     */
    public Integer getMaxAlerts() {
            return (Integer) get(m_maxAlerts);
        }



// ///////////////////////////////////////////////////////
//
// Processing of parameters which specify a new content
// section to be created during (next) startup of the
// system. The initializer has to check if it already
// exists and skip processing.
//
// ///////////////////////////////////////////////////////


    /**
     * Retrieve the name of a new content-section to create.
     *
     * The initializer has to check if it already exists and skip processing.
     */
    public String getNewContentSectionName() {
            return (String) get(m_newContentSectionName);
        }

}
