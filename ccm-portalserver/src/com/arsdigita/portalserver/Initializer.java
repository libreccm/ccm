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
package com.arsdigita.portalserver;

import com.arsdigita.db.*;
// import com.arsdigita.domain.*;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObject;
// import com.arsdigita.persistence.*;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.kernel.URLService;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.pdl.*;
import com.arsdigita.portal.AgentPortlet;
import com.arsdigita.portalserver.admin.PSAdmin;
// import com.arsdigita.runtime.*;
import com.arsdigita.portalserver.personal.MyPortalsPortlet;
import com.arsdigita.portalserver.personal.PersonalPortal;
import com.arsdigita.portalserver.personal.PersonalPortalConfig;
import com.arsdigita.portalserver.personal.PersonalPortalCreator;
import com.arsdigita.portalserver.ui.admin.PortalCreator;
import com.arsdigita.portalserver.ui.admin.PortalSiteMap;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
// import com.arsdigita.web.*;
import com.arsdigita.web.Application;

/**
 * PortalserverInitializer
 *
 * @author Jim Parsons &lt;jparsons@redhat.com&gt;
 * @version $Revision: #19 $ $Date: 2004/08/17 $
 **/

public class Initializer extends CompoundInitializer {


    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-portalserver.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));

    //  add(new LegacyInitializer("com/arsdigita/portalserver/enterprise.init"));
    }


    /**
     *
     * @param evt
     */

    @Override
    public void init(DomainInitEvent evt) {
        super.init(evt);

        // Prerequisite to access basic potalsite (at /administration/)
        DomainObjectFactory.registerInstantiator(
               PortalSite.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                   @Override
                   public DomainObject doNewInstance(DataObject dataObject) {
                       return new PortalSite(dataObject);
                   }
               }
        );

        // XXX Unknown which routine really requires this.
        DomainObjectFactory.registerInstantiator(
               Role.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                   @Override
                   public DomainObject doNewInstance(DataObject dataObject) {
                       return new Role(dataObject);
                   }
               }
        );

        // Prerequisite to access AddTab in portal administration
        DomainObjectFactory.registerInstantiator(
               PortalTab.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new PortalTab(dataObject);
                    }
               }
        );

        // Prerequisite to access ??
        DomainObjectFactory.registerInstantiator(
               SubPortalTab.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new SubPortalTab(dataObject);
                    }
               }
        );

        // Prerequisite to access /portal-admin/ page
        DomainObjectFactory.registerInstantiator(
               PSAdmin.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new PSAdmin(dataObject);
                    }
               }
        );


    //    FORMERLY        ui.admin.Initializer

        // Prerequisite to access /portal-admin/portal-create/ page
        DomainObjectFactory.registerInstantiator(
               PortalCreator.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new PortalCreator(dataObject);
                    }
               }
        );

        // Prerequisite to access /portal-admin/portal-sitemap  page
        DomainObjectFactory.registerInstantiator(
               PortalSiteMap.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new PortalSiteMap(dataObject);
                    }
               }
        );



    //    Registering internal portlets

        // Prerequisite to access ??
        DomainObjectFactory.registerInstantiator(
               AgentPortlet.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new AgentPortlet(dataObject);
                    }
               }
        );

        // Prerequisite to access ??
        DomainObjectFactory.registerInstantiator(
               ApplicationDirectoryPortlet.BASE_DATA_OBJECT_TYPE,
               new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new ApplicationDirectoryPortlet(dataObject);
                    }
               }
        );

        // Prerequisite to access ??
        DomainObjectFactory.registerInstantiator(
               MyPortalsPortlet.BASE_DATA_OBJECT_TYPE,
               new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new MyPortalsPortlet(dataObject);
                    }
               }
        );

        // Prerequisite to access ??
        DomainObjectFactory.registerInstantiator(
               PortalNavigatorPortlet.BASE_DATA_OBJECT_TYPE,
               new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new PortalNavigatorPortlet(dataObject);
                    }
               }
        );

        // Prerequisite to access ??
        DomainObjectFactory.registerInstantiator(
               PortalSummaryPortlet.BASE_DATA_OBJECT_TYPE,
               new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new PortalSummaryPortlet(dataObject);
                    }
               }
        );


    //    FORMERLY        personal.Initializer

        // Prerequisite to access /personal-portal/[userId]/
        new PersonalPortalConfig();

        // Prerequisite to access ??
        DomainObjectFactory.registerInstantiator(
               PersonalPortalCreator.BASE_DATA_OBJECT_TYPE,
               new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new PersonalPortalCreator(dataObject);
                    }
               }
        );

        // Prerequisite to access ??
        DomainObjectFactory.registerInstantiator(
               PersonalPortal.BASE_DATA_OBJECT_TYPE,
               new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new PersonalPortal(dataObject);
                    }
               }
        );



        // Why URLFinder for forum is needed here??
        URLFinder forumFinder = new URLFinder() {
                public String find(OID oid) throws NoValidURLException {
                    Application app = Application.retrieveApplication(oid);
                    return app.getPrimaryURL();
                }
                public String find(OID oid, String context) throws NoValidURLException {
                    return find(oid);
                }

        };
        URLService.registerFinder(Application.BASE_DATA_OBJECT_TYPE, forumFinder);

        // XXX Unknown which routine really requires this.
        CWURLFinder finder = new CWURLFinder("admin");
        // XXX Unknown which routine requires this.
        URLService.registerFinder(PortalSite.BASE_DATA_OBJECT_TYPE, finder);


    }

}
