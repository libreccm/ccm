/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.search;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * 
 * 
 */
public class SearchJobQueue extends LinkedList {

    private static SearchJobQueue s_jobQueue = new SearchJobQueue();

    /**
     * 
     * @return 
     */
    public static SearchJobQueue getInstance() {
        return s_jobQueue;
    }
        
    /**
     * 
     */
    private SearchJobQueue() {}
    
    /**
     * 
     * @return 
     */
    public synchronized SearchJob getSearchJob() {

        SearchJob job = null;
        while (job == null) {
            try {
                // Try to get a job from the front of the queue
                job = (SearchJob) removeFirst();
            } catch( NoSuchElementException ex1 ) {
                try {
                    // If there isn't one, wait until there is
                    this.wait();
                } catch( InterruptedException ex2 ) { }
            }
        }
        
        return job;
    }
    
    /**
     * 
     * @param job 
     */
    public synchronized void addSearchJob( SearchJob job ) {
        addLast( job );
        
        // Wake up a thread waiting for a job
        notify();
    }
}
