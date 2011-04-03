package com.arsdigita.portalworkspace.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
// import com.arsdigita.kernel.security.LegacyInitializer;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.util.GlobalizationUtil;
import com.arsdigita.ui.UI;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;


/**
 * 
 * @author 
 */
public class PersonalPortalPage extends Page {

    private static final Logger s_log = Logger
                                        .getLogger(PersonalPortalPage.class);

    private final static String URL = (String) GlobalizationUtil.globalize(
                                "portal.ui.base_personal_portal_path").localize();

    private final static String TITLE = (String) GlobalizationUtil.globalize(
                                "portal.ui.base_personal_portal_title").localize();

    private final static String SLASH = "/";

    public final static String PERSONAL_PORTAL_PATH = SLASH + URL + SLASH;

    /**
     * Constructor
     */
    public PersonalPortalPage() {
        addRequestListener(new PersonalPortalPageRequestListener());
        lock();
    }

    /**
     * 
     */
    private class PersonalPortalPageRequestListener implements RequestListener {

        public void pageRequested(RequestEvent e) {

            User user;
            String sUrl = "";
            Party party = Kernel.getContext().getParty();

            if (party == null) {
//              sUrl = LegacyInitializer.getURL(LegacyInitializer.LOGIN_PAGE_KEY);
                sUrl = UI.getLoginPageURL();
            } else {
                try {
                    user = User.retrieve(party.getOID());

                    // try to get the user's workspace first
                    s_log.debug("Attempting to retrieve the personal Workspace " +
                                "for User" + user);
                    Workspace personalWorkspace = Workspace
                                                  .retrievePersonalWorkspace(user);

                    // if it's not there we have to create it
                    if (personalWorkspace == null) {
                        s_log.debug("None found.");

                        // check if there is a base personal portal application
                        s_log.debug("Attempting to retrieve the existing personal-portal");
                        final Workspace personalPortalWorkspace = (Workspace) Application
                              .retrieveApplicationForPath(PERSONAL_PORTAL_PATH);

                        if (personalPortalWorkspace == null) {
                            s_log.debug("None found. Setting up the parent ie personal-portal");
                            new KernelExcursion() {
                                public void excurse() {
                                    setEffectiveParty(Kernel.getSystemParty());
                                    Workspace createdPersonalPortal = Workspace
                                              .createWorkspace(URL, TITLE, null,
	                                                       true);
                                }
                            }.run();
                        }

                        s_log.debug("Setting up the user's personal-portal/"
                                    + user.getID());
                        personalWorkspace = Workspace
                                            .createPersonalWorkspace(user);
                    }

                    sUrl = personalWorkspace.getPath();

                } catch (DataObjectNotFoundException donfe) {
//                  sUrl = LegacyInitializer.getURL(LegacyInitializer.LOGIN_PAGE_KEY);
                    sUrl = UI.getLoginPageURL();
                }
            }

            // Redirect to the user's portal (or login)
            try {
                DispatcherHelper.sendRedirect(e.getPageState().getRequest(), 
                                              e.getPageState().getResponse(), sUrl);
            } catch (java.io.IOException ioe) {
                // this method can't throw an exception in this Interface, so
                // ignore
            }
        }

    }

}
