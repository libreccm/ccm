--
-- Copyright (C) 2012 Peter Boy All Rights Reserved.
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
-- $Id: add_domainprovider_inits.sql pboy $

-- With this update the package ccm-atoz has been divided into a generic atoz
-- package and optional extensions, e.g. this ldn (terms ESD domains) extension.
-- Therefore, in an update situation where atoz is already installed, all
-- tables exists and we must just added Initializer to the startup.


INSERT INTO inits (class_name) 
       VALUES ('com.arsdigita.london.atoz.Initializer') ;

INSERT INTO init_requirements (required_init, init) 
       VALUES ('com.arsdigita.atoz.Initializer',
               'com.arsdigita.london.atoz.Initializer') ;

INSERT INTO init_requirements (required_init, init) 
       VALUES ('com.arsdigita.core.Initializer',
               'com.arsdigita.london.atoz.Initializer') ;

