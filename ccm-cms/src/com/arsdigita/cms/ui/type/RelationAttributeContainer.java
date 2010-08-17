/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.toolbox.ui.ComponentMap;
import com.arsdigita.xml.Element;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 *
 * @author quasi
 */
class RelationAttributeContainer extends ComponentMap {

    private ContentTypeRequestLocal m_type;
    private ContentType contentType;
    private StringTokenizer relationAttributeList;

//    private static final RequestLocal s_components = new RequestLocal() {
//        protected final Object initialValue(final PageState state) {
//            return new ComponentMap();
//        }
//    };
    public RelationAttributeContainer(ContentTypeRequestLocal type) {
        super();
        m_type = type;
        relationAttributeList = null;
    }

    @Override
    public final boolean isVisible(final PageState state) {

        boolean retVal = false;
        ContentType ct = (ContentType) m_type.getContentType(state);
        ContentItem ci = null;

        try {
            Class<? extends ContentItem> clazz = Class.forName(ct.getClassName()).asSubclass(ContentItem.class);
            ci = clazz.newInstance();
            retVal = clazz.cast(ci).hasRelationAttributes();
            relationAttributeList = clazz.cast(ci).getRelationAttributes();
            ci.delete();
        } catch (Exception ex) {
            //retVal = false;
        }

        // Test
        while (relationAttributeList.hasMoreElements()) {
            String token = relationAttributeList.nextToken();
            put(token, new Label(token));
//            ActionLink link = new ActionLink(new Label(token));
//            link.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                }
//            });

        }


        return retVal;
    }

    // HACK: Dies sollte geändert werden, aber ich weiß nicht wie. Das ist der einfachste
    //       Weg, die exception zu verhindern, aber ich bin mir sicher, daß das später zu
    //       Problemen führen wird.
    @Override
    public void lock() {
    }

    public final void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {

            Iterator iter = children();

            while (iter.hasNext()) {
                ((Component) iter.next()).generateXML(state, parent);
            }
        }
    }
}
