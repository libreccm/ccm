/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.forum;


import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.apache.log4j.Logger;


import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * Scheduler thread to remove any files or images that are more than a day old
 * and haven't been attached to a post.
 * These posts arise if a user starts to create a post and closes their browser 
 * before completing the post. This
 * process gets rid of them on a daily basis to keep them under control
 * 
 * nb - We can't just delete all unattached assets, as some may be there because the user is 
 * halfway through making a new post
 *
 * @author Chris Gilbert(chris.gilbert@westsussex.gov.uk)
  * @version $Revision: 1.1 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class RemoveUnattachedAssetsScheduler {

    public static final String versionId = "$Id: RemoveUnattachedAssetsScheduler.java,v 1.1 2006/07/13 10:19:28 cgyg9330 Exp $ by $Author: cgyg9330 $, $DateTime: 2004/08/17 23:15:09 $";

    // For storing timer which runs a method periodically to fire
    // begin and end events
    private static Timer s_Timer;
    
    // if process runs very very slowly, prevent duplicate attempt fro mrunning before the first one ends
    private static boolean s_running = false;

    private static Logger s_log =
        Logger.getLogger( RemoveUnattachedAssetsScheduler.class);

    
    /**
     * startTimer - starts the timer
     */
    public static synchronized void startTimer() {
        if ( s_Timer != null ) {
            return;        // Timer already exists
        }

        // Timer triggers straight away, and then every 24 hours
        // don't run timer as a daemon - if server stops, kill this process too, as no one will be creating new posts
        s_Timer = new Timer(false);
        
        s_Timer.scheduleAtFixedRate(new RemoveUnattachedAssetsTask(), 0, 1000 * 60 * 60 * 24);
    }


    /**
     * run - Run the task
     */
    public static synchronized void run() {
    	s_log.debug("Firing off scheduler");
        Session ssn = SessionManager.getSession();
        if ( !s_running ) {
            new KernelExcursion() {
                protected final void excurse() {
                    s_running = true;
                    try {
			setEffectiveParty(Kernel.getSystemParty());
			TransactionContext txn =SessionManager.getSession().getTransactionContext();
			txn.beginTxn();
                	    
                   	PostFileAttachment.removeUnattachedFiles();
                    	PostImageAttachment.removeUnattachedImages();
                    	txn.commitTxn();
                    } catch (Throwable t) {
			s_log.error("Attempt to remove unconfirmed forum posts failed", t);
                	throw new UncheckedWrapperException(t);
                		
                    } finally {
                	s_running = false;
                    }
                    
                }
            }.run();
        }
    }



  

}
