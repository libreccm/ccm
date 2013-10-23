/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.cms.contentassets;

import com.arsdigita.auditing.AuditingObserver;
import com.arsdigita.auditing.BasicAuditTrail;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class GenericOrgaUnitTextAsset extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE
                               = "com.arsdigita.cms.contentassets.GenericOrgaUnitTextAsset";

//    static {
//        DomainObjectFactory.registerInstantiator(
//                BASE_DATA_OBJECT_TYPE,
//                new DomainObjectInstantiator() {
//
//                    @Override
//                    public DomainObjectInstantiator resolveInstantiator(final DataObject dataObject) {
//                        return this;
//                    }
//
//                    @Override
//                    protected DomainObject doNewInstance(final DataObject dataObject) {
//                        return new GenericOrgaUnitTextAsset(dataObject);
//                    }
//
//                });
//    }

    public static final String TEXT_ASSET_NAME = "textAssetName";
    public static final String CONTENT = "content";
    public static final String ORGAUNIT = "orgaunit";
    public static final String TEXT_ASSETS = "textAssets";
//    public static final String AUDIT = "auditing";
//    public static final String CREATION_DATE = AUDIT + "." + BasicAuditTrail.CREATION_DATE;
//    private BasicAuditTrail auditTrail;
//    private boolean isNew = false;

    private GenericOrgaUnitTextAsset() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public GenericOrgaUnitTextAsset(final DataObject dataObject) {
        super(dataObject);
    }

    public static GenericOrgaUnitTextAsset create(final GenericOrganizationalUnit orgaunit,
                                                  final String textAssetName) {
        final GenericOrgaUnitTextAsset textAsset = new GenericOrgaUnitTextAsset();
        textAsset.set(ORGAUNIT, orgaunit);
        textAsset.set(TEXT_ASSET_NAME, textAssetName);

        return textAsset;
    }

//    @Override
//    protected void initialize() {
//        super.initialize();

//        final DataObject dataObject = (DataObject) get(AUDIT);
//        if (dataObject == null) {
//            auditTrail = BasicAuditTrail.retrieveForACSObject(this);
//        } else {
//            auditTrail = new BasicAuditTrail(dataObject);
//        }
        
//        addObserver(new AuditingObserver(auditTrail));
//    }
    
    public String getTextAssetName() {
        return (String) get(TEXT_ASSET_NAME);
    }
    
    public void setTextAssetName(final String textAssetName) {
        set(TEXT_ASSET_NAME, textAssetName);
    }
    
    public String getContent() {
        return (String) get(CONTENT);
    }
    
    public void setContent(final String content) {
        set(CONTENT, content);
    }
    
    public GenericOrganizationalUnit getOwner() {
        final DataObject dataObject = (DataObject) get(ORGAUNIT);
        Assert.exists(dataObject, DataObject.class);
        
        return (GenericOrganizationalUnit) DomainObjectFactory.newInstance(dataObject);
    }
    
//    public User getTextAssetAuthor() {
//        return auditTrail.getCreationUser();
//    }
//    
//    public Date getCreationDate() {
//        return auditTrail.getCreationDate();
//    }
    
    public static DataCollection getTextAssets(final GenericOrganizationalUnit orgaunit) {
        Assert.exists(orgaunit, GenericOrganizationalUnit.class);
        
        final DataCollection textAssets = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        textAssets.addEqualsFilter(ORGAUNIT, orgaunit.getID());
        
        return textAssets;
    }
    
//    @Override
//    protected void beforeSave() {
//        super.beforeSave();
//
//        if (isNew()) {
//            isNew = true;
//        }
//    }
//
//    @Override
//    protected void afterSave() {
//        super.afterSave();
//
//        if (isNew) {
//            PermissionService.setContext(this, getOwner());
//            isNew = false;
//        }
//    }

}
