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


package com.arsdigita.london.rss.ui.admin;

import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Component;
import com.arsdigita.london.rss.Feed;
import com.arsdigita.london.rss.FeedCollection;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.ExternalLink;



public class FeedsTable extends Table {

    private FeedSelectionModel m_feed;

    public FeedsTable(FeedSelectionModel feed,
                      boolean acsj) {
        super(new FeedsTableModelBuilder(acsj),
              new String [] { "Title", "Description", "", "" });
        
        m_feed = feed;
        setRowSelectionModel(m_feed);

        setDefaultCellRenderer(new FeedsTableRenderer());
        addTableActionListener(new FeedsTableActionListener());
    }

    
    public static class FeedsTableModelBuilder extends LockableImpl 
        implements TableModelBuilder {
        private boolean m_acsj;

        public FeedsTableModelBuilder(boolean acsj) {
            m_acsj = acsj;
        }

        public TableModel makeModel(Table t,
                                    PageState s) {
            FeedCollection feeds = Feed.retrieveAll();
            feeds.filterACSJFeeds(m_acsj);

            return new FeedsTableModel(feeds);
        }
    }

    public static class FeedsTableModel implements TableModel {

        private FeedCollection m_feeds;
        
        public FeedsTableModel(FeedCollection feeds) {
            m_feeds = feeds;
        }

        public boolean nextRow() {
            return m_feeds.next();
        }

        public int getColumnCount() {
            return 3;
        }

        public Object getElementAt(int columnIndex) {
            return m_feeds.getFeed();
        }
        public Object getKeyAt(int columnIndex) {
            return m_feeds.getFeed().getID();
        }
    }

    private static class FeedsTableRenderer implements TableCellRenderer {
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            Feed feed = (Feed)value;
            
            if (column == 0) {
                return new ExternalLink(isSelected ? 
                                        new Label(feed.getTitle(),
                                                  Label.BOLD) :
                                        new Label(feed.getTitle()),
                                        feed.getURL());
            } else if (column == 1) {
                return new Label(feed.getDescription());
            } else if (column == 2) {
                return new ControlLink(new Label("[edit]"));
            } else if (column == 3) {
                return new ControlLink(new Label("[delete]"));
            }
            
            throw new UncheckedWrapperException("Column out of bounds " + column);
        }
    }

    private class FeedsTableActionListener implements TableActionListener {
        public void cellSelected(TableActionEvent e) {
            if (e.getColumn().intValue() == 3) {
                Feed feed = m_feed.getSelectedFeed(e.getPageState());
                feed.delete();
                getRowSelectionModel().clearSelection(e.getPageState());
            }
        }
        public void headSelected(TableActionEvent e) {}
    }
}
