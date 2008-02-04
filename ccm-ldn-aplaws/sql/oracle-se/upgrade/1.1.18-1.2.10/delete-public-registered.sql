-- Update direct permissions granted to public/registered
update acs_permissions
set
    grantee_id = -200
where
    grantee_id in (select group_id from groups where name = 'The Public');

update acs_permissions
set
    grantee_id = -202
where
    grantee_id in ( select group_id from groups where name = 'Registered Users' );

-- Create group memberships of new public/registered
insert into group_member_map
select
    group_id,
    -200
from
    group_subgroup_map
where
    subgroup_id in (select group_id from groups where name = 'The Public');

insert into group_member_map
select
    group_id,
    -202
from
    group_subgroup_map
where
    subgroup_id in (select group_id from groups where name = 'Registered Users');

-- Delete the old public/registered groups

delete from
    group_member_map
where
    group_id in (
        select
            object_id
        from
            acs_objects
        where
            object_type = 'com.arsdigita.london.permissions.PublicGroup'
    );

delete from
    group_subgroup_map
where
    group_id in (
        select
            object_id
        from
            acs_objects
        where
            object_type = 'com.arsdigita.london.permissions.PublicGroup'
    );

delete from
    group_subgroup_map
where
    subgroup_id in (
        select
            object_id
        from
            acs_objects
        where
            object_type = 'com.arsdigita.london.permissions.PublicGroup'
    );

delete from
    groups
where
    group_id in (
        select
            object_id
        from
            acs_objects
        where
            object_type = 'com.arsdigita.london.permissions.PublicGroup'
    );

delete from
    parties
where
    party_id in (
        select
            object_id
        from
            acs_objects
        where
            object_type = 'com.arsdigita.london.permissions.PublicGroup'
    );

delete from
    acs_objects
where
    object_type = 'com.arsdigita.london.permissions.PublicGroup';
