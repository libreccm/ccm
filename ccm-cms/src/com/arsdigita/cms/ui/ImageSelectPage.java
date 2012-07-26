/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMSConfig;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.util.GlobalizationUtil;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImageSelectPage extends CMSPage {

    private static final Logger S_LOG = Logger.getLogger(ImagesPane.class);
    
    private final static String XSL_CLASS = "CMS Admin";
    private TabbedPane m_tabbedPane;
    private ImageLibraryComponent m_imageLibrary;
    private ImageUploadComponent m_imageUpload;
    private ImageSelectResultPane m_resultPane;
    private BigDecimalParameter m_sectionId;
    private final StringParameter m_imageComponentKey;
    private final MapComponentSelectionModel m_imageComponent;
    private final ImageComponentSelectListener m_selectListener;
    private static final CMSConfig s_conf = CMSConfig.getInstance();
    public static final String CONTENT_SECTION = "section_id";
    public static final String RESULT = "result";

    public ImageSelectPage() {
        super(GlobalizationUtil.globalize("cms.ui.image_select.page_title").localize().toString(), new SimpleContainer());

        setClassAttr("cms-admin");

        m_sectionId = new BigDecimalParameter(CONTENT_SECTION);
        addGlobalStateParam(m_sectionId);

        m_imageComponentKey = new StringParameter("imageComponent");

        ParameterSingleSelectionModel componentModel =
                new ParameterSingleSelectionModel(m_imageComponentKey);
        m_imageComponent =
                new MapComponentSelectionModel(componentModel, new HashMap());
        
        m_selectListener = new ImageComponentSelectListener(m_imageComponent, getResultPane());

        m_tabbedPane = createTabbedPane();
        m_tabbedPane.setIdAttr("page-body");

        add(m_tabbedPane);
        // ActionListener to change the image component state param to the right value
        addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                final PageState ps = event.getPageState();

                if (m_tabbedPane.getCurrentPane(ps).equals(m_imageLibrary)) {
                    m_imageComponent.setSelectedKey(ps, ImageComponent.LIBRARY);
                }
                if (m_tabbedPane.getCurrentPane(ps).equals(m_imageUpload)) {
                    m_imageComponent.setSelectedKey(ps, ImageComponent.UPLOAD);
                }
            }
        });
        
        add(m_resultPane);
        
        addGlobalStateParam(m_imageComponentKey);
    }

    protected ImageLibraryComponent getImageLibraryPane() {
        if (m_imageLibrary == null) {
            m_imageLibrary = new ImageLibraryComponent(ImageComponent.SELECT_IMAGE, this);
            m_imageLibrary.getForm().addInitListener(m_selectListener);
            m_imageLibrary.getForm().addProcessListener(m_selectListener);
            m_imageComponent.getComponentsMap().put(ImageComponent.LIBRARY, m_imageLibrary);
        }
        return m_imageLibrary;
    }

    protected ImageUploadComponent getImageUploadPane() {

        if (m_imageUpload == null) {
            m_imageUpload = new ImageUploadComponent(ImageComponent.SELECT_IMAGE);
            m_imageUpload.getForm().addInitListener(m_selectListener);
            m_imageUpload.getForm().addProcessListener(m_selectListener);
            m_imageComponent.getComponentsMap().put(ImageComponent.UPLOAD, m_imageUpload);
        }
        return m_imageUpload;
    }

    protected ImageSelectResultPane getResultPane() {
        if (m_resultPane == null) {
            m_resultPane = new ImageSelectResultPane();
        }
        return m_resultPane;
    }

    protected TabbedPane createTabbedPane() {
        TabbedPane pane = new TabbedPane();
        pane.setClassAttr(XSL_CLASS);

        addToPane(pane, ImageComponent.LIBRARY, getImageLibraryPane());
        addToPane(pane, ImageComponent.UPLOAD, getImageUploadPane());
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
    protected void addToPane(final TabbedPane pane, final String tabName, final Component comp) {
        if (comp != null) {
            pane.addTab(GlobalizationUtil.globalize("cms.ui.image_" + tabName).localize().toString(), comp);
        }
    }
}
