#!/bin/sh

set -x 

PDE_ECLIPSE_DIR=/usr/eclipse
PDE_VER=3.5.1.R35x_20090820
EQUINOX_VER=1.0.201.R35x_v20090715
WS_DIR=$1
BUILD_CONF_DIR=${WS_DIR}/org.symbian.tools.wrttools.feature
JAVA_CMD=/usr/java/jdk1.6.0_18/bin/java
BUILD_FILE=${PDE_ECLIPSE_DIR}/plugins/org.eclipse.pde.build_${PDE_VER}/scripts/productBuild/productBuild.xml 
SOURCE_LEVEL=1.5

cd ${BUILD_CONF_DIR}
${JAVA_CMD} -Dsource=${SOURCE_LEVEL} -Dtarget=${SOURCE_LEVEL}  -jar ${PDE_ECLIPSE_DIR}/plugins/org.eclipse.equinox.launcher_${EQUINOX_VER}.jar -application org.eclipse.ant.core.antRunner  -buildfile build.xml

