package com.arsdigita.bundle.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.ui.GenericOrgaUnitTab;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.navigation.ui.AbstractComponent;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A wrapper around {@link GenericOrgaUnitTab} which allows it to use the 
 * output of an implementation of {@link GenericOrgaUnitTab} independently from
 * a {@link GenericOrganizationalUnit} item. This allows it to create an
 * special JSP and the show a tab as a navigation point.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrgaUnitTabComponent extends AbstractComponent {

    private Page page;
    private OID orgaunitOid;
    private GenericOrgaUnitTab tab;

    public void setPage(final Page page) {
        this.page = page;
    }

    /**
     * The OID of the orga unit to use. Use the OID of the master version (can 
     * be found in the overview tab in the content-center in the stable link) 
     * here
     * @param oid 
     */
    public void setOrgaUnit(final String oid) {
        this.orgaunitOid = OID.valueOf(oid);
    }

    public void setOrgaUnitTab(final GenericOrgaUnitTab tab) {
        this.tab = tab;
    }

    public Element generateXML(final HttpServletRequest request,
                               final HttpServletResponse response) {
        final PageState state;
        try {
            state = new PageState(page, request, response);
        } catch (ServletException ex) {
            throw new UncheckedWrapperException(ex);
        }

        GenericOrganizationalUnit orgaunit =
                                  (GenericOrganizationalUnit) DomainObjectFactory.
                newInstance(orgaunitOid);
        if ((DispatcherHelper.getDispatcherPrefix(request) == null) 
            || !DispatcherHelper.getDispatcherPrefix(request).equals("preview")) {
            orgaunit = (GenericOrganizationalUnit) orgaunit.getLiveVersion();
        }

        final Element tabsElem = new Element("orgaUnitTabs");
        final Element selectedTabElem = tabsElem.newChildElement("selectedTab");

        if (orgaunit != null) {
            tab.generateXml(orgaunit, selectedTabElem, state);
        }

        return tabsElem;
    }
}
