/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.AbstractListModelBuilder;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.RelationAttributeCollection;
import java.util.StringTokenizer;

/**
 *
 * @author quasi
 */
public class RelationAttributeList extends List {

    private ContentTypeRequestLocal m_type;
    private ContentType contentType;
    private StringTokenizer relationAttributeList;

    public RelationAttributeList(ContentTypeRequestLocal type) {
        super(new RelationAttributeListModelBuilder(type));

        m_type = type;
        relationAttributeList = null;

        setCellRenderer(new CellRenderer());
    }

    @Override
    public final boolean isVisible(final PageState state) {

// Mit  getModelBuilder(); den ModelBuilder holen und abfragen,, ob es etwas anzuzeigen gibt?

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

        return retVal;
    }

    private static class RelationAttributeListModelBuilder extends AbstractListModelBuilder {

        private ContentTypeRequestLocal m_type;
        private ContentType contentType;
        private StringTokenizer relationAttributeList;

        public RelationAttributeListModelBuilder(ContentTypeRequestLocal type) {
            super();
            m_type = type;
            relationAttributeList = null;
        }

        public final ListModel makeModel(final List list, final PageState state) {

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

            return new Model(relationAttributeList);
        }

        private class Model implements ListModel {

            private final StringTokenizer m_items;
//            private String m_item;
            private RelationAttributeCollection m_item;

            Model(final StringTokenizer items) {
                m_items = items;
                m_item = null;
            }

            public final boolean next() {
                if (m_items.hasMoreTokens()) {
//                    m_item = m_items.nextToken();
                    m_item = new RelationAttributeCollection(m_items.nextToken());
                    return true;
                } else {
                    m_item = null;
                    return false;
                }
            }

            // Label
            public final Object getElement() {
                return m_item.getDisplayName();
            }

            // URL
            public final String getKey() {
                return m_item+"/";//.getName() + "/";
            }
        }
    }

    private class CellRenderer implements ListCellRenderer {

        public final Component getComponent(final List list,
                final PageState state,
                final Object value,
                final String key,
                final int index,
                final boolean isSelected) {
            return new Link((String) value, key);
        }
    }
}
