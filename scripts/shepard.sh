#!/bin/sh

set -x 

SCRIPTS_DIR=/home/symbian/wrttools/scripts
BASE_DIR=/home/symbian/scratch/build_dir
DAY_OF_YEAR=`date +%j`
HG_CMD=/usr/local/bin/hg
ANT_CMD=/usr/local/apache-ant-1.8.0/bin/ant

export PATH=.:/usr/java/jdk1.6.0_18/bin:/usr/local/apache-ant-1.8.0/bin/ant:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin

export SOURCE_DIR=${BASE_DIR}/${DAY_OF_YEAR}/workspace/wrttools
export BUILD_DIR=${BASE_DIR}/${DAY_OF_YEAR}/eclipse.build

# display date and time 
date

# create build structure

mkdir -p ${BASE_DIR}/${DAY_OF_YEAR}/workspace
mkdir -p ${BASE_DIR}

# check out the code.
cd ${BASE_DIR}/${DAY_OF_YEAR}/workspace
${HG_CMD} clone http://sym-mrswitch:8000 wrttools 

cd ${SOURCE_DIR}/scripts

${ANT_CMD} -DbuildDirectory=${BUILD_DIR} -DsourceDirectory=${SOURCE_DIR}

