package com.arsdigita.cms.contenttypes.ui.genericorganization;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.CMSContext;
import com.arsdigita.cms.CMSExcursion;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.contenttypes.OrganizationRole;
import com.arsdigita.cms.contenttypes.OrganizationRoleCollection;
import com.arsdigita.cms.dispatcher.XMLGenerator;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class OrganizationRolePanel extends SimpleComponent {

    private static final Logger logger = Logger.getLogger(OrganizationRolePanel.class);

    //private PageParameter m_page;
    private boolean m_showAllRoles = false;
    //private static final String versionId = "$Id: OrganizationRolePanel.java 2009-06-01T10:42+02:00";

    public OrganizationRolePanel() {
        super();
    }

    @Override
    public void register(Page p) {
        super.register(p);

        addGlobalStateParams(p);
    }

    public void addGlobalStateParams(Page p) {
    }

    protected XMLGenerator getXMLGenerator(PageState state, ContentItem item) {
        ContentSection section = null;

        try {
            section = CMS.getContext().getContentSection();
        } catch (Exception e) {
        }

        if (section == null) {
            logger.debug(String.format("Item id %s-%s-%s", item.getOID().toString(), item.getContentSection().toString(), item.toString()));
            section = item.getContentSection();
            CMS.getContext().setContentSection(section);
        }

        return section.getXMLGenerator();
    }

    public void setShowAllRoles(boolean showAll) {
        this.m_showAllRoles = showAll;
    }

    protected ContentItem getContentItem(PageState state) {
        CMSContext context = CMS.getContext();

        if (!context.hasContentItem()) {
            return null;
        }

        return context.getContentItem();
    }

    protected OrganizationRole[] getOrganizationRoles(ContentItem item, final PageState state) {
        GenericOrganization orga = (GenericOrganization) item;
        OrganizationRoleCollection roles = orga.getOrganizationRoles();

        OrganizationRole[] page = new OrganizationRole[(int) roles.size()];
        int i = 0;
        while (roles.next()) {
            page[i] = roles.getOrganizationRole();
            i++;
        }
        return page;
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {
        ContentItem item = getContentItem(state);

        if (!isVisible(state) ||
                (item == null) ||
                !(item instanceof GenericOrganization)) {
            logger.debug("Skipping");
            return;
        }

        generateXML(item, parent, state);
    }

    public void generateXML(ContentItem item, Element element, PageState state) {
        Element content = element.newChildElement("cms:organizationRolePanel", CMS.CMS_XML_NS);
        exportAttributes(content);

        XMLGenerator xmlGenerator = getXMLGenerator(state, item);

        OrganizationRole roles[] = getOrganizationRoles(item, state);
        for(int i = 0; i < roles.length; i++) {
            generateRoleXML(state, content, roles[i], xmlGenerator);
        }
    }

    protected void generateRoleXML(final PageState state, final Element parent, final ContentItem role, final XMLGenerator xmlGenerator) {
        CMSExcursion excursion = new CMSExcursion() {

            @Override
            protected void excurse() throws ServletException, IOException {
                setContentItem(role);
                xmlGenerator.generateXML(state, parent, null);
            }
        };

        try {
            excursion.run();
        } catch(ServletException e) {
            throw new UncheckedWrapperException("excursion failed", e);
        } catch(IOException e) {
            throw new UncheckedWrapperException("excursion failed", e);
        }
    }
}



