#!/bin/sh
#
# CCM Devel Installation script
#
# Idea from WindowMaker installation script by Alfredo K. Kojimo
#
# Copyright (C) 1999-2002 Daniel Berrange
#

# With modifications by Andrew Hunter

CONFIG_OPT=""

# -----------------------------------------------------------
# First off we need a whole lot of clever functions

# Hmm, maybe we could use terminfo to get this information...

if [ "$1" = "-mono" -o $TERM != "xterm" -a $TERM != "ansi" \
     -a $TERM != "xterm-color" ]; then

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
  echo -e "$ES_BOLD$ES_YEL_FG$ES_BLACK_BG$1$ES_DEF"
}

alert()
{
  echo -e "$ES_RED_FG$ES_BLACK_BG$1$ES_DEF"
}


death() 
{
  alert
  bar
  alert "Installation process failed. Please inform the CCM development"
  alert "team of the problem encountered."
  wide_bar
  exit
}

abort()
{
  alert
  bar
  alert "Installation process aborted by user. Thankyou, please come again."
  wide_bar
  exit
}

prompt()
{
  echo -n -e "$ES_BOLD$ES_GRN_FG$ES_BLACK_BG> $ES_GRY_FG"
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
  echo -n -e "$ES_GRY_FG$ES_BLACK_BG"
  $1 || death
  echo -n -e "$ES_DEF"
}


# -------------------------------------------------------------------------
# We had better watch out for bad shit

trap abort 2 3


# -------------------------------------------------------------------------
# Work out which directory we were run in (as configure is there too)

DIRNAME=`dirname $0`


# -------------------------------------------------------------------------
# There's always one

if [ ! -e $DIRNAME/configure ]; then
  wide_bar
  alert "This script is part of the CCM Devel package, "
  alert "and should be executed from the directory in which the package"
  alert "was extracted."
  wide_bar
  exit
fi
 

# --------------------------------------------------------------------------
# On your marks, get set, GO!

wide_bar
pretty "         CCM Devel Installation Script"
wide_bar
plain "This script will configure, compile and install the CCM Devel"
plain "package."
plain "Pressing <Enter> for any question will select the default."


# -------------------------------------------------------------------------
# Better check who is doing this install

WHOAMI=`whoami`
if [ "$WHOAMI" != "root" ]; then
  bar
  alert "You are NOT root. Be sure to specify an installation path"
  alert "where you have write permissions."
  DEF_PREFIX="$HOME/usr"
else
  DEF_PREFIX="/usr/local"
fi

# -------------------------------------------------------------
# Get hold of the installation directory


flibble=""
bar
while [ "x$flibble" = "x" ]; do
  plain "Did you read the INSTALL file?"
  plain "Y/y/N/n [n]"
  prompt "n"

  if [ "$flibble" = "y" -o "$flibble" = "Y" ]; then
    plain "I know you're lying but you can continue anyway"
    plain "Of course, if this were the WindowMaker install"
    plain "script you wouldn't get of so lightly..."
  else
    if [ "x$flibble" != "x" ]; then
      plain "Just as I expected, no one ever bothers to read"
      plain "the INSTALL, hence this interactive Install script"
    fi
  fi
done


#--------------------------------------------------------------
# It's probably a good idea to compile in an object directory

if [ "x$DIRNAME" = "x" -o "$DIRNAME" = "." ]; then
  flibble=""
#  bar
#  alert "You are running this script in the source directory: to keep"
#  alert "things tidy, it is recommended that you create a seperate object"
#  alert "directory to actually do this build."
#  bar

#  plain "Do you want me to create a build directory?"  
#  plain "Y/y/N/n [y]"
#  prompt "n"

  if [ "$flibble" = "y" -o "$flibble" = "Y" ]; then
     buildok="no"

     while [ "$buildok" = "no" ]; do
       plain  "What do you want to call this build directory?"
       plain  "[obj]"
       prompt "obj"
     
       builddir="$flibble"

       if [ -e "$builddir" ]; then
         if [ -d "$builddir" ]; then
	   bar
           alert "That directory already exists"
	   bar
           plain "Use it anyway?"
           plain "Y/y/N/n [y]"
	   prompt "y"

           if [ $flibble = "y" -o $flibble = "Y" ]; then
	     buildok="yep"
	   fi
         else
	   bar
           alert "That object already exists, and is not a directory"
	   bar
         fi
       else
         bar
         alert "Creating $builddir..."
	 mkdir $builddir
	 buildok="yep"
       fi
     done

     if [ -e $builddir/.install_link ]; then
       if [ -L $builddir/.install_link ]; then
         rm $builddir/.install_link
       else
         bar
	 alert "That directory is unsuitable for installation"
         wide_bar
	 exit
       fi
     fi

     # Weee, let's do a hack

     # Note that this assumes $DIRNAME == pwd... (See condition above for why
     # this should be so...)
     ln -s `pwd` $builddir/.install_link
     cd $builddir
     DIRNAME=".install_link"
  fi
fi
bar
alert "Compiling into `pwd`"
alert "Sources in $DIRNAME"


# -------------------------------------------------------------
# Get hold of the installation directory

bar
plain "Where do you want to install the CCM Devel package?"
plain "The default location is $DEF_PREFIX  [bin include lib ....]"
prompt "$DEF_PREFIX"

CONFIG_OPT="$CONFIG_OPT  --prefix=$flibble"


# -------------------------------------------------------------
# Get user specific configure options

bar
plain "Please enter any special options to configure that may be required."
plain "Press <Enter> for none"
prompt " "

CONFIG_OPT="$CONFIG_OPT  $flibble"


# ------------------------------------------------------------
# Run configure script, make and install

bar
plain "Configuring package..."
command "$DIRNAME/configure $CONFIG_OPT"

bar
plain "Compiling package..."
command "make"

bar
plain "Installing package..."
command "make install"


# ------------------------------------------------------------
# Say goodbye

wide_bar
pretty "The CCM Devel package has been successfully installed."
pretty "Have a nice day "
pretty "                 - The CCM development team."
wide_bar
exit


# End of file
