@echo off
:: Delete old "deploy" dir
echo [36mDeleting old deploy directory contents...[0m
del /s /f /q deploy\*.*
for /f %%f in ('dir /ad /b deploy\') do rd /s /q deploy\%%f

:: If clean option is set, the job is done
if "%1"=="clean" (
    echo [92mDeployment directory has been removed! Done. [0m
    goto:eof
)


set copy_config=TRUE
set push_remote=FALSE
set config_dir=private_config

for %%A in (%*) DO (
    if "%%A"=="--no-push" ( SET push_remote=FALSE )
    if "%%A"=="--copy-config" ( SET copy_config=TRUE )
)

echo.
echo [46mCopying startup files to target...[0m
echo These files should be copied to target machine
echo They are needed to start it

if %copy_config%==TRUE (
    echo [35mCopying custom configuration files from %config_dir%\ ...[0m
    xcopy %config_dir%\docker-compose.yml deploy /Y/R/H
    if errorlevel 1 ( goto error )
    xcopy %config_dir%\.env* deploy\inventory-client /Y/R/H
    if errorlevel 1 ( goto error )
    xcopy %config_dir%\application.yml deploy\config /Y/R/H
    if errorlevel 1 ( goto error )
    xcopy %config_dir%\registry.txt /Y/R/H
    echo.
    echo Private configuration copied successfully!
)

echo.
echo [33mReading configuration from: [36mregistry.txt[0m
for /f "delims== tokens=1,2" %%G in (registry.txt) do set %%G=%%H

:: Build API part
cd InventoryServer
echo.
echo [33mBuilding API Docker image:[0m %apiImageName%...
call mvnw.cmd clean package -DskipTests -DrepositoryName=%apiImageName%
if errorlevel 1 ( goto error )
echo.
echo [36mTagging %apiImageName% with [0m :latest
call mvnw.cmd dockerfile:tag -DtagName=latest -DrepositoryName=%apiImageName%
if errorlevel 1 ( goto error )


if %push_remote%==TRUE (
    echo.
    echo [33mLogging into registry at: [36m%registry%...[0m
    docker login -u %username% -p %password% %registry%
    if errorlevel 1 ( goto error )

    docker tag %apiImageName%:latest %registry%/%apiImageName%
    if errorlevel 1 ( goto error )

    echo.
    echo [33mPushing API image to[36m %registry%/%apiImageName%...[0m
    docker push %registry%/%apiImageName%
    if errorlevel 1 ( goto error )
    echo [33mAPI pushed successfully![0m
    echo.
)

:: Build Client part
echo [33mBuilding client image...[0m
cd ../inventory-client
docker build -t %clientImageName%:latest .
if errorlevel 1 ( goto error )

echo.
echo [33mClient image built.[0m 

if %push_remote%==TRUE (
    echo Pushing...

    docker tag %clientImageName%:latest %registry%/%clientImageName%
    docker push %registry%/%clientImageName%
    if errorlevel 1 ( goto error )
    echo [33mImage pushed successfully to: [36m%registry%/%clientImageName%[0m
    echo.
    echo [33mRemoving temporary images...[0m
    docker rmi %registry%/%clientImageName%
    if errorlevel 1 ( goto error )
    docker rmi %registry%/%apiImageName%
    if errorlevel 1 ( goto error )
)
cd ../
echo.
echo [92mBuild successful![0m Your images are built
if %push_remote%==TRUE ( echo  and pushed into:[36m %registry% )
echo - %apiImageName%:latest
echo - %clientImageName%:latest[0m
echo.

goto:eof

:error
    echo [91mAn error occurred! Terminating...[0m
    exit /B 1