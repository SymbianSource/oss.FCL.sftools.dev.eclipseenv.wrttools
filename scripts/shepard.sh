#!/bin/sh

SCRIPTS_DIR=/home/symbian/wrttools/scripts
BUILD_DIR=/home/symbian/scratch/build_dir
DAY_OF_YEAR=`date +%j`
HG_CMD=/usr/local/bin/hg

export WS_DIR=${BUILD_DIR}/${DAY_OF_YEAR}/workspace/wrttools

# display date and time 
date

# create build structure

mkdir -p ${BUILD_DIR}/${DAY_OF_YEAR}/workspace
mkdir -p ${BUILD_DIR}/${DAY_OF_YEAR}/linux
mkdir -p ${BUILD_DIR}/${DAY_OF_YEAR}/win32
mkdir -p ${BUILD_DIR}/${DAY_OF_YEAR}/macosx

# check out the code.
cd ${BUILD_DIR}/${DAY_OF_YEAR}/workspace
${HG_CMD} clone http://sym-mrswitch:8000 wrttools 

# generate scripts
${SCRIPTS_DIR}/runGen.sh ${SCRIPTS_DIR}/genBuildScripts.xml

# build it. 
${SCRIPTS_DIR}/runAnt.sh ${WS_DIR}

# create product 


# package it up. 


