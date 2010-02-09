/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.forum;

import org.apache.log4j.Logger;

import java.util.TimerTask;

import com.arsdigita.util.UncheckedWrapperException;


/**
 *
 * @version $Revision: 1.1 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: RemoveUnattachedAssetsTask.java,v 1.1 2006/07/13 10:19:28 cgyg9330 Exp $
 **/
class RemoveUnattachedAssetsTask extends TimerTask {

    private static final Logger s_log = Logger.getLogger(RemoveUnattachedAssetsTask.class);
    public void run() {
            RemoveUnattachedAssetsScheduler.run();
       
    }

}
