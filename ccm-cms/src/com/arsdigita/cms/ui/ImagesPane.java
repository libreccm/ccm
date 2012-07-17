/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.LayoutPanel;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * A LayoutPanel to insert into ContentSectionPage or ImageSelectPage
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImagesPane extends LayoutPanel implements Resettable {

    public static final Logger s_log = Logger.getLogger(ImagesPane.class);
    //private ImageChooser imageChooser;
    private final StringParameter m_imageComponentKey;
    private final MapComponentSelectionModel m_imageComponent;
//    private final ImageComponentAdminListener m_adminListener;

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
//        m_adminListener = new ImageComponentAdminListener(m_imageComponent);
        
        ImageUploadComponent upload = new ImageUploadComponent(ImageComponent.ADMIN_IMAGES);
        // For future use
        //upload.getForm().addInitListener(m_adminListener);
        // upload.addProcessListener(m_adminListener);
        selectors.put(ImageComponent.UPLOAD, upload);
        body.addSegment(
                new Label(GlobalizationUtil.globalize("cms.ui.image_upload")),
                upload);

        ImageLibraryComponent library = new ImageLibraryComponent(ImageComponent.ADMIN_IMAGES);
        // For future use
        //library.getForm().addInitListener(m_adminListener);
        // library.getForm().addProcessListener(m_adminListener);
//        library.addUploadLink(new ActionListener() {
//
//            public void actionPerformed(ActionEvent ev) {
//                setImageComponent(ev.getPageState(), ImageComponent.UPLOAD);
//            }
//        });
        selectors.put(ImageComponent.LIBRARY, library);
        body.addSegment(
                new Label(GlobalizationUtil.globalize("cms.ui.image_library")),
                library);

    }

    @Override
    public final void register(Page page) {
        super.register(page);
        Map componentsMap = m_imageComponent.getComponentsMap();

//        Iterator i = componentsMap.keySet().iterator();
//        while (i.hasNext()) {
//            Object key = i.next();
//            Component component = (Component) componentsMap.get(key);
//
//            page.setVisibleDefault(component, ImageComponent.LIBRARY.equals(key));
//        }

        page.addComponentStateParam(this, m_imageComponentKey);
    }

    @Override
    public final void reset(PageState state) {
        super.reset(state);
    }

    private final class SubmissionListener implements FormSubmissionListener {

        public final void submitted(final FormSectionEvent e) {
            final PageState s = e.getPageState();

        }
    }
    
//    private void setImageComponent(PageState ps, final String activeKey) {
//        m_imageComponent.setSelectedKey(ps, activeKey);
//
//        if (s_log.isDebugEnabled()) {
//            s_log.debug("Selected component: " + activeKey);
//        }
//
//        Map componentsMap = m_imageComponent.getComponentsMap();
//        Iterator i = componentsMap.keySet().iterator();
//        while (i.hasNext()) {
//            Object key = i.next();
//            Component component = (Component) componentsMap.get(key);
//
//            boolean isVisible = activeKey.equals(key);
//
//            if (s_log.isDebugEnabled()) {
//                s_log.debug("Key: " + key + "; Visibility: " + isVisible);
//            }
//
//            ps.setVisible(component, isVisible);
//        }
//    }

//    public void init(FormSectionEvent event)
//            throws FormProcessException {
//        PageState ps = event.getPageState();
//        setImageComponent(ps, ImageComponent.LIBRARY);
//
////        ItemImageAttachment attachment = m_imageStep.getAttachment(ps);
////        if (null == attachment) {
//        // XXX: Do something
////        }
//    }
}
