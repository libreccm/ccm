# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

scriptsdir = $(pkgdatadir)/server/resin/bin
scripts_SCRIPTS = resin-conf-gen

EXTRA_DIST = $(scripts_SCRIPTS)

check-local:
	perl -I ../../../lib -c resin-conf-gen

# End of files
