#!/bin/sh

set -x 

SCRIPTS_DIR=/home/symbian/wrttools/scripts
BUILD_DIR=/home/symbian/scratch/build_dir
# DAY_OF_YEAR=`date +%j`
DAY_OF_YEAR=033-1
HG_CMD=/usr/local/bin/hg

export WS_DIR=${BUILD_DIR}/${DAY_OF_YEAR}/workspace/wrttools
LINUX_PROD_DIR=${BUILD_DIR}/${DAY_OF_YEAR}/linux
LINUX_EXPORT_DIR=/home/symbian/scratch/025
WIN32_PROD_DIR=${BUILD_DIR}/${DAY_OF_YEAR}/win32
MACOSX_PROD_DIR=${BUILD_DIR}/${DAY_OF_YEAR}/macosx

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
for j in `find ${WS_DIR} -name '*.jar'`
do
	b=`basename $j`
	case ${b} in
	tagsoup*) 
	;;
	jtidy*)
	;;
	tk.eclipse.plugin.htmleditor*) 
	;;		
	*)  
	cp -p $j ${LINUX_PROD_DIR}/eclipse/plugins/
	;;
	esac
done

# package it up. 

tar -czvf  ${LINUX_PROD_DIR}/webruntime.linux.tar ${LINUX_PROD_DIR}/eclipse/

