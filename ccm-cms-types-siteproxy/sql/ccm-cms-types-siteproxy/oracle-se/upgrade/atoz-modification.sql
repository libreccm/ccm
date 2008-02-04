alter table ct_siteproxy add (
    title_atoz VARCHAR(200)
);
alter table ct_siteproxy add (
    used_in_atoz CHAR(1)
        constraint ct_sitepro_use_in_atoz_c_fitem
          check(used_in_atoz in ('0', '1'))
);
