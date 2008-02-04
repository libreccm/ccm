package com.arsdigita.profiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple Profiler.
 * Collects timing statistic per thread, dumping them in a log after request processing finished.
 * Call Profiler.startRequest() to clear timers and Profiler.stopRequest() to log the timers.
 * For each operation call Profiler.startOp("operation") at the beginning of the operation and Profiler.stopOp("operation)
 * at the end.
 * Operations can be nested, e.g. under operation XML we can have an operation DB, timer name in the output
 * is then XML-DB for all DB operations under XML.  
 * 
 * @author Alan Pevec
 */
public class Profiler {
    
    private static final Set enabledOperations = new HashSet();
    // TODO add a configuration page under Developer Support
    static {
        enabledOperations.add("APP");
        enabledOperations.add("FILT");
        enabledOperations.add("CMS");
        enabledOperations.add("XML");
        enabledOperations.add("DB");
        enabledOperations.add("XSLT");
    }
    
    private static ProfilerConfig config = null;
    private static ProfilerConfig getConfig() {
        if (config == null) {
            config = new ProfilerConfig();
            config.load();
        }
        return config;
    }
    
    /* cumulative timers with nested operation names as keys */
    private static ThreadLocal timers = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new HashMap();
        }
    };

    /* stack of operation start timestamp */
    private static ThreadLocal timestamps = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new ArrayList();
        }
    };

    /**
     * Start the stopwatch for an operation.
     * @param op name of the operation e.g. XML, DB, XSLT ...
     * @pre op != null, op =~ ^[A-Z]+$
     */
    public static void startOp(String op) {
        if ( getConfig().isEnabled() && enabledOperations.contains(op)) {
            long timestampBegin = getCurrentTimestamp();
            List ts = (List) timestamps.get();
            ts.add( new OperationTimestamp(op, timestampBegin) );
        }
    }
    
    /**
     * Stop the stopwatch for an operation.
     * The time lapsed between start and stop for an operation is added to the timer
     * which name is constructed from the parent operation names.
     * For example, CMS-XML-DB for an DB operations inside of XML generation in CMS.
     * @param op name of the operation e.g. XML, DB, XSLT ...
     * @pre op != null, op =~ ^[A-Z]+$
     */
    public static void stopOp(String op) {
        if (getConfig().isEnabled() && enabledOperations.contains(op)) {
            long timestampEnd = getCurrentTimestamp();
            List ts = (List) timestamps.get();
            StringBuffer keyBuffer = new StringBuffer();
            OperationTimestamp prevOp = null;
            for (Iterator i = ts.iterator(); i.hasNext();) {
                OperationTimestamp ots = (OperationTimestamp) i.next();
                if (prevOp == null || !ots.getName().equals(prevOp.getName())) {
                    if (keyBuffer.length() > 0) {
                        keyBuffer.append('-');
                    }
                    keyBuffer.append(ots.getName());
                }
                prevOp = ots;
            }
            String key = keyBuffer.toString();
            if (prevOp == null) {
                log("WARNING: stop for " + op + " without start, at timer key "
                        + key);
                // XXX add stack trace for debugging
            } else {
                if (!op.equals(prevOp.getName())) {
                    log("WARNING: unmatched stop for " + op
                            + ", previous start was " + prevOp.getName());
                } else {
                    ts.remove(ts.size() - 1);
                    Map t = (Map) timers.get();
                    long timestampBegin = prevOp.getTimestamp();
                    Long timer = (Long) t.get(key);
                    if (timer == null) {
                        t.put(key, new Long(timestampEnd - timestampBegin));
                    } else {
                        t.put(key, new Long(timer.longValue() + timestampEnd
                                - timestampBegin));
                    }
                }
            }
        }
    }
    
    /**
     * Reset timers at the beginning of the request processing.
     *
     */
    public static void startRequest() {
        if (getConfig().isEnabled()) {
            Map t = (Map) timers.get();
            List ts = (List) timestamps.get();
            t.clear();
            ts.clear();
        }
    }
    
    
    /**
     * Log the timers at the end of the request processing.
     *
     * @param requestInfo request identifier, e.g. path info or unique cookie 
     * 
     */
    public static void stopRequest(String requestInfo, long totalTime) {
        if (getConfig().isEnabled()) {
            Map t = (Map) timers.get();
            if (t.isEmpty()) {
                // request without profiling info
                return;
            }
            List ts = (List) timestamps.get();
            if ( !ts.isEmpty() ) {
                log("WARNING: following operations are still opened at the end of the request: "+ts);
            }
            StringBuffer msg = new StringBuffer();
            // log timestamp in ms for performance reasons, formatted later by the analysing script
            msg.append(getCurrentTimestamp())
            .append(" profile for: ")
            .append(requestInfo)
            .append(" TOTAL=")
            .append(totalTime);
            for(Iterator i=t.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry e = (Map.Entry) i.next();
                msg.append(' ').append(e.getKey()).append('=').append(e.getValue());
            }
            log(msg.toString());
            t.clear();
            ts.clear();
        }
    }
    
    private static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
    
    private static void log(String msg) {
        // direct stderr to avoid log4j overhead ( -verbose:gc goes to stderr as well)
        System.err.println(msg);
    }

    private static class OperationTimestamp {
        String op;
        long ts;
        
        public OperationTimestamp(String op, long ts) {
            this.op = op;
            this.ts = ts;
        }
        
        public String getName() {
            return this.op;
        }
        
        public long getTimestamp() {
            return this.ts;
        }

        public String toString() {
            return this.op + " at "+this.ts;
        }
    }
}
