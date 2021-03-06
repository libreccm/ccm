/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
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

package com.arsdigita.cms.ui.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.Folder;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;

/**
 * TableModelBuilder that creates a model for the content section summary report.   
 * 
 * @author <a href="https://sourceforge.net/users/thomas-buckel/">thomas-buckel</a>
 */
public class ContentSectionSummaryReportTableModelBuilder extends AbstractTableModelBuilder
{

    @Override
    public TableModel makeModel(Table t, PageState s)
    {
        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery("com.arsdigita.cms.getContentSectionSummary");
        query.setParameter("sectionId", CMS.getContext().getContentSection().getID());
        return new CSSRModel(new DataQueryDataCollectionAdapter(query, "folder"));
    }

    /**
     * Generates a table model by combining a list of top level folder (content items) with statistics about
     * content items with in the folder in draft and live versions.
     */
    private class CSSRModel implements TableModel
    {

        private final DataCollection m_folders;

        private String m_folderName;
        
        private BigDecimal m_subfolderCount;
        
        private ContentTypeStatistics m_currentStatsRow;
        
        private Iterator<ContentTypeStatistics> m_contentTypeStatIter;

        CSSRModel(DataCollection folders)
        {
            m_folders = folders;
        }

        @Override
        public final int getColumnCount()
        {
            return 5;
        }

        /**
         * Combines an 'outer' iterator over the given DataCollection with an 'inner' iterator
         * that contains rows for each row of the outer collection. 
         * {@inheritDoc}
         */
        @Override
        public final boolean nextRow()
        {
            if ((m_contentTypeStatIter == null) && m_folders.next())
            {
                m_folderName = (String) m_folders.get("folder");
                m_subfolderCount = (BigDecimal) m_folders.get("subfolderCount");
                Folder draftFolder = new Folder((BigDecimal) m_folders.get("folderId"));

                m_contentTypeStatIter = retrieveContentTypeStats(draftFolder);
                if (m_contentTypeStatIter.hasNext())
                {
                    m_currentStatsRow = m_contentTypeStatIter.next();
                }
                else
                {
                    // Rather than recursing into nextRow() again, a m_currentStatsRow == null 
                    // is rendered to show one row for the folder but with no content type or values.
                    m_contentTypeStatIter = null;
                    m_currentStatsRow = null;
                }
                return true;
            }
            else if (m_contentTypeStatIter != null)
            {
                if (m_contentTypeStatIter.hasNext())
                {
                    m_currentStatsRow = m_contentTypeStatIter.next();
                }
                else
                {
                    m_contentTypeStatIter = null;
                    return nextRow();
                }
                return true;
            }
            else
            {
                m_folders.close();
                return false;
            }
        }

        @Override
        public final Object getKeyAt(final int column)
        {
            return -1;
        }

        @Override
        public final Object getElementAt(final int column)
        {
            switch (column)
            {
                case 0:
                    return m_folderName;
                case 1:
                    return m_subfolderCount.toString();
                case 2:
                    return (m_currentStatsRow != null) ? m_currentStatsRow.getContentType() : "N/A";
                case 3:
                    return (m_currentStatsRow != null) ? m_currentStatsRow.getDraftCount() : "N/A";
                case 4:
                    return (m_currentStatsRow != null) ? m_currentStatsRow.getLiveCount() : "N/A";
                default:
                    throw new IllegalArgumentException("Illegal column index " + column);
            }
        }

        /**
         * Retrieve a list of content types used within a folder and for each content type
         * the number of draft and live content items of this type.
         * 
         * @param draftFolder       Draft folder to retrieve stats for.
         * 
         * @return Iterator over the retrieved statistics. Empty iterator with no results 
         *         where found.
         */
        private Iterator<ContentTypeStatistics> retrieveContentTypeStats(Folder draftFolder)
        {

            Session session = SessionManager.getSession();

            // Query the number of content items per content type for drafts
            DataQuery query = session.retrieveQuery("com.arsdigita.cms.getContentTypeCountPerFolder");
            query.setParameter("folderId", draftFolder.getID());
            DataCollection types = new DataQueryDataCollectionAdapter(query, "types");
            Map<String, Long> draftContentTypeCounts = new HashMap<String, Long>();
            try
            {
                while (types.next())
                {
                    draftContentTypeCounts.put((String) types.get("contentType"), (Long) types.get("typeCount"));
                }
            }
            finally
            {
                types.close();
            }

            // If there's a live version of the folder, query the number of content items per content type for it
            // and merge both draft and live numbers 
            List<ContentTypeStatistics> result = new ArrayList<ContentTypeStatistics>();
            Folder liveFolder = (Folder) draftFolder.getLiveVersion();
            if (liveFolder != null)
            {
                query = session.retrieveQuery("com.arsdigita.cms.getContentTypeCountPerFolder");
                query.setParameter("folderId", liveFolder.getID());
                types = new DataQueryDataCollectionAdapter(query, "types");
                try
                {
                    while (types.next())
                    {
                        String contentType = (String) types.get("contentType");
                        long draftCount = (draftContentTypeCounts.get(contentType) != null) ? draftContentTypeCounts
                                .get(contentType) : 0;
                        long liveCount = (Long) types.get("typeCount");
                        result.add(new ContentTypeStatistics(contentType, draftCount, liveCount));
                        draftContentTypeCounts.remove(contentType);
                    }
                }
                finally
                {
                    types.close();
                }
            }

            // Add all draft stats that haven't been merged  
            for (Map.Entry<String, Long> draftCount : draftContentTypeCounts.entrySet())
            {
                result.add(new ContentTypeStatistics(draftCount.getKey(), draftCount.getValue(), 0));
            }

            return result.iterator();
        }
    }

    /**
     * Value object that holds content type statistics for a folder.  
     */
    private static class ContentTypeStatistics
    {

        private final String m_contentType;

        private final long m_draftCount;

        private final long m_liveCount;

        public ContentTypeStatistics(String contentType, long draftCount, long liveCount)
        {
            m_contentType = contentType;
            m_draftCount = draftCount;
            m_liveCount = liveCount;
        }

        public String getContentType()
        {
            return m_contentType;
        }

        public long getDraftCount()
        {
            return m_draftCount;
        }

        public long getLiveCount()
        {
            return m_liveCount;
        }

    }

}
