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
-- $Id: content-types-create.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


-- table to hold agenda extended attributes
--
create table ct_agendas (
  item_id       integer
                constraint ct_agendas_item_id_pk primary key
                constraint ct_agendas_item_id_fk 
                references cms_text_pages
                on delete cascade,
-- should time and date be one attribute or two?
  agenda_date   date,
  location      varchar(1000),
  attendees     varchar(1000),
  subject_items varchar(1000),
  contact_info  varchar(1000),
  summary       varchar(4000),
  creation_date date
);

comment on table ct_agendas
is 'table to hold extended attributes of agenda content type';
comment on column ct_agendas.agenda_date 
is 'The date and time for the agenda';
comment on column ct_agendas.location 
is 'The location for the agenda';
comment on column ct_agendas.attendees 
is 'The attendees for the agenda';
comment on column ct_agendas.subject_items 
is 'The subject items for the agenda';
comment on column ct_agendas.contact_info 
is 'Contact information for the agenda';
comment on column ct_agendas.summary 
is 'The summary of the agenda';
comment on column ct_agendas.creation_date 
is 'The date the agenda was created';


-- table to hold event extended attributes
--
create table ct_events (
  item_id          integer
                   constraint ct_events_item_id_pk primary key
                   constraint ct_events_item_id_fk 
                   references cms_text_pages
                   on delete cascade,
  start_date       date,
  end_date         date,
  start_time       date,
  end_time         date,
  event_date       varchar(1000),
  location         varchar(1000),
  tease_lead       varchar(4000),
  mact_contributor varchar(1000),
  event_type       varchar(1000),
  map_link         varchar(1000),
  cost             varchar(1000)
);

comment on table ct_events
is 'table to hold extended attributes of event content type';
comment on column ct_events.event_date 
is 'The date and time of the event, stored as varchar for now so you can enter other information';
comment on column ct_events.start_date 
is 'The starting date and time of the event, so the events content type can be used by calendar';
comment on column ct_events.end_date 
is 'The ending date time of the event, so the events content type can be used by calendar';
comment on column ct_events.location 
is 'The location of the event';
comment on column ct_events.tease_lead 
is 'The tease/lead information for the event';
comment on column ct_events.mact_contributor
is 'The main contributor for the event';
comment on column ct_events.event_type 
is 'The type of the event';
comment on column ct_events.map_link 
is 'The link to a map for the event';
comment on column ct_events.cost 
is 'The cost of the event';


-- table to hold job extended attributes
--
create table ct_jobs (
  item_id              integer
                       constraint ct_jobs_item_id_pk primary key
                       constraint ct_jobs_item_id_fk 
                       references cms_pages
                       on delete cascade,
  grade                varchar(100),
  closing_date         date,
  salary               varchar2(4000),
  body                 varchar(4000),
  ref_number           varchar(100),
  department           varchar(1000),
  job_description      varchar(4000),
  person_specification varchar(4000),
  contact_details   varchar(4000)
);

comment on table ct_jobs
is 'table to hold extended attributes of job content type';
comment on column ct_jobs.grade 
is 'The grade for the job';
comment on column ct_jobs.closing_date 
is 'The closing date for the job';
comment on column ct_jobs.salary 
is 'The salary for the job';
comment on column ct_jobs.body 
is 'The overview of the job';
comment on column ct_jobs.ref_number 
is 'The reference number for the job';
comment on column ct_jobs.department 
is 'The department for the job';
comment on column ct_jobs.job_description 
is 'The description of the job';
comment on column ct_jobs.person_specification 
is 'The person specification for the job';
comment on column ct_jobs.contact_details
is 'The contact details for the job';

-- table to hold legal notice extended attributes
--
create table ct_legal_notices (
  item_id        integer
                 constraint ct_legal_notices_item_id_pk primary key
                 constraint ct_legal_notices_item_id_fk 
                 references cms_text_pages
                 on delete cascade,
  government_uid varchar(100)
);

comment on table ct_legal_notices
is 'table to hold extended attributes of legal notice content type';
comment on column ct_legal_notices.government_uid 
is 'The government issued UID for the legal notice';


-- table to hold minutes extended attributes
--
create table ct_minutes (
  item_id        integer
                 constraint ct_minutes_item_id_pk primary key
                 constraint ct_minutes_item_id_fk 
                 references cms_text_pages
                 on delete cascade,
  minute_number varchar(100),
  description   varchar(4000),
  action_item   varchar(4000),
  attendees     varchar(1000),
  -- is the below needed or is it a duplicate of the description?
  description_of_minutes varchar(4000)
);

comment on table ct_minutes
is 'table to hold extended attributes of minutes content type';
comment on column ct_minutes.minute_number 
is 'The minute number for the minutes';
comment on column ct_minutes.description 
is 'The description of the minutes';
comment on column ct_minutes.action_item 
is 'Action item(s) for the minutes';
comment on column ct_minutes.attendees 
is 'The attendees for the minutes';
comment on column ct_minutes.description_of_minutes 
is 'The description for the minutes';


-- table to hold news extended attributes
--
create table ct_news (
  item_id        integer
                 constraint ct_news_item_id_pk primary key
                 constraint ct_news_item_id_fk 
                 references cms_articles
                 on delete cascade,
  tease_lead     varchar(4000),
  news_date      date,
  is_homepage    integer
                 constraint ct_news_ih_ck
                 check (is_homepage in (0,1))
);

comment on table ct_news
is 'table to hold extended attributes of news content type';
comment on column ct_news.tease_lead 
is 'The tease/lead paragraph for the news item';
comment on column ct_news.news_date 
is 'The date for the news item';


-- Table to hold press release extended attributes.  This duplicates
-- Michael Slater's apl_press_releases type, but the attributes are
-- different. We need to determine which one is correct.
create table ct_press_releases (
  item_id       integer
                constraint ct_press_releases_item_id_pk primary key
                constraint ct_press_releases_item_id_fk 
                references cms_text_pages
                on delete cascade,
  contact_info  varchar(1000),
  summary       varchar(4000),
  ref_code      varchar(80)
);

comment on table ct_press_releases
is 'table to hold extended attributes of press release content type';
comment on column ct_press_releases.ref_code
is 'the (arbitrary) reference code for the press release';
comment on column ct_press_releases.summary
is 'the text summary for the press release';
comment on column ct_press_releases.contact_info
is 'contact information text for the press release';

-- table to hold service extended attributes
--
create table ct_service (
  item_id           integer
                    constraint ct_service_item_id_pk primary key
                    constraint ct_service_item_id_fk 
                    references cms_pages
                    on delete cascade,
  summary           varchar(4000),
  services_provided varchar(1000),
  opening_times     varchar(1000),
  address           varchar(1000),
  contacts          varchar(1000)
);

comment on table ct_service
is 'table to hold extended attributes of service content type';
comment on column ct_service.summary 
is 'The summary for the service item';
comment on column ct_service.services_provided 
is 'The services provided by the service item';
comment on column ct_service.opening_times 
is 'The opening times for the service item';
comment on column ct_service.address 
is 'The address for the service item';
comment on column ct_service.contacts 
is 'The contacts for the service item';


-- table to hold multi-part articles
--
create table ct_mp_articles (
    article_id  integer
                constraint ct_mp_articles_item_id_pk primary key
                constraint ct_mp_articles_item_id_fk
                    references cms_pages(item_id)
                    on delete cascade,
    summary     varchar(4000)
);

comment on table ct_mp_articles
is 'table to hold multi-part article content type';
comment on column ct_mp_articles.summary
is 'the summary of the article';

-- table to hold section contents for articles
--
create table ct_mp_sections (
    section_id  integer
                constraint ct_mp_sections_section_id_pk primary key
                constraint ct_mp_sections_section_id_fk
                    references cms_pages(item_id)
                    on delete cascade,
    text        integer
                constraint ct_mp_sections_text_fk
                    references cms_text(text_id)
                    on delete cascade,
    image       integer
                constraint ct_mp_sections_image_fk
                    references cms_images(image_id)
                    on delete cascade
);

comment on table ct_mp_sections
is 'table to hold sections for multi-part article content type';
comment on column ct_mp_sections.text
is 'text body for section';
comment on column ct_mp_sections.image
is 'image for section';


-- table to map mutli-part articles and the individual sections
--
create table ct_mp_articles_map (
    article     integer
                  constraint ct_mp_articles_map_article_fk
                    references ct_mp_articles(article_id),
    section     integer
                  constraint ct_mp_articles_map_section_fk
                    references ct_mp_sections(section_id)
                    on delete cascade,
    rank        integer
);

create table ct_articles (
  item_id          integer
                   constraint ct_articles_pk primary key
                   constraint ct_articles_item_id_fk 
                   references cms_articles
                   on delete cascade,
  lead             varchar(1000)
);
