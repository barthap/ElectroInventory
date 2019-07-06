@echo off

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


if %skip_compile%==TRUE ( echo [33mSkipping compiling Maven project[0m ) ELSE (
    cd InventoryServer
    echo [46mCompiling Maven project...[0m
    call mvnw.cmd clean package -DskipTests
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

xcopy InventoryServer\Dockerfile deploy\InventoryServer /I/Y/R/H
xcopy config\*.* deploy\config /I/Y/R/H/E
xcopy inventory-client\src\*.* deploy\inventory-client\src /I/Y/R/H/E
xcopy inventory-client\public\*.* deploy\inventory-client\public /I/Y/R/H/E
xcopy inventory-client\server\*.* deploy\inventory-client\server /I/Y/R/H/E
xcopy inventory-client\package*.json deploy\inventory-client /I/Y/R/H
xcopy inventory-client\.env* deploy\inventory-client /I/Y/R/H
xcopy inventory-client\Dockerfile deploy\inventory-client /I/Y/R/H
xcopy docker-compose.yml deploy /I/Y/R/H


echo [46mCopying finished![0m
echo.

if %copy_private_cfg%==TRUE (
    echo [35mCopying custom configuration files from %config_dir%\...[0m
    xcopy %config_dir%\docker-compose.yml deploy /Y/R/H
    xcopy %config_dir%\.env* deploy\inventory-client /Y/R/H
    xcopy %config_dir%\application.yml deploy\config /Y/R/H
    echo.
    echo Private configuration copied successfully!
)

echo [92mDeployment directory created successfully! Copy it to target machine and run docker-compose up -d[0m