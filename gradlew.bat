@echo off
rem Gradle Wrapper for Windows

set DIR=%~dp0
set APP_BASE_NAME=%~n0
set APP_HOME=%DIR%

set DEFAULT_JVM_OPTS="-Xmx2048m" "-Dfile.encoding=UTF-8"

set WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar
set WRAPPER_PROPERTIES=%APP_HOME%gradle\wrapper\gradle-wrapper.properties

if not exist "%WRAPPER_JAR%" (
    echo ERROR: Could not find gradle wrapper jar at %WRAPPER_JAR%
    exit /b 1
)

if not exist "%WRAPPER_PROPERTIES%" (
    echo ERROR: Could not find gradle wrapper properties at %WRAPPER_PROPERTIES%
    exit /b 1
)

java.exe %DEFAULT_JVM_OPTS% -jar "%WRAPPER_JAR%" %*
