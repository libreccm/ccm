# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

commandsdir = $(pkgdatadir)/commands
commands_SCRIPTS = start stop hostinit mkservice
commands_DATA = *.help *.usage

EXTRA_DIST = $(commands_SCRIPTS) $(commands_DATA)

check-local:
	perl -I ../lib -c hostinit
	perl -I ../lib -c mkservice
	perl -I ../lib -c start
	perl -I ../lib -c stop
# End of files
