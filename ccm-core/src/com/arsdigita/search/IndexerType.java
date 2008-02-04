/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search;


/**
 * This class records the capabilities of a
 * search indexer implemenation.
 */
public class IndexerType {

    public static final IndexerType INTERMEDIA = 
        new IndexerType("intermedia",
                        new ContentType[] {
                            ContentType.RAW,
                            ContentType.XML
                        },
                        new com.arsdigita.search.intermedia.DocumentObserver());
    public static final IndexerType LUCENE =
        new IndexerType("lucene",
                        new ContentType[] {
                            ContentType.TEXT
                        },
                        new com.arsdigita.search.lucene.DocumentObserver());
    
    public static final IndexerType NOOP =
        new IndexerType("noop",
                        new ContentType[] {},
                        null);
                        
    
    private String m_key;
    private ContentType[] m_content;
    private DocumentObserver m_observer;
    
    protected IndexerType(String key,
                          ContentType[] content,
                          DocumentObserver observer) {
        m_key = key;
        m_content = content;
        m_observer = observer;
    }
    
    /**
     * Returns the key for the indexer
     */
    public String getKey() {
        return m_key;
    }
    
    /**
     * Returns the list of content types supported
     * by the indexer
     */
    public ContentType[] getContent() {
        return m_content;
    }
    
    /**
     * Gets the document observer for this indexer
     * @return document observer, or null
     */
    public DocumentObserver getObserver() {
        return m_observer;
    }

    /**
     * Simply returns the indexers key
     * @return the indexers key
     */
    public String toString() {
        return getKey();
    }
    
    /**
     * Two IndexerType objects compare equals
     * if they have the same key
     * @return true if o is an IndexerType and has the same key
     */
    public boolean equals(Object o) {
        if (o instanceof IndexerType) {
            return m_key.equals(((IndexerType)o).getKey());
        }
        return false;
    }
    
    /**
     * Generates a hashcode based on the index key
     * @return a hash code based on the key
     */
    public int hashCode() {
        return m_key.hashCode();
    }
}
