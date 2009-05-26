#!/bin/sh
#

# Exit immediately if command fails
set -e

# Print command executed to stdout
if [ "x$CCM_SCRIPTS_VERBOSE" = "x1" ]; then
  set -v
fi

if [ "x$CCM_SCRIPTS_HOME" = "x" ]; then
  echo "Please set the CCM_SCRIPTS_HOME environment variable"
  exit -1
fi

# Pull in env variables for configuration
. $CCM_SCRIPTS_HOME/bin/env-conf.sh

if [ ! -d $CCM_BUILD_DIR ]; then
  mkdir $CCM_BUILD_DIR
fi

# Make the source distributions
[ "$CCM_SKIP_SOURCE" != "1" ] && nice -n 20 $CCM_SCRIPTS_HOME/bin/make-source

# Make The binary distribution
nice -n 20 $CCM_SCRIPTS_HOME/bin/make-dist.sh

# Calculate checksums for all generated pacakges
nice -n 20 $CCM_SCRIPTS_HOME/bin/calc-checksum.sh

exit 0

# End of file
