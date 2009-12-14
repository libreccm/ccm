PROMPT Creating tablespace ccm

create tablespace ccm
  logging
  datafile 'ccm.dbf' 
  size 32m 
  autoextend on 
  next 32m maxsize 2048m
  extent management local;

QUIT;