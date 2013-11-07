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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

/**
 * The description tab for an SciDepartment. Displays the text stored in the description property
 * of a SciDepartment item.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentDescTab implements GenericOrgaUnitTab {

    private static final Logger LOGGER = Logger.getLogger(SciDepartmentDescTab.class);
    private String key;
    
    @Override
    public String getKey() {
        return key;
    }
    
    @Override
    public void setKey(final String key) {
        this.key = key;
    }

    @Override
    public boolean hasData(final GenericOrganizationalUnit orgaunit,
                           final PageState state) {
        final long start = System.currentTimeMillis();
        boolean result;
        final Description desc = getData(orgaunit);

        if (desc.getDesc() == null) {
            result = false;
        } else {
            result = !desc.getDesc().trim().isEmpty();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Needed %d ms to determine if department '%s' "
                                       + "has a description.",
                                       System.currentTimeMillis() - start,
                                       orgaunit.getName()));
        }

        return result;
    }

    @Override
    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final long start = System.currentTimeMillis();
        final Description desc = getData(orgaunit);

        final Element descTabElem = parent.newChildElement("departmentDescription");

        if ((desc.getShortDesc() != null)
            && !desc.getShortDesc().trim().isEmpty()) {
            final Element shortDescElem = descTabElem.newChildElement(
                    "shortDescription");
            shortDescElem.setText(desc.getShortDesc());
        }

        final Element descElem = descTabElem.newChildElement("description");
        descElem.setText(desc.getDesc());

        LOGGER.debug(String.format("Generated XML for description tab of "
                                   + "department '%s' in %d ms",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    private Description getData(final GenericOrganizationalUnit orgaunit) {
        if (!(orgaunit instanceof SciDepartment)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of "
                    + "'com.arsdigita.cms.contenttypes.SciDepartment'. Provided "
                    + "object is of type '%s'",
                    orgaunit.getClass().getName()));
        }

        final SciDepartment department = (SciDepartment) orgaunit;
        final Description desc = new Description();
        desc.setShortDesc(department.getDepartmentShortDescription());
        desc.setDesc(department.getDepartmentDescription());
        return desc;
    }

    /**
     * Internal helper class to transfer the description data between methods.
     * 
     */
    private class Description {

        public Description() {
            //Nothing
        }
        
        private String shortDesc;
        private String desc;

        public String getShortDesc() {
            return shortDesc;
        }

        public void setShortDesc(final String shortDesc) {
            this.shortDesc = shortDesc;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(final String desc) {
            this.desc = desc;
        }

    }
}
