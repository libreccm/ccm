#!/bin/sh
#

if [ "x$CCM_SCRIPTS_HOME" = "x" ]; then
  echo "Please set the CCM_SCRIPTS_HOME environment variable"
  exit 1
fi

$CCM_SCRIPTS_HOME/bin/build.sh

exit 0

# End of file
