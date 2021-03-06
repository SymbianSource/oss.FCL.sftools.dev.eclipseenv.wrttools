#!/bin/sh

set -x 

SCRIPTS_DIR=/home/symbian/wrttools/scripts
LOCAL_REPO_DIR=/home/symbian/local-repo/wrttools
BASE_DIR=/home/symbian/scratch/build_dir
ECLIPSE_TIMESTAMP=`date +%Y%m%d%H%M%S`-${BUILD_NUMBER:-NO_BUILD_NUMBER}
HG_CMD=/usr/local/bin/hg
ANT_CMD=/usr/local/apache-ant-1.8.0/bin/ant

export PATH=.:/usr/java/jdk1.6.0_18/bin:/usr/local/apache-ant-1.8.0/bin/ant:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin

WS_DIR=${BASE_DIR}/${BUILD_NUMBER:-NO_BUILD_NUMBER}/workspace
export SOURCE_DIR=${BASE_DIR}/${BUILD_NUMBER:-NO_BUILD_NUMBER}/workspace/wrttools
export BUILD_DIR=${BASE_DIR}/${BUILD_NUMBER:-NO_BUILD_NUMBER}/eclipse.build

# display date and time 
date

# create build structure

mkdir -p ${WS_DIR}

# synchronize local cache with http://developer.symbian.org/oss/MCL/sftools/dev/eclipseenv/wrttools/ 
cd ${LOCAL_REPO_DIR}
${HG_CMD} pull
${HG_CMD} update 

# check out the code.
cd ${WS_DIR}
${HG_CMD} clone ${LOCAL_REPO_DIR}

cd ${SOURCE_DIR}/scripts

# set the build directory in the build.properties files. 
cat ${SOURCE_DIR}/scripts/build.properties.SED | sed -e s#SED_BUILD_DIR#${BUILD_DIR}#g > ${SOURCE_DIR}/scripts/build.properties

cat ${SOURCE_DIR}/scripts/plugin.properties.SED | sed -e s#SED_BUILD_ID#${ECLIPSE_TIMESTAMP}#g > ${SOURCE_DIR}/org.symbian.tools.wrttools.product/plugin.properties

# bug fix for 1872. [testcase: go to about box. click on installation details.  Do you see an error? ] 
for f in `find ${SOURCE_DIR} -name MANIFEST.MF`
do
	cat $f | sed -e s#qualifier#"${ECLIPSE_TIMESTAMP}"#g > ${f}
done

# wrt-ide.product has the orginal reference to 1.0.0.qualifier. 
prod_file=${SOURCE_DIR}/org.symbian.tools.wrttools.product/wrt-ide.product
cat $prod_file | sed -e s#qualifier#"${ECLIPSE_TIMESTAMP}"#g > $prod_file

${ANT_CMD} -DbuildDirectory=${BUILD_DIR} -DsourceDirectory=${SOURCE_DIR}

cd ${BUILD_DIR}/N.WrtIde

# bug 2165 - add a timestamp to filename 
for file in *.zip
do
  if [ -f $file ] ; then
    file_original=`basename $file`
    # name without extension
    file_stamped=${file_original%\.*}-${ECLIPSE_TIMESTAMP}.zip
    mv ${file_original} ${file_stamped}
  fi ;
done


