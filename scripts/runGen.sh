#!/bin/sh
ECLIPSE_DIR=/usr/eclipse
PDE_VER=3.5.1.R35x_20090820
EQUINOX_VER=1.0.201.R35x_v20090715
JAVA_CMD=/usr/java/jdk1.6.0_18/bin/java

${JAVA_CMD} -jar ${ECLIPSE_DIR}/plugins/org.eclipse.equinox.launcher_${EQUINOX_VER}.jar -application org.eclipse.ant.core.antRunner -buildfile ~/bin/genBuildScripts.xml

