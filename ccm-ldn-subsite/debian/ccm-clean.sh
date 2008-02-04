#!/bin/sh
#
# Author: Berkan Eskikaya <berkan@runtime-collective.com>, 2004
#
# $Id: ccm-clean.sh,v 1.1.1.1 2004/11/12 09:30:16 fabrice Exp $

. debian/ccm-settings.sh

[ -f build.xml ] && $ANT clean-${appname}

rm -rf build build.xml ccm.classpath

# ccm-disperse.sh creates the .install files automatically, so let's clean them too.

rm -f ${appname}.install ${appname}-doc.install
