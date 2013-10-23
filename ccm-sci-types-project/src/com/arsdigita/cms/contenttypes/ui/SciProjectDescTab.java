package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

/**
 * Displays the description text for a project.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectDescTab implements GenericOrgaUnitTab {

    public final Logger logger = Logger.getLogger(SciProjectDescTab.class);
    private String key;
    
    @Override
    public String getKey() {
        return key;
    }
    
    @Override
    public void setKey(final String key) {
        this.key = key;
    }

    public boolean hasData(final GenericOrganizationalUnit orgaunit,
                           final PageState state) {
        final long start = System.currentTimeMillis();
        boolean result;
        final Desc desc = getData(orgaunit);

        if (desc.getDesc() == null) {
            result = false;
        } else {
            result = !desc.getDesc().trim().isEmpty();
        }

        logger.debug(String.format("Needed %d ms to determine if project '%s' "
                                   + "has a description.",
                                   System.currentTimeMillis() - start,
                                   orgaunit.getName()));
        return result;
    }

    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final long start = System.currentTimeMillis();
        final Desc desc = getData(orgaunit);

        final Element descTabElem = parent.newChildElement("projectDescription");
        
        if ((desc.getShortDesc() != null) 
            && !desc.getShortDesc().trim().isEmpty()) {
            final Element shortDescElem = descTabElem.newChildElement("shortDescription");
            shortDescElem.setText(desc.getShortDesc());
        }
        
        final Element descElem = descTabElem.newChildElement("description");
        descElem.setText(desc.getDesc());

        if ((desc.getFunding() != null) 
            && !desc.getFunding().trim().isEmpty()) {
            final Element fundingElem = descTabElem.newChildElement("funding");
            fundingElem.setText(desc.getFunding());
        }

        if ((desc.getFundingVolume() != null)
            && !desc.getFundingVolume().trim().isEmpty()) {
            final Element volumeElem = descTabElem.newChildElement("fundingVolume");
            volumeElem.setText(desc.getFundingVolume());
        }

        logger.debug(String.format("Generated XML for description tab of "
                                   + "project '%s' in %d ms",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    private Desc getData(final GenericOrganizationalUnit orgaunit) {
        if (!(orgaunit instanceof SciProject)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of "
                    + "'com.arsdigita.cms.contenttypes.SciProject'. Provided "
                    + "object is of type '%s'",
                    orgaunit.getClass().getName()));
        }

        final SciProject project = (SciProject) orgaunit;
        final Desc desc = new Desc();
        desc.setShortDesc(project.getProjectShortDescription());
        desc.setDesc(project.getProjectDescription());
        desc.setFunding(project.getFunding());
        desc.setFundingVolume(project.getFundingVolume());
        return desc;
    }

    private class Desc {

        private String shortDesc;
        private String desc;
        private String funding;
        private String fundingVolume;

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

        public String getFunding() {
            return funding;
        }

        public void setFunding(final String funding) {
            this.funding = funding;
        }

        public String getFundingVolume() {
            return fundingVolume;
        }

        public void setFundingVolume(final String fundingVolume) {
            this.fundingVolume = fundingVolume;
        }
    }
}
