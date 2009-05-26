# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

scriptsdir = $(pkgdatadir)/server/tomcat/bin
scripts_SCRIPTS = tomcat-conf-gen

EXTRA_DIST = $(scripts_SCRIPTS)

check-local:
	perl -I ../../../lib -c tomcat-conf-gen

# End of files
