/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.CMSConfig;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImageSelectPage extends CMSPage {

    private final static String XSL_CLASS = "CMS Admin";
    private TabbedPane m_tabbedPane;
    private ImagesPane m_imagePane;
    private BigDecimalParameter m_sectionId;
    private static final CMSConfig s_conf = CMSConfig.getInstance();
    private static final boolean LIMIT_TO_CONTENT_SECTION = false;
    public static final String CONTENT_SECTION = "section_id";

    public ImageSelectPage() {
        super(GlobalizationUtil.globalize("cms.ui.item_search.page_title").localize().toString(), new SimpleContainer());

        setClassAttr("cms-admin");

        m_sectionId = new BigDecimalParameter(CONTENT_SECTION);
        addGlobalStateParam(m_sectionId);

//        m_imagePane = new ImagesPane();
    }
}
