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

OS=`uname -s`
MD5SUM='md5sum'

if [ -d "$CCM_DIST_ZIP_DIR" ]; then
  cd $CCM_DIST_ZIP_DIR

  for i in `ls $CCM_PACKAGE_NAME*.zip 2>/dev/null`
  do
    echo "MD5 sum for $i"
    $MD5SUM $i > $i.md5
  done
fi;

if [ -d "$CCM_DIST_TAR_DIR" ]; then
  cd $CCM_DIST_TAR_DIR

  for i in `ls $CCM_PACKAGE_NAME*.tar.gz 2>/dev/null`
  do
    echo "MD5 sum for $i"
    $MD5SUM $i > $i.md5
  done
fi;

exit 0
