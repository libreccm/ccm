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

package com.arsdigita.london.terms.importer;

import java.net.URL;

import org.apache.log4j.Logger;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;


public class TermItemBuilder {
        
    private static final Logger s_log = 
        Logger.getLogger(TermItemBuilder.class);

    private Domain m_domain;    
        
    public void findDomain(String url) 
        throws Exception {
        m_domain = Domain.find(new URL(url));
        s_log.debug("Domain " + m_domain);
    }
    
    public void addMapping(Integer id,
                           String path) {
        Assert.exists(m_domain, Domain.class);
        Assert.exists(id, Integer.class);
        Assert.exists(path, String.class);

        Term term = m_domain.getTerm(id);
        
        ContentSection section = null;
        section = ContentSection.getSectionForPath(path);        

        String[] bits = StringUtils.split(
            path.substring(section.getURL().length(), 
                           path.length()),
            '/');

        Folder folder = section.getRootFolder();
        ContentItem item = null;
        for (int i = 0 ; i < bits.length && folder != null; i++) {
            if (i == (bits.length - 1)) {
                item = folder.getItem(bits[i], false);
            } else {
                folder = (Folder)folder.getItem(bits[i], true);
            }
        }
        
        if (item == null) {
            s_log.error("Couldn't resolve item " + path);
            return;
        }
        
        term.addObject(item);
        if (s_log.isDebugEnabled()) {
            s_log.debug("Term " + term + " item " + path);
        }
        term.save();
    }
    
}
