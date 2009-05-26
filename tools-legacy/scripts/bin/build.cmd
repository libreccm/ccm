
REM Print command executed to stdout
@echo off
@if not "%ECHO%"=="" echo %ECHO%
if  "%CCM_SCRIPTS_VERBOSE%" == "1" echo on

if "%CCM_SCRIPTS_HOME%" == "" (
  echo CCM_SCRIPTS_HOME not set
  goto endFile
)

REM Pull in env variables for configuration
call %CCM_SCRIPTS_HOME%\bin\env-conf.cmd || goto endFile

if "%CCM_APP%" == "" (
  echo Please set the CCM_APP environment variable
  goto endFile
)

if not exist "%CCM_BUILD_DIR%" mkdir %CCM_BUILD_DIR%

REM Make the source distributions
%COMSPEC% /c %CCM_SCRIPTS_HOME%\bin\make-source.cmd || goto endFile

REM Make The binary distribution
%COMSPEC% /c %CCM_SCRIPTS_HOME%\bin\make-dist.cmd || goto endFile

REM Calculate checksums for all generated pacakges
REM not for windows
REM %COMSPEC% /c %CCM_SCRIPTS_HOME%\bin\calc-checksum.cmd || goto endFile


:endFile
