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

import com.arsdigita.db.DbHelper;
import java.math.BigDecimal;
import java.util.Date;

/**
 * DataOperationText
 *
 * This class tests DataOperation, using data contained in
 * //enterprise/infrastructure/dev/persistence/sql/data-query-test.sql
 *
 *  This data must be loaded as a precondition of this test running.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #18 $ $Date: 2004/08/16 $
 */
public class DataOperationTest extends PersistenceTestCase {

    

    public DataOperationTest(String name) {
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
     * This tests the use of arbitray bind variables within a DataOperation
     */
    public void testDataOperationWithBindVariables() {
        DataOperation operation = getSession().
            retrieveDataOperation("examples.DataOperationWithBindVariables");

        // neither variable being bound should throw a PersistenceException
        try {
            operation.execute();
            fail("calling operation.execute should have failed because no " +
                 "variables were bound");
        } catch (PersistenceException e) {
            // this is the correct behavior
        }

        // bind only "priority"
        operation.setParameter("priority", new BigDecimal(3));
        try {
            operation.execute();
            fail("calling operation.execute should have failed because only " +
                 "'priority' was bound");
        } catch (PersistenceException e) {
            // this is the correct behavior
        }


        // bind only "description"
        // we want a new operation
        operation = getSession().retrieveDataOperation(
                                                       "examples.DataOperationWithBindVariables");
        operation.setParameter("description", "wrote");
        try {
            operation.execute();
            fail("calling operation.execute should have failed because only " +
                 "'description' was bound");
        } catch (PersistenceException e) {
            // this is the correct behavior
        }

        // bind both description and priority
        // we want a new operation
        operation.setParameter("priority", new BigDecimal(3));
        try {
            operation.execute();
            fail("calling operation.execute should have failed because only " +
                 "'priority' was bound");
        } catch (PersistenceException e) {
            // this is the correct behavior
        }


        // binding all three variables
        operation = getSession().retrieveDataOperation(
                                                       "examples.DataOperationWithBindVariables");
        operation.setParameter("priority", new BigDecimal(30000));
        operation.setParameter("description", "wrote");
        operation.setParameter("currentPriority", new BigDecimal(8));

        // let's make sure that there are not currently any items with the
        // high priority to so that we are sure that our query works
        DataQuery query = getSession().retrieveQuery(
                                                     "examples.DataQueryWithBindVariables");
        query.setParameter("priority", new BigDecimal(29999));
        query.setParameter("description", "wrote");

	assertTrue(query.size() == 0);


        // this should succeed
        operation.execute();

        // let's make sure it succeeded
        query = getSession().retrieveQuery("examples.DataQueryWithBindVariables");
        query.setParameter("priority", new BigDecimal(29999));

        try {
            query.size();
	    fail("should have generated an unbound variable exception");
        } catch (PersistenceException e) {
            // this is the correct behavior
        }
        query.setParameter("description", "wrote");


        // let's undo the update
        operation = getSession().retrieveDataOperation(
                                                       "examples.DataOperationWithBindVariables");
        operation.setParameter("priority", new BigDecimal(9));
        operation.setParameter("description", "wrote");
        operation.setParameter("currentPriority", "29999");

        // Test the ability to get out the parameter values
        assertTrue("The retrieved value for 'description' was not correct",
               "wrote".equals(operation.getParameter("description").toString()));
        assertTrue("The retrieved value for 'priority' was not correct",
               "9".equals(operation.getParameter("priority").toString()));
        assertTrue("The retrieved value for 'priority' was not correct",
               "29999".equals(operation.getParameter("currentPriority").toString()));

        // try to get a parameter that does not exist
        assertTrue("Trying to get a non-existent parameter actually returned " +
               "something besides null",
               null == operation.getParameter("this does not exist"));
    }


    /**
     * This tests to see if PL/SQL will work with DataOperations
     */
    public void testDataOperationWithPLSQL() throws Exception {
        DataOperation operation = getSession().
            retrieveDataOperation("examples.DataOperationWithPLSQL");

        // neither variable being bound should throw a PersistenceException
        DataQuery query = getSession().retrieveQuery("examples.DataQuery");
        long size = query.size();
        operation.execute();
        assertTrue("The query should be larger after the PL/SQL than before",
               size < query.size());

        operation = getSession().retrieveDataOperation
            ("examples.DataOperationWithPLSQLAndReturn");
        query = getSession().retrieveQuery("examples.DataQuery");
        size = query.size();

        // get the max_id that can be used to make sure we are getting
        // the correct results back
        DataQuery maxQuery = getSession().retrieveQuery("examples.DataQueryMaxID");
        maxQuery.next();
        int newID = ((BigDecimal)maxQuery.get("id")).intValue();
        maxQuery.close();

        String return_value = null;
        if (DbHelper.getDatabase
            (getSession().getConnection()) == DbHelper.DB_POSTGRES) {
            DataQuery operationQuery = getSession().retrieveQuery
                ("examples.DataOperationFunction");
            operationQuery.next();
            return_value = (String)operationQuery.get("newValue");
            operationQuery.close();
        } else {
            operation = getSession().retrieveDataOperation
                ("examples.DataOperationFunction");
            operation.execute();
            return_value = (String)operation.get("newValue");
        }

        assertTrue("DataOperationFunction did not correctly change the items " +
               "returned by the query.  Expected " + (size + 1) +
               " but got " + query.size(), size + 1 == query.size());
        size = query.size();
        int nextID = Integer.parseInt(return_value);
        assertTrue("DataOperationFunction did not return the correct value. " +
               "We expected " + (newID + 1) + " but got " + nextID,
               newID + 1 == nextID);
        newID = nextID;

        if (DbHelper.getDatabase
            (getSession().getConnection()) == DbHelper.DB_POSTGRES) {
            DataQuery operationQuery = getSession().retrieveQuery
                ("examples.DataOperationProcWithOut");
            operationQuery.setParameter("oldID", null);
            operationQuery.next();
            return_value = (String)operationQuery.get("newID");
            operationQuery.close();
        } else {
            operation = getSession().retrieveDataOperation
                ("examples.DataOperationProcWithOut");
            operation.execute();
            return_value = (String)operation.get("newID");
        }
        assertTrue("DataOperationProcWithOut did not correctly change the items " +
               "returned by the query", size + 1 == query.size());
        size = query.size();
        nextID = Integer.parseInt(return_value);
        assertTrue("DataOperationProcWithOut did not return the correct value. " +
               "We expected " + (newID + 1) + " but got " + nextID,
               newID + 1 == nextID);
        newID = nextID;

        String stringValue = Integer.toString(Integer.parseInt(return_value) + 8);
        if (DbHelper.getDatabase
            (getSession().getConnection()) == DbHelper.DB_POSTGRES) {
            DataQuery operationQuery = getSession().retrieveQuery
                ("examples.DataOperationProcWithInOut");
            operationQuery.setParameter("oldID", new Integer(stringValue));
            operationQuery.next();
            return_value = (String)operationQuery.get("newID");
            operationQuery.close();
        } else {
            operation = getSession().retrieveDataOperation
                ("examples.DataOperationProcWithInOut");
            operation.setParameter("oldID", stringValue);
            operation.execute();
            return_value = (String)operation.get("newID");
        }
        assertTrue("DataOperationProcWithInOut did not correctly change the " +
               "items returned by the query.  We expected " + stringValue +
               " but got " + return_value, stringValue.equals(return_value));
        assertTrue("DataOperationProcWithInOut did not add a row",
               size + 1 == query.size());
        size = query.size();
        nextID = Integer.parseInt(return_value);
        assertTrue("DataOperationProcWithInOut did not return the correct value. " +
               "We expected " + (newID + 8) + " but got " + nextID,
               newID + 8 == nextID);
        newID = nextID;

        Integer integerValue = new Integer(Integer.parseInt(return_value) + 8);
        Integer return_integer = null;
        if (DbHelper.getDatabase
            (getSession().getConnection()) == DbHelper.DB_POSTGRES) {
            DataQuery operationQuery = getSession().retrieveQuery
                ("examples.DataOperationProcWithInOutInt");
            operationQuery.setParameter("oldID", integerValue);
            operationQuery.next();
            return_integer = (Integer)operationQuery.get("newID");
            operationQuery.close();
        } else {
            operation = getSession().retrieveDataOperation
                ("examples.DataOperationProcWithInOutInt");
            operation.setParameter("oldID", integerValue);
            operation.execute();
            return_integer = (Integer)operation.get("newID");
        }
        assertTrue("DataOperationProcWithInOutInt did not correctly change the " +
               "items returned by the query (with Integer)",
               integerValue.toString().equals(return_integer.toString()));
        assertTrue("DataOperationProcWithInOutInt and Integer did not add a row",
               size + 1 == query.size());
        size = query.size();
        nextID = return_integer.intValue();
        assertTrue("DataOperationProcWithInOutInt did not return the correct value. " +
               "We expected " + (newID + 8) + " but got " + nextID,
               newID + 8 == nextID);
        newID = nextID;


        // now we test using Dates
        Date date = new Date();
        if (DbHelper.getDatabase
            (getSession().getConnection()) == DbHelper.DB_POSTGRES) {
            DataQuery operationQuery = getSession().retrieveQuery
                ("examples.DataOperationProcWithDates");
            operationQuery.setParameter("idToUpdate", integerValue);
            operationQuery.setParameter("oldDate", date);
            operationQuery.next();
            operationQuery.close();
        } else {
            fail("ORACLE???");
            operation = getSession().retrieveDataOperation
                ("examples.DataOperationProcWithDates");
            operation.setParameter("idToUpdate", return_integer);
            operation.setParameter("oldDate", date);
            operation.execute();
        }
        assertTrue("DataOperationProcWithDates and Integer added or removed a row",
               size == query.size());


        // get the new time and make sure it is the same as the old time
        query = getSession().retrieveQuery("examples.DataQuery");
        query.addEqualsFilter("id", return_integer);
        query.next();
        long newTime = date.getTime() - ((Date)query.get("actionTime")).getTime();
        assertTrue("The time retrieved is not near the time set",
               newTime > -1000 && newTime < 1000);
        query.close();
    }


    /**
     * This tests to see if PL/SQL will work with DataOperations
     */
    public void testDataOperationWithPLSQLParams() {
        DataOperation operation = getSession().
            retrieveDataOperation("examples.DataOperationWithPLSQLAndArgs");
        operation.setParameter("priority", new BigDecimal(3));

        // neither variable being bound should throw a PersistenceException
        DataQuery query = getSession().retrieveQuery("examples.DataQuery");
        operation.execute();
        query = getSession().retrieveQuery("examples.DataQuery");
    }



    /**
     *  This tests for the case where users try to specify which arguments
     *  to pass in to the DataQuery but they do not pass in all items
     */
    public void testPLSQLWithRandomArgs() {
        // Postgres does not have default values so there is no reason
        // to test for it.
        if (DbHelper.getDatabase
            (getSession().getConnection()) == DbHelper.DB_POSTGRES) {
            return;
        }
        // make sure that the table is empty
        DataQuery query = null;
        try {
            query = getSession().retrieveQuery
                ("examples.PLSQLQueryWithArbitraryArgs");
            assertTrue("the table already has items in it", query.size() == 0);

            DataOperation operation = getSession().
                retrieveDataOperation("examples.PLSQLWithArbitraryArgsQuery");
            operation.setParameter("arg1", new Integer(1));
            operation.setParameter("arg2", new Integer(2));
            operation.setParameter("arg5", new Integer(5));
            operation.execute();
            operation.close();
            // get the row that was just inserted
            query.next();

            // now we want to check the value for all 5 items
            Object arg1 = query.get("arg1");
            Object arg2 = query.get("arg2");
            Object arg3 = query.get("arg3");
            Object arg4 = query.get("arg4");
            Object arg5 = query.get("arg5");

            assertTrue("The first argument should have been 1.  Instead, it is [" +
                   arg1 + "]", "1".equals(arg1.toString()));
            assertTrue("The second argument should have been 1.  Instead, it is [" +
                   arg2 + "]", "2".equals(arg2.toString()));
            assertTrue("The third argument should have been null.  Instead, it is [" +
                   arg3 + "]", arg3 == null);
            assertTrue("The fourth argument should have been null.  Instead, it is [" +
                   arg5 + "]", arg4 == null);
            assertTrue("The fifth argument should have been 5.  Instead, it is [" +
                   arg5 + "]", "5".equals(arg5.toString()));


        } finally {
            if ( null != query ) {
                query.close();
            }
        }

    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(DataOperationTest.class);
    }

}
