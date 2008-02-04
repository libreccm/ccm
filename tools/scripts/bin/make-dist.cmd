@echo off
@if not "%ECHO%"=="" echo %ECHO%
REM Make all the different binary distributions

if  "%CCM_SCRIPTS_VERBOSE%" == "1" echo on

if "%CCM_SCRIPTS_HOME%" == "" (
  echo CCM_SCRIPTS_HOME not set
  exit 1
)

if "%CCM_ROOT_DIR%" == "" (
  echo CCM_ROOT_DIR not set
  exit 1
)

cd %CCM_ROOT_DIR%


REM Always make the .zip binary dist
if "%CCM_SCRIPTS_COMPAT%" == "" (
  %COMSPEC% /c %CCM_SCRIPTS_HOME%\bin\make-zip.cmd || goto endFile
)
if not "%CCM_SCRIPTS_COMPAT%" == "" (
  %COMSPEC% /c %CCM_SCRIPTS_HOME%\bin\make-zip.%CCM_SCRIPTS_COMPAT%.cmd || goto endFile
)

REM no RPMs or PKGS for Win2K

REM Clean out build dir to save disk space
rmdir /s /q %CCM_INST_DIR%

:endFile