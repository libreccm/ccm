#!/bin/sh
#
# Author: Berkan Eskikaya <berkan@runtime-collective.com>, 2004
#
# $Id: ccm-build.sh,v 1.1.1.1 2004/11/12 09:29:59 fabrice Exp $

. debian/ccm-settings.sh

# for eg Oracle jar
CLASSPATH=./lib/:$CLASSPATH
$ANT build-${name}
