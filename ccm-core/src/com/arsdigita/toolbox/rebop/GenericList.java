/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.toolbox.rebop;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ItemSelectable;
import java.awt.Rectangle;
import java.awt.Scrollbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;



public class GenericList extends GenericComponent
    implements AdjustmentListener, ActionListener, ItemSelectable {

    private Scrollbar m_verticalScrollBar = null;

    private GenericComponent m_componentPane = new GenericComponent();

    private ItemListener m_itemListener = null;

    private boolean m_multiSelect = false;
    private GenericListItem m_selectedItem = null;
    private int m_selectedIndex = -1;

    private Image m_defaultIcon = null;

    private int m_bevelSize;

    private int m_firstVisibleItemIndex = 0;
    private int m_topmostItemIndex = 0;

    private int m_dataItems = 0;
    private int m_maxItems = -1;

    private int m_scrollTop = 0;

    private int m_bottomElementEdge = 0;

    private GenericDrawable m_itemUpDrawable = null;
    private GenericDrawable m_itemDownDrawable = null;
    private GenericDrawable m_itemOverDrawable = null;

    private Font m_itemFont;

    private Color m_labelColor = Color.blue;
    private Color m_selectedLabelColor = Color.white;
    private Color m_rolloverLabelColor = Color.blue;

    private Color m_textColor = Color.black;
    private Color m_selectedTextColor = Color.black;
    private Color m_rolloverTextColor = Color.black;

    public GenericList() {
        this(2);
    }

    public GenericList(int bevelSize) {

        m_bevelSize = bevelSize;
        m_verticalScrollBar = new Scrollbar(Scrollbar.VERTICAL);

        m_verticalScrollBar.setVisible(false);
        m_verticalScrollBar.addAdjustmentListener(this);
        setLayout(null);

        add(m_componentPane);
        add(m_verticalScrollBar);
        m_componentPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        setDrawable(new BevelBox(
                                 new Color(0x031ab2),
                                 new Color(0xafbbff),
                                 new Color(0x000d59),
                                 m_bevelSize,
                                 true));
    }

    protected void draw(Graphics g) {
        Dimension mySize = getSize();
        int bevelReserve = m_bevelSize * 2;
        super.draw(g);
        g.setClip(m_bevelSize, m_bevelSize, mySize.width - bevelReserve,
                  mySize.height - bevelReserve);
    }

    public void reshape(int x, int y, int width, int height) {
        super.reshape(x, y, width, height);
        updateComponentBounds();
    }

    public void setBounds(int x, int y, int width, int height) {
        reshape(x, y, width, height);
    }

    public void setFont(Font itemFont) {
        m_itemFont = itemFont;
    }

    public Font getFont() {
        return m_itemFont;
    }

    public void setTextColor(Color color) {
        m_textColor = color;
        updateItemColors();
    }

    public void setSelectedTextColor(Color color) {
        m_selectedTextColor = color;
        updateItemColors();
    }

    public void setRolloverTextColor(Color color) {
        m_rolloverTextColor = color;
        updateItemColors();
    }

    public void setLabelColor(Color color) {
        m_labelColor = color;
        updateItemColors();
    }

    public void setSelectedLabelColor(Color color) {
        m_selectedLabelColor = color;
        updateItemColors();
    }

    public void setRolloverLabelColor(Color color) {
        m_rolloverLabelColor = color;
        updateItemColors();
    }

    /**
     * Iterates through all items and sets their colors
     * to match the ones in the list.
     */
    private void updateItemColors() {
    }

    public void setItemUpDrawable(GenericDrawable upDrawable) {
        m_itemUpDrawable = upDrawable;
    }

    public GenericDrawable getItemUpDrawable() {
        return m_itemUpDrawable;
    }

    public void setItemDownDrawable(GenericDrawable downDrawable) {
        m_itemDownDrawable = downDrawable;
    }

    public GenericDrawable getItemDownDrawable() {
        return m_itemDownDrawable;
    }


    public void setItemOverDrawable(GenericDrawable overDrawable) {
        m_itemOverDrawable = overDrawable;
    }

    public GenericDrawable getItemOverDrawable() {
        return m_itemOverDrawable;
    }

    private void updateScrollbarBounds() {
        Rectangle myBounds = getBounds();
        int scrollBarWidth = m_verticalScrollBar.getPreferredSize().width;

        m_verticalScrollBar.setBounds(
                                      new Rectangle(
                                                    myBounds.width - m_bevelSize - scrollBarWidth,
                                                    m_bevelSize,
                                                    scrollBarWidth,
                                                    myBounds.height - (2 * m_bevelSize)
                                                    ));
    }

    public void addNotify() {
        forceInvalid();
        super.addNotify();
        initComponentBounds();
    }

    private void initComponentBounds() {
        forceInvalid();
        Rectangle myBounds = getBounds();
        //          System.out.println("myBounds in initComponentBounds: " +
        //                             myBounds);
        int scrollBarWidth = 0;
        Rectangle componentPaneBounds = new Rectangle(
                                                      m_bevelSize,
                                                      m_bevelSize,
                                                      myBounds.width - ((2 * m_bevelSize)),
                                                      myBounds.height * 2
                                                      );
        m_componentPane.setBounds(componentPaneBounds);
        //          System.out.println("initComponentBounds: " +
        //                             componentPaneBounds +
        //                             " now: " +
        //                             m_componentPane.getBounds());
    }

    private void updateComponentBounds() {
        forceInvalid();
        updateScrollbarBounds();
        Rectangle myBounds = getBounds();
        //          System.out.println("myBounds in updateComponentBounds: " +
        //                             myBounds);
        int scrollBarWidth = 0;
        if (m_verticalScrollBar.isVisible()) {
            scrollBarWidth = m_verticalScrollBar.getPreferredSize().width;
        }

        Rectangle currentComponentPaneBounds = m_componentPane.getBounds();
        int componentPaneHeight = currentComponentPaneBounds.height;
        int componentPaneWidth = currentComponentPaneBounds.width;
        boolean resizeComponentPane = true;

        if ( myBounds.width != componentPaneWidth ) {
            resizeComponentPane = true;
            componentPaneWidth = myBounds.width;
        }

        if ( myBounds.height > componentPaneHeight) {
            resizeComponentPane = true;
            currentComponentPaneBounds.height =
                myBounds.height * 2;
        }


        if (scrollBarWidth > 0) {
            int scrollBarSpace = myBounds.width - componentPaneWidth;
            if (scrollBarSpace < scrollBarWidth) {
                // Make room for the scrollBar
                resizeComponentPane = true;
            }
        }

        if (resizeComponentPane) {
            Rectangle componentPaneBounds = new Rectangle(
                                                          m_bevelSize,
                                                          m_bevelSize,
                                                          myBounds.width - ((2 * m_bevelSize) + scrollBarWidth),
                                                          currentComponentPaneBounds.height
                                                          );

            // Force all the items to relayout
            for (int i = 0; i < m_componentPane.getComponentCount(); i++) {
                GenericListItem item =
                    (GenericListItem) m_componentPane.getComponent(i);
                item.setSize(componentPaneBounds.width, 20);
                item.updateLabelSize();
            }

            m_componentPane.setBounds(componentPaneBounds);

        }
        m_componentPane.validate();
        m_componentPane.doLayout();
    }

    public void resize(Dimension size) {
        forceInvalid();
        super.resize(size);
        initComponentBounds();
        updateComponentBounds();
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void actionPerformed(ActionEvent e) {
        GenericListItem item = (GenericListItem) e.getSource();
        int itemIndex = item.getCurrentIndex();

        if (0 > itemIndex || itemIndex >= m_dataItems) {
            System.out.println("Bad item index: " + itemIndex);
            return;
        }
        int id = 0;

        ItemEvent ie =
            new ItemEvent(this, id, item.getUserData(), ItemEvent.SELECTED);
        if (!m_multiSelect && null != m_selectedItem && item != m_selectedItem) {
            m_selectedItem.setSelected(false);
            // Uncomment to send deselect events
            // ItemEvent deselect =
            //    new ItemEvent(this, id, item.getUserData(), ItemEvent.DESELECTED);
            // processItemStateChanged(deselect);
        }

        m_selectedIndex = itemIndex;
        m_selectedItem = item;
        processItemStateChanged(ie);
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        int yLoc = e.getValue() * -1;
        m_scrollTop = yLoc;
        m_componentPane.setLocation(
                                    m_componentPane.getLocation().x,
                                    yLoc);
    }

    public void setDefaultItemIcon(Image icon) {
        m_defaultIcon = icon;
    }

    public Image getDefaultItemIcon() {
        return m_defaultIcon;
    }

    public void setMaxItems(int max) {
        m_maxItems = max;
    }

    public int getMaxItems() {
        return m_maxItems;
    }

    public void clear() {
        m_componentPane.removeAll();
        m_dataItems = 0;
        m_firstVisibleItemIndex = 0;
        m_topmostItemIndex = 0;
        updateComponentBounds();
        repaint();
    }

    public void removeElementAtUserData(Object userData) {
        int index = getUserDataIndex(userData);
        if (index >= 0) {
            remove(index);
        }
    }

    public int getUserDataIndex(Object userData) {
        int index = -1;

        if (null == userData) {
            return index;
        }

        for (int i = 0; i < m_componentPane.getComponentCount(); i++) {
            GenericListItem item =
                (GenericListItem) m_componentPane.getComponent(i);
            if (userData.equals(item.getUserData())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public Object getUserDataAt(int itemIndex) {
        if (0 > itemIndex || itemIndex >= m_componentPane.getComponentCount()) {
            return null;
        }
        GenericListItem item =
            (GenericListItem) m_componentPane.getComponent(itemIndex);
        return item.getUserData();
    }

    public void remove(int itemIndexParam) {
        int itemIndex = itemIndexParam - m_topmostItemIndex;
        if (itemIndex >= m_dataItems || itemIndex < 0) {
            return;
        }

        m_componentPane.remove(itemIndex);
        m_dataItems--;
        // renumber
        for (int i = itemIndex; i < m_dataItems; i++) {
            GenericListItem item =
                (GenericListItem) m_componentPane.getComponent(i);
            if (i == itemIndex) {
                m_selectedItem = item;
            }
            item.setCurrentIndex(i);
        }

        if (m_selectedIndex >= m_dataItems) {
            m_selectedIndex = -1;
            m_selectedItem = null;
        }

        if (itemIndex < m_firstVisibleItemIndex) {
            m_firstVisibleItemIndex--;
        }

        updateComponentBounds();

        int count = m_componentPane.getComponentCount();
        if (count > 0) {
            count--;
            GenericListItem item = (GenericListItem)
                m_componentPane.getComponent(count);
            Rectangle bounds = item.getBounds();
            m_bottomElementEdge = bounds.y + bounds.height;
            updateScrollBar();
        }
        repaint();
    }

    public int getSelectedIndex() {
        return m_selectedIndex;
    }

    public void setColors(
                          Color unselectedColor,
                          Color selectedColor
                          ) {
        setItemUpDrawable(new BevelBox(
                                       unselectedColor, null, null, 0, false));

        setItemOverDrawable(new BevelBox(
                                         unselectedColor.brighter(), null, null, 0, false));

        setItemDownDrawable(new BevelBox(
                                         selectedColor, null, null, 0, true));
    }

    public void add(String itemLabel) {
        add(itemLabel, "", null);
    }

    public void add(String itemLabel, String itemText, Object userData) {
        add(m_defaultIcon, itemLabel, itemText, userData);
    }

    protected GenericListItem makeListItem(
                                           Image icon,
                                           String itemLabel,
                                           String itemText,
                                           Object userData) {
        return new GenericListItem(
                                   icon,
                                   itemLabel,
                                   itemText,
                                   userData,
                                   getItemUpDrawable(),
                                   getItemDownDrawable(),
                                   getItemOverDrawable()
                                   );
    }

    public void add(
                    Image icon,
                    String itemLabel,
                    String itemText,
                    Object userData) {
        m_dataItems++;

        if (m_verticalScrollBar.isVisible()) {
            m_firstVisibleItemIndex++;
        }

        GenericListItem listItem = makeListItem(
                                                icon,
                                                itemLabel,
                                                itemText,
                                                userData
                                                );

        listItem.setLabelColor(m_labelColor);
        listItem.setSelectedLabelColor(m_selectedLabelColor);
        listItem.setRolloverLabelColor(m_rolloverLabelColor);
        listItem.setTextColor(m_textColor);
        listItem.setSelectedTextColor(m_selectedTextColor);
        listItem.setRolloverTextColor(m_rolloverTextColor);

        listItem.setFont(m_itemFont);

        addComponent(listItem);
    }

    public GenericListItem getItemAt(int index) {
        GenericListItem item = (GenericListItem)
            m_componentPane.getComponent(index);
        return item;
    }

    private void updateScrollBar() {
        Rectangle myBounds = getBounds();
        Rectangle containerBounds = m_componentPane.getBounds();

        int pageCount = myBounds.height - (m_bevelSize * 2);

        if (0 < pageCount) {
            m_verticalScrollBar.setValues(
                                          Math.abs(m_scrollTop),
                                          pageCount,
                                          0,
                                          m_bottomElementEdge);
            m_verticalScrollBar.setBlockIncrement(pageCount);
        }

        showVerticalScrollbar(m_bottomElementEdge > myBounds.height,
                              pageCount);

        if (m_verticalScrollBar.isVisible()) {
            int itemCount = m_componentPane.getComponentCount();
            int averageItemHeight =
                m_bottomElementEdge / itemCount;
            // Set the unit scroll to about a third of the average
            // item height
            int unitScroll = averageItemHeight / 3;
            if (unitScroll <= 0) {
                unitScroll = 1;
            }
            // System.out.println("unitIncrement: " + unitScroll);
            m_verticalScrollBar.setUnitIncrement(unitScroll);
        }

    }

    private int getVisibleItemCount() {
        int count = m_componentPane.getComponentCount();
        if (0 == count) {
            return count;
        }
        Rectangle bounds = m_componentPane.getBounds();
        int bottom = bounds.height;

        int itemCount = 0;
        for (int i = m_firstVisibleItemIndex; i < count; i++) {
            GenericListItem item =
                (GenericListItem) m_componentPane.getComponent(i);
            Rectangle itemBounds = item.getBounds();

            if (itemBounds.y + itemBounds.height >= bottom) {
                return itemCount;
            }
            itemCount++;
        }
        return itemCount;
    }

    public GenericListItem getLastComponent() {
        int count = m_componentPane.getComponentCount();
        if (0 == count) {
            return null;
        }
        Rectangle bounds = m_componentPane.getBounds();
        int bottom = bounds.height;
        int index = 0;
        GenericListItem candidate = null;
        for (int i = m_firstVisibleItemIndex; i < count; i++) {
            GenericListItem item =
                (GenericListItem) m_componentPane.getComponent(i);
            if (null == candidate) {
                candidate = item; // for really tall items
            }
            Rectangle itemBounds = item.getBounds();
            if (itemBounds.y + itemBounds.height >= bottom) {
                return candidate;
            }
            candidate = item;
            if (i + 1 >= count) {
                return candidate;
            }
        }
        return null;
    }

    private void addComponent(GenericListItem component) {
        // long startTime = System.currentTimeMillis();
        int componentCount = m_componentPane.getComponentCount();
        if (0 >= componentCount) {
            forceInvalid();
            validateTree();
            initComponentBounds();
        }

        Rectangle containerBounds = m_componentPane.getBounds();
        // System.out.println("containerBounds: " + containerBounds);
        int myHeight = getBounds().height - m_bevelSize * 2;


        component.setCurrentIndex(componentCount);
        // Scroll the topmost item off into oblivion
        if (false) {//if (componentCount >= m_maxItems && m_maxItems >= 0) {
            m_componentPane.remove(0);
            GenericListItem topItem = (GenericListItem)
                m_componentPane.getComponent(0);
            m_topmostItemIndex = topItem.getCurrentIndex();
        }

        int width = containerBounds.width;
        int bottom = containerBounds.height;

        component.setSize(width, 30);

        m_componentPane.add(component);

        Dimension prefSize = component.getPreferredSize();

        component.setSize(width, prefSize.height);
        component.addActionListener(this);
        component.validate();
        m_componentPane.doLayout();

        Rectangle componentBounds = component.getBounds();
        int componentHeight = componentBounds.height;

        m_bottomElementEdge = componentBounds.y + componentHeight;
        if (m_bottomElementEdge > bottom) {
            // System.out.println("doubling...");
            m_componentPane.setSize(containerBounds.width,
                                    containerBounds.height * 2);
            m_componentPane.doLayout();
            componentBounds = component.getBounds();
            m_bottomElementEdge = componentBounds.y + componentHeight;
        }

        // System.out.println("componentBounds: " + component.getBounds());

        int relativeBottomEdge = m_bottomElementEdge + m_scrollTop;
        if (relativeBottomEdge > myHeight) {
            // Scroll the new item completely into view
            m_scrollTop = -(m_bottomElementEdge - myHeight);
            m_componentPane.setLocation(m_componentPane.getLocation().x,
                                        m_scrollTop);
            //             System.out.println("m_bottomElementEdge: " +
            //                                 m_bottomElementEdge +
            //                                 " m_scrollTop: " +
            //                                 m_scrollTop +
            //                                 " myHeight: " +
            //                                 myHeight);
        }

        updateScrollBar();

        //          System.out.println("adding took " +
        //                             (System.currentTimeMillis() - startTime));
    }

    public void showVerticalScrollbar(boolean visible, int pageIncrement) {
        if (0 >= pageIncrement) {
            m_verticalScrollBar.setVisible(false);
            return;
        }

        boolean changed = m_verticalScrollBar.isVisible() != visible;
        if (changed) {
            m_verticalScrollBar.setVisible(visible);
            //              doLayout(); // Force a relayout
            //              if (visible && m_dataItems > 0) {
            //                  m_verticalScrollBar.setUnitIncrement(1);
            //                  m_verticalScrollBar.setPageIncrement(pageIncrement);
            //              }
            updateComponentBounds();
        }
    }

    // ItemSelectable interface
    /**
     * Add a listener to recieve item events when the state of an item changes.
     */
    public void addItemListener(ItemListener l) {
        m_itemListener = AWTEventMulticaster.add(m_itemListener, l);
    }

    /**
     * Returns the selected items or null if no items are selected.
     */
    public Object[] getSelectedObjects() {
        return new Object[0];
    }

    public void removeItemListener(ItemListener l) {
        m_itemListener = AWTEventMulticaster.remove(m_itemListener, l);
    }

    public int getItemCount() {
        return m_dataItems;
    }

    protected void processItemStateChanged(ItemEvent e) {
        if (null != m_itemListener) {
            m_itemListener.itemStateChanged(e);
        }
    }
}
