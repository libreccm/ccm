/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
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

package com.arsdigita.cms.ui.report;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;

/**
 * UI model for a report.
 * A report has a name and a component that displays the report.
 * 
 * @author <a href="https://sourceforge.net/users/thomas-buckel/">thomas-buckel</a>
 * @author <a href="https://sourceforge.net/users/tim-permeance/">tim-permeance</a>
 */
public class Report {

   private final String m_key;
   private final String m_name;
   private final Component m_component;
   
   public Report(String key, Component component) {
       Assert.exists(key, "Key for report is required");
       Assert.isTrue(key.length() > 0, "Key for report must not be empty");
       Assert.exists(component, "Component for report is required");
       
       m_key = key;
       m_name = gz(m_key).localize().toString();
       m_component = component;
   }
   
   public String getKey() {
       return m_key;
   }
   
   public String getName() {
       return m_name;
   }
   
   public Component getComponent() {
       return m_component;
   }
   
    protected final static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }
    
}
