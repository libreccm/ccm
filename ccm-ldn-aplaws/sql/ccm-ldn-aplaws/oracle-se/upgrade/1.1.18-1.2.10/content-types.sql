-- agendas

insert into ct_agendas (
    item_id,
    agenda_date,
    location,
    attendees,
    contact_info,
    subject_items,
    summary,
    creation_date)
select
    item_id,
    agenda_date,
    location,
    attendees,
    contact_info,
    subject_items,
    summary, 
    creation_date
from
    in_agendas; 

drop table in_agendas;

update authoring_steps 
set component = 'com.arsdigita.cms.contenttypes.ui.AgendaPropertiesStep'
where component = 'com.arsdigita.intranet.cms.ui.AgendaPropertiesStep';

-- articles

insert into ct_articles
(
    item_id,
    lead
)
select
    item_id,
    lead
from
    in_articles
where in_articles.item_id not in (select item_id from ct_articles);

update ct_articles set lead = (select lead from in_articles where in_articles.item_id = ct_articles.item_id) where lead is null;

drop table in_articles;

update authoring_steps
set component = 'com.arsdigita.cms.contenttypes.ui.ArticlePropertiesStep'
where component = 'com.arsdigita.intranet.cms.ui.ArticlePropertiesStep';

-- events

insert into ct_events
(
    item_id,
    start_date,
    end_date,
    event_date,
    location,
    tease_lead,
    main_contributor,
    event_type,
    map_link,
    cost,
    start_time,
    end_time
)
select
    item_id,
    start_date,
    end_date,
    event_date,
    location,
    tease_lead,
    main_contributor,
    event_type,
    map_link,
    cost,
    start_time,
    end_time
from
    in_events;

drop table in_events;

update authoring_steps 
set component = 'com.arsdigita.cms.contenttypes.ui.EventPropertiesStep'
where component = 'com.arsdigita.intranet.cms.ui.EventPropertiesStep';

-- jobs

insert into ct_jobs
(
    item_id,
    grade,
    closing_date,
    salary,
    body,
    ref_number,
    department,
    job_description,
    person_specification,
    contact_details
)
select
    item_id,
    grade,
    closing_date,
    salary,
    body,
    ref_number,
    department,
    job_description,
    person_specification,
    contact_details
from
    in_jobs;

drop table in_jobs;

update authoring_steps 
set component = 'com.arsdigita.cms.contenttypes.ui.JobPropertiesStep'
where component = 'com.arsdigita.intranet.cms.ui.JobPropertiesStep';

-- legal notices

insert into ct_legal_notices
(
    item_id,
    government_uid
)
select
    item_id,
    government_uid
from
    in_legal_notices;

drop table in_legal_notices;

-- minutes
insert into ct_minutes
(
    item_id,
    minute_number,
    description,
    attendees,
    action_item,
    description_of_minutes
)
select
    item_id,
    minute_number,
    description,
    attendees,
    action_item,
    description_of_minutes
from
    in_minutes;

drop table in_minutes;

update authoring_steps 
set component = 'com.arsdigita.cms.contenttypes.ui.LegalNoticePropertiesStep'
where component = 'com.arsdigita.intranet.cms.ui.LegalNoticePropertiesStep';


-- news

-- A CMS upgrade script mistakenly populates this with bogus
-- rows without lead text.
delete from ct_articles where item_id in (select item_id from in_news);
insert into ct_articles
(
    item_id,
    lead
)
select
    item_id,
    tease_lead
from
    in_news;

insert into ct_news
(
    item_id,
    news_date,
    is_homepage
)
select
    item_id,
    news_date,
    is_homepage
from
    in_news;

drop table in_news;

update authoring_steps 
set component = 'com.arsdigita.cms.contenttypes.ui.NewsItemPropertiesStep'
where component = 'com.arsdigita.intranet.cms.ui.NewsItemPropertiesStep';

-- press releases

insert into ct_press_releases
(
    item_id,
    contact_info,
    ref_code,
    summary
)
select
    item_id,
    contact_info,
    ref_code,
    summary
from
    in_press_releases;

drop table in_press_releases;

update authoring_steps 
set component = 'com.arsdigita.cms.contenttypes.ui.PressReleasePropertiesStep'
where component = 'com.arsdigita.intranet.cms.ui.PressReleasePropertiesStep';

-- services

insert into ct_service
(
    item_id,
    summary,
    services_provided,
    opening_times,
    address,
    contacts
)
select
    item_id,
    summary,
    services_provided,
    opening_times,
    address,
    contacts
from
    in_service;

drop table in_service;

update authoring_steps 
set component = 'com.arsdigita.cms.contenttypes.ui.ServicePropertiesStep'
where component = 'com.arsdigita.intranet.cms.ui.ServicePropertiesStep';


-- mp_articles

insert into ct_mp_articles
(article_id, summary) 
select article_id, summary
from 
in_mp_articles;

update authoring_steps 
set component = 'com.arsdigita.cms.contenttypes.ui.mparticle.MultiPartArticleEdit'
where component = 'com.arsdigita.intranet.cms.ui.MultiPartArticleEdit';

update authoring_steps 
set component = 'com.arsdigita.cms.contenttypes.ui.mparticle.MultiPartArticleViewSections'
where component = 'com.arsdigita.intranet.cms.ui.MultiPartArticleViewSections';


-- mp_sections
insert into ct_mp_sections
(section_id, image, text)
select section_id, image, text
from in_mp_sections;

update ct_mp_sections
set article_id = (select article from in_mp_articles_map where section = ct_mp_sections.section_id);

update ct_mp_sections
set rank = (select rank from in_mp_articles_map where section = ct_mp_sections.section_id);

drop table in_mp_articles_map;
drop table in_mp_sections;

update acs_objects
set object_type = 'com.arsdigita.cms.contenttypes.ArticleSection',
    default_domain_class = 'com.arsdigita.cms.contenttypes.ArticleSection'
where default_domain_class = 'com.arsdigita.intranet.cms.ArticleSection';
                                                                                
update content_types
set classname = 'com.arsdigita.cms.contenttypes.ArticleSection',
    object_type = 'com.arsdigita.cms.contenttypes.ArticleSection'
where object_type = 'com.arsdigita.intranet.cms.ArticleSection';

drop table in_mp_articles;
