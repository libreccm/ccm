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

package com.arsdigita.london.theme;

import com.arsdigita.web.Application;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;

import org.apache.log4j.Logger;

public class ThemeApplication extends Application {

    public static final String DEFAULT_THEME = "defaultTheme";
    
    private static final Logger s_log = Logger.getLogger(Theme.class);

    public static final String BASE_DATA_OBJECT_TYPE 
        = "com.arsdigita.london.theme.ThemeApplication";

    public static ThemeConfig s_config = new ThemeConfig();

    static {
        s_config.load();
    }

    public static ThemeConfig getConfig() {
        return s_config;
    }

    public ThemeApplication(DataObject obj) {
        super(obj);
    }

    public ThemeApplication(OID oid) 
        throws DataObjectNotFoundException {

        super(oid);
    }    

    public String getContextPath() {
        return "/ccm-ldn-theme";
    }

    public String getServletPath() {
        return "/theme-files";
    }

    public Theme getDefaultTheme() {
        DataObject dObj = (DataObject) get( DEFAULT_THEME );
        if( null == dObj ) return null;

        return new Theme( dObj );
    }

    public void setDefaultTheme( Theme theme ) {
        set( DEFAULT_THEME, theme );
    }
}
