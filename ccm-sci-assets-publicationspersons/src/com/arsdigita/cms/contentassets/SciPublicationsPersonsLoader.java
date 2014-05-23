/*
 * Copyright (c) 2014 Jens Pelzetter
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
package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.RelationAttributeImportTool;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.runtime.ScriptContext;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPersonsLoader extends PackageLoader {

    @Override
    public void run(final ScriptContext ctx) {

         final RelationAttributeImportTool importTool = new RelationAttributeImportTool();
        importTool.loadData("WEB-INF/resources/publications_persons_relations.xml");
    }

}
