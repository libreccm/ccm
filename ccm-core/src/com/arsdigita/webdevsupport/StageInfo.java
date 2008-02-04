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
package com.arsdigita.webdevsupport;

//imports here

/**
 *
 * @author Joseph A. Bank (jbank@alum.mit.edu)
 * @version 1.0
 **/
public class StageInfo {
    public static final String versionId = "$Id: StageInfo.java 1460 2007-03-02 14:36:38Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private String m_name;
    private long m_start_time;
    private long m_end_time;
    private int m_depth;
    private int m_start_queries;
    private int m_end_queries;
    private boolean m_leaf = true;
    public StageInfo(String name, int depth, int start_queries) {
        m_name = name;
        m_depth = depth;
        m_start_time = System.currentTimeMillis();
        m_start_queries = start_queries;
    }
    public String getName() {
        return m_name;
    }
    public void end(int end_queries) {
        m_end_time = System.currentTimeMillis();
        m_end_queries = end_queries;
    }
    public long endTime() {
        return m_end_time;
    }
    public long startTime() {
        return m_start_time;
    }
    public long time() {
        return m_end_time-m_start_time;
    }
    public int depth() {
        return m_depth;
    }
    public void setLeaf(boolean leaf) {
        m_leaf = leaf;
    }
    public boolean leaf() {
        return m_leaf;
    }
    public int numQueries() {
        return m_end_queries - m_start_queries;
    }
    public long queryTime(java.util.ListIterator queries) {
        long total_time = 0;
        int i = 0;
        while (queries.hasNext() && i!=m_start_queries) {
            queries.next();
            i++;
        }
        while (queries.hasNext() && i!=m_end_queries) {
            QueryInfo qi = (QueryInfo)queries.next();
            total_time += qi.getTotalTime();
            i++;
        }
        return total_time;
    }
}
