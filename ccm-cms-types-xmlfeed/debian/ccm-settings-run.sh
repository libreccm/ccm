#!/bin/sh
#
# Author: Berkan Eskikaya <berkan@runtime-collective.com>, 2004
#
# $Id: ccm-settings.sh,v 1.1.1.1 2004/11/12 09:29:59 fabrice Exp $

# say which app to compile, amongst those specified in the project.xml
export CCM_APP=$name

# do some checks

check_JAVA_HOME

if [ "$name" = "ccm-core" ]; then
  check_ORACLE_JDBC_LIB
fi


# set the environment

for file in `echo /etc/profile.d/ccm-{tools,devel,scripts}.sh`; do
  . $file
done

cd ..
. $CCM_SCRIPTS_HOME/bin/env-conf.sh ${appname} > /dev/null
cd -
export CCM_HOME=`pwd`

if which classic-ant > /dev/null 2>&1; then
  ANT=classic-ant
else
  ANT=ant
fi


# perhaps we don't like to build some packages -- get them from DH_OPTIONS

export CCM_DEBIAN_NO_PACKAGES=`echo $DH_OPTIONS | tr -s ' ' '\n' | grep -E '(-N|--no-package=)' | xargs | sed -re 's/(-N|--no-package)//g'`

# for now, assume that if we don't want to build some packages then we don't
# want to generate javadocs for them either

export CCM_DEBIAN_NO_JAVADOC="$CCM_DEBIAN_NO_JAVADOC $CCM_DEBIAN_NO_PACKAGES"
