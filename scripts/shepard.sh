#!/bin/sh

set -x 

SCRIPTS_DIR=/home/symbian/wrttools/scripts
BUILD_DIR=/home/symbian/scratch/build_dir
DAY_OF_YEAR=`date +%j`
HG_CMD=/usr/local/bin/hg

export PATH=.:/usr/java/jdk1.6.0_18/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin

export WS_DIR=${BUILD_DIR}/${DAY_OF_YEAR}/workspace/wrttools
LINUX_PROD_DIR=${BUILD_DIR}/${DAY_OF_YEAR}/linux
LINUX_EXPORT_DIR=/home/symbian/scratch/025
WIN32_PROD_DIR=${BUILD_DIR}/${DAY_OF_YEAR}/win32
WIN32_EXPORT_DIR=/home/symbian/buildsFromEugene/win32.win32.x86
MACOSX_PROD_DIR=${BUILD_DIR}/${DAY_OF_YEAR}/macosx
MACOSX_EXPORT_DIR=/home/symbian/buildsFromEugene/macosx.cocoa.x86

# display date and time 
date

# create build structure

mkdir -p ${WS_DIR}
mkdir -p ${LINUX_PROD_DIR}
mkdir -p ${WIN32_PROD_DIR}
mkdir -p ${MACOSX_PROD_DIR}

# check out the code.
cd ${BUILD_DIR}/${DAY_OF_YEAR}/workspace
${HG_CMD} clone http://sym-mrswitch:8000 wrttools 

# generate scripts
sh ${SCRIPTS_DIR}/runGen.sh ${SCRIPTS_DIR}/genBuildScripts.xml

# build it. 
sh ${SCRIPTS_DIR}/runAnt.sh ${WS_DIR}

# create product 
# Linux 
cp -pr ${LINUX_EXPORT_DIR}/eclipse ${LINUX_PROD_DIR}
for j in `find ${WS_DIR} -name 'org.chromium*.jar' -o -name 'org.symbian*.jar'`
do  
	cp -p $j ${LINUX_PROD_DIR}/eclipse/plugins/
done
cp -p ${SCRIPTS_DIR}/supporting/* ${LINUX_PROD_DIR}/eclipse/

# package it up. 

cd ${LINUX_PROD_DIR}
tar -czvf webruntime${DAY_OF_YEAR}.linux.tar eclipse

# Mac OSX
cp -pr ${MACOSX_EXPORT_DIR}/eclipse ${MACOSX_PROD_DIR}
for j in `find ${WS_DIR} -name 'org.chromium*.jar' -o -name 'org.symbian*.jar'`
do  
	cp -p $j ${MACOSX_PROD_DIR}/eclipse/plugins/
done
cp -p ${SCRIPTS_DIR}/supporting/* ${MACOSX_PROD_DIR}/eclipse/

# package it up. 
cd ${MACOSX_PROD_DIR}
tar -czvf  webruntime${DAY_OF_YEAR}.macosx.tar eclipse

# Win32
cp -pr ${WIN32_EXPORT_DIR}/eclipse ${WIN32_PROD_DIR}
for j in `find ${WS_DIR} -name 'org.chromium*.jar' -o -name 'org.symbian*.jar'`
do  
	cp -p $j ${WIN32_PROD_DIR}/eclipse/plugins/
done
cp -p ${SCRIPTS_DIR}/supporting/* ${WIN32_PROD_DIR}/eclipse/

# package it up. 

cd ${WIN32_PROD_DIR}
tar -czvf  webruntime${DAY_OF_YEAR}.win32.tar eclipse

