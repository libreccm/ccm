@echo off
@if not "%ECHO%"=="" echo %ECHO%

setLocal

if  {%1}=={} (
  echo "syntax: ccm-devel-user <username>"
  goto endFile
)

set USERNAME=%1

if "%CCM_ZIP_ROOT%" == "" (
  set CCM_ZIP_ROOT=C:\ccm
)

if not exist "%CCM_ZIP_ROOT%" ( mkdir %CCM_ZIP_ROOT% )
if not exist "%CCM_ZIP_ROOT%\var" ( mkdir %CCM_ZIP_ROOT%\var )
if not exist "%CCM_ZIP_ROOT%\var\ccm-devel" ( mkdir %CCM_ZIP_ROOT%\var\ccm-devel )

cd %CCM_ZIP_ROOT%\var\ccm-devel

if exist "dev\%USERNAME%" (
  echo development directories already exist for %USERNAME%
  goto endFile
)
mkdir dev\%USERNAME%
mkdir web\%USERNAME%

:endFile
endLocal
