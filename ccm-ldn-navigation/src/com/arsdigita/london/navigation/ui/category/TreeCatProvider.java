/*
 * Copyright (C) 2007 Chris Gilbert All Rights Reserved.
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
 */
package com.arsdigita.london.navigation.ui.category;

import java.math.BigDecimal;
import java.util.List;

import com.arsdigita.persistence.DataCollection;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 * Given a list of categories of interest 
 * (by default, the categories in the current path)
 * return a DataCollection of Categories to be 
 * included in the menu tree 
 * 
 */
public interface TreeCatProvider {
	
	public DataCollection getTreeCats(
			List catIDs,
			BigDecimal[] selectedIDs);

}
