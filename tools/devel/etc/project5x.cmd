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

if "%JAVA_HOME%" == "" (
  if exist "C:\jdk1.3.1_04" set JAVA_HOME=C:\jdk1.3.1_04
)

if "%JAVA_HOME%" == "" (
    echo Cannot find suitable JDK 1.3.x installation
    echo Looked in dir: C:\jdk1.3.1_04
    goto endFile
)

if "%CCM_ZIP_ROOT%" == "" (
  set CCM_ZIP_ROOT=C:\ccm
)
  
set  PATH=%JAVA_HOME%\bin;%PATH%

set CCM_HOME=%CCM_ZIP_ROOT%\devel\dev\%USER%\%SERVER%
set WEB_HOME=%CCM_ZIP_ROOT%\devel\web\%USER%\%SERVER%
set WEB_LIB_DIR=%WEB_HOME%\webapps\ccm\WEB-INF\lib
set CLASSPATH=%CLASSPATH%;%ORACLE_HOME%\jdbc\lib\classes12.zip

set SERVER_CLASSPATH=%WEB_LIB_DIR%\jaas.jar;%WEB_LIB_DIR%\jce.jar;%WEB_LIB_DIR%\sunjce_provider.jar;%ORACLE_HOME%\jdbc\lib\classes12.zip;%WEB_LIB_DIR%\xerces.jar;%WEB_LIB_DIR%\xalan.jar;

set RESIN_ARGS=-conf %WEB_HOME%\conf\resin.conf -stdout %WEB_HOME%\logs\resin-stdout.log -stderr %WEB_HOME%\logs\resin-stderr.log

doskey cddev=cd %CCM_ZIP_ROOT%\devel\dev\%USER%\%SERVER%
doskey cdweb=cd %CCM_ZIP_ROOT%\devel\web\%USER%\%SERVER%

:endFile
