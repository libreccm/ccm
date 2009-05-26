@echo off
REM Make all the zip binary distribution

if  "%CCM_SCRIPTS_VERBOSE%" == "1" echo on

if "%CCM_SCRIPTS_HOME%" == "" (
  echo CCM_SCRIPTS_HOME not set
  exit 1
)

if "%CCM_ROOT_DIR%" == "" (
  echo CCM_ROOT_DIR not set
  exit 1
)



echo Creating binary zip file

if not exist "%CCM_INST_DIR%" mkdir %CCM_INST_DIR%

if not exist "%CCM_SRC_DIR%" mkdir %CCM_SRC_DIR%


cd %CCM_SRC_DIR%

REM Extract the sources
echo   Removing old source code directory
rmdir /s /q %CCM_PACKAGE_NAME%
echo   Extracting application sources
unzip %CCM_DIST_ZIP_DIR%\%CCM_PACKAGE_NAME%.zip > nul

cd %CCM_PACKAGE_NAME%

REM Startup scripts

mkdir  %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\bin
mkdir  %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\runtime\bin
mkdir  %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\runtime\bin\resin
mkdir  %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\runtime\bin\tomcat4

copy %CCM_SCRIPTS_HOME%\pkg\bin\* %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\bin
copy %CCM_SCRIPTS_HOME%\pkg\bin\resin\* %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\runtime\bin\resin
copy %CCM_SCRIPTS_HOME%\pkg\bin\tomcat4\* %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\runtime\bin\tomcat4

REM Config files
mkdir %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\conf
mkdir %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\runtime\conf
mkdir %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\runtime\conf\resin
mkdir %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\runtime\conf\tomcat4
copy %CCM_SCRIPTS_HOME%\pkg\conf\resin\* %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\runtime\conf\resin
copy %CCM_SCRIPTS_HOME%\pkg\conf\tomcat4\* %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\runtime\conf\tomcat4

call ccm-configure.cmd || exit 1

call ant make-config || exit 1
call ant make-init || exit 1
call ant make-init-local || exit 1

type %CCM_SCRIPTS_HOME%\pkg\conf\system.conf-resin.in | %CCM_SCRIPTS_HOME%\bin\interpolate.cmd > %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\conf\system.conf-resin.in
type config.vars >> %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\conf\system.conf-resin.in

type %CCM_SCRIPTS_HOME%\pkg\conf\system.conf-tomcat4.in | %CCM_SCRIPTS_HOME%\bin\interpolate.cmd > %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\conf\system.conf-tomcat4.in
type config.vars >> %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\conf\system.conf-tomcat4.in

type %CCM_SCRIPTS_HOME%\pkg\conf\system.conf-tomcat4-win2k.in | %CCM_SCRIPTS_HOME%\bin\interpolate.cmd > %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\conf\system.conf-tomcat4-win2k.in
type config.vars >> %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\conf\system.conf-tomcat4-win2k.in

REM Build the beast
echo   Building sources
call ant build || exit 1

REM Generate the API docs
echo   Generating API docs
call ant javadoc || exit 1

REM Run some tests
REM XXX Nope, not yet we don't - dpb 17/9/2001
REM ant runtests

REM And deploy it
echo   Installing local applications
call ant -Dj2ee.webapp.dir=%CCM_INST_DIR%\%CCM_PACKAGE_NAME%\dist deploy-local || exit 1

echo   Installing source code
call ant -Ddeploy.src.dir=%CCM_INST_DIR%\%CCM_PACKAGE_NAME%\src deploy-src || exit 1

if "%CCM_TYPE%" == "application" (
  call ant -Ddeploy.init.dir=%CCM_INST_DIR%\%CCM_PACKAGE_NAME%\etc deploy-config-init-local || exit 1
  echo   Creating developer distribution
  cd %CCM_INST_DIR%
  zip -r %CCM_DIST_ZIP_DIR%\%CCM_PACKAGE_NAME%-devel.zip %CCM_PACKAGE_NAME%\dist %CCM_PACKAGE_NAME%\src %CCM_PACKAGE_NAME%\etc > nul
  tar -cvf %CCM_PACKAGE_NAME%-devel.tar %CCM_PACKAGE_NAME%/dist %CCM_PACKAGE_NAME%/src %CCM_PACKAGE_NAME%/etc > nul
  gzip -f %CCM_PACKAGE_NAME%-devel.tar
  move %CCM_PACKAGE_NAME%-devel.tar.gz %CCM_DIST_ZIP_DIR%
  echo %CCM_DIST_ZIP_DIR%\%CCM_PACKAGE_NAME%-devel.zip
  echo %CCM_DIST_ZIP_DIR%\%CCM_PACKAGE_NAME%-devel.tar.gz
)

REM (possibly overwrite) local init files with complete config
call ant -Ddeploy.init.dir=%CCM_INST_DIR%\%CCM_PACKAGE_NAME%\etc deploy-config-init || exit 1

echo   Installing API documentation
cd %CCM_SRC_DIR%\%CCM_PACKAGE_NAME%
call ant -Ddeploy.api.dir=%CCM_INST_DIR%\%CCM_PACKAGE_NAME%\api deploy-api-nodeps || exit 1

echo   Creating API doc distribution
cd %CCM_INST_DIR%
zip -r %CCM_DIST_ZIP_DIR%\%CCM_PACKAGE_NAME%-api.zip %CCM_PACKAGE_NAME%\api > nul
echo %CCM_DIST_ZIP_DIR%\%CCM_PACKAGE_NAME%-api.zip

echo   Installing pre built applications
cd %CCM_SRC_DIR%\%CCM_PACKAGE_NAME%
call ant -Dj2ee.webapp.dir=%CCM_INST_DIR%\%CCM_PACKAGE_NAME%\dist deploy-global || exit 1


if not "%CCM_TYPE%" == "application" (
  echo   Creating developer distribution
  cd %CCM_INST_DIR%
  zip -r %CCM_DIST_ZIP_DIR%\%CCM_PACKAGE_NAME%-devel.zip %CCM_PACKAGE_NAME%\dist %CCM_PACKAGE_NAME%\src %CCM_PACKAGE_NAME%\etc > nul
  tar -cvf %CCM_PACKAGE_NAME%-devel.tar %CCM_PACKAGE_NAME%/dist %CCM_PACKAGE_NAME%/src %CCM_PACKAGE_NAME%/etc > nul
  gzip -f %CCM_PACKAGE_NAME%-devel.tar
  move %CCM_PACKAGE_NAME%-devel.tar.gz %CCM_DIST_ZIP_DIR%
  echo %CCM_DIST_ZIP_DIR%\%CCM_PACKAGE_NAME%-devel.zip
  echo %CCM_DIST_ZIP_DIR%\%CCM_PACKAGE_NAME%-devel.tar.gz
)

echo   Creating servlet distribution
cd %CCM_INST_DIR%
copy %CCM_PACKAGE_NAME%\etc\enterprise.init.in %CCM_PACKAGE_NAME%\dist\WEB-INF\resources\enterprise.init.in
xcopy /e /i /o /y %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\runtime\bin\* %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\bin
xcopy /e /i /o /y %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\runtime\conf\* %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\conf
mkdir %CCM_INST_DIR%\%CCM_PACKAGE_NAME%\logs
zip -r %CCM_DIST_ZIP_DIR%\%CCM_PACKAGE_NAME%-bin.zip %CCM_PACKAGE_NAME%\dist %CCM_PACKAGE_NAME%\bin %CCM_PACKAGE_NAME%\conf %CCM_PACKAGE_NAME%\logs > nul
tar -cvf %CCM_PACKAGE_NAME%-bin.tar %CCM_PACKAGE_NAME%/dist %CCM_PACKAGE_NAME%/bin %CCM_PACKAGE_NAME%/conf %CCM_PACKAGE_NAME%/logs > nul
gzip -f %CCM_PACKAGE_NAME%-bin.tar
move %CCM_PACKAGE_NAME%-bin.tar.gz %CCM_DIST_ZIP_DIR%
echo %CCM_DIST_ZIP_DIR%\%CCM_PACKAGE_NAME%-bin.zip
echo %CCM_DIST_ZIP_DIR%\%CCM_PACKAGE_NAME%-bin.tar.gz

exit 0;

REM End of file
