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

import com.arsdigita.persistence.metadata.Utilities;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * DataQueryImplTest
 *
 * This class tests DataQueryImpl, using data contained in
 * //enterprise/infrastructure/dev/persistence/sql/data-query-test.sql
 *
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #25 $ $Date: 2004/08/16 $
 */
public class DataQueryImplTest extends DataQueryTest {

    public final static String versionId = "$Id: DataQueryImplTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static Logger s_log =
        Logger.getLogger(DataQueryImplTest.class.getName());

    private static final int NUM_WRITE_ACTIONS = 5;
    public DataQueryImplTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/DataQuery.pdl");
        load("com/arsdigita/persistence/testpdl/static/DataQueryExtra.pdl");
        load("com/arsdigita/persistence/testpdl/static/DataOperation.pdl");
        load("com/arsdigita/persistence/testpdl/static/DataOperationExtra.pdl");
        super.persistenceSetUp();
    }

    protected void persistenceTearDown() {
        super.persistenceTearDown();
    }

    /**
     *  Tests the ordering capability of DataQuery.
     *  Checks forward, reverse, and multiple field ordering.
     *
     */
    public void testSetOrder() {
        DataQuery dq = getDefaultQuery();
        final String ORDER_FIELD = "id";
        final String PRIORITY = "priority";

        // twice to see if something funny happens
        dq.addOrder(ORDER_FIELD);
        dq.addOrder(ORDER_FIELD);
        assertTrue( "Should be several items in this query set!", dq.next() );
        BigDecimal priorValue = (BigDecimal) dq.get(ORDER_FIELD);

        while ( dq.next() ) {
            final BigDecimal currentValue = (BigDecimal) dq.get(ORDER_FIELD);
            assertTrue("Query was retrieved out of order.",
                   priorValue.compareTo( currentValue ) < 0 );

            priorValue = currentValue;

        }
        dq = null;

        DataQuery descQuery = getDefaultQuery();
        descQuery.addOrder("id desc");
        assertTrue( "Should be several items in this query set!", descQuery.next() );
        priorValue = (BigDecimal) descQuery.get(ORDER_FIELD);

        while ( descQuery.next() ) {
            final BigDecimal currentValue = (BigDecimal) descQuery.get(ORDER_FIELD);
            assertTrue("Query was retrieved out of order.",
                   priorValue.compareTo( currentValue ) > 0 );

            priorValue = currentValue;

        }
        descQuery = null;

        DataQuery multipleColumnOrder = getDefaultQuery();
        multipleColumnOrder.addOrder("action desc, priority asc");
        assertTrue("Should be several items in this query set!",
               multipleColumnOrder.next() );

        BigDecimal priorPriority = (BigDecimal) multipleColumnOrder.get("priority");
        String priorAction = (String) multipleColumnOrder.get("action");

        while (multipleColumnOrder.next()) {
            final BigDecimal currentPriority =
                (BigDecimal) multipleColumnOrder.get("priority");
            final String currentAction = (String) multipleColumnOrder.get("action");
            assertTrue("Action order wrong!",
                   priorAction.compareTo( currentAction ) >= 0 );
            if ( priorAction.equals(currentAction) ) {
                assertTrue("Priority order wrong! " + priorPriority.toString() +
                       " vs " + currentPriority.toString(),
                       priorPriority.compareTo(currentPriority) <= 0 );

            }

            priorPriority = currentPriority;
            priorAction = currentAction;

        }
    }


    /**
     *  Tests the ordering capability of DataQuery.
     *  Checks forward, reverse, and multiple field ordering.
     *
     */
    public void testAddOrder() {
        DataQuery dq = getDefaultQuery();
        final String ORDER_FIELD = "id";
        final String PRIORITY = "priority";

        dq.addOrder(ORDER_FIELD);
        assertTrue( "Should be several items in this query set!", dq.next() );
        BigDecimal priorValue = (BigDecimal) dq.get(ORDER_FIELD);

        while ( dq.next() ) {
            final BigDecimal currentValue = (BigDecimal) dq.get(ORDER_FIELD);
            assertTrue("Query was retrieved out of order.",
                   priorValue.compareTo( currentValue ) < 0 );

            priorValue = currentValue;

        }
        dq = null;

        DataQuery descQuery = getDefaultQuery();
        descQuery.addOrder("id desc");
        assertTrue( "Should be several items in this query set!", descQuery.next() );
        priorValue = (BigDecimal) descQuery.get(ORDER_FIELD);

        while ( descQuery.next() ) {
            final BigDecimal currentValue = (BigDecimal) descQuery.get(ORDER_FIELD);
            assertTrue("Query was retrieved out of order.",
                   priorValue.compareTo( currentValue ) > 0 );

            priorValue = currentValue;

        }
        descQuery = null;

        DataQuery multipleColumnOrder = getDefaultQuery();
        multipleColumnOrder.addOrder("action desc, priority asc");
        assertTrue("Should be several items in this query set!",
               multipleColumnOrder.next() );

        BigDecimal priorPriority = (BigDecimal) multipleColumnOrder.get("priority");
        String priorAction = (String) multipleColumnOrder.get("action");

        while (multipleColumnOrder.next()) {
            final BigDecimal currentPriority =
                (BigDecimal) multipleColumnOrder.get("priority");
            final String currentAction = (String) multipleColumnOrder.get("action");
            assertTrue("Action order wrong!",
                   priorAction.compareTo( currentAction ) >= 0 );
            if ( priorAction.equals(currentAction) ) {
                assertTrue("Priority order wrong! " + priorPriority.toString() +
                       " vs " + currentPriority.toString(),
                       priorPriority.compareTo(currentPriority) <= 0 );

            }

            priorPriority = currentPriority;
            priorAction = currentAction;

        }

        //finally, let's test to make sure that adding two items together
        // is the same as adding them separately
        DataQuery singleOrder = getDefaultQuery();
        singleOrder.addOrder(ORDER_FIELD + " desc, " + PRIORITY + " asc");
        DataQuery multiOrder = getDefaultQuery();
        multiOrder.addOrder(ORDER_FIELD + " desc");
        multiOrder.addOrder(PRIORITY + " asc");

        // the two should be identical
        assertTrue(singleOrder.size() == multiOrder.size());
        while (multiOrder.next() && singleOrder.next()) {
            assertTrue("The MultiOrder ID did not match the single Order ID",
                   multiOrder.get("id").equals(singleOrder.get("id")));
        }
        multiOrder.close();
        singleOrder.close();

    }


    /**
     *  Tests the ordering capability of DataQuery.
     *  Checks forward, reverse, and multiple field ordering.
     *
     */
    public void testAddOrderWithNull() {
        DataQuery dq = getDefaultQuery();
        final String ORDER_FIELD = "id";
        final String PRIORITY = "priority";

        dq.addOrderWithNull(null, ORDER_FIELD, true);
        assertTrue( "Should be several items in this query set!", dq.next() );
        BigDecimal priorValue = (BigDecimal) dq.get(ORDER_FIELD);

        while ( dq.next() ) {
            final BigDecimal currentValue = (BigDecimal) dq.get(ORDER_FIELD);
            assertTrue("Query was retrieved out of order.",
                   priorValue.compareTo( currentValue ) < 0 );

            priorValue = currentValue;

        }


        dq = getDefaultQuery();
        dq.addOrderWithNull(null, ORDER_FIELD, false);
        assertTrue( "Should be several items in this query set!", dq.next() );
        priorValue = (BigDecimal) dq.get(ORDER_FIELD);

        while ( dq.next() ) {
            final BigDecimal currentValue = (BigDecimal) dq.get(ORDER_FIELD);
            assertTrue("Query was retrieved out of order.",
                   priorValue.compareTo( currentValue ) > 0 );

            priorValue = currentValue;

        }
        dq = null;

        DataQuery descQuery = getDefaultQuery();
        descQuery.addOrderWithNull(null, new Integer(4), true);
        descQuery.addOrderWithNull(null, "id", false);
        assertTrue( "Should be several items in this query set!", descQuery.next() );
        priorValue = (BigDecimal) descQuery.get(ORDER_FIELD);

        while ( descQuery.next() ) {
            final BigDecimal currentValue = (BigDecimal) descQuery.get(ORDER_FIELD);
            assertTrue("Query was retrieved out of order.",
                   priorValue.compareTo( currentValue ) > 0 );

            priorValue = currentValue;

        }
        descQuery = null;

        // Test that passing in a string actually works
        descQuery = getDefaultQuery();
        try {
            descQuery.addOrderWithNull(ORDER_FIELD, "---", false);
            fail("Adding values of different types should fail");
        } catch (PersistenceException e) {
            // this should happen
        }
        descQuery.addOrderWithNull("action", "---", false);

        assertTrue( "Should be several items in this query set!", descQuery.next() );
        String prior = (String) descQuery.get("action");
        while ( descQuery.next() ) {
            final String currentValue = (String) descQuery.get("action");
            assertTrue("Query was retrieved out of order.",
                   prior.compareTo( currentValue ) >= 0 );

            prior = currentValue;

        }
        descQuery = null;
    }


    public void testCaseInsensativity() {
        DataQuery dq = getDefaultQuery();
        dq.addOrder("upper(action), lower(description)");
        assertEquals("Case insensativity failed.",
                     20,
                     dq.size());

        // Make sure size() does not move us to the end of the result
        // set
        if (! dq.next()) {
            fail("Can't call next in data query after calling size()");
        }
        dq.close();
    }

    /**
     *  This tests the ability to add multiple filters to a data query
     */
    public void testAddFilter() {
        DataQuery query = getDefaultQuery();
        long size = query.size();

        // let's add a single filter that we know does not do anything
        query = getDefaultQuery();
        Filter f = query.addFilter("1=1");
        assertTrue("Adding a filter of 1=1 should not change the size",
               size == query.size());

        // let's add a single filter that should restrict the results
        query = getDefaultQuery();
        Filter filter = query.addFilter("priority < :priority");
        filter.set("priority", new Integer(3));
        assertTrue("Filtering should lead to less results", query.size() < size);

        // let's add another filter.  This should return the same results
        // as a new filter with the same conditions
        query = getDefaultQuery();
        filter = query.addFilter("priority < :lowerPriority");
        filter.set("lowerPriority", new Integer(3));
        filter = query.addFilter("priority > :upperPriority");
        filter.set("upperPriority", new Integer(7));

        DataQuery query2 = getDefaultQuery();
        filter = query2.addFilter("priority < :lowerPriority and " +
                                  "priority > :upperPriority");
        filter.set("lowerPriority", new Integer(3));
        filter.set("upperPriority", "7");

        assertTrue("The two queries should be the same size.",
               query.size() == query2.size());

        query = getDefaultQuery();
        filter = query.addFilter("id = :id");
        filter.set("id", new Integer(10));

        assertTrue("Filtered query should have at least one row.", query.next());

        assertTrue("Filtered query should have exactly one row.", !query.next());

        query = getDefaultQuery();
        filter = query.addFilter("priority < :lowerPriority");
        filter.set("lowerPriority", "-3");
        assertTrue("The query should not return any results", query.size() == 0);

        // test adding a filter by passing in another filter
        query = getDefaultQuery();
        filter = query.addFilter("priority < :lowerPriority");
        filter.set("lowerPriority", "6");

        f = query.getFilterFactory().simple("action = :action");
        f.set("action", "write");
        query.addFilter(f);
        long withAddingFilter = query.size();

        query = getDefaultQuery();
        filter = query.addFilter("priority < :lowerPriority");
        filter.set("lowerPriority", "6");
        filter = query.addFilter("action = :action");
        filter.set("action", "write");
        assertTrue("seperate string filters and passing in filters should " +
               "be the same.", withAddingFilter == query.size());
    }

    public void testAddEqualsFilter() {
        int count = testEqualsFilter( "priority", new BigDecimal(3));
        assertTrue( count > 0 );
        count = testEqualsFilter( "priority", new BigDecimal(1024));
        assertTrue( 0 == count );
        count = testEqualsFilter( "action", "write" );
        assertTrue( NUM_WRITE_ACTIONS == count );
        count = testEqualsFilter( "action", "smurf" );
        assertTrue( 0 == count );

    }

    private int testEqualsFilter(String name, Object value) {
        DataQuery query = getDefaultQuery();
        query.addEqualsFilter(name, value);
        int count = 0;
        while (query.next()) {
            assertEquals("Equals filter failed for " + name + ":" +
                         value, value, query.get(name));
            count++;
        }
        return count;
    }


    /**
     * This tests the use of arbitray bind variables within a query.
     */
    public void testBindVariables() {
        DataQuery query = getSession().
            retrieveQuery("examples.DataQueryWithBindVariables");

        // neither variable being bound should throw a PersistenceException
        try {
            query.next();
            fail("calling query.next should have failed because no variables " +
                 "were bound");
        } catch (PersistenceException e) {
            // this is the correct behavior
        }

        // bind only "priority"
        query.setParameter("priority", "3");
        try {
            query.next();
            fail("calling query.next should have failed because only " +
                 "'priority' was bound");
        } catch (PersistenceException e) {
            // this is the correct behavior
        }


        // bind only "description"
        // we want a new query
        query = getSession().retrieveQuery("examples.DataQueryWithBindVariables");
        query.setParameter("description", "wrote");
        try {
            query.next();
            fail("calling query.next should have failed because only " +
                 "'description' was bound");
        } catch (PersistenceException e) {
            // this is the correct behavior
        }

        // bind both
        // we want a new query
        query = getSession().retrieveQuery("examples.DataQueryWithBindVariables");
        query.setParameter("priority", "3");

        try {
            assertTrue(query.size() == 1);
        } catch (PersistenceException e) {
            // this is the correct behavior because it is missing a parameter
        }
        query.setParameter("description", "wrote");

        // Test the ability to get out the parameter values
        assertTrue("The retrieved value for 'description' was not correct",
               "wrote".equals(query.getParameter("description").toString()));
        assertTrue("The retrieved value for 'priority' was not correct",
               "3".equals(query.getParameter("priority").toString()));

        // try to get a parameter that does not exist
        assertTrue("Trying to get a non-existent parameter actually returned " +
               "something besides null",
               null == query.getParameter("this does not exist"));

        // here we want to test binding arbitrary date values
        query = getSession().retrieveQuery("examples.DataQuery");
        long size = query.size();
        query = getSession().retrieveQuery(
                                           "examples.DataQueryWithDateBindVariable");

        java.util.Date date = new java.util.Date();

        // now we want the date to be around 1980 so we divide
        // the current time by 3 and set the date
        date.setTime(date.getTime()/3);

        // let's bind
        query.setParameter("actionTime", date);
        assertTrue("Binding with actionTime should return more than zero but less " +
               "than the full size.  Instead, it returned " + query.size(),
               query.size() > 0 && query.size() < size);

        query = getDefaultQuery();
        date = new java.util.Date();
        date.setTime(date.getTime()/3);
        Filter f = query.addFilter("actionTime > :actionTime");
        f.set("actionTime", date);
        /*         while (query.next()) {
                   s_log.info("ZZZZ the object is " + query.get("actionTime"));
                   s_log.info("YYYY the object is " + query.get("id").getClass());
                   s_log.info("YYYY the object is " +
                   query.get("action").getClass());
                   }*/

    }


    /**
     * This tests to see if PL/SQL will work with DataOperations
     * TODO: this does not really test anything...it just makes sure
     * that it does not error
     */
    public void testDataOperationWithPLSQLParamsAndReturn() {
        DataQuery query = getSession().
            retrieveQuery("examples.DataOperationWithPLSQLAndArgsAndReturn");
        query.setParameter("entryID", "3");

        // neither variable being bound should throw a PersistenceException
        query.next();
        query.close();
    }

    public void testCollectionBinding() {
        DataQuery query = getSession().
            retrieveQuery("examples.DataQueryWithIn");
        List l = new ArrayList();
        l.add("read");
        l.add("write");
        query.setParameter("actions", l);
        while (query.next()) {
            s_log.info(query.get("action") + ": " +
                       query.get("description"));
        }
    }

    public void testNext() {
        DataQuery dq = getDefaultQuery();
        int i = 0;
        while (dq.next()) {
            i++;
            assertNotNull("Should have a value for id", dq.get("id"));
        }
        assertTrue("Default data query returned no rows", i != 0);
        int j = 0;
        try {
            Object value = dq.get("id");
            fail("Should throw exception if get() called when cursor off " +
                 "DataQuery.  Instead got: " + value);
        } catch (PersistenceException e) {
            // ignore
        }

        while (j < 2*i) {
            j++;
            assertTrue("Next should continue returning false after initial false",
                   !dq.next());
        }
        dq.close();
        assertTrue("Next should return false after close", !dq.next());
        dq.reset();
        assertTrue("Next should return true after reset", dq.next());
        dq.close();
    }

    public void testZeroOrOneRow() throws Exception {
        DataQuery query = getSession().retrieveQuery
            ("examples.DataQueryZeroOrOneRow");
        query.next();
        assertTrue("we should have only gotten back one row",
               !query.next());

        query = getDefaultQuery();
        query.setReturnsUpperBound(1);
        try {
            query.next();
            fail("Calling query.next() on something with more than one row " +
                 "should fail");
        } catch (PersistenceException e) {
            // this should happen
        }

        query = getSession().retrieveQuery
            ("examples.DataQueryZeroOrOneRow");
        query.addEqualsFilter("id", new Integer(-1));

        // this should not return any rows
        assertTrue("When it does not return any rows it should return false",
               !query.next());
    }

    public void testReachedEndHandling() throws java.sql.SQLException {
        DataQuery dq = getDefaultQuery();
        Connection conn = getSession().getConnection();
        PreparedStatement ps =
            (java.sql.PreparedStatement)conn.prepareStatement
            ("select entry_id, action, description, priority, " +
             "action_time from t_data_query t");
        try {
            ResultSet rs = ps.executeQuery();
            checkRSandDQpositionFunctions(rs, dq);

            // can't do first with a forward-only resultset.
            while (rs.next()) {
                assertTrue("ResultSet and DataQuery next should match",
                           dq.next());
                checkRSandDQpositionFunctions(rs, dq);
            }
            assertTrue("ResultSet and DataQuery next should match",
                       !dq.next());
            checkRSandDQpositionFunctions(rs, dq);

            // can't do previous with a forward-only resultset.

            // also can't do last with a forward-only resultset...

            rs.close();
        } finally {
            ps.close();
        }
    }


    /**
     *  Test the setRange methods
     */
    public void testSetRange() {
        DataQuery query = getDefaultQuery();
        long fullSize = query.size();

        // beginIndex = 1 does not do anything
        query.setRange(new Integer(1));
        assertTrue("adding the range starting with 1 should not change the " +
               "number or rows returned.  Instead of getting " + fullSize +
               "rows, we got " + query.size() + " rows.",
               query.size() == fullSize);
        query.next();
        BigDecimal id = (BigDecimal)query.get("id");
        String action = (String)query.get("action");
        String description = (String)query.get("description");
        query.close();

        // beginIndex > 1 gives us less rows and the first row is
        query = getDefaultQuery();
        query.setRange(new Integer(4));
        assertTrue("adding the range starting with 4 should give us 3 less " +
               "rows returned.  Instead of getting " + (fullSize - 3) +
               " rows, we got " + query.size() + " rows.",
               query.size() == fullSize - 3);

        // not the same as the first row with beginIndex = 1
        query.next();
        assertTrue("changing the beginIndex should change the first row",
               !(id.equals((BigDecimal)query.get("id")) &&
                 action.equals((String)query.get("action")) &&
                 description.equals((String)query.get("description"))));
        query.close();

        // beginIndex > [size of query with no index] then 0 rows returned
        query = getDefaultQuery();
        query.setRange(new Integer(10000));
        assertTrue("setting a beginIndex > [number of rows in table] should " +
               "return zero rows.", query.size() == 0);

        // endIndex <= beginIndex...make sure an exception is thrown
        query = getDefaultQuery();
        try {
            query.setRange(new Integer(4), new Integer(4));
            fail("Setting endIndex = beginIndex should through an error");
        } catch (PersistenceException e) {
            // this is where we should be
        }

        query = getDefaultQuery();
        try {
            query.setRange(new Integer(4), new Integer(3));
            fail("Setting endIndex < beginIndex should through an error");
        } catch (PersistenceException e) {
            // this is where we should be
        }
        query.close();


        // beginIndex = 1, endIndex > [size of query with no index] then
        //    we should get back [size of query with no index]
        query = getDefaultQuery();
        query.setRange(new Integer(1), new Integer(100000));
        assertTrue("not placing reasonable restrictions should return the " +
               "max number of rows", query.size() == fullSize);
        query.close();

        // beginIndex = 1, endIndex < [size of query with no index] then
        //    we should get back endIndex - rows
        query = getDefaultQuery();
        query.setRange(new Integer(1), new Integer(5));
        assertTrue("Asking for 4 rows should give us 4, not " + query.size(),
               query.size() == 4);
        query.close();

        // beginIndex > 1 endIndex > [size of query with no index] then
        //    we should get back the same as setRange(beginIndex);
        query = getDefaultQuery();
        query.setRange(new Integer(4), new Integer(100000));
        long newSize = query.size();
        query = getDefaultQuery();
        query.setRange(new Integer(4));
        assertTrue("setting beginIndex to a reasonalble number but endIndex " +
               "really high should give us the same as " +
               "query.setRange([reasonable number])",
               query.size() == newSize);
        query.close();

        // beginIndex > 1,  endIndex < [size of query with no index] then
        //    we should get back endIndex - beginIndex rows
        query = getDefaultQuery();
        query.setRange(new Integer(4), new Integer(6));
        query.next();
        assertTrue("we should have gotten back 2 rows that does not include " +
               "the first row",
               query.size() == 2 &&
               !(id.equals((BigDecimal)query.get("id")) &&
                 action.equals((String)query.get("action")) &&
                 description.equals((String)query.get("description"))));
        query.close();
    }

    public void testSetRangeWithOrder() {
        DataQuery query = getDefaultQuery();
        query.addOrder("action");
        query.setRange(new Integer(6), new Integer(11));
        int i = 0;
        while (query.next()) {
            i++;
            assertEquals("delete", query.get("action"));
        }
        assertTrue("incorrect number of rows returned", i == 5);
    }

    public void testSetRangeWithFilterWithOrder() {
        DataQuery[] qs = { getDefaultQuery(), getDefaultQuery() };
        for (int i = 0; i < qs.length; i++) {
            qs[i].addFilter("id >= 10");
            qs[i].setRange(new Integer(5), new Integer(7));
        }

        // test both just in case one direction is lucky and happens to
        // be the default order returned by db
        qs[0].addOrder("action asc");
        qs[1].addOrder("action desc");

         assertTrue(qs[0].next());
         assertEquals("create", qs[0].get("action"));
         assertTrue(qs[0].next());
         assertEquals("delete", qs[0].get("action"));
         assertTrue(qs[0].next() == false);
         if (qs[0].next()) { fail("query should return 2 rows"); }

         assertTrue(qs[1].next());
         assertEquals("delete", qs[1].get("action"));
         assertTrue(qs[1].next());
         assertEquals("create", qs[1].get("action"));
         if (qs[1].next()) { fail("query should return 2 rows"); }
    }

    public void testSetRangeWithFilterWithCount() {
        DataQuery query = getDefaultQuery();
        query.addEqualsFilter("action", "create");
        query.setRange(new Integer(5), new Integer(100));
        assertTrue("wrong size", query.size() == 1);
    }

    /**
     *  This tests how we change null and makes sure that we only
     *  change it for selects and not updates
     */
    public void testBindingToNull() throws Exception {
        DataQuery query = getDefaultQuery();
        query.next();
        BigDecimal priority = (BigDecimal)query.get("priority");
        String description = (String)query.get("description");
        query.close();
        assertTrue("If the description is null already, the test will not " +
               "pass.  So, if you get this you need to rewrite the test.",
               description != null);

        // make sure that updating something to null still works
        DataOperation operation = getSession().retrieveDataOperation
            ("examples.DataOperationWithBindVariablesAndNull");
        operation.setParameter("description", null);
        operation.setParameter("priority", priority);
        operation.execute();

        query = getDefaultQuery();
        query.addEqualsFilter("priority", priority);
        query.next();
        description = (String)query.get("description");
        query.close();
        assertTrue("We just updated the description to null so it should " +
               "still be null", description == null);

        // test to make sure that PL/SQL with null still executes
        operation = getSession().retrieveDataOperation
            ("examples.DataOperationProcedureOneArg");
        operation.setParameter("description", null);
        try {
            operation.execute();
            operation.close();
        } catch (Exception e) {
            fail("An exception should not have been thrown. " +
                 Utilities.LINE_BREAK + e.getMessage() +
                 operation.toString());
            throw e;
        }
    }

    public void testAddPathSquared() {
        Session ssn = getSession();
        OrderAssociation oa = new OrderAssociation(ssn, "mdsql");
        DataQuery dq = ssn.retrieve("mdsql.LineItem");
        dq.addPath("order.items.id");
        dq.addOrder("id");
        dq.addOrder("order.items.id");
        dq.addEqualsFilter("order.id", new BigDecimal(1));

        // for each item go through all the other items
        for (int i = 0; i < oa.NUM_ITEMS; i++) {
            for (int j = 0; j < oa.NUM_ITEMS; j++) {
                assertTrue(dq.next());
                assertEquals(new BigDecimal(i), dq.get("id"));
                assertEquals(new BigDecimal(j), dq.get("order.items.id"));
            }
        }

        assertFalse(dq.next());
    }

    public void testCursorGetUnflushed() {
        Session ssn = getSession();
        DataObject user = ssn.create("mdsql.User");
        user.set("id", new BigInteger("1"));
        user.set("email", "user@example.com");
        user.set("firstName", "Abe");
        user.set("lastNames", "Honest");
        DataObject color = ssn.create("mdsql.Color");
        color.set("id", new BigInteger("1"));
        color.set("name", "red");
        user.set("favorateColor", color);
        user.save();

        // null is not flushable, so value in session and db differ
        color.set("name", null);

        DataQuery dq = ssn.retrieve("mdsql.User");
        dq.addPath("favorateColor.name");
        dq.addEqualsFilter("id", user.get("id"));

        try {
            dq.next();
            assertNull("value from cursor does not match value in session",
                       dq.get("favorateColor.name"));
        } finally {
            dq.close();
            color.set("name", "red");
        }
    }

    public void testAddPathToSelf() {
        DataQuery dq = SessionManager.getSession().retrieveQuery
            ("examples.GetOrdersOnlyID");
        dq.addPath("order.seller");
        while(dq.next()) { }
        // just testing that sql generated doesn't cause error
    }

    public void testAddLongPath() {
        Session ssn = SessionManager.getSession();
        DataObject[] nodes = new DataObject[4];
        DataObject prev = null;
        for (int i = 0; i < 4; i++) {
            nodes[i] = ssn.create("mdsql.Node");
            nodes[i].set("id", new BigDecimal(i));
            nodes[i].set("name", String.valueOf(i));
            nodes[i].set("parent", prev);
            nodes[i].save();
            prev = nodes[i];
        }

        DataCollection dc = ssn.retrieve("mdsql.Node");
        dc.addPath("parent.parent.parent.name");
        dc.addEqualsFilter("id", prev.get("id"));

        if (dc.next()) {
            assertEquals("0", dc.get("parent.parent.parent.name"));
            assertEquals(new BigDecimal(1), dc.get("parent.parent.id"));
            assertEquals(new BigDecimal(2), dc.get("parent.id"));
            assertEquals(new BigDecimal(3), dc.get("id"));
            if (dc.next()) {
                fail("query returned more than one row");
            }
        } else {
            fail("query returned no rows");
        }
    }

    public void testLongPaths() {
        Session ssn = SessionManager.getSession();
        DataQuery dq = ssn.retrieve("examples.DataQueryLongNames");
        if (dq.next()) {
            assertEquals("a", dq.get("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
            assertEquals("b", dq.get("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"));
            assertEquals("c", dq.get("cccccccccccccccccccccccccccccccc"));
            assertEquals("d", dq.get("dddddddddddddddddddddddddddddddd"));
            assertEquals("e", dq.get("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"));
            assertEquals("f", dq.get("ffffffffffffffffffffffffffffffff"));
            assertEquals("g", dq.get("gggggggggggggggggggggggggggggggg"));
            assertEquals("h", dq.get("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"));
            assertEquals("i", dq.get("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii"));
            assertEquals("j", dq.get("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj"));
            assertEquals("k", dq.get("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk"));
            if (dq.next()) {
                fail("query returned more than one row");
            }
        } else {
            fail("query returned no rows");
        }
    }

    /**
     *  This test shows that if you select columns of the same
     *  name out of two tables then there are problems with the mapping.
     */
    public void FAILStestColumnMapping() {
        DataQuery dq =
            SessionManager.getSession().retrieveQuery("examples.multipleIDs");

        while (dq.next()) {
            if (!dq.get("id").equals(dq.get("id2"))) {
                dq.close();
                return;
            }
        }

        fail("The mapping failed, properties should differ at some point");
    }

    public void testNamedFilter() {
        DataQuery dq = getDefaultQuery();
        Filter f = dq.addFilter("examples.NamedFilter");
        f.set("year", "1976");
        while (dq.next()) {
            assertTrue("read".equals(dq.get("action")));
        }
        assertEquals(2L, dq.size());
    }

    /**
     *  this tests the getPropertyValues method for DataQuery
     */
    public void getPropertyValues() {
        DataQuery query = getDefaultQuery();
        query.next();
        BigDecimal id = (BigDecimal)query.get("id");
        String description = (String)query.get("description");
        String action = (String)query.get("action");

        Map values = query.getPropertyValues();
        assertTrue("The ID is not the same", id.equals(values.get("id")));
        assertTrue("The DESCRIPTION is not the same",
               description.equals(values.get("description")));
        assertTrue("The ACTION is not the same",
               action.equals(values.get("action")));

        // now make sure that the items from the next map are not
        // the same
        query.next();
        Map values2 = query.getPropertyValues();
        assertTrue("the two maps where the same when they should not have been",
               !values2.equals(values));
        query.close();
    }


    private void checkRSandDQpositionFunctions(ResultSet rs, DataQuery dq)
        throws java.sql.SQLException {
        assertEquals(rs.isFirst(), dq.isFirst());
        assertEquals(rs.isBeforeFirst(), dq.isBeforeFirst());
        // Oracle sez that isLast is invalid for a forward-only resultset.
        /*assertEquals(rs.isLast(), dq.isLast());*/
        assertEquals(rs.isAfterLast(), dq.isAfterLast());
        assertEquals(rs.getRow(), dq.getPosition());
    }

    protected DataQuery getDefaultQuery() {
        DataQuery dq = getSession().retrieveQuery("examples.DataQuery");
        return dq;
    }


    /**
     *  This takes two DataQueries and makes sure that they return
     *  the same results for both "count" and looping through
     *
     *  @param errorText the error text to print out of a failure occurs
     *  @param query1 the first query to use in the comparison
     *  @param query2 the second query to use in the comparison
     *  @param toCompare The string name of the attribute to use when
     *                   comparing
     */
    private void compareQueries(String errorText, DataQuery query1,
                                DataQuery query2, String toCompare,
                                boolean shouldBeIdential) {
        long query1size = query1.size();
        long query2size = query2.size();
        boolean sameSize = true;

        if (shouldBeIdential) {
            sameSize = query1size == query2size;
        } else {
            sameSize = query1size != query2size;
        }
        assertTrue("COUNT: " + errorText + " The queries are: " +
               Utilities.LINE_BREAK + " Query1 = " + query1.toString() +
               Utilities.LINE_BREAK +" count = " + query1.size() +
               Utilities.LINE_BREAK + " Query2 = " + query2.toString() +
               Utilities.LINE_BREAK + " count = " + query2.size(),
               sameSize);

        Collection col = new ArrayList();
        while (query1.next()) {
            col.add(query1.get(toCompare));
        }

        while (query2.next()) {
            if (shouldBeIdential) {
                assertTrue("COMPARE: " + errorText + "The queries are: " +
                       Utilities.LINE_BREAK + " Query1 = " + query1.toString() +
                       Utilities.LINE_BREAK +" count = " + query1size +
                       Utilities.LINE_BREAK + " Query2 = " + query2.toString() +
                       Utilities.LINE_BREAK + " count = " + query2size,
                       col.contains(query2.get(toCompare)));
            } else {
                // if it is not present then they are not identical so we can
                // return
                if (!col.contains(query2.get(toCompare))) {
                    return;
                }
            }
        }
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(DataQueryImplTest.class);
    }

}
