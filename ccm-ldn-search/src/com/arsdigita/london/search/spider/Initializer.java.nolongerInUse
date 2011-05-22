package com.arsdigita.london.search.spider;

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.kernel.URLService;
import com.arsdigita.runtime.CompoundInitializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Initializer for the "spidered search" package.
 * This initializer supports the following parameters:<dl>
 * <dt>urls</dt><dd>list of URLs of sites that should be spidered for searchable
 *  content</dd>
 * <dt>maxDepth</dt><dd>maximum of depth for spidering, i.e. how many levels of
 *  links the spider will follow. <em>Default:</em> 2</dd>
 * <dt>frequency</dt><dd>Frequency of spider runs, expressed as the number of
 *  <em>seconds</em> between two consecutive runs of the
    spider. Default value: 2 hours == 7200s </dd>
 * <dt>delay</dt> <dd>delay between the server startup and the first run of the 
 *  spider <em>in seconds</em>. Default value: 10 minutes == 600 s</dd>
 * </dl>
 * <p>To disable the spider, it is recommended to leave the list of URLs empty,
 *   or to set <code>frequency = 0</code>. Simply removing the spider
 *    from <tt>enterprise.init</tt> is <em>not</em> recommended since this spider
 *  also registers a <tt>DomainObjectInstantiator</tt> and <tt>URLFinder</tt> for this
 * type; if the initializer is removed, there may be problems with retrieving (URLs of)
 * spidered content from the database!
 *
 *@author <a href="mailto:mhanisch@redhat.com">Michael Hanisch</a>
 *@version $Id: Initializer.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class Initializer extends CompoundInitializer {

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(Initializer.class.getName());


    Configuration m_config;
    // constants for parameter names
    private static final String DELAY = "delay";
    private static final String FREQUENCY = "frequency";
    private static final String MAX_DEPTH = "maxDepth";
    private static final String URLS = "urls";

    public Initializer() throws InitializationException {
        m_config.initParameter(MAX_DEPTH, 
                               "The maximum depth the spider will go to",
                               Integer.class,
                               new Integer(2));

        m_config.initParameter(FREQUENCY,
                               "The frequency of spider runs. Please specify"
                               + " the number of seconds for the spider to wait "
                               + " between two runs",
                               Integer.class,
                               new Integer(7200));

        m_config.initParameter(DELAY,
                               "The time between the server startup and the "
                               + "first run of the spider, specified in seconds.",
                               Integer.class,
                               new Integer(600));

        m_config.initParameter(URLS,
                               "List of URLs of sites that should be spidered "
                               + "for searchable content.",
                               java.util.List.class, new ArrayList());

        // TO DO: extend Configuration to allow for error checking

        // register URLFinder for SpideredContent
        // NOTE: There should be NO oldFinder; report an error if something is found!
        URLFinder oldFinder = 
            URLService.registerFinder( SpideredContent.BASE_DATA_OBJECT_TYPE,
                                       new SpideredContentURLFinder() );
        if (oldFinder != null) {
            s_log.error("another initializer registered a URLFinder for "
                        + SpideredContent.BASE_DATA_OBJECT_TYPE 
                        + ": " + oldFinder);
        }
        else {
            s_log.debug("registered URLFinder for " 
                        + SpideredContent.BASE_DATA_OBJECT_TYPE);
        }
    }


    public Configuration getConfiguration() {
        return m_config;
    }

    /**
     * Shutdown all the spidering threads.
     * <p><b>NOTE:</b> This max take a while!</p>
     */
    public void shutdown() {
        Scheduler.stopTimer();
    }

    public void startup() {
        Integer delay = (Integer) m_config.getParameter(DELAY);
        Integer frequency = (Integer) m_config.getParameter(FREQUENCY);
        
        Integer maxDepth = (Integer) m_config.getParameter(MAX_DEPTH);
        List urls = (List) m_config.getParameter(URLS);

        if (urls == null || urls.size() == 0) {
            // no URLs ==> no spider
            s_log.warn("No URLs specified! Spider DISABLED.");
            return;
        }
        else if (frequency == null || frequency.intValue() == 0) {
            s_log.warn("Frequency set to 0! Spider DISABLED.");
            return;
        }
        else {
            if (s_log.isInfoEnabled()) {
                s_log.info("spider running with the following settings: "
                           + "delay=" + delay 
                           + "s, number of seconds between runs=" + frequency
                           + ", max. depth= "+ maxDepth + " levels");
                Iterator it = urls.iterator();
                s_log.info("The following sites will be retrieved and indexed: ");
                while (it.hasNext()) {
                    s_log.info("url: " + (String)it.next());
                }
            }

            // Timer operates on milleseconds!
            Scheduler.setDelay(delay.longValue() * 1000L);
            Scheduler.setFrequency(frequency.longValue() * 1000L);
            Scheduler.setMaxDepth(maxDepth.intValue());
            Scheduler.setURLs(urls);
            
            Scheduler.startTimer();
        }
    }

}
