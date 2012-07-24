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

    public static final int UNSET = 0;
    public static final int CANCEL = 1;
    public static final int SELECT = 2;
    int m_state = UNSET;
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
        m_state = SELECT;
    }

    @Override
    public void generateXML(PageState state, Element parent) {

        if (m_state != UNSET) {

            Element scriptElem = parent.newChildElement("script");
            scriptElem.addAttribute("type", "text/javascript");
            scriptElem.addAttribute("eventHandler", "onload");

            StringBuilder script = new StringBuilder(1000);

            script.append("alert(\"SCRIPT\");");

            if (m_state == SELECT) {
//                script.append("window.opener.document.OpenCCM.imageSet(");
                script.append("window.openCCM.imageSet(");
                script.append("{");
                script.append("      src    : \"/theme/mandalay/ccm/cms-service/stream/image/?image_id=");
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
                script.append("    });");
            }

            script.append("self.close();");
            script.append("return false;");
            scriptElem.setText(script.toString());

        }
    }

    public void reset(PageState state) {
        m_name = null;
        m_id = null;
        m_width = null;
        m_height = null;
        m_state = UNSET;
    }
}
