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
package com.arsdigita.cms.formbuilder;

import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.PersistentOptionGroup;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.WidgetLabel;

import com.arsdigita.cms.ACSObjectFactory;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainService;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A crude class to copy a form and all its widgets.
 *
 * This class should be replaced ASAP by a more
 * generic ACSObject copier and a 'CopyableACSObject'
 * interface
 */
class FormCopier extends DomainService {
    private static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(FormCopier.class);

    private HashMap m_copied = new HashMap();

    public PersistentForm copyForm(PersistentForm form) {
        PersistentForm tgt = (PersistentForm)copyFormSection(form);

        return tgt;
    }

    public PersistentFormSection copyFormSection(PersistentFormSection src) {
        PersistentFormSection tgt = (PersistentFormSection)copyObject(src);

        if( s_log.isDebugEnabled() ) {
            s_log.debug("Copying form section " + src.getClass().getName());
        }

        copyDataObjectAssociation(getDataObject(src),
                                  getDataObject(tgt),
                                  "listeners");
        copyDataObjectAssociation(getDataObject(src),
                                  getDataObject(tgt),
                                  "component");
        tgt.save();

        s_log.debug("Done copying, is now ");

        return tgt;
    }

    protected FormSectionWrapper copyFormSectionWrapper(FormSectionWrapper src) {
        FormSectionWrapper tgt = (FormSectionWrapper)copyObject(src);
        
        if( s_log.isDebugEnabled() ) {
            s_log.debug("Copying form section wrapper " + src.getOID());
        }

        tgt.setFormSectionItem(src.getFormSectionItem());
        tgt.setVersion(ContentItem.LIVE);

        s_log.debug("Done copying form section wrapper ");
        
        return tgt;
    }

    protected WidgetLabel copyWidgetLabel(WidgetLabel src) {
        WidgetLabel tgt = (WidgetLabel)copyObject(src);
        
        if( s_log.isDebugEnabled() ) {
            s_log.debug("Copying widget label " + src.getOID());
        }
        
        PersistentWidget srcWgt = null;
        try {
            srcWgt = src.getWidget();
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException(
                "cannot find widget for label " + src.getOID(), ex);
        }

        DataObject dstObj = copySingleObject( getDataObject( srcWgt ) );

        PersistentWidget dstWgt = (PersistentWidget)
            DomainObjectFactory.newInstance( dstObj );
        
        tgt.setWidget(dstWgt);

        s_log.debug("Done copying widget label ");
        
        return tgt;
    }


    public PersistentOptionGroup copyOptionGroup(PersistentOptionGroup src) {
        PersistentOptionGroup tgt = (PersistentOptionGroup)copyWidget(src);

        if( s_log.isDebugEnabled() ) {
            s_log.debug("Copying option group " + src.getClass().getName());
        }

        copyDataObjectAssociation(getDataObject(src),
                                  getDataObject(tgt),
                                  "component");

        s_log.debug("Done copying, is now ");

        return tgt;
    }


    private PersistentWidget copyWidget(PersistentWidget src) {
        PersistentWidget tgt = (PersistentWidget)copyObject(src);

        if( s_log.isDebugEnabled() ) {
            s_log.debug("Copying form widget " + src.getClass().getName());
        }

        copyDataObjectAssociation(getDataObject(src),
                                  getDataObject(tgt),
                                  "listeners");

        s_log.debug("Done form widget");

        return tgt;
    }


    private ACSObject copyObject(ACSObject src) {
        String objectType = src.getSpecificObjectType();
        String javaClass = (String)get(src, ACSObject.DEFAULT_DOMAIN_CLASS);

        if( s_log.isDebugEnabled() ) {
            s_log.debug("Copying object " + src.getClass().getName() + " " + src.getOID());
        }

        Assert.exists(objectType, String.class);
        Assert.exists(javaClass, String.class);

        // Attempt to instantiate the copy
        ACSObject tgt;
        try {
            tgt =
                (ACSObject)ACSObjectFactory.createACSObject(javaClass, objectType);
        } catch (Exception ex) {
            throw new UncheckedWrapperException( 
                (String) GlobalizationUtil.globalize(
                    "cms.formbuilder.cannot_create_acsobject").localize(),  ex);
        }

        copyDataObjectAttributes(getDataObject(src),
                                 getDataObject(tgt));

        s_log.debug("Done copying object");

        return tgt;
    }

    private void copyDataObjectAssociation(DataObject src,
                                           DataObject tgt,
                                           String name) {
        if( s_log.isDebugEnabled() ) {
            s_log.debug("Copy association " + src.get("objectType") + " " + name);
        }
        DataAssociation tgtAssoc = (DataAssociation)tgt.get(name);

        DataAssociation srcAssoc = (DataAssociation)src.get(name);
        DataAssociationCursor daCursor = ((DataAssociation)srcAssoc).cursor();

        while (daCursor.next()) {
            if( s_log.isDebugEnabled() ) {
                s_log.debug("Copy association object " + name + 
                            " " + daCursor.getDataObject());
            }
            DataObject copy = copySingleObject(daCursor.getDataObject());

            DataObject tgtLink = tgtAssoc.add(copy);
            if (tgtLink != null) {
                if( s_log.isDebugEnabled() ) {
                    s_log.debug("Copy link " + daCursor.getLink());
                }
                copyDataObjectAttributes(daCursor.getLink(),
                                         tgtLink);
            }
        }

        s_log.debug("Done association");
    }

    private DataObject copySingleObject(DataObject src) {
        ACSObject srcObj = (ACSObject) DomainObjectFactory.newInstance(src);

        ACSObject tgtObj = (ACSObject) m_copied.get( srcObj.getOID().toString() );
        if( null != tgtObj ) {
            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Using cached copy of " + srcObj.getOID() );
            }
        } else {
            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Copying " + srcObj.getClass().getName() + " " +
                             srcObj.getOID() );
            }

            if (srcObj instanceof PersistentFormSection) {
                tgtObj = copyFormSection((PersistentFormSection)srcObj);
            } else if (srcObj instanceof PersistentOptionGroup) {
                tgtObj = copyOptionGroup((PersistentOptionGroup)srcObj);
            } else if (srcObj instanceof FormSectionWrapper) {
                tgtObj = copyFormSectionWrapper((FormSectionWrapper)srcObj);
            } else if (srcObj instanceof PersistentWidget) {
                tgtObj = copyWidget((PersistentWidget)srcObj);
            } else if (srcObj instanceof WidgetLabel) {
                tgtObj = copyWidgetLabel((WidgetLabel)srcObj);
            } else {
                tgtObj = copyObject(srcObj);
            }

            m_copied.put( srcObj.getOID().toString(), tgtObj );
        }

        return getDataObject(tgtObj);
    }


    private void copyDataObjectAttributes(DataObject src,
                                          DataObject tgt) {
        ObjectType type = src.getOID().getObjectType();
        Collection keyAttrNames = ACSObjectFactory.getKeyAttributeNames(type);

        for (Iterator i = type.getProperties(); i.hasNext(); ) {
            Property prop = (Property) i.next();
            if (!prop.isAttribute()) {
                continue;
            }
            String attrName = prop.getName();

            // Do not copy primary key attributes
            if (!keyAttrNames.contains(attrName)) {
                tgt.set(attrName, src.get(attrName));
            }
        }
    }
}
