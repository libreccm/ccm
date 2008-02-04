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
 */

package com.arsdigita.london.search;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;

import com.arsdigita.util.UncheckedWrapperException;

import org.apache.log4j.Logger;

public class RemoteSearcher extends Thread {
    private static final Logger s_log =
        Logger.getLogger( RemoteSearcher.class );

    private SearchJobQueue m_queue;
    private boolean m_stop = false;

    private static final String SERVICE_URL = "ccm-ldn-search/services/Search";

    public RemoteSearcher( SearchJobQueue queue ) {
        super();
        setDaemon(true);
        setName("remote-search");
        m_queue = queue;
    }

    public void run() {
        // Initialize the SOAP call
        Service service;
        Call call;
        
        try {
            service = new Service();
            call = (Call) service.createCall();
        } catch( JAXRPCException ex ) {
            throw new UncheckedWrapperException( ex );
        } catch( ServiceException ex ) {
            throw new UncheckedWrapperException( ex );
        }

        call.setOperationName( new QName( "http://www.redhat.com/Search", 
                                          "search" ) );

        QName resultQN = new QName( "http://www.redhat.com/Search",
                                    "SearchResult" );
        call.registerTypeMapping( SearchResult.class, resultQN,
                                  BeanSerializerFactory.class,
                                  BeanDeserializerFactory.class );

        // Loop until we've been asked to stop
        while ( !m_stop ) {
            // Get a new job from the head of the queue
            SearchJob job = m_queue.getSearchJob();
            s_log.debug("Process job terms:" + job.getTerms() + 
                        " url:" + job.getServer());

            String jobURL = job.getServer().getHostname();
            
            URL url = null;
            try {
                url = new URL( jobURL + SERVICE_URL );
            } catch( MalformedURLException ex ) {
                s_log.error( "Bad URL: " + jobURL + SERVICE_URL );

                // Need to call addResults so SearchGroup will eventually
                // finish
                job.getGroup().addResults( Collections.EMPTY_LIST );
                continue;
            }
            call.setTargetEndpointAddress( url );

            try {
                Object res = call.invoke(new Object[] { job.getTerms() } );
                Collection results = null;

                // New search
                if (res != null) {
                    if (res instanceof Collection) {
                        results = (Collection)res;
                    } else {
                        Object[] resArray = (Object[])res;
                        results = new ArrayList();
                        for (int i = 0 ; i < resArray.length ; i++) {
                            results.add(resArray[i]);
                        }
                    }
                    s_log.debug("results size" + results.size());
                } else {
                    s_log.debug("no results returned");
                }
                if (results != null && !results.isEmpty()) {
                    s_log.debug("about to add results to the searchgroup");
                    job.getGroup().addResults(  results  );
                }
            } catch( RemoteException ex ) {
                s_log.error( "Failure making SOAP call to " + url + ": " + 
                             ex.getMessage(), ex );

                // Need to call addResults so SearchGroup will eventually
                // finish
                job.getGroup().addResults( Collections.EMPTY_LIST );
                continue;
            }
        }
    }

    public void pleaseStop() {
        m_stop = true;
    }
}
