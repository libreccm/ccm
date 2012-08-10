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
 * A component which will insert a javascript to the xml output with the
 * image information for the OpenCCM plugin for Xinha editor.
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImageSelectResultComponent extends SimpleContainer implements Resettable {

    boolean m_valid = false;
    ImageAsset m_image;

    public ImageSelectResultComponent() {
        super();
    }

    /**
     * Save image imformation
     *
     * @param iamge an {@link ImageAsset}
     */
    public void setResult(final ImageAsset image/*, final String name, final BigDecimal id, final BigDecimal width, final BigDecimal height*/) {
        m_image = image;
        m_valid = (m_image != null);
    }

    @Override
    public void generateXML(PageState state, Element parent) {

        Element scriptElem = parent.newChildElement("script");
        scriptElem.addAttribute("type", "text/javascript");

        StringBuilder script = new StringBuilder(1000);

        script.append("function selectImage(button) {");
        if (m_valid) {

            script.append("if(button.id == \"save\" ) {");

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
            script.append("}");

            script.append("self.close();");

        }
        script.append("return false;");
        script.append("}");
        scriptElem.setText(script.toString());
    }

    /**
     * Reset this component.
     *
     * @param state Page state
     */
    public void reset(PageState state) {
        setResult(null);
    }
}
