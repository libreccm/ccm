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
package com.arsdigita.search.intermedia;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * This class is used to perform a search for content that
 * has been indexed.  Indexing of content is described
 * in <em>Searchable</em> interface
 * documentation.
 *<p>
 * To perform a search, there are two steps:
 * <ol>
 * <li>Specify the search by calling a constructor
 *     to create an object of this class.  This requires
 *     specifying the SQL Select statement that makes up
 *     the search, and the names of the fields that will
 *     be returned.  Pagination of the results can also
 *     be specified.
 * <li>Execute the search by calling method
 *     <code>getPage</code> to return each page of search
 *     results.
 * </ol>
 *<p>
 * The main task is the specification of the SQL Select
 * statement.  The SQL select statement should query the
 * <code>search_content</code> table, and perhaps other
 * joined tables.  The following are the fields in the
 * <code>search_content</code> table:
 *<pre>
 *  object_id           integer primary key,
 *                      references acs_objects (object_id)
 *  object_type         varchar2(100),  -- Same as acs_objects(object_type)
 *  link_text           varchar2(1000),
 *  url_stub            varchar2(100),
 *  summary             varchar2(4000),
 *  xml_content         clob,
 *  raw_content         blob,
 *  language            varchar2(3)
 *</pre>
 *<p>
 * Fields <code>xml_content</code> and <code>raw_content</code>
 * contain the content that is indexed.  Searching for content
 * is done by selecting from these columns using the Oracle
 * "contains" function.  The xml_content field allows using the
 * "within" operator to search for content within specified
 * XML elements.
 *<p>
 * Example.  Suppose an object has the following content indexed:
 *<pre>
 * xml_content:  &lt;Article&gt;
 *                 &lt;title&gt;Market Research&lt;/title&gt;
 *                 &lt;author&gt;Ernest Johnston&lt;/author&gt;
 *               &lt;/Article&gt;
 *
 * raw_content:  Focus groups indicate ambivalence.
 *</pre>
 * and that another table (article_info) has a field (publish_date)
 * which indicates the date the article was published.
 *<p>
 * Then the following SQL statement could be used to search for
 * a query string in the &lt;title&gt; attribute or raw_content field
 * for articles that were published earler than the current date
 * and order the results. '$queryString' symbolizes where the
 * query string would be placed.
 * <pre>
 * select   object_id, link_text, url_stub, summary, score(1)+score(2) as score
 *    from  search_content sc, article_info ai
 *   where  (contains(xml_content, '$queryString within title', 1) > 0
 *              or contains(raw_content, '$queryString', 2) > 0)
 *     and  ai.publish_date < sysdate
 *     and  ai.article_id = sc.object_id
 *   order  by score desc
 * </pre>
 *
 * The columns that would need to be specified to the constructor are:
 * object_id, link_text, url_stub, summary and score.  They query would find
 * the example object if the queryString was "market" or "ambivalence"
 * (respectively through the title attribute and raw_content field) but
 * not if the query string was "Ernest" since the author section is not searched.
 * All attributes in the xml_content field can be searched by not including a
 * "within" clause.
 * Details of of the Oracle interMedia contains, within and score functions
 * are described in
 * <a href="http://www.oradoc.com/ora817/appdev.817/a86030/adx05tx6.htm#1009100">Oracle8i
 * Application Developer's Guide - XML</a> and in
 * <a href="http://www.oradoc.com/ora817/inter.817/a77063/cqoper26.htm#9314">Oracle8i
 * interMedia Text Reference</a>.
 *
 * @see com.arsdigita.search.intermedia.SearchDataQuery
 *
 * @author Jeff Teeters
 * @version 1.0
 **/
public class SearchSpecification {
    public static final String versionId = "$Id: SearchSpecification.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    // Default values
    final private static int DEFAULT_MAXROWS = 1000;
    final private static int DEFAULT_ROWSPERPAGE = 100;

    private String m_sql;                 // Original SQL user specified
    private String[] m_columns;           //legal columns to grab
    private int m_maxRows;
    private int m_rowsPerPage;
    private HashMap m_parameters = new HashMap();

    // Constructors

    /**
     * Create a SearchSpecification object.
     * @param sql Sql select statement to perform the search.  See
     *    example above.
     * @param columns The names of columns returned by the
     *    search.
     * @param maxRows The maximum number of result rows retrieved
     *    by the search.
     * @param rowsPerPage The maximum number of rows per result
     * page returned by method getPage.
     **/
    public SearchSpecification(String sql, String[] columns,
                               int maxRows, int rowsPerPage) {

        m_sql = sql;
        m_columns = columns;
        m_maxRows = maxRows;
        m_rowsPerPage = rowsPerPage;
        assertInvariant();
    }

    /**
     * Create a SearchSpecification object, using the default values
     * for maxRows and rowsPerPage.
     * @param sql Sql select statement to perform the search.  See
     *    example above.
     * @param columns The names of columns returned by the
     *    search.
     **/
    public SearchSpecification(String sql, String[] columns) {
        this(sql, columns, DEFAULT_MAXROWS, DEFAULT_ROWSPERPAGE);
    }

    /**
     * Allows a user to bind a parameter within the query.
     *
     * @param name The name of the parameter to bind
     * @param value The value to assign to the parameter
     */
    public void setParameter(String name, Object value) {
        m_parameters.put(name, value);
    }

    /**
     * Allows a caller to get a parameter value for a parameter that
     * has already been set
     *
     * @param parameterName The name of the parameter to retrieve
     * @return This returns the object representing the value of the
     * parameter specified by the name or "null" if the parameter value
     * has not yet been set.
     */
    public Object getParameter(String parameterName) {
        return m_parameters.get(parameterName);
    }


    /**
     * Set the Sql select statement and columns used to do a search.
     * @param sql Sql select statement to perform the search.  See
     *    example above.
     * @param columns The names of columns returned by the
     *    search.
     **/
    public void setSelect(String sql, String[] columns) {
        validateSQL(sql);
        validateColumns(columns);
        m_sql = sql;
        m_columns = columns;
    }


    /**
     * Set the maximum number of result rows retrieved
     * by the search.
     * @param maxRows The maximum number of result rows
     *    retrieved by the search.
     **/
    public void setMaxResultRows(int maxRows) {
        validateMaxRows(maxRows);
        m_maxRows = maxRows;
    }


    /**
     * Set the maximum number of result rows retrieved
     * by the search.
     * @param rowsPerPage The maximum number of rows per result
     *    page returned by method getPage.
     **/
    public void setRowsPerPage(int rowsPerPage) {
        validateRowsPerPage(rowsPerPage);
        m_rowsPerPage = rowsPerPage;
    }

    private void assertInvariant() {
        // does some minimal checking of the parameter settings
        validateMaxRows(m_maxRows);
        validateRowsPerPage(m_rowsPerPage);
        validateSQL(m_sql);
        validateColumns(m_columns);
    }

    private void validateColumns(String[] columns) {
        if (null == columns) {
            throw new IllegalArgumentException("Columns cannot be null");
        }
    }

    private void validateRowsPerPage(int rowsPerPage) {
        if (rowsPerPage <= 0) {
            throw new IllegalStateException("rowsPerPage must be greater than 0, " +
                            "Specified value was: " + m_rowsPerPage);
        }
    }

    private void validateMaxRows(int maxRows) {
        if (maxRows <= 0) {
            throw new IllegalStateException("maxRows must be greater than 0, " +
                            "Specified value was: " + m_maxRows);
        }
    }

    private void validateSQL(String sql) {
        // make sure keywords "select" and "from" in SQL statement
        String lc_sql = sql.toLowerCase();
        int pos_select = lc_sql.indexOf("select");
        int pos_from = lc_sql.indexOf("from");
        if (pos_select == -1 || pos_from == -1) {
            throw new IllegalStateException("Invalid SQL statement. " +
                            "Must be a complete SELECT statement. " +
                            "Specified SQL was: " + m_sql);
        }
    }


    /**
     * Get the Sql select statement used to do a search.
     **/
    public String getSelect() {
        return m_sql;
    }

    /**
     * Get the columns names that the sql select returns.
     **/
    public String[] getColumns() {
        return m_columns;
    }

    /**
     * Get the maximum number of result rows that can be
     * retrieved by the search.
     **/
    public int getMaxResultRows() {
        return m_maxRows;
    }

    /**
     * Get the maximum number of rows per result
     *    page returned by method getPage.
     **/
    public int getRowsPerPage() {
        return m_rowsPerPage;
    }


    /**
     * Build a new sql select statement, that is reformatted
     * to select the specified page.  This method would normally
     * not be used by an application developer.  It is a helper
     * function for methods in this class or classes that extend it
     * (e.g. SimpleSearchSpecification).
     * <p>
     * Example usage.<br>
     * Input:
     * <pre>
     * select id, name from result_info order by substr(name,2,3)
     * </pre>
     *<p>
     * Output:
     * <pre>
     * select * from
     * (select temp_view.*, rownum as temp_rownum from
     * (select id, name from result_info order by substr(name,2,3)) temp_view)
     * where temp_rownum >= firstRow and temp_rownum <= lastRow
     * </pre>
     *<p>
     * Notes:
     * <ul>
     * <li>String "temp_" in the identifiers are replaced by an unlikely
     * string to reduce chance of name conflicts.</li>
     *
     * <li>The first condition temp_rownum >= firstRow is left out if
     * firstRow is 1.</li>
     *
     * <li>The lastRow is set to one more than the actual last row on the page
     * (unless its the last page) so the calling routine can figure out
     * if there is another page of search results.</li>
     * </ul>
     **/

    protected String reformatSqlForPage(int page) {
        // not needed, since setRange exists
        /*String unlikely = "l1l1ouevuz_";
          String temp_rownum = unlikely + "_rownum";
          String temp_view = unlikely + "_view";

          int firstRow, lastRow;

          // Safety check
          if (page <= 0) {
          throw new Error("Invalid page specified, must be > 0. " +
          "Value specified was: " + page);
          }

          firstRow = (page - 1) * m_rowsPerPage + 1;
          lastRow = Math.min(page * m_rowsPerPage, m_maxRows);
          // Advance lastRow if not at the end so the calling routine can
          // know if there is another page of results.
          if (lastRow < m_maxRows) {
          lastRow++;
          }

          // Setup string for first condition if needed
          String condition_1;
          if (firstRow > 1) {
          condition_1 = temp_rownum + " >= " + firstRow + " and ";
          } else {
          condition_1 = "";
          }

          // Build reformatted search string
          String result = "select * from\n" +
          "(select " + temp_view + ".*, rownum as " + temp_rownum + " from\n" +
          "(" + m_sql + ") " + temp_view + ")\n" +
          "where " + condition_1 + temp_rownum + " <= " + lastRow;
          return result;*/
        return m_sql;
    }

    /**
     * Execute a search, returning a page of search results.  If there is a
     * "next page" of results after the returned page, the number of rows
     * returned will be the number of rows per page <em>plus one</em>.
     * This allows determining if a "next page" link should be generated by
     * counting the number of rows returned; i.e. if the total is greater than
     * rowsPerPage) then there is a next page, otherwise there is not.
     * A row that flags the presence of a next page, should <em>not</em> be displayed
     * to the user because it will be the first row returned on the next page.
     *
     * @param page The page of search results to retrieve
     *  (page==1 means first page).
     **/

    public DataQuery getPage(final int page) {

        // decide on row range
        if (page <= 0) {
            throw new IllegalArgumentException("Invalid page specified, must be > 0. " +
                            "Value specified was: " + page);
        }

        final int firstRow = (page - 1) * m_rowsPerPage + 1;
        int lastRow = Math.min(page * m_rowsPerPage, m_maxRows);
        // Advance lastRow if not at the end so the calling routine can
        // know if there is another page of results.
        if (lastRow < m_maxRows) {
            lastRow++;
        }

        // Reformat SQL to specify the rows on the page
        //String sql_for_page = reformatSqlForPage(page);
        String sql_for_page = m_sql;

        SearchDataQuery dq = new com.arsdigita.search.SearchDataQuery(
            SessionManager.getSession(),
            sql_for_page, m_columns);

        dq.setRange(new Integer(firstRow), new Integer(lastRow));

        // bind variables
        for (Iterator it = m_parameters.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            String key = (String) me.getKey();
            Object value = me.getValue();
            dq.setParameter(key, value);
        }
        return dq;
    }
}
