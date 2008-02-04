#!/bin/sh
#
# Author: Berkan Eskikaya <berkan@runtime-collective.com>, 2004
#
# $Id: ccm-install.sh,v 1.1.1.1 2004/11/12 09:29:59 fabrice Exp $

. debian/ccm-settings.sh

DESTDIR="$1"
[ "xDESTDIR" != "x" ] || exit 1

rm -rf $DESTDIR
mkdir -p $DESTDIR
mkdir -p $DESTDIR${confdir}
mkdir -p $DESTDIR/var/www/html/${name}-${version}

ANT_OPTS="-Dapps.${name}.version=${version}"
ANT_OPTS="$ANT_OPTS -Ddeploy.conf.dir=$DESTDIR${confdir}"
ANT_OPTS="$ANT_OPTS -Ddeploy.shared.lib.dir=$DESTDIR${sharedjardir}"
ANT_OPTS="$ANT_OPTS -Ddeploy.private.lib.dir=$DESTDIR${sharedjardir}/${name}-${version}"
ANT_OPTS="$ANT_OPTS -Ddeploy.webapp.dir=$DESTDIR${webappdir}/${name}-${version}"
ANT_OPTS="$ANT_OPTS -Ddeploy.system.jars.dir=$DESTDIR${sharedjardir}"
ANT_OPTS="$ANT_OPTS -Ddeploy.api.dir.${appname}=$DESTDIR/var/www/html/${name}-${version}/api"
ANT_OPTS="$ANT_OPTS -Ddeploy.shared.classes.dir=$DESTDIR/tmp/${name}-${version}"
export ANT_OPTS

$ANT deploy-jar-classes-${appname}
$ANT deploy-${appname}

rm -rf "$DESTDIR/tmp"


# only generate and deploy javadocs if we didn't specify otherwise in CCM_DEBIAN_NO_JAVADOC

GENERATE_JAVADOC=true
if [ `echo $CCM_DEBIAN_NO_JAVADOC | tr ' ' '\n' | grep -c "^${name}-doc$"` -eq 1 ]; then
  GENERATE_JAVADOC=false
fi

if [ -d ${appname}/src ]; then
  $ANT deploy-jar-classes-${appname}
  [ "$GENERATE_JAVADOC" == "true" ] && $ANT deploy-javadoc-${appname} || true
fi
