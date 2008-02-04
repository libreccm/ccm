# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

scriptsdir = $(pkgdatadir)/lib/CCM
scripts_SCRIPTS = $(wildcard *.pm)

EXTRA_DIST = $(scripts_SCRIPTS)

SUBDIRS = Server

check-local:
	perl -I ../ CommandsUtil.pm
	perl -I ../ Interpolate.pm
	perl -I ../ Runtime.pm
	perl -I ../ Server.pm
	perl -I ../ Util.pm

# End of files
