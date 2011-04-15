/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver;


import com.arsdigita.bebop.Image;

/**
 * This class contains Images for the various CW icons.  These image
 * instances are all locked, so they may not be directly added to a
 * page's static structure -- instead, they must be cloned.  However,
 * they can be used within a list or table cell renderer.
 **/
public class Icons {


    public static final Image USER_16;
    public static final Image GROUP_16;
    public static final Image LEFT_16;
    public static final Image RIGHT_16;
    public static final Image UP_16;
    public static final Image DOWN_16;
    public static final Image RADIO_EMPTY_16;
    public static final Image RADIO_FULL_16;
    public static final Image RADIO_EMPTY_GRAYED_16;
    public static final Image RADIO_FULL_GRAYED_16;
    public static final Image CHECK_EMPTY_16;
    public static final Image CHECK_FULL_16;
    public static final Image TRASH_16;

    static {
        USER_16 = new Image("/packages/portalserver/www/assets/User16.gif");
        USER_16.setBorder("0");
        USER_16.lock();

        GROUP_16 = new Image("/packages/portalserver/www/assets/Group16.gif");
        GROUP_16.setBorder("0");
        GROUP_16.lock();

        LEFT_16 = new Image("/assets/cw/general/left.gif");
        LEFT_16.setBorder("0");
        LEFT_16.lock();

        RIGHT_16 = new Image("/assets/cw/general/right.gif");
        RIGHT_16.setBorder("0");
        RIGHT_16.lock();

        UP_16 = new Image("/assets/cw/general/up.gif");
        UP_16.setBorder("0");
        UP_16.lock();

        DOWN_16 = new Image("/assets/cw/general/down.gif");
        DOWN_16.setBorder("0");
        DOWN_16.lock();

        RADIO_EMPTY_16 =
            new Image("/packages/portalserver/www/assets/RadioEmpty16.gif");
        RADIO_EMPTY_16.setBorder("0");
        RADIO_EMPTY_16.lock();

        RADIO_FULL_16 =
            new Image("/packages/portalserver/www/assets/RadioFull16.gif");
        RADIO_FULL_16.setBorder("0");
        RADIO_FULL_16.lock();

        RADIO_EMPTY_GRAYED_16 =
            new Image("/packages/portalserver/www/assets/RadioEmptyGrayed16.gif");
        RADIO_EMPTY_GRAYED_16.setBorder("0");
        RADIO_EMPTY_GRAYED_16.lock();

        RADIO_FULL_GRAYED_16 =
            new Image("/packages/portalserver/www/assets/RadioFullGrayed16.gif");
        RADIO_FULL_GRAYED_16.setBorder("0");
        RADIO_FULL_GRAYED_16.lock();

        CHECK_EMPTY_16 =
            new Image("/packages/portalserver/www/assets/CheckEmpty16.gif");
        CHECK_EMPTY_16.setBorder("0");
        CHECK_EMPTY_16.lock();

        CHECK_FULL_16 =
            new Image("/packages/portalserver/www/assets/CheckFull16.gif");
        CHECK_FULL_16.setBorder("0");
        CHECK_FULL_16.lock();

        TRASH_16 = new Image("/assets/cw/general/delete.gif");
        TRASH_16.setBorder("0");
        TRASH_16.lock();

    }

};
