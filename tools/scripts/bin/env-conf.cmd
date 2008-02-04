
echo Setting up build environment variables

REM  Grab the package specific info

perl %CCM_SCRIPTS_HOME%\bin\extract-version.pl > tempenv.cmd 2>&1 && goto extractVersionSuccess
type tempenv.cmd
del tempenv.cmd
goto endFile
:extractVersionSuccess
call tempenv.cmd
del tempenv.cmd

perl %CCM_SCRIPTS_HOME%\bin\env-conf > tempenv.cmd 2>&1 && goto extractEnvSuccess
type tempenv.cmd
del tempenv.cmd
goto endFile
:extractEnvSuccess
call tempenv.cmd
del tempenv.cmd
