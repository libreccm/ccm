/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.Service;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;

/**
 * A component which will insert a javascript to the xml output with the image
 * information for the OpenCCM plugin for Xinha editor.
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImageSelectResultComponent extends SimpleContainer
        implements Resettable {

    boolean m_valid = false;
    ImageAsset m_image;
    String m_lastImageComponent;

    public ImageSelectResultComponent() {
        super();
    }

    /**
     * Save image imformation
     *
     * @param image an {@link ImageAsset}
     */
    public void setResult(final ImageAsset image, final String lastComponent) {
        m_image = image;
        m_lastImageComponent = lastComponent;
        m_valid = (m_image != null);
    }

    @Override
    public void generateXML(PageState state, Element parent) {

        Element scriptElem = parent.newChildElement("script");
        scriptElem.addAttribute("type", "text/javascript");

        StringBuilder script = new StringBuilder(1000);

        // Create funtion
        script.append("function selectImage(button) {");

        // If there is a valid image
        if (m_valid) {

            // If in library mode, only listen to save button
            if (m_lastImageComponent.equals(ImageComponent.LIBRARY)) {
                script.append("if(button.id != \"save\" ) { return false; } ");
            }

            // Send image parameters to xinha plugin
            script.append("window.opener.openCCM.imageSet({");
            script.append("      src    : \"");
            script.append(URL.getDispatcherPath());
            script.append(Service.getImageURL(m_image));
            script.append("\", ");
            script.append("      name   : \"");
            script.append(m_image.getDisplayName());
            script.append("\", ");
            script.append("      width   : \"");
            script.append(m_image.getWidth());
            script.append("\", ");
            script.append("      height   : \"");
            script.append(m_image.getHeight());
            script.append("\"");
            script.append("});");

            // Close window
            script.append("self.close();");

        }
        script.append("return false;");
        script.append("}");

        // If in upload mode and if there is a valid image, execute the
        // javascript function
        if (m_valid && ImageComponent.UPLOAD.equals(m_lastImageComponent)) {
            script.append("selectImage();");
        }

        scriptElem.setText(script.toString());

        // Reset ImageSelectResultComponent
        reset(state);
    }

    /**
     * Reset this component.
     *
     * @param state Page state
     */
    public void reset(PageState state) {
        setResult(null, null);
    }
}
