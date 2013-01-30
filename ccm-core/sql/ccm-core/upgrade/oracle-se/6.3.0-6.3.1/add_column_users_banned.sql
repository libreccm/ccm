alter table users add (banned char(1));
update users set banned=0;
alter table users modify (banned not null);
