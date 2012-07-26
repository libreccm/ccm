/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImageSelectResultPane extends SimpleContainer implements Resettable {

    boolean m_valid = false;
    String m_name;
    BigDecimal m_id;
    BigDecimal m_width;
    BigDecimal m_height;

    public ImageSelectResultPane() {
        super();
    }

    public void setResult(final String name, final BigDecimal id, final BigDecimal width, final BigDecimal height) {
        m_name = name;
        m_id = id;
        m_width = width;
        m_height = height;
        m_valid = true;
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
            script.append("      src    : \"/ccm/cms-service/stream/image/?image_id=");
            script.append(m_id);
            script.append("\", ");
            script.append("      name   : \"");
            script.append(m_name);
            script.append("\", ");
            script.append("      width   : \"");
            script.append(m_width);
            script.append("\", ");
            script.append("      height   : \"");
            script.append(m_height);
            script.append("\"");
            script.append("});");
            script.append("}");

            script.append("self.close();");

        }
        script.append("return false;");
        script.append("}");
        scriptElem.setText(script.toString());
    }

    public void reset(PageState state) {
        m_name = null;
        m_id = null;
        m_width = null;
        m_height = null;
        m_valid = false;
    }
}
