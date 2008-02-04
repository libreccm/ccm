# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

scriptsdir = $(pkgdatadir)/lib/CCM/Server
scripts_SCRIPTS = $(wildcard *.pm)

EXTRA_DIST = $(scripts_SCRIPTS)

check-local:
	perl -I ../../ -c Resin.pm
	perl -I ../../ -c Tomcat.pm

# End of files
