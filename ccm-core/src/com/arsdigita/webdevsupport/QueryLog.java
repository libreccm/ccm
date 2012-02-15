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
package com.arsdigita.webdevsupport;

import com.arsdigita.dispatcher.RequestContext;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;

import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import org.apache.oro.text.perl.Perl5Util;

import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Substitution;
import org.apache.oro.text.regex.Util;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.perl.MalformedPerl5PatternException;

import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.IntegerParameter;


/**
 * Print out all queries in a request
 *
 * @author Daniel Berrange (berrange@redhat.com)
 * @version 1.0
 **/
public class QueryLog implements com.arsdigita.dispatcher.Dispatcher {
    private static final Logger s_log =
        Logger.getLogger(QueryLog.class.getName());

    private ParameterModel m_request_id = new IntegerParameter("request_id");
    private ParameterModel m_query_id = new IntegerParameter("query_id");

    public void dispatch(HttpServletRequest req,
                         HttpServletResponse resp,
                         RequestContext ctx)
        throws IOException, ServletException {

        Integer request_id = (Integer)m_request_id.transformValue(req);
        Integer query_id = (Integer)m_query_id.transformValue(req);
        RequestInfo ri =
            WebDevSupportListener.getInstance().getRequest(request_id.intValue());
        final Iterator iter = (ri == null) ? new ArrayList().iterator() :
            ri.getQueries();
        
        resp.setContentType("text/plain");
        

        Writer w = resp.getWriter();
        while (iter.hasNext()) {
            QueryInfo info = (QueryInfo)iter.next();
            
            Integer id = new Integer(info.getID());
            Long time = new Long(info.getTime());
            
            // If a single query was asked for, skip
            // until we hit it
            if (query_id != null &&
                !query_id.equals(id)) {
                continue;
            }

            w.write("-- ID: #" + id + "\n");
            w.write("-- Duration: " + time + "ms\n");
            w.write(substituteSQL(info) + ";\n\n\n");
        }
    }

    public static String substituteSQL(QueryInfo info) {
            
                final Perl5Matcher matcher = new Perl5Matcher();
                final Perl5Compiler compiler = new Perl5Compiler();
                
                String query = info.getQuery();
                Map vars = info.getBindvars();
            
                HashSubstitution subst = new HashSubstitution(vars);
            
                StringBuffer result = new StringBuffer();
                PatternMatcherInput input = new PatternMatcherInput(query);
            
                try {
                    Util.substitute(
                        result,
                        matcher,
                        compiler.compile("(\\?)"),
                        subst,
                        input,
                        Util.SUBSTITUTE_ALL);
                } catch (MalformedPatternException e) {
                        throw new UncheckedWrapperException(
                                "cannot perform substitution",
                                e);
                }
                
                return result.toString();

    }
    private static Perl5Util s_util = new Perl5Util();

    private static class HashSubstitution implements Substitution {
        private Map m_hash;
        private int index = 1;
        public HashSubstitution(Map hash) {
            m_hash = hash;
        }

        public void appendSubstitution(StringBuffer appendBuffer,
                                       MatchResult match,
                                       int substitutionCount,
                                       PatternMatcherInput originalInput,
                                       PatternMatcher matcher,
                                       Pattern pattern) {
            Integer key = new Integer(index++);
            Object v = m_hash.get(key);
            String val = null;
            try {
                if (v == null || "NULL".equals(v) || "null".equals(v)) {
                    val = "null";
                } else if (v instanceof Boolean) {
                    val = Boolean.TRUE.equals(v) ? "'1'" : "'0'";
                } else {
                    val = "'" + 
                        s_util.substitute("s/'/''/g", v.toString()) +
                        "'";
                }
            } catch (MalformedPerl5PatternException e) {
                throw new UncheckedWrapperException(
                    "cannot perform substitution", e
                );
            }
            appendBuffer.append(val);
        }
    }

}
