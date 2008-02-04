package com.arsdigita.london.cms.freeform.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;

/**
 * A sample Bebop Page class for JSP integration
 *
 * @author (a href="mailto:phong@arsdigita.com">Phong Nguyen</a>
 **/
public class JSPViewAssetPage extends Page {

    public JSPViewAssetPage() {
        super();

        Component c = new FreeformAssetView();
        c.setKey("viewAsset");
        c.setIdAttr("viewAsset");
        add(c);

        lock();
    }

}
