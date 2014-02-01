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
package com.arsdigita.domain;

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Uses JavaPackage & JavaTest for domain testing.
 *
 * @author Jon Orris
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */
public class DependencyTest extends DomainTestCase {

    private static Logger log =
        Logger.getLogger(DependencyTest.class.getName());

    public DependencyTest(String name) {
        super(name);
    }

    protected void domainSetUp() {
        load("com/arsdigita/domain/examples/package.pdl");
        super.domainSetUp();
    }

    protected void domainTearDown() {
        // Return to original state.
        DomainObjectFactory.resetFactory();
        super.domainTearDown();
    }

    public void testNewInstance() throws Exception {

        String type = "com.arsdigita.domain.Package";
        DataObject object = getSession().create(type);
        DomainObjectFactory.registerInstantiator(type,
                                                 new JavaPackageInstantiator());

        JavaPackage p = null;
        try {
            p = (JavaPackage) DomainObjectFactory.newInstance(object);
        }
        catch(RuntimeException e) {
            fail("Should be able to make a newInstance for " + object.getObjectType());
        }

        assertNotNull(p);
        OID id = new OID(type, 1);
        try {

            p = (JavaPackage) DomainObjectFactory.newInstance(id);
        }
        catch(RuntimeException e) {
            fail("Should be able to make newInstance for " + id );
        }

        assertNotNull(p);
        log.info ("Package: " + p.getName());

        DomainCollection collection = p.getClasses();
        while (collection.next()) {
            JavaClass c = (JavaClass) collection.getDomainObject();
            log.info("Class name: " + c.getName());
            log.info("IsAbstract? " + c.isAbstract());
            log.info("Package: " + c.getPackage().getName());
        }

        log.info("Afferent packages!");

        collection = p.getAfferentPackages();
        while (collection.next()) {
            JavaPackage afferent = (JavaPackage) collection.getDomainObject();
            log.info(afferent.getName());
        }

        log.info("Efferent packages!");

        collection = p.getEfferentPackages();
        while (collection.next()) {
            JavaPackage afferent = (JavaPackage) collection.getDomainObject();
            log.info(afferent.getName());
        }

        log.info("Afferent Coupling: " + p.getAfferentCoupling());
        log.info("Efferent Coupling: " + p.getEfferentCoupling());
        log.info("Abstractness: " + p.getAbstractness());
        log.info("Instability: " + p.getInstability());
    }



    /*
     *  This test will fail miserably if testNewInstance fails because
     *  this assumes that it is possible to create a new instance
     */
    public void testAddingAssociation() {

        String type = "com.arsdigita.domain.Package";
        DataObject object = getSession().create(type);
        DomainObjectFactory.registerInstantiator(type,
                                                 new JavaPackageInstantiator());

        JavaPackage p = (JavaPackage) DomainObjectFactory.newInstance(object);
        p.set("id", new BigDecimal(400));
        p.setName("the name");
        p.save();

        DataAssociation association = (DataAssociation) p.get("used_by_set");

        DataObject object2 = getSession().create(type);

        JavaPackage p2 = (JavaPackage) DomainObjectFactory.newInstance(object2);
        p2.set("id", new BigDecimal(500));
        p2.setName("the name2");
        p2.save();


        // now, add something to the association
        p2.addToAssociation(association);

        p.save();
        p2.save();
        assertTrue(association.size() == 1);

    }

}
