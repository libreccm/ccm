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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

/**
 * @author Gavin Doughtie
 * Given images representing the edges of a rectangle, BorderPainter
 * will paint a correctly tiled rectangle
 */
public class BorderPainter {
    public static final String versionId = "$Id: BorderPainter.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    public static final int NORTHWEST = 0;
    public static final int NORTH = 1;
    public static final int NORTHEAST = 2;
    public static final int EAST = 3;
    public static final int SOUTHEAST = 4;
    public static final int SOUTH = 5;
    public static final int SOUTHWEST = 6;
    public static final int WEST = 7;

    private Rectangle[] m_rects;
    private Image m_borderImage;
    private ImageObserver m_obs;
    private Color m_bgColor = Color.blue;

    public BorderPainter(Image m_borderImage, Color m_bgColor, ImageObserver m_obs) {
        this.m_borderImage = m_borderImage;
        this.m_bgColor = m_bgColor;
        this.m_obs = m_obs;
        m_rects = new Rectangle[8];
        initRects(m_borderImage.getWidth(m_obs) / 3);
    }

    public int getBorderWidth() {
        return m_rects[NORTHWEST].width;
    }

    public int getBorderHeight() {
        return m_rects[NORTHWEST].height;
    }

    public void initRects(int m_rectsize) {
        m_rects[NORTHWEST] =
            new Rectangle(0, 0, m_rectsize, m_rectsize); // nw
        m_rects[NORTH] =
            new Rectangle(m_rectsize, 0, m_rectsize, m_rectsize); // n
        m_rects[NORTHEAST] =
            new Rectangle(m_rectsize * 2, 0, m_rectsize, m_rectsize); // ne
        m_rects[EAST] =
            new Rectangle(m_rectsize * 2, m_rectsize, m_rectsize, m_rectsize); // e
        m_rects[SOUTHEAST] =
            new Rectangle(
                          m_rectsize * 2,
                          m_rectsize * 2,
                          m_rectsize,
                          m_rectsize); // se
        m_rects[SOUTH] =
            new Rectangle(m_rectsize, m_rectsize * 2, m_rectsize, m_rectsize); // s
        m_rects[SOUTHWEST] =
            new Rectangle(0, m_rectsize * 2, m_rectsize, m_rectsize); // sw
        m_rects[WEST] =
            new Rectangle(0, m_rectsize, m_rectsize, m_rectsize); // w
    }

    public void paint(Graphics g, Dimension mySize) {
        paint(g, mySize, 0, 0);
    }

    public void paint(Graphics g, Dimension mySize, int x, int y) {
        // width and height that must be tiled
        int width = mySize.width -
            m_rects[NORTHWEST].width -
            m_rects[NORTHEAST].width;

        int height = mySize.height -
            m_rects[NORTHWEST].height -
            m_rects[SOUTHWEST].height;

        // Fill the middle
        if (null != m_bgColor) {
            g.setColor(m_bgColor);
            g.fillRect(
                       x + m_rects[NORTHWEST].width,
                       y + m_rects[NORTHWEST].height,
                       mySize.width -  m_rects[SOUTHEAST].width,
                       mySize.height - m_rects[SOUTHEAST].height);
        }

        drawCorner(g, x, y, NORTHWEST);
        ImageUtils.drawTiledImage
            (
             g,
             x + m_rects[NORTHWEST].width,
             y,
             m_rects[NORTH],
             m_borderImage,
             width,
             m_rects[NORTH].height,
             m_obs
             ); // tiled north
        drawCorner(g, x + (mySize.width - m_rects[NORTHEAST].width), y, NORTHEAST);

        ImageUtils.drawTiledImage
            (
             g,
             x,
             y + m_rects[NORTHWEST].height,
             m_rects[WEST],
             m_borderImage,
             m_rects[WEST].width,
             height,
             m_obs
             ); // tiled west
        drawCorner(g, x, y + (mySize.height - m_rects[SOUTHWEST].height), SOUTHWEST);
        ImageUtils.drawTiledImage
            (
             g,
             x + m_rects[SOUTHWEST].width,
             y + (mySize.height - m_rects[SOUTH].height),
             m_rects[SOUTH],
             m_borderImage,
             width,
             m_rects[SOUTH].height,
             m_obs
             ); // tiled south

        ImageUtils.drawTiledImage
            (
             g,
             x + mySize.width - m_rects[EAST].width,
             y + m_rects[NORTHEAST].height,
             m_rects[EAST],
             m_borderImage,
             m_rects[EAST].width,
             height,
             m_obs
             ); // tiled east
        drawCorner(
                   g,
                   x + (mySize.width - m_rects[SOUTHEAST].width),
                   y + (mySize.height - m_rects[SOUTHEAST].height),
                   SOUTHEAST);

    }

    protected void drawCorner(Graphics g, int currentX, int currentY, int rectIndex) {
        g.drawImage
            (
             m_borderImage,
             currentX,
             currentY,
             currentX + m_rects[rectIndex].width,
             currentY + m_rects[rectIndex].height,
             m_rects[rectIndex].x,
             m_rects[rectIndex].y,
             m_rects[rectIndex].x + m_rects[rectIndex].width,
             m_rects[rectIndex].y + m_rects[rectIndex].height,
             Color.white,
             m_obs
             );
    }
}
