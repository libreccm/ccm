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
package com.arsdigita.bebop.jsp;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Image;
import javax.servlet.jsp.JspException;

/**
 * Tag handler for definining an Image.
 */
public class DefineImage extends DefineComponent {
    public static final String versionId = "$Id: DefineImage.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Image m_image;
    private String m_src;
    private String m_alt;
    private String m_height;
    private String m_width;
    private String m_border;

    public int doStartTag() throws JspException {
        m_image = new Image(m_src);
        m_image.setAlt(m_alt);
        m_image.setWidth(m_width);
        m_image.setHeight(m_height);
        m_image.setBorder(m_border);
        return super.doStartTag();
    }

    public Component getComponent() {
        return m_image;
    }

    public void setSrc(String src) {
        m_src = src;
    }

    public void setAlt(String alt) {
        m_alt = alt;
    }

    public void setHeight(String height) {
        m_height = height;
    }

    public void setWidth(String width) {
        m_width = width;
    }

    public void setBorder(String border) {
        m_border = border;
    }
}
