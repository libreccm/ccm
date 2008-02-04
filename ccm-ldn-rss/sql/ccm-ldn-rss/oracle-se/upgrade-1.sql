update apm_package_types 
    set dispatcher_class = 'com.arsdigita.london.rss.dispatcher.Dispatcher' 
  where dispatcher_class = 'com.arsdigita.london.rss.ui.Dispatcher';

drop table rss_channels;

@@ rss-create.sql

