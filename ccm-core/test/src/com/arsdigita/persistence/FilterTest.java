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

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * DataQueryTest
 *
 * This class tests DataQuery, using data contained in
 * //enterprise/infrastructure/dev/persistence/sql/data-query-test.sql
 *
 *  This data must be loaded as a precondition of this test running.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #19 $ $Date: 2004/08/16 $
 */
public class FilterTest extends PersistenceTestCase {
    

    private static Logger s_log =
        Logger.getLogger(FilterTest.class.getName());

    public FilterTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/DataQuery.pdl");
        load("com/arsdigita/persistence/testpdl/static/DataQueryExtra.pdl");
        load("com/arsdigita/persistence/testpdl/static/DataOperation.pdl");
        load("com/arsdigita/persistence/testpdl/static/DataOperationExtra.pdl");
        super.persistenceSetUp();
    }


    /**
     *  This tests creating a filter with Filter.simple
     */
    public void testFilterSimple() {

        // this is tested all over because it is used when DataQuery
        // creates a new filter.  But, let's try it again anyway
        DataQuery query = getDefaultQuery();
        long fullSize = query.size();

        FilterFactory factory = query.getFilterFactory();
        Filter filter = query.addFilter
            (factory.simple("priority < :lowerPriority"));
        filter.set("lowerPriority", "6");
        assertTrue("adding a filter should lower the number of returned" +
                   " results. In FilterSimple()",
                   fullSize > query.size());

    }


    /**
     *  This tests the FilterImpl.equals and FilterFactory.equals methods
     */
    public void testFilterEquals() {
        DataQuery query = getDefaultQuery();
        long fullSize = query.size();

        FilterFactory factory = query.getFilterFactory();
        query.addFilter(factory.equals("priority", new Integer(6)));
        assertTrue("adding a filter should lower the number of returned results." +
               " in FilterEquals ", + fullSize > query.size());


        // test the convenience method
        query = getDefaultQuery();
        query.addEqualsFilter("priority", new Integer(6));
        assertTrue("adding a filter should lower the number of returned results." +
               " in addEqualsFilter ", + fullSize > query.size());
    }

    public void testFilterEqualsFunction() {
        DataQuery query = getDefaultQuery();
        query.addEqualsFilter("upper(action)", "read");
        assertEquals(0, query.size());
        query.reset();
        query.addEqualsFilter("lower(action)", "read");
        assertEquals(5, query.size());
    }

    /**
     *  This tests the FilterImpl.notEquals and FilterFactory.notEquals methods
     */
    public void testFilterNotEquals() {
        DataQuery query = getDefaultQuery();
        long fullSize = query.size();

        FilterFactory factory = query.getFilterFactory();
        query.addFilter(factory.notEquals("priority", new Integer(3)));
        assertTrue("adding a filter should lower the number of returned results." +
               " in FilterNotEquals ", + fullSize > query.size());

        query = getDefaultQuery();
        query.addNotEqualsFilter("priority", new Integer(3));
        assertTrue("adding a filter should lower the number of returned results." +
               " in addNotEqualsFilter ", + fullSize > query.size());
    }


    /**
     *  This tests the FilterImpl.lessThan and FilterFactory.lessThan methods
     */
    public void testFilterLessThan() {
        DataQuery query = getDefaultQuery();
        long fullSize = query.size();

        FilterFactory factory = query.getFilterFactory();
        query = getDefaultQuery();
        query.addFilter(factory.lessThan("priority", new Integer(3), true));
        assertTrue("adding a filter should lower the number of returned results." +
               " in FilterLessThan no matter if the last param is true or " +
               " false...in true and Factory", fullSize > query.size());

        query = getDefaultQuery();
        query.addFilter(factory.lessThan("priority", new Integer(3), false));
        assertTrue("adding a filter should lower the number of returned results." +
               " in FilterLessThan no matter if the last param is true or " +
               " false...in false and Factory", fullSize > query.size());

        query = getDefaultQuery();
        query.addFilter(factory.lessThan("priority", null, true));
        assertTrue("when the allIfNull is true, it should return all results. " +
               "In Factory", fullSize == query.size());

        query = getDefaultQuery();
        query.addFilter(factory.lessThan("priority", null, false));
        assertTrue("when the allIfNull is false, it should return all results. " +
               "In Factory", 0 == query.size());

    }


    /**
     *  This tests the FilterImpl.greaterThan and FilterFactory.greaterThan
     *  methods
     */
    public void testFilterGreaterThan() {

        DataQuery query = getDefaultQuery();
        long fullSize = query.size();

        FilterFactory factory = query.getFilterFactory();
        query = getDefaultQuery();
        query.addFilter(factory.greaterThan("priority", new Integer(3), true));
        assertTrue("adding a filter should lower the number of returned results." +
               " in FiltergreaterThan no matter if the last param is true or " +
               " false...in true and Factory", fullSize > query.size());

        query = getDefaultQuery();
        query.addFilter(factory.greaterThan("priority", new Integer(3), false));
        assertTrue("adding a filter should lower the number of returned results." +
               " in FiltergreaterThan no matter if the last param is true or " +
               " false...in false and Factory", fullSize > query.size());

        query = getDefaultQuery();
        query.addFilter(factory.greaterThan("priority", null, true));
        assertTrue("when the allIfNull is true, it should return all results. " +
               "In Factory", fullSize == query.size());

        query = getDefaultQuery();
        query.addFilter(factory.greaterThan("priority", null, false));
        assertTrue("when the allIfNull is false, it should return all results. " +
               "In Factory", 0 == query.size());

        // let's make sure that lessThan, greaterThan, and equals are
        // doing different things
        query = getDefaultQuery();
        query.addFilter(factory.greaterThan("priority", new Integer(3), false));
        long size = query.size();

        query = getDefaultQuery();
        query.addFilter(factory.lessThan("priority", new Integer(3), false));
        size += query.size();

        query = getDefaultQuery();
        query.addFilter(factory.equals("priority", new Integer(3)));
        assertTrue("adding equals, less than, and greater than should be all " +
               "results.", size + query.size() == fullSize);
    }


    /**
     *  This tests the FilterImpl.startsWith and FilterFactory.startsWith
     *  methods
     */
    public void testFilterStartsWith() {
        DataQuery query = getDefaultQuery();
        long fullSize = query.size();

        FilterFactory factory = query.getFilterFactory();
        query = getDefaultQuery();
        query.addFilter(factory.startsWith("description", "Read", true));
        assertTrue("adding a filter should lower the number of returned results." +
               " in FilterstartsWith no matter if the last param is true or " +
               " false...in true and Factory", fullSize > query.size());
        assertTrue("adding a filter should restrict the results, not eliminate " +
               "them.", query.size() > 0);

        query = getDefaultQuery();
        query.addFilter(factory.startsWith("description", "Read", false));
        assertTrue("adding a filter should lower the number of returned results." +
               " in FilterstartsWith no matter if the last param is true or " +
               " false...in false and Factory", fullSize > query.size());
        assertTrue("adding a filter should restrict the results, not eliminate " +
               "them.", query.size() > 0);

        query = getDefaultQuery();
        query.addFilter(factory.startsWith("description", null, true));
        assertTrue("when the allIfNull is true, it should return all results. " +
               "In Factory", fullSize == query.size());

        query = getDefaultQuery();
        query.addFilter(factory.startsWith("description", null, false));
        assertTrue("when the allIfNull is false, it should return all results. " +
               "In Factory", 0 == query.size());

        // let's make sure that it does negative stuff as well
        query = getDefaultQuery();
        query.addFilter(factory.startsWith("description", "item", false));
        assertTrue("when looking for an item that does not exist, it should " +
               "return no rows", query.size() == 0);

        query = getDefaultQuery();
        query.addFilter(factory.startsWith("description", "item", false));
        assertTrue("when looking for an item that does not exist, it should " +
               "return no rows", query.size() == 0);
    }


    /**
     *  This tests the FilterImpl.endsWith and FilterFactory.endsWith
     *  methods
     */
    public void testFilterEndsWith() {
        DataQuery query = getDefaultQuery();
        long fullSize = query.size();

        FilterFactory factory = query.getFilterFactory();
        query = getDefaultQuery();
        query.addFilter(factory.endsWith("description", "14", true));
        assertTrue("adding a filter should lower the number of returned results." +
               " in FilterendsWith no matter if the last param is true or " +
               " false...in true and Factory", fullSize > query.size());
        assertTrue("adding a filter should restrict the results, not eliminate " +
               "them", query.size() > 0);

        query = getDefaultQuery();
        query.addFilter(factory.endsWith("description", "14", false));
        assertTrue("adding a filter should lower the number of returned results." +
               " in FilterendsWith no matter if the last param is true or " +
               " false...in false and Factory", fullSize > query.size());
        assertTrue("adding a filter should restrict the results, not eliminate " +
               "them.", query.size() > 0);

        query = getDefaultQuery();
        query.addFilter(factory.endsWith("description", null, true));
        assertTrue("when the allIfNull is true, it should return all results. " +
               "In Factory", fullSize == query.size());

        query = getDefaultQuery();
        query.addFilter(factory.endsWith("description", null, false));
        assertTrue("when the allIfNull is false, it should return all results. " +
               "In Factory", 0 == query.size());

        // let's make sure that it does negative stuff as well
        query = getDefaultQuery();
        query.addFilter(factory.endsWith("description", "item", false));
        assertTrue("when looking for an item that does not exist, it should " +
               "return no rows", query.size() == 0);

        query = getDefaultQuery();
        query.addFilter(factory.endsWith("description", "item", false));
        assertTrue("when looking for an item that does not exist, it should " +
               "return no rows", query.size() == 0);
    }


    /**
     *  This tests the FilterImpl.contains and FilterFactory.contains
     *  methods
     */
    public void testFilterContains() {
        DataQuery query = getDefaultQuery();
        long fullSize = query.size();

        FilterFactory factory = query.getFilterFactory();
        query = getDefaultQuery();
        query.addFilter(factory.contains("description", "ead", true));
        assertTrue("adding a filter should lower the number of returned results." +
               " in Filtercontains no matter if the last param is true or " +
               " false...in true and Factory", fullSize > query.size());
        assertTrue("adding a filter should restrict the results, not eliminate " +
               "them.", query.size() > 0);

        query = getDefaultQuery();
        query.addFilter(factory.contains("description", "ead", false));
        assertTrue("adding a filter should lower the number of returned results." +
               " in Filtercontains no matter if the last param is true or " +
               " false...in false and Factory", fullSize > query.size());
        assertTrue("adding a filter should restrict the results, not eliminate " +
               "them.", query.size() > 0);

        query = getDefaultQuery();
        query.addFilter(factory.contains("description", null, true));
        assertTrue("when the allIfNull is true, it should return all results. " +
               "In Factory", fullSize == query.size());

        query = getDefaultQuery();
        query.addFilter(factory.contains("description", null, false));
        assertTrue("when the allIfNull is false, it should return all results. " +
               "In Factory", 0 == query.size());

        // let's make sure that it does negative stuff as well
        query = getDefaultQuery();
        query.addFilter(factory.endsWith("description", "abcd", false));
        assertTrue("when looking for an item that does not exist, it should " +
               "return no rows", query.size() == 0);

        query = getDefaultQuery();
        query.addFilter(factory.endsWith("description", "abcd", false));
        assertTrue("when looking for an item that does not exist, it should " +
               "return no rows", query.size() == 0);
    }


    /**
     *  This tests ORing filters together
     */
    public void testCompoundFilterOr() {
        DataQuery query = getDefaultQuery();
        FilterFactory factory = query.getFilterFactory();
        CompoundFilter filter = factory.or()
            .addFilter("priority < :upperPriority")
            .addFilter("priority > :lowerPriority");
        filter.set("upperPriority", "-3");
        filter.set("lowerPriority", "-3");
        query.addFilter(filter);
        assertTrue("The query with an OR should return results",
               query.size() > 0);

        // now test the or with ANDs in the conditions
        query = getDefaultQuery();
        filter = factory.or().addFilter("priority < :lowerPriority and " +
                                        "action = :lowerAction");
        filter.set("lowerPriority", "4");
        filter.set("lowerAction", "read");
        query.addFilter(filter);
        long lowerSize = query.size();

        filter = filter.addFilter("priority > :upperPriority and " +
                                  "action = :upperAction");
        filter.set("upperPriority", "5");
        filter.set("upperAction", "write");
        query.clearFilter();
        query.addFilter(filter);
        long bothSize = query.size();

        query = getDefaultQuery();
        filter = factory.or().addFilter("priority > :upperPriority and " +
                                        "action = :upperAction");
        filter.set("upperPriority", "5");
        filter.set("upperAction", "write");
        query.addFilter(filter);
        long upperSize = query.size();

        assertTrue("The (x and y) or (w and z) did not return the correct value",
               lowerSize + upperSize == bothSize);
    }

    public void testCompoundFilterOrSameName() {
        DataQuery q = getDefaultQuery();
        FilterFactory ff = q.getFilterFactory();
        CompoundFilter or = ff.or();
        or.addFilter(ff.equals("action", "create"));
        or.addFilter(ff.equals("action", "delete"));
        q.addFilter(or);
        assertEquals("must have 10 rows in query", 10, q.size());
    }

    public void testCompoundFilterOrSameBind() {
        DataQuery q = getDefaultQuery();
        FilterFactory ff = q.getFilterFactory();
        CompoundFilter or = ff.or();
        Filter f1 = ff.simple("action = :action");
        Filter f2 = ff.simple("action = :action");
        f1.set("action", "create");
        f2.set("action", "delete");
        or.addFilter(f1);
        or.addFilter(f2);
        q.addFilter(or);
        assertEquals("must have 10 rows in query", 10, q.size());
    }

    /**
     *  This tests ANDing filters together
     */
    public void testCompoundFilterAnd() {
        // we just want to make sure that adding two filters together
        // gives the same results and creating them both at once
        DataQuery query = getDefaultQuery();
        FilterFactory factory = query.getFilterFactory();
        Filter filter = factory.and()
            .addFilter("priority <= :upperPriority")
            .addFilter("priority >= :lowerPriority");
        filter.set("upperPriority", "3");
        filter.set("lowerPriority", "3");
        query.addFilter(filter);
        long selectedItemCount = query.size();
        assertTrue("The query with an AND should return results",
               selectedItemCount > 0);

        query = getDefaultQuery();
        filter = query.addFilter("priority <= :upperPriority and priority >= " +
                                 ":lowerPriority");
        filter.set("upperPriority", "3");
        filter.set("lowerPriority", "3");
        assertTrue("two filters at once should be the same as two ANDed filters",
               selectedItemCount == query.size());

    }


    /**
     *  This tests using filters with null values
     */
    public void testFiltersWithNull() {
        // I want to make sure that the system does not have a problem
        // when we use nulls
        DataOperation operation = getSession().retrieveDataOperation
            ("examples.DataOperationWithBindVariablesAndNull");
        operation.setParameter("description", null);
        operation.setParameter("priority", new Integer(3));
        operation.execute();

        DataQuery query = getDefaultQuery();
        long totalItems = query.size();

        FilterFactory factory = query.getFilterFactory();
        query.addFilter(factory.equals("description", null));
        assertTrue("when only selected a small portion of items, the " +
                   "number should be less than the total.",
                   query.size() < totalItems);

        query = getDefaultQuery();
        query.addFilter(factory.notEquals("description", null));
        assertTrue("when only selected a small portion of items, " +
                   "the number should be less than the total.",
                   query.size() < totalItems);
    }


    /**
     *  This tests the setConditions and AddBindings methods in FilterImpl
     */
    public void testFilterSetConditionsAndAddBindings() {

        DataQuery query = getDefaultQuery();
        query.addFilter(FilterImpl.equals("priority", new Integer(3)));
        long size = query.size();
        SimpleFilter f = new SimpleFilter("description = :description");
        Map map = new HashMap();
        map.put("description", "something that will not match");
        f.addBindings(map);
        query.addFilter(f);

        assertTrue("adding a filter on description should narrow the results in " +
               "test setConditions ", size > query.size());

    }


    /**
     *  We want to make sure that it is possible to set a fitler basde on
     *  a date by passing in a java.util.Date object
     */
    public void testFilterOnDates() {
        // uncomment this out when the problem is fixed
        //http://developer.arsdigita.com/acs5/sdm/one-ticket?ticket_id=149713

        DataQuery query = getDefaultQuery();
        long size = query.size();

        java.util.Date date = new java.util.Date();
        long time = date.getTime();

        // now we want the date to be around 1980 so we divide
        // the current time by 3 and set the date
        date.setTime(time/3);

        query.addFilter(query.getFilterFactory().lessThan("actionTime", date, true));
        assertTrue("The size after filtering should be less than the size before " +
               "filtering.  Before: " + size + "; After: " + query.size(),
               query.size() < size && query.size() > 0);

        // let's try out a null value
        query = getDefaultQuery();
        query.addFilter(query.getFilterFactory().lessThan("actionTime", null, true));
        assertTrue("When a < null and true is passed in, the sizes should be the " +
               "same.  Rather, they were " + size + " and " + query.size(),
               size == query.size());


        query = getDefaultQuery();
        query.addFilter(query.getFilterFactory().lessThan("actionTime",
                                                          null, false));
        assertTrue("When a < null and false is passed in, the sizes should be  " +
               "different.  Before: " + size + "; After: " + query.size(),
               size > query.size());
    }


    /**
     *  This tests filters with number bind variables
     */
    public void testFilterWithNumericBindVariables() {

        DataQuery query = getDefaultQuery();
        query.addFilter("priority = :l12").set("l12", new Integer(3));
        assertTrue("binding with an integer should not cause problems.",
               query.size() > 0);
        query = getDefaultQuery();
        query.addFilter("priority = :1").set("1", new Integer(3));
        assertTrue("binding with an integer should not cause problems.",
               query.size() > 0);
    }


    /**
     * This tests subquery expansion in filters.
     **/
    public void testSubQuery() {
        DataQuery query = getDefaultQuery();
        Filter f = query.addFilter("exists (examples.SubQuery)");
        f.set("two", new Integer(2));
        assertTrue("Query must return a row.", query.next());

        query.close();

        query = getDefaultQuery();
        f = query.addFilter("exists (examples.SubQuery)");
        f.set("two", new Integer(1));
        assertTrue("Query must not return a row.",
                   !query.next());
    }


    /**
     *  This tests setting the Filter values to null without
     *  using the correct methods
     */
    public void testNullValues() {

        // first, make some of the values null
        DataQuery query = getDefaultQuery();
        DataOperation operation = SessionManager.getSession()
            .retrieveDataOperation("examples.DataOperationWithBindVariablesAndNull");
        operation.setParameter("priority", new Integer(3));
        operation.setParameter("description", null);
        operation.execute();

        /*
        // I am commenting out the following because
        // of problems with phrases like "upper()" and in-line
        // calls to pl/sql procs
        // Randy Graebner
        // 10/2/2001

        // now get the size for filtering with null
        query.addEqualsFilter("description", null);
        long actualSize = query.size();

        query.clearFilter();
        Filter f = query.addFilter("description = :description");
        f.set("description", null);
        assertTrue("The query should have returned " + actualSize + " but " +
        "instead returned " + query.size(), query.size() == actualSize);

        query.clearFilter();
        f = query.addFilter("description is null");
        assertTrue(query.size() == actualSize);

        query = SessionManager.getSession()
        .retrieveQuery("examples.DataQueryWithMoreBinds");
        query.setParameter("description", null);
        assertTrue("Setting the parameter to null should give you the same " +
        "as having null hard coded", query.size() == actualSize);
        */
    }


    // This tests the method "compare" within the FilterFactory.
    // This test still needs a lot of work
    public void testCompareFilter() {

        DataQuery query = getDefaultQuery();
        DataOperation operation = SessionManager.getSession()
            .retrieveDataOperation("examples.DataOperationWithBindVariablesAndNull");
        operation.setParameter("priority", new Integer(3));
        operation.setParameter("description", null);
        operation.execute();

        query.addFilter("description is null");
        long numberNull = query.size();

        FilterFactory factory = query.getFilterFactory();

        // test the EQUALS
        query = getDefaultQuery();
        Filter filter = query.addFilter(factory.compare("upper(description)",
                                                        FilterFactory.EQUALS,
                                                        ":description"));
        filter.set("description", null);
        assertTrue("The EQUALS filter with null should have returned " +
               numberNull + " but it actually returned " + query.size(),
               numberNull == query.size());

        query = getDefaultQuery();
        filter = query.addFilter(factory.compare("upper(description)",
                                                 FilterFactory.EQUALS,
                                                 ":description"));
        filter.set("description", "Read item 0");
        assertTrue("The EQUALS filter should have returned zero rows.  Instead " +
               "it returned " + query.size(), query.size() == 0);

        query = getDefaultQuery();
        filter = query.addFilter(factory.compare("upper(description)",
                                                 FilterFactory.EQUALS,
                                                 "upper(:description)"));
        filter.set("description", "Read item 0");

        assertTrue("The EQUALS filter should have returned one row.  Instead " +
               "it returned " + query.size(), query.size() == 1);


        query = getDefaultQuery();
        filter = query.addFilter(factory.compare("upper(description)",
                                                 FilterFactory.EQUALS,
                                                 ":description"));
        filter.set("description", "READ ITEM 0");
        assertTrue("The EQUALS filter should have returned one row.  Instead " +
               "it returned " + query.size(), query.size() == 1);


        // test the NOT_EQUALS
        query = getDefaultQuery();
        long totalRows = query.size();

        query = getDefaultQuery();
        query.addFilter("description is not null");
        long numberNotNull = query.size();

        query = getDefaultQuery();
        filter = query.addFilter(factory.compare("upper(description)",
                                                 FilterFactory.NOT_EQUALS,
                                                 ":description"));
        filter.set("description", null);
        assertTrue("The NOT_EQUALS filter with null should have returned " +
               numberNotNull + " but it actually returned " + query.size(),
               numberNotNull == query.size());

        query = getDefaultQuery();
        filter = query.addFilter(factory.compare("upper(description)",
                                                 FilterFactory.NOT_EQUALS,
                                                 ":description"));
        filter.set("description", "Read item 0");
        assertTrue("The NOT_EQUALS filter should have returned " + totalRows +
               ".  " + "Instead it returned " +
               query.size(), query.size() == totalRows);

        query = getDefaultQuery();
        filter = query.addFilter(factory.compare("upper(description)",
                                                 FilterFactory.NOT_EQUALS,
                                                 "upper(:description)"));
        filter.set("description", "Read item 0");
        assertTrue("The NOT_EQUALS filter should have returned " + (totalRows - 1) +
               ".  Instead it returned " + query.size(),
               query.size() == totalRows - 1);


        query = getDefaultQuery();
        filter = query.addFilter(factory.compare("upper(description)",
                                                 FilterFactory.NOT_EQUALS,
                                                 ":description"));
        filter.set("description", "READ ITEM 0");
        assertTrue("The NOT_EQUALS filter should have returned " + (totalRows - 1) +
               ".  Instead it returned " + query.size(),
               query.size() == totalRows - 1);
        /*
        // Still need to test the following filters
        System.out.println(factory.compare("upper(one)",
        FilterFactory.LESS_THAN,
        "upper(two)"));
        System.out.println(factory.compare("upper(one)",
        FilterFactory.GREATER_THAN,
        "upper(two)"));
        System.out.println(factory.compare("upper(one)",
        FilterFactory.LESS_THAN_EQUALS,
        "upper(two)"));
        System.out.println(factory.compare("upper(one)",
        FilterFactory.GREATER_THAN_EQUALS,
        "upper(two)"));
        System.out.println(factory.compare("upper(one)",
        FilterFactory.STARTS_WITH,
        "upper(two)"));
        System.out.println(factory.compare("upper(one)",
        FilterFactory.ENDS_WITH,
        "upper(two)"));

        System.out.println(factory.compare("upper(one)",
        FilterFactory.CONTAINS,
        "upper(two)"));
        */
    }

    public void testFilterWithNVL() {
        DataQuery dq = getDefaultQuery();
        dq.addFilter("nvl('zero', 'zero') = nvl('one', 'one')");
        assertEquals("nvl filter silently failed", 0, dq.size());
    }

    public void testFilterAndOrPrecedence() {
        DataQuery dq = getDefaultQuery();
        dq.addFilter("1 = 1 or 1 = 0");
        dq.addFilter("1 = 0");
        // this should end up parsing as (((1=1) or (1=0)) and (1=0))
        // which is false
        assertEquals("filter should return no rows", 0, dq.size());

        dq = getDefaultQuery();
        dq.addFilter("1 = 0");
        dq.addFilter("1 = 1 or 1 = 0");
        assertEquals("filter should return no rows", 0, dq.size());
    }


    /**
     *  Creates a DataOperation for examples.DataOperation.
     *
     *  @return The DataOperation.
     */
    protected DataQuery getDefaultQuery() {
        DataQuery dq = getSession().retrieveQuery("examples.DataQuery");
        return dq;
    }

}
