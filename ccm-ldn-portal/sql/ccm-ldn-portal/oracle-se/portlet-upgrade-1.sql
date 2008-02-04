create table portlet_freeform_html (
    portlet_id integer not null
        constraint ptl_ffh_portlet_id_pk primary key
        constraint ptl_ffh_portlet_id_fk
        references portlets (portlet_id)
        on delete cascade,
    content varchar2(4000)  
);


create table portlet_content_item (
    portlet_id integer not null
        constraint ptl_ci_portlet_id_pk primary key
        constraint ptl_ci_portlet_id_fk
        references portlets (portlet_id)
        on delete cascade,
    item_id integer
        constraint ptl_ci_item_id_fk
        references cms_items (item_id)
        on delete set null
);



create table portlet_rss_feed (
    portlet_id integer not null
        constraint ptl_rss_portlet_id_pk primary key
        constraint ptl_rss_portlet_id_fk
        references portlets (portlet_id)
        on delete cascade,
    url varchar2(250)  
);


