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

import com.arsdigita.bebop.Page;
// import com.arsdigita.bebop.parameters.ParameterModel;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 * Interface for classes that create forum pages. To build a page different
 * from the default, provide new implemention and register it with the 
 * com.arsdigita.forum.ForumPageFactory
 */
public interface PageBuilder {
	
	public Page buildPage(); 
	
}
