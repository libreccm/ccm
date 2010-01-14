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
package com.arsdigita.cms.publishToFile;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.ClassParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

import org.apache.log4j.Logger;

/**
 * Configuration object for the publish-to-file service.
 *
 * UNFINISHED WORK - NOT USABLE YET!
 * ToDO: Parameter destination is a list, but we have no ListParameter type for now.
 * We have either to develop a ListParameter class or treat this parameter as
 * a set of StringArrayParameter.
 * 
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: $
 */
public class PublishToFileConfig extends AbstractConfig {

    private static final Logger s_log = Logger.getLogger(PublishToFileConfig.class);

    private static PublishToFileConfig s_conf;

    /**
     * List of publish destinations for content types.
     * Each element is a four-element list in the format
     * { "content type", "root directory", "shared storage", "url stub" }.
     *
     * Content type   is the object type of the content type.
     * Root directory must be a path to a writable directory, relative to the
     *                webapp root.
     * Shared storage must be true if the root directory is shared NFS storage,
     *                false otherwise.
     * URL stub       is the path component of the URL from which the live
     *                server will serve from this directory.
     */
    private final Parameter m_destination;

    /**
     * Class which implements PublishToFileListener used to perform
     * additional actions when publishing or unpublishing to the file system.
     */
    private final Parameter m_publishListener;

    /**
     * Time (in seconds)  after system startup to wait before starting to monitor
     * publishToFile queue.
     *
     * Set startupDelay to 0 to disable the processing of the queue. This
     * disables the processing of templates as well so it will affect a basic
     * functionality of the system and is not recommended under normal
     * conditions.
     */
    private final Parameter m_startupDelay;

    /**
     * Time (in seconds) between checking if there are entries in the
     * publishToFile queue.
     *
     * A value <= 0 disables processing the queue on this server.
     */
    private final Parameter m_pollDelay;

    /**
     * Time to wait (seconds) before retrying to process a failed entry.
     */
    private final Parameter m_retryDelay;

    /**
     * Time to wait (seconds) before aborting item request.
     */
    private final Parameter m_requestTimeout;

    /**
     * Number of queue entries to process at once.
     */
    private final Parameter m_blockSize;

    /**
     * Number of times a failed queue entry will be reprocessed.
     * Maximum Fail Count for actions in Queue Manager.
     * If Fail Count in database is more than specified limit, Queue Manager
     * will ignore the action.
     * The default value -1 will ignore this parameter.
     */
    private final Parameter m_maximumFailCount;
    
    /**
     * Method used to select entries for processing.
     * 'QueuedOrder'  - in  queued order.
     * 'GroupByParent'- group entries according to parent when selecting items
     *                  (allows optimizations if a listener task required for
     *                  all elements in a folder can be done only once for the
     *                  folder).
    */
    private final Parameter m_blockSelectMethod;



    /**
     * Get a PublishToFileConfig instance.
     * 
     * Singelton pattern, don't instantiate a notificationConfig object using
     * the constructor directly.
     * @return
     */
    static synchronized PublishToFileConfig getConfig() {
        if (s_conf == null) {
            s_conf = new PublishToFileConfig();
            s_conf.load();
        }

        return s_conf;
    }

    /**
     * Constructor.
     *
     * Do not use it directly!
     */
    public PublishToFileConfig() {

        /**
         * List of publish destinations for content types.
         * Each element is a four-element list in the format
         * { "content type", "root directory", "shared storage", "url stub" }.
         *
         * Content type   is the object type of the content type.
         * Root directory must be a path to a writable directory, relative to the
         *                webapp root.
         * Shared storage must be true if the root directory is shared NFS storage,
         *                false otherwise.
         * URL stub       is the path component of the URL from which the live
         *                server will serve from this directory.
         */
        // Unfinished work! Not usable!
        // Parameter is a list, but we have currently no ListParameter type.
        m_destination = new StringParameter
            ("com.arsdigita.cms.publishToFile.destination", Parameter.REQUIRED,
             "{ " +
             "  { 'com.arsdigita.cms.ContentItem',  " +
             "    'p2fs', " +
             "    false,  " +
             "    '/p2fs' }, " +
             "  { 'com.arsdigita.cms.Template',   " +
             "   'packages/content-section/templates',  " +
             "   false,   " +
             "   '/templates' }  " +
             "} "
             );

        /**
         * Class which implements PublishToFileListener used to perform
         * additional actions when publishing or unpublishing to the file system.
         */
        // publishListener = "com.arsdigita.cms.publishToFile.PublishToFile";
        m_publishListener = new ClassParameter
            ("com.arsdigita.cms.publishToFile.publish_listener", Parameter.REQUIRED,
            PublishToFile.class);

        // Queue management parameters.

        /**
         * Time (in seconds)  after system startup to wait before starting to
         * monitor publishToFile queue.
         *
         * Set startupDelay to 0 to disable the processing of the queue. This
         * disables the processing of templates as well so it will affect a basic
         * functionality of the system and is not recommended under normal
         * conditions.
         */
        m_startupDelay = new IntegerParameter
            ("com.arsdigita.cms.publishToFile.startup_delay", Parameter.REQUIRED,
             new Integer(30));

        /**
         * Time (in seconds) between checking if there are entries in the
         * publishToFile queue.
         *
         * A value <= 0 disables processing the queue on this server.
         */
        m_pollDelay = new IntegerParameter
            ("com.arsdigita.cms.publishToFile.poll_delay", Parameter.REQUIRED,
             new Integer(5));

        /**
         * Time to wait (seconds) before retrying to process a failed entry
         */
        m_retryDelay = new IntegerParameter
            ("com.arsdigita.cms.publishToFile.retry_delay", Parameter.REQUIRED,
             new Integer(120));

        /**
         * Time to wait (seconds) before aborting item request.
         */
        m_requestTimeout = new IntegerParameter
            ("com.arsdigita.cms.publishToFile.request_timeout", Parameter.REQUIRED,
             new Integer(120));

        /**
         * Number of queue entries to process at once (in one transaction).
         */
        m_blockSize = new IntegerParameter
            ("com.arsdigita.cms.publishToFile.block_size", Parameter.REQUIRED,
             new Integer(40));

        /**
         * Number of times a failed queue entry will be reprocessed.
         * Maximum Fail Count for actions in Queue Manager.
         * If Fail Count in database is more than specified limit, Queue Manager
         * will ignore the action.
         * A value -1 will ignore this parameter.
         */
        m_maximumFailCount = new IntegerParameter
            ("com.arsdigita.cms.publishToFile.maximum_fail_count", Parameter.REQUIRED,
             new Integer(10));

        /**
         * Method used to select entries for processing.
         * 'QueuedOrder'  - in  queued order.
         * 'GroupByParent'- group entries according to parent when selecting items
         *                  (allows optimizations if a listener task required for
         *                  all elements in a folder can be done only once for the
         *                  folder).
        */
        m_blockSelectMethod = new StringParameter
            ("com.arsdigita.cms.publishToFile.block_select_method", Parameter.REQUIRED,
             "GroupByParent");


        register(m_destination);
        register(m_publishListener);
        register(m_startupDelay);
        register(m_pollDelay);
        register(m_retryDelay);
        register(m_requestTimeout);
        register(m_blockSize);
        register(m_maximumFailCount);
        register(m_blockSelectMethod);
        
        loadInfo();
    }


    /**
     * Retrieve the class which implements PublishToFileListener used to perform
     * additional actions when publishing or unpublishing to the file system.
     *
     * @return PublishToFileListener implementation class
     */
    public Class getpublishListenerClass() {
        return (Class) get(m_publishListener);
    }

    /**
     * Retrieve request manager's delay in seconds.
     * @return  delay, in seconds. 
     */
    public int getStartupDelay() {
        s_log.debug("Retrieving m_startupDelay.");
        return ((Integer) get(m_startupDelay)).intValue();
    }

    /**
     * Retrieve time (in seconds) between checking if there are entries in the
     * publishToFile queue.
     *
     * A value <= 0 disables processing the queue on this server.
     * 
     * @return  time, in seconds
     */
    public int getPollDelay() {
        s_log.debug("Retrieving m_pollDelay.");
        return ((Integer) get(m_pollDelay)).intValue();
    }

    /**
     * Retrieve time to wait (seconds) before retrying to process a failed entry
     * @return  delay, in seconds.
     */
    public int getRetryDelay() {
        s_log.debug("Retrieving m_retryDelay.");
        return ((Integer) get(m_retryDelay)).intValue();
    }

    /**
     * Retrieve simple queue's period in seconds
     * @return  period, in seconds
     */
    public int getBlockSize() {
        s_log.debug("Retrieving m_blockSize.");
        return ((Integer) get(m_blockSize)).intValue();
    }

    /**
     * Retrieve digest queue's delay in seconds.
     * @return  delay, in seconds.
     */
    public int getMaximumFailCount() {
        s_log.debug("Retrieving m_maximumFailCount.");
        return ((Integer) get(m_maximumFailCount)).intValue();
    }

    /**
     * Retrieve digest queue's period in seconds
     * @return  period, in seconds
     */
    public String getBlockSelectMethod() {
        s_log.debug("Retrieving m_blockSelectMethod.");
        return ( (String) get(m_blockSelectMethod));
    }

}
