#!/bin/sh
#
# APLAWS+ Bundle reconfiguration script
#
# Idea from WindowMaker installation script by Alfredo K. Kojimo
#
# Copyright (C) 1999-2002 Daniel Berrange
#

CONFIG_OPT=""

# -----------------------------------------------------------
# First off we need a whole lot of clever functions

if [ "$1" = "-mono" -o $TERM != "xterm" ]; then

ES_DEF=""
ES_BLK_FG=""
ES_RED_FG=""
ES_GRN_FG=""
ES_YEL_FG=""
ES_BLU_FG=""
ES_PUR_FG=""
ES_CYA_FG=""
ES_GRY_FG=""

ES_BLK_BG=""
ES_RED_BG=""
ES_GRN_BG=""
ES_YEL_BG=""
ES_BLU_BG=""
ES_PUR_BG=""
ES_CYA_BG=""
ES_GRY_BG=""

ES_BOLD=""
ES_USCORE=""
ES_BLINK=""
ES_INVERT=""
ES_HIDDEN=""

else

ES_DEF="\033[0m"
ES_BLK_FG="\033[30m"
ES_RED_FG="\033[31m"
ES_GRN_FG="\033[32m"
ES_YEL_FG="\033[33m"
ES_BLU_FG="\033[34m"
ES_PUR_FG="\033[35m"
ES_CYA_FG="\033[36m"
ES_GRY_FG="\033[37m"

ES_BLK_BG="\033[40m"
ES_RED_BG="\033[41m"
ES_GRN_BG="\033[42m"
ES_YEL_BG="\033[43m"
ES_BLU_BG="\033[44m"
ES_PUR_BG="\033[45m"
ES_CYA_BG="\033[46m"
ES_GRY_BG="\033[47m"

ES_BOLD="\033[1m"
ES_USCORE="\033[4m"
ES_BLINK="\033[5m"
ES_INVERT="\033[7m"
ES_HIDDEN="\033[8m"

fi


plain()
{
  echo "$1"
}

pretty()
{
  echo -e "$ES_BOLD$ES_YEL_FG$1$ES_DEF"
}

alert()
{
  echo -e "$ES_RED_FG$1$ES_DEF"
}


death() 
{
  alert
  bar
  alert "Reconfiguration process failed. Please inform the APLAWS+ development"
  alert "team of the problem encountered."
  wide_bar
  exit
}

abort()
{
  alert
  bar
  alert "Reconfiguration process aborted by user. Thankyou, please come again."
  wide_bar
  exit
}

prompt()
{
  echo -n -e "$ES_BOLD$ES_GRN_FG> $ES_GRY_FG"
  read flibble
  
  if [ "x$flibble" = "x" ]; then
    flibble="$1"
  fi
  echo -n -e "$ES_DEF"
}


bar()
{
  echo -e "$ES_BOLD--------------------------------------------------------------------$ES_DEF"
}


wide_bar()
{
  echo -e "$ES_BOLD====================================================================$ES_DEF"
}


command()
{
  echo -e "$ES_BOLD$1$ES_DEF"
  echo -n -e "$ES_GRY_FG"
  $1 || death
  echo -n -e "$ES_DEF"
}


# -------------------------------------------------------------------------
# We had better watch out for bad shit

trap abort 2 3


# --------------------------------------------------------------------------
# On your marks, get set, GO!

wide_bar
pretty "           APLAWS+ Bundle Re-configuration Script         "
wide_bar
plain "This script will regenerate all the automatic configuration scripts"
plain "and clever Makefiles used by this package. If you don't have autoconf,"
plain "automake and libtool installed you could seriously screw up your"
plain "distribution. Press Control-C now to exit if you are worried"
prompt ""


# -------------------------------------------------------------
# cleanup
bar
plain "Cleaning up directory"
command "rm -f acinclude.m4 aclocal.m4 config.guess config.sub \
 configure ltconfig ltmain.sh stamp-h"



# -------------------------------------------------------------
# aclocal
bar
plain "Regenerating configuration header file"
command "aclocal"


# -------------------------------------------------------------
# autoconf
bar
plain "Regenerating automatic configuration script"
command "autoconf"


# -------------------------------------------------------------
# automake
bar
plain "Regenerating automatic makefiles"
command "automake -a"


# ------------------------------------------------------------
# Say goodbye

wide_bar
pretty "The APLAWS+ Bundle package has been successfully reconfigured."
pretty "Have a nice day "
pretty "                 - The APLAWS+ development team."
wide_bar
exit

