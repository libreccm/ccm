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
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.CompoundType;
import com.arsdigita.profiler.Profiler;
import com.arsdigita.util.Assert;
import com.redhat.persistence.Cursor;
import com.redhat.persistence.DataSet;
import com.redhat.persistence.ProtoException;
import com.redhat.persistence.Signature;
import com.redhat.persistence.common.ParseException;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.SQLParser;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.oql.All;
import com.redhat.persistence.oql.Define;
import com.redhat.persistence.oql.Equals;
import com.redhat.persistence.oql.Exists;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.LeftJoin;
import com.redhat.persistence.oql.Limit;
import com.redhat.persistence.oql.Literal;
import com.redhat.persistence.oql.Offset;
import com.redhat.persistence.oql.Sort;
import com.redhat.persistence.oql.Static;
import com.redhat.persistence.oql.Variable;
import com.redhat.persistence.pdl.PDL;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * DataQueryImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: DataQueryImpl.java 1304 2006-08-31 13:12:47Z sskracic $
 */
class DataQueryImpl implements DataQuery {

    private static final Logger s_log = Logger.getLogger(DataQueryImpl.class);

    private static final String s_unalias =
        "com.arsdigita.persistence.DataQueryImpl.unalias";

    private static final String s_mapAndAddPath =
        "com.arsdigita.persistence.DataQueryImpl.mapAndAddPath";

    private Map m_options = new HashMap();

    private SQLParser getParser(String key, Reader reader,
                                SQLParser.Mapper mapper) {
        TransactionContext ctx = m_ssn.getTransactionContext();
        SQLParser p = (SQLParser) ctx.getAttribute(key);
        if (p == null) {
            p = new SQLParser(reader, mapper);
            ctx.setAttribute(key, p);
        } else {
            p.initialize(reader, mapper);
        }

        return p;
    }

    private Session m_ssn;
    private com.redhat.persistence.Session m_pssn;
    private HashMap m_bindings = new HashMap();

    final Signature m_originalSig;
    private Signature m_signature;

    final private Expression m_originalExpr;
    private Expression m_expr;

    Cursor m_cursor = null;
    private CompoundFilterImpl m_filter;

    private ArrayList m_orders = new ArrayList();

    // This indicates the offset/limit sent of the query
    private Integer m_offset = null;
    private Integer m_limit = null;

    // This indicates the limits on the number of rows returned by the query
    private int m_lowerBound = 0;
    private int m_upperBound = Integer.MAX_VALUE;

    private final List m_aliases = new ArrayList();
    // used by addPath to implement addPath for paths traversing 0..n
    private HashMap m_joins = new HashMap();

    private final FilterFactory m_factory;

    DataQueryImpl(Session ssn, DataSet ds) {
        this(ssn, ds.getSignature(), ds.getExpression());
    }

    DataQueryImpl(Session ssn, Signature sig, Expression expr) {
        m_ssn = ssn;
        m_pssn = ssn.getProtoSession();
        m_originalSig = sig;
        m_originalExpr = expr;
        m_factory = new FilterFactoryImpl(ssn);
        reset();
    }

    Session getSession() {
        return m_ssn;
    }

    /**
     * Returns the com.redhat.persistence type of the query.
     */
    ObjectType getTypeInternal() {
        return m_originalSig.getObjectType();
    }

    public CompoundType getType() {
        throw new Error("not implemented");
    }

    public boolean hasProperty(String propertyName) {
        Path p =  unalias(Path.get(propertyName));
        return hasProperty(p);
    }

    boolean hasProperty(Path p) {
        return getTypeInternal().getProperty(p) != null;
    }

    public void reset() {
	close();
	m_cursor = null;

        clearOrder();
        clearFilter();
	m_lowerBound = 0;
	m_upperBound = Integer.MAX_VALUE;
        m_offset = null;
        m_limit = null;
        m_signature = new Signature(m_originalSig);
        m_joins.clear();
        m_bindings.clear();

        // XXX: hack for data queries with bindings that have calls to addPath
        // addJoin needs to join against the static-ified version of the All.
        // this is equivalent to testing if m_originalExpr instanceof All
        if (this.getClass().equals(DataQueryImpl.class)) {
            m_expr = new Define
                (new Static(getTypeInternal().getQualifiedName(), m_bindings),
                 "this");
        } else {
            m_expr = new Define(m_originalExpr, "this");
        }

        m_signature = new Signature();
        m_signature.addSignature(m_originalSig, Path.get("this"));
        m_joins.put(null, "this");
    }


    public boolean first() {
        throw new Error("not implemented");
    }

    public boolean isEmpty() {
	try {
            // can not use checkCursor() because then we can't add filters
            // after calls to isEmpty
            if (m_cursor == null) {
                return new DataSet
                    (m_pssn, m_signature, makeExpr()).isEmpty();
            } else {
                return m_cursor.getDataSet().isEmpty();
            }
	} catch (ProtoException e) {
	    throw PersistenceException.newInstance(e);
	}
    }


    public boolean isBeforeFirst() {
        checkCursor();
        return m_cursor.isBeforeFirst();
    }

    public boolean isFirst() {
        checkCursor();
        return m_cursor.isFirst();
    }


    public boolean isLast() {
        throw new Error("not implemented");
    }


    public boolean isAfterLast() {
        checkCursor();
        return m_cursor.isAfterLast();
    }


    public boolean last() {
        throw new Error("not implemented");
    }


    public boolean previous() {
        throw new Error("not implemented");
    }

    public void addPath(String path) {
        Path p = unalias(Path.get(path));
        addPath(p, true);
    }

    private void addPath(Path path, boolean requiresFetching) {
        if (m_cursor != null) {
            throw new PersistenceException
                ("Paths cannot be added on an active data query.");
        }

        addJoin(path);
        path = resolvePath(path);
        if (requiresFetching) {
            m_signature.addPath(path);
        } else {
            Assert.isTrue(m_signature.exists(path));
        }
    }

    protected Path resolvePath(Path path) {
        if (m_joins.size() == 0) { return path; }

        Path base = path;
        for (; base != null; base = base.getParent()) {
            if (m_joins.containsKey(base)) {
                break;
            }
        }

        Path candidate = Path.add
            ((String) m_joins.get(base), Path.relative(base, path));
        if (m_signature.exists(candidate)) {
            return candidate;
        }
        return path;
    }

    private void addJoin(Path path) {
        List elts = new ArrayList();
        for (Path p = path; p != null; p = p.getParent()) {
            elts.add(p.getName());
        }

        ObjectType type = getTypeInternal();
        Path coll = null;
        Path prev = null;
        boolean collectionFound = false;
        for (int i = elts.size() - 1; i >= 0; i--) {
            String propName = (String) elts.get(i);
            Property prop = type.getProperty(propName);
            type = prop.getType();
            coll = Path.add(coll, propName);

            if (prop.isCollection()) {
                collectionFound = true;
                String alias = coll.getPath().replace('.', '_');
                if (m_joins.containsKey(coll)) {
                    prev = coll;
                    continue;
                }

                m_joins.put(coll, alias);

                Expression prevColl;
                if (prev == null) {
                    prevColl = new Define
                        (Expression.valueOf(Path.add("this", coll)), "target");
                } else {
                    Path p = Path.add
                        ((String) m_joins.get(prev),
                         Path.relative(prev, coll));
                    prevColl = new Define(Expression.valueOf(p), "target");
                }

                Expression cond = new Exists
                    (new com.redhat.persistence.oql.Filter
                     (prevColl,
                      new Equals
                      (new Variable(alias), new Variable("target"))));

                m_expr = new LeftJoin
                    (m_expr,
                     new Define(new All(type.getQualifiedName()), alias),
                     cond);
                m_signature.addSource(type, Path.get(alias));

                prev = coll;
            }


            if (propName.endsWith(PDL.LINK)) {
                Path rel = null;
                String assocName = propName.substring
                    (0, propName.length() - PDL.LINK.length());
                Path assoc = Path.add(coll.getParent(), assocName);

                addJoin(assoc);
                Path pathThroughLink = Path.add(resolvePath(coll), assocName);
                m_expr = new com.redhat.persistence.oql.Filter
                    (m_expr, new Equals
                     (Expression.valueOf(pathThroughLink),
                      Expression.valueOf(resolvePath(assoc))));
            }
        }
    }

    public Filter setFilter(String conditions) {
        clearFilter();
        return addFilter(conditions);
    }


    public Filter addFilter(String conditions) {
        if (m_cursor != null) {
            throw new PersistenceException
                ("The filter cannot be set on an active data query. " +
                 "Data query must be rewound.");
        }

        return m_filter.addFilter(conditions);
    }


    public Filter addFilter(Filter filter) {
        if (m_cursor != null) {
            throw new PersistenceException
                ("The filter cannot be set on an active data query. " +
                 "Data query must be rewound.");
        }

        return m_filter.addFilter(filter);
    }

    public boolean removeFilter(Filter filter) {
        if (m_cursor != null) {
            throw new PersistenceException
                ("The filter cannot be removed on an active data query. " +
                 "Data query must be rewound.");
        }

        return m_filter.removeFilter(filter);
    }

    public Filter addInSubqueryFilter(String propertyName,
                                      String subqueryName) {
        return addFilter(getFilterFactory().in(propertyName, subqueryName));
    }


    public Filter addInSubqueryFilter(String propertyName,
                                      String subQueryProperty,
                                      String queryName) {
        return addFilter
            (getFilterFactory().in
             (propertyName, subQueryProperty, queryName));
    }

    public Filter addNotInSubqueryFilter(String propertyName,
                                         String subqueryName) {
        return addFilter(getFilterFactory().notIn(propertyName, subqueryName));
    }

    public Filter addEqualsFilter(String attribute, Object value) {
        return addFilter(getFilterFactory().equals(attribute, value));
    }

    public Filter addNotEqualsFilter(String attribute, Object value) {
        return addFilter(getFilterFactory().notEquals(attribute, value));
    }

    public void clearFilter() {
        if (m_cursor != null) {
            throw new PersistenceException
                ("Cannot clear the filter on an active data query. " +
                 "Data query must be rewound.");
        }
        m_filter = (CompoundFilterImpl) getFilterFactory().and();
    }

    public FilterFactory getFilterFactory() {
        return m_factory;
    }

    public void setOrder(String order) {
        clearOrder();
        addOrder(order);
    }

    public void addOrder(String order) {
        if (m_cursor != null) {
            throw new PersistenceException
                ("Cannot order an active data query. " +
                 "Data query must be rewound.");
        }
        order = unalias(order);
        m_orders.add(order);
    }

    private int m_order = 0;

    public void addOrderWithNull(String orderOne, Object orderTwo,
				 boolean isAscending) {
        String suffix = null;
        if (isAscending) {
            suffix = "asc";
        } else {
            suffix = "desc";
        }

        Object secondElement = orderTwo;
        if (orderTwo instanceof String && orderTwo != null) {
            Path two = unalias(Path.get((String) orderTwo));
            // XXX:
            if (!hasProperty(two)) {
                String var = "order" + m_order++;
                secondElement = ":" + var;
                setParameter(var, orderTwo);
                if (orderOne != null) {
                    Root root = getSession().getRoot();
                    ObjectType typeOne = getTypeInternal().getProperty
                        (unalias(Path.get(orderOne))).getType();
                    if (!root.getObjectType("global.String").equals
                        (typeOne)) {
                        // this means that there is going to be a type conflict
                        // by the DB so we prevent it here
                        throw new PersistenceException("type mismatch");
                    }
                }
            }
        }

        addOrder("case when (" + orderOne + " is null) then " +
                 secondElement + " else " + orderOne + " end " + suffix);
    }

    public void clearOrder() {
        m_orders.clear();
        m_order = 0;
    }

    public void setParameter(String parameterName, Object value) {
	m_bindings.put(parameterName, value);
    }


    public Object getParameter(String parameterName) {
	return m_bindings.get(parameterName);
    }

    public void setOption(String optionName, Object value) {
        m_options.put(optionName, value);
    }

    public Object getOption(String optionName) {
        return m_options.get(optionName);
    }


    public void setRange(Integer beginIndex) {
        setRange(beginIndex, null);
    }

    public void setRange(Integer beginIndex, Integer endIndex) {
        if (endIndex != null && endIndex.compareTo(beginIndex) <= 0) {
            throw new PersistenceException
                ("The beginIndex [" + beginIndex + "] must be strictly less " +
                 "than the endIndex [" + endIndex + "]");
        }

        m_offset = new Integer(beginIndex.intValue() - 1);

        if (endIndex != null) {
            m_limit = new Integer(endIndex.intValue() - beginIndex.intValue());
        }
    }


    public Map getPropertyValues() {
        throw new Error("not implemented");
    }


    public void setReturnsUpperBound(int upperBound) {
        m_upperBound = upperBound;
    }


    public void setReturnsLowerBound(int lowerBound) {
        if (lowerBound > 1 || lowerBound < 0) {
            throw new PersistenceException
                ("The lower bound for a given query must be 0 or 1.");
        }
        m_lowerBound = lowerBound;
    }

    public void alias(String fromPrefix, String toPrefix) {
        m_aliases.add(new Alias(fromPrefix, toPrefix));
    }

    public void close() {
        if (m_cursor != null) {
            m_cursor.close();
        }
    }

    public void rewind() {
        if (m_cursor != null) {
            m_cursor.rewind();
        }
    }


    public Object get(String propertyName) {
        Path path = resolvePath(unalias(Path.get(propertyName)));
	try {
	    return m_cursor.get(path);
	} catch (ProtoException e) {
	    throw PersistenceException.newInstance(e);
	}
    }


    public int getPosition() {
        checkCursor();
        return (int) m_cursor.getPosition();
    }

    private class AddPathMapper implements SQLParser.Mapper {
        public Path map(Path path) {
            Path p = unalias(path);
            // XXX: hasProperty(p) does not work because you can't
            // addPath doesn't accept paths starting with
            // Session.LINK_ASSOCIATION
            if (getTypeInternal().getProperty(p) != null) {
                addPath(p, false);
            }
            return resolvePath(p);
        }
    }

    private SQLParser.Mapper m_mapper = new AddPathMapper();

    Path mapAndAddPath(Path p) {
        return m_mapper.map(p);
    }

    String mapAndAddPaths(String s) {
        StringReader reader = new StringReader(s);
        SQLParser p = getParser(s_mapAndAddPath, reader, m_mapper);

        try {
            p.sql();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        return p.getSQL().toString();
    }

    private Expression makeExpr() {
        String[] orders = new String[m_orders.size()];

        for (int i = m_orders.size() - 1; i >= 0; i--) {
            String order = (String) m_orders.get(i);
            orders[i] = mapAndAddPaths(order);
        }

        Expression filter = m_filter.makeExpression(this, m_bindings);

        // can't start finalizing expr until all paths have been added
        Expression expr = m_expr;

        if (filter != null) {
            expr = new com.redhat.persistence.oql.Filter
                (expr, filter);
        }

        for (int i = orders.length - 1; i >= 0; i--) {
            expr = new Sort(expr, new Static(orders[i], m_bindings));
        }

        if (m_offset != null) {
            expr = new Offset(expr, new Literal(m_offset));
        }

        if (m_limit != null) {
            expr = new Limit(expr, new Literal(m_limit));
        }

        return expr;
    }

    private void checkCursor() {
        if (m_cursor == null) {
	    try {
	        Profiler.startOp("DB");
		m_cursor = execute(m_signature, makeExpr());
	    } catch (ProtoException e) {
		throw PersistenceException.newInstance(e);
	    } finally {
	        Profiler.stopOp("DB");
	    }
        }
    }

    protected Cursor execute(Signature sig, Expression expr) {
        Cursor cursor = new DataSet(m_pssn, sig, expr).getCursor();
        cursor.setOptions(m_options);
        return cursor;
    }

    public boolean next() {
        checkCursor();
	if (m_cursor.isClosed()) {
	    return false;
	}

        int pre = getPosition();

	boolean result;
	try {
	    Profiler.startOp("DB");
	    result = m_cursor.next();
	} catch (ProtoException e) {
	    throw PersistenceException.newInstance(e);
	} finally {
	    Profiler.stopOp("DB");
	}

        if (result) {
            if (getPosition() == m_upperBound) {
                if (m_cursor.next()) {
                    throw new PersistenceException
                        ("cursor exceeded upper bound");
                }
            }
        } else {
            if (pre < m_lowerBound) {
                throw new PersistenceException
                    ("cursor failed to meet lower bound");
            }
        }

        return result;
    }

    public long size() {
	try {
            // can not use checkCursor() because then we can't add filters
            // after calls to size
            if (m_cursor == null) {
                return new DataSet
                    (m_pssn, m_signature, makeExpr()).size();
            } else {
                return m_cursor.getDataSet().size();
            }
	} catch (ProtoException e) {
	    throw PersistenceException.newInstance(e);
	}
    }

    private class UnaliasMapper implements SQLParser.Mapper {
        public Path map(Path path) {
            return unalias(path);
        }
    }

    private SQLParser.Mapper m_unaliaser = new UnaliasMapper();

    String unalias(String expr) {
        if (expr == null) { return null; }
        StringReader reader = new StringReader(expr);
        SQLParser p = getParser(s_unalias, reader, m_unaliaser);

        try {
            p.sql();
        } catch (ParseException e) {
            s_log.warn("Could not parse SQL: " + expr);
            throw new IllegalArgumentException(e.getMessage());
        }

        return p.getSQL().toString();
    }

    Path unalias(Path path) {
	if (s_log.isDebugEnabled()) {
	    s_log.debug("External Path: " + path);
	    s_log.debug("Aliases: " + m_aliases.toString());
	}

        String str = path.getPath();

        final int index = str.indexOf(".link.");
        if (index != -1) {
            str = str.substring(0, index)
                + PDL.LINK + "." + str.substring(index + 6);
        }
        path = Path.get(str);

        Path result = path;

        for (Iterator it = m_aliases.iterator(); it.hasNext(); ) {
            Alias alias = (Alias) it.next();
            if (alias.isMatch(path)) {
		if (s_log.isDebugEnabled()) {
                    s_log.debug("matched " + alias);
		}
                Path candidate = alias.unalias(path);
                if (hasProperty(candidate)) {
                    result = candidate;
                    break;
                }

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Candidate " + candidate + " doesn't exist.");
                }
            } else {
		if (s_log.isDebugEnabled()) {
		    s_log.debug("didn't match " + alias);
		}
            }
        }

	if (s_log.isDebugEnabled()) {
	    s_log.debug("Internal Path: " + result);
	}

        return result;
    }

    private static class Alias {

        private Path m_from;
        private Path m_to;

        public Alias(String from, String to) {
            Assert.assertNotEmpty(from, "from");
            Assert.assertNotEmpty(to, "to");

            m_from = Path.get(from);
            m_to = Path.get(to);
        }

        private static final boolean isWildcard(Path path) {
            return path.getParent() == null && path.getName().equals("*");
        }

        public boolean isMatch(Path path) {
            if (isWildcard(m_from)) { return true; }
            if (m_from.getParent() == null) { return m_from.equals(path); }
            while (path.getParent() != null) {
                path = path.getParent();
            }
            return m_from.getParent().equals(path);
        }

        public Path unalias(Path path) {
            if (isWildcard(m_from) && isWildcard(m_to)) {
                return path;
            } else if (isWildcard(m_from) && !isWildcard(m_to)) {
                if (m_to.getParent() != null) {
                    return Path.add(m_to.getParent(), path);
                } else {
                    throw new IllegalStateException(this + " " + path);
                }
            } else if (!isWildcard(m_from) && isWildcard(m_to)) {
                return path.getRelative(m_from);
            } else {
                try {
                    return Path.add(m_to, path.getRelative(m_from));
                } catch (RuntimeException e) {
                    throw new PersistenceException(this + " " + path, e);
                }
            }
        }

        public String toString() {
            return m_from + " --> " + m_to;
        }

    }

}
