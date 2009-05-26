#!/bin/sh
#
# Configures a CCM application

if [ "x$JAVA_HOME" = "x" ]; then
	echo "JAVA_HOME not set"
	exit 1
fi

if [ "x$CCM_CONFIG_HOME" = "x" ]; then
	echo "CCM_CONFIG_HOME not set"
	exit 1
fi

OS=`uname -s`
if [ "x$OS" = "xSunOS" ]; then
  PATH=/usr/xpg4/bin:$PATH
fi

USER_ID=`id -u`

if [ "$USER_ID" == "0" ]; then
  NAME='*'
else
  NAME=`id -un`
fi

for dir in `find /var/ccm-devel/dev/$NAME/* -type d -prune`; do
  if [ -r "$dir/project.xml" ]; then
    cd $dir
    echo -n "running ccm-configure.sh in $dir... "
    OUTPUT=`ccm-configure.sh 2>&1`
    if [ "$?" == "0" ]; then
      echo "done"
    else
      echo 
      echo "error: $OUTPUT"
    fi
  fi
done

