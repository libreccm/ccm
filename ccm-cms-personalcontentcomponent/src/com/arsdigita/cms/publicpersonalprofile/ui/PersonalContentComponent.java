package com.arsdigita.cms.publicpersonalprofile.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonBundle;
import com.arsdigita.cms.publicpersonalprofile.ContentGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.navigation.ui.AbstractComponent;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.xml.Element;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PersonalContentComponent extends AbstractComponent {

    private ContentGenerator generator;

    public ContentGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(final ContentGenerator generator) {
        this.generator = generator;
    }

    public Element generateXML(final HttpServletRequest request, final HttpServletResponse response) {
        ContentItem item = (ContentItem) getObject();

        if (!(item instanceof GenericPersonBundle) || !(item.isLive())) {
            return null;
        }

        if (!ContentItem.LIVE.equals(item.getVersion())) {
            item = item.getLiveVersion();
        }

        final Party currentParty;
        if (Kernel.getContext().getParty() == null) {
            currentParty = Kernel.getPublicUser();
        } else {
            currentParty = Kernel.getContext().getParty();
        }

        final PermissionDescriptor read = new PermissionDescriptor(
                PrivilegeDescriptor.get(com.arsdigita.cms.SecurityManager.CMS_READ_ITEM),
                item,
                currentParty);
        if (!PermissionService.checkPermission(read)) {
            throw new LoginSignal(request);
        }

        final ContentBundle bundle = (ContentBundle) item;
        GenericPerson baseItem = (GenericPerson) bundle.getInstance(GlobalizationHelper.
                getNegotiatedLocale().getLanguage());
        if (baseItem == null) {
            // get the primary instance instead (fallback)
            baseItem = (GenericPerson) bundle.getPrimaryInstance();
        }
        
        final Element parent = new Element("ppp:profile", "http://www.arsdigita.com/PublicPersonalProfile/1.0");                
        generator.generateContent(parent, baseItem, PageState.getPageState(), baseItem.getLanguage());
        
        return parent;
    }

}
