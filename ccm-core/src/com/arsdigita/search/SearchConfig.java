/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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


import org.apache.log4j.Logger;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.EnumerationParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 * Stores the configuration record for the search service
 */
public final class SearchConfig extends AbstractConfig {

    private static Logger s_log = Logger.getLogger(SearchConfig.class);

    private EnumerationParameter m_indexer;
    private Parameter m_lazyUpdates;
    private Parameter m_xmlContentWeight;
    private Parameter m_rawContentWeight;

    public SearchConfig() {

        m_indexer = new IndexerParameter
            ("waf.search.indexer", 
             Parameter.REQUIRED,
             IndexerType.LUCENE);
        m_indexer.put(IndexerType.INTERMEDIA.getKey(), 
                      IndexerType.INTERMEDIA);
        m_indexer.put(IndexerType.LUCENE.getKey(), 
                      IndexerType.LUCENE);
        m_indexer.put(IndexerType.NOOP.getKey(), 
                      IndexerType.NOOP);

        m_lazyUpdates = new BooleanParameter
            ("waf.search.lazy_updates", 
             Parameter.REQUIRED, 
             new Boolean(true));

        m_xmlContentWeight = new IntegerParameter
            ("waf.search.intermedia.xml_content_weight",
             Parameter.REQUIRED,
             new Integer(1));
        m_rawContentWeight = new IntegerParameter
            ("waf.search.intermedia.raw_content_weight",
             Parameter.REQUIRED,
             new Integer(1));

        register(m_indexer);
        register(m_lazyUpdates);
        register(m_xmlContentWeight);
        register(m_rawContentWeight);

        loadInfo();
    }

    /**
     * Returns the current active indexer type
     */
    public IndexerType getIndexerType() {
        return (IndexerType)get(m_indexer);
    }

    /**
     * Returns the current active indexer type key
     * @see #isIntermediaEnabled()
     * @see #isLuceneEnabled()
     */
    public String getIndexer() {
        return getIndexerType().getKey();
    }

    /**
     * Sets the current active indexer type. This is not a public
     * method, as it should <em>only</em> be used by the unit
     * tests. Under normal operations, the indexer is controlled by
     * the parameters.
     */
    void setIndexerType(IndexerType indexer) {
        set(m_indexer, indexer);
    }

    /**
     * Convenience method for determining if intermedia
     * is currently enabled
     * @return true if intermedia is active, false otherwise
     * @see #getIndexer()
     */
    public boolean isIntermediaEnabled() {
        return IndexerType.INTERMEDIA.equals(getIndexerType());
    }

    /**
     * Convenience method for determining if lucene
     * is currently enabled
     * @return true if intermedia is active, false otherwise
     * @see #getIndexer()
     */
    public boolean isLuceneEnabled() {
        return IndexerType.LUCENE.equals(getIndexerType());
    }

    /**
     * Returns the bitmask for allowed content types.  The bits in the mask
     * correspond to the XXX constants defined in the {@link ContentType} class.
     * @return content bit mask
     * @see ContentType#RAW
     * @see ContentType#XML
     * @see ContentType#TEXT
     */
    public ContentType[] getContent() {
        return getIndexerType().getContent();
    }

    /**
     * Determines if raw binary content is supported
     * @return true if raw content is supported
     * @see #allowsTextContent()
     * @see #allowsXMLContent()
     */
    public boolean allowsRawContent() {
        ContentType[] types = getContent();
        for (int i = 0 ; i < types.length ; i++) {
            if (types[i].equals(ContentType.RAW)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Determines if plain text content is supported
     * @return true if text content is supported
     * @see #allowsRawContent()
     * @see #allowsXMLContent()
     */
    public boolean allowsTextContent() {
        ContentType[] types = getContent();
        for (int i = 0 ; i < types.length ; i++) {
            if (types[i].equals(ContentType.TEXT)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if xml content is supported
     * @return true if xml content is supported
     * @see #allowsTextContent()
     * @see #allowsRawContent()
     */
    public boolean allowsXMLContent() {
        ContentType[] types = getContent();
        for (int i = 0 ; i < types.length ; i++) {
            if (types[i].equals(ContentType.XML)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the content type represented by
     * the specified bitmask is supported.
     * @return true if the bitmask requested is supported
     * @see #allowsXMLContent()
     * @see #allowsTextContent()
     * @see #allowsRawContent()
     */
    public boolean allowsContent(ContentType content) {
        ContentType[] types = getContent();
        for (int i = 0 ; i < types.length ; i++) {
            if (types[i].equals(content)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the current document update observer
     * @return the document observer, or null if none is configured.
     */
    public DocumentObserver getObserver() {
        return getIndexerType().getObserver();
    }

    /**
     * Returns the lazy update flag. If this is set to
     * true, then the search index will only be updated
     * at the end of a transaction.
     * @return true if lazy updates are turned on
     */
    public boolean getLazyUpdates() {
        return ((Boolean) get(m_lazyUpdates)).booleanValue();
    }
 

    /**
     * Sets the lazy update flag. This is not a public
     * method, as it should <em>only</em> be used by the unit
     * tests. Under normal operations, the indexer is controlled by
     * the parameters.
     */
    void setLazyUpdates(boolean lazyUpdates) {
        set (m_lazyUpdates,new Boolean(lazyUpdates));
    }

    private class IndexerParameter extends EnumerationParameter {
        public IndexerParameter(final String name,
                                final int multiplicity,
                                final Object defaalt) {
            super(name, multiplicity, defaalt);
        }
        // XXX
        // This validation method is commented out for now due to
        // https://bugzilla.redhat.com/bugzilla/show_bug.cgi?id=111291
        // The basic problem is that the first time that the configuration is
        // being loaded, the RuntimeConfig context hasn't been loaded yet, so
        // RuntimeConfig.getConfig() fails.  - dgregor@redhat.com 12/01/2003
        //
        // protected void doValidate(final Object value, final ErrorList errors) {
        //     super.doValidate(value,errors);
        //     if (Search.INDEXER_INTERMEDIA.equals(value) &&
        //         DbHelper.getDatabaseFromURL(RuntimeConfig.getConfig().getJDBCURL()) != DbHelper.DB_ORACLE) {
        //         errors.add(new ParameterError
        //                    (IndexerParameter.this,
        //                     "Intermedia searching only works on Oracle, not " +
        //                     DbHelper.getDatabaseName(DbHelper.getDatabaseFromURL(RuntimeConfig.getConfig().getJDBCURL()))));
        //     }
        // }
    }

    /**
     * The relative given to XML content when ranking search results.
     * Only used by the interMedia query engine.
     **/
    public Integer getXMLContentWeight() {
        return (Integer) get(m_xmlContentWeight);
    }

    /**
     * The relative given to raw content when ranking search results.
     * Only used by the interMedia query engine.
     **/
    public Integer getRawContentWeight() {
        return (Integer) get(m_rawContentWeight);
    }
}
