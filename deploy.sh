#!/bin/bash
# deploy.sh

skip_compile=0
copy_private_cfg=0
config_dir=private_config

for arg in "$@"
do 
    if [[ "$arg" = "--skip-compile" ]]
    then
        skip_compile=1
    fi

    if [[ "$arg" = "--copy-config" ]]
    then
        copy_private_cfg=1
    fi
done

echo -e "\033[36mDeleting old deploy directory contents...\033[0m"
rm -rf deploy/

if [ $skip_compile -eq 1 ]
then
    echo -e "\033[33mSkipping Maven project compilation...\033[0m"
else
    echo -e "\033[46mBuilding Maven project...\033[0m"
    cd InventoryServer
    ./mvnw clean package -DskipTests
    echo -e "\033[32mFat Jar built successfully!\033[0m"
    cd ../
fi

echo -e "\n\033[34mCopying files...\033[0m"
mkdir deploy
mkdir -p deploy/InventoryServer/target
mkdir -p deploy/inventory-client

if [ ! -f InventoryServer/target/*.jar ]; then
    echo -e "\033[91mAPI Jar file does not exist! Run this script without --skip-compile flag. Terminating...\033[0m"
    echo \n
    exit -1
fi

err=0
cp InventoryServer/target/*.jar deploy/InventoryServer/target/
let err=err+$?
cp InventoryServer/Dockerfile deploy/InventoryServer
let err=err+$?
cp -r config/ deploy/config
let err=err+$?

cp -r inventory-client/src/ deploy/inventory-client/src
let err=err+$?
cp -r inventory-client/public/ deploy/inventory-client/public
let err=err+$?
cp -r inventory-client/server/ deploy/inventory-client/server
let err=err+$?
cp inventory-client/package*.json deploy/inventory-client
let err=err+$?
cp inventory-client/.env* deploy/inventory-client
let err=err+$?
cp inventory-client/Dockerfile deploy/inventory-client
let err=err+$?
cp docker-compose.yml deploy
let err=err+$?
cp docker-compose.build.yml deploy/docker-compose.override.yml
let err=err+$?

if [ $err -ne 0 ]; then
    echo -e "\033[91mAn error occurred during copying files!\033[0m\n"
    exit -1
else
    echo -e "\033[96mCopying finished\033[0m\n"
fi

if [ $copy_private_cfg -eq 1 ];
then
    echo -e "\033[35mCopying custom config files from $config_dir/\033[0m\n"
    err=0
    cp $config_dir/docker-compose.yml deploy/
    let err=err+$?
    cp $config_dir/.env* deploy/inventory-client/
    let err=err+$?
    cp $config_dir/application.yml deploy/config/
    let err=err+$?
    if [ $err -ne 0 ]; then
        echo -e "\033[91mAn error occurred when copying configuration files!\033[0m\n"
        exit -1
    else
        echo -e "Private configuration copied successfully!\n"
    fi
fi

echo -e "\033[92mDeployment directory created successfully! Copy it to target machine and run \"docker-compose up -d\".\033[0m\n"
