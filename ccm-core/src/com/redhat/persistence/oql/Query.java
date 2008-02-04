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
package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;

/**
 * Query
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 **/

public class Query {

    public final static String versionId = "$Id: Query.java 737 2005-09-01 12:27:29Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(Query.class);

    private Expression m_query;
    private List m_names;
    private Map m_fetched;

    public Query(Expression query) {
        m_query = query;
        m_names = new ArrayList();
        m_fetched = new HashMap();
    }

    public void fetch(String name, Expression value) {
        if (m_fetched.containsKey(name)) {
            throw new IllegalArgumentException
                (name + ": already bound to " + m_fetched.get(name));
        }
        m_names.add(name);
        m_fetched.put(name, value);
    }

    private Expression get(String name) {
        return (Expression) m_fetched.get(name);
    }

    public Code generate(Root root) {
        return generate(root, false);
    }

    private static final String ROWNUM = "rownum__";

    private static final ThreadLocal s_generators = new ThreadLocal() {
        public Object initialValue() {
            return new Generator();
        }
    };

    private static LRUMap s_cache = new LRUMap(2000);

    public static void setQueryCacheSize(int size) {
        s_cache = new LRUMap(size);
        if( s_log.isDebugEnabled() )
            s_log.debug( "Reset query cache to size " + size );
    }

    public Code generate(Root root, boolean oracle) {
        Generator gen = (Generator) s_generators.get();
        gen.init(root);

        m_query.hash(gen);

        for (int i = 0; i < m_names.size(); i++) {
            String name = (String) m_names.get(i);
            gen.hash(name);
            Expression e = get(name);
            e.hash(gen);
        }

        Code cached;

        synchronized (s_cache) {
            cached = (Code) s_cache.get(gen.getLookupKey());
        }

        if (cached != null) {
            Code c = cached.resolve(gen.getBindings(), root);
            return c;
        }

        m_query.frame(gen);

        QFrame qframe = gen.getFrame(m_query);
        gen.push(qframe);
        try {
            for (int i = 0; i < m_names.size(); i++) {
                Expression e = get((String) m_names.get(i));
                e.frame(gen);
            }
        } finally {
            gen.pop();
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("unoptimized frame:\n" + qframe);
        }

        List frames = gen.getFrames();

        boolean modified;
        do {
            modified = false;
            for (int i = 0; i < frames.size(); i++) {
                QFrame qf = (QFrame) frames.get(i);
                modified |= qf.hoist();
            }
        } while (modified);

        if (s_log.isDebugEnabled()) {
            s_log.debug("hoisted frame:\n" + qframe);
        }

        for (int i = 0; i < frames.size(); i++) {
            QFrame qf = (QFrame) frames.get(i);
            qf.mergeOuter();
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("outers merged:\n" + qframe);
        }

        for (int i = 0; i < frames.size(); i++) {
            QFrame qf = (QFrame) frames.get(i);
            if (qf.getParent() == null) {
                qf.equifill();
            }
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("eq/nn filled:\n" + qframe);
        }

        Set collapse = new HashSet();
        Map canon = new HashMap();
        do {
            collapse.clear();
            canon.clear();
            modified = false;
            for (int i = 0; i < frames.size(); i++) {
                QFrame qf = (QFrame) frames.get(i);
                long st = System.currentTimeMillis();
                modified |= qf.innerize(collapse, canon);
            }
            if (modified) {
                for (Iterator it = collapse.iterator(); it.hasNext(); ) {
                    EquiSet eq = (EquiSet) it.next();
                    eq.collapse();
                }
            }
        } while (modified);

        if (s_log.isDebugEnabled()) {
            s_log.debug("innerized frame:\n" + qframe);
        }

        for (Iterator it = gen.getFrames().iterator(); it.hasNext(); ) {
            QFrame qf = (QFrame) it.next();
            qf.shrink();
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("shrunk frame:\n" + qframe);
        }

        Code sql = new Code("select ");

        for (Iterator it = m_names.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            Expression e = get(name);
            // XXX: should eliminate duplicate fetches here when
            // we return something smarter than a string from this
            // method.
            sql = sql.add(e.emit(gen))
                .add(" as \"")
                .add(name)
                .add("\"");
            if (it.hasNext()) {
                sql = sql.add(",\n       ");
            }
        }

        if (m_names.isEmpty()) {
            sql = sql.add("1 as dummy");
        }

        sql = sql.add("\nfrom ").add(qframe.emit(false, !oracle));

        Expression offset = qframe.getOffset();
        Expression limit = qframe.getLimit();
        if (oracle && (offset != null || limit != null)) {
            // We need one level of nesting here to make sure that the
            // order by happens before the rownum assignments. The
            // second level of nesting is required because filtering
            // on rownum directly doesn't work.
            sql = new Code("select * from (select r__.*, rownum as ")
                .add(ROWNUM).add(" from (").add(sql).add(") r__) where ");
            if (offset != null) {
                sql = sql.add(ROWNUM).add(" > ").add(offset.emit(gen));
            }
            if (limit != null) {
                if (offset != null) {
                    sql = sql.add(" and ");
                }
                sql = sql.add(ROWNUM);
                if (offset != null) {
                    sql = sql.add(" - ").add(offset.emit(gen));
                }
                sql = sql.add(" <= ").add(limit.emit(gen));
            }
        }

        // XXX: need better way to do size
        if (m_query instanceof Size) {
            sql = new Code("select count(*) as \"size\" from (").add(sql)
                .add(") count__");
        }

        synchronized (s_cache) {
            if( s_log.isInfoEnabled() ) {
                s_log.info( "Query cache MISS. Cache size " + s_cache.size() );

                if( s_log.isDebugEnabled() ) {
                    StringBuffer buf = new StringBuffer();

                    buf.append( "Cache Key: " ).append( gen.getStoreKey().toString() );
                    buf.append( "\nValue: " ).append( sql );
                    buf.append( "\nQuery: " ).append( toString() );

                    s_log.debug( buf.toString() );
                }
            }
            s_cache.put(gen.getStoreKey(), sql);
        }

        sql = sql.resolve(gen.getBindings(), root);

        return sql;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("query(");
        result.append(m_query);
        for (Iterator it = m_names.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            result.append(", ");
            result.append(name);
            result.append(" = ");
            result.append(get(name));
        }
        result.append(")");
        return result.toString();
    }

}
