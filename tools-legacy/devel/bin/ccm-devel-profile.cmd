@echo off
@if not "%ECHO%"=="" echo %ECHO%


if  {%1}=={} (
  echo missing servername
  echo usage: "ccm-devel-profile <servername> <username>"
  goto endFile
)
set SERVER=%1

if  {%2}=={} (
  echo missing username
  echo usage: "ccm-devel-profile <servername> <username>"
  goto endFile
)
set USER=%2

if "%CCM_DEVEL_CONF_DIR%" == "" (
  echo "CCM_DEVEL_CONF_DIR not set"
  goto endFile
)

perl %CCM_DEVEL_CONF_DIR%\project.pl %1 %2 > tempenv.cmd 2>&1 && goto projectSuccess
type tempenv.cmd
del tempenv.cmd
goto endFile
:projectSuccess
call tempenv.cmd
del tempenv.cmd

for %%v in (%CCM_DEVEL_CONF_DIR%\project.d\*.cmd) do call %%v 

:endFile
