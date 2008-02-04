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
-- $Id: table-nt_digests.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table nt_digests (
    digest_id         integer
                      constraint nt_digest_pk
                          primary key
                      constraint nt_digest_fk
                          references acs_objects(object_id),
    party_from        integer
                      constraint nt_digest_party_from_fk
                          references parties(party_id),
    subject           varchar(250)
                      constraint nt_digest_subject_nn 
                          not null,
    header            varchar(4000)
                      constraint nt_digest_header_nn
                          not null,
    separator         varchar(100)
                      constraint nt_digest_separator_nn
                          not null,
    signature         varchar(4000)
                      constraint nt_digest_signature_nn
                          not null,
    frequency         integer
                      default 15
                      constraint nt_digest_frequence_nn
                          not null,
    next_run          date
                      constraint nt_digest_next_run_nn
                          not null
);
