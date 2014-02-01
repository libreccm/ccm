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
package com.arsdigita.notification;

import java.util.Date;
import java.util.TimerTask;

/**
 * This class serves as a container for a TimerTask that can be
 * dispatched at any time. After creation, the first run is invoked
 * with start(), thereafter with restart().
 *
 * The TimerTasks to be tested in the context of this package are
 * RequestManager, DigestQueueManager, SimpleQueueManger. Calling
 * the respective run method once, assumes that the Manager does
 * its job once and then exits. The ManagerDispatcher counts the
 * cycles of execution and waits until the next run.
 *
 * Note, that this class represents a reusable Thread without using
 * the deprecated suspend/resume methods. The difference to java.
 * util.Timer is that execution is triggered explicitly rather than
 * scheduled periodically.
 *
 * @author Stefan Deusch
 * @version $Id: ManagerDispatcher.java 1940 2009-05-29 07:15:05Z terry $
 *
 */

public class ManagerDispatcher extends Thread {


    TimerTask mgr;
    private boolean suspendRequested;
    private int cycle;
    private Date start;


    /**
     * construct a ManagerDispatcher for a TimerTask
     */
    public ManagerDispatcher(TimerTask m) {
        this.mgr = m;
        start = new Date();
    }

    /*
     * suspend thread
     */
    public void requestSuspend() {
        suspendRequested = true;
    }

    private synchronized void checkSuspend() throws InterruptedException {
        while(suspendRequested)
            wait();
    }


    /**
     * re-run thread for one cycle
     */
    public synchronized void restart() {
        suspendRequested = false;
        notify();                // order of calling is relevant here!
    }


    /**
     * run method executes one cycle of the assoociated TimerTask
     */
    public synchronized void run() {
        while(true) {
            try {
                checkSuspend();
                cycle++;
                mgr.run();
                requestSuspend();
            } catch(InterruptedException e) { }
        }
    }

    /**
     * wait for the currently running cycle to finish; needed to synchronize
     * with calling stack for tests to have up-to-date results of TimerTask.
     */
    public void waitCycle(){
        int maxwait = 10;
        float wait = 0;
        try {
            while(!suspendRequested && wait<=maxwait) {
                Thread.sleep(200);
                wait += 0.2;
            }
        } catch(InterruptedException e){}
    }

    /**
     * serves public information on since when this thread is up, and how many
     * cycles it cranked through the associated TimerTask
     */
    public String getInfo() {
        float upmin = (float)((new Date().getTime() - start.getTime())/60000.0);
        String info = "Manager Dispatcher for "+mgr.getClass().getName()+"\n"+
                          "creation date    : "+start+"\n"+
                          "uptime [min]     : "+upmin+"\n"+
                          "cycles completed : "+cycle+"\n";

        return info;
    }

    /**
     * @return how many times this Thread has been invoked
     */
    public int getCycles() {
        return cycle;
    }

}
