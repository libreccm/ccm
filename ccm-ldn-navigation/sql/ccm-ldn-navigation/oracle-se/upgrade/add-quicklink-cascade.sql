alter table nav_quick_links add 
	(cascade_link CHAR(1));
    
update nav_quick_links 
set cascade_link = 1; 

alter table nav_quick_links 
modify (cascade_link CHAR(1) not null);