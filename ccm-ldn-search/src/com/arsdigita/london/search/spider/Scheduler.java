package com.arsdigita.london.search.spider;

import net.matuschek.http.cookie.MemoryCookieManager;
import net.matuschek.spider.WebRobot;
import net.matuschek.spider.WebRobotCallback;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Iterator;
import java.util.List;

/**
 * Scheduler for the execution of spidering tasks.
 *
 *@author <a href="mailto:mhanisch@redhat.com">Michael Hanisch</a>
 *@version $Id: Scheduler.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Scheduler {

    // Time in milliseconds
    private static long s_timerDelay;
    private static long s_timerFrequency;
    
    private static int s_maxDepth;
    private static List s_URLList;

    private static Timer s_timer;
    private static boolean s_running = false;

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(Scheduler.class.getName());

    public static void setDelay(long delay) {
        s_timerDelay = delay;
    }

    public static void setFrequency(long freq) {
        s_timerFrequency = freq;
    }

    public static void setMaxDepth(int maxDepth) {
        s_maxDepth = maxDepth;
    }

    public static void setURLs(List URLList) {
        s_URLList = URLList;
    }

    public static List getURLs() {
        return s_URLList;
    }

    public static synchronized void startTimer() {
        if (s_timer != null) {
            return;             // Timer already exists
        }
        else if (getURLs() == null || getURLs().size() == 0) {
            // No URLs, no spider.
            s_log.info("no URLs specified, spider disabled");
            return;
        }
        
        s_timer = new Timer(true); // start timer as daemon thread
        if (s_log.isInfoEnabled()) {
            s_log.info("Starting timer with delay= " + s_timerDelay + ", frequency= " + s_timerFrequency
                       + ", maxDepth= " + s_maxDepth);
        }
        s_timer.schedule(new Scheduler.SpiderTask(),
                         s_timerDelay,
                         s_timerFrequency);
    }

    public static synchronized void stopTimer() {
        if (s_timer == null) {
            return;             // no Timer (?)
        }

        s_log.info("Stopping timer thread... this might take some time as the "
                   + "spider finishes its current job.");
        
        /* TO DO:
         * Stopping the spider thread gracefully might cause some delays
         * since the spider's threads might take some time, e.g. when
         * downloading a large document.
         * Think of a more clever way of handling this, so the server
         * can shut down immediately.
         */
        s_timer.cancel();
        /* TO DO: we really should stop the timer thread, otherwise it might
         * continue for ages! (cf. net.matuschek.spider.WebRobot )
         * Problem: How can we access the currently running thread?
         */
        s_log.info("Timer thread STOPPED");
    }

 
    private static class SpiderTask extends TimerTask {
        
        protected WebRobot initializeRobot() {
            WebRobot robby = new WebRobot();
            // use our own flavor of HttpDocManager, which stores
            // the content in the DB (but doesn't do caching)
            robby.setDocManager(new DocumentManager());
            robby.setAllowCaching(false);
            
            robby.setWebRobotCallback(new Scheduler.SpiderLogger());

            // TO DO: should we use a home-grown TaskList as well,
            // which stores stuff in the DB?

            robby.setCookieManager(new MemoryCookieManager());

            robby.setFlexibleHostCheck(true);
            robby.setAllowWholeHost(true);
            robby.setAllowWholeDomain(false);
            robby.setIgnoreRobotsTxt(false);
            
            robby.setSleepTime(5); // TO DO: make this a parameter
            // TO DO: should we use a proxy?
            // TO DO: define which (MIME-)types should be allowed

            robby.setMaxDepth(s_maxDepth);
            // TO DO: limit bandwidth?

            return robby;
        }

        public void run() {
            Iterator it = getURLs().iterator();
            
            String url = null;
            WebRobot ccmbot = null; // ;-)

            while (it.hasNext()) {
                try {
                    URL startURL = new URL( (String)it.next() );
                    ccmbot = initializeRobot();
                    ccmbot.setStartURL(startURL);
                    if (s_log.isInfoEnabled()) {
                        s_log.info("starting spider for URL " + startURL);
                    }
                    ccmbot.work();
                }
                catch (MalformedURLException mue) {
                    s_log.error("malformed URL: " + url 
                                + "\n Please check your configuration!");
                }
            }
        }
    }

    /**
     * Nested class which implements the spider's callback interface
     * to log various events.
     * Not necessary, but nice to have when debugging
     */
    private static class SpiderLogger implements WebRobotCallback {
        public void webRobotRetrievedDoc(java.lang.String url,
                                         int size) {

            if (s_log.isInfoEnabled()) {
                s_log.info("spider: document retrieved: " + url 
                           + " ( " + size + ") bytes");
            }
        }

        public void webRobotUpdateQueueStatus(int length) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("spider: lenght of queue is now " + length);
            }
        }

        public void webRobotDone() {
            s_log.info("spider. DONE w/ task list");
        }

        public void webRobotSleeping(boolean sleeping) {
            s_log.debug("spider: is now sleeping");
        }
    }
}
