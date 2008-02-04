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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;


/**
 * GenericImage.java
 *
 *
 * Created: Fri Sep 21 06:54:02 2001
 *
 * @author Gavin Doughtie
 * @version $Date: 2004/08/16 $
 */

public class GenericImage extends GenericComponent
    implements GenericDrawable
{
    private static final Dimension s_noSize = new Dimension(0, 0);

    private Image m_image = null;
    private Image m_tiledImage = null;
    private boolean m_tiled = false;
    private boolean m_useOffset = false;
    private int m_xOffset = 0;
    private int m_yOffset = 0;

    public GenericImage() {
    }

    public GenericImage(Image image) {
        m_image = image;
    }

    public void setImage(Image image) {
        m_image = image;
    }

    public Image getImage() {
        return m_image;
    }

    public void setTiled(boolean tiled) {
        m_tiled = tiled;
    }

    public boolean getTiled() {
        return m_tiled;
    }

    public Dimension getPreferredSize() {

        if (null == m_image) {
            return s_noSize;
        }

        return new Dimension(
                             m_image.getWidth(this), m_image.getHeight(this));
    }

    public void draw(Graphics g, int x, int y, int width, int height) {
        int xPos = x;
        int yPos = y;
        if (m_useOffset) {
            xPos += getXOffset();
            yPos += getYOffset();
        }

        if (m_tiled) {
            if (null != m_image) {
                ImageUtils.drawTiledImage(
                                          g,
                                          xPos,
                                          yPos,
                                          new Rectangle(
                                                        m_image.getWidth(this), m_image.getHeight(this)),
                                          m_image,
                                          width,
                                          height,
                                          this);
                return;
            }
        }

        if (null != m_image) {
            g.drawImage(m_image, xPos, yPos, this);
        }
    }

    /**
     * Used for generating a cached tiled image. Currently
     * not called.
     * @param width Width of image to create
     * @param height Height of image to create
     */
    private void updateTiledImage(int width, int height) {
        m_tiledImage =
            ImageUtils.createTiledImage(m_image, width, height, this);
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

    public void setUseOffset(boolean useOffset) {
        m_useOffset = useOffset;
    }

    public void paint(Graphics g) {
        draw(g, 0, 0, -1, -1);
        super.paint(g);
    }
}// GenericImage
