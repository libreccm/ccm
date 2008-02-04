-- Delete bogus entries
delete from bebop_component_hierarchy
      where not exists(
                select 1
                  from acs_objects
                 where object_id = bebop_component_hierarchy.container_id
                   and object_type = 'com.arsdigita.formbuilder.FormSection'
            )
        and not exists(
                select 1
                  from bebop_component_hierarchy h2
                 where h2.component_id = bebop_component_hierarchy.container_id
            );

delete from forms_dd_select
          where not exists( select 1 from bebop_component_hierarchy h
                                where h.component_id = forms_dd_select.widget_id
                                   or h.container_id = forms_dd_select.widget_id);

delete from bebop_widgets
      where not exists( select 1 from bebop_component_hierarchy h
                                where h.component_id = bebop_widgets.widget_id
                                   or h.container_id = bebop_widgets.widget_id );

delete from bebop_listener_map
      where not exists( select 1 from bebop_component_hierarchy h
                                where h.component_id = bebop_listener_map.component_id
                                   or h.container_id = bebop_listener_map.component_id );

delete from bebop_listeners
      where not exists( select 1 from bebop_listener_map m
                                where bebop_listeners.listener_id = m.listener_id );

delete from bebop_components
      where not exists(
                select 1
                  from bebop_component_hierarchy h
                 where h.component_id = bebop_components.component_id
                    or h.container_id = bebop_components.component_id
            )
        and not exists (
                select 1
                  from acs_objects o
                 where o.object_id = bebop_components.component_id
                   and o.object_type = 'com.arsdigita.formbuilder.FormSection'
            );

delete from bebop_options
      where not exists( select 1 from bebop_components c
                                where bebop_options.option_id = c.component_id );

-- Delete orphaned Listeners

delete from bebop_component_hierarchy
      where component_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.Listener'
                   and not exists(
                           select 1
                             from bebop_listeners l
                            where o.object_id = l.listener_id
                       )
            );

delete from bebop_components
      where component_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.Listener'
                   and not exists(
                           select 1
                             from bebop_listeners l
                            where o.object_id = l.listener_id
                       )
            );

delete from object_context
      where object_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.Listener'
                   and not exists(
                           select 1
                             from bebop_listeners l
                            where o.object_id = l.listener_id
                       )
            );

delete from object_container_map
      where object_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.Listener'
                   and not exists(
                           select 1
                             from bebop_listeners l
                            where o.object_id = l.listener_id
                       )
            );

delete from acs_objects
      where acs_objects.object_type = 'com.arsdigita.formbuilder.Listener'
        and not exists(
                select 1
                  from bebop_listeners l
                 where acs_objects.object_id = l.listener_id
            );

-- Delete orphaned WidgetLabels

delete from bebop_component_hierarchy
      where component_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.WidgetLabel'
                   and not exists(
                           select 1
                             from forms_widget_label l
                            where o.object_id = l.label_id
                       )
            );

delete from bebop_components
      where component_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.WidgetLabel'
                   and not exists(
                           select 1
                             from forms_widget_label l
                            where o.object_id = l.label_id
                       )
            );

delete from object_context
      where object_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.WidgetLabel'
                   and not exists(
                           select 1
                             from forms_widget_label l
                            where o.object_id = l.label_id
                       )
            );

delete from object_container_map
      where object_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.WidgetLabel'
                   and not exists(
                           select 1
                             from forms_widget_label l
                            where o.object_id = l.label_id
                       )
            );

delete from acs_objects
      where acs_objects.object_type = 'com.arsdigita.formbuilder.WidgetLabel'
        and not exists(
                select 1
                  from forms_widget_label l
                 where acs_objects.object_id = l.label_id
            );

-- Delete orphaned Options

delete from bebop_component_hierarchy
      where component_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.Option'
                   and not exists(
                           select 1
                             from bebop_options b
                            where o.object_id = b.option_id
                       )
            );

delete from bebop_components
      where component_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.Option'
                   and not exists(
                           select 1
                             from bebop_options b
                            where o.object_id = b.option_id
                       )
            );

delete from object_context
      where object_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.Option'
                   and not exists(
                           select 1
                             from bebop_options b
                            where o.object_id = b.option_id
                       )
            );

delete from object_container_map
      where object_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.Option'
                   and not exists(
                           select 1
                             from bebop_options b
                            where o.object_id = b.option_id
                       )
            );

delete from acs_objects
      where acs_objects.object_type = 'com.arsdigita.formbuilder.Option'
        and not exists(
                select 1
                  from bebop_options b
                 where acs_objects.object_id = b.option_id
            );

-- Delete orphaned Widgets

delete from bebop_component_hierarchy
      where component_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.Widget'
                   and not exists(
                           select 1
                             from bebop_widgets w
                            where o.object_id = w.widget_id
                       )
            );

delete from bebop_components
      where component_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.Widget'
                   and not exists(
                           select 1
                             from bebop_widgets w
                            where o.object_id = w.widget_id
                       )
            );

delete from object_context
      where object_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.Widget'
                   and not exists(
                           select 1
                             from bebop_widgets w
                            where o.object_id = w.widget_id
                       )
            );

delete from object_container_map
      where object_id in (
                select o.object_id
                  from acs_objects o
                 where o.object_type = 'com.arsdigita.formbuilder.Widget'
                   and not exists(
                           select 1
                             from bebop_widgets w
                            where o.object_id = w.widget_id
                       )
            );

delete from acs_objects
      where acs_objects.object_type = 'com.arsdigita.formbuilder.Widget'
        and not exists(
                select 1
                  from bebop_widgets w
                 where acs_objects.object_id = w.widget_id
            );
