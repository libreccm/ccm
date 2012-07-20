/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.LayoutPanel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * A LayoutPanel to insert into ContentSectionPage or ImageSelectPage
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImagesPane extends LayoutPanel implements Resettable {

    public static final Logger s_log = Logger.getLogger(ImagesPane.class);
    private final StringParameter m_imageComponentKey;
    private final MapComponentSelectionModel m_imageComponent;
    private final ImageComponentAdminListener m_adminListener;

    public ImagesPane() {
        // Left column is empty, this is only to provide the same layout for all
        // tabs in ContentSectionPage
        setLeft(new SimpleComponent());

        SegmentedPanel body = new SegmentedPanel();
        setBody(body);

        m_imageComponentKey = new StringParameter("imageComponent");

        ParameterSingleSelectionModel componentModel = new ParameterSingleSelectionModel(m_imageComponentKey);
        m_imageComponent = new MapComponentSelectionModel(componentModel, new HashMap());

        Map selectors = m_imageComponent.getComponentsMap();
        m_adminListener = new ImageComponentAdminListener(m_imageComponent, this);

        ImageUploadComponent upload = new ImageUploadComponent(ImageComponent.ADMIN_IMAGES);
        upload.getForm().addInitListener(m_adminListener);
        upload.getForm().addSubmissionListener(m_adminListener);
        upload.getForm().addProcessListener(m_adminListener);
        selectors.put(ImageComponent.UPLOAD, upload);
        body.addSegment(
                new Label(GlobalizationUtil.globalize("cms.ui.image_upload")),
                upload);

        ImageLibraryComponent library = new ImageLibraryComponent(ImageComponent.ADMIN_IMAGES);
        library.getForm().addInitListener(m_adminListener);
        library.getForm().addProcessListener(m_adminListener);
        library.addUploadLink(m_adminListener);
        selectors.put(ImageComponent.LIBRARY, library);
        body.addSegment(
                new Label(GlobalizationUtil.globalize("cms.ui.image_library")),
                library);

    }

    @Override
    public final void register(Page page) {
        super.register(page);
        Map componentsMap = m_imageComponent.getComponentsMap();

        Iterator i = componentsMap.keySet().iterator();
        while (i.hasNext()) {
            Object key = i.next();
            Component component = (Component) componentsMap.get(key);

            page.setVisibleDefault(component, ImageComponent.LIBRARY.equals(key));
        }

        page.addComponentStateParam(this, m_imageComponentKey);
    }

    @Override
    public final void reset(PageState ps) {
        super.reset(ps);

        Map componentsMap = m_imageComponent.getComponentsMap();
        m_imageComponent.setSelectedKey(ps, ImageComponent.LIBRARY);
        Iterator i = componentsMap.keySet().iterator();
        while (i.hasNext()) {
            Object key = i.next();
            Component component = (Component) componentsMap.get(key);

            ps.setVisible(component, ImageComponent.LIBRARY.equals(key));

            // Reset all components if they are of type Resettable
            if (component instanceof Resettable) {
                ((Resettable) component).reset(ps);
            }
        }
    }
}
