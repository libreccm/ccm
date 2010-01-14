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
package com.arsdigita.search.lucene;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.runtime.RuntimeConfig;

/**
 * Indexer.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: Indexer.java 1845 2009-03-05 13:39:09Z terry $
 **/

class Indexer extends TimerTask {

    private static final Logger LOG =
        Logger.getLogger(Indexer.class);

    private static final RuntimeConfig CONF = RuntimeConfig.getConfig();

    // All reads and writes to the index must be synchronized against this
    // lock.
    private static final LuceneLock LOCK = LuceneLock.getInstance();
    private File m_index;

    public Indexer(String index) {
        m_index = new File(index);
    }

    public void run() {
        Session ssn = SessionManager.getSession();
        TransactionContext txn = ssn.getTransactionContext();

        if (txn.inTxn()) {
            throw new IllegalStateException("The lucene indexer must be run " +
                                            "from its own transaction.");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Lucene indexer running for '" + m_index + "'.");
        }

        txn.beginTxn();
        List dirtyList = findDirtyDocuments();
        txn.commitTxn();

        if (dirtyList.size() == 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Nothing to sync.");
            }
        } else {
            if (LOG.isInfoEnabled()) {
                LOG.info("About to sync " + dirtyList.size() + " docs.");
            }
        }

        try {
            for (int i=0; i<dirtyList.size(); ) {
                BigDecimal docID = (BigDecimal) dirtyList.get(i);
                String debug = "" + ++i + "/" + dirtyList.size();
                try {
                    syncDoc(docID, debug);
                } catch (IOException ioe) {
                    LOG.error("error syncing doc " + docID, ioe);
                }
            }
        } catch(Throwable t) {
            LOG.error("Unexpected error occured in Lucene indexer.", t);
        }

        if (LOG.isInfoEnabled() && dirtyList.size() > 0) {
            LOG.info("Lucene indexer finished running for '" + m_index +
                     "'.");
        }

    }

    private List findDirtyDocuments() {
        List dirty = new ArrayList();
        Session ssn = SessionManager.getSession();
        DataQuery dq = ssn.retrieveQuery("com.arsdigita.search.lucene.dirtyDocuments");
        dq.setParameter(Document.DIRTY, new Integer(1 << Index.getIndexID()));
        while (dq.next()) {
            dirty.add(dq.get("id"));
        }
        return dirty;
    }


    private void syncDoc(BigDecimal docID, String debug) throws IOException {
        Session ssn = SessionManager.getSession();
        TransactionContext txn = ssn.getTransactionContext();
        txn.beginTxn();
        boolean success = false;
        try {
            Document doc = Document.retrieve(docID);
            // the only way to update a lucene document is to first delete it. See
            // http://lucene.sourceforge.net/cgi-bin/faq/faqmanager.cgi?file=chapter.indexing&toc=faq#q4
            delete(doc);
            if (doc.isDeleted()) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Deleted document " + debug + " (" + doc.getID() + "): " +
                                  doc.getTitle());
                }
                // XXX we can't simply delete the doc because multiple JVMs need to
                // delete the content.
                // We need some other way to periodically cleanup docs.
                //doc.delete();
                doc.setDirty(false);
                doc.save();
            } else {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Indexing document " + debug + " (" + doc.getID() + "): " +
                              doc.getTitle());
                }
                update(doc);
                doc.setDirty(false);
                doc.save();
            }
            success = true;
        } finally {
            if (success) {
                txn.commitTxn();
            } else {
                txn.abortTxn();
            }
        }
    }

    private void delete(Document doc) throws IOException  {
        synchronized (LOCK) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Deleting document (" + doc.getID() + "): " +
                              doc.getTitle());
            }
            IndexReader ir = IndexReader.open(m_index);
            try {
                ir.deleteDocuments(new Term(Document.ID, doc.getID().toString()));
            } finally {
                ir.close();
            }
        }
    }

    private void update(Document doc) throws IOException {
            LuceneConfig conf = LuceneConfig.getConfig();
            Analyzer analyzer = conf.getAnalyzer();
        synchronized (LOCK) {
            IndexWriter iw = new IndexWriter(m_index, analyzer, false);
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Indexing document (" + doc.getID() + "): " +
                              doc.getTitle());
                }
                iw.addDocument(getDocument(doc));
            } finally {
                iw.close();
            }
        }
    }

    private org.apache.lucene.document.Document getDocument(Document doc) {
        org.apache.lucene.document.Document result =
            new org.apache.lucene.document.Document();

        result.add(new Field(Document.ID, doc.getID().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        String language = "";
        String country = "";
        Locale locale = doc.getLocale();
        if (locale != null) {
            language = locale.getLanguage();
            country = locale.getCountry();
        }
        result.add(new Field(Document.LANGUAGE, language, Field.Store.YES, Field.Index.NOT_ANALYZED));
        result.add(new Field(Document.COUNTRY, country, Field.Store.YES, Field.Index.NOT_ANALYZED));
        result.add(new Field(Document.TYPE, doc.getType(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        result.add(new Field(Document.TYPE_SPECIFIC_INFO, toString(doc.getTypeSpecificInfo()), Field.Store.YES,
                Field.Index.NOT_ANALYZED));
        result.add(new Field(Document.TITLE, toString(doc.getTitle()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        result.add(new Field(Document.SUMMARY, toString(doc.getSummary()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        result.add(new Field(Document.CONTENT, new StringReader(toString(doc.getContent()))));
        result.add(new Field(Document.CREATION_DATE, DateTools.timeToString(doc.getCreationDate().getTime(),
                DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
        result.add(new Field(Document.CREATION_PARTY, toString(doc.getCreationParty()), Field.Store.YES,
                Field.Index.NOT_ANALYZED));
        result.add(new Field(Document.LAST_MODIFIED_DATE, DateTools.timeToString(doc.getLastModifiedDate().getTime(),
                DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
        result.add(new Field(Document.LAST_MODIFIED_PARTY, toString(doc.getLastModifiedParty()), Field.Store.YES,
                Field.Index.NOT_ANALYZED));
        result.add(new Field(Document.CONTENT_SECTION, toString(doc.getContentSection()), Field.Store.YES,
                Field.Index.NOT_ANALYZED));

        return result;
    }
    
    private static final String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
