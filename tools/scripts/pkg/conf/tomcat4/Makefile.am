# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

scriptsdir = $(pkgdatadir)/pkg/conf/tomcat4
scripts_SCRIPTS = $(wildcard *.conf.in *.xml.in *.sh.in *.cmd.in) workers.properties.in workers.properties-win32.in

EXTRA_DIST = $(scripts_SCRIPTS)

# End of files
