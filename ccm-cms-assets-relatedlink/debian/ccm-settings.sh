#!/bin/sh
#
# Author: Berkan Eskikaya <berkan@runtime-collective.com>, 2004
#
# $Id: ccm-settings.sh,v 1.1.1.1 2004/11/12 09:29:59 fabrice Exp $

# source the shell library [for the check_* functions]
# FIXME: ugly-looking hack.
orig_DEBIAN_HAS_FRONTEND=$DEBIAN_HAS_FRONTEND
DEBIAN_HAS_FRONTEND=1
. /usr/share/ccm-tools/lib/shellmodule
DEBIAN_HAS_FRONTEND=$orig_DEBIAN_HAS_FRONTEND


# variables we use [especially in ccm-install.sh]

name=ccm-cms-assets-relatedlink
version=6.3.0
appname=ccm-cms-assets-relatedlink
appversion=6.3.0
confdir="/etc/ccm/conf"
sharedjardir="/usr/share/java"
webappdir="/usr/share/java/webapps"
ccmclasspathfile="ccm/ccm.classpath"
ccmwebappfile="ccm/ccm.webapps"

. debian/ccm-settings-run.sh
