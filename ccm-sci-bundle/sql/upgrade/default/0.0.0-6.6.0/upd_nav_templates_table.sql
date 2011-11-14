--
-- Copyright (C) 2011 Peter Boy All Rights Reserved.
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
-- $DateTime: 2011/11/10 23:15:09 $
-- $Id: upd_nav_templates_table  pboy $

-- Internal use at Bremen University only!
-- Updates various university sites from als ccm-xxx-aplaws to scm-sci-bundle

-- Update new path
update nav_templates
    set url=replace(url,'packages/navigation/templates'
                       ,'templates/ccm-navigation/navigation')
  where url like '%packages/navigation/templates%'; 


-- IAW: update jsp names
update nav_templates
    set url=replace(url,'iaw-atoz','sci-atoz'),
        description=replace(description,'IAW','SCI'),
        title=replace(title,'IAW','SCI')
  where url like '%iaw-atoz%'; 

update nav_templates
    set url=replace(url,'iaw-default' ,'sci-default'),
        description=replace(description,'IAW','SCI'),
        title=replace(title,'IAW','SCI')
  where url like '%iaw-default%'; 

update nav_templates
    set url=replace(url,'iaw-portal' ,'sci-portal'),
        description=replace(description,'IAW','SCI'),
        title=replace(title,'IAW','SCI')
  where url like '%iaw-portal%'; 

update nav_templates
    set url=replace(url,'iaw-recent' ,'sci-recent'),
        description=replace(description,'IAW','SCI'),
        title=replace(title,'IAW','SCI')
  where url like '%iaw-recent%'; 

update nav_templates
    set url=replace(url,'iaw-welcome' ,'sci-welcome'),
        description=replace(description,'IAW','SCI'),
        title=replace(title,'IAW','SCI')
  where url like '%iaw-welcome%'; 
