@echo off
setlocal
perl %~dp0\tomcat-conf-gen %*
endlocal
:endFile
