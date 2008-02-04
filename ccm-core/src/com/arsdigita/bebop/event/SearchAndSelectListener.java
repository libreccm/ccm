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
package com.arsdigita.bebop.event;

import java.util.EventListener;

/**
 *  Analogous to Widget
 * PrintListeners, this is called when the widget is displayed (or
 * validated) to get the dataset.  The dataset should be created
 * dynamically so it can vary according to form variables.
 * Eventually, this may also support setting the initial value for a
 * SearchAndSelect widget, so that it may act as an edit widget as
 * well.
 *
 * @author Patrick McNeill
 * @version $Id: SearchAndSelectListener.java 287 2005-02-22 00:29:02Z sskracic $
 * @since 4.5 */
public interface SearchAndSelectListener extends EventListener {

    public static final String versionId = "$Id: SearchAndSelectListener.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    SearchAndSelectModel getModel( PageEvent e );
}
