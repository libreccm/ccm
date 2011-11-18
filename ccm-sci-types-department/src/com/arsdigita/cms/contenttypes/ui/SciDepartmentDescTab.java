package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentDescTab implements GenericOrgaUnitTab {

    public final Logger logger = Logger.getLogger(SciDepartmentDescTab.class);

    public boolean hasData(final GenericOrganizationalUnit orgaunit) {
        final long start = System.currentTimeMillis();
        boolean result;
        final Desc desc = getData(orgaunit);

        if (desc.getDesc() == null) {
            result = false;
        } else {
            result = !desc.getDesc().trim().isEmpty();
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Needed %d ms to determine if department '%s' "
                                       + "has a description.",
                                       System.currentTimeMillis() - start,
                                       orgaunit.getName()));
        }

        return result;
    }

    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final long start = System.currentTimeMillis();
        final Desc desc = getData(orgaunit);

        final Element descTabElem = parent.newChildElement("departmentDescription");
        
        if ((desc.getShortDesc() != null)
            && !desc.getShortDesc().trim().isEmpty()) {
            final Element shortDescElem = descTabElem.newChildElement(
                    "shortDescription");
            shortDescElem.setText(desc.getShortDesc());
        }

        final Element descElem = descTabElem.newChildElement("description");
        descElem.setText(desc.getDesc());

        logger.debug(String.format("Generated XML for description tab of "
                                   + "department '%s' in %d ms",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    private Desc getData(final GenericOrganizationalUnit orgaunit) {
        if (!(orgaunit instanceof SciDepartment)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of "
                    + "'com.arsdigita.cms.contenttypes.SciDepartment'. Provided "
                    + "object is of type '%s'",
                    orgaunit.getClass().getName()));
        }

        final SciDepartment department = (SciDepartment) orgaunit;
        final Desc desc = new Desc();
        desc.setShortDesc(department.getDepartmentShortDescription());
        desc.setDesc(department.getDepartmentDescription());
        return desc;
    }

    private class Desc {

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
