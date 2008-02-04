# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

SUBDIRS = bin project

etcdir = $(sysconfdir)/$(PACKAGE)
etc_DATA = resin.conf.in server.xml.in envvars.in
etc_SCRIPTS = project*.sh project*.pl

profiledir = $(sysconfdir)/profile.d
profile_SCRIPTS = ccm-devel.sh ccm-devel-cvs.sh

vardir = $(localstatedir)/lib/$(PACKAGE)
var_DATA = portalloc.txt

EXTRA_DIST = $(etc_DATA) $(var_DATA) $(profile_SCRIPTS) $(etc_SCRIPTS)

check-local:
	perl -c project.pl

# End of file
