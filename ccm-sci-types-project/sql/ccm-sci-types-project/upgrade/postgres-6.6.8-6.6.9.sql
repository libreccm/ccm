\echo 'ScientificCMS SciProject module upgrade 6.6.7 -> 6.6.8 (PostgreSQL)'

begin;

#  Resource bundle has been relocated, may be updated in the content types
#  authoring step(s) as well. Not urgent, because currently seldomly used
#  (or not at all)
#  \i ../default/upgrade/6.6.7-6.6.8/add_sponsor_fundingcode.sql

end;