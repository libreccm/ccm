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

/**
 * Title:        ImageUtils
 * Description:  Utility functions to manipulate Images
 * @author Gavin Doughtie
 */

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource;

public class ImageUtils {
    public static final String versionId = "$Id: ImageUtils.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public ImageUtils() {
    }

    public static Image createTiledImage(Image tile, int w, int h, Component c) {
        Image i = c.createImage(w, h);
        if (null == i) {
            System.out.println("Null image for: " + w + ", " + h);
            return null;
        }

        return createTiledImage(tile, i, w, h, c);
    }

    public static Image createTiledImage(
                                         Image tile,
                                         Image output,
                                         int w,
                                         int h,
                                         ImageObserver c) {
        if (null == tile || null == output) {
            System.out.println("tile: " + tile + " output: " + output);
            return null;
        }

        int imageW = tile.getWidth(c);
        int imageH = tile.getHeight(c);
        Graphics fillG = output.getGraphics();
        if (null == fillG) {
            return null;
        }

        drawTiledImage(
                       fillG,
                       0,
                       0,
                       new Rectangle(0, 0, imageW, imageH),
                       tile,
                       w,
                       h,
                       c);
        fillG.dispose();
        return output;
    }

    public static void drawTiledImage(
                                      Graphics g,
                                      int startX,
                                      int startY,
                                      Rectangle sourceRect,
                                      Image sourceImage,
                                      int width,
                                      int height,
                                      ImageObserver obs) {
        int columns = width <= sourceRect.width ? 1 : width / sourceRect.width + 1;
        int rows = height <= sourceRect.height ? 1 : height / sourceRect.height + 1;
        int currentX = startX;
        int currentY = startY;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                g.drawImage(sourceImage,
                            currentX,
                            currentY,
                            currentX + sourceRect.width,
                            currentY + sourceRect.height,
                            sourceRect.x,
                            sourceRect.y,
                            sourceRect.x + sourceRect.width,
                            sourceRect.y + sourceRect.height,
                            Color.white,
                            obs);

                currentX += sourceRect.width;
            }
            currentY += sourceRect.height;
            currentX = startX;
        }
    }

    public static void alphaDraw
        (
         Graphics g,
         Component comp,
         int x,
         int y,
         int[] backgroundPixels,
         int bgWidth,
         int bgHeight,
         int spriteW,
         int spriteH,
         Image spriteImage,
         Image alphaImage,
         int pct
         ) {
        try {
            int[] spPixels = new int[spriteW * spriteH];
            int[] alphaPixels = new int[spPixels.length];
            int[] compositePixels = new int[spPixels.length];
            PixelGrabber pg = new PixelGrabber(
                                               spriteImage,
                                               0,
                                               0,
                                               spriteW,
                                               spriteH,
                                               spPixels,
                                               0,
                                               spriteW);
            pg.grabPixels();

            pg = new PixelGrabber(
                                  alphaImage,
                                  0,
                                  0,
                                  spriteW,
                                  spriteH,
                                  alphaPixels,
                                  0,
                                  spriteW);
            pg.grabPixels();

            // Pack the alpha channel into the sprite's pixels
            for (int i = 0; i < spPixels.length; i++) {
                // kill any alpha we might have
                spPixels[i] &= 0xFFFFFF;
                // Replace the alpha with the blue value of the alpha image
                if (null != alphaPixels) {
                    spPixels[i] |= alphaPixels[i] << 24;
                }
            }

            MemoryImageSource source = new MemoryImageSource(
                                                             spriteW, spriteH, compositePixels, 0, spriteW);
            source.setAnimated(true);
            Image compositedImage = comp.createImage(source);

            int wDiff = bgWidth - (x + spriteW);
            if (wDiff < 0) {
                spriteW += wDiff;
            }
            int hDiff = bgHeight - (y + spriteH);
            if (hDiff < 0) {
                spriteH += hDiff;
            }

            ImageUtils.alphaComposite(
                                      x,
                                      y,
                                      spriteW,
                                      spriteH,
                                      bgWidth,
                                      bgHeight,
                                      spPixels,
                                      backgroundPixels,
                                      compositePixels,
                                      pct,
                                      true,
                                      false);

            source.newPixels(0, 0, spriteW, spriteH);

            g.drawImage(compositedImage, x, y, comp);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * @author Gavin Doughtie
     * @param x top-left x-coordinate in background image that sprite overlaps
     * @param y top-left y-coordinate in background image that sprite overlaps
     * @param spWidth width of sprite in pixels
     * @param spHeight height of sprite in pixels
     * @param bgWidth width of background image in pixels
     * @param bgHeight height of background image in pixels
     * @param spritePixels pixels representing the sprite,
     * including an 8-bit Alpha channel
     * @param backgroundPixels pixels representing the background
     * over which the sprite will be composited
     * @param compositePixels pixels that will receive the composite value
     * @param pct degree of intensity for the blend, from 0 to 255
     * @param useAlpha if false, percentage blend only,
     * ignoring alpha channel in sprite
     * Alpha-composites a sprite over a background image
     */
    public static void alphaComposite
        (
         int x,
         int y,
         int spWidth,
         int spHeight,
         int bgWidth,
         int bgHeight,
         int[] spritePixels,
         int[] backgroundPixels,
         int[] compositePixels,
         int pct,
         boolean useAlpha,
         boolean updateBackground
         ) {
        int a = 0;
        int outputIndex = 0;
        int spixelvalue = 0;
        int pixelvalue = 0;

        for (int j = y; j < (y + spHeight); j++) {
            for (int i = x; i < (x + spWidth); i++) {
                int pixelIndex = (j * bgWidth) + i;

                spixelvalue=spritePixels[outputIndex];
                pixelvalue=backgroundPixels[pixelIndex];

                // unpack alpha
                if (useAlpha) {
                    a = spixelvalue >>> 24;
                    a = a * pct >> 8;
                } else {
                    a = pct;
                }

                int dstrb = pixelvalue & 0xFF00FF;
                int dstg = pixelvalue & 0xFF00;

                int srcrb = spixelvalue & 0xFF00FF;
                int srcg = spixelvalue & 0xFF00;

                int drb = dstrb - srcrb;
                int dg = dstg - srcg;

                drb *= a;
                dg *= a;
                drb >>>= 8;
                dg >>>= 8;

                int rb = (dstrb - drb) & 0xFF00FF;
                int g  = (dstg - dg) & 0xFF00;

                compositePixels[outputIndex] = 0xFF000000 | rb | g;
                if (updateBackground) {
                    backgroundPixels[pixelIndex] =
                        compositePixels[outputIndex];
                }
                outputIndex++;
            }
        }
    }
}
