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
 */
package com.arsdigita.london.contenttypes;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;

/**
 * Loader for <code>ContentType</code> <code>ESDService</code>.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ESDServiceLoader.java 287 2005-02-22 00:29:02Z sskracic $
 * 
 */
public class ESDServiceLoader extends AbstractContentTypeLoader {

  private static final String[] TYPES = {
      "/WEB-INF/content-types/com/arsdigita/london/contenttypes/ESDService.xml"
  };

  /**
   * @see com.arsdigita.cms.contenttypes.AbstractContentTypeLoader#getTypes()
   */
  public String[] getTypes() {
    return TYPES;
  }

}