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
package com.arsdigita.docrepo;

import com.arsdigita.db.*;
import com.arsdigita.docrepo.ui.RecentUpdatedDocsPortlet;
import com.arsdigita.domain.*;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.*;
import com.arsdigita.persistence.pdl.*;
// import com.arsdigita.web.*;
import com.arsdigita.runtime.*;

/**
 * Document Repository Initializer
 *
 * @author Jim Parsons &lt;jparsons@redhat.com&gt;
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Revision: #19 $ $Date: 2004/08/17 $
 **/

public class Initializer extends CompoundInitializer {


    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-docrepo.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));

    }

    /**
     *
     * @param evt
     */
    @Override
    public void init(DomainInitEvent evt) {
        super.init(evt);

        // Prerequisite to access a repository instance
        DomainObjectFactory.registerInstantiator(
               Repository.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                   @Override
                   public DomainObject doNewInstance(DataObject dataObject) {
                       return new Repository(dataObject);
                   }
               }
        );

        DomainObjectFactory.registerInstantiator
            (ResourceImpl.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
            @Override
                 public DomainObject doNewInstance(DataObject obj) {
                     Boolean isFolder = (Boolean) obj.get(Repository.IS_FOLDER);
                     if (isFolder != null && isFolder.booleanValue()) {
                         return new Folder(obj);
                     } else {
                         return new File(obj);
                     }
                 }
             });
        // File
        DomainObjectFactory.registerInstantiator(
             File.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
            @Override
                  public DomainObject doNewInstance(DataObject dataObject) {
                        return new File(dataObject);
                        }
                  }
        );

        // Folder
        DomainObjectFactory.registerInstantiator(
            Folder.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                        return new Folder(dataObject);
                        }
                }
        );

        DomainObjectFactory.registerInstantiator(
             DocBlobject.BASE_DATA_OBJECT_TYPE, new DomainObjectInstantiator() {
                  public DomainObject doNewInstance(DataObject dataObject) {
                        return new DocBlobject(dataObject);
                        }
                  }
        );

    //    Registering internal portlets

        // Prerequisite to access RecentUpdatedDocsPortlet
        DomainObjectFactory.registerInstantiator(
               RecentUpdatedDocsPortlet.BASE_DATA_OBJECT_TYPE,
               new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new RecentUpdatedDocsPortlet(dataObject);
                    }
               }
        );
    }

}
