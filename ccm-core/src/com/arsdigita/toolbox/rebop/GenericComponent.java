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

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.event.MouseEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseListener;

/**
 * Root of all rebop components. Takes care of double-buffering.
 * Typically, only the "root" component need have its double-buffer
 * flag set to true, as all sub-components will automatically be
 * double-buffered.
 *
 * @author Gavin Doughtie
 */
public class GenericComponent extends Container
    implements MouseListener, GenericDrawable {

    private Color m_foregroundColor = Color.black;
    private GenericDrawable m_drawable = null;

    private static boolean s_debugPaint = false;
    private Image m_backBuffer = null;
    private boolean m_doubleBuffer = false;
    private boolean m_revalidate = true;

    private int m_xOffset = 0;
    private int m_yOffset = 0;

    public GenericComponent() {
    }

    /**
     * Sets the object that will draw this component.
     * while some components will want to do their
     * own painting, it is recommended that you create
     * re-useable objects that implement the GenericDrawable
     * interface instead.
     * @param drawable Object that will be drawn in this
     * component's paint() method
     */
    public void setDrawable(GenericDrawable drawable) {
        m_drawable = drawable;
    }

    public GenericDrawable getDrawable() {
        return m_drawable;
    }

    /**
     * if db is set to true, then this component will
     * paint using standard double-buffered techniques
     * @param db double buffer flag
     */
    public void setDoubleBuffered(boolean db) {
        m_doubleBuffer = db;
        if (m_doubleBuffer) {
            updateBackBuffer(getSize());
        } // end of if ()
    }

    public boolean getDoubleBuffered() {
        return m_doubleBuffer;
    }

    /**
     * Called when the native peer is available,
     * Double-buffer image is automatically created if
     * required.
     */
    public void addNotify() {
        super.addNotify();
        updateBackBuffer(getSize());
    }

    /**
     * Called whenever a component is resized. Recreate
     * the back buffer if necessary.
     * @param x new x coordinate
     * @param y new y coordinate
     * @param width new width
     * @param height new height
     */
    public void setBounds(int x, int y, int width, int height) {
        componentReshape(x, y, width, height);
    }

    /**
     * Called whenever a component is resized. Recreate
     * the back buffer if necessary. This is a deprecated
     * method, but it is the "root" method of all the awt
     * component methods that change the size of a component
     * (at least up through the JDK 1.3.1x) and overriding
     * this method prevents having to override many others.
     * Whenever this changes, this method can be removed as
     * setBounds() will take care of things.
     * @param x new x coordinate
     * @param y new y coordinate
     * @param width new width
     * @param height new height
     */
    public void reshape(int x, int y, int width, int height) {
        componentReshape(x, y, width, height);
    }

    public void componentReshape(int x, int y, int width, int height) {
        forceInvalid();
        super.reshape(x, y, width, height);
        updateBackBuffer(width, height);
    }

    /**
     * paints the component by calling the draw method. Handles
     * double-buffer and debug drawing logic. Subclasses should
     * override draw if they wish to perform custom painting.
     * @param g Graphics to draw in
     */
    public void paint(Graphics g) {
        // System.out.println("GenericComponent.paint " + this);
        if (m_doubleBuffer) {
            Graphics off = m_backBuffer.getGraphics();
            draw(off);
            super.paint(off);
            g.drawImage(m_backBuffer, 0, 0, this);
            off.dispose();
        } else {
            draw(g);
            super.paint(g);
        }
    }

    protected void validateTree() {
        if (m_revalidate) {
            super.validateTree();
            m_revalidate = false;
        }
    }

    public void forceInvalid() {
        m_revalidate = true;
    }

    private void updateBackBuffer(Dimension size) {
        updateBackBuffer(size.width, size.height);
    }

    /**
     * Creates the image used for double-buffering if necessary
     * @param width new width of back buffer image
     * @param height new height of back buffer image
     */
    private void updateBackBuffer(int width, int height) {
        if (m_doubleBuffer && (width > 0 && height > 0)) {
            m_backBuffer = createImage(width, height);
        } // end of if ()
    }

    /**
     * Draw this component to a Graphics context. Subclasses
     * should override this method rather than paint() for
     * custom drawing.
     * @param g Graphics to draw in
     */
    protected void draw(Graphics g) {
        if (null != m_drawable) {
            m_drawable.draw(g, 0, 0, getSize().width, getSize().height);
        }
        drawDebug(g);
    }


    /**
     * Implementation of the GenericDrawable interface. Allows you to
     * "compose" a complex drawable from simpler sub-components (for
     * example, a GenericComponent with a BevelBox as its drawable, and
     * with a GenericImage as a subcomponent. for this to work, you
     * MUST add any components you wish to use as drawables someplace
     * in the AWT component hierarchy of your applet. You can set them
     * to be not visible, however.
     */
    public void draw(Graphics g, int x, int y, int width, int height) {
        g.translate(x + m_xOffset, y + m_yOffset);
        // setClip?
        boolean vis = isVisible();
        if (!vis) {
            setVisible(true);
        }
        setSize(width, height);
        doLayout();
        boolean tmpDb = m_doubleBuffer;
        m_doubleBuffer = false;
        paint(g);
        m_doubleBuffer = tmpDb;
        setVisible(vis);
    }

    public void setXOffset(int xOffset) {
        m_xOffset = xOffset;
    }

    public int getXOffset() {
        return m_xOffset;
    }

    public int getYOffset() {
        return m_yOffset;
    }

    public void setYOffset(int yOffset) {
        m_yOffset = yOffset;
    }

    /**
     * Draws the outer edge of the component's rectangle in red, and the
     * component's preferred size in blue. Subclasses can override
     * to provide additional debugging graphics, which will not appear
     * unless GenericComponent.setDebugPaint(true) has been called.
     * @param g Graphics to draw in
     */
    protected void drawDebug(Graphics g) {
        if (s_debugPaint) {
            g.setColor(Color.red);
            g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
            Dimension prefSize = getPreferredSize();
            g.setColor(Color.blue);
            g.drawRect(0, 0, prefSize.width - 1, prefSize.height - 1);
        }
    }

    /**
     * Toggles debug component painting for ALL components
     * @param debug if true, then drawDebug will be called from
     * the draw method.
     */
    public static void setDebugPaint(boolean debug) {
        s_debugPaint = debug;
    }

    public static boolean getDebugPaint() {
        return s_debugPaint;
    }

    // implementation of java.awt.event.MouseListener interface
    // subclasses can implement as desired
    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
    }

}// GenericComponent
