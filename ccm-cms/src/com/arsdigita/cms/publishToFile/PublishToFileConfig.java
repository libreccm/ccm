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

import com.arsdigita.cms.CMS;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.SpecificClassParameter;
import com.arsdigita.util.parameter.StringParameter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Configuration object for the publish-to-file service.
 *
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: $
 */
public class PublishToFileConfig extends AbstractConfig {

    /** Private Logger instance for debugging purpose.                        */
    private static final Logger s_log = Logger.getLogger(PublishToFileConfig.class);

    /** Private static instance of this class - singelton design pattern      */
    private static PublishToFileConfig s_conf;

    
    /**
     * Disable publish-to-filesystem for content items (not for templates!).
     * 
     * NOTE: Templates are stored in the database and must be synchronized with
     * the filesystem in order to be used. Therefore publish-to filesystem must
     * not deactivated at all.
     */
    private final Parameter 
            m_disableItemPfs = new BooleanParameter(
                             "com.arsdigita.cms.ptf.disable_item_pfs",
                             Parameter.REQUIRED, 
                             new Boolean(true));


    /**
     * Temporary, constant list of publish destinations. It is intended as a
     * temporary fix until we have a configurable pramameter
     * {@see #m_destinations} and {@see #getPublishDestinations()}.
     * Should be replaced and removed then.
     */
    private static ArrayList s_tmpDestList;

    /**
     * List of publish destinations for content items.
     * Each list element is itself a four-element list in the format
     * { "content type", "root directory", "shared storage", "url stub" }.
     *
     * Content type   is the object type of the content type.
     * Root directory must be a path to a writable directory, relative to 
     *                webapp root(!).
     * Shared storage must be true if the root directory is shared NFS storage,
     *                false otherwise.
     * URL stub       is the path component of the URL from which the live
     *                server will serve from this directory.
     */
     // ToDO: Parameter destination is a list, but we have no ListParameter type
     // for now. We have either to develop a ListParameter class or treat this
     // parameter as a set of StringArrayParameter.
     // Another possibility considered was MapParameter
     // m_destinations is unfinished work! Not usable!
     // Parameter is a list, but we have currently no ListParameter type.
     // Others have considered to develop a MapParameter for it.
    private final Parameter m_destinations = new StringParameter
            ("com.arsdigita.cms.ptf.destination", Parameter.REQUIRED,
             "{ " +
             "  { 'com.arsdigita.cms.ContentItem',  " +
             "    'p2fs', " +
             "    false,  " +
             "    '/p2fs' }, " +
             "  { 'com.arsdigita.cms.Template',   " +
             "   '/templates/ccm-cms/content-section',  " +
             "   false,   " +
             "   '/templates' }  " +
             "} "
             );

    /**
     * Class implementing PublishToFileListener Interface.
     *
     * Contains the initial methods called when publishing or unpublishing. It
     * is usually be set to com.arsdigita.cms.publishToFile.PublishToFile,
     * but custom classes can also be used. That can be used to perform
     * additional actions when publishing or unpublishing to the file system.
     */
     // Originally (old initializer system):
     // publishListener = "com.arsdigita.cms.publishToFile.PublishToFile";
    private final Parameter 
            m_ListenerClass = new SpecificClassParameter(
                                 "com.arsdigita.cms.ptf.listener_class",
                                 Parameter.REQUIRED,
                                 PublishToFile.class,
                                 PublishToFileListener.class);

    /**
     * Time (in seconds)  after system startup to wait before starting to monitor
     * publishToFile queue.
     *
     * Set startupDelay to 0 (< 0) to disable the processing of the queue. This
     * disables the processing of templates as well so it will affect a basic
     * functionality of the system and is not recommended under normal
     * conditions.
     */
    private final Parameter 
            m_startupDelay = new IntegerParameter(
                             "com.arsdigita.cms.ptf.startup_delay", 
                             Parameter.REQUIRED,
                             new Integer(30) );


    /**
     * Time (in seconds) between checking whether there are entries in the
     * publishToFile queue.
     *
     * A value <= 0 disables processing the queue on this server.
     */
    private final Parameter
            m_pollDelay = new IntegerParameter(
                          "com.arsdigita.cms.ptf.poll_delay", 
                          Parameter.REQUIRED,
                          new Integer(5) );


    /**
     * Time to wait (seconds) before retrying to process a failed entry.
     */
    private final Parameter 
            m_retryDelay = new IntegerParameter(
                           "com.arsdigita.cms.ptf.retry_delay", 
                           Parameter.REQUIRED,
                           new Integer(120));


    /**
     * Time to wait (seconds) before aborting item request.
     */
    private final Parameter 
            m_requestTimeout = new IntegerParameter(
                               "com.arsdigita.cms.ptf.request_timeout", 
                               Parameter.REQUIRED,
                               new Integer(60) );


    /**
     * Number of queue entries to process at once.
     */
    private final Parameter 
            m_blockSize = new IntegerParameter(
                          "com.arsdigita.cms.ptf.block_size", 
                          Parameter.REQUIRED,
                          new Integer(40) );

    
    /**
     * Method used to select entries for processing.
     * 'QueuedOrder'  - in  queued order.
     * 'GroupByParent'- group entries according to parent when selecting items
     *                  (allows optimizations if a listener task required for
     *                  all elements in a folder can be done only once for the
     *                  folder).
     */
    private final Parameter 
            m_blockSelectMethod = new StringParameter(
                            "com.arsdigita.cms.ptf.block_select_method", 
                            Parameter.REQUIRED,
                            "GroupByParent");


    /**
     * Number of times a failed queue entry will be reprocessed.
     * Maximum Fail Count for actions in Queue Manager.
     * If Fail Count in database is more than specified limit, Queue Manager
     * will ignore the action.
     * The default value -1 will ignore this parameter.
     */
    private final Parameter 
            m_maximumFailCount = new IntegerParameter(
                            "com.arsdigita.cms.ptf.maximum_fail_count", 
                            Parameter.REQUIRED,
                            new Integer(10) );


    /**
     * Get a PublishToFileConfig instance.
     * 
     * Singelton pattern, don't instantiate a notificationConfig object using
     * the constructor directly.
     * @return
     */
    public static synchronized PublishToFileConfig getConfig() {
        if (s_conf == null) {
            s_conf = new PublishToFileConfig();
            s_conf.load();
        }

        return s_conf;
    }

    /**
     * Constructor.
     *
     * Do not use it directly!  Use PublishToFileConfig.getConfig() instead
     * (singelton design pattern!)
     */
    public PublishToFileConfig() {

        // Register parameters

        register(m_disableItemPfs);
        // register(m_destinations);   Unfinished; currently not usable
        register(m_ListenerClass);
        register(m_startupDelay);
        register(m_pollDelay);
        register(m_retryDelay);
        register(m_requestTimeout);
        register(m_blockSize);
        register(m_blockSelectMethod);
        register(m_maximumFailCount);
        
        loadInfo();
    }


    /**
     * Retrieve whether or not content items should be statically published
     * (i.e. exported) into the filesystem.
     * (Templates are always exported independently from this parameter because
     * otherwise they are not accessible by the servlet container!)
     *
     * @return (boolean) m_disableItemPfs Parameter
     */
    public final boolean isItemPfsDisabled() {
        return ((Boolean) get(m_disableItemPfs)).booleanValue();
    }

    /**
     * Retrieve list of publish destinations for content types. Each
     * list element is itself a four-element list in the format
     * { "content type", "root directory", "shared storage", "url stub" }.
     *
     * Content type   is the object type of the content type.
     * Root directory must be a path to a writable directory, relative to the
     *                webapp root.
     * Shared storage must be true if the root directory is shared NFS storage,
     *                false otherwise.
     * URL stub       is the path component of the URL from which the live
     *                server will serve from this directory.
     *
     * By default the object type of the last element is template. P2FS is not
     * only used to store content items in the file system but to watch templates
     * stored in the database (by parameter or Web GUI) and synchronize them
     * with the file system so that the servlet container is able to use them.
     */
    public List getPublishDestinations() {
        // Comment by pboy (Febr. 2010)
        // In the old Initializer system this parameter was burried in the
        // enterprise.init file which used to be part of the jar file. Therefore,
        // it was practically not configurable by users, just by developers only
        // (who can recompile and create a jar file).
        // P2fs for content items is rarely used, or even not at all. Location of
        // templates is fixed for any installation. So we return a constant List
        // here. Developers may modify it as needed.
        // If someone needs p2fs for content items we have to create a List type
        // parameter and make it configurable.

        // Quick 'n dirty but works
        s_tmpDestList  =  new ArrayList();
        s_tmpDestList.add
            ( new ArrayList() {{ add("com.arsdigita.cms.ContentItem");
                                 add("html");
                                 add(new Boolean(false));
                                 add("/html");
                              }}
            );
        s_tmpDestList.add
            ( new ArrayList() {{
                                 add("com.arsdigita.cms.Template");
                                 add( CMS.getConfig().getTemplateRoot() );
                                 add(new Boolean(false));
                                 add("/templates");
                              }}
            );

        s_log.debug("s_tmpDestList is " + s_tmpDestList.size() );
        return s_tmpDestList;
    }

    /**
     * Retrieve the class which implements PublishToFileListener used to perform
     * additional actions when publishing or unpublishing to the file system.
     *
     * @return PublishToFileListener implementation class
     */
    public Class getPublishListenerClass() {
        return (Class) get(m_ListenerClass);
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
     * Retrieve time to wait (seconds) before aborting an item request.
     *
     * @return  timeout, in seconds.
     */
    public int getRequestTimeout() {
        s_log.debug("Retrieving m_requestTimeout.");
        return ((Integer) get(m_requestTimeout)).intValue();
    }

    /**
     * Retrieve number of queue entries to process at once.
     * 
     * @return  number of blocks.
     */
    public int getBlockSize() {
        s_log.debug("Retrieving m_blockSize.");
        return ((Integer) get(m_blockSize)).intValue();
    }

    /**
     * Retrieve method used to select entries for processing.
     *
     * @return  'QueuedOrder' or 'GroupByParent' (default)
     */
    public String getBlockSelectMethod() {
        s_log.debug("Retrieving m_blockSelectMethod.");
        return ( (String) get(m_blockSelectMethod));
    }

    /**
     * Retrieve number of times a failed queue entry will be reprocessed. If
     * Fail Count in database is more than specified limit, Queue Manager
     * will ignore the action.
     *
     * @return  number of times
     */
    public int getMaximumFailCount() {
        s_log.debug("Retrieving m_maximumFailCount.");
        return ((Integer) get(m_maximumFailCount)).intValue();
    }

}
