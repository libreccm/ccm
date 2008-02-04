
@echo off
@if not "%ECHO%"=="" echo %ECHO%

if "%CCM_ZIP_ROOT%" == "" (
  set CCM_ZIP_ROOT=C:\ccm
)

for %%v in (%CCM_ZIP_ROOT%\etc\profile.d\*.cmd) do call %%v 

REM if called with two arguments, assume that ccm-profile should be called
if not {%2}=={} (
  call ccm-profile %1 %2
)
