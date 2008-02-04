/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.portal.ui;

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
        USER_16 = new Image("/packages/workspace/www/assets/User16.gif");
        USER_16.setBorder("0");
        USER_16.lock();

        GROUP_16 = new Image("/packages/workspace/www/assets/Group16.gif");
        GROUP_16.setBorder("0");
        GROUP_16.lock();

        LEFT_16 = new Image("/packages/workspace/www/assets/left.gif");
        LEFT_16.setBorder("0");
        LEFT_16.lock();

        RIGHT_16 = new Image("/packages/workspace/www/assets/right.gif");
        RIGHT_16.setBorder("0");
        RIGHT_16.lock();

        UP_16 = new Image("/packages/workspace/www/assets/up.gif");
        UP_16.setBorder("0");
        UP_16.lock();

        DOWN_16 = new Image("/packages/workspace/www/assets/down.gif");
        DOWN_16.setBorder("0");
        DOWN_16.lock();

        RADIO_EMPTY_16 =
            new Image("/packages/workspace/www/assets/RadioEmpty16.gif");
        RADIO_EMPTY_16.setBorder("0");
        RADIO_EMPTY_16.lock();

        RADIO_FULL_16 =
            new Image("/packages/workspace/www/assets/RadioFull16.gif");
        RADIO_FULL_16.setBorder("0");
        RADIO_FULL_16.lock();

        RADIO_EMPTY_GRAYED_16 =
            new Image("/packages/workspace/www/assets/RadioEmptyGrayed16.gif");
        RADIO_EMPTY_GRAYED_16.setBorder("0");
        RADIO_EMPTY_GRAYED_16.lock();

        RADIO_FULL_GRAYED_16 =
            new Image("/packages/workspace/www/assets/RadioFullGrayed16.gif");
        RADIO_FULL_GRAYED_16.setBorder("0");
        RADIO_FULL_GRAYED_16.lock();

        CHECK_EMPTY_16 =
            new Image("/packages/workspace/www/assets/CheckEmpty16.gif");
        CHECK_EMPTY_16.setBorder("0");
        CHECK_EMPTY_16.lock();

        CHECK_FULL_16 =
            new Image("/packages/workspace/www/assets/CheckFull16.gif");
        CHECK_FULL_16.setBorder("0");
        CHECK_FULL_16.lock();

        TRASH_16 = new Image("/packages/workspace/www/assets/delete.gif");
        TRASH_16.setBorder("0");
        TRASH_16.lock();
    }
};
