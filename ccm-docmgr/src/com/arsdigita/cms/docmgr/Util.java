package com.arsdigita.cms.docmgr;

import com.arsdigita.bebop.PageState;
import com.arsdigita.web.LoginSignal;

public class Util {
    public static void redirectToLoginPage(PageState ps) {
        throw new LoginSignal(ps.getRequest());
    }
}
