--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: upd_table_authoring_steps.sql pboy $

update authoring_steps
    set   component='com.arsdigita.cms.ui.authoring.GenericArticleBody'
    where component like '%TextPageBody%' ;

update authoring_steps
    set   label_bundle='com.arsdigita.cms.CMSResources'
    where label_bundle like 'com.arsdigita.cms.ui.CMSResources'  ;

update authoring_steps
    set   description_bundle='com.arsdigita.cms.CMSResources'
    where description_bundle like 'com.arsdigita.cms.ui.CMSResources'  ;
