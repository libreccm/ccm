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

package com.arsdigita.cms.docmgr.search;

import java.math.BigDecimal;
/**
 * 
 * Encapsulates a DataQuery returned from SearchSpecification 
 * and provides convenience methods for retrieving results. 
 *
 * ********************* WARNING ***********************
 * ********************* WARNING ***********************
 * ********************* WARNING ***********************
 *
 * Under *NO* circumstances change anything in this file.
 * Doing so will break backwards compatability with the
 * remote search SOAP API.
 *
 * ********************* WARNING ***********************
 * ********************* WARNING ***********************
 * ********************* WARNING ***********************
 *
 *
 * @author Simon Buckle (sbuckle@arsdigita.com)
 **/
public class SearchResult implements Comparable {

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(SearchResult.class);
    private BigDecimal m_id;
    private String m_linkText;
    private String m_urlStub;
    private String m_summary;
    private BigDecimal m_score;
    
    public SearchResult() { };

    public SearchResult(BigDecimal id,
                        String link_text,
                        String url_stub,
                        String summary,
                        BigDecimal score) { 
        s_log.debug("ID in constructor is " +  id);
        
        m_id = id;
        m_linkText = link_text;
        m_urlStub = url_stub;
        m_summary = summary;
        m_score = score;
    }

    public String getSummary() {
        return m_summary;
    }

    public void setSummary( String summary ) {
        m_summary = summary;
    }

    public BigDecimal getScore() {
        return m_score;
    }

    public void setScore( BigDecimal score ) {
        m_score = score;
    }

    public BigDecimal getID() {
        return m_id;
    }

    public void setID( BigDecimal id ) {
        m_id = id;
    }

    public String getLink() {
        return m_linkText;
    }

    public void setLink( String link ) {
        m_linkText = link;
    }

    public String getUrlStub() {
        return m_urlStub;
    }

    public void setUrlStub( String urlStub ) {
        m_urlStub = urlStub;
    }

    public String toString() {
        return "<a href=\"" + m_urlStub + "\">" + m_linkText + "</a>";
    }

    public int compareTo( Object o ) {
        SearchResult other = (SearchResult) o;

        if ( other.getScore().longValue() < m_score.longValue() ) {
            return 1;
        }
        
        if ( other.getScore().longValue() > m_score.longValue() ) {
            return -1;
        }
        
        // Scores are equal. Sort on link_text
        return m_linkText.compareTo( other.getLink() );
    }
}
