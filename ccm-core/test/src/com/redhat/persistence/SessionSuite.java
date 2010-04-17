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
package com.redhat.persistence;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.Model;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Role;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.pdl.PDL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestResult;
import org.apache.log4j.Logger;

/**
 * SessionSuite
 *
 * @author <a href="mailto:ashah@redhat.com">ashah@redhat.com</a>
 * @version $Id: SessionSuite.java 740 2005-09-02 10:13:54Z sskracic $
 **/

public class SessionSuite extends PackageTestSuite {

    private static final Logger s_log = Logger.getLogger(SessionSuite.class);

//     private static class PassthroughEngine extends Engine {

//         private Engine m_engine;
//         private List m_events;
//         private boolean m_recording = false;

//         PassthroughEngine(Engine eng) {
//             m_engine = eng;
//             m_events = new LinkedList();
//         }

//         void start() { m_recording = true; }
//         List stop() {
//             m_recording = false;
//             List l = m_events;
//             m_events = new LinkedList();
//             return l;
//         }

//         protected void write(Event ev) {
//             m_engine.write(ev);
//             if (m_recording) { m_events.add(ev); }
//         }

//         protected void flush() { m_engine.flush(); }
//         protected void commit() { m_engine.commit(); }
//         protected void rollback() { m_engine.rollback(); }
//         protected RecordSet execute(Query query) {
//             return m_engine.execute(query);
//         }
//         protected long size(Query query) {
//             return m_engine.size(query);
//         }
//     }

    public SessionSuite() {}

    public SessionSuite(Class theClass) {
        super(theClass);
    }

    public SessionSuite(String name) {
        super(name);
    }

    public static Test suite() {
        final SessionSuite s = new SessionSuite();

        s.addTest(new Test() {
            public int countTestCases() { return 1; }
            public void run(TestResult result) {
                try {
                    result.startTest(this);
                    s.addTests();
                } finally {
                    result.endTest(this);
                }
            }
        });

        BaseTestSetup wrapper = new CoreTestSetup(s) {
            protected void setUp() throws Exception {
                super.setUp();
                s.initialize();
            }
        };

        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

    private static String INTE = "global.Integer";

    private Model m_model;
    private ObjectType m_root;
    private ObjectType m_one;
    private ObjectType m_two;
    // m_root -> layer 1, m_one -> layer 2
    private Map m_layers = new HashMap(2);

    private Session m_ssn;
    private Map[] m_objs = new Map[] {
        new HashMap(), new HashMap(), new HashMap() };

//    private PassthroughEngine m_engine;
    private Engine m_engine;

    public void initialize() {
        PDL pdl = new PDL();
        Root root = new Root();
        pdl.emit(root);

        m_model = Model.getInstance("test");

//         m_engine = new PassthroughEngine(new MemoryEngine());
        m_ssn = new Session(root, m_engine, new QuerySource());
        initializeModel();


        initializeData();

        m_ssn.commit();
    }

    public void addTests() {
        addTests(m_root);
        addTests(m_one);
    }

    private void addTests(ObjectType type) {
        Collection roles = type.getRoles();
        Collection keys = type.getKeyProperties();

        for (Iterator it = roles.iterator(); it.hasNext(); ) {
            final Role role = (Role) it.next();

            if (keys.contains(role)) { continue; }

            if (role.getName().startsWith("-")) { continue; }

            addTest(new Test() {
                public int countTestCases() { return 1; }
                public void run(TestResult result) {
                    try {
                        result.startTest(this);
                        if (role.isCollection()) {
                            testCollection(role);
                        } else {
                            testSingle(role);
                        }
                    } catch (junit.framework.AssertionFailedError afe) {
                        result.addFailure(this, afe);
                    } catch (RuntimeException re) {
                        result.addError(this, re);
                    } finally {
                        result.endTest(this);
                    }
                }
            });
        }
    }

    private void testCollection(Role role) {
        ObjectType source = role.getContainer();
        ObjectType target = role.getType();

        Object obj = m_objs[0].get(source);

        resetState();

        startTest("test: 0add1" + role.getName());
        m_ssn.add(obj, role, m_objs[0].get(target));
        endTest();

        startTest("test: 1rem1" + role.getName());
        m_ssn.remove(obj, role, m_objs[0].get(target));
        endTest();

        resetState();

        startTest("test: 0add2" + role.getName());
        m_ssn.add(obj, role, m_objs[0].get(target));
        m_ssn.add(obj, role, m_objs[1].get(target));
        endTest();

        startTest("test: 2add1" + role.getName());
        m_ssn.add(obj, role, m_objs[2].get(target));
        endTest();

        startTest("test: 3rem1" + role.getName());
        m_ssn.remove(obj, role, m_objs[2].get(target));
        endTest();

        startTest("test: 2rem2" + role.getName());
        m_ssn.remove(obj, role, m_objs[0].get(target));
        m_ssn.remove(obj, role, m_objs[1].get(target));
        endTest();
    }

    private void testSingle(Role role) {
        ObjectType source = role.getContainer();
        ObjectType target = role.getType();

        Object obj = m_objs[0].get(source);

        resetState();
        startTest("test: set " + role.getName());
        m_ssn.set(obj, role, m_objs[1].get(target));
        endTest();

        startTest("test: setnull " + role.getName());
        m_ssn.set(obj, role, null);
        endTest();
    }

    private void resetState() {
        m_ssn.rollback();
    }

    private void startTest(String name) {
        s_log.info(name);
//         m_engine.stop();
//         m_engine.start();
    }

    private void endTest() {
        m_ssn.flush();
//         List evs = m_engine.stop();
//         if (s_log.isDebugEnabled()) {
//             for (Iterator it = evs.iterator(); it.hasNext(); ) {
//                 s_log.debug(it.next());
//             }
//         }
//         s_log.info("stop: " + evs.size());
//         m_engine.start();
    }

    private void initializeModel() {
        ObjectType inte = m_ssn.getRoot().getObjectType(INTE);

        m_root = createKeyedType(m_model, "Root");
        m_one = createKeyedType(m_model, "One");
        m_two = createKeyedType(m_model, "Two");

        Adapter a = new Generic.Adapter();
        m_ssn.getRoot().addAdapter(Generic.class, a);

//         ObjectType int3 = createUnkeyedType(
//             m_model, "Int3", new ObjectType[] { inte, inte, inte });

//         ObjectType oneint = createUnkeyedType(
//             m_model, "OneInt", new ObjectType[] { m_one, inte });

//         ObjectType twoint = createUnkeyedType(
//             m_model, "TwoInt", new ObjectType[] { m_two, inte });

//         ObjectType rootone = createUnkeyedType(
//             m_model, "RootOne", new ObjectType[] { m_root, m_one});

//         ObjectType roottwo = createUnkeyedType(
//             m_model, "RootTwo", new ObjectType[] { m_root, m_two });

        ArrayList layer1 = new ArrayList();
        layer1.add(m_root);
        layer1.add(m_one);
        layer1.add(inte);
        // layer1.add(int3);
        // layer1.add(oneint);
        // layer1.add(rootone);

        m_layers.put(m_root, layer1);

        ArrayList layer2 = new ArrayList();
        layer2.add(m_root);
        layer2.add(m_two);
        layer2.add(inte);
        // layer2.add(int3);
        // layer2.add(twoint);
        // layer2.add(roottwo);

        m_layers.put(m_one, layer2);

        for (Iterator it = layer1.iterator(); it.hasNext(); ) {
            addProperties(m_root, (ObjectType) it.next());
        }

        for (Iterator it = layer2.iterator(); it.hasNext(); ) {
            addProperties(m_one, (ObjectType) it.next());
        }
    }

    private void initializeData() {
        fill(m_root, 0);
        fill(m_one, 0);
        fill(m_two, 0);
        fill(m_root, 1);
        fill(m_one, 1);
        fill(m_two, 1);
        fill(m_root, 2);
        fill(m_one, 2);
        fill(m_two, 2);
    }

    private Object fill(ObjectType type, int round) {
        if (m_objs[round].get(type) != null) {
            return m_objs[round].get(type);
        }

        Object obj;

        if (type.isKeyed()) {
            if (type.getKeyProperties().size() > 1) {
                throw new IllegalStateException("compound key");
            }
            obj = new Generic(type, new Integer(round));
            m_ssn.create(obj);
        } else if (!type.isCompound()) {
            obj = new Integer(round);
        } else {
            // compound type
            throw new IllegalStateException("can't create compound object");
        }

        m_objs[round].put(type, obj);

        // build up data
        Collection props = type.getProperties();
        Collection keys = type.getKeyProperties();

        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();

            if (keys.contains(prop)) { continue; }
            if (prop.isNullable()) { continue; }

            if (prop.isCollection()) {
                throw new IllegalStateException("nonnullable collection");
            }
            if (!(prop instanceof Role)) {
                throw new IllegalStateException("nonnullable nonrole");
            }

            Role role = (Role) prop;

            if (prop.getName().startsWith("-")
                && !role.getReverse().isNullable()) {
                continue;
            }

            ObjectType targetType = role.getType();
            Object target = m_objs[round].get(targetType);

            if (target != null) {
                m_ssn.set(obj, role, target);
            } else {
                m_ssn.set(obj, role, fill(targetType, round));
            }
        }

        return obj;
    }

    private static final boolean[] B = new boolean[] { false, true };

    private void addProperties(ObjectType ot, ObjectType target) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("props: " + ot.getName() + " " + target.getName());
        }

        // collection, component, nullable are the 3 args
        if (!target.isKeyed()) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 2; k++) {
                        oneWay(ot, target, B[i], B[j], B[k]);
                    }
                }
            }
        } else {
            // One way
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 2; k++) {
                        oneWay(ot, target, B[i], B[j], B[k]);
                    }
                }
            }

            // Two way
            // no composition (ot is collection x target is collection)

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 2; k++) {
                        for (int l = 0; l < 2; l++) {
                            for (int m = 0; m < 2; m++) {
                                for (int n = 0; n < 2; n++) {
                                    twoWay(ot, target,
                                           B[i], B[j], B[k], B[l], B[m], B[n]);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private ObjectType createKeyedType(Model m, String name) {
        ObjectType ot = new ObjectType(m, name, null);
	ot.setJavaClass(Generic.class);

        Role id = new Role(
            "id", m_ssn.getRoot().getObjectType(INTE),
            false, false, false);
        ot.addProperty(id);
        ObjectMap om = new ObjectMap(ot);
        Collection keys = om.getKeyProperties();
        keys.add(id);

        m_ssn.getRoot().addObjectType(ot);
        m_ssn.getRoot().addObjectMap(om);

        return ot;
    }

    private ObjectType createUnkeyedType(Model m, String name,
                                                ObjectType[] ots) {
        ObjectType ot = new ObjectType(m, name, null);
	ot.setJavaClass(Generic.class);

        m_ssn.getRoot().addObjectType(ot);

        for (int i = 0; i < ots.length; i++) {
            ObjectType prop = ots[i];
            ot.addProperty(new Role(prop.getName() + i, prop,
                                    false, false, false));
        }

        return ot;
    }

    private void twoWay(ObjectType a, ObjectType b,
                               boolean aCollection, boolean bCollection,
                               boolean aComponent, boolean bComponent,
                               boolean aNullable, boolean bNullable) {

        if (// two component ends
            (aComponent == true && bComponent == true) ||
            // the composite side must not be a collection
            (aComponent == true && bCollection == true) ||
            (aCollection == true && bComponent == true) ||
            // nonnullable collections are not supported
            (aCollection == true && aNullable == false) ||
            (bCollection == true && bNullable == false)) {
            // with noncollections on both ends
            // only one end can be nullable
            // however if the a end is the nonnullable one,
            // its role to b can not be updated because the reverse
            // would have to be set to null
//             (aCollection == false && bCollection == false
//              && aNullable == false)) {
            return;
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug(" role2: " + aCollection + aComponent +
                        bCollection + bComponent);
        }

        String name =
            a.getName() +
            (aCollection ? "0-n" :
             (aNullable ? "0-1" : "1-1")) +
            (bCollection ? "<->0-n" :
             (bNullable ? "<->0-1" : "<->1-1")) +
            b.getName() +
            (aComponent ? "part,whole" : "") +
            (bComponent ? "whole,part" : "");

        Role arole = new Role(name, b, bComponent, bCollection, bNullable);
        Role brole =
            new Role("-" + name, a, aComponent, aCollection, aNullable);

        try {
            a.addProperty(arole);
        } catch (IllegalArgumentException iae) {
            if (s_log.isDebugEnabled()) {
                s_log.debug(a.getName());
                s_log.debug(arole.getName());
            }
            throw iae;
        }

        try {
            b.addProperty(brole);
        } catch (IllegalArgumentException iae) {
            if (s_log.isDebugEnabled()) {
                s_log.debug(b.getName());
                s_log.debug(brole.getName());
            }
            throw iae;
        }

        arole.setReverse(brole);
    }

    private void oneWay(ObjectType a, ObjectType b,
                               boolean bCollection, boolean bComponent,
                               boolean isNullable) {
        if (!isNullable && bCollection) {
            return;
        }

        if (!b.isKeyed() && (bCollection || bComponent)) {
            return;
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug(" role: " + bCollection + bComponent);
        }

        a.addProperty(new Role(
                               a.getName() +
                               (bCollection ? "->0-n" :
                                (isNullable ? "->0-1" : "->1-1")) +
                               // (aCollection ? "->0-n" : "->0-1") +
                               b.getName() +
                               (bComponent ? "whole,part" : ""),
                               // (aComponent ? "whole" : "");
                               b, bComponent, bCollection, isNullable));
    }
}
