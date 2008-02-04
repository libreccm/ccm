#!/bin/sh
#
# Author: Berkan Eskikaya <berkan@runtime-collective.com>, 2004
#
# $Id: ccm-config.sh,v 1.1.1.1 2004/11/12 09:29:59 fabrice Exp $

. debian/ccm-settings.sh

if [ ! -L $name ]; then
   ln -s . $name
fi
ccm-create-projectxml.pl > project.xml
ccm-configure.sh
