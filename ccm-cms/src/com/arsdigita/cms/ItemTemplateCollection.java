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
package com.arsdigita.cms;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.mimetypes.MimeType;

/**
 * <p>This class contains a collection of {@link
 * com.arsdigita.cms.ItemTemplateMapping item template mappings}.</p>
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ItemTemplateCollection.java 287 2005-02-22 00:29:02Z sskracic $
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 * @see com.arsdigita.cms.ItemTemplateCollection
 */
class ItemTemplateCollection extends TemplateCollection {
    public static final String versionId = "$Id: ItemTemplateCollection.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

  /**
   * Constructor.
   *
   **/
  public ItemTemplateCollection(DataCollection dataCollection) {
    super(dataCollection);
  }

  /**
   * Returns a <code>DomainObject</code> for the current position in
   * the collection.
   *
   */
  public DomainObject getDomainObject() {
    return DomainObjectFactory.newInstance
        (m_dataCollection.getDataObject());
  }

    /**
     * Return the current template
     */
    public Template getTemplate() {
        ItemTemplateMapping m = (ItemTemplateMapping)getDomainObject();
        if(m == null) return null;
        return m.getTemplate();
    }

    /**
     * Return the current content item
     */
    public ContentItem getContentItem() {
        ItemTemplateMapping m = (ItemTemplateMapping)getDomainObject();
        if(m == null) return null;
        return m.getContentItem();
    }

  /**
   * Return the current use context
   */
  public String getUseContext() {
    return (String) m_dataCollection.get("useContext");
  }

    /**
     *  this is the mime type for this context but does not necessarily
     *  have to be the same mime type that is returned by calling
     *  getTemplate().getMimeType().  This can return null
     */
    public MimeType getMimeType() {
        DataObject object = (DataObject)m_dataCollection.get("mimeType");
        if (object != null) {
            return new MimeType(object);
        } else {
            return super.getMimeType();
        }
    }

  /**
   * Return true if the current template is the default for its context
   */
  public Boolean isDefault() {
    // FIXME: Why is this here ? The implementation is definitely bogus
    // [lutter]
    return new Boolean(true);
  }
}
