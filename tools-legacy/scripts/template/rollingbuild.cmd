if  not "%CCM_SCRIPTS_HOME%" == "" goto foundscriptshome
echo "CCM_SCRIPTS_HOME not set"
goto endFile
:foundscriptshome


%CCM_SCRIPTS_HOME%\bin\build.cmd


:endFile
