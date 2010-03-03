#!/bin/sh

set -x 

SCRIPTS_DIR=/home/symbian/wrttools/scripts
BASE_DIR=/home/symbian/scratch/build_dir
DAY_OF_YEAR=`date +%j`
ECLIPSE_TIMESTAMP=`date +%Y%m%d%H%M%S`-${BUILD_NUMBER:-NO_BUILD_NUMBER}
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

# set the build directory in the build.properties files. 
cat ${SOURCE_DIR}/scripts/build.properties.SED | sed -e s#SED_BUILD_DIR#${BUILD_DIR}#g > ${SOURCE_DIR}/scripts/build.properties

# bug fix for 1872. [testcase: go to about box. click on installation details.  Do you see an error? ] 
for f in `find ${SOURCE_DIR} -name MANIFEST.MF`
do
	cat $f | sed -e s#qualifier#"${ECLIPSE_TIMESTAMP}"#g > ${f}
done

# wrt-ide.product has the orginal reference to 1.0.0.qualifier. Try deleting .qualifier 
prod_file=${SOURCE_DIR}/org.symbian.tools.wrttools.product/wrt-ide.product
cat $prod_file | sed -e s#qualifier#"${ECLIPSE_TIMESTAMP}"#g > $prod_file

${ANT_CMD} -DbuildDirectory=${BUILD_DIR} -DsourceDirectory=${SOURCE_DIR}

