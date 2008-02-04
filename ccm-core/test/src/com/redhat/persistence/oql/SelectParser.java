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
package com.redhat.persistence.oql;

import com.arsdigita.util.UncheckedWrapperException;
import com.redhat.persistence.common.SQL;
import com.redhat.persistence.common.SQLParser;
import com.redhat.persistence.common.SQLToken;
import java.io.StringReader;
import org.apache.log4j.Logger;

/**
 * SelectParser
 *
 * Simple select statement parser for use in QueryTest tests.
 * Use for counting subselects, inner, and outer joins.
 *
 * @author jorris@redhat.com
 * @version $Revision $1 $ $Date: 2004/08/16 $
 */
public class SelectParser {

    private static final Logger s_log = Logger.getLogger(SelectParser.class);

    private SQLToken m_token = null;
    private SQLToken m_matchend;
    private int m_subselects = 0;
    private int m_inners = 0;
    private int m_outers = 0;

    public SelectParser(final String sqlText) {
        s_log.debug("Parsing: " + sqlText);
        SQL sql = getParsedSQL(sqlText);

        m_token = sql.getFirst();
        while (m_token != null) {
            if (match("(", "select")) {
                m_subselects++;
            } else if (match("left", "join")) {
                m_outers++;
            } else if (match("join")) {
                m_inners++;
            } else {
                m_token = next(m_token);
                continue;
            }
            m_token = m_matchend;
        }
    }

    public int getSubselectCount() {
        return m_subselects;
    }

    public int getJoinCount() {
        return m_inners + m_outers;
    }

    public int getInnerCount() {
        return m_inners;
    }

    public int getOuterCount() {
        return m_outers;
    }

    private boolean match(String t) {
        return match(new String[] { t });
    }

    private boolean match(String t1, String t2) {
        return match(new String[] { t1, t2 });
    }

    private boolean match(String[] images) {
        SQLToken tok = m_token;
        for (int i = 0; i < images.length; i++) {
            if (tok == null) { return false; }
            if (!tok.getImage().equalsIgnoreCase(images[i])) {
                return false;
            }
            tok = next(tok);
        }
        m_matchend = tok;
        return true;
    }

    SQLToken next(SQLToken t) {
        do {
            t = t.getNext();
        } while (t != null && t.isSpace());
        return t;
    }

    private SQL getParsedSQL(final String sql) {
        SQLParser parser = new SQLParser(new StringReader(sql));
        try {
            parser.sql();
        } catch (com.redhat.persistence.common.ParseException e) {
            throw new UncheckedWrapperException(e);
        }

       return parser.getSQL();

    }

}
