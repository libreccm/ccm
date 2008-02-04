package com.arsdigita.london.cms.freeform.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.util.LockableImpl;

/**
 * A table cell renderer which passes through all Components returned
 * from a TableModel's getElementAt(). Else, a Label will be created
 * using the Object's toString().
 *
 * @author <a href="mailto:phong@arsdigita.com">Phong Nguyen</a>
 **/
public class AssetTableCellRenderer extends LockableImpl
    implements TableCellRenderer {
    
    private boolean m_active;

    public AssetTableCellRenderer() {
        this(false);
    }

    public AssetTableCellRenderer(boolean active) { 
        m_active = active;
    }
    
    public Component getComponent(Table table, PageState state,
                                  Object value, boolean isSelected,
                                  Object key, int row, int column) {
                
        Component returnValue = null;

        // Pass through all components.
        if (value instanceof Component) {
            returnValue = (Component) value;
        } else {
        
            // If the value is not null then set the label as the value's
            // toString(). Else use the default of "&nbsp".
            if (value == null) {
                returnValue = new Label("&nbsp;", false);
            } else {
                if (m_active) {
                    returnValue = new ControlLink(value.toString());
                } else {
                    returnValue = new Label(value.toString());
                }
            }
        }
        return returnValue;
    }
}

