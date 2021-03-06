/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.packaging;

/**
 * Help
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 * @version $Id: Help.java 287 2005-02-22 00:29:02Z sskracic $
 */
class Help extends Command {

    /**
     * Constructor
     */
    public Help() {
        super("--help", "Print generic help");
    }

    /**
     * Invoked from the central tool "MasterTool" to write help message.
     */
    public boolean run(String[] args) {
        // XXX: Should do something better eventually.
        return new Usage().run(args);
    }

}
