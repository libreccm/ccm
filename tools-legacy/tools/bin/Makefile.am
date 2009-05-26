# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

bin_SCRIPTS = ccm ccm-run

scriptsdir = $(pkgdatadir)/bin
scripts_SCRIPTS = javaconfig

EXTRA_DIST = $(bin_SCRIPTS) $(scripts_SCRIPTS)

check-local:
	perl -I ../lib -c ccm
	perl -I ../lib -c ccm-run
# End of files
