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

import com.arsdigita.cms.Template;
import com.arsdigita.db.DbHelper;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectInstantiator;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.runtime.CCMResourceManager;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.ConfigError;
import com.arsdigita.runtime.ContextCloseEvent;
import com.arsdigita.runtime.ContextInitEvent;
// import com.arsdigita.runtime.DataInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.UncheckedWrapperException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * The CMS PublishT0File initializer.
 *
 * Initializer is invoked by the add-method in the CMS initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Peter Boy &lt;pb@zes.uni-bremen.de&gt;
 * @version $Id: Initializer.java 2140 2011-01-16 12:04:20Z pboy $
 */
public class Initializer extends CompoundInitializer {

    /** Creates a s_logging category with name = to the full name of class */
    private static Logger s_log = Logger.getLogger(Initializer.class);

    private static PublishToFileConfig s_conf= PublishToFileConfig.getConfig();

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

    }

//  /**
//   * An empty implementation of {@link Initializer#init(DataInitEvent)}.
//   *
//   * @param evt The data init event.
//   */
//  public void init(DataInitEvent evt) {
//  }

    /**
     * Initializes domain-coupling machinery, usually consisting of
     * registering object instantiators and observers.
     *
     */
    @Override
    public void init(DomainInitEvent e) {
        s_log.debug("publishToFile.Initializer.init(DomainInitEvent) invoked");

        // Recursive invokation of init, is it really necessary??
        // On the other hand:
        // An empty implementations prevents this initializer from being executed.
        // A missing implementations causes the super class method to be executed,
        // which invokes the above added LegacyInitializer.
        // If super is not invoked, various other cms sub-initializer may not run.
        super.init(e);

/*      From old Initializer system
 *         DomainObjectInstantiator inst = new DomainObjectInstantiator() {
 *              public DomainObject doNewInstance(DataObject dobj) {
 *                  return new PublishedFile(dobj);
 *              }
 *          };
 *      DomainObjectFactory.registerInstantiator(
 *          PublishedFile.BASE_DATA_OBJECT_TYPE,
 *          inst);
 */
        e.getFactory().registerInstantiator
            (PublishedFile.BASE_DATA_OBJECT_TYPE,
             new DomainObjectInstantiator() {
                 public DomainObject doNewInstance(DataObject dataObject) {
                     return new PublishedFile(dataObject);
                 }
                @Override
                 public DomainObjectInstantiator
                     resolveInstantiator(DataObject obj) {
                     return this;
                 }
             });

        // Not really handling domain registration but preparing the domain
        processDestination((ArrayList) s_conf.getPublishDestinations());

    }


    /**
     * Implementation of the {@link Initializer#init(ContextInitEvent)} method
     * used to setup and start background threads which synchronize the content
     * stored in the database (all modifications made using the CMS module are
     * persisted in database) with the file system.
     *
     * @param evt The context init event.
     **/
    @Override
    public void init(ContextInitEvent evt) {
        s_log.debug("publishToFile.Initializer.init(ContextInitEvent) invoked");

        // Recursive invokation of init, is it really necessary??
        // On the other hand:
        // An empty implementations prevents this initializer from being executed.
        // A missing implementations causes the super class method to be executed,
        // which invokes the above added LegacyInitializer.
        // If super is not invoked, various other cms sub-initializer may not run.
        super.init(evt);

        PublishToFile.setRequestTimeout(s_conf.getRequestTimeout());
        QueueManager.setRetryDelay(s_conf.getRetryDelay());
        QueueManager.setBlockSize(s_conf.getBlockSize());
        QueueManager.setBlockSelectMethod(s_conf.getBlockSelectMethod());
        QueueManager.setMaximumFailCount(s_conf.getMaximumFailCount());


        // Set the class implementing methods run when for publishing
        // or unpublishing to file. 
        try {
            QueueManager.setListener((PublishToFileListener)
                                     s_conf.getPublishListenerClass()
                                     .newInstance());
        } catch (InstantiationException ex) {
            throw new UncheckedWrapperException
                ("Failed to instantiate the listener class", ex);
        } catch (IllegalAccessException ex) {
            throw new UncheckedWrapperException
                ("Couldn't access the listener class", ex);
        }


        // start thread for monitoring queue
        // int startupDelay = s_conf.getStartupDelay();
        // int pollDelay = s_conf.getPollDelay();
        QueueManager.startWatchingQueue(s_conf.getStartupDelay(),
                                        s_conf.getPollDelay());

        QueueManager.requeueMissingFiles();


        s_log.debug("publishToFile.Initializer.init(ContextInitEvent) completed");
    }

    
    /**
     * Implementation of the {@link Initializer#close()} method.
     *
     * This implementation proceeds through the list of sub
     * initializers in order and invokes the close()
     * method of each sub initializer in turn.
     *
     * @param evt The legacy init event.
     **/
    @Override
    public void close(ContextCloseEvent evt) {
        s_log.debug("publishToFile.Initializer.destroy() invoked");

        QueueManager.stopWatchingQueue();

        s_log.debug("publishToFile.Initializer.destroy() completed");
    }


    /**
     * Process publishing destinations.
     * @param dest
     * @throws ConfigError
     */
    private static void processDestination(ArrayList dest)
        throws ConfigError {
        if (dest == null) {
            throw new ConfigError("publish destinations must not be null");
        }
        if (dest.size() < 1) {
            throw new ConfigError("publish destinations must contain at " +
                                  "least one entry");
        }

        Iterator entries = dest.iterator();
        while (entries.hasNext()) {
            processDestinationEntry((List)entries.next());
        }
    }

    /**
     * Processes one entry of the list of destinations.
     * Helper method for {@see #processDestination}.
     * 
     * @param entry
     * @throws ConfigError
     */
    private static void processDestinationEntry(List entry)
        throws ConfigError {

        if ( entry.size() != 4 ) {
            throw new ConfigError("publish destinations entry must contain " +
                                  "four elements: '{ \n" +
                                  "  \"content type\",\n" +
                                  "  \"root directory\", \n" +
                                  "  \"is shared\", \n" +
                                  "  \"url stub\" \n" +
                                  "};\n");
        }

        String contentType = (String)entry.get(0);
        // destRoot is here relative to webapp root!
        String destRoot = (String) entry.get(1);
        Boolean sharedRoot = (Boolean) entry.get(2);
        String destURL = (String) entry.get(3);

        if ( contentType == null || contentType.trim().length() == 0) {
            throw new ConfigError("The destination content type must not be null");
        }

        ObjectType type = MetadataRoot.getMetadataRoot().getObjectType(contentType);
        if (type == null) {
            throw new ConfigError("The destination content type cannot be found");
        }

        if (destRoot == null || destRoot.trim().length() == 0) {
            throw new ConfigError("The destination root must not be null");
        }
        if (destRoot.endsWith("/")) {
            throw new ConfigError("the destination root '" + destRoot +
                                  "' must not end with a '/'");
        }

        // Does destRoot really now turns into an absolute fully pathname
        destRoot = new File(CCMResourceManager.getBaseDirectory().getPath(),
                            destRoot).getPath();
        s_log.info("Destination Root is set to : " + destRoot);



        if (sharedRoot == null) {
            throw new ConfigError("The destination shared flag must not be null");
        }


        if (destURL == null || "".equals(destURL.trim())) {
            throw new ConfigError("the destination URL must not be null");
        }
        if (!destURL.startsWith("/")) {
            throw new ConfigError("the destination URL '" + destURL +
                                  "' must start with a '/'");
        }
        if (destURL.endsWith("/")) {
            throw new ConfigError("the destination URL '" + destURL +
                                  "' must not end with a '/'");
        }

        DestinationStub dest = new DestinationStub(destRoot,
                                                   sharedRoot.booleanValue(),
                                                   destURL);

        File file = dest.getFile();
        if (!file.exists()) {
            file.mkdirs();
            s_log.info(file.getPath() + " created");
        }
        boolean writable = false;
        FileWriter fl;
        File fname = new File(file.getPath(),"placeholder.txt");
        s_log.info("Try to create : " + destRoot);
        try {
            writable = file.canWrite() && file.isDirectory();
            try {
                fl = new FileWriter(fname.getPath());
                fl.write("Location for the p2fs module to store static content. \n");
                fl.close();
            } catch ( IOException e ) {
                // Will be reported as an initalization error
                s_log.warn("Error creating file " + fname.getPath());
            }


        } catch ( SecurityException ex ) {
            // Will be reported as an initalization error
        }
        if ( ! writable ) {
            // HACK: Let's see if we can write to the config directory.  If we can,
            // then we're running as ccmadmin inside of ccm load, and there is no
            // need to thrown an exception.
            File conf = CCMResourceManager.getConfigDirectory();
            if (conf.isDirectory() && conf.canWrite()) {
                // we're ok
            } else {
                throw new ConfigError(" the document root '" + file
                                     +"' must be a writable directory");
            }
        }

        if (Template.BASE_DATA_OBJECT_TYPE.equals(contentType) ||
            !s_conf.isItemPfsDisabled()) {
            PublishToFile.addDestination(contentType,
                                         dest);
        }
    }


}
