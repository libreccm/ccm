jsp files replace  files provided by ccm-ldn-portal.

goal: Make the portal homepage read-only and cached for 15min. EXPERIMENTAL

Dynamic no-cache version is available at /ccm/portal/custom.jsp for admins
to get the 'customize area' links.
see r1082

Actually:
In the default configuration BaseDispatcher searches in the following order:
1. /templates/ccm-ldn-portal/portal/index.jsp
2. /templates/ccm-ldn-portal/portal/index.html
3. /templates/ccm-ldn-portal/index.jsp
4. /templates/ccm-ldn-portal/index.html  (probably, not tested)

So, currently the first try is successfull and
aplaws/ui/HomepageWorkspaceSelectionModel is always used via index.jsp

