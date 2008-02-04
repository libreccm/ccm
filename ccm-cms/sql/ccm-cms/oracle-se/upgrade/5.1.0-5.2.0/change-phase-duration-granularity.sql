--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: change-phase-duration-granularity.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


-- See SDM #225499.
--
-- In a nutshell, it is silly to meausure lifecycle phase default
-- duration and delay with a millisecond precision. We are bumping up
-- the granularity to one minute to accomdate the fact that the
-- "integer" type on Postgres is only 32 bits. The alternative would
-- have been to change the Postgres type of the corresponding columns
-- from "integer" to "bigint" (which is 64 bits) by forking the PDL
-- file.  This would only serve to perpetuate this whole millisecond
-- granularity folly.
--
-- As a result, the duration and delay upper limit goes from 25 days
-- to 4081 years on Postgres.

update phase_definitions
set
 default_duration = default_duration / 60000,
 default_delay = default_delay / 60000;
