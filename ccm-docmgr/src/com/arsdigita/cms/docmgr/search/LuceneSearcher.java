package com.arsdigita.cms.docmgr.search;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.User;
import com.arsdigita.lucene.LuceneSearch;
import com.arsdigita.lucene.ObjectTypeFilter;
import com.arsdigita.lucene.TypeSpecificFilter;

public class LuceneSearcher implements Searcher
{
    private static final Logger s_log = Logger.getLogger(LuceneSearcher.class);

    private static final int SCORE_MULTIPLIER = 100;

    public SearchResults simpleSearch( String terms, User user ) {
        final TypeSpecificFilter tsf = new TypeSpecificFilter(ContentItem.LIVE);
        LuceneSearch search = new LuceneSearch(terms, tsf);
        s_log.debug("Do lucene simple search for [" + terms + "] ignoring user permissions");
        return new LuceneSearchResults(search);
    }

    /** Returns a collection of the search results.
     *  Currently does not filter by section, user, author, mimeType, workspace
     *  or dates.
     */

    public SearchResults advancedSearch( String terms, String author, String mimeType,
                                         BigDecimal workspaceID,
                                         Date lastModifiedStartDate,
                                         Date lastModifiedEndDate,
                                         String[] types, String[] sections,
                                           User user,
                                           Collection categoryIDs ) {
        //XXX Fix me does not filter by section or user, mime
        LuceneSearch search = null;

        Filter filter = null;
        if (types != null && types.length > 0) {
            try {
                Filter[] filters = new Filter[types.length];
                for (int i = 0 ; i < filters.length ; i++) {
                        
                    ContentType type = new ContentType(new BigDecimal(types[i]));
                    filters[i] = new ObjectTypeFilter(type.getClassName() );
                        
                }
                filter = new UnionFilter(filters);
            } catch ( DataObjectNotFoundException e ) {
                s_log.error(e.getMessage());
            }
        }

        final TypeSpecificFilter tsf = new TypeSpecificFilter(ContentItem.LIVE);
        if ( filter == null ) {
            filter = tsf;
        } else {
            final Filter objectTypeFilter = filter;
            filter = new Filter() {
                    public BitSet bits(IndexReader reader)
                        throws IOException {
            
                        BitSet bits = new BitSet(reader.maxDoc());
                        bits.or(tsf.bits(reader));
                        bits.and(objectTypeFilter.bits(reader));
                        return bits;
                    }
                };
        }

        if (filter != null) {
            search = new LuceneSearch(terms,
                                      filter);
        } else {
            search = new LuceneSearch(terms);
        }

        return new LuceneSearchResults(search);
    }

    private class UnionFilter extends Filter {

        private Filter[] m_filters;

        public UnionFilter(Filter[] filters) {
            m_filters = filters;
        }
        public BitSet bits(IndexReader reader)
            throws IOException {

            BitSet bits = new BitSet(reader.maxDoc());

            for (int i = 0 ; i < m_filters.length ; i++) {
                bits.or(m_filters[i].bits(reader));
            }

            return bits;
        }
    }



}
