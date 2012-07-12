/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
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
    private ImageLibraryComponent m_imageLibrary;
    private ImageUploadComponent m_imageUpload;
    private BigDecimalParameter m_sectionId;
    private static final CMSConfig s_conf = CMSConfig.getInstance();
    private static final boolean LIMIT_TO_CONTENT_SECTION = false;
    public static final String CONTENT_SECTION = "section_id";

    public ImageSelectPage() {
        super(GlobalizationUtil.globalize("cms.ui.item_search.page_title").localize().toString(), new SimpleContainer());

        setClassAttr("cms-admin");

        m_sectionId = new BigDecimalParameter(CONTENT_SECTION);
        addGlobalStateParam(m_sectionId);

        m_tabbedPane = createTabbedPane();
        m_tabbedPane.setIdAttr("page-body");
        add(m_tabbedPane);

    }

    protected ImageLibraryComponent getImageLibraryPane() {
        if (m_imageLibrary == null) {
            m_imageLibrary = new ImageLibraryComponent(ImageComponent.SELECT_IMAGE);
//        library.getForm().addInitListener(this);
//        library.getForm().addProcessListener(this);
        }
        return m_imageLibrary;
    }

    protected ImageUploadComponent getImageUploadPane() {

        if (m_imageUpload == null) {
            m_imageUpload = new ImageUploadComponent(ImageComponent.SELECT_IMAGE);
//        upload.getForm().addInitListener(this);
//        upload.getForm().addProcessListener(this);
        }
        return m_imageUpload;
    }

    protected TabbedPane createTabbedPane() {
        TabbedPane pane = new TabbedPane();
        pane.setClassAttr(XSL_CLASS);

        addToPane(pane, "library", getImageLibraryPane());
        addToPane(pane, "upload", getImageUploadPane());
        pane.setDefaultPane(m_imageLibrary);

        return pane;
    }

    /**
     * Adds the specified component, with the specified tab name, to the tabbed
     * pane only if it is not null.
     *
     * @param pane The pane to which to add the tab
     * @param tabName The name of the tab if it's added
     * @param comp The component to add to the pane
     */
    protected void addToPane(TabbedPane pane, String tabName, Component comp) {
        if (comp != null) {
            pane.addTab(GlobalizationUtil.globalize("cms.ui.item_search." + tabName).localize().toString(), comp);
        }
    }

    /* Listeners */
    
    /** 
     * InitListener
     * 
     * this init listener selects the object with the submitted oid
     */
//    private init() {
//        
//    }

    /**
     * ProcessListener
     * 
     * this process listener 
     */
}
