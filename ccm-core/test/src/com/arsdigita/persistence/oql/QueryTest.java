/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence.oql;

import com.arsdigita.persistence.PersistenceTestCase;
import com.arsdigita.util.StringUtils;
import com.redhat.persistence.Signature;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.oql.All;
import com.redhat.persistence.oql.Define;
import com.redhat.persistence.oql.Equals;
import com.redhat.persistence.oql.Exists;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Filter;
import com.redhat.persistence.oql.Get;
import com.redhat.persistence.oql.Literal;
import com.redhat.persistence.oql.Query;
import com.redhat.persistence.oql.Variable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 * QueryTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: QueryTest.java 745 2005-09-02 10:50:34Z sskracic $
 **/

public class QueryTest extends PersistenceTestCase {

    private static final Logger s_log =
        Logger.getLogger(QueryTest.class);

    public QueryTest(String name) {
        super(name);
    }

    private void doTest(String name, String typeName, String[] properties) {
        Root root = getSession().getMetadataRoot().getRoot();
        ObjectType type = root.getObjectType(typeName);
        assertTrue("No such type: " + typeName, type != null);
        Signature sig = new Signature(type);

        if (properties != null) {
            for (int i = 0; i < properties.length; i++) {
                Path path = Path.get(properties[i]);
                sig.addPath(path);
            }
        }

        doTest(name, sig, new All(type.getQualifiedName()));
    }

    private void doTest(String name, Signature sig, Expression expr) {
        Query query = sig.makeQuery(expr);
        String sql =
            query.generate(getSession().getMetadataRoot().getRoot()).getSQL();

        // XXX need to test db specific syntaxes

        // Test oracle specific syntax.
        String oraQuery = sql;
        String oraResult = compare("oracle-se/" + name + ".op", oraQuery);

        String pgQuery = sql;
        String pgResult = compare("postgres/" + name + ".op", pgQuery);

        if (oraResult != null || pgResult != null) {
            fail("Query:\n" + query + "\n\n" + oraResult + "\n\n" + pgResult);
        }
    }

    private String compare(String expectedResource, String actual) {
        String op = "com/arsdigita/persistence/oql/" + expectedResource;
        InputStream is = getClass().getClassLoader().getResourceAsStream(op);

        if (is == null) {
            return "No such resource: " + op + "\n\nTest output:\n" + actual;
        }

        Reader reader = new InputStreamReader(is);
        StringBuffer expected = new StringBuffer();
        char[] buf = new char[1024];
        try {
            while (true) {
                int n = reader.read(buf);
                if (n < 0) { break; }
                expected.append(buf, 0, n);
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }

        String result = diff(expected.toString(), actual);
        if (result != null) {
            result = expectedResource + ": " + result;
        }
        return result;
    }

    private String diff(String expected, String actual) {
        StringTokenizer expectedTokens = new StringTokenizer(expected, "\n\r");
        StringTokenizer actualTokens = new StringTokenizer(actual, "\n\r");

        int lineNumber = 0;
        while (expectedTokens.hasMoreTokens()) {
	    lineNumber++;
	    String expectedLine = stripWhitespace(expectedTokens.nextToken());
            if (actualTokens.hasMoreTokens()) {
                String actualLine = stripWhitespace(actualTokens.nextToken());
                if(!expectedLine.equals(actualLine)) {
                    return failure(expectedLine, actualLine, lineNumber,
                                   actual);
                }
            } else {
                return failure(expectedLine, null, lineNumber, actual);
            }

        }

        return null;
    }

    private static final String stripWhitespace(String str) {
        StringBuffer result = new StringBuffer();

        str = StringUtils.stripWhiteSpace(str);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isWhitespace(c)) {
                char last;
                if (result.length() > 0) {
                    last = result.charAt(result.length() - 1);
                } else {
                    last = '\0';
                }
                if (last != ' ') {
                    result.append(' ');
                }
            } else {
                result.append(c);
            }
        }

        return result.toString().trim();
    }

    private static final String failure(String expected, String actual,
                                        int lineNumber, String output) {
        return "Diff failed at line " + lineNumber +
            "\nExpected line:\n" + expected + "\n\nActual line:\n" + actual +
            "\n\nTest output:\n" + output;
    }

    /**
     * Tests fetching a parent property that is a self reference. This used to
     * result in an unconstrained join.
     **/

    public void testSelfReference() {
        doTest("SelfReference", "oql.SelfReference", null);
    }


    /**
     * Tests aggressively loading two optional properties. This used to result
     * in outer joining the same table twice, thereby producing invalid sql.
     **/

    public void testTwoOptionalAggressiveLoads() {
        doTest("TwoOptionalAggressiveLoads",
               "oql.TwoOptionalAggressiveLoads",
               null);
    }

    /**
     * Tests whether the optimizer retains the subtype table when only
     * fetching attributes from the supertype.
     **/

    public void testSubtypeTableRetention() {
        doTest("SubtypeTableRetention",
               "oql.Sub",
               new String[] {
                   "id",
                   "supAttribute"
               });
    }

    /**
     * Tests whether the optimizer will be smart enough to eliminate an
     * extraneous outer join.
     **/

    public void testEliminateOuterJoin() {
        doTest("EliminateOuterJoin",
               "oql.Sub",
               new String[] {
                   "id",
                   "optional.id"
               });
    }

    /**
     * Tests whether the optimizer will be smart enough to eliminate an
     * extraneous inner join.
     **/

    public void testEliminateInnerJoin() {
        doTest("EliminateInnerJoin",
               "oql.Sub",
               new String[] {
                   "id",
                   "required.id"
               });
    }

    /**
     * Tests fetching a required property.
     **/

    public void testRequiredFetch() {
        doTest("RequiredFetch",
               "oql.Sub",
               new String[] {
                   "id",
                   "required.id",
                   "required.refAttribute"
               });
    }

    /**
     * Tests fetching an optional property.
     **/

    public void testOptionalFetch() {
        doTest("OptionalFetch",
               "oql.Sub",
               new String[] {
                   "id",
                   "optional.id",
                   "optional.refAttribute"
               });
    }


    /**
     * Test an individual condition filter.
     **/

    private static final String[] CONDITIONS = { "Contains", "Equals" };

    private static final int CONTAINS = 0;
    private static final int EQUALS = 1;

    private void doConditionTest(String from, String assn, String to,
                                 int cond) {
        Root root = getSession().getMetadataRoot().getRoot();
        Signature sig = new Signature(root.getObjectType(to));

        // XXX: should be bind variable of type from
        Literal start = new  Literal(new Object());
        Expression left = new Get(start, assn);

        Expression expr = new Define(new All(to), "right");
        Expression filt;
        switch (cond) {
        case CONTAINS:
            left = new Define(left, "left");
            filt = new Exists
                (new Filter(left, new Equals
                            (new Variable("left"), new Variable("right"))));
            break;
        case EQUALS:
            filt = new Equals(left, new Variable("right"));
            break;
        default:
            throw new IllegalStateException("unknown condition: " + cond);
        }

        expr = new Filter(expr, filt);
        expr = new Get(expr, "right");

        doTest(CONDITIONS[cond] + "-" + assn, sig, expr);
    }

    /**
     * Tests contains filter for join throughs.
     **/

    public void testContainsJoinThrough() {
        doConditionTest("test.Test", "collection", "test.Icle", CONTAINS);
    }

    /**
     * Tests contains filter for join froms.
     **/

    public void testContainsJoinFrom() {
        doConditionTest("test.Test", "components", "test.Component", CONTAINS);
    }

    public void testContainsJoinFromSelf() {
        doConditionTest("test.Test", "children", "test.Test", CONTAINS);
    }

    public void testEqualsJoinTo() {
        doConditionTest("test.Test", "optional", "test.Icle", EQUALS);
    }

    public void testEqualsJoinToSelf() {
        doConditionTest("test.Test", "optionalSelf", "test.Test", EQUALS);
    }


    /**
     * DDL Generation tests.
     **/

    private void doTableTest(String tableName) {
        Root root = getSession().getMetadataRoot().getRoot();
        Table table = root.getTable(tableName);
        assertTrue("No such table: " + tableName, table != null);
        String result = compare(table.getName() + ".sql", table.getSQL(false));
        if (result != null) {
            fail(result);
        }
    }

    public void testTest() {
        doTableTest("tests");
    }

    public void testIcles() {
        doTableTest("icles");
    }

    public void testComponents() {
        doTableTest("components");
    }

    public void testCollectionSelf() {
        doTableTest("collection_self");
    }

    public void testCollection() {
        doTableTest("collection");
    }

}
