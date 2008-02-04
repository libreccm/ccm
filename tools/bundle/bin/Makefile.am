# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

cfgdir = $(datadir)/ccm-tools/commands
cfg_SCRIPTS = hostinit-bundle load-bundle hostinit-bundle.cmd load-bundle.cmd

check-local:
	perl -Tc hostinit-bundle
	perl -Tc load-bundle

EXTRA_DIST = $(cfg_SCRIPTS)

# End of files
