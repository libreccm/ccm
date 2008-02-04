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
package com.arsdigita.cms.installer.xml;

import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.ContentType;

import java.math.BigDecimal;

public interface ContentTypeHelper {
    public void setName(String name) ;
    /**  @deprecated */
    public void setLabel(String label) ;      
    /**  @deprecated */
    public String getLabel() ;      
    /**
     *  The labelBundle and labelKey work together to specify where
     *  to locate the given label.  These are the bundle and key use
     *  to create a GlobalizedMessage
     */
    public void setLabelBundle(String labelBundle);        
    public String getLabelBundle();

    /**
     *  The labelBundle and labelKey work together to specify where
     *  to locate the given label.  These are the bundle and key use
     *  to create a GlobalizedMessage
     */
    public void setLabelKey(String labelKey);        
    public String getLabelKey();
    public boolean isInternal();
    public void setInternal(boolean internal);
    /**  @deprecated */
    public void setDescription(String description) ;      
    /**  @deprecated */
    public String getDescription() ;      
    /**
     *  The descriptionBundle and descriptionKey work together to specify where
     *  to locate the given description.  These are the bundle and key use
     *  to create a GlobalizedMessage
     */
    public void setDescriptionBundle(String descriptionBundle);
    public String getDescriptionBundle();

    /**
     *  The descriptionBundle and descriptionKey work together to specify where
     *  to locate the given description.  These are the bundle and key use
     *  to create a GlobalizedMessage
     */
    public void setDescriptionKey(String descriptionKey);        
    public String getDescriptionKey();
    public void setObjectType(String objType);       
    public String getObjectType() ;              
    public void setClassName(String classname) ;      
    public String getClassName() ;      
    public void setParentType(String classname) ;      
    public void setCreateComponent(String createComponent);      
    public AuthoringKit getAuthoringKit() ;      
    public ContentType getContentType();           
    public ContentType createType() ;      
    public AuthoringKit createAuthoringKit();       
    /** @deprecated */
    public void addAuthoringStep(String label, 
                                 String description,
                                 String component,
                                 BigDecimal ordering) ;

    public void addAuthoringStep(String labelKey, 
                                 String labelBundle,
                                 String descriptionKey,
                                 String descriptionBundle,
                                 String component,
                                 BigDecimal ordering) ;

    public void saveType();      
}
