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
package com.arsdigita.cms;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;

/**
 * This class contains a collection of {@link CategoryTemplateMapping}s
 *
 * @see DomainCollection
 * @see DataCollection
 * @see ItemTemplateCollection
 *
 * @version $Id: CategoryTemplateCollection.java 754 2005-09-02 13:26:17Z sskracic $
 */
public class CategoryTemplateCollection extends TemplateCollection {

  /** 
   * Constructor.
   *
   * @see com.arsdigita.cms.TemplateCollection
   **/
  public CategoryTemplateCollection(DataCollection dataCollection) {
    super(dataCollection);
  }
  
  /**
   * Returns a <code>DomainObject</code> for the current position in
   * the collection.
   *
   */
  public DomainObject getDomainObject() {
    return new CategoryTemplateMapping(m_dataCollection.getDataObject());
  }

  /**
   * Return the current template
   */
  public Template getTemplate() {
    CategoryTemplateMapping m = (CategoryTemplateMapping)getDomainObject();
    if(m == null) return null;
    return m.getTemplate();
  }

  /**
   * Return the current use context
   */
  public String getUseContext() {
    CategoryTemplateMapping m = (CategoryTemplateMapping)getDomainObject();
    if(m == null) return null;
    return m.getUseContext();
  }

  /**
   * Return true if the current template is the default for its context
   */
  public Boolean isDefault() {
    CategoryTemplateMapping m = (CategoryTemplateMapping)getDomainObject();
    if(m == null) return null;
    return m.isDefault();
  }

}
