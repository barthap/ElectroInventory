@echo off

::options: legacy, auto-build ([--push ?]), clear

:: Preparations, parameter parsing
set skip_compile=FALSE
set copy_private_cfg=FALSE
set config_dir=private_config

for %%A in (%*) DO (
    if "%%A"=="--skip-compile" ( SET skip_compile=TRUE )
    if "%%A"=="--copy-config" ( SET copy_private_cfg=TRUE )
)


:: Delete old "deploy" dir
echo [36mDeleting old deploy directory contents...[0m
del /s /f /q deploy\*.*
for /f %%f in ('dir /ad /b deploy\') do rd /s /q deploy\%%f

:: If clean option is set, the job is done
if "%1"=="clean" (
    echo [92mDeployment directory has been removed! Done. [0m
    goto:eof
)


if %skip_compile%==TRUE ( echo [33mSkipping compiling Maven project[0m ) ELSE (
    cd InventoryServer
    echo [46mCompiling Maven project...[0m
    call mvnw.cmd clean package -DskipTests
    if errorlevel 1 ( goto error )

    echo [32mFat JAR built successfully![0m
    cd ..\
)


:: Copy files
echo.
echo [46mCopying files...[0m

if not exist InventoryServer\target\inventory*.jar (
    echo [91mAPI Jar file does not exist! Run this script without --skip-compile flag. Terminating...[0m
    exit /B
)



xcopy InventoryServer\target\*.jar deploy\InventoryServer\target /I/Y/R/H
if errorlevel 1 ( goto error )
xcopy InventoryServer\Dockerfile deploy\InventoryServer /I/Y/R/H
if errorlevel 1 ( goto error )

xcopy config\*.* deploy\config /I/Y/R/H/E
if errorlevel 1 ( goto error )

xcopy inventory-client\src\*.* deploy\inventory-client\src /I/Y/R/H/E
if errorlevel 1 ( goto error )
xcopy inventory-client\public\*.* deploy\inventory-client\public /I/Y/R/H/E
if errorlevel 1 ( goto error )
xcopy inventory-client\server\*.* deploy\inventory-client\server /I/Y/R/H/E
if errorlevel 1 ( goto error )
xcopy inventory-client\package*.json deploy\inventory-client /I/Y/R/H
if errorlevel 1 ( goto error )
xcopy inventory-client\.env* deploy\inventory-client /I/Y/R/H
if errorlevel 1 ( goto error )
xcopy inventory-client\Dockerfile deploy\inventory-client /I/Y/R/H
if errorlevel 1 ( goto error )
copy /Y docker-compose.yml deploy\docker-compose.yml
if errorlevel 1 ( goto error )
copy /Y docker-compose.legacy.yml deploy\docker-compose.override.yml
if errorlevel 1 ( goto error )

echo [46mCopying finished![0m
echo.

if %copy_private_cfg%==TRUE (
    echo [35mCopying custom configuration files from %config_dir%\...[0m
    xcopy %config_dir%\docker-compose.yml deploy /Y/R/H
    if errorlevel 1 ( goto error )
    xcopy %config_dir%\.env* deploy\inventory-client /Y/R/H
    if errorlevel 1 ( goto error )
    xcopy %config_dir%\application.yml deploy\config /Y/R/H
    if errorlevel 1 ( goto error )
    echo.
    echo Private configuration copied successfully!
)

echo [92mDeployment directory created successfully! Copy it to target machine and run docker-compose up -d[0m
goto:eof

:error
    echo [91mAn error occurred when copying files! Terminating...[0m
    exit /B 1