alter table users add banned character(1);
update users set banned=0;
alter table users alter column banned set NOT NULL;
