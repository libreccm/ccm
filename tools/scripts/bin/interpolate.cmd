@echo off
REM Interpolate a file with the usual package variables


perl -T %CCM_SCRIPTS_HOME%\bin\interpolate.pl PACKAGE=%CCM_PACKAGE% PRETTYNAME='%CCM_PRETTYNAME%' VERSION=%CCM_VERSION% RELEASE=%CCM_RELEASE% %* || exit 1

exit 0
